package com.ruoyi.system.service.impl;

import java.util.List;
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


}

