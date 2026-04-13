package com.ruoyi.achievement.domain;
import java.util.List;

public class ExportAttachmentZipReq
{
    private List<String> achievementIds;
    private List<Integer> types;
    private String sourceMode;
    private Boolean groupByCompetition;
    private String competitionId;

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

    public Boolean getGroupByCompetition()
    {
        return groupByCompetition;
    }

    public void setGroupByCompetition(Boolean groupByCompetition)
    {
        this.groupByCompetition = groupByCompetition;
    }

    public String getCompetitionId()
    {
        return competitionId;
    }

    public void setCompetitionId(String competitionId)
    {
        this.competitionId = competitionId;
    }
}
