package com.ruoyi.achievement.mapper;

import com.ruoyi.achievement.domain.FileUuid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileUuidMapper {
    /**
     * 新增文件映射
     */
    public int insertFileUuid(FileUuid sysFileUuid);

    /**
     * 根据UUID查询真实路径
     */
    public FileUuid selectFileUuidById(String fileUuid);

    /**
     * 批量更新文件状态 (0正式 1临时)
     */
    public int updateFileUuidStatus(@Param("uuids") String[] uuids, @Param("status") int status);

    /**
     * 查询超过指定小时的临时文件
     */
    public List<FileUuid> selectTempFilesOlderThan(@Param("hours") int hours);

    /**
     * 根据UUID删除记录
     */
    public int deleteFileUuidByUuid(String fileUuid);
}
