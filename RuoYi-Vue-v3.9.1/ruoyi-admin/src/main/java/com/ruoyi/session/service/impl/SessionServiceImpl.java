package com.ruoyi.session.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.session.domain.Tag;
import com.ruoyi.session.mapper.SessionMapper;
import com.ruoyi.session.domain.Session;
import com.ruoyi.session.service.ISessionService;

/**
 * 赛事届次Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-01
 */
@Service
public class SessionServiceImpl implements ISessionService 
{
    @Autowired
    private SessionMapper sessionMapper;

    /**
     * 查询赛事届次
     * 
     * @param id 赛事届次主键
     * @return 赛事届次
     */
    @Override
    public Session selectSessionById(Long id)
    {
        return sessionMapper.selectSessionById(id);
    }

    /**
     * 查询赛事届次列表
     * 
     * @param session 赛事届次
     * @return 赛事届次
     */
    @Override
    public List<Session> selectSessionList(Session session)
    {
        return sessionMapper.selectSessionList(session);
    }

    /**
     * 新增赛事届次
     * 
     * @param session 赛事届次
     * @return 结果
     */
    @Transactional
    @Override
    public int insertSession(Session session)
    {
        session.setCreateTime(DateUtils.getNowDate());
        int rows = sessionMapper.insertSession(session);
        insertTag(session);
        return rows;
    }

    /**
     * 修改赛事届次
     * 
     * @param session 赛事届次
     * @return 结果
     */
    @Transactional
    @Override
    public int updateSession(Session session)
    {
        session.setUpdateTime(DateUtils.getNowDate());
        sessionMapper.deleteTagByCompetitionSessionId(session.getId());
        insertTag(session);
        return sessionMapper.updateSession(session);
    }

    /**
     * 批量删除赛事届次
     * 
     * @param ids 需要删除的赛事届次主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSessionByIds(Long[] ids)
    {
        sessionMapper.deleteTagByCompetitionSessionIds(ids);
        return sessionMapper.deleteSessionByIds(ids);
    }

    /**
     * 删除赛事届次信息
     * 
     * @param id 赛事届次主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteSessionById(Long id)
    {
        sessionMapper.deleteTagByCompetitionSessionId(id);
        return sessionMapper.deleteSessionById(id);
    }

    /**
     * 新增标签信息
     * 
     * @param session 赛事届次对象
     */
    public void insertTag(Session session)
    {
        List<Tag> tagList = session.getTagList();
        Long id = session.getId();
        if (StringUtils.isNotNull(tagList))
        {
            List<Tag> list = new ArrayList<Tag>();
            for (Tag tag : tagList)
            {
                tag.setCompetitionSessionId(id);
                list.add(tag);
            }
            if (list.size() > 0)
            {
                sessionMapper.batchTag(list);
            }
        }
    }
}
