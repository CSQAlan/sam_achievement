package com.ruoyi.achievement.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 指导老师对象 sam_achievement_advisor
 * * @author ruoyi
 * @date 2026-02-11
 */
public class SamAchievementAdvisor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long advisorId;

    /** 成果ID */
    @Excel(name = "成果ID")
    private String achievementId;

    /** 教师工号 */
    @Excel(name = "教师工号")
    private String teacherId;

    /** 教师姓名 (数据库表中无此字段，通过关联查询获取，或用于前端回显) */
    @Excel(name = "教师姓名")
    private String teacherName;

    /** 排序（1=主指导） */
    @Excel(name = "排序")
    private Integer orderNo;

    /** 是否主要指导 (0=否, 1=是) */
    @Excel(name = "是否主要指导", readConverterExp = "0=否,1=是")
    private Integer manager;

    /** 删除标志 */
    private Long delFlag;

    // ================= Getters and Setters =================

    public void setAdvisorId(Long advisorId)
    {
        this.advisorId = advisorId;
    }

    public Long getAdvisorId()
    {
        return advisorId;
    }

    public void setAchievementId(String achievementId)
    {
        this.achievementId = achievementId;
    }

    public String getAchievementId()
    {
        return achievementId;
    }

    public void setTeacherId(String teacherId)
    {
        this.teacherId = teacherId;
    }

    public String getTeacherId()
    {
        return teacherId;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setOrderNo(Integer orderNo)
    {
        this.orderNo = orderNo;
    }

    public Integer getOrderNo()
    {
        return orderNo;
    }

    public void setManager(Integer manager)
    {
        this.manager = manager;
    }

    public Integer getManager()
    {
        return manager;
    }

    public void setDelFlag(Long delFlag)
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("advisorId", getAdvisorId())
                .append("achievementId", getAchievementId())
                .append("teacherId", getTeacherId())
                .append("teacherName", getTeacherName())
                .append("orderNo", getOrderNo())
                .append("manager", getManager())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .append("remark", getRemark())
                .toString();
    }
}