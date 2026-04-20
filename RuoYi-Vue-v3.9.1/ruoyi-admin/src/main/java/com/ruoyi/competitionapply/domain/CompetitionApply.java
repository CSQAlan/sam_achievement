package com.ruoyi.competitionapply.domain;

import java.beans.Transient;
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
//    @Excel(name = "申请编号")
//    private String applyNo;

    /** 申请人用户ID */
//    @Excel(name = "申请人用户ID")
    private String applicantUserId;

    /** 申请人学院 */
//    @Excel(name = "申请人学院")
    private String applicantDepId;
    /** 申请人用户名（展示用，不映射数据库） */
    @Excel(name = "申请人学号")
    private String userName;

    /** 学院名称（展示用，不映射数据库） */
    @Excel(name = "申请人学院")
    private String deptName;

    /** 赛事名称 */
    @Excel(name = "赛事名称")
    private String name;

    /** 届次（例如2025、十二届） */
    @Excel(name = "届次")
    private String session;

    /** 年份（默认当前年） */
    @Excel(name = "年份")
    private Integer year;

    /** 赛事类别 */
    @Excel(name = "赛事类别",dictType = "sys_competition_category")
    private String category;

    /** 盖章单位 */
    @Excel(name = "盖章单位")
    private String organizations;

    /** 赛事级别 */
    @Excel(name = "赛事级别",dictType = "sys_competition_level")
    private String level;

    /** 适用范围 */
    @Excel(name = "适用范围",dictType = "sys_competition_scope_type")
    private String scopeType;

    /** 审核状态 */
    @Excel(name = "审核状态",dictType = "sys_shenhe_status")
    private String status;

    /**
     * 查询用：审核类型
     * 0=未审核（只查待审），1=已审核（查通过+驳回），为空=全部
     */
    private String reviewedFlag;

    /** 标签 */
    @Excel(name = "标签",dictType = "sys_competition_tag")
    private String tags;

    /** 赛事说明 */
//    @Excel(name = "赛事说明")
    private String memo;

    /** 审核人id */
    private Long auditBy;

    /** 审核时间 */
    private Date auditTime;

    /** 审核意见 */
    private String auditRemark;

    /** 生成赛事ID */
    private Long competitionId;

    /** 生成届次ID */
    private Long sessionId;

    /** 删除标记 */
    private String delFlag;

    /** 附件地址（逗号分隔）- 新增字段，匹配前端upload-file组件 */
    /** 附件地址（逗号分隔）- 前端传输临时字段，不映射数据库 */
    private String attachmentUrls;



    // 生成 getter/setter
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

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

//    public void setApplyNo(String applyNo)
//    {
//        this.applyNo = applyNo;
//    }
//
//    public String getApplyNo()
//    {
//        return applyNo;
//    }

    public void setApplicantUserId(String applicantUserId)
    {
        this.applicantUserId = applicantUserId;
    }

    public String getApplicantUserId()
    {
        return applicantUserId;
    }

    public void setApplicantDepId(String applicantDepId)
    {
        this.applicantDepId = applicantDepId;
    }

    public String getApplicantDepId()
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

    public String getSession()
    {
        return session;
    }

    public void setSession(String session)
    {
        this.session = session;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
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

    public String getReviewedFlag()
    {
        return reviewedFlag;
    }

    public void setReviewedFlag(String reviewedFlag)
    {
        this.reviewedFlag = reviewedFlag;
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

    public Long getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(Long sessionId)
    {
        this.sessionId = sessionId;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    // 新增 attachmentUrls 的 getter/setter 方法
    public String getAttachmentUrls() {
        return attachmentUrls;
    }

    public void setAttachmentUrls(String attachmentUrls) {
        this.attachmentUrls = attachmentUrls;
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
//                .append("applyNo", getApplyNo())
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
                .append("attachmentUrls", getAttachmentUrls()) // 新增到toString方法
                .append("competitionApplyAttachmentList", getCompetitionApplyAttachmentList())
                .toString();
    }
}
