package com.ruoyi.achievement.controller;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.achievement.domain.FileUuid;
import com.ruoyi.achievement.mapper.FileUuidMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachment")
public class AttachmentController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(AttachmentController.class);

    @Autowired
    private FileUuidMapper fileUuidMapper;

    /**
     * 安全上传接口：返回 UUID 而不是路径
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            String relativePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);
            String uuid = IdUtils.simpleUUID();

            FileUuid fileRecord = new FileUuid();
            fileRecord.setFileUuid(uuid);
            fileRecord.setRealPath(relativePath);
            fileRecord.setOriginName(file.getOriginalFilename());
            fileRecord.setIsTemp(1);
            fileRecord.setCreateBy(SecurityUtils.getUsername());

            fileUuidMapper.insertFileUuid(fileRecord);

            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", uuid);
            ajax.put("fileName", uuid);
            ajax.put("newFileName", file.getOriginalFilename());
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通过 UUID 下载/预览附件
     */
    @GetMapping("/download")
    public void downloadFile(String resource, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            if (StringUtils.isEmpty(resource))
            {
                renderDownloadError(response, "文件标识不能为空");
                return;
            }

            FileUuid fileRecord = fileUuidMapper.selectFileUuidById(resource);
            if (fileRecord == null)
            {
                renderDownloadError(response, "附件记录不存在或已失效");
                return;
            }

            String realPath = fileRecord.getRealPath();
            if (StringUtils.isEmpty(realPath))
            {
                renderDownloadError(response, "附件路径不存在");
                return;
            }

            String localPath = realPath.startsWith(Constants.RESOURCE_PREFIX)
                    ? RuoYiConfig.getProfile() + StringUtils.substringAfter(realPath, Constants.RESOURCE_PREFIX)
                    : realPath;

            File localFile = new File(localPath);
            if (!localFile.exists() || !localFile.isFile())
            {
                renderDownloadError(response, "附件文件不存在，可能已被删除");
                return;
            }

            response.reset();
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            String fileName = StringUtils.isNotEmpty(fileRecord.getOriginName())
                    ? fileRecord.getOriginName()
                    : resource + ".pdf";
            FileUtils.setAttachmentResponseHeader(response, fileName);
            FileUtils.writeBytes(localPath, response.getOutputStream());
            response.flushBuffer();
        }
        catch (Exception e)
        {
            log.error("Download attachment failed, resource={}", resource, e);
            renderDownloadError(response, "附件读取失败，请联系管理员");
        }
    }

    private void renderDownloadError(HttpServletResponse response, String message)
    {
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(message)));
    }
}
