package com.ruoyi.achievement.mapper;

import java.util.List;
import com.ruoyi.achievement.domain.school_level_unreviewed;
import com.ruoyi.achievement.domain.SamAchievementParticipant;

/**
 * 校级未审核Mapper接口
 * 
 * @author cyy
 * @date 2026-02-03
 */
public interface school_level_unreviewedMapper 
{
    /**
     * 查询校级未审核
     * 
     * @param achievementId 校级未审核主键
     * @return 校级未审核
     */
    public school_level_unreviewed selectschool_level_unreviewedByAchievementId(String achievementId);

    /**
     * 查询校级未审核列表
     * 
     * @param school_level_unreviewed 校级未审核
     * @return 校级未审核集合
     */
    public List<school_level_unreviewed> selectschool_level_unreviewedList(school_level_unreviewed school_level_unreviewed);

    /**
     * 新增校级未审核
     * 
     * @param school_level_unreviewed 校级未审核
     * @return 结果
     */
    public int insertschool_level_unreviewed(school_level_unreviewed school_level_unreviewed);

    /**
     * 修改校级未审核
     * 
     * @param school_level_unreviewed 校级未审核
     * @return 结果
     */
    public int updateschool_level_unreviewed(school_level_unreviewed school_level_unreviewed);

    /**
     * 删除校级未审核
     * 
     * @param achievementId 校级未审核主键
     * @return 结果
     */
    public int deleteschool_level_unreviewedByAchievementId(String achievementId);

    /**
     * 批量删除校级未审核
     * 
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteschool_level_unreviewedByAchievementIds(String[] achievementIds);

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
     * 通过校级未审核主键删除参赛选手信息
     * 
     * @param achievementId 校级未审核ID
     * @return 结果
     */
    public int deleteSamAchievementParticipantByParticipantId(String achievementId);
}
