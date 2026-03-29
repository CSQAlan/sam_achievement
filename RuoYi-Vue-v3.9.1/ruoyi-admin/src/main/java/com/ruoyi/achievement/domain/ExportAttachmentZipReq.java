package com.ruoyi.achievement.domain;
import java.util.List;

public class ExportAttachmentZipReq
{
    private List<String> achievementIds;
    private List<Integer> types;
    private String sourceMode;

    public List<String> getAchievementIds()
    {
        return achievementIds;
    }

    public void setAchievementIds(List<String> achievementIds)
    {
        this.achievementIds = achievementIds;
    }

    public List<Integer> getTypes()
    {
        return types;
    }

    public void setTypes(List<Integer> types)
    {
        this.types = types;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }
}
