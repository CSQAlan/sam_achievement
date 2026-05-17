package com.ruoyi.competition.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.pdf.PdfParseUtil;
import com.ruoyi.common.utils.pdf.SimilarityMatcher;
import com.ruoyi.common.utils.pdf.SimilarityMatcher.MatchResult;
import com.ruoyi.common.utils.pdf.SimilarityMatcher.BatchMatchResult;
import com.ruoyi.competition.domain.Competition;
import com.ruoyi.competition.domain.Session;
import com.ruoyi.competition.mapper.CompetitionMapper;
import com.ruoyi.competition.service.ICompetitionPdfMappingService;
import com.ruoyi.competition.service.ICompetitionService;
import com.ruoyi.competition.service.ISessionService;

/**
 * 竞赛PDF映射Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-22
 */
@Service
public class CompetitionPdfMappingServiceImpl implements ICompetitionPdfMappingService {
    
    private static final Logger log = LoggerFactory.getLogger(CompetitionPdfMappingServiceImpl.class);
    
    @Autowired
    private ICompetitionService competitionService;
    
    @Autowired
    private ISessionService sessionService;
    
    @Autowired
    private CompetitionMapper competitionMapper;
    
    
    /**
     * 从PDF文件中提取并匹配竞赛名称 (届次级别)
     */
    @Override
    public Map<String, Object> extractAndMatchFromPdf(File pdfFile, double similarityThreshold, Integer year) throws Exception {
        // 1. 校验输入
        if (pdfFile == null || !pdfFile.exists()) {
            throw new ServiceException("PDF文件不存在");
        }
        if (similarityThreshold < 0 || similarityThreshold > 1) {
            throw new ServiceException("相似度阈值必须在0-1之间");
        }
        if (year == null) {
            throw new ServiceException("请选择导入年份");
        }
        
        // 2. 从PDF提取竞赛名称
        List<String> pdfCompetitionNames;
        try {
            pdfCompetitionNames = PdfParseUtil.extractCompetitionNamesFromPdf(pdfFile);
        } catch (Exception e) {
            log.error("PDF解析失败", e);
            throw new ServiceException("PDF解析失败：" + e.getMessage());
        }
        
        if (CollectionUtils.isEmpty(pdfCompetitionNames)) {
            throw new ServiceException("PDF中未找到任何竞赛名称");
        }
        
        // 3. 从数据库获取指定年份的所有届次并构建匹配候选集（包含主表别名）
        Session querySession = new Session();
        querySession.setYear(year);
        List<Session> allSessions = sessionService.selectSessionList(querySession);
        
        // 构建 匹配名 -> 所属届次 的映射
        Map<String, Object> candidateMap = new HashMap<>();
        for (Session sess : allSessions) {
            // 使用关联查询出的赛事主名称
            if (StringUtils.isNotBlank(sess.getCompetitionName())) {
                candidateMap.put(sess.getCompetitionName().trim(), sess);
            }
            
            // 获取对应主赛事的别名
            Competition comp = competitionService.selectCompetitionById(sess.getCompetitionId());
            if (comp != null) {
                String memo = comp.getMemo();
                if (StringUtils.isNotBlank(memo) && memo.contains("[ALIAS:")) {
                    int start = memo.indexOf("[ALIAS:") + 7;
                    int end = memo.indexOf("]", start);
                    if (end > start) {
                        String aliasStr = memo.substring(start, end);
                        String[] aliases = aliasStr.split("[,，]");
                        for (String alias : aliases) {
                            if (StringUtils.isNotBlank(alias)) {
                                candidateMap.put(alias.trim(), sess);
                            }
                        }
                    }
                }
            }
        }
        
        List<String> dbMatchStrings = new ArrayList<>(candidateMap.keySet());
        
        // 4. 执行匹配
        BatchMatchResult matchResult = SimilarityMatcher.batchMatch(pdfCompetitionNames, dbMatchStrings, similarityThreshold);
        
        // 5. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("exactMatches", convertToResponseFormat(matchResult.exactMatches, candidateMap));
        result.put("highSimilarityMatches", convertToResponseFormat(matchResult.highSimilarityMatches, candidateMap));
        result.put("similarMatches", convertToResponseFormat(matchResult.similarMatches, candidateMap));
        result.put("unmatchedNames", matchResult.unmatchedNames);
        result.put("totalExtracted", pdfCompetitionNames.size());
        result.put("totalMatched", matchResult.exactMatches.size() + matchResult.highSimilarityMatches.size() + matchResult.similarMatches.size());
        
        log.info("PDF匹配完成(年份={})：精确{}条，高相似{}条，相似{}条，未匹配{}条",
                 year,
                 matchResult.exactMatches.size(),
                 matchResult.highSimilarityMatches.size(),
                 matchResult.similarMatches.size(),
                 matchResult.unmatchedNames.size());
        
        return result;
    }
    
