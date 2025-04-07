<template>
  <div class="modal-overlay">
    <div class="modal-backdrop" @click="$emit('cancel')"></div>
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Chọn bàn</h5>
        <button class="close-btn" @click="$emit('cancel')">
          <i class="fas fa-times"></i>
        </button>
      </div>
      
      <div class="modal-body">
        <div v-if="loading" class="loading text-center py-4">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
          <p class="mt-2">Đang tải danh sách bàn...</p>
        </div>
        
        <div v-else>
          <div class="area-tabs">
            <button
              v-for="area in areas"
              :key="area.id"
              :class="['area-tab', selectedArea === area.id ? 'active' : '']"
              @click="selectedArea = area.id"
            >
              {{ area.name }}
            </button>
          </div>
          
          <div class="table-grid mt-3">
            <div
              v-for="table in filteredTables"
              :key="table.id"
              :class="['table-item', isTableSelected(table.id) ? 'selected' : '', !table.isActive ? 'inactive' : '']"
              @click="toggleTableSelection(table)"
            >
              <div class="table-icon">
                <v-icon>{{ table.isActive ? 'mdi-table-chair' : 'mdi-table-off' }}</v-icon>
              </div>
              <div class="table-name">{{ table.name }}</div>
              <div v-if="!table.isActive" class="table-status">Không hoạt động</div>
            </div>
          </div>
          
          <div v-if="filteredTables.length === 0" class="no-tables text-center py-4">
            <v-icon size="x-large" color="grey" class="mb-3">mdi-table-chair</v-icon>
            <p>Không có bàn trong khu vực này</p>
          </div>
          
          <div v-if="selectedTables.length > 0" class="selected-tables mt-4">
            <h6>Bàn đã chọn:</h6>
            <div class="selected-table-list">
              <span
                v-for="table in selectedTableObjects"
                :key="table.id"
                class="selected-table-badge"
              >
                {{ table.name }}
                <button class="remove-table-btn" @click.stop="removeTable(table.id)">
                  <v-icon small>mdi-close</v-icon>
                </button>
              </span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button class="cancel-btn" @click="$emit('cancel')">Hủy</button>
        <button
          class="confirm-btn"
          :disabled="selectedTables.length === 0"
          @click="confirmSelection"
        >
          Xác nhận
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import TableService from '../services/table.service';

const emit = defineEmits(['select-tables', 'cancel']);

const props = defineProps({
  initialTables: {
    type: Array,
    default: () => []
  }
});

const areas = ref([]);
const tables = ref([]);
const selectedArea = ref(null);
const selectedTables = ref((props.initialTables && props.initialTables.length > 0) 
  ? props.initialTables.map(table => table.id) 
  : []);
const loading = ref(true);

// Add a special NO_AREA_ID constant
const NO_AREA_ID = 'no-area';

// Lọc bàn theo khu vực đã chọn
const filteredTables = computed(() => {
  if (!selectedArea.value) return [];
  
  // If "No Area" is selected, show tables without areas
  if (selectedArea.value === NO_AREA_ID) {
    return tables.value.filter(table => !table.area);
  }
  
  // Otherwise filter by selected area
  return tables.value.filter(table => table.area && table.area.id === selectedArea.value);
});

// Lấy thông tin chi tiết của các bàn được chọn
const selectedTableObjects = computed(() => {
  return selectedTables.value.map(id => {
    return tables.value.find(table => table.id === id);
  }).filter(table => table); // Loại bỏ các giá trị undefined
});

// Tải danh sách khu vực
async function loadAreas() {
  try {
    console.log('Đang tải danh sách khu vực...');
    const response = await TableService.getActiveAreas();
    
    // Kiểm tra và xử lý dữ liệu Spring Boot Page
    if (response && response.data) {
      areas.value = response.data;
    }

    // Add a special "No Area" option
    areas.value.push({
      id: NO_AREA_ID,
      name: 'Khác',
      isActive: true
    });
    
    console.log('Đã tải khu vực:', areas.value);
    
    // Chọn khu vực đầu tiên nếu có
    if (areas.value.length > 0) {
      selectedArea.value = areas.value[0].id;
    }
  } catch (error) {
    console.error('Lỗi khi tải danh sách khu vực:', error);
    alert('Không thể tải danh sách khu vực');
  }
}

