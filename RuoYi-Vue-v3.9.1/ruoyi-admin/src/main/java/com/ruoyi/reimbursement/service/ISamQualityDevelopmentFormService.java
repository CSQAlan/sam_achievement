package com.ruoyi.reimbursement.service;

import java.util.List;
import com.ruoyi.reimbursement.domain.SamQualityDevelopmentForm;

/**
 * 素质提升Service接口
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
public interface ISamQualityDevelopmentFormService 
{
    /**
     * 查询素质提升
     * 
     * @param id 素质提升主键
     * @return 素质提升
     */
    public SamQualityDevelopmentForm selectSamQualityDevelopmentFormById(Long id);

    /**
     * 查询素质提升列表
     * 
     * @param samQualityDevelopmentForm 素质提升
     * @return 素质提升集合
     */
    public List<SamQualityDevelopmentForm> selectSamQualityDevelopmentFormList(SamQualityDevelopmentForm samQualityDevelopmentForm);

    /**
     * 新增素质提升
     * 
     * @param samQualityDevelopmentForm 素质提升
     * @return 结果
     */
    public int insertSamQualityDevelopmentForm(SamQualityDevelopmentForm samQualityDevelopmentForm);

    /**
     * 修改素质提升
     * 
     * @param samQualityDevelopmentForm 素质提升
     * @return 结果
     */
    public int updateSamQualityDevelopmentForm(SamQualityDevelopmentForm samQualityDevelopmentForm);

    /**
     * 批量删除素质提升
     * 
     * @param ids 需要删除的素质提升主键集合
     * @return 结果
     */
    public int deleteSamQualityDevelopmentFormByIds(Long[] ids);

    /**
     * 删除素质提升信息
     * 
     * @param id 素质提升主键
     * @return 结果
     */
    public int deleteSamQualityDevelopmentFormById(Long id);
}
