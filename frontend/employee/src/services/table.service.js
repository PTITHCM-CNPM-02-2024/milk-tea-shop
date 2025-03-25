import apiClient from './api';

export default {
  // Lấy tất cả bàn
  getAllServiceTables() {
    return apiClient.get('/service-tables');
  },
  
  // Lấy bàn đang hoạt động
  getActiveServiceTables() {
    return apiClient.get('/service-tables/active');
  },
  
  // Lấy thông tin bàn theo ID
  getServiceTableById(id) {
    return apiClient.get(`/service-tables/${id}`);
  },
  
  // Lấy tất cả khu vực
  getAllAreas() {
    return apiClient.get('/areas');
  },
  
  // Lấy khu vực đang hoạt động
  getActiveAreas() {
    return apiClient.get('/areas/active');
  },
  
  // Lấy thông tin khu vực theo ID
  getAreaById(id) {
    return apiClient.get(`/areas/${id}`);
  }
};