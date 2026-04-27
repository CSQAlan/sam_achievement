package com.ruoyi.common.utils.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF解析工具类
 * 提供从PDF文件中提取文本和比赛名称的能力
 * 
 * @author ruoyi
 * @date 2026-04-22
 */
public class PdfParseUtil {
    
    private static final Logger log = LoggerFactory.getLogger(PdfParseUtil.class);
    
    public static String extractTextFromPdf(File pdfFile) throws IOException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IOException("PDF文件不存在或路径无效");
        }
        
        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }
    
    public static List<String> extractLinesFromPdf(File pdfFile) throws IOException {
        String fullText = extractTextFromPdf(pdfFile);
        List<String> lines = new ArrayList<>();
        
        if (fullText != null && !fullText.trim().isEmpty()) {
            String[] textLines = fullText.split("\\r?\\n");
            for (String line : textLines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    lines.add(trimmedLine);
                }
            }
        }
        
        log.info("从PDF提取到{}行文本", lines.size());
        return lines;
    }
    
    public static List<String> extractCompetitionNamesFromPdf(File pdfFile) throws IOException {
        List<String> allLines = extractLinesFromPdf(pdfFile);
        List<String> competitionNames = new ArrayList<>();
        
        for (String line : allLines) {
            String cleaned = line.trim();
            
            if (cleaned.length() < 2) {
                continue;
            }
            
            if (cleaned.matches("^\\d+$")) {
                continue;
            }
            
            if (cleaned.matches("[\\p{P}\\p{S}]+")) {
                continue;
            }
            
            if (cleaned.matches("^\\d+\\s*[-–—]\\s*\\d+$")) {
                continue;
            }
            
            competitionNames.add(cleaned);
        }
        
        log.info("从PDF智能提取到{}个比赛候选名称", competitionNames.size());
        return competitionNames;
    }
}
