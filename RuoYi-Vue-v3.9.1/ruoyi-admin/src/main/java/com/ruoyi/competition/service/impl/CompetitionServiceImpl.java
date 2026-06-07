package com.ruoyi.competition.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.enums.BizAuditOpType;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.competition.domain.CompetitionDeptRel;
import com.ruoyi.competition.domain.CompetitionTagRel;
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
    
    @Override
    public List<Competition> selectCompetitionByTag(String tagName)
    {
        return competitionMapper.selectCompetitionByTag(tagName);
    }

    /**
     * 新增总赛事
     * 
     * @param competition 总赛事
     * @return 结果
     */
    @Transactional
    @Override
    @BizAudit(bizType = "competition", bizName = "新增赛事", opType = BizAuditOpType.ADD, handler = "competitionBizAuditHandler", async = false)
    public int insertCompetition(Competition competition)
    {
        competition.setCreateTime(DateUtils.getNowDate());
        int rows = competitionMapper.insertCompetition(competition);
        insertCompetitionDeptRel(competition);
        insertCompetitionTagRel(competition);
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
    @BizAudit(bizType = "competition", bizName = "修改赛事", opType = BizAuditOpType.UPDATE, handler = "competitionBizAuditHandler", async = false)
    public int updateCompetition(Competition competition)
    {
        competition.setUpdateTime(DateUtils.getNowDate());
        competitionMapper.deleteCompetitionDeptRelBySessionId(competition.getId());
        insertCompetitionDeptRel(competition);
        competitionMapper.deleteCompetitionTagRelByCompetitionId(competition.getId());
        insertCompetitionTagRel(competition);
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
    @BizAudit(bizType = "competition", bizName = "批量删除赛事", opType = BizAuditOpType.DELETE, handler = "competitionBizAuditHandler", async = false)
    public int deleteCompetitionByIds(Long[] ids)
    {
        competitionMapper.deleteCompetitionDeptRelBySessionIds(ids);
        competitionMapper.deleteCompetitionTagRelByCompetitionIds(ids);
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
    @BizAudit(bizType = "competition", bizName = "删除赛事", opType = BizAuditOpType.DELETE, handler = "competitionBizAuditHandler", async = false)
    public int deleteCompetitionById(Long id)
    {
        competitionMapper.deleteCompetitionDeptRelBySessionId(id);
        competitionMapper.deleteCompetitionTagRelByCompetitionId(id);
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

    /**
     * 新增赛事-标签关联信息
     * 优先使用 competitionTagRelList 结构化数据，否则从 tags 字段解析
     */
    public void insertCompetitionTagRel(Competition competition)
    {
        List<CompetitionTagRel> competitionTagRelList = competition.getCompetitionTagRelList();
        Long competitionId = competition.getId();
        String operName = SecurityUtils.getUsername();

        // 优先使用结构化数据
        if (StringUtils.isNotNull(competitionTagRelList))
        {
            for (CompetitionTagRel rel : competitionTagRelList)
            {
                rel.setCompetitionId(competitionId);
                rel.setCreateBy(operName);
                rel.setCreateTime(DateUtils.getNowDate());
                rel.setDelFlag("0");
            }
            if (!competitionTagRelList.isEmpty())
            {
                competitionMapper.batchCompetitionTagRel(competitionTagRelList);
            }
            return;
        }

        // 兜底：从 tags 字段（逗号分隔的编码字符串）解析
        String tags = competition.getTags();
        if (StringUtils.isBlank(tags))
        {
            return;
        }
        String[] tagArray = tags.split(",");
        List<CompetitionTagRel> list = new ArrayList<>();
        for (int i = 0; i < tagArray.length; i++)
        {
            String tagCode = tagArray[i].trim();
            if (tagCode.isEmpty())
            {
                continue;
            }
            CompetitionTagRel rel = new CompetitionTagRel();
            rel.setCompetitionId(competitionId);
            rel.setTagCode(tagCode);
            rel.setSort((long) i);
            rel.setCreateBy(operName);
            rel.setCreateTime(DateUtils.getNowDate());
            rel.setDelFlag("0");
            list.add(rel);
        }
        if (!list.isEmpty())
        {
            competitionMapper.batchCompetitionTagRel(list);
        }
    }

    // ========== 实现新增的按名称查询方法 ==========
    @Override
    public Competition selectCompetitionByCompName(String compName) {
        if (StringUtils.isBlank(compName))
        {
            return null;
        }
        return competitionMapper.selectCompetitionByNameExact(compName.trim());
    }


}
