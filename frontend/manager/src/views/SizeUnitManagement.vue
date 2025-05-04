<template>
  <div>
    <!-- Phần header với tiêu đề và nút thêm mới -->
    <div class="d-flex justify-space-between align-center mb-6 px-4 pt-4">
      <div>
        <h2 class="text-h5 font-weight-bold">Kích thước & Đơn vị tính</h2>
        <p class="text-subtitle-2 text-medium-emphasis mt-1">
          Quản lý kích thước sản phẩm và các đơn vị tính
        </p>
      </div>
      
      <v-btn 
        color="primary" 
        @click="openAddDialog"
      >
        <v-icon start>mdi-ruler-square-compass</v-icon>
        Thêm mới
      </v-btn>
    </div>
    
    <!-- Alert hiển thị lỗi nếu có -->
    <v-alert
      v-if="sizeUnitStore.error"
      type="error"
      variant="tonal"
      closable
      class="mx-4 mb-4"
    >
      {{ sizeUnitStore.error?.detail }}
    </v-alert>
    
    <!-- Tabs cho kích thước và đơn vị tính -->
    <v-tabs v-model="activeTab" color="primary" align-tabs="start" class="px-4 mb-4">
      <v-tab value="size">
        <v-icon start>mdi-ruler</v-icon>
        Kích thước
      </v-tab>
      <v-tab value="unit">
        <v-icon start>mdi-scale-balance</v-icon>
        Đơn vị tính
      </v-tab>
    </v-tabs>
    
    <v-window v-model="activeTab" class="px-4">
      <!-- Tab Kích thước -->
      <v-window-item value="size">
        <v-card class="mb-4 pa-4">
          <div class="d-flex align-center mb-4">
            <h3 class="text-subtitle-1 font-weight-bold">Danh sách kích thước</h3>
            <v-spacer></v-spacer>
            <v-text-field
              v-model="searchSizeQuery"
              label="Tìm kiếm"
              density="compact"
              variant="outlined"
              hide-details
              prepend-inner-icon="mdi-magnify"
              clearable
              class="max-width-200"
              @update:model-value="debounceSizeSearch"
            ></v-text-field>
          </div>
          
          <v-data-table
            :headers="sizeHeaders"
            :items="sizeUnitStore.productSizes"
            :loading="sizeUnitStore.loading"
            loading-text="Đang tải dữ liệu..."
            no-data-text="Không có dữ liệu"
            item-value="id"
            hover
            density="comfortable"
            class="elevation-0"
          >
            <!-- Thêm template v-slot:item.id -->
            <template v-slot:item.id="{ item }">
              <span class="text-caption">#{{ item.id }}</span>
            </template>
            
            <!-- Cột Đơn vị tính -->
            <template v-slot:item.unitId="{ item }">
              {{ getUnitNameById(item.unitId) }}
            </template>
            
            <!-- Cột Hành động -->
            <template v-slot:item.actions="{ item }">
              <div class="d-flex gap-2">
                <!-- Nút chỉnh sửa -->
                <v-btn
                  size="small"
                  color="primary"
                  variant="text"
                  @click="openEditSizeDialog(item)"
                >
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                
                <!-- Nút xóa -->
                <v-btn
                  size="small"
                  color="error"
                  variant="text"
                  @click="openDeleteSizeDialog(item)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </div>
            </template>
          </v-data-table>
          
          <!-- Phân trang cho kích thước -->
          <div class="d-flex justify-center mt-4">
            <v-pagination
              v-if="sizeUnitStore.totalProductSizes > 0"
              v-model="sizePage"
              :length="sizeUnitStore.totalSizePages"
              :total-visible="7"
              @update:model-value="handleSizePageChange"
            ></v-pagination>
          </div>
        </v-card>
      </v-window-item>
      
      <!-- Tab Đơn vị tính -->
      <v-window-item value="unit">
        <v-card class="mb-4 pa-4">
          <div class="d-flex align-center mb-4">
            <h3 class="text-subtitle-1 font-weight-bold">Danh sách đơn vị tính</h3>
            <v-spacer></v-spacer>
            <v-text-field
              v-model="searchUnitQuery"
              label="Tìm kiếm"
              density="compact"
              variant="outlined"
              hide-details
              prepend-inner-icon="mdi-magnify"
              clearable
              class="max-width-200"
              @update:model-value="debounceUnitSearch"
            ></v-text-field>
          </div>
          
          <v-data-table
            :headers="unitHeaders"
            :items="sizeUnitStore.units"
            :loading="sizeUnitStore.loading"
            loading-text="Đang tải dữ liệu..."
            no-data-text="Không có dữ liệu"
            item-value="id"
            hover
            density="comfortable"
            class="elevation-0"
          >
            <!-- Thêm template v-slot:item.id -->
            <template v-slot:item.id="{ item }">
              <span class="text-caption">#{{ item.id }}</span>
            </template>
            
            <!-- Cột Ký hiệu -->
            <template v-slot:item.symbol="{ item }">
              <v-chip
                color="info"
                size="small"
                label
                class="font-weight-medium"
              >
                {{ item.symbol }}
              </v-chip>
            </template>
            
            <!-- Cột Hành động -->
            <template v-slot:item.actions="{ item }">
              <div class="d-flex gap-2">
                <!-- Nút chỉnh sửa -->
                <v-btn
                  size="small"
                  color="primary"
                  variant="text"
                  @click="openEditUnitDialog(item)"
                >
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                
                <!-- Nút xóa -->
                <v-btn
                  size="small"
                  color="error"
                  variant="text"
                  @click="openDeleteUnitDialog(item)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </div>
            </template>
          </v-data-table>
          
          <!-- Phân trang cho đơn vị tính -->
          <div class="d-flex justify-center mt-4">
            <v-pagination
              v-if="sizeUnitStore.totalUnits > 0"
              v-model="unitPage"
              :length="sizeUnitStore.totalUnitPages"
              :total-visible="7"
              @update:model-value="handleUnitPageChange"
            ></v-pagination>
          </div>
        </v-card>
      </v-window-item>
    </v-window>
    
    <!-- Dialog thêm/chỉnh sửa kích thước -->
    <v-dialog v-model="sizeDialog" width="500" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          {{ editMode ? 'Chỉnh sửa kích thước' : 'Thêm kích thước mới' }}
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="sizeForm" @submit.prevent="saveSizeData">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="sizeDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="sizeDialogError = null"
            >
              {{ sizeDialogError }}
            </v-alert>

            <v-text-field
              v-model="editedSize.name"
              label="Tên kích thước"
              variant="outlined"
              required
              :rules="[
                v => !!v || 'Vui lòng nhập tên kích thước',
                v => (v && v.length >= 1 && v.length <= 5) || 'Tên kích thước phải từ 1 đến 5 ký tự',
                v => /^[a-zA-Z0-9]+$/.test(v) || 'Chỉ chứa chữ cái và số'
              ]"
              class="mb-3"
            ></v-text-field>
            
            <v-textarea
              v-model="editedSize.description"
              label="Mô tả"
              variant="outlined"
              rows="3"
              auto-grow
              class="mb-3"
            ></v-textarea>
            
            <v-select
              v-model="editedSize.unitId"
              :items="unitsForSelect"
              label="Đơn vị tính"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng chọn đơn vị tính']"
              class="mb-3"
            ></v-select>
            
            <v-text-field
              v-model="editedSize.quantity"
              label="Số lượng"
              variant="outlined"
              required
              type="number"
              :rules="[v => !!v || 'Vui lòng nhập số lượng']"
              class="mb-3"
            ></v-text-field>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeSizeDialog">Đóng</v-btn>
          <v-btn 
            color="primary" 
            @click="saveSizeData" 
            :loading="sizeUnitStore.loading"
          >
            Lưu
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog thêm/chỉnh sửa đơn vị tính -->
    <v-dialog v-model="unitDialog" width="500" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          {{ editMode ? 'Chỉnh sửa đơn vị tính' : 'Thêm đơn vị tính mới' }}
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="unitForm" @submit.prevent="saveUnitData">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="unitDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="unitDialogError = null"
            >
              {{ unitDialogError }}
            </v-alert>

            <v-text-field
              v-model="editedUnit.name"
              label="Tên đơn vị tính"
              variant="outlined"
              required
              :rules="[
                v => !!v || 'Vui lòng nhập tên đơn vị tính',
                v => (v && v.length >= 1 && v.length <= 30) || 'Tên đơn vị tính phải từ 1 đến 30 ký tự',
                v => /^[a-zA-ZÀ-ỹ\s]+$/.test(v) || 'Chỉ chứa chữ cái và khoảng trắng'
              ]"
              class="mb-3"
            ></v-text-field>
            
            <v-textarea
              v-model="editedUnit.description"
              label="Mô tả"
              variant="outlined"
              rows="3"
              auto-grow
              class="mb-3"
            ></v-textarea>
            
            <v-text-field
              v-model="editedUnit.symbol"
              label="Ký hiệu"
              variant="outlined"
              required
              :rules="[
                v => !!v || 'Vui lòng nhập ký hiệu',
                v => (v && v.length >= 1 && v.length <= 5) || 'Ký hiệu phải từ 1 đến 5 ký tự'
              ]"
              class="mb-3"
            ></v-text-field>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeUnitDialog">Đóng</v-btn>
          <v-btn 
            color="primary" 
            @click="saveUnitData" 
            :loading="sizeUnitStore.loading"
          >
            Lưu
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog xác nhận xóa -->
    <v-dialog v-model="deleteDialog" width="400">
      <v-card>
        <v-card-title class="text-h5 font-weight-medium pa-4 bg-error text-white">
          Xác nhận xóa
        </v-card-title>
        
        <v-card-text class="pa-4 text-justify text-wrap">
          <p v-if="activeTab === 'size'">
            Bạn có chắc chắn muốn xóa kích thước <strong>{{ editedSize.name }}</strong>?
          </p>
          <p v-else>
            Bạn có chắc chắn muốn xóa đơn vị tính <strong>{{ editedUnit.name }}</strong>?
          </p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDeleteDialog">Đóng</v-btn>
          <v-btn 
            color="error" 
            @click="deleteItem" 
            :loading="sizeUnitStore.loading"
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
        <v-btn variant="text" @click="snackbar.show = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useSizeUnitStore } from '@/stores/sizeUnit'

