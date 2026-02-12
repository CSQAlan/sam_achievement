package com.ruoyi.achievement.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 参赛选手对象 sam_achievement_participant
 * * @author cyy
 * @date 2026-02-03
 */
public class SamAchievementParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String participantId; // 注意：数据库通常是 Long 自增，这里你定义为 String，需确保匹配

    /** 成果ID */
    @Excel(name = "成果ID")
    private String achievementId;

    /** 学生学号 */
    @Excel(name = "学生学号")
    private String studentId;

    /** 学生姓名 (数据库无此字段，用于前端传值和业务逻辑) */
    @Excel(name = "学生姓名")
    private String studentName;  // <--- 必须要加上这个字段！！！

    /** 排序 */
    @Excel(name = "排序")
    private Integer orderNo; // 建议用 Integer 对应数据库 int

    /** 是否负责人 (1=是, 0=否) */
    @Excel(name = "是否负责人", readConverterExp = "0=否,1=是")
    private Integer manager; // 建议用 Integer 对应数据库 tinyint

    /** 删除标志 */
    private Long delFlag;

    // ================= Getters and Setters =================

    public void setParticipantId(String participantId)
    {
        this.participantId = participantId;
    }

    public String getParticipantId()
    {
        return participantId;
    }

    public void setAchievementId(String achievementId)
    {
        this.achievementId = achievementId;
    }

    public String getAchievementId()
    {
        return achievementId;
    }

    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentId()
    {
        return studentId;
    }

    // --- 新增的 getStudentName / setStudentName ---
    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public String getStudentName()
    {
        return studentName;
    }
    // -------------------------------------------

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
                .append("participantId", getParticipantId())
                .append("achievementId", getAchievementId())
                .append("studentId", getStudentId())
                .append("studentName", getStudentName()) // toString 也加上
                .append("orderNo", getOrderNo())
                .append("manager", getManager())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .append("remark", getRemark())
                .toString();
    }
}
