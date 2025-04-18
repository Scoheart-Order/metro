<template>
  <div class="stop-management-container">
    <h1 class="page-title">停靠点管理</h1>

    <el-table
      :data="metroStore.routes"
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="所属线路" width="150">
        <template #default="scope">
          <div class="line-option">
            <div
              class="color-badge"
              :style="{ backgroundColor: getLineColor(scope.row.lineId) }"
            ></div>
            <span>{{ getLineName(scope.row.lineId) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="方向名称" width="180" />
      <el-table-column label="起始站" width="150">
        <template #default="scope">
          {{ getStationName(scope.row.startStationId) }}
        </template>
      </el-table-column>
      <el-table-column label="终点站" width="150">
        <template #default="scope">
          {{ getStationName(scope.row.endStationId) }}
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            @click="handleManageStops(scope.row)"
          >
            管理停靠点
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 停靠点管理对话框 -->
    <el-dialog v-model="stopsDialogVisible" title="停靠点管理" width="60%">
      <div v-if="selectedRoute" class="stops-container">
        <div class="stops-header">
          <h3>{{ selectedRoute.name }} 停靠点列表</h3>
          <el-button type="primary" @click="openAddStopDialog">
            <el-icon><Plus /></el-icon>添加停靠点
          </el-button>
        </div>

        <div class="stops-timeline" v-loading="stopsLoading">
          <el-empty v-if="routeStops.length === 0" description="暂无停靠点" />
          
          <el-timeline v-else>
            <el-timeline-item
              v-for="stop in routeStops"
              :key="stop.id"
              :timestamp="getStationDetails(stop)"
              :type="getTimelineItemType(stop.seq)"
              :color="getTimelineItemColor(stop.seq)"
            >
              <div class="timeline-content">
                <div class="timeline-header">
                  <div class="station-info">
                    <span class="station-seq">{{ stop.seq }}</span>
                    <span class="station-name">{{ stop.stationName }}</span>
                    <span class="station-code">({{ stop.stationCode }})</span>
                  </div>
                  <div class="stop-actions">
                    <el-button type="primary" size="small" @click="handleEditStop(stop)" class="action-btn">
                      编辑
                    </el-button>
                    <el-button type="danger" size="small" @click="handleDeleteStop(stop)" class="action-btn">
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-dialog>

    <!-- 添加/编辑停靠点对话框 -->
    <el-dialog
      v-model="addStopDialogVisible"
      :title="isEditMode ? '编辑停靠点' : '添加停靠点'"
      width="40%"
    >
      <el-form
        :model="stopForm"
        label-width="100px"
        :rules="stopRules"
        ref="stopFormRef"
      >
        <el-form-item label="站点" prop="stationId">
          <el-select
            v-model="stopForm.stationId"
            placeholder="选择站点"
            style="width: 100%"
            filterable
            :disabled="isEditMode"
          >
            <el-option
              v-for="station in availableStations"
              :key="station.id"
              :label="station.name"
              :value="station.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="顺序" prop="seq">
          <el-input-number
            v-model="stopForm.seq"
            :min="1"
            :max="routeStops.length + (isEditMode ? 0 : 1)"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addStopDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStopForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { useMetroStore } from '../../stores/metro';
import { metroApi } from '../../api/modules/metro';
import type { Route, Stop, StopDto } from '../../api/modules/metro';

// 定义组件内部使用的Stop接口，包含额外字段
interface StopDisplay extends Stop {
  stationCode: string;
  stationName: string;
}

// 基础数据
const metroStore = useMetroStore();
const loading = ref(true);
const stopsDialogVisible = ref(false);
const addStopDialogVisible = ref(false);
const selectedRoute = ref<Route | null>(null);
const routeStops = ref<StopDisplay[]>([]);
const stopsLoading = ref(false);
const isEditMode = ref(false);

// 停靠点表单
const stopForm = ref<StopDto>({
  routeId: 0,
  stationId: 0,
  seq: 1
});
const stopRules = {
  stationId: [{ required: true, message: '请选择站点', trigger: 'change' }],
  seq: [{ required: true, message: '请输入顺序', trigger: 'change' }],
};
const stopFormRef = ref<FormInstance | null>(null);

// 可选站点列表 - 排除已添加的站点、起始站和终点站
const availableStations = computed(() => {
  if (!selectedRoute.value) return [];
  
  const allStations = metroStore.stations;
  const startStationId = selectedRoute.value.startStationId;
  const endStationId = selectedRoute.value.endStationId;
  
  return allStations.filter(
    (station) => !routeStops.value.some(stop => stop.stationId === station.id) && 
              station.id !== startStationId && 
              station.id !== endStationId
  );
});

// 生命周期钩子
onMounted(async () => {
  loading.value = true;
  await Promise.all([
    metroStore.fetchLines(),
    metroStore.fetchStations(),
    metroStore.fetchRoutes(),
  ]);
  loading.value = false;
});

// 获取线路颜色
const getLineColor = (lineId: number) => {
  const line = metroStore.lines.find((l) => l.id === lineId);
  return line ? line.color : '#909399';
};

// 获取线路名称
const getLineName = (lineId: number) => {
  const line = metroStore.lines.find((l) => l.id === lineId);
  return line ? line.name : '';
};

// 获取站点名称
const getStationName = (stationId: number) => {
  const station = metroStore.stations.find((s) => s.id === stationId);
  return station ? station.name : '';
};

// 获取站点详情
const getStationDetails = (stop: StopDisplay) => {
  return `站点编码: ${stop.stationCode}`;
};

// 获取时间线项类型
const getTimelineItemType = (seq: number) => {
  const types = ['primary', 'success', 'warning', 'danger'];
  return types[seq % types.length];
};

// 获取时间线项颜色
const getTimelineItemColor = (seq: number) => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399'];
  return colors[seq % colors.length];
};

// 加载方向的停靠点
const fetchRouteStops = async (routeId: number) => {
  stopsLoading.value = true;
  try {
    const stops = await metroApi.getStopsByRouteId(routeId);

    routeStops.value = stops.map((stop) => {
      const station = metroStore.getStationById(stop.stationId);

      return {
        ...stop,
        stationCode: station?.code || '',
        stationName: station?.name || '未知站点',
      };
    });

    routeStops.value.sort((a, b) => a.seq - b.seq);
  } catch (error) {
    console.error('获取停靠点数据失败', error);
    ElMessage.error('获取停靠点数据失败');
  } finally {
    stopsLoading.value = false;
  }
};

// 打开停靠点管理对话框
const handleManageStops = async (row: Route) => {
  selectedRoute.value = row;
  await fetchRouteStops(row.id);
  stopsDialogVisible.value = true;
};

// 打开添加停靠点对话框
const openAddStopDialog = () => {
  if (!selectedRoute.value) return;
  
  stopForm.value = {
    id: null,
    routeId: selectedRoute.value.id,
    stationId: 0,
    seq: routeStops.value.length + 1
  };
  isEditMode.value = false;
  addStopDialogVisible.value = true;
};

// 删除停靠点
const handleDeleteStop = (row: StopDisplay) => {
  ElMessageBox.confirm(`确定要删除 ${row.stationName} 停靠点吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        stopsLoading.value = true;
        await metroApi.deleteStop(row.id);
        routeStops.value = routeStops.value.filter(stop => stop.id !== row.id);
        
        // 重新排序
        let seq = 1;
        for (const stop of routeStops.value.sort((a, b) => a.seq - b.seq)) {
          stop.seq = seq++;
        }
        
        ElMessage.success('停靠点删除成功');
      } catch (error) {
        console.error('删除停靠点失败', error);
        ElMessage.error('删除停靠点失败');
      } finally {
        stopsLoading.value = false;
      }
    })
    .catch(() => {
      // 取消删除操作
    });
};

// 提交添加停靠点表单
const submitStopForm = async () => {
  if (!stopFormRef.value || !selectedRoute.value) return;

  try {
    await stopFormRef.value.validate(async (valid: boolean) => {
      if (valid) {
        stopsLoading.value = true;
        const station = metroStore.getStationById(stopForm.value.stationId);
        
        if (!station) {
          ElMessage.error('所选站点不存在');
          stopsLoading.value = false;
          return;
        }

        try {
          const formattedData = {
            ...stopForm.value
          };
          
          let newStop;
          
          if (isEditMode.value && formattedData.id) {
            newStop = await metroApi.updateStop(formattedData.id, formattedData);
            
            const index = routeStops.value.findIndex(stop => stop.id === formattedData.id);
            if (index !== -1) {
              routeStops.value[index] = {
                ...newStop,
                stationName: station.name,
                stationCode: station.code || '',
              };
            }
            
            ElMessage.success('停靠点更新成功');
          } else {
            newStop = await metroApi.createStop(formattedData);
            
            const newStopData: StopDisplay = {
              ...newStop,
              stationName: station.name,
              stationCode: station.code || '',
            };
            
            routeStops.value.push(newStopData);
            ElMessage.success('停靠点添加成功');
          }
          
          routeStops.value.sort((a, b) => a.seq - b.seq);
          addStopDialogVisible.value = false;
        } catch (error) {
          console.error('保存停靠点数据失败', error);
          ElMessage.error('保存停靠点数据失败');
        }
      } else {
        ElMessage.error('表单验证失败');
      }
    });
  } catch (error) {
    console.error('保存停靠点数据失败', error);
    ElMessage.error('保存停靠点数据失败');
  } finally {
    stopsLoading.value = false;
  }
};

// 打开编辑停靠点对话框
const handleEditStop = (row: StopDisplay) => {
  if (!selectedRoute.value) return;
  
  stopForm.value = {
    id: row.id,
    routeId: selectedRoute.value.id,
    stationId: row.stationId,
    seq: row.seq
  };
  isEditMode.value = true;
  addStopDialogVisible.value = true;
};
</script>

<style scoped>
.stop-management-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
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

.stops-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stops-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stops-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.stops-timeline {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0 10px;
}

.timeline-content {
  padding: 10px;
  background-color: #f7f7f7;
  border-radius: 4px;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.station-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.station-seq {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 28px;
  height: 28px;
  background-color: #409EFF;
  color: white;
  border-radius: 50%;
  font-weight: bold;
}

.station-name {
  font-size: 16px;
  font-weight: bold;
}

.station-code {
  font-size: 12px;
  color: #909399;
}

.stop-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
}
</style>
