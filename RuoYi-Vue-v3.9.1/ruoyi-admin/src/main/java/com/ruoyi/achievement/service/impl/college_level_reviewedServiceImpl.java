package com.ruoyi.achievement.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.college_level_reviewedMapper;
import com.ruoyi.achievement.domain.college_level_reviewed;
import com.ruoyi.achievement.service.Icollege_level_reviewedService;

/**
 * 院级已审核Service业务层处理
 * 
 * @author cyy
 * @date 2026-02-03
 */
@Service
public class college_level_reviewedServiceImpl implements Icollege_level_reviewedService 
{
    @Autowired
    private college_level_reviewedMapper college_level_reviewedMapper;

    /**
     * 查询院级已审核
     * 
     * @param achievementId 院级已审核主键
     * @return 院级已审核
     */
    @Override
    public college_level_reviewed selectcollege_level_reviewedByAchievementId(String achievementId)
    {
        return college_level_reviewedMapper.selectcollege_level_reviewedByAchievementId(achievementId);
    }

    /**
     * 查询院级已审核列表
     * 
     * @param college_level_reviewed 院级已审核
     * @return 院级已审核
     */
    @Override
    public List<college_level_reviewed> selectcollege_level_reviewedList(college_level_reviewed college_level_reviewed)
    {
        return college_level_reviewedMapper.selectcollege_level_reviewedList(college_level_reviewed);
    }

    /**
     * 新增院级已审核
     * 
     * @param college_level_reviewed 院级已审核
     * @return 结果
     */
    @Transactional
    @Override
    public int insertcollege_level_reviewed(college_level_reviewed college_level_reviewed)
    {
        college_level_reviewed.setCreateTime(DateUtils.getNowDate());
        int rows = college_level_reviewedMapper.insertcollege_level_reviewed(college_level_reviewed);
        insertSamAchievementParticipant(college_level_reviewed);
        return rows;
    }

    /**
     * 修改院级已审核
     * 
     * @param college_level_reviewed 院级已审核
     * @return 结果
     */
    @Transactional
    @Override
    public int updatecollege_level_reviewed(college_level_reviewed college_level_reviewed)
    {
        college_level_reviewed.setUpdateTime(DateUtils.getNowDate());
        college_level_reviewedMapper.deleteSamAchievementParticipantByParticipantId(college_level_reviewed.getAchievementId());
        insertSamAchievementParticipant(college_level_reviewed);
        return college_level_reviewedMapper.updatecollege_level_reviewed(college_level_reviewed);
    }

    /**
     * 批量删除院级已审核
     * 
     * @param achievementIds 需要删除的院级已审核主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletecollege_level_reviewedByAchievementIds(String[] achievementIds)
    {
        college_level_reviewedMapper.deleteSamAchievementParticipantByParticipantIds(achievementIds);
        return college_level_reviewedMapper.deletecollege_level_reviewedByAchievementIds(achievementIds);
    }

    /**
     * 删除院级已审核信息
     * 
     * @param achievementId 院级已审核主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletecollege_level_reviewedByAchievementId(String achievementId)
    {
        college_level_reviewedMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        return college_level_reviewedMapper.deletecollege_level_reviewedByAchievementId(achievementId);
    }

    /**
     * 新增参赛选手信息
     * 
     * @param college_level_reviewed 院级已审核对象
     */
    public void insertSamAchievementParticipant(college_level_reviewed college_level_reviewed)
    {
        List<SamAchievementParticipant> samAchievementParticipantList = college_level_reviewed.getSamAchievementParticipantList();
        String achievementId = college_level_reviewed.getAchievementId();
        if (StringUtils.isNotNull(samAchievementParticipantList))
        {
            List<SamAchievementParticipant> list = new ArrayList<SamAchievementParticipant>();
            for (SamAchievementParticipant samAchievementParticipant : samAchievementParticipantList)
            {
                samAchievementParticipant.setParticipantId(achievementId);
                list.add(samAchievementParticipant);
            }
            if (list.size() > 0)
            {
                college_level_reviewedMapper.batchSamAchievementParticipant(list);
            }
        }
    }
}
