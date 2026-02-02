package com.ruoyi.competition.domain;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 总赛事对象 competition
 *
 * @author ruoyi
 * @date 2026-02-01
 */
public class Competition extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 赛事主键 */
    private Long id;

    /** 赛事名称 */
    @Excel(name = "赛事名称")
    private String name;

    /** 赛事类别 */
    @Excel(name = "赛事类别")
    private String category;

    /** 盖章单位 */
    @Excel(name = "盖章单位")
    private String organizations;

    /** 赛事标签 */
    @Excel(name = "赛事标签")
    private String tags;

    /** 适用范围 */
    @Excel(name = "适用范围")
    private String scopeType;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 赛事说明 */
    @Excel(name = "赛事说明")
    private String memo;

    /** 删除标记 */
    private String delFlag;

    /** 赛事-部门关系信息 */
    private List<CompetitionDeptRel> competitionDeptRelList;

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

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    public String getTags()
    {
        return tags;
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

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public List<CompetitionDeptRel> getCompetitionDeptRelList()
    {
        return competitionDeptRelList;
    }

    public void setCompetitionDeptRelList(List<CompetitionDeptRel> competitionDeptRelList)
    {
        this.competitionDeptRelList = competitionDeptRelList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("category", getCategory())
            .append("organizations", getOrganizations())
            .append("tags", getTags())
            .append("scopeType", getScopeType())
            .append("status", getStatus())
            .append("memo", getMemo())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("competitionDeptRelList", getCompetitionDeptRelList())
            .toString();
    }
}
