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
                <i class="fas fa-chair"></i>
              </div>
              <div class="table-name">{{ table.name }}</div>
              <div v-if="!table.isActive" class="table-status">Không hoạt động</div>
            </div>
          </div>
          
          <div v-if="filteredTables.length === 0" class="no-tables text-center py-4">
            <i class="fas fa-chair fa-3x text-muted mb-3"></i>
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
                  <i class="fas fa-times"></i>
                </button>
              </span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button class="btn btn-outline-secondary" @click="$emit('cancel')">Hủy</button>
        <button
          class="btn btn-primary"
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

const areas = ref([]);
const tables = ref([]);
const selectedArea = ref(null);
const selectedTables = ref([]);
const loading = ref(true);

const filteredTables = computed(() => {
  if (!selectedArea.value) return [];
  return tables.value.filter(table => table.areaId === selectedArea.value);
});

const selectedTableObjects = computed(() => {
  return selectedTables.value.map(id => {
    return tables.value.find(table => table.id === id);
  }).filter(table => table); // Remove undefined values
});

async function loadAreas() {
  try {
    const response = await TableService.getActiveAreas();
    areas.value = response.data;
    if (areas.value.length > 0) {
      selectedArea.value = areas.value[0].id;
    }
  } catch (error) {
    console.error('Error loading areas:', error);
    alert('Không thể tải danh sách khu vực');
  }
}

async function loadTables() {
  loading.value = true;
  try {
    const response = await TableService.getActiveServiceTables();
    tables.value = response.data;
  } catch (error) {
    console.error('Error loading tables:', error);
    alert('Không thể tải danh sách bàn');
  } finally {
    loading.value = false;
  }
}

function isTableSelected(tableId) {
  return selectedTables.value.includes(tableId);
}

function toggleTableSelection(table) {
  if (!table.isActive) return;
  
  const index = selectedTables.value.indexOf(table.id);
  if (index === -1) {
    selectedTables.value.push(table.id);
  } else {
    selectedTables.value.splice(index, 1);
  }
}

function removeTable(tableId) {
  const index = selectedTables.value.indexOf(tableId);
  if (index !== -1) {
    selectedTables.value.splice(index, 1);
  }
}

function confirmSelection() {
  emit('select-tables', selectedTableObjects.value);
}

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
  border-bottom: 1px solid var(--light-gray);
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
  border: 1px solid var(--light-gray);
  border-radius: 0.25rem;
  cursor: pointer;
  white-space: nowrap;
}

.area-tab.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.table-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 0.75rem;
}

.table-item {
  background-color: white;
  border: 1px solid var(--light-gray);
  border-radius: 0.5rem;
  padding: 1rem 0.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
}

.table-item.selected {
  background-color: rgba(142, 68, 173, 0.1);
  border-color: var(--primary-color);
}

.table-item.inactive {
  opacity: 0.5;
  cursor: not-allowed;
}

.table-icon {
  font-size: 1.5rem;
  color: #6c757d;
  margin-bottom: 0.5rem;
}

.table-name {
  font-size: 0.9rem;
  text-align: center;
}

.table-status {
  font-size: 0.7rem;
  color: var(--danger-color);
  margin-top: 0.25rem;
}

.selected-tables {
  border-top: 1px solid var(--light-gray);
  padding-top: 0.75rem;
}

.selected-table-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.selected-table-badge {
  background-color: var(--primary-color);
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
  border-top: 1px solid var(--light-gray);
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}
</style>