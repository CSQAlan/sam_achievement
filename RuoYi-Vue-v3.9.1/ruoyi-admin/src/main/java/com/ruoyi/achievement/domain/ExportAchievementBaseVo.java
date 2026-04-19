package com.ruoyi.achievement.domain;

public class ExportAchievementBaseVo
{
    private String achievementId;
    private String ownerName;
    private String competitionName;
    private String awardGrade;
    private String awardLevel;
    private Long year;
    private String track;

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

    public String getAwardGrade()
    {
        return awardGrade;
    }

    public void setAwardGrade(String awardGrade)
    {
        this.awardGrade = awardGrade;
    }

    public String getAwardLevel()
    {
        return awardLevel;
    }

    public void setAwardLevel(String awardLevel)
    {
        this.awardLevel = awardLevel;
    }

    public Long getYear()
    {
        return year;
    }

    public void setYear(Long year)
    {
        this.year = year;
    }

    public String getTrack()
    {
        return track;
    }

    public void setTrack(String track)
    {
        this.track = track;
    }
}