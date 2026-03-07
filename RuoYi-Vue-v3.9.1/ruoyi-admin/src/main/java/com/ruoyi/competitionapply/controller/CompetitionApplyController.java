package com.ruoyi.competitionapply.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.file.FileUtils; // 保留这个导入，适配你的工具类
import com.ruoyi.competitionapply.domain.CompetitionApply;
import com.ruoyi.competitionapply.domain.CompetitionApplyAttachment;
import com.ruoyi.competitionapply.service.ICompetitionApplyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 赛事申请Controller
 *
 * @author ruoyi
 * @date 2026-02-01
 */
@RestController
@RequestMapping("/competition-apply/competitionapply")
public class CompetitionApplyController extends BaseController
{
    @Autowired
    private ICompetitionApplyService competitionApplyService;

    /**
     * 查询赛事申请列表
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:list')")
    @GetMapping("/list")
    public TableDataInfo list(CompetitionApply competitionApply)
    {
        startPage();
        List<CompetitionApply> list = competitionApplyService.selectCompetitionApplyList(competitionApply);
        return getDataTable(list);
    }

    /**
     * 导出赛事申请列表
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:export')")
    @Log(title = "赛事申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CompetitionApply competitionApply)
    {
        List<CompetitionApply> list = competitionApplyService.selectCompetitionApplyList(competitionApply);
        ExcelUtil<CompetitionApply> util = new ExcelUtil<CompetitionApply>(CompetitionApply.class);
        util.exportExcel(response, list, "赛事申请数据");
    }

    /**
     * 获取赛事申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(competitionApplyService.selectCompetitionApplyById(id));
    }

    /**
     * 新增赛事申请（统一接口：接收文件地址，不接收文件二进制）
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:add')")
    @Log(title = "赛事申请", businessType = BusinessType.INSERT)
    @PostMapping // 根路径，匹配你前端旧的 API 路径
    public AjaxResult add(@RequestBody CompetitionApply competitionApply) {
        try {
            return toAjax(competitionApplyService.insertCompetitionApply(competitionApply));
        } catch (Exception e) {
            return AjaxResult.error("新增赛事申请失败：" + e.getMessage());
        }
    }

    /**
     * 修改赛事申请
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:edit')")
    @Log(title = "赛事申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CompetitionApply competitionApply)
    {
        return toAjax(competitionApplyService.updateCompetitionApply(competitionApply));
    }

    /**
     * 删除赛事申请
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:remove')")
    @Log(title = "赛事申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(competitionApplyService.deleteCompetitionApplyByIds(ids));
    }

    // ====================== 新增：文件预览核心接口 ======================
    /**
     * 1. 查询指定赛事申请的所有附件列表（前端展示已上传文件用）
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:query')")
    @GetMapping("/attachment/list/{applyId}")
    public AjaxResult getAttachmentList(@PathVariable("applyId") Long applyId) {
        List<CompetitionApplyAttachment> attachmentList = competitionApplyService.selectCompetitionApplyAttachmentListByApplyId(applyId);
        return success(attachmentList);
    }

    /**
     * 2. 文件预览接口（返回文件流，支持图片/PDF/文档在线预览）
     */
    @PreAuthorize("@ss.hasPermi('competition-apply:competitionapply:query')")
    @GetMapping("/attachment/preview/{attachmentId}")
    public ResponseEntity<Resource> previewFile(@PathVariable("attachmentId") Long attachmentId) {
        try {
            // 1. 查询附件信息
            CompetitionApplyAttachment attachment = competitionApplyService.selectCompetitionApplyAttachmentById(attachmentId);
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }

            // 2. 拼接完整文件路径（RuoYi默认上传路径）
            String uploadPath = RuoYiConfig.getUploadPath();
            String fullFilePath = uploadPath + File.separator + attachment.getPath();
            File file = new File(fullFilePath);

            // 校验文件是否存在
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }

            // 3. 构建文件资源对象
            Resource resource = new FileSystemResource(file);

            // 4. 设置响应头（解决中文乱码 + 支持在线预览）
            HttpHeaders headers = new HttpHeaders();
            String fileName = URLEncoder.encode(attachment.getDocumentName(), "UTF-8");

            // ========== 关键修改：适配你的FileUtils类获取文件后缀 ==========
            // 先获取不带路径的纯文件名，再截取后缀
            String pureFileName = FileUtils.getName(attachment.getDocumentName());
            String fileExt = "";
            if (pureFileName.contains(".")) {
                // 截取最后一个"."后的字符串作为后缀
                fileExt = pureFileName.substring(pureFileName.lastIndexOf(".") + 1).toLowerCase();
            }

            // 根据文件类型设置Content-Type，保证浏览器正确预览
            switch (fileExt) {
                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                    headers.setContentType(MediaType.IMAGE_JPEG);
                    break;
                case "pdf":
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    break;
                default:
                    // 其他文件（doc/xlsx/ppt等）返回流形式，支持在线预览/下载
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    break;
            }

            // 关键：设置inline让浏览器直接预览，而非下载
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
            headers.add("Access-Control-Expose-Headers", "Content-Disposition"); // 允许前端获取响应头

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            // 异常时返回500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}