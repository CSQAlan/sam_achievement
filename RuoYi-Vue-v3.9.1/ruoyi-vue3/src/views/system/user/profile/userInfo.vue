<template>
  <div>
    <el-form ref="userRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="用户昵称" prop="nickName">
        <el-input v-model="form.nickName" maxlength="30" />
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input v-model="form.phonenumber" maxlength="11" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" maxlength="50" />
      </el-form-item>
      <!-- 学生特有信息 -->
      <template v-if="isStudent">
        <el-form-item label="学号">
          <el-input v-model="form.userName" disabled />
        </el-form-item>
        <el-form-item label="学院" prop="school">
          <el-select
              v-model="form.school"
              placeholder="请选择学院"
              @change="handleSchoolChange"
          >
            <el-option
                v-for="school in schools"
                :key="school.id"
                :label="school.label"
                :value="school.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="院系" prop="department">
          <el-select v-model="form.department" placeholder="请选择系别" :disabled="!form.school">
            <el-option
                v-for="department in departments"
                :key="department.id"
                :label="department.label"
                :value="department.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" />
        </el-form-item>
        <el-form-item label="入学年份" prop="classYear">
          <el-input v-model="form.classYear" />
        </el-form-item>
        <el-form-item label="班级" prop="className">
          <el-input v-model="form.className" />
        </el-form-item>
      </template>

      <!-- 教师特有信息 -->
      <template v-else-if="isTeacher">
        <el-form-item label="工号">
          <el-input v-model="form.userName" disabled />
        </el-form-item>
        <el-form-item label="学院" prop="school">
          <el-select
              v-model="form.school"
              placeholder="请选择学院"
              @change="handleSchoolChange"
          >
            <el-option
                v-for="school in schools"
                :key="school.id"
                :label="school.label"
                :value="school.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="院系" prop="department">
          <el-select v-model="form.department" placeholder="请选择系别" :disabled="!form.school">
            <el-option
                v-for="department in departments"
                :key="department.id"
                :label="department.label"
                :value="department.value"
            />
          </el-select>
        </el-form-item>
      </template>
      <!-- <el-form-item label="微信绑定">
      <span class="wechat-status">
        {{ form.wxNickName || '未绑定' }}
      </span>
      <el-button
        v-if="!form.wxNickName"
        type="primary"

        @click="showWechatBindDialog"
        class="wechat-btn"
      >
        绑定微信
      </el-button>
      <el-button
        v-else
        type="danger"

        @click="handleUnbindWechat"
        class="wechat-btn"
      >
        解除绑定
      </el-button>
    </el-form-item> -->
      <el-form-item label="性别">
        <el-radio-group v-model="form.sex">
          <el-radio value="0">男</el-radio>
          <el-radio value="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">保存</el-button>
        <el-button type="danger" @click="close">关闭</el-button>
      </el-form-item>
    </el-form>
    <!-- 微信绑定二维码弹窗
    <el-dialog
      title="微信扫码绑定"
      v-model="wechatBindDialogVisible"
      width="300px"
      :close-on-click-modal="false"
      :show-close="true"
      @close="handleDialogClose"
    >
      <div class="qr-code-container">
        <vue-qr
          :text="qrCodeUrl"
          :size="200"
          class="qr-code"
          v-if="qrCodeUrl"
        ></vue-qr>
        <p class="qr-tip" v-if="!qrCodeUrl">正在生成二维码...</p>
        <p class="qr-tip" v-else>请使用微信扫码授权绑定</p>
      </div>
    </el-dialog> -->
  </div>
</template>

<script setup>
import { getProfileDeptTree, getUserProfile, updateUserProfile } from "@/api/system/user";
import {
  ref,
  onMounted,
  onBeforeUnmount,
  computed,
  getCurrentInstance,
} from "vue";
import {
  checkWechatBind,
  unbindWechat,
  generateWechatQRCode,
} from "@/api/common/wechat";
import { getStudentInfoByNo, updateStudentInfo } from "@/api/student/info";
import {
  getTeacherInfoByNo,
  updateTeacherInfo,
} from "@/api/sam/sam_achievement";
import { useRouter, useRoute } from "vue-router";
import useUserStore from "@/store/modules/user";
import { ElMessage, ElMessageBox } from "element-plus";

