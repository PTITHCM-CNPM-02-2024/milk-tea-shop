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
      console.error('Lỗi trong fetchOrders:', err)
      throw err
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
      console.error('Lỗi trong fetchOrderById:', err)
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
      console.error('Lỗi trong fetchPaymentById:', err)
      throw err
    }
  }

  // Xuất báo cáo đơn hàng
  async function generateReport(type, format, filteredData = null) {
    try {
      // Lấy dữ liệu đơn hàng để xuất báo cáo
      let reportData = []
      
      if (type === 'all') {
        // Lấy tất cả đơn hàng, giới hạn 100 đơn hàng để tránh quá tải
        const response = await orderService.getOrders(0, 100)
        reportData = response.data.content || []
      } else if (type === 'filtered' && filteredData) {
        // Sử dụng dữ liệu đã lọc được truyền vào
        reportData = filteredData
      } else {
        // Sử dụng dữ liệu hiện tại
        reportData = orders.value
      }

      if (reportData.length === 0) {
        throw new Error('Không có dữ liệu đơn hàng để xuất báo cáo')
      }

      // Chuẩn bị dữ liệu để xuất báo cáo
      const exportData = reportData.map(order => ({
        'Mã đơn hàng': order.orderId,
        'Thời gian đặt': new Date(order.orderTime).toLocaleString('vi-VN'),
        'Tổng tiền': new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.totalAmount),
        'Thanh toán': new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.finalAmount),
        'Trạng thái': getOrderStatusText(order.orderStatus),
        'Khách hàng': order.customerName || 'Khách vãng lai',
        'Nhân viên': order.employeeName || 'N/A'
      }))

      // Xuất báo cáo theo định dạng được chọn
      if (format === 'excel') {
        exportToExcel(exportData, 'Báo_Cáo_Đơn_Hàng')
      } else if (format === 'csv') {
        exportToCSV(exportData, 'Báo_Cáo_Đơn_Hàng')
      }

      return true
    } catch (err) {
      error.value = err.message || 'Có lỗi xảy ra khi xuất báo cáo'
      console.error('Lỗi trong generateReport:', err)
      throw err
    }
  }

  // Hàm hỗ trợ xuất file Excel
  function exportToExcel(data, filename) {
    // Tạo workbook mới
    const XLSX = require('xlsx')
    const workbook = XLSX.utils.book_new()
    
    // Chuyển đổi dữ liệu thành worksheet
    const worksheet = XLSX.utils.json_to_sheet(data)
    
    // Thêm worksheet vào workbook
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Orders')
    
    // Xuất file
    XLSX.writeFile(workbook, `${filename}.xlsx`)
  }

  // Hàm hỗ trợ xuất file CSV
  function exportToCSV(data, filename) {
    // Tạo tiêu đề CSV
    const headers = Object.keys(data[0])
    let csvContent = headers.join(',') + '\n'
    
    // Thêm dữ liệu
    data.forEach(item => {
      const row = headers.map(header => {
        // Xử lý giá trị có dấu phẩy
        const cell = item[header] || ''
        return `"${cell.toString().replace(/"/g, '""')}"`
      })
      csvContent += row.join(',') + '\n'
    })
    
    // Tạo Blob và URL để tải xuống
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    
    // Tải xuống file
    const link = document.createElement('a')
    link.setAttribute('href', url)
    link.setAttribute('download', `${filename}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  // Hàm hỗ trợ lấy text trạng thái đơn hàng
  function getOrderStatusText(status) {
    switch (status) {
      case 'PROCESSING': return 'Đang xử lý'
      case 'COMPLETED': return 'Đã xuất hóa đơn'
      case 'CANCELED': return 'Đã hủy'
      default: return 'Không xác định'
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
    fetchPaymentById,
    generateReport
  }
}) 