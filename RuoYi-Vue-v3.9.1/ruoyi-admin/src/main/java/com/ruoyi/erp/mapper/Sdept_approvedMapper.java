package com.ruoyi.erp.mapper;

import java.util.List;

import com.ruoyi.erp.domain.Sdept_approved;
import com.ruoyi.erp.domain.ErpContestant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 校级已审核的成果Mapper接口
 *
 * @author cyy
 * @date 2026-01-26
 */
@Mapper
public interface Sdept_approvedMapper
{
    /**
     * 查询校级已审核的成果
     *
     * @param id 校级已审核的成果主键
     * @return 校级已审核的成果
     */
    Sdept_approved selectSdept_approvedById(Long id);

    /**
     * 查询校级已审核的成果列表
     *
     * @param sdept_approved 校级已审核的成果
     * @return 校级已审核的成果集合
     */
    List<Sdept_approved> selectSdept_approvedList(Sdept_approved sdept_approved);

    /**
     * 查询指定审核状态的成果（仿照 dept_approvedMapper.selectByAuditStatuses）
     *
     * ✅ audit_status 在你的实体里是 String（"0"/"1"/"2"）
     * 所以这里也用 List<String> 最稳，避免类型不一致导致查不到
     *
     * @param auditStatus 审核状态列表（例如：["1","0"]）
     * @return 符合条件的成果集合
     */
    List<Sdept_approved> selectByAuditStatuses(@Param("auditStatus") List<String> auditStatus);

    /**
     * 新增校级已审核的成果
     *
     * @param sdept_approved 校级已审核的成果
     * @return 结果
     */
    int insertSdept_approved(Sdept_approved sdept_approved);

    /**
     * 修改校级已审核的成果
     *
     * @param sdept_approved 校级已审核的成果
     * @return 结果
     */
    int updateSdept_approved(Sdept_approved sdept_approved);

    /**
     * 删除校级已审核的成果
     *
     * @param id 校级已审核的成果主键
     * @return 结果
     */
    int deleteSdept_approvedById(Long id);

    /**
     * 批量删除校级已审核的成果
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSdept_approvedByIds(Long[] ids);

    /**
     * 批量删除参赛选手
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteErpContestantByOutcomeIds(Long[] ids);

    /**
     * 批量新增参赛选手
     *
     * @param erpContestantList 参赛选手列表
     * @return 结果
     */
    int batchErpContestant(List<ErpContestant> erpContestantList);

    /**
     * 通过校级已审核的成果主键删除参赛选手信息
     *
     * @param id 校级已审核的成果ID
     * @return 结果
     */
    int deleteErpContestantByOutcomeId(Long id);

    /**
     * 审核更新（仿照 dept_approvedMapper 的 wait 方法）
     *
     * ✅ 建议加 @Param，避免 xml 取不到参数
     *
     * @param id 成果ID
     * @param auditStatus 审核状态
     * @param schoolAuditReason 校级审核原因
     * @param schoolAuditUser 校级审核人（xml 里有用到）
     * @return 是否成功
     */
    boolean wait(
            @Param("id") Long id,
            @Param("auditStatus") String auditStatus,
            @Param("schoolAuditReason") String schoolAuditReason,
            @Param("schoolAuditUser") String schoolAuditUser
    );
}
