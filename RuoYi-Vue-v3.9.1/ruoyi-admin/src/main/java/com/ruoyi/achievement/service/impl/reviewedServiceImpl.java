package com.ruoyi.achievement.service.impl;

import java.util.*;

import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.core.domain.entity.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.reviewedMapper;
import com.ruoyi.achievement.domain.reviewed;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.achievement.service.IreviewedService;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 成果审核 Service 实现
 */
@Service
public class reviewedServiceImpl implements IreviewedService
{
    @Autowired
    private reviewedMapper reviewedMapper;

    @Autowired
    private ISamStudentService samStudentService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private com.ruoyi.system.service.AchievementPostPermissionService postPermissionService;

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
    private static final String SOURCE_COLLEGE_UNREVIEWED = "college_level_unreviewed";
    private static final String SOURCE_COLLEGE_REVIEWED = "college_level_reviewed";
    private static final String SOURCE_SCHOOL_UNREVIEWED = "school_level_unreviewed";
    private static final String SOURCE_SCHOOL_REVIEWED = "school_level_reviewed";
    private static final String AUTO_SCHOOL_REJECT_REASON = "因院级状态变更自动驳回";
    private static final String AUTO_SCHOOL_REJECT_AUDITOR = "system";


    @Override
    public reviewed selectreviewedByAchievementId(String achievementId)
    {
        return reviewedMapper.selectreviewedByAchievementId(achievementId);
    }

    @Override
    public List<reviewed> selectreviewedList(reviewed reviewed)
    {
        // 如果是超级管理员（userId=1），不执行任何数据过滤
        if (SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            return reviewedMapper.selectreviewedList(reviewed);
        }

        // 针对院级审核页面的数据过滤逻辑
        if (STAGE_COLLEGE.equals(reviewed.getReviewStage())) {
            // 优先检查是否具有校级审核权限（岗位或角色）
            String userName = SecurityUtils.getUsername();
            boolean hasSchoolReview = postPermissionService.canAccessSchoolReview(userName)
                                   || SecurityUtils.hasRole("schoolleveladmin")
                                   || SecurityUtils.hasRole("schooladmin");

            // 如果没有校级权限，才进一步判断是否为院级管理员并进行过滤
            if (!hasSchoolReview) {
                // 判断是否具有 院级管理员 角色或岗位
                boolean isCollegeAdmin = SecurityUtils.hasRole("collegeleveladmin") || SecurityUtils.hasRole("collegeadmin")
                                      || postPermissionService.canAccessCollegeReview(userName);

                if (isCollegeAdmin) {
                    // 获取当前登录人的部门 ID
                    Long userDeptId = SecurityUtils.getDeptId();
                    if (userDeptId != null) {
                        // 向上追溯到学院 ID
                        Long collegeId = deptService.getCollegeId(userDeptId);
                        if (collegeId != null) {
                            // 数据库 owner_dep_id 存储的是 ID 数字，这里设置 ID 过滤
                            reviewed.setOwnerDepId(String.valueOf(collegeId));
                        }
                    }
                }
            }
        }
        return reviewedMapper.selectreviewedList(reviewed);
    }

