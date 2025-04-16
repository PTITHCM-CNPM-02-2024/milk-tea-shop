import api from './api'

export const reportService = {
  // Lấy báo cáo tổng quan
  getOverviewReport() {
    return api.get('/reports/overview')
  },

  // Lấy báo cáo doanh thu theo danh mục
  getCategoryRevenueReport(fromDate = null, toDate = null) {
    return api.get('/reports/cat-revenue', {
      params: {
        from: fromDate,
        to: toDate
      }
    })
  },

  // Lấy báo cáo doanh thu theo đơn hàng theo thời gian
  getOrderRevenueReport(fromDate, toDate) {
    return api.get('/reports/order-revenue', {
      params: {
        from: fromDate,
        to: toDate
      }
    })
  },

  // Lấy báo cáo top sản phẩm bán chạy
  getTopProductsReport(fromDate = null, toDate = null, limit = 10) {
    return api.get('/reports/top-products', {
      params: {
        from: fromDate,
        to: toDate,
        limit: limit
      }
    })
  }
}
