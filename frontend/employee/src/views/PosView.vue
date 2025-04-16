<template>
  <v-container fluid class="pa-0 main-container">
    <!-- Alert nội bộ trang -->
    <v-alert
      v-model="showAlert"
      :type="alertType"
      :icon="alertIcon"
      border="start"
      closable
      class="ma-2"
      style="position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); z-index: 100; max-width: 500px;"
    >
      {{ alertMessage }}
    </v-alert>
    
    <div class="d-flex flex-grow-1 main-content">
      <div class="left-panel">
        <CategoryList
            :categories="categoryStore.allCategories"
            :selectedCategory="categoryStore.selectedCategory"
            :products="productStore.products"
            :allProducts="productStore.allProducts"
            @select-category="handleCategorySelect"
        />

        <ProductGrid
            :products="filteredProducts"
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
          :applied-coupons="cartStore.selectedCoupons"
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
import { ref, onMounted, watch, computed } from 'vue';
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
import { useSnackbar } from '../helpers/useSnackbar';

// Sử dụng các store
const productStore = useProductStore();
const categoryStore = useCategoryStore();
const cartStore = useCartStore();

// Snackbar
const { showSuccess, showError, showInfo, showWarning } = useSnackbar();

// Alert trong trang
const showAlert = ref(false);
const alertType = ref('info');
const alertMessage = ref('');
const alertIcon = ref('mdi-information');

// Lấy employeeId và searchQuery từ props
const props = defineProps({
  employeeId: {
    type: Number,
    required: true
  },
  employeeName: {
    type: String,
    required: true
  },
  searchQuery: {
    type: String,
    default: ''
  }
});

// State cho tìm kiếm sản phẩm - sử dụng từ props
const searchQuery = computed(() => props.searchQuery || '');

// Lọc sản phẩm dựa trên từ khóa tìm kiếm
const filteredProducts = computed(() => {
  console.log('Tìm kiếm với từ khóa:', searchQuery.value);
  
  if (!searchQuery.value || !searchQuery.value.trim()) {
    // Nếu không có từ khóa tìm kiếm, trả về danh sách sản phẩm hiện tại
    return productStore.products;
  }
  
  // Chuyển từ khóa tìm kiếm về chữ thường
  const query = searchQuery.value.toLowerCase().trim();
  
  // Lọc sản phẩm dựa trên các tiêu chí
  return productStore.products.filter(product => {
    // Tìm trong tên sản phẩm
    if (product.name && product.name.toLowerCase().includes(query)) {
      return true;
    }
    
    // Tìm trong mô tả sản phẩm
    if (product.description && product.description.toLowerCase().includes(query)) {
      return true;
    }
    
    // Tìm trong tên danh mục
    if (product.category && typeof product.category === 'object' && 
        product.category.name && product.category.name.toLowerCase().includes(query)) {
      return true;
    }
    
    // Tìm theo mã sản phẩm hoặc ID
    if ((product.code && product.code.toLowerCase().includes(query)) || 
        (product.id && product.id.toString().includes(query))) {
      return true;
    }
    
    return false;
  });
});

// Theo dõi thay đổi từ khóa tìm kiếm từ props
watch(() => props.searchQuery, (newQuery) => {
  // Nếu có từ khóa tìm kiếm và đang ở danh mục cụ thể, chuyển về "Tất cả"
  if (newQuery && categoryStore.selectedCategory !== 'all') {
    categoryStore.selectCategory('all');
    // Load tất cả sản phẩm nếu đang lọc theo danh mục
    productStore.handleCategoryChange('all');
  }
  
  // Hiển thị thông báo nếu tìm kiếm không có kết quả
  if (newQuery && filteredProducts.value.length === 0) {
    setAlert('info', `Không tìm thấy sản phẩm phù hợp với "${newQuery}"`);
  } else if (showAlert.value && alertType.value === 'info') {
    // Ẩn alert info nếu đã có kết quả tìm kiếm
    showAlert.value = false;
  }
}, { immediate: true });

// Đặt thông tin alert
function setAlert(type, message) {
  alertType.value = type;
  alertMessage.value = message;
  
  // Đặt icon dựa trên loại
  switch (type) {
    case 'success':
      alertIcon.value = 'mdi-check-circle';
      break;
    case 'error':
      alertIcon.value = 'mdi-alert-circle';
      break;
    case 'warning':
      alertIcon.value = 'mdi-alert';
      break;
    default:
      alertIcon.value = 'mdi-information';
  }
  
  showAlert.value = true;
}

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
  // Thay đổi danh mục đã chọn trong store
  categoryStore.selectCategory(category);
  
  // Lọc sản phẩm từ dữ liệu đã tải (không gọi API)
  const categoryId = typeof category === 'object' ? category.id : category;
  productStore.handleCategoryChange(categoryId);
}

// Mở modal tùy chỉnh sản phẩm
async function openCustomizationModal(product) {
  try {
    // Lấy chi tiết sản phẩm từ API
    const productDetail = await productStore.fetchProductDetail(product.id);
    if (productDetail) {
      editingProduct.value = productDetail;
      editingItemIndex.value = -1; // Mode thêm mới
      showCustomizationModal.value = true;
    } else {
      setAlert('error', 'Không thể tải thông tin chi tiết sản phẩm');
    }
  } catch (error) {
    console.error('Lỗi khi tải chi tiết sản phẩm:', error);
    showError('Không thể tải thông tin chi tiết sản phẩm');
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
  
  // Tính toán lại tổng tiền
  cartStore.calculateOrderFromServer(props.employeeId);
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
          setAlert('error', 'Không thể tải thông tin chi tiết sản phẩm');
        }
      })
      .catch(error => {
        console.error('Lỗi khi tải chi tiết sản phẩm:', error);
        showError('Không thể tải thông tin chi tiết sản phẩm');
      });
  }
}