    @Transactional
    @Override
    @BizAudit(bizType = "achievement_review", bizName = "新增成果审核", opType = BizAuditOpType.ADD, handler = "achievementBizAuditHandler", async = false)
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
        applyDefaultOwnerDepId(reviewed);
        if (!StringUtils.hasText(reviewed.getOwnerDepId()))
        {
            throw new ServiceException("归属学院不能为空");
        }
        sanitizeReasons(reviewed);
        validateInsertByStage(reviewed, stage, status);
        reviewed.setCreateTime(DateUtils.getNowDate());
        int rows = reviewedMapper.insertreviewed(reviewed);
        insertSamAchievementParticipant(reviewed);
        return rows;
    }

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
        if (reviewed.getSamAchievementParticipantList() != null)
        {
            reviewedMapper.deleteSamAchievementParticipantByParticipantId(reviewed.getAchievementId());
            insertSamAchievementParticipant(reviewed);
        }
        return reviewedMapper.updatereviewed(reviewed);
    }

    @Transactional
    @Override
    @BizAudit(bizType = "achievement_review", bizName = "批量删除成果审核", opType = BizAuditOpType.DELETE, handler = "achievementBizAuditHandler", async = false)
    public int deletereviewedByAchievementIds(String[] achievementIds)
    {
        reviewedMapper.deleteSamAchievementParticipantByParticipantIds(achievementIds);
        return reviewedMapper.deletereviewedByAchievementIds(achievementIds);
    }

    @Transactional
    @Override
    @BizAudit(bizType = "achievement_review", bizName = "删除成果审核", opType = BizAuditOpType.DELETE, handler = "achievementBizAuditHandler", async = false)
    public int deletereviewedByAchievementId(String achievementId)
    {
        reviewedMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        return reviewedMapper.deletereviewedByAchievementId(achievementId);
    }

    @Transactional
    @Override
    @BizAudit(bizType = "achievement_review", bizName = "提交成果审核", opType = BizAuditOpType.UPDATE, handler = "achievementBizAuditHandler", async = false)
    public reviewed submitReview(String source, String achievementId, Long reviewStatus, String rejectReason)
    {
        return submitReviewInternal(source, achievementId, reviewStatus, rejectReason);
    }

    @Transactional
    @Override
    @BizAudit(bizType = "achievement_review", bizName = "批量提交成果审核", opType = BizAuditOpType.UPDATE, handler = "achievementBizAuditHandler", async = false)
    public int batchSubmitReview(String source, String[] achievementIds, Long reviewStatus, String rejectReason)
    {
        ReviewSourceContext context = resolveSourceContext(source);
        if (achievementIds == null || achievementIds.length == 0)
        {
            throw new ServiceException("请选择需要批量审核的成果");
        }

        LinkedHashSet<String> uniqueIds = new LinkedHashSet<String>();
        for (String achievementId : achievementIds)
        {
            if (StringUtils.hasText(achievementId))
            {
                uniqueIds.add(achievementId.trim());
            }
        }

        if (uniqueIds.isEmpty())
        {
            throw new ServiceException("请选择需要批量审核的成果");
        }

        String normalizedRejectReason = StringUtils.hasText(rejectReason) ? rejectReason.trim() : null;
        validateTargetStatus(context, reviewStatus, normalizedRejectReason);

        String[] normalizedIds = uniqueIds.toArray(new String[0]);
        List<reviewed> existingList = reviewedMapper.selectreviewedByAchievementIds(normalizedIds);
        Map<String, reviewed> existingMap = new HashMap<String, reviewed>();
        for (reviewed item : existingList)
        {
            if (item != null && StringUtils.hasText(item.getAchievementId()))
            {
                existingMap.put(item.getAchievementId().trim(), item);
            }
        }

        for (String achievementId : uniqueIds)
        {
            reviewed existing = existingMap.get(achievementId);
            if (existing == null)
            {
                throw new ServiceException("未找到对应的成果审核信息");
            }
            validateSourceEntryState(context, existing);
        }

        String operator = resolveOperatorName();
        if (STAGE_COLLEGE.equals(context.getStage()))
        {
            return reviewedMapper.batchUpdateCollegeReviewStatus(normalizedIds, reviewStatus, normalizedRejectReason, operator);
        }
        return reviewedMapper.batchUpdateSchoolReviewStatus(normalizedIds, reviewStatus, normalizedRejectReason, operator);
    }

    private reviewed submitReviewInternal(String source, String achievementId, Long reviewStatus, String rejectReason)
    {
        ReviewSourceContext context = resolveSourceContext(source);
        if (!StringUtils.hasText(achievementId))
        {
            throw new ServiceException("成果ID不能为空");
        }
        if (reviewStatus == null)
        {
            throw new ServiceException("审核状态不能为空");
        }

        String normalizedAchievementId = achievementId.trim();
        String normalizedRejectReason = StringUtils.hasText(rejectReason) ? rejectReason.trim() : null;
        validateTargetStatus(context, reviewStatus, normalizedRejectReason);

        reviewed existing = reviewedMapper.selectreviewedByAchievementId(normalizedAchievementId);
        if (existing == null)
        {
            throw new ServiceException("未找到对应的成果审核信息");
        }

        validateSourceEntryState(context, existing);

        reviewed update = buildReviewUpdate(context, normalizedAchievementId, reviewStatus, normalizedRejectReason);
        updatereviewed(update);
        return reviewedMapper.selectreviewedByAchievementId(normalizedAchievementId);
    }

    private void validateTargetStatus(ReviewSourceContext context, Long reviewStatus, String rejectReason)
    {
        if (SOURCE_COLLEGE_UNREVIEWED.equals(context.getSource()))
        {
            if (!Objects.equals(reviewStatus, COLLEGE_REJECT) && !Objects.equals(reviewStatus, COLLEGE_PASS))
            {
                throw new ServiceException("院级未审核页面仅允许提交通过或驳回");
            }
            if (Objects.equals(reviewStatus, COLLEGE_REJECT) && !StringUtils.hasText(rejectReason))
            {
                throw new ServiceException("请输入院级驳回原因");
            }
            return;
        }

        if (SOURCE_COLLEGE_REVIEWED.equals(context.getSource()))
        {
            if (!Objects.equals(reviewStatus, COLLEGE_PENDING)
                    && !Objects.equals(reviewStatus, COLLEGE_REJECT)
                    && !Objects.equals(reviewStatus, COLLEGE_PASS))
            {
                throw new ServiceException("院级已审核页面仅允许提交待审核、通过或驳回");
            }
            if (Objects.equals(reviewStatus, COLLEGE_REJECT) && !StringUtils.hasText(rejectReason))
            {
                throw new ServiceException("请输入院级驳回原因");
            }
            return;
        }

        if (!Objects.equals(reviewStatus, SCHOOL_REJECT) && !Objects.equals(reviewStatus, SCHOOL_PASS))
        {
            throw new ServiceException("校级审核页面仅允许提交通过或驳回");
        }
        if (Objects.equals(reviewStatus, SCHOOL_REJECT) && !StringUtils.hasText(rejectReason))
        {
            throw new ServiceException("请输入校级驳回原因");
        }
    }

    private void validateSourceEntryState(ReviewSourceContext context, reviewed existing)
    {
        Long currentCollege = existing.getReviewResult() == null ? COLLEGE_PENDING : existing.getReviewResult();
        Long currentSchool = normalizeExistingSchoolStatus(existing);

        if (SOURCE_COLLEGE_UNREVIEWED.equals(context.getSource()))
        {
            if (!Objects.equals(currentCollege, COLLEGE_PENDING))
            {
                throw new ServiceException("该成果当前不在院级未审核列表，不能按院级未审核流程提交");
            }
            return;
        }

        if (SOURCE_COLLEGE_REVIEWED.equals(context.getSource()))
        {
            if (!isCollegeReviewedValue(currentCollege))
            {
                throw new ServiceException("该成果当前不在院级已审核列表，不能按院级已审核流程提交");
            }
            return;
        }

        if (!Objects.equals(currentCollege, COLLEGE_PASS))
        {
            throw new ServiceException("校级审核前必须院级通过");
        }

        if (SOURCE_SCHOOL_UNREVIEWED.equals(context.getSource()))
        {
            if (!Objects.equals(currentSchool, SCHOOL_PENDING))
            {
                throw new ServiceException("该成果当前不在校级未审核列表，不能按校级未审核流程提交");
            }
            return;
        }

        if (!isSchoolReviewedValue(currentSchool))
        {
            throw new ServiceException("该成果当前不在校级已审核列表，不能按校级已审核流程提交");
        }
    }

    private reviewed buildReviewUpdate(ReviewSourceContext context, String achievementId, Long reviewStatus, String rejectReason)
    {
        String operator = resolveOperatorName();
        reviewed update = new reviewed();
        update.setAchievementId(achievementId);
        update.setReviewStage(context.getStage());
        update.setReviewStatus(context.getStatus());
        update.setUpdateBy(operator);

        if (STAGE_COLLEGE.equals(context.getStage()))
        {
            update.setReviewResult(reviewStatus);
            update.setReviewReason(Objects.equals(reviewStatus, COLLEGE_REJECT) ? rejectReason : null);
            if (Objects.equals(reviewStatus, COLLEGE_PASS))
            {
                update.setSchooiReviewResult(SCHOOL_PENDING);
                update.setSchoolReviewReason(null);
                update.setSchoolAuditBy(null);
                update.setSchoolReviewedAt(null);
            }
            if (!Objects.equals(reviewStatus, COLLEGE_PENDING))
            {
                update.setReviewedAt(DateUtils.getNowDate());
                update.setAuditBy(operator);
            }
            return update;
        }

        update.setSchooiReviewResult(reviewStatus);
        update.setSchoolReviewReason(Objects.equals(reviewStatus, SCHOOL_REJECT) ? rejectReason : null);
        update.setSchoolReviewedAt(DateUtils.getNowDate());
        update.setSchoolAuditBy(operator);
        return update;
    }

    private ReviewSourceContext resolveSourceContext(String source)
    {
        String normalizedSource = StringUtils.hasText(source) ? source.trim().toLowerCase() : "";
        if (SOURCE_COLLEGE_UNREVIEWED.equals(normalizedSource))
        {
            return new ReviewSourceContext(normalizedSource, STAGE_COLLEGE, STATUS_UNREVIEWED);
        }
        if (SOURCE_COLLEGE_REVIEWED.equals(normalizedSource))
        {
            return new ReviewSourceContext(normalizedSource, STAGE_COLLEGE, STATUS_REVIEWED);
        }
        if (SOURCE_SCHOOL_UNREVIEWED.equals(normalizedSource))
        {
            return new ReviewSourceContext(normalizedSource, STAGE_SCHOOL, STATUS_UNREVIEWED);
        }
        if (SOURCE_SCHOOL_REVIEWED.equals(normalizedSource))
        {
            return new ReviewSourceContext(normalizedSource, STAGE_SCHOOL, STATUS_REVIEWED);
        }
        throw new ServiceException("审核来源无效");
    }

    private String resolveOperatorName()
    {
        try
        {
            String username = SecurityUtils.getUsername();
            return StringUtils.hasText(username) ? username : AUTO_SCHOOL_REJECT_AUDITOR;
        }
        catch (Exception e)
        {
            return AUTO_SCHOOL_REJECT_AUDITOR;
        }
    }

    private static class ReviewSourceContext
    {
        private final String source;
        private final String stage;
        private final String status;

        ReviewSourceContext(String source, String stage, String status)
        {
            this.source = source;
            this.stage = stage;
            this.status = status;
        }

        public String getSource()
        {
            return source;
        }

        public String getStage()
        {
            return stage;
        }

        public String getStatus()
        {
            return status;
        }
    }

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
                if (!StringUtils.hasText(samAchievementParticipant.getStudentNo())
                        && StringUtils.hasText(samAchievementParticipant.getStudentId()))
                {
                    samAchievementParticipant.setStudentNo(samAchievementParticipant.getStudentId());
                }

                checkAndInsertStudent(
                        samAchievementParticipant.getStudentNo(),
                        samAchievementParticipant.getStudentName());
                list.add(samAchievementParticipant);
            }
            if (list.size() > 0)
            {
                reviewedMapper.batchSamAchievementParticipant(list);
            }
        }
    }

    private void checkAndInsertStudent(String studentNo, String studentName)
    {
        if (!StringUtils.hasText(studentNo) || !StringUtils.hasText(studentName))
        {
            return;
        }

        if (!studentNo.matches("^[a-zA-Z0-9]{4,20}$"))
        {
            throw new ServiceException("非法学号格式：" + studentNo);
        }

        if (studentName.length() > 50 || studentName.contains("<") || studentName.contains(">"))
        {
            throw new ServiceException("非法学生姓名");
        }

        SamStudent query = new SamStudent();
        query.setNo(studentNo);
        List<SamStudent> exists = samStudentService.selectSamStudentList(query);

        if (exists == null || exists.isEmpty())
        {
            SamStudent newStudent = new SamStudent();
            newStudent.setNo(studentNo);
            newStudent.setName(studentName);
            samStudentService.insertSamStudent(newStudent);
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
        Date awardTime = incoming.getAwardTime();
        if (awardTime == null)
        {
            return;
        }
        if (SecurityUtils.isAdmin(SecurityUtils.getUserId()) && incoming.getYear() != null)
        {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(awardTime);
        incoming.setYear((long) calendar.get(Calendar.YEAR));
    }

    private void applyDefaultOwnerDepId(reviewed incoming)
    {
        if (StringUtils.hasText(incoming.getOwnerDepId()))
        {
            incoming.setOwnerDepId(normalizeDeptId(incoming.getOwnerDepId()));
            return;
        }

        // 尝试从参与人（负责人）中获取学院
        List<SamAchievementParticipant> participantList = incoming.getSamAchievementParticipantList();
        if (participantList != null && !participantList.isEmpty())
        {
            SamAchievementParticipant managerParticipant = null;
            for (SamAchievementParticipant participant : participantList)
            {
                if ("1".equals(StringUtils.trim(participant.getManager())))
                {
                    managerParticipant = participant;
                    break;
                }
            }

            if (managerParticipant == null)
            {
                managerParticipant = participantList.get(0);
            }

            String ownerDepId = resolveOwnerDepIdFromParticipant(managerParticipant);
            if (StringUtils.hasText(ownerDepId))
            {
                incoming.setOwnerDepId(ownerDepId);
                return;
            }
        }

        // 兜底方案：从当前登录人的部门获取学院
        try {
            Long userDeptId = SecurityUtils.getDeptId();
            if (userDeptId != null) {
                Long collegeId = deptService.getCollegeId(userDeptId);
                if (collegeId != null) {
                    incoming.setOwnerDepId(String.valueOf(collegeId));
                }
            }
        } catch (Exception e) {
            // 忽略非登录环境下的异常
        }
    }

    private String resolveOwnerDepIdFromParticipant(SamAchievementParticipant participant)
    {
        if (participant == null)
        {
            return null;
        }

        if (StringUtils.hasText(participant.getStudentId()))
        {
            SamStudent query = new SamStudent();
            query.setNo(participant.getStudentId().trim());
            List<SamStudent> students = samStudentService.selectSamStudentList(query);
            if (students != null && !students.isEmpty())
            {
                String ownerDepId = normalizeDeptId(students.get(0).getSchool());
                if (StringUtils.hasText(ownerDepId))
                {
                    return ownerDepId;
                }
            }
        }

        String ownerDepId = normalizeDeptId(participant.getSchool());
        if (StringUtils.hasText(ownerDepId))
        {
            return ownerDepId;
        }

        return null;
    }

    /**
     * 根据部门ID向上查找归属学院ID (parent_id 为 100 的部门)
     */
    private String getCollegeIdByAnyDeptId(String deptIdStr) {
        if (!StringUtils.hasText(deptIdStr) || !deptIdStr.matches("\\d+")) {
            return null;
        }
        Long collegeId = deptService.getCollegeId(Long.valueOf(deptIdStr));
        return collegeId != null ? String.valueOf(collegeId) : null;
    }

    private String normalizeDeptId(String schoolValue)
    {
        if (!StringUtils.hasText(schoolValue))
        {
            return null;
        }

        String normalized = schoolValue.trim();
        String foundId = null;

        if (normalized.matches("\\d+"))
        {
            foundId = normalized;
        } else {
            SysDept query = new SysDept();
            query.setDeptName(normalized);
            List<SysDept> deptList = deptService.selectDeptList(query);
            if (deptList != null && !deptList.isEmpty())
            {
                for (SysDept dept : deptList)
                {
                    if (dept != null && StringUtils.equals(normalized, dept.getDeptName()) && dept.getDeptId() != null)
                    {
                        foundId = String.valueOf(dept.getDeptId());
                        break;
                    }
                }
            }
        }

        // 统一向上追溯到学院级 (parent_id = 100)
        return getCollegeIdByAnyDeptId(foundId);
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
                throw new ServiceException("院级未审核页面新增时，院级状态只能为待审核");
            }
            if (incoming.getSchooiReviewResult() != null)
            {
                throw new ServiceException("院级未审核页面新增时，不允许直接设置校级审核状态");
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
                throw new ServiceException("院级已审核页面新增时，院级状态只能为通过或驳回");
            }
            if (Objects.equals(reviewResult, COLLEGE_REJECT))
            {
                if (!StringUtils.hasText(incoming.getReviewReason()))
                {
                    throw new ServiceException("院级驳回时必须提供原因");
                }
                if (incoming.getSchooiReviewResult() != null)
                {
                    throw new ServiceException("院级驳回时不允许直接设置校级审核状态");
                }
            }
            else
            {
                incoming.setReviewReason(null);
                Long schoolResult = incoming.getSchooiReviewResult();
                if (schoolResult != null && !Objects.equals(schoolResult, SCHOOL_PENDING))
                {
                    throw new ServiceException("院级通过时，校级状态只能为待审核");
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
                throw new ServiceException("校级未审核页面新增时，校级状态只能为待审核");
            }
            Long college = incoming.getReviewResult();
            if (college != null && !Objects.equals(college, COLLEGE_PENDING))
            {
                throw new ServiceException("校级未审核页面新增时，院级状态只能为待审核");
            }
            incoming.setReviewReason(null);
            incoming.setSchoolReviewReason(null);
            return;
        }

        if (STAGE_SCHOOL.equals(stage) && STATUS_REVIEWED.equals(status))
        {
            if (!Objects.equals(incoming.getReviewResult(), COLLEGE_PASS))
            {
                throw new ServiceException("校级已审核页面新增时，院级状态必须为通过");
            }
            Long schoolResult = incoming.getSchooiReviewResult();
            if (!isSchoolReviewedValue(schoolResult))
            {
                throw new ServiceException("校级已审核页面新增时，校级状态只能为通过或驳回");
            }
            if (Objects.equals(schoolResult, SCHOOL_REJECT))
            {
                if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
                {
                    throw new ServiceException("校级驳回时必须提供原因");
                }
            }
            else
            {
                incoming.setSchoolReviewReason(null);
            }
            incoming.setReviewReason(null);
            return;
        }

        throw new ServiceException("新增操作的审核阶段或审核状态不合法");
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
        Long existingSchool = normalizeExistingSchoolStatus(existing);
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
            if (incoming.getSchoolReviewReason() != null
                    && !Objects.equals(incoming.getSchoolReviewReason(), existing.getSchoolReviewReason()))
            {
                incoming.setSchoolReviewReason(existing.getSchoolReviewReason());
            }
        }
        requestedCollege = incoming.getReviewResult();
        requestedSchool = incoming.getSchooiReviewResult();
        boolean resetSchoolOnCollegePass = stageCollege
                && requestedCollege != null
                && !Objects.equals(requestedCollege, existingCollege)
                && Objects.equals(requestedCollege, COLLEGE_PASS);
        if (resetSchoolOnCollegePass)
        {
            incoming.setSchooiReviewResult(SCHOOL_PENDING);
            incoming.setSchoolReviewReason(null);
            incoming.setSchoolAuditBy(null);
            incoming.setSchoolReviewedAt(null);
            requestedSchool = incoming.getSchooiReviewResult();
        }
        boolean allowCollegeReAudit = stageCollege
                && isCollegeReviewedValue(existingCollege)
                && requestedCollege != null
                && (Objects.equals(requestedCollege, COLLEGE_PENDING) || isCollegeReviewedValue(requestedCollege));
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
            if (collegeChanged)
            {
                if (!Objects.equals(existingCollege, COLLEGE_PENDING) && !allowCollegeReAudit)
                {
                    throw new ServiceException("院级审核状态在已审核后不可修改");
                }
                if (!Objects.equals(requestedCollege, COLLEGE_PENDING) && !isCollegeReviewedValue(requestedCollege))
                {
                    throw new ServiceException("院级审核只能修改为待审核、通过或驳回");
                }
                if (Objects.equals(requestedCollege, COLLEGE_REJECT))
                {
                    if (!StringUtils.hasText(incoming.getReviewReason()))
                    {
                        throw new ServiceException("院级驳回必须提供原因");
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
                Long effectiveCollege = collegeChanged ? requestedCollege : existingCollege;
                if (Objects.equals(effectiveCollege, COLLEGE_PASS))
                {
                    if (!Objects.equals(requestedSchool, SCHOOL_PENDING)
                            && !Objects.equals(requestedSchool, existingSchool))
                    {
                        throw new ServiceException("院级通过后，校级审核状态只能保持或设置为待审核");
                    }
                }
                else
                {
                    if (!Objects.equals(requestedSchool, SCHOOL_REJECT))
                    {
                        throw new ServiceException("院级待审核或驳回时，校级审核自动驳回");
                    }
                }
            }
            else if (!resetSchoolOnCollegePass && !StringUtils.hasText(incoming.getSchoolReviewReason()))
            {
                incoming.setSchoolReviewReason(existing.getSchoolReviewReason());
            }
            if (requestedCollege != null)
            {
                if (Objects.equals(requestedCollege, COLLEGE_PASS))
                {
                    incoming.setSchooiReviewResult(SCHOOL_PENDING);
                    incoming.setSchoolReviewReason(null);
                    incoming.setSchoolAuditBy(null);
                    incoming.setSchoolReviewedAt(null);
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
        if (stageSchool)
        {
            if (schoolChanged)
            {
                if (existingSchool == null)
                {
                    if (!Objects.equals(requestedSchool, SCHOOL_PENDING))
                    {
                        throw new ServiceException("校级审核必须从待审核开始");
                    }
                }
                else if (Objects.equals(existingSchool, SCHOOL_PENDING))
                {
                    if (!isSchoolReviewedValue(requestedSchool))
                    {
                        throw new ServiceException("校级审核只能修改为通过或驳回");
                    }
                    if (!Objects.equals(existingCollege, COLLEGE_PASS))
                    {
                        throw new ServiceException("校级审核要求院级已通过");
                    }
                    if (Objects.equals(requestedSchool, SCHOOL_REJECT))
                    {
                        if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
                        {
                            throw new ServiceException("校级驳回必须提供原因");
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
                        throw new ServiceException("校级审核状态在已审核后不可修改");
                    }
                    if (!isSchoolReviewedValue(requestedSchool))
                    {
                        throw new ServiceException("校级审核只能修改为通过或驳回");
                    }
                    if (!Objects.equals(existingCollege, COLLEGE_PASS))
                    {
                        throw new ServiceException("校级审核要求院级已通过");
                    }
                    if (Objects.equals(requestedSchool, SCHOOL_REJECT))
                    {
                        if (!StringUtils.hasText(incoming.getSchoolReviewReason()))
                        {
                            throw new ServiceException("校级驳回必须提供原因");
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
            throw new ServiceException("当前审核阶段不允许修改审核状态");
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
