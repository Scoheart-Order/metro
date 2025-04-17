<template>
  <div class="request-management-container">
    <h1 class="page-title">需求管理</h1>

    <div class="request-filters">
      <el-input
        v-model="searchQuery"
        placeholder="搜索需求标题或内容"
        clearable
        @clear="handleClear"
        @input="handleSearch"
        style="width: 300px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select
        v-model="filterStatus"
        placeholder="按状态筛选"
        clearable
        @change="filterRequests"
        style="width: 150px"
      >
        <el-option label="待处理" value="pending" />
        <el-option label="处理中" value="processing" />
        <el-option label="已解决" value="resolved" />
        <el-option label="已拒绝" value="rejected" />
      </el-select>
    </div>

    <el-table
      v-loading="loading"
      :data="filteredRequests"
      style="width: 100%; margin-top: 20px"
      border
    >
      <el-table-column prop="username" label="用户" width="120" />
      <el-table-column
        prop="title"
        label="需求标题"
        min-width="180"
        show-overflow-tooltip
      />
      <el-table-column
        prop="content"
        label="需求内容"
        min-width="250"
        show-overflow-tooltip
      />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="提交时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="showDetailDialog(scope.row)"
          >
            回复
          </el-button>
          <el-button
            type="success"
            size="small"
            @click="showStatusDialog(scope.row)"
            :disabled="scope.row.status === 'resolved'"
          >
            更新状态
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="requests.length > pageSize"
      background
      layout="prev, pager, next"
      :total="requests.length"
      :page-size="pageSize"
      :current-page="currentPage"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; display: flex; justify-content: flex-end"
    />

    <!-- 详情与回复对话框 -->
    <el-dialog v-model="detailDialogVisible" title="需求详情" width="700px">
      <div v-if="currentRequest" class="request-detail">
        <div class="request-header">
          <h3 class="request-title">{{ currentRequest.title }}</h3>
          <el-tag :type="getStatusType(currentRequest.status)">
            {{ getStatusText(currentRequest.status) }}
          </el-tag>
        </div>

        <div class="request-info">
          <span class="username">提交人: {{ currentRequest.username }}</span>
          <span class="time"
            >提交时间: {{ formatDate(currentRequest.createdAt) }}</span
          >
        </div>

        <div class="request-content">{{ currentRequest.content }}</div>

        <div
          class="replies"
          v-if="currentRequest.replies && currentRequest.replies.length > 0"
        >
          <h4>历史回复</h4>
          <div
            v-for="reply in currentRequest.replies"
            :key="reply.id"
            class="reply-item"
          >
            <div class="reply-user">
              <span class="username">{{ reply.username }}</span>
              <el-tag v-if="reply.isAdmin" size="small" type="danger"
                >管理员</el-tag
              >
              <span class="time">{{ formatDate(reply.createdAt) }}</span>
            </div>
            <div class="reply-content">{{ reply.content }}</div>
          </div>
        </div>
      </div>

      <el-form :model="replyForm" ref="replyFormRef" :rules="replyRules">
        <el-form-item prop="content" label="回复内容">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="submitReply" :loading="submitting">
            提交回复
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 状态更新对话框 -->
    <el-dialog v-model="statusDialogVisible" title="更新需求状态" width="500px">
      <div v-if="currentRequest" class="status-update">
        <p>需求: {{ currentRequest.title }}</p>
        <p>当前状态: {{ getStatusText(currentRequest.status) }}</p>

        <el-form :model="statusForm" label-width="100px">
          <el-form-item label="新状态">
            <el-select v-model="statusForm.status" style="width: 100%">
              <el-option label="待处理" value="pending" />
              <el-option label="处理中" value="processing" />
              <el-option label="已解决" value="resolved" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateStatus" :loading="updating">
            更新状态
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { useFeedbackStore } from '../../stores/feedback';
import { format } from 'date-fns';

const feedbackStore = useFeedbackStore();
const loading = ref(false);
const searchQuery = ref('');
const filterStatus = ref('');
const currentPage = ref(1);
const pageSize = ref(10);

// 详情对话框
const detailDialogVisible = ref(false);
const currentRequest = ref(null);
const replyForm = ref({
  content: '',
});
const replyFormRef = ref(null);
const replyRules = {
  content: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 2, max: 500, message: '长度在 2 到 500 个字符', trigger: 'blur' },
  ],
};
const submitting = ref(false);

// 状态更新对话框
const statusDialogVisible = ref(false);
const statusForm = ref({
  status: '',
});
const updating = ref(false);

