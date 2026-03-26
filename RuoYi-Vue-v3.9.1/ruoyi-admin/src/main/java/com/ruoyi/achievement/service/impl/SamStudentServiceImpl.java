package com.ruoyi.achievement.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.achievement.mapper.SamStudentMapper;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.service.ISamStudentService;
import javax.validation.Validator;

/**
 * 学生档案Service业务层处理
 * 
 * @author 王璨
 * @date 2026-02-03
 */
@Service
public class SamStudentServiceImpl implements ISamStudentService 
{
    private static final Logger log = LoggerFactory.getLogger(SamStudentServiceImpl.class);

    @Autowired
    private SamStudentMapper samStudentMapper;

    @Autowired
    private com.ruoyi.system.mapper.SysDeptMapper sysDeptMapper;

    @Autowired
    protected Validator validator;

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

    /**
     * 导入学生数据
     *
     * @param studentList 学生数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importStudent(List<SamStudent> studentList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(studentList) || studentList.size() == 0)
        {
            throw new ServiceException("导入学生数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        // 预先获取所有部门，方便查找
        com.ruoyi.common.core.domain.entity.SysDept queryDept = new com.ruoyi.common.core.domain.entity.SysDept();
        List<com.ruoyi.common.core.domain.entity.SysDept> allDepts = sysDeptMapper.selectDeptList(queryDept);

        for (SamStudent student : studentList)
        {
            try
            {
                // 根据名称查找学院、院系和专业ID
                if (StringUtils.isNotEmpty(student.getSchoolName())) {
                    for (com.ruoyi.common.core.domain.entity.SysDept d : allDepts) {
                        if (d.getDeptName().equals(student.getSchoolName())) {
                            student.setSchool(d.getDeptId().toString());
                            break;
                        }
                    }
                }
                if (StringUtils.isNotEmpty(student.getDepartmentName())) {
                    for (com.ruoyi.common.core.domain.entity.SysDept d : allDepts) {
                        if (d.getDeptName().equals(student.getDepartmentName())) {
                            student.setDepartment(d.getDeptId().toString());
                            break;
                        }
                    }
                }
                if (StringUtils.isNotEmpty(student.getMajorName())) {
                    for (com.ruoyi.common.core.domain.entity.SysDept d : allDepts) {
                        if (d.getDeptName().equals(student.getMajorName())) {
                            student.setMajor(d.getDeptId().toString());
                            break;
                        }
                    }
                }

                // 验证是否存在这个学生
                SamStudent s = samStudentMapper.selectSamStudentByNo(student.getNo());
                if (StringUtils.isNull(s))
                {
                    BeanValidators.validateWithException(validator, student);
                    student.setCreateBy(operName);
                    this.insertSamStudent(student);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、学号 " + student.getNo() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    BeanValidators.validateWithException(validator, student);
                    student.setUpdateBy(operName);
                    student.setStudentId(s.getStudentId());
                    this.updateSamStudent(student);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、学号 " + student.getNo() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、学号 " + student.getNo() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、学号 " + student.getNo() + " 导入失败：";
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
