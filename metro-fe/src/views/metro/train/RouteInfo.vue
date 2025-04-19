<template>
  <div class="route-info-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>列车线路信息</h2>
        </div>
      </template>

      <!-- Filter Section -->
      <div class="filter-section">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="线路">
            <el-select v-model="filterForm.lineId" clearable placeholder="全部线路" style="width: 130px">
              <el-option
                v-for="line in metroStore.lines"
                :key="line.id"
                :label="line.name"
                :value="line.id"
              >
                <div class="line-option">
                  <div class="line-color" :style="{ backgroundColor: line.color }"></div>
                  <span>{{ line.name }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="起点站">
            <el-select v-model="filterForm.startStationId" clearable placeholder="全部起点站" style="width: 150px">
              <el-option
                v-for="station in metroStore.stations"
                :key="station.id"
                :label="station.name"
                :value="station.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="终点站">
            <el-select v-model="filterForm.endStationId" clearable placeholder="全部终点站" style="width: 150px">
              <el-option
                v-for="station in metroStore.stations"
                :key="station.id"
                :label="station.name"
                :value="station.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="行驶方向">
            <el-input v-model="filterForm.routeName" clearable placeholder="输入行驶方向关键词" style="width: 180px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- Routes Table -->
      <el-table :data="filteredRoutes" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column
          prop="name"
          label="行驶方向"
          width="150"
          align="center"
        />
        <el-table-column label="所属线路" width="150" align="center">
          <template #default="scope">
            <div class="line-badge">
              <div class="line-color" :style="{ backgroundColor: getLineColor(scope.row.lineId) }"></div>
              <span>{{ getLineName(scope.row.lineId) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="起点站" align="center" min-width="120">
          <template #default="scope">
            {{ getStationName(scope.row.startStationId) }}
          </template>
        </el-table-column>
        <el-table-column label="终点站" align="center" min-width="120">
          <template #default="scope">
            {{ getStationName(scope.row.endStationId) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { useMetroStore } from '@/stores/metro';
import type { Route } from '@/api/modules/metro';

const metroStore = useMetroStore();
const router = useRouter();
const loading = ref(true);

// Filter form
const filterForm = reactive({
  lineId: null as number | null,
  startStationId: null as number | null,
  endStationId: null as number | null,
  routeName: ''
});

// Computed property for filtered routes
const filteredRoutes = computed(() => {
  return metroStore.routes.filter(route => {
    // Filter by line
    if (filterForm.lineId !== null && route.lineId !== filterForm.lineId) {
      return false;
    }
    
    // Filter by start station
    if (filterForm.startStationId !== null && route.startStationId !== filterForm.startStationId) {
      return false;
    }
    
    // Filter by end station
    if (filterForm.endStationId !== null && route.endStationId !== filterForm.endStationId) {
      return false;
    }
    
    // Filter by route name
    if (filterForm.routeName && !route.name.toLowerCase().includes(filterForm.routeName.toLowerCase())) {
      return false;
    }
    
    return true;
  });
});

// Reset filters
const resetFilter = () => {
  filterForm.lineId = null;
  filterForm.startStationId = null;
  filterForm.endStationId = null;
  filterForm.routeName = '';
};

// Get station name by ID
const getStationName = (stationId: number) => {
  const station = metroStore.stations.find((s) => s.id === stationId);
  return station?.name || '-';
};

// Get line name by ID
const getLineName = (lineId: number) => {
  const line = metroStore.lines.find((l) => l.id === lineId);
  return line?.name || '-';
};

// Get line color by ID
const getLineColor = (lineId: number) => {
  const line = metroStore.lines.find((l) => l.id === lineId);
  return line?.color || '#909399';
};

// Load data on component mount
onMounted(async () => {
  loading.value = true;
  try {
    // Fetch all required data in parallel
    await Promise.all([
      metroStore.fetchLines(),
      metroStore.fetchStations(),
      metroStore.fetchRoutes(),
    ]);
  } catch (error) {
    console.error('Failed to fetch metro data:', error);
    ElMessage.error('获取路线信息失败');
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="scss">
.route-info-container {
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
  
  .filter-section {
    margin-bottom: 20px;
    
    .filter-form {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      
      .el-form-item {
        margin-right: 10px;
        margin-bottom: 10px;
      }
    }
  }
  
  .line-badge {
    display: flex;
    align-items: center;
  }
  
  .line-color {
    display: inline-block;
    width: 16px;
    height: 16px;
    border-radius: 3px;
    margin-right: 8px;
  }
  
  .line-option {
    display: flex;
    align-items: center;
  }
}
</style>
