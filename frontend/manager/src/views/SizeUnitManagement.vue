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
        prepend-icon="mdi-ruler-square-compass" 
        @click="openAddDialog"
      >
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
      {{ sizeUnitStore.error }}
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
            :items="sizes"
            :loading="sizeUnitStore.loading"
            loading-text="Đang tải dữ liệu..."
            no-data-text="Không có dữ liệu"
            item-value="id"
            hover
            density="comfortable"
            class="elevation-0"
          >
            <!-- Cột STT -->
            <template v-slot:item.index="{ index }">
              {{ index + 1 }}
            </template>
            
            <!-- Cột Ký hiệu -->
            <template v-slot:item.symbol="{ item }">
              <v-chip
                color="primary"
                size="small"
                label
                class="font-weight-medium"
              >
                {{ item.symbol }}
              </v-chip>
            </template>
            
            <!-- Cột mặc định -->
            <template v-slot:item.isDefault="{ item }">
              <v-icon
                :icon="item.isDefault ? 'mdi-check-circle' : 'mdi-minus-circle'"
                :color="item.isDefault ? 'success' : 'grey'"
                size="small"
              ></v-icon>
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
                  @click="openEditSizeDialog(item)"
                ></v-btn>
                
                <!-- Nút xóa -->
                <v-btn
                  icon="mdi-delete"
                  size="small"
                  color="error"
                  variant="text"
                  @click="openDeleteSizeDialog(item)"
                ></v-btn>
              </div>
            </template>
          </v-data-table>
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
            :items="units"
            :loading="sizeUnitStore.loading"
            loading-text="Đang tải dữ liệu..."
            no-data-text="Không có dữ liệu"
            item-value="id"
            hover
            density="comfortable"
            class="elevation-0"
          >
            <!-- Cột STT -->
            <template v-slot:item.index="{ index }">
              {{ index + 1 }}
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
            
            <!-- Cột mặc định -->
            <template v-slot:item.isDefault="{ item }">
              <v-icon
                :icon="item.isDefault ? 'mdi-check-circle' : 'mdi-minus-circle'"
                :color="item.isDefault ? 'success' : 'grey'"
                size="small"
              ></v-icon>
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
                  @click="openEditUnitDialog(item)"
                ></v-btn>
                
                <!-- Nút xóa -->
                <v-btn
                  icon="mdi-delete"
                  size="small"
                  color="error"
                  variant="text"
                  @click="openDeleteUnitDialog(item)"
                ></v-btn>
              </div>
            </template>
          </v-data-table>
        </v-card>
      </v-window-item>
    </v-window>
    
    <!-- Dialog thêm mới -->
    <v-dialog v-model="addDialog" width="500" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          {{ dialogTitle }}
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="addForm" @submit.prevent="saveSizeUnit">
            <v-radio-group v-model="editedItem.type" class="mb-3">
              <v-radio
                label="Kích thước"
                value="SIZE"
              ></v-radio>
              <v-radio
                label="Đơn vị tính"
                value="UNIT"
              ></v-radio>
            </v-radio-group>
            
            <v-text-field
              v-model="editedItem.name"
              label="Tên"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập tên']"
              class="mb-3"
            ></v-text-field>
            
            <v-text-field
              v-model="editedItem.symbol"
              label="Ký hiệu"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập ký hiệu']"
              class="mb-3"
            ></v-text-field>
            
            <v-textarea
              v-model="editedItem.description"
              label="Mô tả"
              variant="outlined"
              auto-grow
              rows="3"
              class="mb-3"
            ></v-textarea>
            
            <v-switch
              v-model="editedItem.isDefault"
              label="Đặt làm mặc định"
              color="primary"
              hide-details
              class="mb-3"
            ></v-switch>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDialog">Hủy</v-btn>
          <v-btn 
            color="primary" 
            @click="saveSizeUnit" 
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
        <v-card-title class="text-h5 font-weight-medium pa-4">
          Xác nhận xóa
        </v-card-title>
        
        <v-card-text class="pa-4">
          Bạn có chắc chắn muốn xóa {{ isSize ? 'kích thước' : 'đơn vị tính' }} <strong>{{ editedItem.name }}</strong>?
          <p class="text-medium-emphasis mt-2">Lưu ý: Sản phẩm sử dụng {{ isSize ? 'kích thước' : 'đơn vị tính' }} này sẽ bị ảnh hưởng.</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDialog">Hủy</v-btn>
          <v-btn 
            color="error" 
            @click="deleteSelectedItem" 
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
        <v-btn variant="text" icon="mdi-close" @click="snackbar.show = false"></v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useSizeUnitStore } from '@/stores/sizeUnit'

