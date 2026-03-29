package com.ruoyi.achievement.domain;

import com.ruoyi.common.annotation.Excel;

public class ExportAttachmentFailExcelVo
{
    @Excel(name = "成果编号")
    private String achievementId;

    @Excel(name = "负责人姓名")
    private String ownerName;

    @Excel(name = "附件类别")
    private String typeName;

    @Excel(name = "失败原因", width = 40)
    private String failReason;

    @Excel(name = "原文件名", width = 40)
    private String originName;

    @Excel(name = "文件UUID", width = 40)
    private String fileUuid;

    public String getAchievementId()
    {
        return achievementId;
    }

    public void setAchievementId(String achievementId)
    {
        this.achievementId = achievementId;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getFailReason()
    {
        return failReason;
    }

    public void setFailReason(String failReason)
    {
        this.failReason = failReason;
    }

    public String getOriginName()
    {
        return originName;
    }

    public void setOriginName(String originName)
    {
        this.originName = originName;
    }

    public String getFileUuid()
    {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid)
    {
        this.fileUuid = fileUuid;
    }
}
