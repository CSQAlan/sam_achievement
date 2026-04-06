package com.ruoyi.reimbursement.service.impl;

import java.util.List;

import com.ruoyi.reimbursement.domain.SamReimbursementAchievementView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.reimbursement.mapper.SamReimbursementAchievementViewMapper;
import com.ruoyi.reimbursement.service.ISamReimbursementAchievementViewService;

/**
 * 报销项目关联成果Service业务层处理
 * 
 * @author lwz
 * @date 2026-03-01
 */
@Service
public class SamReimbursementAchievementViewServiceImpl implements ISamReimbursementAchievementViewService {
    
    @Autowired
    private SamReimbursementAchievementViewMapper samReimbursementAchievementViewMapper;

    /**
     * 查询报销项目关联成果
     * 
     * @param id 主键
     * @return 报销项目关联成果
     */
    @Override
    public SamReimbursementAchievementView selectSamReimbursementAchievementViewById(Long id) {
        return samReimbursementAchievementViewMapper.selectSamReimbursementAchievementViewById(id);
    }

    /**
     * 查询报销项目关联成果列表
     * 
     * @param samReimbursementAchievementView 报销项目关联成果
     * @return 报销项目关联成果
     */
    @Override
    public List<SamReimbursementAchievementView> selectSamReimbursementAchievementViewList(SamReimbursementAchievementView samReimbursementAchievementView) {
        return samReimbursementAchievementViewMapper.selectSamReimbursementAchievementViewList(samReimbursementAchievementView);
    }
}