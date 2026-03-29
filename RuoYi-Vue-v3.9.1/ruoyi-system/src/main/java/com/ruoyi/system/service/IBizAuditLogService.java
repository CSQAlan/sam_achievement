package com.ruoyi.system.service;

import com.ruoyi.system.domain.BizAuditLog;
import com.ruoyi.system.domain.BizAuditLogDetail;
import java.util.List;

/**
 * 高级业务审计日志 service
 */
public interface IBizAuditLogService
{
    int insertBizAuditLog(BizAuditLog log, List<BizAuditLogDetail> details);

    List<BizAuditLog> selectBizAuditLogList(BizAuditLog log);

    BizAuditLog selectBizAuditLogById(Long id);

    List<BizAuditLogDetail> selectBizAuditLogDetailListByLogId(Long logId);

    int deleteBizAuditLogByIds(Long[] ids);

    int cleanBizAuditLogBeforeDays(Integer days);
}
