import axios from 'axios'

// Cấu hình axios
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8181/api/v1',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// Thêm interceptor để xử lý token, lỗi, v.v...
api.interceptors.request.use(
  config => {
    // Lấy token từ localStorage nếu có
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Xử lý các lỗi phản hồi
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    // Xử lý lỗi response
    if (error.response) {
      // Xử lý đăng nhập lại nếu token hết hạn (mã 401)
      if (error.response.status === 401) {
        localStorage.removeItem('accessToken')
        // Có thể điều hướng đến trang đăng nhập tại đây
      }
    }
    return Promise.reject(error)
  }
)

export default api
