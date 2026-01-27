package com.ruoyi.erp.mapper;

import java.util.List;
import com.ruoyi.erp.domain.ErpTeacher;

/**
 * 教师档案Mapper接口
 * 
 * @author 王璨
 * @date 2026-01-27
 */
public interface ErpTeacherMapper 
{
    /**
     * 查询教师档案
     * 
     * @param id 教师档案主键
     * @return 教师档案
     */
    public ErpTeacher selectErpTeacherById(Long id);

    /**
     * 查询教师档案列表
     * 
     * @param erpTeacher 教师档案
     * @return 教师档案集合
     */
    public List<ErpTeacher> selectErpTeacherList(ErpTeacher erpTeacher);

    /**
     * 新增教师档案
     * 
     * @param erpTeacher 教师档案
     * @return 结果
     */
    public int insertErpTeacher(ErpTeacher erpTeacher);

    /**
     * 修改教师档案
     * 
     * @param erpTeacher 教师档案
     * @return 结果
     */
    public int updateErpTeacher(ErpTeacher erpTeacher);

    /**
     * 删除教师档案
     * 
     * @param id 教师档案主键
     * @return 结果
     */
    public int deleteErpTeacherById(Long id);

    /**
     * 批量删除教师档案
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErpTeacherByIds(Long[] ids);
}
