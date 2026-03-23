package com.ruoyi.reimbursement.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报销项目关联成果对象 sam_reimbursement_achievement_view
 * 
 * @author lwz
 * @date 2026-03-15
 */
public class SamReimbursementAchievementView extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 报销项目ID */
    @Excel(name = "报销项目ID")
    private Long reimbursementId;

    /** 报销项目名称 */
    @Excel(name = "报销项目名称")
    private String reimbursementName;

    /** 报销时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报销时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reimbursementTime;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalFee;

    /** 已发放金额 */
    @Excel(name = "已发放金额")
    private BigDecimal paidFee;

    /** 项目状态 */
    @Excel(name = "项目状态")
    private String reimbursementStatus;

    /** 成果ID */
    @Excel(name = "成果ID")
    private Integer achievementId;

    /** 成果名称 */
    @Excel(name = "成果名称")
    private String achievementName;

    /** 团队名称 */
    @Excel(name = "团队名称")
    private String teamName;

    /** 级别 */
    @Excel(name = "级别")
    private String level;

    /** 获奖等级 */
    @Excel(name = "获奖等级")
    private Long grade;

    /** 证书编号 */
    @Excel(name = "证书编号")
    private String certificateNo;

    /** 报名费 */
    @Excel(name = "报名费")
    private BigDecimal fee;

    /** 报销金额 */
    @Excel(name = "报销金额")
    private BigDecimal reimbursementFee;

    /** 获奖时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "获奖时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date awardTime;

    /** 审核结果 */
    @Excel(name = "审核结果")
    private Integer reviewResult;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setReimbursementId(Long reimbursementId) 
    {
        this.reimbursementId = reimbursementId;
    }

    public Long getReimbursementId() 
    {
        return reimbursementId;
    }

    public void setReimbursementName(String reimbursementName) 
    {
        this.reimbursementName = reimbursementName;
    }

    public String getReimbursementName() 
    {
        return reimbursementName;
    }

    public void setReimbursementTime(Date reimbursementTime) 
    {
        this.reimbursementTime = reimbursementTime;
    }

    public Date getReimbursementTime() 
    {
        return reimbursementTime;
    }

    public void setTotalFee(BigDecimal totalFee) 
    {
        this.totalFee = totalFee;
    }

    public BigDecimal getTotalFee() 
    {
        return totalFee;
    }

    public void setPaidFee(BigDecimal paidFee) 
    {
        this.paidFee = paidFee;
    }

    public BigDecimal getPaidFee() 
    {
        return paidFee;
    }

    public void setReimbursementStatus(String reimbursementStatus) 
    {
        this.reimbursementStatus = reimbursementStatus;
    }

    public String getReimbursementStatus() 
    {
        return reimbursementStatus;
    }

    public void setAchievementId(Integer achievementId) 
    {
        this.achievementId = achievementId;
    }

    public Integer getAchievementId() 
    {
        return achievementId;
    }

    public void setAchievementName(String achievementName) 
    {
        this.achievementName = achievementName;
    }

    public String getAchievementName() 
    {
        return achievementName;
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

    public void setCertificateNo(String certificateNo) 
    {
        this.certificateNo = certificateNo;
    }

    public String getCertificateNo() 
    {
        return certificateNo;
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

    public void setAwardTime(Date awardTime) 
    {
        this.awardTime = awardTime;
    }

    public Date getAwardTime() 
    {
        return awardTime;
    }

    public void setReviewResult(Integer reviewResult) 
    {
        this.reviewResult = reviewResult;
    }

    public Integer getReviewResult() 
    {
        return reviewResult;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("reimbursementId", getReimbursementId())
            .append("reimbursementName", getReimbursementName())
            .append("reimbursementTime", getReimbursementTime())
            .append("totalFee", getTotalFee())
            .append("paidFee", getPaidFee())
            .append("reimbursementStatus", getReimbursementStatus())
            .append("achievementId", getAchievementId())
            .append("achievementName", getAchievementName())
            .append("teamName", getTeamName())
            .append("level", getLevel())
            .append("grade", getGrade())
            .append("certificateNo", getCertificateNo())
            .append("fee", getFee())
            .append("reimbursementFee", getReimbursementFee())
            .append("awardTime", getAwardTime())
            .append("reviewResult", getReviewResult())
            .toString();
    }
}
