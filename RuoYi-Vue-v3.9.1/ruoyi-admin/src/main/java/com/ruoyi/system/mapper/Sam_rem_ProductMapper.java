package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map; 

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.system.domain.SamReimbursementItems;
import com.ruoyi.system.domain.Sam_rem_Product;

/**
 * 报销项目详情Mapper接口
 * 
 * @author luo
 * @date 2026-03-22
 */
@Mapper
public interface Sam_rem_ProductMapper 
{
    /**
     * 查询报销项目详情
     * 
     * @param achievementId 报销项目详情主键
     * @return 报销项目详情
     */
    public Sam_rem_Product selectSam_rem_ProductByAchievementId(String achievementId);
    

    /**
     * 根据报销项目ID查询报销项目详情列表
     * 
     * @param reimbursementItemId 报销项目ID
     * @return 报销项目详情集合
    */
    public List<Sam_rem_Product> selectSam_rem_ProductByReimbursementItemId(String reimbursementItemId);


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
     * 删除报销项目详情
     * 
     * @param achievementId 报销项目详情主键
     * @return 结果
     */
    public int deleteSam_rem_ProductByAchievementId(String achievementId);

    /**
     * 批量删除报销项目详情
     * 
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSam_rem_ProductByAchievementIds(String[] achievementIds);

    /**
     * 批量删除报销项目
     * 
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamReimbursementItemsByNames(String[] achievementIds);
    
    /**
     * 批量新增报销项目
     * 
     * @param samReimbursementItemsList 报销项目列表
     * @return 结果
     */
    public int batchSamReimbursementItems(List<SamReimbursementItems> samReimbursementItemsList);
    

    /**
     * 通过报销项目详情主键删除报销项目信息
     * 
     * @param achievementId 报销项目详情ID
     * @return 结果
     */
    public int deleteSamReimbursementItemsByName(String achievementId);
    
    /**
     * 批量更新报销金额
     */
    public int batchUpdateReimbursementFee(List<Sam_rem_Product> list);

    /**
     * 查询未关联的成果列表
     * 
     * @param sam_rem_Product 报销项目详情
     * @return 未关联的成果列表
     */
    public List<Sam_rem_Product> selectUnassociatedAchievements(Sam_rem_Product sam_rem_Product);

    /**
     * 批量更新成果的报销项目ID
     * 
     * @param map 参数map，包含reimbursementItemId和achievementIds
     * @return 结果
     */
    public int batchUpdateReimbursementItemId(Map<String, Object> map);

    /**
     * 查询未关联到当前报销项目的成果列表
     * 
     * @param queryParams 查询参数（包含排除的reimbursementItemId和筛选条件）
     * @return 未关联成果集合
     */
    public List<Sam_rem_Product> selectUnassociatedProductList(Sam_rem_Product queryParams);

    /**
     * 批量关联成果到报销项目
     * 
     * @param achievementIds 成果ID列表
     * @param reimbursementItemId 报销项目ID
     * @return 更新数量
     */
    public int batchAssociateAchievements(@Param("list") List<String> achievementIds, 
                                          @Param("reimbursementItemId") String reimbursementItemId);

    /**
     * 根据报销项目ID统计关联成果数量
     * 
     * @param reimbursementItemId 报销项目ID
     * @return 数量
     */
    public int countByReimbursementItemId(String reimbursementItemId);

    /**
     * 取消关联：将成果从报销项目中移除
     * 
     * @param achievementId 成果ID
     * @return 结果
     */
    public int cancelAssociation(String achievementId);

    /**
     * 批量取消关联
     * 
     * @param achievementIds 成果ID列表
     * @return 结果
     */
    public int batchCancelAssociation(@Param("list") List<String> achievementIds);

    /**
     * 查询成果的参赛选手
     * 
     * @param achievementId 成果ID
     * @return 参赛选手列表
     */
    public List<Map<String, Object>> selectParticipantsByAchievementId(@Param("achievementId") Long achievementId);

    /**
     * 查询成果的指导老师
     * 
     * @param achievementId 成果ID
     * @return 指导老师列表
     */
    public List<Map<String, Object>> selectAdvisorsByAchievementId(@Param("achievementId") Long achievementId);

    /**
     * 查询成果的附件
     * 
     * @param achievementId 成果ID
     * @return 附件列表
     */
    public List<Map<String, Object>> selectAttachmentsByAchievementId(@Param("achievementId") Long achievementId);

} 

