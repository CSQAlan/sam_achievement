package com.ruoyi.erp.mapper;

import java.util.List;

import com.ruoyi.erp.domain.dept_approved;
import com.ruoyi.erp.domain.dept_unapproved;
import com.ruoyi.erp.domain.ErpContestant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 院级未审核的成果Mapper接口
 * 
 * @author cyy
 * @date 2026-01-26
 */
@Mapper
public interface dept_unapprovedMapper 
{
    /**
     * 查询院级未审核的成果
     * 
     * @param id 院级未审核的成果主键
     * @return 院级未审核的成果
     */

    public dept_unapproved selectdept_unapprovedById(Long id);
    public List<dept_unapproved> selectdept_unapprovedList(dept_unapproved dept_unapproved);
    /**
     * 查询系部已审核和系部驳回的成果
     *
     * @param auditStatus 审核状态列表
     * @return 符合条件的成果集合
     */

    List<dept_unapproved> selectByAuditStatuses(@Param("auditStatus") List<Integer> auditStatus);


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
     * 删除院级未审核的成果
     * 
     * @param id 院级未审核的成果主键
     * @return 结果
     */
    public int deletedept_unapprovedById(Long id);

    /**
     * 批量删除院级未审核的成果
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletedept_unapprovedByIds(Long[] ids);

    /**
     * 批量删除参赛选手
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpContestantByOutcomeIds(Long[] ids);
    
    /**
     * 批量新增参赛选手
     * 
     * @param erpContestantList 参赛选手列表
     * @return 结果
     */
    public int batchErpContestant(List<ErpContestant> erpContestantList);
    

    /**
     * 通过院级未审核的成果主键删除参赛选手信息
     * 
     * @param id 院级未审核的成果ID
     * @return 结果
     */
    public int deleteErpContestantByOutcomeId(Long id);
    public boolean wait(Long id, String auditStatus, String deptAuditReason);

}
