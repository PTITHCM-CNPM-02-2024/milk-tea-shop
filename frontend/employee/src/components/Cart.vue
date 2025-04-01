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
        <v-list-item
            v-for="(item, index) in cart"
            :key="index"
            class="cart-item py-2"
        >
          <v-list-item-title class="font-weight-medium">{{ item.product.name }}</v-list-item-title>
          <v-list-item-subtitle class="text-caption">{{ item.size.name }}</v-list-item-subtitle>
          <v-list-item-subtitle v-if="item.options.length > 0" class="text-caption">
            {{ item.options.join(', ') }}
          </v-list-item-subtitle>
          <v-list-item-subtitle class="d-flex align-center mt-1">
            <span class="text-caption text-grey mr-1">SL:</span>
            <span class="font-weight-medium">{{ item.quantity }}</span>
          </v-list-item-subtitle>

          <template v-slot:append>
            <div class="d-flex flex-column align-end">
              <div class="text-primary font-weight-medium mb-2">
                {{ formatPrice(item.price * item.quantity) }}
              </div>
              <div class="d-flex">
                <v-btn
                    icon="mdi-pencil"
                    size="small"
                    variant="text"
                    color="primary"
                    @click.stop="$emit('edit-item', index)"
                ></v-btn>
                <v-btn
                    icon="mdi-close"
                    size="small"
                    variant="text"
                    color="error"
                    @click.stop="$emit('remove-item', index)"
                ></v-btn>
              </div>
            </div>
          </template>
        </v-list-item>
      </v-list>
    </div>

    <v-divider></v-divider>

    <v-card-text class="cart-summary bg-grey-lighten-4 pa-4">
      <div class="d-flex justify-space-between mb-1">
        <span class="text-body-2">Tạm tính:</span>
        <span class="text-body-2">{{ formatPrice(subtotal) }}</span>
      </div>
      <div class="d-flex justify-space-between mb-2">
        <span class="text-body-2">Giảm giá:</span>
        <span class="text-body-2">{{ formatPrice(discount) }}</span>
      </div>
      <v-divider class="my-2"></v-divider>
      <div class="d-flex justify-space-between mb-3">
        <span class="text-subtitle-1 font-weight-bold">Tổng cộng:</span>
        <span class="text-subtitle-1 font-weight-bold primary--text">{{ formatPrice(total) }}</span>
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
  'checkout'
]);

const customerName = computed(() => {
  if (props.customer) {
    return `${props.customer.firstName} ${props.customer.lastName}`;
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

function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}
</script>