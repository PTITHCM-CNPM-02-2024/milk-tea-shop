import { defineStore } from 'pinia'
import { orderService } from '@/services/orderService'

export const useOrderStore = defineStore('order', {
  state: () => ({
    orders: [],
    currentOrder: null,
    loading: false,
    orderLoading: false,
    error: null,
    pagination: {
      page: 0,
      size: 10,
      total: 0
    }
  }),

  getters: {
    getOrderById: (state) => (id) => {
      return state.orders.find(order => order.orderId === id) || null
    }
  },

  actions: {
    // Lấy danh sách đơn hàng
    async fetchOrders(page = 0, size = 10) {
      try {
        this.loading = true
        this.error = null
        const response = await orderService.getAllOrders(page, size)
        
        if (response && response.data && response.data.content) {
          this.orders = response.data.content
          this.pagination = {
            page: response.data.number,
            size: response.data.size,
            total: response.data.totalElements
          }
        } else {
          this.orders = []
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
        }
      } catch (error) {
        this.orders = []
        this.error = error.message || 'Đã xảy ra lỗi khi tải dữ liệu đơn hàng'
        console.error('Error fetching orders:', error)
      } finally {
        this.loading = false
      }
    },

    // Lấy chi tiết đơn hàng 
    async fetchOrderById(orderId) {
      try {
        this.orderLoading = true
        this.error = null
        const response = await orderService.getOrderById(orderId)
        
        if (response && response.data) {
          this.currentOrder = response.data
        } else {
          this.currentOrder = null
          console.error('Invalid response format from API:', response)
          this.error = 'Định dạng dữ liệu không hợp lệ'
        }
      } catch (error) {
        this.currentOrder = null
        this.error = error.message || 'Đã xảy ra lỗi khi tải chi tiết đơn hàng'
        console.error('Error fetching order details:', error)
      } finally {
        this.orderLoading = false
      }
    },

    // Cập nhật trạng thái đơn hàng
    async updateOrderStatus(orderId, status) {
      try {
        this.loading = true
        this.error = null
        await orderService.updateOrderStatus(orderId, status)
        
        // Cập nhật lại chi tiết đơn hàng
        if (this.currentOrder && this.currentOrder.orderId === orderId) {
          await this.fetchOrderById(orderId)
        }
        
        // Cập nhật danh sách đơn hàng nếu có
        if (this.orders.length > 0) {
          const orderIndex = this.orders.findIndex(order => order.orderId === orderId)
          if (orderIndex !== -1) {
            this.orders[orderIndex].orderStatus = status
          }
        }
      } catch (error) {
        this.error = error.message || 'Đã xảy ra lỗi khi cập nhật trạng thái đơn hàng'
        console.error('Error updating order status:', error)
      } finally {
        this.loading = false
      }
    },

    // Tạo đơn hàng mới
    async createOrder(orderData) {
      try {
        this.loading = true
        this.error = null
        const response = await orderService.createOrder(orderData)
        return response.data
      } catch (error) {
        this.error = error.message || 'Đã xảy ra lỗi khi tạo đơn hàng mới'
        console.error('Error creating new order:', error)
        throw error
      } finally {
        this.loading = false
      }
    }
  }
}) 