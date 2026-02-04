package com.ruoyi.erp.mapper;

import java.util.List;

import com.ruoyi.erp.domain.ErpGuideTeacher;
import com.ruoyi.erp.domain.ErpOutcome;
import com.ruoyi.erp.domain.ErpContestant;

/**
 * 成果主Mapper接口
 * 
 * @author 王璨
 * @date 2026-01-26
 */
public interface ErpOutcomeMapper 
{
    /**
     * 查询成果主
     * 
     * @param id 成果主主键
     * @return 成果主
     */
    public ErpOutcome selectErpOutcomeById(Long id);

    /**
     * 查询成果主列表
     * 
     * @param erpOutcome 成果主
     * @return 成果主集合
     */
    public List<ErpOutcome> selectErpOutcomeList(ErpOutcome erpOutcome);

    /**
     * 新增成果主
     * 
     * @param erpOutcome 成果主
     * @return 结果
     */
    public int insertErpOutcome(ErpOutcome erpOutcome);

    /**
     * 修改成果主
     * 
     * @param erpOutcome 成果主
     * @return 结果
     */
    public int updateErpOutcome(ErpOutcome erpOutcome);

    /**
     * 删除成果主
     * 
     * @param id 成果主主键
     * @return 结果
     */
    public int deleteErpOutcomeById(Long id);

    /**
     * 批量删除成果主
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpOutcomeByIds(Long[] ids);

    /**
     * 批量删除参赛选手
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpContestantByOutcomeIds(Long[] ids);
    
    /**
     * 批量新增参赛选手
     * 
     * @param erpContestantList 参赛选手列表
     * @return 结果
     */
    public int batchErpContestant(List<ErpContestant> erpContestantList);
    

    /**
     * 通过成果主主键删除参赛选手信息
     * 
     * @param id 成果主ID
     * @return 结果
     */
    public int deleteErpContestantByOutcomeId(Long id);
    // 指导老师相关方法
    public int deleteGuideTeacherByOutcomeId(Long outcomeId);
    public int deleteGuideTeacherByOutcomeIds(Long[] outcomeIds);
    public int batchGuideTeacher(List<ErpGuideTeacher> guideTeacherList);
}
