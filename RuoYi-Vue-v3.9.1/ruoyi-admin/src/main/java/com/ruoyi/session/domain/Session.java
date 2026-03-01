package com.ruoyi.session.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事届次对象 sam_session
 */
public class Session extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 届次主键 */
    private Long id;

    /** 赛事主表ID */
    private Long competitionId;

    /** 赛事名称（导出导入通用，正常使用） */
    @Excel(name = "赛事名称", prompt = "请填写赛事主表中存在的赛事名称，无则自动新增")
    private String competitionName;

    /** 届次 */
    @Excel(name = "届次", prompt = "例：第一届/2025届")
    private String session;

    // ====================== 导出专用（存数字，带dictType，导出显示文字） ======================
    /** 赛事类别（数据库存数字，导出显示文字） */
    @Excel(name = "赛事类别", dictType = "sys_competition_category")
    private String category;

    /** 盖章单位 */
    @Excel(name = "盖章单位", prompt = "多个单位用分号分隔，例：XX政府;XX学会")
    private String organizations;

    /** 赛事级别（数据库存数字，导出显示文字） */
    @Excel(name = "赛事级别", dictType = "sys_competition_level")
    private String level;

    /** 标签（数据库存数字，导出显示文字） */
    @Excel(name = "标签", dictType = "sys_competition_tag", separator = ",")
    private String tags;

    // ====================== 导入专用（纯读Excel文字，不带任何dictType，永不自动转换） ======================
    /** 导入专用赛事类别（仅读取Excel文字，不参与数据库、不参与导出） */
    @Excel(name = "赛事类别", prompt = "填写：政府类/学会类")
    private String categoryImport;

    /** 导入专用赛事级别 */
    @Excel(name = "赛事级别", prompt = "填写：Ⅰ级（国家级）/Ⅱ级（市级）")
    private String levelImport;

    /** 导入专用标签 */
    @Excel(name = "标签", prompt = "多个标签用逗号分隔，例：素质提升奖,高等教育学会白名单,学院特别认定", separator = ",")
    private String tagsImport;

    // ====================== 状态 ======================
    private String status;
    private String delFlag;

    // ====================== getter/setter 全给你写好 ======================
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getCompetitionId() {return competitionId;}
    public void setCompetitionId(Long competitionId) {this.competitionId = competitionId;}
    public String getCompetitionName() {return competitionName;}
    public void setCompetitionName(String competitionName) {this.competitionName = competitionName;}
    public String getSession() {return session;}
    public void setSession(String session) {this.session = session;}
    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}
    public String getOrganizations() {return organizations;}
    public void setOrganizations(String organizations) {this.organizations = organizations;}
    public String getLevel() {return level;}
    public void setLevel(String level) {this.level = level;}
    public String getTags() {return tags;}
    public void setTags(String tags) {this.tags = tags;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public String getDelFlag() {return delFlag;}
    public void setDelFlag(String delFlag) {this.delFlag = delFlag;}

    // 导入专用getter/setter
    public String getCategoryImport() {return categoryImport;}
    public void setCategoryImport(String categoryImport) {this.categoryImport = categoryImport;}
    public String getLevelImport() {return levelImport;}
    public void setLevelImport(String levelImport) {this.levelImport = levelImport;}
    public String getTagsImport() {return tagsImport;}
    public void setTagsImport(String tagsImport) {this.tagsImport = tagsImport;}

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("competitionId", getCompetitionId())
                .append("session", getSession())
                .append("category", getCategory())
                .append("organizations", getOrganizations())
                .append("level", getLevel())
                .append("tags", getTags())
                .append("delFlag", getDelFlag())
                .toString();
    }
}