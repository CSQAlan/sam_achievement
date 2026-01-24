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
import com.ruoyi.erp.domain.ErpReimburseRatio;
import com.ruoyi.erp.service.IErpReimburseRatioService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报销比例Controller
 * 
 * @author ruoyi
 * @date 2026-01-24
 */
@RestController
@RequestMapping("/erp/ratio")
public class ErpReimburseRatioController extends BaseController
{
    @Autowired
    private IErpReimburseRatioService erpReimburseRatioService;

    /**
     * 查询报销比例列表
     */
    @PreAuthorize("@ss.hasPermi('erp:ratio:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpReimburseRatio erpReimburseRatio)
    {
        startPage();
        List<ErpReimburseRatio> list = erpReimburseRatioService.selectErpReimburseRatioList(erpReimburseRatio);
        return getDataTable(list);
    }

    /**
     * 导出报销比例列表
     */
    @PreAuthorize("@ss.hasPermi('erp:ratio:export')")
    @Log(title = "报销比例", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpReimburseRatio erpReimburseRatio)
    {
        List<ErpReimburseRatio> list = erpReimburseRatioService.selectErpReimburseRatioList(erpReimburseRatio);
        ExcelUtil<ErpReimburseRatio> util = new ExcelUtil<ErpReimburseRatio>(ErpReimburseRatio.class);
        util.exportExcel(response, list, "报销比例数据");
    }

    /**
     * 获取报销比例详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:ratio:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpReimburseRatioService.selectErpReimburseRatioById(id));
    }

    /**
     * 新增报销比例
     */
    @PreAuthorize("@ss.hasPermi('erp:ratio:add')")
    @Log(title = "报销比例", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpReimburseRatio erpReimburseRatio)
    {
        return toAjax(erpReimburseRatioService.insertErpReimburseRatio(erpReimburseRatio));
    }

    /**
     * 修改报销比例
     */
    @PreAuthorize("@ss.hasPermi('erp:ratio:edit')")
    @Log(title = "报销比例", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpReimburseRatio erpReimburseRatio)
    {
        return toAjax(erpReimburseRatioService.updateErpReimburseRatio(erpReimburseRatio));
    }

    /**
     * 删除报销比例
     */
    @PreAuthorize("@ss.hasPermi('erp:ratio:remove')")
    @Log(title = "报销比例", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpReimburseRatioService.deleteErpReimburseRatioByIds(ids));
    }
}
