<template>
  <div class="p-6 ml-56">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-800">Quản lý thanh toán</h1>
    </div>

    <!-- Filter section -->
    <div class="bg-white rounded-lg shadow p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
          <select
            v-model="filters.status"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="">Tất cả trạng thái</option>
            <option value="paid">Đã thanh toán</option>
            <option value="unpaid">Chưa thanh toán</option>
            <option value="refunded">Đã hoàn tiền</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Phương thức</label>
          <select
            v-model="filters.method"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="">Tất cả phương thức</option>
            <option value="cash">Tiền mặt</option>
            <option value="card">Thẻ ngân hàng</option>
            <option value="momo">Ví MoMo</option>
            <option value="vnpay">VNPay</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Từ ngày</label>
          <input
            type="date"
            v-model="filters.startDate"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Đến ngày</label>
          <input
            type="date"
            v-model="filters.endDate"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>
      </div>
      <div class="flex justify-end mt-4 gap-2">
        <button
          @click="resetFilters"
          class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
        >
          Đặt lại
        </button>
        <button
          @click="applyFilters"
          class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark"
        >
          Áp dụng
        </button>
      </div>
    </div>

    <!-- Payment summary cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-green-100 mr-4">
            <CreditCardIcon class="h-6 w-6 text-green-600" />
          </div>
          <div>
            <p class="text-sm font-medium text-gray-600">Tổng doanh thu</p>
            <p class="text-2xl font-bold text-gray-900">
              {{ formatCurrency(summary.totalRevenue) }}
            </p>
          </div>
        </div>
        <div class="mt-4">
          <div class="flex items-center text-sm">
            <ArrowUpIcon class="h-4 w-4 text-green-500 mr-1" />
            <span class="text-green-500 font-medium">12.5%</span>
            <span class="text-gray-500 ml-1">so với tháng trước</span>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-blue-100 mr-4">
            <ReceiptIcon class="h-6 w-6 text-blue-600" />
          </div>
          <div>
            <p class="text-sm font-medium text-gray-600">Tổng số giao dịch</p>
            <p class="text-2xl font-bold text-gray-900">{{ summary.totalTransactions }}</p>
          </div>
        </div>
        <div class="mt-4">
          <div class="flex items-center text-sm">
            <ArrowUpIcon class="h-4 w-4 text-green-500 mr-1" />
            <span class="text-green-500 font-medium">8.2%</span>
            <span class="text-gray-500 ml-1">so với tháng trước</span>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-purple-100 mr-4">
            <TrendingUpIcon class="h-6 w-6 text-purple-600" />
          </div>
          <div>
            <p class="text-sm font-medium text-gray-600">Giá trị trung bình</p>
            <p class="text-2xl font-bold text-gray-900">
              {{ formatCurrency(summary.averageValue) }}
            </p>
          </div>
        </div>
        <div class="mt-4">
          <div class="flex items-center text-sm">
            <ArrowUpIcon class="h-4 w-4 text-green-500 mr-1" />
            <span class="text-green-500 font-medium">3.7%</span>
            <span class="text-gray-500 ml-1">so với tháng trước</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Payments table -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Mã giao dịch
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Mã đơn hàng
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Thời gian
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Phương thức
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Số tiền
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Trạng thái
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="payment in payments" :key="payment.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-primary">{{ payment.id }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">
                  <a :href="`/orders/${payment.orderId}`" class="text-primary hover:underline"
                    >#{{ payment.orderId }}</a
                  >
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ formatDate(payment.date) }}</div>
                <div class="text-sm text-gray-500">{{ formatTime(payment.date) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <component :is="getPaymentMethodIcon(payment.method)" class="h-5 w-5 mr-2" />
                  <span class="text-sm text-gray-900">{{
                    getPaymentMethodText(payment.method)
                  }}</span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">
                  {{ formatCurrency(payment.amount) }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getPaymentStatusClass(payment.status)">
                  {{ getPaymentStatusText(payment.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex justify-end gap-2">
                  <button
                    @click="viewPaymentDetails(payment.id)"
                    class="text-primary hover:text-primary-dark"
                  >
                    <EyeIcon class="w-5 h-5" />
                  </button>
                  <button
                    @click="printReceipt(payment.id)"
                    class="text-gray-600 hover:text-gray-900"
                  >
                    <PrinterIcon class="w-5 h-5" />
                  </button>
                  <button
                    v-if="payment.status === 'paid'"
                    @click="showRefundModal(payment)"
                    class="text-red-600 hover:text-red-900"
                  >
                    <RefreshCwIcon class="w-5 h-5" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div
        class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6"
      >
        <div class="flex-1 flex justify-between sm:hidden">
          <button
            class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            Trước
          </button>
          <button
            class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            Sau
          </button>
        </div>
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700">
              Hiển thị <span class="font-medium">1</span> đến
              <span class="font-medium">10</span> của
              <span class="font-medium">{{ totalPayments }}</span> giao dịch
            </p>
          </div>
          <div>
            <nav
              class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px"
              aria-label="Pagination"
            >
              <button
                class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              >
                <span class="sr-only">Previous</span>
                <ChevronLeftIcon class="h-5 w-5" />
              </button>
              <button
                class="bg-primary border-primary text-white relative inline-flex items-center px-4 py-2 border text-sm font-medium"
              >
                1
              </button>
              <button
                class="bg-white border-gray-300 text-gray-500 hover:bg-gray-50 relative inline-flex items-center px-4 py-2 border text-sm font-medium"
              >
                2
              </button>
              <button
                class="bg-white border-gray-300 text-gray-500 hover:bg-gray-50 relative inline-flex items-center px-4 py-2 border text-sm font-medium"
              >
                3
              </button>
              <button
                class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              >
                <span class="sr-only">Next</span>
                <ChevronRightIcon class="h-5 w-5" />
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>

    <!-- Refund Modal -->
    <div
      v-if="showRefund"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg shadow-lg p-6 w-full max-w-md">
        <h3 class="text-lg font-medium text-gray-900 mb-4">Xác nhận hoàn tiền</h3>
        <p class="text-sm text-gray-600 mb-4">
          Bạn có chắc chắn muốn hoàn tiền cho giao dịch
          <span class="font-medium">{{ selectedPayment.id }}</span> với số tiền
          <span class="font-medium">{{ formatCurrency(selectedPayment.amount) }}</span
          >?
        </p>
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">Lý do hoàn tiền</label>
          <textarea
            v-model="refundReason"
            rows="3"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          ></textarea>
        </div>
        <div class="flex justify-end gap-2">
          <button
            @click="showRefund = false"
            class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
          >
            Hủy
          </button>
          <button
            @click="processRefund"
            class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
          >
            Xác nhận hoàn tiền
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  CreditCardIcon,
  ReceiptIcon,
  TrendingUpIcon,
  ArrowUpIcon,
  EyeIcon,
  PrinterIcon,
  RefreshCwIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  DollarSignIcon,
  CreditCardIcon as CardIcon,
  SmartphoneIcon,
  GlobeIcon,
} from 'lucide-vue-next'

const router = useRouter()
const payments = ref([])
const totalPayments = ref(0)
const showRefund = ref(false)
const selectedPayment = ref({})
const refundReason = ref('')

const filters = ref({
  status: '',
  method: '',
  startDate: '',
  endDate: '',
})

const summary = ref({
  totalRevenue: 5250000,
  totalTransactions: 42,
  averageValue: 125000,
})

onMounted(() => {
  fetchPayments()
})

const fetchPayments = () => {
  // Mô phỏng dữ liệu thanh toán
  payments.value = [
    {
      id: 'PAY001',
      orderId: 'ORD001',
      date: new Date('2023-04-03T14:45:00'),
      method: 'cash',
      amount: 150000,
      status: 'paid',
    },
    {
      id: 'PAY002',
      orderId: 'ORD002',
      date: new Date('2023-04-03T15:50:00'),
      method: 'card',
      amount: 85000,
      status: 'paid',
    },
    {
      id: 'PAY003',
      orderId: 'ORD004',
      date: new Date('2023-04-03T17:15:00'),
      method: 'momo',
      amount: 200000,
      status: 'paid',
    },
    {
      id: 'PAY004',
      orderId: 'ORD005',
      date: new Date('2023-04-03T18:10:00'),
      method: 'cash',
      amount: 95000,
      status: 'refunded',
    },
    {
      id: 'PAY005',
      orderId: 'ORD006',
      date: new Date('2023-04-03T19:25:00'),
      method: 'vnpay',
      amount: 175000,
      status: 'paid',
    },
  ]

  totalPayments.value = 42 // Tổng số giao dịch
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN')
}

const formatTime = (date) => {
  return new Date(date).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
}

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

const getPaymentStatusClass = (status) => {
  const classes = {
    unpaid:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800',
    paid: 'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800',
    refunded:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800',
    failed:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800',
  }
  return classes[status] || ''
}

const getPaymentStatusText = (status) => {
  const statusText = {
    unpaid: 'Chưa thanh toán',
    paid: 'Đã thanh toán',
    refunded: 'Đã hoàn tiền',
    failed: 'Thất bại',
  }
  return statusText[status] || status
}

const getPaymentMethodText = (method) => {
  const methodText = {
    cash: 'Tiền mặt',
    card: 'Thẻ ngân hàng',
    momo: 'Ví MoMo',
    vnpay: 'VNPay',
  }
  return methodText[method] || method
}

const getPaymentMethodIcon = (method) => {
  const icons = {
    cash: DollarSignIcon,
    card: CardIcon,
    momo: SmartphoneIcon,
    vnpay: GlobeIcon,
  }
  return icons[method] || CreditCardIcon
}

const resetFilters = () => {
  filters.value = {
    status: '',
    method: '',
    startDate: '',
    endDate: '',
  }
}

const applyFilters = () => {
  // Thực hiện lọc giao dịch
  console.log('Applying filters:', filters.value)
  fetchPayments() // Gọi API với các bộ lọc
}

const viewPaymentDetails = (paymentId) => {
  // Chuyển đến trang chi tiết giao dịch
  console.log('View payment details:', paymentId)
}

const printReceipt = (paymentId) => {
  console.log('Printing receipt for payment:', paymentId)
  // Thực hiện in hóa đơn
}

const showRefundModal = (payment) => {
  selectedPayment.value = payment
  showRefund.value = true
  refundReason.value = ''
}

const processRefund = () => {
  if (!refundReason.value.trim()) {
    alert('Vui lòng nhập lý do hoàn tiền')
    return
  }

  console.log(
    'Processing refund for payment:',
    selectedPayment.value.id,
    'Reason:',
    refundReason.value,
  )
  // Thực hiện hoàn tiền
  showRefund.value = false

  // Cập nhật trạng thái giao dịch
  const index = payments.value.findIndex((p) => p.id === selectedPayment.value.id)
  if (index !== -1) {
    payments.value[index].status = 'refunded'
  }
}
</script>

<style scoped>
.bg-primary {
  background-color: #f97316;
}
.text-primary {
  color: #f97316;
}
.hover\:bg-primary-dark:hover {
  background-color: #f97316;
}
.hover\:text-primary-dark:hover {
  color: #f97316;
}
.border-primary {
  border-color: #f97316;
}
.focus\:ring-primary:focus {
  --tw-ring-color: ##f97316;
}
</style>
