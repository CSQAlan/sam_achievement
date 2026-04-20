package com.ruoyi.web.audit;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.achievement.service.ISamTeacherService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.bizaudit.BizAuditHandler;
import com.ruoyi.framework.bizaudit.model.BizAuditContext;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("profileBizAuditHandler")
public class ProfileBizAuditHandler implements BizAuditHandler
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISamStudentService samStudentService;

    @Autowired
    private ISamTeacherService samTeacherService;

    @Override
    public Object captureBefore(BizAuditContext context)
    {
        return buildSnapshot();
    }

    @Override
    public Object captureAfter(BizAuditContext context, Object methodResult)
    {
        return buildSnapshot();
    }

    @Override
    public String resolveBizId(BizAuditContext context, Object before, Object after)
    {
        LoginUser loginUser = getSafeLoginUser();
        return loginUser == null ? null : String.valueOf(loginUser.getUserId());
    }

    @Override
    public String resolveBizName(BizAuditContext context, Object before, Object after)
    {
        LoginUser loginUser = getSafeLoginUser();
        if (loginUser != null && StringUtils.isNotBlank(loginUser.getUsername()))
        {
            return loginUser.getUsername();
        }
        return context.getAnnotation().bizName();
    }

    @Override
    public String resolveBizKey(BizAuditContext context, Object before, Object after)
    {
        LoginUser loginUser = getSafeLoginUser();
        return loginUser == null ? null : loginUser.getUsername();
    }

    @Override
    public Map<String, String> fieldLabels(BizAuditContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user.nickName", "昵称");
        map.put("user.phonenumber", "手机号");
        map.put("user.email", "邮箱");
        map.put("user.sex", "性别");
        map.put("user.avatar", "头像");
        map.put("user.pwdUpdateDate", "密码更新时间");
        map.put("student.name", "学生姓名");
        map.put("student.schoolName", "学生学院");
        map.put("student.departmentName", "学生院系");
        map.put("student.majorName", "学生专业");
        map.put("student.classYear", "学生年级");
        map.put("student.className", "学生班级");
        map.put("teacher.teacherName", "教师姓名");
        map.put("teacher.schoolName", "教师学院");
        map.put("teacher.departmentName", "教师院系");
        return map;
    }

    @Override
    public List<String> fieldOrder(BizAuditContext context)
    {
        return Arrays.asList(
                "user.nickName", "user.phonenumber", "user.email", "user.sex", "user.avatar", "user.pwdUpdateDate",
                "student.name", "student.schoolName", "student.departmentName", "student.majorName", "student.classYear", "student.className",
                "teacher.teacherName", "teacher.schoolName", "teacher.departmentName"
        );
    }

    @Override
    public Set<String> sensitiveFields(BizAuditContext context)
    {
        return new HashSet<String>(Arrays.asList("user.password"));
    }

    @Override
    public Set<String> ignoredFields(BizAuditContext context)
    {
        return new HashSet<String>(Arrays.asList(
                "user.userId", "user.userName", "user.password", "user.delFlag",
                "user.loginIp", "user.loginDate", "user.createBy", "user.createTime",
                "user.updateBy", "user.updateTime", "user.remark", "user.admin",
                "user.roles", "user.roleIds", "user.postIds", "user.dept", "user.params",
                "student.studentId", "student.no", "student.school", "student.department", "student.major",
                "teacher.id", "teacher.no", "teacher.school", "teacher.department"
        ));
    }

    @Override
    public JSONObject buildExtra(BizAuditContext context, Object before, Object after)
    {
        JSONObject extra = new JSONObject();
        extra.put("methodName", context.getJoinPoint().getSignature().getName());
        return extra;
    }

    private JSONObject buildSnapshot()
    {
        LoginUser loginUser = getSafeLoginUser();
        if (loginUser == null)
        {
            return null;
        }

        SysUser user = userService.selectUserById(loginUser.getUserId());
        if (user == null)
        {
            return null;
        }

        JSONObject root = new JSONObject();
        root.put("user", user);

        SamStudent student = selectCurrentStudent(loginUser.getUsername());
        if (student != null)
        {
            root.put("student", student);
        }

        SamTeacher teacher = selectCurrentTeacher(loginUser.getUsername());
        if (teacher != null)
        {
            root.put("teacher", teacher);
        }
        return root;
    }

    private SamStudent selectCurrentStudent(String username)
    {
        if (StringUtils.isBlank(username))
        {
            return null;
        }
        SamStudent query = new SamStudent();
        query.setNo(username);
        List<SamStudent> list = samStudentService.selectSamStudentList(query);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    private SamTeacher selectCurrentTeacher(String username)
    {
        if (StringUtils.isBlank(username))
        {
            return null;
        }
        SamTeacher query = new SamTeacher();
        query.setNo(username);
        List<SamTeacher> list = samTeacherService.selectSamTeacherList(query);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    private LoginUser getSafeLoginUser()
    {
        try
        {
            return SecurityUtils.getLoginUser();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
