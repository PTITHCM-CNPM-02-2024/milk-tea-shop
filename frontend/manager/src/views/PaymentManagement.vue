<template>
  <div>
    <v-card>
      <v-card-title>
          <h1 class="text-h5 font-weight-medium">Quản lý thanh toán</h1>
        </v-card-title>

        <!-- Hiển thị thông báo lỗi nếu có -->
        <v-alert
          v-if="paymentStore.error"
          type="error"
          variant="tonal"
          closable
          class="mx-4 mt-2"
        >
          {{ paymentStore.error }}
        </v-alert>

        <v-tabs v-model="activeTab" color="primary" class="px-4">
          <v-tab value="payments" class="text-none">Thanh toán</v-tab>
          <v-tab value="payment-methods" class="text-none">Phương thức thanh toán</v-tab>
        </v-tabs>

        <v-window v-model="activeTab" class="px-4 pb-4">
          <!-- Tab Thanh toán -->
          <v-window-item value="payments">
            <div class="d-flex align-center my-4">
              <v-text-field
                v-model="searchPayment"
                label="Tìm kiếm thanh toán"
                prepend-inner-icon="mdi-magnify"
                density="compact"
                hide-details
                class="mr-4"
                bg-color="background"
                variant="outlined"
                style="max-width: 300px;"
              ></v-text-field>
              <v-spacer></v-spacer>
              <v-btn-group variant="outlined">
                <v-btn color="primary" class="text-none" prepend-icon="mdi-filter-variant">
                  Lọc
                </v-btn>
                <v-btn color="primary" class="text-none" prepend-icon="mdi-export" @click="generateReport">
                  Xuất báo cáo
                </v-btn>
              </v-btn-group>
            </div>

            <!-- Thống kê tổng quan -->
            <v-row class="mb-4">
              <v-col cols="12" md="4">
                <v-card color="primary" theme="dark" class="px-4 py-3">
                  <div class="text-subtitle-2">Tổng số thanh toán</div>
                  <div class="d-flex align-center justify-space-between">
                    <div class="text-h4 font-weight-bold">{{ paymentStore.paymentReport.totalPayment || 0 }}</div>
                    <v-icon size="42" class="text-white">mdi-credit-card-outline</v-icon>
                  </div>
                  <div class="text-caption">Tháng {{ reportMonth }}/{{ reportYear }}</div>
                </v-card>
              </v-col>
              <v-col cols="12" md="4">
                <v-card color="success" theme="dark" class="px-4 py-3">
                  <div class="text-subtitle-2">Tổng doanh thu</div>
                  <div class="d-flex align-center justify-space-between">
                    <div class="text-h4 font-weight-bold">{{ formatCurrency(paymentStore.paymentReport.totalAmount) }}</div>
                    <v-icon size="42" class="text-white">mdi-cash-multiple</v-icon>
                  </div>
                  <div class="text-caption">Tháng {{ reportMonth }}/{{ reportYear }}</div>
                </v-card>
              </v-col>
              <v-col cols="12" md="4">
                <v-card color="info" theme="dark" class="px-4 py-3">
                  <div class="text-subtitle-2">Trung bình mỗi đơn</div>
                  <div class="d-flex align-center justify-space-between">
                    <div class="text-h4 font-weight-bold">{{ formatCurrency(paymentStore.paymentReport.averageAmount) }}</div>
                    <v-icon size="42" class="text-white">mdi-chart-line</v-icon>
                  </div>
                  <div class="text-caption">Tháng {{ reportMonth }}/{{ reportYear }}</div>
                </v-card>
              </v-col>
            </v-row>

            <!-- Bộ lọc báo cáo -->
            <v-card class="mb-4 px-4 py-3">
              <div class="d-flex flex-wrap align-center">
                <div class="me-4 mb-2">
                  <v-select
                    v-model="reportMonth"
                    :items="monthOptions"
                    label="Tháng"
                    density="compact"
                    variant="outlined"
                    style="width: 120px"
                    hide-details
                  ></v-select>
                </div>
                <div class="me-4 mb-2">
                  <v-select
                    v-model="reportYear"
                    :items="yearOptions"
                    label="Năm"
                    density="compact"
                    variant="outlined"
                    style="width: 120px"
                    hide-details
                  ></v-select>
                </div>
                <v-btn 
                  color="primary" 
                  prepend-icon="mdi-refresh" 
                  class="mb-2"
                  :loading="paymentStore.reportLoading"
                  @click="generateReport"
                >
                  Tạo báo cáo
                </v-btn>
                <v-spacer></v-spacer>
                <v-btn 
                  color="success" 
                  prepend-icon="mdi-microsoft-excel" 
                  class="mb-2"
                  @click="downloadReport"
                >
                  Xuất Excel
                </v-btn>
              </div>
            </v-card>

            <!-- Biểu đồ thống kê -->
            <v-card class="mb-4 pa-4">
              <v-card-title class="px-0 pt-0 text-subtitle-1">Doanh thu theo thời gian (Tháng {{ reportMonth }}/{{ reportYear }})</v-card-title>
              <div style="height: 300px; position: relative">
                <Line v-if="chartData.labels && chartData.labels.length > 0" :data="chartData" :options="chartOptions" />
                <!-- Hiển thị khi không có dữ liệu hoặc đang tải -->
                <div v-else class="text-center text-grey d-flex flex-column justify-center align-center" style="height: 100%">
                  <v-icon size="large" class="mb-2">mdi-chart-line</v-icon>
                  <span>{{ paymentStore.reportLoading ? 'Đang tải dữ liệu biểu đồ...' : 'Không có dữ liệu doanh thu cho tháng này' }}</span>
                </div>
              </div>
            </v-card>

            <!-- Bảng lịch sử thanh toán -->
            <v-card flat border>
              <v-card-title class="text-subtitle-1 font-weight-medium bg-light-blue-lighten-5 py-2 px-4">
                <span>Lịch sử thanh toán</span>
              </v-card-title>
              <v-card-text class="pa-0">
                <v-table density="comfortable" hover>
                  <thead>
                    <tr>
                      <th class="text-left">Mã thanh toán</th>
                      <th class="text-left">Mã đơn hàng</th>
                      <th class="text-left">Thời gian</th>
                      <th class="text-left">Phương thức</th>
                      <th class="text-right">Số tiền</th>
                      <th class="text-right">Tiền thừa</th>
                      <th class="text-center">Trạng thái</th>
                      <th class="text-center">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="payment in paymentStore.payments" :key="payment.id">
                      <td>
                        <div class="font-weight-medium">#{{ payment.id }}</div>
                      </td>
                      <td>
                        <div class="font-weight-medium">#{{ payment.orderId }}</div>
                      </td>
                      <td>
                        <div>{{ formatTime(payment.paymentTime) }}</div>
                      </td>
                      <td>
                        <v-chip size="small" :color="getPaymentMethodColor(payment.paymentMethod.name)" density="comfortable">
                          {{ payment.paymentMethod.name }}
                        </v-chip>
                      </td>
                      <td class="text-right font-weight-medium">{{ formatCurrency(payment.amountPaid) }}</td>
                      <td class="text-right font-weight-medium">{{ formatCurrency(payment.change) }}</td>
                      <td class="text-center">
                        <v-chip
                          :color="getPaymentStatusColor(payment.status)"
                          :text="getPaymentStatusText(payment.status)"
                          size="small"
                        ></v-chip>
                      </td>
                      <td class="text-center">
                        <div class="d-flex justify-center">
                          <v-btn icon variant="text" color="primary" size="small" @click="viewPaymentDetails(payment.id)">
                            <v-icon size="small">mdi-eye</v-icon>
                          </v-btn>
                          <v-btn icon variant="text" color="secondary" size="small" @click="printReceipt(payment.id)">
                            <v-icon size="small">mdi-receipt</v-icon>
                          </v-btn>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </v-table>

                <!-- Hiển thị khi không có dữ liệu -->
                <div v-if="paymentStore.payments.length === 0 && !paymentStore.loading" class="text-center py-8">
                  <v-icon icon="mdi-cash-remove" size="large" class="mb-2 text-grey"></v-icon>
                  <div class="text-h6 text-grey">Không có dữ liệu thanh toán</div>
                </div>

                <!-- Hiển thị khi đang tải -->
                <div v-if="paymentStore.loading" class="text-center py-8">
                  <v-progress-circular indeterminate color="primary"></v-progress-circular>
                  <div class="text-body-1 mt-3">Đang tải dữ liệu...</div>
                </div>
              </v-card-text>
              <div class="d-flex align-center justify-center py-2">
                <v-pagination
                  v-model="paymentPage"
                  :length="Math.ceil(paymentStore.pagination.total / paymentStore.pagination.size) || 1"
                  total-visible="7"
                  @update:modelValue="onPaymentPageChange"
                ></v-pagination>
              </div>
            </v-card>
          </v-window-item>

          <!-- Tab Phương thức thanh toán -->
          <v-window-item value="payment-methods">
            <div class="d-flex align-center my-4">
              <v-text-field
                v-model="searchMethod"
                label="Tìm kiếm phương thức"
                prepend-inner-icon="mdi-magnify"
                density="compact"
                hide-details
                class="mr-4"
                bg-color="background"
                variant="outlined"
                style="max-width: 300px;"
              ></v-text-field>
              <v-spacer></v-spacer>
              <v-btn color="primary" class="text-none" rounded="lg" prepend-icon="mdi-plus" @click="openPaymentMethodDialog">
                THÊM PHƯƠNG THỨC
              </v-btn>
            </div>

            <!-- Danh sách phương thức thanh toán -->
            <v-card flat>
              <v-table hover>
                <thead>
                  <tr>
                    <th class="text-left">ID</th>
                    <th class="text-left">Tên phương thức</th>
                    <th class="text-left">Mô tả</th>
                    <th class="text-center">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="method in paymentStore.paymentMethods" :key="method.id">
                    <td>{{ method.id }}</td>
                    <td class="font-weight-medium">{{ method.name }}</td>
                    <td>{{ method.description || 'Không có mô tả' }}</td>
                    <td class="text-center">
                      <div class="d-flex justify-center">
                        <v-btn icon variant="text" color="primary" size="small" @click="editPaymentMethod(method)">
                          <v-icon>mdi-pencil</v-icon>
                          <v-tooltip activator="parent" location="top">Chỉnh sửa</v-tooltip>
                        </v-btn>
                        <v-btn icon variant="text" color="error" size="small" @click="openDeleteMethodDialog(method)">
                          <v-icon>mdi-delete</v-icon>
                          <v-tooltip activator="parent" location="top">Xóa</v-tooltip>
                        </v-btn>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </v-table>
              
              <!-- Hiển thị khi không có dữ liệu -->
              <div v-if="paymentStore.paymentMethods.length === 0 && !paymentStore.loading" class="text-center py-8">
                <v-icon icon="mdi-credit-card-off" size="large" class="mb-2 text-grey"></v-icon>
                <div class="text-h6 text-grey">Không có phương thức thanh toán nào</div>
                <v-btn color="primary" class="mt-4" prepend-icon="mdi-plus" @click="openPaymentMethodDialog">
                  Thêm phương thức thanh toán
                </v-btn>
              </div>

              <!-- Hiển thị khi đang tải -->
              <div v-if="paymentStore.loading" class="text-center py-8">
                <v-progress-circular indeterminate color="primary"></v-progress-circular>
                <div class="text-body-1 mt-3">Đang tải dữ liệu...</div>
              </div>
            </v-card>
          </v-window-item>
        </v-window>
      </v-card>

      <!-- Dialog thêm/sửa phương thức thanh toán -->
      <v-dialog v-model="paymentMethodDialog" width="500" persistent>
        <v-card>
          <v-card-title class="text-h6">
            {{ isEditingMethod ? 'Chỉnh sửa phương thức thanh toán' : 'Thêm phương thức thanh toán mới' }}
          </v-card-title>
          
          <v-card-text>
            <v-form ref="methodForm" @submit.prevent="submitPaymentMethodForm">
              <v-text-field
                v-model="paymentMethodForm.name"
                label="Tên phương thức"
                required
                variant="outlined"
                density="comfortable"
                class="mb-3"
                :rules="[v => !!v || 'Tên phương thức là bắt buộc']"
              ></v-text-field>
              
              <v-textarea
                v-model="paymentMethodForm.description"
                label="Mô tả"
                variant="outlined"
                density="comfortable"
                class="mb-3"
                rows="3"
                auto-grow
              ></v-textarea>
              
              <v-switch
                v-model="paymentMethodForm.isActive"
                label="Kích hoạt phương thức thanh toán"
                color="primary"
                hide-details
                class="mb-3"
              ></v-switch>
            </v-form>
          </v-card-text>
          
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="error" text @click="paymentMethodDialog = false">
              Đóng
            </v-btn>
            <v-btn 
              color="primary" 
              variant="text" 
              @click="submitPaymentMethodForm"
              :loading="paymentStore.loading"
            >
              {{ isEditingMethod ? 'Cập nhật' : 'Thêm mới' }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- Dialog xác nhận xóa -->
      <v-dialog v-model="deleteMethodDialog" width="400" persistent>
        <v-card>
          <v-card-title class="text-h6">
            Xác nhận xóa phương thức thanh toán
          </v-card-title>
          
          <v-card-text>
            Bạn có chắc chắn muốn xóa phương thức thanh toán "{{ selectedMethodToDelete?.name }}" không?
          </v-card-text>
          
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="deleteMethodDialog = false">
              Đóng
            </v-btn>
            <v-btn 
              color="error" 
              variant="text" 
              @click="confirmDeleteMethod"
              :loading="paymentStore.loading"
            >
              Xóa
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- Dialog chi tiết thanh toán -->
      <v-dialog v-model="paymentDetailsDialog" width="700">
        <v-card>
          <v-toolbar color="primary" density="compact">
            <v-toolbar-title>Chi tiết thanh toán #{{ currentPayment?.id }}</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon="mdi-close" @click="paymentDetailsDialog = false"></v-btn>
          </v-toolbar>

          <v-card-text v-if="currentPayment" class="pa-4">
            <v-overlay v-model="paymentStore.paymentLoading" contained class="align-center justify-center">
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
                      <span class="font-weight-medium">{{ currentPayment.id }}</span>
                    </div>
                    <div class="d-flex align-center mb-3">
                      <v-icon class="me-2" size="small">mdi-cash-register</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Phương thức thanh toán:</span>
                      <span class="font-weight-medium">{{ currentPayment.paymentMethod.name }}</span>
                    </div>
                    <div class="d-flex align-center mb-3" v-if="currentPayment.paymentMethod.description">
                      <v-icon class="me-2" size="small">mdi-text-box</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Mô tả phương thức:</span>
                      <span class="font-weight-medium">{{ currentPayment.paymentMethod.description }}</span>
                    </div>
                    <div class="d-flex align-center mb-3">
                      <v-icon class="me-2" size="small">mdi-calendar-clock</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Thời gian thanh toán:</span>
                      <span class="font-weight-medium">{{ formatTime(currentPayment.paymentTime) }}</span>
                    </div>
                    <div class="d-flex align-center mb-3">
                      <v-icon class="me-2" size="small">mdi-check-circle</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Trạng thái thanh toán:</span>
                      <v-chip
                        :color="getPaymentStatusColor(currentPayment.status)"
                        :text="getPaymentStatusText(currentPayment.status)"
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
                      <span class="font-weight-bold">{{ formatCurrency(currentPayment.amountPaid) }}</span>
                    </div>
                    <div class="d-flex align-center mb-3">
                      <v-icon class="me-2" size="small">mdi-cash-refund</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Tiền thừa:</span>
                      <span class="font-weight-medium">{{ formatCurrency(currentPayment.change) }}</span>
                    </div>
                    <div class="border-top pt-3 mt-3">
                      <span class="text-body-2 text-grey-darken-1">Tổng thanh toán thực tế:</span>
                      <div class="font-weight-bold text-h6 text-primary mt-1">
                        {{ formatCurrency(currentPayment.amountPaid - (currentPayment.change || 0)) }}
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
                      <span class="font-weight-medium">#{{ currentPayment.order.orderId }}</span>
                      <v-btn 
                        variant="text" 
                        size="small" 
                        color="primary" 
                        class="ms-2" 
                        @click="viewOrderDetails(currentPayment.order.orderId)"
                      >
                        Xem đơn hàng
                      </v-btn>
                    </div>
                    <div class="d-flex align-center mb-3">
                      <v-icon class="me-2" size="small">mdi-cash-multiple</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Tổng tiền đơn hàng:</span>
                      <span class="font-weight-medium">{{ formatCurrency(currentPayment.order.totalAmount) }}</span>
                    </div>
                    <div class="d-flex align-center mb-3">
                      <v-icon class="me-2" size="small">mdi-cash</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Thành tiền:</span>
                      <span class="font-weight-medium">{{ formatCurrency(currentPayment.order.finalAmount) }}</span>
                    </div>
                    <div class="d-flex align-center mb-3">
                      <v-icon class="me-2" size="small">mdi-flag-variant</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Trạng thái đơn hàng:</span>
                      <v-chip
                        :color="getOrderStatusColor(currentPayment.order.orderStatus)"
                        size="small"
                      >
                        {{ getOrderStatusText(currentPayment.order.orderStatus) }}
                      </v-chip>
                    </div>
                    <div class="d-flex align-center mb-3" v-if="currentPayment.order.note">
                      <v-icon class="me-2" size="small">mdi-note-text</v-icon>
                      <span class="text-body-2 text-grey-darken-1 me-1">Ghi chú:</span>
                      <span class="font-weight-medium">{{ currentPayment.order.note }}</span>
                    </div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>

            <div class="d-flex justify-center mt-4">
              <v-btn color="secondary" prepend-icon="mdi-printer" class="me-2" @click="printReceipt(currentPayment.id)">
                In biên lai
              </v-btn>
              <v-btn color="primary" variant="tonal" @click="paymentDetailsDialog = false">
                Đóng
              </v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { usePaymentStore } from '@/stores/payment'
import { useRouter } from 'vue-router'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'

// Đăng ký các thành phần cần thiết của Chart.js
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
)

