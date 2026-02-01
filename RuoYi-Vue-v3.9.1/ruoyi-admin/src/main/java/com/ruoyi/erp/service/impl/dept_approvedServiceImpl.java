package com.ruoyi.erp.service.impl;

import java.util.Arrays;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.erp.domain.ErpContestant;
import com.ruoyi.erp.mapper.dept_approvedMapper;
import com.ruoyi.erp.domain.dept_approved;
import com.ruoyi.erp.service.Idept_approvedService;

/**
 * 院级已审核的成果Service业务层处理
 * 
 * @author cyy
 * @date 2026-01-26
 */
@Service
public  class dept_approvedServiceImpl implements Idept_approvedService
{
    @Autowired
    private dept_approvedMapper dept_approvedMapper;

    /**
     * 查询院级已审核的成果
     * 
     * @param id 院级已审核的成果主键
     * @return 院级已审核的成果
     */
    @Override
    public dept_approved selectdept_approvedById(Long id)
    {
        return dept_approvedMapper.selectdept_approvedById(id);
    }

    /**
     * 查询院级已审核的成果列表
     * 
     * @param dept_approved 院级已审核的成果
     * @return 院级已审核的成果
     */
    @Override
    public List<dept_approved> selectdept_approvedList(dept_approved dept_approved)
    {
        // 只筛选出院级审核通过（audit_status = 2）和院级驳回（audit_status = 1）的成果
        List<Integer> validStatuses = Arrays.asList(2, 1);  // 院级审核通过和院级驳回的状态值

        // 调用 Mapper 层查询符合条件的成果
        return dept_approvedMapper.selectByAuditStatuses(validStatuses);

    }


    /**
     * 新增院级已审核的成果
     * 
     * @param dept_approved 院级已审核的成果
     * @return 结果
     */
    @Transactional
    @Override
    public int insertdept_approved(dept_approved dept_approved)
    {
        dept_approved.setCreateTime(DateUtils.getNowDate());
        int rows = dept_approvedMapper.insertdept_approved(dept_approved);
        insertErpContestant(dept_approved);
        return rows;
    }

    /**
     * 修改院级已审核的成果
     * 
     * @param dept_approved 院级已审核的成果
     * @return 结果
     */
    @Transactional
    @Override
    public int updatedept_approved(dept_approved dept_approved)
    {
        dept_approved.setUpdateTime(DateUtils.getNowDate());
        dept_approvedMapper.deleteErpContestantByOutcomeId(dept_approved.getId());
        insertErpContestant(dept_approved);
        return dept_approvedMapper.updatedept_approved(dept_approved);
    }

    /**
     * 批量删除院级已审核的成果
     * 
     * @param ids 需要删除的院级已审核的成果主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletedept_approvedByIds(Long[] ids)
    {
        dept_approvedMapper.deleteErpContestantByOutcomeIds(ids);
        return dept_approvedMapper.deletedept_approvedByIds(ids);
    }

    /**
     * 删除院级已审核的成果信息
     * 
     * @param id 院级已审核的成果主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletedept_approvedById(Long id)
    {
        dept_approvedMapper.deleteErpContestantByOutcomeId(id);
        return dept_approvedMapper.deletedept_approvedById(id);
    }

    /**
     * 新增参赛选手信息
     * 
     * @param dept_approved 院级已审核的成果对象
     */
    public void insertErpContestant(dept_approved dept_approved)
    {
        List<ErpContestant> erpContestantList = dept_approved.getErpContestantList();
        Long id = dept_approved.getId();
        if (StringUtils.isNotNull(erpContestantList))
        {
            List<ErpContestant> list = new ArrayList<ErpContestant>();
            for (ErpContestant erpContestant : erpContestantList)
            {
                erpContestant.setOutcomeId(id);
                list.add(erpContestant);
            }
            if (list.size() > 0)
            {
                dept_approvedMapper.batchErpContestant(list);
            }
        }
    }
    @Override
    public List<dept_approved> selectDeptApprovedByStatus(dept_approved deptApproved)
    {
        // 设置只查询系部已审核和系部驳回的状态值
        List<Integer> validStatuses = Arrays.asList(2, 1);  // 2: 院级审核通过, 1: 院级驳回

        // 调用 Mapper 层查询符合条件的成果
        return dept_approvedMapper.selectByAuditStatuses(validStatuses);
    }

    @Override
    public boolean wait(Long id, String auditStatus, String deptAuditReason) {
        return dept_approvedMapper.wait(id,auditStatus,deptAuditReason);
    }

}
