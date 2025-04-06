<template>
  <div class="p-6 ml-56 *:max-w-7xl mx-auto">
    <h1 class="text-2xl font-bold mb-6 flex items-center">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
        class="mr-2 text-orange-500"
      >
        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
        <circle cx="9" cy="7" r="4"></circle>
        <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
      </svg>
      Quản lý nhân viên
    </h1>

    <!-- Thanh tìm kiếm và thêm mới -->
    <div class="flex flex-col sm:flex-row gap-2 mb-6">
      <div class="relative flex-1">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Tìm kiếm nhân viên..."
          class="border p-2 rounded w-full pl-10 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
        />
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
        >
          <circle cx="11" cy="11" r="8"></circle>
          <path d="m21 21-4.3-4.3"></path>
        </svg>
      </div>
      <button
        @click="openForm(null)"
        class="bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded transition-colors flex items-center justify-center sm:justify-start"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          class="mr-1"
        >
          <path d="M12 5v14"></path>
          <path d="M5 12h14"></path>
        </svg>
        Thêm mới
      </button>
    </div>

    <!-- Bộ lọc -->
    <div class="flex flex-wrap gap-2 mb-4">
      <select
        v-model="roleFilter"
        class="border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
      >
        <option value="">Tất cả vai trò</option>
        <option value="Quản lý">Quản lý</option>
        <option value="Nhân viên">Nhân viên</option>
      </select>

      <select
        v-model="statusFilter"
        class="border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
      >
        <option value="">Tất cả trạng thái</option>
        <option value="Hoạt động">Hoạt động</option>
        <option value="Đã nghỉ">Đã nghỉ</option>
      </select>
    </div>

    <!-- Bảng danh sách nhân viên -->
    <div class="overflow-x-auto bg-white rounded-lg shadow">
      <table class="w-full border-collapse">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <th class="border-b p-3 text-left">#</th>
            <th class="border-b p-3 text-left">Họ và tên</th>
            <th class="border-b p-3 text-left">Ngày sinh</th>
            <th class="border-b p-3 text-left">CCCD</th>
            <th class="border-b p-3 text-left">Số điện thoại</th>
            <th class="border-b p-3 text-left">Địa chỉ</th>
            <th class="border-b p-3 text-left">Vai trò</th>
            <th class="border-b p-3 text-left">Trạng thái</th>
            <th class="border-b p-3 text-center">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(employee, index) in filteredEmployees"
            :key="employee.id"
            class="hover:bg-gray-50"
          >
            <td class="border-b p-3">{{ index + 1 }}</td>
            <td class="border-b p-3 font-medium">{{ employee.name }}</td>
            <td class="border-b p-3">{{ formatDate(employee.dob) }}</td>
            <td class="border-b p-3">{{ employee.cccd }}</td>
            <td class="border-b p-3">{{ employee.phone }}</td>
            <td class="border-b p-3 max-w-[200px] truncate">{{ employee.address }}</td>
            <td class="border-b p-3">
              <span class="px-2 py-1 rounded-full text-xs" :class="roleClass(employee.role)">
                {{ employee.role }}
              </span>
            </td>
            <td class="border-b p-3">
              <span class="px-2 py-1 rounded-full text-xs" :class="statusClass(employee.status)">
                {{ employee.status }}
              </span>
            </td>
            <td class="border-b p-3">
              <div class="flex justify-center space-x-1">
                <button
                  @click="openForm(employee)"
                  class="p-1 rounded text-yellow-600 hover:bg-yellow-100 transition-colors"
                  title="Chỉnh sửa"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"></path>
                    <path d="m15 5 4 4"></path>
                  </svg>
                </button>
                <button
                  @click="confirmDelete(employee)"
                  class="p-1 rounded text-red-600 hover:bg-red-100 transition-colors"
                  title="Xóa"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path d="M3 6h18"></path>
                    <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                    <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                    <line x1="10" x2="10" y1="11" y2="17"></line>
                    <line x1="14" x2="14" y1="11" y2="17"></line>
                  </svg>
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredEmployees.length === 0">
            <td colspan="9" class="p-4 text-center text-gray-500">Không tìm thấy nhân viên nào</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <div class="mt-4 flex justify-between items-center">
      <div class="text-sm text-gray-600">
        Hiển thị {{ filteredEmployees.length }} / {{ employees.length }} nhân viên
      </div>
      <div class="flex space-x-1">
        <button
          class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="currentPage === 1"
          @click="currentPage--"
        >
          Trước
        </button>
        <button
          class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="currentPage >= totalPages"
          @click="currentPage++"
        >
          Sau
        </button>
      </div>
    </div>

    <!-- Form thêm/sửa nhân viên -->
    <EmployeeFormNew
      v-if="showForm"
      :employee="selectedEmployee"
      @close="showForm = false"
      @save="saveEmployee"
    />

    <!-- Modal xác nhận xóa -->
    <div
      v-if="showDeleteConfirm"
      class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
    >
      <div class="bg-white p-6 rounded-lg w-full max-w-md mx-auto">
        <h3 class="text-lg font-bold mb-4 flex items-center text-red-600">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="mr-2"
          >
            <path
              d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"
            ></path>
            <line x1="12" x2="12" y1="9" y2="13"></line>
            <line x1="12" x2="12.01" y1="17" y2="17"></line>
          </svg>
          Xác nhận xóa
        </h3>
        <p class="mb-6">
          Bạn có chắc chắn muốn xóa nhân viên <strong>{{ employeeToDelete?.name }}</strong
          >?
        </p>
        <div class="flex justify-end">
          <button
            @click="showDeleteConfirm = false"
            class="bg-gray-400 hover:bg-gray-500 text-white px-4 py-2 rounded mr-2 transition-colors"
          >
            Hủy
          </button>
          <button
            @click="deleteEmployee(employeeToDelete.id)"
            class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded transition-colors"
          >
            Xóa
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EmployeeFormNew from './employees/EmployeeFormNew.vue'

