import { defineComponent, computed } from 'vue';
import { RouterLink } from 'vue-router';

export default defineComponent({
  name: 'ProductCard',
  
  props: {
    product: {
      type: Object,
      required: true
    }
  },
  
  setup(props) {
    // Lấy ra giá thấp nhất từ thuộc tính minPrice hoặc tính toán nếu không có
    const productPrice = computed(() => {
      if (props.product.minPrice !== undefined && props.product.minPrice !== null) {
        return props.product.minPrice;
      }
      
      // Tính toán giá thấp nhất nếu không có minPrice
      if (!props.product.prices || !Array.isArray(props.product.prices) || props.product.prices.length === 0) {
        return 0;
      }
      
      return Math.min(...props.product.prices.map(price => price.price || 0));
    });
    
    // Format số thành định dạng tiền tệ VND
    const formatPrice = (price) => {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(price);
    };
    
    return {
      productPrice,
      formatPrice
    };
  },
  
  template: `
    <div class="product-card">
      <div class="product-image">
        <img :src="product.imageUrl || require('@/assets/images/no-image.png')" :alt="product.name">
      </div>
      <div class="product-info">
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-price">{{ formatPrice(productPrice) }}</p>
      </div>
    </div>
  `,
  
  styles: `
    .product-card {
      background-color: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      transition: transform 0.3s;
      cursor: pointer;
    }
    
    .product-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
    }
    
    .product-image {
      height: 160px;
      overflow: hidden;
    }
    
    .product-image img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .product-info {
      padding: 12px;
    }
    
    .product-name {
      margin: 0 0 8px;
      font-size: 1rem;
      font-weight: 600;
      color: #333;
    }
    
    .product-price {
      margin: 0;
      font-size: 0.9rem;
      font-weight: 700;
      color: #ff6b00;
    }
  `
}); 