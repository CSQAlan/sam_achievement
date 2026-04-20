package com.ruoyi.achievement.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生档案对象 sam_student
 * 
 * @author 王璨
 * @date 2026-02-03
 */
public class SamStudent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 学生id */
    @Excel(name = "学生id", type = Excel.Type.EXPORT)
    private Long studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String name;

    /** 学号 */
    @Excel(name = "学号")
    private String no;

    /** 学院ID */
    private String school;

    /** 院系ID */
    private String department;

    /** 专业ID */
    private String major;

    /** 学院 */
    @Excel(name = "学院")
    private String schoolName;

    /** 院系 */
    @Excel(name = "院系")
    private String departmentName;

    /** 专业 */
    @Excel(name = "专业")
    private String majorName;

    /** 班级 */
    @Excel(name = "班级")
    private String className;

    /** 年级 */
    @Excel(name = "年级")
    private String classYear;

    public void setStudentId(Long studentId) 
    {
        this.studentId = studentId;
    }

    public Long getStudentId() 
    {
        return studentId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
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

    public void setMajor(String major) 
    {
        this.major = major;
    }

    public String getMajor() 
    {
        return major;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public void setClassName(String className) 
    {
        this.className = className;
    }

    public String getClassName() 
    {
        return className;
    }

    public void setClassYear(String classYear) 
    {
        this.classYear = classYear;
    }

    public String getClassYear() 
    {
        return classYear;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("studentId", getStudentId())
            .append("name", getName())
            .append("no", getNo())
            .append("school", getSchool())
            .append("department", getDepartment())
            .append("major", getMajor())
            .append("schoolName", getSchoolName())
            .append("departmentName", getDepartmentName())
            .append("majorName", getMajorName())
            .append("className", getClassName())
            .append("classYear", getClassYear())
            .toString();
    }
}
