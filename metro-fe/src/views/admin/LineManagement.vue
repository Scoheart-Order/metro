<template>
  <div class="line-management-container">
    <h1 class="page-title">线路管理</h1>

    <div class="control-panel">
      <el-input
        v-model="searchQuery"
        placeholder="搜索线路编号或名称"
        class="search-input"
        clearable
        @clear="handleSearchClear"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>添加线路
      </el-button>
    </div>

    <el-table :data="filteredLines" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="线路名称" width="100" />
      <el-table-column prop="code" label="线路编号" width="80" />
      <el-table-column label="线路颜色" width="150">
        <template #default="scope">
          <div
            class="color-badge"
            :style="{ backgroundColor: scope.row.color }"
          ></div>
          {{ scope.row.color }}
        </template>
      </el-table-column>
      <el-table-column prop="operator" label="运营商" width="150" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <el-button
            size="small"
            type="success"
            @click="handleRoutes(scope.row)"
          >
            路线
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
      :total="totalLines"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 添加/编辑线路对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑线路' : '添加线路'"
      width="50%"
    >
      <el-form
        :model="lineForm"
        label-width="120px"
        :rules="rules"
        ref="lineFormRef"
      >
        <el-form-item label="线路名称" prop="name">
          <el-input v-model="lineForm.name" placeholder="例如：1号线" />
        </el-form-item>
        <el-form-item label="线路编号" prop="code">
          <el-input v-model="lineForm.code" placeholder="例如：L01" />
        </el-form-item>
        <el-form-item label="线路颜色" prop="color">
          <el-color-picker v-model="lineForm.color" show-alpha />
        </el-form-item>
        <el-form-item label="运营商" prop="operator">
          <el-input
            v-model="lineForm.operator"
            placeholder="例如：城市地铁集团"
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
import { Search, Plus } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import type { FormInstance } from 'element-plus';
import { useMetroStore } from '../../stores/metro';
import type { Line, LineDto } from '../../api/modules/metro';

const router = useRouter();
const metroStore = useMetroStore();

// 线路数据
const loading = ref(true);
const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');

// 分页和搜索过滤后的线路数据
const filteredLines = computed(() => {
  if (!searchQuery.value) {
    return metroStore.lines.slice(
      (currentPage.value - 1) * pageSize.value,
      currentPage.value * pageSize.value
    );
  }

  const filtered = metroStore.lines.filter(
    (line) =>
      line.name.includes(searchQuery.value) ||
      line.code.includes(searchQuery.value)
  );

  return filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  );
});

// 获取线路总数
const totalLines = computed(() => {
  if (!searchQuery.value) {
    return metroStore.lines.length;
  }
  return metroStore.lines.filter(
    (line) =>
      line.name.includes(searchQuery.value) ||
      line.code.includes(searchQuery.value)
  ).length;
});

// 对话框控制
const dialogVisible = ref(false);
const isEdit = ref(false);
const lineFormRef = ref<FormInstance | null>(null);

// 线路表单
const lineForm = reactive<LineDto>({
  id: null as number | null,
  name: '',
  code: '',
  color: '#409EFF',
  operator: '',
});

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入线路名称', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入线路编号', trigger: 'blur' },
    { max: 20, message: '长度不能超过20个字符', trigger: 'blur' },
  ],
  color: [{ required: true, message: '请选择线路颜色', trigger: 'change' }],
  operator: [
    { required: true, message: '请输入运营商', trigger: 'blur' },
    { max: 100, message: '长度不能超过100个字符', trigger: 'blur' },
  ],
};

// 生命周期钩子
onMounted(async () => {
  loading.value = true;
  await metroStore.fetchLines();
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
  lineForm.id = null;
  lineForm.name = '';
  lineForm.code = '';
  lineForm.color = '#409EFF';
  lineForm.operator = '';
};

const openAddDialog = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: Line) => {
  isEdit.value = true;

  lineForm.id = row.id;
  lineForm.name = row.name;
  lineForm.code = row.code;
  lineForm.color = row.color;
  lineForm.operator = row.operator;

  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!lineFormRef.value) return;

  await lineFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;

      try {
        if (isEdit.value && lineForm.id !== null) {
          const updatedLine = await metroStore.updateLine(
            lineForm.id as number,
            lineForm
          );
          if (updatedLine) {
            ElMessage.success('线路信息更新成功');
            dialogVisible.value = false;
          } else {
            ElMessage.error(metroStore.error || '线路信息更新失败');
          }
        } else {
          const newLine = await metroStore.createLine(lineForm);
          if (newLine) {
            ElMessage.success('线路添加成功');
            dialogVisible.value = false;
          } else {
            ElMessage.error(metroStore.error || '线路添加失败');
          }
        }
      } catch (error) {
        console.error('保存线路数据失败', error);
        ElMessage.error('保存线路数据失败');
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleDelete = (row: Line) => {
  ElMessageBox.confirm(`确定要删除线路 ${row.name} 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      loading.value = true;

      try {
        const success = await metroStore.deleteLine(row.id);

        if (success) {
          ElMessage.success('线路删除成功');
        } else {
          ElMessage.error(metroStore.error || '线路删除失败');
        }
      } catch (error) {
        console.error('删除线路失败', error);
        ElMessage.error('删除线路失败');
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除操作
    });
};

const handleRoutes = (row: Line) => {
  router.push(`/admin/line/${row.id}/routes`);
};
</script>

<style scoped>
.line-management-container {
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

.color-badge {
  display: inline-block;
  width: 20px;
  height: 20px;
  border-radius: 4px;
  margin-right: 8px;
  vertical-align: middle;
}
</style>
