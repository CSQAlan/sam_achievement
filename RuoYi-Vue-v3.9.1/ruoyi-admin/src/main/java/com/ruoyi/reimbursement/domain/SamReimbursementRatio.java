package com.ruoyi.reimbursement.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;


/**
 * 报销比例对象 sam_reimbursement_ratio
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
public class SamReimbursementRatio extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 获奖等级（一等奖、二等奖、三等奖） */
    @Excel(name = "获奖等级", readConverterExp = "一=等奖、二等奖、三等奖")
    private String grade;

    /** 报销类别（存储字典值：0=学会类、1=政府类） */
    @Excel(dictType = "achievement_category") // 对应SQL里的dict_type
    private String category; // 或private Integer category;（如果存数字）

    /** 报销百分比 */
    @Excel(name = "报销百分比")
    private Long ratio;

    /** 归属学院（sys_dept.dept_id；为空表示全校规则） */
    @Excel(name = "归属学院", readConverterExp = "s=ys_dept.dept_id；为空表示全校规则")
    private Long ownerDepId;

    /** 状态（0停用 1启用） */
    @Excel(name = "状态", readConverterExp = "0=停用,1=启用")
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

    public void setGrade(String grade) 
    {
        this.grade = grade;
    }

    public String getGrade() 
    {
        return grade;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setRatio(Long ratio) 
    {
        this.ratio = ratio;
    }

    public Long getRatio() 
    {
        return ratio;
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
            .append("grade", getGrade())
            .append("category", getCategory())
            .append("ratio", getRatio())
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
