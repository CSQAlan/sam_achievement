package com.ruoyi.achievement.mapper;

import java.util.List;
import com.ruoyi.achievement.domain.college_level_reviewed;
import com.ruoyi.achievement.domain.SamAchievementParticipant;

/**
 * 院级已审核Mapper接口
 * 
 * @author cyy
 * @date 2026-02-03
 */
public interface college_level_reviewedMapper 
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
     * 删除院级已审核
     * 
     * @param achievementId 院级已审核主键
     * @return 结果
     */
    public int deletecollege_level_reviewedByAchievementId(String achievementId);

    /**
     * 批量删除院级已审核
     * 
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletecollege_level_reviewedByAchievementIds(String[] achievementIds);

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
     * 通过院级已审核主键删除参赛选手信息
     * 
     * @param achievementId 院级已审核ID
     * @return 结果
     */
    public int deleteSamAchievementParticipantByParticipantId(String achievementId);
}
