package com.ruoyi.session.service.impl;

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
    @Override
    public List<Session> selectSessionList(Session session) {
        return sessionMapper.selectSessionList(session);
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

            // 核心修改：构造SysDictData查询条件
            SysDictData queryDict = new SysDictData();
            queryDict.setDictType(dictType);    // 字典类型
            queryDict.setDictLabel(trimText);   // 字典文字
            queryDict.setStatus("0");           // 只查启用的字典项

            // 调用现有方法查询字典列表（因为类型+文字唯一，列表只会有一条数据）
            List<SysDictData> dictList = sysDictDataService.selectDictDataList(queryDict);
            if (CollectionUtils.isEmpty(dictList)) {
                throw new ServiceException("系统字典【" + dictType + "】中未找到标签【" + trimText + "】");
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

        // ========== 核心修改：替换原有标签处理，调用自动字典匹配方法 ==========
        // 直接传入@Excel注解的dictType，自动匹配文字→数字，支持多标签
        String categoryCode = convertTextToDictCode("sys_competition_category", session.getCategory());
        String levelCode = convertTextToDictCode("sys_competition_level", session.getLevel());
        String tagsCode = convertTextToDictCode("sys_competition_tag", session.getTags());

        // 步骤2：查询赛事总表，无则自动新增
        Competition comp = competitionService.selectCompetitionByCompName(compName);
        if (comp == null) {
            Competition newComp = new Competition();
            newComp.setName(compName);
            newComp.setStatus("1"); // 修正：1=启用
            // ========== 赋值：自动匹配后的数字编码 ==========
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
            if (comp == null) {
                throw new ServiceException("自动新增总赛事后，未查询到赛事信息，请检查");
            }
            log.info("自动新增总赛事：{}，赛事ID：{}", compName, comp.getId());
        }

        // 步骤3：设置届次表基础信息
        session.setCompetitionId(comp.getId());
        session.setStatus("1"); // 修正：0=启用（原1是停用，必改）
        session.setDelFlag("0");
        session.setCreateBy(operName);
        session.setCreateTime(DateUtils.getNowDate());
        session.setUpdateBy(operName);
        session.setUpdateTime(DateUtils.getNowDate());

        // ========== 赋值：届次表也用自动匹配后的数字编码 ==========
        session.setCategory(categoryCode);
        session.setLevel(levelCode);
        session.setTags(tagsCode);

        // 步骤4：届次重复校验+新增/更新
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