const props = defineProps({
  user: {
    type: Object,
  },
});
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const { proxy } = getCurrentInstance();

const form = ref({});
const wechatBindDialogVisible = ref(false);
const qrCodeUrl = ref("");
const uuid = ref("");
const checkBindTimer = ref(null);
const schools = ref([]);
const departments = ref([]);

// 计算属性：判断是否为学生或教师
const isStudent = computed(() => {
  return (
      form.value.roles &&
      form.value.roles.some((role) => role.roleKey === "student")
  );
});

const isTeacher = computed(() => {
  return (
      form.value.roles &&
      form.value.roles.some((role) => role.roleKey === "teacher")
  );
});

const rules = ref({
  nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
  email: [
    { required: true, message: "邮箱地址不能为空", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  phonenumber: [
    { required: true, message: "手机号码不能为空", trigger: "blur" },
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
  school: [{ required: true, message: "请选择学院", trigger: "change" }],
  department: [{ required: true, message: "请选择系别", trigger: "change" }],
  major: [{ required: true, message: "专业不能为空", trigger: "blur" }],
  classYear: [{ required: true, message: "年级不能为空", trigger: "blur" }],
  className: [{ required: true, message: "班级不能为空", trigger: "blur" }],
});
// 初始化微信绑定状态
const initWechatStatus = () => {
  fetchWechatStatus();
};

// 获取用户信息
const fetchWechatStatus = () => {
  getUserProfile().then((response) => {
    if (response.code === 200 && response.data) {
      form.value = response.data;

      // 根据角色和学号/工号查询详细信息
      fetchUserDetailInfo();
    }
  });
};

// 获取用户详细信息
const fetchUserDetailInfo = () => {
  if (isStudent.value && form.value.userName) {
    // 调用学生信息查询接口
    getStudentInfoByNo(form.value.userName)
        .then((response) => {
          if (response.code === 200 && response.data) {
            // 合并学生详细信息到表单
            form.value = {
              ...form.value,
              ...response.data,
            };
          }
          normalizeDeptSelection();
        })
        .catch((err) => {
          console.log("获取学生详细信息失败:", err);
        });
  } else if (isTeacher.value && form.value.userName) {
    // 调用教师信息查询接口
    getTeacherInfoByNo(form.value.userName)
        .then((response) => {
          if (response.code === 200 && response.data) {
            // 合并教师详细信息到表单
            form.value = {
              ...form.value,
              ...response.data,
            };
          }
          normalizeDeptSelection();
        })
        .catch((err) => {
          console.log("获取教师详细信息失败:", err);
        });
  }
};

// 显示微信绑定弹窗
const showWechatBindDialog = () => {
  // 清空之前的二维码
  qrCodeUrl.value = "";
  generateWechatQRCode()
      .then((response) => {
        if (response.code === 200 && response.data) {
          qrCodeUrl.value = response.data.qrcode;
          uuid.value = response.data.uuid;
        }
      })
      .catch((err) => {
        console.error("生成微信二维码失败:", err);
      });
  wechatBindDialogVisible.value = true;

  // 启动轮询检查绑定状态
  setTimeout(() => {
    startCheckBind(uuid.value);
  }, 500);
};

// 轮询检查绑定状态
const startCheckBind = (uuid) => {
  // 清除已有定时器
  if (checkBindTimer.value) {
    clearInterval(checkBindTimer.value);
  }

  let pollCount = 0;
  const maxPollCount = 20; // 最多轮询20次（3秒/次，共60秒）

  checkBindTimer.value = setInterval(() => {
    pollCount++;

    // 轮询超时处理
    if (pollCount >= maxPollCount) {
      ElMessage.warning("绑定超时，请重新尝试");
      handleDialogClose();
      return;
    }

    // 检查弹窗是否已关闭
    if (!wechatBindDialogVisible.value) {
      clearInterval(checkBindTimer.value);
      return;
    }

    // 检查绑定状态
    checkWechatBind(uuid).then((response) => {
      if (
          response.code === 200 &&
          response.data &&
          response.data.status.equals(CONFIRMED)
      ) {
        ElMessage.success("微信绑定成功");
        handleDialogClose();
      }
    });
  }, 3000); // 每3秒检查一次
};

// 解除微信绑定
const handleUnbindWechat = () => {
  ElMessageBox.confirm("确定要解除微信绑定吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    unbindWechat()
        .then((response) => {
          if (response.code === 200) {
            ElMessage.success("解除绑定成功");
            fetchWechatStatus();
          } else {
            ElMessage.error(response.msg || "解除绑定失败");
          }
        })
        .catch((err) => {
          console.error("解除绑定失败:", err);
          ElMessage.error("解除绑定失败，请重试");
        });
  });
};

// 关闭弹窗时清理资源
const handleDialogClose = () => {
  if (checkBindTimer.value) {
    clearInterval(checkBindTimer.value);
    checkBindTimer.value = null;
  }
  wechatBindDialogVisible.value = false;
  qrCodeUrl.value = "";
};

onMounted(() => {
  loadDeptTree().finally(() => {
    initWechatStatus();
  });
});

onBeforeUnmount(() => {
  if (checkBindTimer.value) {
    clearInterval(checkBindTimer.value);
  }
});

function buildSchoolOptions(nodes = []) {
  const schoolOptions = [];

  const visit = (treeNodes) => {
    treeNodes.forEach((node) => {
      if (node.disabled) {
        return;
      }

      const children = Array.isArray(node.children)
          ? node.children.filter((child) => !child.disabled)
          : [];

      if (!children.length) {
        return;
      }

      const hasNestedChildren = children.some(
          (child) => Array.isArray(child.children) && child.children.some((grandChild) => !grandChild.disabled)
      );

      if (hasNestedChildren) {
        visit(children);
        return;
      }

      schoolOptions.push({
        id: node.id,
        value: String(node.id),
        label: node.label,
        departments: children.map((child) => ({
          id: child.id,
          value: String(child.id),
          label: child.label,
        })),
      });
    });
  };

  visit(nodes);
  return schoolOptions;
}

function loadDeptTree() {
  return getProfileDeptTree()
      .then((response) => {
        schools.value = buildSchoolOptions(response.data || []);
        normalizeDeptSelection();
      })
      .catch((err) => {
        console.error("获取部门树失败:", err);
        schools.value = [];
        departments.value = [];
        proxy.$modal.msgError("获取部门树失败，请联系管理员");
      });
}

function findSchoolOption(value) {
  if (value === null || value === undefined || value === "") {
    return null;
  }

  const normalizedValue = String(value);
  return schools.value.find(
      (school) => school.value === normalizedValue || school.label === normalizedValue
  ) || null;
}

function findDepartmentOption(schoolOption, value) {
  if (!schoolOption || value === null || value === undefined || value === "") {
    return null;
  }

  const normalizedValue = String(value);
  return (schoolOption.departments || []).find(
      (department) => department.value === normalizedValue || department.label === normalizedValue
  ) || null;
}

function normalizeDeptSelection() {
  const schoolOption = findSchoolOption(form.value.school);
  if (!schoolOption) {
    departments.value = [];
    if (form.value.school) {
      form.value.school = "";
    }
    if (form.value.department) {
      form.value.department = "";
    }
    return;
  }

  form.value.school = schoolOption.value;
  departments.value = schoolOption.departments || [];

  const departmentOption = findDepartmentOption(schoolOption, form.value.department);
  form.value.department = departmentOption ? departmentOption.value : "";
}

// 处理学院选择变化
function handleSchoolChange(selectedSchoolValue) {
  const selectedSchool = findSchoolOption(selectedSchoolValue);

  if (selectedSchool) {
    departments.value = selectedSchool.departments || [];
    const matchedDepartment = departments.value.some(
        (department) => department.value === String(form.value.department)
    );
    if (!matchedDepartment) {
      form.value.department = "";
    }
  } else {
    departments.value = [];
    form.value.department = "";
  }
}

/** 提交按钮 */
function submit() {
  proxy.$refs.userRef.validate((valid) => {
    if (valid) {
      // 分离用户基本信息和学生/教师信息
      const userData = {
        nickName: form.value.nickName,
        phonenumber: form.value.phonenumber,
        email: form.value.email,
        sex: form.value.sex,
        profileInitialized: 1, // 标记为已初始化
      };

      // 更新用户基本信息
      updateUserProfile(userData)
          .then((response) => {
            if (response.code === 200) {
              props.user.phonenumber = form.value.phonenumber;
              props.user.email = form.value.email;

              const extraTasks = [];

              // 如果是学生，更新学生信息
              if (isStudent.value) {
                const studentData = {
                  no: form.value.no,
                  school: form.value.school,
                  department: form.value.department,
                  major: form.value.major,
                  classYear: form.value.classYear,
                  className: form.value.className,
                  name: form.value.name,
                };
                console.log("传递的学生数据:", studentData); // 添加调试日志
                extraTasks.push(
                    updateStudentInfo(studentData)
                        .then((studentResponse) => {
                          if (studentResponse.code === 200) {
                            proxy.$modal.msgSuccess("学生信息保存成功");
                            console.log("学生信息更新成功");
                          } else {
                            proxy.$modal.msgError("学生信息保存失败");
                          }
                        })
                        .catch((err) => {
                          console.error("更新学生信息失败:", err);
                          proxy.$modal.msgError("学生信息更新失败");
                        })
                );
              }

              // 如果是教师，更新教师信息（假设有相应API）
              // 这里需要根据实际API调整
              if (isTeacher.value) {
                const teacherData = {
                  // 教师字段
                  no: form.value.no,
                  school: form.value.school,
                  department: form.value.department,
                };
                extraTasks.push(
                    updateTeacherInfo(teacherData)
                        .then((teacherResponse) => {
                          if (teacherResponse.code === 200) {
                            proxy.$modal.msgSuccess("教师信息保存成功");
                            console.log("教师信息更新成功");
                          } else {
                            proxy.$modal.msgError("教师信息保存失败");
                          }
                        })
                        .catch((err) => {
                          console.error("更新教师信息失败:", err);
                          proxy.$modal.msgError("教师信息保存失败");
                        })
                );
              }

              const finalize = () => {
                userStore.profileInitialized = 1;
                const redirectTarget = route.query.redirect;
                const resolved = redirectTarget
                    ? router.resolve(redirectTarget)
                    : null;
                // 清除地址栏中的 redirect，避免刷新后仍然携带旧跳转
                router.replace({ path: route.path, query: {} }).finally(() => {
                  if (resolved && resolved.matched.length) {
                    router.replace(redirectTarget);
                  } else {
                    router.replace("/"); // 或 router.back()
                  }
                });
              };

              if (extraTasks.length > 0) {
                Promise.allSettled(extraTasks).finally(finalize);
              } else {
                finalize();
              }
            } else {
              proxy.$modal.msgError(response.msg || "修改失败");
            }
          })
          .catch((err) => {
            console.error("更新用户信息失败:", err);
            proxy.$modal.msgError("修改失败");
          });
    }
  });
}

/** 关闭按钮 */
function close() {
  proxy.$tab.closePage();
}
</script>
<style scoped>
.wechat-status {
  line-height: 32px;
  margin-right: 15px;
}
.wechat-btn {
  margin-left: 10px;
}
.qr-code-container {
  text-align: center;
  padding: 15px 0;
}
.qr-tip {
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}
</style>
