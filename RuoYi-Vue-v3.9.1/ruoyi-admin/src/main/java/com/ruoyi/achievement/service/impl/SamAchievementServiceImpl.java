package com.ruoyi.achievement.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.achievement.domain.ExportAchievementBaseVo;
import com.ruoyi.achievement.domain.ExportAttachmentFileVo;
import com.ruoyi.achievement.domain.ExportAttachmentZipReq;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.domain.SamAchievementAttachment;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.domain.SamAchievementAdvisor;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.achievement.mapper.SamAchievementMapper;
import com.ruoyi.achievement.service.ISamAchievementService;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.achievement.service.ISamTeacherService;
import com.ruoyi.achievement.mapper.FileUuidMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.exception.ServiceException;

/**
 * 成果录入Service业务层处理
 *
 * @author 王璨
 * @date 2026-02-03
 */
@Service
public class SamAchievementServiceImpl implements ISamAchievementService
{
    private static final Logger log = LoggerFactory.getLogger(SamAchievementServiceImpl.class);

    private static final Map<Integer, String> ATTACHMENT_TYPE_NAME_MAP = buildAttachmentTypeNameMap();

    @Autowired
    private SamAchievementMapper samAchievementMapper;

    @Autowired
    private ISamStudentService samStudentService;

    @Autowired
    private ISamTeacherService samTeacherService;

    @Autowired
    private FileUuidMapper fileUuidMapper;

    private static Map<Integer, String> buildAttachmentTypeNameMap()
    {
        Map<Integer, String> typeNameMap = new LinkedHashMap<>();
        typeNameMap.put(1, "奖状(证书)");
        typeNameMap.put(2, "比赛通知");
        typeNameMap.put(3, "参赛作品");
        typeNameMap.put(4, "支付记录");
        typeNameMap.put(5, "正规发票");
        typeNameMap.put(6, "收款码");
        return typeNameMap;
    }

    /**
     * 查询成果录入
     *
     * @param achievementId 成果录入主键
     * @return 成果录入
     */
    @Override
    public SamAchievement selectSamAchievementByAchievementId(String achievementId)
    {
        return samAchievementMapper.selectSamAchievementByAchievementId(achievementId);
    }

    @Override
    public SamAchievement selectSamAchievementForSelf(String achievementId, String selfEditScene)
    {
        SamAchievement existing = samAchievementMapper.selectSamAchievementByAchievementId(achievementId);
        if (existing == null)
        {
            throw new ServiceException("Achievement not found");
        }

        String normalizedScene = normalizeSelfEditScene(selfEditScene);
        if (!isSelfEditScene(normalizedScene))
        {
            throw new ServiceException("Invalid self edit scene");
        }

        validateSelfUpdatePermission(existing, normalizedScene);
        return existing;
    }

    /**
     * 查询成果录入列表
     *
     * @param samAchievement 成果录入
     * @return 成果录入
     */
    @Override
    public List<SamAchievement> selectSamAchievementList(SamAchievement samAchievement)
    {
        return samAchievementMapper.selectSamAchievementList(samAchievement);
    }

    @Override
    public List<SamAchievement> selectSamAchievementListByStudentId(SamAchievement samAchievement)
    {
        // 验证学生ID
        if (samAchievement.getParams() == null || StringUtils.isEmpty((String) samAchievement.getParams().get("studentId"))) {
            throw new ServiceException("学生ID不能为空");
        }
        return samAchievementMapper.selectSamAchievementListByStudentId(samAchievement);
    }

    @Override
    public List<SamAchievement> selectSamAchievementListByTeacherId(SamAchievement samAchievement)
    {
        // 验证教师ID
        if (samAchievement.getParams() == null || StringUtils.isEmpty((String) samAchievement.getParams().get("teacherId"))) {
            throw new ServiceException("教师ID不能为空");
        }
        return samAchievementMapper.selectSamAchievementListByTeacherId(samAchievement);
    }
    @Override
    public List<SamAchievement> selectSamAchievementListByResponsibleStudentId(SamAchievement samAchievement)
    {
        if (samAchievement.getParams() == null || StringUtils.isEmpty((String) samAchievement.getParams().get("studentId"))) {
            throw new ServiceException("学生ID不能为空");
        }
        return samAchievementMapper.selectSamAchievementListByResponsibleStudentId(samAchievement);
    }

