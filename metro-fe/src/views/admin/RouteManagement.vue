<template>
  <div class="route-management-container">
    <h1 class="page-title">方向管理</h1>

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

      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>添加方向
      </el-button>
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
      <el-table-column label="经过站点数" width="100">
        <template #default="scope">
          {{ getStationsCount(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.row)"
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
      :total="totalRoutes"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 添加/编辑方向对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑方向' : '添加方向'"
      width="50%"
    >
      <el-form
        :model="routeForm"
        label-width="120px"
        :rules="rules"
        ref="routeFormRef"
      >
        <el-form-item label="所属线路" prop="lineId">
          <el-select
            v-model="routeForm.lineId"
            placeholder="选择线路"
            style="width: 100%"
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

        <el-form-item label="起始站" prop="startStationId">
          <el-select
            v-model="routeForm.startStationId"
            placeholder="选择起始站"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="station in stations"
              :key="station.id"
              :label="station.name"
              :value="station.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="终点站" prop="endStationId">
          <el-select
            v-model="routeForm.endStationId"
            placeholder="选择终点站"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="station in stations"
              :key="station.id"
              :label="station.name"
              :value="station.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="方向名称" prop="name">
          <el-input
            v-model="routeForm.name"
            placeholder="起始站 -> 终点站"
            :disabled="true"
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
import { Search, Plus } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import type { FormInstance } from 'element-plus';
import { useMetroStore } from '../../stores/metro';
import type { Line, Station, Route, RouteDto } from '../../api/modules/metro';

const router = useRouter();
const metroStore = useMetroStore();

// 基础数据
const loading = ref(true);
const lineFilter = ref<number | null>(null);
const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');

// 分页和搜索过滤后的方向数据
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

// 总方向数，用于分页组件
const totalRoutes = computed(() => filteredRoutes.value.total);

// 对话框控制
const dialogVisible = ref(false);
const isEdit = ref(false);
const routeFormRef = ref<FormInstance | null>(null);

// 方向表单
const routeForm = reactive<RouteDto>({
  id: null,
  lineId: 0,
  name: '',
  startStationId: 0,
  endStationId: 0,
  distance: 0,
  estimatedTime: 0,
});

// 表单验证规则
const rules = {
  lineId: [{ required: true, message: '请选择所属线路', trigger: 'change' }],
  name: [
    {
      required: true,
      message: '请选择起始站和终点站以生成方向名称',
      trigger: 'change',
    },
  ],
  startStationId: [
    { required: true, message: '请选择起始站', trigger: 'change' },
  ],
  endStationId: [
    { required: true, message: '请选择终点站', trigger: 'change' },
  ],
  distance: [
    { required: true, message: '请输入距离', trigger: 'blur' },
    { type: 'number', min: 0, message: '距离不能小于0', trigger: 'blur' },
  ],
  estimatedTime: [
    { required: true, message: '请输入预计时间（分钟）', trigger: 'blur' },
    { type: 'number', min: 0, message: '时间不能小于0', trigger: 'blur' },
  ],
};

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

// 获取站点列表，优先使用已加载的线路站点
const stations = computed(() => {
  if (lineFilter.value !== null && metroStore.lineStations[lineFilter.value]) {
    return metroStore.lineStations[lineFilter.value];
  }
  return metroStore.stations;
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

// 获取方向经过的站点数
const getStationsCount = (route: Route) => {
  if (route.stations) {
    return route.stations.length;
  }
  return 2; // 默认至少有起始站和终点站
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

const resetForm = () => {
  routeForm.id = null;
  routeForm.lineId = lineFilter.value !== null ? lineFilter.value : 0;
  routeForm.name = '';
  routeForm.startStationId = 0;
  routeForm.endStationId = 0;
  routeForm.distance = 0;
  routeForm.estimatedTime = 0;
  routeForm.stationIds = [];
};

const openAddDialog = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: Route) => {
  isEdit.value = true;

  routeForm.id = row.id;
  routeForm.lineId = row.lineId;
  routeForm.startStationId = row.startStationId;
  routeForm.endStationId = row.endStationId;
  // No need to set name as it will be auto-generated by the watcher
  routeForm.distance = row.distance;
  routeForm.estimatedTime = row.estimatedTime;

  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!routeFormRef.value) return;

  await routeFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;

      try {
        if (isEdit.value && routeForm.id !== null) {
          const updatedRoute = await metroStore.updateRoute(
            routeForm.id as number,
            routeForm
          );
          if (updatedRoute) {
            ElMessage.success('方向信息更新成功');
            dialogVisible.value = false;
          } else {
            ElMessage.error(metroStore.error || '方向信息更新失败');
          }
        } else {
          const newRoute = await metroStore.createRoute(routeForm);
          if (newRoute) {
            ElMessage.success('方向添加成功');
            dialogVisible.value = false;
          } else {
            ElMessage.error(metroStore.error || '方向添加失败');
          }
        }
      } catch (error) {
        console.error('保存方向数据失败', error);
        ElMessage.error('保存方向数据失败');
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleDelete = (row: Route) => {
  ElMessageBox.confirm(`确定要删除方向 ${row.name} 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      loading.value = true;

      try {
        // Ensure row.id is defined
        if (typeof row.id !== 'number') {
          ElMessage.error('方向ID无效');
          return;
        }

        const success = await metroStore.deleteRoute(row.id);

        if (success) {
          ElMessage.success('方向删除成功');
        } else {
          ElMessage.error(metroStore.error || '方向删除失败');
        }
      } catch (error) {
        console.error('删除方向失败', error);
        ElMessage.error('删除方向失败');
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除操作
    });
};

const handleStops = (row: Route) => {
  // Ensure row.id is defined
  if (typeof row.id !== 'number') {
    ElMessage.error('方向ID无效');
    return;
  }

  router.push(`/admin/route/${row.id}/stops`);
};

// 添加 watch 以自动更新方向名称
watch([() => routeForm.startStationId, () => routeForm.endStationId], () => {
  if (routeForm.startStationId && routeForm.endStationId) {
    const startStation = metroStore.stations.find(
      (s) => s.id === routeForm.startStationId
    );
    const endStation = metroStore.stations.find(
      (s) => s.id === routeForm.endStationId
    );

    if (startStation && endStation) {
      routeForm.name = `${startStation.name} -> ${endStation.name}`;
    }
  } else {
    routeForm.name = '';
  }
});
</script>

<style scoped>
.route-management-container {
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
</style>
