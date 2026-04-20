package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 高级业务审计日志主表
 */
public class BizAuditLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String traceId;

    private String requestId;

    @Excel(name = "业务类型")
    private String bizType;

    @Excel(name = "业务名称")
    private String bizName;

    @Excel(name = "业务主键")
    private String bizId;

    private String bizKey;

    @Excel(name = "操作类型")
    private String opType;

    @Excel(name = "结果状态")
    private String resultStatus;

    @Excel(name = "是否成功", readConverterExp = "1=是,0=否")
    private Integer success;

    private String errorCode;

    private String errorMsg;

    private Integer batchFlag;

    private Integer batchCount;

    private String className;

    private String methodName;

    private String method;

    private String requestUri;

    private String requestMethod;

    private String requestParams;

    @Excel(name = "耗时(ms)")
    private Long durationMs;

    private Long operUserId;

    @Excel(name = "操作账号")
    private String operUserName;

    @Excel(name = "操作人")
    private String operNickName;

    private Long operDeptId;

    @Excel(name = "部门")
    private String operDeptName;

    @Excel(name = "IP")
    private String operIp;

    private String operLocation;

    private String operBrowser;

    private String operOs;

    private String beforeJson;

    private String afterJson;

    private String diffJson;

    private String extraJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTraceId()
    {
        return traceId;
    }

    public void setTraceId(String traceId)
    {
        this.traceId = traceId;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public String getBizType()
    {
        return bizType;
    }

    public void setBizType(String bizType)
    {
        this.bizType = bizType;
    }

    public String getBizName()
    {
        return bizName;
    }

    public void setBizName(String bizName)
    {
        this.bizName = bizName;
    }

    public String getBizId()
    {
        return bizId;
    }

    public void setBizId(String bizId)
    {
        this.bizId = bizId;
    }

    public String getBizKey()
    {
        return bizKey;
    }

    public void setBizKey(String bizKey)
    {
        this.bizKey = bizKey;
    }

    public String getOpType()
    {
        return opType;
    }

    public void setOpType(String opType)
    {
        this.opType = opType;
    }

    public String getResultStatus()
    {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus)
    {
        this.resultStatus = resultStatus;
    }

    public Integer getSuccess()
    {
        return success;
    }

    public void setSuccess(Integer success)
    {
        this.success = success;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public Integer getBatchFlag()
    {
        return batchFlag;
    }

    public void setBatchFlag(Integer batchFlag)
    {
        this.batchFlag = batchFlag;
    }

    public Integer getBatchCount()
    {
        return batchCount;
    }

    public void setBatchCount(Integer batchCount)
    {
        this.batchCount = batchCount;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getRequestUri()
    {
        return requestUri;
    }

    public void setRequestUri(String requestUri)
    {
        this.requestUri = requestUri;
    }

    public String getRequestMethod()
    {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod)
    {
        this.requestMethod = requestMethod;
    }

    public String getRequestParams()
    {
        return requestParams;
    }

    public void setRequestParams(String requestParams)
    {
        this.requestParams = requestParams;
    }

    public Long getDurationMs()
    {
        return durationMs;
    }

    public void setDurationMs(Long durationMs)
    {
        this.durationMs = durationMs;
    }

    public Long getOperUserId()
    {
        return operUserId;
    }

    public void setOperUserId(Long operUserId)
    {
        this.operUserId = operUserId;
    }

    public String getOperUserName()
    {
        return operUserName;
    }

    public void setOperUserName(String operUserName)
    {
        this.operUserName = operUserName;
    }

    public String getOperNickName()
    {
        return operNickName;
    }

    public void setOperNickName(String operNickName)
    {
        this.operNickName = operNickName;
    }

    public Long getOperDeptId()
    {
        return operDeptId;
    }

    public void setOperDeptId(Long operDeptId)
    {
        this.operDeptId = operDeptId;
    }

    public String getOperDeptName()
    {
        return operDeptName;
    }

    public void setOperDeptName(String operDeptName)
    {
        this.operDeptName = operDeptName;
    }

    public String getOperIp()
    {
        return operIp;
    }

    public void setOperIp(String operIp)
    {
        this.operIp = operIp;
    }

    public String getOperLocation()
    {
        return operLocation;
    }

    public void setOperLocation(String operLocation)
    {
        this.operLocation = operLocation;
    }

    public String getOperBrowser()
    {
        return operBrowser;
    }

    public void setOperBrowser(String operBrowser)
    {
        this.operBrowser = operBrowser;
    }

    public String getOperOs()
    {
        return operOs;
    }

    public void setOperOs(String operOs)
    {
        this.operOs = operOs;
    }

    public String getBeforeJson()
    {
        return beforeJson;
    }

    public void setBeforeJson(String beforeJson)
    {
        this.beforeJson = beforeJson;
    }

    public String getAfterJson()
    {
        return afterJson;
    }

    public void setAfterJson(String afterJson)
    {
        this.afterJson = afterJson;
    }

    public String getDiffJson()
    {
        return diffJson;
    }

    public void setDiffJson(String diffJson)
    {
        this.diffJson = diffJson;
    }

    public String getExtraJson()
    {
        return extraJson;
    }

    public void setExtraJson(String extraJson)
    {
        this.extraJson = extraJson;
    }

    @Override
    public Date getCreateTime()
    {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
