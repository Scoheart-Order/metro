<template>
  <div class="admin-home">
    <h2 class="admin-title">管理员控制台</h2>

    <!-- Statistics Cards -->
    <div class="stat-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-card-icon blue">
                <i class="el-icon-train"></i>
              </div>
              <div class="stat-card-data">
                <div class="stat-card-value">{{ stats.trainCount }}</div>
                <div class="stat-card-label">列车数量</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-card-icon green">
                <i class="el-icon-map-location"></i>
              </div>
              <div class="stat-card-data">
                <div class="stat-card-value">{{ stats.routeCount }}</div>
                <div class="stat-card-label">线路数量</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-card-icon orange">
                <i class="el-icon-chat-line-square"></i>
              </div>
              <div class="stat-card-data">
                <div class="stat-card-value">{{ stats.feedbackCount }}</div>
                <div class="stat-card-label">未处理评价</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-card-icon red">
                <i class="el-icon-document-checked"></i>
              </div>
              <div class="stat-card-data">
                <div class="stat-card-value">{{ stats.requestCount }}</div>
                <div class="stat-card-label">未处理需求</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Quick Access -->
    <div class="quick-access">
      <h3>快捷操作</h3>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="action-card">
            <div
              class="action-card-content"
              @click="$router.push('/admin/train-management')"
            >
              <i class="el-icon-plus"></i>
              <span>发布列车信息</span>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="action-card">
            <div
              class="action-card-content"
              @click="$router.push('/admin/route-management')"
            >
              <i class="el-icon-plus"></i>
              <span>发布线路信息</span>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="action-card">
            <div
              class="action-card-content"
              @click="$router.push('/admin/feedback-management')"
            >
              <i class="el-icon-message"></i>
              <span>处理用户评价</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Recent Activity -->
    <div class="recent-activity">
      <h3>最近活动</h3>
      <el-card shadow="hover">
        <el-timeline>
          <el-timeline-item
            v-for="(activity, index) in activities"
            :key="index"
            :timestamp="activity.time"
            :type="activity.type"
          >
            <div class="activity-content">
              <span class="activity-user">{{ activity.user }}</span>
              <span class="activity-action">{{ activity.action }}</span>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <!-- System Status -->
    <div class="system-status">
      <h3>系统状态</h3>
      <el-card shadow="hover">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="系统版本">V1.0.0</el-descriptions-item>
          <el-descriptions-item label="服务器状态">
            <el-tag type="success">正常</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="数据库状态">
            <el-tag type="success">正常</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="在线用户数">128</el-descriptions-item>
          <el-descriptions-item label="系统更新">
            <el-tag type="info">无可用更新</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最近备份"
            >2023-04-15 03:00</el-descriptions-item
          >
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useUserStore } from '../../stores/user';
import { useTrainStore } from '../../stores/train';
import { useFeedbackStore } from '../../stores/feedback';
import { ElMessage } from 'element-plus';

const userStore = useUserStore();
const trainStore = useTrainStore();
const feedbackStore = useFeedbackStore();

// Mock statistics data
const stats = ref({
  trainCount: 0,
  routeCount: 0,
  feedbackCount: 0,
  requestCount: 0,
});

// Mock activities data
const activities = ref([
  {
    user: '管理员A',
    action: '发布了新的列车时刻信息',
    time: '2023-04-15 15:30',
    type: 'primary',
  },
  {
    user: '管理员B',
    action: '回复了用户评价',
    time: '2023-04-15 14:20',
    type: 'success',
  },
  {
    user: '管理员A',
    action: '处理了用户需求',
    time: '2023-04-15 10:15',
    type: 'warning',
  },
  {
    user: '系统管理员',
    action: '更新了系统配置',
    time: '2023-04-14 16:45',
    type: 'info',
  },
  {
    user: '管理员C',
    action: '发布了1号线临时调整公告',
    time: '2023-04-14 09:30',
    type: 'primary',
  },
]);

onMounted(async () => {
  // Check if user is admin
  if (!userStore.isLoggedIn || !userStore.isAdmin) {
    ElMessage.error('您没有权限访问管理页面');
    return;
  }

  // Load dashboard data
  await loadDashboardData();
});

// Load dashboard data
const loadDashboardData = async () => {
  try {
    // In a real app, we would make API calls to get the data
    // For now, we'll use mock data

    // Load trains and routes
    await trainStore.fetchTrains();
    await trainStore.fetchRoutes();

    // Load feedback and requests
    await feedbackStore.fetchFeedbacks();
    await feedbackStore.fetchRequests();

    // Update statistics
    stats.value = {
      trainCount: trainStore.trains.length,
      routeCount: trainStore.routes.length,
      feedbackCount: feedbackStore.feedbacks.filter(
        (f) => !f.replies || f.replies.length === 0
      ).length,
      requestCount: feedbackStore.requests.filter((r) => r.status === 'pending')
        .length,
    };
  } catch (error) {
    console.error('Load dashboard data error:', error);
    ElMessage.error('加载数据失败');
  }
};
</script>

<style scoped lang="scss">
.admin-home {
  padding: 0 10px;

  .admin-title {
    margin: 0 0 20px 0;
    font-size: 24px;
    color: #303133;
  }

  .stat-cards,
  .quick-access,
  .recent-activity,
  .system-status {
    margin-bottom: 30px;
  }

  h3 {
    margin-bottom: 15px;
    font-size: 18px;
    color: #303133;
    position: relative;
    padding-left: 12px;

    &:before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      height: 70%;
      width: 4px;
      background-color: #409eff;
      border-radius: 2px;
    }
  }

  .stat-card {
    height: 100%;

    .stat-card-content {
      display: flex;
      align-items: center;

      .stat-card-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;

        i {
          font-size: 30px;
          color: white;
        }

        &.blue {
          background-color: #409eff;
        }

        &.green {
          background-color: #67c23a;
        }

        &.orange {
          background-color: #e6a23c;
        }

        &.red {
          background-color: #f56c6c;
        }
      }

      .stat-card-data {
        .stat-card-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
          line-height: 1;
          margin-bottom: 5px;
        }

        .stat-card-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }

  .action-card {
    cursor: pointer;
    transition: transform 0.3s;

    &:hover {
      transform: translateY(-5px);
    }

    .action-card-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px;

      i {
        font-size: 30px;
        color: #409eff;
        margin-bottom: 10px;
      }

      span {
        font-size: 16px;
        color: #303133;
      }
    }
  }

  .activity-content {
    .activity-user {
      font-weight: bold;
      margin-right: 5px;
    }

    .activity-action {
      color: #606266;
    }
  }
}

@media (max-width: 768px) {
  .admin-home {
    .el-col {
      margin-bottom: 15px;
    }
  }
}
</style>
