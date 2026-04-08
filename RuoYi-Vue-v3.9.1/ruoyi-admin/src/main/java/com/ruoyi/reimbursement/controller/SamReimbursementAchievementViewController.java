package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.reimbursement.domain.SamReimbursementAchievementView;
import com.ruoyi.reimbursement.service.ISamReimbursementAchievementViewService;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报销项目关联成果Controller
 * 
 * @author lwz
 * @date 2026-03-01
 */
@RestController
@RequestMapping("/reimbursement/achievement")
public class SamReimbursementAchievementViewController extends BaseController {
    
    @Autowired
    private ISamReimbursementAchievementViewService samReimbursementAchievementViewService;

    /**
     * 查询报销项目关联成果列表
     */
    @PreAuthorize("@ss.hasPermi('reimbursement:achievement:list')")
    @GetMapping("/list")
    public TableDataInfo list(SamReimbursementAchievementView samReimbursementAchievementView) {
        startPage();
        List<SamReimbursementAchievementView> list = 
            samReimbursementAchievementViewService.selectSamReimbursementAchievementViewList(samReimbursementAchievementView);
        return getDataTable(list);
    }
    
    /**
     * 根据报销项目ID查询关联成果
     */
    @PreAuthorize("@ss.hasPermi('reimbursement:achievement:list')")
    @GetMapping("/listByReimbursement/{reimbursementId}")
    public TableDataInfo listByReimbursementId(@PathVariable Long reimbursementId) {
        startPage();
        SamReimbursementAchievementView view = new SamReimbursementAchievementView();
        view.setReimbursementId(reimbursementId);
        List<SamReimbursementAchievementView> list = 
            samReimbursementAchievementViewService.selectSamReimbursementAchievementViewList(view);
        return getDataTable(list);
    }

    /**
     * 导出报销项目关联成果列表
     */
    @PreAuthorize("@ss.hasPermi('reimbursement:achievement:export')")
    @Log(title = "关联成果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,  
                      SamReimbursementAchievementView samReimbursementAchievementView) {
        List<SamReimbursementAchievementView> list = 
            samReimbursementAchievementViewService.selectSamReimbursementAchievementViewList(samReimbursementAchievementView);
        ExcelUtil<SamReimbursementAchievementView> util = 
            new ExcelUtil<SamReimbursementAchievementView>(SamReimbursementAchievementView.class);
        util.exportExcel(response, list, "关联成果数据");
    }

    /**
     * 获取报销项目关联成果详细信息
     */
    @PreAuthorize("@ss.hasPermi('reimbursement:achievement:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(samReimbursementAchievementViewService.selectSamReimbursementAchievementViewById(id));
    }

    /**
     * 新增报销项目关联成果（禁用）
     */
    @PreAuthorize("@ss.hasPermi('reimbursement:achievement:add')")
    @Log(title = "关联成果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SamReimbursementAchievementView samReimbursementAchievementView) {
        return error("该界面为只读界面，不允许新增操作");
    }

    /**
     * 修改报销项目关联成果（禁用）
     */
    @PreAuthorize("@ss.hasPermi('reimbursement:achievement:edit')")
    @Log(title = "关联成果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SamReimbursementAchievementView samReimbursementAchievementView) {
        return error("该界面为只读界面，不允许修改操作");
    }

    /**
     * 删除报销项目关联成果（禁用）
     */
    @PreAuthorize("@ss.hasPermi('reimbursement:achievement:remove')")
    @Log(title = "关联成果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return error("该界面为只读界面，不允许删除操作");
    }
}