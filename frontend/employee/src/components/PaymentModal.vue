<template>
  <div class="modal-overlay">
    <div class="modal-backdrop" @click="$emit('cancel')"></div>
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Thanh toán</h5>
        <button class="close-btn" @click="$emit('cancel')">
          <i class="fas fa-times"></i>
        </button>
      </div>
      
      <div class="modal-body">
        <div class="payment-summary">
          <div class="summary-row">
            <span>Tạm tính:</span>
            <span>{{ formatPrice(subtotal) }}</span>
          </div>
          <!-- Phần coupon - thêm mới -->
          <div class="coupon-section">
            <div v-if="!appliedCoupon" class="coupon-form">
              <div class="coupon-input-group">
                <input
                    type="text"
                    v-model="couponCode"
                    placeholder="Nhập mã giảm giá"
                    class="coupon-input"
                    :disabled="isApplyingCoupon"
                />
                <button
                    @click="applyCoupon"
                    class="apply-coupon-btn"
                    :disabled="!couponCode || isApplyingCoupon"
                >
                  <span v-if="isApplyingCoupon">
                    <v-icon small>mdi-loading mdi-spin</v-icon>
                  </span>
                  <span v-else>Áp dụng</span>
                </button>
              </div>
              <div v-if="couponError" class="coupon-error">
                {{ couponError }}
              </div>
            </div>

            <div v-else class="applied-coupon">
              <div class="coupon-info">
                <div class="coupon-header">
                  <span class="coupon-name">{{ appliedCoupon.name }}</span>
                  <button @click="removeCoupon" class="remove-coupon-btn">
                    <v-icon small>mdi-close</v-icon>
                  </button>
                </div>
                <div class="coupon-detail">
                  <span class="coupon-code">Mã: {{ appliedCoupon.couponCode }}</span>
                  <span class="coupon-value">
                    {{ formatDiscount(appliedCoupon.discountUnit, appliedCoupon.discountValue) }}
                  </span>
                </div>
                <div v-if="appliedCoupon.description" class="coupon-description">
                  {{ appliedCoupon.description }}
                </div>
              </div>
            </div>
          </div>
          <div class="summary-row">
            <span>Giảm giá:</span>
            <span>{{ formatPrice(discount) }}</span>
          </div>
          <div class="summary-row total-row">
            <span>Tổng cộng:</span>
            <span>{{ formatPrice(total) }}</span>
          </div>
        </div>

        <!-- Phương thức thanh toán -->
        <div class="payment-methods my-4">
          <h4 class="mb-3">Chọn phương thức thanh toán</h4>

          <div class="payment-method-grid">
            <div
                v-for="method in paymentMethods"
                :key="method.id"
                :class="['payment-method-card', {
          'active': selectedMethod === method.id,
          'disabled': method.id !== 1
        }]"
                @click="method.id === 1 && selectPaymentMethod(method.id)"
            >
              <div class="payment-method-icon">
                <v-icon>{{getMethodIcon(method.name)}}</v-icon>
              </div>
              <div class="payment-method-content">
                <div class="payment-method-name">{{ formatMethodName(method.name) }}</div>
              </div>
              <div class="payment-method-check" v-if="selectedMethod === method.id">
                <v-icon color="primary">mdi-check-circle</v-icon>
              </div>
              <div class="payment-method-unavailable" v-if="method.id !== 1">
                <span>Chưa hỗ trợ</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="selectedMethod === 1" class="cash-payment mt-4">
          <div class="form-group">
            <label>Tiền khách đưa</label>
            <div class="input-group">
              <input
                type="number"
                class="form-control"
                v-model="cashAmount"
                min="0"
                step="1000"
              >
              <span class="input-group-text">VNĐ</span>
            </div>
          </div>
          
          <div class="quick-amounts">
            <button
              v-for="amount in quickAmounts"
              :key="amount"
              class="quick-amount-btn"
              @click="cashAmount = amount"
            >
              {{ formatPrice(amount) }}
            </button>
          </div>
          
          <div v-if="cashAmount >= total" class="change-amount mt-3">
            <div class="d-flex justify-content-between">
              <span>Tiền thối lại:</span>
              <span class="change-value">{{ formatPrice(cashAmount - total) }}</span>
            </div>
          </div>
        </div>
        
        <div class="form-group mt-4">
          <label>Ghi chú</label>
          <textarea class="form-control" v-model="note" rows="2" placeholder="Nhập ghi chú đơn hàng nếu có"></textarea>
        </div>
      </div>

      <div class="modal-footer">
        <button class="cancel-btn" @click="$emit('cancel')">Hủy</button>
        <button
            class="pay-btn"
            :disabled="!isPaymentValid || isProcessing"
            @click="completePayment"
        >
          <span v-if="isProcessing">Đang xử lý...</span>
          <span v-else>Thanh toán</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import PaymentService from "@/services/payment.service.js";
