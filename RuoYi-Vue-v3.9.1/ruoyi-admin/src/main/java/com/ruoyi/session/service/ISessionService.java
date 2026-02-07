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
     * @return 结果（1=成功，0=失败）
     */
    public int insertSession(Session session);

    /**
     * 修改赛事届次
     *
     * @param session 赛事届次
     * @return 结果（1=成功，0=失败）
     */
    public int updateSession(Session session);

    /**
     * 批量删除赛事届次
     *
     * @param ids 需要删除的赛事届次主键集合
     * @return 结果（删除成功的条数）
     */
    public int deleteSessionByIds(Long[] ids);

    /**
     * 删除赛事届次信息
     *
     * @param id 赛事届次主键
     * @return 结果（1=成功，0=失败）
     */
    public int deleteSessionById(Long id);

    /**
     * 批量导入赛事届次数据
     * <p>核心逻辑：1.自动处理Excel文字转字典数字键值 2.按赛事名称检索主表，无则新增 3.届次表重复校验（赛事ID+届次）4.支持更新/禁止重复</p>
     *
     * @param sessionList Excel解析后的赛事届次实体列表
     * @param updateSupport 是否更新已存在的数据（true=更新，false=存在则抛异常）
     * @return 导入结果信息（包含成功/失败条数、每条数据的导入状态）
     * @throws com.ruoyi.common.exception.ServiceException 导入失败时抛出业务异常（如无数据、重复数据、主表新增失败等）
     */
    public String importSession(List<Session> sessionList, boolean updateSupport);
}