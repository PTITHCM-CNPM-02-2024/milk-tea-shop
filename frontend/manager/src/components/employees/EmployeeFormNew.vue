<template>
  <div class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
    <div class="bg-white p-6 rounded-lg w-full max-w-md mx-auto">
      <h2 class="text-xl font-bold mb-4 flex items-center">
        <span v-if="isEditing" class="mr-2">
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
            class="text-yellow-500"
          >
            <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"></path>
            <path d="m15 5 4 4"></path>
          </svg>
        </span>
        <span v-else class="mr-2">
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
            class="text-blue-500"
          >
            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
        </span>
        {{ isEditing ? 'Chỉnh sửa nhân viên' : 'Thêm nhân viên mới' }}
      </h2>

      <!-- Form nhập thông tin -->
      <div class="grid grid-cols-1 gap-4">
        <div>
          <label class="block font-semibold text-sm mb-1"
            >Họ và tên <span class="text-red-500">*</span></label
          >
          <input
            type="text"
            v-model="employeeData.name"
            class="border p-2 w-full rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
            :class="{ 'border-red-500': errors.name }"
          />
          <p v-if="errors.name" class="text-red-500 text-xs mt-1">{{ errors.name }}</p>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block font-semibold text-sm mb-1">Ngày sinh</label>
            <input
              type="date"
              v-model="employeeData.dob"
              class="border p-2 w-full rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
            />
          </div>
          <div>
            <label class="block font-semibold text-sm mb-1">CCCD</label>
            <input
              type="text"
              v-model="employeeData.cccd"
              class="border p-2 w-full rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
              :class="{ 'border-red-500': errors.cccd }"
            />
            <p v-if="errors.cccd" class="text-red-500 text-xs mt-1">{{ errors.cccd }}</p>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block font-semibold text-sm mb-1">Số điện thoại</label>
            <input
              type="text"
              v-model="employeeData.phone"
              class="border p-2 w-full rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
              :class="{ 'border-red-500': errors.phone }"
            />
            <p v-if="errors.phone" class="text-red-500 text-xs mt-1">{{ errors.phone }}</p>
          </div>
          <div>
            <label class="block font-semibold text-sm mb-1">Vai trò</label>
            <select
              v-model="employeeData.role"
              class="border p-2 w-full rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
            >
              <option value="Quản lý">Quản lý</option>
              <option value="Nhân viên">Nhân viên</option>
            </select>
          </div>
        </div>

        <div>
          <label class="block font-semibold text-sm mb-1">Địa chỉ</label>
          <input
            type="text"
            v-model="employeeData.address"
            class="border p-2 w-full rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
          />
        </div>

        <!-- Chỉ hiển thị username & password khi thêm mới -->
        <div v-if="!isEditing" class="grid grid-cols-2 gap-4">
          <div>
            <label class="block font-semibold text-sm mb-1"
              >Username <span class="text-red-500">*</span></label
            >
            <input
              type="text"
              v-model="employeeData.username"
              class="border p-2 w-full rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
              :class="{ 'border-red-500': errors.username }"
            />
            <p v-if="errors.username" class="text-red-500 text-xs mt-1">{{ errors.username }}</p>
          </div>

          <div>
            <label class="block font-semibold text-sm mb-1"
              >Password <span class="text-red-500">*</span></label
            >
            <div class="relative">
              <input
                :type="showPassword ? 'text' : 'password'"
                v-model="employeeData.password"
                class="border p-2 w-full rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all pr-10"
                :class="{ 'border-red-500': errors.password }"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500"
              >
                <svg
                  v-if="showPassword"
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
                  <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24"></path>
                  <path
                    d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68"
                  ></path>
                  <path
                    d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61"
                  ></path>
                  <line x1="2" x2="22" y1="2" y2="22"></line>
                </svg>
                <svg
                  v-else
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
                  <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"></path>
                  <circle cx="12" cy="12" r="3"></circle>
                </svg>
              </button>
            </div>
            <p v-if="errors.password" class="text-red-500 text-xs mt-1">{{ errors.password }}</p>
          </div>
        </div>

        <div class="mb-4" v-if="isEditing">
          <label class="block font-semibold text-sm mb-1">Trạng thái</label>
          <select
            v-model="employeeData.status"
            class="border p-2 w-full rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
          >
            <option value="Hoạt động">Hoạt động</option>
            <option value="Đã nghỉ">Đã nghỉ</option>
          </select>
        </div>
      </div>

      <!-- Nút lưu & hủy -->
      <div class="flex justify-end mt-6">
        <button
          @click="$emit('close')"
          class="bg-gray-400 hover:bg-gray-500 text-white px-4 py-2 rounded mr-2 transition-colors"
        >
          Hủy
        </button>
        <button
          @click="validateAndSave"
          class="bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded transition-colors flex items-center"
        >
          <svg
            v-if="isEditing"
            xmlns="http://www.w3.org/2000/svg"
            width="16"
            height="16"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="mr-1"
          >
            <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
            <polyline points="17 21 17 13 7 13 7 21"></polyline>
            <polyline points="7 3 7 8 15 8"></polyline>
          </svg>
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            width="16"
            height="16"
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
      errors: {},
      showPassword: false,
    }
  },
  computed: {
    isEditing() {
      return !!this.employee
    },
  },
  methods: {
    validateAndSave() {
      this.errors = {}
      let isValid = true

      // Validate name
      if (!this.employeeData.name.trim()) {
        this.errors.name = 'Vui lòng nhập họ và tên'
        isValid = false
      }

      // Validate username & password for new employees
      if (!this.isEditing) {
        if (!this.employeeData.username.trim()) {
          this.errors.username = 'Vui lòng nhập tên đăng nhập'
          isValid = false
        }

        if (!this.employeeData.password) {
          this.errors.password = 'Vui lòng nhập mật khẩu'
          isValid = false
        } else if (this.employeeData.password.length < 6) {
          this.errors.password = 'Mật khẩu phải có ít nhất 6 ký tự'
          isValid = false
        }
      }

      // Validate CCCD if provided
      if (this.employeeData.cccd && !/^\d{9,12}$/.test(this.employeeData.cccd)) {
        this.errors.cccd = 'CCCD phải có 9-12 chữ số'
        isValid = false
      }

      // Validate phone if provided
      if (this.employeeData.phone && !/^0\d{9,10}$/.test(this.employeeData.phone)) {
        this.errors.phone = 'Số điện thoại không hợp lệ'
        isValid = false
      }

      if (isValid) {
        this.$emit('save', this.employeeData)
        this.$emit('close')
      }
    },
  },
}
</script>
