<template>
  <v-card flat class="cart-container d-flex flex-column h-100">
    <v-card-title class="d-flex justify-space-between py-3">
      <span>Giỏ hàng</span>
      <v-btn
          v-if="cart.length > 0"
          variant="text"
          color="error"
          size="small"
          density="comfortable"
          prepend-icon="mdi-delete"
          @click="$emit('clear-cart')"
      >
        Xóa tất cả
      </v-btn>
    </v-card-title>

    <v-list class="pa-0">
      <v-list-item @click="$emit('find-customer')">
        <template v-slot:prepend>
          <v-avatar color="grey-lighten-3" class="mr-3">
            <v-icon color="primary">mdi-account</v-icon>
          </v-avatar>
        </template>

        <v-list-item-title class="text-caption text-grey">Khách hàng</v-list-item-title>
        <v-list-item-subtitle>{{ customerName }}</v-list-item-subtitle>

        <template v-slot:append>
          <v-icon>mdi-chevron-right</v-icon>
        </template>
      </v-list-item>

      <v-divider></v-divider>

      <v-list-item @click="$emit('select-table')">
        <template v-slot:prepend>
          <v-avatar color="grey-lighten-3" class="mr-3">
            <v-icon color="primary">mdi-table-chair</v-icon>
          </v-avatar>
        </template>

        <v-list-item-title class="text-caption text-grey">Bàn</v-list-item-title>
        <v-list-item-subtitle>{{ tableInfo }}</v-list-item-subtitle>

        <template v-slot:append>
          <v-icon>mdi-chevron-right</v-icon>
        </template>
      </v-list-item>
    </v-list>

    <v-divider></v-divider>

    <div class="cart-items-container flex-grow-1 overflow-auto">
      <div v-if="cart.length === 0" class="d-flex flex-column align-center justify-center text-center py-8">
        <v-icon size="x-large" color="grey" class="mb-4">mdi-cart-outline</v-icon>
        <span class="text-subtitle-1 text-grey">Giỏ hàng trống</span>
        <span class="text-caption text-grey">Chọn sản phẩm từ danh sách để thêm vào giỏ hàng</span>
      </div>

      <v-list v-else class="pa-0" select-strategy="classic">
        <v-list-item v-for="(item, index) in cart" :key="index" class="cart-item">
          <div class="d-flex flex-column w-100">
            <!-- Tên sản phẩm và giá cơ bản -->
            <div class="d-flex justify-space-between align-center">
              <div class="d-flex align-center">
                <span class="text-body-1 font-weight-medium">{{ item.product.name }}</span>
                <span class="text-caption ml-2 size-badge">{{ item.size.name }}</span>
              </div>
              <div class="item-price">{{ formatPrice(getSafePrice(item.price)) }}</div>
            </div>

            <!-- Option đường, đá -->
            <div class="text-caption text-grey mt-1">
        <span v-for="(option, optIndex) in item.options" :key="`opt-${optIndex}`" class="mr-2">
          {{ option }}
        </span>
            </div>

            <!-- Topping với giá riêng -->
            <div v-if="item.toppings && item.toppings.length > 0" class="mt-1">
              <div v-for="(topping, tIndex) in item.toppings" :key="`top-${tIndex}`"
                   class="text-caption text-grey d-flex justify-space-between">
                <span class="topping-name">+ {{ topping.name }}</span>
                <span class="topping-price">{{ formatPrice(getSafePrice(topping.price)) }}</span>
              </div>
            </div>

            <!-- Ghi chú -->
            <div v-if="item.note" class="text-caption text-grey-darken-1 mt-1 font-italic">
              <v-icon size="x-small" class="mr-1">mdi-note-text</v-icon>
              {{ item.note }}
            </div>

            <!-- Đường kẻ phân cách -->
            <v-divider class="my-2" v-if="item.toppings && item.toppings.length > 0"></v-divider>

            <!-- Số lượng và tổng tiền -->
            <div class="d-flex justify-space-between align-center mt-1">
              <div class="quantity-controls d-flex align-center">
                <v-btn icon density="compact" variant="text" size="x-small"
                       @click="$emit('update-quantity', index, item.quantity - 1)"
                       :disabled="item.quantity <= 1">
                  <v-icon>mdi-minus</v-icon>
                </v-btn>
                <span class="quantity-value mx-2">{{ item.quantity }}</span>
                <v-btn icon density="compact" variant="text" size="x-small"
                       @click="$emit('update-quantity', index, item.quantity + 1)">
                  <v-icon>mdi-plus</v-icon>
                </v-btn>
              </div>

              <div class="d-flex align-center">
                <!-- Hiển thị tổng tiền của mỗi item (bao gồm topping * số lượng) -->
                <span class="text-body-2 font-weight-bold primary--text mr-2">
            {{ formatPrice(calculateItemTotal(item)) }}
          </span>

                <div class="item-actions">
                  <v-btn icon density="compact" variant="text" size="small"
                         @click="$emit('edit-item', index)">
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                  <v-btn icon density="compact" variant="text" size="small"
                         color="error" @click="$emit('remove-item', index)">
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </div>
              </div>
            </div>
          </div>
        </v-list-item>
      </v-list>
    </div>

    <v-divider></v-divider>

    <v-card-text class="cart-summary bg-grey-lighten-4 pa-4">
      <div class="d-flex justify-space-between mb-1">
        <span class="text-body-2">Tạm tính:</span>
        <span class="text-body-2">{{ formatPrice(calculateSubtotal()) }}</span>
      </div>
      <div class="d-flex justify-space-between mb-2">
        <span class="text-body-2">Giảm giá:</span>
        <span class="text-body-2">{{ formatPrice(getSafePrice(props.discount)) }}</span>
      </div>
      <v-divider class="my-2"></v-divider>
      <div class="d-flex justify-space-between mb-3">
        <span class="text-subtitle-1 font-weight-bold">Tổng cộng:</span>
        <span class="text-subtitle-1 font-weight-bold primary--text">
        {{ formatPrice(calculateTotal()) }}
      </span>
      </div>

      <v-btn
          block
          color="success"
          size="large"
          :disabled="cart.length === 0"
          prepend-icon="mdi-cash-register"
          @click="$emit('checkout')"
      >
        Thanh toán
      </v-btn>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { defineProps, defineEmits, computed } from 'vue';

