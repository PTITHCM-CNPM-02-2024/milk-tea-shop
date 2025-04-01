<template>
  <div class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white p-6 rounded-lg w-1/3">
      <h2 class="text-xl font-bold mb-4">
        {{ isEditing ? 'Chỉnh sửa nhân viên' : 'Thêm nhân viên mới' }}
      </h2>

      <!-- Form nhập thông tin -->
      <div class="mb-4">
        <label class="block font-semibold">Họ và tên</label>
        <input type="text" v-model="employeeData.name" class="border p-2 w-full rounded" />
      </div>
      <div class="mb-4">
        <label class="block font-semibold">Ngày sinh</label>
        <input type="date" v-model="employeeData.dob" class="border p-2 w-full rounded" />
      </div>
      <div class="mb-4">
        <label class="block font-semibold">Căn cước công dân</label>
        <input type="text" v-model="employeeData.cccd" class="border p-2 w-full rounded" />
      </div>
      <div class="mb-4">
        <label class="block font-semibold">Số điện thoại</label>
        <input type="text" v-model="employeeData.phone" class="border p-2 w-full rounded" />
      </div>

      <div class="mb-4">
        <label class="block font-semibold">Địa chỉ</label>
        <input type="text" v-model="employeeData.address" class="border p-2 w-full rounded" />
      </div>

      <!-- Chỉ hiển thị username & password khi thêm mới -->
      <div v-if="!isEditing">
        <div class="mb-4">
          <label class="block font-semibold">Username</label>
          <input type="text" v-model="employeeData.username" class="border p-2 w-full rounded" />
        </div>

        <div class="mb-4">
          <label class="block font-semibold">Password</label>
          <input
            type="password"
            v-model="employeeData.password"
            class="border p-2 w-full rounded"
          />
        </div>
      </div>

      <div class="mb-4">
        <label class="block font-semibold">Vai trò</label>
        <select v-model="employeeData.role" class="border p-2 w-full rounded">
          <option value="Quản lý">Quản lý</option>
          <option value="Nhân viên">Nhân viên</option>
        </select>
      </div>

      <div class="mb-4" v-if="isEditing">
        <label class="block font-semibold">Trạng thái</label>
        <select v-model="employeeData.status" class="border p-2 w-full rounded">
          <option value="Hoạt động">Hoạt động</option>
          <option value="Đã nghỉ">Đã nghỉ</option>
        </select>
      </div>

      <!-- Nút lưu & hủy -->
      <div class="flex justify-end">
        <button @click="$emit('close')" class="bg-gray-400 text-white px-4 py-2 rounded mr-2">
          Hủy
        </button>
        <button @click="saveEmployee" class="bg-blue-500 text-white px-4 py-2 rounded">
          {{ isEditing ? 'Cập nhật' : 'Thêm mới' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['employee'],
  data() {
    return {
      employeeData: this.employee
        ? { ...this.employee }
        : {
            name: '',
            username: '',
            password: '',
            dob: '',
            cccd: '',
            phone: '',
            address: '',
            role: 'Nhân viên',
            status: 'Hoạt động',
          },
    }
  },
  computed: {
    isEditing() {
      return !!this.employee
    },
  },
  methods: {
    saveEmployee() {
      if (!this.employeeData.name || (!this.isEditing && !this.employeeData.username)) {
        alert('Vui lòng nhập đầy đủ thông tin!')
        return
      }

      this.$emit('save', this.employeeData)
      this.$emit('close')
    },
  },
}
</script>
