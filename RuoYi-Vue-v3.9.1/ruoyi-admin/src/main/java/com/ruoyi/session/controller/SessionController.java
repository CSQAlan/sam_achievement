package com.ruoyi.session.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.session.domain.Session;
import com.ruoyi.session.service.ISessionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 赛事届次Controller
 *
 * @author ruoyi
 * @date 2026-02-01
 */
@RestController
@RequestMapping("/session/session")
public class SessionController extends BaseController
{
    // 新增：日志对象，方便排查导入异常
    private static final Logger log = LoggerFactory.getLogger(SessionController.class);

    @Autowired
    private ISessionService sessionService;

    /**
     * 查询赛事届次列表
     */
    @PreAuthorize("@ss.hasPermi('session:session:list')")
    @GetMapping("/list")
    public TableDataInfo list(Session session)
    {
        startPage();
        List<Session> list = sessionService.selectSessionList(session);
        return getDataTable(list);
    }

    /**
     * 导出赛事届次列表
     */
    @PreAuthorize("@ss.hasPermi('session:session:export')")
    @Log(title = "赛事届次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Session session)
    {
        List<Session> list = sessionService.selectSessionList(session);
        ExcelUtil<Session> util = new ExcelUtil<Session>(Session.class);
        util.exportExcel(response, list, "赛事届次数据");
    }

    /**
     * 获取赛事届次详细信息
     */
    @PreAuthorize("@ss.hasPermi('session:session:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sessionService.selectSessionById(id));
    }

    /**
     * 新增赛事届次
     */
    @PreAuthorize("@ss.hasPermi('session:session:add')")
    @Log(title = "赛事届次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Session session)
    {
        return toAjax(sessionService.insertSession(session));
    }

    /**
     * 修改赛事届次
     */
    @PreAuthorize("@ss.hasPermi('session:session:edit')")
    @Log(title = "赛事届次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Session session)
    {
        return toAjax(sessionService.updateSession(session));
    }

    /**
     * 删除赛事届次
     */
    @PreAuthorize("@ss.hasPermi('session:session:remove')")
    @Log(title = "赛事届次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sessionService.deleteSessionByIds(ids));
    }

    /**
     * 导出赛事届次模板（不含状态列，依托实体@Excel注解自动生成表头）
     */
    @PreAuthorize("@ss.hasPermi('session:session:exportTemplate')")
    @Log(title = "赛事届次模板", businessType = BusinessType.EXPORT)
    @PostMapping("/exportTemplate")
    public void exportTemplate(HttpServletResponse response) {
        ExcelUtil<Session> util = new ExcelUtil<Session>(Session.class);
        // 传空列表仅导出表头，实体中status字段无@Excel注解，模板自动不含该列
        util.exportExcel(response, new ArrayList<>(), "赛事届次导入模板");
    }

    /**
     * 批量导入赛事届次（默认状态为启用）
     * 优化：空文件校验+指定表头行号+全局异常捕获
     */
    @PreAuthorize("@ss.hasPermi('session:session:import')")
    @Log(title = "赛事届次", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) {
        // 1. 空文件校验：避免上传空文件导致解析异常
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("导入失败：请选择有效的Excel文件，文件不能为空！");
        }
        try {
            ExcelUtil<Session> util = new ExcelUtil<Session>(Session.class);
            // 2. 解析Excel：指定表头在第0行（Excel实际第1行），数据从第1行开始，和Service层行号提示一致
            List<Session> sessionList = util.importExcel(file.getInputStream(), 0);
            // 3. 调用Service处理导入逻辑
            String message = sessionService.importSession(sessionList, updateSupport);
            return AjaxResult.success(message);
        } catch (Exception e) {
            // 4. 全局异常捕获：统一处理所有导入异常，打印日志+返回友好提示
            log.error("批量导入赛事届次失败", e);
            return AjaxResult.error("导入失败：" + e.getMessage());
        }
    }
}