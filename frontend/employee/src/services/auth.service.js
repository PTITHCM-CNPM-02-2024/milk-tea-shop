import apiClient from './api.js';

const AUTH_TOKEN_KEY = 'token';
const ACCOUNT_ID_KEY = 'accountId';
const EMPLOYEE_ID_KEY = 'employeeId';

const AuthService = {
  /**
   * Đăng nhập
   * @param {string} username - Tên đăng nhập
   * @param {string} password - Mật khẩu
   * @returns {Promise} - Kết quả đăng nhập
   */
  login(username, password) {
    return apiClient.post('/auth/login', {
      username,
      password
    }).then(response => {
      if (response && response.data) {
        // Lưu token vào localStorage
        localStorage.setItem(AUTH_TOKEN_KEY, response.data.token);
        localStorage.setItem(ACCOUNT_ID_KEY, response.data.id);
        
        // Thiết lập token cho các request tiếp theo
        this.setAuthHeader(response.data.token);
      }
      return response;
    });
  },
  
  /**
   * Lấy thông tin nhân viên theo ID tài khoản
   * @param {number} accountId - ID tài khoản
   * @returns {Promise} - Thông tin nhân viên
   */
  getEmployeeByAccountId(accountId) {
    return apiClient.get(`/employees/account/${accountId}`);
  },
  
  /**
   * Đăng xuất
   * @returns {Promise} - Kết quả đăng xuất
   */
  logout() {
    // Lấy thông tin người dùng hiện tại
    const token = this.getAuthToken();
    
    if (!token) {
      return Promise.resolve();
    }
    
    // Gọi API đăng xuất
    return apiClient.post('/auth/logout').finally(() => {
      // Xóa thông tin lưu trữ
      this.clearAuth();
    });
  },
  
  /**
   * Thiết lập token xác thực cho API client
   * @param {string} token - Token xác thực
   */
  setAuthHeader(token) {
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  },
  
  /**
   * Lấy token xác thực từ localStorage
   * @returns {string|null} - Token xác thực
   */
  getAuthToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
  },
  
  /**
   * Lấy ID tài khoản từ localStorage
   * @returns {string|null} - ID tài khoản
   */
  getAccountId() {
    return localStorage.getItem(ACCOUNT_ID_KEY);
  },
  
  /**
   * Lấy ID nhân viên từ localStorage
   * @returns {string|null} - ID nhân viên
   */
  getEmployeeId() {
    return localStorage.getItem(EMPLOYEE_ID_KEY);
  },
  
  /**
   * Lưu ID nhân viên vào localStorage
   * @param {string} id - ID nhân viên
   */
  setEmployeeId(id) {
    localStorage.setItem(EMPLOYEE_ID_KEY, id);
  },
  
  /**
   * Kiểm tra đã đăng nhập hay chưa
   * @returns {boolean} - Trạng thái đăng nhập
   */
  isAuthenticated() {
    return !!this.getAuthToken();
  },
  
  /**
   * Xóa thông tin xác thực
   */
  clearAuth() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(ACCOUNT_ID_KEY);
    localStorage.removeItem(EMPLOYEE_ID_KEY);
    delete apiClient.defaults.headers.common['Authorization'];
  }
};

export default AuthService; 