package com.ruoyi.framework.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.ServletUtils;

/**
 * Blocks non-profile operations until the logged-in user completes their profile.
 */
@Component
public class ProfileCompletionFilter extends OncePerRequestFilter
{
    private static final String PROFILE_PATH = "/user/profile";

    private final List<RequestMatcher> allowedMatchers = Arrays.asList(
            new AntPathRequestMatcher("/logout"),
            new AntPathRequestMatcher("/getInfo"),
            new AntPathRequestMatcher("/getRouters"),
            new AntPathRequestMatcher("/system/user/profile/**"),
            new AntPathRequestMatcher("/student/student/list", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/student/student", HttpMethod.POST.name()),
            new AntPathRequestMatcher("/student/student", HttpMethod.PUT.name()),
            new AntPathRequestMatcher("/achievement/teacher/list", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/achievement/teacher", HttpMethod.POST.name()),
            new AntPathRequestMatcher("/achievement/teacher", HttpMethod.PUT.name()));

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
    {
        if (HttpMethod.OPTIONS.matches(request.getMethod()))
        {
            return true;
        }
        return allowedMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser))
        {
            chain.doFilter(request, response);
            return;
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        if (loginUser.getUser() == null || Integer.valueOf(1).equals(loginUser.getUser().getProfileInitialized()))
        {
            chain.doFilter(request, response);
            return;
        }

        AjaxResult ajaxResult = AjaxResult.error(HttpStatus.WARN, "请先完善个人中心信息后再进行其他操作");
        ajaxResult.put("profileIncomplete", true);
        ajaxResult.put("redirect", PROFILE_PATH);
        ServletUtils.renderString(response, JSON.toJSONString(ajaxResult));
    }
}
