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
    // Nếu request thành công và trước đó có lỗi (có thể do token hết hạn)
    // thì reset bộ đếm refresh
    if (isRefreshing) {
        // Chỉ reset nếu đang không trong quá trình refresh khác bắt đầu ngay sau đó
        // (check này có thể chưa hoàn hảo, nhưng tạm ổn)
    } else if (refreshAttemptCount > 0) {
        // console.log("Request successful, resetting refresh attempt count.");
        refreshAttemptCount = 0; // Reset khi có request thành công
    }
    return response
  },
  async error => {
    const originalRequest = error.config;

    // Chỉ xử lý lỗi 401 và không phải là yêu cầu refresh token ban đầu
    if (error.response?.status === 401 && originalRequest.url !== '/auth/refresh') {
      
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
        processQueue(new Error('Refresh token attempts limit reached.'), null); // Báo lỗi cho hàng đợi
        authService.clearAuthData();
        router.push('/login').catch(() => {});
        refreshAttemptCount = 0; // Reset bộ đếm sau khi xử lý xong
        isRefreshing = false; // Đảm bảo trạng thái isRefreshing được reset
        return Promise.reject(new Error('Refresh token attempts limit reached.'));
      }

      // Bắt đầu quá trình refresh
      originalRequest._retry = true; // Đánh dấu yêu cầu này đã thử lại
      isRefreshing = true;
      refreshAttemptCount++; // Tăng bộ đếm

      try {
        console.log(`Đang thử làm mới token lần ${refreshAttemptCount}...`);
        const rs = await authService.refreshToken();
        const { accessToken } = rs.data;
        authService.saveToken(accessToken); // Lưu token mới
        
        // Reset bộ đếm khi refresh thành công
        refreshAttemptCount = 0;

        // Cập nhật header cho yêu cầu gốc và các yêu cầu trong hàng đợi
        api.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken;
        originalRequest.headers['Authorization'] = 'Bearer ' + accessToken;
        
        processQueue(null, accessToken); // Xử lý hàng đợi với token mới
        
        return api(originalRequest); // Thử lại yêu cầu gốc
      } catch (refreshError) {
        console.error(`Làm mới token lần ${refreshAttemptCount} thất bại:`, refreshError);
        processQueue(refreshError, null); // Báo lỗi cho các yêu cầu trong hàng đợi

        // Chỉ clear và redirect nếu lỗi refresh là lỗi nghiêm trọng (vd: 401, 403 từ API refresh)
        // Hoặc khi đã hết số lần thử (đã check ở trên)
        // Nếu là lỗi mạng tạm thời, lần thử tiếp theo có thể thành công
        if (refreshError.response?.status === 401 || refreshError.response?.status === 403) {
             console.error("Refresh token không hợp lệ hoặc hết hạn. Đang đăng xuất.");
             authService.clearAuthData(); // Xóa dữ liệu xác thực
             router.push('/login').catch(() => {}); // Điều hướng về trang đăng nhập
             refreshAttemptCount = 0; // Reset bộ đếm
        }
        // Không reset isRefreshing ở đây ngay lập tức nếu muốn thử lại
        // Nhưng vì đã check limit ở trên, nên nếu vào catch này thì hoặc là lỗi thật sự hoặc hết lượt
        // -> nên clear và logout

        return Promise.reject(refreshError); // Trả về lỗi refresh
      } finally {
        isRefreshing = false; // Kết thúc quá trình refresh cho lần thử này
      }
    }

    // Đối với các lỗi khác hoặc lỗi 401 từ /auth/refresh, trả về lỗi gốc
    return Promise.reject(error);
  }
)

export default api
