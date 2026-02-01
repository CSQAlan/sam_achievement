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
import com.ruoyi.erp.domain.dept_approved;
import com.ruoyi.erp.service.Idept_approvedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 院级已审核的成果Controller
 * 
 * @author cyy
 * @date 2026-01-26
 */
@RestController
@RequestMapping("/erp/dept_approved")
public class dept_approvedController extends BaseController
{
    @Autowired
    private Idept_approvedService dept_approvedService;

    /**
     * 查询院级已审核的成果列表
     * 只显示系部已审核或系部驳回的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_approved:list')")
    @GetMapping("/list")
    public TableDataInfo list(dept_approved dept_approved)
    {
        startPage();

        // 只查询系部已审核或系部驳回的成果
        List<dept_approved> list = dept_approvedService.selectDeptApprovedByStatus(dept_approved);

        return getDataTable(list);
    }

    /**
     * 审核院级已审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_approved:audit')")
    @Log(title = "院级已审核的成果", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody dept_approved dept_approved)
    {
        // 调用服务层处理审核逻辑
        boolean result = dept_approvedService.wait(dept_approved.getId(), dept_approved.getAuditStatus(), dept_approved.getDeptAuditReason());
        if (result) {
            return success("审核操作成功");
        } else {
            return error("审核操作失败");
        }
    }

    /**
     * 导出院级已审核的成果列表
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_approved:export')")
    @Log(title = "院级已审核的成果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, dept_approved dept_approved)
    {
        List<dept_approved> list = dept_approvedService.selectdept_approvedList(dept_approved);
        ExcelUtil<dept_approved> util = new ExcelUtil<dept_approved>(dept_approved.class);
        util.exportExcel(response, list, "院级已审核的成果数据");
    }

    /**
     * 获取院级已审核的成果详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_approved:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dept_approvedService.selectdept_approvedById(id));
    }

    /**
     * 新增院级已审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_approved:add')")
    @Log(title = "院级已审核的成果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody dept_approved dept_approved)
    {
        return toAjax(dept_approvedService.insertdept_approved(dept_approved));
    }

    /**
     * 修改院级已审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_approved:edit')")
    @Log(title = "院级已审核的成果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody dept_approved dept_approved)
    {
        return toAjax(dept_approvedService.updatedept_approved(dept_approved));
    }

    /**
     * 删除院级已审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_approved:remove')")
    @Log(title = "院级已审核的成果", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dept_approvedService.deletedept_approvedByIds(ids));
    }
}
