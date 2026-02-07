package com.ruoyi.competitionapply.controller;

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
import com.ruoyi.competitionapply.domain.CompetitionApply;
import com.ruoyi.competitionapply.service.ICompetitionApplyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 赛事申请Controller
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
@RestController
@RequestMapping("/competition-apply/competitionapply")
public class CompetitionApplyController extends BaseController
{
    @Autowired
    private ICompetitionApplyService competitionApplyService;

    /**
     * 查询赛事申请列表
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:list')")
    @GetMapping("/list")
    public TableDataInfo list(CompetitionApply competitionApply)
    {
        startPage();
        List<CompetitionApply> list = competitionApplyService.selectCompetitionApplyList(competitionApply);
        return getDataTable(list);
    }

    /**
     * 导出赛事申请列表
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:export')")
    @Log(title = "赛事申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CompetitionApply competitionApply)
    {
        List<CompetitionApply> list = competitionApplyService.selectCompetitionApplyList(competitionApply);
        ExcelUtil<CompetitionApply> util = new ExcelUtil<CompetitionApply>(CompetitionApply.class);
        util.exportExcel(response, list, "赛事申请数据");
    }

    /**
     * 获取赛事申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(competitionApplyService.selectCompetitionApplyById(id));
    }

    /**
     * 新增赛事申请
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:add')")
    @Log(title = "赛事申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CompetitionApply competitionApply)
    {
        return toAjax(competitionApplyService.insertCompetitionApply(competitionApply));
    }

    /**
     * 修改赛事申请
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:edit')")
    @Log(title = "赛事申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CompetitionApply competitionApply)
    {
        return toAjax(competitionApplyService.updateCompetitionApply(competitionApply));
    }

    /**
     * 删除赛事申请
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:remove')")
    @Log(title = "赛事申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(competitionApplyService.deleteCompetitionApplyByIds(ids));
    }
}
