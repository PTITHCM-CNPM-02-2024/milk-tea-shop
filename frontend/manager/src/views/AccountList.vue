<template>
  <div>
    <!-- Phần header với tiêu đề và nút thêm mới -->
    <div class="d-flex justify-space-between align-center mb-6 px-4 pt-4">
      <div>
        <h2 class="text-h5 font-weight-bold">Danh sách tài khoản</h2>
        <p class="text-subtitle-2 text-medium-emphasis mt-1">
          Quản lý thông tin tài khoản người dùng trong hệ thống
        </p>
      </div>
      
      <v-btn 
        color="primary" 
        prepend-icon="mdi-account-plus" 
        @click="openAddDialog"
      >
        Thêm tài khoản
      </v-btn>
    </div>
    
    <!-- Alert hiển thị lỗi nếu có -->
    <v-alert
      v-if="accountStore.error"
      type="error"
      variant="tonal"
      closable
      class="mx-4 mb-4"
    >
      {{ accountStore.error }}
    </v-alert>
    
    <!-- Filter và Search -->
    <v-row class="mb-4 px-3">
      <v-col cols="12" sm="4">
        <v-text-field
          v-model="searchQuery"
          label="Tìm kiếm"
          density="compact"
          variant="outlined"
          hide-details
          prepend-inner-icon="mdi-magnify"
          clearable
          @update:model-value="debounceSearch"
        ></v-text-field>
      </v-col>
      
      <v-col cols="12" sm="3">
        <v-select
          v-model="selectedRole"
          :items="roleOptions"
          label="Vai trò"
          density="compact"
          variant="outlined"
          hide-details
          clearable
          @update:model-value="loadAccounts"
        ></v-select>
      </v-col>
      
      <v-col cols="12" sm="3">
        <v-select
          v-model="selectedStatus"
          :items="statusOptions"
          label="Trạng thái"
          density="compact"
          variant="outlined"
          hide-details
          clearable
          @update:model-value="loadAccounts"
        ></v-select>
      </v-col>
      
      <v-col cols="12" sm="2">
        <v-btn 
          color="primary" 
          variant="outlined" 
          block
          @click="resetFilters"
          :disabled="!hasFilters"
        >
          Đặt lại
        </v-btn>
      </v-col>
    </v-row>
    
    <!-- Bảng danh sách tài khoản -->
    <v-data-table
      :headers="headers"
      :items="accountStore.accounts"
      :loading="accountStore.loading"
      loading-text="Đang tải dữ liệu..."
      no-data-text="Không có dữ liệu"
      item-value="id"
      hover
      :items-per-page="accountStore.pageSize"
      density="comfortable"
      class="elevation-0"
    >
      <!-- Cột STT -->
      <template v-slot:item.index="{ item, index }">
        {{ accountStore.currentPage * accountStore.pageSize + index + 1 }}
      </template>
      
      <!-- Cột Username -->
      <template v-slot:item.username="{ item }">
        <div class="font-weight-medium">{{ item.username }}</div>
      </template>
      
      <!-- Cột Vai trò -->
      <template v-slot:item.role="{ item }">
        <v-chip
          :color="getRoleColor(item.role?.name)"
          size="small"
          label
          class="text-white"
        >
          {{ item.role?.description }}
        </v-chip>
      </template>
      
      <!-- Cột Trạng thái -->
      <template v-slot:item.status="{ item }">
        <div class="d-flex align-center">
          <v-icon
            :color="item.isActive ? 'success' : 'error'"
            :icon="item.isActive ? 'mdi-check-circle' : 'mdi-close-circle'"
            size="small"
            class="mr-1"
          >
        </v-icon>          
        {{ item.isActive ? 'Hoạt động' : 'Không hoạt động' }}
        </div>
      </template>
      
      <!-- Cột Khóa -->
      <template v-slot:item.lock="{ item }">
        <div class="d-flex align-center">
          <v-icon
            :icon="item.isLocked ? 'mdi-lock-open' : 'mdi-lock'"
            size="small"
            :color="item.isLocked ? 'success' : 'warning'"
          ></v-icon>
          <v-tooltip v-if="item.isLocked" location="top">
            <template v-slot:activator="{ props }">
              <v-icon
                v-bind="props"
                icon="mdi-lock"
                color="warning"
                size="small"
                class="ml-2"
              ></v-icon>
            </template>
            <span>Tài khoản đang bị khóa</span>
          </v-tooltip>
        </div>
      </template>
    </v-data-table>
    
    <!-- Phân trang -->
    <div class="d-flex justify-center mt-4 pb-4">
      <v-pagination
        v-model="page"
        :length="accountStore.totalPages"
        total-visible="7"
        @update:model-value="handlePageChange"
      ></v-pagination>
    </div>
    
  <!-- Dialog thêm tài khoản mới -->
  <v-dialog v-model="addDialog" width="500" persistent>
    <v-card>
      <v-card-title class="text-h5 font-weight-bold pa-4">
        Thêm tài khoản mới
      </v-card-title>
      
      <v-divider></v-divider>
      
      <v-card-text class="pa-4">
        <v-form ref="addForm" @submit.prevent="saveAccount">
          <v-text-field
            v-model="editedAccount.username"
            label="Tên đăng nhập"
            variant="outlined"
            required
            :rules="[v => !!v || 'Vui lòng nhập tên đăng nhập']"
            class="mb-3"
          ></v-text-field>
          
          <v-text-field
            v-model="editedAccount.password"
            label="Mật khẩu"
            variant="outlined"
            type="password"
            required
            :rules="[
              v => !!v || 'Vui lòng nhập mật khẩu',
              v => (v && v.length >= 6) || 'Mật khẩu phải có ít nhất 6 ký tự'
            ]"
            class="mb-3"
          ></v-text-field>
          
          <v-text-field
            v-model="editedAccount.description"
            label="Mô tả"
            variant="outlined"
            class="mb-3"
          ></v-text-field>
          
          <v-select
            v-model="editedAccount.role"
            :items="accountStore.roles"
            item-title="description"
            item-value="id"
            label="Vai trò"
            variant="outlined"
            required
            :rules="[v => !!v || 'Vui lòng chọn vai trò']"
            class="mb-3"
            return-object
          ></v-select>
          
          <v-row>
            <v-col cols="6">
              <v-switch
                v-model="editedAccount.isActive"
                label="Kích hoạt tài khoản"
                color="success"
                hide-details
              ></v-switch>
            </v-col>
            
            <v-col cols="6">
              <v-switch
                v-model="editedAccount.isLocked"
                label="Khóa tài khoản"
                color="warning"
                hide-details
              ></v-switch>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      
      <v-divider></v-divider>
      
      <v-card-actions class="pa-4">
        <v-spacer></v-spacer>
        <v-btn variant="text" @click="closeAddDialog">Hủy</v-btn>
        <v-btn 
          color="primary" 
          @click="saveAccount" 
          :loading="accountStore.loading"
        >
          Lưu
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  
  <!-- Dialog chỉnh sửa tài khoản -->
  <v-dialog v-model="editDialog" width="500" persistent>
    <v-card>
      <v-card-title class="text-h5 font-weight-bold pa-4">
        Chỉnh sửa tài khoản
      </v-card-title>
      
      <v-divider></v-divider>
      
      <v-card-text class="pa-4">
        <v-form ref="editForm" @submit.prevent="updateAccount">
          <v-text-field
            v-model="editedAccount.username"
            label="Tên đăng nhập"
            variant="outlined"
            required
            :rules="[v => !!v || 'Vui lòng nhập tên đăng nhập']"
            class="mb-3"
            disabled
          ></v-text-field>
          
          <v-text-field
            v-model="editedAccount.password"
            label="Mật khẩu mới (để trống nếu không thay đổi)"
            variant="outlined"
            type="password"
            :rules="[
              v => !v || v.length >= 6 || 'Mật khẩu phải có ít nhất 6 ký tự'
            ]"
            class="mb-3"
          ></v-text-field>
          
          <v-text-field
            v-model="editedAccount.description"
            label="Mô tả"
            variant="outlined"
            class="mb-3"
          ></v-text-field>
          
          <v-select
            v-model="editedAccount.role"
            :items="accountStore.roles"
            item-title="description"
            item-value="id"
            label="Vai trò"
            variant="outlined"
            required
            :rules="[v => !!v || 'Vui lòng chọn vai trò']"
            class="mb-3"
            return-object
          ></v-select>
          
          <v-row>
            <v-col cols="6">
              <v-switch
                v-model="editedAccount.isActive"
                label="Kích hoạt tài khoản"
                color="success"
                hide-details
              ></v-switch>
            </v-col>
            
            <v-col cols="6">
              <v-switch
                v-model="editedAccount.isLocked"
                label="Khóa tài khoản"
                color="warning"
                hide-details
              ></v-switch>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      
      <v-divider></v-divider>
      
      <v-card-actions class="pa-4">
        <v-spacer></v-spacer>
        <v-btn variant="text" @click="closeEditDialog">Hủy</v-btn>
        <v-btn 
          color="primary" 
          @click="updateAccount" 
          :loading="accountStore.loading"
        >
          Cập nhật
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  
  <!-- Dialog xác nhận xóa tài khoản -->
  <v-dialog v-model="deleteDialog" width="400">
    <v-card>
      <v-card-title class="text-h5 font-weight-medium pa-4">
        Xác nhận xóa
      </v-card-title>
      
      <v-card-text class="pa-4">
        Bạn có chắc chắn muốn xóa tài khoản <strong>{{ editedAccount.username }}</strong>?
        <p class="text-medium-emphasis mt-2">Hành động này không thể hoàn tác.</p>
      </v-card-text>
      
      <v-divider></v-divider>
      
      <v-card-actions class="pa-4">
        <v-spacer></v-spacer>
        <v-btn variant="text" @click="closeDeleteDialog">Hủy</v-btn>
        <v-btn 
          color="error" 
          @click="deleteSelectedAccount" 
          :loading="accountStore.loading"
        >
          Xóa
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  
  <!-- Snackbar thông báo -->
  <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">
    {{ snackbar.message }}
    <template v-slot:actions>
      <v-btn variant="text" icon="mdi-close" @click="snackbar.show = false"></v-btn>
    </template>
  </v-snackbar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useAccountStore } from '@/stores/account'

