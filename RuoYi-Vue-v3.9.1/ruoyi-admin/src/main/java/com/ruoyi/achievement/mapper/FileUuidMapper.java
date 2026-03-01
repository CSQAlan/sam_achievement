package com.ruoyi.achievement.mapper;

import com.ruoyi.achievement.domain.FileUuid;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileUuidMapper {
    /**
     * 新增文件映射
     */
    public int insertFileUuid(FileUuid sysFileUuid);

    /**
     * 根据UUID查询真实路径
     */
    public  FileUuid selectFileUuidById(String fileUuid);
}
