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
public class Sdept_approvedServiceImpl implements ISdept_approvedService {
    @Autowired
    private Sdept_approvedMapper sdept_approvedMapper;

    @Override
    public Sdept_approved selectSdept_approvedById(Long id) {
        return sdept_approvedMapper.selectSdept_approvedById(id);
    }

    /**
     * 查询校级已审核的成果列表
     * ✅ 只筛选：校级审核通过(1)、校级驳回(0)
     */
    @Override
    public List<Sdept_approved> selectSdept_approvedList(Sdept_approved sdept_approved) {
        // ✅ 统一成 String，和实体/mapper 一致
        List<String> validStatuses = Arrays.asList("1", "0");

        // ⚠️ 注意：selectByAuditStatuses 只按状态查，其他筛选条件会丢失
        // 如果你希望支持 year/category 等筛选，请改走 mapper.selectSdept_approvedList(sdept_approved)
        return sdept_approvedMapper.selectByAuditStatuses(validStatuses);
    }

    @Transactional
    @Override
    public int insertSdept_approved(Sdept_approved sdept_approved) {
        sdept_approved.setCreateTime(DateUtils.getNowDate());
        int rows = sdept_approvedMapper.insertSdept_approved(sdept_approved);
        insertErpContestant(sdept_approved);
        return rows;
    }

    @Transactional
    @Override
    public int updateSdept_approved(Sdept_approved sdept_approved) {
        sdept_approved.setUpdateTime(DateUtils.getNowDate());
        sdept_approvedMapper.deleteErpContestantByOutcomeId(sdept_approved.getId());
        insertErpContestant(sdept_approved);
        return sdept_approvedMapper.updateSdept_approved(sdept_approved);
    }

    @Transactional
    @Override
    public int deleteSdept_approvedByIds(Long[] ids) {
        sdept_approvedMapper.deleteErpContestantByOutcomeIds(ids);
        return sdept_approvedMapper.deleteSdept_approvedByIds(ids);
    }

    @Transactional
    @Override
    public int deleteSdept_approvedById(Long id) {
        sdept_approvedMapper.deleteErpContestantByOutcomeId(id);
        return sdept_approvedMapper.deleteSdept_approvedById(id);
    }

    /**
     * 新增参赛选手信息
     */
    public void insertErpContestant(Sdept_approved sdept_approved) {
        List<ErpContestant> erpContestantList = sdept_approved.getErpContestantList();
        Long id = sdept_approved.getId();
        if (StringUtils.isNotNull(erpContestantList)) {
            List<ErpContestant> list = new ArrayList<>();
            for (ErpContestant erpContestant : erpContestantList) {
                erpContestant.setOutcomeId(id);
                list.add(erpContestant);
            }
            if (list.size() > 0) {
                sdept_approvedMapper.batchErpContestant(list);
            }
        }
    }

    /**
     * 按状态查询（给 Controller 用）
     * ✅ 只查 0/1
     */
    @Override
    public List<Sdept_approved> selectSdeptApprovedByStatus(Sdept_approved sdeptApproved) {
        List<String> validStatuses = Arrays.asList("1", "0");
        return sdept_approvedMapper.selectByAuditStatuses(validStatuses);
    }

    /**
     * ✅ 校级审核更新（对应 mapper.xml 的 wait）
     * 你 Mapper 已改成 4 个参数（含 schoolAuditUser），这里也必须一致
     */
    @Override
    public boolean wait(Long id, String auditStatus, String schoolAuditReason, String schoolAuditUser) {
        return sdept_approvedMapper.wait(id, auditStatus, schoolAuditReason, schoolAuditUser);
    }
}
