import apiClient from './api';

export default {
  // Lấy tổng quan báo cáo theo nhân viên
  getEmployeeOrderOverview(employeeId, fromDate, toDate) {
    return apiClient.get(`/employees/${employeeId}/reports/order-overview`, {
      params: {
        fromDate,
        toDate
      }
    });
  },

  // Lấy doanh thu theo thời gian cho nhân viên
  getEmployeeOrderRevenue(employeeId, fromDate, toDate) {
    return apiClient.get(`/employees/${employeeId}/reports/order-revenue`, {
      params: {
        fromDate,
        toDate
      }
    });
  },

  // Lấy danh sách đơn hàng theo nhân viên
  getEmployeeOrders(employeeId, fromDate, toDate, page = 0, size = 10) {
    return apiClient.get(`/employees/${employeeId}/reports/orders`, {
      params: {
        fromDate,
        toDate,
        page,
        size
      }
    });
  },

  // Định dạng ngày giờ cho API
  formatDateTime(date) {
    if (!date) return null;
    if (typeof date === 'string') {
      // Kiểm tra xem chuỗi đã có thời gian chưa
      if (date.includes('T')) {
        return date;
      }
      // Nếu chỉ có ngày, thêm thời gian là 00:00:00
      return `${date}T00:00:00`;
    }
    
    // Nếu là đối tượng Date, convert sang chuỗi ISO
    if (date instanceof Date) {
      return date.toISOString().split('.')[0]; // loại bỏ phần mili giây
    }
    
    return null;
  }
}; 