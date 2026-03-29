package com.ruoyi.achievement.service;

import java.util.List;
import com.ruoyi.achievement.domain.SamAchievement;
import com.ruoyi.achievement.domain.ExportAttachmentZipReq;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 成果录入Service接口
 * 
 * @author 王璨
 * @date 2026-02-03
 */
public interface ISamAchievementService 
{
    /**
     * 查询成果录入
     * 
     * @param achievementId 成果录入主键
     * @return 成果录入
     */
    public SamAchievement selectSamAchievementByAchievementId(String achievementId);

    public SamAchievement selectSamAchievementForSelf(String achievementId, String selfEditScene);

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
     * 查询我负责的成果列表（学生负责人）
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
     * 批量删除成果录入
     * 
     * @param achievementIds 需要删除的成果录入主键集合
     * @return 结果
     */
    public int deleteSamAchievementByAchievementIds(String[] achievementIds);

    /**
     * 删除成果录入信息
     * 
     * @param achievementId 成果录入主键
     * @return 结果
     */
    public int deleteSamAchievementByAchievementId(String achievementId);

    /**
     * 校验证书编号是否唯一
     *
     * @param samAchievement 成果信息
     * @return 结果
     */
    public boolean checkCertificateNoUnique(SamAchievement samAchievement);

    /**
     * 根据比赛和届次查询已有的赛道列表
     *
     * @param competitionId 比赛ID
     * @param sessionId 届次ID
     * @return 赛道列表
     */
    public List<String> selectTrackList(Long competitionId, Long sessionId);

    /**
     * 批量导出成果附件压缩包
     */
    public void exportAttachmentZip(ExportAttachmentZipReq req, HttpServletResponse response) throws IOException;
}
