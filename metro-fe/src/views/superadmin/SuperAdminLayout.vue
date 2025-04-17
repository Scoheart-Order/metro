<template>
  <div class="superadmin-layout">
    <el-container>
      <el-aside
        :width="isCollapse ? '64px' : '240px'"
        class="sidebar-container"
      >
        <div class="sidebar">
          <div class="logo">
            <h2 v-if="!isCollapse">超级管理系统</h2>
            <h2 v-else>超管</h2>
          </div>
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            :collapse="isCollapse"
            router
            background-color="#001529"
            text-color="#fff"
            active-text-color="#409EFF"
          >
            <el-menu-item index="/superadmin/user-management">
              <el-icon><el-icon-user /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
          </el-menu>
        </div>
      </el-aside>

      <el-container
        class="main-container"
        :style="{ marginLeft: isCollapse ? '64px' : '240px' }"
      >
        <el-header>
          <div class="header-content">
            <div class="left">
              <el-icon class="toggle-btn" @click="toggleSidebar">
                <el-icon-fold v-if="!isCollapse" />
                <el-icon-expand v-else />
              </el-icon>
            </div>
            <div class="right">
              <el-button type="danger" size="small" plain @click="handleLogout">
                退出登录
              </el-button>
            </div>
          </div>
        </el-header>

        <el-main>
          <router-view v-slot="{ Component }">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import {
  User as ElIconUser,
  Fold as ElIconFold,
  Expand as ElIconExpand,
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const isCollapse = ref(false);

const activeMenu = computed(() => route.path);

onMounted(async () => {
  console.log('SuperAdmin layout mounted, checking authentication...');

  // Check if logged in via token existence only
  if (!userStore.isLoggedIn) {
    console.log('Not logged in, redirecting to login');
    router.push('/login');
    return;
  }

  // Check authorization using centralized role checking
  const isSuperAdmin = await checkSuperAdminRole();
  if (!isSuperAdmin) {
    console.log('User is not a superadmin, redirecting to appropriate page');
    // Use basic route redirection based on roles without using full profile info
    if (userStore.token) {
      router.push('/admin');
    } else {
      router.push('/');
    }
    return;
  }
});

// Watch route changes to enforce access restrictions
watch(
  () => route.path,
  () => {
    // Only run this if user is authenticated
    if (!userStore.isLoggedIn) {
      return;
    }
  }
);

// Minimal role check without loading full profile
async function checkSuperAdminRole() {
  // If we already have the user loaded with roles, use store getter
  if (userStore.user) {
    return userStore.isSuperAdmin;
  }

  try {
    const profileLoaded = await userStore.fetchProfile();
    if (!profileLoaded) {
      return false;
    }
    return userStore.isSuperAdmin;
  } catch (error) {
    console.error('Error checking superadmin role:', error);
    return false;
  }
}

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value;
};

const handleLogout = () => {
  userStore.logout();
  router.push('/login');
};
</script>

<style scoped lang="scss">
.superadmin-layout {
  min-height: 100vh;

  .sidebar-container {
    background-color: #001529;
    color: #fff;
    height: 100vh;
    position: fixed;
    left: 0;
    top: 0;
    transition: width 0.3s;
    overflow: hidden;
    z-index: 1000;

    .sidebar {
      height: 100%;
      width: 240px;

      .logo {
        height: 60px;
        line-height: 60px;
        text-align: center;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        overflow: hidden;
        white-space: nowrap;

        h2 {
          margin: 0;
          color: #fff;
          font-size: 1.3rem;
          transition: all 0.3s;
        }
      }

      .el-menu {
        border-right: none;
        height: calc(100% - 60px);
      }
    }
  }

  .main-container {
    transition: margin-left 0.3s;

    .el-header {
      background-color: #fff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      position: sticky;
      top: 0;
      z-index: 999;

      .header-content {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 100%;

        .left {
          .toggle-btn {
            font-size: 20px;
            cursor: pointer;
          }
        }

        .right {
          .superadmin-badge {
            margin-right: 10px;
          }

          .user-info {
            display: flex;
            align-items: center;
            cursor: pointer;

            span {
              margin-left: 8px;
            }
          }
        }
      }
    }

    .el-main {
      padding: 20px;
      background-color: #f5f7fa;
      min-height: calc(100vh - 60px);
    }
  }
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 240px;
  min-height: 400px;
}

.el-menu--collapse {
  width: 64px;
}
</style>