// Khởi tạo store
const sizeUnitStore = useSizeUnitStore()

// Trạng thái local
const activeTab = ref('size')
const searchSizeQuery = ref('')
const searchUnitQuery = ref('')
const sizePage = ref(1)
const unitPage = ref(1)
const sizeDialog = ref(false)
const unitDialog = ref(false)
const deleteDialog = ref(false)
const editMode = ref(false)

// Đối tượng đang được chỉnh sửa
const editedSize = ref({
  id: null,
  name: '',
  description: '',
  unitId: null,
  quantity: 0
})

const editedUnit = ref({
  id: null,
  name: '',
  description: '',
  symbol: ''
})

const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

// Biến theo dõi lỗi dialog
const sizeDialogError = ref(null)
const unitDialogError = ref(null)

const sizeForm = ref(null)
const unitForm = ref(null)

// Cấu hình headers cho bảng kích thước
const sizeHeaders = [
  { title: 'ID', key: 'id', width: '80px', sortable: true },
  { title: 'Tên', key: 'name', align: 'start', sortable: true },
  { title: 'Mô tả', key: 'description', align: 'start', sortable: false },
  { title: 'Đơn vị tính', key: 'unitId', align: 'start', sortable: false },
  { title: 'Số lượng', key: 'quantity', align: 'start', sortable: true, width: '100px' },
  { title: 'Hành động', key: 'actions', align: 'end', sortable: false, width: '120px' }
]

