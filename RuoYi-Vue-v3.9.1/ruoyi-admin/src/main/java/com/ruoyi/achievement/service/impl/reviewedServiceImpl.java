package com.ruoyi.achievement.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Objects;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.reviewedMapper;
import com.ruoyi.achievement.mapper.SamAchievementMapper;
import com.ruoyi.achievement.domain.reviewed;
import com.ruoyi.achievement.service.IreviewedService;

/**
 * 成果审核Service业务层处�? * 
 * @author cyy
 * @date 2026-02-03
 */
@Service
public class reviewedServiceImpl implements IreviewedService 
{
    @Autowired
    private reviewedMapper reviewedMapper;

    @Autowired
    private SamAchievementMapper samAchievementMapper;

    private static final String STAGE_COLLEGE = "college";
    private static final String STAGE_SCHOOL = "school";
    private static final String STATUS_UNREVIEWED = "unreviewed";
    private static final String STATUS_REVIEWED = "reviewed";

    private static final Long COLLEGE_PENDING = 0L;
    private static final Long COLLEGE_REJECT = 1L;
    private static final Long COLLEGE_PASS = 2L;
    private static final Long SCHOOL_REJECT = 0L;
    private static final Long SCHOOL_PASS = 1L;
    private static final Long SCHOOL_PENDING = 2L;


    /**
     * 查询成果审核
     * 
     * @param achievementId 成果ID
     * @return 成果审核
     */
    @Override
    public reviewed selectreviewedByAchievementId(String achievementId)
    {
        return reviewedMapper.selectreviewedByAchievementId(achievementId);
    }

    /**
     * 查询成果审核列表
     * 
     * @param reviewed 成果审核
     * @return 成果审核
     */
    @Override
    public List<reviewed> selectreviewedList(reviewed reviewed)
    {
        return reviewedMapper.selectreviewedList(reviewed);
    }

    /**
     * 新增成果审核
     * 
     * @param reviewed 成果审核
     * @return 结果
     */
    @Transactional
    @Override
    public int insertreviewed(reviewed reviewed)
    {
        String stage = resolveStage(reviewed.getReviewStage());
        String status = resolveStatus(reviewed.getReviewStatus());
        if (stage == null || status == null)
        {
            stage = STAGE_COLLEGE;
            status = STATUS_UNREVIEWED;
        }
        sanitizeReasons(reviewed);
        validateInsertByStage(reviewed, stage, status);
        if (!StringUtils.hasText(reviewed.getAchievementId()))
        {
            Long nextId = samAchievementMapper.selectNextAchievementId();
            samAchievementMapper.incrementNextAchievementId();
            reviewed.setAchievementId(String.valueOf(nextId));
        }

        reviewed.setCreateTime(DateUtils.getNowDate());
        int rows = reviewedMapper.insertreviewed(reviewed);
        insertSamAchievementParticipant(reviewed);
        return rows;
    }

    /**
     * 修改成果审核
     * 
     * @param reviewed 成果审核
     * @return 结果
     */
    @Transactional
    @Override
    public int updatereviewed(reviewed reviewed)
    {
        if (reviewed.getAchievementId() == null)
        {
            throw new ServiceException("????ID?????");
        }
        reviewed existing = reviewedMapper.selectreviewedByAchievementId(reviewed.getAchievementId());
        if (existing == null)
        {
            throw new ServiceException("??????????");
        }
        validateReviewFlow(existing, reviewed);
        reviewed.setUpdateTime(DateUtils.getNowDate());
        reviewedMapper.deleteSamAchievementParticipantByParticipantId(reviewed.getAchievementId());
        insertSamAchievementParticipant(reviewed);
        return reviewedMapper.updatereviewed(reviewed);
    }

