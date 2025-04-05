<template>
  <v-container fluid class="pa-0 main-container">
    <div class="d-flex flex-grow-1 main-content">
      <div class="left-panel">
        <CategoryList
            :categories="categories"
            :selectedCategory="selectedCategory"
            @select-category="selectedCategory = $event"
        />

        <ProductGrid
            :products="filteredProducts"
            :loading="productsLoading"
            @add-to-cart="openCustomizationModal"
        />
      </div>

      <div class="right-panel">
        <Cart
            :cart="cart"
            :subtotal="subtotal"
            :discount="discount"
            :total="total"
            :customer="selectedCustomer"
            :tables="selectedTables"
            @remove-item="removeCartItem"
            @edit-item="editCartItem"
            @clear-cart="clearCart"
            @find-customer="openCustomerModal"
            @select-table="openTableModal"
            @checkout="openPaymentModal"
            @update-quantity="updateCartQuantity"
        />
      </div>
    </div>
    
    <!-- Dialogs -->
    <v-dialog
        v-model="showCustomizationModal"
        max-width="600px"
        persistent
    >
      <OrderCustomization
          :product="editingProduct"
          :editMode="editingItemIndex !== -1"
          :initialOptions="editingProduct?.options || {}"
          @add-to-cart="addToCart"
          @cancel="showCustomizationModal = false"
      />
    </v-dialog>

    <v-dialog
        v-model="showCustomerModal"
        max-width="600px"
        persistent
    >
      <CustomerSearchModal
          @select-customer="selectCustomer"
          @cancel="showCustomerModal = false"
      />
    </v-dialog>

    <v-dialog
        v-model="showTableModal"
        max-width="800px"
        persistent
    >
      <TableSelection
          :initial-tables="selectedTables"
          @select-tables="selectTables"
          @cancel="showTableModal = false"
      />
    </v-dialog>

    <v-dialog
        v-model="showPaymentModal"
        max-width="600px"
        persistent
    >
      <PaymentModal
          :cart="cart"
          :customer="selectedCustomer"
          :tables="selectedTables"
          :subtotal="calculatedSubtotal"
          :discount="calculatedDiscount"
          :total="calculatedTotal"
          :employeeId="employeeId"
          @complete-order="completeOrder"
          @cancel="showPaymentModal = false"
          @apply-coupon="applyCoupon"
          @remove-coupon="removeCoupon"
      />
    </v-dialog>

    <!-- Dialog hiển thị hóa đơn -->
    <v-dialog v-model="billDialog" max-width="600px">
      <BillDialog 
        :bill-html="billHtml" 
        @close="closeBillDialog"
      />
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import CategoryList from '../components/CategoryList.vue';
import ProductGrid from '../components/ProductGrid.vue';
import Cart from '../components/Cart.vue';
import OrderCustomization from '../components/OrderCustomization.vue';
import CustomerSearchModal from '../components/CustomerSearchModal.vue';
import TableSelection from '../components/TableSelection.vue';
import PaymentModal from '../components/PaymentModal.vue';
import BillDialog from '../components/BillDialog.vue';
import ProductService from '../services/product.service';
import OrderService from '../services/order.service';
import useMembership from '../services/useMembership.js';

// Lấy employeeId từ props
const props = defineProps({
  employeeId: {
    type: Number,
    required: true
  },
  employeeName: {
    type: String,
    required: true
  }
});

// Dữ liệu sản phẩm & danh mục
const products = ref([]);
const categories = ref([]);
const productsLoading = ref(true);
const selectedCategory = ref('all');

// Giỏ hàng - chuyển từ App.vue
const cart = ref([]);
const selectedCustomer = ref(null);
const selectedTables = ref([]);
const selectedCoupon = ref(null);

// Modal states - chuyển từ App.vue
const showCustomizationModal = ref(false);
const showCustomerModal = ref(false);
const showTableModal = ref(false);
const showPaymentModal = ref(false);

