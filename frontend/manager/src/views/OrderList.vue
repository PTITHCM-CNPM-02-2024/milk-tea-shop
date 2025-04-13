<template>
  <dashboard-layout>
    <div>
      <v-card>
        <v-card-title>
          <h1 class="text-h5 font-weight-medium">Quản lý đơn hàng</h1>
        </v-card-title>

        <!-- Hiển thị thông báo lỗi nếu có -->
        <v-alert
          v-if="orderStore.error"
          type="error"
          variant="tonal"
          closable
          class="mx-4 mt-2"
        >
          {{ orderStore.error }}
        </v-alert>

        <v-card-text>
          <div class="d-flex align-center my-4">
            <v-text-field
              v-model="searchOrder"
              label="Tìm kiếm đơn hàng"
              prepend-inner-icon="mdi-magnify"
              density="compact"
              hide-details
              class="mr-4"
              bg-color="background"
              variant="outlined"
              style="max-width: 300px;"
            ></v-text-field>
            <v-spacer></v-spacer>
            <v-btn color="primary" class="text-none" rounded="lg" prepend-icon="mdi-plus">
              TẠO ĐƠN HÀNG MỚI
            </v-btn>
          </div>

          <!-- Danh sách đơn hàng -->
          <v-data-table
            :headers="orderHeaders"
            :items="orderStore.orders || []"
            :loading="orderStore.loading"
            hover
            class="mt-2 bg-surface rounded"
          >
            <template v-slot:no-data>
              <div class="text-center py-6">Không có dữ liệu đơn hàng</div>
            </template>
            
            <template v-slot:item.orderTime="{ item }">
              {{ formatDateTime(item.orderTime) }}
            </template>

            <template v-slot:item.totalAmount="{ item }">
              {{ formatCurrency(item.totalAmount) }}
            </template>

            <template v-slot:item.finalAmount="{ item }">
              {{ formatCurrency(item.finalAmount) }}
            </template>

            <template v-slot:item.orderStatus="{ item }">
              <v-chip
                :color="getOrderStatusColor(item.orderStatus)"
                :text="getOrderStatusText(item.orderStatus)"
                size="small"
              ></v-chip>
            </template>

            <template v-slot:item.actions="{ item }">
              <div class="d-flex">
                <v-btn icon variant="text" color="primary" size="small" @click="viewOrderDetails(item.orderId)">
                  <v-icon>mdi-eye</v-icon>
                  <v-tooltip activator="parent" location="top">Xem chi tiết</v-tooltip>
                </v-btn>
                <v-btn icon variant="text" color="warning" size="small">
                  <v-icon>mdi-pencil</v-icon>
                  <v-tooltip activator="parent" location="top">Chỉnh sửa</v-tooltip>
                </v-btn>
                <v-btn icon variant="text" color="error" size="small" :disabled="!canCancel(item.orderStatus)">
                  <v-icon>mdi-cancel</v-icon>
                  <v-tooltip activator="parent" location="top">Hủy đơn</v-tooltip>
                </v-btn>
              </div>
            </template>

            <template v-slot:bottom>
              <div class="d-flex align-center justify-center py-2">
                <v-pagination
                  v-model="currentPage"
                  :length="Math.ceil(orderStore.pagination.total / orderStore.pagination.size) || 1"
                  total-visible="7"
                  @update:modelValue="onPageChange"
                ></v-pagination>
              </div>
            </template>
          </v-data-table>
        </v-card-text>
      </v-card>

      <!-- Dialog chi tiết đơn hàng -->
      <v-dialog v-model="orderDetailsDialog" fullscreen>
        <v-card>
          <v-toolbar color="primary" density="compact">
            <v-btn icon="mdi-close" @click="orderDetailsDialog = false"></v-btn>
            <v-toolbar-title>Chi tiết đơn hàng #{{ selectedOrderId }}</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon="mdi-printer" @click="printOrder">
              <v-tooltip activator="parent" location="bottom">In đơn hàng</v-tooltip>
            </v-btn>
          </v-toolbar>

          <v-card-text v-if="orderStore.currentOrder" class="pa-4">
            <v-overlay v-model="orderStore.orderLoading" contained class="align-center justify-center">
              <v-progress-circular indeterminate size="64"></v-progress-circular>
            </v-overlay>

            <!-- Thông tin đơn hàng -->
            <v-row>
              <v-col cols="12" md="6">
                <v-card flat border>
                  <v-card-title class="text-subtitle-1 font-weight-bold bg-light-blue-lighten-5 py-2 px-4">
                    Thông tin cơ bản
                  </v-card-title>
                  <v-card-text class="py-3">
                    <div class="d-flex align-center mb-2">
                      <v-icon class="me-2" size="small">mdi-account</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Nhân viên:</span>
                      <span class="font-weight-medium">{{ orderStore.currentOrder.employeeName }}</span>
                    </div>
                    <div class="d-flex align-center mb-2">
                      <v-icon class="me-2" size="small">mdi-account-star</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Khách hàng:</span>
                      <span class="font-weight-medium">{{ orderStore.currentOrder.customerName || 'Khách vãng lai' }}</span>
                    </div>
                    <div class="d-flex align-center mb-2">
                      <v-icon class="me-2" size="small">mdi-calendar</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Thời gian đặt:</span>
                      <span class="font-weight-medium">{{ formatDateTime(orderStore.currentOrder.orderTime) }}</span>
                    </div>
                    <div class="d-flex align-center">
                      <v-icon class="me-2" size="small">mdi-note-text</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Ghi chú:</span>
                      <span class="font-weight-medium">{{ orderStore.currentOrder.note || 'Không có' }}</span>
                    </div>
                  </v-card-text>
                </v-card>
              </v-col>

              <v-col cols="12" md="6">
                <v-card flat border>
                  <v-card-title class="text-subtitle-1 font-weight-bold bg-light-blue-lighten-5 py-2 px-4">
                    Thông tin thanh toán
                  </v-card-title>
                  <v-card-text class="py-3">
                    <div class="d-flex align-center mb-2">
                      <v-icon class="me-2" size="small">mdi-cash</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Tổng tiền:</span>
                      <span class="font-weight-medium">{{ formatCurrency(orderStore.currentOrder.totalAmount) }}</span>
                    </div>
                    <div class="d-flex align-center mb-2">
                      <v-icon class="me-2" size="small">mdi-tag</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Giảm giá:</span>
                      <span class="font-weight-medium">{{ calculateDiscount(orderStore.currentOrder) }}</span>
                    </div>
                    <div class="d-flex align-center mb-2">
                      <v-icon class="me-2" size="small">mdi-cash-check</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Thành tiền:</span>
                      <span class="font-weight-bold text-primary">{{ formatCurrency(orderStore.currentOrder.finalAmount) }}</span>
                    </div>
                    <div class="d-flex align-center">
                      <v-icon class="me-2" size="small">mdi-check-decagram</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Trạng thái:</span>
                      <v-chip
                        :color="getOrderStatusColor(orderStore.currentOrder.orderStatus)"
                        :text="getOrderStatusText(orderStore.currentOrder.orderStatus)"
                        size="small"
                      ></v-chip>
                    </div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>

            <!-- Chi tiết bàn -->
            <v-card flat border class="mt-4">
              <v-card-title class="text-subtitle-1 font-weight-bold bg-light-blue-lighten-5 py-2 px-4">
                Thông tin bàn
              </v-card-title>
              <v-card-text class="py-3">
                <div v-if="orderStore.currentOrder.orderTables && orderStore.currentOrder.orderTables.length > 0">
                  <v-chip
                    v-for="table in orderStore.currentOrder.orderTables"
                    :key="table.tableNumber"
                    class="me-2 mb-2"
                    color="blue-lighten-5"
                    label
                  >
                    <template v-slot:prepend>
                      <v-icon size="small">mdi-table-chair</v-icon>
                    </template>
                    Bàn {{ table.tableNumber }}
                  </v-chip>
                </div>
                <div v-else class="text-grey">Không có thông tin bàn</div>
              </v-card-text>
            </v-card>

            <!-- Chi tiết sản phẩm -->
            <v-card flat border class="mt-4">
              <v-card-title class="text-subtitle-1 font-weight-bold bg-light-blue-lighten-5 py-2 px-4">
                Danh sách sản phẩm
              </v-card-title>
              <v-card-text class="pa-0">
                <v-table density="comfortable">
                  <thead>
                    <tr>
                      <th class="text-left">STT</th>
                      <th class="text-left">Tên sản phẩm</th>
                      <th class="text-right">Đơn giá</th>
                      <th class="text-right">Số lượng</th>
                      <th class="text-right">Thành tiền</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(product, index) in orderStore.currentOrder.orderProducts" :key="index">
                      <td>{{ index + 1 }}</td>
                      <td>{{ product.productName }}</td>
                      <td class="text-right">{{ formatCurrency(product.price) }}</td>
                      <td class="text-right">{{ product.quantity }}</td>
                      <td class="text-right">{{ formatCurrency(product.price * product.quantity) }}</td>
                    </tr>
                  </tbody>
                </v-table>
              </v-card-text>
            </v-card>

            <!-- Chi tiết khuyến mãi -->
            <v-card flat border class="mt-4" v-if="orderStore.currentOrder.orderDiscounts && orderStore.currentOrder.orderDiscounts.length > 0">
              <v-card-title class="text-subtitle-1 font-weight-bold bg-light-blue-lighten-5 py-2 px-4">
                Khuyến mãi áp dụng
              </v-card-title>
              <v-card-text class="pa-0">
                <v-table density="comfortable">
                  <thead>
                    <tr>
                      <th class="text-left">STT</th>
                      <th class="text-left">Tên khuyến mãi</th>
                      <th class="text-left">Mã coupon</th>
                      <th class="text-right">Giảm giá</th>
                      <th class="text-right">Số tiền giảm</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(discount, index) in orderStore.currentOrder.orderDiscounts" :key="index">
                      <td>{{ index + 1 }}</td>
                      <td>{{ discount.name }}</td>
                      <td>{{ discount.couponCode }}</td>
                      <td class="text-right">{{ discount.discountValue }}</td>
                      <td class="text-right">{{ formatCurrency(discount.discountAmount) }}</td>
                    </tr>
                  </tbody>
                </v-table>
              </v-card-text>
            </v-card>

            <!-- Chi tiết thanh toán -->
            <v-card flat border class="mt-4">
              <v-card-title class="text-subtitle-1 font-weight-bold bg-light-blue-lighten-5 py-2 px-4">
                Thông tin thanh toán
              </v-card-title>
              <v-card-text class="pa-0">
                <v-table density="comfortable">
                  <thead>
                    <tr>
                      <th class="text-left">STT</th>
                      <th class="text-left">Phương thức</th>
                      <th class="text-right">Số tiền thanh toán</th>
                      <th class="text-right">Tiền thừa</th>
                      <th class="text-center">Trạng thái</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(payment, index) in orderStore.currentOrder.orderPayments" :key="index">
                      <td>{{ index + 1 }}</td>
                      <td>{{ payment.paymentMethod.name }}</td>
                      <td class="text-right">{{ formatCurrency(payment.amountPaid) }}</td>
                      <td class="text-right">{{ formatCurrency(payment.change) }}</td>
                      <td class="text-center">
                        <v-chip
                          :color="getPaymentStatusColor(payment.status)"
                          :text="getPaymentStatusText(payment.status)"
                          size="small"
                        ></v-chip>
                      </td>
                    </tr>
                  </tbody>
                </v-table>
              </v-card-text>
            </v-card>
          </v-card-text>

          <v-card-actions class="pt-0 pb-4 px-4">
            <v-spacer></v-spacer>
            <v-btn 
              color="primary" 
              variant="tonal" 
              @click="orderDetailsDialog = false"
            >
              Đóng
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </div>
  </dashboard-layout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useOrderStore } from '@/stores/order'
