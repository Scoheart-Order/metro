<template>
  <div class="stop-schedule-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>到站发车时刻</h2>
        </div>
      </template>
      
      <!-- Search Form -->
      <div class="search-section">
        <el-form :model="searchForm" :inline="true">
          <el-form-item label="线路">
            <el-select
              v-model="searchForm.lineId"
              filterable
              placeholder="选择线路"
              style="width: 180px"
              @change="handleLineChange"
            >
              <el-option
                v-for="line in lines"
                :key="line.id"
                :label="line.name"
                :value="line.id"
              >
                <div class="line-option">
                  <div class="color-badge" :style="{ backgroundColor: line.color }"></div>
                  <span>{{ line.name }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="方向">
            <el-select
              v-model="searchForm.routeId"
              filterable
              placeholder="选择方向"
              style="width: 180px"
              :disabled="!searchForm.lineId"
              @change="handleRouteChange"
            >
              <el-option
                v-for="route in routes"
                :key="route.id"
                :label="route.name"
                :value="route.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="站点">
            <el-select
              v-model="searchForm.stationId"
              filterable
              placeholder="选择站点"
              style="width: 180px"
              :disabled="!searchForm.routeId"
            >
              <el-option
                v-for="station in stations"
                :key="station.id"
                :label="station.name"
                :value="station.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :loading="loading">
              查询
            </el-button>
            <el-button @click="resetSearch">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- Results Section -->
      <div class="results-section">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        
        <div v-else-if="!stopInfo" class="empty-data">
          <el-empty description="请选择线路、方向和站点进行查询" />
        </div>
        
        <div v-else class="schedule-card">
          <div class="station-info">
            <h3>{{ stopInfo.stationName }}</h3>
            <div class="line-info">
              <div class="color-badge" 
                   :style="{ backgroundColor: getLineColor(searchForm.lineId) }"></div>
              <span>{{ getLineName(searchForm.lineId) }}</span>
              <el-divider direction="vertical" />
              <span>{{ getRouteName(searchForm.routeId) }}</span>
            </div>
          </div>
          
          <div class="time-info">
            <div class="time-item">
              <div class="time-label">到站时间</div>
              <div class="time-value">{{ stopInfo.arrivalTime || '未设置' }}</div>
            </div>
            <div class="time-item">
              <div class="time-label">发车时间</div>
              <div class="time-value">{{ stopInfo.departureTime || '未设置' }}</div>
            </div>
          </div>
          
          <div class="station-position">
            <div class="position-label">站点序号</div>
            <div class="position-value">{{ stopInfo.seq }}</div>
          </div>
          
          <div v-if="stopInfo.isTransfer" class="transfer-info">
            <el-tag type="success">换乘站</el-tag>
          </div>
        </div>
        
        <!-- Route Timeline -->
        <div v-if="stopInfo" class="route-timeline">
          <h3>方向时刻表</h3>
          <el-timeline>
            <el-timeline-item
              v-for="stop in routeStops"
              :key="stop.id"
              :type="getTimelineItemType(stop, stopInfo.id)"
              :timestamp="getTimeDisplay(stop)"
            >
              <div class="timeline-content">
                <div class="station-name">{{ stop.stationName }}</div>
                <div v-if="stop.isTransfer" class="transfer-tag">
                  <el-tag size="small" type="success">换乘站</el-tag>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { metroApi } from '../../api/modules/metro';
import type { Line, Route, Station, Stop } from '../../api/modules/metro';

// 数据状态
const loading = ref(false);
const lines = ref<Line[]>([]);
const routes = ref<Route[]>([]);
const stations = ref<Station[]>([]);
const routeStops = ref<Stop[]>([]);
const stopInfo = ref<Stop | null>(null);

// 搜索表单
const searchForm = reactive({
  lineId: 0,
  routeId: 0,
  stationId: 0
});

// 加载所有线路
const loadLines = async () => {
  try {
    lines.value = await metroApi.getAllLines();
  } catch (error) {
    console.error('获取线路数据失败', error);
    ElMessage.error('获取线路数据失败');
  }
};

// 根据线路加载方向
const loadRoutesByLine = async (lineId: number) => {
  if (!lineId) return;
  
  try {
    routes.value = await metroApi.getRoutesByLineId(lineId);
  } catch (error) {
    console.error('获取方向数据失败', error);
    ElMessage.error('获取方向数据失败');
  }
};

// 加载方向的所有停靠点
const loadStopsByRoute = async (routeId: number) => {
  if (!routeId) return;
  
  try {
    const stops = await metroApi.getStopsByRouteId(routeId);
    routeStops.value = stops.sort((a, b) => a.seq - b.seq);
    
    // 提取出所有站点ID
    const stationIds = stops.map(stop => stop.stationId);
    
    // 获取这些站点的详细信息
    const stationsList = await Promise.all(
      stationIds.map(id => metroApi.getStationById(id))
    );
    
    stations.value = stationsList;
  } catch (error) {
    console.error('获取停靠点数据失败', error);
    ElMessage.error('获取停靠点数据失败');
  }
};

// 获取特定停靠点信息
const getStopInfo = async (routeId: number, stationId: number) => {
  if (!routeId || !stationId) return null;
  
  try {
    loading.value = true;
    
    // 查找方向的所有停靠点
    const stops = await metroApi.getStopsByRouteId(routeId);
    
    // 找到匹配站点的停靠点
    const stop = stops.find(s => s.stationId === stationId);
    
    if (!stop) {
      ElMessage.warning('未找到该站点的停靠信息');
      return null;
    }
    
    return stop;
  } catch (error) {
    console.error('获取停靠点信息失败', error);
    ElMessage.error('获取停靠点信息失败');
    return null;
  } finally {
    loading.value = false;
  }
};

// 事件处理器
const handleLineChange = async (lineId: number) => {
  searchForm.routeId = 0;
  searchForm.stationId = 0;
  routes.value = [];
  stations.value = [];
  stopInfo.value = null;
  
  if (lineId) {
    await loadRoutesByLine(lineId);
  }
};

const handleRouteChange = async (routeId: number) => {
  searchForm.stationId = 0;
  stations.value = [];
  stopInfo.value = null;
  
  if (routeId) {
    await loadStopsByRoute(routeId);
  }
};

const handleSearch = async () => {
  if (!searchForm.lineId || !searchForm.routeId || !searchForm.stationId) {
    ElMessage.warning('请选择线路、方向和站点');
    return;
  }
  
  loading.value = true;
  
  try {
    const stop = await getStopInfo(searchForm.routeId, searchForm.stationId);
    stopInfo.value = stop;
    
    if (!stopInfo.value) {
      ElMessage.warning('未找到该站点的停靠信息');
    }
  } catch (error) {
    console.error('查询失败', error);
    ElMessage.error('查询失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const resetSearch = () => {
  searchForm.lineId = 0;
  searchForm.routeId = 0;
  searchForm.stationId = 0;
  routes.value = [];
  stations.value = [];
  stopInfo.value = null;
  routeStops.value = [];
};

// 辅助函数
const getLineColor = (lineId: number): string => {
  const line = lines.value.find(l => l.id === lineId);
  return line?.color || '#909399';
};

const getLineName = (lineId: number): string => {
  const line = lines.value.find(l => l.id === lineId);
  return line?.name || '';
};

const getRouteName = (routeId: number): string => {
  const route = routes.value.find(r => r.id === routeId);
  return route?.name || '';
};

const getTimelineItemType = (stop: Stop, currentStopId: number): string => {
  return stop.id === currentStopId ? 'primary' : '';
};

const getTimeDisplay = (stop: Stop): string => {
  const arrival = stop.arrivalTime || '--:--';
  const departure = stop.departureTime || '--:--';
  return `${arrival} - ${departure}`;
};

// 生命周期
onMounted(async () => {
  await loadLines();
});
</script>

<style scoped lang="scss">
.stop-schedule-container {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h2 {
      margin: 0;
      font-size: 1.5rem;
      color: #303133;
    }
  }
  
  .search-section {
    margin-bottom: 20px;
    background-color: #f5f7fa;
    padding: 20px;
    border-radius: 4px;
  }
  
  .line-option {
    display: flex;
    align-items: center;
  }
  
  .color-badge {
    display: inline-block;
    width: 16px;
    height: 16px;
    border-radius: 3px;
    margin-right: 8px;
    vertical-align: middle;
  }
  
  .results-section {
    .loading-container {
      padding: 20px 0;
    }
    
    .empty-data {
      padding: 40px 0;
    }
    
    .schedule-card {
      background-color: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      padding: 20px;
      margin-bottom: 20px;
      
      .station-info {
        margin-bottom: 20px;
        
        h3 {
          margin: 0 0 10px 0;
          font-size: 1.5rem;
          color: #303133;
        }
        
        .line-info {
          display: flex;
          align-items: center;
          color: #606266;
        }
      }
      
      .time-info {
        display: flex;
        margin-bottom: 20px;
        gap: 20px;
        
        .time-item {
          flex: 1;
          background-color: #f5f7fa;
          padding: 15px;
          border-radius: 4px;
          
          .time-label {
            font-size: 14px;
            color: #909399;
            margin-bottom: 5px;
          }
          
          .time-value {
            font-size: 24px;
            font-weight: bold;
            color: #303133;
          }
        }
      }
      
      .station-position {
        margin-bottom: 16px;
        
        .position-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 5px;
        }
        
        .position-value {
          font-size: 18px;
          color: #303133;
        }
      }
      
      .transfer-info {
        margin-top: 10px;
      }
    }
    
    .route-timeline {
      margin-top: 30px;
      
      h3 {
        margin-bottom: 16px;
        color: #303133;
      }
      
      .timeline-content {
        display: flex;
        align-items: center;
        
        .station-name {
          font-weight: 500;
          margin-right: 10px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .stop-schedule-container {
    .search-section {
      .el-form {
        display: flex;
        flex-direction: column;
        
        .el-form-item {
          margin-right: 0;
          margin-bottom: 15px;
        }
      }
    }
    
    .results-section {
      .schedule-card {
        .time-info {
          flex-direction: column;
        }
      }
    }
  }
}
</style> 