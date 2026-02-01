package com.ruoyi.erp.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.erp.domain.dept_approved;
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
import com.ruoyi.erp.domain.dept_unapproved;
import com.ruoyi.erp.service.Idept_unapprovedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 院级未审核的成果Controller
 * 
 * @author cyy
 * @date 2026-01-26
 */
@RestController
@RequestMapping("/erp/dept_unapproved")
public class dept_unapprovedController extends BaseController
{
    @Autowired
    private Idept_unapprovedService dept_unapprovedService;

    /**
     * 查询院级未审核的成果列表
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_unapproved:list')")
    @GetMapping("/list")
    public TableDataInfo list(dept_unapproved dept_unapproved)
    {
        startPage();
        List<dept_unapproved> list = dept_unapprovedService.selectDeptUnpprovedByStatus(dept_unapproved);

        return getDataTable(list);
    }

    /**
     * 审核院未审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_unapproved:audit')")
    @Log(title = "院级已审核的成果", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody dept_unapproved dept_unapproved)
    {
        // 调用服务层处理审核逻辑
        boolean result = dept_unapprovedService.wait(dept_unapproved.getId(), dept_unapproved.getAuditStatus(), dept_unapproved.getDeptAuditReason());
        if (result) {
            return success("审核操作成功");
        } else {
            return error("审核操作失败");
        }
    }

    /**
     * 导出院级未审核的成果列表
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_unapproved:export')")
    @Log(title = "院级未审核的成果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, dept_unapproved dept_unapproved)
    {
        List<dept_unapproved> list = dept_unapprovedService.selectdept_unapprovedList(dept_unapproved);
        ExcelUtil<dept_unapproved> util = new ExcelUtil<dept_unapproved>(dept_unapproved.class);
        util.exportExcel(response, list, "院级未审核的成果数据");
    }

    /**
     * 获取院级未审核的成果详细信息
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_unapproved:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dept_unapprovedService.selectdept_unapprovedById(id));
    }

    /**
     * 新增院级未审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_unapproved:add')")
    @Log(title = "院级未审核的成果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody dept_unapproved dept_unapproved)
    {
        return toAjax(dept_unapprovedService.insertdept_unapproved(dept_unapproved));
    }

    /**
     * 修改院级未审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_unapproved:edit')")
    @Log(title = "院级未审核的成果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody dept_unapproved dept_unapproved)
    {
        return toAjax(dept_unapprovedService.updatedept_unapproved(dept_unapproved));
    }

    /**
     * 删除院级未审核的成果
     */
    @PreAuthorize("@ss.hasPermi('erp:dept_unapproved:remove')")
    @Log(title = "院级未审核的成果", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dept_unapprovedService.deletedept_unapprovedByIds(ids));
    }
}
