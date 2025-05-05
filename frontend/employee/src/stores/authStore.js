import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import AuthService from '../services/auth.service';
import apiClient from '../services/api'; // Cần apiClient để gọi endpoint /me

export const useAuthStore = defineStore('auth', () => {
  // --- State --- 
  const token = ref(AuthService.getAuthToken()); // Khởi tạo token từ localStorage
  const accountId = ref(null);
  const employeeId = ref(null);
  const employeeName = ref('');
  const userRole = ref(null); // Có thể thêm thông tin role nếu API trả về
  const status = ref('idle'); // idle | loading | success | error
  const error = ref(null);

  // --- Getters --- 
  const isLoggedIn = computed(() => !!token.value);
  const isLoading = computed(() => status.value === 'loading');
  const authError = computed(() => error.value);
  const currentUser = computed(() => ({
    accountId: accountId.value,
    employeeId: employeeId.value,
    name: employeeName.value,
    role: userRole.value
  }));

  // --- Actions --- 

  /**
   * Xử lý đăng nhập
   */
  async function login(credentials) {
    status.value = 'loading';
    error.value = null;
    try {
      const loginResponse = await AuthService.login(credentials.username, credentials.password);
      
      if (loginResponse && loginResponse.data && loginResponse.data.id) {
        // Lưu token vào state (AuthService/interceptor đã lưu vào localStorage)
        token.value = loginResponse.data.accessToken || AuthService.getAuthToken(); 
        accountId.value = loginResponse.data.id;

        // Sau khi có accountId, lấy thông tin nhân viên
        await fetchEmployeeDetails(accountId.value);
        
        status.value = 'success';
        return true;
      } else {
        throw new Error('Phản hồi đăng nhập không hợp lệ.');
      }
    } catch (err) {
      console.error("Auth Store Login Error:", err);
      token.value = null; // Đảm bảo xóa token khỏi state nếu login lỗi
      resetUserInfo();
      error.value = err.message || 'Đăng nhập thất bại.';
      status.value = 'error';
      return false;
    }
  }

  /**
   * Lấy chi tiết thông tin nhân viên (thường gọi nội bộ sau login hoặc init)
   */
  async function fetchEmployeeDetails(accId) {
    if (!accId) return;
    // Không đặt status loading ở đây để tránh chồng chéo với status login/init
    try {
      const employeeResponse = await AuthService.getEmployeeByAccountId(accId);
      if (employeeResponse && employeeResponse.data) {
        employeeId.value = employeeResponse.data.id;
        employeeName.value = employeeResponse.data.lastName +' '+ employeeResponse.data.firstName || 'Nhân viên';
      } else {
        // Nếu tài khoản tồn tại nhưng không có nhân viên liên kết
        console.warn(`Không tìm thấy thông tin nhân viên cho accountId: ${accId}`);
        employeeId.value = null;
        employeeName.value = 'N/A'; // Hoặc giá trị mặc định khác
      }
    } catch (err) {
      console.error("Auth Store Fetch Employee Error:", err);
      // Không nên đặt status error ở đây vì có thể chỉ là lỗi phụ
      error.value = err.message || 'Lỗi lấy thông tin nhân viên.'; 
      employeeId.value = null;
      employeeName.value = ''; // Reset nếu lỗi
    }
  }
  
  /**
   * Lấy thông tin người dùng hiện tại từ API (dùng token)
   * Cần endpoint backend ví dụ: /auth/me hoặc /accounts/me
   */
  async function fetchCurrentUserInfo() {
    if (!token.value) {
        console.log('No token, skipping fetchCurrentUserInfo')
        return; // Không có token, không cần fetch
    }
    
    status.value = 'loading';
    error.value = null;
    try {
        // !!! THAY THẾ ENDPOINT NÀY BẰNG API THỰC TẾ CỦA BẠN !!!
        console.log('Auth Store: Fetching current user info (/auth/me - placeholder)...');
        const response = await apiClient.get('/auth/me'); // Endpoint giả định

        if (response && response.data) {
            // Giả sử response trả về cấu trúc chứa accountId, employeeId, name, role...
            accountId.value = response.data.accountId;
            employeeId.value = response.data.employeeId;
            employeeName.value = response.data.name || 'Nhân viên';
            userRole.value = response.data.role?.name || null; // Lấy role nếu có
            
            // Nếu API /me không trả về employeeId/name, có thể cần gọi fetchEmployeeDetails sau
            if (!employeeId.value && accountId.value) {
                await fetchEmployeeDetails(accountId.value);
            }

            status.value = 'success';
            console.log('Auth Store: User info fetched successfully', currentUser.value);
        } else {
            throw new Error('Không nhận được dữ liệu người dùng hợp lệ từ API /me');
        }
    } catch (err) {
        console.error("Auth Store Fetch Current User Error:", err);
        error.value = err.message || 'Lỗi tải thông tin người dùng.';
        status.value = 'error';
        // Nếu lỗi (đặc biệt là 401), nên logout
        if (err.response && err.response.status === 401) {
            logout(); // Gọi action logout để reset state
        } else {
           // Với lỗi khác, có thể chỉ reset thông tin user?
           resetUserInfo(); 
        }
    }
  }

  /**
   * Đăng xuất
   */
  function logout() {
    AuthService.logout(); // Xóa token khỏi localStorage
    token.value = null;
    resetUserInfo();
    status.value = 'idle';
    error.value = null;
    console.log('Auth Store: User logged out');
    // Có thể cần chuyển hướng ở đây hoặc để component gọi xử lý
    // import router from '../router'; // Không nên import router trong store
    // router.push('/login'); 
  }

  /**
   * Reset thông tin user về trạng thái mặc định
   */
  function resetUserInfo() {
      accountId.value = null;
      employeeId.value = null;
      employeeName.value = '';
      userRole.value = null;
  }

  /**
   * Khởi tạo trạng thái xác thực khi ứng dụng tải (thường gọi từ App.vue)
   */
  async function initializeAuth() {
      console.log('Auth Store: Initializing...')
      if (token.value) {
          console.log('Auth Store: Token found, attempting to fetch user info.');
          await fetchCurrentUserInfo();
      } else {
          console.log('Auth Store: No token found on initialization.');
          status.value = 'idle'; // Đảm bảo status đúng nếu không có token
      }
  }

  // --- Trả về state, getters, actions --- 
  return {
    // State
    token,
    accountId,
    employeeId,
    employeeName,
    userRole,
    status,
    error,
    // Getters
    isLoggedIn,
    isLoading,
    authError,
    currentUser,
    // Actions
    login,
    logout,
    initializeAuth,
    fetchCurrentUserInfo // Có thể expose nếu cần gọi lại thủ công
  };
}); 