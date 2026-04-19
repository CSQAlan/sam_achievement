<!-- <template>
  <div class="app-container">
    <el-card>
      <template #header>
        <span>修改个人密码</span>
      </template>
      <el-form ref="pwdRef" :model="form" :rules="rules" label-width="100px" class="pwd-form">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="danger" plain @click="close">关闭</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup name="ResetPwd">
import { updateUserPwd } from '@/api/system/user'

const { proxy } = getCurrentInstance()
const pwdRef = ref(null)
const submitting = ref(false)
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
    return
  }
  callback()
}

const rules = {
  oldPassword: [
    { required: true, message: '旧密码不能为空', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '新密码不能为空', trigger: 'blur' },
    { min: 6, max: 20, message: '长度必须在 6 到 20 个字符之间', trigger: 'blur' },
    { pattern: /^[^<>'"|\\]+$/, message: '密码不能包含非法字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '确认密码不能为空', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

function resetForm() {
  form.oldPassword = ''
  form.newPassword = ''
  form.confirmPassword = ''
  pwdRef.value?.clearValidate()
}

function submit() {
  pwdRef.value?.validate(valid => {
    if (!valid) {
      return
    }
    submitting.value = true
    updateUserPwd(form.oldPassword, form.newPassword).then(() => {
      proxy.$modal.msgSuccess('密码修改成功，请重新登录后继续使用')
      resetForm()
    }).finally(() => {
      submitting.value = false
    })
  })
}

function close() {
  if (proxy?.$tab?.closePage) {
    proxy.$tab.closePage()
    return
  }
  history.back()
}
</script>

<style scoped>
.pwd-form {
  max-width: 520px;
}
</style> -->
