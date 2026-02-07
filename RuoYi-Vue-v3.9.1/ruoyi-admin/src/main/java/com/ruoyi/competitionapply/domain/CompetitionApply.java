package com.ruoyi.competitionapply.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事申请对象 competition_apply
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
public class CompetitionApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申请主键 */
    private Long id;

    /** 申请编号 */
    @Excel(name = "申请编号")
    private String applyNo;

    /** 申请人用户ID */
    @Excel(name = "申请人用户ID")
    private Long applicantUserId;

    /** 申请人学院 */
    @Excel(name = "申请人学院")
    private Long applicantDepId;

    /** 赛事名称 */
    @Excel(name = "赛事名称")
    private String name;

    /** 赛事类别 */
    @Excel(name = "赛事类别")
    private String category;

    /** 盖章单位 */
    @Excel(name = "盖章单位")
    private String organizations;

    /** 赛事级别 */
    @Excel(name = "赛事级别")
    private String level;

    /** 适用范围 */
    @Excel(name = "适用范围")
    private String scopeType;

    /** 审核状态 */
    @Excel(name = "审核状态")
    private String status;

    /** 标签 */
    @Excel(name = "标签")
    private String tags;

    /** 赛事说明 */
    @Excel(name = "赛事说明")
    private String memo;

    /** 审核人id */
    private Long auditBy;

    /** 审核时间 */
    private Date auditTime;

    /** 审核意见 */
    private String auditRemark;

    /** 生成赛事ID */
    private Long competitionId;

    /** 删除标记 */
    private String delFlag;

    /** 赛事申请附件信息 */
    private List<CompetitionApplyAttachment> competitionApplyAttachmentList;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setApplyNo(String applyNo) 
    {
        this.applyNo = applyNo;
    }

    public String getApplyNo() 
    {
        return applyNo;
    }

    public void setApplicantUserId(Long applicantUserId) 
    {
        this.applicantUserId = applicantUserId;
    }

    public Long getApplicantUserId() 
    {
        return applicantUserId;
    }

    public void setApplicantDepId(Long applicantDepId) 
    {
        this.applicantDepId = applicantDepId;
    }

    public Long getApplicantDepId() 
    {
        return applicantDepId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setOrganizations(String organizations) 
    {
        this.organizations = organizations;
    }

    public String getOrganizations() 
    {
        return organizations;
    }

    public void setLevel(String level) 
    {
        this.level = level;
    }

    public String getLevel() 
    {
        return level;
    }

    public void setScopeType(String scopeType) 
    {
        this.scopeType = scopeType;
    }

    public String getScopeType() 
    {
        return scopeType;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
    }

    public void setMemo(String memo) 
    {
        this.memo = memo;
    }

    public String getMemo() 
    {
        return memo;
    }

    public void setAuditBy(Long auditBy) 
    {
        this.auditBy = auditBy;
    }

    public Long getAuditBy() 
    {
        return auditBy;
    }

    public void setAuditTime(Date auditTime) 
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime() 
    {
        return auditTime;
    }

    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }

    public void setCompetitionId(Long competitionId) 
    {
        this.competitionId = competitionId;
    }

    public Long getCompetitionId() 
    {
        return competitionId;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public List<CompetitionApplyAttachment> getCompetitionApplyAttachmentList()
    {
        return competitionApplyAttachmentList;
    }

    public void setCompetitionApplyAttachmentList(List<CompetitionApplyAttachment> competitionApplyAttachmentList)
    {
        this.competitionApplyAttachmentList = competitionApplyAttachmentList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("applyNo", getApplyNo())
            .append("applicantUserId", getApplicantUserId())
            .append("applicantDepId", getApplicantDepId())
            .append("name", getName())
            .append("category", getCategory())
            .append("organizations", getOrganizations())
            .append("level", getLevel())
            .append("scopeType", getScopeType())
            .append("status", getStatus())
            .append("tags", getTags())
            .append("memo", getMemo())
            .append("auditBy", getAuditBy())
            .append("auditTime", getAuditTime())
            .append("auditRemark", getAuditRemark())
            .append("competitionId", getCompetitionId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("competitionApplyAttachmentList", getCompetitionApplyAttachmentList())
            .toString();
    }
}
