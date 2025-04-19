<template>
  <div class="stop-management-container">
    <h1 class="page-title">经停站管理</h1>

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
          placeholder="搜索方向名称"
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
            管理经停站
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

    <!-- 经停站管理对话框 -->
    <el-dialog v-model="stopsDialogVisible" title="经停站管理" width="60%">
      <div v-if="selectedRoute" class="stops-container">
        <div class="stops-header">
          <h3>{{ selectedRoute.name }} 经停站列表</h3>
          <el-button type="primary" @click="openAddStopDialog">
            <el-icon><Plus /></el-icon>添加经停站
          </el-button>
        </div>

        <div class="stops-list" v-loading="stopsLoading">
          <el-empty v-if="routeStops.length === 0" description="暂无经停站" />
          
          <div v-else class="draggable-container">
            <el-alert
              v-if="hasChangedOrder"
              type="warning"
              show-icon
              :closable="false"
            >
              <span>经停站顺序已更改，点击"保存排序"按钮以保存更改</span>
              <el-button type="primary" size="small" @click="saveStopsOrder" class="save-order-btn">
                保存排序
              </el-button>
            </el-alert>
            
            <draggable
              v-model="routeStops"
              handle=".drag-handle"
              :animation="150"
              item-key="id"
              ghost-class="ghost"
              chosen-class="chosen"
              tag="ul"
              class="stops-draggable-list"
              @end="onDragEnd"
              :move="checkMove"
            >
              <template #item="{element}">
                <li :class="[
                  'station-item', 
                  { 
                    'start-station': element.isStartStation, 
                    'end-station': element.isEndStation 
                  }
                ]">
                  <div class="station-item-content">
                    <div class="drag-handle" v-if="!element.isStartStation && !element.isEndStation">
                      <el-icon><Rank /></el-icon>
                    </div>
                    <div class="station-badge" :style="{ backgroundColor: getTimelineItemColor(element) }">
                      {{ element.seq }}
                    </div>
                    <div class="station-info">
                      <span class="station-name">{{ element.stationName }}</span>
                      <span class="station-code">({{ element.stationCode }})</span>
                    </div>
                    <div class="stop-actions">
                      <el-button 
                        v-if="!element.isStartStation && !element.isEndStation" 
                        type="primary" 
                        size="small" 
                        @click="handleEditStop(element)" 
                        class="action-btn"
                      >
                        编辑
                      </el-button>
                      <el-button 
                        v-if="!element.isStartStation && !element.isEndStation" 
                        type="danger" 
                        size="small" 
                        @click="handleDeleteStop(element)" 
                        class="action-btn"
                      >
                        删除
                      </el-button>
                      <span v-else class="special-station-badge">{{ element.isStartStation ? '起始站' : '终点站' }}</span>
                    </div>
                  </div>
                </li>
              </template>
            </draggable>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 添加/编辑经停站对话框 -->
    <el-dialog
      v-model="addStopDialogVisible"
      :title="isEditMode ? '编辑经停站' : '添加经停站'"
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
        <div v-if="isEditMode" class="seq-info">
          当前序号: {{ stopForm.seq }}
        </div>
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
import { ref, computed, reactive, onMounted, watch, defineComponent } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Rank } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';
import { useMetroStore } from '../../stores/metro';
import { metroApi } from '../../api/modules/metro';
import type { Route, Stop, StopDto, Station, UpdateStopSequencesDto } from '../../api/modules/metro';
// Import draggable correctly
import draggable from 'vuedraggable';

// Define components
defineComponent({
  components: {
    draggable
  }
});

// 定义组件内部使用的Stop接口，包含额外字段
interface StopDisplay extends Stop {
  stationCode: string;
  stationName: string;
  isStartStation?: boolean;
  isEndStation?: boolean;
}

