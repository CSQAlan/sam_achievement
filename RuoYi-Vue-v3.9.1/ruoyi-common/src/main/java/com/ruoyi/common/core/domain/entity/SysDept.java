package com.ruoyi.common.core.domain.entity;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.annotation.Excel.Type;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 部门表 sys_dept
 * 
 * @author ruoyi
 */
public class SysDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 部门ID */
    private Long deptId;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    @Excel(name = "部门名称", prompt = "填写当前部门名称；如填写“部门全路径”，最后一级应与此列一致；若此列留空，可由路径最后一级自动识别")
    private String deptName;

    /** 显示顺序 */
    @Excel(name = "显示顺序", cellType = ColumnType.NUMERIC, prompt = "填写显示顺序数字；可留空，默认按0导入；数字越小越靠前")
    private Integer orderNum;

    /** 负责人 */
    @Excel(name = "负责人", prompt = "填写部门负责人姓名；选填")
    private String leader;

    /** 联系电话 */
    @Excel(name = "联系电话", cellType = ColumnType.TEXT, prompt = "填写部门联系电话；选填，建议填写11位手机号或固定电话")
    private String phone;

    /** 邮箱 */
    @Excel(name = "邮箱", prompt = "填写部门联系邮箱；选填，需符合邮箱格式")
    private String email;

    /** 部门状态 0正常 1停用 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用", combo = { "0", "1" }, prompt = "填写部门状态：0=正常，1=停用；留空默认0")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 上级部门名称 */
    @Excel(name = "上级部门名称", type = Type.IMPORT, prompt = "填写直接上级部门名称；如存在同名上级，建议改填“部门全路径”；留空时仅在系统存在唯一根部门时自动挂到根部门下")
    private String parentName;

    /** 部门全路径（导入用） */
    @Excel(name = "部门全路径", type = Type.IMPORT, prompt = "推荐填写完整层级路径，如：学校/信息工程学院/计算机系；支持“/”或“\\”分隔；填写后系统优先按路径识别层级，并以最后一级作为部门名称")
    private String deptPath;
    
    /** 子部门 */
    private List<SysDept> children = new ArrayList<SysDept>();

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getAncestors()
    {
        return ancestors;
    }

    public void setAncestors(String ancestors)
    {
        this.ancestors = ancestors;
    }

    @NotBlank(message = "部门名称不能为空")
    @Size(min = 0, max = 30, message = "部门名称长度不能超过30个字符")
    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    @NotNull(message = "显示顺序不能为空")
    public Integer getOrderNum()
    {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum)
    {
        this.orderNum = orderNum;
    }

    public String getLeader()
    {
        return leader;
    }

    public void setLeader(String leader)
    {
        this.leader = leader;
    }

    @Size(min = 0, max = 11, message = "联系电话长度不能超过11个字符")
    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public String getDeptPath()
    {
        return deptPath;
    }

    public void setDeptPath(String deptPath)
    {
        this.deptPath = deptPath;
    }

    public List<SysDept> getChildren()
    {
        return children;
    }

    public void setChildren(List<SysDept> children)
    {
        this.children = children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("deptId", getDeptId())
            .append("parentId", getParentId())
            .append("ancestors", getAncestors())
            .append("deptName", getDeptName())
            .append("orderNum", getOrderNum())
            .append("leader", getLeader())
            .append("phone", getPhone())
            .append("email", getEmail())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
