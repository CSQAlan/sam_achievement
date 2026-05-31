package com.ruoyi.competition.service;

import java.util.List;
import com.ruoyi.competition.domain.SamCompetitionImportLog;

/**
 * 竞赛导入日志Service接口
 * 
 * @author ruoyi
 * @date 2026-04-22
 */
public interface ISamCompetitionImportLogService {
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
     * 撤销导入操作
     * 
     * @param logId 日志ID
     * @return 撤销成功的竞赛记录数
     */
    public int undoImport(Long logId);
}
