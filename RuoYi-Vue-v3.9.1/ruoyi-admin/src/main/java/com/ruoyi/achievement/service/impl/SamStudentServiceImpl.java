package com.ruoyi.achievement.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.achievement.mapper.SamStudentMapper;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.service.ISamStudentService;

/**
 * 学生档案Service业务层处理
 * 
 * @author 王璨
 * @date 2026-02-03
 */
@Service
public class SamStudentServiceImpl implements ISamStudentService 
{
    @Autowired
    private SamStudentMapper samStudentMapper;

    /**
     * 查询学生档案
     * 
     * @param studentId 学生档案主键
     * @return 学生档案
     */
    @Override
    public SamStudent selectSamStudentByStudentId(Long studentId)
    {
        return samStudentMapper.selectSamStudentByStudentId(studentId);
    }

    /**
     * 查询学生档案列表
     * 
     * @param samStudent 学生档案
     * @return 学生档案
     */
    @Override
    public List<SamStudent> selectSamStudentList(SamStudent samStudent)
    {
        return samStudentMapper.selectSamStudentList(samStudent);
    }

    /**
     * 新增学生档案
     * 
     * @param samStudent 学生档案
     * @return 结果
     */
    @Override
    public int insertSamStudent(SamStudent samStudent)
    {
        return samStudentMapper.insertSamStudent(samStudent);
    }

    /**
     * 修改学生档案
     * 
     * @param samStudent 学生档案
     * @return 结果
     */
    @Override
    public int updateSamStudent(SamStudent samStudent)
    {
        return samStudentMapper.updateSamStudent(samStudent);
    }

    /**
     * 批量删除学生档案
     * 
     * @param studentIds 需要删除的学生档案主键
     * @return 结果
     */
    @Override
    public int deleteSamStudentByStudentIds(Long[] studentIds)
    {
        return samStudentMapper.deleteSamStudentByStudentIds(studentIds);
    }

    /**
     * 删除学生档案信息
     * 
     * @param studentId 学生档案主键
     * @return 结果
     */
    @Override
    public int deleteSamStudentByStudentId(Long studentId)
    {
        return samStudentMapper.deleteSamStudentByStudentId(studentId);
    }
}
