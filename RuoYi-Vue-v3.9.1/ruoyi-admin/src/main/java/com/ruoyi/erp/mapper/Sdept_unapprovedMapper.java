package com.ruoyi.erp.mapper;

import java.util.List;

import com.ruoyi.erp.domain.Sdept_unapproved;
import com.ruoyi.erp.domain.ErpContestant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 校级未审核的成果Mapper接口
 *
 * @author cyy
 * @date 2026-01-26
 */
@Mapper
public interface Sdept_unapprovedMapper
{
    /**
     * 查询校级未审核的成果
     *
     * @param id 校级未审核的成果主键
     * @return 校级未审核的成果
     */
    public Sdept_unapproved selectSdept_unapprovedById(Long id);

    /**
     * 查询校级未审核的成果列表
     *
     * @param sdept_unapproved 校级未审核的成果
     * @return 校级未审核的成果集合
     */
    public List<Sdept_unapproved> selectSdept_unapprovedList(Sdept_unapproved sdept_unapproved);

    /**
     * 查询指定审核状态的成果（仿照 dept_approvedMapper.selectByAuditStatuses）
     *
     * @param auditStatus 审核状态列表（例如：未审核/驳回等）
     * @return 符合条件的成果集合
     */
    List<Sdept_unapproved> selectByAuditStatuses(@Param("auditStatus") List<Integer> auditStatus);

    /**
     * 新增校级未审核的成果
     *
     * @param sdept_unapproved 校级未审核的成果
     * @return 结果
     */
    public int insertSdept_unapproved(Sdept_unapproved sdept_unapproved);

    /**
     * 修改校级未审核的成果
     *
     * @param sdept_unapproved 校级未审核的成果
     * @return 结果
     */
    public int updateSdept_unapproved(Sdept_unapproved sdept_unapproved);

    /**
     * 删除校级未审核的成果
     *
     * @param id 校级未审核的成果主键
     * @return 结果
     */
    public int deleteSdept_unapprovedById(Long id);

    /**
     * 批量删除校级未审核的成果
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSdept_unapprovedByIds(Long[] ids);

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
     * 通过校级未审核的成果主键删除参赛选手信息
     *
     * @param id 校级未审核的成果ID
     * @return 结果
     */
    public int deleteErpContestantByOutcomeId(Long id);

    /**
     * 审核更新（仿照 dept_approvedMapper 的 wait 方法）
     *
     * @param id 成果ID
     * @param auditStatus 审核状态
     * @param schoolAuditReason 校级审核原因（字段名按“校级”语义）
     * @return 是否成功
     */
    public boolean wait(Long id, String auditStatus, String schoolAuditReason);
}