function clearCart() {
  if (window.confirm('Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng?')) {
    cartStore.clearCart();
    showInfo('Đã xóa tất cả sản phẩm khỏi giỏ hàng');
  }
}

// Cập nhật số lượng trong giỏ hàng
function updateCartQuantity(index, newQuantity) {
  cartStore.updateQuantity(index, newQuantity);
  
  // Tính toán lại tổng tiền
  cartStore.calculateOrderFromServer(props.employeeId);
}

function openCustomerModal() {
  showCustomerModal.value = true;
}

function selectCustomer(customer) {
  cartStore.setCustomer(customer);
  cartStore.calculateOrderFromServer(props.employeeId);
  showCustomerModal.value = false;
  showSuccess(`Đã chọn khách hàng: ${customer.name}`);
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
    setAlert('warning', 'Giỏ hàng đang trống. Vui lòng thêm sản phẩm vào giỏ hàng.');
    return;
  }
  showPaymentModal.value = true;
}

function applyCoupon(couponData) {
  // Áp dụng coupon mới vào danh sách
  cartStore.applyCoupon(couponData);
  cartStore.calculateOrderFromServer(props.employeeId);
  showSuccess(`Đã áp dụng mã giảm giá: ${couponData.code}`);
}

function removeCoupon(coupon) {
  // Xóa một coupon cụ thể khỏi danh sách
  cartStore.removeCoupon(coupon);
  cartStore.calculateOrderFromServer(props.employeeId);
  showInfo(`Đã xóa mã giảm giá: ${coupon.code}`);
}

// Hoàn tất thanh toán đơn hàng
async function completeOrder(paymentData) {
  try {
    // Hiển thị hộp thoại xác nhận
    const confirmed = window.confirm('Xác nhận thanh toán đơn hàng?');
    if (!confirmed) return;
    
    // Kiểm tra giỏ hàng
    if (cartStore.items.length === 0) {
      showAlert.value = true;
      alertType.value = 'error';
      alertMessage.value = 'Giỏ hàng đang trống, không thể thanh toán.';
      return;
    }
    
    // Tạo đơn hàng
    const orderData = await cartStore.createOrder(props.employeeId, paymentData.note || 'Đơn hàng từ app');
    
    if (!orderData || !orderData.orderId) {
      throw new Error('Không tạo được đơn hàng');
    }
    
    const orderId = orderData.orderId;

    // Khởi tạo thanh toán
    const initiateResponse = await OrderService.initiatePayment({
      orderId: orderId,
      paymentMethodId: paymentData.methodId,
      amount: cartStore.total // Sử dụng tổng tiền từ cartStore
    });

    if (!initiateResponse || !initiateResponse.data || !initiateResponse.data.paymentId) {
      throw new Error('Không khởi tạo được thanh toán');
    }

    const paymentId = initiateResponse.data.paymentId;

    // Hoàn tất thanh toán với số tiền khách hàng trả
    const billing = await OrderService.completePayment(
      paymentId,
      paymentData.methodId,
      { 
        amount: paymentData.amount,
        cashReceived: paymentData.cashReceived || paymentData.amount,
        cashReturned: paymentData.cashReturned || 0
      }
    );

    // Hiển thị hóa đơn
    if (billing && billing.data) {
      billHtml.value = billing.data;
      billDialog.value = true;
    }

    // Đóng modal thanh toán
    showPaymentModal.value = false;
    
    // Reset giỏ hàng
    cartStore.clearCart();
    
    // Hiển thị thông báo thành công
    showSuccess('Thanh toán đơn hàng thành công!');
  } catch (error) {
    console.error('Lỗi khi hoàn tất đơn hàng:', error);
    showAlert.value = true;
    alertType.value = 'error';
    alertMessage.value = error.message || 'Không thể hoàn tất thanh toán';
  }
}

function closeBillDialog() {
  billDialog.value = false;
}

// Tải dữ liệu khi component được tạo
onMounted(async () => {
  // Kiểm tra employeeId
  if (!props.employeeId) {
    showError('Không xác định được nhân viên hiện tại');
    return;
  }
  
  // Hiển thị thông tin nhân viên
  showInfo(`Nhân viên ID: ${props.employeeId} - ${props.employeeName}`);
  
  // Tải tất cả sản phẩm trước
  await productStore.fetchAllProducts();
  
  // Sau đó tải danh mục
  await categoryStore.fetchCategories();
  
  // Cài đặt danh mục được chọn là 'all'
  categoryStore.selectCategory('all');
  
  // Set up watcher cho bất kỳ thay đổi nào của giỏ hàng
  watch(
    () => [cartStore.items.length, cartStore.selectedCustomer, cartStore.selectedCoupons.length],
    () => {
      if (props.employeeId) {
        cartStore.calculateOrderFromServer(props.employeeId);
      } else {
        console.error('employeeId undefined trong watcher');
      }
    }
  );
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
  border-right: 1px solid rgba(var(--v-border-opacity, 1), 0.12);
}

.right-panel {
  flex: 3;
  display: flex;
  flex-direction: column;
  background-color: var(--v-theme-surface);
  min-width: 360px;
}
</style> 