const paymentStore = usePaymentStore()
const router = useRouter()
const activeTab = ref('payments')
const searchPayment = ref('')
const searchMethod = ref('')
const paymentPage = ref(1)
const paymentDetailsDialog = ref(false)
const currentPayment = ref(null)

// Biến báo cáo
const reportMonth = ref(new Date().getMonth() + 1)
const reportYear = ref(new Date().getFullYear())

// Danh sách tháng và năm
const monthOptions = Array.from({ length: 12 }, (_, i) => ({ 
  title: `Tháng ${i + 1}`, 
  value: i + 1 
}))

const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear()
  return Array.from({ length: 5 }, (_, i) => ({
    title: `${currentYear - i}`,
    value: currentYear - i
  }))
})

// Dialog và form phương thức thanh toán
const paymentMethodDialog = ref(false)
const deleteMethodDialog = ref(false)
const isEditingMethod = ref(false)
const selectedMethodToDelete = ref(null)
const paymentMethodForm = ref({
  name: '',
  description: '',
  isActive: true
})
const originalMethod = ref(null)

// Lấy màu sắc cho trạng thái thanh toán
const getPaymentStatusColor = (status) => {
  switch (status) {
    case 'COMPLETED': return 'success'
    case 'PAID': return 'success'
    case 'PENDING': return 'warning'
    case 'PROCESSING': return 'warning'
    case 'FAILED': return 'error'
    case 'CANCELLED': return 'error'
    default: return 'grey'
  }
}

