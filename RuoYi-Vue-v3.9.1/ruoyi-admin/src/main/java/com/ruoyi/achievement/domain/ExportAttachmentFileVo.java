package com.ruoyi.achievement.domain;

public class ExportAttachmentFileVo
{
    private String achievementId;
    private Integer type;
    private String fileUuid;
    private String originName;
    private String realPath;

    public String getAchievementId()
    {
        return achievementId;
    }

    public void setAchievementId(String achievementId)
    {
        this.achievementId = achievementId;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public String getFileUuid()
    {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid)
    {
        this.fileUuid = fileUuid;
    }

    public String getOriginName()
    {
        return originName;
    }

    public void setOriginName(String originName)
    {
        this.originName = originName;
    }

    public String getRealPath()
    {
        return realPath;
    }

    public void setRealPath(String realPath)
    {
        this.realPath = realPath;
    }
}