package com.ruoyi.erp.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 参赛选手对象 erp_contestant
 * 
 * @author cyy
 * @date 2026-01-26
 */
public class ErpContestant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关联成果ID */
    private Long outcomeId;

    /** 学生学号 */
    @Excel(name = "学生学号")
    private String studentCode;

    /** 排序 */
    private Long sortOrder;

    /** 是否队长（0否 1是） */
    @Excel(name = "是否队长", readConverterExp = "0=否,1=是")
    private Integer isLeader;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setOutcomeId(Long outcomeId) 
    {
        this.outcomeId = outcomeId;
    }

    public Long getOutcomeId() 
    {
        return outcomeId;
    }
    public void setStudentCode(String studentCode) 
    {
        this.studentCode = studentCode;
    }

    public String getStudentCode() 
    {
        return studentCode;
    }
    public void setSortOrder(Long sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder() 
    {
        return sortOrder;
    }
    public void setIsLeader(Integer isLeader) 
    {
        this.isLeader = isLeader;
    }

    public Integer getIsLeader() 
    {
        return isLeader;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("outcomeId", getOutcomeId())
            .append("studentCode", getStudentCode())
            .append("sortOrder", getSortOrder())
            .append("isLeader", getIsLeader())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
