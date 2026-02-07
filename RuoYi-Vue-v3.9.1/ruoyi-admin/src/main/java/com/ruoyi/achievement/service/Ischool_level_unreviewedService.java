package com.ruoyi.achievement.service;

import java.util.List;
import com.ruoyi.achievement.domain.school_level_unreviewed;

/**
 * 校级未审核Service接口
 * 
 * @author cyy
 * @date 2026-02-03
 */
public interface Ischool_level_unreviewedService 
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
     * 批量删除校级未审核
     * 
     * @param achievementIds 需要删除的校级未审核主键集合
     * @return 结果
     */
    public int deleteschool_level_unreviewedByAchievementIds(String[] achievementIds);

    /**
     * 删除校级未审核信息
     * 
     * @param achievementId 校级未审核主键
     * @return 结果
     */
    public int deleteschool_level_unreviewedByAchievementId(String achievementId);
}
