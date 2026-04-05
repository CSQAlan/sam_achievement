package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Sam_rem_Product;
import com.ruoyi.system.service.ISam_rem_ProductService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报销项目详情Controller
 * 
 * @author luo
 * @date 2026-03-21
 */
@RestController
@RequestMapping("/system/Reimbursement")
public class Sam_rem_ProductController extends BaseController
{
    @Autowired
    private ISam_rem_ProductService sam_rem_ProductService;

    /**
     * 测试接口 - 验证Controller是否正常工作
     */
    @GetMapping("/test")
    public AjaxResult test() {
        return AjaxResult.success("Controller is working! 报销项目详情接口正常");
    }


    /**
     * 查询报销项目详情列表
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:list')")
@GetMapping("/list")
public TableDataInfo list(Sam_rem_Product sam_rem_Product)
{
    startPage();
    List<Sam_rem_Product> list = null;
    
    // 如果传入了 reimbursementItemId，则按报销项目ID查询
    if (sam_rem_Product.getReimbursementItemId() != null && !"".equals(sam_rem_Product.getReimbursementItemId())) {
        list = sam_rem_ProductService.selectSam_rem_ProductByReimbursementItemId(sam_rem_Product.getReimbursementItemId());
    } 
    // 如果传入了 achievementId，则按成果ID查询
    else if (sam_rem_Product.getAchievementId() != null && !"".equals(sam_rem_Product.getAchievementId())) {
        list = sam_rem_ProductService.selectSam_rem_ProductList(sam_rem_Product);
    }
    // 否则查询所有
    else {
        list = sam_rem_ProductService.selectSam_rem_ProductList(sam_rem_Product);
    }
    
    return getDataTable(list);
}

    /**
     * 导出报销项目详情列表
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:export')")
    @Log(title = "报销项目详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Sam_rem_Product sam_rem_Product)
    {
        List<Sam_rem_Product> list = sam_rem_ProductService.selectSam_rem_ProductList(sam_rem_Product);
        ExcelUtil<Sam_rem_Product> util = new ExcelUtil<Sam_rem_Product>(Sam_rem_Product.class);
        util.exportExcel(response, list, "报销项目详情数据");
    }

    /**
     * 获取报销项目详情详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:query')")
    @GetMapping(value = "/{achievementId}")
    public AjaxResult getInfo(@PathVariable("achievementId") String achievementId)
    {
        return success(sam_rem_ProductService.selectSam_rem_ProductByAchievementId(achievementId));
    }

    /**
     * 新增报销项目详情
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:add')")
    @Log(title = "报销项目详情", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Sam_rem_Product sam_rem_Product)
    {
        return toAjax(sam_rem_ProductService.insertSam_rem_Product(sam_rem_Product));
    }

    /**
     * 修改报销项目详情
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:edit')")
    @Log(title = "报销项目详情", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Sam_rem_Product sam_rem_Product)
    {
        return toAjax(sam_rem_ProductService.updateSam_rem_Product(sam_rem_Product));
    }

    /**
     * 删除报销项目详情
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:remove')")
    @Log(title = "报销项目详情", businessType = BusinessType.DELETE)
	@DeleteMapping("/{achievementIds}")
    public AjaxResult remove(@PathVariable String[] achievementIds)
    {
        return toAjax(sam_rem_ProductService.deleteSam_rem_ProductByAchievementIds(achievementIds));
    }
    
    /**
     * 计算报销金额
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:calculate')")
    @Log(title = "报销项目详情", businessType = BusinessType.OTHER)
    @PostMapping("/recalculateAmount")
    public AjaxResult recalculateAmount(@RequestParam String reimbursementItemId)
    {
        return success(sam_rem_ProductService.recalculateReimbursementAmount(reimbursementItemId));
    }

    /**
     * 查询未关联的成果列表
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:list')")
    @GetMapping("/unassociatedList")
    public TableDataInfo unassociatedList(Sam_rem_Product sam_rem_Product) {
        startPage();
        List<Sam_rem_Product> list = sam_rem_ProductService.selectUnassociatedProductList(sam_rem_Product);
        return getDataTable(list);
    }

    /**
     * 批量关联成果到报销项目
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:edit')")
    @Log(title = "关联成果", businessType = BusinessType.UPDATE)
    @PostMapping("/associateAchievements")
    public AjaxResult associateAchievements(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<String> achievementIds = (List<String>) params.get("achievementIds");
            String reimbursementItemId = (String) params.get("reimbursementItemId");
            
            if (achievementIds == null || achievementIds.isEmpty()) {
                return AjaxResult.error("请选择要关联的成果");
            }
            if (reimbursementItemId == null || reimbursementItemId.isEmpty()) {
                return AjaxResult.error("报销项目ID不能为空");
            }
            
            Map<String, Object> result = sam_rem_ProductService.batchAssociateAchievements(achievementIds, reimbursementItemId);
            return AjaxResult.success("成功关联 " + result.get("successCount") + " 个成果", result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 取消关联成果
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:edit')")
    @Log(title = "取消关联成果", businessType = BusinessType.UPDATE)
    @PostMapping("/cancelAssociation")
    public AjaxResult cancelAssociation(@RequestBody Map<String, Object> params) {
        try {
            String achievementId = (String) params.get("achievementId");
            String reimbursementItemId = (String) params.get("reimbursementItemId");
            
            if (achievementId == null || achievementId.isEmpty()) {
                return AjaxResult.error("成果ID不能为空");
            }
            if (reimbursementItemId == null || reimbursementItemId.isEmpty()) {
                return AjaxResult.error("报销项目ID不能为空");
            }
            
            Map<String, Object> result = sam_rem_ProductService.cancelAssociation(achievementId, reimbursementItemId);
            return AjaxResult.success("取消关联成功", result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 批量取消关联成果
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:edit')")
    @Log(title = "批量取消关联成果", businessType = BusinessType.UPDATE)
    @PostMapping("/batchCancelAssociation")
    public AjaxResult batchCancelAssociation(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<String> achievementIds = (List<String>) params.get("achievementIds");
            String reimbursementItemId = (String) params.get("reimbursementItemId");
            
            if (achievementIds == null || achievementIds.isEmpty()) {
                return AjaxResult.error("请选择要取消关联的成果");
            }
            if (reimbursementItemId == null || reimbursementItemId.isEmpty()) {
                return AjaxResult.error("报销项目ID不能为空");
            }
            
            Map<String, Object> result = sam_rem_ProductService.batchCancelAssociation(achievementIds, reimbursementItemId);
            return AjaxResult.success("成功取消关联 " + result.get("successCount") + " 个成果", result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取成果的参赛选手
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:query')")
    @GetMapping("/participants/{achievementId}")
    public AjaxResult getParticipants(@PathVariable("achievementId") String achievementId) {
        Long id = Long.valueOf(achievementId);
        return success(sam_rem_ProductService.selectParticipantsByAchievementId(id));
    }

    /**
     * 获取成果的指导老师
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:query')")
    @GetMapping("/advisors/{achievementId}")
    public AjaxResult getAdvisors(@PathVariable("achievementId") String achievementId) {
        Long id = Long.valueOf(achievementId);
        return success(sam_rem_ProductService.selectAdvisorsByAchievementId(id));
    }

    /**
     * 获取成果的附件
     */
    @PreAuthorize("@ss.hasPermi('system:Reimbursement:query')")
    @GetMapping("/attachments/{achievementId}")
    public AjaxResult getAttachments(@PathVariable("achievementId") String achievementId) {
        Long id = Long.valueOf(achievementId);
        return success(sam_rem_ProductService.selectAttachmentsByAchievementId(id));
    }
}