// 获取所有需求
const fetchRequests = async () => {
  loading.value = true;
  try {
    await feedbackStore.fetchRequests();
  } catch (error) {
    console.error('获取需求失败:', error);
    ElMessage.error('获取需求数据失败');
  } finally {
    loading.value = false;
  }
};

// 计算属性：筛选后的需求
const filteredRequests = computed(() => {
  let result = [...feedbackStore.requests];

  // 按状态筛选
  if (filterStatus.value) {
    result = result.filter((request) => request.status === filterStatus.value);
  }

  // 按内容搜索
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (request) =>
        request.title.toLowerCase().includes(query) ||
        request.content.toLowerCase().includes(query) ||
        request.username.toLowerCase().includes(query)
    );
  }

  // 分页
  const startIndex = (currentPage.value - 1) * pageSize.value;
  return result.slice(startIndex, startIndex + pageSize.value);
});

// 获取完整需求列表
const requests = computed(() => feedbackStore.requests);

// 事件处理
const handleSearch = () => {
  currentPage.value = 1;
};

const handleClear = () => {
  searchQuery.value = '';
  currentPage.value = 1;
};

const filterRequests = () => {
  currentPage.value = 1;
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
};

const formatDate = (dateStr) => {
  try {
    return format(new Date(dateStr), 'yyyy-MM-dd HH:mm');
  } catch {
    return dateStr;
  }
};

// 获取状态标签颜色
const getStatusType = (status) => {
  const types = {
    pending: 'info',
    processing: 'warning',
    resolved: 'success',
    rejected: 'danger',
  };
  return types[status] || 'info';
};

// 获取状态中文名
const getStatusText = (status) => {
  const texts = {
    pending: '待处理',
    processing: '处理中',
    resolved: '已解决',
    rejected: '已拒绝',
  };
  return texts[status] || status;
};

// 显示详情对话框
const showDetailDialog = (request) => {
  currentRequest.value = request;
  replyForm.value.content = '';
  detailDialogVisible.value = true;
};

// 显示状态更新对话框
const showStatusDialog = (request) => {
  currentRequest.value = request;
  statusForm.value.status = request.status;
  statusDialogVisible.value = true;
};

// 提交回复
const submitReply = async () => {
  if (!replyFormRef.value) return;

  await replyFormRef.value.validate(async (valid) => {
    if (!valid) return;

    submitting.value = true;
    try {
      const success = await feedbackStore.replyToRequest(
        currentRequest.value.id,
        replyForm.value.content
      );

      if (success) {
        ElMessage.success('回复成功');
        replyForm.value.content = '';
      } else {
        ElMessage.error('回复失败');
      }
    } catch (error) {
      console.error('回复失败:', error);
      ElMessage.error('提交回复时出错');
    } finally {
      submitting.value = false;
    }
  });
};

// 更新状态
const updateStatus = async () => {
  if (!currentRequest.value || !statusForm.value.status) return;

  updating.value = true;
  try {
    const success = await feedbackStore.updateRequestStatus(
      currentRequest.value.id,
      statusForm.value.status
    );

    if (success) {
      ElMessage.success('状态更新成功');
      statusDialogVisible.value = false;

      // 更新当前显示的需求状态
      if (currentRequest.value) {
        currentRequest.value.status = statusForm.value.status;
      }
    } else {
      ElMessage.error('状态更新失败');
    }
  } catch (error) {
    console.error('状态更新失败:', error);
    ElMessage.error('更新状态时出错');
  } finally {
    updating.value = false;
  }
};

onMounted(() => {
  fetchRequests();
});
</script>

<style scoped>
.request-management-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.request-filters {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.request-detail {
  margin-bottom: 20px;
}

.request-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.request-title {
  margin: 0;
  font-size: 18px;
}

.request-info {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
}

.request-content {
  background-color: #f7f7f7;
  padding: 15px;
  border-radius: 6px;
  line-height: 1.6;
  margin-bottom: 20px;
}

.replies {
  margin-top: 20px;
  border-top: 1px solid #eaeaea;
  padding-top: 15px;
}

.replies h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #606266;
}

.reply-item {
  padding: 8px 0;
  border-bottom: 1px dashed #eaeaea;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-user {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.username {
  font-weight: bold;
  margin-right: 10px;
}

.time {
  color: #999;
}

.reply-content {
  color: #333;
  line-height: 1.5;
}

.status-update {
  margin-bottom: 20px;
}
</style>
