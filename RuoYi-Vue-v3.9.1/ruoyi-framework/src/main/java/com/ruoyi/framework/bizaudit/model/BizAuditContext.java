package com.ruoyi.framework.bizaudit.model;

import com.ruoyi.common.annotation.BizAudit;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 业务审计上下文
 */
public class BizAuditContext
{
    private BizAudit annotation;

    private ProceedingJoinPoint joinPoint;

    private Object[] args;

    private Object result;

    private Throwable exception;

    private Long startTime;

    public BizAudit getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(BizAudit annotation)
    {
        this.annotation = annotation;
    }

    public ProceedingJoinPoint getJoinPoint()
    {
        return joinPoint;
    }

    public void setJoinPoint(ProceedingJoinPoint joinPoint)
    {
        this.joinPoint = joinPoint;
    }

    public Object[] getArgs()
    {
        return args;
    }

    public void setArgs(Object[] args)
    {
        this.args = args;
    }

    public Object getResult()
    {
        return result;
    }

    public void setResult(Object result)
    {
        this.result = result;
    }

    public Throwable getException()
    {
        return exception;
    }

    public void setException(Throwable exception)
    {
        this.exception = exception;
    }

    public Long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Long startTime)
    {
        this.startTime = startTime;
    }
}
