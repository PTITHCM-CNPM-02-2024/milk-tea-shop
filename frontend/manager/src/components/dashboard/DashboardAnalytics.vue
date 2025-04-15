<template>
  <div>
    <!-- Thống kê tổng quan -->
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-4">
              <div>
                <div class="text-h6 font-weight-bold">Tổng quan cửa hàng</div>
                <div class="text-subtitle-2 text-medium-emphasis">Báo cáo tổng quan về hoạt động kinh doanh</div>
              </div>
              <v-btn color="primary" variant="outlined" prepend-icon="mdi-refresh" @click="loadData">
                Cập nhật
              </v-btn>
            </div>
            
            <v-row>
              <v-col cols="6" sm="3">
                <statistics-card 
                  icon="mdi-basket" 
                  title="Sản phẩm" 
                  :value="reportStore.formatNumber(reportStore.overviewData.totalProduct || 0)" 
                  color="primary"
                />
              </v-col>
              <v-col cols="6" sm="3">
                <statistics-card 
                  icon="mdi-account-multiple" 
                  title="Khách hàng" 
                  :value="reportStore.formatNumber(reportStore.overviewData.totalCustomer || 0)" 
                  color="success"
                />
              </v-col>
              <v-col cols="6" sm="3">
                <statistics-card 
                  icon="mdi-account-tie" 
                  title="Nhân viên" 
                  :value="reportStore.formatNumber(reportStore.overviewData.totalEmployee || 0)" 
                  color="warning"
                />
              </v-col>
              <v-col cols="6" sm="3">
                <statistics-card 
                  icon="mdi-receipt" 
                  title="Đơn hàng" 
                  :value="reportStore.formatNumber(reportStore.overviewData.totalOrder || 0)" 
                  color="info"
                />
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Thống kê doanh thu và đơn hàng -->
    <v-row class="mt-6">
      <v-col cols="12" md="4">
        <v-card>
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-4">
              <div class="text-h6 font-weight-bold">Thống kê đơn hàng</div>
              <v-btn icon="mdi-dots-vertical" variant="text"></v-btn>
            </div>
            
            <div class="mb-4">
              <div class="text-subtitle-2 mb-1">Tổng doanh thu</div>
              <div class="d-flex align-center">
                <span class="text-h4 font-weight-bold">{{ reportStore.formatCurrency(reportStore.overviewData.sumOrderValue) }}</span>
                <v-chip color="success" size="small" class="ml-2">
                  <v-icon size="x-small" start>mdi-trending-up</v-icon>
                  <span>+15%</span>
                </v-chip>
              </div>
            </div>
            
            <div class="order-stats-list">
              <div class="d-flex justify-space-between mb-3">
                <div class="text-subtitle-2">Đơn hàng cao nhất</div>
                <div class="text-subtitle-2 font-weight-bold">{{ reportStore.formatCurrency(reportStore.overviewData.maxOrderValue) }}</div>
              </div>
              <div class="d-flex justify-space-between mb-3">
                <div class="text-subtitle-2">Đơn hàng thấp nhất</div>
                <div class="text-subtitle-2 font-weight-bold">{{ reportStore.formatCurrency(reportStore.overviewData.minOrderValue) }}</div>
              </div>
              <div class="d-flex justify-space-between mb-3">
                <div class="text-subtitle-2">Đơn hàng trung bình</div>
                <div class="text-subtitle-2 font-weight-bold">{{ reportStore.formatCurrency(reportStore.overviewData.avgOrderValue) }}</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="8">
        <v-card height="100%">
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-4">
              <div class="text-h6 font-weight-bold">Doanh thu theo thời gian</div>
              <v-btn-toggle
                v-model="orderRevenueTimeRange"
                mandatory
                density="comfortable"
                color="primary"
                rounded
              >
                <v-btn value="week" size="small">Tuần</v-btn>
                <v-btn value="month" size="small">Tháng</v-btn>
                <v-btn value="year" size="small">Năm</v-btn>
              </v-btn-toggle>
            </div>
            
            <div v-if="reportStore.loading.orderRevenue" class="d-flex justify-center align-center" style="height: 300px">
              <v-progress-circular indeterminate color="primary"></v-progress-circular>
            </div>
            <div v-else class="chart-container" style="height: 300px">
              <div class="text-center text-subtitle-1 mb-2" v-if="reportStore.orderRevenueData.length === 0">
                <v-icon color="warning" class="mr-1">mdi-alert-circle</v-icon>
                Không có dữ liệu để hiển thị
              </div>
              <!-- Đây là nơi sẽ hiển thị biểu đồ doanh thu theo thời gian -->
              <div v-else>
                <!-- Giả lập biểu đồ bằng CSS -->
                <div class="chart-placeholder d-flex align-end">
                  <div v-for="(item, i) in reportStore.orderRevenueData" :key="i" class="chart-bar mx-1" :style="{height: `${calculateHeight(item.revenue)}%`}">
                    <div class="chart-tooltip">
                      <div>{{ item.date }}</div>
                      <div>{{ reportStore.formatCurrency(item.revenue) }}</div>
                    </div>
                  </div>
                </div>
                <!-- Legend -->
                <div class="d-flex justify-space-between mt-2">
                  <div v-for="(item, i) in reportStore.orderRevenueData.slice(0, 7)" :key="i" class="text-caption text-center" style="width: 14.28%">
                    {{ formatDateShort(item.date) }}
                  </div>
                </div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    
    <!-- Doanh thu theo danh mục và sản phẩm bán chạy -->
    <v-row class="mt-6">
      <v-col cols="12" md="5">
        <v-card height="100%">
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-4">
              <div class="text-h6 font-weight-bold">Doanh thu theo danh mục</div>
              <v-btn-toggle
                v-model="catRevenueTimeRange"
                mandatory
                density="comfortable"
                color="primary"
                rounded
              >
                <v-btn value="month" size="small">Tháng</v-btn>
                <v-btn value="year" size="small">Năm</v-btn>
              </v-btn-toggle>
            </div>
            
            <div v-if="reportStore.loading.categoryRevenue" class="d-flex justify-center align-center" style="height: 300px">
              <v-progress-circular indeterminate color="primary"></v-progress-circular>
            </div>
            <div v-else-if="reportStore.categoryRevenueData.length === 0" class="d-flex justify-center align-center" style="height: 300px">
              <div class="text-center text-subtitle-1">
                <v-icon color="warning" class="mr-1">mdi-alert-circle</v-icon>
                Không có dữ liệu để hiển thị
              </div>
            </div>
            <div v-else>
              <div v-for="(item, index) in reportStore.categoryRevenueData" :key="index" class="mb-4">
                <div class="d-flex justify-space-between mb-1">
                  <div class="text-subtitle-2">{{ item.name }}</div>
                  <div class="text-subtitle-2 font-weight-bold">{{ reportStore.formatCurrency(item.revenue) }}</div>
                </div>
                <v-progress-linear 
                  :model-value="calculatePercentage(item.revenue, getTotalCatRevenue())" 
                  :color="getCategoryColor(index)" 
                  height="8"
                  rounded
                ></v-progress-linear>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="7">
        <v-card height="100%">
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-4">
              <div class="text-h6 font-weight-bold">Top sản phẩm bán chạy</div>
              <v-btn-toggle
                v-model="topProductsTimeRange"
                mandatory
                density="comfortable"
                color="primary"
                rounded
              >
                <v-btn value="month" size="small">Tháng</v-btn>
                <v-btn value="year" size="small">Năm</v-btn>
                <v-btn value="all" size="small">Tất cả</v-btn>
              </v-btn-toggle>
            </div>
            
            <div v-if="reportStore.loading.topProducts" class="d-flex justify-center align-center" style="height: 300px">
              <v-progress-circular indeterminate color="primary"></v-progress-circular>
            </div>
            <div v-else>
              <v-table>
                <thead>
                  <tr>
                    <th>Sản phẩm</th>
                    <th>Danh mục</th>
                    <th class="text-center">Số lượng</th>
                    <th class="text-end">Doanh thu</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="reportStore.topProductsData.length === 0">
                    <td colspan="4" class="text-center py-4">Không có dữ liệu</td>
                  </tr>
                  <tr v-for="(item, index) in reportStore.topProductsData" :key="index">
                    <td>
                      <div class="d-flex align-center">
                        <v-avatar size="32" color="grey-lighten-3" class="mr-2">
                          <span class="text-caption">{{ item.productName.charAt(0) }}</span>
                        </v-avatar>
                        <div>{{ item.productName }}</div>
                      </div>
                    </td>
                    <td>{{ item.categoryName }}</td>
                    <td class="text-center">{{ reportStore.formatNumber(item.quantity) }}</td>
                    <td class="text-end font-weight-medium">{{ reportStore.formatCurrency(item.revenue) }}</td>
                  </tr>
                </tbody>
              </v-table>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import StatisticsCard from '@/components/dashboard/StatisticsCard.vue'
