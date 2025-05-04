import axios from 'axios'
import authService from './authService' // Import authService
import router from '@/router'; // Import router để điều hướng

// Cấu hình axios
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8181/api/v1',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true // Quan trọng: để gửi và nhận cookie (refreshToken)
})

// Thêm interceptor để xử lý token
api.interceptors.request.use(
  config => {
    const token = authService.getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Biến cờ và hàng đợi để xử lý refresh token đồng thời
let isRefreshing = false;
let failedQueue = [];
let refreshAttemptCount = 0; // Biến đếm số lần thử refresh
const MAX_REFRESH_ATTEMPTS = 5; // Giới hạn số lần thử
let lastRefreshTime = 0; // Thời điểm refresh token cuối cùng
const MIN_REFRESH_INTERVAL = 5000; // Thời gian tối thiểu giữa các lần refresh (5 giây)

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

// Xử lý các lỗi phản hồi
api.interceptors.response.use(
  response => {
    // Nếu request thành công, reset bộ đếm refresh
    if (refreshAttemptCount > 0 && !isRefreshing) {
      refreshAttemptCount = 0; // Reset khi có request thành công
    }
    return response
  },
  async error => {
    const originalRequest = error.config;
    
    // Ngăn chặn vòng lặp vô hạn - nếu request đã thử lại rồi mà vẫn lỗi 401,
    // không refresh token nữa
    if (originalRequest._retry) {
      return Promise.reject(error);
    }

    // Chỉ xử lý lỗi 401 và không phải là yêu cầu refresh token ban đầu
    if (error.response?.status === 401 && !originalRequest._retry && originalRequest.url !== '/auth/refresh') {
      
      // Kiểm tra khoảng thời gian giữa các lần refresh
      const now = Date.now();
      if (now - lastRefreshTime < MIN_REFRESH_INTERVAL) {
        console.warn(`Refresh token quá thường xuyên. Đợi ít nhất ${MIN_REFRESH_INTERVAL/1000}s giữa các lần refresh.`);
        refreshAttemptCount++; // Vẫn tăng bộ đếm để kiểm soát số lần thử
      }

      // Nếu đang trong quá trình refresh, thêm yêu cầu vào hàng đợi
      if (isRefreshing) {
        return new Promise(function(resolve, reject) {
          failedQueue.push({ resolve, reject });
        }).then(token => {
          originalRequest.headers['Authorization'] = 'Bearer ' + token;
          return api(originalRequest); // Thử lại yêu cầu với token mới
        }).catch(err => {
          return Promise.reject(err); // Trả về lỗi nếu refresh thất bại
        });
      }

      // Kiểm tra giới hạn số lần thử refresh
      if (refreshAttemptCount >= MAX_REFRESH_ATTEMPTS) {
        console.error("Đã đạt giới hạn số lần thử làm mới token. Đang đăng xuất.");
        logoutUser("Đã vượt quá số lần thử làm mới token.");
        return Promise.reject(new Error('Refresh token attempts limit reached.'));
      }

      // Đánh dấu là đang refresh và tăng bộ đếm
      originalRequest._retry = true;
      isRefreshing = true;
      refreshAttemptCount++;
      lastRefreshTime = Date.now();

      try {
        console.log(`Đang thử làm mới token lần ${refreshAttemptCount}...`);
        const rs = await authService.refreshToken();
        const { accessToken } = rs.data;
        
        // Lưu token mới và reset trạng thái
        authService.saveToken(accessToken);
        refreshAttemptCount = 0;

        // Cập nhật header cho request hiện tại và các request trong hàng đợi
        api.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken;
        originalRequest.headers['Authorization'] = 'Bearer ' + accessToken;
        
        // Xử lý hàng đợi và trả về request đã được cập nhật token
        processQueue(null, accessToken);
        return api(originalRequest);
      } catch (refreshError) {
        console.error(`Làm mới token lần ${refreshAttemptCount} thất bại:`, refreshError);
        
        // Nếu lỗi từ server (không phải lỗi mạng), logout user
        if (refreshError.response) {
          logoutUser("Token không hợp lệ hoặc hết hạn.");
        }
        
        // Xử lý hàng đợi với lỗi
        processQueue(refreshError, null);
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false; // Luôn đảm bảo reset flag
      }
    }

    // Đối với các lỗi khác, trả về lỗi gốc
    return Promise.reject(error);
  }
);

// Hàm phụ trợ để thực hiện đăng xuất
function logoutUser(reason) {
  console.warn(`Đăng xuất người dùng: ${reason}`);
  // Thông báo các yêu cầu đang chờ
  processQueue(new Error('User logged out: ' + reason), null);
  // Xóa thông tin đăng nhập
  authService.clearAuthData();
  // Reset các biến trạng thái
  refreshAttemptCount = 0;
  isRefreshing = false;
  // Chuyển hướng về trang đăng nhập
  router.push('/login').catch(() => {});
}

export default api
