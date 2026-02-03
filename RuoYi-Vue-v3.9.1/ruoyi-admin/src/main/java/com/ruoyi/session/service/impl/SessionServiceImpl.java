package com.ruoyi.session.service.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.session.mapper.SessionMapper;
import com.ruoyi.session.domain.Session;
import com.ruoyi.session.service.ISessionService;
// 正确导入（删除其他包的CollectionUtils导入）
import org.springframework.util.CollectionUtils;

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
        // 直接单表插入，无需任何标签子表操作
        return sessionMapper.insertSession(session);
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
        // 直接单表更新，tags字段随主表一起更新
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
        // 仅批量删除主表数据，无标签子表操作
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
        // 仅删除主表数据，无标签子表操作
        return sessionMapper.deleteSessionById(id);
    }

    /**
     * 批量导入赛事届次
     */

    @Transactional
    @Override
    public String importSession(List<Session> sessionList, boolean updateSupport) {
        if (CollectionUtils.isEmpty(sessionList)) {
            throw new ServiceException("导入数据不能为空");
        }
        int successNum = 0;
        StringBuilder successMsg = new StringBuilder();
        for (Session session : sessionList) {
            try {
                // 1. 设置默认状态为“启用”（根据实际字典键值调整，比如字典中“启用”对应值为“0”）
                session.setStatus("1");
                // 2. 设置创建人、时间等基础信息
                session.setCreateBy(SecurityUtils.getUsername());
                session.setCreateTime(DateUtils.getNowDate());
                // 3. 新增或更新数据
                this.insertSession(session);
                successNum++;
            } catch (Exception e) {
                // 异常处理
            }
        }
        successMsg.append("导入成功").append(successNum).append("条");
        return successMsg.toString();
    }
}