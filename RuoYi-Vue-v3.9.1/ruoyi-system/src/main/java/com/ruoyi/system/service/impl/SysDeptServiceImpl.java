package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
import com.ruoyi.system.domain.vo.DeptImportResultVo;
import com.ruoyi.system.domain.vo.DeptImportRowVo;
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
    public DeptImportResultVo checkDeptImport(List<SysDept> deptList, Boolean updateSupport)
    {
        return buildDeptImportResult(deptList, updateSupport, null, false);
    }

    @Override
    public String importDept(List<SysDept> deptList, Boolean updateSupport, String operName)
    {
        DeptImportResultVo result = buildDeptImportResult(deptList, updateSupport, operName, true);
        if (result.getFailureCount() != null && result.getFailureCount() > 0)
        {
            throw new ServiceException(result.getMessage());
        }
        return result.getMessage();
    }

    private DeptImportResultVo buildDeptImportResult(List<SysDept> deptList, Boolean updateSupport, String operName, boolean performImport)
    {
        if (StringUtils.isEmpty(deptList))
        {
            throw new ServiceException("导入部门数据不能为空！");
        }

        DeptImportResultVo result = new DeptImportResultVo();
        result.setPreview(!performImport);
        result.setImportMode(Boolean.TRUE.equals(updateSupport) ? "UPDATE_IF_EXISTS" : "ADD_ONLY");
        result.setCommitMode(performImport ? "IMPORT" : "CHECK_ONLY");

        List<SysDept> allDeptList = deptMapper.selectDeptList(new SysDept());
        Map<String, List<SysDept>> deptNameMap = buildDeptNameMap(allDeptList);
        Map<Long, SysDept> deptIdMap = buildDeptIdMap(allDeptList);
        Map<String, SysDept> deptPathMap = buildDeptPathMap(allDeptList, deptIdMap);
        SysDept defaultRootDept = resolveDefaultRootDept(allDeptList);
        Map<SysDept, Integer> rowNumMap = buildImportRowNumMap(deptList);
        List<SysDept> importRows = sortImportDeptList(deptList);

        for (SysDept dept : importRows)
        {
            DeptImportRowVo row = new DeptImportRowVo();
            row.setRowNum(rowNumMap.getOrDefault(dept, result.getRows().size() + 2));
            row.setDeptName(dept != null ? dept.getDeptName() : null);
            row.setParentName(dept != null ? dept.getParentName() : null);
            row.setDeptPath(dept != null ? dept.getDeptPath() : null);

            try
            {
                normalizeImportDept(dept);
                row.setDeptName(dept.getDeptName());
                row.setParentName(dept.getParentName());
                row.setDeptPath(dept.getDeptPath());

                resolveImportDeptByPath(dept, deptPathMap);
                BeanValidators.validateWithException(validator, dept);

                SysDept parentDept = resolveImportParentDept(dept, deptNameMap, defaultRootDept);
                dept.setParentId(parentDept.getDeptId());
                dept.setParentName(parentDept.getDeptName());
                row.setResolvedParentName(parentDept.getDeptName());

                if (StringUtils.isBlank(dept.getStatus()))
                {
                    dept.setStatus(UserConstants.DEPT_NORMAL);
                }

                String currentFullPath = resolveCurrentDeptFullPath(dept, parentDept, deptIdMap);
                dept.setDeptPath(currentFullPath);
                row.setDeptPath(currentFullPath);

                SysDept exists = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
                if (StringUtils.isNull(exists))
                {
                    row.setAction("新增");
                    if (performImport)
                    {
                        dept.setCreateBy(operName);
                        this.insertDept(dept);
                        SysDept importedDept = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
                        if (importedDept != null)
                        {
                            appendDeptNameMap(deptNameMap, importedDept);
                            deptIdMap.put(importedDept.getDeptId(), importedDept);
                            appendDeptPathMap(deptPathMap, importedDept, deptIdMap);
                        }
                    }
                    else
                    {
                        appendDeptNameMap(deptNameMap, dept);
                        deptPathMap.put(currentFullPath, dept);
                    }
                    result.setInsertCount(result.getInsertCount() + 1);
                    row.setSuccess(Boolean.TRUE);
                    row.setMessage(performImport ? "新增成功" : "校验通过，导入时将新增");
                }
                else if (Boolean.TRUE.equals(updateSupport))
                {
                    row.setAction("更新");
                    dept.setDeptId(exists.getDeptId());
                    if (performImport)
                    {
                        dept.setUpdateBy(operName);
                        this.updateDept(dept);
                        replaceDeptCache(deptNameMap, deptPathMap, deptIdMap, dept.getDeptId());
                    }
                    else
                    {
                        replacePreviewDeptCache(deptNameMap, deptPathMap, dept, exists.getDeptId(), currentFullPath);
                    }
                    result.setUpdateCount(result.getUpdateCount() + 1);
                    row.setSuccess(Boolean.TRUE);
                    row.setMessage(performImport ? "更新成功" : "校验通过，导入时将更新");
                }
                else
                {
                    row.setAction("新增");
                    row.setSuccess(Boolean.FALSE);
                    row.setMessage("部门已存在，若需覆盖请勾选“更新已有数据”");
                    result.setFailureCount(result.getFailureCount() + 1);
                }
            }
            catch (Exception e)
            {
                row.setAction(StringUtils.isBlank(row.getAction()) ? "校验" : row.getAction());
                row.setSuccess(Boolean.FALSE);
                row.setMessage(e.getMessage());
                result.setFailureCount(result.getFailureCount() + 1);
            }
            result.getRows().add(row);
        }

        result.setTotalCount(importRows.size());
        result.setSuccessCount(result.getInsertCount() + result.getUpdateCount() + result.getSkipCount());
        result.setMessage(buildDeptImportSummary(result, performImport));
        return result;
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
     * 根据部门ID获取学院ID (parentId为100的部门)
     *
     * @param deptId 部门ID
     * @return 学院ID
     */
    @Override
    public Long getCollegeId(Long deptId)
    {
        if (StringUtils.isNull(deptId))
        {
            return null;
        }
        SysDept dept = deptMapper.selectDeptById(deptId);
        if (StringUtils.isNull(dept))
        {
            return null;
        }

        // 如果当前部门的父级就是100，说明它自己就是学院
        if (dept.getParentId() != null && dept.getParentId() == 100L)
        {
            return dept.getDeptId();
        }

        // 否则从ancestors中寻找。ancestors格式通常为: 0,100,学院ID,专业ID
        String ancestors = dept.getAncestors();
        if (StringUtils.isNotEmpty(ancestors))
        {
            String[] ids = ancestors.split(",");
            for (int i = 0; i < ids.length; i++)
            {
                // 找到100后，下一个就是学院ID
                if ("100".equals(ids[i]) && (i + 1) < ids.length)
                {
                    return Long.valueOf(ids[i + 1]);
                }
            }
        }
        
        // 兜底方案：如果没找到100（说明层级结构不标准），则直接返回当前部门ID
        return dept.getDeptId();
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
        dept.setDeptPath(normalizeDeptPath(dept.getDeptPath()));
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

    private Map<Long, SysDept> buildDeptIdMap(List<SysDept> deptList)
    {
        Map<Long, SysDept> deptIdMap = new HashMap<Long, SysDept>();
        for (SysDept dept : deptList)
        {
            if (dept != null && dept.getDeptId() != null)
            {
                deptIdMap.put(dept.getDeptId(), dept);
            }
        }
        return deptIdMap;
    }

    private Map<String, SysDept> buildDeptPathMap(List<SysDept> deptList, Map<Long, SysDept> deptIdMap)
    {
        Map<String, SysDept> deptPathMap = new HashMap<String, SysDept>();
        for (SysDept dept : deptList)
        {
            appendDeptPathMap(deptPathMap, dept, deptIdMap);
        }
        return deptPathMap;
    }

    private void appendDeptPathMap(Map<String, SysDept> deptPathMap, SysDept dept, Map<Long, SysDept> deptIdMap)
    {
        String fullPath = buildDeptFullPath(dept, deptIdMap);
        if (StringUtils.isNotBlank(fullPath))
        {
            deptPathMap.put(fullPath, dept);
        }
    }

    private void replaceDeptCache(Map<String, List<SysDept>> deptNameMap, Map<String, SysDept> deptPathMap,
            Map<Long, SysDept> deptIdMap, Long deptId)
    {
        if (deptId == null)
        {
            return;
        }
        for (List<SysDept> sameNameDeptList : deptNameMap.values())
        {
            sameNameDeptList.removeIf(item -> item != null && deptId.equals(item.getDeptId()));
        }
        deptPathMap.entrySet().removeIf(entry -> entry.getValue() != null && deptId.equals(entry.getValue().getDeptId()));
        SysDept latest = deptMapper.selectDeptById(deptId);
        if (latest != null)
        {
            deptIdMap.put(latest.getDeptId(), latest);
            appendDeptNameMap(deptNameMap, latest);
            appendDeptPathMap(deptPathMap, latest, deptIdMap);
        }
    }

    private void replacePreviewDeptCache(Map<String, List<SysDept>> deptNameMap, Map<String, SysDept> deptPathMap,
            SysDept dept, Long deptId, String currentFullPath)
    {
        if (deptId != null)
        {
            dept.setDeptId(deptId);
            for (List<SysDept> sameNameDeptList : deptNameMap.values())
            {
                sameNameDeptList.removeIf(item -> item != null && deptId.equals(item.getDeptId()));
            }
            deptPathMap.entrySet().removeIf(entry -> entry.getValue() != null && deptId.equals(entry.getValue().getDeptId()));
        }
        appendDeptNameMap(deptNameMap, dept);
        deptPathMap.put(currentFullPath, dept);
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
            throw new ServiceException("上级部门[" + dept.getParentName() + "]存在重名，请改用部门全路径导入");
        }
        return parentDeptList.get(0);
    }

    private List<SysDept> sortImportDeptList(List<SysDept> deptList)
    {
        List<SysDept> sortedList = new ArrayList<SysDept>(deptList);
        sortedList.sort((left, right) -> Integer.compare(resolveImportDepth(left), resolveImportDepth(right)));
        return sortedList;
    }

    private Map<SysDept, Integer> buildImportRowNumMap(List<SysDept> deptList)
    {
        Map<SysDept, Integer> rowNumMap = new IdentityHashMap<SysDept, Integer>();
        for (int i = 0; i < deptList.size(); i++)
        {
            rowNumMap.put(deptList.get(i), i + 2);
        }
        return rowNumMap;
    }

    private int resolveImportDepth(SysDept dept)
    {
        if (dept == null)
        {
            return Integer.MAX_VALUE;
        }
        if (StringUtils.isNotBlank(dept.getDeptPath()))
        {
            return splitDeptPath(dept.getDeptPath()).length;
        }
        return StringUtils.isBlank(dept.getParentName()) ? 1 : 2;
    }

    private void resolveImportDeptByPath(SysDept dept, Map<String, SysDept> deptPathMap)
    {
        if (dept == null || StringUtils.isBlank(dept.getDeptPath()))
        {
            return;
        }

        String[] pathNodes = splitDeptPath(dept.getDeptPath());
        if (pathNodes.length == 0)
        {
            return;
        }

        String deptNameFromPath = pathNodes[pathNodes.length - 1];
        if (StringUtils.isBlank(dept.getDeptName()))
        {
            dept.setDeptName(deptNameFromPath);
        }
        else if (!StringUtils.equals(dept.getDeptName(), deptNameFromPath))
        {
            throw new ServiceException("部门全路径最后一级与部门名称不一致");
        }

        if (pathNodes.length > 1)
        {
            String parentPath = joinDeptPath(pathNodes, pathNodes.length - 1);
            SysDept parentDept = deptPathMap.get(parentPath);
            if (parentDept == null)
            {
                throw new ServiceException("未找到部门路径对应的上级部门[" + parentPath + "]");
            }
            dept.setParentName(parentDept.getDeptName());
        }
    }

    private String buildDeptFullPath(SysDept dept, Map<Long, SysDept> deptIdMap)
    {
        if (dept == null || StringUtils.isBlank(dept.getDeptName()))
        {
            return null;
        }
        if (StringUtils.isNotBlank(dept.getDeptPath()))
        {
            return normalizeDeptPath(dept.getDeptPath());
        }

        List<String> names = new ArrayList<String>();
        if (StringUtils.isNotBlank(dept.getAncestors()))
        {
            String[] ancestorIds = dept.getAncestors().split(",");
            for (String ancestorId : ancestorIds)
            {
                String trimmedAncestorId = StringUtils.trim(ancestorId);
                if (StringUtils.isBlank(trimmedAncestorId) || "0".equals(trimmedAncestorId))
                {
                    continue;
                }
                SysDept ancestorDept = deptIdMap.get(Long.valueOf(trimmedAncestorId));
                if (ancestorDept != null && StringUtils.isNotBlank(ancestorDept.getDeptName()))
                {
                    names.add(StringUtils.trim(ancestorDept.getDeptName()));
                }
            }
        }
        names.add(StringUtils.trim(dept.getDeptName()));
        return String.join("/", names);
    }

    private String resolveCurrentDeptFullPath(SysDept dept, SysDept parentDept, Map<Long, SysDept> deptIdMap)
    {
        if (StringUtils.isNotBlank(dept.getDeptPath()))
        {
            return normalizeDeptPath(dept.getDeptPath());
        }
        String parentPath = parentDept == null ? StringUtils.EMPTY : buildDeptFullPath(parentDept, deptIdMap);
        if (StringUtils.isBlank(parentPath))
        {
            return StringUtils.trim(dept.getDeptName());
        }
        return parentPath + "/" + StringUtils.trim(dept.getDeptName());
    }

    private String buildDeptImportSummary(DeptImportResultVo result, boolean performImport)
    {
        if (result.getFailureCount() != null && result.getFailureCount() > 0)
        {
            return (performImport ? "很抱歉，导入失败！" : "校验完成，发现异常！")
                    + "共 " + result.getFailureCount() + " 条数据校验失败，"
                    + "可处理 " + (result.getInsertCount() + result.getUpdateCount()) + " 条。";
        }
        return (performImport ? "恭喜您，数据已全部导入成功！" : "校验通过，可以导入。")
                + "共计 " + result.getTotalCount() + " 条记录，"
                + "其中新增 " + result.getInsertCount() + " 条，更新 " + result.getUpdateCount() + " 条。";
    }

    private String normalizeDeptPath(String deptPath)
    {
        if (StringUtils.isBlank(deptPath))
        {
            return StringUtils.EMPTY;
        }
        String[] pathNodes = splitDeptPath(deptPath);
        return pathNodes.length == 0 ? StringUtils.EMPTY : String.join("/", pathNodes);
    }

    private String[] splitDeptPath(String deptPath)
    {
        if (StringUtils.isBlank(deptPath))
        {
            return new String[0];
        }
        String[] rawNodes = deptPath.trim().split("[/\\\\]+");
        List<String> pathNodes = new ArrayList<String>();
        for (String rawNode : rawNodes)
        {
            String node = StringUtils.trim(rawNode);
            if (StringUtils.isNotBlank(node))
            {
                pathNodes.add(node);
            }
        }
        return pathNodes.toArray(new String[0]);
    }

    private String joinDeptPath(String[] pathNodes, int endExclusive)
    {
        List<String> parentNodes = new ArrayList<String>();
        for (int i = 0; i < endExclusive; i++)
        {
            parentNodes.add(pathNodes[i]);
        }
        return String.join("/", parentNodes);
    }
}


