package com.ruoyi.demo.util;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CasAttrDecodeUtil {

    public static String smartDecode(Object raw) {
        if (raw == null) return null;
        String s = raw.toString().trim();
        if (s.isEmpty()) return s;

        // base64 通常只包含这些字符；先做快速筛选减少误解码
        if (!s.matches("^[A-Za-z0-9+/=]+$")) {
            return s;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(s);
            String out = new String(decoded, StandardCharsets.UTF_8).trim();
            // 防止“误解码成乱码”：简单兜底
            if (out.isEmpty()) return s;
            return out;
        } catch (IllegalArgumentException ex) {
            return s; // 不是 base64 就原样返回
        }
    }
}
