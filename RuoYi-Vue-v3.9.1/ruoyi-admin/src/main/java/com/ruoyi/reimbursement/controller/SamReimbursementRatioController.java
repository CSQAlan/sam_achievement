package com.ruoyi.reimbursement.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.reimbursement.service.ISamReimbursementRatioService;
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
import com.ruoyi.reimbursement.domain.SamReimbursementRatio;
import com.ruoyi.reimbursement.service.ISamReimbursementRatioService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报销比例Controller
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
@RestController
@RequestMapping("/system/SamReimbursementRatio")
public class SamReimbursementRatioController extends BaseController
{
    @Autowired
    private ISamReimbursementRatioService samReimbursementRatioService;

    /**
     * 查询报销比例列表
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementRatio:list')")
    @GetMapping("/list")
    public TableDataInfo list(SamReimbursementRatio samReimbursementRatio)
    {
        startPage();
        List<SamReimbursementRatio> list = samReimbursementRatioService.selectSamReimbursementRatioList(samReimbursementRatio);
        return getDataTable(list);
    }

    /**
     * 导出报销比例列表
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementRatio:export')")
    @Log(title = "报销比例", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamReimbursementRatio samReimbursementRatio)
    {
        List<SamReimbursementRatio> list = samReimbursementRatioService.selectSamReimbursementRatioList(samReimbursementRatio);
        ExcelUtil<SamReimbursementRatio> util = new ExcelUtil<SamReimbursementRatio>(SamReimbursementRatio.class);
        util.exportExcel(response, list, "报销比例数据");
    }

    /**
     * 获取报销比例详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementRatio:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(samReimbursementRatioService.selectSamReimbursementRatioById(id));
    }

    /**
     * 新增报销比例
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementRatio:add')")
    @Log(title = "报销比例", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SamReimbursementRatio samReimbursementRatio)
    {
        return toAjax(samReimbursementRatioService.insertSamReimbursementRatio(samReimbursementRatio));
    }

    /**
     * 修改报销比例
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementRatio:edit')")
    @Log(title = "报销比例", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SamReimbursementRatio samReimbursementRatio)
    {
        return toAjax(samReimbursementRatioService.updateSamReimbursementRatio(samReimbursementRatio));
    }

    /**
     * 删除报销比例
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementRatio:remove')")
    @Log(title = "报销比例", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(samReimbursementRatioService.deleteSamReimbursementRatioByIds(ids));
    }
}
