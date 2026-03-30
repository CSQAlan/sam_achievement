package com.ruoyi.achievement.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.achievement.mapper.SamTeacherMapper;
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.achievement.service.ISamTeacherService;
import com.ruoyi.common.utils.StringUtils;
import javax.validation.Validator;
import com.ruoyi.common.utils.StringUtils;

/**
 * 教师档案Service业务层处理
 * 
 * @author 王璨
 * @date 2026-02-03
 */
@Service
public class SamTeacherServiceImpl implements ISamTeacherService 
{
    private static final Logger log = LoggerFactory.getLogger(SamTeacherServiceImpl.class);

    @Autowired
    private SamTeacherMapper samTeacherMapper;

    @Autowired
    private com.ruoyi.system.mapper.SysDeptMapper sysDeptMapper;

    @Autowired
    protected Validator validator;

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
        if (samTeacher.getId() == null && StringUtils.isNotBlank(samTeacher.getNo()))
        {
            SamTeacher query = new SamTeacher();
            query.setNo(samTeacher.getNo());
            List<SamTeacher> exists = samTeacherMapper.selectSamTeacherList(query);
            if (exists != null && !exists.isEmpty())
            {
                samTeacher.setId(exists.get(0).getId());
            }
            else
            {
                return samTeacherMapper.insertSamTeacher(samTeacher);
            }
        }
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

    /**
     * 导入教师数据
     *
     * @param teacherList 教师数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importTeacher(List<SamTeacher> teacherList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(teacherList) || teacherList.size() == 0)
        {
            throw new ServiceException("导入教师数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        // 预先获取所有部门，方便查找
        com.ruoyi.common.core.domain.entity.SysDept queryDept = new com.ruoyi.common.core.domain.entity.SysDept();
        List<com.ruoyi.common.core.domain.entity.SysDept> allDepts = sysDeptMapper.selectDeptList(queryDept);

        for (SamTeacher teacher : teacherList)
        {
            try
            {
                // 根据名称查找学院和院系ID
                if (StringUtils.isNotEmpty(teacher.getSchoolName())) {
                    for (com.ruoyi.common.core.domain.entity.SysDept d : allDepts) {
                        if (d.getDeptName().equals(teacher.getSchoolName())) {
                            teacher.setSchool(d.getDeptId().toString());
                            break;
                        }
                    }
                }
                if (StringUtils.isNotEmpty(teacher.getDepartmentName())) {
                    for (com.ruoyi.common.core.domain.entity.SysDept d : allDepts) {
                        if (d.getDeptName().equals(teacher.getDepartmentName())) {
                            teacher.setDepartment(d.getDeptId().toString());
                            break;
                        }
                    }
                }

                // 验证是否存在这个教师
                SamTeacher t = samTeacherMapper.selectSamTeacherByNo(teacher.getNo());
                if (StringUtils.isNull(t))
                {
                    BeanValidators.validateWithException(validator, teacher);
                    teacher.setCreateBy(operName);
                    this.insertSamTeacher(teacher);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、工号 " + teacher.getNo() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    BeanValidators.validateWithException(validator, teacher);
                    teacher.setUpdateBy(operName);
                    teacher.setId(t.getId());
                    this.updateSamTeacher(teacher);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、工号 " + teacher.getNo() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、工号 " + teacher.getNo() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、工号 " + teacher.getNo() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
