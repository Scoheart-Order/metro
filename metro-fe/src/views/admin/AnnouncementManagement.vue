<template>
  <div class="announcement-management-container">
    <h1 class="page-title">公告管理</h1>

    <div class="control-panel">
      <el-input
        v-model="searchQuery"
        placeholder="搜索公告标题"
        class="search-input"
        clearable
        @clear="handleSearchClear"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>发布公告
      </el-button>
    </div>

    <el-table
      :data="filteredAnnouncements"
      style="width: 100%"
      v-loading="announcementStore.loading"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="公告标题" min-width="180" />
      <el-table-column label="发布时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
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
      :total="totalAnnouncements"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 添加/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑公告' : '发布公告'"
      width="60%"
    >
      <el-form
        :model="announcementForm"
        label-width="100px"
        :rules="rules"
        ref="announcementFormRef"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input
            v-model="announcementForm.title"
            placeholder="请输入公告标题"
          />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入公告内容"
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
import { format } from 'date-fns';
import { useAnnouncementStore } from '../../stores/announcement';
import { type Announcement, type AnnouncementDto } from '../../api';

// 使用Store
const announcementStore = useAnnouncementStore();

// 公告数据状态
const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');

// 分页和搜索过滤后的公告数据
const filteredAnnouncements = computed(() => {
  let filtered = announcementStore.announcements;

  if (searchQuery.value) {
    filtered = announcementStore.announcements.filter((announcement) =>
      announcement.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }

  const totalFiltered = filtered.length;

  return filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  );
});

// 计算总公告数
const totalAnnouncements = computed(() => {
  if (searchQuery.value) {
    return announcementStore.announcements.filter((announcement) =>
      announcement.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    ).length;
  }
  return announcementStore.announcements.length;
});

// 对话框控制
const dialogVisible = ref(false);
const isEdit = ref(false);
const announcementFormRef = ref(null);

// 公告表单
const announcementForm = reactive({
  id: 0,
  title: '',
  content: '',
});

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { max: 100, message: '标题长度不能超过100个字符', trigger: 'blur' },
  ],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
};

// 生命周期钩子
onMounted(async () => {
  await announcementStore.fetchAnnouncements();
});

// 方法
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-';
  try {
    return format(new Date(dateTime), 'yyyy-MM-dd HH:mm');
  } catch {
    return dateTime;
  }
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
  announcementForm.id = 0;
  announcementForm.title = '';
  announcementForm.content = '';
};

const openAddDialog = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: Announcement) => {
  isEdit.value = true;

  announcementForm.id = row.id;
  announcementForm.title = row.title;
  announcementForm.content = row.content;

  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!announcementFormRef.value) return;

  (announcementFormRef.value as any).validate(async (valid: boolean) => {
    if (valid) {
      try {
        const announcementData: AnnouncementDto = {
          title: announcementForm.title,
          content: announcementForm.content,
        };

        let success;

        if (isEdit.value) {
          success = await announcementStore.updateAnnouncement(
            announcementForm.id,
            announcementData
          );
        } else {
          success = await announcementStore.createAnnouncement(
            announcementData
          );
        }

        if (success) {
          ElMessage.success(isEdit.value ? '公告更新成功' : '公告发布成功');
          dialogVisible.value = false;
        } else if (announcementStore.error) {
          ElMessage.error(announcementStore.error);
        }
      } catch (error) {
        console.error('保存公告数据失败', error);
        ElMessage.error('保存公告数据失败');
      }
    }
  });
};

const handleDelete = (row: Announcement) => {
  ElMessageBox.confirm(`确定要删除公告"${row.title}"吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      const success = await announcementStore.deleteAnnouncement(row.id);

      if (success) {
        ElMessage.success('公告删除成功');
      } else if (announcementStore.error) {
        ElMessage.error(announcementStore.error);
      }
    })
    .catch(() => {
      // 取消删除操作
    });
};
</script>

<style scoped>
.announcement-management-container {
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
