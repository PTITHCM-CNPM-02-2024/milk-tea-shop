import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8181/api/v1',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Thêm xử lý interceptor nếu cần thiết
// Ví dụ: Xử lý token authentication, error handling, etc.
// Thêm interceptor để xử lý response format
apiClient.interceptors.response.use(
    (response) => {
      // Kiểm tra nếu response có cấu trúc ApiResponse
      if (response.data && response.data.hasOwnProperty('data') &&
          response.data.hasOwnProperty('success') &&
          response.data.hasOwnProperty('message')) {

        // Nếu success là false, xử lý lỗi
        if (!response.data.success) {
          return Promise.reject(new Error(response.data.message || 'Có lỗi xảy ra'));
        }

        // Nếu success là true, trả về data
        return {
          data: response.data.data,
          message: response.data.message,
          metadata: response.data.metadata,
          status: response.data.status
        };
      }

      // Trường hợp khác, trả về response như bình thường
      return response;
    },
    (error) => {
      // Xử lý lỗi từ server
      if (error.response && error.response.data) {
        if (error.response.data.message) {
          error.message = error.response.data.message;
        }
      }
      return Promise.reject(error);
    }
);

export default apiClient;