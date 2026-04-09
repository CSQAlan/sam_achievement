package com.ruoyi.reimbursement.service.impl;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletResponse;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.achievement.mapper.SamAchievementMapper;
import com.ruoyi.achievement.mapper.FileUuidMapper;
import com.ruoyi.achievement.domain.FileUuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.reimbursement.mapper.SamReimbursementItemsMapper;
import com.ruoyi.reimbursement.mapper.SamReimbursementRatioMapper;
import com.ruoyi.reimbursement.domain.SamReimbursementItems;
import com.ruoyi.reimbursement.domain.SamReimbursementRatio;
import com.ruoyi.reimbursement.service.ISamReimbursementItemsService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.BaseFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 报销项目Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
@Service
public class SamReimbursementItemsServiceImpl implements ISamReimbursementItemsService
{
    private static final Logger logger = LoggerFactory.getLogger(SamReimbursementItemsServiceImpl.class);

    @Autowired
    private SamReimbursementItemsMapper samReimbursementItemsMapper;

    @Autowired
    private SamAchievementMapper samAchievementMapper;

    @Autowired
    private FileUuidMapper fileUuidMapper;

    @Autowired
    private SamReimbursementRatioMapper samReimbursementRatioMapper;

    /**
     * 查询报销项目
     * 
     * @param id 报销项目主键
     * @return 报销项目
     */
    @Override
    public SamReimbursementItems selectSamReimbursementItemsById(Long id)
    {
        return samReimbursementItemsMapper.selectSamReimbursementItemsById(id);
    }

    /**
     * 查询报销项目列表
     * 
     * @param samReimbursementItems 报销项目
     * @return 报销项目
     */
    @Override
    public List<SamReimbursementItems> selectSamReimbursementItemsList(SamReimbursementItems samReimbursementItems)
    {
        return samReimbursementItemsMapper.selectSamReimbursementItemsList(samReimbursementItems);
    }

    /**
     * 新增报销项目
     * 
     * @param samReimbursementItems 报销项目
     * @return 结果
     */
    @Override
    public int insertSamReimbursementItems(SamReimbursementItems samReimbursementItems)
    {
        samReimbursementItems.setCreateTime(DateUtils.getNowDate());
        return samReimbursementItemsMapper.insertSamReimbursementItems(samReimbursementItems);
    }

    /**
     * 修改报销项目
     * 
     * @param samReimbursementItems 报销项目
     * @return 结果
     */
    @Override
    public int updateSamReimbursementItems(SamReimbursementItems samReimbursementItems)
    {
        samReimbursementItems.setUpdateTime(DateUtils.getNowDate());
        return samReimbursementItemsMapper.updateSamReimbursementItems(samReimbursementItems);
    }

    /**
     * 批量删除报销项目
     * 
     * @param ids 需要删除的报销项目主键
     * @return 结果
     */
    @Override
    public int deleteSamReimbursementItemsByIds(Long[] ids)
    {
        return samReimbursementItemsMapper.deleteSamReimbursementItemsByIds(ids);
    }

    /**
     * 删除报销项目信息
     * 
     * @param id 报销项目主键
     * @return 结果
     */
    @Override
    public int deleteSamReimbursementItemsById(Long id)
    {
        return samReimbursementItemsMapper.deleteSamReimbursementItemsById(id);
    }

    /**
     * 导出报销项目PDF清单
     * 
     * @param id 报销项目主键
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    @Override
    public void exportReimbursementPdf(Long id, HttpServletResponse response) throws IOException
    {
        // 查询报销项目信息
        SamReimbursementItems item = samReimbursementItemsMapper.selectSamReimbursementItemsById(id);
        if (item == null) {
            throw new RuntimeException("报销项目不存在");
        }

        // 设置响应头
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", 
            "attachment; filename=" + URLEncoder.encode("报销清单_" + item.getName() + ".pdf", "UTF-8"));

        try {
            // 创建PDF文档
            Document document = new Document(PageSize.A4);
            OutputStream out = response.getOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, out);
            
            document.open();

            // 设置中文字体
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(bfChinese, 18, Font.BOLD);
            Font headerFont = new Font(bfChinese, 12, Font.BOLD);
            Font contentFont = new Font(bfChinese, 10, Font.NORMAL);

            // 标题
            Paragraph title = new Paragraph("报销项目清单", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 基本信息
            document.add(new Paragraph("项目名称：" + item.getName(), contentFont));
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String reimbursementTime = item.getReimbursementTime() != null ? 
                sdf.format(item.getReimbursementTime()) : "";
            document.add(new Paragraph("报销时间：" + reimbursementTime, contentFont));
            
            document.add(new Paragraph("归属学院：" + (item.getOwnerDepId() != null ? item.getOwnerDepId() : ""), contentFont));
            
            String status = "0".equals(item.getStatus()) ? "进行中" : "已完成";
            document.add(new Paragraph("状态：" + status, contentFont));
            
            document.add(new Paragraph(" ", contentFont)); // 空行

            // 统计信息表格
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(10);
            summaryTable.setSpacingAfter(20);

            // 表头
            PdfPCell cell1 = new PdfPCell(new Phrase("统计项", headerFont));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("金额/数量", headerFont));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(cell2);

            // 数据行
            summaryTable.addCell(new Phrase("报销项目数量", contentFont));
            summaryTable.addCell(new Phrase(item.getAmount() != null ? item.getAmount().toString() + " 项" : "0 项", contentFont));

            summaryTable.addCell(new Phrase("总金额", contentFont));
            summaryTable.addCell(new Phrase(formatMoney(item.getTotalFee()), contentFont));

            summaryTable.addCell(new Phrase("已发放金额", contentFont));
            summaryTable.addCell(new Phrase(formatMoney(item.getPaidFee()), contentFont));

            Double unpaid = 0.0;
            if (item.getTotalFee() != null && item.getPaidFee() != null) {
                unpaid = item.getTotalFee() - item.getPaidFee();
            } else if (item.getTotalFee() != null) {
                unpaid = item.getTotalFee();
            }
            summaryTable.addCell(new Phrase("待发放金额", contentFont));
            summaryTable.addCell(new Phrase(formatMoney(unpaid), contentFont));

            document.add(summaryTable);

            // 备注信息
            if (item.getRemark() != null && !item.getRemark().isEmpty()) {
                document.add(new Paragraph(" ", contentFont)); // 空行
                document.add(new Paragraph("备注：", headerFont));
                document.add(new Paragraph(item.getRemark(), contentFont));
            }

            // 页脚信息
            document.add(new Paragraph(" ", contentFont)); // 空行
            document.add(new Paragraph(" ", contentFont)); // 空行
            Paragraph footer = new Paragraph("生成时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()), contentFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            writer.close();
            out.flush();
            out.close();

        } catch (DocumentException e) {
            throw new IOException("PDF生成失败", e);
        }
    }

    /**
     * 格式化金额
     */
    private String formatMoney(Double money) {
        if (money == null) {
            return "¥0.00";
        }
        return String.format("¥%.2f", money);
    }

