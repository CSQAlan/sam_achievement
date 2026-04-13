package com.ruoyi.achievement.domain;

public class ExportAchievementBaseVo
{
    private String achievementId;
    private String ownerName;
    private String competitionName;

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

    public String getCompetitionName()
    {
        return competitionName;
    }

    public void setCompetitionName(String competitionName)
    {
        this.competitionName = competitionName;
    }
}