package com.ruoyi.achievement.service;

import java.util.List;
import com.ruoyi.achievement.domain.school_level_reviewed;

/**
 * 校级已审核Service接口
 * 
 * @author cyy
 * @date 2026-02-03
 */
public interface Ischool_level_reviewedService 
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
     * 批量删除校级已审核
     * 
     * @param achievementIds 需要删除的校级已审核主键集合
     * @return 结果
     */
    public int deleteschool_level_reviewedByAchievementIds(String[] achievementIds);

    /**
     * 删除校级已审核信息
     * 
     * @param achievementId 校级已审核主键
     * @return 结果
     */
    public int deleteschool_level_reviewedByAchievementId(String achievementId);
}
