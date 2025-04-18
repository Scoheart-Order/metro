<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <img src="../../assets/user-icon.svg" alt="Logo" class="logo" />
        <h2>用户注册</h2>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-position="top"
        @submit.prevent="handleRegister"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
          >
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
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
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
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
            class="register-button"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        
        <div class="login-link">
          <span>已有账号？</span>
          <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { User, Message, Phone, Lock } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const registerFormRef = ref<FormInstance | null>(null)

const registerForm = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

// Password strength calculation
const hasLowerCase = computed(() => /[a-z]/.test(registerForm.password))
const hasUpperCase = computed(() => /[A-Z]/.test(registerForm.password))
const hasNumber = computed(() => /[0-9]/.test(registerForm.password))
const hasSpecialChar = computed(() => /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(registerForm.password))
const isLongEnough = computed(() => registerForm.password.length >= 8)

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

// Validation rules
const validatePassword = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (passwordStrength.value < 3) {
    callback(new Error('密码强度不足，请包含大小写字母、数字和特殊符号'))
  } else {
    if (registerForm.confirmPassword !== '') {
      // Validate confirm password again if it was already filled
      if (registerForm.password !== registerForm.confirmPassword) {
        callback(new Error('两次输入的密码不一致'))
      }
    }
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度应为2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请正确填写表单')
      return
    }
    
    loading.value = true
    
    try {
      const userData = {
        username: registerForm.username,
        phone: registerForm.phone,
        password: registerForm.password,
        email: ''
      }
      
      const result = await userStore.register(userData)
      
      if (result.success) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } else {
        ElMessage.error('注册失败，请稍后再试')
      }
    } catch (error) {
      console.error('Register error:', error)
      ElMessage.error('注册时发生错误')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  
  .register-card {
    width: 450px;
    padding: 30px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    
    .register-header {
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
    
    .register-button {
      width: 100%;
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