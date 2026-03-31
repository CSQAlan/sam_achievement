package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 部门管理 服务实现
 * 
 * @author ruoyi
 */
@Service
public class SysDeptServiceImpl implements ISysDeptService
{
    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private Validator validator;

    /**
     * 查询部门管理数据
     * 
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept)
    {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门树结构信息
     * 
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    @Override
    public List<TreeSelect> selectDeptTreeList(SysDept dept)
    {
        List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
        return buildDeptTreeSelect(depts);
    }

    /**
     * 构建前端所需要树结构
     * 
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts)
    {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId()))
            {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts)
    {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     * 
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId)
    {
        SysRole role = roleMapper.selectRoleById(roleId);
        return deptMapper.selectDeptListByRoleId(roleId, role.isDeptCheckStrictly());
    }

    /**
     * 根据部门ID查询信息
     * 
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(Long deptId)
    {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     * 
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId)
    {
        return deptMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 是否存在子节点
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId)
    {
        int result = deptMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     * 
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId)
    {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public boolean checkDeptNameUnique(SysDept dept)
    {
        Long deptId = StringUtils.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验部门是否有数据权限
     * 
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(Long deptId)
    {
        if (!SysUser.isAdmin(SecurityUtils.getUserId()) && StringUtils.isNotNull(deptId))
        {
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
            if (StringUtils.isEmpty(depts))
            {
                throw new ServiceException("没有权限访问部门数据！");
            }
        }
    }

    /**
     * 新增保存部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDept dept)
    {
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus()))
        {
            throw new ServiceException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return deptMapper.insertDept(dept);
    }

    /**
     * 修改保存部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(SysDept dept)
    {
        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = deptMapper.selectDeptById(dept.getDeptId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept))
        {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = deptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals("0", dept.getAncestors()))
        {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    @Override
    public String importDept(List<SysDept> deptList, Boolean updateSupport, String operName)
    {
        if (StringUtils.isNull(deptList) || deptList.isEmpty())
        {
            throw new ServiceException("导入部门数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        List<SysDept> allDeptList = deptMapper.selectDeptList(new SysDept());
        Map<String, List<SysDept>> deptNameMap = buildDeptNameMap(allDeptList);
        SysDept defaultRootDept = resolveDefaultRootDept(allDeptList);

        for (SysDept dept : deptList)
        {
            try
            {
                normalizeImportDept(dept);
                BeanValidators.validateWithException(validator, dept);

                SysDept parentDept = resolveImportParentDept(dept, deptNameMap, defaultRootDept);
                dept.setParentId(parentDept.getDeptId());
                dept.setParentName(parentDept.getDeptName());

                if (StringUtils.isBlank(dept.getStatus()))
                {
                    dept.setStatus(UserConstants.DEPT_NORMAL);
                }

                SysDept exists = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
                if (StringUtils.isNull(exists))
                {
                    dept.setCreateBy(operName);
                    this.insertDept(dept);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、部门 ").append(dept.getDeptName()).append(" 导入成功");

                    SysDept importedDept = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
                    if (importedDept != null)
                    {
                        appendDeptNameMap(deptNameMap, importedDept);
                    }
                }
                else if (Boolean.TRUE.equals(updateSupport))
                {
                    dept.setDeptId(exists.getDeptId());
                    dept.setUpdateBy(operName);
                    this.updateDept(dept);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、部门 ").append(dept.getDeptName()).append(" 更新成功");
                    replaceDeptNameMap(deptNameMap, dept);
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、部门 ").append(dept.getDeptName()).append(" 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String deptName = dept == null || StringUtils.isBlank(dept.getDeptName()) ? "未知部门" : dept.getDeptName();
                String msg = "<br/>" + failureNum + "、部门 " + deptName + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，成功 " + successNum + " 条，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }

        successMsg.insert(0, "恭喜您，数据已全部导入成功！共计 " + successNum + " 条记录。");
        return successMsg.toString();
    }

    /**
     * 修改该部门的父级部门状态
     * 
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept)
    {
        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);
        deptMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 修改子元素关系
     * 
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors)
    {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(Long deptId)
    {
        return deptMapper.deleteDeptById(deptId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t)
    {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t)
    {
        List<SysDept> tlist = new ArrayList<SysDept>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext())
        {
            SysDept n = (SysDept) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t)
    {
        return getChildList(list, t).size() > 0;
    }

    private void normalizeImportDept(SysDept dept)
    {
        if (dept == null)
        {
            throw new ServiceException("部门数据不能为空");
        }
        dept.setDeptName(StringUtils.trim(dept.getDeptName()));
        dept.setParentName(StringUtils.trim(dept.getParentName()));
        dept.setLeader(StringUtils.trim(dept.getLeader()));
        dept.setPhone(StringUtils.trim(dept.getPhone()));
        dept.setEmail(StringUtils.trim(dept.getEmail()));
        dept.setStatus(StringUtils.trim(dept.getStatus()));
        if (dept.getOrderNum() == null)
        {
            dept.setOrderNum(0);
        }
    }

    private Map<String, List<SysDept>> buildDeptNameMap(List<SysDept> deptList)
    {
        Map<String, List<SysDept>> deptNameMap = new HashMap<String, List<SysDept>>();
        for (SysDept dept : deptList)
        {
            appendDeptNameMap(deptNameMap, dept);
        }
        return deptNameMap;
    }

    private void appendDeptNameMap(Map<String, List<SysDept>> deptNameMap, SysDept dept)
    {
        if (dept == null || StringUtils.isBlank(dept.getDeptName()))
        {
            return;
        }
        String key = dept.getDeptName().trim();
        List<SysDept> sameNameDeptList = deptNameMap.computeIfAbsent(key, k -> new ArrayList<SysDept>());
        sameNameDeptList.add(dept);
    }

    private void replaceDeptNameMap(Map<String, List<SysDept>> deptNameMap, SysDept dept)
    {
        if (dept == null || StringUtils.isBlank(dept.getDeptName()) || dept.getDeptId() == null)
        {
            return;
        }
        String key = dept.getDeptName().trim();
        List<SysDept> sameNameDeptList = deptNameMap.computeIfAbsent(key, k -> new ArrayList<SysDept>());
        sameNameDeptList.removeIf(item -> item != null && dept.getDeptId().equals(item.getDeptId()));
        SysDept latest = deptMapper.selectDeptById(dept.getDeptId());
        if (latest != null)
        {
            sameNameDeptList.add(latest);
        }
    }

    private SysDept resolveDefaultRootDept(List<SysDept> deptList)
    {
        List<SysDept> rootDeptList = deptList.stream()
                .filter(item -> item != null && Long.valueOf(0L).equals(item.getParentId()))
                .collect(Collectors.toList());
        return rootDeptList.size() == 1 ? rootDeptList.get(0) : null;
    }

    private SysDept resolveImportParentDept(SysDept dept, Map<String, List<SysDept>> deptNameMap, SysDept defaultRootDept)
    {
        if (StringUtils.isBlank(dept.getParentName()))
        {
            if (defaultRootDept != null)
            {
                return defaultRootDept;
            }
            throw new ServiceException("上级部门名称不能为空");
        }

        List<SysDept> parentDeptList = deptNameMap.get(dept.getParentName().trim());
        if (parentDeptList == null || parentDeptList.isEmpty())
        {
            throw new ServiceException("未找到上级部门[" + dept.getParentName() + "]");
        }
        if (parentDeptList.size() > 1)
        {
            throw new ServiceException("上级部门[" + dept.getParentName() + "]存在重名，请先保证唯一");
        }
        return parentDeptList.get(0);
    }
}
