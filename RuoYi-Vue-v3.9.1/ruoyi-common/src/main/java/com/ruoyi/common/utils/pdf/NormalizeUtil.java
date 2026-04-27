package com.ruoyi.common.utils.pdf;

/**
 * 名称规范化工具类
 * 将不同格式的竞赛名称统一规范化，便于匹配
 * 
 * @author ruoyi
 * @date 2026-04-22
 */
public class NormalizeUtil {
    
    /**
     * 规范化竞赛名称
     * 处理：
     * 1. 去除首尾空格
     * 2. 统一中文全角符号为半角
     * 3. 去除年份、括号内容等修饰词（可选）
     * 4. 转小写
     * 5. 去除多余空格
     * 
     * @param name 原始名称
     * @return 规范化后的名称
     */
    public static String normalize(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        
        // 1. 去除首尾空格
        String result = name.trim();
        
        // 2. 统一中文全角符号为半角，去除特殊点号和连接符
        result = result.replaceAll("，", ",")
                       .replaceAll("；", ";")
                       .replaceAll("：", ":")
                       .replaceAll("（", "(")
                       .replaceAll("）", ")")
                       .replaceAll("【", "[")
                       .replaceAll("】", "]")
                       .replaceAll("『", "[")
                       .replaceAll("』", "]")
                       .replaceAll("·", "")
                       .replaceAll("•", "");
        
        // 3. 去除年份 (如: 2023, 2024年)
        result = result.replaceAll("(20|19)\\d{2}(年|年度)?", "");

        // 4. 去除多余空格
        result = result.replaceAll("\\s+", " ");
        
        // 5. 转小写（仅处理英文）
        result = result.toLowerCase();
        
        return result.trim();
    }
    
    /**
     * 缩短版本的规范化（仅去除空格和标点转换）
     * 用于精确匹配前的规范化
     */
    public static String normalizeStrict(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        
        String result = name.trim();
        
        // 统一全角符号
        result = result.replaceAll("[，；：（）【】『』]", "");
        
        // 去除多余空格
        result = result.replaceAll("\\s+", "");
        
        return result;
    }
}
