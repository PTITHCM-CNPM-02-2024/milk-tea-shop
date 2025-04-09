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
                prepend-icon="mdi-plus" 
                @click="openAddAreaDialog"
              >
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
                    <v-avatar color="primary" size="36">
                      <span class="text-h7 font-weight-medium">{{ area.name.charAt(0) }}</span>
                    </v-avatar>
                  </template>
                  
                  <template v-slot:append>
                    <div class="d-flex gap-1">
                      <v-btn
                        icon="mdi-pencil"
                        size="x-small"
                        color="primary"
                        variant="text"
                        @click.stop="openEditAreaDialog(area)"
                      ></v-btn>
                      <v-btn
                        icon="mdi-delete"
                        size="x-small"
                        color="error"
                        variant="text"
                        @click.stop="openDeleteAreaDialog(area)"
                      ></v-btn>
                    </div>
                  </template>
                </v-list-item>
                
                <!-- Hiển thị khi không có dữ liệu -->
                <div v-if="areaTableStore.areas.length === 0 && !areaTableStore.loading" class="text-center py-6">
                  <v-icon icon="mdi-map-marker-radius" size="x-large" class="mb-2 text-medium-emphasis"></v-icon>
                  <div class="text-subtitle-1 text-medium-emphasis">Không có khu vực nào</div>
                  <v-btn color="primary" size="small" class="mt-2" prepend-icon="mdi-plus" @click="openAddAreaDialog">
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
                  prepend-icon="mdi-refresh" 
                  class="mr-2"
                  @click="loadAllTables"
                  :disabled="areaTableStore.loading"
                >
                  Tất cả
                </v-btn>
                <v-btn 
                  color="primary" 
                  size="small"
                  variant="text"
                  prepend-icon="mdi-table-plus" 
                  @click="openAddTableDialog"
                >
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
                    <v-icon icon="mdi-table-furniture" size="large" :color="table.isActive ? (table.areaId ? 'success' : 'blue') : 'grey'"></v-icon>
                    <span class="text-h6 mt-2">{{ table.name }}</span>
                    <div class="d-flex mt-2">
                      <v-icon 
                        icon="mdi-circle" 
                        size="x-small" 
                        :color="table.isActive ? (table.areaId ? 'success' : 'blue') : 'grey'"
                        class="mr-1"
                      ></v-icon>
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
                  <v-icon icon="mdi-table-furniture" size="x-large" class="mb-2 text-medium-emphasis"></v-icon>
                  <div class="text-subtitle-1 text-medium-emphasis text-center">
                    {{ selectedAreaId ? 'Không có bàn nào trong khu vực này' : 'Không có bàn nào trong hệ thống' }}
                  </div>
                  <v-btn 
                    v-if="selectedAreaId" 
                    color="primary" 
                    size="small" 
                    class="mt-2" 
                    prepend-icon="mdi-table-plus" 
                    @click="openAddTableDialog"
                  >
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
          <v-card-title class="text-h6">
            {{ isEditingArea ? 'Chỉnh sửa khu vực' : 'Thêm khu vực mới' }}
          </v-card-title>
          
          <v-card-text>
            <v-form ref="areaForm" @submit.prevent="submitAreaForm">
              <v-text-field
                v-model="areaForm.name"
                label="Tên khu vực"
                required
                variant="outlined"
                density="comfortable"
                class="mb-3"
                :rules="[v => !!v || 'Tên khu vực là bắt buộc']"
              ></v-text-field>
              
              <v-textarea
                v-model="areaForm.description"
                label="Mô tả"
                variant="outlined"
                density="comfortable"
                class="mb-3"
                rows="3"
                auto-grow
              ></v-textarea>
              
              <v-text-field
                v-model.number="areaForm.maxTable"
                label="Số bàn tối đa"
                type="number"
                variant="outlined"
                density="comfortable"
                class="mb-3"
                hint="Để trống nếu không giới hạn"
                :rules="[v => !v || v > 0 || 'Số bàn tối đa phải lớn hơn 0']"
              ></v-text-field>
              
              <v-switch
                v-model="areaForm.isActive"
                label="Kích hoạt khu vực"
                color="primary"
                hide-details
                class="mb-3"
              ></v-switch>
            </v-form>
          </v-card-text>
          
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="grey-darken-1" variant="text" @click="areaDialog = false">Hủy</v-btn>
            <v-btn 
              color="primary" 
              variant="text" 
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
          <v-card-title class="text-h6">
            Xác nhận xóa khu vực
          </v-card-title>
          
          <v-card-text>
            Bạn có chắc chắn muốn xóa khu vực "{{ selectedAreaToDelete?.name }}" không? 
            <div class="mt-2 text-red">Lưu ý: Tất cả bàn trong khu vực này cũng sẽ bị xóa!</div>
          </v-card-text>
          
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="grey-darken-1" variant="text" @click="deleteAreaDialog = false">Hủy</v-btn>
            <v-btn 
              color="error" 
              variant="text" 
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
          <v-card-title class="text-h6">
            {{ isEditingTable ? 'Chỉnh sửa bàn' : 'Thêm bàn mới' }}
          </v-card-title>
          
          <v-card-text>
            <v-form ref="tableForm" @submit.prevent="submitTableForm">
              <v-text-field
                v-model="tableForm.name"
                label="Số bàn"
                required
                variant="outlined"
                density="comfortable"
                class="mb-3"
                :rules="[v => !!v || 'Số bàn là bắt buộc']"
              ></v-text-field>
              
              <v-select
                v-model="tableForm.areaId"
                :items="areaItems"
                label="Khu vực"
                required
                variant="outlined"
                density="comfortable"
                class="mb-3"
                :rules="[v => !!v || 'Khu vực là bắt buộc']"
              ></v-select>
              
              <v-switch
                v-model="tableForm.isActive"
                label="Kích hoạt bàn"
                color="primary"
                hide-details
                class="mb-3"
              ></v-switch>
            </v-form>
          </v-card-text>
          
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="grey-darken-1" variant="text" @click="tableDialog = false">Hủy</v-btn>
            <v-btn 
              color="primary" 
              variant="text" 
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
          <v-card-title class="text-h6">
            Xác nhận xóa bàn
          </v-card-title>
          
          <v-card-text>
            Bạn có chắc chắn muốn xóa bàn "{{ selectedTableToDelete?.name }}" không?
          </v-card-text>
          
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="grey-darken-1" variant="text" @click="deleteTableDialog = false">Hủy</v-btn>
            <v-btn 
              color="error" 
              variant="text" 
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
          <v-card-title class="text-h6">
            Chi tiết bàn
          </v-card-title>
          
          <v-card-text v-if="selectedTableDetails">
            <v-list-item class="px-0">
              <template v-slot:prepend>
                <v-avatar color="primary" size="48">
                  <v-icon icon="mdi-table-furniture" color="white"></v-icon>
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
              <v-icon icon="mdi-map-marker" class="mr-2" color="primary"></v-icon>
              <span>Khu vực: {{ selectedTableDetails.areaId ? getAreaNameById(selectedTableDetails.areaId) : 'Chưa gán khu vực' }}</span>
            </div>
            
            <div class="mt-4 d-flex gap-2">
              <v-btn 
                prepend-icon="mdi-pencil" 
                variant="outlined" 
                color="primary" 
                block
                @click="openEditTableDialog(selectedTableDetails)"
              >
                Chỉnh sửa
              </v-btn>
              <v-btn 
                prepend-icon="mdi-delete" 
                variant="outlined" 
                color="error" 
                block
                @click="openDeleteTableDialog(selectedTableDetails)"
              >
                Xóa
              </v-btn>
            </div>
          </v-card-text>
          
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" variant="text" @click="tableDetailsDialog = false">Đóng</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </div>
  </dashboard-layout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useAreaTableStore } from '@/stores/areaTable'
