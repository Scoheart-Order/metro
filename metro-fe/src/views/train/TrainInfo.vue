<template>
  <div class="train-info-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>列车时刻查询</h2>
        </div>
      </template>
      
      <!-- Search Form -->
      <div class="search-section">
        <el-form :model="searchForm" :inline="true">
          <el-form-item label="出发站">
            <el-select
              v-model="searchForm.startStation"
              filterable
              placeholder="出发站"
              style="width: 180px"
            >
              <el-option
                v-for="station in stationOptions"
                :key="station.value"
                :label="station.label"
                :value="station.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="到达站">
            <el-select
              v-model="searchForm.endStation"
              filterable
              placeholder="到达站"
              style="width: 180px"
            >
              <el-option
                v-for="station in stationOptions"
                :key="station.value"
                :label="station.label"
                :value="station.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="日期">
            <el-date-picker
              v-model="searchForm.date"
              type="date"
              placeholder="选择日期"
              style="width: 180px"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :loading="loading">
              查询
            </el-button>
            <el-button @click="resetSearch">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- Train List -->
      <div class="train-list-section">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        
        <div v-else-if="trainStore.trains.length === 0" class="empty-data">
          <el-empty description="暂无列车信息" />
        </div>
        
        <div v-else>
          <el-table :data="trainStore.trains" border stripe>
            <el-table-column prop="trainNumber" label="车次" width="100" align="center" />
            <el-table-column prop="startStation" label="出发站" width="120" align="center" />
            <el-table-column prop="endStation" label="到达站" width="120" align="center" />
            <el-table-column prop="departureTime" label="出发时间" width="150" align="center" />
            <el-table-column prop="arrivalTime" label="到达时间" width="150" align="center" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag
                  :type="getStatusType(scope.row.status)"
                  effect="plain"
                >
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewTrainDetail(scope.row)"
                >
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      
      <!-- Train Detail Dialog -->
      <el-dialog
        v-model="dialogVisible"
        title="列车详情"
        width="500px"
      >
        <div v-if="currentTrain">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="车次">{{ currentTrain.trainNumber }}</el-descriptions-item>
            <el-descriptions-item label="出发站">{{ currentTrain.startStation }}</el-descriptions-item>
            <el-descriptions-item label="到达站">{{ currentTrain.endStation }}</el-descriptions-item>
            <el-descriptions-item label="出发时间">{{ currentTrain.departureTime }}</el-descriptions-item>
            <el-descriptions-item label="到达时间">{{ currentTrain.arrivalTime }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentTrain.status)">
                {{ getStatusText(currentTrain.status) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
          
          <div class="station-timeline">
            <h3>途经站点</h3>
            <el-timeline>
              <el-timeline-item
                v-for="(station, index) in stationTimeline"
                :key="index"
                :timestamp="station.time"
                :type="getTimelineItemType(index, stationTimeline.length)"
              >
                {{ station.name }}
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useTrainStore } from '../../stores/train'
import { ElMessage } from 'element-plus'

const route = useRoute()
const trainStore = useTrainStore()
const loading = ref(false)
const dialogVisible = ref(false)
const currentTrain = ref(null)

// Station timeline (would come from API in real app)
const stationTimeline = ref([
  { name: '中心站', time: '06:30' },
  { name: '北站', time: '06:45' },
  { name: '机场站', time: '07:05' },
  { name: '东站', time: '07:20' },
  { name: '南站', time: '07:35' }
])

// Station options (would come from API in real app)
const stationOptions = ref([
  { value: 'station1', label: '中心站' },
  { value: 'station2', label: '北站' },
  { value: 'station3', label: '南站' },
  { value: 'station4', label: '东站' },
  { value: 'station5', label: '西站' },
  { value: 'station6', label: '机场站' },
  // More stations would be loaded from API
])

// Search form
const searchForm = reactive({
  startStation: route.query.startStation || '',
  endStation: route.query.endStation || '',
  date: route.query.date ? new Date(route.query.date as string) : new Date()
})

// Status display helpers
const getStatusType = (status: string) => {
  switch (status) {
    case 'on-time': return 'success'
    case 'delayed': return 'warning'
    case 'cancelled': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'on-time': return '准点'
    case 'delayed': return '晚点'
    case 'cancelled': return '取消'
    default: return '未知'
  }
}

const getTimelineItemType = (index: number, totalLength: number) => {
  if (index === 0) return 'primary'
  if (index === totalLength - 1) return 'success'
  return ''
}

// Load data on component mount
onMounted(async () => {
  // If there are query parameters, search with them
  if (route.query.startStation && route.query.endStation) {
    await handleSearch()
  } else {
    // Otherwise load all trains
    await fetchTrains()
  }
})

// Fetch all trains
const fetchTrains = async () => {
  loading.value = true
  
  try {
    await trainStore.fetchTrains()
  } catch (error) {
    console.error('Fetch trains error:', error)
    ElMessage.error('获取列车信息失败')
  } finally {
    loading.value = false
  }
}

// Search function
const handleSearch = async () => {
  if (!searchForm.startStation || !searchForm.endStation) {
    ElMessage.warning('请选择出发站和到达站')
    return
  }
  
  loading.value = true
  
  try {
    await trainStore.searchTrains({
      startStation: searchForm.startStation,
      endStation: searchForm.endStation,
      date: searchForm.date.toISOString().split('T')[0]
    })
  } catch (error) {
    console.error('Search error:', error)
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// Reset search form
const resetSearch = () => {
  searchForm.startStation = ''
  searchForm.endStation = ''
  searchForm.date = new Date()
  fetchTrains()
}

// View train detail
const viewTrainDetail = (train) => {
  currentTrain.value = train
  dialogVisible.value = true
}
</script>

<style scoped lang="scss">
.train-info-container {
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
  
  .search-section {
    margin-bottom: 20px;
    background-color: #f5f7fa;
    padding: 20px;
    border-radius: 4px;
  }
  
  .train-list-section {
    .loading-container {
      padding: 20px 0;
    }
    
    .empty-data {
      padding: 40px 0;
    }
  }
  
  .station-timeline {
    margin-top: 20px;
    
    h3 {
      margin-bottom: 16px;
      color: #303133;
    }
  }
}

@media (max-width: 768px) {
  .train-info-container {
    .search-section {
      .el-form {
        display: flex;
        flex-direction: column;
        
        .el-form-item {
          margin-right: 0;
          margin-bottom: 15px;
        }
      }
    }
  }
}
</style> 