// Editing state - chuyển từ App.vue
const editingProduct = ref(null);
const editingItemIndex = ref(-1);

const billDialog = ref(false);
const billHtml = ref('');

const calculatedSubtotal = ref(0);
const calculatedDiscount = ref(0);
const calculatedTotal = ref(0);
const isCalculating = ref(false);

// Computed properties
const filteredProducts = computed(() => {
  if (selectedCategory.value === 'all') {
    return products.value;
  }
  return products.value.filter(product => product.category === selectedCategory.value);
});

// Alias cho calculatedSubtotal, calculatedDiscount, calculatedTotal
const subtotal = computed(() => calculatedSubtotal.value);
const discount = computed(() => calculatedDiscount.value);
const total = computed(() => calculatedTotal.value);

// Functions

// Mở modal tùy chỉnh sản phẩm
function openCustomizationModal(product) {
  editingProduct.value = product;
  editingItemIndex.value = -1; // Mode thêm mới
  showCustomizationModal.value = true;
}

// Thêm vào giỏ hàng
function addToCart(item) {
  if (editingItemIndex.value >= 0 && editingItemIndex.value < cart.value.length) {
    // Sửa item đã có
    cart.value[editingItemIndex.value] = item;
    recalculateItemTotal(editingItemIndex.value);
  } else {
    // Thêm item mới
    cart.value.push(item);
    recalculateItemTotal(cart.value.length - 1);
  }

  // Đóng modal sau khi thêm/sửa
  showCustomizationModal.value = false;
  editingProduct.value = null;
  editingItemIndex.value = -1;
  
  // Tính toán lại tổng tiền
  calculateOrderFromServer();
}

// Tính lại tổng tiền cho một item
function recalculateItemTotal(index) {
  const item = cart.value[index];

  // Giá cơ bản
  const basePrice = Number(item.price) || 0;

  // Tổng giá topping
  const toppingTotal = (item.toppings || []).reduce((total, topping) => {
    return total + (Number(topping.price) || 0);
  }, 0);

  // Tổng giá item = (giá cơ bản + tổng topping) * số lượng
  item.total = (basePrice + toppingTotal) * item.quantity;
}

function removeCartItem(index) {
  if (index >= 0 && index < cart.value.length) {
    cart.value.splice(index, 1);
    calculateOrderFromServer();
  }
}

function editCartItem(index) {
  if (index >= 0 && index < cart.value.length) {
    const item = cart.value[index];
    editingItemIndex.value = index;
    editingProduct.value = item.product;
    showCustomizationModal.value = true;
  }
}

function clearCart() {
  if (confirm('Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng?')) {
    cart.value = [];
    selectedCustomer.value = null;
    selectedTables.value = [];
    selectedCoupon.value = null;
    calculateOrderFromServer();
  }
}

// Cập nhật số lượng trong giỏ hàng
function updateCartQuantity(index, newQuantity) {
  if (index >= 0 && index < cart.value.length && newQuantity > 0) {
    // Cập nhật số lượng
    cart.value[index].quantity = newQuantity;

    // Tính lại tổng tiền cho item
    recalculateItemTotal(index);
    calculateOrderFromServer();
  }
}

function openCustomerModal() {
  showCustomerModal.value = true;
}

function selectCustomer(customer) {
  selectedCustomer.value = customer;
  showCustomerModal.value = false;
  calculateOrderFromServer();
}

function openTableModal() {
  showTableModal.value = true;
}

function selectTables(tables) {
  selectedTables.value = tables;
  showTableModal.value = false;
}

function openPaymentModal() {
  if (cart.value.length === 0) {
    alert('Giỏ hàng đang trống. Vui lòng thêm sản phẩm vào giỏ hàng.');
    return;
  }
  showPaymentModal.value = true;
}