const props = defineProps({
  cart: {
    type: Array,
    required: true
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
  customer: {
    type: Object,
    default: null
  },
  tables: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits([
  'remove-item',
  'edit-item',
  'clear-cart',
  'find-customer',
  'select-table',
  'checkout',
  'update-quantity'
]);

// Tính tổng tiền cho mỗi item (bao gồm cả topping)
function calculateItemTotal(item) {
  if (!item) return 0;

  // Lấy giá cơ bản của sản phẩm
  const basePrice = getSafePrice(item.price);

  // Tính tổng giá của tất cả topping
  const toppingTotal = (item.toppings || []).reduce((total, topping) => {
    return total + getSafePrice(topping.price);
  }, 0);

  // Tổng giá của 1 item = (giá cơ bản + tổng giá topping) * số lượng
  return (basePrice + toppingTotal) * item.quantity;
}

// Tính tổng tạm thời của cả giỏ hàng
function calculateSubtotal() {
  return props.cart.reduce((sum, item) => {
    return sum + calculateItemTotal(item);
  }, 0);
}

// Tính tổng cộng (sau khi trừ giảm giá)
function calculateTotal() {
  return calculateSubtotal() - getSafePrice(props.discount);
}

// Hàm an toàn để lấy giá (tránh NaN)
function getSafePrice(price) {
  const numPrice = Number(price);
  if (isNaN(numPrice)) {
    console.warn('Phát hiện giá trị NaN trong giỏ hàng', price);
    return 0;
  }
  return numPrice;
}

// Định dạng giá tiền
function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
}

const customerName = computed(() => {
  if (props.customer) {
    const firstName = props.customer.firstName || '';
    const lastName = props.customer.lastName || '';
    const phone = props.customer.phone || '';

    if (firstName || lastName) {
      return `${firstName} ${lastName} - ${phone}`.trim();
    } else {
      return phone || 'Chưa chọn';
    }
  }
  return 'Chưa chọn';
});

const tableInfo = computed(() => {
  if (props.tables && props.tables.length > 0) {
    if (props.tables.length === 1) {
      return props.tables[0].name;
    }
    return `${props.tables[0].name} và ${props.tables.length - 1} bàn khác`;
  }
  return 'Chưa chọn';
});
</script>

<style scoped>
.topping-name {
  margin-left: 8px;
  color: #666;
}

.topping-price {
  color: #666;
}

.size-badge {
  background-color: #f0f0f0;
  padding: 2px 6px;
  border-radius: 10px;
  margin-left: 4px;
}
</style>