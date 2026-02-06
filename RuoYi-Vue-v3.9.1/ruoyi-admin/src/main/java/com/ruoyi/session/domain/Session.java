package com.ruoyi.session.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事届次对象 sam_session
 *
 * @author ruoyi
 * @date 2026-02-01
 */
public class Session extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 届次主键 */
    private Long id;

    /** 赛事主表ID */
    private Long competitionId;

    /** 赛事主表ID */
    @Excel(name = "赛事名称", prompt = "请填写赛事主表中存在的赛事名称，无则自动新增")
    private String competitionName;

    /** 届次 */
    @Excel(name = "届次", prompt = "例：第一届/2025届")
    private String session;

    /** 赛事类别（0=政府类，1=学会类） */
    @Excel(name = "赛事类别",prompt = "填写：政府类/学会类" )
    private String category;

    /** 盖章单位（多个分号分隔） */
    @Excel(name = "盖章单位", prompt = "多个单位用分号分隔，例：XX政府;XX学会")
    private String organizations;

    /** 赛事级别（0=Ⅰ级（国家级），1=Ⅱ级（市级）） */
    @Excel(name = "赛事级别", prompt = "填写：Ⅰ级（国家级）/Ⅱ级（市级）")
    private String level;

    /** 状态（导入默认0=启用，仅导出显示） */
    private String status;

    /**标签（多个逗号分隔，0=素质提升奖，1=高等教育学会白名单，2=学院特别认定）*/
    @Excel(name="标签",separator = ",", prompt = "多个标签用逗号分隔，例：素质提升奖,高等教育学会白名单,学院特别认定")
    private String tags;

    /** 删除标记（0=存在，2=删除） */
    private String delFlag;

    // ========== 核心改造：删除原有的3个硬编码setter方法，恢复默认setter ==========
    // 【删除】原setCategory、setLevel、setTags方法，全部删掉！
    // 其他原有get/set方法完全保留，无需改动

    // ========== 原有get/set方法（完整保留，仅删除上述3个setter） ==========
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
    public Long getCompetitionId() {
        return competitionId;
    }
    public void setSession(String session) {
        this.session = session;
    }
    public String getSession() {
        return session;
    }
    // 【保留默认getter】
    public String getCategory() {
        return category;
    }
    // 【新增默认setter（无业务逻辑），如果用lombok则无需手动写】
    public void setCategory(String category) {
        this.category = category;
    }
    public void setOrganizations(String organizations) {
        this.organizations = organizations;
    }
    public String getOrganizations() {
        return organizations;
    }
    // 【保留默认getter】
    public String getLevel() {
        return level;
    }
    // 【新增默认setter（无业务逻辑）】
    public void setLevel(String level) {
        this.level = level;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    // 【保留默认getter】
    public String getTags() {
        return tags;
    }
    // 【新增默认setter（无业务逻辑）】
    public void setTags(String tags) {
        this.tags = tags;
    }
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
    public String getDelFlag() {
        return delFlag;
    }
    public String getCompetitionName() {
        return competitionName;
    }
    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
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
                .append("tags", getTags())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("delFlag", getDelFlag())
                .toString();
    }
}