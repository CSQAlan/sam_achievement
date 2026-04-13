package com.ruoyi.achievement.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.achievement.domain.ExportAchievementBaseVo;
import com.ruoyi.achievement.domain.ExportAttachmentFailExcelVo;
import com.ruoyi.achievement.domain.ExportAttachmentFileVo;
import com.ruoyi.achievement.domain.ExportAttachmentZipReq;
import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
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
import com.ruoyi.system.service.ISysDeptService;

/**
 * 成果录入Service业务层处理
 *
 * @author 王璨
 * @date 2026-02-03
 */
@Service
public class SamAchievementServiceImpl implements ISamAchievementService {
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

    @Autowired
    private ISysDeptService deptService;

    private static Map<Integer, String> buildAttachmentTypeNameMap() {
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
    public SamAchievement selectSamAchievementByAchievementId(String achievementId) {
        return samAchievementMapper.selectSamAchievementByAchievementId(achievementId);
    }

    @Override
    public SamAchievement selectSamAchievementForSelf(String achievementId, String selfEditScene) {
        SamAchievement existing = samAchievementMapper.selectSamAchievementByAchievementId(achievementId);
        if (existing == null) {
            throw new ServiceException("Achievement not found");
        }

        String normalizedScene = normalizeSelfEditScene(selfEditScene);
        if (!isSelfEditScene(normalizedScene)) {
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
    public List<SamAchievement> selectSamAchievementList(SamAchievement samAchievement) {
        return samAchievementMapper.selectSamAchievementList(samAchievement);
    }

    @Override
    public List<SamAchievement> selectSamAchievementListByStudentId(SamAchievement samAchievement) {
        // 验证学生ID
        if (samAchievement.getParams() == null
                || StringUtils.isEmpty((String) samAchievement.getParams().get("studentId"))) {
            throw new ServiceException("学生ID不能为空");
        }
        return samAchievementMapper.selectSamAchievementListByStudentId(samAchievement);
    }

    @Override
    public List<SamAchievement> selectSamAchievementListByTeacherId(SamAchievement samAchievement) {
        // 验证教师ID
        if (samAchievement.getParams() == null
                || StringUtils.isEmpty((String) samAchievement.getParams().get("teacherId"))) {
            throw new ServiceException("教师ID不能为空");
        }
        return samAchievementMapper.selectSamAchievementListByTeacherId(samAchievement);
    }

    @Override
    public List<SamAchievement> selectSamAchievementListByResponsibleStudentId(SamAchievement samAchievement) {
        if (samAchievement.getParams() == null
                || StringUtils.isEmpty((String) samAchievement.getParams().get("studentId"))) {
            throw new ServiceException("学生ID不能为空");
        }
        return samAchievementMapper.selectSamAchievementListByResponsibleStudentId(samAchievement);
    }

    @Override
    public List<SamAchievement> selectSamAchievementListByUserId(SamAchievement samAchievement) {
        // 验证用户ID
        if (samAchievement.getParams() == null
                || StringUtils.isEmpty((String) samAchievement.getParams().get("userId"))) {
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
    @BizAudit(bizType = "achievement", bizName = "新增成果", opType = BizAuditOpType.ADD, handler = "achievementBizAuditHandler", async = false)
    public int insertSamAchievement(SamAchievement samAchievement) {
        applyControlledReviewFields(samAchievement, null);
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

        // 4. 设置归属学院 (owner_dep_id)
        applyDefaultOwnerDepId(samAchievement);

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

    private void applyDefaultOwnerDepId(SamAchievement samAchievement) {
        if (StringUtils.isNotEmpty(samAchievement.getOwnerDepId())) {
            samAchievement.setOwnerDepId(normalizeDeptId(samAchievement.getOwnerDepId()));
            if (StringUtils.isNotEmpty(samAchievement.getOwnerDepId())) {
                return;
            }
        }

        // 尝试从参与人（负责人）中获取学院
        List<SamAchievementParticipant> participantList = samAchievement.getSamAchievementParticipantList();
        if (participantList != null && !participantList.isEmpty()) {
            SamAchievementParticipant managerParticipant = null;
            for (SamAchievementParticipant participant : participantList) {
                if ("1".equals(StringUtils.trim(participant.getManager()))) {
                    managerParticipant = participant;
                    break;
                }
            }

            if (managerParticipant == null) {
                managerParticipant = participantList.get(0);
            }

            String ownerDepId = resolveOwnerDepIdFromParticipant(managerParticipant);
            if (StringUtils.isNotEmpty(ownerDepId)) {
                samAchievement.setOwnerDepId(ownerDepId);
                return;
            }
        }

        // 兜底方案：从当前登录人的部门获取学院
        try {
            Long userDeptId = SecurityUtils.getDeptId();
            if (userDeptId != null) {
                Long collegeId = deptService.getCollegeId(userDeptId);
                if (collegeId != null) {
                    samAchievement.setOwnerDepId(String.valueOf(collegeId));
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
    }

    private String resolveOwnerDepIdFromParticipant(SamAchievementParticipant participant) {
        if (participant == null) {
            return null;
        }

        if (StringUtils.isNotEmpty(participant.getStudentId())) {
            SamStudent query = new SamStudent();
            query.setNo(participant.getStudentId().trim());
            List<SamStudent> students = samStudentService.selectSamStudentList(query);
            if (students != null && !students.isEmpty()) {
                String ownerDepId = normalizeDeptId(students.get(0).getSchool());
                if (StringUtils.isNotEmpty(ownerDepId)) {
                    return ownerDepId;
                }
            }
        }

        String ownerDepId = normalizeDeptId(participant.getSchool());
        if (StringUtils.isNotEmpty(ownerDepId)) {
            return ownerDepId;
        }

        return null;
    }

    private String normalizeDeptId(String schoolValue) {
        if (StringUtils.isEmpty(schoolValue)) {
            return null;
        }

        String normalized = schoolValue.trim();
        String foundId = null;

        if (normalized.matches("\\d+")) {
            foundId = normalized;
        } else {
            SysDept query = new SysDept();
            query.setDeptName(normalized);
            List<SysDept> deptList = deptService.selectDeptList(query);
            if (deptList != null && !deptList.isEmpty()) {
                for (SysDept dept : deptList) {
                    if (dept != null && StringUtils.equals(normalized, dept.getDeptName())
                            && dept.getDeptId() != null) {
                        foundId = String.valueOf(dept.getDeptId());
                        break;
                    }
                }
            }
        }

        // 统一向上追溯到学院级 (parent_id = 100)
        return getCollegeIdByAnyDeptId(foundId);
    }

    private String getCollegeIdByAnyDeptId(String deptIdStr) {
        if (StringUtils.isEmpty(deptIdStr) || !deptIdStr.matches("\\d+")) {
            return null;
        }
        Long collegeId = deptService.getCollegeId(Long.valueOf(deptIdStr));
        return collegeId != null ? String.valueOf(collegeId) : null;
    }

    private void applyControlledReviewFields(SamAchievement incoming, SamAchievement existing) {
        if (incoming == null) {
            return;
        }

        Long autoYear = resolveYearFromAwardTime(incoming.getAwardTime());
        boolean canEditReviewMeta = canEditAchievementReviewFields();
        boolean canEditYear = SecurityUtils.isAdmin(SecurityUtils.getUserId());

        if (canEditYear) {
            if (incoming.getYear() == null) {
                incoming.setYear(autoYear != null ? autoYear : existing == null ? null : existing.getYear());
            }
        } else {
            incoming.setYear(autoYear != null ? autoYear : existing == null ? null : existing.getYear());
        }

        if (!canEditReviewMeta) {
            incoming.setIsSupplement(existing == null ? null : existing.getIsSupplement());
        }
    }

    private Long resolveYearFromAwardTime(Date awardTime) {
        if (awardTime == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(awardTime);
        return (long) cal.get(Calendar.YEAR);
    }

    private boolean canEditAchievementReviewFields() {
        Long userId = SecurityUtils.getUserId();
        if (SecurityUtils.isAdmin(userId)) {
            return true;
        }
        return SecurityUtils.hasPermi("achievement:college_level_unreviewed:edit")
                || SecurityUtils.hasPermi("achievement:college_level_reviewed:edit")
                || SecurityUtils.hasPermi("achievement:school_level_unreviewed:edit")
                || SecurityUtils.hasPermi("achievement:school_level_reviewed:edit");
    }

    /**
     * 修改成果录入
     *
     * @param samAchievement 成果录入
     * @return 结果
     */
    @Transactional
    @Override
    @BizAudit(bizType = "achievement", bizName = "修改成果", opType = BizAuditOpType.UPDATE, handler = "achievementBizAuditHandler", async = false)
    public int updateSamAchievement(SamAchievement samAchievement) {
        SamAchievement existing = samAchievementMapper
                .selectSamAchievementByAchievementId(samAchievement.getAchievementId());
        if (existing == null) {
            throw new ServiceException("成果不存在");
        }
        applyControlledReviewFields(samAchievement, existing);
        // 1. 验证成果录入主表信息
        validateSamAchievement(samAchievement);

        // 2. 验证PDF文件上传
        validatePDFAttachments(samAchievement);

        String selfEditScene = resolveSelfEditScene(samAchievement);

        if (isSelfEditScene(selfEditScene)) {
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

    /**
     * 验证PDF文件上传
     * 
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
            if (type == null)
                continue;
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
     * 
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
        String achievementId = StringUtils.isEmpty(samAchievement.getAchievementId()) ? "-1"
                : samAchievement.getAchievementId();
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

        if (StringUtils.isEmpty(achievementId) || attachments == null || attachments.isEmpty()) {
            return;
        }

        List<String> uuids = new ArrayList<>();
        List<SamAchievementAttachment> insertList = new ArrayList<>();

        for (SamAchievementAttachment attachment : attachments) {
            if (attachment == null || StringUtils.isEmpty(attachment.getFileUuid())) {
                continue;
            }

            String[] splitUuids = attachment.getFileUuid().split(",");
            for (String rawUuid : splitUuids) {
                String uuid = StringUtils.trim(rawUuid);
                if (StringUtils.isEmpty(uuid)) {
                    continue;
                }

                SamAchievementAttachment newAttachment = new SamAchievementAttachment();
                newAttachment.setAchievementId(achievementId);
                newAttachment.setFileUuid(uuid);
                newAttachment.setType(attachment.getType());
                newAttachment.setFileType(attachment.getFileType() != null ? attachment.getFileType() : 1);
                insertList.add(newAttachment);
                uuids.add(uuid);
            }
        }

        if (!uuids.isEmpty()) {
            fileUuidMapper.updateFileUuidStatus(uuids.toArray(new String[0]), 0);
        }

        if (!insertList.isEmpty()) {
            samAchievementMapper.batchSamAchievementAttachment(insertList);
        }
    }

    /**
     * 核心逻辑：插入选手并同步学生档案
     */
    public void insertSamAchievementParticipant(SamAchievement samAchievement) {
        List<SamAchievementParticipant> samAchievementParticipantList = samAchievement
                .getSamAchievementParticipantList();
        // 获取主表自动生成的 ID
        String achievementId = samAchievement.getAchievementId();

        if (StringUtils.isNotNull(samAchievementParticipantList)) {
            List<SamAchievementParticipant> list = new ArrayList<SamAchievementParticipant>();
            for (SamAchievementParticipant participant : samAchievementParticipantList) {
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
                if (StringUtils.isEmpty(participant.getStudentNo())
                        && StringUtils.isNotEmpty(participant.getStudentId())) {
                    participant.setStudentNo(participant.getStudentId());
                }

                // 4. 自动补录学生档案
                checkAndInsertStudent(participant.getStudentNo(), participant.getStudentName());

                list.add(participant);
            }
            if (list.size() > 0) {
                samAchievementMapper.batchSamAchievementParticipant(list);
            }
        }
    }

    /**
     * 插入指导老师并同步教师档案
     */
    public void insertSamAchievementAdvisor(SamAchievement samAchievement) {
        List<SamAchievementAdvisor> samAchievementAdvisorList = samAchievement.getSamAchievementAdvisorList();
        String achievementId = samAchievement.getAchievementId();

        if (StringUtils.isNotNull(samAchievementAdvisorList)) {
            List<SamAchievementAdvisor> list = new ArrayList<SamAchievementAdvisor>();
            for (SamAchievementAdvisor advisor : samAchievementAdvisorList) {
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
            if (list.size() > 0) {
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

    private String resolveSelfEditScene(SamAchievement samAchievement) {
        if (samAchievement == null || samAchievement.getParams() == null) {
            return null;
        }
        Object value = samAchievement.getParams().get("selfEditScene");
        return value == null ? null : normalizeSelfEditScene(String.valueOf(value));
    }

    private String normalizeSelfEditScene(String selfEditScene) {
        if (selfEditScene == null) {
            return null;
        }
        String normalized = selfEditScene.trim().toLowerCase();
        return StringUtils.isEmpty(normalized) ? null : normalized;
    }

    private boolean isSelfEditScene(String selfEditScene) {
        return Objects.equals("responsible", selfEditScene)
                || Objects.equals("participated", selfEditScene)
                || Objects.equals("guided", selfEditScene);
    }

    private void validateSelfUpdatePermission(SamAchievement existing, String selfEditScene) {
        String currentUsername = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(currentUsername)) {
            throw new ServiceException("当前登录用户无效");
        }

        if (SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            return;
        }

        boolean permitted = false;
        if (Objects.equals("responsible", selfEditScene) || Objects.equals("participated", selfEditScene)) {
            List<SamAchievementParticipant> participants = existing.getSamAchievementParticipantList();
            if (participants != null) {
                for (SamAchievementParticipant participant : participants) {
                    if (participant == null) {
                        continue;
                    }
                    String studentNo = StringUtils.isNotEmpty(participant.getStudentNo())
                            ? participant.getStudentNo()
                            : participant.getStudentId();
                    if (Objects.equals(currentUsername, studentNo)) {
                        if (Objects.equals("responsible", selfEditScene)) {
                            permitted = Objects.equals("1", participant.getManager());
                        } else {
                            permitted = true;
                        }
                        if (permitted) {
                            break;
                        }
                    }
                }
            }
        } else if (Objects.equals("guided", selfEditScene)) {
            List<SamAchievementAdvisor> advisors = existing.getSamAchievementAdvisorList();
            if (advisors != null) {
                for (SamAchievementAdvisor advisor : advisors) {
                    if (advisor == null) {
                        continue;
                    }
                    String teacherNo = StringUtils.isNotEmpty(advisor.getTeacherNo())
                            ? advisor.getTeacherNo()
                            : advisor.getTeacherId();
                    if (Objects.equals(currentUsername, teacherNo)) {
                        permitted = true;
                        break;
                    }
                }
            }
        }

        if (!permitted) {
            throw new ServiceException("无权操作该成果");
        }
    }

    private void validateSelfUpdateStatus(SamAchievement existing) {
        Long schoolReviewResult = existing.getSchooiReviewResult();
        if (schoolReviewResult != null && Objects.equals(1L, schoolReviewResult)) {
            throw new ServiceException("该成果已通过校级审核，不能再修改");
        }
    }

    private void resetReviewStateForSelfUpdate(SamAchievement samAchievement) {
        samAchievement.setReviewResult(0L);
        samAchievement.setSchooiReviewResult(2L);
        samAchievement.setReviewReason(null);
        samAchievement.setSchoolReviewReason(null);
        samAchievement.setAuditBy(null);
        samAchievement.setSchoolAuditBy(null);
        samAchievement.setReviewedAt(null);
        samAchievement.setSchoolReviewedAt(null);
    }

    @Override
    public void exportAttachmentZip(ExportAttachmentZipReq req, HttpServletResponse response) throws IOException {
        if (req == null) {
            throw new ServiceException("导出参数不能为空");
        }

        boolean isGroupByCompetition = Boolean.TRUE.equals(req.getGroupByCompetition());
        String competitionId = req.getCompetitionId();

        List<String> achievementIdList = normalizeAchievementIds(req.getAchievementIds());
        if (!isGroupByCompetition && achievementIdList.isEmpty()) {
            throw new ServiceException("请选择要导出的成果");
        }
        if (isGroupByCompetition && competitionId == null) {
            throw new ServiceException("请选择要导出的比赛");
        }

        List<Integer> typeList = normalizeAttachmentTypes(req.getTypes());
        if (typeList.isEmpty()) {
            throw new ServiceException("请选择至少一个附件类别");
        }

        Long userId = SecurityUtils.getUserId();
        boolean isAdmin = SecurityUtils.isAdmin(userId);
        String loginName = SecurityUtils.getUsername();
        if (!isAdmin && StringUtils.isEmpty(loginName)) {
            throw new ServiceException("当前登录用户无效");
        }

        List<ExportAchievementBaseVo> authorizedAchievementList = samAchievementMapper
                .selectAuthorizedExportAchievementBase(
                        achievementIdList.isEmpty() ? new String[0] : achievementIdList.toArray(new String[0]),
                        loginName,
                        normalizeSourceMode(req.getSourceMode()),
                        SecurityUtils.hasRole("student"),
                        SecurityUtils.hasRole("teacher"),
                        isAdmin,
                        competitionId,
                        isGroupByCompetition);

        if (authorizedAchievementList == null || authorizedAchievementList.isEmpty()) {
            throw new ServiceException("当前没有可导出的成果");
        }

        Map<String, ExportAchievementBaseVo> achievementMap = new LinkedHashMap<>();
        for (ExportAchievementBaseVo item : authorizedAchievementList) {
            if (item == null || StringUtils.isEmpty(item.getAchievementId())) {
                continue;
            }
            if (!achievementMap.containsKey(item.getAchievementId())) {
                item.setOwnerName(defaultFileNamePart(item.getOwnerName(), "未知负责人"));
                item.setCompetitionName(defaultFileNamePart(item.getCompetitionName(), "未知赛事"));
                achievementMap.put(item.getAchievementId(), item);
            }
        }

        if (achievementMap.isEmpty()) {
            throw new ServiceException("当前没有可导出的成果");
        }

        String filenameTemplate = req.getFilenameTemplate();

        List<ExportAttachmentFileVo> attachmentFiles = samAchievementMapper.selectExportAttachmentFiles(
                achievementMap.keySet().toArray(new String[0]),
                typeList.toArray(new Integer[0]));
        if (attachmentFiles == null) {
            attachmentFiles = new ArrayList<>();
        }

        attachmentFiles.sort(Comparator
                .comparingInt((ExportAttachmentFileVo item) -> attachmentTypeSortIndex(typeList,
                        item == null ? null : item.getType()))
                .thenComparing(item -> item == null ? "" : StringUtils.nvl(item.getAchievementId(), ""))
                .thenComparing(item -> item == null ? "" : StringUtils.nvl(item.getFileUuid(), "")));

        response.reset();
        response.setContentType("application/zip");
        FileUtils.setAttachmentResponseHeader(response, "成果附件导出_" + DateUtils.dateTimeNow() + ".zip");

        Set<String> writtenDirectorySet = new HashSet<>();
        Map<String, Integer> nameCounterMap = new HashMap<>();
        List<ExportAttachmentFailExcelVo> failList = new ArrayList<>();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream(),
                StandardCharsets.UTF_8)) {
            zipOutputStream.setLevel(java.util.zip.Deflater.BEST_SPEED);
            if (!isGroupByCompetition) {
                for (Integer type : typeList) {
                    addDirectoryEntry(zipOutputStream, writtenDirectorySet,
                            defaultFileNamePart(getAttachmentTypeName(type), "未分类") + "/");
                }
            }

            for (ExportAttachmentFileVo attachmentFile : attachmentFiles) {
                if (attachmentFile == null || StringUtils.isEmpty(attachmentFile.getAchievementId())) {
                    continue;
                }

                String typeName = getAttachmentTypeName(attachmentFile.getType());
                if (StringUtils.isEmpty(typeName)) {
                    continue;
                }

                ExportAchievementBaseVo baseVo = achievementMap.get(attachmentFile.getAchievementId());
                String ownerName = baseVo.getOwnerName();
                File sourceFile = resolveAttachmentFile(attachmentFile.getRealPath());
                if (sourceFile == null || !sourceFile.exists() || !sourceFile.isFile()) {
                    log.warn("Skip missing attachment file, achievementId={}, type={}, fileUuid={}, realPath={}",
                            attachmentFile.getAchievementId(), attachmentFile.getType(),
                            attachmentFile.getFileUuid(), attachmentFile.getRealPath());
                    addAttachmentFailRecord(failList, attachmentFile, ownerName, typeName, "文件不存在或路径无效");
                    continue;
                }

                String baseFileName = resolveAttachmentExportFileName(
                        filenameTemplate,
                        attachmentFile.getAchievementId(),
                        ownerName,
                        typeName,
                        baseVo);

                String folderPath;
                if (isGroupByCompetition) {
                    folderPath = baseVo.getCompetitionName() + "/" +
                            attachmentFile.getAchievementId() + "_" + ownerName;
                } else {
                    folderPath = typeName;
                }

                String entryName = resolveUniqueEntryName(nameCounterMap, folderPath, baseFileName);

                boolean opened = false;
                try (InputStream inputStream = new BufferedInputStream(new FileInputStream(sourceFile))) {
                    zipOutputStream.putNextEntry(new ZipEntry(entryName));
                    opened = true;
                    copyStream(inputStream, zipOutputStream);
                } catch (Exception ex) {
                    log.warn("Skip unreadable attachment file, achievementId={}, type={}, fileUuid={}, message={}",
                            attachmentFile.getAchievementId(), attachmentFile.getType(),
                            attachmentFile.getFileUuid(), ex.getMessage());
                    addAttachmentFailRecord(failList, attachmentFile, ownerName, typeName,
                            "文件读取失败" + (StringUtils.isNotEmpty(ex.getMessage()) ? "：" + ex.getMessage() : ""));
                } finally {
                    if (opened) {
                        try {
                            zipOutputStream.closeEntry();
                        } catch (IOException ex) {
                            log.warn("Close zip entry failed, entryName={}, message={}", entryName, ex.getMessage());
                        }
                    }
                }
            }

            writeFailExcelToZip(zipOutputStream, failList);
            zipOutputStream.finish();
            zipOutputStream.flush();
        }
    }

    private List<String> normalizeAchievementIds(List<String> achievementIds) {
        LinkedHashSet<String> normalizedSet = new LinkedHashSet<>();
        if (achievementIds == null) {
            return new ArrayList<>();
        }

        for (String achievementId : achievementIds) {
            if (StringUtils.isEmpty(achievementId)) {
                continue;
            }
            String normalized = achievementId.trim();
            if (StringUtils.isNotEmpty(normalized)) {
                normalizedSet.add(normalized);
            }
        }
        return new ArrayList<>(normalizedSet);
    }

    private List<Integer> normalizeAttachmentTypes(List<Integer> types) {
        LinkedHashSet<Integer> normalizedSet = new LinkedHashSet<>();
        if (types == null) {
            return new ArrayList<>();
        }

        for (Integer type : types) {
            if (type != null && ATTACHMENT_TYPE_NAME_MAP.containsKey(type)) {
                normalizedSet.add(type);
            }
        }
        return new ArrayList<>(normalizedSet);
    }

    private String normalizeSourceMode(String sourceMode) {
        if (sourceMode == null) {
            return null;
        }
        String normalized = sourceMode.trim().toLowerCase();
        return StringUtils.isEmpty(normalized) ? null : normalized;
    }

    private int attachmentTypeSortIndex(List<Integer> orderedTypes, Integer type) {
        if (type == null) {
            return Integer.MAX_VALUE;
        }
        int index = orderedTypes.indexOf(type);
        return index >= 0 ? index : Integer.MAX_VALUE;
    }

    private String getAttachmentTypeName(Integer type) {
        return ATTACHMENT_TYPE_NAME_MAP.get(type);
    }

    private void addAttachmentFailRecord(List<ExportAttachmentFailExcelVo> failList,
            ExportAttachmentFileVo attachmentFile,
            String ownerName,
            String typeName,
            String failReason) {
        ExportAttachmentFailExcelVo failItem = new ExportAttachmentFailExcelVo();
        failItem.setAchievementId(attachmentFile == null ? null : attachmentFile.getAchievementId());
        failItem.setOwnerName(ownerName);
        failItem.setTypeName(typeName);
        failItem.setFailReason(failReason);
        failItem.setOriginName(attachmentFile == null ? null : attachmentFile.getOriginName());
        failItem.setFileUuid(attachmentFile == null ? null : attachmentFile.getFileUuid());
        failList.add(failItem);
    }

    private void writeFailExcelToZip(ZipOutputStream zipOutputStream,
            List<ExportAttachmentFailExcelVo> failList) throws IOException {
        if (failList == null || failList.isEmpty()) {
            return;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ExcelUtil<ExportAttachmentFailExcelVo> util = new ExcelUtil<>(ExportAttachmentFailExcelVo.class);
            util.exportExcel(outputStream, failList, "附件导出失败清单", "附件导出失败清单");

            zipOutputStream.putNextEntry(new ZipEntry("失败清单.xlsx"));
            zipOutputStream.write(outputStream.toByteArray());
            zipOutputStream.closeEntry();
        }
    }

    private void addDirectoryEntry(ZipOutputStream zipOutputStream, Set<String> writtenDirectorySet, String entryName)
            throws IOException {
        if (!writtenDirectorySet.add(entryName)) {
            return;
        }

        zipOutputStream.putNextEntry(new ZipEntry(entryName));
        zipOutputStream.closeEntry();
    }

    private File resolveAttachmentFile(String realPath) {
        if (StringUtils.isEmpty(realPath)) {
            return null;
        }

        if (realPath.startsWith(Constants.RESOURCE_PREFIX)) {
            return new File(RuoYiConfig.getProfile() + StringUtils.substringAfter(realPath, Constants.RESOURCE_PREFIX));
        }
        return new File(realPath);
    }

    private String resolveAttachmentExportFileName(String template, String achievementId, String ownerName, String typeName, ExportAchievementBaseVo baseVo) {
        if (StringUtils.isEmpty(template)) {
            return buildAttachmentExportBaseName(achievementId, ownerName, typeName);
        }

        // Translate codes to labels
        String gradeLabel = DictUtils.getDictLabel("award_rank", baseVo.getAwardGrade());
        String levelLabel = DictUtils.getDictLabel("award_level_type", baseVo.getAwardLevel());

        String result = template;
        result = result.replace("{id}", defaultFileNamePart(achievementId, "未知成果"));
        result = result.replace("{manager}", defaultFileNamePart(ownerName, "未知负责人"));
        result = result.replace("{competition}", defaultFileNamePart(baseVo.getCompetitionName(), "未知赛事"));
        result = result.replace("{grade}", defaultFileNamePart(gradeLabel, "未知等级"));
        result = result.replace("{type}", defaultFileNamePart(typeName, "未知类别"));
        result = result.replace("{level}", defaultFileNamePart(levelLabel, "未知级别"));
        result = result.replace("{year}", defaultFileNamePart(String.valueOf(baseVo.getYear()), "未知年份"));
        result = result.replace("{track}", defaultFileNamePart(baseVo.getTrack(), "未知赛道"));

        return defaultFileNamePart(result, "未命名附件");
    }

    private String buildAttachmentExportBaseName(String achievementId, String ownerName, String typeName) {
        return defaultFileNamePart(achievementId, "未知成果")
                + "_"
                + defaultFileNamePart(ownerName, "未知负责人")
                + "_"
                + defaultFileNamePart(typeName, "未知类别");
    }

    private String defaultFileNamePart(String value, String defaultValue) {
        String source = StringUtils.isNotEmpty(value) ? value.trim() : defaultValue;
        String sanitized = source
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", " ");
        sanitized = sanitized.trim();
        return StringUtils.isNotEmpty(sanitized) ? sanitized : defaultValue;
    }

    private String resolveUniqueEntryName(Map<String, Integer> nameCounterMap, String folderName, String baseFileName) {
        String sanitizedFolderName = StringUtils.isNotEmpty(folderName) ? folderName : "未分类";
        String key = sanitizedFolderName + "/" + baseFileName;
        Integer count = nameCounterMap.get(key);
        if (count == null) {
            nameCounterMap.put(key, 1);
            return sanitizedFolderName + "/" + baseFileName + ".pdf";
        }

        int next = count + 1;
        nameCounterMap.put(key, next);
        return sanitizedFolderName + "/" + baseFileName + "(" + next + ").pdf";
    }

    private void copyStream(InputStream inputStream, ZipOutputStream zipOutputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
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
    @BizAudit(bizType = "achievement", bizName = "批量删除成果", opType = BizAuditOpType.DELETE, handler = "achievementBizAuditHandler", async = false)
    public int deleteSamAchievementByAchievementIds(String[] achievementIds) {
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
    @BizAudit(bizType = "achievement", bizName = "删除成果", opType = BizAuditOpType.DELETE, handler = "achievementBizAuditHandler", async = false)
    public int deleteSamAchievementByAchievementId(String achievementId) {
        samAchievementMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        samAchievementMapper.deleteSamAchievementAdvisorByAchievementId(achievementId);
        samAchievementMapper.deleteSamAchievementAttachmentByAchievementId(achievementId);
        return samAchievementMapper.deleteSamAchievementByAchievementId(achievementId);
    }

    /**
     * 根据比赛和届次查询已有的赛道列表
     *
     * @param competitionId 比赛ID
     * @param sessionId     届次ID
     * @return 赛道列表
     */
    @Override
    public List<String> selectTrackList(Long competitionId, Long sessionId) {
        return samAchievementMapper.selectTrackList(competitionId, sessionId);
    }
}
