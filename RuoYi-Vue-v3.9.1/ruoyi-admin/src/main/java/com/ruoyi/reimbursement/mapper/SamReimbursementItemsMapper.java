package com.ruoyi.reimbursement.mapper;

import java.util.List;

import com.ruoyi.reimbursement.domain.SamReimbursementItems;

/**
 * 报销项目Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
public interface SamReimbursementItemsMapper 
{
    /**
     * 查询报销项目
     * 
     * @param id 报销项目主键
     * @return 报销项目
     */
    public SamReimbursementItems selectSamReimbursementItemsById(Long id);

    /**
     * 查询报销项目列表
     * 
     * @param samReimbursementItems 报销项目
     * @return 报销项目集合
     */
    public List<SamReimbursementItems> selectSamReimbursementItemsList(SamReimbursementItems samReimbursementItems);

    /**
     * 新增报销项目
     * 
     * @param samReimbursementItems 报销项目
     * @return 结果
     */
    public int insertSamReimbursementItems(SamReimbursementItems samReimbursementItems);

    /**
     * 修改报销项目
     * 
     * @param samReimbursementItems 报销项目
     * @return 结果
     */
    public int updateSamReimbursementItems(SamReimbursementItems samReimbursementItems);

    /**
     * 删除报销项目
     * 
     * @param id 报销项目主键
     * @return 结果
     */
    public int deleteSamReimbursementItemsById(Long id);

    /**
     * 批量删除报销项目
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamReimbursementItemsByIds(Long[] ids);
}
