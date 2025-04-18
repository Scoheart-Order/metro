<template>
  <div class="profile-container">
    <el-card>
      <div class="page-header">
        <h2>个人中心</h2>
      </div>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>

      <div v-else-if="!userStore.user" class="empty-data">
        <el-empty description="请先登录" />
        <el-button
          type="primary"
          @click="$router.push('/login')"
          style="margin-top: 16px"
        >
          前往登录
        </el-button>
      </div>

      <div v-else>
        <!-- Balance Section -->
        <el-divider content-position="center">账户余额</el-divider>
        <div class="balance-section">
          <div class="balance-display">
            <div class="balance-amount">￥{{ userBalance.toFixed(2) }}</div>
            <el-button type="primary" @click="showRechargeDialog">充值</el-button>
          </div>
          <div class="balance-promotion">
            <el-tag type="success">活动：充值满100元送10元</el-tag>
          </div>
        </div>

        <!-- User Profile Section -->
        <el-divider content-position="center">基本信息</el-divider>
        <!-- Basic Information Form -->
        <div class="profile-form-container">
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="100px"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>

            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="profileForm.phone" />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                @click="updateProfile"
                :loading="updating"
              >
                更新基本信息
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-divider content-position="center">修改密码</el-divider>

        <!-- Password Change Form -->
        <div class="profile-form-container">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="120px"
          >
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input
                v-model="passwordForm.currentPassword"
                type="password"
                show-password
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
              />
              <div class="password-strength">
                <p>
                  密码强度:
                  <span :class="passwordStrengthClass">{{
                    passwordStrengthText
                  }}</span>
                </p>
                <el-progress
                  :percentage="passwordStrengthPercentage"
                  :color="passwordStrengthColor"
                ></el-progress>
                <p class="password-hint">
                  密码必须包含大小写字母、数字和特殊符号，长度至少8位
                </p>
              </div>
            </el-form-item>

            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                @click="updatePassword"
                :loading="updating"
              >
                更新密码
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>

    <!-- Recharge Dialog -->
    <el-dialog
      v-model="rechargeDialogVisible"
      title="账户充值"
      width="30%"
      :close-on-click-modal="false"
    >
      <el-form :model="rechargeForm" label-width="100px">
        <el-form-item label="充值金额">
          <el-input-number 
            v-model="rechargeForm.amount" 
            :min="1" 
            :precision="2" 
            :step="10"
            style="width: 100%"
          />
        </el-form-item>
        <div class="recharge-info">
          <el-alert
            title="充值满100元送10元"
            type="success"
            :closable="false"
            show-icon
          />
          <div class="bonus-info" v-if="rechargeBonusAmount > 0">
            <span>本次充值金额：￥{{ rechargeForm.amount.toFixed(2) }}</span>
            <span>赠送金额：￥{{ rechargeBonusAmount.toFixed(2) }}</span>
            <span class="total-amount">总计到账：￥{{ (rechargeForm.amount + rechargeBonusAmount).toFixed(2) }}</span>
          </div>
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rechargeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRecharge" :loading="recharging">
            确认充值
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { userApi } from '@/api';

const userStore = useUserStore();
const loading = ref(false);
const updating = ref(false);
const avatarInput = ref<HTMLInputElement | null>(null);
const profileFormRef = ref<FormInstance | null>(null);
const passwordFormRef = ref<FormInstance | null>(null);
const userBalance = ref(0);
const rechargeDialogVisible = ref(false);
const recharging = ref(false);

// Profile form
const profileForm = reactive({
  username: '',
  email: '',
  phone: '',
});

// Password form
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

// Recharge form
const rechargeForm = reactive({
  amount: 100
});

// Calculate bonus amount (10 per 100)
const rechargeBonusAmount = computed(() => {
  if (rechargeForm.amount >= 100) {
    return Math.floor(rechargeForm.amount / 100) * 10;
  }
  return 0;
});

