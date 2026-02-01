package com.ruoyi.erp.service;

import java.util.List;
import com.ruoyi.erp.domain.Sdept_approved;

/**
 * 校级已审核的成果Service接口
 *
 * @author cyy
 * @date 2026-01-26
 */
public interface ISdept_approvedService
{
    /**
     * 查询校级已审核的成果
     *
     * @param id 校级已审核的成果主键
     * @return 校级已审核的成果
     */
    public Sdept_approved selectSdept_approvedById(Long id);

    /**
     * 查询校级已审核的成果列表
     *
     * @param sdept_approved 校级已审核的成果
     * @return 校级已审核的成果集合
     */
    public List<Sdept_approved> selectSdept_approvedList(Sdept_approved sdept_approved);

    /**
     * 新增校级已审核的成果
     *
     * @param sdept_approved 校级已审核的成果
     * @return 结果
     */
    public int insertSdept_approved(Sdept_approved sdept_approved);

    /**
     * 修改校级已审核的成果
     *
     * @param sdept_approved 校级已审核的成果
     * @return 结果
     */
    public int updateSdept_approved(Sdept_approved sdept_approved);

    /**
     * 批量删除校级已审核的成果
     *
     * @param ids 需要删除的校级已审核的成果主键集合
     * @return 结果
     */
    public int deleteSdept_approvedByIds(Long[] ids);

    /**
     * 删除校级已审核的成果信息
     *
     * @param id 校级已审核的成果主键
     * @return 结果
     */
    public int deleteSdept_approvedById(Long id);

    /**
     * 按状态查询校级已审核的成果（与院级保持一致风格）
     */
    List<Sdept_approved> selectSdeptApprovedByStatus(Sdept_approved sdeptApproved);

    /**
     * 校级审核操作（与院级 wait 一致）
     *
     * @param id 成果id
     * @param auditStatus 审核状态
     * @param schoolAuditReason 审核原因
     * @return 是否成功
     */
    boolean wait(Long id, String auditStatus, String schoolAuditReason,String schoolAuditUser);
}
