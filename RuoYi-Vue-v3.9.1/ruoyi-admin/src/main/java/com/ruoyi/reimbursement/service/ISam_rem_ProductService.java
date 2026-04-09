package com.ruoyi.reimbursement.service;

import java.util.List;

import com.ruoyi.reimbursement.domain.Sam_rem_Product;

/**
 * 报销项目详情Service接口
 * 
 * @author luo
 * @date 2026-03-22
 */
public interface ISam_rem_ProductService 
{
    
    /**
       * 根据报销项目ID查询报销项目详情列表
       * 
       * @param reimbursementItemId 报销项目ID
       * @return 报销项目详情集合
       */
    public List<Sam_rem_Product> selectSam_rem_ProductByReimbursementItemId(String reimbursementItemId);
    
    /**
     * 查询报销项目详情
     * 
     * @param achievementId 报销项目详情主键
     * @return 报销项目详情
     */
    public Sam_rem_Product selectSam_rem_ProductByAchievementId(String achievementId);

    /**
     * 查询报销项目详情列表
     * 
     * @param sam_rem_Product 报销项目详情
     * @return 报销项目详情集合
     */
    public List<Sam_rem_Product> selectSam_rem_ProductList(Sam_rem_Product sam_rem_Product);

    /**
     * 新增报销项目详情
     * 
     * @param sam_rem_Product 报销项目详情
     * @return 结果
     */
    public int insertSam_rem_Product(Sam_rem_Product sam_rem_Product);

    /**
     * 修改报销项目详情
     * 
     * @param sam_rem_Product 报销项目详情
     * @return 结果
     */
    public int updateSam_rem_Product(Sam_rem_Product sam_rem_Product);

    /**
     * 批量删除报销项目详情
     * 
     * @param achievementIds 需要删除的报销项目详情主键集合
     * @return 结果
     */
    public int deleteSam_rem_ProductByAchievementIds(String[] achievementIds);

    /**
     * 删除报销项目详情信息
     * 
     * @param achievementId 报销项目详情主键
     * @return 结果
     */
    public int deleteSam_rem_ProductByAchievementId(String achievementId);

}
