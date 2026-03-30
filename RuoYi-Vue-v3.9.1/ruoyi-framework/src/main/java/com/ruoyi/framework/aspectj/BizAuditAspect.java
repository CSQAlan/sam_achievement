package com.ruoyi.framework.aspectj;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.utils.ExceptionUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.common.utils.http.UserAgentUtils;
import com.ruoyi.framework.bizaudit.BizAuditHandler;
import com.ruoyi.framework.bizaudit.model.BizAuditContext;
import com.ruoyi.framework.bizaudit.model.BizAuditDiffResult;
import com.ruoyi.framework.bizaudit.util.BizAuditDiffUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.domain.BizAuditLog;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 高级业务审计切面
 */
@Aspect
@Component
public class BizAuditAspect
{
    private static final Logger log = LoggerFactory.getLogger(BizAuditAspect.class);

    @Around("@annotation(com.ruoyi.common.annotation.BizAudit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable
    {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        BizAudit annotation = method.getAnnotation(BizAudit.class);
        BizAuditContext context = new BizAuditContext();
        context.setAnnotation(annotation);
        context.setJoinPoint(joinPoint);
        context.setArgs(joinPoint.getArgs());
        context.setStartTime(System.currentTimeMillis());

        BizAuditHandler handler = resolveHandler(annotation);
        Object before = null;
        Object result = null;
        Throwable throwable = null;
        try
        {
            before = handler.captureBefore(context);
            result = joinPoint.proceed();
            context.setResult(result);
            return result;
        }
        catch (Throwable ex)
        {
            throwable = ex;
            context.setException(ex);
            throw ex;
        }
        finally
        {
            try
            {
                Object after = throwable == null ? handler.captureAfter(context, result) : null;
                BizAuditLog auditLog = buildAuditLog(joinPoint, annotation, handler, context, before, after, throwable);
                BizAuditDiffResult diffResult = BizAuditDiffUtils.buildDiff(before, after, handler, context);
                auditLog.setDiffJson(diffResult.getDiffJson());
                if (annotation.async())
                {
                    AsyncManager.me().execute(AsyncFactory.recordBizAudit(auditLog, diffResult.getDetails()));
                }
                else
                {
                    SpringUtils.getBean(com.ruoyi.system.service.IBizAuditLogService.class)
                            .insertBizAuditLog(auditLog, diffResult.getDetails());
                }
            }
            catch (Exception auditEx)
            {
                log.error("业务审计记录失败: {}", auditEx.getMessage(), auditEx);
            }
        }
    }

    private BizAuditLog buildAuditLog(ProceedingJoinPoint joinPoint, BizAudit annotation, BizAuditHandler handler,
            BizAuditContext context, Object before, Object after, Throwable throwable)
    {
        BizAuditLog logEntity = new BizAuditLog();
        LoginUser loginUser = null;
        try
        {
            loginUser = SecurityUtils.getLoginUser();
        }
        catch (Exception ignore)
        {
        }

        HttpServletRequest request = null;
        try
        {
            request = ServletUtils.getRequest();
        }
        catch (Exception ignore)
        {
        }

        BizAuditOpType resolvedOpType = handler.resolveOpType(context, before, after);
        Set<String> sensitiveFields = handler.sensitiveFields(context);
        logEntity.setTraceId(UUID.randomUUID().toString().replace("-", ""));
        logEntity.setBizType(annotation.bizType());
        logEntity.setBizName(StringUtils.defaultIfBlank(handler.resolveBizName(context, before, after), annotation.bizName()));
        logEntity.setBizId(handler.resolveBizId(context, before, after));
        logEntity.setBizKey(handler.resolveBizKey(context, before, after));
        logEntity.setOpType(resolvedOpType.name());
        logEntity.setSuccess(throwable == null ? 1 : 0);
        logEntity.setResultStatus(throwable == null ? "SUCCESS" : "FAIL");
        logEntity.setBatchFlag(handler.resolveBatchFlag(context, before, after));
        logEntity.setBatchCount(handler.resolveBatchCount(context, before, after));
        logEntity.setBeforeJson(BizAuditDiffUtils.maskSnapshotJson(before, sensitiveFields));
        logEntity.setAfterJson(BizAuditDiffUtils.maskSnapshotJson(after, sensitiveFields));
        logEntity.setExtraJson(handler.buildExtra(context, before, after).toJSONString());
        logEntity.setDurationMs(System.currentTimeMillis() - context.getStartTime());

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        logEntity.setClassName(className);
        logEntity.setMethodName(methodName);
        logEntity.setMethod(className + "." + methodName + "()");

        if (request != null)
        {
            logEntity.setRequestUri(StringUtils.substring(request.getRequestURI(), 0, 255));
            logEntity.setRequestMethod(request.getMethod());
            logEntity.setRequestId(StringUtils.substring(request.getHeader("X-Request-Id"), 0, 64));
            logEntity.setOperIp(IpUtils.getIpAddr());
            String userAgent = StringUtils.substring(request.getHeader("User-Agent"), 0, 500);
            logEntity.setOperBrowser(UserAgentUtils.getBrowser(userAgent));
            logEntity.setOperOs(UserAgentUtils.getOperatingSystem(userAgent));
        }

        if (annotation.saveRequest())
        {
            logEntity.setRequestParams(StringUtils.substring(BizAuditDiffUtils.maskSnapshotJson(JSON.parse(argsToJson(joinPoint.getArgs())), sensitiveFields), 0, 20000));
        }

        if (loginUser != null)
        {
            logEntity.setOperUserId(loginUser.getUserId());
            logEntity.setOperUserName(loginUser.getUsername());
            SysUser user = loginUser.getUser();
            if (user != null)
            {
                logEntity.setOperNickName(user.getNickName());
                if (user.getDept() != null)
                {
                    logEntity.setOperDeptId(user.getDept().getDeptId());
                    logEntity.setOperDeptName(user.getDept().getDeptName());
                }
            }
        }

        if (throwable != null)
        {
            logEntity.setErrorMsg(StringUtils.substring(ExceptionUtil.getExceptionMessage(throwable), 0, 2000));
        }
        return logEntity;
    }

    private BizAuditHandler resolveHandler(BizAudit annotation)
    {
        if (StringUtils.isBlank(annotation.handler()))
        {
            throw new IllegalArgumentException("@BizAudit.handler 不能为空: " + annotation.bizType());
        }
        return SpringUtils.getBean(annotation.handler());
    }

    private String argsToJson(Object[] args)
    {
        JSONObject root = new JSONObject();
        int index = 0;
        for (Object arg : args)
        {
            if (arg != null && !isFilterObject(arg))
            {
                root.put("arg" + index++, JSON.toJSON(arg));
            }
        }
        return root.toJSONString();
    }

    @SuppressWarnings("rawtypes")
    private boolean isFilterObject(final Object o)
    {
        Class<?> clazz = o.getClass();
        if (clazz.isArray())
        {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        if (Collection.class.isAssignableFrom(clazz))
        {
            Collection collection = (Collection) o;
            for (Object value : collection)
            {
                return value instanceof MultipartFile;
            }
        }
        if (Map.class.isAssignableFrom(clazz))
        {
            Map map = (Map) o;
            for (Object value : map.entrySet())
            {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof BindingResult || o instanceof javax.servlet.http.HttpServletRequest
                || o instanceof javax.servlet.http.HttpServletResponse;
    }
}