// Khởi tạo store
const accountStore = useAccountStore()

// Trạng thái local
const page = ref(1)
const searchQuery = ref('')
const selectedRole = ref(null)
const selectedStatus = ref(null)
const addDialog = ref(false)
const editDialog = ref(false)
const deleteDialog = ref(false)
const editedIndex = ref(-1)
const editedAccount = ref({
  id: null,
  username: '',
  description: '',
  isActive: true,
  isLocked: false,
  role: null,
  password: ''
})
const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})
const addForm = ref(null)
const editForm = ref(null)

// Cấu hình headers cho bảng
const headers = [
  { title: 'STT', key: 'index', width: '70px', sortable: false },
  { title: 'Tên đăng nhập', key: 'username', sortable: true },
  { title: 'Mô tả', key: 'description', sortable: false },
  { title: 'Vai trò', key: 'role', sortable: false },
  { title: 'Trạng thái', key: 'status', sortable: false },
  { title: 'Khóa', key: 'lock', sortable: false, align: 'end', width: '70px' }
]

// Danh sách vai trò để filter
const roleOptions = computed(() => {
  return Array.isArray(accountStore.roles) 
    ? accountStore.roles.map(role => ({
        title: role.description,
        value: role.id
      }))
    : []
})

// Danh sách trạng thái để filter
const statusOptions = [
  { title: 'Đang hoạt động', value: 'active' },
  { title: 'Không hoạt động', value: 'inactive' },
  { title: 'Đã khóa', value: 'locked' }
]

