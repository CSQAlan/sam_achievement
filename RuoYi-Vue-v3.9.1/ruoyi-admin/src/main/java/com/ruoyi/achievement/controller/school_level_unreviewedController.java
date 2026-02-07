package com.ruoyi.achievement.controller;

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
import com.ruoyi.achievement.domain.school_level_unreviewed;
import com.ruoyi.achievement.service.Ischool_level_unreviewedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 校级未审核Controller
 * 
 * @author cyy
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/achievement/school_level_unreviewed")
public class school_level_unreviewedController extends BaseController
{
    @Autowired
    private Ischool_level_unreviewedService school_level_unreviewedService;

    /**
     * 查询校级未审核列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:list')")
    @GetMapping("/list")
    public TableDataInfo list(school_level_unreviewed school_level_unreviewed)
    {
        startPage();
        List<school_level_unreviewed> list = school_level_unreviewedService.selectschool_level_unreviewedList(school_level_unreviewed);
        return getDataTable(list);
    }

    /**
     * 导出校级未审核列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:export')")
    @Log(title = "校级未审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, school_level_unreviewed school_level_unreviewed)
    {
        List<school_level_unreviewed> list = school_level_unreviewedService.selectschool_level_unreviewedList(school_level_unreviewed);
        ExcelUtil<school_level_unreviewed> util = new ExcelUtil<school_level_unreviewed>(school_level_unreviewed.class);
        util.exportExcel(response, list, "校级未审核数据");
    }

    /**
     * 获取校级未审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:query')")
    @GetMapping(value = "/{achievementId}")
    public AjaxResult getInfo(@PathVariable("achievementId") String achievementId)
    {
        return success(school_level_unreviewedService.selectschool_level_unreviewedByAchievementId(achievementId));
    }

    /**
     * 新增校级未审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:add')")
    @Log(title = "校级未审核", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody school_level_unreviewed school_level_unreviewed)
    {
        return toAjax(school_level_unreviewedService.insertschool_level_unreviewed(school_level_unreviewed));
    }

    /**
     * 修改校级未审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:edit')")
    @Log(title = "校级未审核", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody school_level_unreviewed school_level_unreviewed)
    {
        return toAjax(school_level_unreviewedService.updateschool_level_unreviewed(school_level_unreviewed));
    }

    /**
     * 删除校级未审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:remove')")
    @Log(title = "校级未审核", businessType = BusinessType.DELETE)
	@DeleteMapping("/{achievementIds}")
    public AjaxResult remove(@PathVariable String[] achievementIds)
    {
        return toAjax(school_level_unreviewedService.deleteschool_level_unreviewedByAchievementIds(achievementIds));
    }
}
