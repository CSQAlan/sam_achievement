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
import com.ruoyi.erp.mapper.Sdept_approvedMapper;
import com.ruoyi.erp.domain.Sdept_approved;
import com.ruoyi.erp.service.ISdept_approvedService;

/**
 * 校级已审核的成果Service业务层处理
 *
 * @author cyy
 * @date 2026-01-26
 */
@Service
public class Sdept_approvedServiceImpl implements ISdept_approvedService
{
    @Autowired
    private Sdept_approvedMapper sdept_approvedMapper;

    /**
     * 查询校级已审核的成果
     *
     * @param id 校级已审核的成果主键
     * @return 校级已审核的成果
     */
    @Override
    public Sdept_approved selectSdept_approvedById(Long id)
    {
        return sdept_approvedMapper.selectSdept_approvedById(id);
    }

    /**
     * 查询校级已审核的成果列表
     *
     * 只筛选：校级驳回(5)、校级审核通过(6)
     */
    @Override
    public List<Sdept_approved> selectSdept_approvedList(Sdept_approved sdept_approved)
    {
        // 只查询校级已审核相关状态：5=校级驳回，6=校级审核通过
        List<Integer> validStatuses = Arrays.asList(5, 6);

        // 调用 Mapper 层查询符合条件的成果
        return sdept_approvedMapper.selectByAuditStatuses(validStatuses);
    }

    /**
     * 新增校级已审核的成果
     *
     * @param sdept_approved 校级已审核的成果
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSdept_approved(Sdept_approved sdept_approved)
    {
        sdept_approved.setCreateTime(DateUtils.getNowDate());
        int rows = sdept_approvedMapper.insertSdept_approved(sdept_approved);
        insertErpContestant(sdept_approved);
        return rows;
    }

    /**
     * 修改校级已审核的成果
     *
     * @param sdept_approved 校级已审核的成果
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSdept_approved(Sdept_approved sdept_approved)
    {
        sdept_approved.setUpdateTime(DateUtils.getNowDate());
        sdept_approvedMapper.deleteErpContestantByOutcomeId(sdept_approved.getId());
        insertErpContestant(sdept_approved);
        return sdept_approvedMapper.updateSdept_approved(sdept_approved);
    }

    /**
     * 批量删除校级已审核的成果
     *
     * @param ids 需要删除的校级已审核的成果主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSdept_approvedByIds(Long[] ids)
    {
        sdept_approvedMapper.deleteErpContestantByOutcomeIds(ids);
        return sdept_approvedMapper.deleteSdept_approvedByIds(ids);
    }

    /**
     * 删除校级已审核的成果信息
     *
     * @param id 校级已审核的成果主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSdept_approvedById(Long id)
    {
        sdept_approvedMapper.deleteErpContestantByOutcomeId(id);
        return sdept_approvedMapper.deleteSdept_approvedById(id);
    }

    /**
     * 新增参赛选手信息
     *
     * @param sdept_approved 校级已审核的成果对象
     */
    public void insertErpContestant(Sdept_approved sdept_approved)
    {
        List<ErpContestant> erpContestantList = sdept_approved.getErpContestantList();
        Long id = sdept_approved.getId();
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
                sdept_approvedMapper.batchErpContestant(list);
            }
        }
    }

    /**
     * 按状态查询（给 Controller 用，风格与院级已审核一致）
     */
    @Override
    public List<Sdept_approved> selectSdeptApprovedByStatus(Sdept_approved sdeptApproved)
    {
        // 只查询校级驳回(5)、校级审核通过(6)
        List<Integer> validStatuses = Arrays.asList(5, 6);
        return sdept_approvedMapper.selectByAuditStatuses(validStatuses);
    }

    /**
     * 审核更新（对应 mapper.xml 里的 wait）
     * 说明：如果你的 wait 只传 3 个参数，就把 schoolAuditUser 去掉即可
     */
    @Override
    public boolean wait(Long id, String auditStatus, String schoolAuditReason)
    {
        return sdept_approvedMapper.wait(id, auditStatus, schoolAuditReason);
    }
}
