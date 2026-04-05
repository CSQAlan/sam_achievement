package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
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
import com.ruoyi.system.domain.SamReimbursementItems;
import com.ruoyi.system.service.ISamReimbursementItemsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报销项目Controller
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
@RestController
@RequestMapping("/system/SamReimbursementItems")
public class SamReimbursementItemsController extends BaseController
{
    @Autowired
    private ISamReimbursementItemsService samReimbursementItemsService;

    /**
     * 查询报销项目列表
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementItems:list')")
    @GetMapping("/list")
    public TableDataInfo list(SamReimbursementItems samReimbursementItems)
    {
        startPage();
        List<SamReimbursementItems> list = samReimbursementItemsService.selectSamReimbursementItemsList(samReimbursementItems);
        return getDataTable(list);
    }

    /**
     * 导出报销项目列表
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementItems:export')")
    @Log(title = "报销项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamReimbursementItems samReimbursementItems)
    {
        List<SamReimbursementItems> list = samReimbursementItemsService.selectSamReimbursementItemsList(samReimbursementItems);
        ExcelUtil<SamReimbursementItems> util = new ExcelUtil<SamReimbursementItems>(SamReimbursementItems.class);
        util.exportExcel(response, list, "报销项目数据");
    }

    /**
     * 获取报销项目详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementItems:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(samReimbursementItemsService.selectSamReimbursementItemsById(id));
    }

    /**
     * 新增报销项目
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementItems:add')")
    @Log(title = "报销项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SamReimbursementItems samReimbursementItems)
    {
        return toAjax(samReimbursementItemsService.insertSamReimbursementItems(samReimbursementItems));
    }

    /**
     * 修改报销项目
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementItems:edit')")
    @Log(title = "报销项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SamReimbursementItems samReimbursementItems)
    {
        return toAjax(samReimbursementItemsService.updateSamReimbursementItems(samReimbursementItems));
    }

    /**
     * 删除报销项目
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementItems:remove')")
    @Log(title = "报销项目", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(samReimbursementItemsService.deleteSamReimbursementItemsByIds(ids));
    }

    /**
     * 导出报销项目PDF清单
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementItems:export')")
    @Log(title = "报销项目", businessType = BusinessType.EXPORT)
    @GetMapping("/exportPdf/{id}")
    public void exportPdf(@PathVariable("id") Long id, HttpServletResponse response) throws IOException
    {
        samReimbursementItemsService.exportReimbursementPdf(id, response);
    }

    /**
     * 更新报销项目状态
     */
    @PreAuthorize("@ss.hasPermi('system:SamReimbursementItems:edit')")
    @Log(title = "报销项目", businessType = BusinessType.UPDATE)
    @PutMapping("/updateStatus")
    public AjaxResult updateStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String status = params.get("status").toString();
        
        SamReimbursementItems item = new SamReimbursementItems();
        item.setId(id);
        item.setStatus(status);
        return toAjax(samReimbursementItemsService.updateSamReimbursementItems(item));
    }
}
