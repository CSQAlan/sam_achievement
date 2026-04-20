package com.ruoyi.reimbursement.service;

import java.util.List;

import com.ruoyi.reimbursement.domain.SamReimbursementAchievementView;

/**
 * 报销项目关联成果Service接口
 * 
 * @author lwz
 * @date 2026-03-01
 */
public interface ISamReimbursementAchievementViewService {
    
    /**
     * 查询报销项目关联成果
     * 
     * @param id 主键
     * @return 报销项目关联成果
     */
    public SamReimbursementAchievementView selectSamReimbursementAchievementViewById(Long id);

    /**
     * 查询报销项目关联成果列表
     * 
     * @param samReimbursementAchievementView 报销项目关联成果
     * @return 报销项目关联成果集合
     */
    public List<SamReimbursementAchievementView> selectSamReimbursementAchievementViewList(SamReimbursementAchievementView samReimbursementAchievementView);
}