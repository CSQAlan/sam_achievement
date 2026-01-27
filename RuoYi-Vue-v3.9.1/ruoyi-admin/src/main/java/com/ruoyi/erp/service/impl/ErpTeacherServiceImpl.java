package com.ruoyi.erp.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.erp.mapper.ErpTeacherMapper;
import com.ruoyi.erp.domain.ErpTeacher;
import com.ruoyi.erp.service.IErpTeacherService;

/**
 * 教师档案Service业务层处理
 * 
 * @author 王璨
 * @date 2026-01-27
 */
@Service
public class ErpTeacherServiceImpl implements IErpTeacherService 
{
    @Autowired
    private ErpTeacherMapper erpTeacherMapper;

    /**
     * 查询教师档案
     * 
     * @param id 教师档案主键
     * @return 教师档案
     */
    @Override
    public ErpTeacher selectErpTeacherById(Long id)
    {
        return erpTeacherMapper.selectErpTeacherById(id);
    }

    /**
     * 查询教师档案列表
     * 
     * @param erpTeacher 教师档案
     * @return 教师档案
     */
    @Override
    public List<ErpTeacher> selectErpTeacherList(ErpTeacher erpTeacher)
    {
        return erpTeacherMapper.selectErpTeacherList(erpTeacher);
    }

    /**
     * 新增教师档案
     * 
     * @param erpTeacher 教师档案
     * @return 结果
     */
    @Override
    public int insertErpTeacher(ErpTeacher erpTeacher)
    {
        erpTeacher.setCreateTime(DateUtils.getNowDate());
        return erpTeacherMapper.insertErpTeacher(erpTeacher);
    }

    /**
     * 修改教师档案
     * 
     * @param erpTeacher 教师档案
     * @return 结果
     */
    @Override
    public int updateErpTeacher(ErpTeacher erpTeacher)
    {
        erpTeacher.setUpdateTime(DateUtils.getNowDate());
        return erpTeacherMapper.updateErpTeacher(erpTeacher);
    }

    /**
     * 批量删除教师档案
     * 
     * @param ids 需要删除的教师档案主键
     * @return 结果
     */
    @Override
    public int deleteErpTeacherByIds(Long[] ids)
    {
        return erpTeacherMapper.deleteErpTeacherByIds(ids);
    }

    /**
     * 删除教师档案信息
     * 
     * @param id 教师档案主键
     * @return 结果
     */
    @Override
    public int deleteErpTeacherById(Long id)
    {
        return erpTeacherMapper.deleteErpTeacherById(id);
    }
}
