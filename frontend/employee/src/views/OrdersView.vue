<template>
  <v-container>
    <v-card>
      <v-card-title class="d-flex align-center">
        <span>Đơn hàng đang sử dụng bàn</span>
        <v-spacer></v-spacer>
        <v-text-field
          v-model="search"
          prepend-icon="mdi-magnify"
          label="Tìm kiếm"
          single-line
          hide-details
          density="compact"
          class="ml-2"
          style="max-width: 300px"
        ></v-text-field>
        <v-btn color="primary" class="ml-2" prepend-icon="mdi-refresh" @click="refreshOrders">
          Làm mới
        </v-btn>
      </v-card-title>
      
      <v-data-table
        v-if="orders.length > 0"
        :headers="headers"
        :items="orders"
        :search="search"
        :loading="loading"
        :items-per-page="pageSize"
        :page="currentPage + 1"
        :server-items-length="totalItems"
        loading-text="Đang tải dữ liệu..."
        no-data-text="Không có đơn hàng nào"
        class="elevation-1"
        @update:options="handleDataTableOptionsChange"
      >
        <template v-slot:item.orderTables="{ item }">
          <v-chip 
            v-for="table in item.orderTables" 
            :key="table.tableNumber" 
            size="small"
            class="mr-1"
          >
            {{ table.tableNumber }}
          </v-chip>
        </template>
        
        <template v-slot:item.customerName="{ item }">
          <span v-if="item.customerName">{{ item.customerName }}</span>
          <span v-else class="text-caption text-disabled">Không có</span>
        </template>
        
        <template v-slot:item.finalAmount="{ item }">
          {{ formatCurrency(item.finalAmount) }}
        </template>
        
        <template v-slot:item.orderTime="{ item }">
          {{ formatDate(item.orderTime) }}
        </template>
        
        <template v-slot:item.actions="{ item }">
          <v-btn size="small" color="primary" variant="text" @click="viewOrderDetails(item)">
            <v-icon>mdi-eye</v-icon>
          </v-btn>
          <v-btn size="small" color="success" variant="text" @click="checkoutOrder(item)">
            <v-icon>mdi-cash-register</v-icon>
          </v-btn>
        </template>
      </v-data-table>
      
      <v-card-text v-else-if="!loading" class="text-center py-5">
        <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-clipboard-text-outline</v-icon>
        <div class="text-h6 mb-2">Không có đơn hàng nào</div>
        <div class="text-body-2 text-grey">Hiện không có đơn hàng nào đang sử dụng bàn</div>
      </v-card-text>
      
      <v-card-text v-else class="text-center py-5">
        <v-progress-circular indeterminate color="primary"></v-progress-circular>
        <div class="mt-3">Đang tải dữ liệu...</div>
      </v-card-text>

      <!-- Hiển thị thông tin phân trang -->
      <v-card-actions v-if="orders.length > 0" class="d-flex justify-end px-4 py-2">
        <span class="text-caption text-grey">
          Hiển thị {{ orders.length }} / {{ totalItems }} đơn hàng
        </span>
      </v-card-actions>
    </v-card>
    
    <!-- Dialog chi tiết đơn hàng -->
    <v-dialog v-model="detailDialog" max-width="700px">
      <v-card v-if="selectedOrder">
        <v-card-title>
          Chi tiết đơn hàng #{{ selectedOrder.orderId }}
          <v-spacer></v-spacer>
          <v-btn icon @click="detailDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        
        <v-card-text>
          <div class="d-flex mb-4">
            <div class="flex-grow-1">
              <p><strong>Thời gian:</strong> {{ formatDate(selectedOrder.orderTime) }}</p>
              <p><strong>Nhân viên:</strong> {{ selectedOrder.employeeName || 'Không có' }}</p>
              <p><strong>Khách hàng:</strong> {{ selectedOrder.customerName || 'Không có' }}</p>
            </div>
            <div>
              <p><strong>Bàn:</strong> 
                <v-chip v-for="table in selectedOrder.orderTables" :key="table.tableNumber" size="small" class="mr-1 mb-1">
                  {{ table.tableNumber }}
                </v-chip>
              </p>
              <p><strong>Tổng tiền:</strong> {{ formatCurrency(selectedOrder.totalAmount) }}</p>
              <p><strong>Thành tiền:</strong> {{ formatCurrency(selectedOrder.finalAmount) }}</p>
              <p><strong>Trạng thái:</strong> 
                <v-chip :color="getStatusColor(selectedOrder.orderStatus)">
                  {{ getStatusText(selectedOrder.orderStatus) }}
                </v-chip>
              </p>
            </div>
          </div>
          
          <v-divider></v-divider>
          
          <h3 class="text-h6 my-3">Sản phẩm</h3>
          <div v-if="selectedOrder.orderProducts && selectedOrder.orderProducts.length > 0">
            <v-table density="compact">
              <thead>
                <tr>
                  <th>Sản phẩm</th>
                  <th>Số lượng</th>
                  <th>Đơn giá</th>
                  <th>Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, i) in selectedOrder.orderProducts" :key="i">
                  <td>{{ item.productName }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>{{ formatCurrency(item.price) }}</td>
                  <td>{{ formatCurrency(item.price * item.quantity) }}</td>
                </tr>
              </tbody>
            </v-table>
          </div>
          <div v-else class="text-center py-3">
            <v-icon color="grey-lighten-1">mdi-cart-outline</v-icon>
            <p class="text-body-2 text-grey mt-2">Không có thông tin sản phẩm</p>
          </div>

          <v-divider class="my-4"></v-divider>
          
          <!-- Thông tin khuyến mãi -->
          <h3 class="text-h6 my-3">Khuyến mãi áp dụng</h3>
          <div v-if="selectedOrder.orderDiscounts && selectedOrder.orderDiscounts.length > 0">
            <v-table density="compact">
              <thead>
                <tr>
                  <th>Tên khuyến mãi</th>
                  <th>Mã giảm giá</th>
                  <th>Giá trị</th>
                  <th>Số tiền giảm</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(discount, i) in selectedOrder.orderDiscounts" :key="i">
                  <td>{{ discount.name }}</td>
                  <td>{{ discount.couponCode || 'Không có' }}</td>
                  <td>{{ discount.discountValue }}</td>
                  <td>{{ formatCurrency(discount.discountAmount) }}</td>
                </tr>
              </tbody>
            </v-table>
          </div>
          <div v-else class="text-center py-3">
            <v-icon color="grey-lighten-1">mdi-tag-outline</v-icon>
            <p class="text-body-2 text-grey mt-2">Không có khuyến mãi nào được áp dụng</p>
          </div>
          
          <v-divider class="my-4"></v-divider>
          
          <!-- Thông tin thanh toán -->
          <h3 class="text-h6 my-3">Thanh toán</h3>
          <div v-if="selectedOrder.orderPayments && selectedOrder.orderPayments.length > 0">
            <v-table density="compact">
              <thead>
                <tr>
                  <th>Phương thức</th>
                  <th>Số tiền thanh toán</th>
                  <th>Tiền thừa</th>
                  <th>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(payment, i) in selectedOrder.orderPayments" :key="i">
                  <td>{{ payment.paymentMethod?.name || 'Không có' }}</td>
                  <td>{{ formatCurrency(payment.amountPaid) }}</td>
                  <td>{{ formatCurrency(payment.change) }}</td>
                  <td>
                    <v-chip :color="getPaymentStatusColor(payment.status)" size="small">
                      {{ getPaymentStatusText(payment.status) }}
                    </v-chip>
                  </td>
                </tr>
              </tbody>
            </v-table>
          </div>
          <div v-else class="text-center py-3">
            <v-icon color="grey-lighten-1">mdi-cash-register</v-icon>
            <p class="text-body-2 text-grey mt-2">Chưa có thông tin thanh toán</p>
          </div>
        </v-card-text>
        
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="checkoutSelectedOrder" v-if="selectedOrder.orderStatus === 'PENDING'">
            Trả bàn
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog xác nhận thanh toán -->
    <v-dialog v-model="checkoutDialog" max-width="500px">
      <v-card>
        <v-card-title>
          Xác nhận thanh toán
        </v-card-title>
        <v-card-text>
          <p>Bạn có chắc chắn muốn thanh toán đơn hàng #{{ checkoutOrderId }} không?</p>
          <p>Các bàn sẽ được giải phóng sau khi thanh toán.</p>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="checkoutDialog = false">
            Hủy
          </v-btn>
          <v-btn color="success" @click="confirmCheckout" :loading="checkoutLoading">
            Xác nhận
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import OrderService from '../services/order.service';
import { useSnackbar } from '../helpers/useSnackbar';

// Props từ App.vue
const props = defineProps({
  employeeId: {
    type: Number,
    default: 0
  },
  employeeName: {
    type: String,
    default: ''
  }
});

// Trạng thái
const loading = ref(false);
const search = ref('');
const orders = ref([]);
const detailDialog = ref(false);
const selectedOrder = ref(null);
const checkoutDialog = ref(false);
const checkoutOrderId = ref(null);
const checkoutTableNumber = ref(null);
const checkoutLoading = ref(false);

// Phân trang
const currentPage = ref(0);
const pageSize = ref(10);
const totalItems = ref(0);
const totalPages = ref(0);

// Composables
const { showSnackbar } = useSnackbar();

// Cấu hình cho data table
const headers = ref([
  { title: 'Mã đơn', key: 'orderId' },
  { title: 'Bàn', key: 'orderTables' },
  { title: 'Khách hàng', key: 'customerName' },
  { title: 'Tổng tiền', key: 'finalAmount' },
  { title: 'Thời gian tạo', key: 'orderTime' },
  { title: 'Thao tác', key: 'actions', sortable: false }
]);

// Tải danh sách đơn hàng
async function loadOrders(page = currentPage.value, size = pageSize.value) {
  // Đảm bảo page và size luôn là số
  if (typeof page === 'object' || isNaN(page)) {
    console.warn('Invalid page parameter, using default', page);
    page = 0;
  }
  
  if (typeof size === 'object' || isNaN(size)) {
    console.warn('Invalid size parameter, using default', size);
    size = 10;
  }
  
  page = parseInt(page);
  size = parseInt(size);
  
  loading.value = true;
  try {
    if (!props.employeeId) {
      showSnackbar('Không tìm thấy thông tin nhân viên', 'error');
      return;
    }
    
    console.log(`Gọi API với page=${page}, size=${size}`);
    const response = await OrderService.getOrderTables(props.employeeId, page, size);
    
    if (response && response.data) {
      if (Array.isArray(response.data)) {
        // Trường hợp API trả về trực tiếp mảng
        orders.value = response.data;
        totalItems.value = response.data.length;
        totalPages.value = 1;
      } else if (response.data.content && Array.isArray(response.data.content)) {
        // Trường hợp API trả về Page object của Spring
        orders.value = response.data.content;
        totalItems.value = response.data.totalElements || 0;
        totalPages.value = response.data.totalPages || 1;
        currentPage.value = response.data.number || 0;
        pageSize.value = response.data.size || 10;
      } else {
        orders.value = [];
        totalItems.value = 0;
        totalPages.value = 0;
        console.error('Cấu trúc dữ liệu không đúng định dạng:', response.data);
      }
    } else {
      orders.value = [];
      totalItems.value = 0;
      totalPages.value = 0;
    }
  } catch (error) {
    console.error('Lỗi khi tải danh sách đơn hàng:', error);
    showSnackbar(error.message || 'Không thể tải danh sách đơn hàng', 'error');
  } finally {
    loading.value = false;
  }
}

// Xử lý sự kiện khi thay đổi trang
function handlePageChange(page) {
  if (typeof page === 'object') {
    // Nếu page là một đối tượng sự kiện, bỏ qua
    console.warn('Page param is an event object, ignoring', page);
    return;
  }
  
  // Vuetify page bắt đầu từ 1, trong khi Spring page bắt đầu từ 0
  const pageIndex = parseInt(page) - 1;
  currentPage.value = pageIndex >= 0 ? pageIndex : 0;
  
  // Gọi loadOrders với tham số là số
  loadOrders(currentPage.value, pageSize.value);
}

// Xử lý sự kiện khi thay đổi số lượng mục trên trang
function handlePageSizeChange(size) {
  if (typeof size === 'object') {
    // Nếu size là một đối tượng sự kiện, bỏ qua
    console.warn('Size param is an event object, ignoring', size);
    return;
  }
  
  const pageSizeValue = parseInt(size);
  pageSize.value = isNaN(pageSizeValue) ? 10 : pageSizeValue;
  currentPage.value = 0; // Về trang đầu khi thay đổi kích thước trang
  
  // Gọi loadOrders với tham số là số
  loadOrders(currentPage.value, pageSize.value);
}

function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
}

