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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

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

    /**
     * 查询学生档案列表
     */
    @PreAuthorize("@ss.hasPermi('student:student:list')")
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

    /**
     * 获取学生档案详细信息
     */
    @PreAuthorize("@ss.hasPermi('student:student:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") Long studentId)
    {
        return success(samStudentService.selectSamStudentByStudentId(studentId));
    }

    /**
     * 新增学生档案
     */
    @PreAuthorize("@ss.hasPermi('student:student:add')")
    @Log(title = "学生档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SamStudent samStudent)
    {
        return toAjax(samStudentService.insertSamStudent(samStudent));
    }

    /**
     * 修改学生档案
     */
    @PreAuthorize("@ss.hasPermi('student:student:edit')")
    @Log(title = "学生档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SamStudent samStudent)
    {
        return toAjax(samStudentService.updateSamStudent(samStudent));
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
