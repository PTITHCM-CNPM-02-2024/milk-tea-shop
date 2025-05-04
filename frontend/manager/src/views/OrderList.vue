<template>
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
          
          <v-select
            v-model="selectedStatus"
            :items="statusOptions"
            label="Trạng thái"
            density="compact"
            hide-details
            variant="outlined"
            style="max-width: 200px;"
            class="mr-4"
          ></v-select>
          
          <v-spacer></v-spacer>
          
          <!-- Nút xuất báo cáo -->
          <v-btn
            prepend-icon="mdi-file-export"
            color="primary"
            @click="exportReport"
          >
            Xuất báo cáo
          </v-btn>
        </div>

        <!-- Menu lựa chọn định dạng báo cáo -->
        <v-dialog v-model="exportDialog" max-width="400">
          <v-card>
            <v-card-title class="text-h5 font-weight-bold pa-4">
              Xuất báo cáo đơn hàng
            </v-card-title>
            
            <v-divider></v-divider>
            
            <v-card-text class="pa-4">
              <p class="mb-4">Chọn loại báo cáo muốn xuất:</p>
              
              <v-radio-group v-model="exportType">
                <v-radio value="all" label="Tất cả đơn hàng"></v-radio>
                <v-radio value="filtered" label="Đơn hàng đang hiển thị"></v-radio>
              </v-radio-group>
              
              <v-radio-group v-model="exportFormat" class="mt-4">
                <v-radio value="excel" label="Excel (.xlsx)"></v-radio>
                <v-radio value="csv" label="CSV (.csv)"></v-radio>
              </v-radio-group>
            </v-card-text>
            
            <v-card-actions class="pa-4">
              <v-spacer></v-spacer>
              <v-btn variant="text" @click="exportDialog = false">Hủy</v-btn>
              <v-btn 
                color="primary" 
                @click="generateReport" 
                :loading="exportLoading"
              >
                Xuất báo cáo
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- Danh sách đơn hàng -->
        <v-data-table
          :headers="orderHeaders"
          :items="filteredOrders"
          :loading="orderStore.loading"
          hover
          class="mt-2 bg-surface rounded"
          :items-per-page="10"
          :page="currentPage"
          @update:page="currentPage = $event"
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
            </div>
          </template>

          <template v-slot:bottom>
            <div class="d-flex align-center justify-center py-2">
              <v-pagination
                v-model="currentPage"
                :length="Math.ceil(filteredOrders.length / 10) || 1"
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
                    <th class="text-center">Thao tác</th>
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
                    <td class="text-center">
                      <v-btn icon="mdi-eye" variant="text" size="small" color="primary" @click="viewPaymentDetails(payment.id)">
                        <v-tooltip activator="parent" location="top">Xem chi tiết</v-tooltip>
                      </v-btn>
                    </td>
                  </tr>
                </tbody>
              </v-table>
            </v-card-text>
          </v-card>
        </v-card-text>
      </v-card>
    </v-dialog>
    
    <!-- Dialog chi tiết thanh toán -->
    <v-dialog v-model="paymentDetailsDialog" max-width="700">
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          Chi tiết thanh toán
        </v-card-title>

        <v-divider></v-divider>

        <v-card-text class="pa-4">
          <v-overlay v-model="paymentLoading" contained class="align-center justify-center">
            <v-progress-circular indeterminate size="64"></v-progress-circular>
          </v-overlay>

          <v-row>
            <v-col cols="12">
              <v-card flat border>
                <v-card-title class="text-subtitle-1 font-weight-bold bg-light-blue-lighten-5 py-2 px-4">
                  Thông tin thanh toán
                </v-card-title>
                <v-card-text class="py-3">
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-identifier</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Mã thanh toán:</span>
                    <span class="font-weight-medium">{{ selectedPayment.id }}</span>
                  </div>
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-cash-register</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Phương thức thanh toán:</span>
                    <span class="font-weight-medium">{{ selectedPayment.paymentMethod.name }}</span>
                  </div>
                  <div class="d-flex align-center mb-3" v-if="selectedPayment.paymentMethod.description">
                    <v-icon class="me-2" size="small">mdi-text-box</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Mô tả phương thức:</span>
                    <span class="font-weight-medium">{{ selectedPayment.paymentMethod.description }}</span>
                  </div>
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-calendar-clock</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Thời gian thanh toán:</span>
                    <span class="font-weight-medium">{{ formatDateTime(selectedPayment.paymentTime) }}</span>
                  </div>
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-check-circle</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Trạng thái thanh toán:</span>
                    <v-chip
                      :color="getPaymentStatusColor(selectedPayment.status)"
                      :text="getPaymentStatusText(selectedPayment.status)"
                      size="small"
                    ></v-chip>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>

          <v-row class="mt-2">
            <v-col cols="12" md="6">
              <v-card flat border height="100%">
                <v-card-title class="text-subtitle-1 font-weight-bold bg-light-blue-lighten-5 py-2 px-4">
                  Thông tin tài chính
                </v-card-title>
                <v-card-text class="py-3">
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-cash-plus</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Số tiền thanh toán:</span>
                    <span class="font-weight-bold">{{ formatCurrency(selectedPayment.amountPaid) }}</span>
                  </div>
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-cash-refund</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Tiền thừa:</span>
                    <span class="font-weight-medium">{{ formatCurrency(selectedPayment.change) }}</span>
                  </div>
                  <div class="border-top pt-3 mt-3">
                    <span class="text-body-2 text-grey-darken-1">Tổng thanh toán thực tế:</span>
                    <div class="font-weight-bold text-h6 text-primary mt-1">
                      {{ formatCurrency(selectedPayment.amountPaid - (selectedPayment.change || 0)) }}
                    </div>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>

            <v-col cols="12" md="6">
              <v-card flat border height="100%">
                <v-card-title class="text-subtitle-1 font-weight-bold bg-light-blue-lighten-5 py-2 px-4">
                  Thông tin đơn hàng
                </v-card-title>
                <v-card-text class="py-3">
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-file-document</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Mã đơn hàng:</span>
                    <span class="font-weight-medium">{{ selectedPayment.order.orderId }}</span>
                    <v-btn 
                      variant="text" 
                      size="small" 
                      color="primary" 
                      class="ms-2" 
                      @click="viewOrderFromPayment(selectedPayment.order.orderId)"
                    >
                      Xem đơn hàng
                    </v-btn>
                  </div>
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-cash-multiple</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Tổng tiền đơn hàng:</span>
                    <span class="font-weight-medium">{{ formatCurrency(selectedPayment.order.totalAmount) }}</span>
                  </div>
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-cash</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Thành tiền:</span>
                    <span class="font-weight-medium">{{ formatCurrency(selectedPayment.order.finalAmount) }}</span>
                  </div>
                  <div class="d-flex align-center mb-3">
                    <v-icon class="me-2" size="small">mdi-flag-variant</v-icon>
                    <span class="text-body-2 text-grey-darken-1 me-1">Trạng thái đơn hàng:</span>
                    <v-chip
                      :color="getOrderStatusColor(selectedPayment.order.orderStatus)"
                      :text="getOrderStatusText(selectedPayment.order.orderStatus)"
                      size="small"
                    ></v-chip>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn color="primary" variant="tonal" @click="paymentDetailsDialog = false">
            Đóng
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useOrderStore } from '@/stores/order'

