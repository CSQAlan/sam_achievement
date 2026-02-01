package com.ruoyi.erp.service;

import java.util.List;
import com.ruoyi.erp.domain.dept_approved;

/**
 * 院级已审核的成果Service接口
 * 
 * @author cyy
 * @date 2026-01-26
 */

public interface Idept_approvedService 
{
    /**
     * 查询院级已审核的成果
     * 
     * @param id 院级已审核的成果主键
     * @return 院级已审核的成果
     */
    public dept_approved selectdept_approvedById(Long id);

    /**
     * 查询院级已审核的成果列表
     * 
     * @param dept_approved 院级已审核的成果
     * @return 院级已审核的成果集合
     */
    public List<dept_approved> selectdept_approvedList(dept_approved dept_approved);

    /**
     * 新增院级已审核的成果
     * 
     * @param dept_approved 院级已审核的成果
     * @return 结果
     */
    public int insertdept_approved(dept_approved dept_approved);

    /**
     * 修改院级已审核的成果
     * 
     * @param dept_approved 院级已审核的成果
     * @return 结果
     */
    public int updatedept_approved(dept_approved dept_approved);

    /**
     * 批量删除院级已审核的成果
     * 
     * @param ids 需要删除的院级已审核的成果主键集合
     * @return 结果
     */
    public int deletedept_approvedByIds(Long[] ids);

    /**
     * 删除院级已审核的成果信息
     * 
     * @param id 院级已审核的成果主键
     * @return 结果
     */
    public int deletedept_approvedById(Long id);

    List<dept_approved> selectDeptApprovedByStatus(dept_approved deptApproved);

    boolean wait(Long id, String auditStatus, String deptAuditReason);
}
