import api from './api'

export const areaTableService = {
  // Service cho khu vực (Area)
  getAreas(page = 0, size = 10) {
    return api.get('/areas', {
      params: { page, size }
    })
  },

  getAreaById(id) {
    return api.get(`/areas/${id}`)
  },

  createArea(areaData) {
    const request = {
      name: areaData.name,
      isActive: areaData.isActive,
      maxTable: areaData.maxTable
    }
    return api.post('/areas', request)
  },

  updateArea(id, areaData) {
    const request = {
      name: areaData.name,
      description: areaData.description,
      isActive: areaData.isActive,
      maxTable: areaData.maxTable
    }
    return api.put(`/areas/${id}`, request)
  },

  deleteArea(id) {
    return api.delete(`/areas/${id}`)
  },

  // Service cho bàn (ServiceTable)
  getTables(page = 0, size = 40) {
    return api.get('/service-tables', {
      params: { page, size }
    })
  },

  getTablesByAreaId(areaId, page = 0, size = 40) {
    return api.get(`/areas/${areaId}/tables`, {
      params: { page, size }
    })
  },

  getTableById(id) {
    return api.get(`/service-tables/${id}`)
  },

  createTable(tableData) {
    const request = {
      name: tableData.name,
      areaId: tableData.areaId,
      description: tableData.description || null,
      isActive: true // Mặc định bàn mới là active
    }
    return api.post('/service-tables', request)
  },

  updateTable(id, tableData) {
    const request = {
      id: tableData.id,
      name: tableData.name,
      isActive: tableData.isActive,
      areaId: tableData.areaId
    }
    return api.put(`/service-tables/${id}`, request)
  },

  deleteTable(id) {
    return api.delete(`/service-tables/${id}`)
  }
}
