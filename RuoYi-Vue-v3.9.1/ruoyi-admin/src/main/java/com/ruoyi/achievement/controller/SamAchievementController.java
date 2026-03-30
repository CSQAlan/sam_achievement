package com.ruoyi.achievement.controller;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 查询成果录入列表（我负责的成果）
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:list,achievement:manage:participated:list,achievement:manage:guided:list')")
    @GetMapping("/list")
    public TableDataInfo list(SamAchievement samAchievement)
    {
        startPage();
        if (SecurityUtils.hasRole("student")) {
            String studentId = SecurityUtils.getUsername();
            if (samAchievement.getParams() == null) {
                samAchievement.setParams(new HashMap<>());
            }
            samAchievement.getParams().put("studentId", studentId);
            samAchievement.getParams().put("manager", "1");
            List<SamAchievement> list = samAchievementService.selectSamAchievementListByStudentId(samAchievement);
            return getDataTable(list);
        }

        if (SecurityUtils.hasRole("teacher")) {
            String teacherId = SecurityUtils.getUsername();
            if (samAchievement.getParams() == null) {
                samAchievement.setParams(new HashMap<>());
            }
            samAchievement.getParams().put("teacherId", teacherId);
            samAchievement.getParams().put("isFirst", 1);
            List<SamAchievement> list = samAchievementService.selectSamAchievementListByTeacherId(samAchievement);
            return getDataTable(list);
        }

        List<SamAchievement> list = samAchievementService.selectSamAchievementList(samAchievement);
        return getDataTable(list);
    }

    /**
     * 导出成果录入列表
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:export,achievement:manage:participated:export,achievement:manage:guided:export')")
    @Log(title = "成果录入", businessType = BusinessType.EXPORT)
    @BizAudit(bizType = "achievement", bizName = "导出成果", opType = BizAuditOpType.EXPORT, handler = "achievementBizAuditHandler")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamAchievement samAchievement)
    {
        List<SamAchievement> list;
        if (SecurityUtils.hasRole("student")) {
            String studentId = SecurityUtils.getUsername();
            if (samAchievement.getParams() == null) {
                samAchievement.setParams(new HashMap<>());
            }
            samAchievement.getParams().put("studentId", studentId);
            samAchievement.getParams().put("manager", "1");
            list = samAchievementService.selectSamAchievementListByStudentId(samAchievement);
        } else if (SecurityUtils.hasRole("teacher")) {
            String teacherId = SecurityUtils.getUsername();
            if (samAchievement.getParams() == null) {
                samAchievement.setParams(new HashMap<>());
            }
            samAchievement.getParams().put("teacherId", teacherId);
            samAchievement.getParams().put("isFirst", 1);
            list = samAchievementService.selectSamAchievementListByTeacherId(samAchievement);
        } else {
            list = samAchievementService.selectSamAchievementList(samAchievement);
        }
        ExcelUtil<SamAchievement> util = new ExcelUtil<SamAchievement>(SamAchievement.class);
        util.exportExcel(response, list, "成果录入数据");
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
    public AjaxResult add(@RequestBody SamAchievement samAchievement)
    {
        return toAjax(samAchievementService.insertSamAchievement(samAchievement));
    }

    /**
     * 修改成果录入
     */
    @PreAuthorize("@ss.hasAnyPermi('achievement:manage:edit,achievement:manage:guided:edit')")
    @Log(title = "成果录入", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SamAchievement samAchievement)
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
    @GetMapping("/listTracks")
    public AjaxResult listTracks(Long competitionId, Long sessionId)
    {
        return success(samAchievementService.selectTrackList(competitionId, sessionId));
    }

    /**
     * 校验证书编号是否唯一
     */
    @GetMapping("/checkCertificateNoUnique")
    public AjaxResult checkCertificateNoUnique(SamAchievement samAchievement)
    {
        return success(samAchievementService.checkCertificateNoUnique(samAchievement));
    }

    /**
     * 查询我参与的成果列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:participated:list')")
    @GetMapping("/list-participated")
    public TableDataInfo listParticipated(SamAchievement samAchievement)
    {
        startPage();
        String username = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(username)) {
            throw new com.ruoyi.common.exception.ServiceException("当前用户信息不能为空");
        }
        if (samAchievement.getParams() == null) {
            samAchievement.setParams(new HashMap<>());
        }

        // 使用统一的查询方法，查找该用户在“选手”或“指导老师”中出现的所有记录
        samAchievement.getParams().put("userId", username);
        List<SamAchievement> list = samAchievementService.selectSamAchievementListByUserId(samAchievement);

        return getDataTable(list);
    }

    /**
     * Responsible achievement list
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:list')")
    @GetMapping("/list-responsible")
    public TableDataInfo listResponsible(SamAchievement samAchievement)
    {
        startPage();
        String username = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(username)) {
            throw new com.ruoyi.common.exception.ServiceException("Current user info cannot be empty");
        }
        if (samAchievement.getParams() == null) {
            samAchievement.setParams(new HashMap<>());
        }
        samAchievement.getParams().put("studentId", username);
        List<SamAchievement> list = samAchievementService.selectSamAchievementListByResponsibleStudentId(samAchievement);

        return getDataTable(list);
    }

    /**
     * Query achievements guided by the current teacher
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:guided:list')")
    @GetMapping("/list-guided")
    public TableDataInfo listGuided(SamAchievement samAchievement)
    {
        // 获取当前用户工号
        String username = SecurityUtils.getUsername();

        // 核心逻辑：如果是老师或者管理员，查询本用户参与指导的所有成果
        if (SecurityUtils.hasRole("teacher") || SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            startPage();
            if (samAchievement.getParams() == null) {
                samAchievement.setParams(new HashMap<>());
            }
            samAchievement.getParams().put("teacherId", username);
            // 不再限制必须是第一指导老师

            List<SamAchievement> list = samAchievementService.selectSamAchievementListByTeacherId(samAchievement);
            return getDataTable(list);
        }

        // 如果是纯学生角色（且不是管理员），或者不具备上述权限，返回空
        return getDataTable(new java.util.ArrayList<>());
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

}
