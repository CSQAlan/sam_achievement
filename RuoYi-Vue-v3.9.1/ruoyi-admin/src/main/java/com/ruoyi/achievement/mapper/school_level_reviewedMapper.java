package com.ruoyi.achievement.mapper;

import java.util.List;
import com.ruoyi.achievement.domain.school_level_reviewed;
import com.ruoyi.achievement.domain.SamAchievementParticipant;

/**
 * 校级已审核Mapper接口
 * 
 * @author cyy
 * @date 2026-02-03
 */
public interface school_level_reviewedMapper 
{
    /**
     * 查询校级已审核
     * 
     * @param achievementId 校级已审核主键
     * @return 校级已审核
     */
    public school_level_reviewed selectschool_level_reviewedByAchievementId(String achievementId);

    /**
     * 查询校级已审核列表
     * 
     * @param school_level_reviewed 校级已审核
     * @return 校级已审核集合
     */
    public List<school_level_reviewed> selectschool_level_reviewedList(school_level_reviewed school_level_reviewed);

    /**
     * 新增校级已审核
     * 
     * @param school_level_reviewed 校级已审核
     * @return 结果
     */
    public int insertschool_level_reviewed(school_level_reviewed school_level_reviewed);

    /**
     * 修改校级已审核
     * 
     * @param school_level_reviewed 校级已审核
     * @return 结果
     */
    public int updateschool_level_reviewed(school_level_reviewed school_level_reviewed);

    /**
     * 删除校级已审核
     * 
     * @param achievementId 校级已审核主键
     * @return 结果
     */
    public int deleteschool_level_reviewedByAchievementId(String achievementId);

    /**
     * 批量删除校级已审核
     * 
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteschool_level_reviewedByAchievementIds(String[] achievementIds);

    /**
     * 批量删除参赛选手
     * 
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamAchievementParticipantByParticipantIds(String[] achievementIds);
    
    /**
     * 批量新增参赛选手
     * 
     * @param samAchievementParticipantList 参赛选手列表
     * @return 结果
     */
    public int batchSamAchievementParticipant(List<SamAchievementParticipant> samAchievementParticipantList);
    

    /**
     * 通过校级已审核主键删除参赛选手信息
     * 
     * @param achievementId 校级已审核ID
     * @return 结果
     */
    public int deleteSamAchievementParticipantByParticipantId(String achievementId);
}
