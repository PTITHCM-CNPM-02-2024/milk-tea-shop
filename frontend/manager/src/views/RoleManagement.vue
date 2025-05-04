<template>
  <div>
    <!-- Phần header với tiêu đề và nút thêm mới -->
    <div class="d-flex justify-space-between align-center mb-6 px-4 pt-4">
      <div>
        <h2 class="text-h5 font-weight-bold">Quản lý vai trò</h2>
        <p class="text-subtitle-2 text-medium-emphasis mt-1">
          Quản lý danh sách vai trò người dùng trong hệ thống
        </p>
      </div>
      
      <v-btn 
        color="primary" 
        prepend-icon="mdi-shield-account" 
        @click="openAddDialog"
      >
        Thêm vai trò
      </v-btn>
    </div>
    
    <!-- Alert hiển thị lỗi nếu có -->
    <v-alert
      v-if="roleStore.error"
      type="error"
      variant="tonal"
      closable
      class="mx-4 mb-4"
    >
      {{ roleStore.error }}
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
      
      <v-spacer></v-spacer>
      
      <v-col cols="12" sm="2">
        <v-btn 
          color="primary" 
          variant="outlined" 
          block
          @click="resetFilters"
          :disabled="!searchQuery"
        >
          Đặt lại
        </v-btn>
      </v-col>
    </v-row>
    
    <!-- Bảng danh sách vai trò -->
    <v-data-table
      :headers="headers"
      :items="roleStore.roles"
      :loading="roleStore.loading"
      loading-text="Đang tải dữ liệu..."
      no-data-text="Không có dữ liệu"
      item-value="id"
      hover
      :items-per-page="roleStore.pageSize"
      density="comfortable"
      class="elevation-0"
    >
      <!-- Thêm template v-slot:item.id -->
      <template v-slot:item.id="{ item }">
        <span class="text-caption">#{{ item.id }}</span>
      </template>
      
      <!-- Cột Tên -->
      <template v-slot:item.name="{ item }">
        <v-chip
          :color="getRoleColor(item.name)"
          size="small"
          label
          class="text-white"
        >
          {{ item.name }}
        </v-chip>
      </template>
      
      <!-- Cột Hành động -->
      <template v-slot:item.actions="{ item }">
        <div class="d-flex gap-2">
          <!-- Nút chỉnh sửa -->
          <v-btn
            icon="mdi-pencil"
            size="small"
            color="primary"
            variant="text"
            @click="openEditDialog(item)"
          ></v-btn>
          
          <!-- Nút xóa -->
          <v-btn
            icon="mdi-delete"
            size="small"
            color="error"
            variant="text"
            @click="openDeleteDialog(item)"
            :disabled="isSystemRole(item.name)"
          ></v-btn>
        </div>
      </template>
    </v-data-table>
    
    <!-- Phân trang -->
    <div class="d-flex justify-center mt-4 pb-4">
      <v-pagination
        v-model="page"
        :length="roleStore.totalPages"
        total-visible="7"
        @update:model-value="handlePageChange"
      ></v-pagination>
    </div>
    
    <!-- Dialog thêm vai trò mới -->
    <v-dialog v-model="addDialog" width="500" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          Thêm vai trò mới
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="addForm" @submit.prevent="saveRole">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="addDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="addDialogError = null"
            >
              {{ addDialogError }}
            </v-alert>

            <v-text-field
              v-model="editedRole.name"
              label="Tên vai trò"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập tên vai trò']"
              class="mb-3"
            ></v-text-field>
            
            <v-text-field
              v-model="editedRole.description"
              label="Mô tả"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập mô tả vai trò']"
              class="mb-3"
            ></v-text-field>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeAddDialog">Đóng</v-btn>
          <v-btn 
            color="primary" 
            @click="saveRole" 
            :loading="roleStore.loading"
          >
            Lưu
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog chỉnh sửa vai trò -->
    <v-dialog v-model="editDialog" width="500" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          Chỉnh sửa vai trò
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="editForm" @submit.prevent="updateRole">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="editDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="editDialogError = null"
            >
              {{ editDialogError }}
            </v-alert>

            <v-text-field
              v-model="editedRole.name"
              label="Tên vai trò"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập tên vai trò']"
              class="mb-3"
              :disabled="isSystemRole(originalRole.name)"
            ></v-text-field>
            
            <v-text-field
              v-model="editedRole.description"
              label="Mô tả"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập mô tả vai trò']"
              class="mb-3"
            ></v-text-field>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeEditDialog">Đóng</v-btn>
          <v-btn 
            color="primary" 
            @click="updateRole" 
            :loading="roleStore.loading"
          >
            Cập nhật
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog xác nhận xóa vai trò -->
    <v-dialog v-model="deleteDialog" width="400" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-medium pa-4 bg-error text-white">
          Xác nhận xóa
        </v-card-title>
        
        <v-card-text class="pa-4 pt-5">
          Bạn có chắc chắn muốn xóa vai trò <strong>{{ editedRole.name }}</strong>?
          <p class="text-medium-emphasis mt-2">Hành động này không thể hoàn tác.</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDeleteDialog">Đóng</v-btn>
          <v-btn 
            color="error" 
            @click="deleteSelectedRole" 
            :loading="roleStore.loading"
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
import { ref, computed, onMounted } from 'vue'
import { useRoleStore } from '@/stores/role'

