import apiClient from './api';

class MembershipService {
  /**
   * Lấy danh sách các loại thành viên (memberships)
   * @param {number} page - Số trang (mặc định: 0)
   * @param {number} size - Kích thước trang (mặc định: 10)
   * @returns {Promise} Phản hồi từ API
   */
  async getMemberships(page = 0, size = 10) {
    try {
      const response = await apiClient.get('/memberships', {
        params: {
          page,
          size
        }
      });
      return response; // apiClient đã xử lý ApiResponse wrapper
    } catch (error) {
      console.error('Lỗi khi lấy danh sách thành viên:', error);
      throw error;
    }
  }
  
  /**
   * Lấy thông tin chi tiết của một loại thành viên theo ID
   * @param {number} id - ID của loại thành viên
   * @returns {Promise} Phản hồi từ API
   */
  async getMembershipById(id) {
    try {
      const response = await apiClient.get(`/memberships/${id}`);
      return response; // apiClient đã xử lý ApiResponse wrapper
    } catch (error) {
      console.error(`Lỗi khi lấy thông tin thành viên có id ${id}:`, error);
      throw error;
    }
  }
}

export default new MembershipService();