import DashboardLayout from '@/components/layouts/DashboardLayout.vue'

const orderStore = useOrderStore()
const searchOrder = ref('')
const currentPage = ref(1)
const orderDetailsDialog = ref(false)
const selectedOrderId = ref(null)

const orderHeaders = [
  { title: 'Mã đơn hàng', key: 'orderId', align: 'start' },
  { title: 'Thời gian đặt', key: 'orderTime' },
  { title: 'Tổng tiền', key: 'totalAmount' },
  { title: 'Thanh toán', key: 'finalAmount' },
  { title: 'Trạng thái', key: 'orderStatus' },
  { title: 'Thao tác', key: 'actions', sortable: false, align: 'center' }
]

// Format tiền tệ
const formatCurrency = (value) => {
  if (!value) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

// Format ngày tháng
const formatDateTime = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('vi-VN')
}

// Xử lý khi thay đổi trang
const onPageChange = (page) => {
  orderStore.fetchOrders(page - 1)
}

// Lấy màu sắc trạng thái đơn hàng
const getOrderStatusColor = (status) => {
  switch (status) {
    case 'PENDING': return 'warning'
    case 'PROCESSING': return 'info'
    case 'COMPLETED': return 'success'
    case 'CANCELED': return 'error'
    default: return 'grey'
  }
}

