package com.ruoyi.session.mapper;

import java.util.List;
import com.ruoyi.session.domain.Session;
import com.ruoyi.session.domain.Tag;

/**
 * 赛事届次Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
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
     * 查询赛事届次列表
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

    /**
     * 批量删除标签
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTagByCompetitionSessionIds(Long[] ids);
    
    /**
     * 批量新增标签
     * 
     * @param tagList 标签列表
     * @return 结果
     */
    public int batchTag(List<Tag> tagList);
    

    /**
     * 通过赛事届次主键删除标签信息
     * 
     * @param id 赛事届次ID
     * @return 结果
     */
    public int deleteTagByCompetitionSessionId(Long id);
}
