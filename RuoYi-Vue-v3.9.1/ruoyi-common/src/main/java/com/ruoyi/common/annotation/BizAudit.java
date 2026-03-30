package com.ruoyi.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.ruoyi.common.enums.BizAuditOpType;

/**
 * 高级业务审计注解
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizAudit
{
    /**
     * 业务类型/模块编码
     */
    String bizType();

    /**
     * 业务名称
     */
    String bizName() default "";

    /**
     * 操作类型
     */
    BizAuditOpType opType() default BizAuditOpType.OTHER;

    /**
     * 业务处理器 Bean 名称
     */
    String handler() default "";

    /**
     * 是否记录请求参数
     */
    boolean saveRequest() default true;

    /**
     * 是否异步写审计日志
     */
    boolean async() default true;
}
