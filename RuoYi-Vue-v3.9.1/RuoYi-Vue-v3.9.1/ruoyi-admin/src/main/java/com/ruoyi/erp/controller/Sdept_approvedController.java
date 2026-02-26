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
import com.ruoyi.erp.domain.Sdept_approved;
import com.ruoyi.erp.service.ISdept_approvedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 校级已审核的成果Controller
 *
 * @author cyy
 * @date 2026-01-26
 */
@RestController
@RequestMapping("/erp/Sdept_approved")
public class Sdept_approvedController extends BaseController
{
    @Autowired
    private ISdept_approvedService sdept_approvedService;

    /**
     * 查询校级已审核的成果列表
     * 只显示“校级已审核”或“校级驳回”的成果（仿照院级已审核写法）
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_approved:list')")
    @GetMapping("/list")
    public TableDataInfo list(Sdept_approved sdept_approved)
    {
        startPage();

        // 只查询校级已审核或校级驳回的成果（需要你在service/mapper里实现此方法）
        List<Sdept_approved> list = sdept_approvedService.selectSdeptApprovedByStatus(sdept_approved);

        return getDataTable(list);
    }

    /**
     * 导出校级已审核的成果列表
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_approved:export')")
    @Log(title = "校级已审核的成果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Sdept_approved sdept_approved)
    {
        List<Sdept_approved> list = sdept_approvedService.selectSdept_approvedList(sdept_approved);
        ExcelUtil<Sdept_approved> util = new ExcelUtil<Sdept_approved>(Sdept_approved.class);
        util.exportExcel(response, list, "校级已审核的成果数据");
    }

    /**
     * 获取校级已审核的成果详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_approved:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sdept_approvedService.selectSdept_approvedById(id));
    }

    /**
     * 新增校级已审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_approved:add')")
    @Log(title = "校级已审核的成果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Sdept_approved sdept_approved)
    {
        return toAjax(sdept_approvedService.insertSdept_approved(sdept_approved));
    }

    /**
     * 修改校级已审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_approved:edit')")
    @Log(title = "校级已审核的成果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Sdept_approved sdept_approved)
    {
        return toAjax(sdept_approvedService.updateSdept_approved(sdept_approved));
    }

    /**
     * 删除校级已审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:Sdept_approved:remove')")
    @Log(title = "校级已审核的成果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sdept_approvedService.deleteSdept_approvedByIds(ids));
    }
}
