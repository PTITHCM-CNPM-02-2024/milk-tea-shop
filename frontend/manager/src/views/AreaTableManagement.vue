<template>
  <dashboard-layout>
    <div>
      <!-- Phần header với tiêu đề -->
      <div class="d-flex justify-space-between align-center mb-4 px-4 pt-4">
        <div>
          <h2 class="text-h5 font-weight-bold">Quản lý khu vực và bàn</h2>
          <p class="text-subtitle-2 text-medium-emphasis mt-1">
            Quản lý thông tin khu vực và bàn trong hệ thống
          </p>
        </div>
      </div>
      
      <!-- Alert hiển thị lỗi nếu có -->
      <v-alert
        v-if="areaTableStore.error"
        type="error"
        variant="tonal"
        closable
        class="mx-4 mb-4"
      >
        {{ areaTableStore.error }}
      </v-alert>
      
      <v-row class="px-2">
        <!-- Phần Danh sách khu vực -->
        <v-col cols="12" md="4">
          <v-card class="mb-4">
            <v-card-title class="d-flex justify-space-between align-center py-3 px-4">
              <span class="text-h6">Khu vực</span>
              <v-btn 
                color="primary" 
                size="small"
                variant="text"
                @click="openAddAreaDialog"
              >
                <v-icon start>mdi-plus</v-icon>
                Thêm mới
              </v-btn>
            </v-card-title>
            
            <v-divider></v-divider>
            
            <v-card-text class="pa-2">
              <v-list lines="two" class="py-0">
                <v-list-item
                  v-for="area in areaTableStore.areas"
                  :key="area.id"
                  :title="area.name"
                  :subtitle="area.description || 'Không có mô tả'"
                  :value="area.id"
                  :active="selectedAreaId === area.id"
                  rounded="lg"
                  class="mb-1"
                  @click="selectArea(area)"
                >
                  <template v-slot:prepend>
                    <v-avatar :color="area.isActive ? 'success' : 'grey-darken-1'" size="36">
                      <span class="text-h7 font-weight-medium">{{ area.name}}</span>
                    </v-avatar>
                  </template>
                  
                  <template v-slot:append>
                    <div class="d-flex gap-1">
                      <v-btn
                        size="x-small"
                        color="primary"
                        variant="text"
                        @click.stop="openEditAreaDialog(area)"
                      >
                        <v-icon>mdi-pencil</v-icon>
                      </v-btn>
                      <v-btn
                        size="x-small"
                        color="error"
                        variant="text"
                        @click.stop="openDeleteAreaDialog(area)"
                      >
                        <v-icon>mdi-delete</v-icon>
                      </v-btn>
                    </div>
                  </template>
                </v-list-item>
                
                <!-- Hiển thị khi không có dữ liệu -->
                <div v-if="areaTableStore.areas.length === 0 && !areaTableStore.loading" class="text-center py-6">
                  <v-icon size="x-large" class="mb-2 text-medium-emphasis">mdi-map-marker-radius</v-icon>
                  <div class="text-subtitle-1 text-medium-emphasis">Không có khu vực nào</div>
                  <v-btn color="primary" size="small" class="mt-2" @click="openAddAreaDialog">
                    <v-icon start>mdi-plus</v-icon>
                    Thêm khu vực
                  </v-btn>
                </div>
                
                <!-- Loading skeleton -->
                <div v-if="areaTableStore.loading" class="pa-4">
                  <v-skeleton-loader type="list-item-avatar-two-line" class="mb-2"></v-skeleton-loader>
                  <v-skeleton-loader type="list-item-avatar-two-line" class="mb-2"></v-skeleton-loader>
                  <v-skeleton-loader type="list-item-avatar-two-line"></v-skeleton-loader>
                </div>
              </v-list>
            </v-card-text>
          </v-card>
        </v-col>
        
        <!-- Phần Danh sách bàn -->
        <v-col cols="12" md="8">
          <v-card>
            <v-card-title class="d-flex justify-space-between align-center py-3 px-4">
              <span class="text-h6">
                Danh sách bàn 
                <span v-if="selectedAreaName" class="text-subtitle-1 ml-2">
                  - {{ selectedAreaName }}
                </span>
                <span v-else class="text-subtitle-1 ml-2">
                  - Tất cả bàn
                </span>
              </span>
              <div>
                <v-btn 
                  color="info" 
                  size="small"
                  variant="text"
                  class="mr-2"
                  @click="loadAllTables"
                  :disabled="areaTableStore.loading"
                >
                  <v-icon start>mdi-refresh</v-icon>
                  Tất cả
                </v-btn>
                <v-btn 
                  color="primary" 
                  size="small"
                  variant="text"
                  @click="openAddTableDialog"
                >
                  <v-icon start>mdi-table-plus</v-icon>
                  Thêm mới
                </v-btn>
              </div>
            </v-card-title>
            
            <v-divider></v-divider>
            
            <v-card-text class="pa-2">
              <!-- Grid hiển thị bàn -->
              <div class="d-flex flex-wrap gap-2 pa-2">
                <v-card
                  v-for="table in areaTableStore.tables"
                  :key="table.id"
                  width="120"
                  height="120"
                  class="table-card"
                  :color="table.isActive ? (table.areaId ? 'success-lighten-4' : 'blue-lighten-4') : 'grey-lighten-3'"
                  @click="showTableDetails(table)"
                >
                  <v-card-text class="d-flex flex-column align-center justify-center pa-2 h-100">
                    <v-icon size="large" :color="table.isActive ? 'success' : 'grey-darken-1'">mdi-table-furniture</v-icon>
                    <span class="text-h6 mt-2">{{ table.name }}</span>
                    <div class="d-flex mt-2">
                      <v-icon 
                        size="x-small" 
                        :color="table.isActive ? 'success' : 'grey-darken-1'"
                        class="mr-1"
                      >mdi-circle</v-icon>
                      <span class="text-caption" v-if="table.areaId">{{ table.isActive ? 'Hoạt động' : 'Không hoạt động' }}</span>
                      <span class="text-caption" v-else>Chưa gán khu vực</span>
                    </div>
                  </v-card-text>
                </v-card>
                
                <!-- Hiển thị khi không có bàn -->
                <div 
                  v-if="areaTableStore.tables.length === 0 && !areaTableStore.loading"
                  class="d-flex flex-column align-center justify-center pa-6 w-100"
                >
                  <v-icon size="x-large" class="mb-2 text-medium-emphasis">mdi-table-furniture</v-icon>
                  <div class="text-subtitle-1 text-medium-emphasis text-center">
                    {{ selectedAreaId ? 'Không có bàn nào trong khu vực này' : 'Không có bàn nào trong hệ thống' }}
                  </div>
                  <v-btn 
                    v-if="selectedAreaId" 
                    color="primary" 
                    size="small" 
                    class="mt-2" 
                    @click="openAddTableDialog"
                  >
                    <v-icon start>mdi-table-plus</v-icon>
                    Thêm bàn
                  </v-btn>
                </div>
                
                <!-- Loading skeleton -->
                <template v-if="areaTableStore.loading">
                  <v-skeleton-loader
                    v-for="i in 8"
                    :key="i"
                    type="card"
                    width="120"
                    height="120"
                    class="ma-1"
                  ></v-skeleton-loader>
                </template>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
      
      <!-- Dialog thêm/sửa khu vực -->
      <v-dialog v-model="areaDialog" width="500" persistent>
        <v-card>
          <v-card-title class="text-h5 font-weight-bold pa-4">
            {{ isEditingArea ? 'Chỉnh sửa khu vực' : 'Thêm khu vực mới' }}
          </v-card-title>
          
          <v-divider></v-divider>
          
          <v-card-text class="pa-4">
            <v-form ref="areaForm" @submit.prevent="submitAreaForm">
              <v-text-field
                v-model="areaFormData.name"
                label="Tên khu vực"
                variant="outlined"
                required
                class="mb-3"
                :rules="[
                  v => !!v || 'Tên khu vực là bắt buộc',
                  v => v.length == 3 || 'Tên khu vực phải có đúng 3 ký tự'
                ]"
              ></v-text-field>
              
              <v-textarea
                v-model="areaFormData.description"
                label="Mô tả"
                variant="outlined"
                class="mb-3"
                rows="3"
                auto-grow
              ></v-textarea>
              
              <v-text-field
                v-model.number="areaFormData.maxTable"
                label="Số bàn tối đa"
                type="number"
                variant="outlined"
                class="mb-3"
                hint="Để trống nếu không giới hạn"
                :rules="[v => !v || v > 0 || v < 100 || 'Số bàn tối đa phải lớn hơn 0 và nhỏ hơn 100']"
              ></v-text-field>
              
              <v-switch
                v-model="areaFormData.isActive"
                label="Kích hoạt khu vực"
                color="primary"
                hide-details
                class="mb-3"
              ></v-switch>
            </v-form>
          </v-card-text>
          
          <v-divider></v-divider>
          
          <v-card-actions class="pa-4">
            <v-spacer></v-spacer>
            <v-btn variant="text" @click="areaDialog = false">Hủy</v-btn>
            <v-btn 
              color="primary" 
              @click="submitAreaForm"
              :loading="areaTableStore.loading"
            >
              {{ isEditingArea ? 'Cập nhật' : 'Thêm mới' }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      
      <!-- Dialog xóa khu vực -->
      <v-dialog v-model="deleteAreaDialog" width="400" persistent>
        <v-card>
          <v-card-title class="text-h5 font-weight-medium pa-4">
            Xác nhận xóa khu vực
          </v-card-title>
          
          <v-card-text class="pa-4">
            <p>Bạn có chắc chắn muốn xóa khu vực "<strong>{{ selectedAreaToDelete?.name }}</strong>" không?</p>
            <p class="text-red mt-2">Lưu ý: Tất cả bàn trong khu vực này cũng sẽ bị xóa!</p>
          </v-card-text>
          
          <v-divider></v-divider>
          
          <v-card-actions class="pa-4">
            <v-spacer></v-spacer>
            <v-btn variant="text" @click="deleteAreaDialog = false">Hủy</v-btn>
            <v-btn 
              color="error" 
              @click="confirmDeleteArea"
              :loading="areaTableStore.loading"
            >
              Xóa
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      
      <!-- Dialog thêm/sửa bàn -->
      <v-dialog v-model="tableDialog" width="500" persistent>
        <v-card>
          <v-card-title class="text-h5 font-weight-bold pa-4">
            {{ isEditingTable ? 'Chỉnh sửa bàn' : 'Thêm bàn mới' }}
          </v-card-title>
          
          <v-divider></v-divider>
          
          <v-card-text class="pa-4">
            <v-form ref="tableForm" @submit.prevent="submitTableForm">
              <v-text-field
                v-model="tableFormData.name"
                label="Số bàn"
                variant="outlined"
                required
                class="mb-3"
                :rules="[v => !!v || 'Số bàn là bắt buộc']"
              ></v-text-field>
              
              <v-select
                v-model="tableFormData.areaId"
                :items="areaItems"
                label="Khu vực"
                variant="outlined"
                required
                class="mb-3"
                :rules="[v => !!v || 'Khu vực là bắt buộc']"
                return-object
                item-title="title"
                item-value="value"
              ></v-select>
              
              <v-switch
                v-model="tableFormData.isActive"
                label="Kích hoạt bàn"
                color="primary"
                hide-details
                class="mb-3"
              ></v-switch>
            </v-form>
          </v-card-text>
          
          <v-divider></v-divider>
          
          <v-card-actions class="pa-4">
            <v-spacer></v-spacer>
            <v-btn variant="text" @click="tableDialog = false">Hủy</v-btn>
            <v-btn 
              color="primary" 
              @click="submitTableForm"
              :loading="areaTableStore.loading"
            >
              {{ isEditingTable ? 'Cập nhật' : 'Thêm mới' }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      
      <!-- Dialog xóa bàn -->
      <v-dialog v-model="deleteTableDialog" width="400" persistent>
        <v-card>
          <v-card-title class="text-h5 font-weight-medium pa-4">
            Xác nhận xóa bàn
          </v-card-title>
          
          <v-card-text class="pa-4">
            <p>Bạn có chắc chắn muốn xóa bàn "<strong>{{ selectedTableToDelete?.name }}</strong>" không?</p>
            <p class="text-medium-emphasis mt-2">Lưu ý: Dữ liệu liên quan đến bàn này sẽ bị ảnh hưởng.</p>
          </v-card-text>
          
          <v-divider></v-divider>
          
          <v-card-actions class="pa-4">
            <v-spacer></v-spacer>
            <v-btn variant="text" @click="deleteTableDialog = false">Hủy</v-btn>
            <v-btn 
              color="error" 
              @click="confirmDeleteTable"
              :loading="areaTableStore.loading"
            >
              Xóa
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- Dialog chi tiết bàn -->
      <v-dialog v-model="tableDetailsDialog" width="400">
        <v-card>
          <v-card-title class="text-h5 font-weight-bold pa-4">
            Chi tiết bàn
          </v-card-title>
          
          <v-divider></v-divider>
          
          <v-card-text class="pa-4" v-if="selectedTableDetails">
            <v-list-item class="px-0">
              <template v-slot:prepend>
                <v-avatar :color="selectedTableDetails.isActive ? 'success' : 'grey-darken-1'" size="48">
                  <v-icon color="white">mdi-table-furniture</v-icon>
                </v-avatar>
              </template>
              <v-list-item-title class="text-h6">Bàn {{ selectedTableDetails.name }}</v-list-item-title>
              <v-list-item-subtitle>
                <v-chip
                  size="small"
                  :color="selectedTableDetails.isActive ? 'success' : 'grey'"
                  class="mt-1"
                >
                  {{ selectedTableDetails.isActive ? 'Đang hoạt động' : 'Không hoạt động' }}
                </v-chip>
              </v-list-item-subtitle>
            </v-list-item>
            
            <v-divider class="my-3"></v-divider>
            
            <div class="d-flex align-center mb-2">
              <v-icon class="mr-2" :color="selectedTableDetails.area ? 'primary' : 'grey-darken-1'">mdi-map-marker</v-icon>
              <span>
                Khu vực: {{ selectedTableDetails.area ? selectedTableDetails.area.name : 'Chưa gán khu vực' }}
              </span>
            </div>
            
            <div class="mt-4">
              <v-btn 
                color="primary" 
                variant="outlined" 
                block
                class="mb-3"
                @click="openEditTableDialog(selectedTableDetails)"
              >
                <v-icon start>mdi-pencil</v-icon>
                Chỉnh sửa
              </v-btn>
              
              <v-btn 
                color="error" 
                variant="outlined" 
                block
                class="red-border"
                @click="openDeleteTableDialog(selectedTableDetails)"
              >
                <v-icon start>mdi-delete</v-icon>
                Xóa
              </v-btn>
            </div>
          </v-card-text>
          
          <v-divider></v-divider>
          
          <v-card-actions class="pa-4">
            <v-spacer></v-spacer>
            <v-btn variant="text" @click="tableDetailsDialog = false">Đóng</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      <!-- Snackbar thông báo -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
    >
      {{ snackbar.message }}
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
  </dashboard-layout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useAreaTableStore } from '@/stores/areaTable'
import { areaTableService } from '@/services/areaTableService'
import DashboardLayout from '@/components/layouts/DashboardLayout.vue'

const areaTableStore = useAreaTableStore()
const loading = ref(false)

const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

function showSnackbar(message, color = 'success') {
  snackbar.value = {  
    show: true,
    message,
    color
  }
}

// Data cho chọn khu vực
const selectedAreaId = ref(null)
const selectedAreaName = computed(() => {
  if (!selectedAreaId.value) return null
  const area = areaTableStore.areas.find(a => a.id === selectedAreaId.value)
  return area ? area.name : null
})

// Forms
const areaForm = ref(null)
const tableForm = ref(null)

// Data cho dialog khu vực
const areaDialog = ref(false)
const deleteAreaDialog = ref(false)
const isEditingArea = ref(false)
const selectedAreaToDelete = ref(null)
const areaFormData = ref({
  name: '',
  description: '',
  maxTable: null,
  isActive: true
})
const areaFormOriginal = ref(null)

// Data cho dialog bàn
const tableDialog = ref(false)
const deleteTableDialog = ref(false)
const tableDetailsDialog = ref(false)
const isEditingTable = ref(false)
const selectedTableToDelete = ref(null)
const selectedTableDetails = ref(null)
const tableFormData = ref({
  name: '',
  areaId: null,
  isActive: true
})
const tableFormOriginal = ref(null)

// Danh sách khu vực cho select
const areaItems = computed(() => {
  return areaTableStore.areas.map(area => ({
    title: area.name,
    value: area.id
  }))
})

// Lấy tên khu vực từ ID
const getAreaNameById = (areaId) => {
  const area = areaTableStore.areas.find(a => a.id === areaId)
  return area ? area.name : 'Không xác định'
}

// Khởi tạo dữ liệu
onMounted(async () => {
  await areaTableStore.fetchAreas()
  await loadAllTables()
})

// Load tất cả bàn
const loadAllTables = async () => {
  selectedAreaId.value = null
  await areaTableStore.fetchTables()
}

// Theo dõi thay đổi khu vực được chọn
watch(selectedAreaId, async (newValue) => {
  if (newValue) {
    await areaTableStore.fetchTables(0, 40, newValue)
  } else {
    areaTableStore.tables = []
  }
})

// Chọn khu vực
const selectArea = (area) => {
  selectedAreaId.value = area.id
  areaTableStore.selectArea(area)
}

// CRUD khu vực
const openAddAreaDialog = () => {
  isEditingArea.value = false
  areaFormData.value = {
    name: '',
    description: '',
    maxTable: null,
    isActive: true
  }
  areaFormOriginal.value = null
  areaDialog.value = true
}

const openEditAreaDialog = async (area) => {
  isEditingArea.value = true
  areaFormData.value = {
    name: area.name || '',
    description: area.description || '',
    maxTable: area.maxTable || null,
    isActive: area.isActive !== undefined ? area.isActive : true
  }
  areaFormOriginal.value = {...area}
  areaDialog.value = true
}

const openDeleteAreaDialog = (area) => {
  selectedAreaToDelete.value = area
  deleteAreaDialog.value = true
}

async function submitAreaForm() {
  if (!areaForm.value) return
  
  const { valid } = await areaForm.value.validate()
  if (!valid) return

  try {
    const areaData = {
      name: areaFormData.value.name,
      description: areaFormData.value.description || null,
      isActive: Boolean(areaFormData.value.isActive),
      maxTable: areaFormData.value.maxTable > 0 ? areaFormData.value.maxTable : null
    }
    
    if (isEditingArea.value) {
      await areaTableStore.updateArea(areaFormOriginal.value.id, areaData)
      showSnackbar('Cập nhật khu vực thành công', 'success')
    } else {
      await areaTableStore.createArea(areaData)
      showSnackbar('Thêm khu vực mới thành công', 'success')
    }
    areaDialog.value = false
    await areaTableStore.fetchAreas()
    
    // Nếu đang chỉnh sửa khu vực đang được chọn, cập nhật lại tên khu vực
    if (isEditingArea.value && selectedAreaId.value === areaFormOriginal.value.id) {
      selectArea({id: selectedAreaId.value, name: areaFormData.value.name})
    }
  } catch (err) {
    showSnackbar('Đã xảy ra lỗi: ' + err.message, 'error')
  }
}

async function confirmDeleteArea() {
  try {
    await areaTableStore.deleteArea(selectedAreaToDelete.value.id)
    showSnackbar('Xóa khu vực thành công', 'success')
    deleteAreaDialog.value = false
    
    // Nếu xóa khu vực đang được chọn, reset lại selection
    if (selectedAreaId.value === selectedAreaToDelete.value.id) {
      selectedAreaId.value = null
    }
    
    // Tải lại danh sách khu vực sau khi xóa
    await areaTableStore.fetchAreas()
  } catch (err) {
    showSnackbar('Đã xảy ra lỗi: ' + err.message, 'error')
  }
}

// CRUD bàn
const openAddTableDialog = () => {
  isEditingTable.value = false
  tableFormData.value = {
    name: '',
    areaId: selectedAreaId.value ? areaItems.value.find(item => item.value === selectedAreaId.value) : null,
    isActive: true
  }
  tableFormOriginal.value = null
  tableDialog.value = true
}

const openEditTableDialog = (table) => {
  isEditingTable.value = true
  
  // Tìm khu vực trong danh sách areaItems
  const selectedArea = areaItems.value.find(item => item.value === (table.areaId || (table.area ? table.area.id : null)));
  
  tableFormData.value = {
    name: table.name || '',
    areaId: selectedArea || null,
    isActive: table.isActive !== undefined ? table.isActive : true
  }
  
  tableFormOriginal.value = {...table}
  tableDetailsDialog.value = false
  tableDialog.value = true
}

const openDeleteTableDialog = (table) => {
  selectedTableToDelete.value = table
  tableDetailsDialog.value = false
  deleteTableDialog.value = true
}

async function submitTableForm() {
  if (!tableForm.value) return
  
  const { valid } = await tableForm.value.validate()
  if (!valid) return

  try {
    const tableData = {
      name: tableFormData.value.name,
      areaId: tableFormData.value.areaId?.value || tableFormData.value.areaId,
      isActive: Boolean(tableFormData.value.isActive),
      description: null
    }
    
    if (isEditingTable.value) {
      // Thêm id khi cập nhật
      tableData.id = tableFormOriginal.value.id
      await areaTableStore.updateTable(tableFormOriginal.value.id, tableData)
      showSnackbar('Cập nhật bàn thành công', 'success')
    } else {
      await areaTableStore.createTable(tableData)
      showSnackbar('Thêm bàn mới thành công', 'success')
    }
    tableDialog.value = false
    
    // Nếu đang ở chế độ xem tất cả bàn hoặc đang xem khu vực mà bàn đang thuộc về, tải lại danh sách bàn
    if (!selectedAreaId.value || tableData.areaId === selectedAreaId.value) {
      if (selectedAreaId.value) {
        await areaTableStore.fetchTables(0, 40, selectedAreaId.value)
      } else {
        await areaTableStore.fetchTables()
      }
    }
  } catch (err) {
    showSnackbar('Đã xảy ra lỗi: ' + err.message, 'error')
  }
}

async function confirmDeleteTable() {
  try {
    await areaTableStore.deleteTable(selectedTableToDelete.value.id)
    showSnackbar('Xóa bàn thành công', 'success')
    deleteTableDialog.value = false
    
    // Tải lại danh sách bàn sau khi xóa
    if (selectedAreaId.value) {
      await areaTableStore.fetchTables(0, 40, selectedAreaId.value)
    } else {
      await areaTableStore.fetchTables()
    }
  } catch (err) {
    showSnackbar('Đã xảy ra lỗi: ' + err.message, 'error')
  }
}

// Hiển thị chi tiết bàn
async function showTableDetails(table) {
  try {
    loading.value = true
    // Lấy thông tin chi tiết bàn từ API
    const response = await areaTableService.getTableById(table.id)
    selectedTableDetails.value = response.data
    tableDetailsDialog.value = true
  } catch (err) {
    showSnackbar('Đã xảy ra lỗi khi lấy thông tin chi tiết bàn', 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.table-card {
  transition: all 0.2s ease;
  cursor: pointer;
}

.table-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
}

.red-border {
  border: 2px solid #ff5252 !important;
  border-color: #ff5252 !important;
}

/* Override Vuetify form styles */
:deep(.v-field__input) {
  opacity: 1 !important;
}

:deep(.v-field--focused .v-field__input) {
  opacity: 1 !important;
}

:deep(.v-switch__track), 
:deep(.v-switch__thumb) {
  opacity: 1 !important;
}

:deep(.v-btn) {
  text-transform: none !important;
}

:deep(.v-field--error) {
  --v-field-padding-start: 12px !important;
}
</style> 