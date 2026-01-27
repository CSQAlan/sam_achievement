package com.ruoyi.erp.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.erp.mapper.ErpStudentMapper;
import com.ruoyi.erp.domain.ErpStudent;
import com.ruoyi.erp.service.IErpStudentService;

/**
 * 学生档案Service业务层处理
 * 
 * @author 王璨
 * @date 2026-01-27
 */
@Service
public class ErpStudentServiceImpl implements IErpStudentService 
{
    @Autowired
    private ErpStudentMapper erpStudentMapper;

    /**
     * 查询学生档案
     * 
     * @param id 学生档案主键
     * @return 学生档案
     */
    @Override
    public ErpStudent selectErpStudentById(Long id)
    {
        return erpStudentMapper.selectErpStudentById(id);
    }

    /**
     * 查询学生档案列表
     * 
     * @param erpStudent 学生档案
     * @return 学生档案
     */
    @Override
    public List<ErpStudent> selectErpStudentList(ErpStudent erpStudent)
    {
        return erpStudentMapper.selectErpStudentList(erpStudent);
    }

    /**
     * 新增学生档案
     * 
     * @param erpStudent 学生档案
     * @return 结果
     */
    @Override
    public int insertErpStudent(ErpStudent erpStudent)
    {
        erpStudent.setCreateTime(DateUtils.getNowDate());
        return erpStudentMapper.insertErpStudent(erpStudent);
    }

    /**
     * 修改学生档案
     * 
     * @param erpStudent 学生档案
     * @return 结果
     */
    @Override
    public int updateErpStudent(ErpStudent erpStudent)
    {
        erpStudent.setUpdateTime(DateUtils.getNowDate());
        return erpStudentMapper.updateErpStudent(erpStudent);
    }

    /**
     * 批量删除学生档案
     * 
     * @param ids 需要删除的学生档案主键
     * @return 结果
     */
    @Override
    public int deleteErpStudentByIds(Long[] ids)
    {
        return erpStudentMapper.deleteErpStudentByIds(ids);
    }

    /**
     * 删除学生档案信息
     * 
     * @param id 学生档案主键
     * @return 结果
     */
    @Override
    public int deleteErpStudentById(Long id)
    {
        return erpStudentMapper.deleteErpStudentById(id);
    }
}
