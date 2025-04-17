<template>
  <div class="station-management-container">
    <h1 class="page-title">站点管理</h1>

    <div class="control-panel">
      <el-input
        v-model="searchQuery"
        placeholder="搜索站点名称或编号"
        class="search-input"
        clearable
        @clear="handleSearchClear"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>添加站点
      </el-button>
    </div>

    <el-table :data="filteredStations" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="站点名称" width="180" />
      <el-table-column prop="code" label="站点编号" width="120" />
      <el-table-column prop="address" label="地址" width="200" />
      <el-table-column label="是否换乘站" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isTransferStation ? 'success' : 'info'">
            {{ scope.row.isTransferStation ? '是' : '否' }}
          </el-tag>
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
      :total="totalStations"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 添加/编辑站点对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑站点' : '添加站点'"
      width="50%"
    >
      <el-form
        :model="stationForm"
        label-width="120px"
        :rules="rules"
        ref="stationFormRef"
      >
        <el-form-item label="站点名称" prop="name">
          <el-input v-model="stationForm.name" placeholder="例如：人民广场站" />
        </el-form-item>
        <el-form-item label="站点编号" prop="code">
          <el-input v-model="stationForm.code" placeholder="例如：S001" />
        </el-form-item>
        <el-form-item label="站点地址" prop="address">
          <el-input
            v-model="stationForm.address"
            placeholder="例如：人民广场1号"
          />
        </el-form-item>
        <el-form-item label="是否换乘站" prop="isTransferStation">
          <el-switch v-model="stationForm.isTransferStation" />
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
import { Search, Plus } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';
import { useMetroStore } from '../../stores/metro';
import type { Station, StationDto } from '../../api/modules/metro';

// 引入 metro store
const metroStore = useMetroStore();

// 站点数据
const loading = ref(true);
const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');

// 分页和搜索过滤后的站点数据
const filteredStations = computed(() => {
  if (!searchQuery.value) {
    return metroStore.stations.slice(
      (currentPage.value - 1) * pageSize.value,
      currentPage.value * pageSize.value
    );
  }

  const filtered = metroStore.stations.filter(
    (station) =>
      station.name.includes(searchQuery.value) ||
      station.code.includes(searchQuery.value)
  );

  return filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  );
});

// 总站点数
const totalStations = computed(() => {
  if (!searchQuery.value) {
    return metroStore.stations.length;
  }
  return metroStore.stations.filter(
    (station) =>
      station.name.includes(searchQuery.value) ||
      station.code.includes(searchQuery.value)
  ).length;
});

// 对话框控制
const dialogVisible = ref(false);
const isEdit = ref(false);
const stationFormRef = ref<FormInstance | null>(null);

// 站点表单
const stationForm = reactive<StationDto>({
  id: null,
  name: '',
  code: '',
  address: '',
  isTransferStation: false,
  lineIds: [],
});

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入站点名称', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入站点编号', trigger: 'blur' },
    { max: 20, message: '长度不能超过20个字符', trigger: 'blur' },
  ],
  address: [{ required: true, message: '请输入站点地址', trigger: 'blur' }],
};

// 生命周期钩子
onMounted(async () => {
  loading.value = true;
  await metroStore.fetchStations();
  loading.value = false;
});

// 方法
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
  stationForm.id = null;
  stationForm.name = '';
  stationForm.code = '';
  stationForm.address = '';
  stationForm.isTransferStation = false;
  stationForm.lineIds = [];
};

const openAddDialog = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: Station) => {
  isEdit.value = true;

  stationForm.id = row.id;
  stationForm.name = row.name;
  stationForm.code = row.code;
  stationForm.address = row.address || '';
  stationForm.isTransferStation = row.isTransferStation;
  stationForm.lineIds = row.lineIds || [];

  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!stationFormRef.value) return;

  await stationFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;

        if (
          isEdit.value &&
          stationForm.id !== null &&
          typeof stationForm.id === 'number'
        ) {
          const updatedStation = await metroStore.updateStation(
            stationForm.id,
            stationForm
          );
          if (updatedStation) {
            ElMessage.success('站点信息更新成功');
            dialogVisible.value = false;
          } else {
            ElMessage.error(metroStore.error || '站点信息更新失败');
          }
        } else {
          const newStation = await metroStore.createStation(stationForm);
          if (newStation) {
            ElMessage.success('站点添加成功');
            dialogVisible.value = false;
          } else {
            ElMessage.error(metroStore.error || '站点添加失败');
          }
        }
      } catch (error) {
        console.error('保存站点数据失败', error);
        ElMessage.error('保存站点数据失败');
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleDelete = (row: Station) => {
  ElMessageBox.confirm(`确定要删除站点 ${row.name} 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        loading.value = true;

        if (typeof row.id !== 'number') {
          ElMessage.error('站点ID无效');
          return;
        }

        const success = await metroStore.deleteStation(row.id);

        if (success) {
          ElMessage.success('站点删除成功');
        } else {
          ElMessage.error(metroStore.error || '站点删除失败');
        }
      } catch (error) {
        console.error('删除站点失败', error);
        ElMessage.error('删除站点失败');
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
.station-management-container {
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
</style>
