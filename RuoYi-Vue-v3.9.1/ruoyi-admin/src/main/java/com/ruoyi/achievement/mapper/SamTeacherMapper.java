package com.ruoyi.achievement.mapper;

import java.util.List;
import com.ruoyi.achievement.domain.SamTeacher;

/**
 * 教师档案Mapper接口
 * 
 * @author 王璨
 * @date 2026-02-03
 */
public interface SamTeacherMapper 
{
    /**
     * 查询教师档案
     * 
     * @param id 教师档案主键
     * @return 教师档案
     */
    public SamTeacher selectSamTeacherById(Long id);

    /**
     * 查询教师档案列表
     * 
     * @param samTeacher 教师档案
     * @return 教师档案集合
     */
    public List<SamTeacher> selectSamTeacherList(SamTeacher samTeacher);

    /**
     * 新增教师档案
     * 
     * @param samTeacher 教师档案
     * @return 结果
     */
    public int insertSamTeacher(SamTeacher samTeacher);

    /**
     * 修改教师档案
     * 
     * @param samTeacher 教师档案
     * @return 结果
     */
    public int updateSamTeacher(SamTeacher samTeacher);

    /**
     * 删除教师档案
     * 
     * @param id 教师档案主键
     * @return 结果
     */
    public int deleteSamTeacherById(Long id);

    /**
     * 批量删除教师档案
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamTeacherByIds(Long[] ids);
}
