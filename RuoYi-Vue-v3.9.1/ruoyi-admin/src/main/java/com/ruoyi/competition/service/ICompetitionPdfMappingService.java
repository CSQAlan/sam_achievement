package com.ruoyi.competition.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.utils.pdf.SimilarityMatcher.MatchResult;

/**
 * 竞赛PDF映射Service接口
 * 用于处理PDF中的竞赛清单与系统竞赛数据的匹配
 * 
 * @author ruoyi
 * @date 2026-04-22
 */
public interface ICompetitionPdfMappingService {
    
    /**
     * 从PDF文件中提取并匹配竞赛名称 (届次级别)
     * 返回已分类的匹配结果
     * 
     * @param pdfFile PDF文件
     * @param similarityThreshold 相似度阈值（0-1）
     * @param year 指定年份
     * @return 匹配结果Map：包含exactMatches、highSimilarityMatches、similarMatches、unmatchedNames
     * @throws Exception 如果处理失败
     */
    Map<String, Object> extractAndMatchFromPdf(File pdfFile, double similarityThreshold, Integer year) throws Exception;
    
    /**
     * 批量添加多个标签到竞赛届次
     * 
     * @param sessionIds 届次ID列表
     * @param tagCodes 要添加的标签编码列表
     * @return 成功更新的记录数
     */
    int batchAddTagsToSessions(List<Long> sessionIds, List<String> tagCodes);
    
    /**
     * 获取指定届次的当前标签
     * 
     * @param sessionId 届次ID
     * @return 标签字符串（逗号分隔）
     */
    String getSessionTags(Long sessionId);
    
    /**
     * 确认并建立关联 (届次级别)
     * 
     * @param sessionIds 届次ID列表
     * @param tagCodes 标签编码列表
     * @param filename 原始文件名
     * @param year 年份
     * @return 建立关联成功的记录数
     */
    int confirmAndLink(List<Long> sessionIds, List<String> tagCodes, String filename, Integer year);
    
    /**
     * 建立手动关联并学习别名
     * 
     * @param sessionId 届次ID
     * @param pdfName PDF中的原始名称
     * @return 是否关联成功
     */
    boolean linkManualMatch(Long sessionId, String pdfName);

    /**
     * 撤销指定导入
     * 
     * @param logId 日志ID
     * @return 撤销成功的记录数
     */
    int undoImport(Long logId);

    /**
     * 添加标签到届次（避免重复）
     * 
     * @param sessionId 届次ID
     * @param newTagCode 新标签编码
     * @return 是否成功添加
     */
    boolean addTagToSession(Long sessionId, String newTagCode);

    /**
     * 批量从竞赛届次中删除指定标签
     * 
     * @param sessionIds 届次ID列表
     * @param tagCodes 要删除的标签编码列表
     * @return 成功更新的记录数
     */
    int batchRemoveTagsFromSessions(List<Long> sessionIds, List<String> tagCodes);
}
