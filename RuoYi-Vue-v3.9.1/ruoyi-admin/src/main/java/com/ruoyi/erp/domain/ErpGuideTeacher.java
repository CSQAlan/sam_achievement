package com.ruoyi.erp.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 指导老师对象 erp_guide_teacher
 */
public class ErpGuideTeacher extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long outcomeId;
    private String teacherCode; // 教师工号
    private Integer sortOrder;
    private Integer isLeader;   // 是否第一指导

    /** 教师姓名 (数据库不存在，仅用于显示) */
    private String teacherName;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setOutcomeId(Long outcomeId) { this.outcomeId = outcomeId; }
    public Long getOutcomeId() { return outcomeId; }

    public void setTeacherCode(String teacherCode) { this.teacherCode = teacherCode; }
    public String getTeacherCode() { return teacherCode; }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getSortOrder() { return sortOrder; }

    public void setIsLeader(Integer isLeader) { this.isLeader = isLeader; }
    public Integer getIsLeader() { return isLeader; }
}