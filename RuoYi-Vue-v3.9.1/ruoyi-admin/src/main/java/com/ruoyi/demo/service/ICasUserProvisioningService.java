package com.ruoyi.demo.service;

import com.ruoyi.common.core.domain.entity.SysUser;

import java.util.Map;

public interface ICasUserProvisioningService {

    /**
     * 根据 CAS 回调信息：如果用户不存在则自动注册；存在则可选择同步更新信息
     * @param loginName CAS 返回的唯一登录名（学号/工号）
     * @param attributes CAS 返回的属性（可能需 base64 解码）
     * @return 返回可用于后续发 token 的 SysUser；失败返回 null 或抛业务异常
     */
    SysUser provisionOrUpdate(String loginName, Map<String, Object> attributes);
}
