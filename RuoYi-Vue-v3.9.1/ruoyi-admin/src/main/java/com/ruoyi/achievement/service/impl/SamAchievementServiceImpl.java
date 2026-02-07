package com.ruoyi.achievement.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.SamAchievementMapper;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.service.ISamAchievementService;

/**
 * 成果录入Service业务层处�?
 * 
 * @author 王璨
 * @date 2026-02-03
 */
@Service
public class SamAchievementServiceImpl implements ISamAchievementService 
{
    @Autowired
    private SamAchievementMapper samAchievementMapper;

    /**
     * 查询成果录入
     * 
     * @param achievementId 成果录入主键
     * @return 成果录入
     */
    @Override
    public SamAchievement selectSamAchievementByAchievementId(String achievementId)
    {
        return samAchievementMapper.selectSamAchievementByAchievementId(achievementId);
    }

    /**
     * 查询成果录入列表
     * 
     * @param samAchievement 成果录入
     * @return 成果录入
     */
    @Override
    public List<SamAchievement> selectSamAchievementList(SamAchievement samAchievement)
    {
        return samAchievementMapper.selectSamAchievementList(samAchievement);
    }

    /**
     * 新增成果录入
     * 
     * @param samAchievement 成果录入
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSamAchievement(SamAchievement samAchievement)
    {
        if (!StringUtils.hasText(samAchievement.getAchievementId()))
        {
            Long nextId = samAchievementMapper.selectNextAchievementId();
            samAchievementMapper.incrementNextAchievementId();
            samAchievement.setAchievementId(String.valueOf(nextId));
        }

        samAchievement.setCreateTime(DateUtils.getNowDate());
        int rows = samAchievementMapper.insertSamAchievement(samAchievement);
        insertSamAchievementParticipant(samAchievement);
        return rows;
    }

    /**
     * 修改成果录入
     * 
     * @param samAchievement 成果录入
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSamAchievement(SamAchievement samAchievement)
    {
        samAchievement.setUpdateTime(DateUtils.getNowDate());
        samAchievementMapper.deleteSamAchievementParticipantByParticipantId(samAchievement.getAchievementId());
        insertSamAchievementParticipant(samAchievement);
        return samAchievementMapper.updateSamAchievement(samAchievement);
    }

    /**
     * 批量删除成果录入
     * 
     * @param achievementIds 需要删除的成果录入主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSamAchievementByAchievementIds(String[] achievementIds)
    {
        samAchievementMapper.deleteSamAchievementParticipantByParticipantIds(achievementIds);
        return samAchievementMapper.deleteSamAchievementByAchievementIds(achievementIds);
    }

    /**
     * 删除成果录入信息
     * 
     * @param achievementId 成果录入主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSamAchievementByAchievementId(String achievementId)
    {
        samAchievementMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        return samAchievementMapper.deleteSamAchievementByAchievementId(achievementId);
    }

    /**
     * 新增参赛选手信息
     * 
     * @param samAchievement 成果录入对象
     */
    public void insertSamAchievementParticipant(SamAchievement samAchievement)
    {
        List<SamAchievementParticipant> samAchievementParticipantList = samAchievement.getSamAchievementParticipantList();
        String achievementId = samAchievement.getAchievementId();
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
                samAchievementMapper.batchSamAchievementParticipant(list);
            }
        }
    }
}



