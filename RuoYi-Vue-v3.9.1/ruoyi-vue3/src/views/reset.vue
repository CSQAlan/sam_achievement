<template>
  <div class="reset">
    <el-form ref="resetRef" :model="resetForm" :rules="resetRules" class="reset-form">
      <h3 class="title">重置密码</h3>
      
      <el-form-item prop="username">
        <el-input
          v-model="resetForm.username"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="请输入账号"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>

      <el-form-item prop="password" v-show="resetForm.resetType != 'wechat'">
        <el-input
          v-model="resetForm.password"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="密码"
        
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword" v-show="resetForm.resetType != 'wechat'">
        <el-input
          v-model="resetForm.confirmPassword"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="确认密码"
         
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      
      <!-- 邮箱重置 -->
      <el-form-item prop="email" v-show="resetForm.resetType === 'email'">
        <el-input
          v-model="resetForm.email"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="请输入QQ邮箱"
        >
          <template #prefix><svg-icon icon-class="email" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>



      
      <!-- 手机重置 -->
      <el-form-item prop="phoneNumber"  v-show="resetForm.resetType === 'phone'">
        <el-input
          v-model="resetForm.phoneNumber"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="请输入手机号"
        >
          <template #prefix><svg-icon icon-class="phone" class="el-input__icon input-icon" /></template>
        </el-input>

      </el-form-item>

      <el-form-item prop="code" v-show="resetForm.resetType != 'wechat'">
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <el-input
            v-model="resetForm.code"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="请输入验证码"
            style="flex: 1; margin-right: 40px;" 
          />
          <el-button @click="getCode" type="primary" size="large" :disabled="isGetCodeDisabled">
            <!--如果有倒计时（countDown > 0），显示秒数；否则显示原始文字 -->
            {{ countDown > 0 ? `${countDown}秒后可重新发送` : '获取验证码' }}
          </el-button>
        </div>
      </el-form-item>
    
      <el-form-item prop="resetType">
        <el-radio-group v-model="resetForm.resetType" @change="handleResetTypeChange">
          <el-radio label="email">QQ邮箱</el-radio>
          <el-radio label="phone">手机号</el-radio>
          <el-radio label="wechat">微信重置</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <!-- 微信二维码展示 -->
      <div v-show="resetForm.resetType === 'wechat'" class="wechat-qrcode-section">
        <div class="qrcode-placeholder">
          <div class="qrcode-description">
            <p>请使用微信扫描下方二维码进行验证</p>
            <p>验证成功后将自动跳转</p>
          </div>
          <!-- 实际的二维码 -->
          <div class="qrcode-image" v-loading="qrCodeLoading">
            <vue-qr
              :text="qrCodeUrl"
              :size="200"
              class="qrcode-img"
              v-if="qrCodeUrl && !qrCodeLoading"
            ></vue-qr>
            <div class="qrcode-placeholder-box" v-else-if="!qrCodeUrl && !qrCodeLoading">
              <p>请获取二维码</p>
            </div>
          </div>
          <el-button 
            type="primary" 
            @click="getWechatQRCode"
            :loading="qrCodeLoading"
            v-if="(!qrCodeUrl || resetForm.resetType === 'wechat') && resetForm.resetType === 'wechat'"
          >
            {{ qrCodeUrl ? '刷新二维码' : '获取二维码' }}
          </el-button>
        </div>
      </div>

      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="large"
          type="primary"
          style="width:100%;"
          @click.prevent="handleReset"
          v-show="resetForm.resetType !== 'wechat'"
          :disabled="isResetBtnDisabled || loading"
        >
          <span v-if="!loading">发送重置请求</span>
          <span v-else>发送中...</span>
        </el-button>
        <div style="float: right;">
          <router-link class="link-type" :to="'/login'">返回登录</router-link>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount,computed } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentInstance } from '@vue/runtime-core'
import { getConfirQrcode, checkWechatBind } from "@/api/common/wechat";
import { sendCodeApi } from "@/api/jsgl/email";
import { sendCodePhoneApi } from '@/api/jsgl/sms';
import { verifyCodeEmailApi } from "@/api/jsgl/email";
import { verifyCodePhoneApi } from "@/api/jsgl/sms";



// 引入vue-qr组件
import vueQr from 'vue-qr/src/packages/vue-qr.vue'
import { set } from 'nprogress';

const router = useRouter()
const { proxy } = getCurrentInstance()
// 倒计时相关变量
const countDown = ref(0); // 倒计时秒数，0 表示未倒计时
const countDownTimer = ref(null); // 存储定时器实例，用于清除

