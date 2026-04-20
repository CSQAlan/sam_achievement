package com.ruoyi.reimbursement.controller;

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
import com.ruoyi.reimbursement.domain.SamQualityDevelopmentForm;
import com.ruoyi.reimbursement.service.ISamQualityDevelopmentFormService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 素质提升Controller
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
@RestController
@RequestMapping("/system/SamQualityDevelopmentForm")
public class SamQualityDevelopmentFormController extends BaseController
{
    @Autowired
    private ISamQualityDevelopmentFormService samQualityDevelopmentFormService;

    /**
     * 查询素质提升列表
     */
    @PreAuthorize("@ss.hasPermi('system:SamQualityDevelopmentForm:list')")
    @GetMapping("/list")
    public TableDataInfo list(SamQualityDevelopmentForm samQualityDevelopmentForm)
    {
        startPage();
        List<SamQualityDevelopmentForm> list = samQualityDevelopmentFormService.selectSamQualityDevelopmentFormList(samQualityDevelopmentForm);
        return getDataTable(list);
    }

    /**
     * 导出素质提升列表
     */
    @PreAuthorize("@ss.hasPermi('system:SamQualityDevelopmentForm:export')")
    @Log(title = "素质提升", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamQualityDevelopmentForm samQualityDevelopmentForm)
    {
        List<SamQualityDevelopmentForm> list = samQualityDevelopmentFormService.selectSamQualityDevelopmentFormList(samQualityDevelopmentForm);
        ExcelUtil<SamQualityDevelopmentForm> util = new ExcelUtil<SamQualityDevelopmentForm>(SamQualityDevelopmentForm.class);
        util.exportExcel(response, list, "素质提升数据");
    }

    /**
     * 获取素质提升详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:SamQualityDevelopmentForm:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(samQualityDevelopmentFormService.selectSamQualityDevelopmentFormById(id));
    }

    /**
     * 新增素质提升
     */
    @PreAuthorize("@ss.hasPermi('system:SamQualityDevelopmentForm:add')")
    @Log(title = "素质提升", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SamQualityDevelopmentForm samQualityDevelopmentForm)
    {
        return toAjax(samQualityDevelopmentFormService.insertSamQualityDevelopmentForm(samQualityDevelopmentForm));
    }

    /**
     * 修改素质提升
     */
    @PreAuthorize("@ss.hasPermi('system:SamQualityDevelopmentForm:edit')")
    @Log(title = "素质提升", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SamQualityDevelopmentForm samQualityDevelopmentForm)
    {
        return toAjax(samQualityDevelopmentFormService.updateSamQualityDevelopmentForm(samQualityDevelopmentForm));
    }

    /**
     * 删除素质提升
     */
    @PreAuthorize("@ss.hasPermi('system:SamQualityDevelopmentForm:remove')")
    @Log(title = "素质提升", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(samQualityDevelopmentFormService.deleteSamQualityDevelopmentFormByIds(ids));
    }
}
