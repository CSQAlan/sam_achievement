package com.ruoyi.achievement.service;

import java.util.List;
import com.ruoyi.achievement.domain.reviewed;

/**
 * 成果审核Service接口
 * 
 * @author cyy
 * @date 2026-02-03
 */
public interface IreviewedService 
{
    /**
     * 查询成果审核
     * 
     * @param achievementId 成果ID
     * @return 成果审核
     */
    public reviewed selectreviewedByAchievementId(String achievementId);

    /**
     * 查询成果审核列表
     * 
     * @param reviewed 成果审核
     * @return 成果审核集合
     */
    public List<reviewed> selectreviewedList(reviewed reviewed);

    /**
     * 新增成果审核
     * 
     * @param reviewed 成果审核
     * @return 结果
     */
    public int insertreviewed(reviewed reviewed);

    /**
     * 审核
     * 
     * @param reviewed
     * @return 结果
     */
    public int updatereviewed(reviewed reviewed);

    /**
     * 批量删除成果审核
     * 
     * @param achievementIds 需要删除的成果ID集合
     * @return 结果
     */
    public int deletereviewedByAchievementIds(String[] achievementIds);

    /**
     * 删除成果审核信息
     * 
     * @param achievementId 成果ID
     * @return 结果
     */
    public int deletereviewedByAchievementId(String achievementId);
}