import OrderService from "@/services/order.service.js";

const props = defineProps({
  cart: {
    type: Array,
    required: true
  },
  customer: {
    type: Object,
    default: null
  },
  tables: {
    type: Array,
    default: () => []
  },
  subtotal: {
    type: Number,
    required: true
  },
  discount: {
    type: Number,
    required: true
  },
  total: {
    type: Number,
    required: true
  },
  employeeId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['complete-order', 'cancel', 'apply-coupon', 'remove-coupon']);

// Trạng thái
const isProcessing = ref(false);
const selectedMethod = ref(1);
const cashAmount = ref(0);
const note = ref('');
const paymentMethods = ref([
  { id: 1, name: 'Tiền mặt', icon: 'fa-money-bill-wave' },
  { id: 2, name: 'Thẻ ngân hàng', icon: 'fa-credit-card' },
  { id: 3, name: 'Ví điện tử', icon: 'fa-wallet' }
]);

// Xử lý coupon
const couponCode = ref('');
const couponError = ref('');
const isApplyingCoupon = ref(false);
const appliedCoupon = ref(null);

// Tải phương thức thanh toán từ API
async function loadPaymentMethods() {
  try {
    const response = await PaymentService.getAllPaymentMethods();
    if (response.data) {
      const apiMethods = response.data;

      // Cập nhật thông tin phương thức thanh toán từ API
      paymentMethods.value = apiMethods.map(method => ({
        id: method.id,
        name: method.name,
        description: method.description,
        // Giữ lại icon mặc định cho các phương thức
        icon: method.id === 1 ? 'fa-money-bill-wave' :
            method.id === 2 ? 'fa-credit-card' : 'fa-wallet'
      }));

      console.log('Đã tải phương thức thanh toán:', paymentMethods.value);
    }
  } catch (error) {
    console.error('Lỗi khi tải phương thức thanh toán:', error);
    // Giữ nguyên dữ liệu mặc định nếu API lỗi
  }
}


const quickAmounts = computed(() => {
  const roundedTotal = Math.ceil(props.total / 10000) * 10000;
  return [
    roundedTotal,
    roundedTotal + 10000,
    roundedTotal + 20000,
    roundedTotal + 50000,
    roundedTotal + 100000
  ];
});

const isPaymentValid = computed(() => {
  if (selectedMethod.value === 1) {
    return cashAmount.value >= props.total;
  }
  return true;
});

function selectPaymentMethod(methodId) {
  selectedMethod.value = methodId;
}

function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}

function completePayment() {
  if (!isPaymentValid.value) return;

  isProcessing.value = true;

  try {
    // Chuẩn bị dữ liệu thanh toán
    const paymentData = {
      methodId: selectedMethod.value,
      amount: cashAmount.value, // Sử dụng số tiền khách đưa thay vì tổng tiền từ server
      cashReceived: selectedMethod.value === 1 ? cashAmount.value : undefined,
      cashReturned: selectedMethod.value === 1 ? cashAmount.value - props.total : undefined,
      note: note.value
    };

    // Gửi dữ liệu lên component cha để xử lý
    emit('complete-order', paymentData);

    // Không cần setTimeout ở đây vì component cha sẽ đóng modal nếu thành công
  } catch (error) {
    console.error('Lỗi khi xử lý thanh toán:', error);
    alert('Có lỗi xảy ra khi xử lý thanh toán. Vui lòng thử lại sau.');
  } finally {
    isProcessing.value = false;
  }
}