const orderStore = useOrderStore()
const searchOrder = ref('')
const currentPage = ref(1)
const orderDetailsDialog = ref(false)
const selectedOrderId = ref(null)
const paymentDetailsDialog = ref(false)
const selectedPayment = ref(null)
const paymentLoading = ref(false)
const exportDialog = ref(false)
const exportType = ref('all')
const exportFormat = ref('excel')
const exportLoading = ref(false)
const selectedStatus = ref(null)

const orderHeaders = [
  { title: 'Mã đơn hàng', key: 'orderId', align: 'start' },
  { title: 'Thời gian đặt', key: 'orderTime' },
  { title: 'Tổng tiền', key: 'totalAmount' },
  { title: 'Thanh toán', key: 'finalAmount' },
  { title: 'Trạng thái', key: 'orderStatus' },
  { title: 'Thao tác', key: 'actions', sortable: false, align: 'center', width: '100px' }
]

// Danh sách trạng thái đơn hàng
const statusOptions = [
  { title: 'Tất cả', value: null },
  { title: 'Đang xử lý', value: 'PROCESSING' },
  { title: 'Đã xuất hóa đơn', value: 'COMPLETED' },
  { title: 'Đã hủy', value: 'CANCELED' }
]

// Computed để lọc đơn hàng theo từ khóa tìm kiếm và trạng thái
const filteredOrders = computed(() => {
  let result = [...orderStore.orders]
  
  // Lọc theo từ khóa tìm kiếm
  if (searchOrder.value) {
    const query = searchOrder.value.toLowerCase()
    result = result.filter(order => 
      order.orderId.toString().includes(query) || 
      (order.customerName && order.customerName.toLowerCase().includes(query)) ||
      (order.employeeName && order.employeeName.toLowerCase().includes(query))
    )
  }
  
  // Lọc theo trạng thái
  if (selectedStatus.value) {
    result = result.filter(order => order.orderStatus === selectedStatus.value)
  }
  
  return result
})

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
  currentPage.value = page
}

