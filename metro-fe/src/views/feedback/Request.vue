<template>
  <div class="request-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="提交需求" name="submit">
        <el-card>
          <template #header>
            <div class="page-header">
              <h2>提交需求</h2>
            </div>
          </template>

          <div class="request-form-container">
            <el-form
              ref="requestFormRef"
              :model="requestForm"
              :rules="requestRules"
              label-position="top"
            >
              <el-form-item label="需求标题" prop="title">
                <el-input
                  v-model="requestForm.title"
                  placeholder="请输入需求标题"
                />
              </el-form-item>

              <el-form-item label="需求内容" prop="content">
                <el-input
                  v-model="requestForm.content"
                  type="textarea"
                  :rows="6"
                  placeholder="请详细描述您的需求，至少10个字"
                />
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  :loading="submitting"
                  @click="submitRequest"
                >
                  提交需求
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="我的需求" name="my">
        <el-card>
          <template #header>
            <div class="page-header">
              <h2>我的需求</h2>
              <el-button type="primary" @click="fetchMyRequests">
                刷新
              </el-button>
            </div>
          </template>

          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>

          <div
            v-else-if="feedbackStore.userRequests.length === 0"
            class="empty-data"
          >
            <el-empty description="您还没有提交过需求" />
          </div>

          <div v-else class="request-list">
            <el-timeline>
              <el-timeline-item
                v-for="request in feedbackStore.userRequests"
                :key="request.id"
                :timestamp="request.createdAt"
                :type="getStatusType(request.status)"
              >
                <el-card>
                  <div class="request-header">
                    <div>
                      <h3 class="request-title">{{ request.title }}</h3>
                      <el-tag
                        :type="getStatusType(request.status)"
                        effect="plain"
                      >
                        {{ getStatusText(request.status) }}
                      </el-tag>
                    </div>
                    <span class="request-user">{{ request.username }}</span>
                  </div>
                  <div class="request-content">
                    {{ request.content }}
                  </div>

                  <!-- Replies -->
                  <div
                    v-if="request.replies && request.replies.length"
                    class="request-replies"
                  >
                    <div
                      v-for="reply in request.replies"
                      :key="reply.id"
                      class="reply-item"
                      :class="{ 'admin-reply': reply.isAdmin }"
                    >
                      <div class="reply-header">
                        <span class="username">
                          {{ reply.username }}
                          <el-tag
                            v-if="reply.isAdmin"
                            size="small"
                            type="danger"
                            >管理员</el-tag
                          >
                        </span>
                        <span class="time">{{ reply.createdAt }}</span>
                      </div>
                      <div class="reply-content">
                        {{ reply.content }}
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- Request Detail Dialog -->
    <el-dialog v-model="dialogVisible" title="需求详情" width="600px">
      <div v-if="currentRequest" class="request-detail">
        <div class="request-header">
          <div>
            <h3 class="request-title">{{ currentRequest.title }}</h3>
            <el-tag :type="getStatusType(currentRequest.status)" effect="plain">
              {{ getStatusText(currentRequest.status) }}
            </el-tag>
          </div>
          <span class="request-user">{{ currentRequest.username }}</span>
        </div>

        <div class="request-content">
          {{ currentRequest.content }}
        </div>

        <div class="request-meta">
          <span>提交时间：{{ currentRequest.createdAt }}</span>
        </div>

        <!-- Replies -->
        <div
          v-if="currentRequest.replies && currentRequest.replies.length"
          class="request-replies"
        >
          <h4>处理记录</h4>
          <div
            v-for="reply in currentRequest.replies"
            :key="reply.id"
            class="reply-item"
            :class="{ 'admin-reply': reply.isAdmin }"
          >
            <div class="reply-header">
              <span class="username">
                {{ reply.username }}
                <el-tag v-if="reply.isAdmin" size="small" type="danger"
                  >管理员</el-tag
                >
              </span>
              <span class="time">{{ reply.createdAt }}</span>
            </div>
            <div class="reply-content">
              {{ reply.content }}
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useFeedbackStore } from '../../stores/feedback';
import { useUserStore } from '../../stores/user';
import type { FormInstance } from 'element-plus';

const userStore = useUserStore();
const feedbackStore = useFeedbackStore();
const activeTab = ref('submit');
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const currentRequest = ref<any>(null);
const requestFormRef = ref<FormInstance | null>(null);

