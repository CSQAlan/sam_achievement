package com.ruoyi.competitionapply.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile; // 新增导入
import com.ruoyi.competitionapply.domain.CompetitionApply;
import com.ruoyi.competitionapply.domain.CompetitionApplyAttachment; // 新增导入

/**
 * 赛事申请Service接口
 *
 * @author ruoyi
 * @date 2026-02-01
 */
public interface ICompetitionApplyService
{
    /**
     * 查询赛事申请
     *
     * @param id 赛事申请主键
     * @return 赛事申请
     */
    public CompetitionApply selectCompetitionApplyById(Long id);

    /**
     * 查询赛事申请列表
     *
     * @param competitionApply 赛事申请
     * @return 赛事申请集合
     */
    public List<CompetitionApply> selectCompetitionApplyList(CompetitionApply competitionApply);

    /**
     * 新增赛事申请（仅表单，无文件）- 原有方法保留
     *
     * @param competitionApply 赛事申请
     * @return 结果
     */
    public int insertCompetitionApply(CompetitionApply competitionApply);

    /**
     * 新增赛事申请（带文件上传）- 核心新增方法
     *
     * @param competitionApply 赛事申请表单数据
     * @param files 上传的附件文件（可多文件，允许为null）
     * @return 结果
     * @throws Exception 文件上传/数据库操作异常
     */
    public int insertCompetitionApplyWithFile(CompetitionApply competitionApply, MultipartFile[] files) throws Exception;

    /**
     * 修改赛事申请
     *
     * @param competitionApply 赛事申请
     * @return 结果
     */
    public int updateCompetitionApply(CompetitionApply competitionApply);

    /**
     * 批量删除赛事申请
     *
     * @param ids 需要删除的赛事申请主键集合
     * @return 结果
     */
    public int deleteCompetitionApplyByIds(Long[] ids);

    /**
     * 删除赛事申请信息
     *
     * @param id 赛事申请主键
     * @return 结果
     */
    public int deleteCompetitionApplyById(Long id);

    // ====================== 新增：文件预览所需的2个方法 ======================
    /**
     * 根据附件ID查询单个附件信息（用于文件预览）
     * @param attachmentId 附件主键ID
     * @return 附件信息
     */
    public CompetitionApplyAttachment selectCompetitionApplyAttachmentById(Long attachmentId);

    /**
     * 根据赛事申请ID查询所有附件列表（用于前端展示已上传文件）
     * @param applyId 赛事申请主键ID
     * @return 附件列表
     */
    public List<CompetitionApplyAttachment> selectCompetitionApplyAttachmentListByApplyId(Long applyId);

}