package com.ruoyi.erp.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 附件对象 erp_attachment (升级版)
 */
public class ErpAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long outcomeId;
    private String attachType;
    private String fileUuid;    // 新增
    private String filePath;
    private String fileName;
    private String fileExt;     // 新增
    private Long fileSize;      // 新增
    private Integer isRequired; // 新增
    private Integer isReimburseReq; // 新增
    private Long uploadUserId;  // 新增
    private Date uploadTime;    // 新增

    // Getters and Setters
    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setOutcomeId(Long outcomeId) { this.outcomeId = outcomeId; }
    public Long getOutcomeId() { return outcomeId; }

    public void setAttachType(String attachType) { this.attachType = attachType; }
    public String getAttachType() { return attachType; }

    public void setFileUuid(String fileUuid) { this.fileUuid = fileUuid; }
    public String getFileUuid() { return fileUuid; }

    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFilePath() { return filePath; }

    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileName() { return fileName; }

    public void setFileExt(String fileExt) { this.fileExt = fileExt; }
    public String getFileExt() { return fileExt; }

    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public Long getFileSize() { return fileSize; }

    public void setIsRequired(Integer isRequired) { this.isRequired = isRequired; }
    public Integer getIsRequired() { return isRequired; }

    public void setIsReimburseReq(Integer isReimburseReq) { this.isReimburseReq = isReimburseReq; }
    public Integer getIsReimburseReq() { return isReimburseReq; }

    public void setUploadUserId(Long uploadUserId) { this.uploadUserId = uploadUserId; }
    public Long getUploadUserId() { return uploadUserId; }

    public void setUploadTime(Date uploadTime) { this.uploadTime = uploadTime; }
    public Date getUploadTime() { return uploadTime; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("outcomeId", getOutcomeId())
                .append("filePath", getFilePath())
                .toString();
    }
}