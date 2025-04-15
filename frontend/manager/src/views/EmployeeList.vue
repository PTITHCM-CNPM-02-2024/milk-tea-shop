<template>
  <div>
    <!-- Phần header với tiêu đề và nút thêm mới -->
    <div class="d-flex justify-space-between align-center mb-4 px-4 pt-4">
      <div>
        <h2 class="text-h5 font-weight-bold">Quản lý nhân viên</h2>
        <p class="text-subtitle-2 text-medium-emphasis mt-1">
          Quản lý thông tin nhân viên và phân quyền truy cập
        </p>
      </div>

      <v-btn
        color="primary"
        prepend-icon="mdi-account-plus"
        variant="flat"
        @click="openAddDialog"
      >
        Thêm nhân viên
      </v-btn>
    </div>

    <!-- Alert hiển thị lỗi nếu có -->
    <v-alert
      v-if="employeeStore.error"
      type="error"
      variant="tonal"
      closable
      class="mx-4 mb-4"
    >
      {{ employeeStore.error }}
    </v-alert>

    <!-- Card danh sách nhân viên -->
    <v-card class="mx-4">
      <v-card-title class="d-flex align-center py-3">
        <span class="text-h6">Danh sách nhân viên</span>
        <v-spacer></v-spacer>

        <!-- Tìm kiếm -->
        <v-text-field
          v-model="searchQuery"
          label="Tìm kiếm"
          density="compact"
          variant="outlined"
          hide-details
          prepend-inner-icon="mdi-magnify"
          clearable
          class="max-width-300"
          @update:model-value="debounceSearch"
        ></v-text-field>
      </v-card-title>

      <v-divider></v-divider>

      <v-card-text class="pa-0">
        <!-- Bảng nhân viên -->
        <v-data-table
          :headers="headers"
          :items="filteredEmployees"
          :loading="employeeStore.loading"
          loading-text="Đang tải dữ liệu..."
          no-data-text="Không có dữ liệu"
          item-value="id"
          hover
          class="elevation-0"
          :items-per-page="10"
          :page="page"
          @update:page="page = $event"
          :items-per-page-options="[5, 10, 15, 20]"
        >
          <!-- ID column -->
          <template v-slot:item.id="{ item }">
            <span class="text-caption">#{{ item.id }}</span>
          </template>

          <!-- Tên nhân viên -->
          <template v-slot:item.fullName="{ item }">
            <div class="d-flex align-center">
              <v-avatar size="40" color="grey-lighten-3" class="mr-3">
                <span class="font-weight-medium">{{ getInitials(item.firstName, item.lastName) }}</span>
              </v-avatar>
              <div>
                <div class="font-weight-medium">{{ item.firstName }} {{ item.lastName }}</div>
                <div class="text-caption text-medium-emphasis">{{ item.position }}</div>
              </div>
            </div>
          </template>

          <!-- Giới tính -->
          <template v-slot:item.gender="{ item }">
            <v-chip
              size="small"
              :color="item.gender === 'MALE' ? 'blue' : 'pink'"
              text-color="white"
              variant="flat"
            >
              {{ getGenderText(item.gender) }}
            </v-chip>
          </template>

          <!-- Hành động -->
          <template v-slot:item.actions="{ item }">
            <div class="d-flex gap-2">
              <v-btn
                icon="mdi-pencil"
                size="small"
                color="primary"
                variant="text"
                @click="openEditDialog(item)"
              ></v-btn>
              <v-btn
                icon="mdi-delete"
                size="small"
                color="error"
                variant="text"
                @click="openDeleteDialog(item)"
              ></v-btn>
            </div>
          </template>
        </v-data-table>

        <!-- Phân trang -->
        <div class="d-flex justify-center py-4">
          <v-pagination
            v-if="totalFilteredPages > 0"
            v-model="page"
            :length="totalFilteredPages"
            :total-visible="7"
            @update:model-value="handlePageChange"
          ></v-pagination>
        </div>
      </v-card-text>
    </v-card>

    <!-- Dialog quản lý nhân viên -->
    <v-dialog v-model="employeeDialog" max-width="1000px" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          {{ editMode ? 'Chỉnh sửa thông tin nhân viên' : 'Thêm nhân viên mới' }}
        </v-card-title>

        <v-divider></v-divider>

        <v-card-text class="pa-0">
          <v-tabs v-model="activeTab" bg-color="primary">
            <v-tab value="info">
              <v-icon start>mdi-account</v-icon>
              Thông tin nhân viên
            </v-tab>
            <v-tab value="account" v-if="editMode && accountDetail">
              <v-icon start>mdi-shield-account</v-icon>
              Thông tin tài khoản
            </v-tab>
            <v-tab value="password" v-if="editMode && accountDetail">
              <v-icon start>mdi-lock-reset</v-icon>
              Đổi mật khẩu
            </v-tab>
            <v-tab value="role" v-if="editMode && accountDetail">
              <v-icon start>mdi-account-cog</v-icon>
              Phân quyền
            </v-tab>
          </v-tabs>

          <v-window v-model="activeTab">
            <!-- Tab thông tin nhân viên -->
            <v-window-item value="info">
              <v-container class="pa-4">
                <v-form ref="form" @submit.prevent="saveEmployee">
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedEmployee.firstName"
                        label="Họ"
                        variant="outlined"
                        :rules="[v => !!v || 'Vui lòng nhập họ']"
                      ></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedEmployee.lastName"
                        label="Tên"
                        variant="outlined"
                        :rules="[v => !!v || 'Vui lòng nhập tên']"
                      ></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedEmployee.email"
                        label="Email"
                        variant="outlined"
                        :rules="[
                          v => !!v || 'Vui lòng nhập email',
                          v => /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(v) || 'Email không hợp lệ'
                        ]"
                      ></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedEmployee.phone"
                        label="Số điện thoại"
                        variant="outlined"
                        :rules="[
                          v => !!v || 'Vui lòng nhập số điện thoại',
                          v => /^(0|\+84)[3|5|7|8|9][0-9]{8}$/.test(v) || 'Số điện thoại không hợp lệ'
                        ]"
                      ></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-select
                        v-model="editedEmployee.gender"
                        label="Giới tính"
                        variant="outlined"
                        :items="genderOptions"
                        :rules="[v => !!v || 'Vui lòng chọn giới tính']"
                      ></v-select>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedEmployee.position"
                        label="Chức vụ"
                        variant="outlined"
                        :rules="[v => !!v || 'Vui lòng nhập chức vụ']"
                      ></v-text-field>
                    </v-col>

                    <template v-if="!editMode">
                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="editedEmployee.username"
                          label="Tên đăng nhập"
                          variant="outlined"
                          :rules="[v => !!v || 'Vui lòng nhập tên đăng nhập']"
                        ></v-text-field>
                      </v-col>

                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="editedEmployee.password"
                          label="Mật khẩu"
                          variant="outlined"
                          type="password"
                          :rules="[v => !!v || 'Vui lòng nhập mật khẩu']"
                        ></v-text-field>
                      </v-col>

                      <v-col cols="12">
                        <v-select
                          v-model="editedEmployee.roleId"
                          label="Vai trò"
                          variant="outlined"
                          :items="roleOptions"
                          :rules="[v => !!v || 'Vui lòng chọn vai trò']"
                        ></v-select>
                      </v-col>
                    </template>
                  </v-row>
                </v-form>
              </v-container>
            </v-window-item>

            <!-- Tab thông tin tài khoản -->
            <v-window-item value="account">
              <v-container class="pa-4">
                <div v-if="accountDetail">
                  <div class="d-flex align-center mb-4">
                    <v-avatar size="80" color="primary" class="mr-4">
                      <span class="text-h4 font-weight-medium white--text">
                        {{ getInitials(employeeDetail.firstName, employeeDetail.lastName) }}
                      </span>
                    </v-avatar>

                    <div>
                      <h2 class="text-h5 mb-1">{{ employeeDetail.firstName }} {{ employeeDetail.lastName }}</h2>
                      <div class="d-flex align-center">
                        <v-chip
                          size="small"
                          :color="employeeDetail.gender === 'MALE' ? 'blue' : 'pink'"
                          text-color="white"
                          variant="flat"
                          class="mr-2"
                        >
                          {{ getGenderText(employeeDetail.gender) }}
                        </v-chip>
                        <span class="text-body-2">{{ employeeDetail.position }}</span>
                      </div>
                    </div>
                  </div>

                  <v-divider class="mb-4"></v-divider>

                  <v-row>
                    <v-col cols="12" md="6">
                      <h3 class="text-subtitle-1 font-weight-bold mb-3">Thông tin cá nhân</h3>

                      <v-list density="compact">
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-email</v-icon>
                          </template>
                          <v-list-item-title>Email</v-list-item-title>
                          <v-list-item-subtitle>{{ employeeDetail.email }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-phone</v-icon>
                          </template>
                          <v-list-item-title>Số điện thoại</v-list-item-title>
                          <v-list-item-subtitle>{{ employeeDetail.phone }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-briefcase</v-icon>
                          </template>
                          <v-list-item-title>Chức vụ</v-list-item-title>
                          <v-list-item-subtitle>{{ employeeDetail.position }}</v-list-item-subtitle>
                        </v-list-item>
                      </v-list>
                    </v-col>

                    <v-col cols="12" md="6">
                      <h3 class="text-subtitle-1 font-weight-bold mb-3">Thông tin tài khoản</h3>

                      <v-list density="compact">
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-account</v-icon>
                          </template>
                          <v-list-item-title>Tài khoản</v-list-item-title>
                          <v-list-item-subtitle>{{ accountDetail.username }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-shield-account</v-icon>
                          </template>
                          <v-list-item-title>Vai trò</v-list-item-title>
                          <v-list-item-subtitle>
                            <v-chip size="small" color="primary" class="mt-1">
                              {{ accountDetail.role.name }}
                            </v-chip>
                            <div v-if="accountDetail.role.description" class="mt-1 text-caption">
                              {{ accountDetail.role.description }}
                            </div>
                          </v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-account-check</v-icon>
                          </template>
                          <v-list-item-title>Trạng thái hoạt động</v-list-item-title>
                          <v-list-item-subtitle>
                            <v-chip 
                              size="small" 
                              :color="accountDetail.isActive ? 'success' : 'warning'" 
                              class="mt-1"
                            >
                              {{ accountDetail.isActive ? 'Đang hoạt động' : 'Vô hiệu hóa' }}
                            </v-chip>
                          </v-list-item-subtitle>
                        </v-list-item>
                        
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-lock</v-icon>
                          </template>
                          <v-list-item-title>Trạng thái khóa</v-list-item-title>
                          <v-list-item-subtitle>
                            <v-chip 
                              size="small" 
                              :color="accountDetail.isLocked ? 'error' : 'success'" 
                              class="mt-1"
                            >
                              {{ accountDetail.isLocked ? 'Đã khóa' : 'Không khóa' }}
                            </v-chip>
                          </v-list-item-subtitle>
                        </v-list-item>
                      </v-list>
                    </v-col>
                  </v-row>

                  <div class="d-flex gap-2 mt-4 flex-column">
                    <v-btn
                      color="indigo"
                      variant="outlined"
                      @click="activeTab = 'role'; initRoleChange()"
                      class="mb-3"
                    >
                      <v-icon start>mdi-shield-edit</v-icon>
                      Đổi vai trò
                    </v-btn>

                    <v-btn
                      :color="accountDetail.isLocked ? 'success' : 'error'"
                      variant="outlined"
                      @click="toggleAccountLockStatus"
                    >
                      <v-icon start>{{ accountDetail.isLocked ? 'mdi-lock-open' : 'mdi-lock' }}</v-icon>
                      {{ accountDetail.isLocked ? 'Mở khóa tài khoản' : 'Khóa tài khoản' }}
                    </v-btn>
                  </div>
                </div>

                <v-alert v-else type="info" variant="tonal" class="mb-3">
                  Nhân viên chưa có tài khoản trong hệ thống
                </v-alert>
              </v-container>
            </v-window-item>

            <!-- Tab đổi mật khẩu -->
            <v-window-item value="password">
              <v-container class="pa-4">
                <v-form ref="passwordForm" @submit.prevent="savePasswordChange">
                  <v-text-field
                    v-model="passwordChange.oldPassword"
                    label="Mật khẩu cũ"
                    type="password"
                    variant="outlined"
                    :rules="[v => !!v || 'Vui lòng nhập mật khẩu cũ']"
                  ></v-text-field>

                  <v-text-field
                    v-model="passwordChange.newPassword"
                    label="Mật khẩu mới"
                    type="password"
                    variant="outlined"
                    :rules="[
                      v => !!v || 'Vui lòng nhập mật khẩu mới',
                      v => (v && v.length >= 6) || 'Mật khẩu phải có ít nhất 6 ký tự'
                    ]"
                  ></v-text-field>

                  <v-text-field
                    v-model="passwordChange.confirmPassword"
                    label="Xác nhận mật khẩu mới"
                    type="password"
                    variant="outlined"
                    :rules="[
                      v => !!v || 'Vui lòng xác nhận mật khẩu mới',
                      v => v === passwordChange.newPassword || 'Mật khẩu xác nhận không khớp'
                    ]"
                  ></v-text-field>

                  <div class="d-flex justify-end mt-4">
                    <v-btn
                      color="primary"
                      @click="savePasswordChange"
                      :loading="loading"
                    >
                      Cập nhật mật khẩu
                    </v-btn>
                  </div>
                </v-form>
              </v-container>
            </v-window-item>

            <!-- Tab đổi vai trò -->
            <v-window-item value="role">
              <v-container class="pa-4">
                <v-form ref="roleForm" @submit.prevent="saveRoleChange">
                  <v-select
                    v-model="roleChange.roleId"
                    label="Vai trò mới"
                    variant="outlined"
                    :items="roleOptions"
                    :rules="[v => !!v || 'Vui lòng chọn vai trò']"
                  ></v-select>

                  <div class="d-flex justify-end mt-4">
                    <v-btn
                      color="primary"
                      @click="saveRoleChange"
                      :loading="loading"
                    >
                      Cập nhật vai trò
                    </v-btn>
                  </div>
                </v-form>
              </v-container>
            </v-window-item>
          </v-window>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeEmployeeDialog">Hủy</v-btn>
          <v-btn
            color="primary"
            @click="activeTab === 'info' ? saveEmployee() : null"
            :loading="loading"
            v-if="activeTab === 'info'"
          >
            {{ editMode ? 'Cập nhật' : 'Thêm mới' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog xác nhận xóa -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h5 font-weight-medium pa-4">
          Xác nhận xóa
        </v-card-title>

        <v-card-text class="pa-4">
          Bạn có chắc chắn muốn xóa nhân viên <strong>{{ editedEmployee.firstName }} {{ editedEmployee.lastName }}</strong> khỏi hệ thống?
          <p class="text-medium-emphasis mt-2">Hành động này không thể hoàn tác.</p>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDeleteDialog">Hủy</v-btn>
          <v-btn
            color="error"
            @click="deleteEmployee"
            :loading="loading"
          >
            Xóa
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar thông báo -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
    >
      {{ snackbar.text }}
      <template v-slot:actions>
        <v-btn
          color="white"
          icon="mdi-close"
          variant="text"
          @click="snackbar.show = false"
        ></v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useEmployeeStore } from '@/stores/employee'
import { useRoleStore } from '@/stores/role'
import { debounce } from 'lodash'
import { accountService } from '@/services/accountService'

const employeeStore = useEmployeeStore()
const roleStore = useRoleStore()

// State
const page = ref(1)
const searchQuery = ref('')
const employeeDialog = ref(false)
const deleteDialog = ref(false)
const editMode = ref(false)
const activeTab = ref('info')
const editedEmployee = ref({
  id: null,
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  gender: 'MALE',
  position: '',
  username: '',
  password: '',
  roleId: null
})
const defaultEmployee = {
  id: null,
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  gender: 'MALE',
  position: '',
  username: '',
  password: '',
  roleId: null
}

// State cho xem chi tiết
const loading = ref(false)
const employeeDetail = ref({})
const accountDetail = ref(null)

// State cho đổi mật khẩu
const passwordChange = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// State cho đổi vai trò
const roleChange = ref({
  roleId: null
})

// Form
const form = ref(null)
const passwordForm = ref(null)
const roleForm = ref(null)
const snackbar = reactive({
  show: false,
  text: '',
  color: 'success'
})

// Cấu hình headers cho bảng
const headers = [
  { title: 'ID', key: 'id', width: '70px', sortable: true },
  { title: 'Họ Tên', key: 'fullName', align: 'start', sortable: false },
  { title: 'Email', key: 'email', align: 'start', sortable: true },
  { title: 'Số điện thoại', key: 'phone', align: 'start', sortable: true },
  { title: 'Giới tính', key: 'gender', align: 'center', width: '100px', sortable: false },
  { title: 'Hành động', key: 'actions', align: 'end', sortable: false, width: '100px' }
]

// Options cho select
const genderOptions = [
  { title: 'Nam', value: 'MALE' },
  { title: 'Nữ', value: 'FEMALE' },
  { title: 'Khác', value: 'OTHER' }
]

// Danh sách vai trò từ roleStore
const roleOptions = computed(() => {
  return roleStore.roles.map(role => ({
    title: role.name,
    value: role.id
  }))
})

// Computed property cho nhân viên đã lọc
const filteredEmployees = computed(() => {
  let result = [...employeeStore.employees]

  // Lọc theo từ khóa tìm kiếm
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(employee =>
      employee.firstName?.toLowerCase().includes(query) ||
      employee.lastName?.toLowerCase().includes(query) ||
      employee.email?.toLowerCase().includes(query) ||
      employee.phone?.includes(query) ||
      employee.position?.toLowerCase().includes(query)
    )
  }

  return result
})

// Computed property cho tổng số trang dựa trên kết quả đã lọc
const totalFilteredPages = computed(() => {
  return Math.ceil(filteredEmployees.value.length / 10) // 10 là số mục mỗi trang
})

// Methods
// Lấy danh sách nhân viên
const loadEmployees = () => {
  employeeStore.fetchEmployees(0, 100) // Lấy tất cả nhân viên để lọc ở client
}

// Lấy danh sách vai trò
const loadRoles = () => {
  roleStore.fetchRoles(0, 100) // Lấy tất cả vai trò với kích thước lớn
}

// Lấy chữ cái đầu
const getInitials = (firstName, lastName) => {
  if (!firstName && !lastName) return 'N/A'
  return ((firstName ? firstName.charAt(0) : '') + (lastName ? lastName.charAt(0) : '')).toUpperCase()
}

// Lấy text giới tính
const getGenderText = (gender) => {
  switch(gender) {
    case 'MALE': return 'Nam'
    case 'FEMALE': return 'Nữ'
    default: return 'Khác'
  }
}

// Mở dialog thêm nhân viên
const openAddDialog = () => {
  editMode.value = false
  activeTab.value = 'info'
  editedEmployee.value = {...defaultEmployee}
  accountDetail.value = null
  employeeDialog.value = true
}

// Mở dialog sửa nhân viên
const openEditDialog = async (employee) => {
  loading.value = true
  editMode.value = true
  activeTab.value = 'info'

  try {
    // Lấy thông tin chi tiết nhân viên
    const response = await employeeStore.fetchEmployeeById(employee.id)
    employeeDetail.value = response

    // Set dữ liệu cho form
    editedEmployee.value = {
      id: employeeDetail.value.id,
      firstName: employeeDetail.value.firstName,
      lastName: employeeDetail.value.lastName,
      email: employeeDetail.value.email,
      phone: employeeDetail.value.phone,
      gender: employeeDetail.value.gender,
      position: employeeDetail.value.position
    }

    // Nếu nhân viên có tài khoản liên kết, lấy thông tin tài khoản
    if (employeeDetail.value.accountId) {
      try {
        const accountResponse = await accountService.getAccountById(employeeDetail.value.accountId)
        accountDetail.value = accountResponse.data
        activeTab.value = 'account' // Nếu có tài khoản, mở tab thông tin tài khoản
      } catch (error) {
        console.error('Lỗi khi lấy thông tin tài khoản:', error)
        accountDetail.value = null
      }
    } else {
      accountDetail.value = null
    }

    employeeDialog.value = true
  } catch (error) {
    console.error('Lỗi khi lấy thông tin nhân viên:', error)
    showSnackbar('Đã xảy ra lỗi khi tải thông tin chi tiết nhân viên', 'error')
  } finally {
    loading.value = false
  }
}

// Đóng dialog quản lý nhân viên
const closeEmployeeDialog = () => {
  employeeDialog.value = false
  // Reset các tab về ban đầu
  setTimeout(() => {
    activeTab.value = 'info'
  }, 300)
}

// Mở dialog xác nhận xóa
const openDeleteDialog = (employee) => {
  editedEmployee.value = {...employee}
  deleteDialog.value = true
}

// Đóng dialog xác nhận xóa
const closeDeleteDialog = () => {
  deleteDialog.value = false
}

// Lưu nhân viên
const saveEmployee = async () => {
  if (!form.value) return

  const { valid } = await form.value.validate()
  if (!valid) return

  try {
    const employeeData = { ...editedEmployee.value }

    if (editMode.value) {
      // Xóa các trường không cần thiết khi cập nhật
      delete employeeData.username
      delete employeeData.password
      delete employeeData.roleId
      delete employeeData.accountId

      await employeeStore.updateEmployee(employeeData.id, employeeData)
      showSnackbar('Cập nhật thông tin nhân viên thành công', 'success')
    } else {
      // Đảm bảo dữ liệu đúng định dạng cho API tạo mới
      await employeeStore.createEmployee(employeeData)
      showSnackbar('Thêm nhân viên mới thành công', 'success')
    }

    closeEmployeeDialog()
    loadEmployees()
  } catch (error) {
    console.error('Lỗi khi lưu nhân viên:', error)
    showSnackbar('Đã xảy ra lỗi: ' + (error.response?.data || error.message), 'error')
  }
}

// Xóa nhân viên
const deleteEmployee = async () => {
  try {
    await employeeStore.deleteEmployee(editedEmployee.value.id)
    showSnackbar('Xóa nhân viên thành công', 'success')
    closeDeleteDialog()
    loadEmployees()
  } catch (error) {
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
  }
}

// Xử lý thay đổi trang
const handlePageChange = (newPage) => {
  page.value = newPage
}

// Debounce search
const debounceSearch = debounce(() => {
  page.value = 1 // Reset về trang đầu khi tìm kiếm
}, 300)

// Hiển thị snackbar
const showSnackbar = (text, color = 'success') => {
  snackbar.text = text
  snackbar.color = color
  snackbar.show = true
}

// Lưu thay đổi mật khẩu
const savePasswordChange = async () => {
  if (!passwordForm.value) return

  const { valid } = await passwordForm.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await accountService.changePassword(
      employeeDetail.value.accountId,
      passwordChange.value.oldPassword,
      passwordChange.value.newPassword,
      passwordChange.value.confirmPassword
    )
    showSnackbar('Đổi mật khẩu thành công', 'success')

    // Reset form và chuyển về tab thông tin tài khoản
    passwordChange.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
    activeTab.value = 'account'
  } catch (error) {
    showSnackbar('Đã xảy ra lỗi: ' + (error.response?.data?.message || error.message), 'error')
  } finally {
    loading.value = false
  }
}

