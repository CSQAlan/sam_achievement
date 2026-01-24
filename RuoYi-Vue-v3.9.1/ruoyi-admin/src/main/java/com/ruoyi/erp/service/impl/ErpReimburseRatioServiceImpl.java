package com.ruoyi.erp.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.erp.mapper.ErpReimburseRatioMapper;
import com.ruoyi.erp.domain.ErpReimburseRatio;
import com.ruoyi.erp.service.IErpReimburseRatioService;

/**
 * 报销比例Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-24
 */
@Service
public class ErpReimburseRatioServiceImpl implements IErpReimburseRatioService 
{
    @Autowired
    private ErpReimburseRatioMapper erpReimburseRatioMapper;

    /**
     * 查询报销比例
     * 
     * @param id 报销比例主键
     * @return 报销比例
     */
    @Override
    public ErpReimburseRatio selectErpReimburseRatioById(Long id)
    {
        return erpReimburseRatioMapper.selectErpReimburseRatioById(id);
    }

    /**
     * 查询报销比例列表
     * 
     * @param erpReimburseRatio 报销比例
     * @return 报销比例
     */
    @Override
    public List<ErpReimburseRatio> selectErpReimburseRatioList(ErpReimburseRatio erpReimburseRatio)
    {
        return erpReimburseRatioMapper.selectErpReimburseRatioList(erpReimburseRatio);
    }

    /**
     * 新增报销比例
     * 
     * @param erpReimburseRatio 报销比例
     * @return 结果
     */
    @Override
    public int insertErpReimburseRatio(ErpReimburseRatio erpReimburseRatio)
    {
        erpReimburseRatio.setCreateTime(DateUtils.getNowDate());
        return erpReimburseRatioMapper.insertErpReimburseRatio(erpReimburseRatio);
    }

    /**
     * 修改报销比例
     * 
     * @param erpReimburseRatio 报销比例
     * @return 结果
     */
    @Override
    public int updateErpReimburseRatio(ErpReimburseRatio erpReimburseRatio)
    {
        erpReimburseRatio.setUpdateTime(DateUtils.getNowDate());
        return erpReimburseRatioMapper.updateErpReimburseRatio(erpReimburseRatio);
    }

    /**
     * 批量删除报销比例
     * 
     * @param ids 需要删除的报销比例主键
     * @return 结果
     */
    @Override
    public int deleteErpReimburseRatioByIds(Long[] ids)
    {
        return erpReimburseRatioMapper.deleteErpReimburseRatioByIds(ids);
    }

    /**
     * 删除报销比例信息
     * 
     * @param id 报销比例主键
     * @return 结果
     */
    @Override
    public int deleteErpReimburseRatioById(Long id)
    {
        return erpReimburseRatioMapper.deleteErpReimburseRatioById(id);
    }
}
