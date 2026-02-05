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
import com.ruoyi.achievement.domain.reviewed;
import com.ruoyi.achievement.service.IreviewedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/achievement")
public class reviewedController extends BaseController
{
    @Autowired
    private IreviewedService reviewedService;

    private void applyFilter(reviewed reviewed, String stage, String status)
    {
        reviewed.setReviewStage(stage);
        reviewed.setReviewStatus(status);
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:list')")
    @GetMapping("/college_level_unreviewed/list")
    public TableDataInfo listCollegeUnreviewed(reviewed reviewed)
    {
        applyFilter(reviewed, "college", "unreviewed");
        startPage();
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:export')")
    @Log(title = "College Unreviewed", businessType = BusinessType.EXPORT)
    @PostMapping("/college_level_unreviewed/export")
    public void exportCollegeUnreviewed(HttpServletResponse response, reviewed reviewed)
    {
        applyFilter(reviewed, "college", "unreviewed");
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        ExcelUtil<reviewed> util = new ExcelUtil<reviewed>(reviewed.class);
        util.exportExcel(response, list, "College Unreviewed");
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:query')")
    @GetMapping(value = "/college_level_unreviewed/{achievementId}")
    public AjaxResult getCollegeUnreviewed(@PathVariable("achievementId") String achievementId)
    {
        return success(reviewedService.selectreviewedByAchievementId(achievementId));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:add')")
    @Log(title = "College Unreviewed", businessType = BusinessType.INSERT)
    @PostMapping("/college_level_unreviewed")
    public AjaxResult addCollegeUnreviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "college", "unreviewed");
        return toAjax(reviewedService.insertreviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:edit')")
    @Log(title = "College Unreviewed", businessType = BusinessType.UPDATE)
    @PutMapping("/college_level_unreviewed")
    public AjaxResult editCollegeUnreviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "college", "unreviewed");
        return toAjax(reviewedService.updatereviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:remove')")
    @Log(title = "College Unreviewed", businessType = BusinessType.DELETE)
    @DeleteMapping("/college_level_unreviewed/{achievementIds}")
    public AjaxResult removeCollegeUnreviewed(@PathVariable String[] achievementIds)
    {
        return toAjax(reviewedService.deletereviewedByAchievementIds(achievementIds));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:list')")
    @GetMapping("/college_level_reviewed/list")
    public TableDataInfo listCollegeReviewed(reviewed reviewed)
    {
        applyFilter(reviewed, "college", "reviewed");
        startPage();
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:export')")
    @Log(title = "College Reviewed", businessType = BusinessType.EXPORT)
    @PostMapping("/college_level_reviewed/export")
    public void exportCollegeReviewed(HttpServletResponse response, reviewed reviewed)
    {
        applyFilter(reviewed, "college", "reviewed");
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        ExcelUtil<reviewed> util = new ExcelUtil<reviewed>(reviewed.class);
        util.exportExcel(response, list, "College Reviewed");
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:query')")
    @GetMapping(value = "/college_level_reviewed/{achievementId}")
    public AjaxResult getCollegeReviewed(@PathVariable("achievementId") String achievementId)
    {
        return success(reviewedService.selectreviewedByAchievementId(achievementId));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:add')")
    @Log(title = "College Reviewed", businessType = BusinessType.INSERT)
    @PostMapping("/college_level_reviewed")
    public AjaxResult addCollegeReviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "college", "reviewed");
        return toAjax(reviewedService.insertreviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:edit')")
    @Log(title = "College Reviewed", businessType = BusinessType.UPDATE)
    @PutMapping("/college_level_reviewed")
    public AjaxResult editCollegeReviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "college", "reviewed");
        return toAjax(reviewedService.updatereviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:remove')")
    @Log(title = "College Reviewed", businessType = BusinessType.DELETE)
    @DeleteMapping("/college_level_reviewed/{achievementIds}")
    public AjaxResult removeCollegeReviewed(@PathVariable String[] achievementIds)
    {
        return toAjax(reviewedService.deletereviewedByAchievementIds(achievementIds));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:list')")
    @GetMapping("/school_level_unreviewed/list")
    public TableDataInfo listSchoolUnreviewed(reviewed reviewed)
    {
        applyFilter(reviewed, "school", "unreviewed");
        startPage();
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:export')")
    @Log(title = "School Unreviewed", businessType = BusinessType.EXPORT)
    @PostMapping("/school_level_unreviewed/export")
    public void exportSchoolUnreviewed(HttpServletResponse response, reviewed reviewed)
    {
        applyFilter(reviewed, "school", "unreviewed");
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        ExcelUtil<reviewed> util = new ExcelUtil<reviewed>(reviewed.class);
        util.exportExcel(response, list, "School Unreviewed");
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:query')")
    @GetMapping(value = "/school_level_unreviewed/{achievementId}")
    public AjaxResult getSchoolUnreviewed(@PathVariable("achievementId") String achievementId)
    {
        return success(reviewedService.selectreviewedByAchievementId(achievementId));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:add')")
    @Log(title = "School Unreviewed", businessType = BusinessType.INSERT)
    @PostMapping("/school_level_unreviewed")
    public AjaxResult addSchoolUnreviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "school", "unreviewed");
        return toAjax(reviewedService.insertreviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:edit')")
    @Log(title = "School Unreviewed", businessType = BusinessType.UPDATE)
    @PutMapping("/school_level_unreviewed")
    public AjaxResult editSchoolUnreviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "school", "unreviewed");
        return toAjax(reviewedService.updatereviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:remove')")
    @Log(title = "School Unreviewed", businessType = BusinessType.DELETE)
    @DeleteMapping("/school_level_unreviewed/{achievementIds}")
    public AjaxResult removeSchoolUnreviewed(@PathVariable String[] achievementIds)
    {
        return toAjax(reviewedService.deletereviewedByAchievementIds(achievementIds));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:list')")
    @GetMapping("/school_level_reviewed/list")
    public TableDataInfo listSchoolReviewed(reviewed reviewed)
    {
        applyFilter(reviewed, "school", "reviewed");
        startPage();
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:export')")
    @Log(title = "School Reviewed", businessType = BusinessType.EXPORT)
    @PostMapping("/school_level_reviewed/export")
    public void exportSchoolReviewed(HttpServletResponse response, reviewed reviewed)
    {
        applyFilter(reviewed, "school", "reviewed");
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        ExcelUtil<reviewed> util = new ExcelUtil<reviewed>(reviewed.class);
        util.exportExcel(response, list, "School Reviewed");
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:query')")
    @GetMapping(value = "/school_level_reviewed/{achievementId}")
    public AjaxResult getSchoolReviewed(@PathVariable("achievementId") String achievementId)
    {
        return success(reviewedService.selectreviewedByAchievementId(achievementId));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:add')")
    @Log(title = "School Reviewed", businessType = BusinessType.INSERT)
    @PostMapping("/school_level_reviewed")
    public AjaxResult addSchoolReviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "school", "reviewed");
        return toAjax(reviewedService.insertreviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:edit')")
    @Log(title = "School Reviewed", businessType = BusinessType.UPDATE)
    @PutMapping("/school_level_reviewed")
    public AjaxResult editSchoolReviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "school", "reviewed");
        return toAjax(reviewedService.updatereviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:remove')")
    @Log(title = "School Reviewed", businessType = BusinessType.DELETE)
    @DeleteMapping("/school_level_reviewed/{achievementIds}")
    public AjaxResult removeSchoolReviewed(@PathVariable String[] achievementIds)
    {
        return toAjax(reviewedService.deletereviewedByAchievementIds(achievementIds));
    }
}
