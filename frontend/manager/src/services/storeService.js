import api from './api'

export const storeService = {
  // Lấy thông tin chi tiết cửa hàng
  getStoreInfo() {
    return api.get('/store/info')
  },

  // Cập nhật thông tin cửa hàng
  updateStoreInfo(storeData) {
    return api.put('/store', storeData)
  }
} 