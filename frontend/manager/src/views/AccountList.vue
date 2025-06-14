<template>
  <div>
    <!-- Phần header với tiêu đề -->
    <div class="d-flex justify-space-between align-center mb-6 px-4 pt-4">
      <div>
        <h2 class="text-h5 font-weight-bold">Danh sách tài khoản</h2>
        <p class="text-subtitle-2 text-medium-emphasis mt-1">
          Quản lý thông tin tài khoản người dùng trong hệ thống
        </p>
      </div>
    </div>



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
      :items="filteredAccounts"
      :loading="accountStore.loading"
      loading-text="Đang tải dữ liệu..."
      no-data-text="Không có dữ liệu"
      item-value="id"
      hover
      :items-per-page="accountStore.pageSize"
      :page="page"
      @update:page="page = $event" 
      :items-per-page-options="[5, 10, 15, 20]"
      density="comfortable"
      class="elevation-0"
    >
      <!-- Thêm template v-slot:item.id -->
      <template v-slot:item.id="{ item }">
        <span class="text-caption">#{{ item.id }}</span>
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

      <!-- Cột Hành động -->
      <template v-slot:item.actions="{ item }">
        <v-btn
          icon="mdi-eye"
          size="small"
          color="primary"
          variant="text"
          @click="viewAccountDetail(item)"
          title="Xem chi tiết"
        ></v-btn>
      </template>
    </v-data-table>

    <!-- Phân trang -->
    <div class="d-flex justify-center mt-4 pb-4">
      <v-pagination
        v-model="page"
        :length="Math.ceil(filteredAccounts.length / accountStore.pageSize)"
        total-visible="7"
      ></v-pagination>
    </div>

  <!-- Dialog xem chi tiết tài khoản -->
  <v-dialog v-model="detailDialog" width="500">
    <v-card>
      <v-card-title class="text-h5 font-weight-bold pa-4">
        Chi tiết tài khoản
      </v-card-title>

      <v-divider></v-divider>

      <v-card-text class="pa-4" v-if="selectedAccount">
        <v-list>
          <v-list-item>
            <template v-slot:prepend>
              <v-icon color="primary" class="mr-2">mdi-account</v-icon>
            </template>
            <v-list-item-title>Tên đăng nhập</v-list-item-title>
            <v-list-item-subtitle>{{ selectedAccount.username }}</v-list-item-subtitle>
          </v-list-item>

          <v-list-item>
            <template v-slot:prepend>
              <v-icon color="primary" class="mr-2">mdi-text</v-icon>
            </template>
            <v-list-item-title>Mô tả</v-list-item-title>
            <v-list-item-subtitle>{{ selectedAccount.description || 'Không có mô tả' }}</v-list-item-subtitle>
          </v-list-item>

          <v-list-item>
            <template v-slot:prepend>
              <v-icon color="primary" class="mr-2">mdi-shield-account</v-icon>
            </template>
            <v-list-item-title>Vai trò</v-list-item-title>
            <v-list-item-subtitle>
              <v-chip
                :color="getRoleColor(selectedAccount.role?.name)"
                size="small"
                label
                class="text-white mt-1"
              >
                {{ selectedAccount.role?.description }}
              </v-chip>
            </v-list-item-subtitle>
          </v-list-item>

          <v-list-item>
            <template v-slot:prepend>
              <v-icon color="primary" class="mr-2">mdi-circle</v-icon>
            </template>
            <v-list-item-title>Trạng thái</v-list-item-title>
            <v-list-item-subtitle class="d-flex align-center mt-1">
              <v-icon
                :color="selectedAccount.isActive ? 'success' : 'error'"
                :icon="selectedAccount.isActive ? 'mdi-check-circle' : 'mdi-close-circle'"
                size="small"
                class="mr-1"
              ></v-icon>
              {{ selectedAccount.isActive ? 'Hoạt động' : 'Không hoạt động' }}
            </v-list-item-subtitle>
          </v-list-item>

          <v-list-item>
            <template v-slot:prepend>
              <v-icon color="primary" class="mr-2">mdi-lock</v-icon>
            </template>
            <v-list-item-title>Khóa</v-list-item-title>
            <v-list-item-subtitle class="d-flex align-center mt-1">
              <v-icon
                :icon="selectedAccount.isLocked ? 'mdi-lock' : 'mdi-lock-open'"
                size="small"
                :color="selectedAccount.isLocked ? 'warning' : 'success'"
                class="mr-1"
              ></v-icon>
              {{ selectedAccount.isLocked ? 'Đã khóa' : 'Mở khóa' }}
            </v-list-item-subtitle>
          </v-list-item>
        </v-list>
      </v-card-text>

      <v-divider></v-divider>

      <v-card-actions class="pa-4">
        <v-spacer></v-spacer>
        <v-btn color="primary" variant="text" @click="closeDetailDialog">Đóng</v-btn>
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
const detailDialog = ref(false)
const selectedAccount = ref(null)
const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

