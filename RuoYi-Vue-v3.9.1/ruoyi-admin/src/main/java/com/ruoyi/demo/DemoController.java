package com.ruoyi.demo;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.demo.service.ICasUserProvisioningService;
import com.ruoyi.framework.web.service.PermissionService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysUserService;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Controller
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    // 你前端地址（已改为默认 80 端口，访问直接用 localhost 即可）
    private static final String FRONT_URL = "http://localhost";

    // 你写给浏览器的 token cookie key（若依常用）
    private static final String WEB_TOKEN_KEY = "Admin-Token";

    private final TokenService tokenService;
    private final ISysUserService userService;
    private final SysPermissionService sysPermissionService;
    private final ICasUserProvisioningService casUserProvisioningService;

    @Value("${cas.casServerUrlPrefix}")
    private String casServerUrlPrefix;

    @Value("${cas.serverName}")
    private String serverName;


    public DemoController(TokenService tokenService,
                          ISysUserService userService,
                          SysPermissionService syspermissionService,
                          ICasUserProvisioningService casUserProvisioningService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.sysPermissionService = syspermissionService;
        this.casUserProvisioningService = casUserProvisioningService;
    }

    /**
     * CAS 回调入口：
     * - 默认：发若依 token + 写 cookie + 跳回前端
     * - debug=1：保留原本 info 页面展示 attributes（方便你后续 base64 解码和注册映射）
     */
    @RequestMapping(value = "/cas", method = RequestMethod.GET)
    public Object cas(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ticket = request.getParameter("ticket");
        if (StringUtils.hasText(ticket)) {
            log.info("登录票据ticket:[{}]", ticket);
        }

        AttributePrincipalImpl principal = (AttributePrincipalImpl) request.getUserPrincipal();
        if (principal == null) {
            response.sendError(401, "CAS principal is null（可能还没走到验票filter或被放行了）");
            return null;
        }

        String loginName = principal.getName();
        Map<String, Object> attributes = principal.getAttributes();

        log.info("登录名:[{}]", loginName);
        log.info("--------attributes keys--------");
        for (String key : attributes.keySet()) {
            log.info("{}==[{}]", key, attributes.get(key));
        }

        // 1) 保留原逻辑：debug=1 时返回 info 页面
        if ("1".equals(request.getParameter("debug"))) {
            ModelAndView mv = new ModelAndView();
            mv.addObject("loginName", loginName);
            mv.addObject("info", attributes);
            mv.setViewName("info");
            return mv;
        }

        // 2) 正常逻辑：把 CAS 登录结果桥接成若依 token
        SysUser user = userService.selectUserByUserName(loginName);

        // 你后面要做“未注册自动注册”，就把逻辑放这里：
        // if (user == null) { user = casUserProvisioningService.provisionOrUpdate(loginName, attributes); }

        if (user == null) {
            user = casUserProvisioningService.provisionOrUpdate(loginName, attributes);
        }
        if (user == null) {
            response.sendError(403, "自动注册失败：" + loginName);
            return null;
        }

        Set<String> permissions = sysPermissionService.getMenuPermission(user);

        LoginUser loginUser = new LoginUser(user, permissions);
        loginUser.setUserId(user.getUserId());
        loginUser.setDeptId(user.getDeptId());
        String ruoyiToken = tokenService.createToken(loginUser);

        // 3) 把 token 写给浏览器（cookie）
        Cookie cookie = new Cookie(WEB_TOKEN_KEY, ruoyiToken);
        cookie.setPath("/");
        // 本地开发先不加 secure；如果以后 https 再加 cookie.setSecure(true)
        cookie.setHttpOnly(false); // 若依前端一般需要读 token，先别设 true
        response.addCookie(cookie);

        // 4) 跳回前端
        response.sendRedirect(FRONT_URL);
        log.info("requestURL={}", request.getRequestURL());
        log.info("serverName={}, serverPort={}", request.getServerName(), request.getServerPort());

        return null;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String index2(HttpServletRequest request) {
        return "index2";
    }

    @GetMapping("/cas/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1) 获取当前登录用户信息（内部会自动调用 getToken 解析）
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null) {
            tokenService.delLoginUser(loginUser.getToken());
        }

        // 2) 清除浏览器 Cookie
        Cookie cookie = new Cookie(WEB_TOKEN_KEY, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        // 3) 重定向到 CAS 登出地址，要求 CAS 彻底登出后跳回我们的前端主页
        // 拼接成: https://cas服务器/logout?service=http://localhost
        response.sendRedirect(casServerUrlPrefix + "/logout?service=" + FRONT_URL);
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello-world";
    }
}
