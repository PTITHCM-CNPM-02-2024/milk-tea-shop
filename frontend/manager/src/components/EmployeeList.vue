<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lý nhân viên</h1>

    <!-- Thanh tìm kiếm -->
    <div class="flex mb-4">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Tìm kiếm nhân viên..."
        class="border p-2 rounded w-full"
      />
      <button @click="openForm(null)" class="ml-2 bg-blue-500 text-white px-4 py-2 rounded">
        + Thêm mới
      </button>
    </div>

    <!-- Bảng danh sách nhân viên -->
    <table class="w-full border-collapse border">
      <thead>
        <tr class="bg-gray-200">
          <th class="border p-2">#</th>
          <th class="border p-2">Họ và tên</th>
          <th class="border p-2">Ngày sinh</th>
          <th class="border p-2">Căn cước công dân</th>
          <th class="border p-2">Số điện thoại</th>
          <th class="border p-2">Địa chỉ</th>
          <th class="border p-2">Vai trò</th>
          <th class="border p-2">Trạng thái</th>
          <th class="border p-2">Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(employee, index) in filteredEmployees" :key="employee.id">
          <td class="border p-2 text-center">{{ index + 1 }}</td>
          <td class="border p-2 text-center">{{ employee.name }}</td>
          <td class="border p-2 text-center">{{ employee.dob }}</td>
          <td class="border p-2 text-center">{{ employee.cccd }}</td>
          <td class="border p-2 text-center">{{ employee.phone }}</td>
          <td class="border p-2 text-center">{{ employee.address }}</td>
          <td class="border p-2 text-center">{{ employee.role }}</td>

          <!-- <span
            :class="statusClass(employee.status)"
            class="flex justify-center items-center h-full w-full pt-3"
            >{{ employee.status }}</span
          > -->
          <td class="border p-2 text-center align-middle" :class="statusClass(employee.status)">
            {{ employee.status }}
          </td>

          <td class="border p-2 text-center">
            <button
              @click="openForm(employee)"
              class="bg-yellow-500 text-white px-2 py-1 rounded mr-2"
            >
              Sửa
            </button>
            <button
              @click="deleteEmployee(employee.id)"
              class="bg-red-500 text-white px-2 py-1 rounded"
            >
              Xóa
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Form thêm/sửa nhân viên -->
    <EmployeeForm
      v-if="showForm"
      :employee="selectedEmployee"
      @close="showForm = false"
      @save="saveEmployee"
    />
  </div>
</template>

<script>
import EmployeeForm from './EmployeeForm.vue'

export default {
  components: { EmployeeForm },
  data() {
    return {
      searchQuery: '',
      employees: [
        { id: 1, name: 'Nguyễn Văn A', role: 'Quản lý', status: 'Hoạt động' },
        { id: 2, name: 'Trần Thị B', role: 'Nhân viên', status: 'Đã nghỉ' },
      ],
      showForm: false,
      selectedEmployee: null,
    }
  },
  computed: {
    filteredEmployees() {
      return this.employees.filter((emp) =>
        emp.name.toLowerCase().includes(this.searchQuery.toLowerCase()),
      )
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
        employee.id = this.employees.length + 1
        this.employees.push(employee)
      }
    },
    openForm(employee) {
      this.selectedEmployee = employee
      this.showForm = true
    },
    deleteEmployee(id) {
      this.employees = this.employees.filter((emp) => emp.id !== id)
    },
    statusClass(status) {
      return status === 'Hoạt động' ? 'text-green-500' : 'text-red-500'
    },
  },
}
</script>