// Cấu hình headers cho bảng đơn vị tính
const unitHeaders = [
  { title: 'ID', key: 'id', width: '80px', sortable: true },
  { title: 'Tên', key: 'name', align: 'start', sortable: true },
  { title: 'Mô tả', key: 'description', align: 'start', sortable: false },
  { title: 'Ký hiệu', key: 'symbol', align: 'start', sortable: true, width: '100px' },
  { title: 'Hành động', key: 'actions', align: 'end', sortable: false, width: '120px' }
]

// Danh sách đơn vị tính cho select dropdown
const unitsForSelect = computed(() => {
  return sizeUnitStore.units.map(unit => ({
    title: `${unit.name} (${unit.symbol})`,
    value: unit.id
  }))
})

// Lấy tên đơn vị tính theo ID
function getUnitNameById(unitId) {
  const unit = sizeUnitStore.units.find(u => u.id === unitId)
  return unit ? `${unit.name} (${unit.symbol})` : 'Không xác định'
}

// Tải dữ liệu
async function loadData() {
  if (activeTab.value === 'size') {
    await loadSizes()
  } else {
    await loadUnits()
  }
}

// Tải danh sách kích thước
async function loadSizes() {
  try {
    await sizeUnitStore.fetchProductSizes(sizePage.value - 1, 10)
  } catch (error) {
    console.error('Lỗi khi tải danh sách kích thước:', error)
  }
}

