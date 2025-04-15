import api from './api'

export const storeService = {
  // Lấy thông tin chi tiết cửa hàng
  getStoreInfo() {
    return api.get('/store/info')
  },

  // Cập nhật thông tin cửa hàng
  updateStoreInfo(id, storeData) {
    console.log(id, storeData)
    return api.put(`/store/${id}`, storeData)
  }
} 