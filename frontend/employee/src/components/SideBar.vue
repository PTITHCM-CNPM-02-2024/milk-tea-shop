<template>
  <v-navigation-drawer
    v-model="drawer"
    :rail="rail"
    permanent
    @click="rail = false"
    color="grey-lighten-4"
    :width="240"
    :z-index="1"
  >
    <v-list-item
      prepend-avatar="../images/logo.jpg"
      title="POS Trà Sữa"
      nav
    >
      <template v-slot:append>
        <v-btn
          variant="text"
          icon="mdi-chevron-left"
          @click.stop="rail = !rail"
        ></v-btn>
      </template>
    </v-list-item>

    <v-divider></v-divider>

    <v-list density="compact" nav>
      <v-list-item prepend-icon="mdi-cash-register" title="Bán hàng" value="pos" to="/"></v-list-item>
      <v-list-item prepend-icon="mdi-cart-outline" title="Đơn hàng" value="orders" to="/orders"></v-list-item>
      <v-list-item prepend-icon="mdi-book-open-variant" title="Báo cáo" value="reports" to="/reports"></v-list-item>
      <v-list-item prepend-icon="mdi-account-group" title="Khách hàng" value="customers" to="/customers"></v-list-item>
      <v-list-item prepend-icon="mdi-coffee" title="Sản phẩm" value="products" to="/products"></v-list-item>
      <v-list-item prepend-icon="mdi-cog-outline" title="Cài đặt" value="settings" to="/settings"></v-list-item>
    </v-list>
    
    <!-- Footer của sidebar -->
    <template v-slot:append>
      <div class="pa-2">
        <v-btn
          block
          color="primary"
          prepend-icon="mdi-help-circle"
        >
          Trợ giúp
        </v-btn>
      </div>
    </template>
  </v-navigation-drawer>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: true
  },
  collapsed: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'update:collapsed']);

const drawer = ref(props.modelValue);
const rail = ref(props.collapsed);

watch(drawer, (val) => {
  emit('update:modelValue', val);
});

watch(rail, (val) => {
  emit('update:collapsed', val);
});
</script> 