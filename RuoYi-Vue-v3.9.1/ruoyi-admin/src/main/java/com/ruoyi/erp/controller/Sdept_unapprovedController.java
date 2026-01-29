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
import com.ruoyi.erp.domain.Sdept_unapproved;
import com.ruoyi.erp.service.ISdept_unapprovedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 校级未审核的成果Controller
 *
 * @author cyy
 * @date 2026-01-26
 */
@RestController
@RequestMapping("/erp/Sdept_unapproved")
public class Sdept_unapprovedController extends BaseController
{
    @Autowired
    private ISdept_unapprovedService sdept_unapprovedService;

    /**
     * 查询校级未审核的成果列表
     * （仿照院级已审核写法：只查询“校级未审核/待校审”的成果）
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_unapproved:list')")
    @GetMapping("/list")
    public TableDataInfo list(Sdept_unapproved sdept_unapproved)
    {
        startPage();

        // 只查询校级未审核（待校审）的成果（需要你在service/mapper里实现此方法）
        List<Sdept_unapproved> list = sdept_unapprovedService.selectSdeptUnapprovedByStatus(sdept_unapproved);

        return getDataTable(list);
    }

    /**
     * 导出校级未审核的成果列表
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_unapproved:export')")
    @Log(title = "校级未审核的成果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Sdept_unapproved sdept_unapproved)
    {
        List<Sdept_unapproved> list = sdept_unapprovedService.selectSdept_unapprovedList(sdept_unapproved);
        ExcelUtil<Sdept_unapproved> util = new ExcelUtil<Sdept_unapproved>(Sdept_unapproved.class);
        util.exportExcel(response, list, "校级未审核的成果数据");
    }

    /**
     * 获取校级未审核的成果详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_unapproved:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sdept_unapprovedService.selectSdept_unapprovedById(id));
    }

    /**
     * 新增校级未审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_unapproved:add')")
    @Log(title = "校级未审核的成果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Sdept_unapproved sdept_unapproved)
    {
        return toAjax(sdept_unapprovedService.insertSdept_unapproved(sdept_unapproved));
    }

    /**
     * 修改校级未审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_unapproved:edit')")
    @Log(title = "校级未审核的成果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Sdept_unapproved sdept_unapproved)
    {
        return toAjax(sdept_unapprovedService.updateSdept_unapproved(sdept_unapproved));
    }

    /**
     * 删除校级未审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_unapproved:remove')")
    @Log(title = "校级未审核的成果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sdept_unapprovedService.deleteSdept_unapprovedByIds(ids));
    }
}
