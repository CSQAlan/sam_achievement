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
     * 批量更新文件状态
     * @param uuids UUID数组
     * @param isTemp 是否为临时文件 (0正式 1临时)
     */
    public int updateFileUuidStatus(@Param("uuids") String[] uuids, @Param("isTemp") Integer isTemp);

    /**
     * 查询超时的临时文件
     * @param hours 超时小时数
     */
    public List<FileUuid> selectTempFilesOlderThan(Integer hours);

    /**
     * 根据UUID删除记录
     */
    public int deleteFileUuidByUuid(String fileUuid);
}
