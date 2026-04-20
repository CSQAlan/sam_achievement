package com.ruoyi.reimbursement.service;

import java.util.List;
import com.ruoyi.reimbursement.domain.SamReimbursementRatio;

/**
 * 报销比例Service接口
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
public interface ISamReimbursementRatioService 
{
    /**
     * 查询报销比例
     * 
     * @param id 报销比例主键
     * @return 报销比例
     */
    public SamReimbursementRatio selectSamReimbursementRatioById(Long id);

    /**
     * 查询报销比例列表
     * 
     * @param samReimbursementRatio 报销比例
     * @return 报销比例集合
     */
    public List<SamReimbursementRatio> selectSamReimbursementRatioList(SamReimbursementRatio samReimbursementRatio);

    /**
     * 新增报销比例
     * 
     * @param samReimbursementRatio 报销比例
     * @return 结果
     */
    public int insertSamReimbursementRatio(SamReimbursementRatio samReimbursementRatio);

    /**
     * 修改报销比例
     * 
     * @param samReimbursementRatio 报销比例
     * @return 结果
     */
    public int updateSamReimbursementRatio(SamReimbursementRatio samReimbursementRatio);

    /**
     * 批量删除报销比例
     * 
     * @param ids 需要删除的报销比例主键集合
     * @return 结果
     */
    public int deleteSamReimbursementRatioByIds(Long[] ids);

    /**
     * 删除报销比例信息
     * 
     * @param id 报销比例主键
     * @return 结果
     */
    public int deleteSamReimbursementRatioById(Long id);
}
