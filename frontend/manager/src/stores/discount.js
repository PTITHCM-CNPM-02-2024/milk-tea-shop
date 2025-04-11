import { defineStore } from 'pinia'
import { discountService } from '@/services/discountService'

export const useDiscountStore = defineStore('discount', {
  state: () => ({
    discounts: [],
    coupons: [],
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
        this.error = error.message || 'Đã xảy ra lỗi khi tải dữ liệu'
        console.error('Error fetching discounts:', error)
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
        this.error = error.message || 'Đã xảy ra lỗi khi tải dữ liệu'
        console.error('Error fetching coupons:', error)
      } finally {
        this.loading = false
      }
    }
  }
}) 