async function calculateOrderFromServer() {
  if (cart.value.length === 0) {
    calculatedSubtotal.value = 0;
    calculatedDiscount.value = 0;
    calculatedTotal.value = 0;
    return;
  }

  isCalculating.value = true;

  try {
    // Chuẩn bị dữ liệu đơn hàng
    const orderData = {
      employeeId: props.employeeId,
      customerId: selectedCustomer.value ? selectedCustomer.value.id : null,
      note: 'Đơn hàng từ app',
      products: prepareProductsForServer(),
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

// Chuẩn bị dữ liệu sản phẩm để gửi lên server
function prepareProductsForServer() {
  const mainProducts = cart.value.map(item => ({
    productId: item.product.id,
    sizeId: item.size.id,
    quantity: item.quantity,
    option: item.options.join(', ')
  }));

  // Chuẩn bị mảng topping
  const toppingProducts = [];
  cart.value.forEach(item => {
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

// Tính toán dự phòng trên client (sử dụng khi server lỗi)
function fallbackCalculation() {
  // Tính subtotal (tổng tiền trước giảm giá)
  const subtotalValue = cart.value.reduce((sum, item) => {
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

// Hàm áp dụng mã giảm giá
function applyCoupon(couponData) {
  selectedCoupon.value = couponData;
  calculateOrderFromServer();
}

// Hàm xóa mã giảm giá
function removeCoupon() {
  selectedCoupon.value = null;
  calculateOrderFromServer();
}

// Hoàn tất thanh toán đơn hàng
async function completeOrder(paymentData) {
  try {
    const orderData = {
      employeeId: props.employeeId,
      customerId: selectedCustomer.value ? selectedCustomer.value.id : null,
      note: 'Đơn hàng từ app',
      products: prepareProductsForServer(),
      tables: selectedTables.value.map(table => ({
        serviceTableId: table.id
      })),
      discounts: selectedCoupon.value ? [{ discountId: selectedCoupon.value.id }] : []
    };

    // Tạo đơn hàng
    const orderResponse = await OrderService.createOrder(orderData);
    const orderId = orderResponse.data.orderId;

    // Khởi tạo thanh toán
    const initiateResponse = await OrderService.initiatePayment({
      orderId: orderId,
      paymentMethodId: paymentData.methodId
    });

    const paymentId = initiateResponse.data.paymentId;

    // Hoàn tất thanh toán
    const billing = await OrderService.completePayment(
        paymentId,
        paymentData.methodId,
        { amount: paymentData.amount }
    );

    billHtml.value = billing.data;
    billDialog.value = true;

    // Reset giỏ hàng và các trạng thái
    cart.value = [];
    selectedCustomer.value = null;
    selectedTables.value = [];
    selectedCoupon.value = null;

    alert('Đơn hàng đã được tạo và thanh toán thành công!');
    showPaymentModal.value = false;
  } catch (error) {
    console.error('Error completing order:', error);
    alert('Có lỗi xảy ra khi tạo đơn hàng. Vui lòng thử lại sau.');
  }
}

function closeBillDialog() {
  billDialog.value = false;
}

// Tải sản phẩm
async function loadProducts() {
  productsLoading.value = true;
  try {
    const response = await ProductService.getAvailableProducts();
    products.value = response.data;

    // Trích xuất danh mục từ sản phẩm
    const categorySet = new Set();
    products.value.forEach(product => {
      if (product.category) {
        categorySet.add(product.category);
      }
    });
    categories.value = ['all', ...Array.from(categorySet)];
  } catch (error) {
    console.error('Error loading products:', error);
    alert('Không thể tải danh sách sản phẩm');
  } finally {
    productsLoading.value = false;
  }
}

// Tải dữ liệu khi component được tạo
onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
.main-container {
  height: calc(100vh - 64px);
  overflow: hidden;
}

.main-content {
  height: 100%;
}

.left-panel {
  flex: 7;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-right: 1px solid rgba(0, 0, 0, 0.12);
}

.right-panel {
  flex: 3;
  display: flex;
  flex-direction: column;
  background-color: white;
  min-width: 360px;
}
</style> 