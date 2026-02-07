package com.ruoyi.achievement.controller;

import com.ruoyi.achievement.domain.FileUuid;
import com.ruoyi.achievement.mapper.FileUuidMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
                // 1. 使用若依工具类将文件存入磁盘 (默认存到 profile/upload 目录下)
                String relativePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);

                // 2. 将相对路径 (/profile/upload/...) 转换为绝对本地路径以便后续读取
                // 若依存库的是 URL 路径，我们需要转换一下，或者直接存这个 path 只要下载时能解析即可
                // 这里为了简单，我们存若依返回的这个 path，下载时再转换

                // 3. 生成 UUID
                String uuid = IdUtils.simpleUUID();

                // 4. 入库映射表
                FileUuid fileRecord = new FileUuid();
                fileRecord.setFileUuid(uuid);
                fileRecord.setRealPath(relativePath); // 存入真实路径/相对路径
                fileRecord.setOriginName(file.getOriginalFilename());
                fileRecord.setIsTemp(1); // 默认为临时文件，正式提交表单后可改为0
                fileRecord.setCreateBy(SecurityUtils.getUsername());

               fileUuidMapper.insertFileUuid(fileRecord);

                // 5. 返回结果 (关键：url 和 fileName 都返回 UUID)
                AjaxResult ajax = AjaxResult.success();
                ajax.put("url", uuid);      // 前端组件通常绑定 url
                ajax.put("fileName", uuid); // 或者是 fileName
                ajax.put("newFileName", file.getOriginalFilename());
                ajax.put("originalFilename", file.getOriginalFilename());
                return ajax;
            }
            catch (Exception e)
            {
                return AjaxResult.error(e.getMessage());
            }
        }
    }