function formatDate(dateString) {
  if (!dateString) return 'Không có';
  
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit', 
    minute: '2-digit'
  }).format(date);
}

function getStatusColor(status) {
  switch (status) {
    case 'COMPLETED':
      return 'success';
    case 'PENDING':
      return 'warning';
    case 'CANCELLED':
      return 'error';
    default:
      return 'grey';
  }
}

function getStatusText(status) {
  switch (status) {
    case 'COMPLETED':
      return 'Đã hoàn thành';
    case 'PENDING':
      return 'Đang xử lý';
    case 'CANCELLED':
      return 'Đã hủy';
    default:
      return 'Không xác định';
  }
}

function getPaymentStatusColor(status) {
  switch (status) {
    case 'PAID':
      return 'success';
    case 'CANCELLED':
      return 'error';
    case 'PROCESSING':
      return 'warning';
    
  }
}

function getPaymentStatusText(status) {
  switch (status) {
    case 'PAID':
      return 'Đã thanh toán';
    case 'CANCELLED':
      return 'Đã hủy';
    case 'PROCESSING':
      return 'Đang xử lý';
    default:
      return 'Không xác định';
  }
}

function viewOrderDetails(order) {
  // Hiển thị tạm thời thông tin đơn hàng từ danh sách
  selectedOrder.value = order;
  detailDialog.value = true;
  
  // Gọi API để lấy thông tin chi tiết đơn hàng
  loadOrderDetails(order.orderId);
}

