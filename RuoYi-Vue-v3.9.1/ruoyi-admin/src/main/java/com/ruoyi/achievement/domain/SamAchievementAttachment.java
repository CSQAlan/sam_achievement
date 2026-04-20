package com.ruoyi.achievement.domain;

import java.io.Serializable;

public class SamAchievementAttachment implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long attachmentId;

    private String achievementId;

    private String fileUuid;

    private Integer type;

    private Integer fileType;

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public String getAchievementId()
    {
        return achievementId;
    }

    public void setAchievementId(String achievementId)
    {
        this.achievementId = achievementId;
    }

    public String getFileUuid()
    {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid)
    {
        this.fileUuid = fileUuid;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getFileType()
    {
        return fileType;
    }

    public void setFileType(Integer fileType)
    {
        this.fileType = fileType;
    }
}
