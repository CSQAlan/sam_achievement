package com.ruoyi.framework.bizaudit.model;

import com.ruoyi.system.domain.BizAuditLogDetail;
import java.util.ArrayList;
import java.util.List;

/**
 * diff 结果
 */
public class BizAuditDiffResult
{
    private String diffJson;

    private List<BizAuditLogDetail> details = new ArrayList<BizAuditLogDetail>();

    public String getDiffJson()
    {
        return diffJson;
    }

    public void setDiffJson(String diffJson)
    {
        this.diffJson = diffJson;
    }

    public List<BizAuditLogDetail> getDetails()
    {
        return details;
    }

    public void setDetails(List<BizAuditLogDetail> details)
    {
        this.details = details;
    }
}
