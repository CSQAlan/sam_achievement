package com.ruoyi.achievement.service;

import java.util.List;
import com.ruoyi.achievement.domain.college_level_reviewed;

/**
 * 院级已审核Service接口
 * 
 * @author cyy
 * @date 2026-02-03
 */
public interface Icollege_level_reviewedService 
{
    /**
     * 查询院级已审核
     * 
     * @param achievementId 院级已审核主键
     * @return 院级已审核
     */
    public college_level_reviewed selectcollege_level_reviewedByAchievementId(String achievementId);

    /**
     * 查询院级已审核列表
     * 
     * @param college_level_reviewed 院级已审核
     * @return 院级已审核集合
     */
    public List<college_level_reviewed> selectcollege_level_reviewedList(college_level_reviewed college_level_reviewed);

    /**
     * 新增院级已审核
     * 
     * @param college_level_reviewed 院级已审核
     * @return 结果
     */
    public int insertcollege_level_reviewed(college_level_reviewed college_level_reviewed);

    /**
     * 修改院级已审核
     * 
     * @param college_level_reviewed 院级已审核
     * @return 结果
     */
    public int updatecollege_level_reviewed(college_level_reviewed college_level_reviewed);

    /**
     * 批量删除院级已审核
     * 
     * @param achievementIds 需要删除的院级已审核主键集合
     * @return 结果
     */
    public int deletecollege_level_reviewedByAchievementIds(String[] achievementIds);

    /**
     * 删除院级已审核信息
     * 
     * @param achievementId 院级已审核主键
     * @return 结果
     */
    public int deletecollege_level_reviewedByAchievementId(String achievementId);
}
