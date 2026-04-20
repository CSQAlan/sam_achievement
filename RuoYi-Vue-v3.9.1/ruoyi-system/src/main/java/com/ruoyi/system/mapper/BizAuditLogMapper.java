package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.BizAuditLog;
import com.ruoyi.system.domain.BizAuditLogDetail;
import java.util.List;

/**
 * 高级业务审计日志 mapper
 */
public interface BizAuditLogMapper
{
    int insertBizAuditLog(BizAuditLog log);

    int batchInsertBizAuditLogDetail(List<BizAuditLogDetail> details);

    List<BizAuditLog> selectBizAuditLogList(BizAuditLog log);

    BizAuditLog selectBizAuditLogById(Long id);

    List<BizAuditLogDetail> selectBizAuditLogDetailListByLogId(Long logId);

    int deleteBizAuditLogByIds(Long[] ids);

    int deleteBizAuditLogDetailByLogIds(Long[] logIds);

    int cleanBizAuditLogDetailBeforeDays(Integer days);

    int cleanBizAuditLogBeforeDays(Integer days);
}
