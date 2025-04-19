<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <img src="../../assets/user-icon.svg" alt="Logo" class="logo" />
        <h2>用户登录</h2>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="账号" prop="account">
          <el-input
            v-model="loginForm.account"
            placeholder="请输入用户名或手机号"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div class="form-actions">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
        </div>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="register-link">
          <span>还没有账号？</span>
          <el-link type="primary" @click="$router.push('/register')"
            >立即注册</el-link
          >
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../../stores';
import { User, Lock } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';
import { type LoginDto, type PhoneLoginDto } from '../../api';

const router = useRouter();
const userStore = useUserStore();

const loginFormRef = ref<FormInstance | null>(null);
const rememberMe = ref(false);
const loading = ref(false);

const loginForm = reactive({
  account: '',
  password: '',
});

// Helper to check if the account input is a phone number
const isPhoneNumber = computed(() => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  return phoneRegex.test(loginForm.account);
});

// Helper to check if the account input is a username
const isUsername = computed(() => {
  // Username is typically 4-20 characters
  return loginForm.account.length >= 4 && loginForm.account.length <= 20;
});

const validateAccount = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请输入用户名或手机号'));
  } else if (!isPhoneNumber.value && !isUsername.value) {
    callback(new Error('请输入有效的用户名或手机号'));
  } else {
    callback();
  }
};

const loginRules = reactive({
  account: [
    { required: true, message: '请输入用户名或手机号', trigger: 'blur' },
    { validator: validateAccount, trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' },
  ],
});

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请正确填写表单');
      return;
    }

    loading.value = true;

    try {
      let success = false;

      // Determine login method based on input type
      if (isPhoneNumber.value) {
        const phoneLoginDto: PhoneLoginDto = {
          phone: loginForm.account,
          password: loginForm.password,
        };
        success = await userStore.loginByPhone(phoneLoginDto);
      } else {
        const loginDto: LoginDto = {
          username: loginForm.account,
          password: loginForm.password,
        };
        success = await userStore.login(loginDto);
      }

      // 只有在明确返回成功时才继续
      if (success) {
        // 登录成功处理
        ElMessage.success('登录成功');

        // Store credentials if "remember me" is checked
        if (rememberMe.value) {
          localStorage.setItem('rememberedUser', loginForm.account);
        } else {
          localStorage.removeItem('rememberedUser');
        }

        // Make sure user profile is fully loaded before redirecting
        if (!userStore.user) {
          await userStore.fetchProfile();
        }

        // Double-check that user data is available before redirecting
        if (userStore.user) {
          // Redirect based on user role using the homeRoute getter
          router.push(userStore.homeRoute);
          console.log(
            `User logged in successfully. Redirecting to ${userStore.homeRoute}`
          );
        } else {
          // If user data is still not available, go to home page
          router.push('/');
          console.error('Failed to load user profile after login');
        }
      } else {
        // 明确处理登录失败的情况
        ElMessage.error('用户名或密码错误');
      }
    } catch (error: any) {
      console.error('Login error:', error);
      // 显示具体错误信息
      ElMessage.error('登录失败，请检查用户名和密码');
    } finally {
      loading.value = false;
    }
  });
};
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;

  .login-card {
    width: 400px;
    padding: 30px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);

    .login-header {
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

    .form-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }

    .login-button {
      width: 100%;
    }

    .register-link {
      text-align: center;
      margin-top: 16px;

      span {
        color: #909399;
      }
    }
  }
}
</style>
