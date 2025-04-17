import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '../stores/user';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../views/Layout.vue'),
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('../views/Home.vue'),
        },
        {
          path: 'train-info',
          name: 'TrainInfo',
          component: () => import('../views/train/TrainInfo.vue'),
        },
        {
          path: 'route-info',
          name: 'RouteInfo',
          component: () => import('../views/train/RouteInfo.vue'),
        },
        {
          path: 'feedback',
          name: 'Feedback',
          component: () => import('../views/feedback/Feedback.vue'),
        },
        {
          path: 'request',
          name: 'Request',
          component: () => import('../views/feedback/Request.vue'),
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('../views/user/Profile.vue'),
        },
      ],
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/auth/Login.vue'),
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/auth/Register.vue'),
    },
    {
      path: '/reset-password',
      name: 'ResetPassword',
      component: () => import('../views/auth/ResetPassword.vue'),
    },
    {
      path: '/admin',
      component: () => import('../views/admin/AdminLayout.vue'),
      children: [
        {
          path: '',
          name: 'AdminHome',
          component: () => import('../views/admin/AdminHome.vue'),
        },
        {
          path: 'train-management',
          name: 'TrainManagement',
          component: () => import('../views/admin/TrainManagement.vue'),
        },
        {
          path: 'line-management',
          name: 'LineManagement',
          component: () => import('../views/admin/LineManagement.vue'),
        },
        {
          path: 'station-management',
          name: 'StationManagement',
          component: () => import('../views/admin/StationManagement.vue'),
        },
        {
          path: 'route-management',
          name: 'RouteManagement',
          component: () => import('../views/admin/RouteManagement.vue'),
        },
        {
          path: 'stop-management',
          name: 'StopManagement',
          component: () => import('../views/admin/StopManagement.vue'),
        },
        {
          path: 'feedback-management',
          name: 'FeedbackManagement',
          component: () => import('../views/admin/FeedbackManagement.vue'),
        },
        {
          path: 'request-management',
          name: 'RequestManagement',
          component: () => import('../views/admin/RequestManagement.vue'),
        },
        {
          path: 'announcement-management',
          name: 'AnnouncementManagement',
          component: () => import('../views/admin/AnnouncementManagement.vue'),
          meta: {
            requiresAuth: true,
            requiresAdmin: true,
          },
        },
      ],
    },
    {
      path: '/superadmin',
      component: () => import('../views/superadmin/SuperAdminLayout.vue'),
      children: [
        {
          path: '',
          name: 'SuperAdminHome',
          redirect: '/superadmin/user-management',
        },
        {
          path: 'user-management',
          name: 'SuperAdminUserManagement',
          component: () => import('../views/superadmin/UserManagement.vue'),
        },
      ],
    },
  ],
});

// Define public routes as a constant for reuse
const publicRoutes = ['/login', '/register', '/reset-password'];

// Navigation guard to check authentication
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token');

  // Check if the route is public (doesn't require authentication)
  const isPublicRoute = publicRoutes.some((route) => to.path === route);

  // For public routes, allow access without authentication
  if (isPublicRoute) {
    next();
    return;
  }

  // If no token, redirect to login for all non-public routes
  if (!token) {
    next('/login');
    return;
  }

  // For authenticated users, just proceed to the requested route
  // Role-based access control will be handled by the layout components
  // using the centralized store getters
  next();
});

export default router;