    @Override
    public List<SamAchievement> selectSamAchievementListByUserId(SamAchievement samAchievement)
    {
        // 验证用户ID
        if (samAchievement.getParams() == null || StringUtils.isEmpty((String) samAchievement.getParams().get("userId"))) {
            throw new ServiceException("用户ID不能为空");
        }
        return samAchievementMapper.selectSamAchievementListByUserId(samAchievement);
    }


    /**
     * 新增成果录入
     *
     * @param samAchievement 成果录入
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSamAchievement(SamAchievement samAchievement)
    {
        // 1. 验证成果录入主表信息
        validateSamAchievement(samAchievement);

        // 2. 验证PDF文件上传
        validatePDFAttachments(samAchievement);

        // 3. 自动提取年份
        if (samAchievement.getAwardTime() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(samAchievement.getAwardTime());
            samAchievement.setYear((long) cal.get(Calendar.YEAR));
        }

        // 4. 设置所属学院 (选取参赛选手第一个负责人的所属学院)
        if (samAchievement.getSamAchievementParticipantList() != null) {
            for (SamAchievementParticipant participant : samAchievement.getSamAchievementParticipantList()) {
                if ("1".equals(participant.getManager())) {
                    // 如果前端传了学院信息，直接使用
                    if (StringUtils.isNotEmpty(participant.getSchool())) {
                        samAchievement.setOwnerDepId(participant.getSchool());
                    } else if (StringUtils.isNotEmpty(participant.getStudentId())) {
                        // 如果前端没传，根据学号查档案
                        SamStudent query = new SamStudent();
                        query.setNo(participant.getStudentId());
                        List<SamStudent> students = samStudentService.selectSamStudentList(query);
                        if (students != null && !students.isEmpty() && StringUtils.isNotEmpty(students.get(0).getSchool())) {
                            samAchievement.setOwnerDepId(students.get(0).getSchool());
                        }
                    }
                    break;
                }
            }
        }

        samAchievement.setCreateTime(DateUtils.getNowDate());

        // 5. 插入主表
        int rows = samAchievementMapper.insertSamAchievement(samAchievement);

        // 5. 处理参赛选手 (包含自动补录学生档案)
        insertSamAchievementParticipant(samAchievement);

        // 6. 处理指导老师 (包含自动补录教师档案)
        insertSamAchievementAdvisor(samAchievement);

        // 7. 处理附件转正 (标记为非临时文件)
        processAttachments(samAchievement);

        return rows;
    }

    /**
     * 修改成果录入
     *
     * @param samAchievement 成果录入
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSamAchievement(SamAchievement samAchievement)
    {
        SamAchievement existing = samAchievementMapper.selectSamAchievementByAchievementId(samAchievement.getAchievementId());
        if (existing == null)
        {
            throw new ServiceException("成果不存在");
        }
        // 1. 验证成果录入主表信息
        validateSamAchievement(samAchievement);

        // 2. 验证PDF文件上传
        validatePDFAttachments(samAchievement);

        String selfEditScene = resolveSelfEditScene(samAchievement);

        if (isSelfEditScene(selfEditScene))
        {
            validateSelfUpdatePermission(existing, selfEditScene);
            validateSelfUpdateStatus(existing);
            resetReviewStateForSelfUpdate(samAchievement);
        }

        samAchievement.setUpdateTime(DateUtils.getNowDate());

        // 3. 处理参赛选手：先删后加
        samAchievementMapper.deleteSamAchievementParticipantByParticipantId(samAchievement.getAchievementId());
        insertSamAchievementParticipant(samAchievement);

        // 4. 处理指导老师：先删后加
        samAchievementMapper.deleteSamAchievementAdvisorByAchievementId(samAchievement.getAchievementId());
        insertSamAchievementAdvisor(samAchievement);

        // 5. 处理附件：先删后加
        samAchievementMapper.deleteSamAchievementAttachmentByAchievementId(samAchievement.getAchievementId());
        processAttachments(samAchievement);

        return samAchievementMapper.updateSamAchievement(samAchievement);
    }

    private String resolveSelfEditScene(SamAchievement samAchievement)
    {
        if (samAchievement.getParams() == null) {
            return null;
        }
        Object value = samAchievement.getParams().get("selfEditScene");
        return normalizeSelfEditScene(value == null ? null : String.valueOf(value));
    }

    private String normalizeSelfEditScene(String scene)
    {
        if (scene == null) {
            return null;
        }
        String normalized = scene.trim().toLowerCase();
        return StringUtils.isEmpty(normalized) ? null : normalized;
    }

    private boolean isSelfEditScene(String scene)
    {
        return "responsible".equals(scene) || "guided".equals(scene);
    }

    private void validateSelfUpdatePermission(SamAchievement existing, String selfEditScene)
    {
        String loginName = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(loginName))
        {
            throw new ServiceException("当前登录账号无效");
        }

        if ("responsible".equals(selfEditScene))
        {
            if (!isResponsibleStudent(existing, loginName))
            {
                throw new ServiceException("只有成果负责人才能修改自己的成果");
            }
            return;
        }

        if ("guided".equals(selfEditScene))
        {
            if (!isGuidingTeacher(existing, loginName))
            {
                throw new ServiceException("只有指导教师才能修改自己指导的成果");
            }
            return;
        }

        throw new ServiceException("不支持的自助修改场景");
    }

    private void validateSelfUpdateStatus(SamAchievement existing)
    {
        Long college = existing.getReviewResult() == null ? 0L : existing.getReviewResult();
        Long school = existing.getSchooiReviewResult();

        boolean canEditCollegePending = Objects.equals(college, 0L);
        boolean canEditCollegeRejected = Objects.equals(college, 1L);
        boolean canEditSchoolPending = Objects.equals(college, 2L)
                && (school == null || Objects.equals(school, 2L));
        boolean canEditSchoolRejected = Objects.equals(college, 2L)
                && Objects.equals(school, 0L);

        if (!(canEditCollegePending || canEditCollegeRejected || canEditSchoolPending || canEditSchoolRejected))
        {
            throw new ServiceException("只有待院级审核、院级驳回、待校级审核、校级驳回的成果允许本人修改");
        }
    }

    private boolean isResponsibleStudent(SamAchievement existing, String studentNo)
    {
        List<SamAchievementParticipant> participants = existing.getSamAchievementParticipantList();
        if (participants == null || participants.isEmpty())
        {
            return false;
        }

        for (SamAchievementParticipant participant : participants)
        {
            String currentStudentNo = StringUtils.isNotEmpty(participant.getStudentNo())
                    ? participant.getStudentNo()
                    : participant.getStudentId();
            boolean isCurrentStudent = StringUtils.isNotEmpty(currentStudentNo)
                    && studentNo.equals(currentStudentNo);
            boolean isManager = "1".equals(String.valueOf(participant.getManager()))
                    || Objects.equals(participant.getOrderNo(), 1L);
            if (isCurrentStudent && isManager)
            {
                return true;
            }
        }
        return false;
    }

    private boolean isGuidingTeacher(SamAchievement existing, String teacherNo)
    {
        List<SamAchievementAdvisor> advisors = existing.getSamAchievementAdvisorList();
        if (advisors == null || advisors.isEmpty())
        {
            return false;
        }

        for (SamAchievementAdvisor advisor : advisors)
        {
            String currentTeacherNo = StringUtils.isNotEmpty(advisor.getTeacherNo())
                    ? advisor.getTeacherNo()
                    : advisor.getTeacherId();
            if (StringUtils.isNotEmpty(currentTeacherNo) && teacherNo.equals(currentTeacherNo))
            {
                return true;
            }
        }
        return false;
    }

    private void resetReviewStateForSelfUpdate(SamAchievement samAchievement)
    {
        samAchievement.setReviewResult(0L);
        samAchievement.setReviewedAt(null);
        samAchievement.setReviewReason(null);
        samAchievement.setAuditBy(null);

        samAchievement.setSchooiReviewResult(null);
        samAchievement.setSchoolReviewedAt(null);
        samAchievement.setSchoolReviewReason(null);
        samAchievement.setSchoolAuditBy(null);
    }

    private void preserveReviewState(SamAchievement existing, SamAchievement samAchievement)
    {
        samAchievement.setReviewResult(existing.getReviewResult());
        samAchievement.setReviewedAt(existing.getReviewedAt());
        samAchievement.setReviewReason(existing.getReviewReason());
        samAchievement.setAuditBy(existing.getAuditBy());

        samAchievement.setSchooiReviewResult(existing.getSchooiReviewResult());
        samAchievement.setSchoolReviewedAt(existing.getSchoolReviewedAt());
        samAchievement.setSchoolReviewReason(existing.getSchoolReviewReason());
        samAchievement.setSchoolAuditBy(existing.getSchoolAuditBy());
    }

    /**
     * 验证PDF文件上传
     * @param samAchievement 成果录入
     */
    private void validatePDFAttachments(SamAchievement samAchievement) {
        List<SamAchievementAttachment> attachments = samAchievement.getSamAchievementAttachmentList();
        if (attachments == null || attachments.isEmpty()) {
            throw new ServiceException("请上传必要的PDF文件");
        }

        // 检查基础PDF文件（无论是否报销都需要）
        boolean hasAward = false; // 奖状
        boolean hasNotice = false; // 通知
        boolean hasWork = false; // 作品

        // 检查报销相关PDF文件
        boolean hasPayment = false; // 支付记录
        boolean hasInvoice = false; // 发票
        boolean hasReceipt = false; // 收款码

        for (SamAchievementAttachment attachment : attachments) {
            Integer type = attachment == null ? null : attachment.getType();
            if (type == null) continue;
            switch (type) {
                case 1:
                    hasAward = true;
                    break;
                case 2:
                    hasNotice = true;
                    break;
                case 3:
                    hasWork = true;
                    break;
                case 4:
                    hasPayment = true;
                    break;
                case 5:
                    hasInvoice = true;
                    break;
                case 6:
                    hasReceipt = true;
                    break;
            }
        }

        // 验证基础PDF文件
        if (!hasAward) {
            throw new ServiceException("请上传奖状(证书)PDF文件");
        }
        if (!hasNotice) {
            throw new ServiceException("请上传比赛通知PDF文件");
        }
        if (!hasWork) {
            throw new ServiceException("请上传参赛作品PDF文件");
        }

        // 验证报销相关PDF文件
        if (samAchievement.getIsReimburse() != null && samAchievement.getIsReimburse() == 1) {
            if (!hasPayment) {
                throw new ServiceException("请上传支付记录PDF文件");
            }
            if (!hasInvoice) {
                throw new ServiceException("请上传正规发票PDF文件");
            }
            if (!hasReceipt) {
                throw new ServiceException("请上传收款码PDF文件");
            }
        }
    }

