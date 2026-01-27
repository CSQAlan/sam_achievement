package com.ruoyi.erp.service;

import java.util.List;
import com.ruoyi.erp.domain.ErpStudent;

/**
 * 学生档案Service接口
 * 
 * @author 王璨
 * @date 2026-01-27
 */
public interface IErpStudentService 
{
    /**
     * 查询学生档案
     * 
     * @param id 学生档案主键
     * @return 学生档案
     */
    public ErpStudent selectErpStudentById(Long id);

    /**
     * 查询学生档案列表
     * 
     * @param erpStudent 学生档案
     * @return 学生档案集合
     */
    public List<ErpStudent> selectErpStudentList(ErpStudent erpStudent);

    /**
     * 新增学生档案
     * 
     * @param erpStudent 学生档案
     * @return 结果
     */
    public int insertErpStudent(ErpStudent erpStudent);

    /**
     * 修改学生档案
     * 
     * @param erpStudent 学生档案
     * @return 结果
     */
    public int updateErpStudent(ErpStudent erpStudent);

    /**
     * 批量删除学生档案
     * 
     * @param ids 需要删除的学生档案主键集合
     * @return 结果
     */
    public int deleteErpStudentByIds(Long[] ids);

    /**
     * 删除学生档案信息
     * 
     * @param id 学生档案主键
     * @return 结果
     */
    public int deleteErpStudentById(Long id);
}
