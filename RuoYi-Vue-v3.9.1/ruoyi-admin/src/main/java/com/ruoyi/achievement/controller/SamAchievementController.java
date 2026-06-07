package com.ruoyi.achievement.controller;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
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
import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.service.ISamAchievementService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.achievement.domain.ExportAttachmentZipReq;
import com.ruoyi.system.service.ISysDeptService;
import java.io.IOException;

/**
 * 成果录入Controller
 * 
 * @author 王璨
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/achievement/manage")
public class SamAchievementController extends BaseController
{
    @Autowired
    private ISamAchievementService samAchievementService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 判断是否为校级管理员（通过角色或权限）
     */
    private boolean isSchoolAdmin() {
        return SecurityUtils.hasRole("admin") || SecurityUtils.hasRole("schooladmin")
            || SecurityUtils.hasRole("schoolleveladmin") || SecurityUtils.hasRole("schooleveladmin")
            || SecurityUtils.hasPermi("achievement:school_level_unreviewed:list");
    }

    /**
     * 判断是否为院级管理员（通过角色或权限）
     */
    private boolean isCollegeAdmin() {
        if (isSchoolAdmin()) return false;
        return SecurityUtils.hasRole("collegeadmin") || SecurityUtils.hasRole("collegeleveladmin")
            || SecurityUtils.hasPermi("achievement:college_level_unreviewed:list");
    }

    /**
     * 查询成果管理列表（管理视图：全校/全院）
     * 只有管理员或特定角色的“管理”菜单才会调用此接口
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @GetMapping("/list")
    public TableDataInfo list(SamAchievement samAchievement)
    {
        startPage();

        // 优先处理明确要求的个人视角（不论是否为管理员）
        if (samAchievement.getParams() != null) {
            String sourceMode = (String) samAchievement.getParams().get("sourceMode");
            if ("responsible".equals(sourceMode)) {
                return listResponsible(samAchievement);
            } else if ("guided".equals(sourceMode)) {
                return listGuided(samAchievement);
            } else if ("participated".equals(sourceMode)) {
                return listParticipated(samAchievement);
            }
        }

        // 1. 学校管理员：查看全量数据
        if (isSchoolAdmin()) {
            List<SamAchievement> list = samAchievementService.selectSamAchievementList(samAchievement);
            return getDataTable(list);
        }

        // 2. 学院管理员：查看本院数据
        if (isCollegeAdmin()) {
            if (StringUtils.isEmpty(samAchievement.getOwnerDepId())) {
                Long userDeptId = SecurityUtils.getDeptId();
                if (userDeptId != null) {
                    Long collegeId = deptService.getCollegeId(userDeptId);
                    if (collegeId != null) {
                        samAchievement.setOwnerDepId(String.valueOf(collegeId));
                    }
                }
            }
            List<SamAchievement> list = samAchievementService.selectSamAchievementList(samAchievement);
            return getDataTable(list);
        }

        // 3. 如果不是管理员，回退到个人视角（负责或指导）
        if (SecurityUtils.hasRole("student")) {
            return listResponsible(samAchievement);
        }
        if (SecurityUtils.hasRole("teacher")) {
            return listGuided(samAchievement);
        }

        return getDataTable(new java.util.ArrayList<>());
    }

    /**
     * 查询我负责的成果（学生端专用）
     * 规则：只展示当前登录人作为“负责人”的成果。即使是管理员，在此页面也只看个人数据。
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:list')")
    @GetMapping("/list-responsible")
    public TableDataInfo listResponsible(SamAchievement samAchievement)
    {
        // 严禁老师查看“我负责的成果”（除非该老师也有学生号，此处以角色判定）
        if (!SecurityUtils.hasRole("student") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
             return getDataTable(new java.util.ArrayList<>());
        }

        startPage();
        String username = SecurityUtils.getUsername();
        if (samAchievement.getParams() == null) {
            samAchievement.setParams(new HashMap<>());
        }
        samAchievement.getParams().put("studentId", username);
        samAchievement.getParams().put("manager", "1");

        List<SamAchievement> list = samAchievementService.selectSamAchievementListByResponsibleStudentId(samAchievement);
        return getDataTable(list);
    }

    /**
     * 查询我指导的成果（教师端专用）
     * 规则：只展示当前登录人作为“第一指导老师”的成果。
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:guided:list')")
    @GetMapping("/list-guided")
    public TableDataInfo listGuided(SamAchievement samAchievement)
    {
        // 严禁纯学生查看“我指导的成果”
        if (!SecurityUtils.hasRole("teacher") && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            return getDataTable(new java.util.ArrayList<>());
        }

        startPage();
        String username = SecurityUtils.getUsername();
        if (samAchievement.getParams() == null) {
            samAchievement.setParams(new HashMap<>());
        }
        samAchievement.getParams().put("teacherId", username);
        // 限制位次：仅限第一指导老师（遵循“我负责的成果权限是我为第一负责人”规则）
        samAchievement.getParams().put("isFirst", 1);

        List<SamAchievement> list = samAchievementService.selectSamAchievementListByTeacherId(samAchievement);
        return getDataTable(list);
    }

    /**
     * 查询我参与的成果（学生/老师通用）
     * 规则：展示所有包含当前登录人名字的成果（不论是负责人、参与人还是指导老师）。
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:participated:list')")
    @GetMapping("/list-participated")
    public TableDataInfo listParticipated(SamAchievement samAchievement)
    {
        startPage();
        String username = SecurityUtils.getUsername();
        if (samAchievement.getParams() == null) {
            samAchievement.setParams(new HashMap<>());
        }
        samAchievement.getParams().put("userId", username);
        // 核心逻辑：exists (participant or advisor)，展示“有名字都能显示”的成果
        List<SamAchievement> list = samAchievementService.selectSamAchievementListByUserId(samAchievement);
        return getDataTable(list);
    }

    /**
     * 查询年度成果统计（遵循管理/个人视图分离）
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:list')")
    @GetMapping("/stats/year")
    public AjaxResult selectYearStats(SamAchievement samAchievement)
    {
        if (isSchoolAdmin()) {
            // 全校
        } else if (isCollegeAdmin()) {
            // 学院
            Long collegeId = deptService.getCollegeId(SecurityUtils.getDeptId());
            if (collegeId != null) samAchievement.setOwnerDepId(String.valueOf(collegeId));
        } else {
            // 个人
            if (samAchievement.getParams() == null) samAchievement.setParams(new HashMap<>());
            if (SecurityUtils.hasRole("student")) {
                samAchievement.getParams().put("studentId", SecurityUtils.getUsername());
                samAchievement.getParams().put("manager", "1");
            } else if (SecurityUtils.hasRole("teacher")) {
                samAchievement.getParams().put("teacherId", SecurityUtils.getUsername());
            }
        }
        return success(samAchievementService.selectYearStats(samAchievement));
    }

    /**
     * 查询首页统计数据
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:list')")
    @GetMapping("/stats/dashboard")
    public AjaxResult getDashboardStats()
    {
        return success(samAchievementService.selectDashboardStats());
    }

    /**
     * 导出成果（遵循管理/个人视图分离）
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:export,achievement:manage:participated:export,achievement:manage:guided:export')")
    @Log(title = "成果录入", businessType = BusinessType.EXPORT)
    @BizAudit(bizType = "achievement", bizName = "导出成果", opType = BizAuditOpType.EXPORT, handler = "achievementBizAuditHandler")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamAchievement samAchievement)
    {
        List<SamAchievement> list;
        if (isSchoolAdmin()) {
            list = samAchievementService.selectSamAchievementList(samAchievement);
        } else if (isCollegeAdmin()) {
            Long collegeId = deptService.getCollegeId(SecurityUtils.getDeptId());
            if (collegeId != null) samAchievement.setOwnerDepId(String.valueOf(collegeId));
            list = samAchievementService.selectSamAchievementList(samAchievement);
        } else if (SecurityUtils.hasRole("student")) {
            if (samAchievement.getParams() == null) samAchievement.setParams(new HashMap<>());
            samAchievement.getParams().put("studentId", SecurityUtils.getUsername());
            samAchievement.getParams().put("manager", "1");
            list = samAchievementService.selectSamAchievementListByResponsibleStudentId(samAchievement);
        } else if (SecurityUtils.hasRole("teacher")) {
            if (samAchievement.getParams() == null) samAchievement.setParams(new HashMap<>());
            samAchievement.getParams().put("teacherId", SecurityUtils.getUsername());
            list = samAchievementService.selectSamAchievementListByTeacherId(samAchievement);
        } else {
            list = samAchievementService.selectSamAchievementList(samAchievement);
        }
        ExcelUtil<SamAchievement> util = new ExcelUtil<SamAchievement>(SamAchievement.class);
        util.exportExcel(response, list, "成果数据");
    }

    /**
     * 获取成果录入详细信息
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:query,achievement:manage:participated:query,achievement:manage:guided:query')")
    @GetMapping(value = "/{achievementId}")
    public AjaxResult getInfo(@PathVariable("achievementId") String achievementId,
                              @RequestParam(value = "selfEditScene", required = false) String selfEditScene)
    {
        if (StringUtils.isNotEmpty(selfEditScene))
        {
            return success(samAchievementService.selectSamAchievementForSelf(achievementId, selfEditScene));
        }
        return success(samAchievementService.selectSamAchievementByAchievementId(achievementId));
    }

    /**
     * 新增成果录入
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:add,achievement:manage:guided:add')")
    @Log(title = "成果录入", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody SamAchievement samAchievement)
    {
        return toAjax(samAchievementService.insertSamAchievement(samAchievement));
    }

    /**
     * 修改成果录入
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:edit,achievement:manage:guided:edit')")
    @Log(title = "成果录入", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody SamAchievement samAchievement)
    {
        return toAjax(samAchievementService.updateSamAchievement(samAchievement));
    }

    /**
     * 删除成果录入
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:remove,achievement:manage:guided:remove')")
    @Log(title = "成果录入", businessType = BusinessType.DELETE)
	@DeleteMapping("/{achievementIds}")
    public AjaxResult remove(@PathVariable String[] achievementIds)
    {
        return toAjax(samAchievementService.deleteSamAchievementByAchievementIds(achievementIds));
    }

    /**
     * 根据比赛和届次查询已有的赛道
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:list')")
    @GetMapping("/listTracks")
    public AjaxResult listTracks(Long competitionId, Long sessionId)
    {
         return success(samAchievementService.selectTrackList(competitionId, sessionId));
    }

    /**
     * 校验证书编号是否唯一
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:add')")
    @GetMapping("/checkCertificateNoUnique")
    public AjaxResult checkCertificateNoUnique(SamAchievement samAchievement)
    {
        return success(samAchievementService.checkCertificateNoUnique(samAchievement));
    }

    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:export,achievement:manage:participated:export,achievement:manage:guided:export')")
    @Log(title = "成果附件批量导出", businessType = BusinessType.EXPORT)
    @BizAudit(bizType = "achievement", bizName = "导出成果附件", opType = BizAuditOpType.EXPORT, handler = "achievementBizAuditHandler")
    @PostMapping("/exportAttachmentZip")
    public void exportAttachmentZip(@RequestBody ExportAttachmentZipReq req,
                                    HttpServletResponse response) throws IOException
    {
        samAchievementService.exportAttachmentZip(req, response);
    }

    /**
     * 查询带有素质提升奖标签的成果列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:list')")
    @GetMapping("/list-quality")
    public TableDataInfo listQuality(SamAchievement samAchievement)
    {
        startPage();
        List<SamAchievement> list = samAchievementService.selectSamAchievementListByCompetitionTag(samAchievement);
        return getDataTable(list);
    }

    /**
     * 查询教师指导的带有素质提升奖标签的成果列表（教师版素质提升）
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:guided:list')")
    @GetMapping("/list-quality-teacher")
    public TableDataInfo listQualityTeacher(SamAchievement samAchievement)
    {
        // 获取当前用户工号
        String username = SecurityUtils.getUsername();
        if (samAchievement.getParams() == null) {
            samAchievement.setParams(new HashMap<>());
        }
        samAchievement.getParams().put("teacherId", username);
        startPage();
        List<SamAchievement> list = samAchievementService.selectQualityAchievementListByTeacher(samAchievement);
        return getDataTable(list);
    }

    /**
     * 导出素质提升奖成果列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:export')")
    @PostMapping("/export-quality")
    public void exportQuality(HttpServletResponse response, @RequestBody SamAchievement samAchievement)
    {
        List<SamAchievement> list = samAchievementService.selectSamAchievementListByCompetitionTag(samAchievement);
        ExcelUtil<SamAchievement> util = new ExcelUtil<SamAchievement>(SamAchievement.class);
        util.exportExcel(response, list, "素质提升奖成果数据");
    }

    /**
     * 导出教师指导的素质提升奖成果列表（教师版）
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:guided:export')")
    @PostMapping("/export-quality-teacher")
    public void exportQualityTeacher(HttpServletResponse response, @RequestBody SamAchievement samAchievement)
    {
        // 获取当前用户工号
        String username = SecurityUtils.getUsername();
        if (samAchievement.getParams() == null) {
            samAchievement.setParams(new HashMap<>());
        }
        samAchievement.getParams().put("teacherId", username);
        List<SamAchievement> list = samAchievementService.selectQualityAchievementListByTeacher(samAchievement);
        ExcelUtil<SamAchievement> util = new ExcelUtil<SamAchievement>(SamAchievement.class);
        util.exportExcel(response, list, "教师指导素质提升奖成果数据");
    }

}
