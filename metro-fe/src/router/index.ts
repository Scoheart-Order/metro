import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '../stores/user';
import { ElMessage } from 'element-plus';
import { ROLE_NAMES } from '../constants/roles';

// 定义路由元数据类型
interface CustomRouteMeta {
  requiresAuth?: boolean; // 是否需要登录
  requiredRoles?: string[]; // 所需角色列表 (如 ['ADMIN', 'SUPER_ADMIN'])
  title?: string; // 页面标题
}

// 扩展Vue Router的RouteMeta类型
declare module 'vue-router' {
  interface RouteMeta extends CustomRouteMeta {}
}

// 辅助函数：检查当前用户是否有所需角色
function userHasRequiredRoles(
  userStore: ReturnType<typeof useUserStore>,
  requiredRoles: string[]
): boolean {
  if (!requiredRoles || requiredRoles.length === 0) return true;

  // 检查用户是否拥有任一所需角色
  return requiredRoles.some((role) => userStore.hasRole(role));
}

// 按模块划分路由
const routes = [
  // 前台页面路由 - metro模块
  {
    path: '/metro',
    component: () => import(/* webpackChunkName: "layout" */ '../views/metro/Layout.vue'),
    meta: { requiresAuth: false, title: '地铁系统' },
    children: [
      {
        path: '',
        name: 'MetroHome',
        component: () => import(/* webpackChunkName: "metro-home" */ '../views/metro/Home.vue'),
        meta: { requiresAuth: false, title: '首页' },
      },
      {
        path: 'train-info',
        name: 'TrainInfo',
        component: () => import(/* webpackChunkName: "train-info" */ '../views/metro/train/TrainInfo.vue'),
        meta: { requiresAuth: false, title: '车次信息' },
      },
      {
        path: 'route-info',
        name: 'RouteInfo',
        component: () => import(/* webpackChunkName: "route-info" */ '../views/metro/train/RouteInfo.vue'),
        meta: { requiresAuth: false, title: '路线信息' },
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import(/* webpackChunkName: "feedback" */ '../views/metro/feedback/Feedback.vue'),
        meta: { requiresAuth: false, title: '反馈' },
      },
      {
        path: 'request',
        name: 'Request',
        component: () => import(/* webpackChunkName: "request" */ '../views/metro/feedback/Request.vue'),
        meta: { requiresAuth: true, title: '需求提交' },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import(/* webpackChunkName: "profile" */ '../views/metro/user/Profile.vue'),
        meta: { requiresAuth: true, title: '个人中心' },
      },
    ],
  },

  // 管理后台路由 - admin模块
  {
    path: '/admin',
    component: () => import(/* webpackChunkName: "admin-layout" */ '../views/admin/AdminLayout.vue'),
    meta: {
      requiresAuth: true,
      requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
      title: '管理后台',
    },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import(/* webpackChunkName: "admin-home" */ '../views/admin/AdminHome.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '管理首页',
        },
      },
      {
        path: 'line-management',
        name: 'LineManagement',
        component: () => import(/* webpackChunkName: "line-management" */ '../views/admin/LineManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '线路管理',
        },
      },
      {
        path: 'station-management',
        name: 'StationManagement',
        component: () => import(/* webpackChunkName: "station-management" */ '../views/admin/StationManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '站点管理',
        },
      },
      {
        path: 'route-management',
        name: 'RouteManagement',
        component: () => import(/* webpackChunkName: "route-management" */ '../views/admin/RouteManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '路线管理',
        },
      },
      {
        path: 'stop-management',
        name: 'StopManagement',
        component: () => import(/* webpackChunkName: "stop-management" */ '../views/admin/StopManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '停站管理',
        },
      },
      {
        path: 'feedback-management',
        name: 'FeedbackManagement',
        component: () => import(/* webpackChunkName: "feedback-management" */ '../views/admin/FeedbackManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '反馈管理',
        },
      },
      {
        path: 'request-management',
        name: 'RequestManagement',
        component: () => import(/* webpackChunkName: "request-management" */ '../views/admin/RequestManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '需求管理',
        },
      },
      {
        path: 'announcement-management',
        name: 'AnnouncementManagement',
        component: () => import(/* webpackChunkName: "announcement-management" */ '../views/admin/AnnouncementManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '公告管理',
        },
      },
      {
        path: 'train-trip-management',
        name: 'TrainTripManagement',
        component: () => import(/* webpackChunkName: "train-trip-management" */ '../views/admin/TrainTripManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '列车行程管理',
        },
      },
      {
        path: 'stop-time-management/:id',
        name: 'StopTimeManagement',
        component: () => import(/* webpackChunkName: "stop-time-management" */ '../views/admin/StopTimeManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.ADMIN, ROLE_NAMES.SUPER_ADMIN],
          title: '时刻表管理',
        },
      },
    ],
  },

  // 超级管理员路由 - superadmin模块
  {
    path: '/superadmin',
    component: () => import(/* webpackChunkName: "superadmin-layout" */ '../views/superadmin/SuperAdminLayout.vue'),
    meta: {
      requiresAuth: true,
      requiredRoles: [ROLE_NAMES.SUPER_ADMIN],
      title: '超级管理后台',
    },
    children: [
      {
        path: '',
        name: 'SuperAdminHome',
        redirect: '/superadmin/user-management',
        meta: { requiresAuth: true, requiredRoles: [ROLE_NAMES.SUPER_ADMIN] },
      },
      {
        path: 'user-management',
        name: 'SuperAdminUserManagement',
        component: () => import(/* webpackChunkName: "user-management" */ '../views/superadmin/UserManagement.vue'),
        meta: {
          requiresAuth: true,
          requiredRoles: [ROLE_NAMES.SUPER_ADMIN],
          title: '用户管理',
        },
      },
    ],
  },

  // 认证相关路由 - 独立页面
  {
    path: '/login',
    name: 'Login',
    component: () => import(/* webpackChunkName: "login" */ '../views/auth/Login.vue'),
    meta: { requiresAuth: false, title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import(/* webpackChunkName: "register" */ '../views/auth/Register.vue'),
    meta: { requiresAuth: false, title: '注册' },
  },

  // 根路径重定向到metro
  {
    path: '/',
    redirect: '/metro',
  },

  // 404 Not Found 路由
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import(/* webpackChunkName: "not-found" */ '../views/NotFound.vue'),
    meta: { requiresAuth: false, title: '404 Not Found' },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - Metro系统` : 'Metro系统';

  // 获取用户状态
  const userStore = useUserStore();
  const hasToken = !!localStorage.getItem('token');

  // 是否需要登录验证
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);

  // 获取所需角色列表 (合并路由链上所有requiredRoles)
  const requiredRoles: string[] = [];
  to.matched.forEach((record) => {
    if (record.meta.requiredRoles) {
      record.meta.requiredRoles.forEach((role) => {
        if (!requiredRoles.includes(role)) {
          requiredRoles.push(role);
        }
      });
    }
  });

  // 不需要登录的页面直接通过
  if (!requiresAuth) {
    next();
    return;
  }

  // 需要登录但没有token，跳转登录页
  if (requiresAuth && !hasToken) {
    ElMessage.warning('请先登录');
    next({ path: '/login', query: { redirect: to.fullPath } });
    return;
  }

  // 确保用户信息已加载
  if (hasToken && !userStore.user) {
    try {
      await userStore.fetchProfile();
    } catch (error) {
      // 加载用户信息失败，清除token并重定向到登录页
      userStore.logout();
      ElMessage.error('登录状态已过期，请重新登录');
      next({ path: '/login', query: { redirect: to.fullPath } });
      return;
    }
  }

  // 检查角色权限
  if (
    requiredRoles.length > 0 &&
    !userHasRequiredRoles(userStore, requiredRoles)
  ) {
    ElMessage.error(`权限不足，需要 ${requiredRoles.join(' 或 ')} 角色`);
    next({ path: '/metro' });
    return;
  }

  // 通过所有验证，继续访问
  next();
});

export default router;
