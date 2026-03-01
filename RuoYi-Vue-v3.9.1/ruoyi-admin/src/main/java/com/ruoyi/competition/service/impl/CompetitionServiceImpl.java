package com.ruoyi.competition.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.competition.domain.CompetitionDeptRel;
import com.ruoyi.competition.mapper.CompetitionMapper;
import com.ruoyi.competition.domain.Competition;
import com.ruoyi.competition.service.ICompetitionService;

/**
 * 总赛事Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
@Service
public class CompetitionServiceImpl implements ICompetitionService 
{
    @Autowired
    private CompetitionMapper competitionMapper;

    /**
     * 查询总赛事
     * 
     * @param id 总赛事主键
     * @return 总赛事
     */
    @Override
    public Competition selectCompetitionById(Long id)
    {
        return competitionMapper.selectCompetitionById(id);
    }

    /**
     * 查询总赛事列表
     * 
     * @param competition 总赛事
     * @return 总赛事
     */
    @Override
    public List<Competition> selectCompetitionList(Competition competition)
    {
        return competitionMapper.selectCompetitionList(competition);
    }

    /**
     * 新增总赛事
     * 
     * @param competition 总赛事
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCompetition(Competition competition)
    {
        competition.setCreateTime(DateUtils.getNowDate());
        int rows = competitionMapper.insertCompetition(competition);
        insertCompetitionDeptRel(competition);
        return rows;
    }

    /**
     * 修改总赛事
     * 
     * @param competition 总赛事
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCompetition(Competition competition)
    {
        competition.setUpdateTime(DateUtils.getNowDate());
        competitionMapper.deleteCompetitionDeptRelBySessionId(competition.getId());
        insertCompetitionDeptRel(competition);
        return competitionMapper.updateCompetition(competition);
    }

    /**
     * 批量删除总赛事
     * 
     * @param ids 需要删除的总赛事主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCompetitionByIds(Long[] ids)
    {
        competitionMapper.deleteCompetitionDeptRelBySessionIds(ids);
        return competitionMapper.deleteCompetitionByIds(ids);
    }

    /**
     * 删除总赛事信息
     * 
     * @param id 总赛事主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCompetitionById(Long id)
    {
        competitionMapper.deleteCompetitionDeptRelBySessionId(id);
        return competitionMapper.deleteCompetitionById(id);
    }

    /**
     * 新增赛事-部门关系信息
     * 
     * @param competition 总赛事对象
     */
    public void insertCompetitionDeptRel(Competition competition)
    {
        List<CompetitionDeptRel> competitionDeptRelList = competition.getCompetitionDeptRelList();
        Long id = competition.getId();
        if (StringUtils.isNotNull(competitionDeptRelList))
        {
            List<CompetitionDeptRel> list = new ArrayList<CompetitionDeptRel>();
            for (CompetitionDeptRel competitionDeptRel : competitionDeptRelList)
            {
                competitionDeptRel.setSessionId(id);
                list.add(competitionDeptRel);
            }
            if (list.size() > 0)
            {
                competitionMapper.batchCompetitionDeptRel(list);
            }
        }
    }

    // ========== 实现新增的按名称查询方法 ==========
    @Override
    public Competition selectCompetitionByCompName(String compName) {
        Competition query = new Competition();
        query.setName(compName); // 假设赛事主表中“名称”对应的字段是name
        List<Competition> list = competitionMapper.selectCompetitionList(query);
        // 按名称查询，返回第一个匹配的实体（默认赛事名称唯一）
        return list.isEmpty() ? null : list.get(0);
    }


}
