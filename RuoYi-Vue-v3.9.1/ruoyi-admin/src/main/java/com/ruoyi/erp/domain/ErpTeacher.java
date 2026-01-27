package com.ruoyi.erp.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 教师档案对象 erp_teacher
 * 
 * @author 王璨
 * @date 2026-01-27
 */
public class ErpTeacher extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关联系统用户ID */
    @Excel(name = "关联系统用户ID")
    private Long userId;

    /** 工号 */
    @Excel(name = "工号")
    private String teacherCode;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 所属学院ID */
    @Excel(name = "所属学院ID")
    private Long deptId;

    /** 职称 */
    @Excel(name = "职称")
    private String title;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** 状态（0=正常, 1=停用） */
    @Excel(name = "状态", readConverterExp = "0==正常,,1==停用")
    private String status;

    /** 删除标志 */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setTeacherCode(String teacherCode) 
    {
        this.teacherCode = teacherCode;
    }

    public String getTeacherCode() 
    {
        return teacherCode;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("teacherCode", getTeacherCode())
            .append("name", getName())
            .append("deptId", getDeptId())
            .append("title", getTitle())
            .append("phone", getPhone())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
