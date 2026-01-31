package com.ruoyi.erp.service.impl;

import java.util.Arrays;
import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.erp.service.ISdept_unapprovedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.erp.domain.ErpContestant;
import com.ruoyi.erp.mapper.Sdept_unapprovedMapper;
import com.ruoyi.erp.domain.Sdept_unapproved;

/**
 * 校级未审核的成果Service业务层处理
 *
 * @author cyy
 * @date 2026-01-26
 */
@Service
public class Sdept_unapprovedServiceImpl implements ISdept_unapprovedService
{
    @Autowired
    private Sdept_unapprovedMapper sdept_unapprovedMapper;

    /**
     * 查询校级未审核的成果
     *
     * @param id 校级未审核的成果主键
     * @return 校级未审核的成果
     */
    @Override
    public Sdept_unapproved selectSdept_unapprovedById(Long id)
    {
        return sdept_unapprovedMapper.selectSdept_unapprovedById(id);
    }

    /**
     * 查询校级未审核的成果列表
     *
     * 只筛选：待校级审核(2)
     */
    @Override
    public List<Sdept_unapproved> selectSdept_unapprovedList(Sdept_unapproved sdept_unapproved)
    {
        // 只查询校级待审核：2=待校级审核
        List<Integer> validStatuses = Arrays.asList(2);

        // 调用 Mapper 层查询符合条件的成果
        return sdept_unapprovedMapper.selectByAuditStatuses(validStatuses);
    }

    /**
     * 新增校级未审核的成果
     *
     * @param sdept_unapproved 校级未审核的成果
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSdept_unapproved(Sdept_unapproved sdept_unapproved)
    {
        sdept_unapproved.setCreateTime(DateUtils.getNowDate());
        int rows = sdept_unapprovedMapper.insertSdept_unapproved(sdept_unapproved);
        insertErpContestant(sdept_unapproved);
        return rows;
    }

    /**
     * 修改校级未审核的成果
     *
     * @param sdept_unapproved 校级未审核的成果
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSdept_unapproved(Sdept_unapproved sdept_unapproved)
    {
        sdept_unapproved.setUpdateTime(DateUtils.getNowDate());
        sdept_unapprovedMapper.deleteErpContestantByOutcomeId(sdept_unapproved.getId());
        insertErpContestant(sdept_unapproved);
        return sdept_unapprovedMapper.updateSdept_unapproved(sdept_unapproved);
    }

    /**
     * 批量删除校级未审核的成果
     *
     * @param ids 需要删除的校级未审核的成果主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSdept_unapprovedByIds(Long[] ids)
    {
        sdept_unapprovedMapper.deleteErpContestantByOutcomeIds(ids);
        return sdept_unapprovedMapper.deleteSdept_unapprovedByIds(ids);
    }

    /**
     * 删除校级未审核的成果信息
     *
     * @param id 校级未审核的成果主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSdept_unapprovedById(Long id)
    {
        sdept_unapprovedMapper.deleteErpContestantByOutcomeId(id);
        return sdept_unapprovedMapper.deleteSdept_unapprovedById(id);
    }

    /**
     * 新增参赛选手信息
     *
     * @param sdept_unapproved 校级未审核的成果对象
     */
    public void insertErpContestant(Sdept_unapproved sdept_unapproved)
    {
        List<ErpContestant> erpContestantList = sdept_unapproved.getErpContestantList();
        Long id = sdept_unapproved.getId();
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
                sdept_unapprovedMapper.batchErpContestant(list);
            }
        }
    }

    /**
     * 按状态查询（给 Controller 用，风格与院级一致）
     */
    @Override
    public List<Sdept_unapproved> selectSdeptUnapprovedByStatus(Sdept_unapproved sdeptUnapproved)
    {
        // 只查询校级待审核：2=待校级审核
        List<Integer> validStatuses = Arrays.asList(2);
        return sdept_unapprovedMapper.selectByAuditStatuses(validStatuses);
    }
    @Override
    public boolean wait(Long id, String auditStatus, String schoolAuditReason)
    {
        return sdept_unapprovedMapper.wait(id, auditStatus, schoolAuditReason);
    }
}
