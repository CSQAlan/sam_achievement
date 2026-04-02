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
import com.ruoyi.common.utils.StringUtils;

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
        if (samStudent.getStudentId() == null && StringUtils.isNotBlank(samStudent.getNo()))
        {
            SamStudent query = new SamStudent();
            query.setNo(samStudent.getNo());
            List<SamStudent> exists = samStudentMapper.selectSamStudentList(query);
            if (exists != null && !exists.isEmpty())
            {
                samStudent.setStudentId(exists.get(0).getStudentId());
            }
            else
            {
                return samStudentMapper.insertSamStudent(samStudent);
            }
        }
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

        for (SamStudent student : studentList)
        {
            try
            {
                // 1. 验证学号是否为空
                if (StringUtils.isEmpty(student.getNo())) {
                    throw new Exception("学号不能为空");
                }

                // 2. 严谨的层级机构匹配逻辑 (根节点 ID 默认为 100)
                Long rootId = 100L;

                // 匹配学院
                com.ruoyi.common.core.domain.entity.SysDept schoolDept = sysDeptMapper.checkDeptNameUnique(student.getSchoolName(), rootId);
                if (schoolDept == null) {
                    throw new Exception("找不到名为 [" + student.getSchoolName() + "] 的学院，请检查名称是否正确");
                }
                student.setSchool(schoolDept.getDeptName());

                // 匹配院系 (在学院下找)
                com.ruoyi.common.core.domain.entity.SysDept deptDept = sysDeptMapper.checkDeptNameUnique(student.getDepartmentName(), schoolDept.getDeptId());
                if (deptDept == null) {
                    throw new Exception("在 " + student.getSchoolName() + " 下找不到名为 [" + student.getDepartmentName() + "] 的院系");
                }
                student.setDepartment(deptDept.getDeptName());

                // 匹配专业 (在院系下找)
                com.ruoyi.common.core.domain.entity.SysDept majorDept = sysDeptMapper.checkDeptNameUnique(student.getMajorName(), deptDept.getDeptId());
                if (majorDept == null) {
                    throw new Exception("在 " + student.getDepartmentName() + " 下找不到名为 [" + student.getMajorName() + "] 的专业");
                }
                student.setMajor(majorDept.getDeptName());

                // 3. 验证业务主键 (学号) 是否已存在
                SamStudent s = samStudentMapper.selectSamStudentByNo(student.getNo());
                if (StringUtils.isNull(s))
                {
                    BeanValidators.validateWithException(validator, student);
                    student.setCreateBy(operName);
                    this.insertSamStudent(student);
                    successNum++;
                }
                else if (isUpdateSupport)
                {
                    BeanValidators.validateWithException(validator, student);
                    student.setUpdateBy(operName);
                    student.setStudentId(s.getStudentId());
                    this.updateSamStudent(student);
                    successNum++;
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
                String msg = "<br/>" + failureNum + "、学号 " + (student.getNo() != null ? student.getNo() : "未知") + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入过程中出现错误！共失败 " + failureNum + " 条数据，成功 " + successNum + " 条，错误明细如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共计 " + successNum + " 条记录。");
        }
        return successMsg.toString();
    }
}
