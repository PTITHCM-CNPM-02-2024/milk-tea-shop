<template>
  <v-container>
    <h2 class="text-h4 mb-6">Báo cáo doanh thu nhân viên</h2>
    
    <!-- Bộ lọc thời gian -->
    <v-card class="mb-6">
      <v-card-text>
        <v-row align="center">
          <v-col cols="12" sm="3" class="pt-8">
            <v-select
              v-model="reportType"
              :items="reportTypes"
              label="Loại báo cáo"
              variant="outlined"
              density="compact"
            ></v-select>
          </v-col>
          
          <v-col cols="12" sm="3" v-if="showDateRange">
            <v-text-field
              v-model="dateRange.from"
              label="Từ ngày"
              type="date"
              variant="outlined"
              density="compact"
            ></v-text-field>
          </v-col>
          
          <v-col cols="12" sm="3" v-if="showDateRange" class="pt-8">
            <v-text-field
              v-model="dateRange.to"
              label="Đến ngày"
              type="date"
              variant="outlined"
              density="compact"
            ></v-text-field>
          </v-col>
          
          <v-col cols="12" sm="3" v-if="!showDateRange" class="pt-8">
            <v-select
              v-model="selectedPeriod"
              :items="timePeriods"
              label="Thời gian"
              variant="outlined"
              density="compact"
            ></v-select>
          </v-col>
          
          <v-col cols="12" sm="3" class="pt-3">
            <v-btn
              color="primary"
              @click="loadReportData"
              :loading="loading"
              prepend-icon="mdi-filter"
              density="default"
            >
              Lọc dữ liệu
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    
    <!-- Hiển thị khi không có dữ liệu -->
    <v-card v-if="!hasData && !loading" class="mb-6 text-center pa-6">
      <v-icon size="large" icon="mdi-information-outline" color="info" class="mb-4"></v-icon>
      <h3 class="text-h5 mb-2">Không có dữ liệu</h3>
      <p class="text-body-1">Không tìm thấy dữ liệu báo cáo trong khoảng thời gian đã chọn. Vui lòng thử lại với khoảng thời gian khác.</p>
    </v-card>
    
    <!-- Báo cáo tổng quan và biểu đồ (chỉ hiển thị với loại báo cáo tổng quan hoặc doanh thu) -->
    <div v-if="(reportType === 'overview' || reportType === 'revenue') && hasData">
      <!-- Tổng quan -->
      <v-row>
        <v-col cols="12" md="6" lg="3">
          <v-card class="mb-4" variant="outlined">
            <v-card-text class="d-flex align-center">
              <div>
                <div class="text-overline">Tổng doanh thu</div>
                <div class="text-h5 mb-1">{{ formatCurrency(summaryData.totalRevenue) }}</div>
                <div class="text-caption">{{ summaryData.totalOrders }} đơn hàng</div>
              </div>
              <v-spacer></v-spacer>
              <v-avatar color="primary" size="56">
                <v-icon color="white" size="28">mdi-cash-register</v-icon>
              </v-avatar>
            </v-card-text>
          </v-card>
        </v-col>
        
        <v-col cols="12" md="6" lg="3">
          <v-card class="mb-4" variant="outlined">
            <v-card-text class="d-flex align-center">
              <div>
                <div class="text-overline">Giá trị đơn trung bình</div>
                <div class="text-h5 mb-1">{{ formatCurrency(summaryData.averageOrderValue) }}</div>
                <div class="text-caption">Trung bình mỗi đơn</div>
              </div>
              <v-spacer></v-spacer>
              <v-avatar color="success" size="56">
                <v-icon color="white" size="28">mdi-chart-timeline-variant</v-icon>
              </v-avatar>
            </v-card-text>
          </v-card>
        </v-col>
        
        <v-col cols="12" md="6" lg="3">
          <v-card class="mb-4" variant="outlined">
            <v-card-text class="d-flex align-center">
              <div>
                <div class="text-overline">Đơn hàng cao nhất</div>
                <div class="text-h5 mb-1">{{ formatCurrency(summaryData.highestOrder) }}</div>
                <div class="text-caption">Giá trị cao nhất</div>
              </div>
              <v-spacer></v-spacer>
              <v-avatar color="info" size="56">
                <v-icon color="white" size="28">mdi-trophy</v-icon>
              </v-avatar>
            </v-card-text>
          </v-card>
        </v-col>
        
        <v-col cols="12" md="6" lg="3">
          <v-card class="mb-4" variant="outlined">
            <v-card-text class="d-flex align-center">
              <div>
                <div class="text-overline">Đơn hàng thấp nhất</div>
                <div class="text-h5 mb-1">{{ formatCurrency(summaryData.lowestOrder) }}</div>
                <div class="text-caption">Giá trị thấp nhất</div>
              </div>
              <v-spacer></v-spacer>
              <v-avatar color="warning" size="56">
                <v-icon color="white" size="28">mdi-chart-bell-curve</v-icon>
              </v-avatar>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
      
      <!-- Biểu đồ doanh thu -->
      <v-card class="mb-4">
        <v-card-title class="d-flex align-center">
          <span>Doanh thu theo thời gian</span>
          <v-spacer></v-spacer>
          <v-btn-toggle
            v-model="revenueChartType"
            mandatory
            density="compact"
          >
            <v-btn value="line">
              <v-icon>mdi-chart-line</v-icon>
            </v-btn>
            <v-btn value="bar">
              <v-icon>mdi-chart-bar</v-icon>
            </v-btn>
          </v-btn-toggle>
        </v-card-title>
        <v-card-text>
          <div v-if="loading" class="d-flex justify-center align-center" style="height: 300px">
            <v-progress-circular indeterminate color="primary"></v-progress-circular>
          </div>
          <div v-else-if="revenueChartData.labels.length === 0" class="d-flex justify-center align-center" style="height: 300px">
            <p class="text-subtitle-1 text-medium-emphasis">Không có dữ liệu biểu đồ</p>
          </div>
          <div v-else>
            <line-chart 
              v-if="revenueChartType === 'line'"
              :chart-data="revenueChartData" 
              :height="300"
            />
            <bar-chart 
              v-else
              :chart-data="revenueChartData" 
              :height="300"
            />
          </div>
        </v-card-text>
      </v-card>
    </div>
    
    <!-- Danh sách đơn hàng (chỉ hiển thị với loại báo cáo đơn hàng) -->
    <v-card v-if="reportType === 'orders'" class="mb-4">
      <v-card-title>Danh sách đơn hàng</v-card-title>
      <v-card-text>
        <div v-if="loading" class="d-flex justify-center py-5">
          <v-progress-circular indeterminate color="primary"></v-progress-circular>
        </div>
        <div v-else-if="orders.length === 0" class="text-center py-5">
          <v-icon size="large" icon="mdi-cart-off" color="grey" class="mb-2"></v-icon>
          <p class="text-subtitle-1 text-medium-emphasis">Không có đơn hàng nào trong khoảng thời gian này</p>
        </div>
        <v-table v-else density="compact">
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Thời gian</th>
              <th>Khách hàng</th>
              <th>Tổng tiền</th>
              <th>Giảm giá</th>
              <th>Thanh toán</th>
              <th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.orderId">
              <td>{{ order.orderId }}</td>
              <td>{{ formatDate(order.orderTime) }}</td>
              <td>{{ order.customerId || 'Khách lẻ' }}</td>
              <td>{{ formatCurrency(order.totalAmount) }}</td>
              <td>{{ formatCurrency(order.totalAmount - order.finalAmount) }}</td>
              <td>{{ formatCurrency(order.finalAmount) }}</td>
              <td>
                <v-chip
                  :color="getStatusColor(order.orderStatus)"
                  size="small"
                  class="text-capitalize"
                >
                  {{ getStatusText(order.orderStatus) }}
                </v-chip>
              </td>
            </tr>
          </tbody>
        </v-table>
        
        <!-- Phân trang -->
        <div v-if="orders.length > 0" class="d-flex justify-center mt-4">
          <v-pagination
            v-model="currentPage"
            :length="totalPages"
            rounded="circle"
            @update:model-value="changePage"
          ></v-pagination>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import LineChart from '../components/charts/LineChart.vue';
