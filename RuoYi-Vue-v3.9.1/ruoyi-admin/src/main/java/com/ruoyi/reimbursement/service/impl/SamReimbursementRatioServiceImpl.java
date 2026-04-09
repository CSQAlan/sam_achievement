package com.ruoyi.reimbursement.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.reimbursement.mapper.SamReimbursementRatioMapper;
import com.ruoyi.reimbursement.domain.SamReimbursementRatio;
import com.ruoyi.reimbursement.service.ISamReimbursementRatioService;

/**
 * 报销比例Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
@Service
public class SamReimbursementRatioServiceImpl implements ISamReimbursementRatioService 
{
    @Autowired
    private SamReimbursementRatioMapper samReimbursementRatioMapper;

    /**
     * 查询报销比例
     * 
     * @param id 报销比例主键
     * @return 报销比例
     */
    @Override
    public SamReimbursementRatio selectSamReimbursementRatioById(Long id)
    {
        return samReimbursementRatioMapper.selectSamReimbursementRatioById(id);
    }

    /**
     * 查询报销比例列表
     * 
     * @param samReimbursementRatio 报销比例
     * @return 报销比例
     */
    @Override
    public List<SamReimbursementRatio> selectSamReimbursementRatioList(SamReimbursementRatio samReimbursementRatio)
    {
        return samReimbursementRatioMapper.selectSamReimbursementRatioList(samReimbursementRatio);
    }

    /**
     * 新增报销比例
     * 
     * @param samReimbursementRatio 报销比例
     * @return 结果
     */
    @Override
    public int insertSamReimbursementRatio(SamReimbursementRatio samReimbursementRatio)
    {
        samReimbursementRatio.setCreateTime(DateUtils.getNowDate());
        return samReimbursementRatioMapper.insertSamReimbursementRatio(samReimbursementRatio);
    }

    /**
     * 修改报销比例
     * 
     * @param samReimbursementRatio 报销比例
     * @return 结果
     */
    @Override
    public int updateSamReimbursementRatio(SamReimbursementRatio samReimbursementRatio)
    {
        samReimbursementRatio.setUpdateTime(DateUtils.getNowDate());
        return samReimbursementRatioMapper.updateSamReimbursementRatio(samReimbursementRatio);
    }

    /**
     * 批量删除报销比例
     * 
     * @param ids 需要删除的报销比例主键
     * @return 结果
     */
    @Override
    public int deleteSamReimbursementRatioByIds(Long[] ids)
    {
        return samReimbursementRatioMapper.deleteSamReimbursementRatioByIds(ids);
    }

    /**
     * 删除报销比例信息
     * 
     * @param id 报销比例主键
     * @return 结果
     */
    @Override
    public int deleteSamReimbursementRatioById(Long id)
    {
        return samReimbursementRatioMapper.deleteSamReimbursementRatioById(id);
    }
}
