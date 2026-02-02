package com.ruoyi.session.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
}
