<template>
  <div class="user-management-container">
    <h1 class="page-title">系统用户管理</h1>

    <div class="control-panel">
      <el-input
        v-model="searchQuery"
        placeholder="搜索用户名或姓名"
        class="search-input"
        clearable
        @clear="handleSearchClear"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>添加用户
      </el-button>
    </div>

    <el-table
      :data="filteredUsers"
      style="width: 100%"
      :row-class-name="tableRowClassName"
      v-loading="loading"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="phone" label="电话" width="120" />
      <el-table-column label="角色" width="120">
        <template #default="scope">
          <el-tag :type="getRoleType(scope.row.roles)">
            {{ getRoleText(scope.row.roles) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.enabled)">
            {{ getStatusText(scope.row.enabled) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <el-button
            size="small"
            :type="scope.row.enabled ? 'danger' : 'success'"
            @click="handleToggleStatus(scope.row)"
          >
            {{ scope.row.enabled ? '禁用' : '启用' }}
          </el-button>
          <el-button
            size="small"
            type="warning"
            @click="handleResetPassword(scope.row)"
          >
            重置密码
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="totalUsers"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="50%"
    >
      <el-form
        :model="userForm"
        label-width="120px"
        :rules="rules"
        ref="userFormRef"
      >
        <el-form-item label="用户名" prop="username" :disabled="isEdit">
          <el-input v-model="userForm.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select
            v-model="userForm.role"
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option label="普通用户" value="ROLE_USER" />
            <el-option label="普通管理员" value="ROLE_ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="userForm.status"
            placeholder="请选择状态"
            style="width: 100%"
          >
            <el-option label="正常" value="true" />
            <el-option label="禁用" value="false" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="确认密码" prop="confirmPassword">
          <el-input
            v-model="userForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="resetPasswordDialogVisible"
      title="重置密码"
      width="40%"
    >
      <el-form
        :model="resetPasswordForm"
        label-width="120px"
        :rules="resetPasswordRules"
        ref="resetPasswordFormRef"
      >
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="resetPasswordForm.password"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="resetPasswordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false"
            >取消</el-button
          >
          <el-button type="primary" @click="submitResetPassword"
            >确认</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus } from '@element-plus/icons-vue';
import {
  adminApi,
  type AdminUserCreateDto,
  type AdminUserUpdateDto,
} from '../../api/modules/admin';
import type { User } from '../../api/modules/user';
import { ROLE_NAMES } from '../../constants/roles';

// 用户数据
const users = ref<User[]>([]);
const loading = ref(true);
const currentPage = ref(1);
const pageSize = ref(10);
const totalUsers = ref(0);
const searchQuery = ref('');

// 分页和搜索过滤后的用户数据
const filteredUsers = computed(() => {
  let filtered = users.value;

  if (searchQuery.value) {
    filtered = users.value.filter(
      (user) =>
        user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        (user.realName &&
          user.realName.toLowerCase().includes(searchQuery.value.toLowerCase()))
    );
  }

  totalUsers.value = filtered.length;

  return filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  );
});

// 对话框控制
const dialogVisible = ref(false);
const isEdit = ref(false);
const userFormRef = ref(null);
const resetPasswordDialogVisible = ref(false);
const resetPasswordFormRef = ref(null);
const currentUser = ref<User | null>(null);

// 用户表单
const userForm = reactive({
  id: 0,
  username: '',
  realName: '',
  email: '',
  phone: '',
  role: 'ROLE_USER',
  status: 'true',
  password: '',
  confirmPassword: '',
});

// 重置密码表单
const resetPasswordForm = reactive({
  userId: 0,
  password: '',
  confirmPassword: '',
});

// 验证两次密码输入是否一致
const validatePasswordConfirmation = (
  rule: any,
  value: string,
  callback: any
) => {
  if (value !== userForm.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

// 验证重置密码是否一致
const validateResetPasswordConfirmation = (
  rule: any,
  value: string,
  callback: any
) => {
  if (value !== resetPasswordForm.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '长度在 4 到 20 个字符', trigger: 'blur' },
  ],
  realName: [{ required: false, message: '请输入姓名', trigger: 'blur' }],
  email: [
    { required: false, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur',
    },
  ],
  role: [{ required: false, message: '请选择角色', trigger: 'change' }],
  status: [{ required: false, message: '请选择状态', trigger: 'change' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 30, message: '长度在 6 到 30 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePasswordConfirmation, trigger: 'blur' },
  ],
};

// 重置密码表单验证规则
const resetPasswordRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 30, message: '长度在 6 到 30 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateResetPasswordConfirmation, trigger: 'blur' },
  ],
};

// 生命周期钩子
onMounted(async () => {
  await fetchUsers();
});

// 方法
const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await adminApi.getAllUsers();
    users.value = response;
    totalUsers.value = response.length;
  } catch (error) {
    console.error('获取用户数据失败', error);
    ElMessage.error('获取用户数据失败');
  } finally {
    loading.value = false;
  }
};

