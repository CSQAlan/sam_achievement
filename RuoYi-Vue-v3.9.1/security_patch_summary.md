# RuoYi-Vue 安全漏洞修复整理报告

本报告整理了针对 RuoYi-Vue-v3.9.1 项目中修复的所有 8 项安全漏洞。包含漏洞详情、影响文件及修改前后的代码对比。

---

## 目录
1. [FastJSON2 AutoType 反序列化漏洞](#1-fastjson2-autotype-反序列化漏洞)
2. [GenController 新建表结构 SQL 注入漏洞](#2-gencontroller-新建表结构-sql-注入漏洞)
3. [Quartz 任务反射调用 RCE 漏洞](#3-quartz-任务反射调用-rce-漏洞)
4. [全局异常处理敏感信息泄露漏洞](#4-全局异常处理敏感信息泄露漏洞)
5. [Druid Wall 过滤器多语句执行漏洞](#5-druid-wall-过滤器多语句执行漏洞)
6. [BizAuditLogMapper SQL 注入漏洞](#6-bizauditlogmapper-sql-注入漏洞)
7. [ImageUtils SSRF（服务端请求伪造）漏洞](#7-imageutils-ssrf服务端请求伪造漏洞)
8. [Jackson 非标准 JSON 解析安全风险](#8-jackson-非标准-json-解析安全风险)

---

### 1. FastJSON2 AutoType 反序列化漏洞
*   **漏洞说明**：FastJSON2 开启 AutoType 会反序列化任意指定类型的对象，可能导致远程代码执行（RCE）。若直接启用 `safeMode` 会导致 Redis 缓存多态对象（如 `LoginUser`）反序列化时发生 `ClassCastException` 异常。
*   **修复方案**：将 Redis 缓存的反序列化白名单由宽泛的 `"com.ruoyi"` 命名空间缩小至仅允许被缓存的 5 个具体实体类。
*   **修改文件**：`ruoyi-common/src/main/java/com/ruoyi/common/constant/Constants.java`
*   **代码修改对比**：
```diff
-    public static final String[] JSON_WHITELIST_STR = { "com.ruoyi" };
+    public static final String[] JSON_WHITELIST_STR = {
+            "com.ruoyi.common.core.domain.model.LoginUser",
+            "com.ruoyi.common.core.domain.entity.SysUser",
+            "com.ruoyi.common.core.domain.entity.SysRole",
+            "com.ruoyi.common.core.domain.entity.SysDept",
+            "com.ruoyi.common.core.domain.entity.SysDictData"
+    };
```

---

### 2. GenController 新建表结构 SQL 注入漏洞
*   **漏洞说明**：生成代码模块中的新建表 `/createTable` 接口，允许执行未经完全过滤的 DDL SQL，容易引发 SQL 注入和数据库越权破坏。
*   **修复方案**：在此接口的首行增加超级管理员校验，只有 `userId = 1` 的超级管理员账号有权使用此功能，普通管理员或其他被盗账号被严格拦截。
*   **修改文件**：`ruoyi-generator/src/main/java/com/ruoyi/generator/controller/GenController.java`
*   **代码修改对比**：
```diff
     @PreAuthorize("@ss.hasRole('admin')")
     @Log(title = "创建表", businessType = BusinessType.OTHER)
     @PostMapping("/createTable")
     public AjaxResult createTableSave(String sql)
     {
+        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId()))
+        {
+            return AjaxResult.error("创建表结构失败，仅允许超级管理员操作");
+        }
         try
         {
             SqlUtil.filterKeyword(sql);
```

---

### 3. Quartz 任务反射调用 RCE 漏洞
*   **漏洞说明**：定时任务调用可通过反射执行任意类的任意方法，如果攻击者具备数据库修改权限或通过其他漏洞篡改了任务配置，可以实现远程代码执行。
*   **修复方案**：在 Quartz 执行反射调用 `JobInvokeUtil.invokeMethod` 之前，强制对目标方法进行白名单校验，只有 `ScheduleUtils.whiteList(invokeTarget)` 通过的任务才被允许执行。
*   **修改文件**：`ruoyi-quartz/src/main/java/com/ruoyi/quartz/util/JobInvokeUtil.java`
*   **代码修改对比**：
```diff
     public static void invokeMethod(SysJob sysJob) throws Exception
     {
         String invokeTarget = sysJob.getInvokeTarget();
+        if (!ScheduleUtils.whiteList(invokeTarget))
+        {
+            throw new Exception("目标字符串不在白名单内，不允许执行！");
+        }
         String beanName = getBeanName(invokeTarget);
```

---

### 4. 全局异常处理敏感信息泄露漏洞
*   **漏洞说明**：当后端发生 SQL 异常、连接错误或内部运行错误时，异常详细信息会直接暴露在响应体中返回给前端展示，泄露了后端表结构、字段名甚至服务器环境信息。
*   **修复方案**：在全局异常拦截器中，对未知的运行时异常和系统级异常统一替换为模糊的友好提示，真实异常信息保留在服务器日志中。
*   **修改文件**：`ruoyi-framework/src/main/java/com/ruoyi/framework/web/exception/GlobalExceptionHandler.java`
*   **代码修改对比**：
```diff
     /**
      * 拦截未知的运行时异常
      */
     @ExceptionHandler(RuntimeException.class)
     public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request)
     {
         String requestURI = request.getRequestURI();
         log.error("请求地址'{}',发生未知异常.", requestURI, e);
-        return AjaxResult.error(e.getMessage());
+        return AjaxResult.error("系统发生未知异常，请联系管理员");
     }
 
     /**
      * 系统异常
      */
     @ExceptionHandler(Exception.class)
     public AjaxResult handleException(Exception e, HttpServletRequest request)
     {
         String requestURI = request.getRequestURI();
         log.error("请求地址'{}',发生系统异常.", requestURI, e);
-        return AjaxResult.error(e.getMessage());
+        return AjaxResult.error("系统内部异常，请联系管理员");
     }
```

---

### 5. Druid Wall 过滤器多语句执行漏洞
*   **漏洞说明**：默认配置的 Druid 允许在单条查询中通过分号执行多条语句（`multi-statement-allow: true`），放大了潜在 SQL 注入漏洞的破坏能力。
*   **修复方案**：将 `multi-statement-allow` 显式设为 `false`，禁止单次调用执行多个以分号分隔的 SQL 语句。
*   **修改文件**：`ruoyi-admin/src/main/resources/application-druid.yml`
*   **代码修改对比**：
```diff
                 wall:
                     config:
-                        multi-statement-allow: true
+                        multi-statement-allow: false
```

---

### 6. BizAuditLogMapper SQL 注入漏洞
*   **漏洞说明**：清理审计日志的 SQL 映射中，使用了字符串插值语法 `${days}`，这会导致参数未被编译预处理而直接嵌入 SQL，存在注入隐患。
*   **修复方案**：将 `${days}` 替换为标准的预编译占位符 `#{days}`。
*   **修改文件**：`ruoyi-admin/src/main/resources/mapper/reimbursement/BizAuditLogMapper.xml`
*   **代码修改对比**：
```diff
     <delete id="cleanBizAuditLogBeforeDays" parameterType="Integer">
         delete from biz_audit_log
-        where create_time &lt; DATE_SUB(now(), interval ${days} day)
+        where create_time &lt; DATE_SUB(now(), interval #{days} day)
     </delete>
 
     <delete id="cleanBizAuditLogDetailBeforeDays" parameterType="Integer">
         delete d from biz_audit_log_detail d
         inner join biz_audit_log l on l.id = d.log_id
-        where l.create_time &lt; DATE_SUB(now(), interval ${days} day)
+        where l.create_time &lt; DATE_SUB(now(), interval #{days} day)
     </delete>
```

---

### 7. ImageUtils SSRF（服务端请求伪造）漏洞
*   **漏洞说明**：`ImageUtils.readFile` 方法在下载远程图片（以 `http` 开头的 URL）时，直接建立网络连接，攻击者可以通过特定的 URL（如 `http://127.0.0.1:8080/` 或元数据服务器地址）探测内网开放端口和服务。
*   **修复方案**：在网络下载前引入 `isSafeUrl` 过滤函数，验证协议仅限 HTTP(S)，解析域名，并阻止包含回环地址、多播地址、链路本地地址、私有 IP 范围（RFC 1918、CGNAT、保留地址）等所有非公网 IP 的请求。
*   **修改文件**：`ruoyi-common/src/main/java/com/ruoyi/common/utils/file/ImageUtils.java`
*   **代码修改对比**：
```diff
     public static byte[] readFile(String url)
     {
         InputStream in = null;
         try
         {
             if (url.startsWith("http"))
             {
+                // SSRF 验证
+                if (!isSafeUrl(url))
+                {
+                    log.warn("URL has SSRF risk, block it: {}", url);
+                    return null;
+                }
                 // 网络地址
                 URL urlObj = new URL(url);
                 URLConnection urlConnection = urlObj.openConnection();
```
*(新增的安全拦截方法源码)*：
```java
    /**
     * 验证URL是否安全以防范SSRF攻击
     */
    private static boolean isSafeUrl(String url)
    {
        try
        {
            if (StringUtils.isEmpty(url))
            {
                return false;
            }
            URL urlObj = new URL(url);
            String protocol = urlObj.getProtocol().toLowerCase();
            if (!"http".equals(protocol) && !"https".equals(protocol))
            {
                return false;
            }
            String host = urlObj.getHost();
            if (StringUtils.isEmpty(host))
            {
                return false;
            }
            java.net.InetAddress[] addresses = java.net.InetAddress.getAllByName(host);
            for (java.net.InetAddress address : addresses)
            {
                if (address.isLoopbackAddress() 
                    || address.isAnyLocalAddress() 
                    || address.isLinkLocalAddress() 
                    || address.isSiteLocalAddress()
                    || address.isMulticastAddress())
                {
                    return false;
                }
                
                // 校验自定义IPv4私有和保留地址范围
                byte[] ipBytes = address.getAddress();
                if (ipBytes.length == 4)
                {
                    int firstOctet = ipBytes[0] & 0xFF;
                    int secondOctet = ipBytes[1] & 0xFF;
                    int thirdOctet = ipBytes[2] & 0xFF;
                    
                    // CGNAT: 100.64.0.0/10
                    if (firstOctet == 100 && (secondOctet >= 64 && secondOctet <= 127))
                    {
                        return false;
                    }
                    // Benchmarking: 198.18.0.0/15
                    if (firstOctet == 198 && (secondOctet == 18 || secondOctet == 19))
                    {
                        return false;
                    }
                    // Documentation / Test-Net: 192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24
                    if (firstOctet == 192 && secondOctet == 0 && thirdOctet == 2)
                    {
                        return false;
                    }
                    if (firstOctet == 198 && secondOctet == 51 && thirdOctet == 100)
                    {
                        return false;
                    }
                    if (firstOctet == 203 && secondOctet == 0 && thirdOctet == 113)
                    {
                        return false;
                    }
                    // Reserved (Class E): 240.0.0.0/4
                    if (firstOctet >= 240)
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
```

---

### 8. Jackson 非标准 JSON 解析安全风险
*   **漏洞说明**：`application.yml` 中默认开启了 Jackson 解析非标准 JSON 的特性（例如允许单引号、允许无键名引号），增大了绕过 WAF 等网络防火墙的几率。
*   **修复方案**：关闭非标准特性的解析，强制解析器严格按照官方的标准 JSON 格式进行反序列化。
*   **修改文件**：`ruoyi-admin/src/main/resources/application.yml`
*   **代码修改对比**：
```diff
     # 添加以下配置，使反序列化更灵活
     parser:
-       allow_single_quotes: true
-       allow_unquoted_field_names: true
+       allow_single_quotes: false
+       allow_unquoted_field_names: false
```
