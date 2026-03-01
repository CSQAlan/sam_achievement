package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报销项目对象 sam_reimbursement_items
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
public class SamReimbursementItems extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 报销项目名称 */
    @Excel(name = "报销项目名称")
    private String name;

    /** 报销时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报销时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reimbursementTime;

    /** 总金额 */
    @Excel(name = "总金额")
    private Double totalFee;

    /** 已发放金额 */
    @Excel(name = "已发放金额")
    private Double paidFee;

    /** 报销项目数量 */
    @Excel(name = "报销项目数量")
    private Integer amount;

    /** 归属学院（sys_dept.dept_id顶层学院） */
    @Excel(name = "归属学院", readConverterExp = "s=ys_dept.dept_id顶层学院")
    private Long ownerDepId;

    /** 状态（0进行中 1已完成） */
    @Excel(name = "状态", dictType = "reimbursement_status")
    private String status;

    /** 删除标记（0存在 2删除） */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setReimbursementTime(Date reimbursementTime) 
    {
        this.reimbursementTime = reimbursementTime;
    }

    public Date getReimbursementTime() 
    {
        return reimbursementTime;
    }

    public void setTotalFee(Double totalFee) 
    {
        this.totalFee = totalFee;
    }

    public Double getTotalFee() 
    {
        return totalFee;
    }

    public void setPaidFee(Double paidFee) 
    {
        this.paidFee = paidFee;
    }

    public Double getPaidFee() 
    {
        return paidFee;
    }

    public void setAmount(Integer amount) 
    {
        this.amount = amount;
    }

    public Integer getAmount() 
    {
        return amount;
    }

    public void setOwnerDepId(Long ownerDepId) 
    {
        this.ownerDepId = ownerDepId;
    }

    public Long getOwnerDepId() 
    {
        return ownerDepId;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("reimbursementTime", getReimbursementTime())
            .append("totalFee", getTotalFee())
            .append("paidFee", getPaidFee())
            .append("amount", getAmount())
            .append("ownerDepId", getOwnerDepId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
