import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { orderService } from '@/services/orderService'
import { paymentService } from '@/services/paymentService'

export const useOrderStore = defineStore('order', () => {
  // State
  const orders = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)
  const orderLoading = ref(false)
  const error = ref(null)
  const pagination = ref({
    page: 0,
    size: 10,
    total: 0
  })

  // Lấy danh sách đơn hàng
  async function fetchOrders(page = 0, size = 10) {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderService.getOrders(page, size)
      orders.value = response.data.content || []
      pagination.value = {
        page: page,
        size: size,
        total: response.data.totalElements || 0
      }
    } catch (err) {
      error.value = err.message || 'Có lỗi xảy ra khi lấy danh sách đơn hàng'
      console.error('Error fetching orders:', err)
    } finally {
      loading.value = false
    }
  }

  // Lấy chi tiết đơn hàng theo ID
  async function fetchOrderById(orderId) {
    orderLoading.value = true
    error.value = null
    
    try {
      const response = await orderService.getOrderById(orderId)
      currentOrder.value = response.data
      return response.data
    } catch (err) {
      error.value = err.message || 'Có lỗi xảy ra khi lấy chi tiết đơn hàng'
      console.error('Error fetching order details:', err)
      throw err
    } finally {
      orderLoading.value = false
    }
  }

  // Lấy chi tiết thanh toán theo ID
  async function fetchPaymentById(paymentId) {
    error.value = null
    
    try {
      const response = await paymentService.getPaymentById(paymentId)
      return response.data
    } catch (err) {
      error.value = err.message || 'Có lỗi xảy ra khi lấy chi tiết thanh toán'
      console.error('Error fetching payment details:', err)
      throw err
    }
  }

  return {
    // State
    orders,
    currentOrder,
    loading,
    orderLoading,
    error,
    pagination,
    
    // Actions
    fetchOrders,
    fetchOrderById,
    fetchPaymentById
  }
}) 