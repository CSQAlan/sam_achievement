package com.ruoyi.system.domain.vo;

import com.ruoyi.common.annotation.Excel;

/**
 * 部门导入明细行
 */
public class DeptImportRowVo
{
    @Excel(name = "行号")
    private Integer rowNum;

    @Excel(name = "部门名称")
    private String deptName;

    @Excel(name = "上级部门名称")
    private String parentName;

    @Excel(name = "部门全路径")
    private String deptPath;

    @Excel(name = "解析后上级部门")
    private String resolvedParentName;

    @Excel(name = "处理动作")
    private String action;

    @Excel(name = "校验结果")
    private String successText;

    @Excel(name = "结果说明", width = 40)
    private String message;

    private Boolean success;

    public Integer getRowNum()
    {
        return rowNum;
    }

    public void setRowNum(Integer rowNum)
    {
        this.rowNum = rowNum;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public String getDeptPath()
    {
        return deptPath;
    }

    public void setDeptPath(String deptPath)
    {
        this.deptPath = deptPath;
    }

    public String getResolvedParentName()
    {
        return resolvedParentName;
    }

    public void setResolvedParentName(String resolvedParentName)
    {
        this.resolvedParentName = resolvedParentName;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public Boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(Boolean success)
    {
        this.success = success;
        this.successText = Boolean.TRUE.equals(success) ? "通过" : "失败";
    }

    public String getSuccessText()
    {
        return successText;
    }

    public void setSuccessText(String successText)
    {
        this.successText = successText;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