    /**
     * 获取批量报销支付信息
     * 
     * @param achievementIds 成果ID列表，逗号分隔
     * @return 支付信息列表
     */
    @Override
    public List<Map<String, Object>> getPaymentInfo(String achievementIds) {
        List<Map<String, Object>> paymentInfoList = new ArrayList<>();
        
        if (achievementIds == null || achievementIds.isEmpty()) {
            return paymentInfoList;
        }
        
        // 解析成果ID列表
        String[] ids = achievementIds.split(",");
        
        for (String achievementId : ids) {
            if (achievementId == null || achievementId.trim().isEmpty()) {
                continue;
            }
            
            Map<String, Object> paymentInfo = new HashMap<>();
            paymentInfo.put("achievementId", achievementId.trim());
            
            // 查询成果的收款码附件（type=6表示收款码）
            List<String> qrCodeUuids = samAchievementMapper.selectAttachmentUuidByAchievementIdAndType(
                achievementId.trim(), 6);
            
            if (qrCodeUuids != null && !qrCodeUuids.isEmpty()) {
                // 获取第一个收款码的UUID
                String qrCodeUuid = qrCodeUuids.get(0);
                paymentInfo.put("qrCodeUuid", qrCodeUuid);
                
                // 查询文件真实路径
                FileUuid fileUuid = fileUuidMapper.selectFileUuidById(qrCodeUuid);
                if (fileUuid != null) {
                    paymentInfo.put("qrCodeUrl", qrCodeUuid);
                    paymentInfo.put("originName", fileUuid.getOriginName());
                } else {
                    paymentInfo.put("qrCodeUrl", null);
                    paymentInfo.put("originName", null);
                }
            } else {
                paymentInfo.put("qrCodeUuid", null);
                paymentInfo.put("qrCodeUrl", null);
                paymentInfo.put("originName", null);
            }
            
            paymentInfoList.add(paymentInfo);
        }
        
        return paymentInfoList;
    }

    @Override
    public List<Map<String, Object>> getReimbursementRules(Long ownerDepId) {
        List<Map<String, Object>> rulesList = new ArrayList<>();
        
        try {
            // 1. 查询学院级规则
            SamReimbursementRatio collegeRatio = new SamReimbursementRatio();
            collegeRatio.setOwnerDepId(ownerDepId);
            collegeRatio.setStatus("1"); // 只查询启用的规则
            List<SamReimbursementRatio> collegeRules = samReimbursementRatioMapper.selectSamReimbursementRatioList(collegeRatio);
            
            // 2. 查询全校通用规则（owner_dep_id IS NULL）
            SamReimbursementRatio globalRatio = new SamReimbursementRatio();
            globalRatio.setOwnerDepId(null);
            globalRatio.setStatus("1"); // 只查询启用的规则
            List<SamReimbursementRatio> globalRules = samReimbursementRatioMapper.selectSamReimbursementRatioList(globalRatio);
            
            // 3. 合并规则，学院级优先
            // 先添加学院级规则
            for (SamReimbursementRatio rule : collegeRules) {
                Map<String, Object> ruleMap = new HashMap<>();
                ruleMap.put("grade", rule.getGrade());
                ruleMap.put("category", rule.getCategory());
                ruleMap.put("ratio", rule.getRatio());
                ruleMap.put("ownerDepId", rule.getOwnerDepId());
                ruleMap.put("ruleType", "学院级");
                rulesList.add(ruleMap);
            }
            
            // 再添加全校通用规则（如果学院级规则中没有相同的等级和类别）
            for (SamReimbursementRatio globalRule : globalRules) {
                boolean exists = false;
                for (SamReimbursementRatio collegeRule : collegeRules) {
                    if (collegeRule.getGrade().equals(globalRule.getGrade()) &&
                        collegeRule.getCategory().equals(globalRule.getCategory())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    Map<String, Object> ruleMap = new HashMap<>();
                    ruleMap.put("grade", globalRule.getGrade());
                    ruleMap.put("category", globalRule.getCategory());
                    ruleMap.put("ratio", globalRule.getRatio());
                    ruleMap.put("ownerDepId", globalRule.getOwnerDepId());
                    ruleMap.put("ruleType", "全校通用");
                    rulesList.add(ruleMap);
                }
            }
            
        } catch (Exception e) {
            logger.error("获取报销比例规则失败", e);
        }
        
        return rulesList;
    }
}
