package com.ruoyi.achievement.controller;

import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.domain.reviewed;
import com.ruoyi.achievement.service.ISamAchievementService;
import com.ruoyi.achievement.service.IreviewedService;
import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 成果审核 Controller。
 */
@RestController
@RequestMapping("/achievement")
public class reviewedController extends BaseController
{
    @Autowired
    private IreviewedService reviewedService;

    @Autowired
    private ISamAchievementService samAchievementService;

    public static class BatchReviewStatusRequest
    {
        private String source;
        private String[] achievementIds;
        private Long reviewStatus;
        private String rejectReason;

        public String getSource()
        {
            return source;
        }

        public void setSource(String source)
        {
            this.source = source;
        }

        public String[] getAchievementIds()
        {
            return achievementIds;
        }

        public void setAchievementIds(String[] achievementIds)
        {
            this.achievementIds = achievementIds;
        }

        public Long getReviewStatus()
        {
            return reviewStatus;
        }

        public void setReviewStatus(Long reviewStatus)
        {
            this.reviewStatus = reviewStatus;
        }

        public String getRejectReason()
        {
            return rejectReason;
        }

        public void setRejectReason(String rejectReason)
        {
            this.rejectReason = rejectReason;
        }
    }

    public static class ReviewSubmitRequest
    {
        private String source;
        private String achievementId;
        private Long reviewStatus;
        private String rejectReason;

        public String getSource()
        {
            return source;
        }

        public void setSource(String source)
        {
            this.source = source;
        }

        public String getAchievementId()
        {
            return achievementId;
        }

        public void setAchievementId(String achievementId)
        {
            this.achievementId = achievementId;
        }

        public Long getReviewStatus()
        {
            return reviewStatus;
        }

        public void setReviewStatus(Long reviewStatus)
        {
            this.reviewStatus = reviewStatus;
        }

        public String getRejectReason()
        {
            return rejectReason;
        }

        public void setRejectReason(String rejectReason)
        {
            this.rejectReason = rejectReason;
        }
    }

    /**
     * 在审核页保存表单时，不改变当前审核状态。
     */
    private AjaxResult saveReviewForm(SamAchievement samAchievement)
    {
        SamAchievement existing = samAchievementService.selectSamAchievementByAchievementId(samAchievement.getAchievementId());
        if (existing == null)
        {
            return AjaxResult.error("未找到对应的成果信息");
        }

        samAchievement.setReviewResult(existing.getReviewResult());
        samAchievement.setSchooiReviewResult(existing.getSchooiReviewResult());
        samAchievement.setReviewedAt(existing.getReviewedAt());
        samAchievement.setSchoolReviewedAt(existing.getSchoolReviewedAt());
        samAchievement.setReviewReason(existing.getReviewReason());
        samAchievement.setSchoolReviewReason(existing.getSchoolReviewReason());
        samAchievement.setAuditBy(existing.getAuditBy());
        samAchievement.setSchoolAuditBy(existing.getSchoolAuditBy());
        samAchievement.setSubmittedAt(existing.getSubmittedAt());

        return toAjax(samAchievementService.updateSamAchievement(samAchievement));
    }

    private void initCollegeUnreviewedForInsert(SamAchievement samAchievement)
    {
        samAchievement.setReviewResult(0L);
        samAchievement.setSchooiReviewResult(null);
        samAchievement.setReviewedAt(null);
        samAchievement.setSchoolReviewedAt(null);
        samAchievement.setReviewReason(null);
        samAchievement.setSchoolReviewReason(null);
        samAchievement.setAuditBy(null);
        samAchievement.setSchoolAuditBy(null);
    }

    private void initSchoolUnreviewedForInsert(SamAchievement samAchievement)
    {
        samAchievement.setReviewResult(2L);
        samAchievement.setSchooiReviewResult(2L);
        samAchievement.setReviewedAt(null);
        samAchievement.setSchoolReviewedAt(null);
        samAchievement.setReviewReason(null);
        samAchievement.setSchoolReviewReason(null);
        samAchievement.setAuditBy(null);
        samAchievement.setSchoolAuditBy(null);
    }