import DashboardLayout from '@/components/layouts/DashboardLayout.vue'

const areaTableStore = useAreaTableStore()

// Data cho chọn khu vực
const selectedAreaId = ref(null)
const selectedAreaName = computed(() => {
  if (!selectedAreaId.value) return null
  const area = areaTableStore.areas.find(a => a.id === selectedAreaId.value)
  return area ? area.name : null
})

// Data cho dialog khu vực
const areaDialog = ref(false)
const deleteAreaDialog = ref(false)
const isEditingArea = ref(false)
const selectedAreaToDelete = ref(null)
const areaForm = ref({
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
const tableForm = ref({
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
  areaForm.value = {
    name: '',
    description: '',
    maxTable: null,
    isActive: true
  }
  areaDialog.value = true
}

const openEditAreaDialog = (area) => {
  isEditingArea.value = true
  areaForm.value = {
    name: area.name,
    description: area.description || '',
    maxTable: area.maxTable,
    isActive: area.isActive
  }
  areaFormOriginal.value = area
  areaDialog.value = true
}

const openDeleteAreaDialog = (area) => {
  selectedAreaToDelete.value = area
  deleteAreaDialog.value = true
}

const submitAreaForm = async () => {
  try {
    if (isEditingArea.value) {
      await areaTableStore.updateArea(areaFormOriginal.value.id, areaForm.value)
    } else {
      await areaTableStore.createArea(areaForm.value)
    }
    areaDialog.value = false
    // Nếu đang chỉnh sửa khu vực đang được chọn, cập nhật lại tên khu vực
    if (isEditingArea.value && selectedAreaId.value === areaFormOriginal.value.id) {
      selectArea({id: selectedAreaId.value, name: areaForm.value.name})
    }
  } catch (err) {
    console.error('Lỗi khi lưu khu vực:', err)
  }
}

const confirmDeleteArea = async () => {
  try {
    await areaTableStore.deleteArea(selectedAreaToDelete.value.id)
    deleteAreaDialog.value = false
    
    // Nếu xóa khu vực đang được chọn, reset lại selection
    if (selectedAreaId.value === selectedAreaToDelete.value.id) {
      selectedAreaId.value = null
    }
  } catch (err) {
    console.error('Lỗi khi xóa khu vực:', err)
  }
}

// CRUD bàn
const openAddTableDialog = () => {
  isEditingTable.value = false
  tableForm.value = {
    name: '',
    areaId: selectedAreaId.value || null,
    isActive: true
  }
  tableDialog.value = true
}

const openEditTableDialog = (table) => {
  isEditingTable.value = true
  tableForm.value = {
    name: table.name,
    areaId: table.areaId,
    isActive: table.isActive
  }
  tableFormOriginal.value = table
  tableDetailsDialog.value = false
  tableDialog.value = true
}

const openDeleteTableDialog = (table) => {
  selectedTableToDelete.value = table
  tableDetailsDialog.value = false
  deleteTableDialog.value = true
}

const submitTableForm = async () => {
  try {
    if (isEditingTable.value) {
      await areaTableStore.updateTable(tableFormOriginal.value.id, tableForm.value)
    } else {
      await areaTableStore.createTable(tableForm.value)
    }
    tableDialog.value = false
    
    // Nếu đang ở chế độ xem tất cả bàn hoặc đang xem khu vực mà bàn đang thuộc về, tải lại danh sách bàn
    if (!selectedAreaId.value || tableForm.value.areaId === selectedAreaId.value) {
      if (selectedAreaId.value) {
        await areaTableStore.fetchTables(0, 40, selectedAreaId.value)
      } else {
        await areaTableStore.fetchTables()
      }
    }
  } catch (err) {
    console.error('Lỗi khi lưu bàn:', err)
  }
}

const confirmDeleteTable = async () => {
  try {
    await areaTableStore.deleteTable(selectedTableToDelete.value.id)
    deleteTableDialog.value = false
  } catch (err) {
    console.error('Lỗi khi xóa bàn:', err)
  }
}

// Hiển thị chi tiết bàn
const showTableDetails = (table) => {
  selectedTableDetails.value = table
  tableDetailsDialog.value = true
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
</style> 