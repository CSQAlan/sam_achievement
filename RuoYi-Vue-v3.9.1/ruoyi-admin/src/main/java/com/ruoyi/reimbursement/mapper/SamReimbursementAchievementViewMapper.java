package com.ruoyi.reimbursement.mapper;

import java.util.List;
import com.ruoyi.reimbursement.domain.SamReimbursementAchievementView;

/**
 * 报销项目关联成果Mapper接口
 * 
 * @author lwz
 * @date 2026-03-15
 */
public interface SamReimbursementAchievementViewMapper 
{
    /**
     * 查询报销项目关联成果
     * 
     * @param id 报销项目关联成果主键
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

    /**
     * 新增报销项目关联成果
     * 
     * @param samReimbursementAchievementView 报销项目关联成果
     * @return 结果
     */
    public int insertSamReimbursementAchievementView(SamReimbursementAchievementView samReimbursementAchievementView);

    /**
     * 修改报销项目关联成果
     * 
     * @param samReimbursementAchievementView 报销项目关联成果
     * @return 结果
     */
    public int updateSamReimbursementAchievementView(SamReimbursementAchievementView samReimbursementAchievementView);

    /**
     * 删除报销项目关联成果
     * 
     * @param id 报销项目关联成果主键
     * @return 结果
     */
    public int deleteSamReimbursementAchievementViewById(Long id);

    /**
     * 批量删除报销项目关联成果
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamReimbursementAchievementViewByIds(Long[] ids);
}
