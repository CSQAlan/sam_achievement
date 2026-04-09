package com.ruoyi.demo;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EventListener;
import java.util.HashMap;

@Configuration
public class Config {

    @Value("${cas.casServerUrlPrefix}")
    private String casServerUrlPrefix; //认证服务端地址前缀,包含协议,域名,端口,上下文
 
    @Value("${cas.serverName}")
    private String serverName; //集成认证的客户端url(处理cas返回的url), 只包含协议,域名, 端口, 非微服务情况下不需要上下文, 微服务环境需要包括网关的前缀
 
    @Bean
    public FilterRegistrationBean filterCustomSingleRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CustomSingleSignOutFilter()); // 自定义登出过滤器 自己实现其登出逻辑
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 用于单点退出，该过滤器用于实现单点登出功能
     */
    @Bean
    public FilterRegistrationBean filterSingleRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SingleSignOutFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 用于单点退出，该过滤器用于实现单点登出功能
     */
    @Bean
    public ServletListenerRegistrationBean<EventListener> singleSignOutListenerRegistration() {
        ServletListenerRegistrationBean<EventListener> registrationBean = new ServletListenerRegistrationBean<EventListener>();
        registrationBean.setListener(new SingleSignOutHttpSessionListener());// 如果不是基于cookie+session 是基于token 模式则注册CustomSingleSignOutHttpSessionListener
        registrationBean.setOrder(1);
                                                                             // 
        return registrationBean;
    }

    /**
     * 该过滤器负责对Ticket的校验工作
     */
    @Bean
    public FilterRegistrationBean filterValidationRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new Cas20ProxyReceivingTicketValidationFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        java.util.Map<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerUrlPrefix", casServerUrlPrefix);
        initParameters.put("serverName", serverName);
        initParameters.put("redirectAfterValidation", "false");// 设置false，表示不重定向 不去掉ticket 参数
        registration.setInitParameters(initParameters);
        // 设定加载的顺序
        registration.setOrder(3);
        return registration;
    }

    /**
     * 该过滤器负责实现HttpServletRequest请求的包裹，比如允许开发者通过HttpServletRequest的getRemoteUser()方法获得SSO登录用户的登录名
     */
    @Bean
    public FilterRegistrationBean filterWrapperRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestWrapperFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        // 设定加载的顺序
        registration.setOrder(4);
        return registration;
    }

    /**
     * 该过滤器使得开发者可以通过org.jasig.cas.client.util.AssertionHolder来获取用户的登录名。比如AssertionHolder.getAssertion().getPrincipal().getName()
     */
    @Bean
    public FilterRegistrationBean assertionThreadLocalFilter() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AssertionThreadLocalFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        // 设定加载的顺序
        registration.setOrder(5);
        return registration;
    }

    /**
     * 用于检测用户是否需要进行身份验证 构建登录地址
     * ps:前后端分离项目可忽略此过滤器，自己构建访问认证地址
     */
    // @Bean
    // public FilterRegistrationBean AuthenticationFilter() {
    //     final FilterRegistrationBean registration = new FilterRegistrationBean();
    //     registration.setFilter(new AuthenticationFilter());
    //     // 设定匹配的路径
    //     registration.addUrlPatterns("/*");
    //     java.util.Map<String, String> initParameters = new HashMap<>();
    //     initParameters.put("casServerLoginUrl", casServerUrlPrefix + "/login");
    //     initParameters.put("serverName", serverName);
    //     initParameters.put("ignorePattern", "/hello"); // 放行路径
    //     registration.setInitParameters(initParameters);
    //     // 设定加载的顺序
    //     registration.setOrder(6);
    //     return registration;
    // }

}
