<template>
  <v-app>
    <Header :employeeId="employeeId" :employeeName="employeeName" />

    <v-main>
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
      </v-container>
    </v-main>

    <!-- Sử dụng v-dialog của Vuetify thay cho component tự tạo -->
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
          :subtotal="subtotal"
          :discount="discount"
          :total="total"
          :employeeId="employeeId"
          @complete-order="completeOrder"
          @cancel="showPaymentModal = false"
      />
    </v-dialog>


  <!-- Dialog hiển thị hóa đơn -->
  <v-dialog v-model="billDialog" max-width="600px">
  <BillDialog 
    :bill-html="billHtml" 
    @close="closeBillDialog"
  />
</v-dialog>

  </v-app>
</template>

<script setup>
import {ref, computed, onMounted, watch} from 'vue';
import Header from './components/Header.vue';
import CategoryList from './components/CategoryList.vue';
import ProductGrid from './components/ProductGrid.vue';
import Cart from './components/Cart.vue';
import OrderCustomization from './components/OrderCustomization.vue';
import CustomerSearchModal from './components/CustomerSearchModal.vue';
import TableSelection from './components/TableSelection.vue';
import PaymentModal from './components/PaymentModal.vue';
import ProductService from './services/product.service';
import OrderService from './services/order.service';
import BillDialog from './components/BillDialog.vue';
import useMembership from './services/useMembership.js'

// Nhân viên hiện tại (trong thực tế sẽ lấy từ đăng nhập)
const employeeId = ref(4139561627);
const employeeName = ref('Phạm Văn A');

// Dữ liệu sản phẩm & danh mục
const products = ref([]);
const categories = ref([]);
const productsLoading = ref(true);
const selectedCategory = ref('all');

// Giỏ hàng
const cart = ref([]);
const selectedCustomer = ref(null);
const selectedTables = ref([]);
const selectedCoupon = ref(null);

// Modal states - Vuetify sẽ tự động xử lý việc hiển thị/ẩn modal thông qua v-model
const showCustomizationModal = ref(false);
const showCustomerModal = ref(false);
const showTableModal = ref(false);
const showPaymentModal = ref(false);

// Editing state
const editingProduct = ref(null);
const editingItemIndex = ref(-1);

const billDialog = ref(false);
const billHtml = ref('');

const calculatedSubtotal = ref(0);
const calculatedDiscount = ref(0);
const calculatedTotal = ref(0);
const isCalculating = ref(false);

// Gọi API tính toán đơn hàng mỗi khi có thay đổi
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
      employeeId: employeeId.value,
      customerId: selectedCustomer.value ? selectedCustomer.value.id : null,
      note: 'Đơn hàng từ app',
      products: prepareProductsForServer(),
      tables: selectedTables.value.map(table => ({
        serviceTableId: table.id
      })),
      discounts: selectedCoupon.value ? [{ discountId: selectedCoupon.value.id }] : []
    };

    // Gọi API tính toán
    const response = await OrderService.calculateOrder(orderData);
    console.log('Kết quả tính toán từ server:', response.data);

    // Cập nhật giá trị từ server
    if (response.data && response.data.data) {
      const result = response.data.data;
      calculatedSubtotal.value = result.subtotal || 0;
      calculatedDiscount.value = result.discount || 0;
      calculatedTotal.value = result.total || 0;
    }
  } catch (error) {
    console.error('Lỗi khi tính toán đơn hàng:', error);
    // Sử dụng giá trị dự phòng tính toán trên client
    fallbackCalculation();
  } finally {
    isCalculating.value = false;
  }
}

// Tính toán dự phòng trên client (sử dụng khi server lỗi)
function fallbackCalculation() {
  calculatedSubtotal.value = subtotal.value;
  calculatedDiscount.value = discount.value;
  calculatedTotal.value = total.value;
}

// Thêm watch để theo dõi thay đổi trong giỏ hàng và gọi API
watch(
  [cart, selectedCustomer, selectedTables, selectedCoupon],
  () => {
    calculateOrderFromServer();
  },
  { deep: true }
);

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

const { getMembershipDetails } = useMembership();


// Computed
const filteredProducts = computed(() => {
  if (selectedCategory.value === 'all') {
    return products.value;
  }
  return products.value.filter(product => product.category === selectedCategory.value);
});