async function loadOrderDetails(orderId) {
  try {
    const response = await OrderService.getOrderById(orderId);
    if (response && response.data) {
      // Cập nhật lại thông tin chi tiết đơn hàng
      selectedOrder.value = response.data;
    }
  } catch (error) {
    console.error('Lỗi khi tải chi tiết đơn hàng:', error);
    showSnackbar('Không thể tải chi tiết đơn hàng', 'error');
  }
}

function checkoutOrder(order) {
  checkoutOrderId.value = order.orderId;
  checkoutDialog.value = true;
}

function checkoutSelectedOrder() {
  if (selectedOrder.value) {
    checkoutOrderId.value = selectedOrder.value.orderId;
    checkoutDialog.value = true;
    detailDialog.value = false;
  }
}

async function confirmCheckout() {
  checkoutLoading.value = true;
  try {
    // Lấy thông tin đơn hàng cần thanh toán
    const order = orders.value.find(o => o.orderId === checkoutOrderId.value);
    if (!order) {
      showSnackbar('Không tìm thấy thông tin đơn hàng', 'error');
      return;
    }

    // Thực hiện thanh toán đơn hàng
    await OrderService.checkoutTable(checkoutOrderId.value);
    
    showSnackbar('Thanh toán thành công', 'success');
    checkoutDialog.value = false;
    
    // Tải lại danh sách đơn hàng
    await loadOrders();
  } catch (error) {
    console.error('Lỗi khi thanh toán:', error);
    showSnackbar(error.message || 'Không thể thanh toán đơn hàng', 'error');
  } finally {
    checkoutLoading.value = false;
  }
}

// Xử lý sự kiện khi các tùy chọn của bảng dữ liệu thay đổi
function handleDataTableOptionsChange(options) {
  console.log('Data table options changed:', options);

  // Xử lý thay đổi trang
  const newPage = options.page - 1; // Vuetify page bắt đầu từ 1, Spring page bắt đầu từ 0
  
  // Xử lý thay đổi kích thước trang
  const newPageSize = options.itemsPerPage;
  
  // Chỉ gọi API khi có thay đổi thực sự
  if (newPage !== currentPage.value || newPageSize !== pageSize.value) {
    currentPage.value = newPage;
    pageSize.value = newPageSize;
    
    // Gọi API với các tham số phân trang mới
    loadOrders(currentPage.value, pageSize.value);
  }
}

// Phương thức làm mới dữ liệu
function refreshOrders() {
  // Reset về trang đầu tiên khi làm mới
  currentPage.value = 0;
  // Gọi loadOrders với tham số rõ ràng
  loadOrders(0, pageSize.value);
}

onMounted(() => {
  loadOrders();
});
</script> 