package com.ruoyi.achievement.controller;

import com.ruoyi.achievement.domain.FileUuid;
import com.ruoyi.achievement.mapper.FileUuidMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/attachment")
public class AttachmentController extends BaseController
{
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
     * 安全下载/预览接口：通过 UUID 换取真实路径并直接输出流
     */
    @GetMapping("/download")
    public void downloadFile(String resource, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            // 1. 根据前端传来的 UUID 查询数据库记录
            FileUuid fileRecord = fileUuidMapper.selectFileUuidById(resource);

            if (fileRecord == null) {
                // 返回 404 状态码，防止前端拿到空数据去强行解析 PDF
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String realPath = fileRecord.getRealPath();

            // 2. 将相对路径（如 /profile/upload/...）转换为服务器的绝对物理路径
            // Constants.RESOURCE_PREFIX 默认值就是 "/profile"
            String localPath = RuoYiConfig.getProfile() + StringUtils.substringAfter(realPath, Constants.RESOURCE_PREFIX);

            // 3. 设置响应头为二进制流
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, fileRecord.getOriginName());

            // 4. 利用若依自带的工具类，直接将物理文件写入 HttpServletResponse 的输出流中
            FileUtils.writeBytes(localPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            // 发生异常时返回 500 状态码
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}