// Tải danh sách bàn
async function loadTables() {
  loading.value = true;
  try {
    console.log('Đang tải danh sách bàn...');
    const response = await TableService.getActiveServiceTables();
    
    tables.value = response.data;
    
    console.log('Đã tải bàn:', tables.value);
  } catch (error) {
    console.error('Lỗi khi tải danh sách bàn:', error);
    alert('Không thể tải danh sách bàn');
  } finally {
    loading.value = false;
  }
}

// Kiểm tra xem bàn có được chọn hay không
function isTableSelected(tableId) {
  return selectedTables.value.includes(tableId);
}

// Thêm/xóa bàn khỏi danh sách đã chọn
function toggleTableSelection(table) {
  // Chỉ cho phép chọn bàn đang hoạt động
  if (!table.isActive) {
    alert('Bàn này hiện không hoạt động');
    return;
  }
  
  const index = selectedTables.value.indexOf(table.id);
  if (index === -1) {
    selectedTables.value.push(table.id);
  } else {
    selectedTables.value.splice(index, 1);
  }
}

// Xóa bàn khỏi danh sách đã chọn
function removeTable(tableId) {
  const index = selectedTables.value.indexOf(tableId);
  if (index !== -1) {
    selectedTables.value.splice(index, 1);
  }
}

// Xác nhận lựa chọn bàn
function confirmSelection() {
  emit('select-tables', selectedTableObjects.value);
}

// Tải dữ liệu khi component được tạo
onMounted(() => {
  Promise.all([loadAreas(), loadTables()]);
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  background-color: white;
  border-radius: 0.5rem;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 1rem;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  margin: 0;
  font-size: 1.25rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #6c757d;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
}

.area-tabs {
  display: flex;
  overflow-x: auto;
  gap: 0.5rem;
  padding-bottom: 0.5rem;
}

.area-tab {
  padding: 0.5rem 1rem;
  background-color: #f8f9fa;
  border: 1px solid #e0e0e0;
  border-radius: 0.25rem;
  cursor: pointer;
  white-space: nowrap;
}

.area-tab.active {
  background-color: #2196F3;
  color: white;
  border-color: #2196F3;
}

.table-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 0.75rem;
  margin-top: 1rem;
}

.table-item {
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 0.5rem;
  padding: 1rem 0.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
}

.table-item:hover:not(.inactive) {
  border-color: #2196F3;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.table-item.selected {
  background-color: #E3F2FD;
  border-color: #2196F3;
}

.table-item.inactive {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: #f5f5f5;
  border: 1px dashed #ccc;
}

.table-icon {
  font-size: 1.5rem;
  color: #2196F3;
  margin-bottom: 0.5rem;
}

.table-name {
  font-size: 0.9rem;
  text-align: center;
  font-weight: 500;
}

.table-status {
  font-size: 0.7rem;
  color: #F44336;
  margin-top: 0.25rem;
}

.selected-tables {
  border-top: 1px solid #e0e0e0;
  padding-top: 0.75rem;
  margin-top: 1rem;
}

.selected-tables h6 {
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.selected-table-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.selected-table-badge {
  background-color: #2196F3;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
}

.remove-table-btn {
  background: none;
  border: none;
  color: white;
  margin-left: 0.5rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #e0e0e0;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.cancel-btn, .confirm-btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn {
  background-color: #f5f5f5;
  border: 1px solid #e0e0e0;
  color: #333;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.confirm-btn {
  background-color: #2196F3;
  border: none;
  color: white;
}

.confirm-btn:hover:not(:disabled) {
  background-color: #1976D2;
}

.confirm-btn:disabled {
  background-color: #90CAF9;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>