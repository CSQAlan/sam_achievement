package com.ruoyi.session.domain;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事届次对象 session
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
public class Session extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 届次主键 */
    private Long id;

    /** 赛事名称 */
    @Excel(name = "赛事名称")
    private Long competitionId;

    /** 届次 */
    @Excel(name = "届次")
    private String session;

    /** 赛事类别 */
    @Excel(name = "赛事类别")
    private String category;

    /** 盖章单位（多个分号分隔） */
    @Excel(name = "盖章单位", readConverterExp = "多=个分号分隔")
    private String organizations;

    /** 赛事级别 */
    @Excel(name = "赛事级别")
    private String level;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 删除标记（0=存在，2=删除） */
    private String delFlag;

    /** 标签信息 */
    private List<Tag> tagList;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setCompetitionId(Long competitionId) 
    {
        this.competitionId = competitionId;
    }

    public Long getCompetitionId() 
    {
        return competitionId;
    }

    public void setSession(String session) 
    {
        this.session = session;
    }

    public String getSession() 
    {
        return session;
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

    public List<Tag> getTagList()
    {
        return tagList;
    }

    public void setTagList(List<Tag> tagList)
    {
        this.tagList = tagList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("competitionId", getCompetitionId())
            .append("session", getSession())
            .append("category", getCategory())
            .append("organizations", getOrganizations())
            .append("level", getLevel())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .append("tagList", getTagList())
            .toString();
    }
}
