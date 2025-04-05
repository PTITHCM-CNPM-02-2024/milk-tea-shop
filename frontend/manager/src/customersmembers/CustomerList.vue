<template>
  <div class="p-6 ml-56">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-800">Quản lý khách hàng</h1>
      <button class="bg-primary text-white px-4 py-2 rounded-lg flex items-center gap-2">
        <PlusIcon class="w-5 h-5" />
        Thêm khách hàng mới
      </button>
    </div>

    <!-- Search and filter section -->
    <div class="bg-white rounded-lg shadow p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="md:col-span-2">
          <label class="block text-sm font-medium text-gray-700 mb-1">Tìm kiếm</label>
          <div class="relative">
            <input
              type="text"
              v-model="filters.search"
              placeholder="Tìm theo tên, số điện thoại, email..."
              class="w-full border border-gray-300 rounded-md pl-10 pr-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
            />
            <SearchIcon class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Hạng thành viên</label>
          <select
            v-model="filters.membershipLevel"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="">Tất cả hạng</option>
            <option value="bronze">Đồng</option>
            <option value="silver">Bạc</option>
            <option value="gold">Vàng</option>
            <option value="platinum">Bạch kim</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Sắp xếp theo</label>
          <select
            v-model="filters.sortBy"
            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="recent">Mới nhất</option>
            <option value="name">Tên A-Z</option>
            <option value="orders">Số đơn hàng</option>
            <option value="points">Điểm thưởng</option>
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

    <!-- Customers table -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
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
                Liên hệ
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Ngày đăng ký
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Đơn hàng
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Điểm thưởng
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Hạng thành viên
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
            <tr v-for="customer in customers" :key="customer.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="h-10 w-10 flex-shrink-0">
                    <img
                      v-if="customer.avatar"
                      class="h-10 w-10 rounded-full object-cover"
                      :src="customer.avatar"
                      alt=""
                    />
                    <div
                      v-else
                      class="h-10 w-10 rounded-full bg-primary flex items-center justify-center text-white font-medium"
                    >
                      {{ getInitials(customer.name) }}
                    </div>
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">{{ customer.name }}</div>
                    <div v-if="customer.birthday" class="text-sm text-gray-500">
                      {{ formatDate(customer.birthday) }}
                    </div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ customer.phone }}</div>
                <div class="text-sm text-gray-500">{{ customer.email }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ formatDate(customer.registeredAt) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ customer.totalOrders }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ customer.rewardPoints }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getMembershipClass(customer.membershipLevel)">
                  {{ getMembershipText(customer.membershipLevel) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex justify-end gap-2">
                  <button
                    @click="viewCustomer(customer.id)"
                    class="text-primary hover:text-primary-dark"
                  >
                    <EyeIcon class="w-5 h-5" />
                  </button>
                  <button
                    @click="editCustomer(customer.id)"
                    class="text-gray-600 hover:text-gray-900"
                  >
                    <EditIcon class="w-5 h-5" />
                  </button>
                  <button
                    @click="viewOrderHistory(customer.id)"
                    class="text-blue-600 hover:text-blue-900"
                  >
                    <ClipboardListIcon class="w-5 h-5" />
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
              <span class="font-medium">{{ totalCustomers }}</span> khách hàng
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  PlusIcon,
  SearchIcon,
  EyeIcon,
  EditIcon,
  ClipboardListIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
} from 'lucide-vue-next'

const router = useRouter()
const customers = ref([])
const totalCustomers = ref(0)

const filters = ref({
  search: '',
  membershipLevel: '',
  sortBy: 'recent',
})

onMounted(() => {
  fetchCustomers()
})

const fetchCustomers = () => {
  // Mô phỏng dữ liệu khách hàng
  customers.value = [
    {
      id: 1,
      name: 'Nguyễn Văn A',
      phone: '0901234567',
      email: 'nguyenvana@example.com',
      birthday: new Date('1990-05-15'),
      registeredAt: new Date('2022-01-10'),
      totalOrders: 25,
      rewardPoints: 250,
      membershipLevel: 'gold',
      avatar: null,
    },
    {
      id: 2,
      name: 'Trần Thị B',
      phone: '0912345678',
      email: 'tranthib@example.com',
      birthday: new Date('1995-08-20'),
      registeredAt: new Date('2022-03-15'),
      totalOrders: 12,
      rewardPoints: 120,
      membershipLevel: 'silver',
      avatar: null,
    },
    {
      id: 3,
      name: 'Lê Văn C',
      phone: '0923456789',
      email: 'levanc@example.com',
      birthday: new Date('1988-11-05'),
      registeredAt: new Date('2022-02-20'),
      totalOrders: 8,
      rewardPoints: 80,
      membershipLevel: 'bronze',
      avatar: null,
    },
    {
      id: 4,
      name: 'Phạm Thị D',
      phone: '0934567890',
      email: 'phamthid@example.com',
      birthday: new Date('1992-04-25'),
      registeredAt: new Date('2021-12-05'),
      totalOrders: 35,
      rewardPoints: 450,
      membershipLevel: 'platinum',
      avatar: null,
    },
    {
      id: 5,
      name: 'Hoàng Văn E',
      phone: '0945678901',
      email: 'hoangvane@example.com',
      birthday: new Date('1985-07-30'),
      registeredAt: new Date('2022-04-10'),
      totalOrders: 15,
      rewardPoints: 150,
      membershipLevel: 'silver',
      avatar: null,
    },
  ]

  totalCustomers.value = 68 // Tổng số khách hàng
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN')
}

const getInitials = (name) => {
  return name
    .split(' ')
    .map((word) => word[0])
    .join('')
    .substring(0, 2)
    .toUpperCase()
}

const getMembershipClass = (level) => {
  const classes = {
    bronze:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800',
    silver:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800',
    gold: 'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-300 text-yellow-800',
    platinum:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800',
  }
  return classes[level] || ''
}

const getMembershipText = (level) => {
  const levelText = {
    bronze: 'Đồng',
    silver: 'Bạc',
    gold: 'Vàng',
    platinum: 'Bạch kim',
  }
  return levelText[level] || level
}

const resetFilters = () => {
  filters.value = {
    search: '',
    membershipLevel: '',
    sortBy: 'recent',
  }
}

const applyFilters = () => {
  // Thực hiện lọc khách hàng
  console.log('Applying filters:', filters.value)
  fetchCustomers() // Gọi API với các bộ lọc
}

const viewCustomer = (customerId) => {
  router.push(`/customers/${customerId}`)
}

const editCustomer = (customerId) => {
  router.push(`/customers/${customerId}/edit`)
}

const viewOrderHistory = (customerId) => {
  router.push(`/customers/${customerId}/orders`)
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