function formatMethodName(name) {
  const nameMap = {
    'CASH': 'Tiền mặt',
    'VISA': 'Thẻ Visa',
    'BANKCARD': 'Thẻ ngân hàng',
    'CREDIT_CARD': 'Thẻ tín dụng',
    'E-WALLET': 'Ví điện tử'
  };

  return nameMap[name] || name;
}

// Lấy icon cho phương thức thanh toán
function getMethodIcon(name) {
  const iconMap = {
    'CASH': 'mdi-cash',
    'VISA': 'mdi-credit-card',
    'BANKCARD': 'mdi-credit-card',
    'CREDIT_CARD': 'mdi-credit-card-outline',
    'E-WALLET': 'mdi-wallet-outline'
  };

  return iconMap[name] || 'mdi-credit-card';
}

function formatDiscount(unit, value) {
  if (unit === 'PERCENTAGE') {
    return `${value}%`;
  } else {
    return formatPrice(value);
  }
}

// Áp dụng mã giảm giá
async function applyCoupon() {
  if (!couponCode.value) return;

  couponError.value = '';
  isApplyingCoupon.value = true;

  try {
    const response = await OrderService.getDiscountByCoupon(couponCode.value);

    if (response.data) {
      const couponData = response.data;

      // Kiểm tra tính hợp lệ
      if (!couponData.isActive) {
        couponError.value = 'Mã giảm giá không còn hiệu lực';
        return;
      }

      // Áp dụng coupon
      appliedCoupon.value = couponData;
      
      // Emit event để App.vue cập nhật và tính toán lại giá
      emit('apply-coupon', couponData);

      // Sau khi emit, giá trị subtotal, discount và total sẽ được cập nhật từ App.vue
      // và tự động cập nhật trong giao diện qua props binding

      // Reset input
      couponCode.value = '';
    } else {
      couponError.value = 'Không tìm thấy mã giảm giá';
    }
  } catch (error) {
    console.error('Lỗi khi áp dụng mã giảm giá:', error);
    if (error.response && error.response.data && error.response.data.message) {
      couponError.value = error.response.data.message;
    } else {
      couponError.value = 'Có lỗi xảy ra, vui lòng thử lại';
    }
  } finally {
    isApplyingCoupon.value = false;
  }
}

// Xóa mã giảm giá
function removeCoupon() {
  appliedCoupon.value = null;
  
  // Emit event để App.vue cập nhật và tính toán lại giá
  emit('remove-coupon');
  
  // Sau khi emit, giá trị subtotal, discount và total sẽ được cập nhật từ App.vue
}

