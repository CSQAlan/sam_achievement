package com.ruoyi.demo.service.impl;


import com.ruoyi.achievement.domain.SamStudent;
import com.ruoyi.achievement.domain.SamTeacher;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.demo.service.ICasUserProvisioningService;
import com.ruoyi.demo.util.CasAttrDecodeUtil;
import com.ruoyi.achievement.service.ISamStudentService;
import com.ruoyi.achievement.service.ISamTeacherService;

import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CasUserProvisioningService implements ICasUserProvisioningService {

    @Autowired
    private ISysUserService userService;

    // 你可能还需要：角色/部门服务（看你库结构）
    @Autowired
    private ISysRoleService roleService;

    //学生表注册插入
    @Autowired
    ISamStudentService studentService;
    //老师表注册插入
    @Autowired
    ISamTeacherService teacherService;
    public SysUser provisionOrUpdate(String loginName, Map<String, Object> attributes) {

        SysUser exist = userService.selectUserByUserName(loginName);
        if (exist != null) {
            // 可选：同步更新邮箱/手机号/姓名
            return exist;
        }

        // 1) 解码关键字段
        //学号或者工号
        String acpuid = CasAttrDecodeUtil.smartDecode(attributes.get("ACPUID"));
        //名字
        String name   = CasAttrDecodeUtil.smartDecode(attributes.get("ACPNAME"));
        //学校邮箱
        String email  = CasAttrDecodeUtil.smartDecode(attributes.get("ACPSCHOOLEMAIL"));
        //手机号
        String mobile = CasAttrDecodeUtil.smartDecode(attributes.get("ACPMOBILE"));

        // 身份判断：优先 ACPUID，其次 loginName
        String id = (acpuid != null && !acpuid.trim().isEmpty()) ? acpuid : loginName;

        Long roleId;
        if (id.length() == 8) {
            roleId = 103L; // 老师
        } else if (id.length() == 13) {
            roleId = 3L;   // 学生

        } else {
            // 不符合规则：给一个最小权限角色，或者直接拒绝
            // roleId = 3L;
            throw new RuntimeException("无法识别身份，ACPUID/loginName=" + id);
        }

        // 2) 组装新用户（字段按你的库来）
        SysUser user = new SysUser();
        user.setUserName(loginName);     // 本地登录名
        user.setNickName(name != null ? name : loginName); // 昵称/姓名
        user.setEmail(email);
        user.setPhonenumber(mobile);

        // 3) 必填字段兜底（若依通常要求）
        user.setStatus("0");  // 0 正常（按你库）
        user.setDelFlag("0"); // 未删除
        // user.setDeptId(默认部门id); // 必填的话要设
        // user.setSex("0"); // 可选

        // 4) 密码：CAS 登录不需要本地密码，但数据库可能非空
        // 建议生成随机密码并加密存库（防止空密码）
        user.setPassword(SecurityUtils.encryptPassword(IdUtils.fastUUID()));

        // 5) 插入用户
        userService.insertUser(user);

        // 6) 赋默认角色（非常重要，否则进系统没菜单）
        // 例如默认给“学生角色” roleId=xxx（你需要按你系统实际配置）
        userService.insertUserAuth(user.getUserId(), new Long[]{ roleId });

        // Insert into business tables based on role if not already exists
        if (roleId == 103L) { // Teacher
           SamTeacher query = new com.ruoyi.achievement.domain.SamTeacher();
            query.setNo(id);
           List<SamTeacher> list = teacherService.selectSamTeacherList(query);
            if (list == null || list.isEmpty()) {
                com.ruoyi.achievement.domain.SamTeacher teacher = new com.ruoyi.achievement.domain.SamTeacher();
                teacher.setTeacherName(name != null ? name : loginName);
                teacher.setNo(id);
                // Additional fields can be set if needed
                teacherService.insertSamTeacher(teacher);
            }
        } else if (roleId == 3L) { // Student
           SamStudent query = new com.ruoyi.achievement.domain.SamStudent();
            query.setNo(id);
            List<com.ruoyi.achievement.domain.SamStudent> list = studentService.selectSamStudentList(query);
            if (list == null || list.isEmpty()) {
                com.ruoyi.achievement.domain.SamStudent student = new com.ruoyi.achievement.domain.SamStudent();
                student.setName(name != null ? name : loginName);
                student.setNo(id);
                // Additional fields can be set if needed
                studentService.insertSamStudent(student);
            }
        }

        // 或者调用 roleService 相关方法（看你的版本）

        return userService.selectUserByUserName(loginName);
    }
}
