import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import OrderService from '../services/order.service';

export const useCartStore = defineStore('cart', () => {
  // State
  const items = ref([]);
  const selectedCustomer = ref(null);
  const selectedTables = ref([]);
  const selectedCoupon = ref(null);
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
    selectedCoupon.value = null;
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
    selectedCoupon.value = coupon;
    calculateOrderFromServer();
  }
  
  function removeCoupon() {
    selectedCoupon.value = null;
    calculateOrderFromServer();
  }
  
  // Chuẩn bị dữ liệu sản phẩm để gửi lên server
  function prepareProductsForServer(employeeId = 1) {
    if (items.value.length === 0) {
      return [];
    }
    
    const mainProducts = items.value.map(item => ({
      productId: item.product.id,
      sizeId: item.size.id,
      quantity: item.quantity,
      option: item.options.join(', ')
    }));
    
    // Chuẩn bị mảng topping
    const toppingProducts = [];
    items.value.forEach(item => {
      if (item.toppings && item.toppings.length > 0) {
        item.toppings.forEach(topping => {
          toppingProducts.push({
            productId: topping.id,
            sizeId: topping.sizeId || null,
            quantity: item.quantity,
            option: `Topping cho ${item.product.name}`
          });
        });
      }
    });
    
    return [...mainProducts, ...toppingProducts];
  }
  
  async function calculateOrderFromServer(employeeId = 1) {
    if (items.value.length === 0) {
      calculatedSubtotal.value = 0;
      calculatedDiscount.value = 0;
      calculatedTotal.value = 0;
      return;
    }
    
    isCalculating.value = true;
    
    try {
      // Chuẩn bị dữ liệu đơn hàng
      const orderData = {
        employeeId: employeeId,
        customerId: selectedCustomer.value ? selectedCustomer.value.id : null,
        note: 'Đơn hàng từ app',
        products: prepareProductsForServer(employeeId),
        discounts: selectedCoupon.value ? [{ discountId: selectedCoupon.value.id }] : []
      };
      
      // Gọi API tính toán
      const response = await OrderService.calculateOrder(orderData);
      
      // Cập nhật giá trị từ server
      if (response.data) {
        const result = response.data;
        calculatedSubtotal.value = result.totalAmount || 0;
        calculatedDiscount.value = result.discountAmount || 0;
        calculatedTotal.value = result.finalAmount || 0;
      }
    } catch (error) {
      console.error('Lỗi khi tính toán đơn hàng:', error);
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
    if (selectedCoupon.value) {
      if (selectedCoupon.value.type === 'PERCENTAGE') {
        discountValue += (subtotalValue * selectedCoupon.value.value) / 100;
      } else {
        discountValue += Math.min(selectedCoupon.value.value, subtotalValue);
      }
    }
    
    calculatedDiscount.value = discountValue;
    
    // Tính total (tổng tiền sau giảm giá)
    calculatedTotal.value = subtotalValue - discountValue;
  }
  
  async function createOrder(employeeId, note = 'Đơn hàng từ app') {
    try {
      const orderData = {
        employeeId: employeeId,
        customerId: selectedCustomer.value ? selectedCustomer.value.id : null,
        note: note,
        products: prepareProductsForServer(employeeId),
        tables: selectedTables.value.map(table => ({
          serviceTableId: table.id
        })),
        discounts: selectedCoupon.value ? [{ discountId: selectedCoupon.value.id }] : []
      };
      
      // Tạo đơn hàng
      const orderResponse = await OrderService.createOrder(orderData);
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
    selectedCoupon,
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