// Lấy text trạng thái đơn hàng
const getOrderStatusText = (status) => {
  switch (status) {
    case 'PENDING': return 'Chờ xử lý'
    case 'PROCESSING': return 'Đang xử lý'
    case 'COMPLETED': return 'Hoàn thành'
    case 'CANCELED': return 'Đã hủy'
    default: return 'Không xác định'
  }
}

// Lấy màu sắc trạng thái thanh toán
const getPaymentStatusColor = (status) => {
  switch (status) {
    case 'PENDING': return 'warning'
    case 'COMPLETED': return 'success'
    case 'FAILED': return 'error'
    default: return 'grey'
  }
}

// Lấy text trạng thái thanh toán
const getPaymentStatusText = (status) => {
  switch (status) {
    case 'PENDING': return 'Chờ xử lý'
    case 'COMPLETED': return 'Hoàn thành'
    case 'FAILED': return 'Thất bại'
    default: return 'Không xác định'
  }
}

// Kiểm tra đơn hàng có thể hủy hay không
const canCancel = (status) => {
  return status === 'PENDING' || status === 'PROCESSING'
}

// Tính tổng tiền giảm giá
const calculateDiscount = (order) => {
  if (!order || !order.orderDiscounts || order.orderDiscounts.length === 0) {
    return formatCurrency(0)
  }
  
  const totalDiscount = order.orderDiscounts.reduce((sum, discount) => sum + discount.discountAmount, 0)
  return formatCurrency(totalDiscount)
}

// Xem chi tiết đơn hàng
const viewOrderDetails = async (orderId) => {
  selectedOrderId.value = orderId
  await orderStore.fetchOrderById(orderId)
  orderDetailsDialog.value = true
}

// In đơn hàng
const printOrder = () => {
  window.print()
}

// Khởi tạo dữ liệu
onMounted(async () => {
  await orderStore.fetchOrders()
})
</script>

<style scoped>
.v-data-table {
  border-radius: 8px;
  overflow: hidden;
}
</style> 