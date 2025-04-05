import apiClient from './api';

export default {
  // Lấy tất cả bàn
  getAllServiceTables() {
    return apiClient.get('/service-tables');
  },
  
  // Lấy danh sách bàn đang hoạt động
  getActiveServiceTables(size = 100) {
    return apiClient.get(`/service-tables/active?active=true&size=${size}`);
  },
  
  // Lấy thông tin bàn theo ID
  getServiceTableById(id) {
    return apiClient.get(`/service-tables/${id}`);
  },
  
  // Lấy tất cả khu vực
  getAllAreas() {
    return apiClient.get('/areas');
  },
  
  // Lấy danh sách khu vực
  getActiveAreas() {
    return apiClient.get('/areas?active=true');
  },
  
  // Lấy thông tin khu vực theo ID
  getAreaById(id) {
    return apiClient.get(`/areas/${id}`);
  }
};