// Lưu thay đổi vai trò
const saveRoleChange = async () => {
  if (!roleForm.value) return

  const { valid } = await roleForm.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await accountService.changeRole(
      employeeDetail.value.accountId,
      roleChange.value.roleId
    )

    // Cập nhật lại thông tin hiển thị
    const accountResponse = await accountService.getAccountById(employeeDetail.value.accountId)
    accountDetail.value = accountResponse.data

    showSnackbar('Thay đổi vai trò thành công', 'success')
    activeTab.value = 'account'
  } catch (error) {
    showSnackbar('Đã xảy ra lỗi: ' + (error.response?.data?.message || error.message), 'error')
  } finally {
    loading.value = false
  }
}

// Khởi tạo dữ liệu cho thay đổi vai trò
const initRoleChange = () => {
  // Đảm bảo đã tải danh sách vai trò
  if (roleStore.roles.length === 0) {
    loadRoles()
  }

  roleChange.value = {
    roleId: accountDetail.value.role.id
  }
}


// Khởi tạo thêm phương thức khóa/mở khóa tài khoản
// Khóa/Mở khóa tài khoản
const toggleAccountLockStatus = async () => {
  loading.value = true
  try {
    await employeeStore.toggleAccountLock(accountDetail.value.id, !accountDetail.value.isLocked)

    // Cập nhật trạng thái khóa trong UI
    accountDetail.value.isLocked = !accountDetail.value.isLocked

    showSnackbar(
      accountDetail.value.isLocked
        ? 'Tài khoản đã được khóa thành công'
        : 'Tài khoản đã được mở khóa thành công',
      'success'
    )
  } catch (error) {
    showSnackbar('Đã xảy ra lỗi: ' + (error.response?.data?.message || error.message), 'error')
  } finally {
    loading.value = false
  }
}

// Lifecycle
onMounted(() => {
  loadEmployees()
  loadRoles()
})
</script>

<style scoped>
.max-width-300 {
  max-width: 300px;
}
</style>
