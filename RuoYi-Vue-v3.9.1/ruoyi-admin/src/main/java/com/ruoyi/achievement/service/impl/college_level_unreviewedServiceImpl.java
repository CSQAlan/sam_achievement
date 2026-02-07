package com.ruoyi.achievement.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.college_level_unreviewedMapper;
import com.ruoyi.achievement.domain.college_level_unreviewed;
import com.ruoyi.achievement.service.Icollege_level_unreviewedService;

/**
 * 院级未审核Service业务层处理
 * 
 * @author cyy
 * @date 2026-02-03
 */
@Service
public class college_level_unreviewedServiceImpl implements Icollege_level_unreviewedService 
{
    @Autowired
    private college_level_unreviewedMapper college_level_unreviewedMapper;

    /**
     * 查询院级未审核
     * 
     * @param achievementId 院级未审核主键
     * @return 院级未审核
     */
    @Override
    public college_level_unreviewed selectcollege_level_unreviewedByAchievementId(String achievementId)
    {
        return college_level_unreviewedMapper.selectcollege_level_unreviewedByAchievementId(achievementId);
    }

    /**
     * 查询院级未审核列表
     * 
     * @param college_level_unreviewed 院级未审核
     * @return 院级未审核
     */
    @Override
    public List<college_level_unreviewed> selectcollege_level_unreviewedList(college_level_unreviewed college_level_unreviewed)
    {
        return college_level_unreviewedMapper.selectcollege_level_unreviewedList(college_level_unreviewed);
    }

    /**
     * 新增院级未审核
     * 
     * @param college_level_unreviewed 院级未审核
     * @return 结果
     */
    @Transactional
    @Override
    public int insertcollege_level_unreviewed(college_level_unreviewed college_level_unreviewed)
    {
        college_level_unreviewed.setCreateTime(DateUtils.getNowDate());
        int rows = college_level_unreviewedMapper.insertcollege_level_unreviewed(college_level_unreviewed);
        insertSamAchievementParticipant(college_level_unreviewed);
        return rows;
    }

    /**
     * 修改院级未审核
     * 
     * @param college_level_unreviewed 院级未审核
     * @return 结果
     */
    @Transactional
    @Override
    public int updatecollege_level_unreviewed(college_level_unreviewed college_level_unreviewed)
    {
        college_level_unreviewed.setUpdateTime(DateUtils.getNowDate());
        college_level_unreviewedMapper.deleteSamAchievementParticipantByParticipantId(college_level_unreviewed.getAchievementId());
        insertSamAchievementParticipant(college_level_unreviewed);
        return college_level_unreviewedMapper.updatecollege_level_unreviewed(college_level_unreviewed);
    }

    /**
     * 批量删除院级未审核
     * 
     * @param achievementIds 需要删除的院级未审核主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletecollege_level_unreviewedByAchievementIds(String[] achievementIds)
    {
        college_level_unreviewedMapper.deleteSamAchievementParticipantByParticipantIds(achievementIds);
        return college_level_unreviewedMapper.deletecollege_level_unreviewedByAchievementIds(achievementIds);
    }

    /**
     * 删除院级未审核信息
     * 
     * @param achievementId 院级未审核主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletecollege_level_unreviewedByAchievementId(String achievementId)
    {
        college_level_unreviewedMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        return college_level_unreviewedMapper.deletecollege_level_unreviewedByAchievementId(achievementId);
    }

    /**
     * 新增参赛选手信息
     * 
     * @param college_level_unreviewed 院级未审核对象
     */
    public void insertSamAchievementParticipant(college_level_unreviewed college_level_unreviewed)
    {
        List<SamAchievementParticipant> samAchievementParticipantList = college_level_unreviewed.getSamAchievementParticipantList();
        String achievementId = college_level_unreviewed.getAchievementId();
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
                college_level_unreviewedMapper.batchSamAchievementParticipant(list);
            }
        }
    }
}
