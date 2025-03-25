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
          <div class="summary-row">
            <span>Giảm giá:</span>
            <span>{{ formatPrice(discount) }}</span>
          </div>
          <div class="summary-row total-row">
            <span>Tổng cộng:</span>
            <span>{{ formatPrice(total) }}</span>
          </div>
        </div>
        
        <div class="divider">
          <span>Phương thức thanh toán</span>
        </div>
        
        <div class="payment-methods">
          <div
            v-for="method in paymentMethods"
            :key="method.id"
            :class="['payment-method', selectedMethod === method.id ? 'selected' : '']"
            @click="selectPaymentMethod(method.id)"
          >
            <div class="method-icon">
              <i :class="['fas', method.icon]"></i>
            </div>
            <div class="method-name">{{ method.name }}</div>
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
        <button class="btn btn-outline-secondary" @click="$emit('cancel')">Hủy</button>
        <button
          class="btn btn-success"
          :disabled="!isPaymentValid"
          @click="completePayment"
        >
          <i class="fas fa-check me-2"></i> Hoàn tất thanh toán
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

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

const emit = defineEmits(['complete-order', 'cancel']);

const paymentMethods = [
  { id: 1, name: 'Tiền mặt', icon: 'fa-money-bill-wave' },
  { id: 2, name: 'Thẻ ngân hàng', icon: 'fa-credit-card' },
  { id: 3, name: 'Ví điện tử', icon: 'fa-wallet' }
];

const selectedMethod = ref(1);
const cashAmount = ref(0);
const note = ref('');

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
  const paymentData = {
    methodId: selectedMethod.value,
    amount: props.total,
    note: note.value
  };
  
  emit('complete-order', paymentData);
}
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

.modal-footer {
  padding: 1rem;
  border-top: 1px solid var(--light-gray);
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}
</style>