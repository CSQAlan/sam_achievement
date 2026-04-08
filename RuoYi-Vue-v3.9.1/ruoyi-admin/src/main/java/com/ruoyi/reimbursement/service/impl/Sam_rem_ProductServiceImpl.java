package com.ruoyi.reimbursement.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.time.Year;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.reimbursement.domain.SamReimbursementItems;
import com.ruoyi.reimbursement.mapper.Sam_rem_ProductMapper;
import com.ruoyi.reimbursement.domain.Sam_rem_Product;
import com.ruoyi.reimbursement.service.ISam_rem_ProductService;
import com.ruoyi.reimbursement.mapper.SamReimbursementRatioMapper;
import com.ruoyi.reimbursement.mapper.SamReimbursementItemsMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 报销项目详情Service业务层处理
 * 
 * @author luo
 * @date 2026-03-22
 */
@Service
public class Sam_rem_ProductServiceImpl implements ISam_rem_ProductService 
{
    private static final Logger log = LoggerFactory.getLogger(Sam_rem_ProductServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Sam_rem_ProductMapper sam_rem_ProductMapper;
    
    @Autowired
    private SamReimbursementRatioMapper samReimbursementRatioMapper;
    
    @Autowired
    private SamReimbursementItemsMapper samReimbursementItemsMapper;

    /**
     * 查询报销项目详情
     * 
     * @param achievementId 报销项目详情主键
     * @return 报销项目详情
     */
    @Override
    public Sam_rem_Product selectSam_rem_ProductByAchievementId(String achievementId)
    {
        return sam_rem_ProductMapper.selectSam_rem_ProductByAchievementId(achievementId);
    }


    @Override
    public List<Sam_rem_Product> selectSam_rem_ProductByReimbursementItemId(String reimbursementItemId)
    {
        return sam_rem_ProductMapper.selectSam_rem_ProductByReimbursementItemId(reimbursementItemId);
    }

    /**
     * 查询报销项目详情列表
     * 
     * @param sam_rem_Product 报销项目详情
     * @return 报销项目详情
     */
    @Override
    public List<Sam_rem_Product> selectSam_rem_ProductList(Sam_rem_Product sam_rem_Product)
    {
        return sam_rem_ProductMapper.selectSam_rem_ProductList(sam_rem_Product);
    }

    /**
     * 新增报销项目详情
     * 
     * @param sam_rem_Product 报销项目详情
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSam_rem_Product(Sam_rem_Product sam_rem_Product)
    {
        // 在插入前设置 year
        if (sam_rem_Product.getYear() == null) {
            // 设置当前年份为 Long 类型
            sam_rem_Product.setYear(Long.valueOf(Year.now().getValue()));
        }
        
        sam_rem_Product.setCreateTime(DateUtils.getNowDate());
        int rows = sam_rem_ProductMapper.insertSam_rem_Product(sam_rem_Product);
        insertSamReimbursementItems(sam_rem_Product);
        return rows;
    }

    /**
     * 修改报销项目详情
     * 
     * @param sam_rem_Product 报销项目详情
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSam_rem_Product(Sam_rem_Product sam_rem_Product)
    {
        sam_rem_Product.setUpdateTime(DateUtils.getNowDate());
        sam_rem_ProductMapper.deleteSamReimbursementItemsByName(sam_rem_Product.getAchievementId());
        insertSamReimbursementItems(sam_rem_Product);
        return sam_rem_ProductMapper.updateSam_rem_Product(sam_rem_Product);
    }

    /**
     * 批量删除报销项目详情
     * 
     * @param achievementIds 需要删除的报销项目详情主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSam_rem_ProductByAchievementIds(String[] achievementIds)
    {
        sam_rem_ProductMapper.deleteSamReimbursementItemsByNames(achievementIds);
        return sam_rem_ProductMapper.deleteSam_rem_ProductByAchievementIds(achievementIds);
    }

    /**
     * 删除报销项目详情信息
     * 
     * @param achievementId 报销项目详情主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSam_rem_ProductByAchievementId(String achievementId)
    {
        sam_rem_ProductMapper.deleteSamReimbursementItemsByName(achievementId);
        return sam_rem_ProductMapper.deleteSam_rem_ProductByAchievementId(achievementId);
    }

    /**
     * 新增报销项目信息
     * 
     * @param sam_rem_Product 报销项目详情对象
     */
    public void insertSamReimbursementItems(Sam_rem_Product sam_rem_Product)
    {
        List<SamReimbursementItems> samReimbursementItemsList = sam_rem_Product.getSamReimbursementItemsList();
        String achievementId = sam_rem_Product.getAchievementId();
        if (StringUtils.isNotNull(samReimbursementItemsList))
        {
            List<SamReimbursementItems> list = new ArrayList<SamReimbursementItems>();
            for (SamReimbursementItems samReimbursementItems : samReimbursementItemsList)
            {
                samReimbursementItems.setName(achievementId);
                list.add(samReimbursementItems);
            }
            if (list.size() > 0)
            {
                sam_rem_ProductMapper.batchSamReimbursementItems(list);
            }
        }
    }

    /**
     * 重新计算报销金额
     * 
     * @param reimbursementItemId 报销项目ID
     * @return 结果
     */
    @Transactional
    @Override
    public Map<String, Object> recalculateReimbursementAmount(String reimbursementItemId)
    {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查询关联成果列表
        List<Sam_rem_Product> productList = sam_rem_ProductMapper.selectSam_rem_ProductByReimbursementItemId(reimbursementItemId);
        if (productList == null || productList.isEmpty()) {
            result.put("success", false);
            result.put("message", "没有找到关联的成果数据");
            return result;
        }
        
        // 2. 计算并更新报销金额
        List<Sam_rem_Product> updateList = new ArrayList<>();
        double totalAmount = 0;
        double paidAmount = 0;
        
        for (Sam_rem_Product product : productList) {
            // 根据获奖等级、类别、归属学院获取报销比例
            Integer ratio = samReimbursementRatioMapper.getReimbursementRatio(
                product.getGrade() != null ? product.getGrade().toString() : null,
                product.getCategory(),
                product.getOwnerDepId() != null ? Long.valueOf(product.getOwnerDepId()) : null
            );
            
            if (ratio != null) {
                // 计算：报销金额 = 报名费 × 比例 / 100
                BigDecimal fee = product.getFee();
                if (fee != null) {
                    BigDecimal reimbursementFee = fee.multiply(new BigDecimal(ratio)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                    product.setReimbursementFee(reimbursementFee);
                    product.setReimbursementRatio(ratio.toString());
                    updateList.add(product);
                    
                    totalAmount += reimbursementFee.doubleValue();
                    // 这里简化处理，实际应根据报销状态计算已发放金额
                    // paidAmount += ...
                }
            }
        }
        
        // 3. 批量更新成果表
        if (!updateList.isEmpty()) {
            sam_rem_ProductMapper.batchUpdateReimbursementFee(updateList);
        }
        
        // 4. 更新报销项目的统计信息
        updateProjectStatistics(reimbursementItemId);
        
        result.put("success", true);
        result.put("message", "报销金额计算成功");
        result.put("totalAmount", totalAmount);
        result.put("paidAmount", paidAmount);
        result.put("productCount", productList.size());
        
        return result;
    }

    @Transactional
    @Override
    public Map<String, Object> associateAchievements(String reimbursementItemId, String[] achievementIds) {
        if (achievementIds == null || achievementIds.length == 0) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "请选择要关联的成果");
            return result;
        }
        List<String> idList = java.util.Arrays.asList(achievementIds);
        return batchAssociateAchievements(idList, reimbursementItemId);
    }

