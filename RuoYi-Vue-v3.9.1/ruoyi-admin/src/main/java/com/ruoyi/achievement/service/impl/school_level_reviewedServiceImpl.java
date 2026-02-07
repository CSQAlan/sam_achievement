package com.ruoyi.achievement.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.school_level_reviewedMapper;
import com.ruoyi.achievement.domain.school_level_reviewed;
import com.ruoyi.achievement.service.Ischool_level_reviewedService;

/**
 * 校级已审核Service业务层处理
 * 
 * @author cyy
 * @date 2026-02-03
 */
@Service
public class school_level_reviewedServiceImpl implements Ischool_level_reviewedService 
{
    @Autowired
    private school_level_reviewedMapper school_level_reviewedMapper;

    /**
     * 查询校级已审核
     * 
     * @param achievementId 校级已审核主键
     * @return 校级已审核
     */
    @Override
    public school_level_reviewed selectschool_level_reviewedByAchievementId(String achievementId)
    {
        return school_level_reviewedMapper.selectschool_level_reviewedByAchievementId(achievementId);
    }

    /**
     * 查询校级已审核列表
     * 
     * @param school_level_reviewed 校级已审核
     * @return 校级已审核
     */
    @Override
    public List<school_level_reviewed> selectschool_level_reviewedList(school_level_reviewed school_level_reviewed)
    {
        return school_level_reviewedMapper.selectschool_level_reviewedList(school_level_reviewed);
    }

    /**
     * 新增校级已审核
     * 
     * @param school_level_reviewed 校级已审核
     * @return 结果
     */
    @Transactional
    @Override
    public int insertschool_level_reviewed(school_level_reviewed school_level_reviewed)
    {
        school_level_reviewed.setCreateTime(DateUtils.getNowDate());
        int rows = school_level_reviewedMapper.insertschool_level_reviewed(school_level_reviewed);
        insertSamAchievementParticipant(school_level_reviewed);
        return rows;
    }

    /**
     * 修改校级已审核
     * 
     * @param school_level_reviewed 校级已审核
     * @return 结果
     */
    @Transactional
    @Override
    public int updateschool_level_reviewed(school_level_reviewed school_level_reviewed)
    {
        school_level_reviewed.setUpdateTime(DateUtils.getNowDate());
        school_level_reviewedMapper.deleteSamAchievementParticipantByParticipantId(school_level_reviewed.getAchievementId());
        insertSamAchievementParticipant(school_level_reviewed);
        return school_level_reviewedMapper.updateschool_level_reviewed(school_level_reviewed);
    }

    /**
     * 批量删除校级已审核
     * 
     * @param achievementIds 需要删除的校级已审核主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteschool_level_reviewedByAchievementIds(String[] achievementIds)
    {
        school_level_reviewedMapper.deleteSamAchievementParticipantByParticipantIds(achievementIds);
        return school_level_reviewedMapper.deleteschool_level_reviewedByAchievementIds(achievementIds);
    }

    /**
     * 删除校级已审核信息
     * 
     * @param achievementId 校级已审核主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteschool_level_reviewedByAchievementId(String achievementId)
    {
        school_level_reviewedMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        return school_level_reviewedMapper.deleteschool_level_reviewedByAchievementId(achievementId);
    }

    /**
     * 新增参赛选手信息
     * 
     * @param school_level_reviewed 校级已审核对象
     */
    public void insertSamAchievementParticipant(school_level_reviewed school_level_reviewed)
    {
        List<SamAchievementParticipant> samAchievementParticipantList = school_level_reviewed.getSamAchievementParticipantList();
        String achievementId = school_level_reviewed.getAchievementId();
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
                school_level_reviewedMapper.batchSamAchievementParticipant(list);
            }
        }
    }
}
