import api from './api'

export const authService = {
  // Đăng nhập
  login(username, password) {
    return api.post('/auth/login', { username, password })
  },
  
  // Đăng xuất
  logout() {
    return api.post('/auth/logout')
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