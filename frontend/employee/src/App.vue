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
                :subtotal="calculateSubtotal()"
                :discount="calculateDiscount()"
                :total="calculateTotal()"
                @remove-item="removeCartItem"
                @edit-item="editCartItem"
                @clear-cart="clearCart"
                @find-customer="openCustomerModal"
                @select-table="openTableModal"
                @checkout="openPaymentModal"
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
          :product="selectedProduct"
          :editMode="customizationEditMode"
          :editIndex="customizationEditIndex"
          :initialOptions="customizationInitialOptions"
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
          :subtotal="calculateSubtotal()"
          :discount="calculateDiscount()"
          :total="calculateTotal()"
          :employeeId="employeeId"
          @complete-order="completeOrder"
          @cancel="showPaymentModal = false"
      />
    </v-dialog>
  </v-app>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
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

// Nhân viên hiện tại (trong thực tế sẽ lấy từ đăng nhập)
const employeeId = ref(1);
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

// Trạng thái tùy chỉnh sản phẩm
const selectedProduct = ref(null);
const customizationEditMode = ref(false);
const customizationEditIndex = ref(-1);
const customizationInitialOptions = ref({});

// Computed
const filteredProducts = computed(() => {
  if (selectedCategory.value === 'all') {
    return products.value;
  }
  return products.value.filter(product => product.category === selectedCategory.value);
});

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
  selectedProduct.value = product;
  customizationEditMode.value = false;
  customizationEditIndex.value = -1;
  customizationInitialOptions.value = {};
  showCustomizationModal.value = true;
}

function addToCart(item) {
  if (customizationEditMode.value && customizationEditIndex.value >= 0) {
    // Edit mode
    cart.value[customizationEditIndex.value] = item;
  } else {
    // Add new item
    cart.value.push(item);
  }
  showCustomizationModal.value = false;
}

function editCartItem(index) {
  const item = cart.value[index];
  selectedProduct.value = item.product;
  customizationEditMode.value = true;
  customizationEditIndex.value = index;
  customizationInitialOptions.value = {
    size: item.size,
    quantity: item.quantity,
    options: item.options
  };
  showCustomizationModal.value = true;
}

function removeCartItem(index) {
  cart.value.splice(index, 1);
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
    // Chuẩn bị dữ liệu đơn hàng
    const orderData = {
      employeeId: employeeId.value,
      customerId: selectedCustomer.value ? selectedCustomer.value.id : null,
      note: paymentData.note || '',

      // Danh sách sản phẩm
      products: cart.value.map(item => ({
        productId: item.product.id,
        sizeId: item.size.id,
        quantity: item.quantity,
        option: item.options.join(', ')
      })),

      // Danh sách bàn
      tables: selectedTables.value.map(table => ({
        serviceTableId: table.id
      })),

      // Mã giảm giá
      discounts: selectedCoupon.value ? [{ discountId: selectedCoupon.value.id }] : []
    };

    // Tạo đơn hàng
    const orderResponse = await OrderService.createOrder(orderData);
    const orderId = orderResponse.data;

    // Khởi tạo thanh toán
    const initiateResponse = await OrderService.initiatePayment({
      orderId: orderId,
      paymentMethodId: paymentData.methodId
    });

    const paymentId = initiateResponse.data;

    // Hoàn tất thanh toán
    await OrderService.completePayment(
        paymentId,
        paymentData.methodId,
        { amount: paymentData.amount }
    );

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