    /**
     * 批量删除成果审核
     * 
     * @param achievementIds 需要删除的成果ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deletereviewedByAchievementIds(String[] achievementIds)
    {
        reviewedMapper.deleteSamAchievementParticipantByParticipantIds(achievementIds);
        return reviewedMapper.deletereviewedByAchievementIds(achievementIds);
    }

    /**
     * 删除成果审核信息
     * 
     * @param achievementId 成果ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deletereviewedByAchievementId(String achievementId)
    {
        reviewedMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        return reviewedMapper.deletereviewedByAchievementId(achievementId);
    }

    /**
     * 新增参赛选手信息
     * 
     * @param reviewed 成果审核对象
     */
    public void insertSamAchievementParticipant(reviewed reviewed)
    {
        List<SamAchievementParticipant> samAchievementParticipantList = reviewed.getSamAchievementParticipantList();
        String achievementId = reviewed.getAchievementId();
        if (StringUtils.isNotNull(samAchievementParticipantList))
        {
            List<SamAchievementParticipant> list = new ArrayList<SamAchievementParticipant>();
            for (SamAchievementParticipant samAchievementParticipant : samAchievementParticipantList)
            {
                samAchievementParticipant.setParticipantId(achievementId);
                list.add(samAchievementParticipant);
            }
            if (list.size() > 0)
            {
                reviewedMapper.batchSamAchievementParticipant(list);
            }
        }
    }

    private void sanitizeReasons(reviewed incoming)
    {
        if (!StringUtils.hasText(incoming.getReviewReason()))
        {
            incoming.setReviewReason(null);
        }
        if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
        {
            incoming.setSchoolReviewReason(null);
        }
    }

    private void validateInsertByStage(reviewed incoming, String stage, String status)
    {
        if (STAGE_COLLEGE.equals(stage) && STATUS_UNREVIEWED.equals(status))
        {
            if (incoming.getReviewResult() == null)
            {
                incoming.setReviewResult(COLLEGE_PENDING);
            }
            if (!Objects.equals(incoming.getReviewResult(), COLLEGE_PENDING))
            {
                throw new ServiceException("College unreviewed must be pending.");
            }
            if (incoming.getSchooiReviewResult() != null)
            {
                throw new ServiceException("School review status must be empty for new achievements.");
            }
            incoming.setReviewReason(null);
            incoming.setSchoolReviewReason(null);
            return;
        }

        if (STAGE_COLLEGE.equals(stage) && STATUS_REVIEWED.equals(status))
        {
            Long reviewResult = incoming.getReviewResult();
            if (!isCollegeReviewedValue(reviewResult))
            {
                throw new ServiceException("College reviewed must be pass or reject.");
            }
            if (Objects.equals(reviewResult, COLLEGE_REJECT))
            {
        if (!StringUtils.hasText(incoming.getReviewReason()))
                {
                    throw new ServiceException("College rejection reason is required.");
                }
                if (incoming.getSchooiReviewResult() != null)
                {
                    throw new ServiceException("School review status cannot be set when college is rejected.");
                }
            }
            else
            {
                incoming.setReviewReason(null);
                Long schoolResult = incoming.getSchooiReviewResult();
                if (schoolResult != null && !Objects.equals(schoolResult, SCHOOL_PENDING))
                {
                    throw new ServiceException("School review status can only be pending after college pass.");
                }
            }
            incoming.setSchoolReviewReason(null);
            return;
        }

        if (STAGE_SCHOOL.equals(stage) && STATUS_UNREVIEWED.equals(status))
        {
            if (incoming.getSchooiReviewResult() == null)
            {
                incoming.setSchooiReviewResult(SCHOOL_PENDING);
            }
            if (!Objects.equals(incoming.getSchooiReviewResult(), SCHOOL_PENDING))
            {
                throw new ServiceException("School unreviewed must be pending.");
            }
            Long college = incoming.getReviewResult();
            if (college != null && !Objects.equals(college, COLLEGE_PENDING))
            {
                throw new ServiceException("School unreviewed cannot change college review status.");
            }
            incoming.setReviewReason(null);
            incoming.setSchoolReviewReason(null);
            return;
        }

        if (STAGE_SCHOOL.equals(stage) && STATUS_REVIEWED.equals(status))
        {
            if (!Objects.equals(incoming.getReviewResult(), COLLEGE_PASS))
            {
                throw new ServiceException("School reviewed requires college pass.");
            }
            Long schoolResult = incoming.getSchooiReviewResult();
            if (!isSchoolReviewedValue(schoolResult))
            {
                throw new ServiceException("School reviewed must be pass or reject.");
            }
            if (Objects.equals(schoolResult, SCHOOL_REJECT))
            {
        if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
                {
                    throw new ServiceException("School rejection reason is required.");
                }
            }
            else
            {
                incoming.setSchoolReviewReason(null);
            }
            incoming.setReviewReason(null);
            return;
        }

        throw new ServiceException("Invalid review stage or status.");
    }

