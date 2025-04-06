<template>
  <v-container>
    <h2 class="text-h4 mb-6">Báo cáo doanh thu</h2>
    
    <!-- Bộ lọc thời gian -->
    <v-card class="mb-6">
      <v-card-text>
        <v-row align="center">
          <v-col cols="12" sm="3">
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
          
          <v-col cols="12" sm="3" v-if="showDateRange">
            <v-text-field
              v-model="dateRange.to"
              label="Đến ngày"
              type="date"
              variant="outlined"
              density="compact"
            ></v-text-field>
          </v-col>
          
          <v-col cols="12" sm="3" v-if="!showDateRange">
            <v-select
              v-model="selectedPeriod"
              :items="timePeriods"
              label="Thời gian"
              variant="outlined"
              density="compact"
            ></v-select>
          </v-col>
          
          <v-col cols="12" sm="3" class="d-flex justify-end">
            <v-btn
              color="primary"
              @click="loadReportData"
              :loading="loading"
              prepend-icon="mdi-filter"
            >
              Lọc dữ liệu
            </v-btn>
            
            <v-btn
              class="ml-2"
              variant="outlined"
              @click="exportToExcel"
              :loading="exporting"
              prepend-icon="mdi-file-excel"
            >
              Xuất Excel
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    
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
              <div class="text-overline">Doanh thu trung bình</div>
              <div class="text-h5 mb-1">{{ formatCurrency(summaryData.averageRevenue) }}</div>
              <div class="text-caption">{{ formatCurrency(summaryData.averageOrderValue) }}/đơn</div>
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
              <div class="text-caption">Mã đơn #{{ summaryData.highestOrderId }}</div>
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
              <div class="text-overline">Sản phẩm bán chạy</div>
              <div class="text-h5 mb-1">{{ summaryData.topProduct }}</div>
              <div class="text-caption">{{ summaryData.topProductQuantity }} đã bán</div>
            </div>
            <v-spacer></v-spacer>
            <v-avatar color="warning" size="56">
              <v-icon color="white" size="28">mdi-cup</v-icon>
            </v-avatar>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    
    <!-- Biểu đồ -->
    <v-row>
      <v-col cols="12" lg="8">
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
      </v-col>
      
      <v-col cols="12" lg="4">
        <v-card class="mb-4">
          <v-card-title>Doanh thu theo danh mục</v-card-title>
          <v-card-text>
            <div v-if="loading" class="d-flex justify-center align-center" style="height: 300px">
              <v-progress-circular indeterminate color="primary"></v-progress-circular>
            </div>
            <div v-else>
              <pie-chart 
                :chart-data="categoryChartData" 
                :height="300"
              />
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    
    <!-- Top sản phẩm bán chạy -->
    <v-card class="mb-4">
      <v-card-title>Top sản phẩm bán chạy</v-card-title>
      <v-card-text>
        <div v-if="loading" class="d-flex justify-center py-5">
          <v-progress-circular indeterminate color="primary"></v-progress-circular>
        </div>
        <v-table v-else density="compact">
          <thead>
            <tr>
              <th>STT</th>
              <th>Sản phẩm</th>
              <th>Danh mục</th>
              <th>Số lượng</th>
              <th>Doanh thu</th>
              <th>Tỷ lệ (%)</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(product, index) in topProducts" :key="product.id">
              <td>{{ index + 1 }}</td>
              <td>{{ product.name }}</td>
              <td>{{ product.category }}</td>
              <td>{{ product.quantity }}</td>
              <td>{{ formatCurrency(product.revenue) }}</td>
              <td>
                <v-progress-linear
                  :model-value="product.percentage"
                  height="8"
                  color="primary"
                  rounded
                ></v-progress-linear>
                {{ product.percentage.toFixed(1) }}%
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import LineChart from '../components/charts/LineChart.vue';
import PieChart from '../components/charts/PieChart.vue';
import BarChart from '../components/charts/BarChart.vue';
import ReportService from '../services/report.service';
import { useSnackbar } from '../helpers/useSnackbar';

// Snackbar
const { showSnackbar } = useSnackbar();

// Trạng thái
const loading = ref(false);
const exporting = ref(false);

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
  { title: 'Sản phẩm', value: 'products' },
  { title: 'Nhân viên', value: 'employees' }
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
  totalRevenue: 28500000,
  totalOrders: 350,
  averageRevenue: 950000,
  averageOrderValue: 81428,
  highestOrder: 580000,
  highestOrderId: '12345',
  topProduct: 'Trà sữa trân châu',
  topProductQuantity: 210
});

