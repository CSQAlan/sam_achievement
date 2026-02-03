package com.ruoyi.session.service;

import java.util.List;
import com.ruoyi.session.domain.Session;

/**
 * 赛事届次Service接口
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
public interface ISessionService 
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
     * 批量删除赛事届次
     * 
     * @param ids 需要删除的赛事届次主键集合
     * @return 结果
     */
    public int deleteSessionByIds(Long[] ids);

    /**
     * 删除赛事届次信息
     * 
     * @param id 赛事届次主键
     * @return 结果
     */
    public int deleteSessionById(Long id);

    /**
     * 批量导入赛事届次
     * @param sessionList 赛事届次列表
     * @param updateSupport 是否更新已存在的数据
     * @return 导入结果信息
     */
    public String importSession(List<Session> sessionList, boolean updateSupport);
}
