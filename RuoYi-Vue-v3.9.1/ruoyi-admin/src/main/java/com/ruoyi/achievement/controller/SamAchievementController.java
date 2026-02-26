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
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.service.ISamAchievementService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

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
     * 查询成果录入列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:list')")
    @GetMapping("/list")
    public TableDataInfo list(SamAchievement samAchievement)
    {
        startPage();
        List<SamAchievement> list = samAchievementService.selectSamAchievementList(samAchievement);
        return getDataTable(list);
    }

    /**
     * 导出成果录入列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:export')")
    @Log(title = "成果录入", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SamAchievement samAchievement)
    {
        List<SamAchievement> list = samAchievementService.selectSamAchievementList(samAchievement);
        ExcelUtil<SamAchievement> util = new ExcelUtil<SamAchievement>(SamAchievement.class);
        util.exportExcel(response, list, "成果录入数据");
    }

    /**
     * 获取成果录入详细信息
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:query')")
    @GetMapping(value = "/{achievementId}")
    public AjaxResult getInfo(@PathVariable("achievementId") String achievementId)
    {
        return success(samAchievementService.selectSamAchievementByAchievementId(achievementId));
    }

    /**
     * 新增成果录入
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:add')")
    @Log(title = "成果录入", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SamAchievement samAchievement)
    {
        return toAjax(samAchievementService.insertSamAchievement(samAchievement));
    }

    /**
     * 修改成果录入
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:edit')")
    @Log(title = "成果录入", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SamAchievement samAchievement)
    {
        return toAjax(samAchievementService.updateSamAchievement(samAchievement));
    }

    /**
     * 删除成果录入
     */
    @PreAuthorize("@ss.hasPermi('achievement:manage:remove')")
    @Log(title = "成果录入", businessType = BusinessType.DELETE)
	@DeleteMapping("/{achievementIds}")
    public AjaxResult remove(@PathVariable String[] achievementIds)
    {
        return toAjax(samAchievementService.deleteSamAchievementByAchievementIds(achievementIds));
    }
}
