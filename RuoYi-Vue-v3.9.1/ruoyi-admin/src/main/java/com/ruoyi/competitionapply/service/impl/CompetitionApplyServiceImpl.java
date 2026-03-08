package com.ruoyi.competitionapply.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.competitionapply.domain.CompetitionApplyAttachment;
import com.ruoyi.competitionapply.mapper.CompetitionApplyMapper;
import com.ruoyi.competitionapply.domain.CompetitionApply;
import com.ruoyi.competitionapply.service.ICompetitionApplyService;

/**
 * 赛事申请Service业务层处理
 *
 * @author ruoyi
 * @date 2026-02-01
 */
@Service
public class CompetitionApplyServiceImpl implements ICompetitionApplyService
{
    @Autowired
    private CompetitionApplyMapper competitionApplyMapper;

    // 注入用户Mapper，查询当前用户的dept_id
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询赛事申请（关联查询附件列表，供前端修改页面使用）
     *
     * @param id 赛事申请主键
     * @return 赛事申请（包含附件列表）
     */
    @Override
    public CompetitionApply selectCompetitionApplyById(Long id)
    {
        // 查询主表信息
        CompetitionApply competitionApply = competitionApplyMapper.selectCompetitionApplyById(id);
        if (competitionApply != null) {
            // 关联查询该赛事的所有附件列表，赋值给主表对象
            List<CompetitionApplyAttachment> attachmentList = competitionApplyMapper.selectCompetitionApplyAttachmentListByApplyId(id);
            competitionApply.setCompetitionApplyAttachmentList(attachmentList);

            // 拼接附件地址字符串（兼容前端upload-file组件的v-model）
            if (attachmentList != null && !attachmentList.isEmpty()) {
                StringBuilder attachmentUrls = new StringBuilder();
                for (CompetitionApplyAttachment attachment : attachmentList) {
                    if (StringUtils.isNotBlank(attachment.getPath())) {
                        attachmentUrls.append(attachment.getPath()).append(",");
                    }
                }
                if (attachmentUrls.length() > 0) {
                    attachmentUrls.deleteCharAt(attachmentUrls.length() - 1);
                    competitionApply.setAttachmentUrls(attachmentUrls.toString());
                }
            }
        }
        return competitionApply;
    }

    /**
     * 查询赛事申请列表
     *
     * @param competitionApply 赛事申请
     * @return 赛事申请
     */
    @Override
    public List<CompetitionApply> selectCompetitionApplyList(CompetitionApply competitionApply)
    {
        return competitionApplyMapper.selectCompetitionApplyList(competitionApply);
    }

    /**
     * 新增赛事申请（仅表单，无文件）- 原有方法保留，修复子表字段名
     */
    @Transactional
    @Override
    public int insertCompetitionApply(CompetitionApply competitionApply) {
        // 1. 自动填充用户/默认值（原有逻辑保留）
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (StringUtils.isNotNull(currentUser)) {
            competitionApply.setApplicantUserId(currentUser.getUserId().toString());
            competitionApply.setApplicantDepId(currentUser.getDeptId().toString());
            competitionApply.setCreateBy(currentUser.getUserName());
            competitionApply.setUpdateBy(currentUser.getUserName());
        }
        if (StringUtils.isNull(competitionApply.getStatus())) competitionApply.setStatus("0");
        if (StringUtils.isNull(competitionApply.getScopeType())) competitionApply.setScopeType("0");
        if (StringUtils.isNull(competitionApply.getCreateTime())) competitionApply.setCreateTime(new Date());

        // 2. 插入主表（确保ID回写）
        int mainResult = competitionApplyMapper.insertCompetitionApply(competitionApply);
        Long mainId = competitionApply.getId();
        if (mainResult <= 0 || mainId == null) return 0;

        // 3. 解析前端传的 attachmentUrls，拆分成子表数据（核心新增）
        String attachmentUrls = competitionApply.getAttachmentUrls();
        if (StringUtils.isNotBlank(attachmentUrls)) {
            // 按逗号拆分文件地址
            String[] urlArray = attachmentUrls.split(",");
            for (String url : urlArray) {
                if (StringUtils.isBlank(url)) continue;
                // 组装子表对象（适配你的新实体类）
                CompetitionApplyAttachment attachment = new CompetitionApplyAttachment();
                attachment.setCompetitionApplyId(mainId); // 关联主表ID
                attachment.setUuid(IdUtils.fastUUID()); // 生成UUID
                attachment.setPath(url); // 存文件地址
                attachment.setDocumentName(url.substring(url.lastIndexOf("/") + 1)); // 从地址截取文件名
                attachment.setAttachmentType(1); // 默认类型
                attachment.setDelFlag("0");
                // 填充继承字段
                attachment.setCreateBy(currentUser.getUserName());
                attachment.setCreateTime(new Date());
                attachment.setUpdateBy(currentUser.getUserName());
                attachment.setUpdateTime(new Date());
                // 插入子表
                competitionApplyMapper.insertCompetitionApplyAttachment(attachment);
            }
        }

        return mainResult;
    }