    /**
     * 验证成果录入主表信息
     * @param samAchievement 成果录入
     */
    private void validateSamAchievement(SamAchievement samAchievement) {
        // 验证比赛ID
        if (StringUtils.isEmpty(samAchievement.getCompetitionId())) {
            throw new ServiceException("比赛ID不能为空");
        }

        // 验证获奖时间
        if (samAchievement.getAwardTime() == null) {
            throw new ServiceException("获奖时间不能为空");
        }

        // 验证证书编号
        if (StringUtils.isEmpty(samAchievement.getCertificateNo())) {
            throw new ServiceException("证书编号不能为空");
        }

        // 校验证书编号唯一性
        if (!checkCertificateNoUnique(samAchievement)) {
            throw new ServiceException("证书编号'" + samAchievement.getCertificateNo() + "'已存在");
        }

        // 验证参赛选手列表
        List<SamAchievementParticipant> participants = samAchievement.getSamAchievementParticipantList();
        if (participants == null || participants.isEmpty()) {
            throw new ServiceException("参赛选手列表不能为空");
        }

        // 验证是否至少有一个负责人
        boolean hasManager = false;
        for (SamAchievementParticipant participant : participants) {
            if ("1".equals(participant.getManager())) {
                hasManager = true;
                break;
            }
        }
        if (!hasManager) {
            throw new ServiceException("参赛选手列表中至少需要有一个负责人");
        }

        // 验证报销金额
        if (samAchievement.getIsReimburse() != null && samAchievement.getIsReimburse() == 1) {
            if (samAchievement.getFee() == null) {
                throw new ServiceException("报销金额不能为空");
            }
            if (samAchievement.getFee().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new ServiceException("报销金额必须大于0");
            }
        }
    }

