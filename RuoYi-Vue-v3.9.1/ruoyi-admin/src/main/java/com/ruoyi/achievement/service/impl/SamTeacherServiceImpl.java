package com.ruoyi.achievement.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.achievement.mapper.SamTeacherMapper;
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.achievement.service.ISamTeacherService;

/**
 * 教师档案Service业务层处理
 * 
 * @author 王璨
 * @date 2026-02-03
 */
@Service
public class SamTeacherServiceImpl implements ISamTeacherService 
{
    @Autowired
    private SamTeacherMapper samTeacherMapper;

    /**
     * 查询教师档案
     * 
     * @param id 教师档案主键
     * @return 教师档案
     */
    @Override
    public SamTeacher selectSamTeacherById(Long id)
    {
        return samTeacherMapper.selectSamTeacherById(id);
    }

    /**
     * 查询教师档案列表
     * 
     * @param samTeacher 教师档案
     * @return 教师档案
     */
    @Override
    public List<SamTeacher> selectSamTeacherList(SamTeacher samTeacher)
    {
        return samTeacherMapper.selectSamTeacherList(samTeacher);
    }

    /**
     * 新增教师档案
     * 
     * @param samTeacher 教师档案
     * @return 结果
     */
    @Override
    public int insertSamTeacher(SamTeacher samTeacher)
    {
        return samTeacherMapper.insertSamTeacher(samTeacher);
    }

    /**
     * 修改教师档案
     * 
     * @param samTeacher 教师档案
     * @return 结果
     */
    @Override
    public int updateSamTeacher(SamTeacher samTeacher)
    {
        return samTeacherMapper.updateSamTeacher(samTeacher);
    }

    /**
     * 批量删除教师档案
     * 
     * @param ids 需要删除的教师档案主键
     * @return 结果
     */
    @Override
    public int deleteSamTeacherByIds(Long[] ids)
    {
        return samTeacherMapper.deleteSamTeacherByIds(ids);
    }

    /**
     * 删除教师档案信息
     * 
     * @param id 教师档案主键
     * @return 结果
     */
    @Override
    public int deleteSamTeacherById(Long id)
    {
        return samTeacherMapper.deleteSamTeacherById(id);
    }
}
