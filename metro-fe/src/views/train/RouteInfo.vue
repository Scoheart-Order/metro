<template>
  <div class="route-info-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>列车线路信息</h2>
        </div>
      </template>

      <!-- Routes Table -->
      <el-table :data="routes" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column
          prop="name"
          label="行驶方向"
          width="150"
          align="center"
        />
        <el-table-column label="所属线路" width="150" align="center">
          <template #default="scope">
            {{ getLineName(scope.row.lineId) }}
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
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { useMetroStore } from '../../stores/metro';
import type { Route } from '../../api/modules/metro';

const metroStore = useMetroStore();
const router = useRouter();
const loading = ref(true);

// Computed property for routes
const routes = computed(() => {
  return metroStore.routes || [];
});

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
}
</style>