export default {
  components: { EmployeeFormNew },
  data() {
    return {
      searchQuery: '',
      roleFilter: '',
      statusFilter: '',
      currentPage: 1,
      itemsPerPage: 5,
      employees: [
        {
          id: 1,
          name: 'Nguyễn Văn A',
          dob: '1990-01-15',
          cccd: '012345678901',
          phone: '0901234567',
          address: 'Số 123 Đường ABC, Quận 1, TP.HCM',
          role: 'Quản lý',
          status: 'Hoạt động',
          username: 'nguyenvana',
        },
        {
          id: 2,
          name: 'Trần Thị B',
          dob: '1995-05-20',
          cccd: '098765432109',
          phone: '0909876543',
          address: 'Số 456 Đường XYZ, Quận 2, TP.HCM',
          role: 'Nhân viên',
          status: 'Đã nghỉ',
          username: 'tranthib',
        },
      ],
      showForm: false,
      selectedEmployee: null,
      showDeleteConfirm: false,
      employeeToDelete: null,
    }
  },
  computed: {
    filteredEmployees() {
      let result = this.employees

      // Lọc theo tìm kiếm
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        result = result.filter(
          (emp) =>
            emp.name.toLowerCase().includes(query) ||
            emp.phone.includes(query) ||
            emp.cccd.includes(query),
        )
      }

      // Lọc theo vai trò
      if (this.roleFilter) {
        result = result.filter((emp) => emp.role === this.roleFilter)
      }

      // Lọc theo trạng thái
      if (this.statusFilter) {
        result = result.filter((emp) => emp.status === this.statusFilter)
      }

      // Phân trang
      const startIndex = (this.currentPage - 1) * this.itemsPerPage
      const endIndex = startIndex + this.itemsPerPage

      return result.slice(startIndex, endIndex)
    },
    totalPages() {
      return Math.ceil(this.filteredEmployees.length / this.itemsPerPage)
    },
  },
  methods: {
    saveEmployee(employee) {
      if (employee.id) {
        // Chỉnh sửa nhân viên cũ
        const index = this.employees.findIndex((emp) => emp.id === employee.id)
        if (index !== -1) this.employees[index] = employee
      } else {
        // Thêm nhân viên mới
        employee.id =
          this.employees.length > 0 ? Math.max(...this.employees.map((e) => e.id)) + 1 : 1
        this.employees.push(employee)
      }
    },
    openForm(employee) {
      this.selectedEmployee = employee
      this.showForm = true
    },
    confirmDelete(employee) {
      this.employeeToDelete = employee
      this.showDeleteConfirm = true
    },
    deleteEmployee(id) {
      this.employees = this.employees.filter((emp) => emp.id !== id)
      this.showDeleteConfirm = false
    },
    statusClass(status) {
      return status === 'Hoạt động' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
    },
    roleClass(role) {
      return role === 'Quản lý' ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-700'
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('vi-VN')
    },
  },
}
</script>
