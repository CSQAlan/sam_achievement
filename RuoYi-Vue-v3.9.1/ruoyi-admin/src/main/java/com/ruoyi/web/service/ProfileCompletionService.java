package com.ruoyi.web.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.achievement.service.ISamTeacherService;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;

/**
 * Recomputes profile completeness from persisted data instead of trusting the client flag.
 */
@Service
public class ProfileCompletionService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISamStudentService samStudentService;

    @Autowired
    private ISamTeacherService samTeacherService;

    public int refreshProfileCompletion(LoginUser loginUser)
    {
        if (loginUser == null || StringUtils.isEmpty(loginUser.getUsername()))
        {
            return 0;
        }

        SysUser currentUser = userService.selectUserByUserName(loginUser.getUsername());
        if (currentUser == null)
        {
            return 0;
        }

        int profileInitialized = isProfileCompleted(currentUser, loginUser.getUser() != null ? loginUser.getUser().getRoles() : null) ? 1 : 0;
        SysUser updateUser = new SysUser();
        updateUser.setUserId(currentUser.getUserId());
        updateUser.setProfileInitialized(profileInitialized);
        userService.updateUserProfile(updateUser);

        if (loginUser.getUser() != null)
        {
            loginUser.getUser().setProfileInitialized(profileInitialized);
        }
        return profileInitialized;
    }

    private boolean isProfileCompleted(SysUser user, List<SysRole> roles)
    {
        if (!hasBaseProfile(user))
        {
            return false;
        }
        if (hasRole(roles, "student"))
        {
            return hasStudentProfile(user.getUserName());
        }
        if (hasRole(roles, "teacher"))
        {
            return hasTeacherProfile(user.getUserName());
        }
        return true;
    }

    private boolean hasBaseProfile(SysUser user)
    {
        return StringUtils.isNotBlank(user.getNickName())
                && StringUtils.isNotBlank(user.getPhonenumber())
                && StringUtils.isNotBlank(user.getEmail());
    }

    private boolean hasStudentProfile(String studentNo)
    {
        SamStudent student = getStudentByNo(studentNo);
        return student != null
                && StringUtils.isNotBlank(student.getSchool())
                && StringUtils.isNotBlank(student.getDepartment())
                && StringUtils.isNotBlank(student.getMajor())
                && StringUtils.isNotBlank(student.getClassYear())
                && StringUtils.isNotBlank(student.getClassName());
    }

    private boolean hasTeacherProfile(String teacherNo)
    {
        SamTeacher teacher = getTeacherByNo(teacherNo);
        return teacher != null
                && StringUtils.isNotBlank(teacher.getSchool())
                && StringUtils.isNotBlank(teacher.getDepartment());
    }

    private SamStudent getStudentByNo(String studentNo)
    {
        if (StringUtils.isBlank(studentNo))
        {
            return null;
        }
        SamStudent query = new SamStudent();
        query.setNo(studentNo);
        List<SamStudent> students = samStudentService.selectSamStudentList(query);
        return StringUtils.isNotEmpty(students) ? students.get(0) : null;
    }

    private SamTeacher getTeacherByNo(String teacherNo)
    {
        if (StringUtils.isBlank(teacherNo))
        {
            return null;
        }
        SamTeacher query = new SamTeacher();
        query.setNo(teacherNo);
        List<SamTeacher> teachers = samTeacherService.selectSamTeacherList(query);
        return StringUtils.isNotEmpty(teachers) ? teachers.get(0) : null;
    }

    private boolean hasRole(List<SysRole> roles, String roleKey)
    {
        if (StringUtils.isEmpty(roles) || StringUtils.isBlank(roleKey))
        {
            return false;
        }
        return roles.stream().anyMatch(role -> role != null && roleKey.equals(role.getRoleKey()));
    }
}
