package com.ruoyi.session.service.impl;

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
import com.ruoyi.session.mapper.SessionMapper;
import com.ruoyi.session.domain.Session;
import com.ruoyi.session.service.ISessionService;
// ========== 新增：导入字典相关类（RuoYi框架自带） ==========
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
    // ========== 新增：注入框架自带的字典服务（核心） ==========
    @Autowired
    private ISysDictDataService sysDictDataService;

    // ========== 原有方法：完整保留，无需改动 ==========
    @Override
    public Session selectSessionById(Long id) {
        return sessionMapper.selectSessionById(id);
    }
    // 在 SessionServiceImpl 中修改 selectSessionList 方法
    @Override
    public List<Session> selectSessionList(Session session) {
        List<Session> sessionList = sessionMapper.selectSessionList(session);
        // 补充：遍历列表，从赛事主表查询并赋值 competitionName（解决导出为空）
        for (Session s : sessionList) {
            if (s.getCompetitionId() != null) {
                Competition competition = competitionService.selectCompetitionById(s.getCompetitionId());
                if (competition != null) {
                    s.setCompetitionName(competition.getName());
                }
            }
        }
        return sessionList;
    }
    @Transactional
    @Override
    public int insertSession(Session session) {
        session.setCreateTime(DateUtils.getNowDate());
        return sessionMapper.insertSession(session);
    }
    @Transactional
    @Override
    public int updateSession(Session session) {
        session.setUpdateTime(DateUtils.getNowDate());
        return sessionMapper.updateSession(session);
    }
    @Transactional
    @Override
    public int deleteSessionByIds(Long[] ids) {
        return sessionMapper.deleteSessionByIds(ids);
    }
    @Transactional
    @Override
    public int deleteSessionById(Long id) {
        return sessionMapper.deleteSessionById(id);
    }

    // ========== 新增：通用工具方法（核心）→ 文字标签自动匹配字典数字 ==========
    /**
     * 文字标签转字典数字编码（自动从系统字典查询，无需写死）
     * @param dictType 字典类型（如sys_competition_category），对应@Excel的dictType
     * @param textLabels Excel导入的文字标签（支持多标签，中英文逗号分隔）
     * @return 逗号分隔的数字编码（如"0,1,2"）
     */
    private String convertTextToDictCode(String dictType, String textLabels) {
        if (textLabels == null || textLabels.trim().isEmpty()) {
            return "";
        }
        String handleText = textLabels.trim().replaceAll("，", ",");
        String[] textArr = handleText.split(",");
        StringBuilder codeSb = new StringBuilder();

        for (String text : textArr) {
            String trimText = text.trim();
            if (trimText.isEmpty()) continue;

            // ========== 核心修改1：清洗用户输入的空格 ==========
            // 去掉所有空格（半角、全角、多个连续空格）
            String cleanText = trimText.replaceAll("\\s+", "").replaceAll("　", "");

            // ========== 核心修改2：构造查询条件（兼容去空格匹配） ==========
            SysDictData queryDict = new SysDictData();
            queryDict.setDictType(dictType);    // 字典类型
            queryDict.setStatus("0");           // 只查启用的字典项

            // 第一步：先按原始清洗后的文字查（优先）
            queryDict.setDictLabel(cleanText);
            List<SysDictData> dictList = sysDictDataService.selectDictDataList(queryDict);

            // 第二步：如果没查到，遍历该字典类型下所有标签，去空格后匹配（兜底）
            if (CollectionUtils.isEmpty(dictList)) {
                // 查询该字典类型下所有启用的标签
                SysDictData allDict = new SysDictData();
                allDict.setDictType(dictType);
                allDict.setStatus("0");
                List<SysDictData> allDictList = sysDictDataService.selectDictDataList(allDict);

                // 遍历字典，把字典标签去空格后和用户输入的清洗后文字匹配
                for (SysDictData dict : allDictList) {
                    String dictLabel = dict.getDictLabel();
                    if (dictLabel == null) continue;
                    // 字典标签也去空格
                    String cleanDictLabel = dictLabel.replaceAll("\\s+", "").replaceAll("　", "");
                    // 匹配成功则赋值
                    if (cleanText.equals(cleanDictLabel)) {
                        dictList = Collections.singletonList(dict);
                        break;
                    }
                }
            }

            // 校验是否找到
            if (CollectionUtils.isEmpty(dictList)) {
                throw new ServiceException("系统字典【" + dictType + "】中未找到标签【" + trimText + "】（清洗后：" + cleanText + "）");
            }

            // 获取对应的字典值
            String dictValue = dictList.get(0).getDictValue();
            codeSb.append(dictValue).append(",");
        }

        return codeSb.length() > 0 ? codeSb.deleteCharAt(codeSb.length()-1).toString() : "";
    }

    // ========== 原有方法：importSession 完整保留，无需改动 ==========
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

    // ========== 核心修改：processSingleSession 中的标签处理逻辑 ==========
    @Transactional(rollbackFor = Exception.class)
    private void processSingleSession(Session session, boolean updateSupport, String operName) {
        // 步骤1：校验赛事名称
        String compName = session.getCompetitionName();
        if (compName == null || compName.trim().isEmpty()) {
            throw new ServiceException("赛事名称不能为空");
        }
        compName = compName.trim();

        // ====================== 核心：从【导入专用字段】读文字，永远不会被框架转成数字！ ======================
        String categoryCode = convertTextToDictCode("sys_competition_category", session.getCategoryImport());
        String levelCode = convertTextToDictCode("sys_competition_level", session.getLevelImport());
        String tagsCode = convertTextToDictCode("sys_competition_tag", session.getTagsImport());

        // 步骤2：查询赛事总表，无则自动新增
        Competition comp = competitionService.selectCompetitionByCompName(compName);
        if (comp == null) {
            Competition newComp = new Competition();
            newComp.setName(compName);
            newComp.setStatus("1"); // 固定0=启用，RuoYi标准
            newComp.setCategory(categoryCode);
            newComp.setLevel(levelCode);
            newComp.setTags(tagsCode);
            newComp.setOrganizations(session.getOrganizations());
            newComp.setScopeType("0");
            newComp.setCreateBy(operName);
            newComp.setCreateTime(DateUtils.getNowDate());
            newComp.setDelFlag("0");
            competitionService.insertCompetition(newComp);
            comp = competitionService.selectCompetitionByCompName(compName);
        }

        // 步骤3：赋值给【数据库真实字段】，用于导出和列表显示
        session.setCompetitionId(comp.getId());
        session.setCategory(categoryCode);    // 真实字段存数字
        session.setLevel(levelCode);          // 真实字段存数字
        session.setTags(tagsCode);            // 真实字段存数字
        session.setStatus("1");
        session.setDelFlag("0");

        // 步骤4：重复校验
        String sessionName = session.getSession();
        if (sessionName == null || sessionName.trim().isEmpty()) {
            throw new ServiceException("赛事届次不能为空");
        }
        Session query = new Session();
        query.setCompetitionId(session.getCompetitionId());
        query.setSession(sessionName.trim());
        List<Session> existList = sessionMapper.selectSessionList(query);

        if (CollectionUtils.isEmpty(existList)) {
            this.insertSession(session);
        } else if (updateSupport) {
            session.setId(existList.get(0).getId());
            this.updateSession(session);
        } else {
            throw new ServiceException("该赛事届次已存在，且未开启更新模式");
        }
    }
}