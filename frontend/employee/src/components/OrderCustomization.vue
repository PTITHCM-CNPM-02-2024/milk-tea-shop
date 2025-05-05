<template>
  <v-card class="order-customization">
    <v-toolbar color="primary" density="compact">
      <v-toolbar-title>{{ editMode ? 'Chỉnh sửa sản phẩm' : 'Tùy chỉnh sản phẩm' }}</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon @click="$emit('cancel')">
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </v-toolbar>

    <v-card-text>
      <div class="d-flex mt-2 mb-4">
        <v-img
            :src="getProductImage(product)"
            :alt="product.name"
            width="80"
            height="80"
            cover
            class="rounded-lg"
        ></v-img>

        <div class="ml-4">
          <h3 class="text-h6 mb-1">{{ product.name }}</h3>
          <p class="text-caption text-grey">{{ product.description || 'Không có mô tả' }}</p>
        </div>
      </div>

      <v-divider class="mb-4"></v-divider>

      <!-- Size selection -->
      <h4 class="text-subtitle-1 font-weight-medium mb-2">Kích cỡ</h4>
      <v-btn-toggle
          v-model="selectedSizeIndex"
          mandatory
          density="comfortable"
          color="primary"
          class="mb-4"
      >
        <v-btn
            v-for="(size, index) in product.prices"
            :key="index"
            :value="index"
            class="size-btn text-center px-3"
        >
          <div>
            <div>{{ size.size }}</div>
            <div class="text-caption">{{ formatPrice(size.price) }}</div>
          </div>
        </v-btn>
      </v-btn-toggle>

      <!-- Sugar options -->
      <h4 class="text-subtitle-1 font-weight-medium mb-2">Tùy chọn đường</h4>
      <v-chip-group
          v-model="selectedSugar"
          mandatory
          class="mb-4"
      >
        <v-chip
            v-for="sugar in sugarOptions"
            :key="sugar"
            :value="sugar"
            filter
            variant="elevated"
        >
          {{ sugar }}
        </v-chip>
      </v-chip-group>

      <!-- Ice options -->
      <h4 class="text-subtitle-1 font-weight-medium mb-2">Tùy chọn đá</h4>
      <v-chip-group
          v-model="selectedIce"
          mandatory
          class="mb-4"
      >
        <v-chip
            v-for="ice in iceOptions"
            :key="ice"
            :value="ice"
            filter
            variant="elevated"
        >
          {{ ice }}
        </v-chip>
      </v-chip-group>

      <h4 class="text-subtitle-1 font-weight-medium mb-2">Topping</h4>
      <v-row dense class="mb-4">
        <v-col
            v-if="loading"
            cols="12"
        >
          <v-progress-linear indeterminate color="primary"></v-progress-linear>
          <p class="text-caption text-center mt-2">Đang tải danh sách topping...</p>
        </v-col>

        <template v-else>
          <v-col
              v-for="topping in availableToppings"
              :key="topping.id"
              cols="12"
              sm="6"
          >
            <v-checkbox
                v-model="selectedToppings"
                :label="`${topping.name} (+${formatPrice(getToppingPrice(topping))})`"
                :value="topping"
                hide-details
                density="compact"
            ></v-checkbox>
          </v-col>

          <v-col v-if="availableToppings.length === 0" cols="12">
            <p class="text-caption text-center">Không có topping nào khả dụng.</p>
          </v-col>
        </template>
      </v-row>


      <!-- Quantity -->
      <h4 class="text-subtitle-1 font-weight-medium mb-2">Số lượng</h4>
      <div class="d-flex align-center mb-4">
        <v-btn
            icon
            variant="outlined"
            density="comfortable"
            @click="decreaseQuantity"
            :disabled="quantity <= 1"
        >
          <v-icon>mdi-minus</v-icon>
        </v-btn>

        <span class="mx-4 text-h6">{{ quantity }}</span>

        <v-btn
            icon
            variant="outlined"
            density="comfortable"
            @click="quantity++"
        >
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </div>

      <!-- Total -->
      <v-divider class="mb-4"></v-divider>

      <div class="d-flex justify-space-between align-center">
        <span class="text-subtitle-1">Thành tiền:</span>
        <span class="text-h6 primary--text font-weight-bold">{{ formatPrice(calculateItemTotal()) }}</span>
      </div>
    </v-card-text>

    <v-card-actions class="pa-4 pt-0">
      <v-spacer></v-spacer>
      <v-btn
          variant="outlined"
          @click="$emit('cancel')"
      >
        Hủy
      </v-btn>
      <v-btn
          color="primary"
          class="ml-2"
          @click="addToCart"
      >
        {{ editMode ? 'Cập nhật' : 'Thêm vào giỏ hàng' }}
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useProductStore } from '@/stores/productStore';
const productStore = useProductStore();
const note = ref('');
const props = defineProps({
  product: {
    type: Object,
    required: true
  },
  editMode: {
    type: Boolean,
    default: false
  },
  editIndex: {
    type: Number,
    default: -1
  },
  initialOptions: {
    type: Object,
    default: () => ({})
  }
});

