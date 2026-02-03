package com.ruoyi.achievement.mapper;

import java.util.List;
import com.ruoyi.achievement.domain.SamStudent;

/**
 * 学生档案Mapper接口
 * 
 * @author 王璨
 * @date 2026-02-03
 */
public interface SamStudentMapper 
{
    /**
     * 查询学生档案
     * 
     * @param studentId 学生档案主键
     * @return 学生档案
     */
    public SamStudent selectSamStudentByStudentId(Long studentId);

    /**
     * 查询学生档案列表
     * 
     * @param samStudent 学生档案
     * @return 学生档案集合
     */
    public List<SamStudent> selectSamStudentList(SamStudent samStudent);

    /**
     * 新增学生档案
     * 
     * @param samStudent 学生档案
     * @return 结果
     */
    public int insertSamStudent(SamStudent samStudent);

    /**
     * 修改学生档案
     * 
     * @param samStudent 学生档案
     * @return 结果
     */
    public int updateSamStudent(SamStudent samStudent);

    /**
     * 删除学生档案
     * 
     * @param studentId 学生档案主键
     * @return 结果
     */
    public int deleteSamStudentByStudentId(Long studentId);

    /**
     * 批量删除学生档案
     * 
     * @param studentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSamStudentByStudentIds(Long[] studentIds);
}
