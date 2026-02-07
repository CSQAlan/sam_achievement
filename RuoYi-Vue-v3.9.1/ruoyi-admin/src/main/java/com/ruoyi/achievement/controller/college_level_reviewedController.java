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
import com.ruoyi.achievement.domain.college_level_reviewed;
import com.ruoyi.achievement.service.Icollege_level_reviewedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 院级已审核Controller
 * 
 * @author cyy
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/achievement/college_level_reviewed")
public class college_level_reviewedController extends BaseController
{
    @Autowired
    private Icollege_level_reviewedService college_level_reviewedService;

    /**
     * 查询院级已审核列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:list')")
    @GetMapping("/list")
    public TableDataInfo list(college_level_reviewed college_level_reviewed)
    {
        startPage();
        List<college_level_reviewed> list = college_level_reviewedService.selectcollege_level_reviewedList(college_level_reviewed);
        return getDataTable(list);
    }

    /**
     * 导出院级已审核列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:export')")
    @Log(title = "院级已审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, college_level_reviewed college_level_reviewed)
    {
        List<college_level_reviewed> list = college_level_reviewedService.selectcollege_level_reviewedList(college_level_reviewed);
        ExcelUtil<college_level_reviewed> util = new ExcelUtil<college_level_reviewed>(college_level_reviewed.class);
        util.exportExcel(response, list, "院级已审核数据");
    }

    /**
     * 获取院级已审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:query')")
    @GetMapping(value = "/{achievementId}")
    public AjaxResult getInfo(@PathVariable("achievementId") String achievementId)
    {
        return success(college_level_reviewedService.selectcollege_level_reviewedByAchievementId(achievementId));
    }

    /**
     * 新增院级已审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:add')")
    @Log(title = "院级已审核", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody college_level_reviewed college_level_reviewed)
    {
        return toAjax(college_level_reviewedService.insertcollege_level_reviewed(college_level_reviewed));
    }

    /**
     * 修改院级已审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:edit')")
    @Log(title = "院级已审核", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody college_level_reviewed college_level_reviewed)
    {
        return toAjax(college_level_reviewedService.updatecollege_level_reviewed(college_level_reviewed));
    }

    /**
     * 删除院级已审核
     */
    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:remove')")
    @Log(title = "院级已审核", businessType = BusinessType.DELETE)
	@DeleteMapping("/{achievementIds}")
    public AjaxResult remove(@PathVariable String[] achievementIds)
    {
        return toAjax(college_level_reviewedService.deletecollege_level_reviewedByAchievementIds(achievementIds));
    }
}
