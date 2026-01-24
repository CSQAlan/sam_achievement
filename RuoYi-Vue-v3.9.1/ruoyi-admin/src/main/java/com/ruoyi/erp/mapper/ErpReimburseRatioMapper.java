package com.ruoyi.erp.mapper;

import java.util.List;
import com.ruoyi.erp.domain.ErpReimburseRatio;

/**
 * 报销比例Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-24
 */
public interface ErpReimburseRatioMapper 
{
    /**
     * 查询报销比例
     * 
     * @param id 报销比例主键
     * @return 报销比例
     */
    public ErpReimburseRatio selectErpReimburseRatioById(Long id);

    /**
     * 查询报销比例列表
     * 
     * @param erpReimburseRatio 报销比例
     * @return 报销比例集合
     */
    public List<ErpReimburseRatio> selectErpReimburseRatioList(ErpReimburseRatio erpReimburseRatio);

    /**
     * 新增报销比例
     * 
     * @param erpReimburseRatio 报销比例
     * @return 结果
     */
    public int insertErpReimburseRatio(ErpReimburseRatio erpReimburseRatio);

    /**
     * 修改报销比例
     * 
     * @param erpReimburseRatio 报销比例
     * @return 结果
     */
    public int updateErpReimburseRatio(ErpReimburseRatio erpReimburseRatio);

    /**
     * 删除报销比例
     * 
     * @param id 报销比例主键
     * @return 结果
     */
    public int deleteErpReimburseRatioById(Long id);

    /**
     * 批量删除报销比例
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpReimburseRatioByIds(Long[] ids);
}
