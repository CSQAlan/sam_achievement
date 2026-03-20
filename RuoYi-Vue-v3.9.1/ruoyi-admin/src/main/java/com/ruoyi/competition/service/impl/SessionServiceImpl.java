package com.ruoyi.competition.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.competition.domain.Competition;
import com.ruoyi.competition.service.ICompetitionService;
import com.ruoyi.competition.mapper.SessionMapper;
import com.ruoyi.competition.domain.Session;
import com.ruoyi.competition.service.ISessionService;
import com.ruoyi.competition.domain.Tag;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.service.ISysDictDataService;

/**
 * 赛事届次Service业务层处理
 *
 * @author ruoyi
 * @date 2026-02-01
 */
@Service
public class SessionServiceImpl implements ISessionService {
    private static final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);

    @Autowired
    private SessionMapper sessionMapper;

    @Autowired
    private ICompetitionService competitionService;

    @Autowired
    private ISysDictDataService sysDictDataService;

    // ========== 原有方法：完整保留 ==========
    @Override
    public Session selectSessionById(Long id) {
        if (id == null) {
            throw new ServiceException("查询届次必须传入主键ID！");
        }
        Session session = sessionMapper.selectSessionById(id);
        if (session == null) {
            throw new ServiceException("届次记录不存在（ID：" + id + "）");
        }
        return session;
    }

    @Override
    public List<Session> selectSessionList(Session session) {
        return sessionMapper.selectSessionList(session);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertSession(Session session) {
        session.setCreateTime(DateUtils.getNowDate());
        return sessionMapper.insertSession(session);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateSession(Session session) {
        if (session.getId() == null) {
            throw new ServiceException("修改届次必须传入主键ID！");
        }
        session.setUpdateTime(DateUtils.getNowDate());
        int updateCount = sessionMapper.updateSession(session);
        // 关键：校验是否真的更新了记录
        if (updateCount == 0) {
            throw new ServiceException("届次记录不存在或已被删除，无法修改！");
        }

        // ========== 新增：同步更新标签子表 ==========
        Long sessionId = session.getId();
        String tagsCode = session.getTags(); // 拿到修改后的标签编码

        // 1. 先删除该届次下所有旧标签
        sessionMapper.deleteTagBySessionId(sessionId);

        // 2. 插入新标签（和导入逻辑保持一致）
        if (tagsCode != null && !tagsCode.trim().isEmpty()) {
            String[] tagArray = tagsCode.split(",");
            List<Tag> tagList = new ArrayList<>();
            String operName = SecurityUtils.getUsername(); // 获取当前操作人
            for (String tagValue : tagArray) {
                if (tagValue.trim().isEmpty()) continue;
                Tag tag = new Tag();
                tag.setCompetitionSessionId(sessionId);
                tag.setTagName(tagValue.trim());
                tag.setCreateBy(operName);
                tag.setCreateTime(DateUtils.getNowDate());
                tag.setDelFlag("0");
                tagList.add(tag);
            }
            if (!tagList.isEmpty()) {
                sessionMapper.batchInsertTag(tagList);
            }
        }

        return updateCount;
    }

    // ========== 修复删除逻辑：按顺序删成果表→标签表→届次表 ==========
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteSessionByIds(Long[] ids) {
        // 1. 删成果表
        sessionMapper.deleteAchievementBySessionIds(ids);
        // 2. 删标签表
        sessionMapper.deleteTagBySessionIds(ids);
        // 3. 删届次表
        return sessionMapper.deleteSessionByIds(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteSessionById(Long id) {
        // 1. 删成果表
        sessionMapper.deleteAchievementBySessionId(id);
        // 2. 删标签表
        sessionMapper.deleteTagBySessionId(id);
        // 3. 删届次表
        return sessionMapper.deleteSessionById(id);
    }

    // ========== 工具方法：convertTextToDictCode 保留 ==========
    private String convertTextToDictCode(String dictType, String textLabels) {
        if (textLabels == null || textLabels.trim().isEmpty() || "null".equals(textLabels.trim())) {
            return "";
        }
        String handleText = textLabels.trim().replaceAll("，", ",");
        String[] textArr = handleText.split(",");
        StringBuilder codeSb = new StringBuilder();

        for (String text : textArr) {
            String trimText = text.trim();
            if (trimText.isEmpty()) continue;

            String cleanText = trimText.replaceAll("\\s+", "").replaceAll("　", "");
            if (cleanText.isEmpty()) continue;

            SysDictData queryDict = new SysDictData();
            queryDict.setDictType(dictType);
            queryDict.setStatus("0");
            queryDict.setDictLabel(cleanText);
            List<SysDictData> dictList = sysDictDataService.selectDictDataList(queryDict);

            if (CollectionUtils.isEmpty(dictList)) {
                SysDictData allDict = new SysDictData();
                allDict.setDictType(dictType);
                allDict.setStatus("0");
                List<SysDictData> allDictList = sysDictDataService.selectDictDataList(allDict);

                for (SysDictData dict : allDictList) {
                    String dictLabel = dict.getDictLabel();
                    if (dictLabel == null) continue;
                    String cleanDictLabel = dictLabel.replaceAll("\\s+", "").replaceAll("　", "");
                    if (cleanText.equals(cleanDictLabel)) {
                        dictList = Collections.singletonList(dict);
                        break;
                    }
                }
            }

            if (CollectionUtils.isEmpty(dictList)) {
                throw new ServiceException("系统字典【" + dictType + "】中未找到标签【" + trimText + "】（清洗后：" + cleanText + "）");
            }

            String dictValue = dictList.get(0).getDictValue();
            codeSb.append(dictValue).append(",");
        }

        return codeSb.length() > 0 ? codeSb.deleteCharAt(codeSb.length()-1).toString() : "";
    }

    // ========== 修复导入逻辑：加事务+校验comp是否为null ==========
    @Transactional(rollbackFor = Exception.class) // 核心：新增事务注解
    @Override
    public String importSession(List<Session> sessionList, boolean updateSupport) {
        if (CollectionUtils.isEmpty(sessionList)) {
            throw new ServiceException("导入数据不能为空，请检查Excel文件是否有数据！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String operName = SecurityUtils.getUsername();
        for (int i = 0; i < sessionList.size(); i++) {
            Session session = sessionList.get(i);
            int rowNum = i + 2;
            try {
                processSingleSession(session, updateSupport, operName);
                successNum++;
                successMsg.append("<br/>第").append(rowNum).append("行：【")
                        .append(session.getCompetitionName()).append("-").append(session.getSession())
                        .append("】导入成功");
            } catch (Exception e) {
                failureNum++;
                failureMsg.append("<br/>第").append(rowNum).append("行导入失败：").append(e.getMessage());
                log.error("第{}行导入失败", rowNum, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "本次导入共失败" + failureNum + "条，成功" + successNum + "条：");
            if (successNum > 0) {
                return successMsg.toString() + failureMsg.toString();
            } else {
                throw new ServiceException(failureMsg.toString());
            }
        } else {
            return "本次导入全部成功，共导入" + successNum + "条数据：" + successMsg.toString();
        }
    }

    // ========== 核心方法：processSingleSession 修复null校验 ==========
    // ========== 核心方法：processSingleSession 修复null校验 + 调整字典转码顺序 ==========
    @Transactional(rollbackFor = Exception.class)
    private void processSingleSession(Session session, boolean updateSupport, String operName) {
        // ========== 步骤0：先处理字典转码（核心调整：移到创建赛事之前） ==========
        String categoryCode = session.getCategory();
        String levelCode = session.getLevel();
        String tagsCode = session.getTags();

        // 如果是导入场景（categoryImport有值），则覆盖为转码后的值
        if (session.getCategoryImport() != null && !session.getCategoryImport().trim().isEmpty()) {
            categoryCode = convertTextToDictCode("sys_competition_category", session.getCategoryImport());
            // 修复1：回写到session对象，确保创建赛事时能拿到编码
            session.setCategory(categoryCode);
        }
        if (session.getLevelImport() != null && !session.getLevelImport().trim().isEmpty()) {
            levelCode = convertTextToDictCode("sys_competition_level", session.getLevelImport());
            // 修复2：回写到session对象
            session.setLevel(levelCode);
        }
        if (session.getTagsImport() != null && !session.getTagsImport().trim().isEmpty()) {
            tagsCode = convertTextToDictCode("sys_competition_tag", session.getTagsImport());
            // 修复3：回写到session对象
            session.setTags(tagsCode);
        }

        // ========== 步骤1：校验赛事（此时session已带转码后的编码） ==========
        String compName;
        Competition comp;
        if (session.getCompetitionId() != null) {
            // 场景A：前端下拉框选择赛事，通过ID反查
            comp = competitionService.selectCompetitionById(session.getCompetitionId());
            if (comp == null) {
                throw new ServiceException("赛事不存在，请重新选择");
            }
            compName = comp.getName();
        } else if (session.getCompetitionName() != null && !session.getCompetitionName().trim().isEmpty()) {
            // 场景B：批量导入，通过名称自动创建（此时session.getCategory()已是编码）
            compName = session.getCompetitionName().trim();
            comp = competitionService.selectCompetitionByCompName(compName);
            if (comp == null) {
                Competition newComp = new Competition();
                newComp.setName(compName);
                newComp.setStatus("1");
                // 现在能拿到转码后的编码，不再是null
                newComp.setCategory(session.getCategory());
                newComp.setLevel(session.getLevel());
                newComp.setTags(session.getTags());
                newComp.setOrganizations(session.getOrganizations());
                newComp.setScopeType("0");
                newComp.setCreateBy(operName);
                newComp.setCreateTime(DateUtils.getNowDate());
                newComp.setDelFlag("0");
                competitionService.insertCompetition(newComp);
                comp = competitionService.selectCompetitionByCompName(compName);
                if (comp == null) {
                    throw new ServiceException("自动新增赛事【" + compName + "】失败");
                }
            }
        } else {
            throw new ServiceException("赛事名称或ID不能为空");
        }

        // ========== 步骤2：赋值届次表字段（逻辑不变） ==========
        session.setCompetitionId(comp.getId());
        session.setCategory(categoryCode);
        session.setLevel(levelCode);
        session.setTags(tagsCode); // 届次表保留编码，用于导出
        session.setStatus("1");
        session.setDelFlag("0");
        session.setCreateBy(operName);
        session.setCreateTime(DateUtils.getNowDate());
        session.setUpdateBy(operName);
        session.setUpdateTime(DateUtils.getNowDate());

        // ========== 步骤3：重复校验 + 新增/更新届次（逻辑不变） ==========
        String sessionName = session.getSession();
        if (sessionName == null || sessionName.trim().isEmpty()) {
            throw new ServiceException("赛事届次不能为空");
        }
        Session query = new Session();
        query.setCompetitionId(session.getCompetitionId());
        query.setSession(sessionName.trim());
        List<Session> existList = sessionMapper.selectSessionList(query);

        Long sessionId;
        if (CollectionUtils.isEmpty(existList)) {
            this.insertSession(session);
            sessionId = session.getId();
        } else if (updateSupport) {
            session.setId(existList.get(0).getId());
            this.updateSession(session);
            sessionId = session.getId();
        } else {
            throw new ServiceException("该赛事届次已存在，且未开启更新模式");
        }

        // ========== 步骤4：写入标签子表（逻辑不变） ==========
        sessionMapper.deleteTagBySessionId(sessionId);
        if (tagsCode != null && !tagsCode.trim().isEmpty()) {
            String[] tagArray = tagsCode.split(",");
            List<Tag> tagList = new ArrayList<>();
            for (String tagValue : tagArray) {
                if (tagValue.trim().isEmpty()) continue;
                Tag tag = new Tag();
                tag.setCompetitionSessionId(sessionId);
                tag.setTagName(tagValue.trim());
                tag.setCreateBy(operName);
                tag.setCreateTime(DateUtils.getNowDate());
                tag.setDelFlag("0");
                tagList.add(tag);
            }
            if (!tagList.isEmpty()) {
                sessionMapper.batchInsertTag(tagList);
            }
        }
    }
}