// Lấy hình ảnh sản phẩm, kiểm tra cả hai trường hợp
function getProductImage(product) {
  //console.log(product);
  return product.image_url;
}

const emit = defineEmits(['add-to-cart', 'cancel']);
const availableToppings = ref([]);
const loading = ref(false);
const error = ref(null);

// Tải danh sách topping
async function loadToppings() {
  loading.value = true;
  try {
    // Sử dụng hàm fetchToppings từ productStore thay vì gọi API riêng
    availableToppings.value = await productStore.fetchToppings() || [];
  } catch (error) {
    console.error('Lỗi khi tải danh sách topping:', error);
    availableToppings.value = [];
  } finally {
    loading.value = false;
  }
}

// Các tùy chọn có sẵn
const sugarOptions = ['0%', '30%', '50%', '70%', '100%'];
const iceOptions = ['0%', '30%', '50%', '70%', '100%'];

// Trạng thái tùy chọn
const selectedSizeIndex = ref(0);
const selectedSugar = ref('100%');
const selectedIce = ref('100%');
const selectedToppings = ref([]);
const quantity = ref(1);

// Computed để lấy size đã chọn
const selectedSize = computed(() => {
  if (!props.product.prices || props.product.prices.length === 0) return {};
  return props.product.prices[selectedSizeIndex.value];
});

// Khởi tạo các giá trị mặc định hoặc từ item đang chỉnh sửa
// Khởi tạo các giá trị mặc định hoặc từ item đang chỉnh sửa
onMounted(() => {
  // Tải danh sách topping
  loadToppings();

  // Nếu đang chỉnh sửa, thiết lập các giá trị từ item được chỉnh sửa
  if (props.editMode && props.initialOptions) {
    if (props.initialOptions.size) {
      const sizeIndex = props.product.prices.findIndex(p => p.size === props.initialOptions.size.name);
      if (sizeIndex !== -1) selectedSizeIndex.value = sizeIndex;
    }

    if (props.initialOptions.quantity) {
      quantity.value = props.initialOptions.quantity;
    }

    if (props.initialOptions.options && Array.isArray(props.initialOptions.options)) {
      // Parse các tùy chọn từ mảng
      props.initialOptions.options.forEach(option => {
        // Kiểm tra nếu là tùy chọn đường
        if (option.includes('đường')) {
          const sugarMatch = option.match(/(\d+)%\s+đường/);
          if (sugarMatch && sugarMatch[1]) {
            selectedSugar.value = `${sugarMatch[1]}%`;
          }
        }
        // Kiểm tra nếu là tùy chọn đá
        else if (option.includes('đá')) {
          const iceMatch = option.match(/(\d+)%\s+đá/);
          if (iceMatch && iceMatch[1]) {
            selectedIce.value = `${iceMatch[1]}%`;
          }
        }
      });
    }

    // Khôi phục topping đã chọn
    if (props.initialOptions.toppings && Array.isArray(props.initialOptions.toppings)) {
      // Cần đợi danh sách topping load xong để tìm kiếm
      const initializeToppings = async () => {
        await loadToppings();
        if (availableToppings.value.length > 0) {
          selectedToppings.value = props.initialOptions.toppings.map(t => {
            // Tìm topping từ danh sách topping có sẵn dựa trên ID hoặc tên
            return availableToppings.value.find(at => at.id === t.id || at.name === t.name) || t;
          });
        }
      };

      initializeToppings();
    }
  }
});


