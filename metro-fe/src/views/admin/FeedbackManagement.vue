<template>
  <div class="feedback-management-container">
    <h1 class="page-title">评价管理</h1>

    <div class="feedback-filters">
      <el-input
        v-model="searchQuery"
        placeholder="搜索评价内容"
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
        v-model="filterRating"
        placeholder="按评分筛选"
        clearable
        @change="filterFeedbacks"
        style="width: 150px"
      >
        <el-option v-for="i in 5" :key="i" :label="`${i}星`" :value="i" />
      </el-select>
    </div>

    <el-table
      v-loading="loading"
      :data="filteredFeedbacks"
      style="width: 100%; margin-top: 20px"
      border
    >
      <el-table-column prop="username" label="用户" width="120" />
      <el-table-column
        prop="content"
        label="评价内容"
        min-width="250"
        show-overflow-tooltip
      />
      <el-table-column prop="rating" label="评分" width="100">
        <template #default="scope">
          <el-rate v-model="scope.row.rating" disabled text-color="#ff9900" />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="提交时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="showReplyDialog(scope.row)"
          >
            回复
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="feedbacks.length > pageSize"
      background
      layout="prev, pager, next"
      :total="feedbacks.length"
      :page-size="pageSize"
      :current-page="currentPage"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; display: flex; justify-content: flex-end"
    />

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="回复评价" width="600px">
      <div v-if="currentFeedback" class="feedback-detail">
        <div class="feedback-user">
          <span class="username">{{ currentFeedback.username }}</span>
          <el-rate v-model="currentFeedback.rating" disabled />
          <span class="time">{{ formatDate(currentFeedback.createdAt) }}</span>
        </div>
        <div class="feedback-content">{{ currentFeedback.content }}</div>

        <div
          class="replies"
          v-if="currentFeedback.replies && currentFeedback.replies.length > 0"
        >
          <h4>历史回复</h4>
          <div
            v-for="reply in currentFeedback.replies"
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
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply" :loading="submitting">
            提交回复
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useFeedbackStore } from '../../stores/feedback';
import { format } from 'date-fns';

const feedbackStore = useFeedbackStore();
const loading = ref(false);
const searchQuery = ref('');
const filterRating = ref('');
const currentPage = ref(1);
const pageSize = ref(10);

// 回复对话框
const replyDialogVisible = ref(false);
const currentFeedback = ref(null);
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

// 获取所有评价
const fetchFeedbacks = async () => {
  loading.value = true;
  try {
    await feedbackStore.fetchFeedbacks();
  } catch (error) {
    console.error('获取评价失败:', error);
    ElMessage.error('获取评价数据失败');
  } finally {
    loading.value = false;
  }
};

// 计算属性：筛选后的评价
const filteredFeedbacks = computed(() => {
  let result = [...feedbackStore.feedbacks];

  // 按评分筛选
  if (filterRating.value) {
    result = result.filter(
      (feedback) => feedback.rating === filterRating.value
    );
  }

  // 按内容搜索
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (feedback) =>
        feedback.content.toLowerCase().includes(query) ||
        feedback.username.toLowerCase().includes(query)
    );
  }

  // 分页
  const startIndex = (currentPage.value - 1) * pageSize.value;
  return result.slice(startIndex, startIndex + pageSize.value);
});

// 获取完整评价列表
const feedbacks = computed(() => feedbackStore.feedbacks);

// 事件处理
const handleSearch = () => {
  currentPage.value = 1;
};

const handleClear = () => {
  searchQuery.value = '';
  currentPage.value = 1;
};

const filterFeedbacks = () => {
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

// 显示回复对话框
const showReplyDialog = (feedback) => {
  currentFeedback.value = feedback;
  replyForm.value.content = '';
  replyDialogVisible.value = true;
};

// 提交回复
const submitReply = async () => {
  if (!replyFormRef.value) return;

  await replyFormRef.value.validate(async (valid) => {
    if (!valid) return;

    submitting.value = true;
    try {
      const success = await feedbackStore.replyToFeedback(
        currentFeedback.value.id,
        replyForm.value.content
      );

      if (success) {
        ElMessage.success('回复成功');
        replyDialogVisible.value = false;
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

// 删除评价
const handleDelete = (feedback) => {
  ElMessageBox.confirm('确定要删除此评价吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      loading.value = true;
      try {
        // 这里需要在 feedbackStore 中添加删除评价的方法
        const success = await feedbackStore.deleteFeedback(feedback.id);
        if (success) {
          ElMessage.success('删除成功');
        } else {
          ElMessage.error('删除失败');
        }
      } catch (error) {
        console.error('删除失败:', error);
        ElMessage.error('删除评价时出错');
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除
    });
};

onMounted(() => {
  fetchFeedbacks();
});
</script>

<style scoped>
.feedback-management-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.feedback-filters {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.feedback-detail {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f7f7f7;
  border-radius: 6px;
}

.feedback-user {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.username {
  font-weight: bold;
  margin-right: 10px;
}

.time {
  margin-left: 10px;
  color: #999;
  font-size: 13px;
}

.feedback-content {
  line-height: 1.5;
  margin-bottom: 15px;
}

.replies {
  margin-top: 15px;
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

.reply-content {
  color: #333;
  line-height: 1.5;
}
</style>
