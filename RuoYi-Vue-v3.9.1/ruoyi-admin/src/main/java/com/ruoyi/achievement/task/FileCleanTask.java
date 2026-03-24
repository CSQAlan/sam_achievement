package com.ruoyi.achievement.task;

import com.ruoyi.achievement.domain.FileUuid;
import com.ruoyi.achievement.mapper.FileUuidMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * 定时清理临时文件任务
 */
@Component("fileCleanTask")
public class FileCleanTask {
    private static final Logger log = LoggerFactory.getLogger(FileCleanTask.class);

    @Autowired
    private FileUuidMapper fileUuidMapper;

    /**
     * 清理临时文件 (默认 24 小时)
     */
    public void cleanTempFiles() {
        this.cleanTempFiles(24);
    }

    /**
     * 清理临时文件
     * @param hours 清理超过多少小时的文件
     */
    public void cleanTempFiles(Integer hours) {
        int h = (hours == null || hours <= 0) ? 24 : hours;
        log.info("开始执行定时任务：清理上传未提交的临时文件 (清理范围：超过 {} 小时)...", h);
        
        // 查找超过指定小时的临时文件 (is_temp = 1)
        List<FileUuid> tempFiles = fileUuidMapper.selectTempFilesOlderThan(h);
        
        if (tempFiles == null || tempFiles.isEmpty()) {
            log.info("未发现需要清理的临时文件。");
            return;
        }

        int count = 0;
        String baseProfile = RuoYiConfig.getProfile(); // 假设为 D:/ruoyi/uploadPath

        for (FileUuid fileUuid : tempFiles) {
            try {
                String realPath = fileUuid.getRealPath();
                boolean canDeleteFromDb = false;

                if (StringUtils.isNotEmpty(realPath)) {
                    // 1. 处理相对路径：规范化去掉可能的 /profile 前缀
                    String relativePath = realPath;
                    if (realPath.startsWith("/profile")) {
                        relativePath = realPath.substring(8);
                    }
                    
                    // 2. 规范化路径拼接：确保 baseProfile 和 relativePath 之间有且只有一个斜杠
                    String normalizedBase = baseProfile;
                    if (normalizedBase.endsWith("/") || normalizedBase.endsWith("\\")) {
                        normalizedBase = normalizedBase.substring(0, normalizedBase.length() - 1);
                    }
                    if (!relativePath.startsWith("/") && !relativePath.startsWith("\\")) {
                        relativePath = "/" + relativePath;
                    }
                    
                    String absolutePath = normalizedBase + relativePath;
                    File file = new File(absolutePath);
                    
                    // 3. 执行物理删除逻辑
                    if (file.exists() && file.isFile()) {
                        if (file.delete()) {
                            log.info("【成功】物理文件已删除: {}", absolutePath);
                            canDeleteFromDb = true;
                        } else {
                            log.error("【失败】物理文件删除失败（可能被系统占用）: {}", absolutePath);
                            // 注意：删除失败不设置 canDeleteFromDb = true，留待下次定时任务重试
                        }
                    } else {
                        log.warn("【跳过】磁盘未找到物理文件，视为已清理: {}", absolutePath);
                        // 如果磁盘上确实没这个文件，留着数据库记录也没用，标记为可删除
                        canDeleteFromDb = true; 
                    }
                } else {
                    // 如果路径本身为空，直接清理数据库记录
                    canDeleteFromDb = true;
                }

                // 4. 只有物理文件“搞定了”（已删或确认不存在），才删除数据库记录
                if (canDeleteFromDb) {
                    fileUuidMapper.deleteFileUuidByUuid(fileUuid.getFileUuid());
                    count++;
                }
            } catch (Exception e) {
                log.error("清理临时文件 {} 失败: {}", fileUuid.getFileUuid(), e.getMessage());
            }
        }
        
        log.info("定时任务执行结束，共清理 {} 条临时文件记录。", count);
    }
}
