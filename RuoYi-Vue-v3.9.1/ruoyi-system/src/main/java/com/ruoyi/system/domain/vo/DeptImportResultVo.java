package com.ruoyi.system.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门导入结果
 */
public class DeptImportResultVo
{
    private Boolean preview;

    private Boolean rolledBack = Boolean.FALSE;

    private Integer totalCount = 0;

    private Integer insertCount = 0;

    private Integer updateCount = 0;

    private Integer skipCount = 0;

    private Integer failureCount = 0;

    private Integer successCount = 0;

    private String message;

    private String importMode;

    private String commitMode;

    private List<DeptImportRowVo> rows = new ArrayList<DeptImportRowVo>();

    public Boolean getPreview()
    {
        return preview;
    }

    public void setPreview(Boolean preview)
    {
        this.preview = preview;
    }

    public Boolean getRolledBack()
    {
        return rolledBack;
    }

    public void setRolledBack(Boolean rolledBack)
    {
        this.rolledBack = rolledBack;
    }

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getInsertCount()
    {
        return insertCount;
    }

    public void setInsertCount(Integer insertCount)
    {
        this.insertCount = insertCount;
    }

    public Integer getUpdateCount()
    {
        return updateCount;
    }

    public void setUpdateCount(Integer updateCount)
    {
        this.updateCount = updateCount;
    }

    public Integer getSkipCount()
    {
        return skipCount;
    }

    public void setSkipCount(Integer skipCount)
    {
        this.skipCount = skipCount;
    }

    public Integer getFailureCount()
    {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount)
    {
        this.failureCount = failureCount;
    }

    public Integer getSuccessCount()
    {
        return successCount;
    }

    public void setSuccessCount(Integer successCount)
    {
        this.successCount = successCount;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getImportMode()
    {
        return importMode;
    }

    public void setImportMode(String importMode)
    {
        this.importMode = importMode;
    }

    public String getCommitMode()
    {
        return commitMode;
    }

    public void setCommitMode(String commitMode)
    {
        this.commitMode = commitMode;
    }

    public List<DeptImportRowVo> getRows()
    {
        return rows;
    }

    public void setRows(List<DeptImportRowVo> rows)
    {
        this.rows = rows;
    }
}
