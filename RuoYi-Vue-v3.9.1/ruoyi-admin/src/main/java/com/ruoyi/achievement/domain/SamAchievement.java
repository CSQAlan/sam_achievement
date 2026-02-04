package com.ruoyi.achievement.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 成果录入对象 sam_achievement
 * 
 * @author 王璨
 * @date 2026-02-03
 */
public class SamAchievement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成果ID */
    @Excel(name = "成果ID")
    private String achievementId;

    /** 届次  */
    @Excel(name = "届次 ")
    private String sessionId;

    /** 类别 */
    @Excel(name = "类别")
    private String category;

    /** 作品名称 */
    @Excel(name = "作品名称")
    private String name;

    /** 团队名称 */
    @Excel(name = "团队名称")
    private String teamName;

    /** 获奖级别 */
    @Excel(name = "获奖级别")
    private String level;

    /** 获奖等级 */
    @Excel(name = "获奖等级")
    private Long grade;

    /** 赛道 */
    @Excel(name = "赛道")
    private String track;

    /** 证书编号 */
    @Excel(name = "证书编号")
    private String certificateNo;

    /** 组别 */
    @Excel(name = "组别")
    private Long groupId;

    /** 获奖时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "获奖时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date awardTime;

    /** 年份 */
    private Long year;

    /** 部门 */
    private String ownerDepId;

    /** 是否报销 */
    private Integer isReimburse;

    /** 是否补录 */
    private Integer isSupplement;

    /** 报名费金额 */
    private BigDecimal fee;

    /** 实际报销金额 */
    private BigDecimal reimbursementFee;

    /** 报销百分比（0-100） */
    private String reimbursementRatio;

    /** 报销项目 id */
    private String reimbursementItemId;

    /** 报销发放时间 */
    private Date reimbursementDate;

    /** 报销项目序号 */
    private Long itemIndex;

    /** 素质提升序号 */
    private Long qualityIndex;

    /** 提交时间 */
    private Date submittedAt;

    /** 院级审核时间 */
    private Date reviewedAt;

    /** 校级审核时间 */
    private Date schoolReviewedAt;

    /** 院级审核结果（待院级审核0，院级驳回1，院级审核通过2） */
    private Long reviewResult;

    /** 校级审核结果（待校级审核2，校级审核通过1，校级驳回0 ） */
    private Long schooiReviewResult;

    /** 院级审核不通过原因 */
    private String reviewReason;

    /** 校级审核不通过原因 */
    private String schoolReviewReason;

    /** 院级审核人 */
    private String auditBy;

    /** 校级审核人 */
    private String schoolAuditBy;

    /** 删除标志（0正常 2删除） */
    private Integer delFlag;

    /** 参赛选手信息 */
    private List<SamAchievementParticipant> samAchievementParticipantList;

    public void setAchievementId(String achievementId) 
    {
        this.achievementId = achievementId;
    }

    public String getAchievementId() 
    {
        return achievementId;
    }

    public void setSessionId(String sessionId) 
    {
        this.sessionId = sessionId;
    }

    public String getSessionId() 
    {
        return sessionId;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setTeamName(String teamName) 
    {
        this.teamName = teamName;
    }

    public String getTeamName() 
    {
        return teamName;
    }

    public void setLevel(String level) 
    {
        this.level = level;
    }

    public String getLevel() 
    {
        return level;
    }

    public void setGrade(Long grade) 
    {
        this.grade = grade;
    }

    public Long getGrade() 
    {
        return grade;
    }

    public void setTrack(String track) 
    {
        this.track = track;
    }

    public String getTrack() 
    {
        return track;
    }

    public void setCertificateNo(String certificateNo) 
    {
        this.certificateNo = certificateNo;
    }

    public String getCertificateNo() 
    {
        return certificateNo;
    }

    public void setGroupId(Long groupId) 
    {
        this.groupId = groupId;
    }

    public Long getGroupId() 
    {
        return groupId;
    }

    public void setAwardTime(Date awardTime) 
    {
        this.awardTime = awardTime;
    }

    public Date getAwardTime() 
    {
        return awardTime;
    }

    public void setYear(Long year) 
    {
        this.year = year;
    }

    public Long getYear() 
    {
        return year;
    }

    public void setOwnerDepId(String ownerDepId) 
    {
        this.ownerDepId = ownerDepId;
    }

    public String getOwnerDepId() 
    {
        return ownerDepId;
    }

    public void setIsReimburse(Integer isReimburse) 
    {
        this.isReimburse = isReimburse;
    }

    public Integer getIsReimburse() 
    {
        return isReimburse;
    }

    public void setIsSupplement(Integer isSupplement) 
    {
        this.isSupplement = isSupplement;
    }

    public Integer getIsSupplement() 
    {
        return isSupplement;
    }

    public void setFee(BigDecimal fee) 
    {
        this.fee = fee;
    }

    public BigDecimal getFee() 
    {
        return fee;
    }

    public void setReimbursementFee(BigDecimal reimbursementFee) 
    {
        this.reimbursementFee = reimbursementFee;
    }

    public BigDecimal getReimbursementFee() 
    {
        return reimbursementFee;
    }

    public void setReimbursementRatio(String reimbursementRatio) 
    {
        this.reimbursementRatio = reimbursementRatio;
    }

    public String getReimbursementRatio() 
    {
        return reimbursementRatio;
    }

    public void setReimbursementItemId(String reimbursementItemId) 
    {
        this.reimbursementItemId = reimbursementItemId;
    }

    public String getReimbursementItemId() 
    {
        return reimbursementItemId;
    }

    public void setReimbursementDate(Date reimbursementDate) 
    {
        this.reimbursementDate = reimbursementDate;
    }

    public Date getReimbursementDate() 
    {
        return reimbursementDate;
    }

    public void setItemIndex(Long itemIndex) 
    {
        this.itemIndex = itemIndex;
    }

    public Long getItemIndex() 
    {
        return itemIndex;
    }

    public void setQualityIndex(Long qualityIndex) 
    {
        this.qualityIndex = qualityIndex;
    }

    public Long getQualityIndex() 
    {
        return qualityIndex;
    }

    public void setSubmittedAt(Date submittedAt) 
    {
        this.submittedAt = submittedAt;
    }

    public Date getSubmittedAt() 
    {
        return submittedAt;
    }

    public void setReviewedAt(Date reviewedAt) 
    {
        this.reviewedAt = reviewedAt;
    }

    public Date getReviewedAt() 
    {
        return reviewedAt;
    }

    public void setSchoolReviewedAt(Date schoolReviewedAt) 
    {
        this.schoolReviewedAt = schoolReviewedAt;
    }

    public Date getSchoolReviewedAt() 
    {
        return schoolReviewedAt;
    }

    public void setReviewResult(Long reviewResult) 
    {
        this.reviewResult = reviewResult;
    }

    public Long getReviewResult() 
    {
        return reviewResult;
    }

    public void setSchooiReviewResult(Long schooiReviewResult) 
    {
        this.schooiReviewResult = schooiReviewResult;
    }

    public Long getSchooiReviewResult() 
    {
        return schooiReviewResult;
    }

    public void setReviewReason(String reviewReason) 
    {
        this.reviewReason = reviewReason;
    }

    public String getReviewReason() 
    {
        return reviewReason;
    }

    public void setSchoolReviewReason(String schoolReviewReason) 
    {
        this.schoolReviewReason = schoolReviewReason;
    }

    public String getSchoolReviewReason() 
    {
        return schoolReviewReason;
    }

    public void setAuditBy(String auditBy) 
    {
        this.auditBy = auditBy;
    }

    public String getAuditBy() 
    {
        return auditBy;
    }

    public void setSchoolAuditBy(String schoolAuditBy) 
    {
        this.schoolAuditBy = schoolAuditBy;
    }

    public String getSchoolAuditBy() 
    {
        return schoolAuditBy;
    }

    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    public List<SamAchievementParticipant> getSamAchievementParticipantList()
    {
        return samAchievementParticipantList;
    }

    public void setSamAchievementParticipantList(List<SamAchievementParticipant> samAchievementParticipantList)
    {
        this.samAchievementParticipantList = samAchievementParticipantList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("achievementId", getAchievementId())
            .append("sessionId", getSessionId())
            .append("category", getCategory())
            .append("name", getName())
            .append("teamName", getTeamName())
            .append("level", getLevel())
            .append("grade", getGrade())
            .append("track", getTrack())
            .append("certificateNo", getCertificateNo())
            .append("groupId", getGroupId())
            .append("awardTime", getAwardTime())
            .append("year", getYear())
            .append("ownerDepId", getOwnerDepId())
            .append("isReimburse", getIsReimburse())
            .append("isSupplement", getIsSupplement())
            .append("fee", getFee())
            .append("reimbursementFee", getReimbursementFee())
            .append("reimbursementRatio", getReimbursementRatio())
            .append("reimbursementItemId", getReimbursementItemId())
            .append("reimbursementDate", getReimbursementDate())
            .append("itemIndex", getItemIndex())
            .append("qualityIndex", getQualityIndex())
            .append("submittedAt", getSubmittedAt())
            .append("reviewedAt", getReviewedAt())
            .append("schoolReviewedAt", getSchoolReviewedAt())
            .append("reviewResult", getReviewResult())
            .append("schooiReviewResult", getSchooiReviewResult())
            .append("reviewReason", getReviewReason())
            .append("schoolReviewReason", getSchoolReviewReason())
            .append("auditBy", getAuditBy())
            .append("schoolAuditBy", getSchoolAuditBy())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .append("samAchievementParticipantList", getSamAchievementParticipantList())
            .toString();
    }
}
