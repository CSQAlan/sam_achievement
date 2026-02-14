package com.ruoyi.achievement.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 教师档案对象 sam_teacher
 * 
 * @author 王璨
 * @date 2026-02-03
 */
public class SamTeacher extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 教师id */
    @Excel(name = "教师id")
    private Long id;

    /** 教师姓名 */
    @Excel(name = "教师姓名")
    private String teacherName;

    /** 工号 */
    @Excel(name = "工号")
    private String no;

    /** 学院 */
    @Excel(name = "学院")
    private String school;

    /** 院系 */
    @Excel(name = "院系")
    private String department;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setTeacherName(String teacherName) 
    {
        this.teacherName = teacherName;
    }

    public String getTeacherName() 
    {
        return teacherName;
    }

    public void setNo(String no) 
    {
        this.no = no;
    }

    public String getNo() 
    {
        return no;
    }

    public void setSchool(String school) 
    {
        this.school = school;
    }

    public String getSchool() 
    {
        return school;
    }

    public void setDepartment(String department) 
    {
        this.department = department;
    }

    public String getDepartment() 
    {
        return department;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("teacherName", getTeacherName())
            .append("no", getNo())
            .append("school", getSchool())
            .append("department", getDepartment())
            .toString();
    }
}