    /**
     * 新增赛事申请（带文件上传）- 核心新增方法
     */
    @Transactional // 事务保证主表+子表原子性
    @Override
    public int insertCompetitionApplyWithFile(CompetitionApply competitionApply, MultipartFile[] files) throws Exception {
        // ========== 1. 复用原有逻辑，先插入主表并填充默认值 ==========
        int mainResult = this.insertCompetitionApply(competitionApply);
        if (mainResult <= 0) {
            throw new Exception("主表插入失败，无法继续上传附件");
        }

        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        Long mainId = competitionApply.getId(); // 主表主键ID

        // ========== 2. 处理文件上传并存入子表 ==========
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // 2.1 生成UUID（作为文件唯一标识）
                    String uuid = IdUtils.fastUUID(); // RuoYi自带工具类，生成无横线的UUID

                    // 2.2 上传文件到服务器（RuoYi自带工具类，返回文件相对路径）
                    // 存储目录：/profile/competition/attachment（可根据业务调整）
                    String filePath = FileUploadUtils.upload("/profile/competition/attachment", file);

                    // 2.3 组装子表对象（匹配新实体类字段）
                    CompetitionApplyAttachment attachment = new CompetitionApplyAttachment();
                    attachment.setCompetitionApplyId(mainId); // 关联主表ID
                    attachment.setUuid(uuid); // 存入UUID
                    attachment.setPath(filePath); // 存入文件路径
                    attachment.setDocumentName(file.getOriginalFilename()); // 存入原始文件名
                    attachment.setAttachmentType(1); // 附件类型（1=赛事相关，可自定义）
                    attachment.setDelFlag("0"); // 默认未删除

                    // 2.4 填充继承字段（创建人/时间）
                    attachment.setCreateBy(currentUser.getUserName());
                    attachment.setCreateTime(new Date());
                    attachment.setUpdateBy(currentUser.getUserName());
                    attachment.setUpdateTime(new Date());

                    // 2.5 插入子表
                    competitionApplyMapper.insertCompetitionApplyAttachment(attachment);
                }
            }
        }

        return mainResult;
    }

    /**
     * 修改赛事申请（最终版：适配前端增量操作 + attachmentUrls兜底）
     *
     * @param competitionApply 赛事申请
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCompetitionApply(CompetitionApply competitionApply)
    {
        // 填充更新人、更新时间
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (StringUtils.isNotNull(currentUser)) {
            competitionApply.setUpdateBy(currentUser.getUserName());
        }
        competitionApply.setUpdateTime(DateUtils.getNowDate());

        // ========== 核心修复：从attachmentUrls兜底解析附件列表 ==========
        List<CompetitionApplyAttachment> newAttachmentList = competitionApply.getCompetitionApplyAttachmentList();

        // 如果前端传的列表为空/null，从attachmentUrls解析
        if (StringUtils.isNull(newAttachmentList) || newAttachmentList.isEmpty()) {
            newAttachmentList = new ArrayList<>();
            String attachmentUrls = competitionApply.getAttachmentUrls();
            if (StringUtils.isNotBlank(attachmentUrls)) {
                String[] urlArray = attachmentUrls.split(",");
                for (String url : urlArray) {
                    if (StringUtils.isBlank(url)) continue;
                    CompetitionApplyAttachment attachment = new CompetitionApplyAttachment();
                    attachment.setCompetitionApplyId(competitionApply.getId());
                    attachment.setUuid(IdUtils.fastUUID()); // 生成新UUID
                    attachment.setPath(url);
                    attachment.setDocumentName(url.substring(url.lastIndexOf("/") + 1));
                    attachment.setAttachmentType(1);
                    attachment.setDelFlag("0");
                    // 填充创建/更新信息
                    if (StringUtils.isNotNull(currentUser)) {
                        attachment.setCreateBy(currentUser.getUserName());
                        attachment.setUpdateBy(currentUser.getUserName());
                    }
                    attachment.setCreateTime(new Date());
                    attachment.setUpdateTime(new Date());
                    newAttachmentList.add(attachment);
                }
            }
            // 把解析后的列表塞回对象，复用后续逻辑
            competitionApply.setCompetitionApplyAttachmentList(newAttachmentList);
        }

        // ========== 删旧插新逻辑 ==========
        if (StringUtils.isNotNull(newAttachmentList)) {
            // 1. 先删除该赛事原有所有附件
            competitionApplyMapper.deleteCompetitionApplyAttachmentByApplyId(competitionApply.getId());

            // 2. 只有新列表非空时，才批量插入新附件
            if (!newAttachmentList.isEmpty()) {
                for (CompetitionApplyAttachment attachment : newAttachmentList) {
                    attachment.setCompetitionApplyId(competitionApply.getId());
                    attachment.setUuid(StringUtils.isBlank(attachment.getUuid()) ? IdUtils.fastUUID() : attachment.getUuid());
                    attachment.setAttachmentType(StringUtils.isNull(attachment.getAttachmentType()) ? 1 : attachment.getAttachmentType());
                    attachment.setDelFlag(StringUtils.isNull(attachment.getDelFlag()) ? "0" : attachment.getDelFlag());

                    // 填充更新字段
                    if (StringUtils.isNotNull(currentUser)) {
                        attachment.setUpdateBy(currentUser.getUserName());
                    }
                    attachment.setUpdateTime(new Date());

                    // 新增附件补充创建字段
                    if (StringUtils.isNull(attachment.getId())) {
                        if (StringUtils.isNotNull(currentUser)) {
                            attachment.setCreateBy(currentUser.getUserName());
                        }
                        attachment.setCreateTime(new Date());
                    }
                }
                // 批量插入新附件
                insertCompetitionApplyAttachment(competitionApply);
            }
        }

        // 3. 仅更新主表信息
        return competitionApplyMapper.updateCompetitionApply(competitionApply);
    }

    /**
     * 批量删除赛事申请
     *
     * @param ids 需要删除的赛事申请主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCompetitionApplyByIds(Long[] ids)
    {
        // 删除关联的附件
        competitionApplyMapper.deleteCompetitionApplyAttachmentByApplyIds(ids);
        return competitionApplyMapper.deleteCompetitionApplyByIds(ids);
    }

    /**
     * 删除赛事申请信息
     *
     * @param id 赛事申请主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCompetitionApplyById(Long id)
    {
        // 删除关联的附件
        competitionApplyMapper.deleteCompetitionApplyAttachmentByApplyId(id);
        return competitionApplyMapper.deleteCompetitionApplyById(id);
    }

    /**
     * 新增赛事申请附件信息（批量）- 简化逻辑
     */
    public void insertCompetitionApplyAttachment(CompetitionApply competitionApply)
    {
        List<CompetitionApplyAttachment> attachmentList = competitionApply.getCompetitionApplyAttachmentList();
        if (StringUtils.isNotNull(attachmentList) && !attachmentList.isEmpty())
        {
            // 直接批量插入，无需重复设置ID
            competitionApplyMapper.batchCompetitionApplyAttachment(attachmentList);
        }
    }

    /**
     * 根据附件ID查询单个附件信息
     */
    @Override
    public CompetitionApplyAttachment selectCompetitionApplyAttachmentById(Long attachmentId) {
        return competitionApplyMapper.selectCompetitionApplyAttachmentById(attachmentId);
    }

    /**
     * 根据赛事申请ID查询所有附件列表
     */
    @Override
    public List<CompetitionApplyAttachment> selectCompetitionApplyAttachmentListByApplyId(Long applyId) {
        return competitionApplyMapper.selectCompetitionApplyAttachmentListByApplyId(applyId);
    }

}