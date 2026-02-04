package com.ruoyi.erp.service;

import java.util.List;
import com.ruoyi.erp.domain.ErpOutcome;

/**
 * 成果主Service接口
 * 
 * @author 王璨
 * @date 2026-01-26
 */
public interface IErpOutcomeService 
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
     * 批量删除成果主
     * 
     * @param ids 需要删除的成果主主键集合
     * @return 结果
     */
    public int deleteErpOutcomeByIds(Long[] ids);

    /**
     * 删除成果主信息
     * 
     * @param id 成果主主键
     * @return 结果
     */
    public int deleteErpOutcomeById(Long id);
}
