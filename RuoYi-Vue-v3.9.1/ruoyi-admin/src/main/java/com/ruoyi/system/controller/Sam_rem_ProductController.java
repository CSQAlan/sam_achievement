package com.ruoyi.system.controller;

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
}

