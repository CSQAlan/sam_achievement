package com.ruoyi.achievement.service.impl;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.domain.SamAchievementAdvisor;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.achievement.mapper.SamAchievementMapper;
import com.ruoyi.achievement.service.ISamAchievementService;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.achievement.service.ISamTeacherService;
import com.ruoyi.achievement.mapper.FileUuidMapper;
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
    @Autowired
    private SamAchievementMapper samAchievementMapper;

    @Autowired
    private ISamStudentService samStudentService;

    @Autowired
    private ISamTeacherService samTeacherService;

    @Autowired
    private FileUuidMapper fileUuidMapper;

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
        // 1. 验证成果录入主表信息
        validateSamAchievement(samAchievement);

        // 2. 验证PDF文件上传
        validatePDFAttachments(samAchievement);

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
     * @param samAchievement 成果录入
     */
    private void validatePDFAttachments(SamAchievement samAchievement) {
        List<Map<String, Object>> attachments = samAchievement.getSamAchievementAttachmentList();
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

        for (Map<String, Object> attachment : attachments) {
            Integer type = (Integer) attachment.get("type");
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
        SamAchievement info = samAchievementMapper.checkCertificateNoUnique(samAchievement.getCertificateNo(), samAchievement.getCompetitionId());
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
        List<Map<String, Object>> attachments = samAchievement.getSamAchievementAttachmentList();
        String achievementId = samAchievement.getAchievementId();

        if (StringUtils.isNotNull(attachments) && attachments.size() > 0) {
            List<String> uuids = new ArrayList<>();
            List<Map<String, Object>> insertList = new ArrayList<>();

            for (Map<String, Object> attachment : attachments) {
                String uuid = (String) attachment.get("fileUuid");
                if (StringUtils.isNotEmpty(uuid)) {
                    uuids.add(uuid);

                    // 补全附件表所需字段
                    attachment.put("achievementId", achievementId);
                    // 如果前端没传 fileType，这里给个默认值
                    if (attachment.get("fileType") == null) {
                        attachment.put("fileType", 1);
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
