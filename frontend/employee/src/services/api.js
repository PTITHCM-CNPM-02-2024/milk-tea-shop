import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8181/api/v1',
  headers: {
    'Content-Type': 'application/json'
  },
});

// Thêm interceptor cho request để thêm token xác thực
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

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
          status: response.status
        };
      }

      // Trường hợp ResponseEntity trả về trực tiếp (không có cấu trúc ApiResponse)
      return response;
    },
    (error) => {
      // Xử lý lỗi từ server
      let errorMessage = 'Có lỗi xảy ra';
      
      if (error.response) {
        // Khi có phản hồi từ server nhưng là lỗi
        if (error.response.status === 401) {
          // Trường hợp token hết hạn hoặc không hợp lệ
          localStorage.removeItem('token');
          localStorage.removeItem('accountId');
          localStorage.removeItem('employeeId');
          
          // Thông báo cho người dùng
          errorMessage = 'Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.';
          
          // Chuyển đến trang đăng nhập
          if (window.location.pathname !== '/login') {
            window.location.href = '/login';
          }
        } else if (error.response.data) {
          if (typeof error.response.data === 'string') {
            // Nếu phản hồi lỗi là chuỗi
            errorMessage = error.response.data;
          } else if (error.response.data.message) {
            // Nếu phản hồi lỗi có trường message
            errorMessage = error.response.data.message;
          } else if (error.response.data.error) {
            // Nếu phản hồi lỗi có trường error
            errorMessage = error.response.data.error;
          }
        }
      } else if (error.request) {
        // Khi không nhận được phản hồi
        errorMessage = 'Không nhận được phản hồi từ server';
      } else {
        // Lỗi khi thiết lập request
        errorMessage = error.message;
      }
      
      error.message = errorMessage;
      return Promise.reject(error);
    }
);

export default apiClient;