// Khởi tạo
onMounted(() => {
  loadPaymentMethods();
  cashAmount.value = props.total;
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  background-color: white;
  border-radius: 0.5rem;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 1rem;
  border-bottom: 1px solid var(--light-gray);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  margin: 0;
  font-size: 1.25rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #6c757d;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
}

.payment-summary {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 0.5rem;
  margin-bottom: 1.5rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
}

.total-row {
  font-size: 1.2rem;
  font-weight: 600;
  margin-top: 0.5rem;
  padding-top: 0.5rem;
  border-top: 1px dashed var(--light-gray);
}

.divider {
  display: flex;
  align-items: center;
  margin: 1.5rem 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid var(--light-gray);
}

.divider span {
  padding: 0 1rem;
  color: #6c757d;
  font-size: 0.9rem;
}

.payment-methods {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.75rem;
}
.payment-method {
  background-color: white;
  border: 1px solid var(--light-gray);
  border-radius: 0.5rem;
  padding: 1rem 0.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-method.selected {
  background-color: rgba(142, 68, 173, 0.1);
  border-color: var(--primary-color);
}

.method-icon {
  font-size: 1.5rem;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.method-name {
  font-size: 0.85rem;
  text-align: center;
}

.quick-amounts {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.75rem;
}

.quick-amount-btn {
  background-color: #f8f9fa;
  border: 1px solid var(--light-gray);
  border-radius: 0.25rem;
  padding: 0.5rem;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-amount-btn:hover {
  background-color: var(--light-gray);
}

.change-amount {
  background-color: #e8f5e9;
  padding: 0.75rem;
  border-radius: 0.5rem;
}

.change-value {
  color: var(--success-color);
  font-weight: 600;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.payment-methods {
  background-color: white;
  border: 1px solid var(--light-gray);
  border-radius: 0.5rem;
  padding: 1rem 0.5rem;
  display: flex;
  flex-direction: column;
  align-items: start;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-methods h4 {
  font-size: 16px;
  margin-bottom: 15px;
  display: block;
  width: 100%;
}

.payment-method-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.payment-method-card {
  flex: 0 0 calc(40% - 6px);
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  background-color: #fff;
  transition: all 0.2s ease;
  cursor: pointer;
  position: relative;
  height: 70px;
  overflow: hidden;
}

@media (min-width: 768px) {
  .payment-method-card {
    flex: 0 0 calc(33.33% - 8px);
  }
}

.payment-method-card:hover:not(.disabled) {
  border-color: #2196F3;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.payment-method-card.active {
  border-color: #2196F3;
  background-color: #E3F2FD;
}

.payment-method-card.disabled {
  opacity: 0.7;
  cursor: not-allowed;
  background-color: #f5f5f5;
  border: 1px dashed #ccc;
}

.payment-method-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #F5F5F5;
  margin-right: 12px;
}

.payment-method-card.active .payment-method-icon {
  background-color: #BBDEFB;
}

.payment-method-content {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.payment-method-name {
  font-weight: 600;
  color: #263238;
  margin-bottom: 2px;
  font-size: 0.8rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}


.payment-method-check {
  position: absolute;
  top: 8px;
  right: 8px;
}

.payment-method-unavailable {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #F44336;
  color: white;
  font-size: 0.65rem;
  padding: 2px 6px;
  border-radius: 0 8px 0 8px;
  font-weight: 500;
}

.payment-method-card.disabled::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-linear-gradient(
      45deg,
      rgba(0, 0, 0, 0.03),
      rgba(0, 0, 0, 0.03) 10px,
      rgba(0, 0, 0, 0.06) 10px,
      rgba(0, 0, 0, 0.06) 20px
  );
  pointer-events: none;
}

.payment-method-card.disabled .payment-method-name,
.payment-method-card.disabled .payment-method-description {
  color: #9e9e9e;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  padding: 16px;
  border-top: 1px solid #e0e0e0;
  background-color: #f9f9f9;
}

.cancel-btn, .pay-btn {
  padding: 10px 24px;
  border-radius: 4px;
  font-weight: 500;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  min-width: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #424242;
  border: 1px solid #e0e0e0;
}

.cancel-btn:hover {
  background-color: #eeeeee;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.cancel-btn:active {
  background-color: #e0e0e0;
  transform: translateY(1px);
}

.pay-btn {
  background-color: #2196F3;
  color: white;
  margin-left: 12px;
}

.pay-btn:hover:not(:disabled) {
  background-color: #1976D2;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.pay-btn:active:not(:disabled) {
  background-color: #0D47A1;
  transform: translateY(1px);
}

.pay-btn:disabled {
  background-color: #90CAF9;
  cursor: not-allowed;
  opacity: 0.7;
}

/* CSS cho phần coupon */
.coupon-section {
  margin: 12px 0;
}

.coupon-input-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.coupon-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
}

.apply-coupon-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  min-width: 80px;
  display: flex;
  justify-content: center;
}

.apply-coupon-btn:hover:not(:disabled) {
  background-color: #43A047;
}

.apply-coupon-btn:disabled {
  background-color: #A5D6A7;
  cursor: not-allowed;
}

.coupon-error {
  color: #F44336;
  font-size: 12px;
  margin-top: 4px;
}

.applied-coupon {
  border: 1px solid #2196F3;
  border-radius: 4px;
  background-color: #E3F2FD;
  padding: 12px;
}

.coupon-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.coupon-name {
  font-weight: 600;
  color: #0D47A1;
  font-size: 14px;
}

.remove-coupon-btn {
  background: none;
  border: none;
  color: #F44336;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
}

.remove-coupon-btn:hover {
  color: #D32F2F;
}

.coupon-detail {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #455A64;
  margin-bottom: 4px;
}

.coupon-description {
  font-size: 12px;
  color: #546E7A;
  white-space: normal;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>