// Dữ liệu cho biểu đồ
const revenueChartData = reactive({
  labels: ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'],
  datasets: [
    {
      label: 'Doanh thu',
      backgroundColor: 'rgba(71, 183, 132, 0.5)',
      borderColor: '#47b784',
      data: [3200000, 2800000, 4500000, 5100000, 4800000, 5300000, 2800000]
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

// Top sản phẩm
const topProducts = ref([
  { id: 1, name: 'Trà sữa trân châu', category: 'Trà sữa', quantity: 210, revenue: 8400000, percentage: 29.5 },
  { id: 2, name: 'Trà đào', category: 'Trà trái cây', quantity: 180, revenue: 6300000, percentage: 22.1 },
  { id: 3, name: 'Cà phê sữa đá', category: 'Cà phê', quantity: 150, revenue: 4500000, percentage: 15.8 },
  { id: 4, name: 'Trà sữa matcha', category: 'Trà sữa', quantity: 120, revenue: 4200000, percentage: 14.7 },
  { id: 5, name: 'Bánh plan', category: 'Đồ ăn nhẹ', quantity: 90, revenue: 2100000, percentage: 7.4 }
]);

// Computed
const showDateRange = computed(() => reportType.value === 'custom');

// Phương thức
function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
}

// Tải dữ liệu báo cáo
async function loadReportData() {
  loading.value = true;
  
  try {
    // Xây dựng tham số
    const params = {
      type: reportType.value,
      period: selectedPeriod.value
    };
    
    if (showDateRange.value) {
      params.from = dateRange.from;
      params.to = dateRange.to;
    }
    
    // Trong thực tế, sẽ gọi API ở đây
    // const response = await ReportService.getReportSummary(params);
    // summaryData = response.data;
    
    // Mô phỏng dữ liệu
    simulateReportData();
    
    showSnackbar('Đã tải dữ liệu báo cáo', 'success');
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu báo cáo:', error);
    showSnackbar('Không thể tải dữ liệu báo cáo', 'error');
  } finally {
    loading.value = false;
  }
}

// Mô phỏng dữ liệu cho demo
function simulateReportData() {
  // Random doanh thu trong khoảng
  const randomRevenue = (min, max) => Math.floor(Math.random() * (max - min + 1) + min);
  
  // Cập nhật dữ liệu tổng quan
  summaryData.totalRevenue = randomRevenue(25000000, 35000000);
  summaryData.totalOrders = randomRevenue(300, 400);
  summaryData.averageRevenue = Math.floor(summaryData.totalRevenue / 30);
  summaryData.averageOrderValue = Math.floor(summaryData.totalRevenue / summaryData.totalOrders);
  summaryData.highestOrder = randomRevenue(500000, 650000);
  
  // Cập nhật biểu đồ doanh thu
  if (selectedPeriod.value === 'last7days') {
    revenueChartData.labels = ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'];
    revenueChartData.datasets[0].data = Array.from({ length: 7 }, () => randomRevenue(2500000, 5500000));
  } else if (selectedPeriod.value === 'thisMonth') {
    const daysInMonth = 30;
    revenueChartData.labels = Array.from({ length: daysInMonth }, (_, i) => `${i + 1}`);
    revenueChartData.datasets[0].data = Array.from({ length: daysInMonth }, () => randomRevenue(800000, 1500000));
  }
  
  // Cập nhật biểu đồ danh mục
  const totalCategoryRevenue = summaryData.totalRevenue;
  const categories = ['Trà sữa', 'Trà trái cây', 'Cà phê', 'Đồ ăn nhẹ', 'Sinh tố'];
  const percentages = [0.45, 0.25, 0.15, 0.10, 0.05];
  
  categoryChartData.datasets[0].data = percentages.map(p => Math.floor(totalCategoryRevenue * p));
  
  // Cập nhật top sản phẩm
  const products = [
    { id: 1, name: 'Trà sữa trân châu', category: 'Trà sữa' },
    { id: 2, name: 'Trà đào', category: 'Trà trái cây' },
    { id: 3, name: 'Cà phê sữa đá', category: 'Cà phê' },
    { id: 4, name: 'Trà sữa matcha', category: 'Trà sữa' },
    { id: 5, name: 'Bánh plan', category: 'Đồ ăn nhẹ' }
  ];
  
  const totalQuantity = randomRevenue(700, 900);
  const productPercentages = [0.3, 0.25, 0.2, 0.15, 0.1];
  
  topProducts.value = products.map((product, index) => {
    const quantity = Math.floor(totalQuantity * productPercentages[index]);
    const revenue = Math.floor(quantity * randomRevenue(35000, 45000));
    const percentage = (revenue / totalCategoryRevenue * 100);
    
    return {
      ...product,
      quantity,
      revenue,
      percentage
    };
  });
  
  // Cập nhật sản phẩm bán chạy nhất
  summaryData.topProduct = topProducts.value[0].name;
  summaryData.topProductQuantity = topProducts.value[0].quantity;
}

// Xuất báo cáo
async function exportToExcel() {
  exporting.value = true;
  
  try {
    // Trong thực tế, gọi API để xuất báo cáo
    // const response = await ReportService.exportReport('excel', {
    //   type: reportType.value,
    //   period: selectedPeriod.value,
    //   from: dateRange.from,
    //   to: dateRange.to
    // });
    
    // Download file
    // const url = window.URL.createObjectURL(new Blob([response.data]));
    // const link = document.createElement('a');
    // link.href = url;
    // link.setAttribute('download', `report-${Date.now()}.xlsx`);
    // document.body.appendChild(link);
    // link.click();
    
    // Mô phỏng thành công
    setTimeout(() => {
      showSnackbar('Báo cáo đã được xuất thành công', 'success');
      exporting.value = false;
    }, 1500);
  } catch (error) {
    console.error('Lỗi khi xuất báo cáo:', error);
    showSnackbar('Không thể xuất báo cáo', 'error');
    exporting.value = false;
  }
}

// Khởi tạo
onMounted(() => {
  loadReportData();
});
</script> 