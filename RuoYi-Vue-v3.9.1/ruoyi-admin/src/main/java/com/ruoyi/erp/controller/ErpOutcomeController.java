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
import com.ruoyi.erp.domain.ErpOutcome;
import com.ruoyi.erp.service.IErpOutcomeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 成果主Controller
 * 
 * @author 王璨
 * @date 2026-01-26
 */
@RestController
@RequestMapping("/erp/outcome")
public class ErpOutcomeController extends BaseController
{
    @Autowired
    private IErpOutcomeService erpOutcomeService;

    /**
     * 查询成果主列表
     */
    @PreAuthorize("@ss.hasPermi('erp:outcome:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpOutcome erpOutcome)
    {
        startPage();
        List<ErpOutcome> list = erpOutcomeService.selectErpOutcomeList(erpOutcome);
        return getDataTable(list);
    }

    /**
     * 导出成果主列表
     */
    @PreAuthorize("@ss.hasPermi('erp:outcome:export')")
    @Log(title = "成果主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErpOutcome erpOutcome)
    {
        List<ErpOutcome> list = erpOutcomeService.selectErpOutcomeList(erpOutcome);
        ExcelUtil<ErpOutcome> util = new ExcelUtil<ErpOutcome>(ErpOutcome.class);
        util.exportExcel(response, list, "成果主数据");
    }

    /**
     * 获取成果主详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:outcome:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(erpOutcomeService.selectErpOutcomeById(id));
    }

    /**
     * 新增成果主
     */
    @PreAuthorize("@ss.hasPermi('erp:outcome:add')")
    @Log(title = "成果主", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErpOutcome erpOutcome)
    {
        return toAjax(erpOutcomeService.insertErpOutcome(erpOutcome));
    }

    /**
     * 修改成果主
     */
    @PreAuthorize("@ss.hasPermi('erp:outcome:edit')")
    @Log(title = "成果主", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErpOutcome erpOutcome)
    {
        return toAjax(erpOutcomeService.updateErpOutcome(erpOutcome));
    }

    /**
     * 删除成果主
     */
    @PreAuthorize("@ss.hasPermi('erp:outcome:remove')")
    @Log(title = "成果主", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(erpOutcomeService.deleteErpOutcomeByIds(ids));
    }
}
