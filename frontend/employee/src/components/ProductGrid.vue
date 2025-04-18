<template>
  <div class="product-grid-container pa-3 overflow-auto">
    <v-progress-circular
        v-if="loading"
        indeterminate
        color="primary"
        size="70"
        class="ma-auto d-block mt-10"
    ></v-progress-circular>

    <v-container v-else-if="filteredProducts.length === 0" class="d-flex flex-column align-center justify-center py-12">
      <v-icon size="x-large" color="grey" class="mb-4">mdi-coffee</v-icon>
      <span class="text-subtitle-1 text-grey">Không có sản phẩm nào</span>
    </v-container>

    <v-row v-else>
      <v-col
          v-for="product in filteredProducts"
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
              :src="getProductImage(product)"
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
            <div class="d-flex align-center">
              <div class="mr-2 text-caption text-grey-darken-1">Từ</div>
              <div class="text-primary font-weight-bold price-display">
                {{ formatPrice(getProductPrice(product)) }}
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, computed } from 'vue';

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

// Lọc bỏ các sản phẩm topping
const filteredProducts = computed(() => {
  return props.products.filter(product => {
    // Kiểm tra nếu sản phẩm là topping dựa vào tên danh mục hoặc tên sản phẩm
    const categoryName = product.category && typeof product.category === 'object' 
      ? product.category.name?.toLowerCase() 
      : '';
    
    const productName = product.name?.toLowerCase() || '';
    
    const isTopping = categoryName.includes('topping') || productName.includes('topping');
    
    return !isTopping;
  });
});

// Lấy hình ảnh sản phẩm, kiểm tra cả hai trường hợp
function getProductImage(product) {
  return product.image_url || product.imageUrl || '/images/default-product.png';
}

function getProductPrice(product) {
  // Nếu có minPrice, sử dụng nó
  if (product.minPrice !== undefined && product.minPrice !== null) {
    return product.minPrice;
  }
  
  // Nếu không có minPrice, tìm giá thấp nhất từ mảng giá (như cũ)
  return getLowestPrice(product);
}

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

.price-display {
  font-size: 1.1rem;
  color: #FF6B00 !important;
  background-color: rgba(255, 107, 0, 0.1);
  padding: 2px 8px;
  border-radius: 12px;
}
</style>