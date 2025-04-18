<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h1 class="error-title">页面不存在</h1>
      <p class="error-message">很抱歉，您访问的页面不存在或已被移除</p>
      <div class="suggestions">
        <p>您可以尝试以下操作：</p>
        <ul>
          <li>检查网址拼写是否正确</li>
          <li>返回上一页面</li>
          <li>前往首页重新开始</li>
        </ul>
      </div>
      <div class="actions">
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        <el-button @click="$router.back()">返回上一页</el-button>
      </div>
    </div>
    
    <div class="train-animation">
      <div class="train">
        <div class="locomotive"></div>
        <div class="wheels"></div>
        <div class="track"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

onMounted(() => {
  console.log('404 页面被访问:', router.currentRoute.value.fullPath);
});
</script>

<style scoped lang="scss">
.not-found-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 2rem;
  text-align: center;
  
  .not-found-content {
    max-width: 600px;
    margin-bottom: 3rem;
    
    .error-code {
      font-size: 8rem;
      font-weight: 700;
      color: #409EFF;
      line-height: 1;
      margin-bottom: 1rem;
      text-shadow: 4px 4px 0px rgba(0, 0, 0, 0.1);
    }
    
    .error-title {
      font-size: 2rem;
      color: #303133;
      margin-bottom: 1rem;
    }
    
    .error-message {
      font-size: 1.2rem;
      color: #606266;
      margin-bottom: 2rem;
    }
    
    .suggestions {
      margin-bottom: 2rem;
      text-align: left;
      
      p {
        font-weight: bold;
        margin-bottom: 0.5rem;
      }
      
      ul {
        padding-left: 1.5rem;
        
        li {
          margin-bottom: 0.5rem;
          color: #606266;
        }
      }
    }
    
    .actions {
      display: flex;
      justify-content: center;
      gap: 1rem;
    }
  }
  
  .train-animation {
    width: 100%;
    height: 80px;
    position: relative;
    overflow: hidden;
    
    .train {
      position: absolute;
      bottom: 10px;
      left: -100px;
      animation: train-moving 8s infinite linear;
      
      .locomotive {
        width: 80px;
        height: 30px;
        background-color: #409EFF;
        border-radius: 5px 20px 5px 5px;
        position: relative;
        
        &:before {
          content: '';
          position: absolute;
          top: -15px;
          left: 15px;
          width: 30px;
          height: 15px;
          background-color: #409EFF;
          border-radius: 5px 5px 0 0;
        }
        
        &:after {
          content: '';
          position: absolute;
          top: -5px;
          right: 10px;
          width: 8px;
          height: 10px;
          background-color: #606266;
          border-radius: 4px;
        }
      }
      
      .wheels {
        position: absolute;
        bottom: -8px;
        left: 15px;
        
        &:before, &:after {
          content: '';
          position: absolute;
          width: 12px;
          height: 12px;
          background-color: #303133;
          border-radius: 50%;
          box-shadow: 45px 0 0 #303133;
        }
      }
      
      .track {
        position: absolute;
        bottom: -15px;
        left: -50px;
        width: 500px;
        height: 5px;
        background-color: #909399;
        
        &:before, &:after {
          content: '';
          position: absolute;
          width: 100%;
          height: 2px;
          background: repeating-linear-gradient(
            90deg,
            #c0c4cc,
            #c0c4cc 10px,
            transparent 10px,
            transparent 20px
          );
        }
        
        &:before {
          top: -5px;
        }
        
        &:after {
          bottom: -5px;
        }
      }
    }
  }
}

@keyframes train-moving {
  0% {
    left: -100px;
  }
  100% {
    left: 100%;
  }
}

@media (max-width: 768px) {
  .not-found-container {
    padding: 1rem;
    
    .not-found-content {
      .error-code {
        font-size: 6rem;
      }
      
      .error-title {
        font-size: 1.5rem;
      }
      
      .error-message {
        font-size: 1rem;
      }
    }
    
    .actions {
      flex-direction: column;
      align-items: center;
      
      .el-button {
        margin-bottom: 0.5rem;
      }
    }
  }
}
</style> 