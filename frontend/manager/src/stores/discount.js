import { defineStore } from 'pinia'
import { discountService } from '@/services/discountService'

export const useDiscountStore = defineStore('discount', {
  state: () => ({
    discounts: [],
    coupons: [],
    unusedCoupons: [],
    loading: false,
    error: null,
    pagination: {
      page: 0,
      size: 10,
      total: 0
    }
  }),

  actions: {
    async fetchDiscounts(page = 0, size = 10) {
      try {
        this.loading = true
        this.error = null
        const response = await discountService.getAllDiscounts(page, size)
        
        if (response && response.data && response.data.content) {
          this.discounts = response.data.content
          this.pagination = {
            page: response.data.number,
            size: response.data.size,
            total: response.data.totalElements
          }
        } else {
          this.discounts = []
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
        }
      } catch (error) {
        this.discounts = []
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải dữ liệu'
        console.error('Lỗi trong fetchDiscounts:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchCoupons(page = 0, size = 10) {
      try {
        this.loading = true
        this.error = null
        const response = await discountService.getAllCoupons(page, size)
        
        if (response && response.data && response.data.content) {
          this.coupons = response.data.content
          this.pagination = {
            page: response.data.number,
            size: response.data.size,
            total: response.data.totalElements
          }
        } else {
          this.coupons = []
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
        }
      } catch (error) {
        this.coupons = []
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải dữ liệu'
        console.error('Lỗi trong fetchCoupons:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async getCouponById(id) {
      try {
        this.loading = true
        this.error = null
        const response = await discountService.getCouponDetail(id)
        return response.data
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải thông tin mã giảm giá'
        console.error('Lỗi trong getCouponById:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async createCoupon(couponData) {
      try {
        this.loading = true
        this.error = null
        const response = await discountService.createCoupon(couponData)
        await this.fetchCoupons(this.pagination.page)
        return response.data
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tạo mã giảm giá'
        console.error('Lỗi trong createCoupon:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateCoupon(id, couponData) {
      try {
        this.loading = true
        this.error = null
        const response = await discountService.updateCoupon(id, couponData)
        await this.fetchCoupons(this.pagination.page)
        return response.data
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi cập nhật mã giảm giá'
        console.error('Lỗi trong updateCoupon:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteCoupon(id) {
      try {
        this.loading = true
        this.error = null
        await discountService.deleteCoupon(id)
        await this.fetchCoupons(this.pagination.page)
        return true
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi xóa mã giảm giá'
        console.error('Lỗi trong deleteCoupon:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async getDiscountById(id) {
      try {
        this.loading = true
        this.error = null
        const response = await discountService.getDiscountDetail(id)
        return response.data
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải thông tin chương trình khuyến mãi'
        console.error('Lỗi trong getDiscountById:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async createDiscount(discountData) {
      try {
        this.loading = true
        this.error = null
        const response = await discountService.createDiscount(discountData)
        await this.fetchDiscounts(this.pagination.page)
        return response.data
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tạo chương trình khuyến mãi'
        console.error('Lỗi trong createDiscount:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateDiscount(id, discountData) {
      try {
        this.loading = true
        this.error = null
        const response = await discountService.updateDiscount(id, discountData)
        await this.fetchDiscounts(this.pagination.page)
        return response.data
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi cập nhật chương trình khuyến mãi'
        console.error('Lỗi trong updateDiscount:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteDiscount(id) {
      try {
        this.loading = true
        this.error = null
        await discountService.deleteDiscount(id)
        await this.fetchDiscounts(this.pagination.page)
        return true
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi xóa chương trình khuyến mãi'
        console.error('Lỗi trong deleteDiscount:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchUnusedCoupons(page = 0, size = 100) {
      try {
        this.loading = true
        this.error = null
        const response = await discountService.getUnusedCoupons(page, size)
        
        if (response && response.data && response.data.content) {
          this.unusedCoupons = response.data.content
          return this.unusedCoupons
        } else {
          this.unusedCoupons = []
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
          return []
        }
      } catch (error) {
        this.unusedCoupons = []
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải danh sách mã giảm giá chưa được sử dụng'
        console.error('Lỗi trong fetchUnusedCoupons:', error)
        throw error
      } finally {
        this.loading = false
      }
    }
  }
}) 