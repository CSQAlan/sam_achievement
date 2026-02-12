package com.ruoyi.achievement.service.impl;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import com.ruoyi.achievement.domain.SamAchievementAdvisor;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.achievement.service.ISamTeacherService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.mapper.FileUuidMapper;
import com.ruoyi.achievement.mapper.SamAchievementMapper;
import com.ruoyi.achievement.service.ISamAchievementService;

/**
 * 成果录入Service业务层处理
 */
@Service
public class SamAchievementServiceImpl implements ISamAchievementService
{
    @Autowired
    private SamAchievementMapper samAchievementMapper;

    @Autowired
    private FileUuidMapper fileUuidMapper;

    @Autowired
    private ISamStudentService samStudentService; // 注入学生服务

    @Autowired
    private ISamTeacherService samTeacherService; // 注入教师服务

    // ... (查询方法的代码保持不变，省略以节省篇幅) ...
    @Override
    public SamAchievement selectSamAchievementByAchievementId(String achievementId)
    {
        return samAchievementMapper.selectSamAchievementByAchievementId(achievementId);
    }
    @Override
    public List<SamAchievement> selectSamAchievementList(SamAchievement samAchievement)
    {
        return samAchievementMapper.selectSamAchievementList(samAchievement);
    }
    @Override
    public List<SamAchievement> selectSamAchievementListByStudentId(SamAchievement samAchievement)
    {
        return samAchievementMapper.selectSamAchievementListByStudentId(samAchievement);
    }
    @Override
    public List<SamAchievement> selectSamAchievementListByTeacherId(SamAchievement samAchievement)
    {
        return samAchievementMapper.selectSamAchievementListByTeacherId(samAchievement);
    }

    /**
     * 新增成果录入
     */
    @Transactional
    @Override
    public int insertSamAchievement(SamAchievement samAchievement)
    {
        // 1. 自动提取年份
        if (samAchievement.getAwardTime() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(samAchievement.getAwardTime());
            samAchievement.setYear((long) cal.get(Calendar.YEAR));
        }
        samAchievement.setCreateTime(DateUtils.getNowDate());

        // 2. 插入主表
        int rows = samAchievementMapper.insertSamAchievement(samAchievement);

        // 3. 处理参赛选手 (包含自动补录学生档案)
        insertSamAchievementParticipant(samAchievement);

        // 4. 处理指导老师 (包含自动补录教师档案) -> 你之前漏掉了这个
        insertSamAchievementAdvisor(samAchievement);

        // 5. 处理附件转正 (标记为非临时文件)
        processAttachments(samAchievement);

        return rows;
    }

    /**
     * 修改成果录入
     */
    @Transactional
    @Override
    public int updateSamAchievement(SamAchievement samAchievement)
    {
        samAchievement.setUpdateTime(DateUtils.getNowDate());

        // 1. 处理参赛选手：先删后加
        samAchievementMapper.deleteSamAchievementParticipantByParticipantId(samAchievement.getAchievementId());
        insertSamAchievementParticipant(samAchievement);

        // 2. 处理指导老师：先删后加 -> 你之前漏掉了这个
        samAchievementMapper.deleteSamAchievementAdvisorByAchievementId(samAchievement.getAchievementId());
        insertSamAchievementAdvisor(samAchievement);

        // 3. 处理附件转正
        processAttachments(samAchievement);

        return samAchievementMapper.updateSamAchievement(samAchievement);
    }

    /**
     * 附件转正逻辑：将关联的 UUID 在 sys_file_uuid 表中标记为正式 (is_temp = 0)
     */
    private void processAttachments(SamAchievement samAchievement) {
        List<java.util.Map<String, Object>> attachments = samAchievement.getSamAchievementAttachmentList();
        if (StringUtils.isNotNull(attachments) && attachments.size() > 0) {
            List<String> uuids = new ArrayList<>();
            for (java.util.Map<String, Object> attachment : attachments) {
                String uuid = (String) attachment.get("fileUuid");
                if (StringUtils.isNotEmpty(uuid)) {
                    uuids.add(uuid);
                }
            }
            if (uuids.size() > 0) {
                fileUuidMapper.updateFileUuidStatus(uuids.toArray(new String[0]), 0);
            }
        }
    }

