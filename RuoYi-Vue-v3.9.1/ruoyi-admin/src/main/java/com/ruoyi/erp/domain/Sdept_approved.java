package com.ruoyi.erp.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 校级已审核的成果对象 erp_outcome
 * 
 * @author cyy
 * @date 2026-01-26
 */
public class Sdept_approved extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "主键")
    private Long id;

    /** 所属年份 */
    @Excel(name = "所属年份")
    private Long year;

    /** 成果类别 */
    @Excel(name = "成果类别")
    private String category;

    /** 作品名称 */
    private String workName;

    /** 级别类型 */
    @Excel(name = "级别类型")
    private String levelType;

    /** 奖项等级 */
    @Excel(name = "奖项等级")
    private String awardLevel;

    /** 赛道 */
    @Excel(name = "赛道")
    private String track;

    /** 证书编号 */
    @Excel(name = "证书编号")
    private String certNo;

    /** 组别 */
    @Excel(name = "组别", readConverterExp = "1=本科生组,2=研究生组,3=高职高专组")
    private String groupType;

    /** 赛事 */
    @Excel(name = "赛事")
    private Long contestId;

    /** 所属学院 */
    @Excel(name = "所属学院")
    private Long deptId;

    /** 获奖时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "获奖时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date awardTime;

    /** 提交时间 */
    private Date submitTime;

    /** 报名费 */
    @Excel(name = "报名费")
    private BigDecimal entryFee;

    /** 审核状态（1:待院级审核, 2:待校级审核, 3:院级审核通过, 4:院级驳回, 5:校级驳回, 6:校级审核通过） */
    @Excel(name = "审核状态", readConverterExp = "1=待院级审核,2=待校级审核,3=院级审核通过,4=院级驳回,5=校级驳回,6=校级审核通过")
    private String auditStatus;

    /** 系部审核人 */
    @Excel(name = "系部审核人")
    private String deptAuditUser;

    /** 系部审核时间 */
    private Date deptAuditTime;

    /** 系部审核意见 */
    private String deptAuditReason;

    /** 校级审核人 */
    @Excel(name = "校级审核人")
    private String schoolAuditUser;

    /** 校级审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "校级审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date schoolAuditTime;

    /** 校级审核意见 */
    @Excel(name = "校级审核意见")
    private String schoolAuditReason;

    /** 是否申请报销 */
    @Excel(name = "是否申请报销")
    private Integer isReimburse;

    /** 报销金额 */
    private BigDecimal reimburseFee;

    /** 报销比例 */
    private BigDecimal reimburseRatio;

    /** 报销项目号 */
    private String reimburseProjectId;

    /** 报销发放时间 */
    private Date reimbursePayTime;

    /** 报销审核状态 */
    private String reimburseAuditStatus;

    /** 报销审核时间 */
    private Date reimburseAuditTime;

    /** 报销审核人 */
    private Long reimburseAuditUserId;

    /** 报销审核意见 */
    private String reimburseAuditReason;

    /** 删除标志 */
    private String delFlag;

    /** 参赛选手信息 */
    private List<ErpContestant> erpContestantList;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setYear(Long year) 
    {
        this.year = year;
    }

    public Long getYear() 
    {
        return year;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setWorkName(String workName) 
    {
        this.workName = workName;
    }

    public String getWorkName() 
    {
        return workName;
    }

    public void setLevelType(String levelType) 
    {
        this.levelType = levelType;
    }

    public String getLevelType() 
    {
        return levelType;
    }

    public void setAwardLevel(String awardLevel) 
    {
        this.awardLevel = awardLevel;
    }

    public String getAwardLevel() 
    {
        return awardLevel;
    }

    public void setTrack(String track) 
    {
        this.track = track;
    }

    public String getTrack() 
    {
        return track;
    }

    public void setCertNo(String certNo) 
    {
        this.certNo = certNo;
    }

    public String getCertNo() 
    {
        return certNo;
    }

    public void setGroupType(String groupType) 
    {
        this.groupType = groupType;
    }

    public String getGroupType() 
    {
        return groupType;
    }

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    public void setAwardTime(Date awardTime) 
    {
        this.awardTime = awardTime;
    }

    public Date getAwardTime() 
    {
        return awardTime;
    }

    public void setSubmitTime(Date submitTime) 
    {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() 
    {
        return submitTime;
    }

    public void setEntryFee(BigDecimal entryFee) 
    {
        this.entryFee = entryFee;
    }

    public BigDecimal getEntryFee() 
    {
        return entryFee;
    }

    public void setAuditStatus(String auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() 
    {
        return auditStatus;
    }

    public void setDeptAuditUser(String deptAuditUser) 
    {
        this.deptAuditUser = deptAuditUser;
    }

    public String getDeptAuditUser() 
    {
        return deptAuditUser;
    }

    public void setDeptAuditTime(Date deptAuditTime) 
    {
        this.deptAuditTime = deptAuditTime;
    }

    public Date getDeptAuditTime() 
    {
        return deptAuditTime;
    }

    public void setDeptAuditReason(String deptAuditReason) 
    {
        this.deptAuditReason = deptAuditReason;
    }

    public String getDeptAuditReason() 
    {
        return deptAuditReason;
    }

    public void setSchoolAuditUser(String schoolAuditUser) 
    {
        this.schoolAuditUser = schoolAuditUser;
    }

    public String getSchoolAuditUser() 
    {
        return schoolAuditUser;
    }

    public void setSchoolAuditTime(Date schoolAuditTime) 
    {
        this.schoolAuditTime = schoolAuditTime;
    }

    public Date getSchoolAuditTime() 
    {
        return schoolAuditTime;
    }

    public void setSchoolAuditReason(String schoolAuditReason) 
    {
        this.schoolAuditReason = schoolAuditReason;
    }

    public String getSchoolAuditReason() 
    {
        return schoolAuditReason;
    }

    public void setIsReimburse(Integer isReimburse) 
    {
        this.isReimburse = isReimburse;
    }

    public Integer getIsReimburse() 
    {
        return isReimburse;
    }

    public void setReimburseFee(BigDecimal reimburseFee) 
    {
        this.reimburseFee = reimburseFee;
    }

    public BigDecimal getReimburseFee() 
    {
        return reimburseFee;
    }

    public void setReimburseRatio(BigDecimal reimburseRatio) 
    {
        this.reimburseRatio = reimburseRatio;
    }

    public BigDecimal getReimburseRatio() 
    {
        return reimburseRatio;
    }

    public void setReimburseProjectId(String reimburseProjectId) 
    {
        this.reimburseProjectId = reimburseProjectId;
    }

    public String getReimburseProjectId() 
    {
        return reimburseProjectId;
    }

    public void setReimbursePayTime(Date reimbursePayTime) 
    {
        this.reimbursePayTime = reimbursePayTime;
    }

    public Date getReimbursePayTime() 
    {
        return reimbursePayTime;
    }

    public void setReimburseAuditStatus(String reimburseAuditStatus) 
    {
        this.reimburseAuditStatus = reimburseAuditStatus;
    }

    public String getReimburseAuditStatus() 
    {
        return reimburseAuditStatus;
    }

    public void setReimburseAuditTime(Date reimburseAuditTime) 
    {
        this.reimburseAuditTime = reimburseAuditTime;
    }

    public Date getReimburseAuditTime() 
    {
        return reimburseAuditTime;
    }

    public void setReimburseAuditUserId(Long reimburseAuditUserId) 
    {
        this.reimburseAuditUserId = reimburseAuditUserId;
    }

    public Long getReimburseAuditUserId() 
    {
        return reimburseAuditUserId;
    }

    public void setReimburseAuditReason(String reimburseAuditReason) 
    {
        this.reimburseAuditReason = reimburseAuditReason;
    }

    public String getReimburseAuditReason() 
    {
        return reimburseAuditReason;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public List<ErpContestant> getErpContestantList()
    {
        return erpContestantList;
    }

    public void setErpContestantList(List<ErpContestant> erpContestantList)
    {
        this.erpContestantList = erpContestantList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("year", getYear())
            .append("category", getCategory())
            .append("workName", getWorkName())
            .append("levelType", getLevelType())
            .append("awardLevel", getAwardLevel())
            .append("track", getTrack())
            .append("certNo", getCertNo())
            .append("groupType", getGroupType())
            .append("contestId", getContestId())
            .append("deptId", getDeptId())
            .append("awardTime", getAwardTime())
            .append("submitTime", getSubmitTime())
            .append("entryFee", getEntryFee())
            .append("auditStatus", getAuditStatus())
            .append("deptAuditUser", getDeptAuditUser())
            .append("deptAuditTime", getDeptAuditTime())
            .append("deptAuditReason", getDeptAuditReason())
            .append("schoolAuditUser", getSchoolAuditUser())
            .append("schoolAuditTime", getSchoolAuditTime())
            .append("schoolAuditReason", getSchoolAuditReason())
            .append("isReimburse", getIsReimburse())
            .append("reimburseFee", getReimburseFee())
            .append("reimburseRatio", getReimburseRatio())
            .append("reimburseProjectId", getReimburseProjectId())
            .append("reimbursePayTime", getReimbursePayTime())
            .append("reimburseAuditStatus", getReimburseAuditStatus())
            .append("reimburseAuditTime", getReimburseAuditTime())
            .append("reimburseAuditUserId", getReimburseAuditUserId())
            .append("reimburseAuditReason", getReimburseAuditReason())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("erpContestantList", getErpContestantList())
            .toString();
    }
}
