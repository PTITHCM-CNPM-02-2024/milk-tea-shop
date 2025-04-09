import api from './api'

// Service cho khu vực (Area)
export const getAreas = async (page = 0, size = 10) => {
  try {
    const response = await api.get('/areas', {
      params: { page, size }
    })
    return response.data
  } catch (error) {
    console.error('Error fetching areas:', error)
    throw error
  }
}

export const getAreaById = async (id) => {
  try {
    const response = await api.get(`/areas/${id}`)
    return response.data
  } catch (error) {
    console.error(`Error fetching area with id ${id}:`, error)
    throw error
  }
}

export const createArea = async (areaData) => {
  try {
    const response = await api.post('/areas', areaData)
    return response.data
  } catch (error) {
    console.error('Error creating area:', error)
    throw error
  }
}

export const updateArea = async (id, areaData) => {
  try {
    const response = await api.put(`/areas/${id}`, areaData)
    return response.data
  } catch (error) {
    console.error(`Error updating area with id ${id}:`, error)
    throw error
  }
}

export const deleteArea = async (id) => {
  try {
    const response = await api.delete(`/areas/${id}`)
    return response.data
  } catch (error) {
    console.error(`Error deleting area with id ${id}:`, error)
    throw error
  }
}

// Service cho bàn (ServiceTable)
export const getTables = async (page = 0, size = 40) => {
  try {
    const response = await api.get('/service-tables', {
      params: { page, size }
    })
    return response.data
  } catch (error) {
    console.error('Error fetching tables:', error)
    throw error
  }
}

export const getTablesByAreaId = async (areaId, page = 0, size = 40) => {
  try {
    const response = await api.get(`/areas/${areaId}/tables`, {
      params: { page, size }
    })
    return response.data
  } catch (error) {
    console.error(`Error fetching tables for area ${areaId}:`, error)
    throw error
  }
}

export const getTableById = async (id) => {
  try {
    const response = await api.get(`/service-tables/${id}`)
    return response.data
  } catch (error) {
    console.error(`Error fetching table with id ${id}:`, error)
    throw error
  }
}

export const createTable = async (tableData) => {
  try {
    const response = await api.post('/service-tables', tableData)
    return response.data
  } catch (error) {
    console.error('Error creating table:', error)
    throw error
  }
}

export const updateTable = async (id, tableData) => {
  try {
    const response = await api.put(`/service-tables/${id}`, tableData)
    return response.data
  } catch (error) {
    console.error(`Error updating table with id ${id}:`, error)
    throw error
  }
}

export const deleteTable = async (id) => {
  try {
    const response = await api.delete(`/service-tables/${id}`)
    return response.data
  } catch (error) {
    console.error(`Error deleting table with id ${id}:`, error)
    throw error
  }
}
