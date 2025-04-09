import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as areaTableService from '@/services/areaTableService'

export const useAreaTableStore = defineStore('areaTable', () => {
  // State
  const areas = ref([])
  const tables = ref([])
  const loading = ref(false)
  const error = ref(null)
  const totalAreas = ref(0)
  const totalTables = ref(0)
  const selectedArea = ref(null)
  
  // Getters
  const getAreaById = computed(() => {
    return (id) => areas.value.find(area => area.id === id)
  })
  
  const getTableById = computed(() => {
    return (id) => tables.value.find(table => table.id === id)
  })
  
  const getTablesByAreaId = computed(() => {
    return (areaId) => {
      if (!areaId) return tables.value.filter(table => !table.areaId)
      return tables.value.filter(table => table.areaId === areaId)
    }
  })
  
  // Actions
  // Khu vực (Area)
  const fetchAreas = async (page = 0, size = 10) => {
    loading.value = true
    error.value = null
    try {
      const response = await areaTableService.getAreas(page, size)
      areas.value = response.content || []
      totalAreas.value = response.totalElements || 0
    } catch (err) {
      error.value = 'Có lỗi xảy ra khi tải danh sách khu vực: ' + (err.message || 'Lỗi không xác định')
      console.error(err)
    } finally {
      loading.value = false
    }
  }
  
  const createArea = async (areaData) => {
    loading.value = true
    error.value = null
    try {
      const newArea = await areaTableService.createArea(areaData)
      // Thêm vào danh sách nếu cần
      areas.value.push(newArea)
      return newArea
    } catch (err) {
      error.value = 'Có lỗi xảy ra khi tạo khu vực mới: ' + (err.message || 'Lỗi không xác định')
      console.error(err)
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const updateArea = async (id, areaData) => {
    loading.value = true
    error.value = null
    try {
      const updatedArea = await areaTableService.updateArea(id, areaData)
      // Cập nhật trong danh sách
      const index = areas.value.findIndex(area => area.id === id)
      if (index !== -1) {
        areas.value[index] = { ...areas.value[index], ...updatedArea }
      }
      return updatedArea
    } catch (err) {
      error.value = 'Có lỗi xảy ra khi cập nhật khu vực: ' + (err.message || 'Lỗi không xác định')
      console.error(err)
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const deleteArea = async (id) => {
    loading.value = true
    error.value = null
    try {
      await areaTableService.deleteArea(id)
      // Xóa khỏi danh sách
      areas.value = areas.value.filter(area => area.id !== id)
      return true
    } catch (err) {
      error.value = 'Có lỗi xảy ra khi xóa khu vực: ' + (err.message || 'Lỗi không xác định')
      console.error(err)
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const selectArea = (area) => {
    selectedArea.value = area
  }
  
  // Bàn (Table)
  const fetchTables = async (page = 0, size = 40, areaId = null) => {
    loading.value = true
    error.value = null
    try {
      let response
      if (areaId) {
        response = await areaTableService.getTablesByAreaId(areaId, page, size)
      } else {
        response = await areaTableService.getTables(page, size)
      }
      tables.value = response.content || []
      totalTables.value = response.totalElements || 0
    } catch (err) {
      error.value = 'Có lỗi xảy ra khi tải danh sách bàn: ' + (err.message || 'Lỗi không xác định')
      console.error(err)
    } finally {
      loading.value = false
    }
  }
  
  const createTable = async (tableData) => {
    loading.value = true
    error.value = null
    try {
      const newTable = await areaTableService.createTable(tableData)
      // Thêm vào danh sách nếu cần
      tables.value.push(newTable)
      return newTable
    } catch (err) {
      error.value = 'Có lỗi xảy ra khi tạo bàn mới: ' + (err.message || 'Lỗi không xác định')
      console.error(err)
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const updateTable = async (id, tableData) => {
    loading.value = true
    error.value = null
    try {
      const updatedTable = await areaTableService.updateTable(id, tableData)
      // Cập nhật trong danh sách
      const index = tables.value.findIndex(table => table.id === id)
      if (index !== -1) {
        tables.value[index] = { ...tables.value[index], ...updatedTable }
      }
      return updatedTable
    } catch (err) {
      error.value = 'Có lỗi xảy ra khi cập nhật bàn: ' + (err.message || 'Lỗi không xác định')
      console.error(err)
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const deleteTable = async (id) => {
    loading.value = true
    error.value = null
    try {
      await areaTableService.deleteTable(id)
      // Xóa khỏi danh sách
      tables.value = tables.value.filter(table => table.id !== id)
      return true
    } catch (err) {
      error.value = 'Có lỗi xảy ra khi xóa bàn: ' + (err.message || 'Lỗi không xác định')
      console.error(err)
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // Reset lại store
  const resetStore = () => {
    areas.value = []
    tables.value = []
    loading.value = false
    error.value = null
    totalAreas.value = 0
    totalTables.value = 0
    selectedArea.value = null
  }
  
  return {
    // State
    areas,
    tables,
    loading,
    error,
    totalAreas,
    totalTables,
    selectedArea,
    
    // Getters
    getAreaById,
    getTableById,
    getTablesByAreaId,
    
    // Actions
    fetchAreas,
    createArea,
    updateArea,
    deleteArea,
    selectArea,
    fetchTables,
    createTable,
    updateTable,
    deleteTable,
    resetStore
  }
}) 