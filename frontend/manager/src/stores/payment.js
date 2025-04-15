import { defineStore } from 'pinia'
import { paymentService } from '@/services/paymentService'

export const usePaymentStore = defineStore('payment', {
  state: () => ({
    payments: [],
    currentPayment: null,
    paymentMethods: [],
    loading: false,
    paymentLoading: false,
    error: null,
    pagination: {
      page: 0,
      size: 10,
      total: 0
    },
    paymentReport: {
      totalPayment: 0,
      totalAmount: 0,
      averageAmount: 0,
      paymentDetailResponses: []
    },
    reportLoading: false
  }),

  getters: {
    getPaymentById: (state) => (id) => {
      return state.payments.find(payment => payment.id === id) || null
    },
    getPaymentMethodById: (state) => (id) => {
      return state.paymentMethods.find(method => method.id === id) || null
    }
  },

  actions: {
    // Lấy danh sách thanh toán
    async fetchPayments(page = 0, size = 10) {
      try {
        this.loading = true
        this.error = null
        const response = await paymentService.getAllPayments(page, size)

        if (response && response.data && response.data.content) {
          this.payments = response.data.content
          this.pagination = {
            page: response.data.number,
            size: response.data.size,
            total: response.data.totalElements
          }
        } else {
          this.payments = []
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
        }
      } catch (error) {
        this.payments = []
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải dữ liệu thanh toán'
        console.error('Error fetching payments:', error)
      } finally {
        this.loading = false
      }
    },

    // Lấy báo cáo thanh toán theo tháng
    async fetchPaymentReport(year, month) {
      try {
        this.reportLoading = true
        this.error = null
        const response = await paymentService.getPaymentReport(month, year)

        if (response && response.data) {
          this.paymentReport = response.data
        } else {
          this.paymentReport = {
            totalPayment: 0,
            totalAmount: 0,
            averageAmount: 0,
            paymentDetailResponses: []
          }
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
        }
      } catch (error) {
        this.paymentReport = {
          totalPayment: 0,
          totalAmount: 0,
          averageAmount: 0,
          paymentDetailResponses: []
        }
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải báo cáo thanh toán'
        console.error('Error fetching payment report:', error)
      } finally {
        this.reportLoading = false
      }
    },

    // Lấy chi tiết thanh toán
    async fetchPaymentById(paymentId) {
      try {
        this.paymentLoading = true
        this.error = null
        const response = await paymentService.getPaymentById(paymentId)

        if (response && response.data) {
          this.currentPayment = response.data
        } else {
          this.currentPayment = null
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
        }
      } catch (error) {
        this.currentPayment = null
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải chi tiết thanh toán'
        console.error('Error fetching payment details:', error)
      } finally {
        this.paymentLoading = false
      }
    },

    // Thêm thanh toán cho đơn hàng
    async addPaymentToOrder(orderId, paymentData) {
      try {
        this.loading = true
        this.error = null
        const response = await paymentService.addPaymentToOrder(orderId, paymentData)
        return response.data
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi thêm thanh toán'
        console.error('Error adding payment to order:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    // Lấy danh sách phương thức thanh toán
    async fetchPaymentMethods() {
      try {
        this.loading = true
        this.error = null
        const response = await paymentService.getAllPaymentMethods()

        if (response && response.data) {
          this.paymentMethods = response.data
        } else {
          this.paymentMethods = []
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
        }
      } catch (error) {
        this.paymentMethods = []
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải phương thức thanh toán'
        console.error('Error fetching payment methods:', error)
      } finally {
        this.loading = false
      }
    },

    // Lấy chi tiết phương thức thanh toán
    async fetchPaymentMethodById(id) {
      try {
        this.loading = true
        this.error = null
        const response = await paymentService.getPaymentMethodById(id)

        if (response && response.data) {
          return response.data
        } else {
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
          return null
        }
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi tải chi tiết phương thức thanh toán'
        console.error('Error fetching payment method details:', error)
        return null
      } finally {
        this.loading = false
      }
    },

    // Thêm phương thức thanh toán
    async createPaymentMethod(data) {
      try {
        this.loading = true
        this.error = null
        const response = await paymentService.createPaymentMethod(data)
        await this.fetchPaymentMethods() // Tải lại danh sách sau khi thêm
        return response.data
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi thêm phương thức thanh toán'
        console.error('Error creating payment method:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    // Cập nhật phương thức thanh toán
    async updatePaymentMethod(id, data) {
      try {
        this.loading = true
        this.error = null
        const response = await paymentService.updatePaymentMethod(id, data)
        await this.fetchPaymentMethods() // Tải lại danh sách sau khi cập nhật
        return response.data
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi cập nhật phương thức thanh toán'
        console.error('Error updating payment method:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    // Xóa phương thức thanh toán
    async deletePaymentMethod(id) {
      try {
        this.loading = true
        this.error = null
        await paymentService.deletePaymentMethod(id)
        await this.fetchPaymentMethods() // Tải lại danh sách sau khi xóa
      } catch (error) {
        this.error = error.response?.data || 'Đã xảy ra lỗi khi xóa phương thức thanh toán'
        console.error('Error deleting payment method:', error)
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})
