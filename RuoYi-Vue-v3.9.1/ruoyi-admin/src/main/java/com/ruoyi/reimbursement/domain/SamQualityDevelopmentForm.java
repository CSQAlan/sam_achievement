package com.ruoyi.reimbursement.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 素质提升对象 sam_quality_development_form
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
public class SamQualityDevelopmentForm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 年份 */
    @Excel(name = "年份")
    private Long year;

    /** 统计成果数 */
    @Excel(name = "统计成果数")
    private Long amount;

    /** 归属学院（sys_dept.dept_id顶层学院） */
    @Excel(name = "归属学院", readConverterExp = "s=ys_dept.dept_id顶层学院")
    private Long ownerDepId;

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

    public void setAmount(Long amount) 
    {
        this.amount = amount;
    }

    public Long getAmount() 
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("year", getYear())
            .append("amount", getAmount())
            .append("ownerDepId", getOwnerDepId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
