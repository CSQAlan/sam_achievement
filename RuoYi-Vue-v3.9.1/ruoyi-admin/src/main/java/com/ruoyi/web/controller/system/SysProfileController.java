package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.achievement.service.ISamTeacherService;
import com.ruoyi.common.annotation.BizAudit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BizAuditOpType;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.service.ProfileCompletionService;

/**
 * 个人信息业务处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ProfileCompletionService profileCompletionService;

    @Autowired
    private ISamStudentService samStudentService;

    @Autowired
    private ISamTeacherService samTeacherService;

    public static class ProfileSaveRequest
    {
        private String nickName;
        private String phonenumber;
        private String email;
        private String sex;
        private String school;
        private String department;
        private String major;
        private String classYear;
        private String className;
        private String name;

        public String getNickName()
        {
            return nickName;
        }

        public void setNickName(String nickName)
        {
            this.nickName = nickName;
        }

        public String getPhonenumber()
        {
            return phonenumber;
        }

        public void setPhonenumber(String phonenumber)
        {
            this.phonenumber = phonenumber;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public String getSex()
        {
            return sex;
        }

        public void setSex(String sex)
        {
            this.sex = sex;
        }

        public String getSchool()
        {
            return school;
        }

        public void setSchool(String school)
        {
            this.school = school;
        }

        public String getDepartment()
        {
            return department;
        }

        public void setDepartment(String department)
        {
            this.department = department;
        }

        public String getMajor()
        {
            return major;
        }

        public void setMajor(String major)
        {
            this.major = major;
        }

        public String getClassYear()
        {
            return classYear;
        }

        public void setClassYear(String classYear)
        {
            this.classYear = classYear;
        }

        public String getClassName()
        {
            return className;
        }

        public void setClassName(String className)
        {
            this.className = className;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }

    /**
     * 个人信息
     */
    @GetMapping
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser();
        profileCompletionService.refreshProfileCompletion(loginUser);
        SysUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        return ajax;
    }

    /**
     * 个人中心部门树
     */
    @GetMapping("/deptTree")
    public AjaxResult deptTree()
    {
        return success(deptService.selectDeptTreeList(new SysDept()));
    }

    /**
     * 查询当前登录人的学生档案
     */
    @GetMapping("/studentDetail")
    public AjaxResult studentDetail()
    {
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (!hasRole(currentUser, "student"))
        {
            return success(null);
        }

        SamStudent query = new SamStudent();
        query.setNo(loginUser.getUsername());
        List<SamStudent> list = samStudentService.selectSamStudentList(query);
        SamStudent student = (list != null && !list.isEmpty()) ? list.get(0) : null;
        return success(student);
    }

    /**
     * 查询当前登录人的教师档案
     */
    @GetMapping("/teacherDetail")
    public AjaxResult teacherDetail()
    {
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (!hasRole(currentUser, "teacher"))
        {
            return success(null);
        }

        SamTeacher query = new SamTeacher();
        query.setNo(loginUser.getUsername());
        List<SamTeacher> list = samTeacherService.selectSamTeacherList(query);
        SamTeacher teacher = (list != null && !list.isEmpty()) ? list.get(0) : null;
        return success(teacher);
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @BizAudit(bizType = "profile", bizName = "修改个人资料", opType = BizAuditOpType.UPDATE, handler = "profileBizAuditHandler", async = false)
    @PutMapping
    public AjaxResult updateProfile(@RequestBody SysUser user)
    {
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();
        currentUser.setNickName(user.getNickName());
        currentUser.setWxNickName(user.getWxNickName());
        currentUser.setOpenid(user.getOpenid());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(currentUser) > 0)
        {
            profileCompletionService.refreshProfileCompletion(loginUser);
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * 原子保存个人中心完整资料（基础信息 + 学生/教师档案）
     */
    @Transactional(rollbackFor = Exception.class)
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @BizAudit(bizType = "profile", bizName = "保存个人中心资料", opType = BizAuditOpType.UPDATE, handler = "profileBizAuditHandler", async = false)
    @PutMapping("/saveAll")
    public AjaxResult saveAllProfile(@RequestBody ProfileSaveRequest request)
    {
        if (request == null)
        {
            return error("请求参数不能为空");
        }

        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();
        currentUser.setNickName(request.getNickName());
        currentUser.setEmail(request.getEmail());
        currentUser.setPhonenumber(request.getPhonenumber());
        currentUser.setSex(request.getSex());

        if (StringUtils.isNotEmpty(request.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(request.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(currentUser) <= 0)
        {
            return error("修改个人信息异常，请联系管理员");
        }

        String username = loginUser.getUsername();
        if (hasRole(currentUser, "student"))
        {
            SamStudent student = new SamStudent();
            student.setNo(username);
            student.setName(StringUtils.isNotEmpty(request.getName()) ? request.getName() : currentUser.getNickName());
            student.setSchool(request.getSchool());
            student.setDepartment(request.getDepartment());
            student.setMajor(request.getMajor());
            student.setClassYear(request.getClassYear());
            student.setClassName(request.getClassName());
            if (samStudentService.updateSamStudent(student) <= 0)
            {
                throw new ServiceException("保存学生档案失败");
            }
        }
        if (hasRole(currentUser, "teacher"))
        {
            SamTeacher teacher = new SamTeacher();
            teacher.setNo(username);
            teacher.setTeacherName(currentUser.getNickName());
            teacher.setSchool(request.getSchool());
            teacher.setDepartment(request.getDepartment());
            if (samTeacherService.updateSamTeacher(teacher) <= 0)
            {
                throw new ServiceException("保存教师档案失败");
            }
        }

        profileCompletionService.refreshProfileCompletion(loginUser);
        tokenService.setLoginUser(loginUser);
        return success(loginUser.getUser());
    }

    /**
     * 修改密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @BizAudit(bizType = "profile", bizName = "修改个人密码", opType = BizAuditOpType.UPDATE, handler = "profileBizAuditHandler", async = false)
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(@RequestBody Map<String, String> params)
    {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return error("新密码不能与旧密码相同");
        }
        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (userService.resetUserPwd(userId, newPassword) > 0)
        {
            loginUser.getUser().setPwdUpdateDate(DateUtils.getNowDate());
            loginUser.getUser().setPassword(newPassword);
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @BizAudit(bizType = "profile", bizName = "修改个人头像", opType = BizAuditOpType.UPDATE, handler = "profileBizAuditHandler", async = false)
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty())
        {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);
            if (userService.updateUserAvatar(loginUser.getUserId(), avatar))
            {
                String oldAvatar = loginUser.getUser().getAvatar();
                if (StringUtils.isNotEmpty(oldAvatar))
                {
                    FileUtils.deleteFile(RuoYiConfig.getProfile() + FileUtils.stripPrefix(oldAvatar));
                }
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", avatar);
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                return ajax;
            }
        }
        return error("上传图片异常，请联系管理员");
    }

    private boolean hasRole(SysUser user, String roleKey)
    {
        if (user == null || user.getRoles() == null || StringUtils.isEmpty(roleKey))
        {
            return false;
        }
        return user.getRoles().stream().anyMatch(role -> role != null && roleKey.equals(role.getRoleKey()));
    }
}

