package com.ruoyi.reimbursement.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.reimbursement.mapper.SamQualityDevelopmentFormMapper;
import com.ruoyi.reimbursement.domain.SamQualityDevelopmentForm;
import com.ruoyi.reimbursement.service.ISamQualityDevelopmentFormService;

/**
 * 素质提升Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
@Service
public class SamQualityDevelopmentFormServiceImpl implements ISamQualityDevelopmentFormService 
{
    @Autowired
    private SamQualityDevelopmentFormMapper samQualityDevelopmentFormMapper;

    /**
     * 查询素质提升
     * 
     * @param id 素质提升主键
     * @return 素质提升
     */
    @Override
    public SamQualityDevelopmentForm selectSamQualityDevelopmentFormById(Long id)
    {
        return samQualityDevelopmentFormMapper.selectSamQualityDevelopmentFormById(id);
    }

    /**
     * 查询素质提升列表
     * 
     * @param samQualityDevelopmentForm 素质提升
     * @return 素质提升
     */
    @Override
    public List<SamQualityDevelopmentForm> selectSamQualityDevelopmentFormList(SamQualityDevelopmentForm samQualityDevelopmentForm)
    {
        return samQualityDevelopmentFormMapper.selectSamQualityDevelopmentFormList(samQualityDevelopmentForm);
    }

    /**
     * 新增素质提升
     * 
     * @param samQualityDevelopmentForm 素质提升
     * @return 结果
     */
    @Override
    public int insertSamQualityDevelopmentForm(SamQualityDevelopmentForm samQualityDevelopmentForm)
    {
        samQualityDevelopmentForm.setCreateTime(DateUtils.getNowDate());
        return samQualityDevelopmentFormMapper.insertSamQualityDevelopmentForm(samQualityDevelopmentForm);
    }

    /**
     * 修改素质提升
     * 
     * @param samQualityDevelopmentForm 素质提升
     * @return 结果
     */
    @Override
    public int updateSamQualityDevelopmentForm(SamQualityDevelopmentForm samQualityDevelopmentForm)
    {
        return samQualityDevelopmentFormMapper.updateSamQualityDevelopmentForm(samQualityDevelopmentForm);
    }

    /**
     * 批量删除素质提升
     * 
     * @param ids 需要删除的素质提升主键
     * @return 结果
     */
    @Override
    public int deleteSamQualityDevelopmentFormByIds(Long[] ids)
    {
        return samQualityDevelopmentFormMapper.deleteSamQualityDevelopmentFormByIds(ids);
    }

    /**
     * 删除素质提升信息
     * 
     * @param id 素质提升主键
     * @return 结果
     */
    @Override
    public int deleteSamQualityDevelopmentFormById(Long id)
    {
        return samQualityDevelopmentFormMapper.deleteSamQualityDevelopmentFormById(id);
    }
}
