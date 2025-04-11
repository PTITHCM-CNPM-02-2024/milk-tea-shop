import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import TableService from '../services/table.service';

export const useTableStore = defineStore('table', () => {
  // State
  const tables = ref([]);
  const areas = ref([]);
  const selectedArea = ref(null);
  const loading = ref(false);
  const error = ref(null);
  
  // Constants
  const NO_AREA_ID = 'no-area';
  
  // Computed
  const allAreas = computed(() => {
    // Thêm khu vực "Khác" cho các bàn không có khu vực
    const noAreaOption = { id: NO_AREA_ID, name: 'Khác', isActive: true };
    return [...areas.value, noAreaOption];
  });
  
  const filteredTables = computed(() => {
    console.log('Lọc bàn theo khu vực:', selectedArea.value);
    console.log('Tổng số bàn:', tables.value.length);
    
    if (!selectedArea.value) return [];
    
    // Nếu "Khác" được chọn, hiển thị các bàn không có khu vực
    if (selectedArea.value === NO_AREA_ID) {
      const result = tables.value.filter(table => !table.areaId);
      console.log('Bàn không có khu vực:', result.length);
      return result;
    }
    
    // Lọc bàn theo khu vực đang chọn
    const result = tables.value.filter(table => {
      // So sánh ID của khu vực
      return table.areaId === selectedArea.value || 
             (table.areaId && selectedArea.value && table.areaId.toString() === selectedArea.value.toString());
    });
    
    console.log('Bàn thuộc khu vực:', result.length);
    return result;
  });
  
  // Actions
  async function fetchTables(active = true) {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await TableService.getActiveServiceTables(active);
      
      if (response && response.data) {
        const rawTables = Array.isArray(response.data) 
          ? response.data 
          : (response.data.content && Array.isArray(response.data.content) ? response.data.content : []);
        
        // Bổ sung thêm trường area cho mỗi bàn để dễ dàng hiển thị
        tables.value = rawTables.map(table => {
          // In ra bàn để debug
          console.log('Bàn gốc:', table);
          
          if (table.areaId) {
            return {
              ...table,
              area: { id: table.areaId }
            };
          }
          return table;
        });
        
        console.log('Đã tải bàn:', tables.value);
      }
    } catch (err) {
      console.error('Lỗi khi tải danh sách bàn:', err);
      error.value = err.message || 'Không thể tải danh sách bàn';
    } finally {
      loading.value = false;
    }
  }
  
  async function fetchAreas(active = true) {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await TableService.getActiveAreas(active);
      
      if (response && response.data) {
        areas.value = Array.isArray(response.data) 
          ? response.data 
          : (response.data.content && Array.isArray(response.data.content) ? response.data.content : []);
        
        // Chọn khu vực đầu tiên nếu có
        if (areas.value.length > 0) {
          selectedArea.value = areas.value[0].id;
        }
        
        console.log('Đã tải khu vực:', areas.value);
      }
    } catch (err) {
      console.error('Lỗi khi tải danh sách khu vực:', err);
      error.value = err.message || 'Không thể tải danh sách khu vực';
    } finally {
      loading.value = false;
    }
  }
  
  function selectArea(areaId) {
    selectedArea.value = areaId;
  }
  
  return {
    // State
    tables,
    areas,
    selectedArea,
    loading,
    error,
    
    // Constants
    NO_AREA_ID,
    
    // Computed
    allAreas,
    filteredTables,
    
    // Actions
    fetchTables,
    fetchAreas,
    selectArea
  };
}); 