package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.domain.vo.DeptImportResultVo;

/**
 * 部门管理 服务层
 * 
 * @author ruoyi
 */
public interface ISysDeptService
{
    public List<SysDept> selectDeptList(SysDept dept);

    public List<TreeSelect> selectDeptTreeList(SysDept dept);

    public List<SysDept> buildDeptTree(List<SysDept> depts);

    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts);

    public List<Long> selectDeptListByRoleId(Long roleId);

    public SysDept selectDeptById(Long deptId);

    public int selectNormalChildrenDeptById(Long deptId);

    public boolean hasChildByDeptId(Long deptId);

    public boolean checkDeptExistUser(Long deptId);

    public boolean checkDeptNameUnique(SysDept dept);

    public void checkDeptDataScope(Long deptId);

    public int insertDept(SysDept dept);

    public int updateDept(SysDept dept);

    public DeptImportResultVo checkDeptImport(List<SysDept> deptList, Boolean updateSupport);

    public String importDept(List<SysDept> deptList, Boolean updateSupport, String operName);

    public int deleteDeptById(Long deptId);
}