const getRoleType = (
  roles: { id: number; name: string; description: string }[]
) => {
  if (!roles || roles.length === 0) return 'info';

  if (roles.some((role) => role.name === ROLE_NAMES.SUPER_ADMIN)) {
    return 'danger';
  } else if (roles.some((role) => role.name === ROLE_NAMES.ADMIN)) {
    return 'warning';
  } else {
    return 'info';
  }
};

const getRoleText = (
  roles: { id: number; name: string; description: string }[]
) => {
  if (!roles || roles.length === 0) return '未知角色';

  if (roles.some((role) => role.name === ROLE_NAMES.SUPER_ADMIN)) {
    return '系统管理员';
  } else if (roles.some((role) => role.name === ROLE_NAMES.ADMIN)) {
    return '普通管理员';
  } else {
    return '普通用户';
  }
};

const getStatusType = (enabled: boolean) => {
  return enabled ? 'success' : 'info';
};

const getStatusText = (enabled: boolean) => {
  return enabled ? '正常' : '禁用';
};

const tableRowClassName = ({ row }: { row: User }) => {
  if (!row.enabled) {
    return 'disabled-row';
  }
  return '';
};

const handleSearchClear = () => {
  currentPage.value = 1;
  searchQuery.value = '';
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
};

const resetForm = () => {
  userForm.id = 0;
  userForm.username = '';
  userForm.realName = '';
  userForm.email = '';
  userForm.phone = '';
  userForm.role = 'ROLE_USER';
  userForm.status = 'true';
  userForm.password = '';
  userForm.confirmPassword = '';
};

const openAddDialog = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: User) => {
  isEdit.value = true;

  userForm.id = row.id;
  userForm.username = row.username;
  userForm.realName = row.realName || '';
  userForm.email = row.email || '';
  userForm.phone = row.phone || '';

  // 设置角色
  if (row.roles && row.roles.length > 0) {
    if (row.roles.some((role) => role.name === ROLE_NAMES.SUPER_ADMIN)) {
      userForm.role = ROLE_NAMES.SUPER_ADMIN;
    } else if (row.roles.some((role) => role.name === ROLE_NAMES.ADMIN)) {
      userForm.role = ROLE_NAMES.ADMIN;
    } else {
      userForm.role = ROLE_NAMES.USER;
    }
  } else {
    userForm.role = ROLE_NAMES.USER;
  }

  userForm.status = row.enabled ? 'true' : 'false';

  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!userFormRef.value) return;

  (userFormRef.value as any).validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;

      try {
        if (isEdit.value) {
          // 更新用户
          const updateData: AdminUserUpdateDto = {
            realName: userForm.realName,
            email: userForm.email,
            phone: userForm.phone,
            role: userForm.role,
            status: userForm.status,
          };

          await adminApi.updateUser(userForm.id, updateData);
          ElMessage.success('用户信息更新成功');
        } else {
          // 创建用户
          const createData: AdminUserCreateDto = {
            username: userForm.username,
            password: userForm.password,
            realName: userForm.realName,
            email: userForm.email,
            phone: userForm.phone,
            role: userForm.role,
            status: userForm.status,
          };

          await adminApi.createUser(createData);
          ElMessage.success('用户添加成功');
        }

        // 刷新用户列表
        await fetchUsers();
        dialogVisible.value = false;
      } catch (error) {
        console.error('保存用户数据失败', error);
        ElMessage.error('保存用户数据失败');
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleToggleStatus = (row: User) => {
  const actionText = row.enabled ? '禁用' : '启用';

  ElMessageBox.confirm(
    `确定要${actionText}用户 ${row.username} 吗？`,
    `确认${actionText}`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: row.enabled ? 'warning' : 'info',
    }
  )
    .then(async () => {
      try {
        loading.value = true;
        await adminApi.toggleUserStatus(row.id);
        ElMessage.success(`用户${actionText}成功`);
        await fetchUsers();
      } catch (error) {
        console.error(`${actionText}用户失败`, error);
        ElMessage.error(`${actionText}用户失败`);
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const handleResetPassword = (row: User) => {
  currentUser.value = row;
  resetPasswordForm.userId = row.id;
  resetPasswordForm.password = '';
  resetPasswordForm.confirmPassword = '';
  resetPasswordDialogVisible.value = true;
};

const submitResetPassword = async () => {
  if (!resetPasswordFormRef.value) return;

  (resetPasswordFormRef.value as any).validate(async (valid: boolean) => {
    if (valid) {
      try {
        loading.value = true;
        await adminApi.resetPassword(
          resetPasswordForm.userId,
          resetPasswordForm.password
        );
        ElMessage.success('密码重置成功');
        resetPasswordDialogVisible.value = false;
      } catch (error) {
        console.error('重置密码失败', error);
        ElMessage.error('重置密码失败');
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.user-management-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.control-panel {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}

.search-input {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 自定义表格行样式 */
:deep(.disabled-row) {
  --el-table-tr-bg-color: rgba(240, 240, 240, 0.3);
}
</style>