function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value--;
  }
}

function addToCart() {
  // Tạo mảng các tùy chọn đã chọn (chỉ đường và đá)
  const optionsArray = [
    `${selectedSugar.value} đường`,
    `${selectedIce.value} đá`
  ];

  // Chuẩn bị mảng topping
  const toppingsArray = selectedToppings.value.map(topping => {
    // Log để debug cấu trúc topping
    console.log('Topping đang xử lý:', topping);
    
    // Xử lý nhiều trường hợp cấu trúc topping khác nhau
    let toppingPrice = getToppingPrice(topping);
    let toppingSizeId = null;
    
    // Xác định sizeId
    if (topping.prices && Array.isArray(topping.prices) && topping.prices.length > 0) {
      const naSize = topping.prices.find(price => price.size === "NA");
      if (naSize) {
        toppingSizeId = naSize.sizeId;
      } else {
        toppingSizeId = topping.prices[0].sizeId;
      }
    }
    
    return {
      id: topping.id || 0,
      name: topping.name || '',
      sizeId: toppingSizeId,
      price: toppingPrice
    };
  });

  // Tính tổng giá
  const basePrice = Number(selectedSize.value.price) || 0;
  const toppingTotal = toppingsArray.reduce((total, topping) => {
    return total + (Number(topping.price) || 0);
  }, 0);
  const totalPrice = (basePrice + toppingTotal) * quantity.value;

  // Tạo item để thêm vào giỏ hàng
  const cartItem = {
    product: props.product,
    size: {
      id: selectedSize.value.sizeId,
      name: selectedSize.value.size
    },
    price: basePrice,
    options: optionsArray,
    toppings: toppingsArray,
    quantity: quantity.value,
    note: note.value || '',
    total: totalPrice
  };

  console.log('Thêm vào giỏ hàng:', cartItem);
  emit('add-to-cart', cartItem, props.editIndex);
}

// Hàm trợ giúp để lấy giá topping chính xác
function getToppingPrice(topping) {
  // Kiểm tra topping có hợp lệ không
  if (!topping) return 0;
  
  // Nếu topping có mảng prices
  if (topping.prices && Array.isArray(topping.prices) && topping.prices.length > 0) {
    // Tìm size "NA" trước
    const naSize = topping.prices.find(price => price.size === "NA");
    if (naSize && !isNaN(naSize.price)) return Number(naSize.price);

    // Nếu không tìm thấy size "NA", lấy giá đầu tiên
    const firstPrice = Number(topping.prices[0].price);
    return !isNaN(firstPrice) ? firstPrice : 0;
  }
  

  return 0;
}

// Cập nhật hàm tính tổng giá tiền để đảm bảo không trả về NaN
function calculateItemTotal() {
  // Giá cơ bản của size đã chọn
  const basePrice = Number(selectedSize.value.price) || 0;

  // Tính tổng giá topping
  const toppingTotal = selectedToppings.value.reduce((total, topping) => {
    const toppingPrice = getToppingPrice(topping);
    return total + toppingPrice;
  }, 0);

  // Tổng tiền = (giá cơ bản + giá topping) * số lượng
  return (basePrice + toppingTotal) * quantity.value;
}
function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}

function getSizeIdFromName(sizeName) {
  // Giả định ID cho các kích cỡ (trong thực tế sẽ lấy từ backend)
  const sizeMap = {
    'S': 1,
    'M': 2,
    'L': 3,
    'XL': 4
  };
  return sizeMap[sizeName] || 1;
}
</script>

<style scoped>
.size-btn {
  min-width: 80px;
}
</style>