const resetForm = ref({
  username: "",
  email: "",
  phoneNumber: "",
  wechat: "",
  resetType: "email" // 默认重置方式
})

// 用 computed 动态生成规则（替代原有的 resetRules 静态定义）
const resetRules = computed(() => {
  const type = resetForm.value.resetType; // 当前选中的重置方式
  return {
    // 账号是所有方式都必填的
    username: [{ required: true, trigger: ["blur", "change"], message: "请输入您的账号" }],

    // 邮箱：仅当选择 email 方式时才校验
    email: type === 'email' 
      ? [
          { required: true, trigger: ["blur", "change"], message: "请输入邮箱地址" },
          { type: "email", trigger: ["blur", "change"], message: "请输入正确的邮箱地址" }
        ] 
      : [], // 非 email 方式时，不校验

    // 手机号：仅当选择 phone 方式时才校验
    phoneNumber: type === 'phone' 
      ? [
          { required: true, trigger: ["blur", "change"], message: "请输入手机号" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号格式", trigger: ["blur", "change"] }
        ] 
      : [], // 非 phone 方式时，不校验

    // 验证码：仅当选择 email/phone 方式时才校验（微信不需要验证码）
    code: type !== 'wechat' 
      ? [{ required: true, trigger: ["blur", "change"], message: "请输入验证码" }] 
      : [],

    // 密码和确认密码：仅当选择 email/phone 方式时才校验（微信可能不需要密码输入）
    password: type !== 'wechat' 
      ? [{ required: true, trigger: ["blur", "change"], message: "请输入新密码" }] 
      : [],
    confirmPassword: type !== 'wechat' 
      ? [
          { required: true, trigger: ["blur", "change"], message: "请确认新密码" },
          { 
            validator: (rule, value, callback) => {
              if (value !== resetForm.value.password) {
                callback(new Error("两次输入的密码不一致"));
              } else {
                callback();
              }
            },
            trigger: ["blur", "change"]
          }
        ] 
      : []
  };
});

const loading = ref(false)
const qrCodeUrl = ref("")
const qrCodeLoading = ref(false)
const checkBindTimer = ref(null)

// 判断发送验证码按钮是否禁用（关键信息未填完则禁用）
const isGetCodeDisabled = computed(() => {
  // 正确读取 ref 响应式数据：通过 .value 获取内部值
  const { resetType, username, email, phoneNumber } = resetForm.value;

  // 如果正在倒计时（countDown > 0），直接禁用
  if (countDown.value > 0) return true;
  
  // 验证逻辑：先判断用户名是否为空（去空格避免纯空格的无效输入）
  if (!username.trim()) return true;
  
  // 按重置类型判断对应字段是否为空
  if (resetType === 'email' && !email.trim()) return true;
  if (resetType === 'phone' && !phoneNumber.trim()) return true;
  
  // 所有条件都满足时，按钮可点击（返回 false 表示不禁用）
  return false;
});

// 判断重置按钮是否禁用（关键信息未填完则禁用）
const isResetBtnDisabled = computed(() => {
  const { resetType, username, password, confirmPassword, code, email, phoneNumber } = resetForm.value;

  // 1. 基础必填项：账号、新密码、确认密码、验证码 必须填
  if (!username.trim() || !password.trim() || !confirmPassword.trim() || !code.trim()) {
    return true;
  }

  // 2. 按重置类型判断：邮箱/手机号必须填
  if (resetType === 'email' && !email.trim()) {
    return true;
  }
  if (resetType === 'phone' && !phoneNumber.trim()) {
    return true;
  }

  // 所有必填项都填完了，按钮可点击（返回false表示不禁用）
  return false;
});
// 重置方式切换
function handleResetTypeChange(value) {
  // 清空表单验证
  proxy.$refs.resetRef.clearValidate()
  
  
}

//获取二维码
function getWechatQRCode(){
  console.log("用户账号：", resetForm.value.username)

  getConfirQrcode(resetForm.value.username).then(response => { 
    if(response.code == 200 && response.data){
      qrCodeUrl.value = response.data.qrcode
      qrCodeLoading.value = false
    } else {
      proxy.$modal.msgError(response.msg || "获取二维码失败")
      qrCodeLoading.value = false
    }
  })
}
// 获取验证码
function getCode(){
  if(resetForm.value.resetType === 'email'){
    console.log("获取验证码")
    //请求发送验证码
    const params = {
      username: resetForm.value.username,
      email: resetForm.value.email
    }
    sendCodeApi(params)
     .then(res =>{
      if(res.code == 200){
        proxy.$modal.msgSuccess(res.msg )
        // 启动 60 秒倒计时
        startCountDown();
      } else {
        proxy.$modal.msgError(res.msg)
      }

     })
  }
  if(resetForm.value.resetType === 'phone'){
    console.log("获取验证码")
    //请求发送验证码
    const params = {
      username: resetForm.value.username,
      phone: resetForm.value.phoneNumber
    }
    sendCodePhoneApi(params)
     .then(res =>{
      if(res.code == 200){
        proxy.$modal.msgSuccess(res.msg )
        // 启动 60 秒倒计时
        startCountDown();
      }else {
        proxy.$modal.msgError(res.msg)
      }
     })
  }
}

// 倒计时核心函数
function startCountDown() {
  // 1. 清除之前可能残留的定时器，避免重复倒计时
  if (countDownTimer.value) {
    clearInterval(countDownTimer.value);
  }
  // 2. 设置初始倒计时为 60 秒
  countDown.value = 60;
  // 3. 启动定时器，每秒减 1
  countDownTimer.value = setInterval(() => {
    countDown.value--;
    // 4. 倒计时结束后，清除定时器
    if (countDown.value <= 0) {
      clearInterval(countDownTimer.value);
      countDownTimer.value = null; // 重置定时器实例
    }
  }, 1000);
}

function handleReset() {
  console.log("点击重置按钮");
  proxy.$refs.resetRef.validate(valid => {
    console.log("准备开始验证");
    if (valid) {
      console.log("开始验证");
      
      loading.value = true;

      // 1. 准备验证码验证参数（包含账号、对应联系方式、验证码）
      const verifyParams = {
        username: resetForm.value.username,
        code: resetForm.value.code,
        // 补充：传邮箱/手机号（与验证码归属匹配，后端可能需要二次校验）
        ...(resetForm.value.resetType === 'email' 
          ? { email: resetForm.value.email } 
          : { phoneNumber: resetForm.value.phoneNumber })
      };

      // 2. 选择对应的验证码验证接口
      let verifyApi;
      if (resetForm.value.resetType === 'email') {
        verifyApi = verifyCodeEmailApi(verifyParams);
      } else if (resetForm.value.resetType === 'phone') {
        verifyApi = verifyCodePhoneApi(verifyParams);
      }

      // 3. 执行验证码验证
      verifyApi.then(verifyRes => {
        // 3.1 验证码正确：调用接口存新密码
        if (verifyRes.code === 200) {
          loading.value = true;
          let message = "";
          switch (resetForm.value.resetType) {
            case 'email':
              message = "重置邮件已发送,请查收";
              break;
            case 'phone':
              message = "短信验证码已发送,请查收";
              break;
            case 'wechat':
              message = "微信重置请求已发送,请查收";
              break;
          }
          //模拟异步操作
          setTimeout(() => {
            loading.value = false;
            proxy.$modal.msgSuccess(message);
            router.push("/login");
          }, 1000);

          //组件销毁前清除定时器
          onBeforeUnmount(() => {
            if (checkBindTimer.value) {
              clearInterval(checkBindTimer.value);
            }
          });

          //初始化时如果默认是微信重置方式则生成二维码
          onMounted(() => {
            if (resetForm.value.resetType === 'wechat') {
              getWechatQRCode();
            }
          });
        // 3.2 验证码错误：提示并关闭加载
        } else {
          loading.value = false;
          proxy.$modal.msgError(verifyRes.msg || "验证码错误或已过期，请重新输入");
        }
      }).catch(error => {
        // 验证码接口请求异常（如网络错误）
        loading.value = false;
        proxy.$modal.msgError("网络错误，无法验证验证码");
        console.error("验证码验证接口异常：", error);
      });
    }
  });
}
</script>

<style lang='scss' scoped>
.reset {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}

.reset-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.wechat-qrcode-section {
  text-align: center;
  margin-bottom: 20px;
  
  .qrcode-description {
    margin-bottom: 15px;
    
    p {
      margin: 5px 0;
      color: #666;
    }
  }
  
  .qrcode-image {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 200px;
    
    .qrcode-img {
      max-width: 100%;
      height: auto;
    }
  }
  
  .qrcode-placeholder-box {
    border: 1px dashed #ddd;
    border-radius: 8px;
    padding: 20px;
    margin: 0 auto 15px;
    width: 200px;
    height: 200px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    
    .qrcode-icon {
      font-size: 48px;
      color: #1aad19;
      margin-bottom: 10px;
    }
    
    p {
      color: #999;
      margin: 0;
    }
  }
}
</style>