// Request form
const requestForm = reactive({
  title: '',
  content: '',
});

// Form validation rules
const requestRules = reactive({
  title: [
    { required: true, message: '请输入需求标题', trigger: 'blur' },
    { min: 3, max: 50, message: '标题长度应为3-50个字符', trigger: 'blur' },
  ],
  content: [
    { required: true, message: '请输入需求内容', trigger: 'blur' },
    { min: 10, message: '需求内容不能少于10个字', trigger: 'blur' },
  ],
});

// Status helpers
const getStatusType = (status: string) => {
  switch (status) {
    case 'pending':
      return 'info';
    case 'processing':
      return 'warning';
    case 'resolved':
      return 'success';
    case 'rejected':
      return 'danger';
    default:
      return 'info';
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case 'pending':
      return '待处理';
    case 'processing':
      return '处理中';
    case 'resolved':
      return '已解决';
    case 'rejected':
      return '已拒绝';
    default:
      return '未知';
  }
};

// Load data on component mount
onMounted(async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再提交需求');
    activeTab.value = 'my';
  }

  if (userStore.isLoggedIn) {
    fetchMyRequests();
  }
});

// Fetch user's requests
const fetchMyRequests = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    return;
  }

  loading.value = true;

  try {
    await feedbackStore.fetchUserRequests();
  } catch (error) {
    console.error('Fetch user requests error:', error);
    ElMessage.error('获取个人需求失败');
  } finally {
    loading.value = false;
  }
};

// Submit new request
const submitRequest = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再提交需求');
    return;
  }

  // Validate form first
  if (!requestFormRef.value) return;
  const valid = await requestFormRef.value.validate().catch(() => false);
  if (!valid) {
    ElMessage.error('请正确填写表单');
    return;
  }

  submitting.value = true;

  try {
    const success = await feedbackStore.createRequest({
      title: requestForm.title,
      content: requestForm.content,
    });

    if (success) {
      ElMessage.success('需求提交成功');
      requestForm.title = '';
      requestForm.content = '';
      activeTab.value = 'my';
      await fetchMyRequests();
    } else {
      ElMessage.error('需求提交失败');
    }
  } catch (error) {
    console.error('Submit request error:', error);
    ElMessage.error('需求提交过程中发生错误');
  } finally {
    submitting.value = false;
  }
};

// View request detail
const viewRequestDetail = (request: any) => {
  currentRequest.value = request;
  dialogVisible.value = true;
};
</script>

<style scoped lang="scss">
.request-container {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h2 {
      margin: 0;
      font-size: 1.5rem;
      color: #303133;
    }
  }

  .loading-container {
    padding: 20px 0;
  }

  .empty-data {
    padding: 40px 0;
  }

  .request-form-container {
    max-width: 600px;
    margin: 0 auto;
  }

  .request-list {
    .request-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 15px;

      .request-title {
        margin: 0 0 8px 0;
        font-size: 18px;
        color: #303133;
      }

      .request-user {
        color: #909399;
        font-size: 14px;
      }
    }

    .request-content {
      margin-bottom: 15px;
      color: #303133;
      line-height: 1.6;
    }

    .request-meta {
      color: #909399;
      font-size: 14px;
      margin-bottom: 15px;
    }

    .request-replies {
      background-color: #f5f7fa;
      border-radius: 4px;
      padding: 15px;
      margin-top: 15px;

      h4 {
        margin-top: 0;
        margin-bottom: 10px;
        color: #303133;
      }

      .reply-item {
        padding: 10px;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        &.admin-reply {
          background-color: rgba(64, 158, 255, 0.1);
        }

        .reply-header {
          display: flex;
          justify-content: space-between;
          margin-bottom: 5px;

          .username {
            font-weight: bold;
            color: #409eff;
            display: flex;
            align-items: center;

            .el-tag {
              margin-left: 5px;
            }
          }

          .time {
            font-size: 12px;
            color: #909399;
          }
        }

        .reply-content {
          color: #606266;
          line-height: 1.6;
        }
      }
    }
  }

  .request-detail {
    .request-header,
    .request-content,
    .request-meta,
    .request-replies {
      margin-bottom: 20px;
    }
  }
}
</style>