    /**
     * 将匹配结果转换为响应格式
     */
    private List<Map<String, Object>> convertToResponseFormat(List<MatchResult> matches, Map<String, Object> candidateMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (MatchResult match : matches) {
            Session sess = (Session) candidateMap.get(match.dbName);
            
            if (sess != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("sessionId", sess.getId());
                item.put("competitionName", sess.getCompetitionName()); 
                item.put("sessionName", sess.getSession());
                item.put("matchedName", match.dbName);
                item.put("pdfName", match.pdfName);
                item.put("similarity", String.format("%.2f%%", match.similarity * 100));
                item.put("matchLevel", match.matchLevel);
                item.put("currentTags", sess.getTags());
                result.add(item);
            }
        }
        
        return result;
    }
    
    /**
     * 确认并建立关联 (届次级别)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @BizAudit(bizType = "competition_import", bizName = "竞赛导入", opType = BizAuditOpType.IMPORT, handler = "competitionImportBizAuditHandler", async = false)
    public int confirmAndLink(List<Long> sessionIds, List<String> tagCodes, String filename, Integer year) {
        if (CollectionUtils.isEmpty(sessionIds)) {
            return 0;
        }
        
        int successCount = batchAddTagsToSessions(sessionIds, tagCodes);
        
        // 记录日志 (由于不使用数据库，仅记录系统日志)
        if (successCount > 0) {
            log.info("PDF导入确认(年份={})：文件名={}, 标签={}, 操作人={}, 关联ID={}", 
                year, filename, tagCodes, SecurityUtils.getUsername(), StringUtils.join(sessionIds, ","));
        }
        
        return successCount;
    }
    
    /**
     * 建立手动关联并学习别名
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean linkManualMatch(Long sessionId, String pdfName) {
        if (sessionId == null || StringUtils.isBlank(pdfName)) {
            return false;
        }
        
        Session session = sessionService.selectSessionById(sessionId);
        if (session == null) {
            return false;
        }
        
        Competition competition = competitionService.selectCompetitionById(session.getCompetitionId());
        if (competition == null) {
            return false;
        }
        
        String memo = competition.getMemo();
        String aliasTag = "[ALIAS:";
        
        if (StringUtils.isBlank(memo)) {
            memo = aliasTag + pdfName + "]";
        } else if (memo.contains(aliasTag)) {
            // 检查别名是否已存在
            int start = memo.indexOf(aliasTag) + aliasTag.length();
            int end = memo.indexOf("]", start);
            String existingAliases = memo.substring(start, end);
            
            if (!existingAliases.contains(pdfName)) {
                String newMemo = memo.substring(0, end) + "," + pdfName + memo.substring(end);
                memo = newMemo;
            }
        } else {
            memo = memo + "\n" + aliasTag + pdfName + "]";
        }
        
        Competition updateComp = new Competition();
        updateComp.setId(competition.getId());
        updateComp.setMemo(memo);
        updateComp.setUpdateTime(DateUtils.getNowDate());
        updateComp.setUpdateBy(SecurityUtils.getUsername());
        
        return competitionService.updateCompetition(updateComp) > 0;
    }
    
    /**
     * 撤销导入操作 (当前禁用，因为不使用数据库存储日志)
     */
    @Override
    public int undoImport(Long logId) {
        log.warn("撤销导入功能在无数据库日志模式下暂不可用");
        return 0;
    }
    
