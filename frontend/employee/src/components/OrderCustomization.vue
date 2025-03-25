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
            :src="product.image_url || '/images/default-product.png'"
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

      <!-- Toppings -->
      <h4 class="text-subtitle-1 font-weight-medium mb-2">Topping</h4>
      <v-row dense class="mb-4">
        <v-col
            v-for="topping in toppingOptions"
            :key="topping.name"
            cols="12"
            sm="6"
        >
          <v-checkbox
              v-model="selectedToppings"
              :label="`${topping.name} (+${formatPrice(topping.price)})`"
              :value="topping.name"
              hide-details
              density="compact"
          ></v-checkbox>
        </v-col>
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

const emit = defineEmits(['add-to-cart', 'cancel']);

// Các tùy chọn có sẵn
const sugarOptions = ['0%', '30%', '50%', '70%', '100%'];
const iceOptions = ['0%', '30%', '50%', '70%', '100%'];
const toppingOptions = [
  { name: 'Trân châu đen', price: 5000 },
  { name: 'Trân châu trắng', price: 5000 },
  { name: 'Thạch trái cây', price: 7000 },
  { name: 'Pudding', price: 8000 },
  { name: 'Kem phô mai', price: 10000 }
];

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
onMounted(() => {
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
        // Các tùy chọn khác xem như topping
        else if (toppingOptions.some(t => option.includes(t.name))) {
          toppingOptions.forEach(topping => {
            if (option.includes(topping.name)) {
              if (!selectedToppings.value.includes(topping.name)) {
                selectedToppings.value.push(topping.name);
              }
            }
          });
        }
      });
    }
  }
});

function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value--;
  }
}

function calculateItemTotal() {
  let total = selectedSize.value.price || 0;

  // Thêm giá topping
  if (selectedToppings.value.length > 0) {
    selectedToppings.value.forEach(topping => {
      const toppingInfo = toppingOptions.find(t => t.name === topping);
      if (toppingInfo) {
        total += toppingInfo.price;
      }
    });
  }

  return total * quantity.value;
}

function formatPrice(price) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
}

function addToCart() {
  // Tạo mảng các tùy chọn đã chọn
  const optionsArray = [];

  // Thêm tùy chọn đường
  optionsArray.push(`${selectedSugar.value} đường`);

  // Thêm tùy chọn đá
  optionsArray.push(`${selectedIce.value} đá`);

  // Thêm các topping
  selectedToppings.value.forEach(topping => {
    optionsArray.push(topping);
  });

  // Tạo item để thêm vào giỏ hàng
  const cartItem = {
    product: props.product,
    size: {
      id: getSizeIdFromName(selectedSize.value.size),
      name: selectedSize.value.size
    },
    price: selectedSize.value.price,
    options: optionsArray,
    quantity: quantity.value
  };

  emit('add-to-cart', cartItem);
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