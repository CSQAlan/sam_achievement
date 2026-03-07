package com.ruoyi.competitionapply.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // 新增导入：处理文件上传
import java.util.ArrayList;
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
     * 查询赛事申请
     *
     * @param id 赛事申请主键
     * @return 赛事申请
     */
    @Override
    public CompetitionApply selectCompetitionApplyById(Long id)
    {
        return competitionApplyMapper.selectCompetitionApplyById(id);
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

        // 2. 插入主表
        int mainResult = competitionApplyMapper.insertCompetitionApply(competitionApply);
        Long mainId = competitionApply.getId();
        if (mainResult <= 0) return 0;

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
     * 修改赛事申请
     *
     * @param competitionApply 赛事申请
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCompetitionApply(CompetitionApply competitionApply)
    {
        competitionApply.setUpdateTime(DateUtils.getNowDate());
        // 修复：删除附件的方法参数名（applyId → competitionApplyId，需确认Mapper中方法名）
        competitionApplyMapper.deleteCompetitionApplyAttachmentByApplyId(competitionApply.getId());
        insertCompetitionApplyAttachment(competitionApply);
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
        // 修复：删除附件的方法参数名（applyIds → competitionApplyIds，需确认Mapper中方法名）
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
        // 修复：删除附件的方法参数名（applyId → competitionApplyId，需确认Mapper中方法名）
        competitionApplyMapper.deleteCompetitionApplyAttachmentByApplyId(id);
        return competitionApplyMapper.deleteCompetitionApplyById(id);
    }

    /**
     * 新增赛事申请附件信息（批量）- 修复字段名
     *
     * @param competitionApply 赛事申请对象
     */
    public void insertCompetitionApplyAttachment(CompetitionApply competitionApply)
    {
        List<CompetitionApplyAttachment> competitionApplyAttachmentList = competitionApply.getCompetitionApplyAttachmentList();
        Long id = competitionApply.getId();
        if (StringUtils.isNotNull(competitionApplyAttachmentList))
        {
            List<CompetitionApplyAttachment> list = new ArrayList<CompetitionApplyAttachment>();
            for (CompetitionApplyAttachment competitionApplyAttachment : competitionApplyAttachmentList)
            {
                // 修复：子表外键字段名改为competitionApplyId
                competitionApplyAttachment.setCompetitionApplyId(id);
                list.add(competitionApplyAttachment);
            }
            if (list.size() > 0)
            {
                competitionApplyMapper.batchCompetitionApplyAttachment(list);
            }
        }
    }

    // ====================== 新增：文件预览所需的2个方法实现 ======================
    /**
     * 根据附件ID查询单个附件信息
     */
    @Override
    public CompetitionApplyAttachment selectCompetitionApplyAttachmentById(Long attachmentId) {
        // 直接调用Mapper的查询方法（你已在XML里新增该SQL）
        return competitionApplyMapper.selectCompetitionApplyAttachmentById(attachmentId);
    }

    /**
     * 根据赛事申请ID查询所有附件列表
     */
    @Override
    public List<CompetitionApplyAttachment> selectCompetitionApplyAttachmentListByApplyId(Long applyId) {
        // 直接调用Mapper的查询方法（你已在XML里新增该SQL）
        return competitionApplyMapper.selectCompetitionApplyAttachmentListByApplyId(applyId);
    }

}