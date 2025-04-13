import apiClient from './api';

export default {

    // Lấy danh sách bàn đang hoạt động
    getActiveServiceTables(active = true) {
        return apiClient.get(`/service-tables/active/${active}`);
    },

    // Lấy thông tin bàn theo ID
    getServiceTableById(id) {
        return apiClient.get(`/service-tables/${id}`);
    },

    // Lấy tất cả khu vực
    // Lấy danh sách khu vực
    getActiveAreas(active = true) {
        return apiClient.get(`/areas/active/${active}`);
    },

    // Lấy thông tin khu vực theo ID
    getAreaById(id) {
        return apiClient.get(`/areas/${id}`);
    }
};