// Khởi tạo store
const roleStore = useRoleStore()

// Trạng thái local
const page = ref(1)
const searchQuery = ref('')
const addDialog = ref(false)
const editDialog = ref(false)
const deleteDialog = ref(false)
const editedIndex = ref(-1)
const editedRole = ref({
  id: null,
  name: '',
  description: ''
})
const originalRole = ref({
  name: ''
})
const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})
const addForm = ref(null)
const editForm = ref(null)

// Biến theo dõi lỗi dialog
const addDialogError = ref(null)
const editDialogError = ref(null)

// Cấu hình headers cho bảng
const headers = [
  { title: 'ID', key: 'id', width: '80px', sortable: true },
  { title: 'Tên vai trò', key: 'name', align: 'start', sortable: true },
  { title: 'Mô tả', key: 'description', align: 'start', sortable: false },
  { title: 'Hành động', key: 'actions', sortable: false, align: 'end', width: '120px' }
]

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
      return 'primary'
  }
}

// Kiểm tra xem có phải là vai trò hệ thống không (không thể xóa)
function isSystemRole(roleName) {
  return ['MANAGER', 'STAFF', 'CUSTOMER', 'GUEST'].includes(roleName)
}

// Tải danh sách vai trò
async function loadRoles() {
  try {
    await roleStore.fetchRoles(page.value - 1, roleStore.pageSize)
  } catch (error) {
    showSnackbar('Lỗi khi tải danh sách vai trò: ' + (error.response?.data?.detail || error.message || ''), 'error')
  }
}

// Xử lý khi chuyển trang
function handlePageChange(newPage) {
  page.value = newPage
  loadRoles()
}

// Debounce tìm kiếm
let searchTimeout = null
function debounceSearch() {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    loadRoles()
  }, 500)
}

// Reset các filter
function resetFilters() {
  searchQuery.value = ''
  loadRoles()
}

// Mở dialog thêm vai trò
function openAddDialog() {
  editedRole.value = {
    id: null,
    name: '',
    description: ''
  }
  addDialog.value = true
}

// Đóng dialog thêm vai trò
function closeAddDialog() {
  addDialog.value = false
  if (addForm.value) {
    addForm.value.reset()
  }
}

// Mở dialog chỉnh sửa vai trò
function openEditDialog(item) {
  editedIndex.value = roleStore.roles.indexOf(item)
  editedRole.value = { ...item }
  originalRole.value = { ...item }
  editDialog.value = true
}

// Đóng dialog chỉnh sửa vai trò
function closeEditDialog() {
  editDialog.value = false
  editedIndex.value = -1
  if (editForm.value) {
    editForm.value.reset()
  }
}

// Mở dialog xác nhận xóa vai trò
function openDeleteDialog(item) {
  editedRole.value = { ...item }
  deleteDialog.value = true
}

// Đóng dialog xác nhận xóa vai trò
function closeDeleteDialog() {
  deleteDialog.value = false
}

// Lưu vai trò mới
async function saveRole() {
  if (!addForm.value) return
  
  // Reset lỗi dialog
  addDialogError.value = null
  
  const { valid } = await addForm.value.validate()
  if (!valid) return
  
  try {
    // Chuẩn bị dữ liệu để gửi lên server
    const roleData = {
      name: editedRole.value.name,
      description: editedRole.value.description
    }
    
    await roleStore.createRole(roleData)
    showSnackbar('Vai trò đã được tạo thành công.', 'success')
    closeAddDialog()
  } catch (error) {
    console.error('Lỗi khi tạo vai trò:', error)
    addDialogError.value = error.response?.data?.message || 'Không thể tạo vai trò mới. Vui lòng thử lại'
    showSnackbar(error.response?.data || 'Không thể tạo vai trò mới. Vui lòng thử lại', 'error')
    // Không đóng dialog khi có lỗi
  }
}

// Cập nhật vai trò
async function updateRole() {
  if (!editForm.value) return
  
  // Reset lỗi dialog
  editDialogError.value = null
  
  const { valid } = await editForm.value.validate()
  if (!valid) return
  
  try {
    // Chuẩn bị dữ liệu để gửi lên server
    const roleData = {
      name: editedRole.value.name,
      description: editedRole.value.description
    }
    
    await roleStore.updateRole(editedRole.value.id, roleData)
    showSnackbar('Vai trò đã được cập nhật thành công.', 'success')
    closeEditDialog()
  } catch (error) {
    console.error('Lỗi khi cập nhật vai trò:', error)
    editDialogError.value = error.response?.data?.message || 'Không thể cập nhật vai trò. Vui lòng thử lại'
    showSnackbar(error.response?.data || 'Không thể cập nhật vai trò. Vui lòng thử lại', 'error')
    // Không đóng dialog khi có lỗi
  }
}

// Xóa vai trò đã chọn
async function deleteSelectedRole() {
  try {
    await roleStore.deleteRole(editedRole.value.id)
    showSnackbar('Vai trò đã được xóa thành công.', 'success')
    closeDeleteDialog()
  } catch (error) {
    console.error('Lỗi khi xóa vai trò:', error)
    showSnackbar(error.response?.data || 'Không thể xóa vai trò. Vui lòng thử lại', 'error')
    closeDeleteDialog()
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
onMounted(() => {
  loadRoles()
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