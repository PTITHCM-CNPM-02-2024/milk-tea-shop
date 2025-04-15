import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import OrderService from '../services/order.service';

export const useCartStore = defineStore('cart', () => {
  // State
  const items = ref([]);
  const selectedCustomer = ref(null);
  const selectedTables = ref([]);
  const selectedCoupons = ref([]);
  const calculatedSubtotal = ref(0);
  const calculatedDiscount = ref(0);
  const calculatedTotal = ref(0);
  const isCalculating = ref(false);
  
  // Computed
  const subtotal = computed(() => calculatedSubtotal.value);
  const discount = computed(() => calculatedDiscount.value);
  const total = computed(() => calculatedTotal.value);
  
  // Actions
  function addItem(item) {
    items.value.push(item);
    recalculateItemTotal(items.value.length - 1);
    calculateOrderFromServer();
  }
  
  function updateItem(index, updatedItem) {
    if (index >= 0 && index < items.value.length) {
      items.value[index] = updatedItem;
      recalculateItemTotal(index);
      calculateOrderFromServer();
    }
  }
  
  function removeItem(index) {
    if (index >= 0 && index < items.value.length) {
      items.value.splice(index, 1);
      calculateOrderFromServer();
    }
  }
  
  function updateQuantity(index, quantity) {
    if (index >= 0 && index < items.value.length && quantity > 0) {
      items.value[index].quantity = quantity;
      recalculateItemTotal(index);
      calculateOrderFromServer();
    }
  }
  
  function clearCart() {
    items.value = [];
    selectedCustomer.value = null;
    selectedTables.value = [];
    selectedCoupons.value = [];
    calculatedSubtotal.value = 0;
    calculatedDiscount.value = 0;
    calculatedTotal.value = 0;
  }
  
  function recalculateItemTotal(index) {
    const item = items.value[index];
    
    // Giá cơ bản
    const basePrice = Number(item.price) || 0;
    
    // Tổng giá topping
    const toppingTotal = (item.toppings || []).reduce((total, topping) => {
      return total + (Number(topping.price) || 0);
    }, 0);
    
    // Tổng giá item = (giá cơ bản + tổng topping) * số lượng
    item.total = (basePrice + toppingTotal) * item.quantity;
  }
  
  function setCustomer(customer) {
    selectedCustomer.value = customer;
    calculateOrderFromServer();
  }
  
  function setTables(tables) {
    selectedTables.value = tables;
  }
  
  function applyCoupon(coupon) {
    const existingIndex = selectedCoupons.value.findIndex(c => c.id === coupon.id);
    if (existingIndex === -1) {
      selectedCoupons.value.push(coupon);
      calculateOrderFromServer();
    }
  }
  
  function removeCoupon(coupon) {
    if (!coupon) {
      selectedCoupons.value = [];
    } else {
      const index = selectedCoupons.value.findIndex(c => c.id === coupon.id);
      if (index !== -1) {
        selectedCoupons.value.splice(index, 1);
      }
    }
    calculateOrderFromServer();
  }
  
  // Chuẩn bị dữ liệu sản phẩm để gửi lên server
  function prepareProductsForServer() {
    if (items.value.length === 0) {
      return [];
    }
    
    // Sản phẩm chính
    const mainProducts = items.value.map(item => ({
      productId: item.product.id,
      sizeId: item.size.id,
      quantity: item.quantity,
      option: Array.isArray(item.options) ? item.options.join(', ') : (item.options || '')
    }));
    
    // Chuẩn bị mảng topping
    const toppingProducts = [];
    items.value.forEach((item, index) => {
      if (item.toppings && item.toppings.length > 0) {
        item.toppings.forEach(topping => {
          toppingProducts.push({
            productId: topping.id,
            sizeId: topping.sizeId || null,
            quantity: item.quantity,
            option: `Topping`,
            parentProductId: item.product.id
          });
        });
      }
    });
    
    return [...mainProducts, ...toppingProducts];
  }
  
  async function calculateOrderFromServer(employeeId) {
    if (items.value.length === 0) {
      calculatedSubtotal.value = 0;
      calculatedDiscount.value = 0;
      calculatedTotal.value = 0;
      return;
    }
    
    if (!employeeId) {
      console.error('Không có employeeId, sử dụng tính toán dự phòng');
      fallbackCalculation();
      return;
    }
    
    isCalculating.value = true;
    
    try {
      // Chuẩn bị dữ liệu đơn hàng
      const orderData = {
        employeeId: employeeId,
        customerId: selectedCustomer.value ? selectedCustomer.value.id : null,
        note: 'Tính toán đơn hàng',
        products: prepareProductsForServer(),
        discounts: selectedCoupons.value.map(coupon => ({ discountId: coupon.id }))
      };
      
      console.log('Tính toán đơn hàng với employeeId:', employeeId);
      
      // Kiểm tra xem có sản phẩm không
      if (!orderData.products || orderData.products.length === 0) {
        throw new Error('Không có sản phẩm để tính toán');
      }
      
      // Gọi API tính toán
      const response = await OrderService.calculateOrder(orderData);
      
      // Cập nhật giá trị từ server
      if (response && response.data) {
        const result = response.data;
        calculatedSubtotal.value = result.totalAmount || 0;
        calculatedDiscount.value = result.discountAmount || 0;
        calculatedTotal.value = result.finalAmount || 0;
      } else {
        throw new Error('Không nhận được dữ liệu từ server');
      }
    } catch (error) {
      console.error('Lỗi khi tính toán đơn hàng:', error);
      // Dùng tính toán dự phòng khi server lỗi
      fallbackCalculation();
    } finally {
      isCalculating.value = false;
    }
  }
  
  // Tính toán dự phòng trên client (sử dụng khi server lỗi)
  function fallbackCalculation() {
    // Tính subtotal (tổng tiền trước giảm giá)
    const subtotalValue = items.value.reduce((sum, item) => {
      if (item.total !== undefined && !isNaN(Number(item.total))) {
        return sum + Number(item.total);
      }
      const basePrice = Number(item.price) || 0;
      const toppingTotal = (item.toppings || []).reduce((toppingSum, topping) => {
        return toppingSum + (Number(topping.price) || 0);
      }, 0);
      return sum + ((basePrice + toppingTotal) * item.quantity);
    }, 0);
    
    calculatedSubtotal.value = subtotalValue;
    
    // Tính discount (giảm giá)
    let discountValue = 0;
    
    // Tính tổng giảm giá từ tất cả coupons
    selectedCoupons.value.forEach(coupon => {
      if (coupon.discountUnit === 'PERCENTAGE') {
        discountValue += (subtotalValue * coupon.discountValue) / 100;
      } else {
        discountValue += Math.min(coupon.discountValue, subtotalValue);
      }
    });
    
    calculatedDiscount.value = discountValue;
    
    // Tính total (tổng tiền sau giảm giá)
    calculatedTotal.value = subtotalValue - discountValue;
  }
  
  async function createOrder(employeeId, note = 'Đơn hàng từ app') {
    if (items.value.length === 0) {
      throw new Error('Giỏ hàng trống, không thể tạo đơn hàng');
    }
    
    if (!employeeId) {
      throw new Error('Thiếu thông tin nhân viên');
    }
    
    try {
      // Kiểm tra thông tin bàn nếu có
      const tables = selectedTables.value || [];
      
      // Chuẩn bị dữ liệu đơn hàng
      const orderData = {
        employeeId: employeeId,
        customerId: selectedCustomer.value ? selectedCustomer.value.id : null,
        note: note,
        products: prepareProductsForServer(),
        tables: tables.map(table => ({
          serviceTableId: table.id
        })),
        discounts: selectedCoupons.value.map(coupon => ({ discountId: coupon.id }))
      };
      
      // Kiểm tra dữ liệu đơn hàng
      if (!orderData.products || orderData.products.length === 0) {
        throw new Error('Không có sản phẩm trong đơn hàng');
      }
      
      // Tạo đơn hàng
      const orderResponse = await OrderService.createOrder(orderData);
      
      if (!orderResponse || !orderResponse.data) {
        throw new Error('Không nhận được phản hồi khi tạo đơn hàng');
      }
      
      return orderResponse.data;
    } catch (error) {
      console.error('Lỗi khi tạo đơn hàng:', error);
      throw error;
    }
  }
  
  return {
    // State
    items,
    selectedCustomer,
    selectedTables,
    selectedCoupons,
    isCalculating,
    
    // Computed
    subtotal,
    discount,
    total,
    
    // Actions
    addItem,
    updateItem,
    removeItem,
    updateQuantity,
    clearCart,
    setCustomer,
    setTables,
    applyCoupon,
    removeCoupon,
    calculateOrderFromServer,
    prepareProductsForServer,
    createOrder
  };
}); 