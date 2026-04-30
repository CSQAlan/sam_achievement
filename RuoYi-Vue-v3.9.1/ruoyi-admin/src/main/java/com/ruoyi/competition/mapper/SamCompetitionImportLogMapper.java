package com.ruoyi.competition.mapper;

import java.util.List;
import com.ruoyi.competition.domain.SamCompetitionImportLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 竞赛导入日志Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-22
 */
@Mapper
public interface SamCompetitionImportLogMapper {
    /**
     * 查询竞赛导入日志
     * 
     * @param id 竞赛导入日志主键
     * @return 竞赛导入日志
     */
    public SamCompetitionImportLog selectSamCompetitionImportLogById(Long id);

    /**
     * 查询竞赛导入日志列表
     * 
     * @param samCompetitionImportLog 竞赛导入日志
     * @return 竞赛导入日志集合
     */
    public List<SamCompetitionImportLog> selectSamCompetitionImportLogList(SamCompetitionImportLog samCompetitionImportLog);

    /**
     * 新增竞赛导入日志
     * 
     * @param samCompetitionImportLog 竞赛导入日志
     * @return 结果
     */
    public int insertSamCompetitionImportLog(SamCompetitionImportLog samCompetitionImportLog);

    /**
     * 修改竞赛导入日志
     * 
     * @param samCompetitionImportLog 竞赛导入日志
     * @return 结果
     */
    public int updateSamCompetitionImportLog(SamCompetitionImportLog samCompetitionImportLog);

    /**
     * 删除竞赛导入日志
     * 
     * @param id 竞赛导入日志主键
     * @return 结果
     */
    public int deleteSamCompetitionImportLogById(Long id);

    /**
     * 批量删除竞赛导入日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamCompetitionImportLogByIds(Long[] ids);
}
