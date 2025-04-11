import axios from 'axios'
import { useSnackbarStore } from '@/stores/snackbarStore'

const baseURL = 'http://localhost:8181/'

const api = axios.create({
  baseURL,
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// Thêm interceptor xử lý lỗi
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    const snackbar = useSnackbarStore()
    let errorMessage = 'Đã xảy ra lỗi khi gọi API'

    if (error.response) {
      // Lỗi từ server
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          errorMessage = data.message || 'Dữ liệu không hợp lệ'
          break
        case 401:
          errorMessage = 'Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại'
          // Có thể thêm logic chuyển hướng đến trang đăng nhập ở đây
          break
        case 403:
          errorMessage = 'Bạn không có quyền truy cập'
          break
        case 404:
          errorMessage = 'Không tìm thấy tài nguyên'
          break
        case 422:
          errorMessage = data.message || 'Dữ liệu không hợp lệ'
          break
        case 500:
          errorMessage = 'Lỗi hệ thống, vui lòng thử lại sau'
          break
        default:
          errorMessage = data.message || `Lỗi ${status}: ${data.error || 'Không xác định'}`
      }
    } else if (error.request) {
      // Lỗi không nhận được phản hồi
      errorMessage = 'Không nhận được phản hồi từ máy chủ'
    }

    // Hiển thị thông báo lỗi
    snackbar.showError(errorMessage)
    
    return Promise.reject(error)
  }
)

export default api 