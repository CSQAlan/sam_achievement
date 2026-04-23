package com.ruoyi.competition.controller;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.competition.domain.Competition;
import com.ruoyi.competition.service.ICompetitionService;
import com.ruoyi.competition.service.ICompetitionPdfMappingService;
import org.springframework.util.CollectionUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 总赛事Controller
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
@RestController
@RequestMapping("/competition/competition")
public class CompetitionController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(CompetitionController.class);

    @Autowired
    private ICompetitionService competitionService;


    @Autowired
    private ICompetitionPdfMappingService competitionPdfMappingService;

    /**
     * 查询总赛事列表
     */
    @PreAuthorize("@ss.hasPermi('competition:competition:list')")
    @GetMapping("/list")
    public TableDataInfo list(Competition competition) {
        startPage();
        List<Competition> list = competitionService.selectCompetitionList(competition);
        return getDataTable(list);
    }

    /**
     * 导出总赛事列表
     */
    @PreAuthorize("@ss.hasPermi('competition:competition:export')")
    @Log(title = "总赛事", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Competition competition) {
        List<Competition> list = competitionService.selectCompetitionList(competition);
        ExcelUtil<Competition> util = new ExcelUtil<Competition>(Competition.class);
        util.exportExcel(response, list, "总赛事数据");
    }

    /**
     * 获取总赛事详细信息
     */
    @PreAuthorize("@ss.hasPermi('competition:competition:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(competitionService.selectCompetitionById(id));
    }

    /**
     * 新增总赛事
     */
    @PreAuthorize("@ss.hasPermi('competition:competition:add')")
    @Log(title = "总赛事", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Competition competition) {
        return toAjax(competitionService.insertCompetition(competition));
    }

    /**
     * 修改总赛事
     */
    @PreAuthorize("@ss.hasPermi('competition:competition:edit')")
    @Log(title = "总赛事", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Competition competition) {
        return toAjax(competitionService.updateCompetition(competition));
    }

    /**
     * 删除总赛事
     */
    @PreAuthorize("@ss.hasPermi('competition:competition:remove')")
    @Log(title = "总赛事", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(competitionService.deleteCompetitionByIds(ids));
    }

    /**
     * 解析PDF竞赛清单并匹配
     */
    @PreAuthorize("@ss.hasPermi('competition:competition:edit')")
    @Log(title = "竞赛导入", businessType = BusinessType.OTHER)
    @PostMapping("/import/analyze")
    public AjaxResult analyzePdf(@RequestParam("file") MultipartFile file, 
                                @RequestParam(value = "threshold", defaultValue = "0.7") Double threshold) {
        if (file == null || file.isEmpty()) {
            return error("请上传PDF文件");
        }
        
        try {
            // 保存临时文件以便处理
            File tempFile = File.createTempFile("comp_import_", ".pdf");
            file.transferTo(tempFile);
            
            try {
                Map<String, Object> matchResult = competitionPdfMappingService.extractAndMatchFromPdf(tempFile, threshold);
                return success(matchResult);
            } finally {
                FileUtils.deleteFile(tempFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("解析PDF失败", e);
            return error("解析失败：" + e.getMessage());
        }
    }

    /**
     * 确认匹配并建立关联（打标签）
     */
    @PreAuthorize("@ss.hasPermi('competition:competition:edit')")
    @Log(title = "竞赛导入", businessType = BusinessType.UPDATE)
    @PostMapping("/import/link")
    public AjaxResult confirmLink(@RequestBody Map<String, Object> params) {
        List<Integer> idsInt = (List<Integer>) params.get("competitionIds");
        if (idsInt == null) {
            return error("请选择要关联的竞赛");
        }
        List<Long> competitionIds = idsInt.stream().map(Integer::longValue).collect(Collectors.toList());
        List<String> tagCodes = (List<String>) params.get("tagCodes");
        String filename = (String) params.get("filename");
        
        if (CollectionUtils.isEmpty(competitionIds)) {
            return error("请选择要关联的竞赛");
        }
        
        int count = competitionPdfMappingService.confirmAndLink(competitionIds, tagCodes, filename);
        return AjaxResult.success("成功关联 " + count + " 条竞赛数据", count);
    }

    /**
     * 手动关联并学习别名
     */
    @PreAuthorize("@ss.hasPermi('competition:competition:edit')")
    @Log(title = "竞赛导入", businessType = BusinessType.UPDATE)
    @PostMapping("/import/manualLink")
    public AjaxResult manualLink(@RequestBody Map<String, Object> params) {
        Long competitionId = Long.valueOf(params.get("competitionId").toString());
        String pdfName = (String) params.get("pdfName");
        
        if (competitionPdfMappingService.linkManualMatch(competitionId, pdfName)) {
            return success("手动关联成功，系统已自动学习该名称为别名");
        }
        return error("手动关联失败");
    }

    /**
     * 获取总赛事列表选项（不拦截权限，供申请时下拉选择）
     */
    @GetMapping("/optionList")
    public AjaxResult optionList() {
        Competition query = new Competition();

        List<Competition> list = competitionService.selectCompetitionList(query);
        return AjaxResult.success(list);
    }
}
