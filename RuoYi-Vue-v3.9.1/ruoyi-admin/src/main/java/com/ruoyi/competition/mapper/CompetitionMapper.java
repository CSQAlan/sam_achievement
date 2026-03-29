package com.ruoyi.competition.mapper;

import java.util.List;
import com.ruoyi.competition.domain.Competition;
import com.ruoyi.competition.domain.CompetitionDeptRel;
import org.apache.ibatis.annotations.Param;

/**
 * 总赛事Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
public interface CompetitionMapper 
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
     * 删除总赛事
     * 
     * @param id 总赛事主键
     * @return 结果
     */
    public int deleteCompetitionById(Long id);

    /**
     * 批量删除总赛事
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCompetitionByIds(Long[] ids);

    /**
     * 批量删除赛事-部门关系
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCompetitionDeptRelBySessionIds(Long[] ids);
    
    /**
     * 批量新增赛事-部门关系
     * 
     * @param competitionDeptRelList 赛事-部门关系列表
     * @return 结果
     */
    public int batchCompetitionDeptRel(List<CompetitionDeptRel> competitionDeptRelList);
    

    /**
     * 通过总赛事主键删除赛事-部门关系信息
     * 
     * @param id 总赛事ID
     * @return 结果
     */
    public int deleteCompetitionDeptRelBySessionId(Long id);

    /**
     * 按赛事名称精确查询（用于保证名称唯一的业务场景）
     *
     * @param name 赛事名称
     * @return 总赛事
     */
    public Competition selectCompetitionByNameExact(@Param("name") String name);

}