import { ref, onMounted, watch } from 'vue'
import { useReportStore } from '@/stores/report'

// Store
const reportStore = useReportStore()

// State
const orderRevenueTimeRange = ref('week')
const catRevenueTimeRange = ref('month')
const topProductsTimeRange = ref('month')

// Hàm fetch dữ liệu theo khoảng thời gian
const fetchOrderRevenueData = async () => {
  const { fromDate, toDate } = getDateRange(orderRevenueTimeRange.value)
  await reportStore.fetchOrderRevenueReport(fromDate, toDate)
}

const fetchCategoryRevenueData = async () => {
  const { fromDate, toDate } = getDateRange(catRevenueTimeRange.value)
  await reportStore.fetchCategoryRevenueReport(fromDate, toDate)
}

const fetchTopProductsData = async () => {
  const { fromDate, toDate } = getDateRange(topProductsTimeRange.value)
  await reportStore.fetchTopProductsReport(fromDate, toDate, 10)
}

// Helper functions
const getDateRange = (timeRange) => {
  const now = new Date()
  let fromDate, toDate
  
  if (timeRange === 'week') {
    // 7 ngày gần đây
    fromDate = new Date(now)
    fromDate.setDate(now.getDate() - 7)
    toDate = now
  } else if (timeRange === 'month') {
    // 30 ngày gần đây
    fromDate = new Date(now)
    fromDate.setDate(now.getDate() - 30)
    toDate = now
  } else if (timeRange === 'year') {
    // Năm hiện tại
    fromDate = new Date(now.getFullYear(), 0, 1)
    toDate = now
  } else {
    // Mặc định lấy tất cả
    fromDate = new Date(2020, 0, 1)
    toDate = now
  }
  
  return {
    fromDate: formatDateISO(fromDate),
    toDate: formatDateISO(toDate)
  }
}

