package com.ruoyi.achievement.mapper;

import java.util.List;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.domain.SamAchievementAdvisor;
import com.ruoyi.achievement.domain.SamAchievementParticipant;

/**
 * 成果录入Mapper接口
 * 
 * @author 王璨
 * @date 2026-02-03
 */
public interface SamAchievementMapper 
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
     * Get next achievement id (row-locked in transaction).
     *
     * @return next id
     */
    public Long selectNextAchievementId();

    /**
     * Increment achievement id sequence (row-locked in transaction).
     *
     * @return affected rows
     */
    public int incrementNextAchievementId();

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
     * 删除成果录入
     * 
     * @param achievementId 成果录入主键
     * @return 结果
     */
    public int deleteSamAchievementByAchievementId(String achievementId);

    /**
     * 批量删除成果录入
     * 
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamAchievementByAchievementIds(String[] achievementIds);

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
     * 通过成果录入主键删除参赛选手信息
     * 
     * @param achievementId 成果录入ID
     * @return 结果
     */
    public int deleteSamAchievementParticipantByParticipantId(String achievementId);
    public List<SamAchievement> selectSamAchievementListByStudentId(SamAchievement samAchievement);
    public List<SamAchievement> selectSamAchievementListByTeacherId(SamAchievement samAchievement);
    // 在 SamAchievementMapper 接口中添加以下方法

    /**
     * 批量新增指导老师
     */
    public int batchSamAchievementAdvisor(List<SamAchievementAdvisor> samAchievementAdvisorList);

    /**
     * 通过成果ID删除指导老师
     */
    public int deleteSamAchievementAdvisorByAchievementId(String achievementId);

    /**
     * 批量通过成果ID删除指导老师
     */
    public int deleteSamAchievementAdvisorByAchievementIds(String[] achievementIds);

    /**
     * 批量新增成果附件
     */
    public int batchSamAchievementAttachment(List<java.util.Map<String, Object>> samAchievementAttachmentList);

    /**
     * 通过成果ID删除成果附件
     */
    public int deleteSamAchievementAttachmentByAchievementId(String achievementId);

    /**
     * 批量通过成果ID删除成果附件
     */
    public int deleteSamAchievementAttachmentByAchievementIds(String[] achievementIds);
}