    private String resolveStage(String stage)
    {
        if (!StringUtils.hasText(stage))
        {
            return null;
        }
        String normalized = stage.trim().toLowerCase();
        if (STAGE_COLLEGE.equals(normalized) || STAGE_SCHOOL.equals(normalized))
        {
            return normalized;
        }
        return null;
    }

    private String resolveStatus(String status)
    {
        if (!StringUtils.hasText(status))
        {
            return null;
        }
        String normalized = status.trim().toLowerCase();
        if (STATUS_UNREVIEWED.equals(normalized) || STATUS_REVIEWED.equals(normalized))
        {
            return normalized;
        }
        return null;
    }

    private void validateReviewFlow(reviewed existing, reviewed incoming)
    {
        String stage = resolveStage(incoming.getReviewStage());
        boolean stageCollege = STAGE_COLLEGE.equals(stage);
        boolean stageSchool = STAGE_SCHOOL.equals(stage);
        Long existingCollegeRaw = existing.getReviewResult();
        Long existingCollege = existingCollegeRaw == null ? COLLEGE_PENDING : existingCollegeRaw;
        Long existingSchool = existing.getSchooiReviewResult();

        Long requestedCollege = incoming.getReviewResult();
        Long requestedSchool = incoming.getSchooiReviewResult();

        if (stageSchool)
        {
            if (requestedCollege != null && !Objects.equals(requestedCollege, existingCollegeRaw))
            {
                incoming.setReviewResult(existingCollegeRaw);
            }
            if (incoming.getReviewReason() != null && !Objects.equals(incoming.getReviewReason(), existing.getReviewReason()))
            {
                incoming.setReviewReason(existing.getReviewReason());
            }
        }
        if (stageCollege)
        {
            if (requestedSchool != null
                && !Objects.equals(requestedSchool, SCHOOL_PENDING)
                && !Objects.equals(requestedSchool, existingSchool))
            {
                incoming.setSchooiReviewResult(existingSchool);
            }
            if (incoming.getSchoolReviewReason() != null
                && !Objects.equals(incoming.getSchoolReviewReason(), existing.getSchoolReviewReason()))
            {
                incoming.setSchoolReviewReason(existing.getSchoolReviewReason());
            }
        }

        requestedCollege = incoming.getReviewResult();
        requestedSchool = incoming.getSchooiReviewResult();

        boolean allowCollegeReAudit = stageCollege
            && isCollegeReviewedValue(existingCollege)
            && requestedCollege != null
            && isCollegeReviewedValue(requestedCollege);
        boolean allowSchoolReAudit = stageSchool
            && isSchoolReviewedValue(existingSchool)
            && requestedSchool != null
            && isSchoolReviewedValue(requestedSchool);

        if (!stageCollege
            && requestedCollege != null
            && Objects.equals(requestedCollege, COLLEGE_PENDING)
            && !Objects.equals(existingCollege, COLLEGE_PENDING))
        {
            incoming.setReviewResult(existingCollegeRaw);
            requestedCollege = incoming.getReviewResult();
        }

        boolean collegeChanged = requestedCollege != null && !Objects.equals(requestedCollege, existingCollege);
        boolean schoolChanged = requestedSchool != null && !Objects.equals(requestedSchool, existingSchool);

        if (stageCollege)
        {
            if (requestedCollege != null
                && Objects.equals(requestedCollege, COLLEGE_PENDING)
                && !Objects.equals(existingCollege, COLLEGE_PENDING)
                && !allowCollegeReAudit)
            {
                incoming.setReviewResult(existingCollegeRaw);
                requestedCollege = incoming.getReviewResult();
                collegeChanged = false;
            }

            if (collegeChanged)
            {
                if (!Objects.equals(existingCollege, COLLEGE_PENDING) && !allowCollegeReAudit)
                {
                    throw new ServiceException("College review status cannot be changed after reviewed.");
                }
                if (!isCollegeReviewedValue(requestedCollege))
                {
                    throw new ServiceException("College review can only change to pass or reject.");
                }
                if (Objects.equals(requestedCollege, COLLEGE_REJECT))
                {
        if (!StringUtils.hasText(incoming.getReviewReason()))
                    {
                        throw new ServiceException("College rejection reason is required.");
                    }
                }
                else
                {
                    incoming.setReviewReason(null);
                }
            }
            else if (!StringUtils.hasText(incoming.getReviewReason()))
            {
                incoming.setReviewReason(existing.getReviewReason());
            }

            if (schoolChanged)
            {
                if (!Objects.equals(requestedSchool, SCHOOL_PENDING))
                {
                    throw new ServiceException("School review status can only be set to pending by college review.");
                }
                if (existingSchool != null && !Objects.equals(existingSchool, SCHOOL_PENDING))
                {
                    throw new ServiceException("School review status cannot be changed after reviewed.");
                }
                Long effectiveCollege = collegeChanged ? requestedCollege : existingCollege;
                if (!Objects.equals(effectiveCollege, COLLEGE_PASS))
                {
                    throw new ServiceException("School review can only start after college pass.");
                }
            }
            else if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
            {
                incoming.setSchoolReviewReason(existing.getSchoolReviewReason());
            }
            return;
        }

        if (stageSchool)
        {
            if (schoolChanged)
            {
                if (existingSchool == null)
                {
                    if (!Objects.equals(requestedSchool, SCHOOL_PENDING))
                    {
                        throw new ServiceException("School review must start from pending.");
                    }
                }
                else if (Objects.equals(existingSchool, SCHOOL_PENDING))
                {
                    if (!isSchoolReviewedValue(requestedSchool))
                    {
                        throw new ServiceException("School review can only change to pass or reject.");
                    }
                    if (!Objects.equals(existingCollege, COLLEGE_PASS))
                    {
                        throw new ServiceException("School review requires college pass.");
                    }
                    if (Objects.equals(requestedSchool, SCHOOL_REJECT))
                    {
        if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
                        {
                            throw new ServiceException("School rejection reason is required.");
                        }
                    }
                    else
                    {
                        incoming.setSchoolReviewReason(null);
                    }
                }
                else
                {
                    if (!allowSchoolReAudit)
                    {
                        throw new ServiceException("School review status cannot be changed after reviewed.");
                    }
                    if (!isSchoolReviewedValue(requestedSchool))
                    {
                        throw new ServiceException("School review can only change to pass or reject.");
                    }
                    if (!Objects.equals(existingCollege, COLLEGE_PASS))
                    {
                        throw new ServiceException("School review requires college pass.");
                    }
                    if (Objects.equals(requestedSchool, SCHOOL_REJECT))
                    {
        if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
                        {
                            throw new ServiceException("School rejection reason is required.");
                        }
                    }
                    else
                    {
                        incoming.setSchoolReviewReason(null);
                    }
                }
            }
            else if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
            {
                incoming.setSchoolReviewReason(existing.getSchoolReviewReason());
            }
        if (!StringUtils.hasText(incoming.getReviewReason()))
            {
                incoming.setReviewReason(null);
            }
            return;
        }

        if (collegeChanged || schoolChanged)
        {
            throw new ServiceException("Invalid review stage for status update.");
        }
        if (!StringUtils.hasText(incoming.getReviewReason()))
        {
            incoming.setReviewReason(existing.getReviewReason());
        }
        if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
        {
            incoming.setSchoolReviewReason(existing.getSchoolReviewReason());
        }
    }

    private boolean isCollegeReviewedValue(Long value)
    {
        return Objects.equals(value, COLLEGE_REJECT) || Objects.equals(value, COLLEGE_PASS);
    }

    private boolean isSchoolReviewedValue(Long value)
    {
        return Objects.equals(value, SCHOOL_REJECT) || Objects.equals(value, SCHOOL_PASS);
    }
}


