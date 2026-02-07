package com.ruoyi.achievement.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 参赛选手对象 sam_achievement_participant
 * 
 * @author cyy
 * @date 2026-02-03
 */
public class SamAchievementParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String participantId;

    /** 成果ID */
    @Excel(name = "成果ID")
    private String achievementId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String studentId;

    /** 排序（负责人 1） */
    @Excel(name = "排序", readConverterExp = "负=责人,1=")
    private Long orderNo;

    /** 是否负责人 */
    @Excel(name = "是否负责人")
    private String manager;

    /** $column.columnComment */
    private Long delFlag;

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
    public void setOrderNo(Long orderNo) 
    {
        this.orderNo = orderNo;
    }

    public Long getOrderNo() 
    {
        return orderNo;
    }
    public void setManager(String manager) 
    {
        this.manager = manager;
    }

    public String getManager() 
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
