package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.BizAuditLog;
import com.ruoyi.system.domain.BizAuditLogDetail;
import com.ruoyi.system.mapper.BizAuditLogMapper;
import com.ruoyi.system.service.IBizAuditLogService;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 高级业务审计日志 service 实现
 */
@Service
public class BizAuditLogServiceImpl implements IBizAuditLogService
{
    @Autowired
    private BizAuditLogMapper bizAuditLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBizAuditLog(BizAuditLog log, List<BizAuditLogDetail> details)
    {
        int rows = bizAuditLogMapper.insertBizAuditLog(log);
        if (rows > 0 && log.getId() != null && details != null && !details.isEmpty())
        {
            for (BizAuditLogDetail detail : details)
            {
                detail.setLogId(log.getId());
            }
            bizAuditLogMapper.batchInsertBizAuditLogDetail(details);
        }
        return rows;
    }

    @Override
    public List<BizAuditLog> selectBizAuditLogList(BizAuditLog log)
    {
        return bizAuditLogMapper.selectBizAuditLogList(log);
    }

    @Override
    public BizAuditLog selectBizAuditLogById(Long id)
    {
        return bizAuditLogMapper.selectBizAuditLogById(id);
    }

    @Override
    public List<BizAuditLogDetail> selectBizAuditLogDetailListByLogId(Long logId)
    {
        List<BizAuditLogDetail> details = bizAuditLogMapper.selectBizAuditLogDetailListByLogId(logId);
        return details == null ? Collections.<BizAuditLogDetail>emptyList() : details;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBizAuditLogByIds(Long[] ids)
    {
        bizAuditLogMapper.deleteBizAuditLogDetailByLogIds(ids);
        return bizAuditLogMapper.deleteBizAuditLogByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanBizAuditLogBeforeDays(Integer days)
    {
        bizAuditLogMapper.cleanBizAuditLogDetailBeforeDays(days);
        return bizAuditLogMapper.cleanBizAuditLogBeforeDays(days);
    }
}
