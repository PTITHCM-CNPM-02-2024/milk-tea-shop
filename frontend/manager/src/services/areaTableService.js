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
    return api.post('/areas', areaData)
  },

  updateArea(id, areaData) {
    return api.put(`/areas/${id}`, areaData)
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
    return api.post('/service-tables', tableData)
  },

  updateTable(id, tableData) {
    return api.put(`/service-tables/${id}`, tableData)
  },

  deleteTable(id) {
    return api.delete(`/service-tables/${id}`)
  }
}
