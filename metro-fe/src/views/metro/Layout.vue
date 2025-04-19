<template>
  <div class="layout-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <div class="logo">
            <h1>城市地铁便民服务</h1>
          </div>
          <div class="nav-menu">
            <el-menu
              mode="horizontal"
              :ellipsis="false"
              :default-active="activeRoute"
              router
            >
              <el-menu-item index="/metro">首页</el-menu-item>
              <el-sub-menu index="/metro/train">
                <template #title>地铁信息</template>
                <el-menu-item index="/metro/route-info">线路查询</el-menu-item>
                <el-menu-item index="/metro/train-info">时刻查询</el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="/metro/feedback">
                <template #title>评价与需求</template>
                <el-menu-item index="/metro/feedback">发表评价</el-menu-item>
                <el-menu-item index="/metro/request">提交需求</el-menu-item>
              </el-sub-menu>
            </el-menu>
          </div>
          <div class="user-area">
            <template v-if="userStore.isLoggedIn">
              <el-dropdown trigger="click">
                <div class="user-profile">
                  <!-- <el-avatar :src="userStore.user?.avatar || defaultAvatar" size="small"></el-avatar> -->
                  <span>{{ userStore.user?.username }}</span>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="$router.push('/metro/profile')"
                      >个人信息</el-dropdown-item
                    >
                    <el-dropdown-item divided @click="handleLogout"
                      >退出登录</el-dropdown-item
                    >
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button type="primary" @click="$router.push('/login')"
                >登录</el-button
              >
              <el-button @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>

      <el-footer>
        <div class="footer-content">
          <p>© {{ currentYear }} 城市地铁便民服务信息系统 | 版权所有</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
// import defaultAvatar from '../assets/default-avatar.png'

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const currentYear = new Date().getFullYear();

const activeRoute = computed(() => route.path);

onMounted(async () => {
  // Redirect to login if not authenticated
  if (!userStore.isLoggedIn) {
    router.push('/login');
    return;
  }

  // Fetch user profile if logged in
  await userStore.fetchProfile();
});

const handleLogout = () => {
  userStore.logout();
  router.push('/login');
};
</script>

<style scoped lang="scss">
.layout-container {
  min-height: 100vh;

  .el-header {
    background-color: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    position: sticky;
    top: 0;
    z-index: 1000;

    .header-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 100%;

      .logo {
        h1 {
          font-size: 1.5rem;
          margin: 0;
          color: #409eff;
        }
      }

      .nav-menu {
        flex: 1;
        margin-left: 40px;
      }

      .user-area {
        display: flex;
        align-items: center;
        gap: 10px;

        .user-profile {
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
    min-height: calc(100vh - 120px);
  }

  .el-footer {
    height: 60px;
    background-color: #fff;
    border-top: 1px solid #e4e7ed;

    .footer-content {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #909399;
    }
  }
}
</style>