// Password strength calculation
const hasLowerCase = computed(() => /[a-z]/.test(passwordForm.newPassword));
const hasUpperCase = computed(() => /[A-Z]/.test(passwordForm.newPassword));
const hasNumber = computed(() => /[0-9]/.test(passwordForm.newPassword));
const hasSpecialChar = computed(() =>
  /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(passwordForm.newPassword)
);
const isLongEnough = computed(() => passwordForm.newPassword.length >= 8);

const passwordStrength = computed(() => {
  const criteria = [
    hasLowerCase.value,
    hasUpperCase.value,
    hasNumber.value,
    hasSpecialChar.value,
    isLongEnough.value,
  ];
  return criteria.filter(Boolean).length;
});

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value;
  if (strength === 0) return '无';
  if (strength === 1) return '弱';
  if (strength === 2) return '弱';
  if (strength === 3) return '中';
  if (strength === 4) return '强';
  return '非常强';
});

const passwordStrengthClass = computed(() => {
  const strength = passwordStrength.value;
  if (strength <= 2) return 'weak';
  if (strength === 3) return 'medium';
  return 'strong';
});

const passwordStrengthPercentage = computed(() => {
  return passwordStrength.value * 20;
});

const passwordStrengthColor = computed(() => {
  const strength = passwordStrength.value;
  if (strength <= 2) return '#F56C6C';
  if (strength === 3) return '#E6A23C';
  if (strength === 4) return '#67C23A';
  return '#409EFF';
});

// Form validation rules
const profileRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度应为2-20个字符', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号格式',
      trigger: 'blur',
    },
  ],
});

// Password validation
const validatePassword = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else if (passwordStrength.value < 3) {
    callback(new Error('密码强度不足，请包含大小写字母、数字和特殊符号'));
  } else {
    if (passwordForm.confirmPassword !== '') {
      // Validate confirm password again if it was already filled
      if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        callback(new Error('两次输入的密码不一致'));
      }
    }
    callback();
  }
};

const validateConfirmPassword = (
  rule: any,
  value: string,
  callback: Function
) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const passwordRules = reactive({
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
});

// Fetch user balance
const fetchUserBalance = async () => {
  try {
    userBalance.value = await userApi.getBalance();
  } catch (error) {
    console.error('Error fetching balance:', error);
    ElMessage.error('获取账户余额失败');
  }
};

// Load user data
onMounted(async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    return;
  }

  loading.value = true;

  try {
    await userStore.fetchProfile();
    await fetchUserBalance();

    // Initialize form with user data - add additional null checks
    if (userStore.user) {
      // Use optional chaining and nullish coalescing to handle potential null/undefined values
      profileForm.username = userStore.user?.username ?? '';
      profileForm.email = userStore.user?.email ?? '';
      profileForm.phone = userStore.user?.phone ?? '';
    }
  } catch (error) {
    console.error('Fetch profile error:', error);
    ElMessage.error('获取个人信息失败');
  } finally {
    loading.value = false;
  }
});

// Show recharge dialog
const showRechargeDialog = () => {
  rechargeDialogVisible.value = true;
};

// Handle recharge
const handleRecharge = async () => {
  if (rechargeForm.amount <= 0) {
    ElMessage.warning('请输入有效的充值金额');
    return;
  }

  recharging.value = true;
  try {
    await userApi.recharge({
      amount: rechargeForm.amount
    });
    
    ElMessage.success('充值成功');
    rechargeDialogVisible.value = false;
    
    // Refresh balance
    await fetchUserBalance();
  } catch (error) {
    console.error('Recharge error:', error);
    ElMessage.error('充值过程中发生错误');
  } finally {
    recharging.value = false;
  }
};

// Update profile info
const updateProfile = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    return;
  }

  // Validate the form first
  if (!profileFormRef.value) return;
  const valid = await profileFormRef.value.validate().catch(() => false);
  if (!valid) {
    ElMessage.error('请正确填写表单');
    return;
  }

  updating.value = true;

  try {
    const success = await userStore.updateProfile({
      email: profileForm.email,
      phone: profileForm.phone,
    });

    if (success) {
      ElMessage.success('个人信息更新成功');
    } else {
      ElMessage.error('个人信息更新失败');
    }
  } catch (error) {
    console.error('Update profile error:', error);
    ElMessage.error('个人信息更新过程中发生错误');
  } finally {
    updating.value = false;
  }
};

