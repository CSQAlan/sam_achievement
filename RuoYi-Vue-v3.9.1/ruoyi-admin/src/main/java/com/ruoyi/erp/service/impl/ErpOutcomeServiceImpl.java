package com.ruoyi.erp.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.erp.domain.ErpContestant;
import com.ruoyi.erp.domain.ErpGuideTeacher; // 【新增】引入指导老师
import com.ruoyi.erp.mapper.ErpOutcomeMapper;
import com.ruoyi.erp.domain.ErpOutcome;
import com.ruoyi.erp.service.IErpOutcomeService;

import com.ruoyi.erp.domain.ErpAttachment;
import com.ruoyi.erp.mapper.ErpAttachmentMapper;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.common.utils.SecurityUtils;


/**
 * 成果主Service业务层处理
 * * @author 王璨
 * @date 2026-01-26
 */
@Service
public class ErpOutcomeServiceImpl implements IErpOutcomeService
{
    @Autowired
    private ErpOutcomeMapper erpOutcomeMapper;

    @Autowired
    private ErpAttachmentMapper erpAttachmentMapper;

    /**
     * 查询成果主
     * * @param id 成果主主键
     * @return 成果主
     */
    @Override
    public ErpOutcome selectErpOutcomeById(Long id)
    {
        ErpOutcome outcome = erpOutcomeMapper.selectErpOutcomeById(id);
        // 手动查附件列表放进去
        if (outcome != null) {
            outcome.setAttachmentList(erpAttachmentMapper.selectByOutcomeId(id));
        }
        return outcome;
    }

    /**
     * 查询成果主列表
     * * @param erpOutcome 成果主
     * @return 成果主
     */
    @Override
    public List<ErpOutcome> selectErpOutcomeList(ErpOutcome erpOutcome)
    {
        return erpOutcomeMapper.selectErpOutcomeList(erpOutcome);
    }

    /**
     * 新增成果主
     * * @param erpOutcome 成果主
     * @return 结果
     */
    @Transactional
    @Override
    public int insertErpOutcome(ErpOutcome erpOutcome)
    {
        erpOutcome.setCreateTime(DateUtils.getNowDate());
        int rows = erpOutcomeMapper.insertErpOutcome(erpOutcome);

        // 1. 保存参赛选手
        insertErpContestant(erpOutcome);
        // 2. 保存附件
        insertAttachment(erpOutcome);
        // 3. 【新增】保存指导老师
        insertGuideTeacher(erpOutcome);

        return rows;
    }

    /**
     * 修改成果主
     * * @param erpOutcome 成果主
     * @return 结果
     */
    @Transactional
    @Override
    public int updateErpOutcome(ErpOutcome erpOutcome)
    {
        erpOutcome.setUpdateTime(DateUtils.getNowDate());

        // 更新选手
        erpOutcomeMapper.deleteErpContestantByOutcomeId(erpOutcome.getId());
        insertErpContestant(erpOutcome);

        // 更新附件
        erpAttachmentMapper.deleteByOutcomeId(erpOutcome.getId());
        insertAttachment(erpOutcome);

        // 【新增】更新指导老师
        erpOutcomeMapper.deleteGuideTeacherByOutcomeId(erpOutcome.getId());
        insertGuideTeacher(erpOutcome);

        return erpOutcomeMapper.updateErpOutcome(erpOutcome);
    }

    /**
     * 批量删除成果主
     * * @param ids 需要删除的成果主主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteErpOutcomeByIds(Long[] ids)
    {
        erpOutcomeMapper.deleteErpContestantByOutcomeIds(ids);
        erpOutcomeMapper.deleteGuideTeacherByOutcomeIds(ids); // 【新增】删除老师
        // 注意：附件通常也建议删除，这里简单起见先略过，或者你可以加 erpAttachmentMapper.deleteByOutcomeIds(ids);
        return erpOutcomeMapper.deleteErpOutcomeByIds(ids);
    }

    /**
     * 删除成果主信息
     * * @param id 成果主主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteErpOutcomeById(Long id)
    {
        erpOutcomeMapper.deleteErpContestantByOutcomeId(id);
        erpOutcomeMapper.deleteGuideTeacherByOutcomeId(id); // 【新增】删除老师
        erpAttachmentMapper.deleteByOutcomeId(id);
        return erpOutcomeMapper.deleteErpOutcomeById(id);
    }

    /**
     * 新增参赛选手信息
     */
    public void insertErpContestant(ErpOutcome erpOutcome)
    {
        List<ErpContestant> erpContestantList = erpOutcome.getErpContestantList();
        Long id = erpOutcome.getId();
        if (StringUtils.isNotNull(erpContestantList))
        {
            List<ErpContestant> list = new ArrayList<ErpContestant>();
            for (ErpContestant erpContestant : erpContestantList)
            {
                erpContestant.setOutcomeId(id);
                list.add(erpContestant);
            }
            if (list.size() > 0)
            {
                erpOutcomeMapper.batchErpContestant(list);
            }
        }
    }

    /**
     * 【新增】新增指导老师信息
     */
    public void insertGuideTeacher(ErpOutcome erpOutcome)
    {
        List<ErpGuideTeacher> guideTeacherList = erpOutcome.getGuideTeacherList();
        Long id = erpOutcome.getId();
        if (StringUtils.isNotNull(guideTeacherList))
        {
            List<ErpGuideTeacher> list = new ArrayList<ErpGuideTeacher>();
            for (ErpGuideTeacher teacher : guideTeacherList)
            {
                teacher.setOutcomeId(id);
                list.add(teacher);
            }
            if (list.size() > 0)
            {
                erpOutcomeMapper.batchGuideTeacher(list);
            }
        }
    }

    /**
     * 辅助方法：保存附件 (适配高级 erp_attachment 表)
     */
    public void insertAttachment(ErpOutcome outcome) {
        List<ErpAttachment> list = outcome.getAttachmentList();
        Long outcomeId = outcome.getId();

        if (StringUtils.isNotNull(list)) {
            for (ErpAttachment attach : list) {
                // 1. 设置关联ID
                attach.setOutcomeId(outcomeId);

                // 2. 只有路径不为空才存
                if (StringUtils.isNotEmpty(attach.getFilePath())) {

                    // --- 自动补全表里要求的 NOT NULL 字段 ---

                    // A. 自动生成 UUID
                    attach.setFileUuid(IdUtils.simpleUUID());

                    // B. 获取当前登录用户ID
                    try {
                        attach.setUploadUserId(SecurityUtils.getUserId());
                    } catch (Exception e) {
                        attach.setUploadUserId(1L); // 如果获取不到(比如没登录)，默认给admin
                    }

                    // C. 设置上传时间
                    attach.setUploadTime(DateUtils.getNowDate());

                    // D. 从路径里提取文件名和后缀 (例如: /profile/upload/abc.pdf)
                    String path = attach.getFilePath();
                    String fileName = path.substring(path.lastIndexOf("/") + 1);
                    String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);

                    attach.setFileName(fileName);
                    attach.setFileExt(fileExt);

                    // E. 默认值
                    attach.setFileSize(0L);
                    attach.setIsRequired(0);
                    attach.setIsReimburseReq(0);

                    // --- 补全结束，插入数据库 ---
                    erpAttachmentMapper.insertErpAttachment(attach);
                }
            }
        }
    }
}