    private void applyFilter(reviewed reviewed, String stage, String status)
    {
        reviewed.setReviewStage(stage);
        reviewed.setReviewStatus(status);
    }

    private AjaxResult batchUpdateStatus(String source, BatchReviewStatusRequest request)
    {
        if (request == null)
        {
            return error("请求参数不能为空");
        }
        return toAjax(reviewedService.batchSubmitReview(source, request.getAchievementIds(), request.getReviewStatus(), request.getRejectReason()));
    }

    private void checkReviewEditPermission(String source)
    {
        String permission = resolveReviewEditPermission(source);
        if (StringUtils.isEmpty(permission))
        {
            throw new ServiceException("无法识别审核来源");
        }
        if (!SecurityUtils.hasPermi(permission))
        {
            throw new AccessDeniedException("没有权限进行当前审核阶段的操作");
        }
    }

    private String resolveReviewEditPermission(String source)
    {
        String normalizedSource = StringUtils.hasText(source) ? source.trim().toLowerCase() : "";
        switch (normalizedSource)
        {
            case "college_level_unreviewed":
                return "achievement:college_level_unreviewed:edit";
            case "college_level_reviewed":
                return "achievement:college_level_reviewed:edit";
            case "school_level_unreviewed":
                return "achievement:school_level_unreviewed:edit";
            case "school_level_reviewed":
                return "achievement:school_level_reviewed:edit";
            default:
                return "";
        }
    }

    @PreAuthorize(
            "@ss.hasPermi('achievement:college_level_unreviewed:edit')"
                    + " or @ss.hasPermi('achievement:college_level_reviewed:edit')"
                    + " or @ss.hasPermi('achievement:school_level_unreviewed:edit')"
                    + " or @ss.hasPermi('achievement:school_level_reviewed:edit')")
    @Log(title = "成果审核提交", businessType = BusinessType.UPDATE)
    @PutMapping("/review/submit")
    public AjaxResult submitReview(@RequestBody ReviewSubmitRequest request)
    {
        if (request == null)
        {
            return error("请求参数不能为空");
        }
        checkReviewEditPermission(request.getSource());
        return success(reviewedService.submitReview(
                request.getSource(),
                request.getAchievementId(),
                request.getReviewStatus(),
                request.getRejectReason()));
    }

