package com.ruoyi.competitionapply.mapper;

import java.util.List;
import com.ruoyi.competitionapply.domain.CompetitionApply;
import com.ruoyi.competitionapply.domain.CompetitionApplyAttachment;

/**
 * 赛事申请Mapper接口
 *
 * @author ruoyi
 * @date 2026-02-01
 */
public interface CompetitionApplyMapper
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
     * 新增赛事申请
     *
     * @param competitionApply 赛事申请
     * @return 结果
     */
    public int insertCompetitionApply(CompetitionApply competitionApply);

    /**
     * 修改赛事申请
     *
     * @param competitionApply 赛事申请
     * @return 结果
     */
    public int updateCompetitionApply(CompetitionApply competitionApply);

    /**
     * 删除赛事申请
     *
     * @param id 赛事申请主键
     * @return 结果
     */
    public int deleteCompetitionApplyById(Long id);

    /**
     * 批量删除赛事申请
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCompetitionApplyByIds(Long[] ids);

    /**
     * 批量删除赛事申请附件
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCompetitionApplyAttachmentByApplyIds(Long[] ids);

    /**
     * 批量新增赛事申请附件
     *
     * @param competitionApplyAttachmentList 赛事申请附件列表
     * @return 结果
     */
    public int batchCompetitionApplyAttachment(List<CompetitionApplyAttachment> competitionApplyAttachmentList);


    /**
     * 通过赛事申请主键删除赛事申请附件信息
     *
     * @param id 赛事申请ID
     * @return 结果
     */
    public int deleteCompetitionApplyAttachmentByApplyId(Long id);

    /**
     * 插入赛事申请附件（单条）
     * @param attachment 附件对象
     * @return 结果
     */
    int insertCompetitionApplyAttachment(CompetitionApplyAttachment attachment);

    // ====================== 新增：文件预览所需的2个方法声明 ======================
    /**
     * 根据附件ID查询单个附件信息
     * @param attachmentId 附件主键ID
     * @return 附件信息
     */
    public CompetitionApplyAttachment selectCompetitionApplyAttachmentById(Long attachmentId);

    /**
     * 根据赛事申请ID查询附件列表
     * @param applyId 赛事申请主键ID
     * @return 附件列表
     */
    public List<CompetitionApplyAttachment> selectCompetitionApplyAttachmentListByApplyId(Long applyId);
}