    /**
     * 校验证书编号是否唯一
     *
     * @param samAchievement 成果信息
     * @return 结果
     */
    @Override
    public boolean checkCertificateNoUnique(SamAchievement samAchievement) {
        String achievementId = StringUtils.isEmpty(samAchievement.getAchievementId()) ? "-1" : samAchievement.getAchievementId();
        SamAchievement info = samAchievementMapper.checkCertificateNoUnique(
                samAchievement.getCertificateNo(),
                samAchievement.getCompetitionId());
        if (StringUtils.isNotNull(info) && !info.getAchievementId().equals(achievementId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 附件处理逻辑：
     * 1. 将关联的 UUID 在 sys_file_uuid 表中标记为正式 (is_temp = 0)
     * 2. 将附件关联关系存入 sam_achievement_attachment 表
     */
    private void processAttachments(SamAchievement samAchievement) {
        List<SamAchievementAttachment> attachments = samAchievement.getSamAchievementAttachmentList();
        String achievementId = samAchievement.getAchievementId();

        if (StringUtils.isNotNull(attachments) && attachments.size() > 0) {
            List<String> uuids = new ArrayList<>();
            List<SamAchievementAttachment> insertList = new ArrayList<>();

            for (SamAchievementAttachment attachment : attachments) {
                if (attachment == null) {
                    continue;
                }
                String uuid = attachment.getFileUuid();
                if (StringUtils.isNotEmpty(uuid)) {
                    uuids.add(uuid);

                    attachment.setAchievementId(achievementId);
                    if (attachment.getFileType() == null) {
                        attachment.setFileType(1);
                    }
                    insertList.add(attachment);
                }
            }

            // A. 更新 UUID 状态为正式
            if (uuids.size() > 0) {
                fileUuidMapper
                        .updateFileUuidStatus(uuids.toArray(new String[0]), 0);
            }

            // B. 批量插入附件中间表
            if (insertList.size() > 0) {
                samAchievementMapper.batchSamAchievementAttachment(insertList);
            }
        }
    }

    /**
     * 核心逻辑：插入选手并同步学生档案
     */
    public void insertSamAchievementParticipant(SamAchievement samAchievement)
    {
        List<SamAchievementParticipant> samAchievementParticipantList = samAchievement.getSamAchievementParticipantList();
        // 获取主表自动生成的 ID
        String achievementId = samAchievement.getAchievementId();

        if (StringUtils.isNotNull(samAchievementParticipantList))
        {
            List<SamAchievementParticipant> list = new ArrayList<SamAchievementParticipant>();
            for (SamAchievementParticipant participant : samAchievementParticipantList)
            {
                // 1. 设置外键关联
                participant.setAchievementId(achievementId);

                // 2. 清空子表主键 (让数据库自增)
                participant.setParticipantId(null);

                // 3. 补全基础字段
                participant.setCreateBy(samAchievement.getCreateBy());
                participant.setUpdateBy(samAchievement.getCreateBy());
                participant.setCreateTime(DateUtils.getNowDate());
                participant.setUpdateTime(DateUtils.getNowDate());
                participant.setDelFlag(0L);

                // 核心兼容：如果前端传的是 studentId 而非 studentNo，进行补位
                if (StringUtils.isEmpty(participant.getStudentNo()) && StringUtils.isNotEmpty(participant.getStudentId())) {
                    participant.setStudentNo(participant.getStudentId());
                }

                // 4. 自动补录学生档案
                checkAndInsertStudent(participant.getStudentNo(), participant.getStudentName());

                list.add(participant);
            }
            if (list.size() > 0)
            {
                samAchievementMapper.batchSamAchievementParticipant(list);
            }
        }
    }

    /**
     * 插入指导老师并同步教师档案
     */
    public void insertSamAchievementAdvisor(SamAchievement samAchievement)
    {
        List<SamAchievementAdvisor> samAchievementAdvisorList = samAchievement.getSamAchievementAdvisorList();
        String achievementId = samAchievement.getAchievementId();

        if (StringUtils.isNotNull(samAchievementAdvisorList))
        {
            List<SamAchievementAdvisor> list = new ArrayList<SamAchievementAdvisor>();
            for (SamAchievementAdvisor advisor : samAchievementAdvisorList)
            {
                advisor.setAchievementId(achievementId);
                advisor.setAdvisorId(null);
                advisor.setCreateBy(samAchievement.getCreateBy());
                advisor.setUpdateBy(samAchievement.getCreateBy());
                advisor.setCreateTime(DateUtils.getNowDate());
                advisor.setUpdateTime(DateUtils.getNowDate());
                advisor.setDelFlag(0L);

                // 核心兼容：如果前端传的是 teacherId 而非 teacherNo，进行补位
                if (StringUtils.isEmpty(advisor.getTeacherNo()) && StringUtils.isNotEmpty(advisor.getTeacherId())) {
                    advisor.setTeacherNo(advisor.getTeacherId());
                }

                // 自动补录教师档案
                checkAndInsertTeacher(advisor.getTeacherNo(), advisor.getTeacherName());

                list.add(advisor);
            }
            if (list.size() > 0)
            {
                samAchievementMapper.batchSamAchievementAdvisor(list);
            }
        }
    }

    /**
     * 辅助方法：检查学生是否存在，不存在则新增
     */
    private void checkAndInsertStudent(String studentNo, String studentName) {
        if (StringUtils.isEmpty(studentNo) || StringUtils.isEmpty(studentName)) {
            return;
        }

        // 安全加固：校验学号格式 (只允许字母和数字，长度 4-20)
        if (!studentNo.matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new ServiceException("非法学号格式：" + studentNo);
        }
        // 安全加固：校验姓名格式 (防止脚本注入)
        if (studentName.length() > 50 || studentName.contains("<") || studentName.contains(">")) {
            throw new ServiceException("非法学生姓名");
        }

        SamStudent query = new SamStudent();
        query.setNo(studentNo);
        List<SamStudent> exists = samStudentService.selectSamStudentList(query);

        if (exists == null || exists.isEmpty()) {
            SamStudent newStudent = new SamStudent();
            newStudent.setNo(studentNo);
            newStudent.setName(studentName);
            samStudentService.insertSamStudent(newStudent);
        }
    }

    /**
     * 辅助方法：检查教师是否存在，不存在则新增
     */
    private void checkAndInsertTeacher(String teacherNo, String teacherName) {
        if (StringUtils.isEmpty(teacherNo) || StringUtils.isEmpty(teacherName)) {
            return;
        }

        // 安全加固：校验工号格式
        if (!teacherNo.matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new ServiceException("非法工号格式：" + teacherNo);
        }
        // 安全加固：校验姓名格式
        if (teacherName.length() > 50 || teacherName.contains("<") || teacherName.contains(">")) {
            throw new ServiceException("非法教师姓名");
        }

        SamTeacher query = new SamTeacher();
        query.setNo(teacherNo);
        List<SamTeacher> exists = samTeacherService.selectSamTeacherList(query);

        if (exists == null || exists.isEmpty()) {
            SamTeacher newTeacher = new SamTeacher();
            newTeacher.setNo(teacherNo);
            newTeacher.setTeacherName(teacherName);
            samTeacherService.insertSamTeacher(newTeacher);
        }
    }

    @Override
    public void exportAttachmentZip(ExportAttachmentZipReq req, HttpServletResponse response) throws IOException
    {
        if (req == null)
        {
            throw new ServiceException("导出参数不能为空");
        }

        List<String> achievementIdList = normalizeAchievementIds(req.getAchievementIds());
        if (achievementIdList.isEmpty())
        {
            throw new ServiceException("请选择要导出的成果");
        }

        List<Integer> typeList = normalizeAttachmentTypes(req.getTypes());
        if (typeList.isEmpty())
        {
            throw new ServiceException("请选择至少一个附件类别");
        }

        Long userId = SecurityUtils.getUserId();
        boolean isAdmin = SecurityUtils.isAdmin(userId);
        String loginName = SecurityUtils.getUsername();
        if (!isAdmin && StringUtils.isEmpty(loginName))
        {
            throw new ServiceException("当前登录用户无效");
        }

        List<ExportAchievementBaseVo> authorizedAchievementList =
                samAchievementMapper.selectAuthorizedExportAchievementBase(
                        achievementIdList.toArray(new String[0]),
                        loginName,
                        normalizeSourceMode(req.getSourceMode()),
                        SecurityUtils.hasRole("student"),
                        SecurityUtils.hasRole("teacher"),
                        isAdmin);

        if (authorizedAchievementList == null || authorizedAchievementList.isEmpty())
        {
            throw new ServiceException("当前没有可导出的成果");
        }

        Map<String, String> ownerNameMap = new LinkedHashMap<>();
        for (ExportAchievementBaseVo item : authorizedAchievementList)
        {
            if (item == null || StringUtils.isEmpty(item.getAchievementId()))
            {
                continue;
            }
            if (!ownerNameMap.containsKey(item.getAchievementId()))
            {
                ownerNameMap.put(item.getAchievementId(), defaultFileNamePart(item.getOwnerName(), "未知负责人"));
            }
        }

        if (ownerNameMap.isEmpty())
        {
            throw new ServiceException("当前没有可导出的成果");
        }

        List<ExportAttachmentFileVo> attachmentFiles =
                samAchievementMapper.selectExportAttachmentFiles(
                        ownerNameMap.keySet().toArray(new String[0]),
                        typeList.toArray(new Integer[0]));
        if (attachmentFiles == null)
        {
            attachmentFiles = new ArrayList<>();
        }

        attachmentFiles.sort(Comparator
                .comparingInt((ExportAttachmentFileVo item) -> attachmentTypeSortIndex(typeList, item == null ? null : item.getType()))
                .thenComparing(item -> item == null ? "" : StringUtils.nvl(item.getAchievementId(), ""))
                .thenComparing(item -> item == null ? "" : StringUtils.nvl(item.getFileUuid(), "")));

        response.reset();
        response.setContentType("application/zip");
        FileUtils.setAttachmentResponseHeader(response, "成果附件导出_" + DateUtils.dateTimeNow() + ".zip");

        Set<String> writtenDirectorySet = new HashSet<>();
        Map<String, Integer> nameCounterMap = new HashMap<>();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream(), StandardCharsets.UTF_8))
        {
            for (Integer type : typeList)
            {
                addDirectoryEntry(zipOutputStream, writtenDirectorySet, getAttachmentTypeName(type) + "/");
            }

            for (ExportAttachmentFileVo attachmentFile : attachmentFiles)
            {
                if (attachmentFile == null || StringUtils.isEmpty(attachmentFile.getAchievementId()))
                {
                    continue;
                }

                String typeName = getAttachmentTypeName(attachmentFile.getType());
                if (StringUtils.isEmpty(typeName))
                {
                    continue;
                }

                File sourceFile = resolveAttachmentFile(attachmentFile.getRealPath());
                if (sourceFile == null || !sourceFile.exists() || !sourceFile.isFile())
                {
                    log.warn("Skip missing attachment file, achievementId={}, type={}, fileUuid={}, realPath={}",
                            attachmentFile.getAchievementId(), attachmentFile.getType(),
                            attachmentFile.getFileUuid(), attachmentFile.getRealPath());
                    continue;
                }

                String ownerName = ownerNameMap.get(attachmentFile.getAchievementId());
                String baseFileName = buildAttachmentExportBaseName(
                        attachmentFile.getAchievementId(),
                        ownerName,
                        typeName);
                String entryName = resolveUniqueEntryName(nameCounterMap, typeName, baseFileName);

                boolean opened = false;
                try (InputStream inputStream = new BufferedInputStream(new FileInputStream(sourceFile)))
                {
                    zipOutputStream.putNextEntry(new ZipEntry(entryName));
                    opened = true;
                    copyStream(inputStream, zipOutputStream);
                }
                catch (Exception ex)
                {
                    log.warn("Skip unreadable attachment file, achievementId={}, type={}, fileUuid={}, message={}",
                            attachmentFile.getAchievementId(), attachmentFile.getType(),
                            attachmentFile.getFileUuid(), ex.getMessage());
                }
                finally
                {
                    if (opened)
                    {
                        try
                        {
                            zipOutputStream.closeEntry();
                        }
                        catch (IOException ex)
                        {
                            log.warn("Close zip entry failed, entryName={}, message={}", entryName, ex.getMessage());
                        }
                    }
                }
            }

            zipOutputStream.finish();
            zipOutputStream.flush();
        }
    }

    private List<String> normalizeAchievementIds(List<String> achievementIds)
    {
        LinkedHashSet<String> normalizedSet = new LinkedHashSet<>();
        if (achievementIds == null)
        {
            return new ArrayList<>();
        }

        for (String achievementId : achievementIds)
        {
            if (StringUtils.isEmpty(achievementId))
            {
                continue;
            }
            String normalized = achievementId.trim();
            if (StringUtils.isNotEmpty(normalized))
            {
                normalizedSet.add(normalized);
            }
        }
        return new ArrayList<>(normalizedSet);
    }

    private List<Integer> normalizeAttachmentTypes(List<Integer> types)
    {
        LinkedHashSet<Integer> normalizedSet = new LinkedHashSet<>();
        if (types == null)
        {
            return new ArrayList<>();
        }

        for (Integer type : types)
        {
            if (type != null && ATTACHMENT_TYPE_NAME_MAP.containsKey(type))
            {
                normalizedSet.add(type);
            }
        }
        return new ArrayList<>(normalizedSet);
    }

    private String normalizeSourceMode(String sourceMode)
    {
        if (sourceMode == null)
        {
            return null;
        }
        String normalized = sourceMode.trim().toLowerCase();
        return StringUtils.isEmpty(normalized) ? null : normalized;
    }

    private int attachmentTypeSortIndex(List<Integer> orderedTypes, Integer type)
    {
        if (type == null)
        {
            return Integer.MAX_VALUE;
        }
        int index = orderedTypes.indexOf(type);
        return index >= 0 ? index : Integer.MAX_VALUE;
    }

    private String getAttachmentTypeName(Integer type)
    {
        return ATTACHMENT_TYPE_NAME_MAP.get(type);
    }

    private void addDirectoryEntry(ZipOutputStream zipOutputStream, Set<String> writtenDirectorySet, String entryName) throws IOException
    {
        if (!writtenDirectorySet.add(entryName))
        {
            return;
        }

        zipOutputStream.putNextEntry(new ZipEntry(entryName));
        zipOutputStream.closeEntry();
    }

    private File resolveAttachmentFile(String realPath)
    {
        if (StringUtils.isEmpty(realPath))
        {
            return null;
        }

        if (realPath.startsWith(Constants.RESOURCE_PREFIX))
        {
            return new File(RuoYiConfig.getProfile() + StringUtils.substringAfter(realPath, Constants.RESOURCE_PREFIX));
        }
        return new File(realPath);
    }

    private String buildAttachmentExportBaseName(String achievementId, String ownerName, String typeName)
    {
        return defaultFileNamePart(achievementId, "未知成果")
                + "_"
                + defaultFileNamePart(ownerName, "未知负责人")
                + "_"
                + defaultFileNamePart(typeName, "未知类别");
    }

    private String defaultFileNamePart(String value, String defaultValue)
    {
        String source = StringUtils.isNotEmpty(value) ? value.trim() : defaultValue;
        String sanitized = source
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", " ");
        sanitized = sanitized.trim();
        return StringUtils.isNotEmpty(sanitized) ? sanitized : defaultValue;
    }

    private String resolveUniqueEntryName(Map<String, Integer> nameCounterMap, String folderName, String baseFileName)
    {
        String sanitizedFolderName = defaultFileNamePart(folderName, "未分类");
        String key = sanitizedFolderName + "/" + baseFileName;
        Integer count = nameCounterMap.get(key);
        if (count == null)
        {
            nameCounterMap.put(key, 1);
            return sanitizedFolderName + "/" + baseFileName + ".pdf";
        }

        int next = count + 1;
        nameCounterMap.put(key, next);
        return sanitizedFolderName + "/" + baseFileName + "(" + next + ").pdf";
    }

    private void copyStream(InputStream inputStream, ZipOutputStream zipOutputStream) throws IOException
    {
        byte[] buffer = new byte[8192];
        int length;
        while ((length = inputStream.read(buffer)) != -1)
        {
            zipOutputStream.write(buffer, 0, length);
        }
    }

    /**
     * 批量删除成果录入
     *
     * @param achievementIds 需要删除的成果录入主键集合
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSamAchievementByAchievementIds(String[] achievementIds)
    {
        samAchievementMapper.deleteSamAchievementParticipantByParticipantIds(achievementIds);
        samAchievementMapper.deleteSamAchievementAdvisorByAchievementIds(achievementIds);
        samAchievementMapper.deleteSamAchievementAttachmentByAchievementIds(achievementIds);
        return samAchievementMapper.deleteSamAchievementByAchievementIds(achievementIds);
    }

    /**
     * 删除成果录入信息
     *
     * @param achievementId 成果录入主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSamAchievementByAchievementId(String achievementId)
    {
        samAchievementMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        samAchievementMapper.deleteSamAchievementAdvisorByAchievementId(achievementId);
        samAchievementMapper.deleteSamAchievementAttachmentByAchievementId(achievementId);
        return samAchievementMapper.deleteSamAchievementByAchievementId(achievementId);
    }

    /**
     * 根据比赛和届次查询已有的赛道列表
     *
     * @param competitionId 比赛ID
     * @param sessionId 届次ID
     * @return 赛道列表
     */
    @Override
    public List<String> selectTrackList(Long competitionId, Long sessionId)
    {
        return samAchievementMapper.selectTrackList(competitionId, sessionId);
    }
}