// Khởi tạo store
const sizeUnitStore = useSizeUnitStore()

// Trạng thái local
const activeTab = ref('size')
const searchSizeQuery = ref('')
const searchUnitQuery = ref('')
const addDialog = ref(false)
const deleteDialog = ref(false)
const editMode = ref(false)
const editedItem = ref({
  id: null,
  name: '',
  symbol: '',
  description: '',
  type: 'SIZE',
  isDefault: false
})

const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})
const addForm = ref(null)

// Mẫu dữ liệu kích thước và đơn vị tính
const sizeUnitsData = ref([
  { id: 1, name: 'Nhỏ', symbol: 'S', description: 'Kích thước nhỏ', type: 'SIZE', isDefault: false },
  { id: 2, name: 'Vừa', symbol: 'M', description: 'Kích thước vừa', type: 'SIZE', isDefault: true },
  { id: 3, name: 'Lớn', symbol: 'L', description: 'Kích thước lớn', type: 'SIZE', isDefault: false },
  { id: 4, name: 'Cực lớn', symbol: 'XL', description: 'Kích thước cực lớn', type: 'SIZE', isDefault: false },
  { id: 5, name: 'Mililít', symbol: 'ml', description: 'Đơn vị đo dung tích', type: 'UNIT', isDefault: true },
  { id: 6, name: 'Gram', symbol: 'g', description: 'Đơn vị đo khối lượng', type: 'UNIT', isDefault: false },
  { id: 7, name: 'Gói', symbol: 'bag', description: 'Đơn vị đếm', type: 'UNIT', isDefault: false }
])

// Lọc ra danh sách kích thước
const sizes = computed(() => {
  return sizeUnitsData.value.filter(item => item.type === 'SIZE')
})

// Lọc ra danh sách đơn vị tính
const units = computed(() => {
  return sizeUnitsData.value.filter(item => item.type === 'UNIT')
})

// Cấu hình headers cho bảng kích thước
const sizeHeaders = [
  { title: 'STT', key: 'index', width: '80px', sortable: false },
  { title: 'Tên', key: 'name', align: 'start', sortable: true },
  { title: 'Ký hiệu', key: 'symbol', align: 'start', sortable: true, width: '100px' },
  { title: 'Mô tả', key: 'description', align: 'start', sortable: false },
  { title: 'Mặc định', key: 'isDefault', align: 'center', sortable: false, width: '100px' },
  { title: 'Hành động', key: 'actions', align: 'end', sortable: false, width: '120px' }
]

// Cấu hình headers cho bảng đơn vị tính
const unitHeaders = [
  { title: 'STT', key: 'index', width: '80px', sortable: false },
  { title: 'Tên', key: 'name', align: 'start', sortable: true },
  { title: 'Ký hiệu', key: 'symbol', align: 'start', sortable: true, width: '100px' },
  { title: 'Mô tả', key: 'description', align: 'start', sortable: false },
  { title: 'Mặc định', key: 'isDefault', align: 'center', sortable: false, width: '100px' },
  { title: 'Hành động', key: 'actions', align: 'end', sortable: false, width: '120px' }
]

// Kiểm tra xem đang làm việc với kích thước hay không
const isSize = computed(() => {
  return editedItem.value.type === 'SIZE'
})

// Tiêu đề dialog
const dialogTitle = computed(() => {
  if (editMode.value) {
    return `Chỉnh sửa ${isSize.value ? 'kích thước' : 'đơn vị tính'}`
  }
  return `Thêm ${isSize.value ? 'kích thước' : 'đơn vị tính'} mới`
})

// Tải danh sách kích thước và đơn vị tính
async function loadSizeUnits() {
  try {
    // Trong thực tế sẽ gọi API từ store
    // await sizeUnitStore.fetchSizeUnits()
    console.log('Đã tải danh sách kích thước và đơn vị tính')
  } catch (error) {
    showSnackbar('Không thể tải danh sách kích thước và đơn vị tính.', 'error')
  }
}

