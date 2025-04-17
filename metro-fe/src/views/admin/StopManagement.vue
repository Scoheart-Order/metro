<template>
  <div class="stop-overview-container">
    <h1 class="page-title">停靠点管理</h1>

    <div class="control-panel">
      <div class="filter-group">
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

        <el-input
          v-model="searchQuery"
          placeholder="搜索路线名称"
          class="search-input"
          clearable
          @clear="handleSearchClear"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <el-table
      :data="filteredRoutes.data"
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
      <el-table-column prop="name" label="路线名称" width="180" />
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
      <el-table-column label="经过站点数" width="100">
        <template #default="scope">
          {{ getStationsCount(scope.row) }}
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
          <el-button
            size="small"
            type="success"
            @click="handleViewDetails(scope.row)"
          >
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="totalRoutes"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 路线详情对话框 -->
    <el-dialog v-model="dialogVisible" title="路线停靠点详情" width="60%">
      <div v-if="selectedRoute" class="route-details">
        <div class="route-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="路线ID">{{
              selectedRoute.id
            }}</el-descriptions-item>
            <el-descriptions-item label="路线名称">{{
              selectedRoute.name
            }}</el-descriptions-item>
            <el-descriptions-item label="所属线路">
              <div class="line-option">
                <div
                  class="color-badge"
                  :style="{
                    backgroundColor: getLineColor(selectedRoute.lineId),
                  }"
                ></div>
                <span>{{ getLineName(selectedRoute.lineId) }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="方向">
              {{ getStationName(selectedRoute.startStationId) }} →
              {{ getStationName(selectedRoute.endStationId) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="route-actions">
          <h3>停靠站点列表</h3>
          <el-button type="primary" size="small" @click="openAddStopDialog">
            <el-icon><Plus /></el-icon>添加停靠点
          </el-button>
        </div>

        <el-table
          :data="routeStops"
          style="width: 100%"
          v-loading="stopsLoading"
        >
          <el-table-column prop="seq" label="顺序" width="80" sortable />
          <el-table-column prop="stationName" label="站点名称" width="180" />
          <el-table-column prop="stationCode" label="站点编号" width="120" />
          <el-table-column label="是否换乘站" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.isTransfer ? 'success' : 'info'">
                {{ scope.row.isTransfer ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="到达时间" width="150">
            <template #default="scope">
              {{ scope.row.arrivalTime || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="发车时间" width="150">
            <template #default="scope">
              {{ scope.row.departureTime || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" type="primary" @click="handleEditStop(scope.row)">
                编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDeleteStop(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button
            type="primary"
            @click="selectedRoute && handleManageStops(selectedRoute)"
            >管理停靠点</el-button
          >
        </span>
      </template>
    </el-dialog>

    <!-- 添加/编辑停靠点对话框 -->
    <el-dialog
      v-model="stopDialogVisible"
      :title="isEditStop ? '编辑停靠点' : '添加停靠点'"
      width="50%"
    >
      <el-form
        :model="stopForm"
        label-width="120px"
        :rules="stopRules"
        ref="stopFormRef"
      >
        <el-form-item label="站点" prop="stationId">
          <el-select
            v-model="stopForm.stationId"
            placeholder="选择站点"
            style="width: 100%"
            filterable
            :disabled="isEditStop"
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
            :max="routeStops.length + (isEditStop ? 0 : 1)"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="是否换乘站" prop="isTransfer">
          <el-switch v-model="stopForm.isTransfer" />
        </el-form-item>
        <el-form-item label="时刻信息" required>
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item
                prop="arrivalTime"
                label="到达时间"
                label-width="80px"
              >
                <el-time-picker
                  v-model="stopForm.arrivalTime"
                  format="HH:mm"
                  placeholder="选择时间"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                prop="departureTime"
                label="发车时间"
                label-width="80px"
              >
                <el-time-picker
                  v-model="stopForm.departureTime"
                  format="HH:mm"
                  placeholder="选择时间"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stopDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStopForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { Search, Plus } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { useMetroStore } from '../../stores/metro';
import type { Line, Station, Route } from '../../api/modules/metro';
import { format } from 'date-fns';

// 定义Stop接口
interface Stop {
  id: number;
  routeId: number;
  stationId: number;
  stationName: string;
  stationCode: string;
  seq: number;
  isTransfer: boolean;
  arrivalTime?: string;
  departureTime?: string;
}

// 定义StopForm接口
interface StopForm {
  id?: number | null;
  routeId?: number;
  stationId: number | null;
  seq: number;
  isTransfer: boolean;
  arrivalTime: string | Date | null;
  departureTime: string | Date | null;
}

const router = useRouter();
const metroStore = useMetroStore();

// 基础数据
const loading = ref(true);
const lineFilter = ref<number | null>(null);
const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');

// 详情对话框
const dialogVisible = ref(false);
const selectedRoute = ref<Route | null>(null);
const routeStops = ref<Stop[]>([]);
const stopsLoading = ref(false);

// 添加/编辑停靠点对话框
const stopDialogVisible = ref(false);
const isEditStop = ref(false);
const stopForm = ref<StopForm>({
  stationId: null,
  seq: 1,
  isTransfer: false,
  arrivalTime: null,
  departureTime: null,
});
const stopRules = {
  stationId: [{ required: true, message: '请选择站点', trigger: 'change' }],
  seq: [{ required: true, message: '请输入顺序', trigger: 'change' }],
};
const stopFormRef = ref<FormInstance | null>(null);

// 可选站点列表 - 排除已添加的站点
const availableStations = computed(() => {
  if (!selectedRoute.value) return [];
  
  // 获取当前路线所属线路的所有站点
  const lineStations = metroStore.stations;
  
  // 过滤掉已经添加到当前路线的站点
  return lineStations.filter(
    station => !routeStops.value.some(stop => stop.stationId === station.id) || 
              (isEditStop.value && stopForm.value.stationId === station.id)
  );
});

// 分页和搜索过滤后的路线数据
const filteredRoutes = computed(() => {
  let filtered = metroStore.routes;

  // 应用线路筛选
  if (lineFilter.value) {
    filtered = filtered.filter((route) => route.lineId === lineFilter.value);
  }

  // 应用搜索筛选
  if (searchQuery.value) {
    filtered = filtered.filter((route) =>
      route.name.includes(searchQuery.value)
    );
  }

  // 计算总数
  const totalFilteredRoutes = filtered.length;

  // 应用分页
  filtered = filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  );

  return {
    data: filtered,
    total: totalFilteredRoutes,
  };
});

// 总路线数，用于分页组件
const totalRoutes = computed(() => filteredRoutes.value.total);

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

// 监听线路过滤器变化，如果选择了线路，加载该线路的站点
watch(lineFilter, async (newValue) => {
  if (newValue !== null) {
    await metroStore.fetchStationsByLineId(newValue);
  }
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

// 获取路线经过的站点数
const getStationsCount = (route: Route) => {
  if (route.stations) {
    return route.stations.length;
  }
  return 2; // 默认至少有起始站和终点站
};

// 加载路线的停靠点
const fetchRouteStops = async (routeId: number) => {
  stopsLoading.value = true;
  try {
    // 使用API获取路线的停靠点
    const response = await fetch(`/api/metro/stops/route/${routeId}`);
    const data = await response.json();

    if (data && Array.isArray(data)) {
      routeStops.value = data.map((stop: any) => {
        // 获取站点信息
        const station = metroStore.getStationById(stop.stationId);

        return {
          id: stop.id,
          routeId: stop.routeId,
          stationId: stop.stationId,
          stationName: station?.name || '未知站点',
          stationCode: station?.code || '',
          seq: stop.seq,
          isTransfer: stop.isTransfer || false,
          arrivalTime: stop.arrivalTime || null,
          departureTime: stop.departureTime || null,
        };
      });

      // 按顺序排序
      routeStops.value.sort((a, b) => a.seq - b.seq);
    }
  } catch (error) {
    console.error('获取停靠点数据失败', error);
    ElMessage.error('获取停靠点数据失败');
  } finally {
    stopsLoading.value = false;
  }
};

// 方法
const handleLineFilterClear = () => {
  currentPage.value = 1;
};

const handleLineFilterChange = () => {
  currentPage.value = 1;
};

const handleSearchClear = () => {
  currentPage.value = 1;
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
};

const handleManageStops = async (row: Route) => {
  // Ensure row.id is defined
  if (typeof row.id !== 'number') {
    ElMessage.error('路线ID无效');
    return;
  }
  
  // Set the selected route and fetch its stops
  selectedRoute.value = row;
  await fetchRouteStops(row.id);
  
  // Open the add stop dialog
  openAddStopDialog();
};

const handleViewDetails = async (row: Route) => {
  selectedRoute.value = row;
  await fetchRouteStops(row.id);
  dialogVisible.value = true;
};

const openAddStopDialog = () => {
  if (!selectedRoute.value) return;
  
  isEditStop.value = false;
  stopForm.value = {
    id: null,
    routeId: selectedRoute.value.id,
    stationId: null,
    seq: routeStops.value.length + 1,
    isTransfer: false,
    arrivalTime: null,
    departureTime: null,
  };
  stopDialogVisible.value = true;
};

const handleEditStop = (row: Stop) => {
  if (!selectedRoute.value) return;
  
  isEditStop.value = true;
  stopForm.value = {
    id: row.id,
    routeId: row.routeId,
    stationId: row.stationId,
    seq: row.seq,
    isTransfer: row.isTransfer,
    arrivalTime: row.arrivalTime || null,
    departureTime: row.departureTime || null,
  };
  stopDialogVisible.value = true;
};

const handleDeleteStop = (row: Stop) => {
  ElMessageBox.confirm(`确定要删除 ${row.stationName} 停靠点吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        stopsLoading.value = true;
        const response = await fetch(`/api/metro/stops/${row.id}`, {
          method: 'DELETE'
        });
        
        if (response.ok) {
          // 从本地数据中删除
          routeStops.value = routeStops.value.filter(stop => stop.id !== row.id);
          
          // 重新排序
          routeStops.value.forEach((stop, index) => {
            stop.seq = index + 1;
          });
          
          ElMessage.success('停靠点删除成功');
        } else {
          const error = await response.json();
          ElMessage.error(error.message || '停靠点删除失败');
        }
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

const submitStopForm = async () => {
  if (!stopFormRef.value || !selectedRoute.value) return;

  try {
    await stopFormRef.value.validate(async (valid: boolean) => {
      if (valid) {
        stopsLoading.value = true;
        
        // 查找选择的站点信息
        const station = metroStore.getStationById(stopForm.value.stationId as number);
        
        if (!station) {
          ElMessage.error('所选站点不存在');
          stopsLoading.value = false;
          return;
        }

        // 格式化时间
        const formattedStop = {
          ...stopForm.value,
          routeId: selectedRoute.value?.id,
          arrivalTime: stopForm.value.arrivalTime instanceof Date 
            ? format(stopForm.value.arrivalTime, 'HH:mm')
            : stopForm.value.arrivalTime,
          departureTime: stopForm.value.departureTime instanceof Date
            ? format(stopForm.value.departureTime, 'HH:mm')
            : stopForm.value.departureTime,
        };

        if (isEditStop.value && stopForm.value.id) {
          // 更新停靠点
          const response = await fetch(`/api/metro/stops/${stopForm.value.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formattedStop)
          });
          
          if (response.ok) {
            const updatedStop = await response.json();
            
            // 更新本地数据
            const index = routeStops.value.findIndex(stop => stop.id === stopForm.value.id);
            if (index !== -1) {
              routeStops.value[index] = {
                ...routeStops.value[index],
                stationId: formattedStop.stationId as number,
                seq: formattedStop.seq,
                isTransfer: formattedStop.isTransfer,
                arrivalTime: formattedStop.arrivalTime as string,
                departureTime: formattedStop.departureTime as string,
                stationName: station.name,
                stationCode: station.code || ''
              };
            }
            
            ElMessage.success('停靠点信息更新成功');
            stopDialogVisible.value = false;
          } else {
            const error = await response.json();
            ElMessage.error(error.message || '停靠点信息更新失败');
          }
        } else {
          // 创建新停靠点
          const response = await fetch('/api/metro/stops', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formattedStop)
          });
          
          if (response.ok) {
            const newStop = await response.json();
            
            // 添加到本地数据
            const newStopData: Stop = {
              id: newStop.id,
              routeId: newStop.routeId,
              stationId: newStop.stationId,
              stationName: station.name,
              stationCode: station.code || '',
              seq: newStop.seq,
              isTransfer: newStop.isTransfer,
              arrivalTime: newStop.arrivalTime,
              departureTime: newStop.departureTime
            };
            
            routeStops.value.push(newStopData);
            
            // 重新排序
            routeStops.value.sort((a, b) => a.seq - b.seq);
            
            ElMessage.success('停靠点添加成功');
            stopDialogVisible.value = false;
          } else {
            const error = await response.json();
            ElMessage.error(error.message || '停靠点添加失败');
          }
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
</script>

<style scoped>
.stop-overview-container {
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

.filter-group {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 250px;
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

.route-details {
  margin-top: 10px;
}

.route-details h3 {
  margin: 20px 0 10px 0;
  font-size: 18px;
  color: #303133;
}

.route-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0 10px 0;
}

.route-actions h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}
</style>
