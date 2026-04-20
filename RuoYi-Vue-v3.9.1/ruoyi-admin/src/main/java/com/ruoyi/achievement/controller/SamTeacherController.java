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
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.achievement.service.ISamTeacherService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.web.service.ProfileCompletionService;

/**
 * 教师档案Controller
 * 
 * @author 王璨
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/achievement/teacher")
public class SamTeacherController extends BaseController
{
    @Autowired
    private ISamTeacherService samTeacherService;

    @Autowired
    private ProfileCompletionService profileCompletionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 查询教师档案列表
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:teacher:list,achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @GetMapping("/list")
    public TableDataInfo list(SamTeacher samTeacher)
    {
        startPage();
        List<SamTeacher> list = samTeacherService.selectSamTeacherList(samTeacher);
        return getDataTable(list);
    }

    /**
     * 导出教师档案列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:teacher:export')")
    @Log(title = "教师档案", businessType = BusinessType.EXPORT)
    @BizAudit(bizType = "teacher_archive", bizName = "导出教师档案", opType = BizAuditOpType.EXPORT, handler = "teacherBizAuditHandler")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamTeacher samTeacher)
    {
        List<SamTeacher> list = samTeacherService.selectSamTeacherList(samTeacher);
        ExcelUtil<SamTeacher> util = new ExcelUtil<SamTeacher>(SamTeacher.class);
        util.exportExcel(response, list, "教师档案数据");
    }

    @Log(title = "教师管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('achievement:teacher:import')")
    @BizAudit(bizType = "teacher_archive", bizName = "导入教师档案", opType = BizAuditOpType.IMPORT, handler = "teacherBizAuditHandler", async = false)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SamTeacher> util = new ExcelUtil<SamTeacher>(SamTeacher.class);
        List<SamTeacher> teacherList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = samTeacherService.importTeacher(teacherList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SamTeacher> util = new ExcelUtil<SamTeacher>(SamTeacher.class);
        util.importTemplateExcel(response, "教师数据");
    }

    /**
     * 获取教师档案详细信息
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:teacher:query,achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(samTeacherService.selectSamTeacherById(id));
    }

    /**
     * 新增教师档案
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:teacher:add,achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @Log(title = "教师档案", businessType = BusinessType.INSERT)
    @BizAudit(bizType = "teacher_archive", bizName = "新增教师档案", opType = BizAuditOpType.ADD, handler = "teacherBizAuditHandler", async = false)
    @PostMapping
    public AjaxResult add(@RequestBody SamTeacher samTeacher)
    {
        int rows = samTeacherService.insertSamTeacher(samTeacher);
        if (rows > 0 && StringUtils.isNotBlank(samTeacher.getNo()))
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && StringUtils.equals(loginUser.getUsername(), samTeacher.getNo()))
            {
                profileCompletionService.refreshProfileCompletion(loginUser);
                tokenService.setLoginUser(loginUser);
            }
        }
        return toAjax(rows);
    }

    /**
     * 修改教师档案
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:teacher:edit,achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @Log(title = "教师档案", businessType = BusinessType.UPDATE)
    @BizAudit(bizType = "teacher_archive", bizName = "修改教师档案", opType = BizAuditOpType.UPDATE, handler = "teacherBizAuditHandler", async = false)
    @PutMapping
    public AjaxResult edit(@RequestBody SamTeacher samTeacher)
    {
        int rows = samTeacherService.updateSamTeacher(samTeacher);
        if (rows > 0 && StringUtils.isNotBlank(samTeacher.getNo()))
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && StringUtils.equals(loginUser.getUsername(), samTeacher.getNo()))
            {
                profileCompletionService.refreshProfileCompletion(loginUser);
                tokenService.setLoginUser(loginUser);
            }
        }
        return toAjax(rows);
    }

    /**
     * 删除教师档案
     */
    @PreAuthorize("@ss.hasPermi('achievement:teacher:remove')")
    @Log(title = "教师档案", businessType = BusinessType.DELETE)
    @BizAudit(bizType = "teacher_archive", bizName = "删除教师档案", opType = BizAuditOpType.DELETE, handler = "teacherBizAuditHandler", async = false)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(samTeacherService.deleteSamTeacherByIds(ids));
    }
}
