import api from './api'

export const discountService = {
  // Lấy danh sách chương trình khuyến mãi
  getAllDiscounts(page = 0, size = 10) {
    return api.get(`/discounts?page=${page}&size=${size}`)
  },

  // Lấy danh sách mã giảm giá
  getAllCoupons(page = 0, size = 10) {
    return api.get(`/coupons?page=${page}&size=${size}`)
  },

  // Lấy chi tiết khuyến mãi
  getDiscountDetail(id) {
    return api.get(`/discounts/${id}`)
  },

  // Tạo mới khuyến mãi
  createDiscount(data) {
    return api.post(`/discounts`, data)
  },

  // Cập nhật khuyến mãi
  updateDiscount(id, data) {
    return api.put(`/discounts/${id}`, data)
  },

  // Xóa khuyến mãi
  deleteDiscount(id) {
    return api.delete(`/discounts/${id}`)
  },

  // Lấy chi tiết mã giảm giá
  getCouponDetail(id) {
    return api.get(`/coupons/${id}`)
  },

  // Tạo mới mã giảm giá
  createCoupon(data) {
    return api.post(`/coupons`, data)
  },

  // Cập nhật mã giảm giá
  updateCoupon(id, data) {
    return api.put(`/coupons/${id}`, data)
  },

  // Xóa mã giảm giá
  deleteCoupon(id) {
    return api.delete(`/coupons/${id}`)
  },

  // Lấy danh sách coupon chưa được sử dụng
  getUnusedCoupons(page = 0, size = 100) {
    return api.get(`/coupons/unused?page=${page}&size=${size}`)
  }
}

export default discountService 