// Tải danh sách đơn vị tính
async function loadUnits() {
  try {
    await sizeUnitStore.fetchUnits(unitPage.value - 1, 10)
  } catch (error) {
    console.error('Lỗi khi tải danh sách đơn vị tính:', error)
  }
}

// Debounce tìm kiếm kích thước
let searchSizeTimeout = null
function debounceSizeSearch() {
  clearTimeout(searchSizeTimeout)
  searchSizeTimeout = setTimeout(() => {
    // Thực hiện tìm kiếm
    loadSizes()
  }, 500)
}

// Debounce tìm kiếm đơn vị tính
let searchUnitTimeout = null
function debounceUnitSearch() {
  clearTimeout(searchUnitTimeout)
  searchUnitTimeout = setTimeout(() => {
    // Thực hiện tìm kiếm
    loadUnits()
  }, 500)
}

// Mở dialog thêm mới
function openAddDialog() {
  editMode.value = false
  
  if (activeTab.value === 'size') {
    editedSize.value = {
      id: null,
      name: '',
      description: '',
      unitId: null,
      quantity: 0
    }
    sizeDialog.value = true
  } else {
    editedUnit.value = {
      id: null,
      name: '',
      description: '',
      symbol: ''
    }
    unitDialog.value = true
  }
}

// Mở dialog chỉnh sửa kích thước
function openEditSizeDialog(item) {
  editMode.value = true
  editedSize.value = { ...item }
  sizeDialog.value = true
}

// Mở dialog chỉnh sửa đơn vị tính
function openEditUnitDialog(item) {
  console.log(item) 
  editMode.value = true
  editedUnit.value = { ...item }
  unitDialog.value = true
}

// Mở dialog xác nhận xóa kích thước
function openDeleteSizeDialog(item) {
  editedSize.value = { ...item }
  activeTab.value = 'size'
  deleteDialog.value = true
}

// Mở dialog xác nhận xóa đơn vị tính
function openDeleteUnitDialog(item) {
  editedUnit.value = { ...item }
  activeTab.value = 'unit'
  deleteDialog.value = true
}

// Đóng dialog kích thước
function closeSizeDialog() {
  sizeDialog.value = false
}

// Đóng dialog đơn vị tính
function closeUnitDialog() {
  unitDialog.value = false
}

// Đóng dialog xác nhận xóa
function closeDeleteDialog() {
  deleteDialog.value = false
}

