package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.BizAuditLog;
import com.ruoyi.system.service.IBizAuditLogService;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 高级业务审计日志
 */
@RestController
@RequestMapping("/monitor/bizAudit")
public class BizAuditLogController extends BaseController
{
    @Autowired
    private IBizAuditLogService bizAuditLogService;

    @PreAuthorize("@ss.hasPermi('monitor:bizaudit:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizAuditLog log)
    {
        startPage();
        List<BizAuditLog> list = bizAuditLogService.selectBizAuditLogList(log);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('monitor:bizaudit:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        AjaxResult ajax = success();
        ajax.put("log", bizAuditLogService.selectBizAuditLogById(id));
        ajax.put("details", bizAuditLogService.selectBizAuditLogDetailListByLogId(id));
        return ajax;
    }

    @Log(title = "业务审计日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:bizaudit:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizAuditLog log)
    {
        List<BizAuditLog> list = bizAuditLogService.selectBizAuditLogList(log);
        ExcelUtil<BizAuditLog> util = new ExcelUtil<BizAuditLog>(BizAuditLog.class);
        util.exportExcel(response, list, "业务审计日志");
    }

    @Log(title = "业务审计日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('monitor:bizaudit:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizAuditLogService.deleteBizAuditLogByIds(ids));
    }

    @Log(title = "业务审计日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('monitor:bizaudit:remove')")
    @DeleteMapping("/clean")
    public AjaxResult clean(@RequestParam(value = "days", required = false, defaultValue = "30") Integer days)
    {
        return success("clean success, affected=" + bizAuditLogService.cleanBizAuditLogBeforeDays(days));
    }
}