    /**
     * 批量添加标签到竞赛届次
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchAddTagsToSessions(List<Long> sessionIds, List<String> tagCodes) {
        if (CollectionUtils.isEmpty(sessionIds) || CollectionUtils.isEmpty(tagCodes)) {
            return 0;
        }
        
        int updateCount = 0;
        String operName = SecurityUtils.getUsername();
        
        for (Long sessionId : sessionIds) {
            boolean anyUpdated = false;
            for (String tagCode : tagCodes) {
                if (addTagToSession(sessionId, tagCode)) {
                    anyUpdated = true;
                }
            }
            if (anyUpdated) {
                updateCount++;
            }
        }
        
        log.info("批量添加多标签到届次完成，共更新{}条记录，操作人:{}", updateCount, operName);
        return updateCount;
    }
    
    /**
     * 添加标签到届次（避免重复）
     */
    @Override
    public boolean addTagToSession(Long sessionId, String newTagCode) {
        if (sessionId == null || StringUtils.isBlank(newTagCode)) {
            return false;
        }
        
        // 获取当前标签
        String currentTags = getSessionTags(sessionId);
        
        // 检查是否已包含该标签
        if (StringUtils.isNotBlank(currentTags)) {
            String[] tagArray = currentTags.split(",");
            for (String tag : tagArray) {
                if (tag.equals(newTagCode)) {
                    log.info("届次{}已包含标签{}，跳过", sessionId, newTagCode);
                    return false;  // 已包含，无需添加
                }
            }
            // 追加新标签
            String updatedTags = currentTags + "," + newTagCode;
            return updateSessionTags(sessionId, updatedTags);
        } else {
            // 原本无标签，直接设置
            return updateSessionTags(sessionId, newTagCode);
        }
    }
    
    /**
     * 获取指定届次的当前标签
     */
    @Override
    public String getSessionTags(Long sessionId) {
        Session session = sessionService.selectSessionById(sessionId);
        return session != null ? session.getTags() : null;
    }
    
    /**
     * 批量从竞赛届次中删除指定标签
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchRemoveTagsFromSessions(List<Long> sessionIds, List<String> tagCodes) {
        if (CollectionUtils.isEmpty(sessionIds) || CollectionUtils.isEmpty(tagCodes)) {
            return 0;
        }
        
        int updateCount = 0;
        String operName = SecurityUtils.getUsername();
        
        for (Long sessionId : sessionIds) {
            String currentTags = getSessionTags(sessionId);
            if (StringUtils.isBlank(currentTags)) {
                continue;
            }
            
            List<String> currentTagList = new java.util.ArrayList<>(java.util.Arrays.asList(currentTags.split(",")));
            boolean removed = false;
            for (String tagCode : tagCodes) {
                if (currentTagList.remove(tagCode)) {
                    removed = true;
                }
            }
            
            if (removed) {
                String updatedTags = StringUtils.join(currentTagList, ",");
                if (updateSessionTags(sessionId, updatedTags)) {
                    updateCount++;
                }
            }
        }
        
        log.info("批量移除标签完成，共影响{}条届次记录，操作人:{}", updateCount, operName);
        return updateCount;
    }

    /**
     * 更新届次标签字符串
     */
    private boolean updateSessionTags(Long sessionId, String tags) {
        Session updateSess = new Session();
        updateSess.setId(sessionId);
        updateSess.setTags(tags);
        updateSess.setUpdateBy(SecurityUtils.getUsername());
        updateSess.setUpdateTime(DateUtils.getNowDate());
        return sessionService.updateSession(updateSess) > 0;
    }
}
