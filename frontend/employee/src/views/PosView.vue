<template>
  <v-container fluid class="pa-0 main-container">
    <div class="d-flex flex-grow-1 main-content">
      <div class="left-panel">
        <CategoryList
            :categories="categoryStore.categories"
            :selectedCategory="categoryStore.selectedCategory"
            :products="productStore.products"
            @select-category="handleCategorySelect"
        />

        <ProductGrid
            :products="productStore.products"
            :loading="productStore.loading"
            @add-to-cart="openCustomizationModal"
        />
      </div>

      <div class="right-panel">
        <Cart
            :cart="cartStore.items"
            :subtotal="cartStore.subtotal"
            :discount="cartStore.discount"
            :total="cartStore.total"
            :customer="cartStore.selectedCustomer"
            :tables="cartStore.selectedTables"
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
          :initial-tables="cartStore.selectedTables"
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
          :cart="cartStore.items"
          :customer="cartStore.selectedCustomer"
          :tables="cartStore.selectedTables"
          :subtotal="cartStore.subtotal"
          :discount="cartStore.discount"
          :total="cartStore.total"
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
import { ref, onMounted, watch } from 'vue';
import CategoryList from '../components/CategoryList.vue';
import ProductGrid from '../components/ProductGrid.vue';
import Cart from '../components/Cart.vue';
import OrderCustomization from '../components/OrderCustomization.vue';
import CustomerSearchModal from '../components/CustomerSearchModal.vue';
import TableSelection from '../components/TableSelection.vue';
import PaymentModal from '../components/PaymentModal.vue';
import BillDialog from '../components/BillDialog.vue';
import OrderService from '../services/order.service';
import { useProductStore } from '../stores/productStore';
import { useCategoryStore } from '../stores/categoryStore';
import { useCartStore } from '../stores/cartStore';

// Sử dụng các store
const productStore = useProductStore();
const categoryStore = useCategoryStore();
const cartStore = useCartStore();

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

// Modal states
const showCustomizationModal = ref(false);
const showCustomerModal = ref(false);
const showTableModal = ref(false);
const showPaymentModal = ref(false);

// Editing state
const editingProduct = ref(null);
const editingItemIndex = ref(-1);

const billDialog = ref(false);
const billHtml = ref('');

// Xử lý khi chọn danh mục
function handleCategorySelect(category) {
  categoryStore.selectCategory(category);
  productStore.handleCategoryChange(category);
}

// Mở modal tùy chỉnh sản phẩm
async function openCustomizationModal(product) {
  // Lấy chi tiết sản phẩm từ API
  const productDetail = await productStore.fetchProductDetail(product.id);
  if (productDetail) {
    editingProduct.value = productDetail;
    editingItemIndex.value = -1; // Mode thêm mới
    showCustomizationModal.value = true;
  } else {
    alert('Không thể tải thông tin chi tiết sản phẩm');
  }
}

// Thêm vào giỏ hàng
function addToCart(item) {
  if (editingItemIndex.value >= 0) {
    // Sửa item đã có
    cartStore.updateItem(editingItemIndex.value, item);
  } else {
    // Thêm item mới
    cartStore.addItem(item);
  }

  // Đóng modal sau khi thêm/sửa
  showCustomizationModal.value = false;
  editingProduct.value = null;
  editingItemIndex.value = -1;
  
  // Tính toán lại tổng tiền
  cartStore.calculateOrderFromServer(props.employeeId);
}

function removeCartItem(index) {
  cartStore.removeItem(index);
}

function editCartItem(index) {
  if (index >= 0 && index < cartStore.items.length) {
    const item = cartStore.items[index];
    editingItemIndex.value = index;
    
    // Lấy chi tiết sản phẩm từ server
    productStore.fetchProductDetail(item.product.id)
      .then(productDetail => {
        if (productDetail) {
          editingProduct.value = productDetail;
          showCustomizationModal.value = true;
        } else {
          alert('Không thể tải thông tin chi tiết sản phẩm');
        }
      })
      .catch(error => {
        console.error('Lỗi khi tải chi tiết sản phẩm:', error);
        alert('Không thể tải thông tin chi tiết sản phẩm');
      });
  }
}

function clearCart() {
  if (confirm('Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng?')) {
    cartStore.clearCart();
  }
}

// Cập nhật số lượng trong giỏ hàng
function updateCartQuantity(index, newQuantity) {
  cartStore.updateQuantity(index, newQuantity);
}

function openCustomerModal() {
  showCustomerModal.value = true;
}

function selectCustomer(customer) {
  cartStore.setCustomer(customer);
  showCustomerModal.value = false;
}

function openTableModal() {
  showTableModal.value = true;
}

function selectTables(tables) {
  cartStore.setTables(tables);
  showTableModal.value = false;
}

function openPaymentModal() {
  if (cartStore.items.length === 0) {
    alert('Giỏ hàng đang trống. Vui lòng thêm sản phẩm vào giỏ hàng.');
    return;
  }
  showPaymentModal.value = true;
}

function applyCoupon(couponData) {
  cartStore.applyCoupon(couponData);
}

function removeCoupon() {
  cartStore.removeCoupon();
}

// Hoàn tất thanh toán đơn hàng
async function completeOrder(paymentData) {
  try {
    // Tạo đơn hàng
    const orderData = await cartStore.createOrder(props.employeeId);
    const orderId = orderData.orderId;

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
    cartStore.clearCart();

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

// Tải dữ liệu khi component được tạo
onMounted(async () => {
  // Tải danh mục
  await categoryStore.fetchCategories();
  
  // Nếu đã có danh mục, tải sản phẩm cho danh mục đầu tiên
  if (categoryStore.categories.length > 0) {
    productStore.handleCategoryChange(categoryStore.selectedCategory);
  }
  
  // Set up watcher cho bất kỳ thay đổi nào của giỏ hàng
  watch(() => [cartStore.items.length, cartStore.selectedCustomer], () => {
    cartStore.calculateOrderFromServer(props.employeeId);
  });
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