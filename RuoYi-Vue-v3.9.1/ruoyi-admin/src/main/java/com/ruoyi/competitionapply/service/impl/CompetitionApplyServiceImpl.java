package com.ruoyi.competitionapply.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.ruoyi.achievement.domain.FileUuid;
import com.ruoyi.achievement.mapper.FileUuidMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.competition.domain.Competition;
import com.ruoyi.competition.domain.CompetitionDeptRel;
import com.ruoyi.competition.service.ICompetitionService;
import com.ruoyi.competitionapply.domain.CompetitionApplyAttachment;
import com.ruoyi.competitionapply.mapper.CompetitionApplyMapper;
import com.ruoyi.competitionapply.domain.CompetitionApply;
import com.ruoyi.competitionapply.service.ICompetitionApplyService;
import com.ruoyi.system.mapper.SysUserMapper;

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

    /**
     * 兜底查询用户信息：
     * 某些场景下（如登录信息未完整回填deptId）需要从数据库补全。
     */
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private FileUuidMapper fileUuidMapper;

    /**
     * 审核通过后需要生成赛事主表数据（sam_competition）
     */
    @Autowired
    private ICompetitionService competitionService;

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

            // 兼容 upload-file 组件：v-model 只需要 uuid 串（逗号分隔）
            if (attachmentList != null && !attachmentList.isEmpty()) {
                StringBuilder attachmentUrls = new StringBuilder();
                for (CompetitionApplyAttachment attachment : attachmentList) {
                    if (StringUtils.isNotBlank(attachment.getUuid())) {
                        attachmentUrls.append(attachment.getUuid()).append(",");
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
     * 规范化附件列表：
     * 1) uuid 由上传接口生成，这里严禁再生成 uuid；
     * 2) 子表仅保存 uuid + 元数据，不保存真实路径；真实路径由 FileUuidMapper 反查；
     * 3) 兼容两种前端入参：competitionApplyAttachmentList（优先） 或 attachmentUrls（兜底）。
     */
    private List<CompetitionApplyAttachment> normalizeAttachments(CompetitionApply competitionApply, SysUser currentUser)
    {
        List<CompetitionApplyAttachment> sourceList = competitionApply.getCompetitionApplyAttachmentList();
        List<String> uuidList = new ArrayList<>();

        // A. 优先从子表列表取 uuid
        if (StringUtils.isNotNull(sourceList) && !sourceList.isEmpty())
        {
            for (CompetitionApplyAttachment item : sourceList)
            {
                if (item == null)
                {
                    continue;
                }
                // 兼容旧前端字段：如果误传到 path，这里也允许兜底成 uuid
                String uuid = StringUtils.isNotBlank(item.getUuid()) ? item.getUuid() : "0";
                uuid = StringUtils.trim(uuid);
                if (StringUtils.isNotBlank(uuid))
                {
                    uuidList.add(uuid);
                }
            }
        }
        else
        {
            // B. 兜底从 attachmentUrls 解析（逗号分隔的 uuid 串）
            String attachmentUrls = competitionApply.getAttachmentUrls();
            if (StringUtils.isNotBlank(attachmentUrls))
            {
                String[] uuidArray = attachmentUrls.split(",");
                for (String uuid : uuidArray)
                {
                    String trimmed = StringUtils.trim(uuid);
                    if (StringUtils.isNotBlank(trimmed))
                    {
                        uuidList.add(trimmed);
                    }
                }
            }
        }

        // 去重 + 保序
        uuidList = uuidList.stream().distinct().collect(Collectors.toList());
        if (uuidList.isEmpty())
        {
            return new ArrayList<>();
        }

        Date now = new Date();
        Long applyId = competitionApply.getId();
        List<CompetitionApplyAttachment> result = new ArrayList<>();
        for (String uuid : uuidList)
        {
            CompetitionApplyAttachment attachment = new CompetitionApplyAttachment();
            // 子表是申请表的子表：必须关联主表ID
            attachment.setCompetitionApplyId(applyId);
            // 只存 uuid（真实路径由 sys_file_uuid 反查）
            attachment.setUuid(uuid);
            attachment.setDelFlag("0");

            // 附件类型：前端应提交 attachmentType；若未传则给默认值 1
            Integer attachmentType = null;
            if (StringUtils.isNotNull(sourceList))
            {
                for (CompetitionApplyAttachment item : sourceList)
                {
                    String itemUuid = item == null ? null : (StringUtils.isNotBlank(item.getUuid()) ? item.getUuid() : "0");
                    if (StringUtils.equals(StringUtils.trim(itemUuid), uuid))
                    {
                        attachmentType = item.getAttachmentType();
                        if (StringUtils.isNotBlank(item.getDocumentName()))
                        {
                            attachment.setDocumentName(item.getDocumentName());
                        }
                        break;
                    }
                }
            }
            attachment.setAttachmentType(attachmentType == null ? 1 : attachmentType);

            // 若前端未传文件名，则通过 uuid 反查 originName 作为展示名
            if (StringUtils.isBlank(attachment.getDocumentName()))
            {
                FileUuid fileUuid = fileUuidMapper.selectFileUuidById(uuid);
                if (fileUuid != null && StringUtils.isNotBlank(fileUuid.getOriginName()))
                {
                    attachment.setDocumentName(fileUuid.getOriginName());
                }
                else
                {
                    attachment.setDocumentName(uuid);
                }
            }

            // 审计字段
            if (StringUtils.isNotNull(currentUser))
            {
                attachment.setCreateBy(currentUser.getUserName());
                attachment.setUpdateBy(currentUser.getUserName());
            }
            attachment.setCreateTime(now);
            attachment.setUpdateTime(now);

            result.add(attachment);
        }
        return result;
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
            // 注意：deptId 可能为空（比如用户未绑定部门，或登录信息未回填deptId），避免空指针
            Long deptId = currentUser.getDeptId();
            if (deptId == null && currentUser.getUserId() != null) {
                // 兜底：从数据库查询一次用户信息获取deptId
                SysUser dbUser = sysUserMapper.selectUserById(currentUser.getUserId());
                if (dbUser != null) {
                    deptId = dbUser.getDeptId();
                }
            }
            if (deptId != null) {
                competitionApply.setApplicantDepId(deptId.toString());
            } else if (StringUtils.isBlank(competitionApply.getApplicantDepId())) {
                // 前端若传空串，这里统一置空，避免写入数值字段时报类型转换错误
                competitionApply.setApplicantDepId(null);
            }
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

        // 3. 写入子表（仅保存 uuid + 元数据；uuid 在上传时生成）
        List<CompetitionApplyAttachment> attachmentList = normalizeAttachments(competitionApply, currentUser);
        if (!attachmentList.isEmpty())
        {
            // 将临时文件标记为正式文件（is_temp: 1 -> 0）
            String[] uuids = attachmentList.stream().map(CompetitionApplyAttachment::getUuid).toArray(String[]::new);
            fileUuidMapper.updateFileUuidStatus(uuids, 0);

            competitionApply.setCompetitionApplyAttachmentList(attachmentList);
            insertCompetitionApplyAttachment(competitionApply);
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
        // 1) 先查原记录：用于权限与状态判断
        if (competitionApply == null || competitionApply.getId() == null)
        {
            throw new ServiceException("参数错误：申请ID不能为空");
        }
        CompetitionApply existing = competitionApplyMapper.selectCompetitionApplyById(competitionApply.getId());
        if (existing == null)
        {
            throw new ServiceException("赛事申请不存在");
        }

        // 2) 只有申请人本人才能修改（管理员不走此页面，后续要放开可再调整）
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null || currentUser.getUserId() == null)
        {
            throw new ServiceException("用户信息异常，无法修改");
        }
        String currentUserId = String.valueOf(currentUser.getUserId());
        if (!StringUtils.equals(currentUserId, existing.getApplicantUserId()))
        {
            throw new ServiceException("无权限：只能修改自己的申请");
        }

        // 3) 状态流转：
        // - 通过(1)：冻结，不允许再修改
        // - 驳回(2)：允许覆盖原记录；提交后重置为待审(0)，并清空上次审核信息
        if ("1".equals(existing.getStatus()))
        {
            throw new ServiceException("已审核通过，不能再修改");
        }
        if ("2".equals(existing.getStatus()))
        {
            competitionApply.setStatus("0");
            competitionApply.setAuditBy(null);
            competitionApply.setAuditTime(null);
            competitionApply.setAuditRemark(null);
            competitionApply.setCompetitionId(null);
        }

        // 填充更新人、更新时间
        if (StringUtils.isNotNull(currentUser)) {
            competitionApply.setUpdateBy(currentUser.getUserName());
        }
        competitionApply.setUpdateTime(DateUtils.getNowDate());

        // 子表仅保存 uuid：uuid 在上传时生成，这里只做规范化与入库
        List<CompetitionApplyAttachment> newAttachmentList = normalizeAttachments(competitionApply, currentUser);
        competitionApply.setCompetitionApplyAttachmentList(newAttachmentList);

        // ========== 删旧插新逻辑 ==========
        // 1. 先删除该申请原有所有附件（子表）
        competitionApplyMapper.deleteCompetitionApplyAttachmentByApplyId(competitionApply.getId());
        // 2. 再插入新附件列表
        if (!newAttachmentList.isEmpty())
        {
            String[] uuids = newAttachmentList.stream().map(CompetitionApplyAttachment::getUuid).toArray(String[]::new);
            fileUuidMapper.updateFileUuidStatus(uuids, 0);
            insertCompetitionApplyAttachment(competitionApply);
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
        // 仅允许删除自己的申请，且已通过的申请不允许删除
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null || currentUser.getUserId() == null)
        {
            throw new ServiceException("用户信息异常，无法删除");
        }
        String currentUserId = String.valueOf(currentUser.getUserId());
        for (Long id : ids)
        {
            CompetitionApply existing = competitionApplyMapper.selectCompetitionApplyById(id);
            if (existing == null)
            {
                continue;
            }
            if (!StringUtils.equals(currentUserId, existing.getApplicantUserId()))
            {
                throw new ServiceException("无权限：只能删除自己的申请");
            }
            if ("1".equals(existing.getStatus()))
            {
                throw new ServiceException("已审核通过，不能删除");
            }
        }
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
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null || currentUser.getUserId() == null)
        {
            throw new ServiceException("用户信息异常，无法删除");
        }
        CompetitionApply existing = competitionApplyMapper.selectCompetitionApplyById(id);
        if (existing == null)
        {
            return 0;
        }
        if (!StringUtils.equals(String.valueOf(currentUser.getUserId()), existing.getApplicantUserId()))
        {
            throw new ServiceException("无权限：只能删除自己的申请");
        }
        if ("1".equals(existing.getStatus()))
        {
            throw new ServiceException("已审核通过，不能删除");
        }
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

    /**
     * 审核赛事申请：
     * 1. 通过：写入审核信息，生成赛事并回写 competitionId
     * 2. 驳回：写入审核信息与驳回原因，不生成赛事
     *
     * 备注：审核更新只更新主表字段，不触碰附件子表。
     */
    @Transactional
    @Override
    public int reviewCompetitionApply(Long id, String status, String auditRemark)
    {
        // 1) 参数校验：只允许 1(通过) 或 2(驳回)
        if (!"1".equals(status) && !"2".equals(status))
        {
            throw new ServiceException("审核状态不合法，仅支持：1(通过)、2(驳回)");
        }
        if ("2".equals(status) && StringUtils.isBlank(auditRemark))
        {
            throw new ServiceException("驳回原因不能为空");
        }

        // 2) 查询申请记录，校验是否存在
        CompetitionApply existing = competitionApplyMapper.selectCompetitionApplyById(id);
        if (existing == null)
        {
            throw new ServiceException("赛事申请不存在");
        }

        // 3) 已通过且已生成赛事的，不重复生成
        Long competitionId = existing.getCompetitionId();
        if ("1".equals(status) && competitionId == null)
        {
            // 3.1 生成赛事主表数据（sam_competition）
            Competition competition = new Competition();
            competition.setName(existing.getName());
            competition.setCategory(existing.getCategory());
            competition.setOrganizations(existing.getOrganizations());
            competition.setLevel(existing.getLevel());
            competition.setTags(existing.getTags());
            competition.setScopeType(existing.getScopeType());
            // 默认启用
            competition.setStatus("1");
            competition.setMemo(existing.getMemo());
            competition.setDelFlag("0");

            // 适用范围为“指定学院”时，默认把申请人学院写入赛事-部门关系
            if ("1".equals(existing.getScopeType()) && StringUtils.isNotBlank(existing.getApplicantDepId()))
            {
                try
                {
                    CompetitionDeptRel rel = new CompetitionDeptRel();
                    rel.setDeptId(Long.valueOf(existing.getApplicantDepId()));
                    rel.setDelFlag("0");
                    List<CompetitionDeptRel> relList = new ArrayList<>();
                    relList.add(rel);
                    competition.setCompetitionDeptRelList(relList);
                }
                catch (NumberFormatException ignore)
                {
                    // applicantDepId 非数字时忽略学院关系，不影响审核通过
                }
            }

            SysUser auditor = SecurityUtils.getLoginUser().getUser();
            if (auditor != null)
            {
                competition.setCreateBy(auditor.getUserName());
                competition.setUpdateBy(auditor.getUserName());
            }

            competitionService.insertCompetition(competition);
            competitionId = competition.getId();
            if (competitionId == null)
            {
                throw new ServiceException("审核通过失败：赛事生成异常");
            }
        }

        // 4) 更新申请审核字段（只更新主表字段）
        SysUser auditor = SecurityUtils.getLoginUser().getUser();
        CompetitionApply update = new CompetitionApply();
        update.setId(id);
        update.setStatus(status);
        update.setAuditRemark(auditRemark);
        update.setAuditTime(new Date());
        if (auditor != null)
        {
            update.setAuditBy(auditor.getUserId());
            update.setUpdateBy(auditor.getUserName());
        }
        update.setUpdateTime(DateUtils.getNowDate());
        if ("1".equals(status))
        {
            update.setCompetitionId(competitionId);
        }
        return competitionApplyMapper.updateCompetitionApply(update);
    }

}