// Computed property kiểm tra xem có filter nào đang được áp dụng không
const hasFilters = computed(() => {
  return searchQuery.value || selectedRole.value || selectedStatus.value
})

// Lấy màu tương ứng với vai trò
function getRoleColor(roleName) {
  switch (roleName) {
    case 'MANAGER':
      return 'purple'
    case 'STAFF':
      return 'blue'
    case 'CUSTOMER':
      return 'green'
    case 'GUEST':
      return 'grey'
    default:
      return 'grey'
  }
}

// Tải danh sách tài khoản
async function loadAccounts() {
  try {
    await accountStore.fetchAccounts(page.value - 1, accountStore.pageSize)
  } catch (error) {
    showSnackbar('Không thể tải danh sách tài khoản.', 'error')
  }
}

// Xử lý khi chuyển trang
function handlePageChange(newPage) {
  page.value = newPage
  loadAccounts()
}

// Debounce tìm kiếm
let searchTimeout = null
function debounceSearch() {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    loadAccounts()
  }, 500)
}

// Reset các filter
function resetFilters() {
  searchQuery.value = ''
  selectedRole.value = null
  selectedStatus.value = null
  loadAccounts()
}

// Mở dialog thêm tài khoản
function openAddDialog() {
  editedAccount.value = {
    id: null,
    username: '',
    description: '',
    isActive: true,
    isLocked: false,
    role: Array.isArray(accountStore.roles) 
      ? accountStore.roles.find(r => r.name === 'CUSTOMER')
      : null,
    password: ''
  }
  addDialog.value = true
}

// Đóng dialog thêm tài khoản
function closeAddDialog() {
  addDialog.value = false
  if (addForm.value) {
    addForm.value.reset()
  }
}

