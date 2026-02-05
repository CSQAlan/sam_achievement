package com.ruoyi.competitionapply.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.competitionapply.domain.CompetitionApplyAttachment;
import com.ruoyi.competitionapply.mapper.CompetitionApplyMapper;
import com.ruoyi.competitionapply.domain.CompetitionApply;
import com.ruoyi.competitionapply.service.ICompetitionApplyService;

/**
 * 赛事申请Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
@Service
public class CompetitionApplyServiceImpl implements ICompetitionApplyService 
{
    @Autowired
    private CompetitionApplyMapper competitionApplyMapper;

    /**
     * 查询赛事申请
     * 
     * @param id 赛事申请主键
     * @return 赛事申请
     */
    @Override
    public CompetitionApply selectCompetitionApplyById(Long id)
    {
        return competitionApplyMapper.selectCompetitionApplyById(id);
    }

    /**
     * 查询赛事申请列表
     * 
     * @param competitionApply 赛事申请
     * @return 赛事申请
     */
    @Override
    public List<CompetitionApply> selectCompetitionApplyList(CompetitionApply competitionApply)
    {
        return competitionApplyMapper.selectCompetitionApplyList(competitionApply);
    }

    /**
     * 新增赛事申请
     * 
     * @param competitionApply 赛事申请
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCompetitionApply(CompetitionApply competitionApply)
    {
        competitionApply.setCreateTime(DateUtils.getNowDate());
        int rows = competitionApplyMapper.insertCompetitionApply(competitionApply);
        insertCompetitionApplyAttachment(competitionApply);
        return rows;
    }

    /**
     * 修改赛事申请
     * 
     * @param competitionApply 赛事申请
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCompetitionApply(CompetitionApply competitionApply)
    {
        competitionApply.setUpdateTime(DateUtils.getNowDate());
        competitionApplyMapper.deleteCompetitionApplyAttachmentByApplyId(competitionApply.getId());
        insertCompetitionApplyAttachment(competitionApply);
        return competitionApplyMapper.updateCompetitionApply(competitionApply);
    }

    /**
     * 批量删除赛事申请
     * 
     * @param ids 需要删除的赛事申请主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCompetitionApplyByIds(Long[] ids)
    {
        competitionApplyMapper.deleteCompetitionApplyAttachmentByApplyIds(ids);
        return competitionApplyMapper.deleteCompetitionApplyByIds(ids);
    }

    /**
     * 删除赛事申请信息
     * 
     * @param id 赛事申请主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCompetitionApplyById(Long id)
    {
        competitionApplyMapper.deleteCompetitionApplyAttachmentByApplyId(id);
        return competitionApplyMapper.deleteCompetitionApplyById(id);
    }

    /**
     * 新增赛事申请附件信息
     * 
     * @param competitionApply 赛事申请对象
     */
    public void insertCompetitionApplyAttachment(CompetitionApply competitionApply)
    {
        List<CompetitionApplyAttachment> competitionApplyAttachmentList = competitionApply.getCompetitionApplyAttachmentList();
        Long id = competitionApply.getId();
        if (StringUtils.isNotNull(competitionApplyAttachmentList))
        {
            List<CompetitionApplyAttachment> list = new ArrayList<CompetitionApplyAttachment>();
            for (CompetitionApplyAttachment competitionApplyAttachment : competitionApplyAttachmentList)
            {
                competitionApplyAttachment.setApplyId(id);
                list.add(competitionApplyAttachment);
            }
            if (list.size() > 0)
            {
                competitionApplyMapper.batchCompetitionApplyAttachment(list);
            }
        }
    }
}
