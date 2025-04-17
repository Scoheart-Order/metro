<template>
  <div class="reset-password-container">
    <div class="reset-password-card">
      <div class="reset-password-header">
        <img src="../../assets/logo.png" alt="Logo" class="logo" />
        <h2>重置密码</h2>
      </div>
      
      <el-steps :active="currentStep" finish-status="success" simple>
        <el-step title="验证身份"></el-step>
        <el-step title="设置新密码"></el-step>
        <el-step title="完成"></el-step>
      </el-steps>
      
      <!-- Step 1: Verify Identity -->
      <div v-if="currentStep === 0">
        <el-form
          ref="identityFormRef"
          :model="identityForm"
          :rules="identityRules"
          label-position="top"
          @submit.prevent="verifyIdentity"
        >
          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="identityForm.email"
              placeholder="请输入注册时使用的邮箱"
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="identityForm.phone"
              placeholder="请输入注册时使用的手机号"
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="邮箱验证码" prop="emailCode">
            <div class="code-input-group">
              <el-input
                v-model="identityForm.emailCode"
                placeholder="请输入邮箱验证码"
              />
              <el-button 
                type="primary" 
                :disabled="emailCodeSending || emailCountdown > 0"
                @click="sendEmailCode"
              >
                {{ emailCountdown > 0 ? `${emailCountdown}秒后重试` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item label="短信验证码" prop="smsCode">
            <div class="code-input-group">
              <el-input
                v-model="identityForm.smsCode"
                placeholder="请输入短信验证码"
              />
              <el-button 
                type="primary" 
                :disabled="smsCodeSending || smsCountdown > 0"
                @click="sendSmsCode"
              >
                {{ smsCountdown > 0 ? `${smsCountdown}秒后重试` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="reset-button"
              @click="verifyIdentity"
            >
              下一步
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- Step 2: Set New Password -->
      <div v-else-if="currentStep === 1">
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-position="top"
          @submit.prevent="resetPassword"
        >
          <el-form-item label="新密码" prop="password">
            <el-input
              v-model="passwordForm.password"
              type="password"
              placeholder="请输入新密码"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
            <div class="password-strength">
              <p>密码强度: <span :class="passwordStrengthClass">{{ passwordStrengthText }}</span></p>
              <el-progress :percentage="passwordStrengthPercentage" :color="passwordStrengthColor"></el-progress>
              <p class="password-hint">密码必须包含大小写字母、数字和特殊符号，长度至少8位</p>
            </div>
          </el-form-item>
          
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="reset-button"
              @click="resetPassword"
            >
              重置密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- Step 3: Complete -->
      <div v-else class="complete-step">
        <el-result
          icon="success"
          title="密码重置成功"
          sub-title="您的密码已成功重置，请使用新密码登录"
        >
          <template #extra>
            <el-button type="primary" @click="$router.push('/login')">前往登录</el-button>
          </template>
        </el-result>
      </div>
      
      <div class="login-link" v-if="currentStep !== 2">
        <span>记起密码了？</span>
        <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { Message, Phone, Lock } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const currentStep = ref(0)

// Form refs
const identityFormRef = ref<FormInstance | null>(null)
const passwordFormRef = ref<FormInstance | null>(null)

// For verification codes
const emailCodeSending = ref(false)
const smsCodeSending = ref(false)
const emailCountdown = ref(0)
const smsCountdown = ref(0)

// Form data
const identityForm = reactive({
  email: '',
  phone: '',
  emailCode: '',
  smsCode: ''
})

const passwordForm = reactive({
  password: '',
  confirmPassword: ''
})

// Password strength calculation (same as in Register.vue)
const hasLowerCase = computed(() => /[a-z]/.test(passwordForm.password))
const hasUpperCase = computed(() => /[A-Z]/.test(passwordForm.password))
const hasNumber = computed(() => /[0-9]/.test(passwordForm.password))
const hasSpecialChar = computed(() => /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(passwordForm.password))
const isLongEnough = computed(() => passwordForm.password.length >= 8)

const passwordStrength = computed(() => {
  const criteria = [hasLowerCase.value, hasUpperCase.value, hasNumber.value, hasSpecialChar.value, isLongEnough.value]
  return criteria.filter(Boolean).length
})

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return '无'
  if (strength === 1) return '弱'
  if (strength === 2) return '弱'
  if (strength === 3) return '中'
  if (strength === 4) return '强'
  return '非常强'
})

const passwordStrengthClass = computed(() => {
  const strength = passwordStrength.value
  if (strength <= 2) return 'weak'
  if (strength === 3) return 'medium'
  return 'strong'
})

const passwordStrengthPercentage = computed(() => {
  return passwordStrength.value * 20
})

const passwordStrengthColor = computed(() => {
  const strength = passwordStrength.value
  if (strength <= 2) return '#F56C6C'
  if (strength === 3) return '#E6A23C'
  if (strength === 4) return '#67C23A'
  return '#409EFF'
})

// Form validation rules
const identityRules = reactive({
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  emailCode: [
    { required: true, message: '请输入邮箱验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ],
  smsCode: [
    { required: true, message: '请输入短信验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
})

// Password validation (same as in Register.vue)
const validatePassword = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (passwordStrength.value < 3) {
    callback(new Error('密码强度不足，请包含大小写字母、数字和特殊符号'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      // Validate confirm password again if it was already filled
      if (passwordForm.password !== passwordForm.confirmPassword) {
        callback(new Error('两次输入的密码不一致'))
      }
    }
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = reactive({
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

// Send verification codes
const sendEmailCode = async () => {
  if (!identityForm.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  
  emailCodeSending.value = true
  
  try {
    // Mock API call, replace with actual API
    await axios.post('/api/auth/send-email-code', { email: identityForm.email })
    ElMessage.success('邮箱验证码已发送')
    
    // Start countdown
    emailCountdown.value = 60
    const timer = setInterval(() => {
      emailCountdown.value--
      if (emailCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error('Send email code error:', error)
    ElMessage.error('邮箱验证码发送失败')
  } finally {
    emailCodeSending.value = false
  }
}

const sendSmsCode = async () => {
  if (!identityForm.phone) {
    ElMessage.warning('请先输入手机号')
    return
  }
  
  smsCodeSending.value = true
  
  try {
    // Mock API call, replace with actual API
    await axios.post('/api/auth/send-sms-code', { phone: identityForm.phone })
    ElMessage.success('短信验证码已发送')
    
    // Start countdown
    smsCountdown.value = 60
    const timer = setInterval(() => {
      smsCountdown.value--
      if (smsCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error('Send SMS code error:', error)
    ElMessage.error('短信验证码发送失败')
  } finally {
    smsCodeSending.value = false
  }
}

// Step handlers
const verifyIdentity = async () => {
  if (!identityFormRef.value) return
  
  await identityFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请正确填写表单')
      return
    }
    
    loading.value = true
    
    try {
      // Mock API call, replace with actual API
      await axios.post('/api/auth/verify-codes', {
        email: identityForm.email,
        phone: identityForm.phone,
        emailCode: identityForm.emailCode,
        smsCode: identityForm.smsCode
      })
      
      // Move to next step if verification is successful
      currentStep.value = 1
      ElMessage.success('身份验证成功')
    } catch (error) {
      console.error('Verify identity error:', error)
      ElMessage.error('身份验证失败，请检查验证码是否正确')
    } finally {
      loading.value = false
    }
  })
}

const resetPassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请正确填写表单')
      return
    }
    
    loading.value = true
    
    try {
      const result = await userStore.resetPassword(
        identityForm.email,
        identityForm.emailCode,
        passwordForm.password
      )
      
      if (result.success) {
        currentStep.value = 2
        ElMessage.success('密码重置成功')
      } else {
        ElMessage.error('密码重置失败，请稍后重试')
      }
    } catch (error) {
      console.error('Reset password error:', error)
      ElMessage.error('密码重置过程中发生错误')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.reset-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  
  .reset-password-card {
    width: 450px;
    padding: 30px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    
    .reset-password-header {
      text-align: center;
      margin-bottom: 30px;
      
      .logo {
        width: 80px;
        margin-bottom: 16px;
      }
      
      h2 {
        margin: 0;
        font-size: 24px;
        color: #303133;
      }
    }
    
    .el-steps {
      margin: 20px 0 30px;
    }
    
    .code-input-group {
      display: flex;
      gap: 10px;
      
      .el-input {
        flex: 1;
      }
      
      .el-button {
        min-width: 120px;
      }
    }
    
    .password-strength {
      margin-top: 8px;
      
      p {
        margin: 8px 0;
        font-size: 14px;
      }
      
      .weak {
        color: #F56C6C;
      }
      
      .medium {
        color: #E6A23C;
      }
      
      .strong {
        color: #67C23A;
      }
      
      .password-hint {
        font-size: 12px;
        color: #909399;
      }
    }
    
    .reset-button {
      width: 100%;
    }
    
    .complete-step {
      padding: 20px 0;
    }
    
    .login-link {
      text-align: center;
      margin-top: 16px;
      
      span {
        color: #909399;
      }
    }
  }
}
</style> 