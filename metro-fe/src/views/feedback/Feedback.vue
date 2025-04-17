<template>
  <div class="feedback-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="发表评价" name="submit">
        <el-card>
          <template #header>
            <div class="page-header">
              <h2>发表评价</h2>
            </div>
          </template>

          <div class="feedback-form-container">
            <el-form
              ref="feedbackFormRef"
              :model="feedbackForm"
              :rules="feedbackRules"
              label-position="top"
            >
              <el-form-item label="满意度评分" prop="rating">
                <el-rate
                  v-model="feedbackForm.rating"
                  :colors="rateColors"
                  show-text
                  :texts="rateTexts"
                />
              </el-form-item>

              <el-form-item label="评价内容" prop="content">
                <el-input
                  v-model="feedbackForm.content"
                  type="textarea"
                  :rows="5"
                  placeholder="请输入您的评价内容，至少5个字"
                />
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  :loading="submitting"
                  @click="submitFeedback"
                >
                  提交评价
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="我的评价" name="my">
        <el-card>
          <template #header>
            <div class="page-header">
              <h2>我的评价</h2>
              <el-button type="primary" @click="fetchMyFeedbacks">
                刷新
              </el-button>
            </div>
          </template>

          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>

          <div
            v-else-if="feedbackStore.userFeedbacks.length === 0"
            class="empty-data"
          >
            <el-empty description="您还没有发表过评价" />
          </div>

          <div v-else class="feedback-list">
            <el-timeline>
              <el-timeline-item
                v-for="feedback in feedbackStore.userFeedbacks"
                :key="feedback.id"
                :timestamp="feedback.createdAt"
                type="primary"
              >
                <el-card>
                  <div class="feedback-header">
                    <span class="username">{{ feedback.username }}</span>
                    <el-rate
                      v-model="feedback.rating"
                      disabled
                      :colors="rateColors"
                    />
                  </div>
                  <div class="feedback-content">
                    {{ feedback.content }}
                  </div>

                  <!-- Replies -->
                  <div
                    v-if="feedback.replies && feedback.replies.length"
                    class="feedback-replies"
                  >
                    <div
                      v-for="reply in feedback.replies"
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
const replying = ref(false);
const replyingToId = ref<string | number | null>(null);
const replyContent = ref('');
const feedbackFormRef = ref<FormInstance | null>(null);

// Rate options
const rateColors = ['#F56C6C', '#E6A23C', '#E6A23C', '#67C23A', '#67C23A'];
const rateTexts = ['很差', '较差', '一般', '较好', '很好'];

// Feedback form
const feedbackForm = reactive({
  content: '',
  rating: 3,
});

// Form validation rules
const feedbackRules = reactive({
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 5, message: '评价内容不能少于5个字', trigger: 'blur' },
  ],
  rating: [{ required: true, message: '请给出您的评分', trigger: 'change' }],
});

// Load data on component mount
onMounted(async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再发表评价');
    activeTab.value = 'my';
  }

  if (userStore.isLoggedIn) {
    fetchMyFeedbacks();
  }
});

// Fetch user's feedbacks
const fetchMyFeedbacks = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    return;
  }

  loading.value = true;

  try {
    await feedbackStore.fetchUserFeedbacks();
  } catch (error) {
    console.error('Fetch user feedbacks error:', error);
    ElMessage.error('获取个人评价失败');
  } finally {
    loading.value = false;
  }
};

// Submit new feedback
const submitFeedback = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再发表评价');
    return;
  }

  // Validate form first
  if (!feedbackFormRef.value) return;
  const valid = await feedbackFormRef.value.validate().catch(() => false);
  if (!valid) {
    ElMessage.error('请正确填写表单');
    return;
  }

  submitting.value = true;

  try {
    const success = await feedbackStore.createFeedback({
      content: feedbackForm.content,
      rating: feedbackForm.rating,
    });

    if (success) {
      ElMessage.success('评价提交成功');
      feedbackForm.content = '';
      feedbackForm.rating = 3;
      activeTab.value = 'my';
    } else {
      ElMessage.error('评价提交失败');
    }
  } catch (error) {
    console.error('Submit feedback error:', error);
    ElMessage.error('评价提交过程中发生错误');
  } finally {
    submitting.value = false;
  }
};

// Reply functions
const startReply = (feedbackId: string | number) => {
  replyingToId.value = feedbackId;
  replyContent.value = '';
};

const cancelReply = () => {
  replyingToId.value = null;
  replyContent.value = '';
};

const submitReply = async (feedbackId: string | number) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('回复内容不能为空');
    return;
  }

  replying.value = true;

  try {
    // Convert feedbackId to number if it's a string
    const numericId =
      typeof feedbackId === 'string' ? parseInt(feedbackId) : feedbackId;
    const success = await feedbackStore.replyToFeedback(
      numericId,
      replyContent.value
    );

    if (success) {
      ElMessage.success('回复成功');
      replyingToId.value = null;
      replyContent.value = '';
    } else {
      ElMessage.error('回复失败');
    }
  } catch (error) {
    console.error('Submit reply error:', error);
    ElMessage.error('回复过程中发生错误');
  } finally {
    replying.value = false;
  }
};
</script>

<style scoped lang="scss">
.feedback-container {
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

  .feedback-form-container {
    max-width: 600px;
    margin: 0 auto;
  }

  .feedback-list {
    .feedback-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;

      .username {
        font-weight: bold;
        color: #409eff;
      }
    }

    .feedback-content {
      margin-bottom: 15px;
      color: #303133;
      line-height: 1.6;
    }

    .feedback-replies {
      background-color: #f5f7fa;
      border-radius: 4px;
      padding: 10px;
      margin-top: 10px;

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

    .feedback-actions {
      margin-top: 15px;
      display: flex;
      justify-content: flex-end;

      .reply-input {
        width: 100%;
      }
    }
  }
}
</style>