// Lấy màu sắc trạng thái đơn hàng
const getOrderStatusColor = (status) => {
  switch (status) {
    case 'PROCESSING': return 'info'
    case 'COMPLETED': return 'success'
    case 'CANCELED': return 'error'
    default: return 'grey'
  }
}

// Lấy text trạng thái đơn hàng
const getOrderStatusText = (status) => {
  switch (status) {
    case 'PROCESSING': return 'Đang xử lý'
    case 'COMPLETED': return 'Đã xuất hóa đơn'
    case 'CANCELED': return 'Đã hủy'
    default: return 'Không xác định'
  }
}

// Lấy màu sắc trạng thái thanh toán
const getPaymentStatusColor = (status) => {
  switch (status) {
    case 'PROCESSING': return 'warning'
    case 'PAID': return 'success'
    case 'CANCELED': return 'error'
    default: return 'grey'
  }
}

// Lấy text trạng thái thanh toán
const getPaymentStatusText = (status) => {
  switch (status) {
    case 'PROCESSING': return 'Chờ xử lý'
    case 'PAID': return 'Đã thanh toán'
    case 'CANCELED': return 'Đã hủy'
    default: return 'Không xác định'
  }
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
  try {
    selectedOrderId.value = orderId
    await orderStore.fetchOrderById(orderId)
    orderDetailsDialog.value = true
  } catch (error) {
    // Lỗi đã được xử lý và gán vào orderStore.error trong store
    console.error('Lỗi khi tải chi tiết đơn hàng:', error)
    // Không cần gán lại error ở đây vì store đã làm
  }
}

// In đơn hàng
const printOrder = () => {
  window.print()
}

// Xem chi tiết thanh toán
const viewPaymentDetails = async (paymentId) => {
  try {
    paymentLoading.value = true
    
    // Giả định rằng có một phương thức fetchPaymentById trong orderStore
    // Nếu không có, bạn cần thêm nó vào orderStore hoặc tạo một paymentStore riêng
    const payment = await orderStore.fetchPaymentById(paymentId)
    console.log(payment)
    selectedPayment.value = payment
    
    paymentDetailsDialog.value = true
  } catch (error) {
    console.error('Lỗi khi tải chi tiết thanh toán:', error)
    // Gán lỗi vào store để hiển thị alert chung nếu cần
    // orderStore.error = error.message || 'Lỗi khi tải chi tiết thanh toán' 
    // Xem xét có nên hiển thị lỗi này trong v-alert chính không
  } finally {
    paymentLoading.value = false
  }
}

// Xem đơn hàng từ chi tiết thanh toán
const viewOrderFromPayment = (orderId) => {
  paymentDetailsDialog.value = false
  viewOrderDetails(orderId)
}

// Xuất báo cáo
const exportReport = () => {
  exportDialog.value = true
}

// Tạo báo cáo
const generateReport = async () => {
  try {
    exportLoading.value = true
    
    // Sử dụng danh sách đơn hàng đã lọc nếu chọn xuất báo cáo đơn hàng đang hiển thị
    let reportData = exportType.value === 'filtered' ? filteredOrders.value : null
    
    await orderStore.generateReport(exportType.value, exportFormat.value, reportData)
    
    // Sau khi tạo báo cáo thành công, đóng dialog
    exportDialog.value = false
  } catch (error) {
    console.error('Lỗi khi tạo báo cáo:', error)
    // Gán lỗi vào store để hiển thị alert chung
    orderStore.error = error.message || 'Lỗi khi tạo báo cáo'
  } finally {
    exportLoading.value = false
  }
}

// Khởi tạo dữ liệu
onMounted(async () => {
  try {
    await orderStore.fetchOrders()
  } catch (error) {
    // Lỗi đã được xử lý và gán vào orderStore.error trong store
    console.error('Lỗi khi tải danh sách đơn hàng ban đầu:', error)
    // Không cần gán lại error ở đây vì store đã làm
  }
})

// Watch tìm kiếm và lọc
watch([searchOrder, selectedStatus], () => {
  currentPage.value = 1 // Reset về trang đầu khi tìm kiếm hoặc lọc
})
</script>

<style scoped>
.v-data-table {
  border-radius: 8px;
  overflow: hidden;
}
</style> 