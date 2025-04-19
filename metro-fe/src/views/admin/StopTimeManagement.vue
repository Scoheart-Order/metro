<template>
  <div class="stop-time-management-container">
    <h1 class="page-title">时刻表管理</h1>

    <div class="train-trip-info" v-if="trainTrip">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="列车车次">
          {{ trainTrip.trainNumber }}
        </el-descriptions-item>
        <el-descriptions-item label="所属线路">
          <div class="line-badge" v-if="trainTrip.routeId && getRouteLineId(trainTrip.routeId)">
            <div
              class="color-badge"
              :style="{ backgroundColor: getLineColor(getRouteLineId(trainTrip.routeId)) }"
            ></div>
            <span>{{ getLineName(getRouteLineId(trainTrip.routeId)) }}</span>
          </div>
          <span v-else>未知线路</span>
        </el-descriptions-item>
        <el-descriptions-item label="行驶方向">
          {{ getRouteName(trainTrip.routeId) }}
        </el-descriptions-item>
        <el-descriptions-item label="运行日期">
          {{ formatDate(trainTrip.runDate) }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="control-panel">
      <div class="buttons">
        <el-button type="primary" @click="openAddDialog" :disabled="!routeStops.length || !availableStops.length">
          <el-icon><Plus /></el-icon>添加时刻
        </el-button>
        <el-button @click="goBack">
          <el-icon><Back /></el-icon>返回列车行程
        </el-button>
      </div>
    </div>

    <el-table
      :data="sortedStopTimes"
      style="width: 100%"
      v-loading="loading"
      empty-text="暂无时刻表数据"
    >
      <el-table-column prop="stopSeq" label="顺序" width="80" sortable />
      <el-table-column label="停靠站" min-width="180">
        <template #default="scope">
          {{ getStopStationName(scope.row.stopId) }}
        </template>
      </el-table-column>
      <el-table-column label="到站时间" width="180">
        <template #default="scope">
          {{ scope.row.arrivalTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="发车时间" width="180">
        <template #default="scope">
          {{ scope.row.departureTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleEdit(scope.row)" title="编辑时刻">
            编辑
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.row)"
            title="删除时刻"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑时刻对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑时刻' : '添加时刻'"
      width="50%"
      destroy-on-close
    >
      <el-form
        :model="stopTimeForm"
        label-width="120px"
        :rules="rules"
        ref="stopTimeFormRef"
      >
        <el-form-item label="停靠站" prop="stopId">
          <el-select
            v-model="stopTimeForm.stopId"
            placeholder="选择停靠站"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="stop in availableStops"
              :key="stop.id"
              :label="getStopStationName(stop.id)"
              :value="stop.id"
            >
              <div>
                <span>{{ getStopStationName(stop.id) }}</span>
                <span style="color: #999; margin-left: 10px">序号: {{ stop.seq }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="到站时间" prop="arrivalTime">
          <el-time-picker
            v-model="stopTimeForm.arrivalTime"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="选择到站时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="发车时间" prop="departureTime">
          <el-time-picker
            v-model="stopTimeForm.departureTime"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="选择发车时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import { Plus, Back } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';
import { useMetroStore } from '../../stores/metro';
import type { TrainTrip, StopTime, StopTimeDto, Stop } from '../../api/modules/metro';
import { format, parseISO } from 'date-fns';

const route = useRoute();
const router = useRouter();
const metroStore = useMetroStore();

// 基础数据
const loading = ref(true);
const trainTripId = computed(() => Number(route.params.id));
const trainTrip = ref<TrainTrip | null>(null);
const routeStops = ref<Stop[]>([]);

// 已分配给当前行程的停靠点ID
const usedStopIds = computed(() => {
  return stopTimes.value.map(st => st.stopId);
});

// 可用于分配的停靠点(排除已分配的)
const availableStops = computed(() => {
  if (isEdit.value) {
    // 编辑模式下，当前编辑的停靠站也可选
    return routeStops.value.filter(
      stop => !usedStopIds.value.includes(stop.id) || stop.id === currentEditingStopId.value
    );
  }
  return routeStops.value.filter(stop => !usedStopIds.value.includes(stop.id));
});

// 当前行程的所有时刻
const stopTimes = ref<StopTime[]>([]);

// 按照顺序排列的时刻
const sortedStopTimes = computed(() => {
  return [...stopTimes.value].sort((a, b) => {
    // 首先按照停靠点的顺序排序
    const stopA = routeStops.value.find(s => s.id === a.stopId);
    const stopB = routeStops.value.find(s => s.id === b.stopId);
    
    if (stopA && stopB) {
      return stopA.seq - stopB.seq;
    }
    
    // 如果找不到停靠点信息，则按照id排序
    return a.id - b.id;
  });
});

// 对话框控制
const dialogVisible = ref(false);
const isEdit = ref(false);
const stopTimeFormRef = ref<FormInstance | null>(null);
const currentEditingStopId = ref<number | null>(null);

// 时刻表单
const stopTimeForm = reactive<StopTimeDto>({
  id: null,
  trainTripId: 0,
  stopId: 0,
  arrivalTime: '',
  departureTime: '',
  stopSeq: 0
});

// 表单验证规则
const rules = {
  stopId: [{ required: true, message: '请选择停靠站', trigger: 'change' }],
  arrivalTime: [{ required: true, message: '请选择到站时间', trigger: 'change' }],
  departureTime: [{ required: true, message: '请选择发车时间', trigger: 'change' }]
};

// 生命周期钩子
onMounted(async () => {
  if (!trainTripId.value) {
    ElMessage.error('无效的列车行程ID');
    goBack();
    return;
  }
  
  loading.value = true;
  try {
    await loadTrainTripData();
  } catch (error) {
    console.error('加载数据失败:', error);
    ElMessage.error('加载数据失败');
  } finally {
    loading.value = false;
  }
});

// 加载行程相关数据
const loadTrainTripData = async () => {
  // 加载列车行程
  trainTrip.value = await metroStore.fetchTrainTripById(trainTripId.value);
  if (!trainTrip.value) {
    ElMessage.error('列车行程不存在');
    goBack();
    return;
  }
  
  // 确保线路数据已加载
  await metroStore.fetchLines();
  
  // 检查routeId是否有效
  if (!trainTrip.value.routeId) {
    ElMessage.error('行程没有关联有效的路线');
    goBack();
    return;
  }
  
  // 加载路线数据
  const route = await metroStore.fetchRouteById(trainTrip.value.routeId);
  if (!route) {
    ElMessage.error('行驶路线不存在');
    goBack();
    return;
  }
  
  // 加载路线的停靠点
  routeStops.value = await metroStore.fetchStopsByRouteId(trainTrip.value.routeId);
  
  // 加载时刻表
  stopTimes.value = await metroStore.fetchStopTimesByTrainTripId(trainTripId.value);
};

// 辅助方法
const getLineColor = (lineId: number | null) => {
  if (lineId === null) return '#909399';
  const line = metroStore.lines.find((l) => l.id === lineId);
  return line ? line.color : '#909399';
};

const getLineName = (lineId: number | null) => {
  if (lineId === null) return '未知线路';
  const line = metroStore.lines.find((l) => l.id === lineId);
  return line ? line.name : '未知线路';
};

const getRouteName = (routeId: number | null) => {
  if (routeId === null) return '未知方向';
  const route = metroStore.routes.find((r) => r.id === routeId);
  return route ? route.name : '未知方向';
};

const getRouteLineId = (routeId: number | null) => {
  if (routeId === null) return null;
  const route = metroStore.routes.find((r) => r.id === routeId);
  return route ? route.lineId : null;
};

const getStopStationName = (stopId: number) => {
  const stop = routeStops.value.find(s => s.id === stopId);
  if (stop) {
    const station = metroStore.stations.find(s => s.id === stop.stationId);
    return station ? station.name : '未知站点';
  }
  return '未知站点';
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  try {
    return format(parseISO(dateStr), 'yyyy-MM-dd');
  } catch (e) {
    return dateStr;
  }
};

// 导航
const goBack = () => {
  router.push('/admin/train-trip-management');
};

// 表单方法
const resetForm = () => {
  stopTimeForm.id = null;
  stopTimeForm.trainTripId = trainTripId.value;
  stopTimeForm.stopId = 0;
  stopTimeForm.arrivalTime = '';
  stopTimeForm.departureTime = '';
  stopTimeForm.stopSeq = 0;
  currentEditingStopId.value = null;
};

const openAddDialog = () => {
  if (availableStops.value.length === 0) {
    ElMessage.warning('所有停靠站点已经分配了时刻，无法添加新时刻');
    return;
  }
  
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: StopTime) => {
  isEdit.value = true;
  currentEditingStopId.value = row.stopId;
  
  stopTimeForm.id = row.id;
  stopTimeForm.trainTripId = row.trainTripId;
  stopTimeForm.stopId = row.stopId;
  stopTimeForm.arrivalTime = row.arrivalTime;
  stopTimeForm.departureTime = row.departureTime;
  stopTimeForm.stopSeq = row.stopSeq;
  
  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!stopTimeFormRef.value) return;

  await stopTimeFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;

      try {
        // 设置停靠点的顺序
        const stop = routeStops.value.find(s => s.id === stopTimeForm.stopId);
        if (stop) {
          stopTimeForm.stopSeq = stop.seq;
        }

        if (isEdit.value && stopTimeForm.id !== null) {
          const updatedStopTime = await metroStore.updateStopTime(
            stopTimeForm.id as number,
            stopTimeForm
          );
          if (updatedStopTime) {
            ElMessage.success('时刻更新成功');
            dialogVisible.value = false;
            await loadTrainTripData(); // 重新加载数据
          } else if (metroStore.error) {
            ElMessage.error(metroStore.error);
          }
        } else {
          const newStopTime = await metroStore.createStopTime(stopTimeForm);
          if (newStopTime) {
            ElMessage.success('时刻添加成功');
            dialogVisible.value = false;
            await loadTrainTripData(); // 重新加载数据
          } else if (metroStore.error) {
            ElMessage.error(metroStore.error);
          }
        }
      } catch (error: any) {
        console.error('保存时刻数据失败', error);
        if (error.apiMessage) {
          ElMessage.error(error.apiMessage);
        } else if (error.message) {
          ElMessage.error(error.message);
        } else {
          ElMessage.error('保存时刻数据失败');
        }
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleDelete = (row: StopTime) => {
  ElMessageBox.confirm(`确定要删除该时刻记录吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      loading.value = true;

      try {
        const success = await metroStore.deleteStopTime(row.id);

        if (success) {
          ElMessage.success('时刻删除成功');
          await loadTrainTripData(); // 重新加载数据
        } else if (metroStore.error) {
          ElMessage.error(metroStore.error);
        }
      } catch (error: any) {
        console.error('删除时刻失败', error);
        if (error.apiMessage) {
          ElMessage.error(error.apiMessage);
        } else if (error.message) {
          ElMessage.error(error.message);
        } else {
          ElMessage.error('删除时刻失败');
        }
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除操作
    });
};
</script>

<style scoped>
.stop-time-management-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.train-trip-info {
  margin-bottom: 20px;
}

.line-badge {
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

.control-panel {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}

.buttons {
  display: flex;
  gap: 10px;
}
</style> 