import PieChart from '../components/charts/PieChart.vue';
import BarChart from '../components/charts/BarChart.vue';
import ReportService from '../services/report.service';
import { useSnackbar } from '../helpers/useSnackbar';

// Snackbar
const { showSuccess, showError, showInfo } = useSnackbar();

// Props
const props = defineProps({
  employeeId: {
    type: Number,
    required: true
  }
});

// Trạng thái
const loading = ref(false);
const exporting = ref(false);
const hasData = ref(false);

// Bộ lọc
const reportType = ref('overview');
const selectedPeriod = ref('today');
const dateRange = reactive({
  from: new Date().toISOString().substr(0, 10),
  to: new Date().toISOString().substr(0, 10)
});

// Loại biểu đồ
const revenueChartType = ref('line');

// Lựa chọn
const reportTypes = [
  { title: 'Tổng quan', value: 'overview' },
  { title: 'Doanh thu', value: 'revenue' },
  { title: 'Đơn hàng', value: 'orders' }
];

const timePeriods = [
  { title: 'Hôm nay', value: 'today' },
  { title: 'Hôm qua', value: 'yesterday' },
  { title: '7 ngày qua', value: 'last7days' },
  { title: '30 ngày qua', value: 'last30days' },
  { title: 'Tháng này', value: 'thisMonth' },
  { title: 'Tháng trước', value: 'lastMonth' },
  { title: 'Năm nay', value: 'thisYear' }
];

