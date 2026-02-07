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
import com.ruoyi.achievement.domain.school_level_reviewed;
import com.ruoyi.achievement.service.Ischool_level_reviewedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 校级已审核Controller
 * 
 * @author cyy
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/achievement/school_level_reviewed")
public class school_level_reviewedController extends BaseController
{
    @Autowired
    private Ischool_level_reviewedService school_level_reviewedService;

    /**
     * 查询校级已审核列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:list')")
    @GetMapping("/list")
    public TableDataInfo list(school_level_reviewed school_level_reviewed)
    {
        startPage();
        List<school_level_reviewed> list = school_level_reviewedService.selectschool_level_reviewedList(school_level_reviewed);
        return getDataTable(list);
    }

    /**
     * 导出校级已审核列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:export')")
    @Log(title = "校级已审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, school_level_reviewed school_level_reviewed)
    {
        List<school_level_reviewed> list = school_level_reviewedService.selectschool_level_reviewedList(school_level_reviewed);
        ExcelUtil<school_level_reviewed> util = new ExcelUtil<school_level_reviewed>(school_level_reviewed.class);
        util.exportExcel(response, list, "校级已审核数据");
    }

    /**
     * 获取校级已审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:query')")
    @GetMapping(value = "/{achievementId}")
    public AjaxResult getInfo(@PathVariable("achievementId") String achievementId)
    {
        return success(school_level_reviewedService.selectschool_level_reviewedByAchievementId(achievementId));
    }

    /**
     * 新增校级已审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:add')")
    @Log(title = "校级已审核", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody school_level_reviewed school_level_reviewed)
    {
        return toAjax(school_level_reviewedService.insertschool_level_reviewed(school_level_reviewed));
    }

    /**
     * 修改校级已审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:edit')")
    @Log(title = "校级已审核", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody school_level_reviewed school_level_reviewed)
    {
        return toAjax(school_level_reviewedService.updateschool_level_reviewed(school_level_reviewed));
    }

    /**
     * 删除校级已审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:remove')")
    @Log(title = "校级已审核", businessType = BusinessType.DELETE)
	@DeleteMapping("/{achievementIds}")
    public AjaxResult remove(@PathVariable String[] achievementIds)
    {
        return toAjax(school_level_reviewedService.deleteschool_level_reviewedByAchievementIds(achievementIds));
    }
}
