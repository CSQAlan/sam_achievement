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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.web.service.ProfileCompletionService;

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
        List<SamStudent> list = samStudentService.selectSamStudentList(samStudent);
        return getDataTable(list);
    }

    /**
     * 导出学生档案列表
     */
    @PreAuthorize("@ss.hasPermi('student:student:export')")
    @Log(title = "学生档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamStudent samStudent)
    {
        List<SamStudent> list = samStudentService.selectSamStudentList(samStudent);
        ExcelUtil<SamStudent> util = new ExcelUtil<SamStudent>(SamStudent.class);
        util.exportExcel(response, list, "学生档案数据");
    }

    @Log(title = "学生管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('student:student:import')")
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
    @PreAuthorize("@ss.hasPermi('student:student:edit')")
    @Log(title = "学生档案", businessType = BusinessType.UPDATE)
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
	@DeleteMapping("/{studentIds}")
    public AjaxResult remove(@PathVariable Long[] studentIds)
    {
        return toAjax(samStudentService.deleteSamStudentByStudentIds(studentIds));
    }
}
