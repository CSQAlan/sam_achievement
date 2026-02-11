package com.ruoyi.achievement.service;

import java.util.List;
import com.ruoyi.achievement.domain.SamAchievement;

/**
 * 成果录入Service接口
 * 
 * @author 王璨
 * @date 2026-02-03
 */
public interface ISamAchievementService 
{
    /**
     * 查询成果录入
     * 
     * @param achievementId 成果录入主键
     * @return 成果录入
     */
    public SamAchievement selectSamAchievementByAchievementId(String achievementId);

    /**
     * 查询成果录入列表
     * 
     * @param samAchievement 成果录入
     * @return 成果录入集合
     */
    public List<SamAchievement> selectSamAchievementList(SamAchievement samAchievement);

    /**
     * 新增成果录入
     * 
     * @param samAchievement 成果录入
     * @return 结果
     */
    public int insertSamAchievement(SamAchievement samAchievement);

    /**
     * 修改成果录入
     * 
     * @param samAchievement 成果录入
     * @return 结果
     */
    public int updateSamAchievement(SamAchievement samAchievement);

    /**
     * 批量删除成果录入
     * 
     * @param achievementIds 需要删除的成果录入主键集合
     * @return 结果
     */
    public int deleteSamAchievementByAchievementIds(String[] achievementIds);

    /**
     * 删除成果录入信息
     * 
     * @param achievementId 成果录入主键
     * @return 结果
     */
    public int deleteSamAchievementByAchievementId(String achievementId);
    /**
     * 查询我参与的成果列表
     * * @param samAchievement 成果录入
     * @return 成果录入集合
     */
    public List<SamAchievement> selectSamAchievementListByStudentId(SamAchievement samAchievement);

    /**
     * 查询我指导的成果列表
     * * @param samAchievement 成果录入
     * @return 成果录入集合
     */
    public List<SamAchievement> selectSamAchievementListByTeacherId(SamAchievement samAchievement);
}
