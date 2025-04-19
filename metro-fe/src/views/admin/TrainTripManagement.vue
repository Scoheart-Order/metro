<template>
  <div class="train-trip-management-container">
    <h1 class="page-title">列车行程管理</h1>

    <div class="control-panel">
      <div class="filters">
        <el-select
          v-model="lineFilter"
          placeholder="选择线路"
          clearable
          @clear="handleLineFilterClear"
          @change="handleLineFilterChange"
        >
          <el-option
            v-for="line in metroStore.lines"
            :key="line.id"
            :label="line.name"
            :value="line.id"
          >
            <div class="line-option">
              <div
                class="color-badge"
                :style="{ backgroundColor: line.color }"
              ></div>
              <span>{{ line.name }}</span>
            </div>
          </el-option>
        </el-select>

        <el-select
          v-model="routeFilter"
          placeholder="选择方向"
          clearable
          :disabled="!lineFilter"
          @clear="handleRouteFilterClear"
        >
          <el-option
            v-for="route in filteredRoutes"
            :key="route.id"
            :label="route.name"
            :value="route.id"
          />
        </el-select>

        <el-date-picker
          v-model="dateFilter"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          clearable
          @clear="handleDateFilterClear"
        />
      </div>

      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>添加行程
      </el-button>
    </div>

    <el-table
      :data="filteredTrainTrips.data"
      style="width: 100%"
      v-loading="loading"
      empty-text="暂无列车行程数据"
    >
      <el-table-column prop="id" label="ID" width="80" sortable />
      <el-table-column label="列车车次" width="120">
        <template #default="scope">
          {{ scope.row.trainNumber || '未设置' }}
        </template>
      </el-table-column>
      <el-table-column label="所属线路" width="150">
        <template #default="scope">
          <div class="line-option" v-if="getRouteLineId(scope.row.routeId)">
            <div
              class="color-badge"
              :style="{ backgroundColor: getLineColor(getRouteLineId(scope.row.routeId)) }"
            ></div>
            <span>{{ getLineName(getRouteLineId(scope.row.routeId)) }}</span>
          </div>
          <span v-else>未知线路</span>
        </template>
      </el-table-column>
      <el-table-column label="行驶方向" width="200">
        <template #default="scope">
          {{ scope.row.routeId ? getRouteName(scope.row.routeId) : '未设置' }}
        </template>
      </el-table-column>
      <el-table-column label="运行日期" width="120" sortable>
        <template #default="scope">
          {{ scope.row.runDate ? formatDate(scope.row.runDate) : '未设置' }}
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            @click="navigateToStopTimes(scope.row)"
            title="管理列车的到站时刻表"
          >
            时刻表
          </el-button>
          <el-button 
            size="small" 
            type="info" 
            @click="handleEdit(scope.row)"
            title="编辑列车行程信息"
          >
            编辑
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.row)"
            title="删除列车行程"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="filteredTrainTrips.total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 添加/编辑列车行程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑列车行程' : '添加列车行程'"
      width="50%"
      destroy-on-close
    >
      <el-form
        :model="trainTripForm"
        label-width="120px"
        :rules="rules"
        ref="trainTripFormRef"
      >
        <el-form-item label="所属线路" prop="lineId">
          <el-select
            v-model="formLineId"
            placeholder="选择线路"
            style="width: 100%"
            @change="handleFormLineChange"
          >
            <el-option
              v-for="line in metroStore.lines"
              :key="line.id"
              :label="line.name"
              :value="line.id"
            >
              <div class="line-option">
                <div
                  class="color-badge"
                  :style="{ backgroundColor: line.color }"
                ></div>
                <span>{{ line.name }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="行驶方向" prop="routeId">
          <el-select
            v-model="trainTripForm.routeId"
            placeholder="选择行驶方向"
            style="width: 100%"
            :disabled="!formLineId"
          >
            <el-option
              v-for="route in formRoutes"
              :key="route.id"
              :label="route.name"
              :value="route.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="列车车次" prop="trainNumber">
          <el-input
            v-model="trainTripForm.trainNumber"
            placeholder="例如：G101"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="运行日期" prop="runDate">
          <el-date-picker
            v-model="trainTripForm.runDate"
            type="date"
            placeholder="选择运行日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
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
import { ref, computed, reactive, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import type { FormInstance } from 'element-plus';
import { useMetroStore } from '../../stores/metro';
import type { TrainTrip, TrainTripDto, Route } from '../../api/modules/metro';
import { format, parseISO } from 'date-fns';

const router = useRouter();
const metroStore = useMetroStore();

// 基础数据
const loading = ref(true);
const lineFilter = ref<number | null>(null);
const routeFilter = ref<number | null>(null);
const dateFilter = ref<string | null>(null);
const currentPage = ref(1);
const pageSize = ref(10);

// 过滤后的路线
const filteredRoutes = computed(() => {
  if (!lineFilter.value) return [];
  return metroStore.routes.filter((route) => route.lineId === lineFilter.value);
});

// 分页和过滤后的行程数据
const filteredTrainTrips = computed(() => {
  // 不过滤掉routeId为null或undefined的行程
  let filtered = metroStore.trainTrips;

  // 按线路筛选
  if (lineFilter.value) {
    const routeIds = metroStore.routes
      .filter((route) => route.lineId === lineFilter.value)
      .map((route) => route.id);
    filtered = filtered.filter((trip) => trip.routeId && routeIds.includes(trip.routeId));
  }

  // 按路线筛选
  if (routeFilter.value) {
    filtered = filtered.filter((trip) => trip.routeId === routeFilter.value);
  }

  // 按日期筛选
  if (dateFilter.value) {
    filtered = filtered.filter((trip) => {
      const tripDate = trip.runDate ? trip.runDate.substring(0, 10) : '';
      return tripDate === dateFilter.value;
    });
  }

  // 计算总数
  const totalFiltered = filtered.length;

  // 应用分页
  filtered = filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  );

  return {
    data: filtered,
    total: totalFiltered,
  };
});

// 对话框控制
const dialogVisible = ref(false);
const isEdit = ref(false);
const trainTripFormRef = ref<FormInstance | null>(null);
const formLineId = ref<number | null>(null);
const formRoutes = ref<Route[]>([]);

// 行程表单
const trainTripForm = reactive<TrainTripDto>({
  id: null,
  routeId: 0,
  trainNumber: '',
  runDate: '',
});

// 表单验证规则
const rules = {
  routeId: [{ required: true, message: '请选择行驶方向', trigger: 'change' }],
  trainNumber: [
    { required: true, message: '请输入列车车次', trigger: 'blur' },
    { max: 20, message: '长度不能超过20个字符', trigger: 'blur' },
  ],
  runDate: [{ required: true, message: '请选择运行日期', trigger: 'change' }],
};

// 生命周期钩子
onMounted(async () => {
  loading.value = true;
  
  try {
    // 先加载线路数据
    await metroStore.fetchLines();
    
    // 然后加载路线数据
    await metroStore.fetchRoutes();
    
    // 最后加载行程数据
    await metroStore.fetchTrainTrips();
  } catch (error) {
    console.error('加载数据失败:', error);
    ElMessage.error('加载数据失败');
  } finally {
    loading.value = false;
  }
});

// 监听线路过滤器变化
watch(lineFilter, async (newValue) => {
  if (newValue !== null) {
    // 如果之前没有加载过该线路的路线，则加载
    if (filteredRoutes.value.length === 0) {
      await metroStore.fetchRoutesByLineId(newValue);
    }
  }
  // 重置路线过滤器
  routeFilter.value = null;
});

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

const getRouteLineId = (routeId: number | null | undefined) => {
  if (routeId === null || routeId === undefined) {
    return null;
  }
  const route = metroStore.routes.find((r) => r.id === routeId);
  return route ? route.lineId : null;
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  try {
    return format(parseISO(dateStr), 'yyyy-MM-dd');
  } catch (e) {
    return dateStr;
  }
};

// 过滤器方法
const handleLineFilterClear = () => {
  lineFilter.value = null;
  routeFilter.value = null;
  currentPage.value = 1;
};

const handleLineFilterChange = () => {
  routeFilter.value = null;
  currentPage.value = 1;
};

const handleRouteFilterClear = () => {
  routeFilter.value = null;
  currentPage.value = 1;
};

const handleDateFilterClear = () => {
  dateFilter.value = null;
  currentPage.value = 1;
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
};

// 表单方法
const handleFormLineChange = async (lineId: number) => {
  trainTripForm.routeId = 0;
  if (lineId) {
    // 如果之前没有加载过该线路的路线，则加载
    await metroStore.fetchRoutesByLineId(lineId);
    formRoutes.value = metroStore.routes.filter((r) => r.lineId === lineId);
  } else {
    formRoutes.value = [];
  }
};

const resetForm = () => {
  trainTripForm.id = null;
  trainTripForm.routeId = 0;
  trainTripForm.trainNumber = '';
  trainTripForm.runDate = '';
  formLineId.value = null;
  formRoutes.value = [];
};

const openAddDialog = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: TrainTrip) => {
  isEdit.value = true;

  // 获取线路ID
  const route = metroStore.routes.find((r) => r.id === row.routeId);
  formLineId.value = route ? route.lineId : null;

  // 加载路线数据
  if (formLineId.value) {
    formRoutes.value = metroStore.routes.filter(
      (r) => r.lineId === formLineId.value
    );
  }

  trainTripForm.id = row.id;
  trainTripForm.routeId = row.routeId;
  trainTripForm.trainNumber = row.trainNumber;
  trainTripForm.runDate = row.runDate;

  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!trainTripFormRef.value) return;

  await trainTripFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;

      try {
        if (isEdit.value && trainTripForm.id !== null) {
          const updatedTrainTrip = await metroStore.updateTrainTrip(
            trainTripForm.id as number,
            trainTripForm
          );
          if (updatedTrainTrip) {
            ElMessage.success('列车行程更新成功');
            dialogVisible.value = false;
          } else if (metroStore.error) {
            ElMessage.error(metroStore.error);
          }
        } else {
          const newTrainTrip = await metroStore.createTrainTrip(trainTripForm);
          if (newTrainTrip) {
            ElMessage.success('列车行程添加成功');
            dialogVisible.value = false;
          } else if (metroStore.error) {
            ElMessage.error(metroStore.error);
          }
        }
      } catch (error: any) {
        console.error('保存列车行程数据失败', error);
        if (error.apiMessage) {
          ElMessage.error(error.apiMessage);
        } else if (error.message) {
          ElMessage.error(error.message);
        } else {
          ElMessage.error('保存列车行程数据失败');
        }
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleDelete = (row: TrainTrip) => {
  ElMessageBox.confirm(`确定要删除列车行程 ${row.trainNumber} 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      loading.value = true;

      try {
        const success = await metroStore.deleteTrainTrip(row.id);

        if (success) {
          ElMessage.success('列车行程删除成功');
        } else if (metroStore.error) {
          ElMessage.error(metroStore.error);
        }
      } catch (error: any) {
        console.error('删除列车行程失败', error);
        if (error.apiMessage) {
          ElMessage.error(error.apiMessage);
        } else if (error.message) {
          ElMessage.error(error.message);
        } else {
          ElMessage.error('删除列车行程失败');
        }
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除操作
    });
};

const navigateToStopTimes = (row: TrainTrip) => {
  router.push(`/admin/stop-time-management/${row.id}`);
};
</script>

<style scoped>
.train-trip-management-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.control-panel {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}

.filters {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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
</style> 