// Debounce tìm kiếm kích thước
let searchSizeTimeout = null
function debounceSizeSearch() {
  clearTimeout(searchSizeTimeout)
  searchSizeTimeout = setTimeout(() => {
    // Trong thực tế sẽ gọi API với tham số tìm kiếm
    console.log('Tìm kiếm kích thước:', searchSizeQuery.value)
  }, 500)
}

// Debounce tìm kiếm đơn vị tính
let searchUnitTimeout = null
function debounceUnitSearch() {
  clearTimeout(searchUnitTimeout)
  searchUnitTimeout = setTimeout(() => {
    // Trong thực tế sẽ gọi API với tham số tìm kiếm
    console.log('Tìm kiếm đơn vị tính:', searchUnitQuery.value)
  }, 500)
}

// Mở dialog thêm mới
function openAddDialog() {
  editMode.value = false
  editedItem.value = {
    id: null,
    name: '',
    symbol: '',
    description: '',
    type: activeTab.value === 'size' ? 'SIZE' : 'UNIT',
    isDefault: false
  }
  addDialog.value = true
}

// Mở dialog chỉnh sửa kích thước
function openEditSizeDialog(item) {
  editMode.value = true
  editedItem.value = { ...item }
  addDialog.value = true
}

// Mở dialog chỉnh sửa đơn vị tính
function openEditUnitDialog(item) {
  editMode.value = true
  editedItem.value = { ...item }
  addDialog.value = true
}

// Mở dialog xác nhận xóa kích thước
function openDeleteSizeDialog(item) {
  editedItem.value = { ...item }
  deleteDialog.value = true
}

// Mở dialog xác nhận xóa đơn vị tính
function openDeleteUnitDialog(item) {
  editedItem.value = { ...item }
  deleteDialog.value = true
}

// Đóng dialog
function closeDialog() {
  addDialog.value = false
  deleteDialog.value = false
  
  setTimeout(() => {
    editedItem.value = {
      id: null,
      name: '',
      symbol: '',
      description: '',
      type: activeTab.value === 'size' ? 'SIZE' : 'UNIT',
      isDefault: false
    }
  }, 300)
}

// Lưu kích thước/đơn vị tính
async function saveSizeUnit() {
  if (!addForm.value) return
  
  const { valid } = await addForm.value.validate()
  if (!valid) return
  
  try {
    // Trong thực tế sẽ gọi API từ store
    console.log('Lưu dữ liệu:', editedItem.value)
    
    if (editMode.value) {
      // Cập nhật dữ liệu hiện có
      const index = sizeUnitsData.value.findIndex(item => item.id === editedItem.value.id)
      if (index !== -1) {
        sizeUnitsData.value[index] = { ...editedItem.value }
      }
      showSnackbar(`${isSize.value ? 'Kích thước' : 'Đơn vị tính'} đã được cập nhật thành công.`, 'success')
    } else {
      // Thêm dữ liệu mới
      const newId = Math.max(...sizeUnitsData.value.map(item => item.id)) + 1
      sizeUnitsData.value.push({ ...editedItem.value, id: newId })
      showSnackbar(`${isSize.value ? 'Kích thước' : 'Đơn vị tính'} đã được tạo thành công.`, 'success')
    }
    
    // Nếu item mới được đặt làm mặc định, cập nhật các item khác
    if (editedItem.value.isDefault) {
      sizeUnitsData.value.forEach(item => {
        if (item.id !== editedItem.value.id && item.type === editedItem.value.type) {
          item.isDefault = false
        }
      })
    }
    
    closeDialog()
  } catch (error) {
    showSnackbar(`Không thể ${editMode.value ? 'cập nhật' : 'tạo'} ${isSize.value ? 'kích thước' : 'đơn vị tính'}. Vui lòng thử lại.`, 'error')
  }
}

// Xóa kích thước/đơn vị tính đã chọn
async function deleteSelectedItem() {
  try {
    // Trong thực tế sẽ gọi API từ store
    console.log('Xóa dữ liệu:', editedItem.value)
    
    const index = sizeUnitsData.value.findIndex(item => item.id === editedItem.value.id)
    if (index !== -1) {
      sizeUnitsData.value.splice(index, 1)
    }
    
    showSnackbar(`${isSize.value ? 'Kích thước' : 'Đơn vị tính'} đã được xóa thành công.`, 'success')
    closeDialog()
  } catch (error) {
    showSnackbar(`Không thể xóa ${isSize.value ? 'kích thước' : 'đơn vị tính'}. Vui lòng thử lại.`, 'error')
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
  loadSizeUnits()
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