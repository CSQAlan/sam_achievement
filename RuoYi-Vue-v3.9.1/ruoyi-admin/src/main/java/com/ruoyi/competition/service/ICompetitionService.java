package com.ruoyi.competition.service;

import java.util.List;
import com.ruoyi.competition.domain.Competition;

/**
 * 总赛事Service接口
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
public interface ICompetitionService 
{
    /**
     * 查询总赛事
     * 
     * @param id 总赛事主键
     * @return 总赛事
     */
    public Competition selectCompetitionById(Long id);

    /**
     * 查询总赛事列表
     * 
     * @param competition 总赛事
     * @return 总赛事集合
     */
    public List<Competition> selectCompetitionList(Competition competition);

    /**
     * 新增总赛事
     * 
     * @param competition 总赛事
     * @return 结果
     */
    public int insertCompetition(Competition competition);

    /**
     * 修改总赛事
     * 
     * @param competition 总赛事
     * @return 结果
     */
    public int updateCompetition(Competition competition);

    /**
     * 批量删除总赛事
     * 
     * @param ids 需要删除的总赛事主键集合
     * @return 结果
     */
    public int deleteCompetitionByIds(Long[] ids);

    /**
     * 删除总赛事信息
     * 
     * @param id 总赛事主键
     * @return 结果
     */
    public int deleteCompetitionById(Long id);


}
