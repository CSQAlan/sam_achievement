package com.ruoyi.competitionapply.service;

import java.util.List;
import com.ruoyi.competitionapply.domain.CompetitionApply;

/**
 * 赛事申请Service接口
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
public interface ICompetitionApplyService 
{
    /**
     * 查询赛事申请
     * 
     * @param id 赛事申请主键
     * @return 赛事申请
     */
    public CompetitionApply selectCompetitionApplyById(Long id);

    /**
     * 查询赛事申请列表
     * 
     * @param competitionApply 赛事申请
     * @return 赛事申请集合
     */
    public List<CompetitionApply> selectCompetitionApplyList(CompetitionApply competitionApply);

    /**
     * 新增赛事申请
     * 
     * @param competitionApply 赛事申请
     * @return 结果
     */
    public int insertCompetitionApply(CompetitionApply competitionApply);

    /**
     * 修改赛事申请
     * 
     * @param competitionApply 赛事申请
     * @return 结果
     */
    public int updateCompetitionApply(CompetitionApply competitionApply);

    /**
     * 批量删除赛事申请
     * 
     * @param ids 需要删除的赛事申请主键集合
     * @return 结果
     */
    public int deleteCompetitionApplyByIds(Long[] ids);

    /**
     * 删除赛事申请信息
     * 
     * @param id 赛事申请主键
     * @return 结果
     */
    public int deleteCompetitionApplyById(Long id);
}
