<template>
  <div class="home-container">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <h1>便捷出行，智慧地铁</h1>
        <p>查询地铁时刻表、线路信息，让您的出行更加便捷</p>
      </div>
    </div>

    <!-- Information Cards Section -->
    <div class="info-section">
      <el-row :gutter="30">
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="info-card">
            <template #header>
              <div class="card-header">
                <i class="el-icon-time"></i>
                <span>到站发车时刻</span>
              </div>
            </template>
            <div class="card-content">
              <p>查询各站点的列车到站与发车时刻表，帮助您合理安排出行时间</p>
              <el-button
                type="primary"
                plain
                @click="$router.push('/train-info')"
              >
                查看列车时刻
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="info-card">
            <template #header>
              <div class="card-header">
                <i class="el-icon-map-location"></i>
                <span>列车线路信息</span>
              </div>
            </template>
            <div class="card-content">
              <p>查询地铁线路详情和站点信息，了解各条线路的运行情况</p>
              <el-button
                type="primary"
                plain
                @click="$router.push('/route-info')"
              >
                查看线路地图
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="info-card">
            <template #header>
              <div class="card-header">
                <i class="el-icon-chat-line-square"></i>
                <span>用户反馈</span>
              </div>
            </template>
            <div class="card-content">
              <p>提交您的使用体验和建议，帮助我们提升服务质量</p>
              <el-button
                type="primary"
                plain
                @click="$router.push('/feedback')"
              >
                提交反馈
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Recent Announcements -->
    <div class="announcements-section">
      <h2>最新公告</h2>
      <div v-if="announcementStore.loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <div
        v-else-if="announcementStore.announcements.length === 0"
        class="empty-data"
      >
        <el-empty description="暂无公告" />
      </div>

      <el-timeline v-else>
        <el-timeline-item
          v-for="announcement in announcementStore.announcements"
          :key="announcement.id"
          :timestamp="announcement.createdAt"
          type="primary"
        >
          <el-card>
            <h3>{{ announcement.title }}</h3>
            <p>{{ announcement.content }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useTrainStore } from '../stores/train';
import { useAnnouncementStore } from '../stores/announcement';
import { ElMessage } from 'element-plus';

const router = useRouter();
const trainStore = useTrainStore();
const announcementStore = useAnnouncementStore();

// Station options (would come from API in real app)
const stationOptions = ref([
  { value: 'station1', label: '中心站' },
  { value: 'station2', label: '北站' },
  { value: 'station3', label: '南站' },
  { value: 'station4', label: '东站' },
  { value: 'station5', label: '西站' },
  { value: 'station6', label: '机场站' },
  // More stations would be loaded from API
]);

// Search form
const searchForm = reactive({
  startStation: '',
  endStation: '',
  date: new Date(),
});

// Load data on component mount
onMounted(async () => {
  // Load announcements from API
  try {
    await announcementStore.fetchAnnouncements();
  } catch (error) {
    console.error('Failed to load announcements:', error);
    ElMessage.error('获取公告失败，请刷新页面重试');
  }

  // In a real app, we would also load stations from API
  // await loadStations()
});

// Search function
const handleSearch = async () => {
  if (!searchForm.startStation || !searchForm.endStation) {
    ElMessage.warning('请选择出发站和到达站');
    return;
  }

  try {
    await trainStore.searchTrains({
      startStation: searchForm.startStation,
      endStation: searchForm.endStation,
      date: searchForm.date.toISOString().split('T')[0],
    });

    // Navigate to train info page with search parameters
    router.push({
      path: '/train-info',
      query: {
        startStation: searchForm.startStation,
        endStation: searchForm.endStation,
        date: searchForm.date.toISOString().split('T')[0],
      },
    });
  } catch (error) {
    console.error('Search error:', error);
    ElMessage.error('查询失败，请稍后重试');
  }
};
</script>

<style scoped lang="scss">
.home-container {
  .hero-section {
    background-image: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)),
      url('../assets/metro-bg.jpg');
    background-size: cover;
    background-position: center;
    color: white;
    padding: 80px 20px;
    text-align: center;
    border-radius: 4px;
    margin-bottom: 40px;

    .hero-content {
      max-width: 800px;
      margin: 0 auto;

      h1 {
        font-size: 2.5rem;
        margin-bottom: 16px;
      }

      p {
        font-size: 1.2rem;
        margin-bottom: 30px;
      }

      .search-box {
        background: rgba(255, 255, 255, 0.9);
        padding: 30px;
        border-radius: 8px;

        .search-form {
          .search-button {
            width: 100%;
            height: 50px;
            font-size: 16px;
          }
        }
      }
    }
  }

  .info-section {
    margin-bottom: 40px;

    .info-card {
      height: 100%;
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-5px);
      }

      .card-header {
        display: flex;
        align-items: center;

        i {
          margin-right: 8px;
          font-size: 20px;
        }

        span {
          font-size: 18px;
          font-weight: bold;
        }
      }

      .card-content {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        height: 120px;

        p {
          margin-bottom: 20px;
          color: #606266;
        }
      }
    }
  }

  .announcements-section {
    h2 {
      margin-bottom: 20px;
      font-size: 1.8rem;
      color: #303133;
      position: relative;
      padding-left: 15px;

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

    .loading-container {
      padding: 20px 0;
    }

    .empty-data {
      padding: 40px 0;
      text-align: center;
    }
  }
}

@media (max-width: 768px) {
  .home-container {
    .hero-section {
      padding: 40px 10px;

      .hero-content {
        h1 {
          font-size: 2rem;
        }
      }
    }

    .info-section {
      .el-col {
        margin-bottom: 20px;
      }
    }
  }
}
</style>
