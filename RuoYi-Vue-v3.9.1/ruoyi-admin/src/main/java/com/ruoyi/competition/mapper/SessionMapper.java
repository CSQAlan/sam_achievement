package com.ruoyi.competition.mapper;

import java.util.List;
import com.ruoyi.competition.domain.Session;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.competition.domain.Tag;

/**
 * 赛事届次Mapper接口
 *
 * @author ruoyi
 * @date 2026-02-01
 */
@Mapper
public interface SessionMapper
{
    /**
     * 查询赛事届次
     *
     * @param id 赛事届次主键
     * @return 赛事届次
     */
    public Session selectSessionById(Long id);

    /**
     * 查询赛事届次列表（支持多条件筛选，批量导入时复用做重复校验）
     *
     * @param session 赛事届次
     * @return 赛事届次集合
     */
    public List<Session> selectSessionList(Session session);

    /**
     * 新增赛事届次
     *
     * @param session 赛事届次
     * @return 结果
     */
    public int insertSession(Session session);

    /**
     * 修改赛事届次
     *
     * @param session 赛事届次
     * @return 结果
     */
    public int updateSession(Session session);

    /**
     * 批量更新届次状态（用于预录批量启用）
     *
     * @param ids 届次ID集合
     * @param status 状态（0停用/1启用/2预录）
     * @param updateBy 更新人
     * @return 结果
     */
    public int updateSessionStatusByIds(@Param("ids") Long[] ids, @Param("status") String status, @Param("updateBy") String updateBy);

    /**
     * 删除赛事届次
     *
     * @param id 赛事届次主键
     * @return 结果
     */
    public int deleteSessionById(Long id);

    /**
     * 批量删除赛事届次
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSessionByIds(Long[] ids);

    // ====================== 新增：删除成果表的方法（解决爆红） ======================
    /**
     * 根据届次ID删除关联的成果表数据
     * @param sessionId 届次主键ID
     * @return 影响行数
     */
    public int deleteAchievementBySessionId(Long sessionId);

    /**
     * 批量删除关联的成果表数据
     * @param ids 届次主键ID数组
     * @return 影响行数
     */
    public int deleteAchievementBySessionIds(Long[] ids);

    // ====================== 原有：标签子表操作方法 ======================
    /**
     * 根据届次ID删除关联的标签子表数据
     * @param sessionId 届次主键ID
     * @return 影响行数
     */
    public int deleteTagBySessionId(Long sessionId);

    /**
     * 批量删除关联的标签子表数据
     * @param ids 届次主键ID数组
     * @return 影响行数
     */
    public int deleteTagBySessionIds(Long[] ids); // 这个也补上，批量删标签用

    /**
     * 新增单条标签数据
     * @param tag 标签实体
     * @return 影响行数
     */
    public int insertTag(Tag tag);

    /**
     * 批量新增标签数据（可选：优化批量插入性能）
     * @param tagList 标签列表
     * @return 影响行数
     */
    public int batchInsertTag(List<Tag> tagList);

    /**
     * 根据届次ID查询关联的标签列表
     * @param sessionId 届次主键ID
     * @return 标签列表
     */
    public List<Tag> selectTagListBySessionId(Long sessionId);
}