// Lưu dữ liệu kích thước
async function saveSizeData() {
  if (!sizeForm.value) return
  
  // Reset lỗi dialog
  sizeDialogError.value = null
  
  const { valid } = await sizeForm.value.validate()
  if (!valid) return
  
  try {
    if (editMode.value) {
      await sizeUnitStore.updateProductSize(editedSize.value.id, editedSize.value)
      showSnackbar('Kích thước đã được cập nhật thành công', 'success')
      closeSizeDialog()
    } else {
      await sizeUnitStore.createProductSize(editedSize.value)
      showSnackbar('Kích thước đã được tạo thành công', 'success')
      closeSizeDialog()
    }
    
    loadSizes()
  } catch (error) {
    console.error('Lỗi khi lưu kích thước:', error)
    sizeDialogError.value = error.response?.data || 'Đã xảy ra lỗi khi lưu kích thước'
    showSnackbar('Đã xảy ra lỗi: ' + error.response.data?.detail, 'error')
    // Không đóng dialog khi có lỗi
  }
}

// Lưu dữ liệu đơn vị tính
async function saveUnitData() {
  if (!unitForm.value) return
  
  // Reset lỗi dialog
  unitDialogError.value = null
  
  const { valid } = await unitForm.value.validate()
  if (!valid) return
  
  try {
    if (editMode.value) {
      await sizeUnitStore.updateUnit(editedUnit.value.id, editedUnit.value)
      showSnackbar('Đơn vị tính đã được cập nhật thành công', 'success')
      closeUnitDialog()
    } else {
      await sizeUnitStore.createUnit(editedUnit.value)
      showSnackbar('Đơn vị tính đã được tạo thành công', 'success')
      closeUnitDialog()
    }
    
    loadUnits()
    
    // Nếu đã thêm đơn vị tính mới và đang ở tab kích thước, cập nhật danh sách đơn vị tính cho dropdown
    if (activeTab.value === 'size') {
      await sizeUnitStore.fetchUnits(0, 100)
    }
  } catch (error) {
    console.error('Lỗi khi lưu đơn vị tính:', error)
    unitDialogError.value = error.response?.data || 'Đã xảy ra lỗi khi lưu đơn vị tính'
    showSnackbar('Đã xảy ra lỗi: ' + error.response.data?.detail, 'error')
    // Không đóng dialog khi có lỗi
  }
}

// Xóa item (kích thước hoặc đơn vị tính)
async function deleteItem() {
  try {
    if (activeTab.value === 'size') {
      await sizeUnitStore.deleteProductSize(editedSize.value.id)
      showSnackbar('Kích thước đã được xóa thành công', 'success')
      loadSizes()
    } else {
      await sizeUnitStore.deleteUnit(editedUnit.value.id)
      showSnackbar('Đơn vị tính đã được xóa thành công', 'success')
      loadUnits()
    }
    
    closeDeleteDialog()
  } catch (error) {
    showSnackbar('Đã xảy ra lỗi: ' + error.response.data?.detail, 'error')
  }
}

// Xử lý thay đổi trang kích thước
function handleSizePageChange(page) {
  sizePage.value = page
  loadSizes()
}

// Xử lý thay đổi trang đơn vị tính
function handleUnitPageChange(page) {
  unitPage.value = page
  loadUnits()
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
  // Tải đơn vị tính trước để có thể hiển thị trong bảng kích thước
  await sizeUnitStore.fetchUnits(0, 100)
  loadData()
})

// Theo dõi thay đổi tab
watch(activeTab, () => {
  loadData()
})
</script>

<style scoped>
.max-width-200 {
  max-width: 200px;
}

.v-data-table ::v-deep(th) {
  font-weight: 600 !important;
  color: rgba(0, 0, 0, 0.87) !important;
}

.v-data-table ::v-deep(.v-data-table__tr:hover) {
  background-color: rgba(115, 103, 240, 0.04) !important;
}
</style> 