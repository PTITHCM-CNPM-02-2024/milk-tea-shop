<template>
  <div class="product-grid-container pa-3 overflow-auto">
    <v-progress-circular
        v-if="loading"
        indeterminate
        color="primary"
        size="70"
        class="ma-auto d-block mt-10"
    ></v-progress-circular>

    <v-container v-else-if="products.length === 0" class="d-flex flex-column align-center justify-center py-12">
      <v-icon size="x-large" color="grey" class="mb-4">mdi-coffee</v-icon>
      <span class="text-subtitle-1 text-grey">Không có sản phẩm nào</span>
    </v-container>

    <v-row v-else>
      <v-col
          v-for="product in products"
          :key="product.id"
          cols="12" sm="6" md="4" lg="3" xl="2"
      >
        <v-card
            elevation="2"
            height="100%"
            class="product-card"
            @click="$emit('add-to-cart', product)"
        >
          <v-img
              :src="product.image_url || '/images/default-product.png'"
              :alt="product.name"
              height="120"
              cover
          >
            <template v-if="product.signature" v-slot:placeholder>
              <v-chip
                  color="accent"
                  size="small"
                  class="ma-2"
              >
                <v-icon size="small" start>mdi-star</v-icon>
                Đặc trưng
              </v-chip>
            </template>
          </v-img>

          <v-card-title class="text-subtitle-1 px-3 py-2">{{ product.name }}</v-card-title>

          <v-card-text class="px-3 py-1">
            <div class="text-primary font-weight-medium">
              {{ formatPrice(getLowestPrice(product)) }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  products: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['add-to-cart']);

function getLowestPrice(product) {
  if (!product.prices || product.prices.length === 0) return 0;
  return Math.min(...product.prices.map(p => p.price));
}

function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}
</script>

<style scoped>
.product-grid-container {
  flex: 1;
}

.product-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);
}
</style>