package com.ruoyi.common.utils.pdf;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 竞赛名称相似度匹配器
 * 使用编辑距离(Levenshtein Distance)和字符相似度算法
 * 
 * @author ruoyi
 * @date 2026-04-22
 */
public class SimilarityMatcher {
    
    private static final Logger log = LoggerFactory.getLogger(SimilarityMatcher.class);
    
    /**
     * 相似度匹配结果
     */
    public static class MatchResult {
        public String pdfName;           // PDF中的名称
        public String dbName;            // 数据库中匹配的名称
        public double similarity;        // 相似度（0-1）
        public int matchLevel;           // 匹配等级：1=精确, 2=高度相似, 3=相似
        
        public MatchResult(String pdfName, String dbName, double similarity, int matchLevel) {
            this.pdfName = pdfName;
            this.dbName = dbName;
            this.similarity = similarity;
            this.matchLevel = matchLevel;
        }
    }
    
    /**
     * 计算两个字符串的编辑距离(Levenshtein Distance)
     * 值越小表示越相似
     */
    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        
        int len1 = s1.length();
        int len2 = s2.length();
        
        int[][] dp = new int[len1 + 1][len2 + 1];
        
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[len1][len2];
    }
    
    /**
     * 基于编辑距离计算相似度 (0-1)
     * 公式: 1 - (editDistance / maxLen)
     */
    public static double calculateSimilarity(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) {
            return 1.0;
        }
        
        int distance = levenshteinDistance(s1, s2);
        return 1.0 - (double) distance / maxLen;
    }
    
    /**
     * 检查是否精确匹配（规范化后相同）
     */
    public static boolean isExactMatch(String s1, String s2) {
        String normalized1 = NormalizeUtil.normalizeStrict(s1);
        String normalized2 = NormalizeUtil.normalizeStrict(s2);
        return normalized1.equals(normalized2);
    }
    
    /**
     * 从候选列表中查找最相似的项
     * 
     * @param pdfName PDF中的竞赛名称
     * @param candidates 候选列表
     * @param similarityThreshold 相似度阈值 (0-1)
     * @return 匹配结果，如果未找到则返回null
     */
    public static MatchResult findBestMatch(String pdfName, List<String> candidates, double similarityThreshold) {
        if (pdfName == null || pdfName.trim().isEmpty() || candidates == null || candidates.isEmpty()) {
            return null;
        }
        
        String normalizedPdf = NormalizeUtil.normalize(pdfName);
        MatchResult bestMatch = null;
        double maxSimilarity = 0;
        
        for (String candidate : candidates) {
            if (candidate == null || candidate.trim().isEmpty()) {
                continue;
            }
            
            String normalizedCandidate = NormalizeUtil.normalize(candidate);
            
            // 优先检查精确匹配
            if (isExactMatch(pdfName, candidate)) {
                return new MatchResult(pdfName, candidate, 1.0, 1);
            }
            
            // 计算相似度
            double similarity = calculateSimilarity(normalizedPdf, normalizedCandidate);
            
            if (similarity >= similarityThreshold && similarity > maxSimilarity) {
                maxSimilarity = similarity;
                int matchLevel = similarity >= 0.85 ? 2 : 3;  // 0.85以上为高度相似，否则为相似
                bestMatch = new MatchResult(pdfName, candidate, similarity, matchLevel);
            }
        }
        
        return bestMatch;
    }
    
    /**
     * 批量匹配：从PDF名称列表中找出所有可能的匹配
     * 返回分类后的结果：精确匹配、高度相似、相似
     * 
     * @param pdfNames PDF中的竞赛名称列表
     * @param candidates 数据库候选列表
     * @param similarityThreshold 相似度阈值，仅用于低于精确匹配的候选
     * @return 按等级分类的匹配结果
     */
    public static BatchMatchResult batchMatch(List<String> pdfNames, List<String> candidates, double similarityThreshold) {
        BatchMatchResult result = new BatchMatchResult();
        
        if (pdfNames == null || candidates == null) {
            return result;
        }
        
        for (String pdfName : pdfNames) {
            if (pdfName == null || pdfName.trim().isEmpty()) {
                continue;
            }
            
            MatchResult match = findBestMatch(pdfName, candidates, similarityThreshold);
            
            if (match != null) {
                if (match.matchLevel == 1) {
                    result.exactMatches.add(match);
                } else if (match.matchLevel == 2) {
                    result.highSimilarityMatches.add(match);
                } else {
                    result.similarMatches.add(match);
                }
            } else {
                result.unmatchedNames.add(pdfName);
            }
        }
        
        return result;
    }
    
    /**
     * 批量匹配结果容器
     */
    public static class BatchMatchResult {
        public List<MatchResult> exactMatches = new ArrayList<>();       // 精确匹配
        public List<MatchResult> highSimilarityMatches = new ArrayList<>();  // 高度相似 (≥85%)
        public List<MatchResult> similarMatches = new ArrayList<>();     // 相似 (<85%)
        public List<String> unmatchedNames = new ArrayList<>();          // 无匹配
    }
}
