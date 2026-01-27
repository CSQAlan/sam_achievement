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
import com.ruoyi.erp.domain.ErpStudent;
import com.ruoyi.erp.service.IErpStudentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学生档案Controller
 * 
 * @author 王璨
 * @date 2026-01-27
 */
@RestController
@RequestMapping("/erp/student")
public class ErpStudentController extends BaseController
{
    @Autowired
    private IErpStudentService erpStudentService;

    /**
     * 查询学生档案列表
     */
    @PreAuthorize("@ss.hasPermi('erp:student:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpStudent erpStudent)
    {
        startPage();
        List<ErpStudent> list = erpStudentService.selectErpStudentList(erpStudent);
        return getDataTable(list);
    }

    /**
     * 导出学生档案列表
     */
    @PreAuthorize("@ss.hasPermi('erp:student:export')")
    @Log(title = "学生档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpStudent erpStudent)
    {
        List<ErpStudent> list = erpStudentService.selectErpStudentList(erpStudent);
        ExcelUtil<ErpStudent> util = new ExcelUtil<ErpStudent>(ErpStudent.class);
        util.exportExcel(response, list, "学生档案数据");
    }

    /**
     * 获取学生档案详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:student:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpStudentService.selectErpStudentById(id));
    }

    /**
     * 新增学生档案
     */
    @PreAuthorize("@ss.hasPermi('erp:student:add')")
    @Log(title = "学生档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpStudent erpStudent)
    {
        return toAjax(erpStudentService.insertErpStudent(erpStudent));
    }

    /**
     * 修改学生档案
     */
    @PreAuthorize("@ss.hasPermi('erp:student:edit')")
    @Log(title = "学生档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpStudent erpStudent)
    {
        return toAjax(erpStudentService.updateErpStudent(erpStudent));
    }

    /**
     * 删除学生档案
     */
    @PreAuthorize("@ss.hasPermi('erp:student:remove')")
    @Log(title = "学生档案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpStudentService.deleteErpStudentByIds(ids));
    }
}