// Cập nhật số lượng trong giỏ hàng
function updateCartQuantity(index, newQuantity) {
  if (index >= 0 && index < cart.value.length && newQuantity > 0) {
    // Cập nhật số lượng
    cart.value[index].quantity = newQuantity;

    // Tính lại tổng tiền cho item
    recalculateItemTotal(index);
  }
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

// Tính tổng tạm thời
const subtotal = computed(() => {
  return cart.value.reduce((sum, item) => {
    // Sử dụng item.total nếu có, ngược lại tính lại
    if (item.total !== undefined && !isNaN(Number(item.total))) {
      return sum + Number(item.total);
    }

    // Tính lại nếu không có item.total
    const basePrice = Number(item.price) || 0;
    const toppingTotal = (item.toppings || []).reduce((toppingSum, topping) => {
      return toppingSum + (Number(topping.price) || 0);
    }, 0);

    return sum + ((basePrice + toppingTotal) * item.quantity);
  }, 0);
});

// Tính giảm giá (từ coupon và membership)
const discount = computed(() => {
  let totalDiscount = 0;
  
  // 1. Giảm giá từ coupon nếu có
  if (selectedCoupon.value) {
    const couponDiscount = selectedCoupon.value.value || 0;
    
    if (selectedCoupon.value.type === 'PERCENTAGE') {
      // Giảm giá theo phần trăm
      totalDiscount += (subtotal.value * couponDiscount) / 100;
    } else {
      // Giảm giá theo số tiền cố định
      totalDiscount += Math.min(couponDiscount, subtotal.value);
    }
  }
  
  // 2. Giảm giá theo membership nếu khách hàng có membershipId
  if (selectedCustomer.value && selectedCustomer.value.membershipId) {
    // Lấy thông tin chi tiết về membership từ ID
    const membershipDetails = getMembershipDetails(selectedCustomer.value.membershipId);
    
    if (membershipDetails && membershipDetails.discountValue) {
      console.log('Membership discount details:', membershipDetails);
      
      if (membershipDetails.discountUnit === 'PERCENT') {
        // Giảm giá theo phần trăm
        const memberDiscount = (subtotal.value * membershipDetails.discountValue) / 100;
        totalDiscount += memberDiscount;
        console.log(`Giảm giá thành viên ${membershipDetails.discountValue}%:`, memberDiscount);
      } else {
        // Giảm giá theo số tiền cố định
        const memberDiscount = Math.min(membershipDetails.discountValue, subtotal.value - totalDiscount);
        totalDiscount += memberDiscount;
        console.log(`Giảm giá thành viên cố định:`, memberDiscount);
      }
    }
  }
  
  return totalDiscount;
});

// Tính tổng cộng
const total = computed(() => {
  return subtotal.value - discount.value;
});

// Các hàm xử lý sản phẩm
function getBasePrice(product) {
  if (product.prices && product.prices.length > 0) {
    return product.prices[0].price;
  }
  return 0;
}

// Product modal functions
function openProductDetails(product) {
  editingProduct.value = product;
  editingItemIndex.value = -1; // Chế độ thêm mới
  showCustomizationModal.value = true;
}

function closeProductModal() {
  showProductModal.value = false;
  editingProduct.value = null;
  editingItemIndex.value = -1;
}


// Methods
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

function openCustomizationModal(product) {
  editingProduct.value = product;
  editingItemIndex.value = -1; // Mode thêm mới
  showCustomizationModal.value = true;
}

// Hàm thêm vào giỏ hàng
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
}


function removeCartItem(index) {
  if (index >= 0 && index < cart.value.length) {
    cart.value.splice(index, 1);
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
  }
}

function openCustomerModal() {
  showCustomerModal.value = true;
}

function selectCustomer(customer) {
  selectedCustomer.value = customer;
  showCustomerModal.value = false;
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

function calculateSubtotal() {
  return cart.value.reduce((sum, item) => {
    return sum + (item.price * item.quantity);
  }, 0);
}

function calculateDiscount() {
  // Tính giảm giá dựa trên mã giảm giá hoặc thành viên
  let discount = 0;
  const subtotal = calculateSubtotal();

  // Áp dụng giảm giá từ coupon nếu có
  if (selectedCoupon.value) {
    if (selectedCoupon.value.discountUnit === 'PERCENT') {
      discount += subtotal * (selectedCoupon.value.discountValue / 100);
      if (selectedCoupon.value.maxDiscountAmount && discount > selectedCoupon.value.maxDiscountAmount) {
        discount = selectedCoupon.value.maxDiscountAmount;
      }
    } else {
      discount += selectedCoupon.value.discountValue;
    }
  }

  // Áp dụng giảm giá thành viên nếu có
  if (selectedCustomer.value && selectedCustomer.value.membership) {
    const membership = selectedCustomer.value.membership;
    if (membership.discountUnit === 'PERCENT') {
      discount += subtotal * (membership.discountValue / 100);
    } else {
      discount += membership.discountValue;
    }
  }

  return discount;
}

function calculateTotal() {
  return calculateSubtotal() - calculateDiscount();
}

async function completeOrder(paymentData) {
  try {
    const orderData = {
      employeeId: employeeId.value,
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

// Trong phần script của App.vue

function closeBillDialog() {
  console.log('Closing bill dialog');
  billDialog.value = false;
}



onMounted(() => {
  loadProducts();
});
</script>

<style>
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