// Mở dialog chỉnh sửa tài khoản
function openEditDialog(item) {
  editedIndex.value = accountStore.accounts.indexOf(item)
  editedAccount.value = { 
    ...item,
    // Đảm bảo role là object
    role: item.role && Array.isArray(accountStore.roles) 
      ? accountStore.roles.find(r => r.id === item.role.id) 
      : null,
    password: '' // Xóa mật khẩu khi chỉnh sửa
  }
  editDialog.value = true
}

// Đóng dialog chỉnh sửa tài khoản
function closeEditDialog() {
  editDialog.value = false
  editedIndex.value = -1
  if (editForm.value) {
    editForm.value.reset()
  }
}

// Mở dialog xác nhận xóa tài khoản
function openDeleteDialog(item) {
  editedAccount.value = { ...item }
  deleteDialog.value = true
}

// Đóng dialog xác nhận xóa tài khoản
function closeDeleteDialog() {
  deleteDialog.value = false
}

// Lưu tài khoản mới
async function saveAccount() {
  if (!addForm.value) return
  
  const { valid } = await addForm.value.validate()
  if (!valid) return
  
  try {
    // Chuẩn bị dữ liệu để gửi lên server
    const accountData = {
      username: editedAccount.value.username,
      password: editedAccount.value.password,
      description: editedAccount.value.description,
      isActive: editedAccount.value.isActive,
      isLocked: editedAccount.value.isLocked,
      roleId: editedAccount.value.role?.id
    }
    
    await accountStore.createAccount(accountData)
    showSnackbar('Tài khoản đã được tạo thành công.', 'success')
    closeAddDialog()
  } catch (error) {
    showSnackbar('Không thể tạo tài khoản mới. Vui lòng thử lại.', 'error')
  }
}

// Cập nhật tài khoản
async function updateAccount() {
  if (!editForm.value) return
  
  const { valid } = await editForm.value.validate()
  if (!valid) return
  
  try {
    // Chuẩn bị dữ liệu để gửi lên server
    const accountData = {
      username: editedAccount.value.username,
      description: editedAccount.value.description,
      isActive: editedAccount.value.isActive,
      isLocked: editedAccount.value.isLocked,
      roleId: editedAccount.value.role?.id
    }
    
    // Chỉ gửi password nếu người dùng đã nhập
    if (editedAccount.value.password) {
      accountData.password = editedAccount.value.password
    }
    
    await accountStore.updateAccount(editedAccount.value.id, accountData)
    showSnackbar('Tài khoản đã được cập nhật thành công.', 'success')
    closeEditDialog()
  } catch (error) {
    showSnackbar('Không thể cập nhật tài khoản. Vui lòng thử lại.', 'error')
  }
}

// Xóa tài khoản đã chọn
async function deleteSelectedAccount() {
  try {
    await accountStore.deleteAccount(editedAccount.value.id)
    showSnackbar('Tài khoản đã được xóa thành công.', 'success')
    closeDeleteDialog()
  } catch (error) {
    showSnackbar('Không thể xóa tài khoản. Vui lòng thử lại.', 'error')
  }
}

// Khóa/Mở khóa tài khoản
async function toggleLock(item) {
  try {
    await accountStore.toggleAccountLock(item.id, !item.isLocked)
    showSnackbar(
      `Tài khoản đã được ${item.isLocked ? 'mở khóa' : 'khóa'} thành công.`,
      'success'
    )
  } catch (error) {
    showSnackbar('Không thể thay đổi trạng thái khóa tài khoản. Vui lòng thử lại.', 'error')
  }
}

// Kích hoạt/Vô hiệu hóa tài khoản
async function toggleActive(item) {
  try {
    await accountStore.toggleAccountActive(item.id, !item.isActive)
    showSnackbar(
      `Tài khoản đã được ${item.isActive ? 'vô hiệu hóa' : 'kích hoạt'} thành công.`,
      'success'
    )
  } catch (error) {
    showSnackbar('Không thể thay đổi trạng thái kích hoạt tài khoản. Vui lòng thử lại.', 'error')
  }
}

// Hiển thị snackbar thông báo
function showSnackbar(message, color = 'success') {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

// Hook lifecycle - mounted
onMounted(async () => {
  // Tải danh sách vai trò trước
  try {
    await accountStore.fetchRoles()
  } catch (error) {
    showSnackbar('Không thể tải danh sách vai trò.', 'error')
  }
  
  // Sau đó tải danh sách tài khoản
  loadAccounts()
})
</script>

<style scoped>
.v-data-table ::v-deep(th) {
  font-weight: 600 !important;
  color: rgba(0, 0, 0, 0.87) !important;
}

.v-data-table ::v-deep(.v-data-table__tr:hover) {
  background-color: rgba(115, 103, 240, 0.04) !important;
}
</style> 