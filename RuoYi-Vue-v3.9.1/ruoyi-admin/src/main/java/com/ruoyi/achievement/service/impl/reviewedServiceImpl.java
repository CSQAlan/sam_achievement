package com.ruoyi.achievement.service.impl;

import java.util.*;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.reviewedMapper;
import com.ruoyi.achievement.domain.reviewed;
import com.ruoyi.achievement.service.IreviewedService;

/**
 * 成果审核Service业务层处理
 *
 * @author cyy
 * @date 2026-02-03
 */
@Service
public class reviewedServiceImpl implements IreviewedService
{
    @Autowired
    private reviewedMapper reviewedMapper;

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
    private static final String AUTO_SCHOOL_REJECT_REASON = "因院级状态变更自动驳回";
    private static final String AUTO_SCHOOL_REJECT_AUDITOR = "system";


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
     * @return 成果审核列表
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
        applyDefaultYear(reviewed);
        sanitizeReasons(reviewed);
        validateInsertByStage(reviewed, stage, status);
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
            throw new ServiceException("成果ID不能为空");
        }
        reviewed existing = reviewedMapper.selectreviewedByAchievementId(reviewed.getAchievementId());
        if (existing == null)
        {
            throw new ServiceException("未找到对应的成果审核信息");
        }
        applyDefaultYear(reviewed);
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
     * 批量更新审核状态
     */
    @Transactional
    @Override
    public int batchUpdateReviewStatus(String[] achievementIds, String stage, Long reviewStatus, String rejectReason)
    {
        if (achievementIds == null || achievementIds.length == 0)
        {
            throw new ServiceException("请选择需要批量审核的成果");
        }

        String normalizedStage = resolveStage(stage);
        if (normalizedStage == null)
        {
            throw new ServiceException("审核归属无效");
        }

        if (reviewStatus == null)
        {
            throw new ServiceException("审核状态不能为空");
        }

        String normalizedRejectReason = StringUtils.hasText(rejectReason) ? rejectReason.trim() : null;
        String operator = null;
        try
        {
            operator = SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            operator = null;
        }

        // 处理院级审核
        if (STAGE_COLLEGE.equals(normalizedStage))
        {
            if (!Objects.equals(reviewStatus, COLLEGE_PENDING)
                    && !Objects.equals(reviewStatus, COLLEGE_REJECT)
                    && !Objects.equals(reviewStatus, COLLEGE_PASS))
            {
                throw new ServiceException("院级审核状态无效");
            }

            if (Objects.equals(reviewStatus, COLLEGE_REJECT) && !StringUtils.hasText(normalizedRejectReason))
            {
                throw new ServiceException("请输入院级驳回原因");
            }

            // 批量更新院级审核状态
            return reviewedMapper.batchUpdateCollegeReviewStatus(achievementIds, reviewStatus, normalizedRejectReason, operator);
        }

        // 处理校级审核
        if (STAGE_SCHOOL.equals(normalizedStage))
        {
            if (!Objects.equals(reviewStatus, SCHOOL_PENDING)
                    && !Objects.equals(reviewStatus, SCHOOL_REJECT)
                    && !Objects.equals(reviewStatus, SCHOOL_PASS))
            {
                throw new ServiceException("校级审核状态无效");
            }

            if (Objects.equals(reviewStatus, SCHOOL_REJECT) && !StringUtils.hasText(normalizedRejectReason))
            {
                throw new ServiceException("请输入校级驳回原因");
            }

            // 批量更新校级审核状态
            return reviewedMapper.batchUpdateSchoolReviewStatus(achievementIds, reviewStatus, normalizedRejectReason, operator);
        }

        throw new ServiceException("审核归属无效");
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
            Date now = DateUtils.getNowDate();
            String username = null;
            try
            {
                username = SecurityUtils.getUsername();
            }
            catch (Exception e)
            {
                username = null;
            }
            String fallbackCreateBy = StringUtils.hasText(reviewed.getCreateBy()) ? reviewed.getCreateBy() : username;
            String fallbackUpdateBy = StringUtils.hasText(reviewed.getUpdateBy()) ? reviewed.getUpdateBy() : username;
            String fallbackRemark = StringUtils.hasText(reviewed.getRemark()) ? reviewed.getRemark() : "";
            for (SamAchievementParticipant samAchievementParticipant : samAchievementParticipantList)
            {
                samAchievementParticipant.setAchievementId(achievementId);
                samAchievementParticipant.setParticipantId(null);
                if (!StringUtils.hasText(samAchievementParticipant.getCreateBy()))
                {
                    samAchievementParticipant.setCreateBy(fallbackCreateBy);
                }
                if (!StringUtils.hasText(samAchievementParticipant.getUpdateBy()))
                {
                    samAchievementParticipant.setUpdateBy(fallbackUpdateBy);
                }
                if (samAchievementParticipant.getCreateTime() == null)
                {
                    samAchievementParticipant.setCreateTime(now);
                }
                if (samAchievementParticipant.getUpdateTime() == null)
                {
                    samAchievementParticipant.setUpdateTime(now);
                }
                if (samAchievementParticipant.getRemark() == null)
                {
                    samAchievementParticipant.setRemark(fallbackRemark);
                }
                if (samAchievementParticipant.getDelFlag() == null)
                {
                    samAchievementParticipant.setDelFlag(0L);
                }
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

    private void applyDefaultYear(reviewed incoming)
    {
        if (incoming.getYear() != null)
        {
            return;
        }
        Date awardTime = incoming.getAwardTime();
        if (awardTime == null)
        {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(awardTime);
        incoming.setYear((long) calendar.get(Calendar.YEAR));
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
                throw new ServiceException("院级未审核状态必须为待审核。");
            }
            if (incoming.getSchooiReviewResult() != null)
            {
                throw new ServiceException("新成果的校级审核状态必须为空。");
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
                throw new ServiceException("院级已审核状态必须为通过或驳回。");
            }
            if (Objects.equals(reviewResult, COLLEGE_REJECT))
            {
                if (!StringUtils.hasText(incoming.getReviewReason()))
                {
                    throw new ServiceException("院级驳回必须提供原因。");
                }
                if (incoming.getSchooiReviewResult() != null)
                {
                    throw new ServiceException("院级驳回时校级审核状态不能设置。");
                }
            }
            else
            {
                incoming.setReviewReason(null);
                Long schoolResult = incoming.getSchooiReviewResult();
                if (schoolResult != null && !Objects.equals(schoolResult, SCHOOL_PENDING))
                {
                    throw new ServiceException("院级通过后，校级审核状态只能为待审核。");
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
                throw new ServiceException("校级未审核状态必须为待审核。");
            }
            Long college = incoming.getReviewResult();
            if (college != null && !Objects.equals(college, COLLEGE_PENDING))
            {
                throw new ServiceException("校级未审核不能修改院级审核状态。");
            }
            incoming.setReviewReason(null);
            incoming.setSchoolReviewReason(null);
            return;
        }

        if (STAGE_SCHOOL.equals(stage) && STATUS_REVIEWED.equals(status))
        {
            if (!Objects.equals(incoming.getReviewResult(), COLLEGE_PASS))
            {
                throw new ServiceException("校级审核前必须院级通过。");
            }
            Long schoolResult = incoming.getSchooiReviewResult();
            if (!isSchoolReviewedValue(schoolResult))
            {
                throw new ServiceException("校级已审核状态必须为通过或驳回。");
            }
            if (Objects.equals(schoolResult, SCHOOL_REJECT))
            {
                if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
                {
                    throw new ServiceException("校级驳回必须提供原因。");
                }
            }
            else
            {
                incoming.setSchoolReviewReason(null);
            }
            incoming.setReviewReason(null);
            return;
        }

        throw new ServiceException("无效的审核阶段或状态。");
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
        // 解析当前操作的审核阶段
        String stage = resolveStage(incoming.getReviewStage());
        boolean stageCollege = STAGE_COLLEGE.equals(stage);
        boolean stageSchool = STAGE_SCHOOL.equals(stage);

        // 获取现有院级和校级审核状态
        Long existingCollegeRaw = existing.getReviewResult();
        Long existingCollege = existingCollegeRaw == null ? COLLEGE_PENDING : existingCollegeRaw;
        Long existingSchool = normalizeExistingSchoolStatus(existing);

        // 获取请求更新的院级和校级状态
        Long requestedCollege = incoming.getReviewResult();
        Long requestedSchool = incoming.getSchooiReviewResult();

        // 如果当前操作为校级审核阶段
        if (stageSchool)
        {
            // 不允许修改院级状态
            if (requestedCollege != null && !Objects.equals(requestedCollege, existingCollegeRaw))
            {
                incoming.setReviewResult(existingCollegeRaw);
            }
            // 不允许修改院级驳回原因
            if (incoming.getReviewReason() != null && !Objects.equals(incoming.getReviewReason(), existing.getReviewReason()))
            {
                incoming.setReviewReason(existing.getReviewReason());
            }
        }

        // 如果当前操作为院级审核阶段
        if (stageCollege)
        {
            // 不允许修改校级驳回原因
            if (incoming.getSchoolReviewReason() != null
                    && !Objects.equals(incoming.getSchoolReviewReason(), existing.getSchoolReviewReason()))
            {
                incoming.setSchoolReviewReason(existing.getSchoolReviewReason());
            }
        }

        // 更新请求状态变量
        requestedCollege = incoming.getReviewResult();
        requestedSchool = incoming.getSchooiReviewResult();

        // 当院级通过且校级尚未审核时，将校级状态设为待审核
        if (stageCollege
                && Objects.equals(requestedCollege, COLLEGE_PASS)
                && requestedSchool == null
                && (existingSchool == null || Objects.equals(existingSchool, SCHOOL_PENDING)))
        {
            incoming.setSchooiReviewResult(SCHOOL_PENDING);
            requestedSchool = incoming.getSchooiReviewResult();
        }

        // 判断是否允许院级复审
        boolean allowCollegeReAudit = stageCollege
                && isCollegeReviewedValue(existingCollege)
                && requestedCollege != null
                && (Objects.equals(requestedCollege, COLLEGE_PENDING) || isCollegeReviewedValue(requestedCollege));

        // 判断是否允许校级复审
        boolean allowSchoolReAudit = stageSchool
                && isSchoolReviewedValue(existingSchool)
                && requestedSchool != null
                && isSchoolReviewedValue(requestedSchool);

        // 校级操作不能将院级状态改为待审核
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

        // 院级操作逻辑
        if (stageCollege)
        {
            // 院级状态有变化
            if (collegeChanged)
            {
                if (!Objects.equals(existingCollege, COLLEGE_PENDING) && !allowCollegeReAudit)
                {
                    throw new ServiceException("院级审核状态在已审核后不可更改。");
                }
                if (!Objects.equals(requestedCollege, COLLEGE_PENDING) && !isCollegeReviewedValue(requestedCollege))
                {
                    throw new ServiceException("院级审核只能修改为待审核、通过或驳回。");
                }
                if (Objects.equals(requestedCollege, COLLEGE_REJECT))
                {
                    if (!StringUtils.hasText(incoming.getReviewReason()))
                    {
                        throw new ServiceException("院级驳回必须提供原因。");
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

            // 校级状态变化处理
            if (schoolChanged)
            {
                Long effectiveCollege = collegeChanged ? requestedCollege : existingCollege;
                if (Objects.equals(effectiveCollege, COLLEGE_PASS))
                {
                    if (!Objects.equals(requestedSchool, SCHOOL_PENDING)
                            && !Objects.equals(requestedSchool, existingSchool))
                    {
                        throw new ServiceException("院级通过后，校级审核状态只能保持或设置为待审核。");
                    }
                }
                else
                {
                    if (!Objects.equals(requestedSchool, SCHOOL_REJECT))
                    {
                        throw new ServiceException("院级待审核或驳回时，校级审核自动驳回。");
                    }
                }
            }
            else if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
            {
                incoming.setSchoolReviewReason(existing.getSchoolReviewReason());
            }

            // 根据院级状态自动更新校级
            if (requestedCollege != null)
            {
                if (Objects.equals(requestedCollege, COLLEGE_PASS))
                {
                    if (requestedSchool == null && (existingSchool == null || Objects.equals(existingSchool, SCHOOL_PENDING)))
                    {
                        incoming.setSchooiReviewResult(SCHOOL_PENDING);
                    }
                }
                else if (Objects.equals(requestedCollege, COLLEGE_PENDING) || Objects.equals(requestedCollege, COLLEGE_REJECT))
                {
                    incoming.setSchooiReviewResult(SCHOOL_REJECT);
                    incoming.setSchoolReviewReason(AUTO_SCHOOL_REJECT_REASON);
                    incoming.setSchoolAuditBy(AUTO_SCHOOL_REJECT_AUDITOR);
                    incoming.setSchoolReviewedAt(DateUtils.getNowDate());
                }
            }
            return;
        }

        // 校级操作逻辑
        if (stageSchool)
        {
            if (schoolChanged)
            {
                if (existingSchool == null)
                {
                    if (!Objects.equals(requestedSchool, SCHOOL_PENDING))
                    {
                        throw new ServiceException("校级审核必须从待审核开始。");
                    }
                }
                else if (Objects.equals(existingSchool, SCHOOL_PENDING))
                {
                    if (!isSchoolReviewedValue(requestedSchool))
                    {
                        throw new ServiceException("校级审核只能修改为通过或驳回。");
                    }
                    if (!Objects.equals(existingCollege, COLLEGE_PASS))
                    {
                        throw new ServiceException("校级审核要求院级已通过。");
                    }
                    if (Objects.equals(requestedSchool, SCHOOL_REJECT))
                    {
                        if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
                        {
                            throw new ServiceException("校级驳回必须提供原因。");
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
                        throw new ServiceException("校级审核状态在已审核后不可更改。");
                    }
                    if (!isSchoolReviewedValue(requestedSchool))
                    {
                        throw new ServiceException("校级审核只能修改为通过或驳回。");
                    }
                    if (!Objects.equals(existingCollege, COLLEGE_PASS))
                    {
                        throw new ServiceException("校级审核要求院级已通过。");
                    }
                    if (Objects.equals(requestedSchool, SCHOOL_REJECT))
                    {
                        if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
                        {
                            throw new ServiceException("校级驳回必须提供原因。");
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
            throw new ServiceException("无效的审核阶段更新。");
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

    private Long normalizeExistingSchoolStatus(reviewed existing)
    {
        Long raw = existing.getSchooiReviewResult();
        if (isSchoolReviewedValue(raw) && !hasSchoolReviewTrace(existing))
        {
            // 兼容旧数据，状态初始化为0/1但未实际审核
            return SCHOOL_PENDING;
        }
        return raw;
    }

    private boolean hasSchoolReviewTrace(reviewed existing)
    {
        return hasEffectiveText(existing.getSchoolAuditBy())
                || hasEffectiveText(existing.getSchoolReviewReason());
    }

    private boolean hasEffectiveText(String value)
    {
        if (!StringUtils.hasText(value))
        {
            return false;
        }
        String normalized = value.trim().toLowerCase();
        return !"null".equals(normalized) && !"undefined".equals(normalized);
    }
}