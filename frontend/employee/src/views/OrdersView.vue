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
        <v-btn color="primary" class="ml-2" prepend-icon="mdi-refresh" @click="loadOrders">
          Làm mới
        </v-btn>
      </v-card-title>
      
      <v-data-table
        v-if="orders.length > 0"
        :headers="headers"
        :items="orders"
        :search="search"
        :loading="loading"
        loading-text="Đang tải dữ liệu..."
        no-data-text="Không có đơn hàng nào"
        class="elevation-1"
      >
        <template v-slot:item.tables="{ item }">
          <v-chip 
            v-for="table in item.tables" 
            :key="table.id" 
            size="small"
            class="mr-1"
          >
            {{ table.name }}
          </v-chip>
        </template>
        
        <template v-slot:item.customer="{ item }">
          <span v-if="item.customer">{{ item.customer.firstName }} {{ item.customer.lastName }}</span>
          <span v-else class="text-caption text-disabled">Không có</span>
        </template>
        
        <template v-slot:item.total="{ item }">
          {{ formatCurrency(item.total) }}
        </template>
        
        <template v-slot:item.createdAt="{ item }">
          {{ formatDate(item.createdAt) }}
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
    </v-card>
    
    <!-- Dialog chi tiết đơn hàng -->
    <v-dialog v-model="detailDialog" max-width="700px">
      <v-card v-if="selectedOrder">
        <v-card-title>
          Chi tiết đơn hàng #{{ selectedOrder.id }}
          <v-spacer></v-spacer>
          <v-btn icon @click="detailDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        
        <v-card-text>
          <div class="d-flex mb-4">
            <div class="flex-grow-1">
              <p><strong>Thời gian:</strong> {{ formatDate(selectedOrder.createdAt) }}</p>
              <p><strong>Nhân viên:</strong> {{ selectedOrder.employee?.name || 'Không có' }}</p>
              <p><strong>Khách hàng:</strong> {{ selectedOrder.customer ? 
                `${selectedOrder.customer.firstName} ${selectedOrder.customer.lastName}` : 'Không có' }}</p>
            </div>
            <div>
              <p><strong>Bàn:</strong> 
                <v-chip v-for="table in selectedOrder.tables" :key="table.id" size="small" class="mr-1 mb-1">
                  {{ table.name }}
                </v-chip>
              </p>
              <p><strong>Tổng tiền:</strong> {{ formatCurrency(selectedOrder.total) }}</p>
              <p><strong>Trạng thái:</strong> 
                <v-chip color="warning">Đang sử dụng</v-chip>
              </p>
            </div>
          </div>
          
          <v-divider></v-divider>
          
          <h3 class="text-h6 my-3">Sản phẩm</h3>
          <v-table density="compact">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th>Kích thước</th>
                <th>Số lượng</th>
                <th>Đơn giá</th>
                <th>Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, i) in selectedOrder.items" :key="i">
                <td>{{ item.product.name }}</td>
                <td>{{ item.size.name }}</td>
                <td>{{ item.quantity }}</td>
                <td>{{ formatCurrency(item.price) }}</td>
                <td>{{ formatCurrency(item.price * item.quantity) }}</td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
        
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="checkoutOrder(selectedOrder)">
            Thanh toán
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import OrderService from '../services/order.service';

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

// Cấu hình cho data table
const headers = ref([
  { title: 'Mã đơn', key: 'id' },
  { title: 'Bàn', key: 'tables' },
  { title: 'Khách hàng', key: 'customer' },
  { title: 'Tổng tiền', key: 'total' },
  { title: 'Thời gian tạo', key: 'createdAt' }
]);

// // Tải danh sách đơn hàng
// async function loadOrders() {
//   loading.value = true;
//   try {
//     const response = await OrderService.getActiveTableOrders();
//     orders.value = response.data;
//   } catch (error) {
//     console.error('Lỗi khi tải danh sách đơn hàng:', error);
//     alert('Không thể tải danh sách đơn hàng');
//   } finally {
//     loading.value = false;
//   }
// }

function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
}

function formatDate(dateString) {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit', 
    minute: '2-digit'
  }).format(date);
}

function viewOrderDetails(order) {
  selectedOrder.value = order;
  detailDialog.value = true;
}

function checkoutOrder(order) {
  // Xử lý thanh toán đơn hàng
  alert(`Thanh toán đơn hàng #${order.id}`);
  // Có thể chuyển hướng đến trang thanh toán hoặc mở modal thanh toán
}

onMounted(() => {
  //loadOrders();
});
</script> 