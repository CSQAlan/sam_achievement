package com.ruoyi.achievement.task;

import com.ruoyi.achievement.domain.FileUuid;
import com.ruoyi.achievement.mapper.FileUuidMapper;
import com.ruoyi.common.config.RuoYiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.List;

/**
 * 瀹氭椂娓呯悊鍨冨溇鏂囦欢浠诲姟 (宸茬Щ鑷?ruoyi-admin 妯″潡浠ヨВ鍐冲惊鐜緷璧?
 */
@Component("fileCleanTask")
public class FileCleanTask {
    private static final Logger log = LoggerFactory.getLogger(FileCleanTask.class);

    @Autowired
    private FileUuidMapper fileUuidMapper;

    /**
     * 娓呯悊瓒呰繃24灏忔椂鏈彁浜ょ殑涓存椂鏂囦欢
     * 杩愯鎸囦护锛歠ileCleanTask.cleanTempFiles()
     */
    public void cleanTempFiles() {
        log.info("--- ---");
        
        List<FileUuid> tempFiles = fileUuidMapper.selectTempFilesOlderThan(24);
        
        if (tempFiles == null || tempFiles.isEmpty()) {
            log.info("娌℃湁闇€瑕佹竻鐞嗙殑鍨冨溇鏂囦欢銆?");
            return;
        }

        int count = 0;
        String profilePath = RuoYiConfig.getProfile(); 

        for (FileUuid fileRecord : tempFiles) {
            try {
                String realPath = fileRecord.getRealPath();
                String absolutePath = "";
                if (realPath.startsWith("/profile")) {
                    absolutePath = profilePath + realPath.substring(8);
                } else {
                    absolutePath = profilePath + realPath;
                }
                
                File file = new File(absolutePath);
                if (file.exists()) {
                    if (file.delete()) {
                        log.info("鐗╃悊鍒犻櫎鎴愬姛: {}", absolutePath);
                    } else {
                        log.warn("鐗╃悊鍒犻櫎澶辫触: {}", absolutePath);
                    }
                } else {
                    log.info("鏂囦欢涓嶅瓨鍦紝璺宠繃鐗╃悊鍒犻櫎: {}", absolutePath);
                }

                fileUuidMapper.deleteFileUuidByUuid(fileRecord.getFileUuid());
                count++;
            } catch (Exception e) {
                log.error("娓呯悊鏂囦欢 {} 澶辫触", fileRecord.getFileUuid(), e);
            }
        }

        log.info("娓呯悊瀹屾垚锛屽叡澶勭悊 {} 涓枃浠躲€?", count);
    }
}