// 定义批量更新序号的接口
interface UpdateSeqsRequest {
  routeId: number;
  stopSequences: { id: number; seq: number }[];
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
const lineFilter = ref<number | null>(null);
const searchQuery = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const hasChangedOrder = ref(false);
const originalOrder = ref<number[]>([]);

// 经停站表单
const stopForm = ref<StopDto>({
  routeId: 0,
  stationId: 0,
  seq: 1
});
const stopRules = {
  stationId: [{ required: true, message: '请选择站点', trigger: 'change' }],
};
const stopFormRef = ref<FormInstance | null>(null);

// 过滤后的路线数据
const filteredRoutes = computed(() => {
  let filtered = metroStore.routes;

  // 按线路筛选
  if (lineFilter.value) {
    filtered = filtered.filter((route) => route.lineId === lineFilter.value);
  }

  // 按搜索筛选
  if (searchQuery.value) {
    filtered = filtered.filter((route) =>
      route.name.toLowerCase().includes(searchQuery.value.toLowerCase())
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

// 路线总数
const totalRoutes = computed(() => filteredRoutes.value.total);

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
  try {
    // Load data in parallel, but ensure stations are loaded first
    await metroStore.fetchStations();
    await Promise.all([
      metroStore.fetchLines(),
      metroStore.fetchRoutes(),
    ]);
  } catch (error) {
    console.error('加载数据失败', error);
    ElMessage.error('加载初始数据失败');
  } finally {
    loading.value = false;
  }
});

// 监听线路过滤器变化
watch(lineFilter, async (newValue) => {
  if (newValue !== null) {
    await metroStore.fetchRoutesByLineId(newValue);
  }
  currentPage.value = 1;
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
const getTimelineItemType = (stop: StopDisplay) => {
  if (stop.isStartStation) return 'primary';
  if (stop.isEndStation) return 'success';
  
  const types = ['warning', 'danger', 'info'];
  return types[stop.seq % types.length];
};

// 获取时间线项颜色
const getTimelineItemColor = (stop: StopDisplay) => {
  if (stop.isStartStation) return '#409EFF'; // 蓝色
  if (stop.isEndStation) return '#67C23A';   // 绿色
  
  const colors = ['#E6A23C', '#F56C6C', '#909399', '#FF9900'];
  return colors[stop.seq % colors.length];
};

// 加载方向的经停站
const fetchRouteStops = async (routeId: number) => {
  console.log(`fetchRouteStops starting for route ID: ${routeId}`);
  stopsLoading.value = true;
  try {
    // Make sure stations are loaded first
    if (metroStore.stations.length === 0) {
      console.log('No stations loaded, fetching stations first');
      await metroStore.fetchStations();
    }
    
    console.log(`Calling API to get stops for route ID: ${routeId}`);
    const stops = await metroApi.getStopsByRouteId(routeId);
    console.log('Stops received from API:', stops);
    
    // 获取当前路线信息
    const currentRoute = selectedRoute.value;
    if (!currentRoute) {
      console.error('No selected route found');
      throw new Error('未找到当前路线信息');
    }
    
    // Process each stop and ensure station data is correctly mapped
    console.log('Processing stops');
    routeStops.value = stops.map((stop) => {
      // Try to get station data from the returned stop first (from nested objects)
      let stationName = '';
      let stationCode = '';
      
      if (stop.station) {
        stationName = stop.station.name;
        stationCode = stop.station.code;
        console.log(`Stop ${stop.id} has station info in response: ${stationName}`);
      } else if (stop.stationId) {
        // If no nested station, try to find it in the store
        const station = metroStore.getStationById(stop.stationId);
        if (station) {
          stationName = station.name;
          stationCode = station.code;
          console.log(`Stop ${stop.id} station found in store: ${stationName}`);
        } else {
          console.warn(`Station with ID ${stop.stationId} not found for stop ${stop.id}`);
          stationName = `未知站点(ID: ${stop.stationId})`;
          stationCode = `未知(${stop.stationId})`;
        }
      } else {
        console.error(`Stop ${stop.id} has no stationId`);
        stationName = '数据错误';
        stationCode = '数据错误';
      }
      
      // 添加特殊标记，显示起始站和终点站
      let stationInfo = stationName;
      if (stop.stationId === currentRoute.startStationId) {
        stationInfo += ' (起始站)';
      } else if (stop.stationId === currentRoute.endStationId) {
        stationInfo += ' (终点站)';
      }
      
      return {
        ...stop,
        stationCode: stationCode,
        stationName: stationInfo,
        isStartStation: stop.stationId === currentRoute.startStationId,
        isEndStation: stop.stationId === currentRoute.endStationId
      };
    });

    // Log warning if there are stops with missing data
    const invalidStops = routeStops.value.filter(stop => 
      !stop.stationId || 
      (stop.stationName.includes('未知') && !stop.stationName.includes('(起始站)') && !stop.stationName.includes('(终点站)')) || 
      stop.stationName.includes('错误')
    );
    
    if (invalidStops.length > 0) {
      console.warn(`Found ${invalidStops.length} stops with missing or invalid data:`, invalidStops);
    }

    // Sort the stops by sequence
    routeStops.value.sort((a, b) => a.seq - b.seq);
    console.log('Stops sorted:', routeStops.value);
    
    // Store the original order for comparison
    originalOrder.value = routeStops.value.map(stop => stop.id);
    hasChangedOrder.value = false;
    console.log('fetchRouteStops completed successfully');
  } catch (error) {
    console.error('获取经停站数据失败', error);
    ElMessage.error('获取经停站数据失败');
  } finally {
    stopsLoading.value = false;
    console.log('fetchRouteStops finished, loading set to false');
  }
};

// 拖拽结束处理
const onDragEnd = (evt: any) => {
  console.log('Drag ended:', evt);
  // 更新排序
  updateStopSequences();
};

// 检查拖动是否允许
const checkMove = (evt: any) => {
  // 获取拖动的元素和目标位置
  const { draggedContext, relatedContext } = evt;
  const draggedElement = draggedContext.element;
  const targetIndex = relatedContext.index;
  
  // 禁止普通站拖到第一个位置（起始站位置）
  if (targetIndex === 0) {
    return false;
  }
  
  // 如果有终点站，禁止普通站拖到最后一个位置
  if (routeStops.value.some(stop => stop.isEndStation) && 
      targetIndex === routeStops.value.length - 1) {
    return false;
  }
  
  // 禁止拖动起始站和终点站
  if (draggedElement.isStartStation || draggedElement.isEndStation) {
    return false;
  }
  
  return true;
};

// 更新经停站序号
const updateStopSequences = () => {
  console.log('Updating stop sequences');
  // 获取当前排序
  const currentOrder = routeStops.value.map(stop => stop.id);
  
  // 检查顺序是否有变化
  const hasChanged = !currentOrder.every((id, index) => id === originalOrder.value[index]);
  hasChangedOrder.value = hasChanged;
  console.log('Order changed:', hasChanged);
  
  if (hasChanged) {
    // 更新序号，但不保持起始站和终点站的位置
    routeStops.value.forEach((stop, index) => {
      // 起始站和终点站的seq不变，其他站按顺序重新编号
      if (!stop.isStartStation && !stop.isEndStation) {
        stop.seq = index + 1;
      }
    });
    console.log('New sequence:', routeStops.value.map(stop => ({ id: stop.id, seq: stop.seq })));
  }
};

// 保存排序
const saveStopsOrder = async () => {
  if (!selectedRoute.value) return;
  
  stopsLoading.value = true;
  try {
    // 准备批量更新序号的请求
    const updateSeqsRequest: UpdateStopSequencesDto = {
      routeId: selectedRoute.value.id,
      stopSequences: routeStops.value.map(stop => ({
        id: stop.id as number,
        seq: stop.seq
      }))
    };
    
    // 调用批量更新API
    const success = await metroApi.updateStopSequences(updateSeqsRequest);
    
    if (success) {
      ElMessage.success('经停站排序已更新');
      // 重新加载经停站以获取最新顺序
      await fetchRouteStops(selectedRoute.value.id);
      hasChangedOrder.value = false;
    } else {
      ElMessage.error('更新经停站排序失败');
    }
  } catch (error) {
    console.error('更新经停站排序失败', error);
    ElMessage.error('更新经停站排序失败');
  } finally {
    stopsLoading.value = false;
  }
};

// 过滤器方法
const handleLineFilterClear = () => {
  lineFilter.value = null;
  currentPage.value = 1;
};

const handleLineFilterChange = () => {
  currentPage.value = 1;
};

const handleSearchClear = () => {
  searchQuery.value = '';
  currentPage.value = 1;
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
};

// 打开经停站管理对话框
const handleManageStops = async (row: Route) => {
  console.log('handleManageStops called with route:', row);
  try {
    selectedRoute.value = row;
    console.log('Selected route set:', selectedRoute.value);
    
    // Set dialog visible first to ensure it renders
    stopsDialogVisible.value = true;
    console.log('Dialog visibility set to:', stopsDialogVisible.value);
    
    // Then fetch the data
    await fetchRouteStops(row.id);
    console.log('Stops fetched successfully');
    
  } catch (error) {
    console.error('Error in handleManageStops:', error);
    ElMessage.error('打开经停站管理对话框失败');
  }
};

// 打开添加经停站对话框
const openAddStopDialog = () => {
  if (!selectedRoute.value) return;
  
  // 确定合适的序号
  // 默认设置为最后一个站点之前的位置
  const lastSeq = routeStops.value.length > 0 
    ? Math.max(...routeStops.value.map(stop => stop.seq))
    : 0;
  
  // 找到终点站的序号（如果有）
  const endStationStop = routeStops.value.find(
    stop => stop.stationId === selectedRoute.value?.endStationId
  );
  
  // 序号应该设置在终点站之前
  let newSeq = endStationStop 
    ? endStationStop.seq - 1 
    : lastSeq + 1;
  
  // 防止序号小于1
  newSeq = Math.max(1, newSeq);
  
  stopForm.value = {
    id: null,
    routeId: selectedRoute.value.id,
    stationId: 0,
    seq: newSeq
  };
  
  isEditMode.value = false;
  addStopDialogVisible.value = true;
};

// 删除经停站
const handleDeleteStop = (stop: StopDisplay) => {
  ElMessageBox.confirm(
    `确定要删除经停站 ${stop.stationName} 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      stopsLoading.value = true;
      try {
        const success = await metroApi.deleteStop(stop.id);
        if (success) {
          ElMessage.success('经停站删除成功');
          
          // 如果删除成功，获取当前路线的所有经停站并重新排序
          if (selectedRoute.value) {
            // 先获取最新的经停站列表
            await fetchRouteStops(selectedRoute.value.id);
            
            // 重新排序所有经停站，确保序号连续
            await resequenceStops();
          }
        } else {
          ElMessage.error('经停站删除失败');
        }
      } catch (error) {
        console.error('删除经停站失败', error);
        ElMessage.error('删除经停站失败');
      } finally {
        stopsLoading.value = false;
      }
    })
    .catch(() => {
      // 取消删除操作
    });
};

// 重新排序所有经停站，确保序号连续
const resequenceStops = async () => {
  if (!selectedRoute.value || routeStops.value.length === 0) return;
  
  // 临时保存当前顺序
  const startStopId = selectedRoute.value.startStationId;
  const endStopId = selectedRoute.value.endStationId;
  
  // 创建一个新的排序数组，确保起始站在第一位，终点站在最后一位
  const sortedStops = [...routeStops.value].sort((a, b) => {
    // 起始站始终排在第一位
    if (a.stationId === startStopId) return -1;
    if (b.stationId === startStopId) return 1;
    
    // 终点站始终排在最后一位
    if (a.stationId === endStopId) return 1;
    if (b.stationId === endStopId) return -1;
    
    // 其他站点按原有序号排序
    return a.seq - b.seq;
  });
  
  // 重新分配序号，确保连续
  for (let i = 0; i < sortedStops.length; i++) {
    const stop = sortedStops[i];
    
    // 特殊处理：起始站seq=1，终点站seq=最大值
    if (stop.stationId === startStopId) {
      stop.seq = 1;
    } else if (stop.stationId === endStopId) {
      stop.seq = sortedStops.length;
    } else {
      // 一般站点从1开始按顺序递增，但要跳过起始站的1
      stop.seq = i + 1;
      
      // 如果第一站是起始站，则从2开始编号
      if (sortedStops[0].stationId === startStopId) {
        stop.seq = i + 1;
      }
    }
  }
  
  // 准备更新序号的请求
  const updateSeqsRequest: UpdateStopSequencesDto = {
    routeId: selectedRoute.value.id,
    stopSequences: sortedStops.map(stop => ({
      id: stop.id as number,
      seq: stop.seq
    }))
  };
  
  // 调用批量更新API
  try {
    const success = await metroApi.updateStopSequences(updateSeqsRequest);
    
    if (success) {
      ElMessage.success('经停站序号已更新');
      // 重新加载经停站以获取最新顺序
      await fetchRouteStops(selectedRoute.value.id);
    } else {
      ElMessage.error('更新经停站序号失败');
    }
  } catch (error) {
    console.error('重新排序经停站失败', error);
    ElMessage.error('重新排序经停站失败');
  }
};

// 打开编辑经停站对话框
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

// 提交经停站表单
const submitStopForm = async () => {
  if (!stopFormRef.value) return;

  try {
    stopsLoading.value = true;
    
    await stopFormRef.value.validate(async (valid) => {
      if (valid) {
        try {
          if (isEditMode.value && stopForm.value.id !== null) {
            const success = await metroApi.updateStop(
              stopForm.value.id as number,
              stopForm.value
            );
            if (success) {
              ElMessage.success('经停站更新成功');
              addStopDialogVisible.value = false;
              // 重新加载经停站列表
              if (selectedRoute.value) {
                await fetchRouteStops(selectedRoute.value.id);
              }
            } else {
              ElMessage.error('经停站更新失败');
            }
          } else {
            const newStop = await metroApi.createStop(stopForm.value);
            if (newStop) {
              ElMessage.success('经停站添加成功');
              addStopDialogVisible.value = false;
              // 重新加载经停站列表
              if (selectedRoute.value) {
                await fetchRouteStops(selectedRoute.value.id);
              }
            } else {
              ElMessage.error('经停站添加失败');
            }
          }
        } catch (error) {
          console.error('保存经停站数据失败', error);
          ElMessage.error('保存经停站数据失败');
        }
      } else {
        ElMessage.error('表单验证失败');
      }
    });
  } catch (error) {
    console.error('保存经停站数据失败', error);
    ElMessage.error('保存经停站数据失败');
  } finally {
    stopsLoading.value = false;
  }
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

.stops-list {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0 10px;
}

.draggable-container {
  margin-top: 16px;
}

.stops-draggable-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.station-item {
  margin-bottom: 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.station-item-content {
  display: flex;
  align-items: center;
  padding: 12px;
  background-color: #f7f7f7;
  border-radius: 4px;
  gap: 12px;
}

.start-station .station-item-content {
  background-color: #ecf5ff;
  border-left: 4px solid #409EFF;
}

.end-station .station-item-content {
  background-color: #f0f9eb;
  border-left: 4px solid #67C23A;
}

.drag-handle {
  cursor: move;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  padding: 4px;
}

.drag-handle:hover {
  color: #409EFF;
}

.station-badge {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 28px;
  height: 28px;
  background-color: #409EFF;
  color: white;
  border-radius: 50%;
  font-weight: bold;
  flex-shrink: 0;
}

.station-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
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

.seq-info {
  margin: 15px 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #606266;
  font-size: 14px;
}

.special-station-badge {
  padding: 4px 8px;
  background-color: #409EFF;
  color: white;
  border-radius: 4px;
  font-size: 12px;
}

.start-station .special-station-badge {
  background-color: #409EFF;
}

.end-station .special-station-badge {
  background-color: #67C23A;
}

.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}

.chosen {
  background-color: #f5f7fa;
}

.flip-list-move {
  transition: transform 0.5s;
}

.save-order-btn {
  margin-left: 10px;
}
</style>
