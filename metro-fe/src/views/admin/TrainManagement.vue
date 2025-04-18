<template>
  <div class="train-management-container">
    <h1 class="page-title">列车管理</h1>

    <div class="control-panel">
      <el-input
        v-model="searchQuery"
        placeholder="搜索列车编号或名称"
        class="search-input"
        clearable
        @clear="handleSearchClear"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>添加列车
      </el-button>
    </div>

    <el-table
      :data="filteredTrains"
      style="width: 100%"
      :row-class-name="tableRowClassName"
      v-loading="loading"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="trainNumber" label="列车编号" width="120" />
      <el-table-column prop="name" label="列车名称" width="180" />
      <el-table-column prop="startTime" label="首班时间" width="140" />
      <el-table-column prop="endTime" label="末班时间" width="140" />
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <!-- <el-button
            size="small"
            type="success"
            @click="handleSchedule(scope.row)"
          >
            时刻表
          </el-button> -->
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
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="totalTrains"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 添加/编辑列车对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑列车' : '添加列车'"
      width="50%"
    >
      <el-form
        :model="trainForm"
        label-width="120px"
        :rules="rules"
        ref="trainFormRef"
      >
        <el-form-item label="列车编号" prop="trainNumber">
          <el-input v-model="trainForm.trainNumber" />
        </el-form-item>
        <el-form-item label="列车名称" prop="name">
          <el-input v-model="trainForm.name" />
        </el-form-item>
        <el-form-item label="列车类型" prop="type">
          <el-select
            v-model="trainForm.type"
            placeholder="请选择列车类型"
            style="width: 100%"
          >
            <el-option label="地铁列车" value="subway" />
            <el-option label="轻轨列车" value="light_rail" />
            <el-option label="磁悬浮列车" value="maglev" />
          </el-select>
        </el-form-item>
        <el-form-item label="载客量" prop="capacity">
          <el-input-number v-model="trainForm.capacity" :min="1" :max="2000" />
        </el-form-item>
        <el-form-item label="运行状态" prop="status">
          <el-select
            v-model="trainForm.status"
            placeholder="请选择运行状态"
            style="width: 100%"
          >
            <el-option label="正常运行" value="running" />
            <el-option label="临时停运" value="paused" />
            <el-option label="检修中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item label="首班时间" prop="startTime">
          <el-time-picker
            v-model="trainForm.startTime"
            format="HH:mm"
            placeholder="选择时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="末班时间" prop="endTime">
          <el-time-picker
            v-model="trainForm.endTime"
            format="HH:mm"
            placeholder="选择时间"
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

    <!-- 时刻表对话框 -->
    <el-dialog v-model="scheduleDialogVisible" title="列车时刻表" width="70%">
      <div v-if="currentTrain" class="schedule-dialog-header">
        <h3>{{ currentTrain.trainNumber }} - {{ currentTrain.name }}</h3>
        <el-button type="primary" @click="addScheduleItem">添加时刻</el-button>
      </div>

      <el-table
        :data="scheduleData"
        style="width: 100%"
        v-loading="scheduleLoading"
      >
        <el-table-column prop="stationName" label="站点名称" width="180" />
        <el-table-column prop="arrivalTime" label="到达时间" width="150" />
        <el-table-column prop="departureTime" label="发车时间" width="150" />
        <el-table-column
          prop="stopDuration"
          label="停站时长(分钟)"
          width="150"
        />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="editScheduleItem(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="deleteScheduleItem(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 时刻表编辑对话框 -->
    <el-dialog
      v-model="scheduleItemDialogVisible"
      :title="isEditSchedule ? '编辑时刻' : '添加时刻'"
      width="50%"
    >
      <el-form
        :model="scheduleForm"
        label-width="120px"
        :rules="scheduleRules"
        ref="scheduleFormRef"
      >
        <el-form-item label="站点名称" prop="stationName">
          <el-select
            v-model="scheduleForm.stationName"
            placeholder="请选择站点"
            style="width: 100%"
          >
            <el-option
              v-for="station in stations"
              :key="station.id"
              :label="station.name"
              :value="station.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="到达时间" prop="arrivalTime">
          <el-time-picker
            v-model="scheduleForm.arrivalTime"
            format="HH:mm"
            placeholder="选择时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="发车时间" prop="departureTime">
          <el-time-picker
            v-model="scheduleForm.departureTime"
            format="HH:mm"
            placeholder="选择时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="停站时长(分钟)" prop="stopDuration">
          <el-input-number
            v-model="scheduleForm.stopDuration"
            :min="1"
            :max="30"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scheduleItemDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitScheduleForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus } from '@element-plus/icons-vue';