// Cấu hình headers cho bảng
const headers = [
  { title: 'ID', key: 'id', width: '80px', sortable: true },
  { title: 'Tên đăng nhập', key: 'username', align: 'start', sortable: true },
  { title: 'Mô tả', key: 'description', align: 'start', sortable: false },
  { title: 'Vai trò', key: 'role', sortable: false },
  { title: 'Trạng thái', key: 'status', sortable: false },
  { title: 'Hành động', key: 'actions', sortable: false, align: 'center', width: '100px' }
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

// Computed property cho danh sách tài khoản đã lọc
const filteredAccounts = computed(() => {
  let result = [...accountStore.accounts]
  
  // Lọc theo từ khóa tìm kiếm
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(account => 
      account.username?.toLowerCase().includes(query) || 
      account.description?.toLowerCase().includes(query)
    )
  }
  
  // Lọc theo vai trò
  if (selectedRole.value) {
    result = result.filter(account => account.role?.id === selectedRole.value)
  }
  
  // Lọc theo trạng thái
  if (selectedStatus.value) {
    if (selectedStatus.value === 'active') {
      result = result.filter(account => account.isActive === true && account.isLocked === false)
    } else if (selectedStatus.value === 'inactive') {
      result = result.filter(account => account.isActive === false)
    } else if (selectedStatus.value === 'locked') {
      result = result.filter(account => account.isLocked === true)
    }
  }
  
  return result
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
    const detailMessage = error.response?.data?.detail || 'Không thể tải danh sách tài khoản. Vui lòng thử lại.';
    showSnackbar(detailMessage, 'error');
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
    // Không cần gọi API, chỉ cập nhật UI
  }, 500)
}

// Reset các filter
function resetFilters() {
  searchQuery.value = ''
  selectedRole.value = null
  selectedStatus.value = null
}

// Xem chi tiết tài khoản
async function viewAccountDetail(account) {
  try {
    const accountDetail = await accountStore.fetchAccountById(account.id)
    selectedAccount.value = accountDetail
    detailDialog.value = true
  } catch (error) {
    const detailMessage = error.response?.data?.detail || 'Không thể tải chi tiết tài khoản. Vui lòng thử lại.';
    showSnackbar(detailMessage, 'error');
  }
}

// Đóng dialog xem chi tiết
function closeDetailDialog() {
  detailDialog.value = false
  selectedAccount.value = null
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
    // Không cần catch ở đây vì store không ném lại lỗi fetchRoles
    // Nhưng nếu store *có* ném lại lỗi, thì bạn sẽ xử lý UI ở đây
    // console.error('Lỗi khi tải danh sách vai trò:', error); // Xóa log nếu có
    // const detailMessage = error.response?.data?.detail || 'Không thể tải danh sách vai trò. Vui lòng thử lại.';
    // showSnackbar(detailMessage, 'error'); 
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
  cursor: pointer;
}
</style>