const formatDateISO = (date) => {
  return date.toISOString().slice(0, 19) // Format: YYYY-MM-DDTHH:MM:SS
}

const formatDateShort = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' })
}

const calculateHeight = (value) => {
  if (!reportStore.orderRevenueData.length) return 0
  
  const maxValue = Math.max(...reportStore.orderRevenueData.map(item => parseFloat(item.revenue) || 0))
  if (maxValue === 0) return 0
  
  return (parseFloat(value) / maxValue) * 90
}

const calculatePercentage = (value, total) => {
  if (!total || !value) return 0
  return (parseFloat(value) / parseFloat(total)) * 100
}

const getTotalCatRevenue = () => {
  return reportStore.categoryRevenueData.reduce((sum, item) => sum + parseFloat(item.revenue || 0), 0)
}

const getCategoryColor = (index) => {
  const colors = ['primary', 'success', 'info', 'warning', 'error', 'purple', 'cyan', 'orange']
  return colors[index % colors.length]
}

const loadData = () => {
  reportStore.fetchOverviewReport()
  fetchOrderRevenueData()
  fetchCategoryRevenueData()
  fetchTopProductsData()
}

// Watch time range changes
watch(orderRevenueTimeRange, () => {
  fetchOrderRevenueData()
})

watch(catRevenueTimeRange, () => {
  fetchCategoryRevenueData()
})

watch(topProductsTimeRange, () => {
  fetchTopProductsData()
})

// Lifecycle hooks
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.chart-container {
  position: relative;
  width: 100%;
}

.chart-placeholder {
  height: 250px;
  width: 100%;
}

.chart-bar {
  position: relative;
  width: 8%;
  border-radius: 4px 4px 0 0;
  background-color: #7367F0;
  transition: height 0.5s;
  min-height: 4px;
  margin: 0 1%;
  cursor: pointer;
}

.chart-bar:hover .chart-tooltip {
  display: block;
}

.chart-tooltip {
  display: none;
  position: absolute;
  top: -60px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
  z-index: 10;
  white-space: nowrap;
}

.chart-tooltip:after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  margin-left: -5px;
  border-width: 5px;
  border-style: solid;
  border-color: rgba(0, 0, 0, 0.8) transparent transparent transparent;
}

.max-width-300 {
  max-width: 300px;
}

.order-stats-list {
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  padding-top: 16px;
}
</style>