package com.ruoyi.competitionapply.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事申请附件对象 competition_apply_attachment
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
public class CompetitionApplyAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 赛事申请ID（关联competition_apply.id） */
    @Excel(name = "赛事申请ID", readConverterExp = "关=联competition_apply.id")
    private Long applyId;

    /** 附件类型（赛事通知/证明材料/其他） */
    @Excel(name = "附件类型", readConverterExp = "赛=事通知/证明材料/其他")
    private String type;

    /** 文件类型（PDF等） */
    @Excel(name = "文件类型", readConverterExp = "P=DF等")
    private String fileType;

    /** 原始文件名 */
    @Excel(name = "原始文件名")
    private String originName;

    /** 存储文件名（建议UUID） */
    @Excel(name = "存储文件名", readConverterExp = "建=议UUID")
    private String storeName;

    /** 相对路径（不含域名） */
    @Excel(name = "相对路径", readConverterExp = "不=含域名")
    private String filePath;

    /** 访问URL（file_path+配置拼接） */
    @Excel(name = "访问URL", readConverterExp = "f=ile_path+配置拼接")
    private String fileUrl;

    /** 文件大小（单位Byte） */
    @Excel(name = "文件大小", readConverterExp = "单=位Byte")
    private Long fileSize;

    /** 上传人（关联sys_user.user_id） */
    @Excel(name = "上传人", readConverterExp = "关=联sys_user.user_id")
    private Long uploadBy;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date uploadTime;

    /** 删除标记（0=存在，2=删除） */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setApplyId(Long applyId) 
    {
        this.applyId = applyId;
    }

    public Long getApplyId() 
    {
        return applyId;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setFileType(String fileType) 
    {
        this.fileType = fileType;
    }

    public String getFileType() 
    {
        return fileType;
    }
    public void setOriginName(String originName) 
    {
        this.originName = originName;
    }

    public String getOriginName() 
    {
        return originName;
    }
    public void setStoreName(String storeName) 
    {
        this.storeName = storeName;
    }

    public String getStoreName() 
    {
        return storeName;
    }
    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }
    public void setFileUrl(String fileUrl) 
    {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() 
    {
        return fileUrl;
    }
    public void setFileSize(Long fileSize) 
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize() 
    {
        return fileSize;
    }
    public void setUploadBy(Long uploadBy) 
    {
        this.uploadBy = uploadBy;
    }

    public Long getUploadBy() 
    {
        return uploadBy;
    }
    public void setUploadTime(Date uploadTime) 
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime() 
    {
        return uploadTime;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("applyId", getApplyId())
            .append("type", getType())
            .append("fileType", getFileType())
            .append("originName", getOriginName())
            .append("storeName", getStoreName())
            .append("filePath", getFilePath())
            .append("fileUrl", getFileUrl())
            .append("fileSize", getFileSize())
            .append("uploadBy", getUploadBy())
            .append("uploadTime", getUploadTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