    @PreAuthorize(
            "@ss.hasPermi('achievement:college_level_unreviewed:edit')"
                    + " or @ss.hasPermi('achievement:college_level_reviewed:edit')"
                    + " or @ss.hasPermi('achievement:school_level_unreviewed:edit')"
                    + " or @ss.hasPermi('achievement:school_level_reviewed:edit')")
    @Log(title = "成果审核批量提交", businessType = BusinessType.UPDATE)
    @PutMapping("/review/batchSubmit")
    public AjaxResult batchSubmitReview(@RequestBody BatchReviewStatusRequest request)
    {
        if (request == null)
        {
            return error("请求参数不能为空");
        }
        checkReviewEditPermission(request.getSource());
        return toAjax(reviewedService.batchSubmitReview(
                request.getSource(),
                request.getAchievementIds(),
                request.getReviewStatus(),
                request.getRejectReason()));
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
    @BizAudit(bizType = "achievement_review", bizName = "导出院级未审核成果", opType = BizAuditOpType.EXPORT, handler = "achievementBizAuditHandler")
    @Log(title = "院级未审核成果", businessType = BusinessType.EXPORT)
    @PostMapping("/college_level_unreviewed/export")
    public void exportCollegeUnreviewed(HttpServletResponse response, reviewed reviewed)
    {
        applyFilter(reviewed, "college", "unreviewed");
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        ExcelUtil<reviewed> util = new ExcelUtil<reviewed>(reviewed.class);
        util.exportExcel(response, list, "院级未审核成果");
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:query')")
    @GetMapping(value = "/college_level_unreviewed/{achievementId}")
    public AjaxResult getCollegeUnreviewed(@PathVariable("achievementId") String achievementId)
    {
        return success(samAchievementService.selectSamAchievementByAchievementId(achievementId));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:add')")
    @Log(title = "院级未审核成果", businessType = BusinessType.INSERT)
    @PostMapping("/college_level_unreviewed")
    public AjaxResult addCollegeUnreviewed(@RequestBody SamAchievement samAchievement)
    {
        initCollegeUnreviewedForInsert(samAchievement);
        return toAjax(samAchievementService.insertSamAchievement(samAchievement));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:edit')")
    @Log(title = "院级未审核成果", businessType = BusinessType.UPDATE)
    @PutMapping("/college_level_unreviewed")
    public AjaxResult editCollegeUnreviewed(@RequestBody SamAchievement samAchievement)
    {
        return saveReviewForm(samAchievement);
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:edit')")
    @Log(title = "院级未审核成果批量审核", businessType = BusinessType.UPDATE)
    @PutMapping("/college_level_unreviewed/batchReviewStatus")
    public AjaxResult batchCollegeUnreviewedStatus(@RequestBody BatchReviewStatusRequest request)
    {
        return batchUpdateStatus("college_level_unreviewed", request);
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_unreviewed:remove')")
    @Log(title = "院级未审核成果", businessType = BusinessType.DELETE)
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
    @BizAudit(bizType = "achievement_review", bizName = "导出院级已审核成果", opType = BizAuditOpType.EXPORT, handler = "achievementBizAuditHandler")
    @Log(title = "院级已审核成果", businessType = BusinessType.EXPORT)
    @PostMapping("/college_level_reviewed/export")
    public void exportCollegeReviewed(HttpServletResponse response, reviewed reviewed)
    {
        applyFilter(reviewed, "college", "reviewed");
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        ExcelUtil<reviewed> util = new ExcelUtil<reviewed>(reviewed.class);
        util.exportExcel(response, list, "院级已审核成果");
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:query')")
    @GetMapping(value = "/college_level_reviewed/{achievementId}")
    public AjaxResult getCollegeReviewed(@PathVariable("achievementId") String achievementId)
    {
        return success(samAchievementService.selectSamAchievementByAchievementId(achievementId));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:add')")
    @Log(title = "院级已审核成果", businessType = BusinessType.INSERT)
    @PostMapping("/college_level_reviewed")
    public AjaxResult addCollegeReviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "college", "reviewed");
        return toAjax(reviewedService.insertreviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:edit')")
    @Log(title = "院级已审核成果", businessType = BusinessType.UPDATE)
    @PutMapping("/college_level_reviewed")
    public AjaxResult editCollegeReviewed(@RequestBody SamAchievement samAchievement)
    {
        return saveReviewForm(samAchievement);
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:edit')")
    @Log(title = "院级已审核成果批量审核", businessType = BusinessType.UPDATE)
    @PutMapping("/college_level_reviewed/batchReviewStatus")
    public AjaxResult batchCollegeReviewedStatus(@RequestBody BatchReviewStatusRequest request)
    {
        return batchUpdateStatus("college_level_reviewed", request);
    }

    @PreAuthorize("@ss.hasPermi('achievement:college_level_reviewed:remove')")
    @Log(title = "院级已审核成果", businessType = BusinessType.DELETE)
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
    @BizAudit(bizType = "achievement_review", bizName = "导出校级未审核成果", opType = BizAuditOpType.EXPORT, handler = "achievementBizAuditHandler")
    @Log(title = "校级未审核成果", businessType = BusinessType.EXPORT)
    @PostMapping("/school_level_unreviewed/export")
    public void exportSchoolUnreviewed(HttpServletResponse response, reviewed reviewed)
    {
        applyFilter(reviewed, "school", "unreviewed");
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        ExcelUtil<reviewed> util = new ExcelUtil<reviewed>(reviewed.class);
        util.exportExcel(response, list, "校级未审核成果");
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:query')")
    @GetMapping(value = "/school_level_unreviewed/{achievementId}")
    public AjaxResult getSchoolUnreviewed(@PathVariable("achievementId") String achievementId)
    {
        return success(samAchievementService.selectSamAchievementByAchievementId(achievementId));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:add')")
    @Log(title = "校级未审核成果", businessType = BusinessType.INSERT)
    @PostMapping("/school_level_unreviewed")
    public AjaxResult addSchoolUnreviewed(@RequestBody SamAchievement samAchievement)
    {
        initSchoolUnreviewedForInsert(samAchievement);
        return toAjax(samAchievementService.insertSamAchievement(samAchievement));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:edit')")
    @Log(title = "校级未审核成果", businessType = BusinessType.UPDATE)
    @PutMapping("/school_level_unreviewed")
    public AjaxResult editSchoolUnreviewed(@RequestBody SamAchievement samAchievement)
    {
        return saveReviewForm(samAchievement);
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:edit')")
    @Log(title = "校级未审核成果批量审核", businessType = BusinessType.UPDATE)
    @PutMapping("/school_level_unreviewed/batchReviewStatus")
    public AjaxResult batchSchoolUnreviewedStatus(@RequestBody BatchReviewStatusRequest request)
    {
        return batchUpdateStatus("school_level_unreviewed", request);
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_unreviewed:remove')")
    @Log(title = "校级未审核成果", businessType = BusinessType.DELETE)
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
    @BizAudit(bizType = "achievement_review", bizName = "导出校级已审核成果", opType = BizAuditOpType.EXPORT, handler = "achievementBizAuditHandler")
    @Log(title = "校级已审核成果", businessType = BusinessType.EXPORT)
    @PostMapping("/school_level_reviewed/export")
    public void exportSchoolReviewed(HttpServletResponse response, reviewed reviewed)
    {
        applyFilter(reviewed, "school", "reviewed");
        List<reviewed> list = reviewedService.selectreviewedList(reviewed);
        ExcelUtil<reviewed> util = new ExcelUtil<reviewed>(reviewed.class);
        util.exportExcel(response, list, "校级已审核成果");
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:query')")
    @GetMapping(value = "/school_level_reviewed/{achievementId}")
    public AjaxResult getSchoolReviewed(@PathVariable("achievementId") String achievementId)
    {
        return success(samAchievementService.selectSamAchievementByAchievementId(achievementId));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:add')")
    @Log(title = "校级已审核成果", businessType = BusinessType.INSERT)
    @PostMapping("/school_level_reviewed")
    public AjaxResult addSchoolReviewed(@RequestBody reviewed reviewed)
    {
        applyFilter(reviewed, "school", "reviewed");
        return toAjax(reviewedService.insertreviewed(reviewed));
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:edit')")
    @Log(title = "校级已审核成果", businessType = BusinessType.UPDATE)
    @PutMapping("/school_level_reviewed")
    public AjaxResult editSchoolReviewed(@RequestBody SamAchievement samAchievement)
    {
        return saveReviewForm(samAchievement);
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:edit')")
    @Log(title = "校级已审核成果批量审核", businessType = BusinessType.UPDATE)
    @PutMapping("/school_level_reviewed/batchReviewStatus")
    public AjaxResult batchSchoolReviewedStatus(@RequestBody BatchReviewStatusRequest request)
    {
        return batchUpdateStatus("school_level_reviewed", request);
    }

    @PreAuthorize("@ss.hasPermi('achievement:school_level_reviewed:remove')")
    @Log(title = "校级已审核成果", businessType = BusinessType.DELETE)
    @DeleteMapping("/school_level_reviewed/{achievementIds}")
    public AjaxResult removeSchoolReviewed(@PathVariable String[] achievementIds)
    {
        return toAjax(reviewedService.deletereviewedByAchievementIds(achievementIds));
    }
}