// Dữ liệu tổng quan
const summaryData = reactive({
  totalRevenue: 0,
  totalOrders: 0,
  averageRevenue: 0,
  averageOrderValue: 0,
  highestOrder: 0,
  highestOrderId: '',
  lowestOrder: 0,
  lowestOrderId: ''
});

// Dữ liệu cho biểu đồ
const revenueChartData = reactive({
  labels: [],
  datasets: [
    {
      label: 'Doanh thu',
      backgroundColor: 'rgba(71, 183, 132, 0.5)',
      borderColor: '#47b784',
      data: []
    }
  ]
});

const categoryChartData = reactive({
  labels: ['Trà sữa', 'Trà trái cây', 'Cà phê', 'Đồ ăn nhẹ', 'Sinh tố'],
  datasets: [
    {
      backgroundColor: ['#41B883', '#E46651', '#00D8FF', '#DD1B16', '#7957d5'],
      data: [12500000, 8300000, 4500000, 2100000, 1100000]
    }
  ]
});

// Dữ liệu đơn hàng
const orders = ref([]);
const totalPages = ref(0);
const currentPage = ref(0);
const pageSize = ref(10);

// Computed
const showDateRange = computed(() => reportType.value === 'custom');

// Thiết lập ngày từ period
function setupDateRangeFromPeriod() {
  const today = new Date();
  const todayStr = today.toISOString().substr(0, 10);
  
  switch (selectedPeriod.value) {
    case 'today':
      dateRange.from = todayStr + 'T00:00:00';
      dateRange.to = todayStr + 'T23:59:59';
      break;
    case 'yesterday':
      const yesterday = new Date(today);
      yesterday.setDate(yesterday.getDate() - 1);
      const yesterdayStr = yesterday.toISOString().substr(0, 10);
      dateRange.from = yesterdayStr + 'T00:00:00';
      dateRange.to = yesterdayStr + 'T23:59:59';
      break;
    case 'last7days':
      const last7days = new Date(today);
      last7days.setDate(last7days.getDate() - 7);
      dateRange.from = last7days.toISOString().substr(0, 10) + 'T00:00:00';
      dateRange.to = todayStr + 'T23:59:59';
      break;
    case 'last30days':
      const last30days = new Date(today);
      last30days.setDate(last30days.getDate() - 30);
      dateRange.from = last30days.toISOString().substr(0, 10) + 'T00:00:00';
      dateRange.to = todayStr + 'T23:59:59';
      break;
    case 'thisMonth':
      const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
      dateRange.from = firstDayOfMonth.toISOString().substr(0, 10) + 'T00:00:00';
      dateRange.to = todayStr + 'T23:59:59';
      break;
    case 'lastMonth':
      const firstDayOfLastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
      const lastDayOfLastMonth = new Date(today.getFullYear(), today.getMonth(), 0);
      dateRange.from = firstDayOfLastMonth.toISOString().substr(0, 10) + 'T00:00:00';
      dateRange.to = lastDayOfLastMonth.toISOString().substr(0, 10) + 'T23:59:59';
      break;
    case 'thisYear':
      const firstDayOfYear = new Date(today.getFullYear(), 0, 1);
      dateRange.from = firstDayOfYear.toISOString().substr(0, 10) + 'T00:00:00';
      dateRange.to = todayStr + 'T23:59:59';
      break;
  }
}

