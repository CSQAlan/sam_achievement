package com.ruoyi.common.utils.file;

import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.InputStream;
import java.io.OutputStream;

public class PdfUtils {
    public static void removeRestrictions(InputStream is, OutputStream os) {
        try (PDDocument document = PDDocument.load(is, "")) {
            if (document.isEncrypted()) {
                document.setAllSecurityToBeRemoved(true);
            }
            document.save(os);
        } catch (Exception e) {
            // 如果不是加密PDF或者加载失败，这里通常应该抛出异常供调用方捕获并决定是否回退
            throw new RuntimeException("PDF restrictions removal failed", e);
        }
    }
}