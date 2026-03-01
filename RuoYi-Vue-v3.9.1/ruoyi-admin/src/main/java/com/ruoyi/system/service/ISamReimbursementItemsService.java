package com.ruoyi.system.service;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.ruoyi.system.domain.SamReimbursementItems;

/**
 * 报销项目Service接口
 * 
 * @author ruoyi
 * @date 2026-02-07
 */
public interface ISamReimbursementItemsService 
{
    /**
     * 查询报销项目
     * 
     * @param id 报销项目主键
     * @return 报销项目
     */
    public SamReimbursementItems selectSamReimbursementItemsById(Long id);

    /**
     * 查询报销项目列表
     * 
     * @param samReimbursementItems 报销项目
     * @return 报销项目集合
     */
    public List<SamReimbursementItems> selectSamReimbursementItemsList(SamReimbursementItems samReimbursementItems);

    /**
     * 新增报销项目
     * 
     * @param samReimbursementItems 报销项目
     * @return 结果
     */
    public int insertSamReimbursementItems(SamReimbursementItems samReimbursementItems);

    /**
     * 修改报销项目
     * 
     * @param samReimbursementItems 报销项目
     * @return 结果
     */
    public int updateSamReimbursementItems(SamReimbursementItems samReimbursementItems);

    /**
     * 批量删除报销项目
     * 
     * @param ids 需要删除的报销项目主键集合
     * @return 结果
     */
    public int deleteSamReimbursementItemsByIds(Long[] ids);

    /**
     * 删除报销项目信息
     * 
     * @param id 报销项目主键
     * @return 结果
     */
    public int deleteSamReimbursementItemsById(Long id);

    /**
     * 导出报销项目PDF清单
     * 
     * @param id 报销项目主键
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    public void exportReimbursementPdf(Long id, HttpServletResponse response) throws IOException;
}
