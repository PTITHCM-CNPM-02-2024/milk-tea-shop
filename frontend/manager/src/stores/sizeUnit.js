import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { productService } from '@/services/productService'

export const useSizeUnitStore = defineStore('sizeUnit', () => {
  // State
  const productSizes = ref([])
  const units = ref([])
  const totalProductSizes = ref(0)
  const totalUnits = ref(0)
  const loading = ref(false)
  const error = ref(null)
  const currentSizePage = ref(0)
  const currentUnitPage = ref(0)
  const pageSize = ref(10)

  // Getters
  const totalSizePages = computed(() => Math.ceil(totalProductSizes.value / pageSize.value))
  const totalUnitPages = computed(() => Math.ceil(totalUnits.value / pageSize.value))

  // Lấy danh sách kích thước sản phẩm
  async function fetchProductSizes(page = 0, size = 10) {
    loading.value = true
    error.value = null

    try {
      currentSizePage.value = page

      const response = await productService.getProductSizes(page, size)
      productSizes.value = response.data.content || []
      totalProductSizes.value = response.data.totalElements || 0
      return response.data
    } catch (err) {
      console.error('Lỗi trong fetchProductSizes:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách kích thước sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Tạo kích thước sản phẩm mới
  async function createProductSize(sizeData) {
    loading.value = true
    error.value = null

    try {
      const response = await productService.createProductSize(sizeData)
      // Cập nhật danh sách kích thước sản phẩm
      await fetchProductSizes(currentSizePage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong createProductSize:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tạo kích thước sản phẩm mới'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Cập nhật kích thước sản phẩm
  async function updateProductSize(id, sizeData) {
    loading.value = true
    error.value = null

    try {
      const response = await productService.updateProductSize(id, sizeData)
      // Cập nhật danh sách kích thước sản phẩm
      await fetchProductSizes(currentSizePage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong updateProductSize:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật kích thước sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Xóa kích thước sản phẩm
  async function deleteProductSize(id) {
    loading.value = true
    error.value = null

    try {
      const response = await productService.deleteProductSize(id)
      // Cập nhật danh sách kích thước sản phẩm
      await fetchProductSizes(currentSizePage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong deleteProductSize:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi xóa kích thước sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Lấy danh sách đơn vị tính
  async function fetchUnits(page = 0, size = 10) {
    loading.value = true
    error.value = null

    try {
      currentUnitPage.value = page

      const response = await productService.getUnits(page, size)
      units.value = response.data.content || []
      totalUnits.value = response.data.totalElements || 0
      return response.data
    } catch (err) {
      console.error('Lỗi trong fetchUnits:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tải danh sách đơn vị tính'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Tạo đơn vị tính mới
  async function createUnit(unitData) {
    loading.value = true
    error.value = null

    try {
      const response = await productService.createUnit(unitData)
      // Cập nhật danh sách đơn vị tính
      await fetchUnits(currentUnitPage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong createUnit:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi tạo đơn vị tính mới'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Cập nhật đơn vị tính
  async function updateUnit(id, unitData) {
    loading.value = true
    error.value = null

    try {
      const response = await productService.updateUnit(id, unitData)
      // Cập nhật danh sách đơn vị tính
      await fetchUnits(currentUnitPage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong updateUnit:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi cập nhật đơn vị tính'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Xóa đơn vị tính
  async function deleteUnit(id) {
    loading.value = true
    error.value = null

    try {
      const response = await productService.deleteUnit(id)
      // Cập nhật danh sách đơn vị tính
      await fetchUnits(currentUnitPage.value, pageSize.value)
      return response.data
    } catch (err) {
      console.error('Lỗi trong deleteUnit:', err)
      error.value = err.response?.data?.detail || err.response?.data?.message || 'Đã xảy ra lỗi khi xóa đơn vị tính'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    // State
    productSizes,
    units,
    totalProductSizes,
    totalUnits,
    loading,
    error,
    currentSizePage,
    currentUnitPage,
    pageSize,

    // Getters
    totalSizePages,
    totalUnitPages,

    // Actions
    fetchProductSizes,
    createProductSize,
    updateProductSize,
    deleteProductSize,
    fetchUnits,
    createUnit,
    updateUnit,
    deleteUnit
  }
})