    /**
     * 核心逻辑：插入选手并同步学生档案
     */
    public void insertSamAchievementParticipant(SamAchievement samAchievement)
    {
        List<SamAchievementParticipant> list = samAchievement.getSamAchievementParticipantList();
        String achievementId = samAchievement.getAchievementId();

        if (StringUtils.isNotNull(list))
        {
            List<SamAchievementParticipant> insertList = new ArrayList<>();
            for (SamAchievementParticipant participant : list)
            {
                // Logic A: 检查并自动同步到学生档案表 (sam_student)
                checkAndInsertStudent(participant.getStudentId(), participant.getStudentName());
                // Logic B: 准备插入关系表
                participant.setAchievementId(achievementId);
                participant.setParticipantId(null); // 让数据库自增
                participant.setCreateBy(samAchievement.getCreateBy());
                participant.setCreateTime(DateUtils.getNowDate());
                participant.setUpdateTime(DateUtils.getNowDate());
                participant.setDelFlag(0L);
                insertList.add(participant);
            }
            if (insertList.size() > 0)
            {
                samAchievementMapper.batchSamAchievementParticipant(insertList);
            }
        }
    }

    /**
     * 核心逻辑：插入老师并同步教师档案
     */
    public void insertSamAchievementAdvisor(SamAchievement samAchievement)
    {
        List<SamAchievementAdvisor> list = samAchievement.getSamAchievementAdvisorList();
        String achievementId = samAchievement.getAchievementId();

        if (StringUtils.isNotNull(list))
        {
            List<SamAchievementAdvisor> insertList = new ArrayList<>();
            for (SamAchievementAdvisor advisor : list)
            {
                // Logic A: 检查并自动同步到教师档案表 (sam_teacher)
                checkAndInsertTeacher(advisor.getTeacherId(), advisor.getTeacherName());

                // Logic B: 准备插入关系表
                advisor.setAchievementId(achievementId);
                advisor.setAdvisorId(null);
                advisor.setCreateBy(samAchievement.getCreateBy());
                advisor.setCreateTime(DateUtils.getNowDate());
                advisor.setUpdateTime(DateUtils.getNowDate());
                advisor.setDelFlag(0L);
                insertList.add(advisor);
            }
            if (insertList.size() > 0)
            {
                samAchievementMapper.batchSamAchievementAdvisor(insertList);
            }
        }
    }

    /**
     * 辅助方法：检查学生是否存在，不存在则新增
     */
    private void checkAndInsertStudent(String studentNo, String studentName) {
        if (StringUtils.isEmpty(studentNo)) return;

        SamStudent query = new SamStudent();
        query.setNo(studentNo);
        // 使用 list 查询避免报错，如果只有一条也兼容
        List<SamStudent> exists = samStudentService.selectSamStudentList(query);

        if (exists == null || exists.isEmpty()) {
            SamStudent newStudent = new SamStudent();
            newStudent.setNo(studentNo);
            newStudent.setName(studentName);
            // 可以设置一些默认值，例如暂时设为"未知学院"或空
            samStudentService.insertSamStudent(newStudent);
        }
    }

    /**
     * 辅助方法：检查教师是否存在，不存在则新增
     */
    private void checkAndInsertTeacher(String teacherNo, String teacherName) {
        if (StringUtils.isEmpty(teacherNo)) return;

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

    @Transactional
    @Override
    public int deleteSamAchievementByAchievementIds(String[] achievementIds)
    {
        // 删除关联的选手和老师
        samAchievementMapper.deleteSamAchievementParticipantByParticipantIds(achievementIds);
        samAchievementMapper.deleteSamAchievementAdvisorByAchievementIds(achievementIds); // 记得加这个方法
        return samAchievementMapper.deleteSamAchievementByAchievementIds(achievementIds);
    }

    @Transactional
    @Override
    public int deleteSamAchievementByAchievementId(String achievementId)
    {
        samAchievementMapper.deleteSamAchievementParticipantByParticipantId(achievementId);
        samAchievementMapper.deleteSamAchievementAdvisorByAchievementId(achievementId); // 记得加这个方法
        return samAchievementMapper.deleteSamAchievementByAchievementId(achievementId);
    }
}