import apiClient from './api';

export default {

    // Lấy danh sách bàn đang hoạt động
    getActiveServiceTables(active = true) {
        return apiClient.get('/service-tables/active', {
            params: {
                active
            }
        });
    },

    // Lấy thông tin bàn theo ID
    getServiceTableById(id) {
        return apiClient.get(`/service-tables/${id}`);
    },

    // Lấy tất cả khu vực
    // Lấy danh sách khu vực
    getActiveAreas(page = 0, size = 100, active = true) {
        return apiClient.get('/areas', {
            params: {
                page,
                size,
                active
            }
        });
    },

    // Lấy thông tin khu vực theo ID
    getAreaById(id) {
        return apiClient.get(`/areas/${id}`);
    }
};