package com.ruoyi.achievement.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.web.service.ProfileCompletionService;

import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.common.core.domain.entity.SysDept;

/**
 * 学生档案Controller
 * 
 * @author 王璨
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/student/student")
public class SamStudentController extends BaseController
{
    @Autowired
    private ISamStudentService samStudentService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ProfileCompletionService profileCompletionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 查询学生档案列表
     */
    @PreAuthorize("@ss.hasAnyPermi('student:student:list,achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @GetMapping("/list")
    public TableDataInfo list(SamStudent samStudent)
    {
        startPage();
        applyHierarchicalFilter(samStudent);
        List<SamStudent> list = samStudentService.selectSamStudentList(samStudent);
        return getDataTable(list);
    }

    /**
     * 导出学生档案列表
     */
    @PreAuthorize("@ss.hasPermi('student:student:export')")
    @Log(title = "学生档案", businessType = BusinessType.EXPORT)
    @BizAudit(bizType = "student_archive", bizName = "导出学生档案", opType = BizAuditOpType.EXPORT, handler = "studentBizAuditHandler")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamStudent samStudent)
    {
        applyHierarchicalFilter(samStudent);
        List<SamStudent> list = samStudentService.selectSamStudentList(samStudent);
        ExcelUtil<SamStudent> util = new ExcelUtil<SamStudent>(SamStudent.class);
        util.exportExcel(response, list, "学生档案数据");
    }

    /**
     * 应用层级权限过滤
     */
    private void applyHierarchicalFilter(SamStudent samStudent) {
        // 1. 学校管理员 (School Level) - 优先级最高，可查看全量
        if (SecurityUtils.hasRole("admin") || SecurityUtils.hasRole("schooladmin") || SecurityUtils.hasRole("schoolleveladmin") || SecurityUtils.hasRole("schooleveladmin")) {
            return; // 不过滤
        }

        // 2. 学院管理员 (College Level) - 仅查看本院学生
        if (SecurityUtils.hasRole("collegeadmin") || SecurityUtils.hasRole("collegeleveladmin")) {
            if (StringUtils.isEmpty(samStudent.getSchool())) {
                Long userDeptId = SecurityUtils.getDeptId();
                if (userDeptId != null) {
                    Long collegeId = deptService.getCollegeId(userDeptId);
                    if (collegeId != null) {
                        SysDept collegeDept = deptService.selectDeptById(collegeId);
                        if (collegeDept != null) {
                            samStudent.setSchool(collegeDept.getDeptName());
                        }
                    }
                }
            }
            return;
        }

        // 3. 个人角色 (Student/Teacher) - 默认情况下，如果没有管理角色，普通学生只能看到自己（可选，目前系统默认允许查询列表但可能受到其他限制）
        // 如果需要强制过滤个人，可以在这里添加逻辑。目前根据业务，列表页通常由管理员使用。
    }

    @Log(title = "学生管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('student:student:import')")
    @BizAudit(bizType = "student_archive", bizName = "导入学生档案", opType = BizAuditOpType.IMPORT, handler = "studentBizAuditHandler", async = false)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SamStudent> util = new ExcelUtil<SamStudent>(SamStudent.class);
        List<SamStudent> studentList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = samStudentService.importStudent(studentList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SamStudent> util = new ExcelUtil<SamStudent>(SamStudent.class);
        util.importTemplateExcel(response, "学生数据");
    }

    /**
     * 获取学生档案详细信息
     */
    @PreAuthorize("@ss.hasAnyPermi('student:student:query,achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") Long studentId)
    {
        return success(samStudentService.selectSamStudentByStudentId(studentId));
    }

    /**
     * 新增学生档案
     */
    @PreAuthorize("@ss.hasAnyPermi('student:student:add,achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @Log(title = "学生档案", businessType = BusinessType.INSERT)
    @BizAudit(bizType = "student_archive", bizName = "新增学生档案", opType = BizAuditOpType.ADD, handler = "studentBizAuditHandler", async = false)
    @PostMapping
    public AjaxResult add(@RequestBody SamStudent samStudent)
    {
        int rows = samStudentService.insertSamStudent(samStudent);
        if (rows > 0 && StringUtils.isNotBlank(samStudent.getNo()))
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && StringUtils.equals(loginUser.getUsername(), samStudent.getNo()))
            {
                profileCompletionService.refreshProfileCompletion(loginUser);
                tokenService.setLoginUser(loginUser);
            }
        }
        return toAjax(rows);
    }

    /**
     * 修改学生档案
     */
    @PreAuthorize("@ss.hasAnyPermi('student:student:edit,achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @Log(title = "学生档案", businessType = BusinessType.UPDATE)
    @BizAudit(bizType = "student_archive", bizName = "修改学生档案", opType = BizAuditOpType.UPDATE, handler = "studentBizAuditHandler", async = false)
    @PutMapping
    public AjaxResult edit(@RequestBody SamStudent samStudent)
    {
        int rows = samStudentService.updateSamStudent(samStudent);
        if (rows > 0 && StringUtils.isNotBlank(samStudent.getNo()))
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && StringUtils.equals(loginUser.getUsername(), samStudent.getNo()))
            {
                profileCompletionService.refreshProfileCompletion(loginUser);
                tokenService.setLoginUser(loginUser);
            }
        }
        return toAjax(rows);
    }

    /**
     * 删除学生档案
     */
    @PreAuthorize("@ss.hasPermi('student:student:remove')")
    @Log(title = "学生档案", businessType = BusinessType.DELETE)
    @BizAudit(bizType = "student_archive", bizName = "删除学生档案", opType = BizAuditOpType.DELETE, handler = "studentBizAuditHandler", async = false)
	@DeleteMapping("/{studentIds}")
    public AjaxResult remove(@PathVariable Long[] studentIds)
    {
        return toAjax(samStudentService.deleteSamStudentByStudentIds(studentIds));
    }
}
