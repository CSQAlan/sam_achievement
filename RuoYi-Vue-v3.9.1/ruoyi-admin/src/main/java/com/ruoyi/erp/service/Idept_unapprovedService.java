package com.ruoyi.erp.service;

import java.util.List;

import com.ruoyi.erp.domain.dept_unapproved;

/**
 * 院级未审核的成果Service接口
 * 
 * @author cyy
 * @date 2026-01-26
 */
public interface Idept_unapprovedService 
{
    /**
     * 查询院级未审核的成果
     * 
     * @param id 院级未审核的成果主键
     * @return 院级未审核的成果
     */
    public dept_unapproved selectdept_unapprovedById(Long id);

    /**
     * 查询院级未审核的成果列表
     * 
     * @param dept_unapproved 院级未审核的成果
     * @return 院级未审核的成果集合
     */
    public List<dept_unapproved> selectdept_unapprovedList(dept_unapproved dept_unapproved);

    /**
     * 新增院级未审核的成果
     * 
     * @param dept_unapproved 院级未审核的成果
     * @return 结果
     */
    public int insertdept_unapproved(dept_unapproved dept_unapproved);

    /**
     * 修改院级未审核的成果
     * 
     * @param dept_unapproved 院级未审核的成果
     * @return 结果
     */
    public int updatedept_unapproved(dept_unapproved dept_unapproved);

    /**
     * 批量删除院级未审核的成果
     * 
     * @param ids 需要删除的院级未审核的成果主键集合
     * @return 结果
     */
    public int deletedept_unapprovedByIds(Long[] ids);

    /**
     * 删除院级未审核的成果信息
     * 
     * @param id 院级未审核的成果主键
     * @return 结果
     */
    public int deletedept_unapprovedById(Long id);
    List<dept_unapproved> selectDeptUnpprovedByStatus(dept_unapproved deptUnapproved);

    boolean wait(Long id, String auditStatus, String deptAuditReason);
}