// Update password
const updatePassword = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    return;
  }

  // Validate the form first
  if (!passwordFormRef.value) return;
  const valid = await passwordFormRef.value.validate().catch(() => false);
  if (!valid) {
    ElMessage.error('请正确填写表单');
    return;
  }

  updating.value = true;

  try {
    await userApi.updatePassword({
      oldPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword
    });

    ElMessage.success('密码更新成功');
    passwordForm.currentPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
  } catch (error) {
    console.error('Update password error:', error);
    ElMessage.error('密码更新过程中发生错误');
  } finally {
    updating.value = false;
  }
};

// Avatar functions
const triggerAvatarUpload = () => {
  if (avatarInput.value) {
    avatarInput.value.click();
  }
};

const handleAvatarChange = async (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (!target.files?.length) return;

  const file = target.files[0];
  // Validate file type
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件');
    return;
  }

  // Validate file size (max 2MB)
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB');
    return;
  }

  try {
    const success = await userStore.uploadAvatar(file);

    if (success) {
      ElMessage.success('头像上传成功');
    } else {
      ElMessage.error('头像上传失败');
    }
  } catch (error) {
    console.error('Upload avatar error:', error);
    ElMessage.error('头像上传过程中发生错误');
  } finally {
    // Reset file input
    target.value = '';
  }
};
</script>

<style scoped lang="scss">
.profile-container {
  .loading-container {
    padding: 20px 0;
  }

  .empty-data {
    padding: 40px 0;
    text-align: center;
  }

  .balance-section {
    max-width: 500px;
    margin: 20px auto;
    text-align: center;
    
    .balance-display {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 15px;
      
      .balance-amount {
        font-size: 28px;
        font-weight: bold;
        color: #409EFF;
        margin-right: 20px;
      }
    }
    
    .balance-promotion {
      margin-top: 10px;
    }
  }

  .bonus-info {
    margin-top: 10px;
    display: flex;
    flex-direction: column;
    gap: 5px;
    
    .total-amount {
      font-weight: bold;
      color: #67C23A;
      margin-top: 5px;
    }
  }

  .recharge-info {
    margin-top: 15px;
  }

  .user-profile-section {
    display: flex;
    align-items: center;
    padding: 20px;
    margin-bottom: 20px;

    .avatar-section {
      margin-right: 20px;

      .avatar-wrapper {
        position: relative;
        width: 100px;
        height: 100px;
        border-radius: 50%;
        background-color: #e0e0e0;
        overflow: hidden;
        cursor: pointer;

        .avatar-overlay {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          border-radius: 50%;
          background-color: rgba(0, 0, 0, 0.5);
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          color: white;
          opacity: 0;
          transition: opacity 0.3s;

          i {
            font-size: 24px;
            margin-bottom: 5px;
          }

          span {
            font-size: 12px;
          }
        }

        &:hover .avatar-overlay {
          opacity: 1;
        }
      }
    }

    .user-info {
      .profile-username {
        margin: 0 0 10px 0;
        font-size: 20px;
        color: #303133;
      }

      .profile-role {
        margin-bottom: 10px;
      }
    }
  }

  .profile-form-container {
    max-width: 500px;
    margin: 20px auto 30px;
    padding: 0 20px;

    .password-strength {
      margin-top: 8px;

      p {
        margin: 8px 0;
        font-size: 14px;
      }

      .weak {
        color: #f56c6c;
      }

      .medium {
        color: #e6a23c;
      }

      .strong {
        color: #67c23a;
      }

      .password-hint {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

@media (max-width: 768px) {
  .profile-container {
    .user-profile-section {
      flex-direction: column;
      text-align: center;

      .avatar-section {
        margin-right: 0;
        margin-bottom: 15px;
      }
    }
  }
}
</style>