import { format } from 'date-fns';

// 列车数据
const trains = ref([]);
const loading = ref(true);
const currentPage = ref(1);
const pageSize = ref(10);
const totalTrains = ref(0);
const searchQuery = ref('');

// 分页和搜索过滤后的列车数据
const filteredTrains = computed(() => {
  if (!searchQuery.value) {
    return trains.value.slice(
      (currentPage.value - 1) * pageSize.value,
      currentPage.value * pageSize.value
    );
  }

  const filtered = trains.value.filter(
    (train) =>
      train.trainNumber.includes(searchQuery.value) ||
      train.name.includes(searchQuery.value)
  );

  return filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  );
});

// 对话框控制
const dialogVisible = ref(false);
const isEdit = ref(false);
const trainFormRef = ref(null);

// 列车表单
const trainForm = reactive({
  id: '',
  trainNumber: '',
  name: '',
  type: '',
  capacity: 0,
  status: 'running',
  startTime: '',
  endTime: '',
});

// 表单验证规则
const rules = {
  trainNumber: [
    { required: true, message: '请输入列车编号', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  name: [
    { required: true, message: '请输入列车名称', trigger: 'blur' },
    { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' },
  ],
  type: [{ required: true, message: '请选择列车类型', trigger: 'change' }],
  capacity: [{ required: true, message: '请输入载客量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择运行状态', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择首班时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择末班时间', trigger: 'change' }],
};

// 时刻表相关
const scheduleDialogVisible = ref(false);
const scheduleItemDialogVisible = ref(false);
const isEditSchedule = ref(false);
const scheduleFormRef = ref(null);
const scheduleData = ref([]);
const scheduleLoading = ref(false);
const currentTrain = ref(null);

// 时刻表表单
const scheduleForm = reactive({
  id: '',
  trainId: '',
  stationName: '',
  arrivalTime: '',
  departureTime: '',
  stopDuration: 1,
});

// 时刻表表单验证规则
const scheduleRules = {
  stationName: [{ required: true, message: '请选择站点', trigger: 'change' }],
  arrivalTime: [
    { required: true, message: '请选择到达时间', trigger: 'change' },
  ],
  departureTime: [
    { required: true, message: '请选择发车时间', trigger: 'change' },
  ],
  stopDuration: [
    { required: true, message: '请输入停站时长', trigger: 'blur' },
  ],
};

// 模拟站点数据
const stations = ref([
  { id: 1, name: '中心站' },
  { id: 2, name: '人民广场站' },
  { id: 3, name: '大学城站' },
  { id: 4, name: '体育中心站' },
  { id: 5, name: '科技园站' },
  { id: 6, name: '机场站' },
  { id: 7, name: '南湖站' },
  { id: 8, name: '东门站' },
  { id: 9, name: '西门站' },
  { id: 10, name: '北站' },
]);

// 生命周期钩子
onMounted(async () => {
  await fetchTrains();
});

// 方法
const fetchTrains = async () => {
  loading.value = true;
  try {
    // 模拟API调用
    // const response = await fetch('/api/admin/trains')
    // const data = await response.json()
    // trains.value = data.trains
    // totalTrains.value = data.total

    // 模拟数据
    setTimeout(() => {
      const mockTrains = Array.from({ length: 25 }, (_, index) => ({
        id: index + 1,
        trainNumber: `T${(index + 1).toString().padStart(3, '0')}`,
        name: `城市地铁${index + 1}号`,
        type:
          index % 3 === 0
            ? 'subway'
            : index % 3 === 1
            ? 'light_rail'
            : 'maglev',
        capacity: 800 + ((index * 50) % 1000),
        status:
          index % 4 === 0
            ? 'paused'
            : index % 5 === 0
            ? 'maintenance'
            : 'running',
        startTime: '05:00',
        endTime: '23:30',
      }));

      trains.value = mockTrains;
      totalTrains.value = mockTrains.length;
      loading.value = false;
    }, 500);
  } catch (error) {
    console.error('获取列车数据失败', error);
    ElMessage.error('获取列车数据失败');
    loading.value = false;
  }
};

const getStatusType = (status) => {
  switch (status) {
    case 'running':
      return 'success';
    case 'paused':
      return 'warning';
    case 'maintenance':
      return 'danger';
    default:
      return 'info';
  }
};

const getStatusText = (status) => {
  switch (status) {
    case 'running':
      return '正常运行';
    case 'paused':
      return '临时停运';
    case 'maintenance':
      return '检修中';
    default:
      return '未知状态';
  }
};

const tableRowClassName = ({ row }) => {
  if (row.status === 'maintenance') {
    return 'warning-row';
  } else if (row.status === 'paused') {
    return 'paused-row';
  }
  return '';
};

const handleSearchClear = () => {
  currentPage.value = 1;
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
};

const resetForm = () => {
  trainForm.id = '';
  trainForm.trainNumber = '';
  trainForm.name = '';
  trainForm.type = '';
  trainForm.capacity = 0;
  trainForm.status = 'running';
  trainForm.startTime = '';
  trainForm.endTime = '';
};

const openAddDialog = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  isEdit.value = true;

  trainForm.id = row.id;
  trainForm.trainNumber = row.trainNumber;
  trainForm.name = row.name;
  trainForm.type = row.type;
  trainForm.capacity = row.capacity;
  trainForm.status = row.status;
  trainForm.startTime = row.startTime;
  trainForm.endTime = row.endTime;

  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!trainFormRef.value) return;

  await trainFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;

        // 格式化时间
        const formattedTrain = {
          ...trainForm,
          startTime:
            trainForm.startTime instanceof Date
              ? format(trainForm.startTime, 'HH:mm')
              : trainForm.startTime,
          endTime:
            trainForm.endTime instanceof Date
              ? format(trainForm.endTime, 'HH:mm')
              : trainForm.endTime,
        };

        if (isEdit.value) {
          // 模拟API调用 - 编辑
          // await fetch(`/api/admin/trains/${trainForm.id}`, {
          //   method: 'PUT',
          //   headers: { 'Content-Type': 'application/json' },
          //   body: JSON.stringify(formattedTrain)
          // })

          // 模拟数据更新
          const index = trains.value.findIndex((t) => t.id === trainForm.id);
          if (index !== -1) {
            trains.value[index] = { ...formattedTrain };
          }

          ElMessage.success('列车信息更新成功');
        } else {
          // 模拟API调用 - 新增
          // const response = await fetch('/api/admin/trains', {
          //   method: 'POST',
          //   headers: { 'Content-Type': 'application/json' },
          //   body: JSON.stringify(formattedTrain)
          // })
          // const data = await response.json()

          // 模拟数据新增
          const newTrain = {
            ...formattedTrain,
            id: trains.value.length + 1,
          };
          trains.value.push(newTrain);
          totalTrains.value += 1;

          ElMessage.success('列车添加成功');
        }

        dialogVisible.value = false;
        loading.value = false;
      } catch (error) {
        console.error('保存列车数据失败', error);
        ElMessage.error('保存列车数据失败');
        loading.value = false;
      }
    }
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除列车 ${row.trainNumber} - ${row.name} 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        loading.value = true;

        // 模拟API调用
        // await fetch(`/api/admin/trains/${row.id}`, {
        //   method: 'DELETE'
        // })

        // 模拟删除
        trains.value = trains.value.filter((t) => t.id !== row.id);
        totalTrains.value -= 1;

        ElMessage.success('列车删除成功');
        loading.value = false;
      } catch (error) {
        console.error('删除列车失败', error);
        ElMessage.error('删除列车失败');
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除操作
    });
};

