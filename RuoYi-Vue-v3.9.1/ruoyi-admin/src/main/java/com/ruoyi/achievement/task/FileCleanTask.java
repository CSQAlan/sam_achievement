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
        for (FileUuid fileUuid : tempFiles) {
            try {
                // 1. 获取物理路径
                String realPath = fileUuid.getRealPath(); // 例如 /profile/upload/2026/03/01/xxx.pdf
                if (StringUtils.isNotEmpty(realPath)) {
                    // 去掉 /profile 前缀，拼上本地磁盘路径
                    String relativePath = StringUtils.substringAfter(realPath, Constants.RESOURCE_PREFIX);
                    String absolutePath = RuoYiConfig.getProfile() + relativePath;
                    
                    File file = new File(absolutePath);
                    if (file.exists()) {
                        if (file.delete()) {
                            log.debug("成功删除临时文件：{}", absolutePath);
                        } else {
                            log.warn("无法删除临时文件：{}", absolutePath);
                        }
                    }
                }

                // 2. 删除数据库记录
                fileUuidMapper.deleteFileUuidByUuid(fileUuid.getFileUuid());
                count++;
            } catch (Exception e) {
                log.error("清理临时文件 {} 失败: {}", fileUuid.getFileUuid(), e.getMessage());
            }
        }
        
        log.info("定时任务执行结束，共清理 {} 条临时文件记录。", count);
    }
}