// Lấy màu sắc cho phương thức thanh toán
const getPaymentMethodColor = (method) => {
  switch (method) {
    case 'CASH': return 'green-lighten-4'
    case 'VISA': return 'blue-lighten-4'
    case 'BANKCARD': return 'pink-lighten-4'
    case 'CREADIT_CARD': return 'indigo-lighten-4'
    case 'E-WALLET': return 'orange-lighten-4'
    default: return 'grey-lighten-3'
  }
}

// Lấy text trạng thái thanh toán
const getPaymentStatusText = (status) => {
  switch (status) {
    case 'PAID': return 'Đã thanh toán'
    case 'COMPLETED': return 'Đã thanh toán'
    case 'PROCESSING': return 'Đang xử lý'
    case 'PENDING': return 'Đang xử lý'
    case 'CANCELLED': return 'Đã hủy'
    case 'FAILED': return 'Thất bại'
    default: return 'Không xác định'
  }
}

// Format tiền tệ
const formatCurrency = (value) => {
  if (!value) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

// Format thời gian
const formatTime = (time) => {
  if (!time) return '-'
  // Format lại nếu là dạng ISO 8601 hoặc timestamp
  try {
    const date = new Date(time);
    return new Intl.DateTimeFormat('vi-VN', { 
      year: 'numeric', 
      month: '2-digit', 
      day: '2-digit', 
      hour: '2-digit', 
      minute: '2-digit' 
    }).format(date);
  } catch (e) {
    return time; // Trả về giá trị gốc nếu không parse được
  }
}

// Xử lý khi thay đổi trang thanh toán
const onPaymentPageChange = (page) => {
  paymentStore.fetchPayments(page - 1)
}

// Xem chi tiết thanh toán
const viewPaymentDetails = async (paymentId) => {
  try {
    await paymentStore.fetchPaymentById(paymentId)
    currentPayment.value = paymentStore.currentPayment
    paymentDetailsDialog.value = true
  } catch (error) {
    console.error('Lỗi khi tải chi tiết thanh toán:', error)
  }
}

// In biên lai thanh toán
const printReceipt = (paymentId) => {
  console.log('In biên lai cho thanh toán:', paymentId)
  // Tạo và in biên lai
}

// Tạo báo cáo thanh toán
const generateReport = async () => {
  try {
    await paymentStore.fetchPaymentReport(reportYear.value, reportMonth.value)
  } catch (error) {
    console.error('Lỗi khi tạo báo cáo:', error)
  }
}

// Tải xuống báo cáo
const downloadReport = () => {
  // Thực hiện tải xuống báo cáo Excel nếu API hỗ trợ
  console.log('Tải xuống báo cáo Excel')
}

// Xem chi tiết đơn hàng
const viewOrderDetails = (orderId) => {
  router.push(`/orders?id=${orderId}`)
}

// Mở dialog thêm mới phương thức thanh toán
const openPaymentMethodDialog = () => {
  isEditingMethod.value = false
  paymentMethodForm.value = {
    name: '',
    description: '',
    isActive: true
  }
  paymentMethodDialog.value = true
}

// Mở dialog chỉnh sửa phương thức thanh toán
const editPaymentMethod = (method) => {
  isEditingMethod.value = true
  paymentMethodForm.value = {
    name: method.name,
    description: method.description || '',
    isActive: !!method.isActive
  }
  originalMethod.value = method
  paymentMethodDialog.value = true
}

// Mở dialog xóa phương thức thanh toán
const openDeleteMethodDialog = (method) => {
  selectedMethodToDelete.value = method
  deleteMethodDialog.value = true
}

// Xử lý form thêm/sửa phương thức thanh toán
const submitPaymentMethodForm = async () => {
  try {
    if (isEditingMethod.value) {
      // Cập nhật phương thức thanh toán
      await paymentStore.updatePaymentMethod(originalMethod.value.id, paymentMethodForm.value)
    } else {
      // Thêm mới phương thức thanh toán
      await paymentStore.createPaymentMethod(paymentMethodForm.value)
    }
    paymentMethodDialog.value = false
  } catch (error) {
    console.error('Lỗi khi lưu phương thức thanh toán:', error)
  }
}

// Xác nhận xóa phương thức thanh toán
const confirmDeleteMethod = async () => {
  try {
    await paymentStore.deletePaymentMethod(selectedMethodToDelete.value.id)
    deleteMethodDialog.value = false
  } catch (error) {
    console.error('Lỗi khi xóa phương thức thanh toán:', error)
  }
}

// Lấy màu sắc và text cho trạng thái đơn hàng
const getOrderStatusColor = (status) => {
  switch (status) {
    case 'PROCESSING': return 'info'
    case 'COMPLETED': return 'success'
    case 'CANCELED': return 'error'
    default: return 'grey'
  }
}

const getOrderStatusText = (status) => {
  switch (status) {
    case 'PROCESSING': return 'Đang xử lý'
    case 'COMPLETED': return 'Đã xuất hóa đơn'
    case 'CANCELED': return 'Đã hủy'
    default: return 'Không xác định'
  }
}

// ---- Dữ liệu và tùy chọn cho biểu đồ ----
const chartData = computed(() => {
  const labels = []
  const data = []

  if (paymentStore.paymentReport && paymentStore.paymentReport.paymentDetailResponses) {
    // Nhóm doanh thu theo ngày
    const dailyRevenue = {}
    paymentStore.paymentReport.paymentDetailResponses.forEach(payment => {
      if (payment.paymentTime && payment.amountPaid) {
        const date = new Date(payment.paymentTime)
        const day = date.getDate() // Lấy ngày trong tháng
        const revenue = payment.amountPaid - (payment.change || 0)
        
        if (dailyRevenue[day]) {
          dailyRevenue[day] += revenue
        } else {
          dailyRevenue[day] = revenue
        }
      }
    })

    // Lấy số ngày trong tháng được chọn
    const daysInMonth = new Date(reportYear.value, reportMonth.value, 0).getDate()
    
    // Tạo labels và data cho tất cả các ngày trong tháng
    for (let day = 1; day <= daysInMonth; day++) {
      labels.push(`Ngày ${day}`)
      data.push(dailyRevenue[day] || 0) // Nếu không có doanh thu thì là 0
    }
  }

  return {
    labels,
    datasets: [
      {
        label: 'Doanh thu (VND)',
        backgroundColor: 'rgba(54, 162, 235, 0.2)',
        borderColor: 'rgb(54, 162, 235)',
        borderWidth: 1,
        tension: 0.1,
        data: data
      }
    ]
  }
})

const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false // Ẩn chú thích mặc định
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          let label = context.dataset.label || '';
          if (label) {
            label += ': ';
          }
          if (context.parsed.y !== null) {
            label += formatCurrency(context.parsed.y);
          }
          return label;
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: function(value) {
          return formatCurrency(value);
        }
      }
    }
  }
})
// ----------------------------------------

// Khởi tạo
onMounted(async () => {
  // Tải danh sách phương thức thanh toán
  await paymentStore.fetchPaymentMethods()
  
  // Tải danh sách thanh toán và báo cáo nếu tab mặc định là 'payments'
  if (activeTab.value === 'payments') {
    await paymentStore.fetchPayments()
    await generateReport() // Tải báo cáo ban đầu
  }
})

// Theo dõi thay đổi tab
watch(activeTab, async (newTab) => {
  if (newTab === 'payments' && paymentStore.payments.length === 0) {
    await paymentStore.fetchPayments()
    await generateReport() // Tải báo cáo khi chuyển tab
  } else if (newTab === 'payment-methods' && paymentStore.paymentMethods.length === 0) {
    await paymentStore.fetchPaymentMethods()
  }
})
</script>

<style scoped>
.v-data-table {
  border-radius: 8px;
  overflow: hidden;
}
</style>