// 时刻表相关方法
const handleSchedule = (row) => {
  currentTrain.value = row;
  scheduleDialogVisible.value = true;
  fetchScheduleData(row.id);
};

const fetchScheduleData = async (trainId) => {
  scheduleLoading.value = true;
  try {
    // 模拟API调用
    // const response = await fetch(`/api/admin/trains/${trainId}/schedule`)
    // const data = await response.json()
    // scheduleData.value = data.schedule

    // 模拟数据
    setTimeout(() => {
      const mockSchedule = [
        {
          id: 1,
          trainId: trainId,
          stationName: '中心站',
          arrivalTime: '06:00',
          departureTime: '06:05',
          stopDuration: 5,
        },
        {
          id: 2,
          trainId: trainId,
          stationName: '人民广场站',
          arrivalTime: '06:15',
          departureTime: '06:18',
          stopDuration: 3,
        },
        {
          id: 3,
          trainId: trainId,
          stationName: '大学城站',
          arrivalTime: '06:30',
          departureTime: '06:33',
          stopDuration: 3,
        },
        {
          id: 4,
          trainId: trainId,
          stationName: '体育中心站',
          arrivalTime: '06:45',
          departureTime: '06:48',
          stopDuration: 3,
        },
      ];

      scheduleData.value = mockSchedule;
      scheduleLoading.value = false;
    }, 500);
  } catch (error) {
    console.error('获取时刻表数据失败', error);
    ElMessage.error('获取时刻表数据失败');
    scheduleLoading.value = false;
  }
};

