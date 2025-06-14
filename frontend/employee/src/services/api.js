import axios from 'axios';

// Biến trạng thái để theo dõi quá trình làm mới token
let isRefreshing = false;
// Hàng đợi chứa các yêu cầu bị lỗi 401 trong khi đang làm mới token
let failedQueue = [];

// Hàm xử lý hàng đợi yêu cầu
const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

const apiClient = axios.create({
  baseURL: 'http://localhost:8181/api/v1',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true,
});

// Thêm interceptor cho request để thêm token xác thực
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && !config.headers['Authorization']) { // Chỉ thêm nếu chưa có header Authorization
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
      // hoặc các response khác (ví dụ: từ yêu cầu làm mới token không qua ApiResponse)
      return response;
    },
    async (error) => {
      const originalRequest = error.config;
      let errorMessage = 'Có lỗi xảy ra'; // Default

      if (error.response) {
        // --- Xử lý làm mới token khi gặp lỗi 401 (logic này giữ nguyên) ---
        if (error.response.status === 401 && originalRequest.url !== '/auth/refresh' && !originalRequest._retry) {
          if (isRefreshing) {
            return new Promise((resolve, reject) => {
              failedQueue.push({ resolve, reject });
            }).then(token => {
              originalRequest.headers['Authorization'] = 'Bearer ' + token;
              return apiClient(originalRequest);
            }).catch(err => {
              return Promise.reject(err);
            });
          }

          originalRequest._retry = true;
          isRefreshing = true;

          try {
            console.log('Attempting to refresh token...');
            const refreshResponse = await apiClient.post('/auth/refresh');

            if (refreshResponse.status === 200 && refreshResponse.data.accessToken) {
              const newAccessToken = refreshResponse.data.accessToken;
              localStorage.setItem('token', newAccessToken);
              apiClient.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;
              originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
              processQueue(null, newAccessToken);
              console.log('Token refreshed successfully, retrying original request.');
              return apiClient(originalRequest);
            } else {
              throw new Error('Could not refresh token from response.');
            }
          } catch (refreshError) {
            console.error('Unable to refresh token:', refreshError);
            processQueue(refreshError, null);
            // Logout user
            localStorage.removeItem('token');
            // Xóa cả accountId, employeeId nếu có (tùy thuộc logic bạn có dùng không)
            // localStorage.removeItem('accountId');
            // localStorage.removeItem('employeeId');
            delete apiClient.defaults.headers.common['Authorization'];
            errorMessage = 'Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.';
            if (window.location.pathname !== '/login') {
              window.location.href = '/login';
            }
             // Gán lỗi trước khi reject bên trong catch này
            const finalError = refreshError instanceof Error ? refreshError : new Error(errorMessage);
            finalError.originalError = error; // Giữ lại lỗi gốc nếu cần
            finalError.message = errorMessage;
            return Promise.reject(finalError);
          } finally {
            isRefreshing = false;
          }
        } // --- Kết thúc xử lý làm mới token ---

        // --- Trích xuất thông báo lỗi chung --- 
        const responseData = error.response.data;
        if (responseData) {
          if (typeof responseData === 'object') {
            errorMessage =
              responseData.detail ??      // RFC 7807 detail
              responseData.title ??       // RFC 7807 title
              responseData.message ??     // Common message field
              responseData.error ??       // Common error field
              `Lỗi ${error.response.status}`; // Fallback
          } else if (typeof responseData === 'string' && responseData.length > 0) {
            errorMessage = responseData; // Raw string error
          } else {
            errorMessage = `Lỗi ${error.response.status}`; // Fallback for other data types
          }
        } else {
           errorMessage = `Lỗi ${error.response.status}`; // No response data
        }

        // --- Xử lý riêng cho lỗi 401 (nếu không được xử lý bởi refresh ở trên) ---
        if (error.response.status === 401) {
            console.error('Handling final 401 error. Logging out.');
           // Đảm bảo logout nếu là lỗi 401 cuối cùng
           localStorage.removeItem('token');
           // localStorage.removeItem('accountId');
           // localStorage.removeItem('employeeId');
           delete apiClient.defaults.headers.common['Authorization'];
           errorMessage = errorMessage || 'Phiên đăng nhập hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.';
           if (window.location.pathname !== '/login') {
             window.location.href = '/login';
           }
        }

      } else if (error.request) {
        // Lỗi không nhận được phản hồi
        errorMessage = 'Không nhận được phản hồi từ server. Vui lòng kiểm tra kết nối mạng.';
      } else {
        // Lỗi thiết lập request
        errorMessage = error.message || 'Lỗi không xác định khi thiết lập yêu cầu.';
      }

      // Gán message cuối cùng cho đối tượng lỗi
      error.message = errorMessage;

      // Reject promise với lỗi đã được cập nhật message
      return Promise.reject(error);
    }
);

export default apiClient;