    @Override
    public List<Sam_rem_Product> selectUnassociatedAchievements(Sam_rem_Product sam_rem_Product) {
        return sam_rem_ProductMapper.selectUnassociatedProductList(sam_rem_Product);
    }

    @Override
    public List<Sam_rem_Product> selectUnassociatedProductList(Sam_rem_Product queryParams) {

        return sam_rem_ProductMapper.selectUnassociatedProductList(queryParams);
    }

    @Override
    @Transactional
    public Map<String, Object> batchAssociateAchievements(List<String> achievementIds, String reimbursementItemId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 验证报销项目存在且状态为未确认
        SamReimbursementItems project = samReimbursementItemsMapper.selectSamReimbursementItemsById(Long.valueOf(reimbursementItemId));
        if (project == null) {
            throw new RuntimeException("报销项目不存在");
        }
        
        // 状态判断：0-进行中（未确认），1-已完成（已确认）
        if ("1".equals(project.getStatus())) {
            throw new RuntimeException("报销项目已完成，无法关联成果");
        }
        
        // 2. 验证成果是否可关联（检查是否已被关联）
        for (String achievementId : achievementIds) {
            Sam_rem_Product product = sam_rem_ProductMapper.selectSam_rem_ProductByAchievementId(achievementId);
            if (product == null) {
                throw new RuntimeException("成果ID " + achievementId + " 不存在");
            }
            if (product.getReimbursementItemId() != null && !"".equals(product.getReimbursementItemId())) {
                throw new RuntimeException("成果【" + product.getName() + "】已关联到其他报销项目");
            }
        }
        
        // 3. 执行关联
        int updated = sam_rem_ProductMapper.batchAssociateAchievements(achievementIds, reimbursementItemId);
        
        // 4. 更新报销项目的统计信息
        updateProjectStatistics(reimbursementItemId);
        
        // 重新获取统计信息
        List<Sam_rem_Product> achievements = sam_rem_ProductMapper.selectSam_rem_ProductByReimbursementItemId(reimbursementItemId);
        double totalFee = 0.0;
        for (Sam_rem_Product achievement : achievements) {
            if (achievement.getReimbursementFee() != null) {
                totalFee += achievement.getReimbursementFee().doubleValue();
            }
        }
        
        result.put("successCount", updated);
        result.put("totalCount", achievementIds.size());
        result.put("totalAmount", totalFee);
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> cancelAssociation(String achievementId, String reimbursementItemId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 验证报销项目存在且状态为未确认
        SamReimbursementItems project = samReimbursementItemsMapper.selectSamReimbursementItemsById(Long.valueOf(reimbursementItemId));
        if (project == null) {
            throw new RuntimeException("报销项目不存在");
        }
        
        // 状态判断：1-已完成（已确认）不允许取消关联
        if ("1".equals(project.getStatus())) {
            throw new RuntimeException("报销项目已完成，无法取消关联");
        }
        
        // 2. 验证成果是否存在且确实关联到当前项目
        Sam_rem_Product product = sam_rem_ProductMapper.selectSam_rem_ProductByAchievementId(achievementId);
        if (product == null) {
            throw new RuntimeException("成果不存在");
        }
        if (!reimbursementItemId.equals(product.getReimbursementItemId())) {
            throw new RuntimeException("成果未关联到当前报销项目");
        }
        
        // 3. 检查是否已报销（有报销时间）
        if (product.getReimbursementDate() != null) {
            throw new RuntimeException("已报销的成果无法取消关联");
        }
        
        // 4. 执行取消关联
        int updated = sam_rem_ProductMapper.cancelAssociation(achievementId);
        
        // 4. 更新报销项目的统计信息
        updateProjectStatistics(reimbursementItemId);
        
        result.put("success", updated > 0);
        result.put("achievementId", achievementId);
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> batchCancelAssociation(List<String> achievementIds, String reimbursementItemId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 验证报销项目存在且状态为未确认
        SamReimbursementItems project = samReimbursementItemsMapper.selectSamReimbursementItemsById(Long.valueOf(reimbursementItemId));
        if (project == null) {
            throw new RuntimeException("报销项目不存在");
        }
        
        if ("1".equals(project.getStatus())) {
            throw new RuntimeException("报销项目已完成，无法取消关联");
        }
        
        // 2. 验证所有成果都关联到当前项目
        for (String achievementId : achievementIds) {
            Sam_rem_Product product = sam_rem_ProductMapper.selectSam_rem_ProductByAchievementId(achievementId);
            if (product == null) {
                throw new RuntimeException("成果ID " + achievementId + " 不存在");
            }
            if (!reimbursementItemId.equals(product.getReimbursementItemId())) {
                throw new RuntimeException("成果【" + product.getName() + "】未关联到当前报销项目");
            }
        }
        
        // 3. 执行批量取消关联
        int updated = sam_rem_ProductMapper.batchCancelAssociation(achievementIds);
        
        // 4. 更新报销项目的统计信息
        updateProjectStatistics(reimbursementItemId);
        
        result.put("successCount", updated);
        result.put("totalCount", achievementIds.size());
        
        return result;
    }

    /**
     * 更新报销项目统计信息
     */
    private void updateProjectStatistics(String reimbursementItemId) {
        // 重新统计关联成果数量
        int totalCount = sam_rem_ProductMapper.countByReimbursementItemId(reimbursementItemId);
        
        // 重新计算总金额
        List<Sam_rem_Product> achievements = sam_rem_ProductMapper.selectSam_rem_ProductByReimbursementItemId(reimbursementItemId);
        double totalFee = 0.0;  // 需报销金额总和（报名费）
        double paidFee = 0.0;  // 已发放金额
        for (Sam_rem_Product achievement : achievements) {
            // 统计需报销金额（报名费）
            if (achievement.getFee() != null) {
                totalFee += achievement.getFee().doubleValue();
            }
            // 统计已发放金额（已报销成果的实际报销金额）
            if (achievement.getIsReimburse() != null && achievement.getIsReimburse() == 1) {
                if (achievement.getReimbursementFee() != null) {
                    paidFee += achievement.getReimbursementFee().doubleValue();
                }
            }
        }
        
        SamReimbursementItems updateProject = new SamReimbursementItems();
        updateProject.setId(Long.valueOf(reimbursementItemId));
        updateProject.setAmount(totalCount);
        updateProject.setTotalFee(totalFee);
        updateProject.setPaidFee(paidFee);
        samReimbursementItemsMapper.updateSamReimbursementItems(updateProject);
    }

    @Override
    @Transactional
    public Map<String, Object> updateTransferStatus(List<String> achievementIds, String reimbursementItemId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 验证报销项目存在
        SamReimbursementItems project = samReimbursementItemsMapper.selectSamReimbursementItemsById(Long.valueOf(reimbursementItemId));
        if (project == null) {
            throw new RuntimeException("报销项目不存在");
        }
        
        // 2. 验证所有成果都关联到当前项目
        for (String achievementId : achievementIds) {
            Sam_rem_Product product = sam_rem_ProductMapper.selectSam_rem_ProductByAchievementId(achievementId);
            if (product == null) {
                throw new RuntimeException("成果ID " + achievementId + " 不存在");
            }
            if (!reimbursementItemId.equals(product.getReimbursementItemId())) {
                throw new RuntimeException("成果【" + product.getName() + "】未关联到当前报销项目");
            }
            // 检查是否已报销（通过报销时间判断）
            if (product.getReimbursementDate() != null) {
                throw new RuntimeException("成果【" + product.getName() + "】已经报销，无法重复报销");
            }
        }
        
        // 3. 执行批量更新报销状态
        int updated = sam_rem_ProductMapper.batchUpdateTransferStatus(achievementIds);
        
        // 4. 更新报销项目的统计信息
        updateProjectStatistics(reimbursementItemId);
        
        result.put("successCount", updated);
        result.put("totalCount", achievementIds.size());
        
        return result;
    }

    @Override
    public Map<String, Object> getPaymentInfo(String reimbursementItemId) {
        // 1. 验证报销项目存在
        SamReimbursementItems project = samReimbursementItemsMapper.selectSamReimbursementItemsById(Long.valueOf(reimbursementItemId));
        if (project == null) {
            throw new RuntimeException("报销项目不存在");
        }
        
        // 2. 查询支付信息
        Map<String, Object> paymentInfo = sam_rem_ProductMapper.getPaymentInfo(reimbursementItemId);
        if (paymentInfo == null) {
            paymentInfo = new HashMap<>();
            paymentInfo.put("projectName", project.getName());
            paymentInfo.put("reimbursementTime", project.getReimbursementTime());
            paymentInfo.put("totalAmount", 0);
            paymentInfo.put("totalCount", 0);
        }
        
        // 3. 从附件表查询收款码
        String sql = "SELECT id, file_url, store_name, origin_name FROM sam_attachment " +
                     "WHERE achievement_id IN (SELECT achievement_id FROM sam_achievement " +
                     "WHERE reimbursement_item_id = " + reimbursementItemId + " AND del_flag = '0') " +
                     "AND type = 'receipt_code' AND del_flag = '0' LIMIT 1";
        try {
            List<Map<String, Object>> attachments = jdbcTemplate.queryForList(sql);
            if (!attachments.isEmpty()) {
                Map<String, Object> attachment = attachments.get(0);
                String storeName = (String) attachment.get("store_name");
                if (storeName != null && storeName.endsWith(".pdf")) {
                    String uuid = storeName.replace(".pdf", "");
                    paymentInfo.put("qrCodeUrl", "/common/previewByUuid/" + uuid);
                } else {
                    paymentInfo.put("qrCodeUrl", attachment.get("file_url"));
                }
                paymentInfo.put("hasReceiptCode", true);
                paymentInfo.put("attachmentId", attachment.get("id"));
            } else {
                paymentInfo.put("qrCodeUrl", null);
                paymentInfo.put("hasReceiptCode", false);
            }
        } catch (Exception e) {
            log.error("查询收款码失败", e);
            paymentInfo.put("qrCodeUrl", null);
            paymentInfo.put("hasReceiptCode", false);
        }
        
        return paymentInfo;
    }

}  


