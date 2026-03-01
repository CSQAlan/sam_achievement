package com.ruoyi.session.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 标签对象 tag
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
public class Tag extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 届次ID（关联session.id） */
    @Excel(name = "届次ID", readConverterExp = "关=联session.id")
    private Long competitionSessionId;

    /** 标签名称（字典/白名单） */
    @Excel(name = "标签名称", readConverterExp = "字=典/白名单")
    private String tagName;

    /** 适用学院（关联sys_dept.dept_id，为空表示全校共享） */
    @Excel(name = "适用学院", readConverterExp = "关=联sys_dept.dept_id，为空表示全校共享")
    private Long depId;

    /** 删除标记（0=存在，2=删除） */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCompetitionSessionId(Long competitionSessionId) 
    {
        this.competitionSessionId = competitionSessionId;
    }

    public Long getCompetitionSessionId() 
    {
        return competitionSessionId;
    }
    public void setTagName(String tagName) 
    {
        this.tagName = tagName;
    }

    public String getTagName() 
    {
        return tagName;
    }
    public void setDepId(Long depId) 
    {
        this.depId = depId;
    }

    public Long getDepId() 
    {
        return depId;
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
            .append("competitionSessionId", getCompetitionSessionId())
            .append("tagName", getTagName())
            .append("depId", getDepId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