const resetScheduleForm = () => {
  scheduleForm.id = '';
  scheduleForm.trainId = currentTrain.value ? currentTrain.value.id : '';
  scheduleForm.stationName = '';
  scheduleForm.arrivalTime = '';
  scheduleForm.departureTime = '';
  scheduleForm.stopDuration = 1;
};

const addScheduleItem = () => {
  isEditSchedule.value = false;
  resetScheduleForm();
  scheduleItemDialogVisible.value = true;
};

const editScheduleItem = (row) => {
  isEditSchedule.value = true;

  scheduleForm.id = row.id;
  scheduleForm.trainId = row.trainId;
  scheduleForm.stationName = row.stationName;
  scheduleForm.arrivalTime = row.arrivalTime;
  scheduleForm.departureTime = row.departureTime;
  scheduleForm.stopDuration = row.stopDuration;

  scheduleItemDialogVisible.value = true;
};

const submitScheduleForm = async () => {
  if (!scheduleFormRef.value) return;

  await scheduleFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        scheduleLoading.value = true;

        // 格式化时间
        const formattedSchedule = {
          ...scheduleForm,
          arrivalTime:
            scheduleForm.arrivalTime instanceof Date
              ? format(scheduleForm.arrivalTime, 'HH:mm')
              : scheduleForm.arrivalTime,
          departureTime:
            scheduleForm.departureTime instanceof Date
              ? format(scheduleForm.departureTime, 'HH:mm')
              : scheduleForm.departureTime,
        };

        if (isEditSchedule.value) {
          // 模拟API调用 - 编辑
          // await fetch(`/api/admin/schedule/${scheduleForm.id}`, {
          //   method: 'PUT',
          //   headers: { 'Content-Type': 'application/json' },
          //   body: JSON.stringify(formattedSchedule)
          // })

          // 模拟数据更新
          const index = scheduleData.value.findIndex(
            (s) => s.id === scheduleForm.id
          );
          if (index !== -1) {
            scheduleData.value[index] = { ...formattedSchedule };
          }

          ElMessage.success('时刻表更新成功');
        } else {
          // 模拟API调用 - 新增
          // const response = await fetch('/api/admin/schedule', {
          //   method: 'POST',
          //   headers: { 'Content-Type': 'application/json' },
          //   body: JSON.stringify(formattedSchedule)
          // })
          // const data = await response.json()

          // 模拟数据新增
          const newSchedule = {
            ...formattedSchedule,
            id: scheduleData.value.length + 1,
          };
          scheduleData.value.push(newSchedule);

          ElMessage.success('时刻表添加成功');
        }

        scheduleItemDialogVisible.value = false;
        scheduleLoading.value = false;
      } catch (error) {
        console.error('保存时刻表数据失败', error);
        ElMessage.error('保存时刻表数据失败');
        scheduleLoading.value = false;
      }
    }
  });
};

const deleteScheduleItem = (row) => {
  ElMessageBox.confirm(
    `确定要删除 ${row.stationName} 的时刻表吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        scheduleLoading.value = true;

        // 模拟API调用
        // await fetch(`/api/admin/schedule/${row.id}`, {
        //   method: 'DELETE'
        // })

        // 模拟删除
        scheduleData.value = scheduleData.value.filter((s) => s.id !== row.id);

        ElMessage.success('时刻表删除成功');
        scheduleLoading.value = false;
      } catch (error) {
        console.error('删除时刻表失败', error);
        ElMessage.error('删除时刻表失败');
        scheduleLoading.value = false;
      }
    })
    .catch(() => {
      // 取消删除操作
    });
};
</script>

<style scoped>
.train-management-container {
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

.search-input {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 自定义表格行样式 */
:deep(.warning-row) {
  --el-table-tr-bg-color: rgba(250, 219, 219, 0.3);
}

:deep(.paused-row) {
  --el-table-tr-bg-color: rgba(250, 236, 216, 0.3);
}

/* 时刻表对话框 */
.schedule-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.schedule-dialog-header h3 {
  margin: 0;
  color: #303133;
}
</style>
