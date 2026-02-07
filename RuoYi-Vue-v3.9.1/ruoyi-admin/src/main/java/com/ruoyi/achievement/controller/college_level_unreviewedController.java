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
import com.ruoyi.achievement.domain.college_level_unreviewed;
import com.ruoyi.achievement.service.Icollege_level_unreviewedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 院级未审核Controller
 * 
 * @author cyy
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/achievement/college_level_unreviewed")
public class college_level_unreviewedController extends BaseController
{
    @Autowired
    private Icollege_level_unreviewedService college_level_unreviewedService;

    /**
     * 查询院级未审核列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:list')")
    @GetMapping("/list")
    public TableDataInfo list(college_level_unreviewed college_level_unreviewed)
    {
        startPage();
        List<college_level_unreviewed> list = college_level_unreviewedService.selectcollege_level_unreviewedList(college_level_unreviewed);
        return getDataTable(list);
    }

    /**
     * 导出院级未审核列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:export')")
    @Log(title = "院级未审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, college_level_unreviewed college_level_unreviewed)
    {
        List<college_level_unreviewed> list = college_level_unreviewedService.selectcollege_level_unreviewedList(college_level_unreviewed);
        ExcelUtil<college_level_unreviewed> util = new ExcelUtil<college_level_unreviewed>(college_level_unreviewed.class);
        util.exportExcel(response, list, "院级未审核数据");
    }

    /**
     * 获取院级未审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:query')")
    @GetMapping(value = "/{achievementId}")
    public AjaxResult getInfo(@PathVariable("achievementId") String achievementId)
    {
        return success(college_level_unreviewedService.selectcollege_level_unreviewedByAchievementId(achievementId));
    }

    /**
     * 新增院级未审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:add')")
    @Log(title = "院级未审核", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody college_level_unreviewed college_level_unreviewed)
    {
        return toAjax(college_level_unreviewedService.insertcollege_level_unreviewed(college_level_unreviewed));
    }

    /**
     * 修改院级未审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:edit')")
    @Log(title = "院级未审核", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody college_level_unreviewed college_level_unreviewed)
    {
        return toAjax(college_level_unreviewedService.updatecollege_level_unreviewed(college_level_unreviewed));
    }

    /**
     * 删除院级未审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:remove')")
    @Log(title = "院级未审核", businessType = BusinessType.DELETE)
	@DeleteMapping("/{achievementIds}")
    public AjaxResult remove(@PathVariable String[] achievementIds)
    {
        return toAjax(college_level_unreviewedService.deletecollege_level_unreviewedByAchievementIds(achievementIds));
    }
}
