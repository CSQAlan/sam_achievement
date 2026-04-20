package com.ruoyi.achievement.service;

import java.util.List;
import com.ruoyi.achievement.domain.reviewed;

/**
 * Achievement review service.
 */
public interface IreviewedService
{
    public reviewed selectreviewedByAchievementId(String achievementId);

    public List<reviewed> selectreviewedList(reviewed reviewed);

    public int insertreviewed(reviewed reviewed);

    public int updatereviewed(reviewed reviewed);

    public int deletereviewedByAchievementIds(String[] achievementIds);

    public int deletereviewedByAchievementId(String achievementId);

    public reviewed submitReview(String source, String achievementId, Long reviewStatus, String rejectReason);

    public int batchSubmitReview(String source, String[] achievementIds, Long reviewStatus, String rejectReason);
}