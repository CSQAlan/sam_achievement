package com.ruoyi.achievement.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.school_level_unreviewedMapper;
import com.ruoyi.achievement.domain.school_level_unreviewed;
import com.ruoyi.achievement.service.Ischool_level_unreviewedService;

/**
 * 校级未审核Service业务层处理
 * 
 * @author cyy
 * @date 2026-02-03
 */
@Service
public class school_level_unreviewedServiceImpl implements Ischool_level_unreviewedService 
{
    @Autowired
    private school_level_unreviewedMapper school_level_unreviewedMapper;

    /**
     * 查询校级未审核
     * 
     * @param achievementId 校级未审核主键
     * @return 校级未审核
     */
    @Override
    public school_level_unreviewed selectschool_level_unreviewedByAchievementId(String achievementId)
    {
        return school_level_unreviewedMapper.selectschool_level_unreviewedByAchievementId(achievementId);
    }

    /**
     * 查询校级未审核列表
     * 
     * @param school_level_unreviewed 校级未审核
     * @return 校级未审核
     */
    @Override
    public List<school_level_unreviewed> selectschool_level_unreviewedList(school_level_unreviewed school_level_unreviewed)
    {
        return school_level_unreviewedMapper.selectschool_level_unreviewedList(school_level_unreviewed);
    }

    /**
     * 新增校级未审核
     * 
     * @param school_level_unreviewed 校级未审核
     * @return 结果
     */
    @Transactional
    @Override
    public int insertschool_level_unreviewed(school_level_unreviewed school_level_unreviewed)
    {
        school_level_unreviewed.setCreateTime(DateUtils.getNowDate());
        int rows = school_level_unreviewedMapper.insertschool_level_unreviewed(school_level_unreviewed);
        insertSamAchievementParticipant(school_level_unreviewed);
        return rows;
    }

    /**
     * 修改校级未审核
     * 
     * @param school_level_unreviewed 校级未审核
     * @return 结果
     */
    @Transactional
    @Override
    public int updateschool_level_unreviewed(school_level_unreviewed school_level_unreviewed)
    {
        school_level_unreviewed.setUpdateTime(DateUtils.getNowDate());
        school_level_unreviewedMapper.deleteSamAchievementParticipantByParticipantId(school_level_unreviewed.getAchievementId());
        insertSamAchievementParticipant(school_level_unreviewed);
        return school_level_unreviewedMapper.updateschool_level_unreviewed(school_level_unreviewed);
    }

    /**
     * 批量删除校级未审核
     * 
     * @param achievementIds 需要删除的校级未审核主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteschool_level_unreviewedByAchievementIds(String[] achievementIds)
    {
        school_level_unreviewedMapper.deleteSamAchievementParticipantByParticipantIds(achievementIds);
        return school_level_unreviewedMapper.deleteschool_level_unreviewedByAchievementIds(achievementIds);
    }

    /**
     * 删除校级未审核信息
     * 
     * @param achievementId 校级未审核主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteschool_level_unreviewedByAchievementId(String achievementId)
    {
        school_level_unreviewedMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        return school_level_unreviewedMapper.deleteschool_level_unreviewedByAchievementId(achievementId);
    }

    /**
     * 新增参赛选手信息
     * 
     * @param school_level_unreviewed 校级未审核对象
     */
    public void insertSamAchievementParticipant(school_level_unreviewed school_level_unreviewed)
    {
        List<SamAchievementParticipant> samAchievementParticipantList = school_level_unreviewed.getSamAchievementParticipantList();
        String achievementId = school_level_unreviewed.getAchievementId();
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
                school_level_unreviewedMapper.batchSamAchievementParticipant(list);
            }
        }
    }
}
