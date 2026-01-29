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
import com.ruoyi.erp.mapper.dept_unapprovedMapper;
import com.ruoyi.erp.domain.dept_unapproved;
import com.ruoyi.erp.service.Idept_unapprovedService;

/**
 * 院级未审核的成果Service业务层处理
 *
 * @author cyy
 * @date 2026-01-26
 */
@Service
public class dept_unapprovedServiceImpl implements Idept_unapprovedService
{
    @Autowired
    private dept_unapprovedMapper dept_unapprovedMapper;

    /**
     * 查询院级未审核的成果
     *
     * @param id 院级未审核的成果主键
     * @return 院级未审核的成果
     */
    @Override
    public dept_unapproved selectdept_unapprovedById(Long id)
    {
        return dept_unapprovedMapper.selectdept_unapprovedById(id);
    }

    /**
     * 查询院级未审核的成果列表
     *
     * audit_status = 1 ：待院级审核（你字典里原“待系部审核”已改为“待院级审核”）
     */
    @Override
    public List<dept_unapproved> selectdept_unapprovedList(dept_unapproved dept_unapproved)
    {
        // 只筛选出待院级审核（audit_status = 1）的成果
        List<Integer> validStatuses = Arrays.asList(1);

        // 调用 Mapper 层查询符合条件的成果
        return dept_unapprovedMapper.selectByAuditStatuses(validStatuses);
    }

    /**
     * 新增院级未审核的成果
     *
     * @param dept_unapproved 院级未审核的成果
     * @return 结果
     */
    @Transactional
    @Override
    public int insertdept_unapproved(dept_unapproved dept_unapproved)
    {
        dept_unapproved.setCreateTime(DateUtils.getNowDate());
        int rows = dept_unapprovedMapper.insertdept_unapproved(dept_unapproved);
        insertErpContestant(dept_unapproved);
        return rows;
    }

    /**
     * 修改院级未审核的成果
     *
     * @param dept_unapproved 院级未审核的成果
     * @return 结果
     */
    @Transactional
    @Override
    public int updatedept_unapproved(dept_unapproved dept_unapproved)
    {
        dept_unapproved.setUpdateTime(DateUtils.getNowDate());
        dept_unapprovedMapper.deleteErpContestantByOutcomeId(dept_unapproved.getId());
        insertErpContestant(dept_unapproved);
        return dept_unapprovedMapper.updatedept_unapproved(dept_unapproved);
    }

    /**
     * 批量删除院级未审核的成果
     *
     * @param ids 需要删除的院级未审核的成果主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletedept_unapprovedByIds(Long[] ids)
    {
        dept_unapprovedMapper.deleteErpContestantByOutcomeIds(ids);
        return dept_unapprovedMapper.deletedept_unapprovedByIds(ids);
    }

    /**
     * 删除院级未审核的成果信息
     *
     * @param id 院级未审核的成果主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletedept_unapprovedById(Long id)
    {
        dept_unapprovedMapper.deleteErpContestantByOutcomeId(id);
        return dept_unapprovedMapper.deletedept_unapprovedById(id);
    }

    /**
     * 新增参赛选手信息
     *
     * @param dept_unapproved 院级未审核的成果对象
     */
    public void insertErpContestant(dept_unapproved dept_unapproved)
    {
        List<ErpContestant> erpContestantList = dept_unapproved.getErpContestantList();
        Long id = dept_unapproved.getId();
        if (StringUtils.isNotNull(erpContestantList))
        {
            List<ErpContestant> list = new ArrayList<>();
            for (ErpContestant erpContestant : erpContestantList)
            {
                erpContestant.setOutcomeId(id);
                list.add(erpContestant);
            }
            if (list.size() > 0)
            {
                dept_unapprovedMapper.batchErpContestant(list);
            }
        }
    }

    /**
     * 按状态查询（给 Controller 用，风格与 dept_approvedServiceImpl 一致）
     */
    @Override
    public List<dept_unapproved> selectDeptUnpprovedByStatus(dept_unapproved deptUnpproved)
    {
        // 只查询待院级审核（1）
        List<Integer> validStatuses = Arrays.asList(1);
        return dept_unapprovedMapper.selectByAuditStatuses(validStatuses);
    }

    /**
     * 审核更新（对应 mapper.xml 里的 wait）
     */
    @Override
    public boolean wait(Long id, String auditStatus, String deptAuditReason)
    {
        return dept_unapprovedMapper.wait(id, auditStatus, deptAuditReason);
    }
}
