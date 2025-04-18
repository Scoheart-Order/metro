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

        <div class="stops-list" v-loading="stopsLoading">
          <el-empty v-if="routeStops.length === 0" description="暂无停靠点" />
          <el-card v-for="stop in routeStops" :key="stop.id" class="stop-item">
            <div class="stop-info">
              <div class="stop-seq">{{ stop.seq }}</div>
              <div class="stop-content">
                <div class="stop-name">{{ stop.stationName }}</div>
                <div class="stop-code">{{ stop.stationCode }}</div>
                <div class="stop-time">
                  <div class="time-item">
                    <span class="time-label">到站时间:</span>
                    <span class="time-value">{{ stop.arrivalTime || '未设置' }}</span>
                  </div>
                  <div class="time-item">
                    <span class="time-label">发车时间:</span>
                    <span class="time-value">{{ stop.departureTime || '未设置' }}</span>
                  </div>
                </div>
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
          </el-card>
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
        <el-form-item label="到站时间" prop="arrivalTime">
          <el-time-picker
            v-model="stopForm.arrivalTime"
            format="HH:mm"
            placeholder="选择到站时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="发车时间" prop="departureTime">
          <el-time-picker
            v-model="stopForm.departureTime"
            format="HH:mm"
            placeholder="选择发车时间"
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
  seq: 1,
  arrivalTime: null,
  departureTime: null,
});
const stopRules = {
  stationId: [{ required: true, message: '请选择站点', trigger: 'change' }],
  seq: [{ required: true, message: '请输入顺序', trigger: 'change' }],
};
const stopFormRef = ref<FormInstance | null>(null);

// 可选站点列表 - 排除已添加的站点、起始站和终点站
const availableStations = computed(() => {
  if (!selectedRoute.value) return [];
  
  // 获取所有站点
  const allStations = metroStore.stations;
  
  // 获取当前方向的起始站和终点站ID
  const startStationId = selectedRoute.value.startStationId;
  const endStationId = selectedRoute.value.endStationId;
  
  // 过滤掉已经添加到当前方向的站点，以及起始站和终点站
  return allStations.filter(
    (station) => !routeStops.value.some(stop => stop.stationId === station.id) && 
              station.id !== startStationId && 
              station.id !== endStationId
  );
});

// 生命周期钩子
onMounted(async () => {
  loading.value = true;
  // 并行加载数据提高效率
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

// 加载方向的停靠点
const fetchRouteStops = async (routeId: number) => {
  stopsLoading.value = true;
  try {
    // 使用metroApi获取方向的停靠点
    const stops = await metroApi.getStopsByRouteId(routeId);

    // 转换为组件内部使用的格式
    routeStops.value = stops.map((stop) => {
      // 获取站点信息
      const station = metroStore.getStationById(stop.stationId);

      return {
        ...stop,
        stationCode: station?.code || '',
        stationName: station?.name || '未知站点',
      };
    });

    // 按顺序排序
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
    seq: routeStops.value.length + 1,
    arrivalTime: null,
    departureTime: null,
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
        
        // 使用metroApi删除停靠点
        await metroApi.deleteStop(row.id);
        
        // 从本地数据中删除
        routeStops.value = routeStops.value.filter(stop => stop.id !== row.id);
        
        // 重新排序
        routeStops.value.forEach((stop, index) => {
          stop.seq = index + 1;
        });
        
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
        
        // 查找选择的站点信息
        const station = metroStore.getStationById(stopForm.value.stationId);
        
        if (!station) {
          ElMessage.error('所选站点不存在');
          stopsLoading.value = false;
          return;
        }

        try {
          // 格式化时间为HH:mm格式
          const formattedData = {
            ...stopForm.value,
            arrivalTime: stopForm.value.arrivalTime ? formatTime(stopForm.value.arrivalTime) : null,
            departureTime: stopForm.value.departureTime ? formatTime(stopForm.value.departureTime) : null,
          };
          
          let newStop;
          
          if (isEditMode.value && formattedData.id) {
            // 使用metroApi更新停靠点
            newStop = await metroApi.updateStop(formattedData.id, formattedData);
            
            // 更新本地数据
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
            // 使用metroApi创建新停靠点
            newStop = await metroApi.createStop(formattedData);
            
            // 添加到本地数据
            const newStopData: StopDisplay = {
              ...newStop,
              stationName: station.name,
              stationCode: station.code || '',
            };
            
            routeStops.value.push(newStopData);
            ElMessage.success('停靠点添加成功');
          }
          
          // 重新排序
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

// 辅助函数 - 格式化时间为HH:mm格式
const formatTime = (time: any): string => {
  if (!time) return '';
  
  if (typeof time === 'string') {
    // 如果已经是格式化的字符串，直接返回
    if (time.match(/^\d{2}:\d{2}$/)) return time;
    
    // 尝试解析ISO格式的日期字符串
    const date = new Date(time);
    if (!isNaN(date.getTime())) {
      return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }
  } else if (time instanceof Date) {
    return `${String(time.getHours()).padStart(2, '0')}:${String(time.getMinutes()).padStart(2, '0')}`;
  }
  
  return '';
};

// 打开编辑停靠点对话框
const handleEditStop = (row: StopDisplay) => {
  if (!selectedRoute.value) return;
  
  stopForm.value = {
    id: row.id,
    routeId: selectedRoute.value.id,
    stationId: row.stationId,
    seq: row.seq,
    arrivalTime: row.arrivalTime,
    departureTime: row.departureTime,
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
}

.stops-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.stops-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 60vh;
  overflow-y: auto;
}

.stop-item {
  margin-bottom: 10px;
}

.stop-info {
  display: flex;
  align-items: center;
}

.stop-seq {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 36px;
  height: 36px;
  background-color: #409EFF;
  color: white;
  border-radius: 50%;
  font-weight: bold;
  margin-right: 16px;
}

.stop-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.stop-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stop-code {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.stop-time {
  margin-top: 8px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.time-item {
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 4px 8px;
  min-width: 120px;
}

.time-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.time-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.stop-actions {
  margin-left: auto;
}

.tag-item {
  margin-top: 8px;
}

.action-btn {
  margin-left: 8px;
}
</style>
