package com.ruoyi.system.service.impl;

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
import com.ruoyi.system.domain.SamReimbursementItems;
import com.ruoyi.system.mapper.Sam_rem_ProductMapper;
import com.ruoyi.system.domain.Sam_rem_Product;
import com.ruoyi.system.service.ISam_rem_ProductService;
import com.ruoyi.system.mapper.SamReimbursementRatioMapper;
import com.ruoyi.system.mapper.SamReimbursementItemsMapper;

/**
 * 报销项目详情Service业务层处理
 * 
 * @author luo
 * @date 2026-03-22
 */
@Service
public class Sam_rem_ProductServiceImpl implements ISam_rem_ProductService 
{
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
        
        // 4. 更新报销项目的总金额和已发放金额
        // 这里需要根据实际的报销项目表结构进行更新
        // 暂时省略，实际项目中需要实现
        
        result.put("success", true);
        result.put("message", "报销金额计算成功");
        result.put("totalAmount", totalAmount);
        result.put("paidAmount", paidAmount);
        result.put("productCount", productList.size());
        
        return result;
    }

    /**
     * 关联成果到报销项目
     */
    @Transactional
    @Override
    public Map<String, Object> associateAchievements(String reimbursementItemId, String[] achievementIds) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 验证报销项目状态（必须为未确认状态）
            SamReimbursementItems reimbursementItem = samReimbursementItemsMapper.selectSamReimbursementItemsById(Long.parseLong(reimbursementItemId));
            if (reimbursementItem == null) {
                result.put("success", false);
                result.put("message", "报销项目不存在");
                return result;
            }
            
            // 检查报销项目状态，只有状态为0（进行中）时才能关联成果
            if (!"0".equals(reimbursementItem.getStatus())) {
                result.put("success", false);
                result.put("message", "只有进行中的报销项目才能关联成果");
                return result;
            }
            
            // 2. 验证成果是否可关联（未关联到其他报销项目）
            List<Sam_rem_Product> products = sam_rem_ProductMapper.selectSam_rem_ProductList(new Sam_rem_Product());
            Map<String, Sam_rem_Product> productMap = new HashMap<>();
            for (Sam_rem_Product product : products) {
                productMap.put(product.getAchievementId(), product);
            }
            
            for (String achievementId : achievementIds) {
                Sam_rem_Product product = productMap.get(achievementId);
                if (product == null) {
                    result.put("success", false);
                    result.put("message", "成果不存在: " + achievementId);
                    return result;
                }
                
                if (product.getReimbursementItemId() != null && !product.getReimbursementItemId().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "成果已关联到其他报销项目: " + product.getName());
                    return result;
                }
            }
            
            // 3. 更新成果的 reimbursementItemId 字段，关联到当前报销项目
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("reimbursementItemId", reimbursementItemId);
            updateMap.put("achievementIds", achievementIds);
            int updateCount = sam_rem_ProductMapper.batchUpdateReimbursementItemId(updateMap);
            
            if (updateCount != achievementIds.length) {
                result.put("success", false);
                result.put("message", "关联成果失败");
                return result;
            }
            
            // 4. 更新报销项目的成果数量和总金额
            Map<String, Object> recalculateResult = recalculateReimbursementAmount(reimbursementItemId);
            if ((Boolean) recalculateResult.get("success")) {
                // 更新报销项目的总金额和数量
                SamReimbursementItems updateItem = new SamReimbursementItems();
                updateItem.setId(Long.parseLong(reimbursementItemId));
                updateItem.setTotalFee((Double) recalculateResult.get("totalAmount"));
                updateItem.setAmount((Integer) recalculateResult.get("productCount"));
                samReimbursementItemsMapper.updateSamReimbursementItems(updateItem);
            }
            
            result.put("success", true);
            result.put("message", "关联成果成功");
            result.put("associatedCount", updateCount);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "关联成果失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 查询未关联的成果列表
     */
    @Override
    public List<Sam_rem_Product> selectUnassociatedAchievements(Sam_rem_Product sam_rem_Product) {
        return sam_rem_ProductMapper.selectUnassociatedAchievements(sam_rem_Product);
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
        
        // 3. 执行取消关联
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
        double totalFee = 0.0;
        double paidFee = 0.0;
        for (Sam_rem_Product achievement : achievements) {
            if (achievement.getReimbursementFee() != null) {
                totalFee += achievement.getReimbursementFee().doubleValue();
            }
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

} 


