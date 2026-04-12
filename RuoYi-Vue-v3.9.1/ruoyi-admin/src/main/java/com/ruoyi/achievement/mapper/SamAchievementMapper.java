package com.ruoyi.achievement.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.domain.SamAchievementParticipant;
import com.ruoyi.achievement.domain.SamAchievementAdvisor;
import com.ruoyi.achievement.domain.SamAchievementAttachment;
import com.ruoyi.achievement.domain.ExportAchievementBaseVo;
import com.ruoyi.achievement.domain.ExportAttachmentFileVo;
/**
 * 成果录入Mapper接口
 *
 * @author 王璨
 * @date 2026-02-03
 */
@Mapper
public interface SamAchievementMapper
{
    /**
     * 查询成果录入
     *
     * @param achievementId 成果录入主键
     * @return 成果录入
     */
    public SamAchievement selectSamAchievementByAchievementId(String achievementId);

    /**
     * 查询成果基本信息，包括名称和负责人信息
     *
     * @param achievementId 成果录入主键
     * @return 成果基本信息
     */
    public Map<String, Object> selectAchievementInfoById(String achievementId);

    /**
     * 查询成果录入列表
     *
     * @param samAchievement 成果录入
     * @return 成果录入集合
     */
    public List<SamAchievement> selectSamAchievementList(SamAchievement samAchievement);

    /**
     * 查询我参与的成果列表（学生端-非负责人）
     */
    public List<SamAchievement> selectSamAchievementListByStudentId(SamAchievement samAchievement);

    /**
     * 查询我负责的成果列表（学生端-负责人）
     */
    public List<SamAchievement> selectSamAchievementListByResponsibleStudentId(SamAchievement samAchievement);

    /**
     * 查询我指导的成果列表（教师端）
     */
    public List<SamAchievement> selectSamAchievementListByTeacherId(SamAchievement samAchievement);

    /**
     * 根据用户ID查询其作为学生或教师参与的所有成果列表
     */
    public List<SamAchievement> selectSamAchievementListByUserId(SamAchievement samAchievement);

    /**
     * 新增成果录入
     *
     * @param samAchievement 成果录入
     * @return 结果
     */
    public int insertSamAchievement(SamAchievement samAchievement);

    /**
     * 修改成果录入
     *
     * @param samAchievement 成果录入
     * @return 结果
     */
    public int updateSamAchievement(SamAchievement samAchievement);

    /**
     * 删除成果录入
     *
     * @param achievementId 成果录入主键
     * @return 结果
     */
    public int deleteSamAchievementByAchievementId(String achievementId);

    /**
     * 批量删除成果录入
     *
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamAchievementByAchievementIds(String[] achievementIds);

    /**
     * 校验证书编号是否唯一
     *
     * @param certificateNo 证书编号
     * @param competitionId 赛事ID
     * @return 结果
     */
    public SamAchievement checkCertificateNoUnique(@Param("certificateNo") String certificateNo, @Param("competitionId") String competitionId);

    /**
     * 根据比赛和届次查询已有的赛道列表
     *
     * @param competitionId 比赛ID
     * @param sessionId 届次ID
     * @return 赛道列表
     */
    public List<String> selectTrackList(@Param("competitionId") Long competitionId, @Param("sessionId") Long sessionId);

    /**
     * 批量删除参赛选手
     */
    public int deleteSamAchievementParticipantByParticipantIds(String[] achievementIds);

    /**
     * 批量新增参赛选手
     */
    public int batchSamAchievementParticipant(List<SamAchievementParticipant> samAchievementParticipantList);

    /**
     * 通过成果录入主键删除参赛选手信息
     */
    public int deleteSamAchievementParticipantByParticipantId(String achievementId);

    /**
     * 批量新增指导老师
     */
    public int batchSamAchievementAdvisor(List<SamAchievementAdvisor> samAchievementAdvisorList);

    /**
     * 通过成果录入主键删除指导老师信息
     */
    public int deleteSamAchievementAdvisorByAchievementId(String achievementId);

    /**
     * 批量删除指导老师
     */
    public int deleteSamAchievementAdvisorByAchievementIds(String[] achievementIds);

    /**
     * 批量新增附件关联
     */
    public int batchSamAchievementAttachment(List<SamAchievementAttachment> samAchievementAttachmentList);

    /**
     * 通过成果录入主键删除附件信息
     */
    public int deleteSamAchievementAttachmentByAchievementId(String achievementId);

    /**
     * 批量删除附件关联
     */
    public int deleteSamAchievementAttachmentByAchievementIds(String[] achievementIds);

    /**
     * 查询当前用户有权限导出的成果及负责人信息
     */
    public List<ExportAchievementBaseVo> selectAuthorizedExportAchievementBase(
            @Param("achievementIds") String[] achievementIds,
            @Param("loginName") String loginName,
            @Param("sourceMode") String sourceMode,
            @Param("isStudent") boolean isStudent,
            @Param("isTeacher") boolean isTeacher,
            @Param("isAdmin") boolean isAdmin);

    /**
     * 查询成果附件文件信息
     */
    public List<ExportAttachmentFileVo> selectExportAttachmentFiles(
            @Param("achievementIds") String[] achievementIds,
            @Param("types") Integer[] types);

    /**
     * 根据成果ID和附件类型查询附件UUID列表
     *
     * @param achievementId 成果ID
     * @param type 附件类型
     * @return 附件UUID列表
     */
    public List<String> selectAttachmentUuidByAchievementIdAndType(@Param("achievementId") String achievementId, @Param("type") Integer type);
}
