package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.Sam_rem_Product;

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
    
    /**
     * 重新计算报销金额
     * 
     * @param reimbursementItemId 报销项目ID
     * @return 结果
     */
    public Map<String, Object> recalculateReimbursementAmount(String reimbursementItemId);

    /**
     * 关联成果到报销项目
     * 
     * @param reimbursementItemId 报销项目ID
     * @param achievementIds 成果ID列表
     * @return 结果
     */
    public Map<String, Object> associateAchievements(String reimbursementItemId, String[] achievementIds);

    /**
     * 查询未关联的成果列表
     * 
     * @param sam_rem_Product 报销项目详情
     * @return 未关联的成果列表
     */
    public List<Sam_rem_Product> selectUnassociatedAchievements(Sam_rem_Product sam_rem_Product);

    /**
     * 查询未关联的成果列表
     */
    public List<Sam_rem_Product> selectUnassociatedProductList(Sam_rem_Product queryParams);

    /**
     * 批量关联成果到报销项目
     * 
     * @param achievementIds 成果ID列表
     * @param reimbursementItemId 报销项目ID
     * @return 关联结果（成功数量、失败原因等）
     */
    public Map<String, Object> batchAssociateAchievements(List<String> achievementIds, String reimbursementItemId);

    /**
     * 取消关联成果
     * 
     * @param achievementId 成果ID
     * @param reimbursementItemId 报销项目ID
     * @return 结果
     */
    public Map<String, Object> cancelAssociation(String achievementId, String reimbursementItemId);

    /**
     * 批量取消关联成果
     * 
     * @param achievementIds 成果ID列表
     * @param reimbursementItemId 报销项目ID
     * @return 结果
     */
    public Map<String, Object> batchCancelAssociation(List<String> achievementIds, String reimbursementItemId);

    /**
     * 查询成果的参赛选手
     * 
     * @param achievementId 成果ID
     * @return 参赛选手列表
     */
    public List<Map<String, Object>> selectParticipantsByAchievementId(Long achievementId);

    /**
     * 查询成果的指导老师
     * 
     * @param achievementId 成果ID
     * @return 指导老师列表
     */
    public List<Map<String, Object>> selectAdvisorsByAchievementId(Long achievementId);

    /**
     * 查询成果的附件
     * 
     * @param achievementId 成果ID
     * @return 附件列表
     */
    public List<Map<String, Object>> selectAttachmentsByAchievementId(Long achievementId);

} 
