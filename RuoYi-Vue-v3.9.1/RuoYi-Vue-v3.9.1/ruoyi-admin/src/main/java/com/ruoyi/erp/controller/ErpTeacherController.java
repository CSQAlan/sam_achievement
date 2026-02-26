package com.ruoyi.erp.controller;

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
import com.ruoyi.erp.domain.ErpTeacher;
import com.ruoyi.erp.service.IErpTeacherService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 教师档案Controller
 * 
 * @author 王璨
 * @date 2026-01-27
 */
@RestController
@RequestMapping("/erp/teacher")
public class ErpTeacherController extends BaseController
{
    @Autowired
    private IErpTeacherService erpTeacherService;

    /**
     * 查询教师档案列表
     */
    @PreAuthorize("@ss.hasPermi('erp:teacher:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpTeacher erpTeacher)
    {
        startPage();
        List<ErpTeacher> list = erpTeacherService.selectErpTeacherList(erpTeacher);
        return getDataTable(list);
    }

    /**
     * 导出教师档案列表
     */
    @PreAuthorize("@ss.hasPermi('erp:teacher:export')")
    @Log(title = "教师档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpTeacher erpTeacher)
    {
        List<ErpTeacher> list = erpTeacherService.selectErpTeacherList(erpTeacher);
        ExcelUtil<ErpTeacher> util = new ExcelUtil<ErpTeacher>(ErpTeacher.class);
        util.exportExcel(response, list, "教师档案数据");
    }

    /**
     * 获取教师档案详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:teacher:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpTeacherService.selectErpTeacherById(id));
    }

    /**
     * 新增教师档案
     */
    @PreAuthorize("@ss.hasPermi('erp:teacher:add')")
    @Log(title = "教师档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpTeacher erpTeacher)
    {
        return toAjax(erpTeacherService.insertErpTeacher(erpTeacher));
    }

    /**
     * 修改教师档案
     */
    @PreAuthorize("@ss.hasPermi('erp:teacher:edit')")
    @Log(title = "教师档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpTeacher erpTeacher)
    {
        return toAjax(erpTeacherService.updateErpTeacher(erpTeacher));
    }

    /**
     * 删除教师档案
     */
    @PreAuthorize("@ss.hasPermi('erp:teacher:remove')")
    @Log(title = "教师档案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpTeacherService.deleteErpTeacherByIds(ids));
    }
}
