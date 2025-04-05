<template>
  <div class="p-6 ml-56">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold mb-6 flex items-center">
        <CubeIcon class="w-6 h-6 mr-2 text-orange-500" />Quản lý đơn hàng
      </h1>
      <button class="bg-primary text-white px-4 py-2 rounded-lg flex items-center gap-2">
        <PlusIcon class="w-5 h-5" />
        Tạo đơn hàng mới
      </button>
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
            <option value="pending">Chờ xử lý</option>
            <option value="processing">Đang xử lý</option>
            <option value="completed">Hoàn thành</option>
            <option value="cancelled">Đã hủy</option>
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
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Nhân viên</label>
          <select
            v-model="filters.staff"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="">Tất cả nhân viên</option>
            <option v-for="staff in staffList" :key="staff.id" :value="staff.id">
              {{ staff.name }}
            </option>
          </select>
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

    <!-- Orders table -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Mã đơn
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Khách hàng
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
                Tổng tiền
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Trạng thái
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Thanh toán
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Nhân viên
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
            <tr v-for="order in orders" :key="order.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-primary">#{{ order.id }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ order.customer.name }}</div>
                <div class="text-sm text-gray-500">{{ order.customer.phone }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ formatDate(order.createdAt) }}</div>
                <div class="text-sm text-gray-500">{{ formatTime(order.createdAt) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">
                  {{ formatCurrency(order.total) }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(order.status)">
                  {{ getStatusText(order.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getPaymentStatusClass(order.paymentStatus)">
                  {{ getPaymentStatusText(order.paymentStatus) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ order.staff.name }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex justify-end gap-2">
                  <button @click="viewOrder(order.id)" class="text-primary hover:text-primary-dark">
                    <EyeIcon class="w-5 h-5" />
                  </button>
                  <button @click="printOrder(order.id)" class="text-gray-600 hover:text-gray-900">
                    <PrinterIcon class="w-5 h-5" />
                  </button>
                  <button
                    v-if="order.status === 'pending'"
                    @click="cancelOrder(order.id)"
                    class="text-red-600 hover:text-red-900"
                  >
                    <XCircleIcon class="w-5 h-5" />
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
              <span class="font-medium">{{ totalOrders }}</span> đơn hàng
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
              <span
                class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700"
              >
                ...
              </span>
              <button
                class="bg-white border-gray-300 text-gray-500 hover:bg-gray-50 relative inline-flex items-center px-4 py-2 border text-sm font-medium"
              >
                8
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  PlusIcon,
  EyeIcon,
  PrinterIcon,
  XCircleIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
} from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { CubeIcon } from '@heroicons/vue/24/outline'

const router = useRouter()
const orders = ref([])
const totalOrders = ref(0)
const staffList = ref([])

const filters = ref({
  status: '',
  startDate: '',
  endDate: '',
  staff: '',
})

onMounted(() => {
  fetchOrders()
  fetchStaffList()
})

const fetchOrders = () => {
  // Mô phỏng dữ liệu đơn hàng
  orders.value = [
    {
      id: 'ORD001',
      customer: { name: 'Nguyễn Văn A', phone: '0901234567' },
      createdAt: new Date('2023-04-03T14:30:00'),
      total: 150000,
      status: 'completed',
      paymentStatus: 'paid',
      staff: { name: 'Trần Thị B' },
    },
    {
      id: 'ORD002',
      customer: { name: 'Lê Thị C', phone: '0912345678' },
      createdAt: new Date('2023-04-03T15:45:00'),
      total: 85000,
      status: 'processing',
      paymentStatus: 'paid',
      staff: { name: 'Phạm Văn D' },
    },
    {
      id: 'ORD003',
      customer: { name: 'Hoàng Văn E', phone: '0923456789' },
      createdAt: new Date('2023-04-03T16:20:00'),
      total: 120000,
      status: 'pending',
      paymentStatus: 'unpaid',
      staff: { name: 'Trần Thị B' },
    },
    {
      id: 'ORD004',
      customer: { name: 'Trần Thị F', phone: '0934567890' },
      createdAt: new Date('2023-04-03T17:10:00'),
      total: 200000,
      status: 'completed',
      paymentStatus: 'paid',
      staff: { name: 'Nguyễn Văn G' },
    },
    {
      id: 'ORD005',
      customer: { name: 'Phạm Văn H', phone: '0945678901' },
      createdAt: new Date('2023-04-03T18:05:00'),
      total: 95000,
      status: 'cancelled',
      paymentStatus: 'refunded',
      staff: { name: 'Lê Thị I' },
    },
  ]

  totalOrders.value = 42 // Tổng số đơn hàng
}

const fetchStaffList = () => {
  // Mô phỏng dữ liệu nhân viên
  staffList.value = [
    { id: 1, name: 'Trần Thị B' },
    { id: 2, name: 'Phạm Văn D' },
    { id: 3, name: 'Nguyễn Văn G' },
    { id: 4, name: 'Lê Thị I' },
  ]
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

const getStatusClass = (status) => {
  const classes = {
    pending:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800',
    processing:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800',
    completed:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800',
    cancelled:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800',
  }
  return classes[status] || ''
}

const getStatusText = (status) => {
  const statusText = {
    pending: 'Chờ xử lý',
    processing: 'Đang xử lý',
    completed: 'Hoàn thành',
    cancelled: 'Đã hủy',
  }
  return statusText[status] || status
}

const getPaymentStatusClass = (status) => {
  const classes = {
    unpaid:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800',
    paid: 'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800',
    refunded:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800',
  }
  return classes[status] || ''
}

const getPaymentStatusText = (status) => {
  const statusText = {
    unpaid: 'Chưa thanh toán',
    paid: 'Đã thanh toán',
    refunded: 'Đã hoàn tiền',
  }
  return statusText[status] || status
}

const resetFilters = () => {
  filters.value = {
    status: '',
    startDate: '',
    endDate: '',
    staff: '',
  }
}

const applyFilters = () => {
  // Thực hiện lọc đơn hàng
  console.log('Applying filters:', filters.value)
  fetchOrders() // Gọi API với các bộ lọc
}

const viewOrder = (orderId) => {
  router.push(`/orders/${orderId}`)
}

const printOrder = (orderId) => {
  console.log('Printing order:', orderId)
  // Thực hiện in đơn hàng
}

const cancelOrder = (orderId) => {
  if (confirm('Bạn có chắc chắn muốn hủy đơn hàng này không?')) {
    console.log('Cancelling order:', orderId)
    // Thực hiện hủy đơn hàng
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
  --tw-ring-color: #f97316;
}
</style>
