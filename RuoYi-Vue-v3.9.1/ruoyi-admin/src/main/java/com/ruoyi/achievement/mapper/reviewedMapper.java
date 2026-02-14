package com.ruoyi.achievement.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.achievement.domain.reviewed;
import com.ruoyi.achievement.domain.SamAchievementParticipant;

/**
 * 成果审核Mapper接口
 * 
 * @author cyy
 * @date 2026-02-03
 */
@Mapper
public interface reviewedMapper 
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
     * 修改成果审核
     * 
     * @param reviewed 成果审核
     * @return 结果
     */
    public int updatereviewed(reviewed reviewed);

    /**
     * 删除成果审核
     * 
     * @param achievementId 成果ID
     * @return 结果
     */
    public int deletereviewedByAchievementId(String achievementId);

    /**
     * 批量删除成果审核
     * 
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletereviewedByAchievementIds(String[] achievementIds);

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
     * 通过成果ID删除参赛选手信息
     * 
     * @param achievementId 成果ID
     * @return 结果
     */
    public int deleteSamAchievementParticipantByParticipantId(String achievementId);
}