// Phương thức
function formatCurrency(value) {
  if (value == null) return '0 ₫';
  
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
}

function formatDate(dateStr) {
  if (!dateStr) return '';
  try {
    const date = new Date(dateStr);
    return new Intl.DateTimeFormat('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  } catch (e) {
    console.error('Lỗi định dạng ngày:', e);
    return dateStr;
  }
}

// Tải dữ liệu báo cáo tổng quan
async function loadOverviewData() {
  loading.value = true;
  hasData.value = false;
  
  try {
    setupDateRangeFromPeriod();
    
    const fromDate = ReportService.formatDateTime(dateRange.from);
    const toDate = ReportService.formatDateTime(dateRange.to);
    
    const response = await ReportService.getEmployeeOrderOverview(props.employeeId, fromDate, toDate);
    
    if (response && response.data) {
      const data = response.data;
      
      summaryData.totalRevenue = data.sumOrderValue || 0;
      summaryData.averageOrderValue = data.avgOrderValue || 0;
      summaryData.lowestOrder = data.minOrderValue || 0;
      summaryData.highestOrder = data.maxOrderValue || 0;
      
      // Tải thêm dữ liệu doanh thu theo thời gian
      await loadRevenueData();
      
      hasData.value = true;
      showSuccess('Đã tải dữ liệu báo cáo tổng quan');
    } else {
      throw new Error('Không có dữ liệu trả về');
    }
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu báo cáo tổng quan:', error);
    showError(error.message || 'Không thể tải dữ liệu báo cáo tổng quan');
  } finally {
    loading.value = false;
  }
}

// Tải dữ liệu doanh thu
async function loadRevenueData() {
  try {
    const fromDate = ReportService.formatDateTime(dateRange.from);
    const toDate = ReportService.formatDateTime(dateRange.to);
    
    const response = await ReportService.getEmployeeOrderRevenue(props.employeeId, fromDate, toDate);
    
    if (response && response.data) {
      const revenueData = response.data;
      
      // Xử lý dữ liệu cho biểu đồ
      const labels = [];
      const data = [];
      
      for (const item of revenueData) {
        if (item.date && item.revenue) {
          try {
            const date = new Date(item.date);
            const formattedDate = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
            labels.push(formattedDate);
            data.push(item.revenue);
          } catch (e) {
            console.error('Lỗi xử lý dữ liệu ngày:', e);
          }
        }
      }
      
      // Cập nhật dữ liệu biểu đồ
      revenueChartData.labels = labels;
      revenueChartData.datasets[0].data = data;
      
      // Tính tổng số đơn hàng (nếu không có từ API tổng quan)
      summaryData.totalOrders = revenueData.length;
    }
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu doanh thu:', error);
  }
}

// Tải danh sách đơn hàng
async function loadOrderData(page = 0) {
  loading.value = true;
  
  try {
    setupDateRangeFromPeriod();
    
    const fromDate = ReportService.formatDateTime(dateRange.from);
    const toDate = ReportService.formatDateTime(dateRange.to);

    
    const response = await ReportService.getEmployeeOrders(
      props.employeeId,
      fromDate,
      toDate,
      page,
      pageSize.value
    );
    
    if (response && response.data) {
      orders.value = response.data.content || [];
      totalPages.value = response.data.totalPages || 1;
      currentPage.value = response.data.number || 0;
      
      hasData.value = orders.value.length > 0;
      
      if (hasData.value) {
        showSuccess(`Đã tải ${orders.value.length} đơn hàng`);
      } else {
        showInfo('Không có đơn hàng trong khoảng thời gian này');
      }
    } else {
      throw new Error('Không có dữ liệu trả về');
    }
  } catch (error) {
    console.error('Lỗi khi tải danh sách đơn hàng:', error);
    showError(error.message || 'Không thể tải danh sách đơn hàng');
  } finally {
    loading.value = false;
  }
}

// Tải dữ liệu báo cáo dựa trên loại báo cáo
async function loadReportData() {
  loading.value = true;
  
  try {
    if (reportType.value === 'overview' || reportType.value === 'revenue') {
      await loadOverviewData();
    } else if (reportType.value === 'orders') {
      await loadOrderData();
    }
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu báo cáo:', error);
    showError(error.message || 'Không thể tải dữ liệu báo cáo');
  } finally {
    loading.value = false;
  }
}

// Xuất báo cáo ra Excel
async function exportToExcel() {
  exporting.value = true;
  
  try {
    showInfo('Đang chuẩn bị xuất báo cáo...');
    
    // Mô phỏng xuất báo cáo vì API chưa hỗ trợ
    setTimeout(() => {
      showSuccess('Báo cáo đã được xuất thành công');
      exporting.value = false;
    }, 1500);
  } catch (error) {
    console.error('Lỗi khi xuất báo cáo:', error);
    showError(error.message || 'Không thể xuất báo cáo');
    exporting.value = false;
  }
}

// Xử lý thay đổi trang
function changePage(newPage) {
  if (newPage >= 0 && newPage < totalPages.value) {
    currentPage.value = newPage;
    loadOrderData(newPage);
  }
}

// Theo dõi thay đổi của loại báo cáo
watch(reportType, () => {
  loadReportData();
});

// Theo dõi thay đổi của khoảng thời gian
watch(selectedPeriod, () => {
  loadReportData();
});

// Khởi tạo
onMounted(() => {
  if (!props.employeeId) {
    showError('Không xác định được ID của nhân viên');
    return;
  }
  
  showInfo(`Đang tải báo cáo cho nhân viên ID: ${props.employeeId}`);
  loadReportData();
});

// Lấy màu cho trạng thái đơn hàng
function getStatusColor(status) {
  if (!status) return 'grey';
  
  switch (status.toUpperCase()) {
    case 'COMPLETED':
      return 'success';
    case 'PROCESSING':
      return 'info';
    case 'CANCELED':
      return 'error';
    default:
      return 'grey';
  }
}

// Lấy text hiển thị cho trạng thái đơn hàng
function getStatusText(status) {
  if (!status) return 'Không rõ';
  
  switch (status.toUpperCase()) {
    case 'COMPLETED':
      return 'Hoàn thành';
    case 'PROCESSING':
      return 'Đang xử lý';
    case 'CANCELED':
      return 'Đã hủy';
    default:
      return status;
  }
}
</script> 