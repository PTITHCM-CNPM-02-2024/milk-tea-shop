import { defineStore } from 'pinia'
import { ref } from 'vue'
import { reportService } from '@/services/reportService'

export const useReportStore = defineStore('report', () => {
  // State
  const overviewData = ref({
    totalProduct: 0,
    totalEmployee: 0,
    totalOrder: 0,
    totalCustomer: 0,
    maxOrderValue: 0,
    minOrderValue: 0,
    avgOrderValue: 0,
    sumOrderValue: 0
  })
  
  const orderRevenueData = ref([])
  const categoryRevenueData = ref([])
  const topProductsData = ref([])
  
  const loading = ref({
    overview: false,
    orderRevenue: false,
    categoryRevenue: false,
    topProducts: false
  })
  
  const error = ref({
    overview: null,
    orderRevenue: null,
    categoryRevenue: null,
    topProducts: null
  })
  
  // Actions
  // Lấy báo cáo tổng quan
  async function fetchOverviewReport() {
    loading.value.overview = true
    error.value.overview = null
    
    try {
      const response = await reportService.getOverviewReport()
      overviewData.value = response.data
      return response.data
    } catch (err) {
      error.value.overview = err.response?.data || 'Đã xảy ra lỗi khi tải báo cáo tổng quan'
      throw err
    } finally {
      loading.value.overview = false
    }
  }
  
  // Lấy báo cáo doanh thu theo danh mục
  async function fetchCategoryRevenueReport(fromDate = null, toDate = null) {
    loading.value.categoryRevenue = true
    error.value.categoryRevenue = null
    
    try {
      const response = await reportService.getCategoryRevenueReport(fromDate, toDate)
      categoryRevenueData.value = response.data || []
      return response.data
    } catch (err) {
      error.value.categoryRevenue = err.response?.data || 'Đã xảy ra lỗi khi tải báo cáo doanh thu theo danh mục'
      categoryRevenueData.value = []
      throw err
    } finally {
      loading.value.categoryRevenue = false
    }
  }
  
  // Lấy báo cáo doanh thu theo đơn hàng theo thời gian
  async function fetchOrderRevenueReport(fromDate, toDate) {
    loading.value.orderRevenue = true
    error.value.orderRevenue = null
    
    try {
      const response = await reportService.getOrderRevenueReport(fromDate, toDate)
      orderRevenueData.value = response.data || []
      return response.data
    } catch (err) {
      error.value.orderRevenue = err.response?.data || 'Đã xảy ra lỗi khi tải báo cáo doanh thu theo thời gian'
      orderRevenueData.value = []
      throw err
    } finally {
      loading.value.orderRevenue = false
    }
  }
  
  // Lấy báo cáo top sản phẩm bán chạy
  async function fetchTopProductsReport(fromDate = null, toDate = null, limit = 10) {
    loading.value.topProducts = true
    error.value.topProducts = null
    
    try {
      const response = await reportService.getTopProductsReport(fromDate, toDate, limit)
      topProductsData.value = response.data || []
      return response.data
    } catch (err) {
      error.value.topProducts = err.response?.data || 'Đã xảy ra lỗi khi tải báo cáo sản phẩm bán chạy'
      topProductsData.value = []
      throw err
    } finally {
      loading.value.topProducts = false
    }
  }
  
  // Helper functions để định dạng dữ liệu
  function formatNumber(value) {
    if (value === null || value === undefined) return '0'
    return new Intl.NumberFormat('vi-VN').format(value)
  }
  
  function formatCurrency(value) {
    if (value === null || value === undefined) return '0 đ'
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
  }
  
  function formatDateRange(fromDate, toDate) {
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('vi-VN')
    }
    
    return `${formatDate(fromDate)} - ${formatDate(toDate)}`
  }
  
  return {
    // State
    overviewData,
    orderRevenueData,
    categoryRevenueData,
    topProductsData,
    loading,
    error,
    
    // Actions
    fetchOverviewReport,
    fetchCategoryRevenueReport,
    fetchOrderRevenueReport,
    fetchTopProductsReport,
    
    // Helpers
    formatNumber,
    formatCurrency,
    formatDateRange
  }
})
