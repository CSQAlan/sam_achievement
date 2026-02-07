package com.ruoyi.achievement.service.impl;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.SamAchievementMapper;
import com.ruoyi.achievement.service.ISamAchievementService;

/**
 * 成果录入Service业务层处理
 */
@Service
public class SamAchievementServiceImpl implements ISamAchievementService
{
    @Autowired
    private SamAchievementMapper samAchievementMapper;

    /**
     * 查询成果录入
     */
    @Override
    public SamAchievement selectSamAchievementByAchievementId(String achievementId)
    {
        return samAchievementMapper.selectSamAchievementByAchievementId(achievementId);
    }

    /**
     * 查询成果录入列表
     */
    @Override
    public List<SamAchievement> selectSamAchievementList(SamAchievement samAchievement)
    {
        return samAchievementMapper.selectSamAchievementList(samAchievement);
    }

    /**
     * 新增成果录入
     */
    @Transactional
    @Override
    public int insertSamAchievement(SamAchievement samAchievement)
    {
        // 1. 自动提取年份 (数据库字段为 int，此处根据 awardTime 计算)
        if (samAchievement.getAwardTime() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(samAchievement.getAwardTime());
            samAchievement.setYear((long) cal.get(Calendar.YEAR));
        }

        // 2. 注意：数据库是 auto_increment，不要手动 setAchievementId
        // 必须依靠 XML 中的 useGeneratedKeys="true" 来回填 ID
        samAchievement.setCreateTime(DateUtils.getNowDate());

        // 3. 执行主表插入
        int rows = samAchievementMapper.insertSamAchievement(samAchievement);

        // 4. 执行子表插入 (此时 samAchievement 已经拿到了回填的自增 ID)
        insertSamAchievementParticipant(samAchievement);

        return rows;
    }

    /**
     * 修改成果录入
     */
    @Transactional
    @Override
    public int updateSamAchievement(SamAchievement samAchievement)
    {
        samAchievement.setUpdateTime(DateUtils.getNowDate());
        // 修改时先删除旧的关联选手，再重新插入
        samAchievementMapper.deleteSamAchievementParticipantByParticipantId(samAchievement.getAchievementId());
        insertSamAchievementParticipant(samAchievement);
        return samAchievementMapper.updateSamAchievement(samAchievement);
    }
    public void insertSamAchievementParticipant(SamAchievement samAchievement)
    {
        List<SamAchievementParticipant> samAchievementParticipantList = samAchievement.getSamAchievementParticipantList();
        // 获取主表自动生成的 ID
        String achievementId = samAchievement.getAchievementId();

        if (StringUtils.isNotNull(samAchievementParticipantList))
        {
            List<SamAchievementParticipant> list = new ArrayList<SamAchievementParticipant>();
            for (SamAchievementParticipant participant : samAchievementParticipantList)
            {
                // 1. 设置外键关联
                participant.setAchievementId(achievementId);

                // 2. 清空子表主键 (让数据库自增)
                participant.setParticipantId(null);

                // 3. 补全基础字段
                participant.setCreateBy(samAchievement.getCreateBy());
                participant.setCreateTime(DateUtils.getNowDate());
                participant.setUpdateTime(DateUtils.getNowDate()); // 刚才补的

                // --- 核心修复：设置删除标志 (0代表正常，2代表删除) ---
                // 如果你的实体类里 delFlag 是 String 类型，就填 "0"
                // 如果是 Integer/Long 类型，就填 0
                participant.setDelFlag(0L); // 这里的类型(0 或 "0")请根据你的实体类定义自行调整

                list.add(participant);
            }
            if (list.size() > 0)
            {
                samAchievementMapper.batchSamAchievementParticipant(list);
            }
        }
    }

    /**
     * 批量删除成果录入
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
     */
    @Transactional
    @Override
    public int deleteSamAchievementByAchievementId(String achievementId)
    {
        samAchievementMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        return samAchievementMapper.deleteSamAchievementByAchievementId(achievementId);
    }
}