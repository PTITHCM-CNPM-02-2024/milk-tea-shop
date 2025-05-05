import apiClient from './api';
// import { jwtDecode } from 'jwt-decode'; // Không dùng nữa

const TOKEN_KEY = 'token';
// const ACCOUNT_ID_KEY = 'accountId'; // Không dùng nữa
// const EMPLOYEE_ID_KEY = 'employeeId'; // Không dùng nữa

class AuthService {
  /**
   * Đăng nhập
   * @param {string} username - Tên đăng nhập
   * @param {string} password - Mật khẩu
   * @returns {Promise} - Kết quả đăng nhập
   */
  async login(username, password) {
    try {
      const response = await apiClient.post('/auth/login', {
        username,
        password
      });

      if (response && response.data && response.data.accessToken) {
        // Interceptor trong api.js đã xử lý lưu token
        // localStorage.setItem(TOKEN_KEY, response.data.accessToken);
        // Không cần lưu accountId ở đây nữa
        // if (response.data.id) {
        //   localStorage.setItem(ACCOUNT_ID_KEY, response.data.id);
        // }
        // Trả về toàn bộ data để LoginView có thể lấy accountId
        return response; // Trả về nguyên response để LoginView có thể truy cập response.data.id
      } else {
        throw new Error('Dữ liệu đăng nhập không hợp lệ');
      }
    } catch (error) {
      console.error('Lỗi dịch vụ đăng nhập:', error);
      // Interceptor đã chuẩn hóa error.message
      throw error; // Ném lại lỗi đã được xử lý bởi interceptor
    }
  }
  
  /**
   * Lấy thông tin nhân viên theo ID tài khoản
   * @param {number} accountId - ID tài khoản
   * @returns {Promise} - Thông tin nhân viên
   */
  async getEmployeeByAccountId(accountId) {
    try {
      const response = await apiClient.get(`/employees/account/${accountId}`);
      return response;
    } catch (error) {
      console.error(`Lỗi khi lấy thông tin nhân viên cho account ID ${accountId}:`, error);
      throw error;
    }
  }
  
  /**
   * Đăng xuất
   * @returns {Promise} - Kết quả đăng xuất
   */
  logout() {
    localStorage.removeItem(TOKEN_KEY);
    // localStorage.removeItem(ACCOUNT_ID_KEY); // Không dùng nữa
    // localStorage.removeItem(EMPLOYEE_ID_KEY); // Không dùng nữa
    // Xóa header mặc định nếu cần (mặc dù interceptor sẽ không thêm nếu không có token)
    delete apiClient.defaults.headers.common['Authorization'];
    console.log('User logged out, token removed.');
  }
  
  /**
   * Lấy token xác thực từ localStorage
   * @returns {string|null} - Token xác thực
   */
  getAuthToken() {
    return localStorage.getItem(TOKEN_KEY);
  }
  
  /**
   * Kiểm tra đã đăng nhập hay chưa (chỉ kiểm tra sự tồn tại của token)
   * @returns {boolean} - Trạng thái đăng nhập
   */
  isAuthenticated() {
    const token = this.getAuthToken();
    // Chỉ cần kiểm tra xem token có tồn tại hay không.
    // Việc token có hợp lệ hay hết hạn sẽ do API và interceptor xử lý.
    return !!token;

    /* --- Logic cũ sử dụng jwt-decode đã bị xóa --- 
    if (!token) return false;

    try {
      const decoded = jwtDecode(token);
      const isExpired = decoded.exp * 1000 < Date.now();
      if (isExpired) {
        console.log('Token expired.');
        this.logout(); // Tự động logout nếu token hết hạn khi kiểm tra
        return false;
      }
      return true;
    } catch (error) {
      console.error('Invalid token:', error);
      this.logout(); // Tự động logout nếu token không hợp lệ
      return false;
    }
    */
  }
}

export default new AuthService(); 