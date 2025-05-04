import api from './api'

export const authService = {
  // Đăng nhập
  login(username, password) {
    return api.post('/auth/login', { username, password })
  },
  
  // Đăng xuất
  logout() {
    // Khi đăng xuất, cũng nên xóa refreshToken trên server nếu có API
    // Hiện tại chỉ xóa local
    return api.post('/auth/logout') // Gọi API logout của backend (nếu có)
  },
  
  // Làm mới access token
  refreshToken() {
    // API refresh sử dụng cookie refreshToken và cần access token cũ (đã hết hạn) trong header
    // Interceptor đã tự động thêm access token cũ vào header
    // Cookie được trình duyệt tự động gửi nếu `withCredentials: true` được đặt trong api.js
    return api.post('/auth/refresh'); // Sử dụng POST thay vì GET như mô tả API Java
  },
  
  // Lấy thông tin quản lý từ id tài khoản
  getManagerByAccountId(accountId) {
    return api.get(`/managers/${accountId}`)
  },
  
  // Lưu token vào localStorage
  saveToken(token) {
    localStorage.setItem('accessToken', token)
  },
  
  // Lưu thông tin người dùng vào localStorage
  saveUserInfo(userInfo) {
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  },
  
  // Lấy token từ localStorage
  getToken() {
    return localStorage.getItem('accessToken')
  },
  
  // Lấy thông tin người dùng từ localStorage
  getUserInfo() {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
  },
  
  // Kiểm tra đã đăng nhập chưa
  isAuthenticated() {
    return !!this.getToken()
  },
  
  // Xóa thông tin đăng nhập
  clearAuthData() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('userInfo')
  }
}

export default authService 