package com.ruoyi.achievement.task;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.service.IBizAuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 高级业务审计日志清理任务
 */
@Component("bizAuditCleanTask")
public class BizAuditCleanTask
{
    private static final Logger log = LoggerFactory.getLogger(BizAuditCleanTask.class);

    public void clean30Days()
    {
        cleanBeforeDays(30);
    }

    public void cleanBeforeDays(Integer days)
    {
        int affected = SpringUtils.getBean(IBizAuditLogService.class).cleanBizAuditLogBeforeDays(days == null ? 30 : days);
        log.info("高级业务审计日志清理完成，days={}, affected={}", days, affected);
    }
}
