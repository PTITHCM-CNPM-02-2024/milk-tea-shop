<template>
  <dashboard-layout>
    <div>
      <v-card>
        <v-card-title>
          <h1 class="text-h5 font-weight-medium">Quản lý khuyến mãi</h1>
        </v-card-title>
        
        <!-- Hiển thị thông báo lỗi nếu có -->
        <v-alert
          v-if="discountStore.error"
          type="error"
          variant="tonal"
          closable
          class="mx-4 mt-2"
        >
          {{ discountStore.error }}
        </v-alert>
        
        <v-tabs v-model="activeTab" color="primary" class="px-4">
          <v-tab value="discounts" class="text-none">Khuyến mãi</v-tab>
          <v-tab value="coupons" class="text-none">Mã giảm giá</v-tab>
        </v-tabs>

        <v-window v-model="activeTab" class="px-4 pb-4">
          <!-- Tab Chương trình khuyến mãi -->
          <v-window-item value="discounts">
            <div class="d-flex align-center my-4">
              <v-text-field
                v-model="searchDiscount"
                label="Tìm kiếm chương trình"
                prepend-inner-icon="mdi-magnify"
                density="compact"
                hide-details
                class="mr-4"
                bg-color="background"
                variant="outlined"
                style="max-width: 300px;"
              ></v-text-field>
              <v-spacer></v-spacer>
              <v-btn color="primary" class="text-none" rounded="lg" prepend-icon="mdi-plus">
                THÊM CHƯƠNG TRÌNH
              </v-btn>
            </div>

            <v-data-table
              :headers="discountHeaders"
              :items="discountStore.discounts || []"
              :loading="discountStore.loading"
              hover
              class="mt-2 bg-surface rounded"
            >
              <template v-slot:no-data>
                <div class="text-center py-6">No data available</div>
              </template>
              
              <template v-slot:item.discountValue="{ item }">
                {{ formatDiscountValue(item.discountValue, item.discountUnit) }}
              </template>

              <template v-slot:item.maxDiscountAmount="{ item }">
                {{ formatCurrency(item.maxDiscountAmount) }}
              </template>

              <template v-slot:item.minimumOrderValue="{ item }">
                {{ formatCurrency(item.minimumOrderValue) }}
              </template>

              <template v-slot:item.validFrom="{ item }">
                {{ formatDate(item.validFrom) }}
              </template>

              <template v-slot:item.validUntil="{ item }">
                {{ formatDate(item.validUntil) }}
              </template>

              <template v-slot:item.isActive="{ item }">
                <v-chip
                  v-if="item.isActive"
                  color="success"
                  text="Hoạt động"
                  size="small"
                ></v-chip>
                <span v-else>Không hoạt động</span>
              </template>

              <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                  <v-btn icon variant="text" color="primary" size="small" v-if="item.raw">
                    <v-icon>mdi-eye</v-icon>
                    <v-tooltip activator="parent" location="top">Xem chi tiết</v-tooltip>
                  </v-btn>
                  <v-btn icon variant="text" color="warning" size="small" v-if="item.raw">
                    <v-icon>mdi-pencil</v-icon>
                    <v-tooltip activator="parent" location="top">Chỉnh sửa</v-tooltip>
                  </v-btn>
                  <v-btn icon variant="text" color="error" size="small" v-if="item.raw">
                    <v-icon>mdi-delete</v-icon>
                    <v-tooltip activator="parent" location="top">Xóa</v-tooltip>
                  </v-btn>
                </div>
              </template>

              <template v-slot:bottom>
                <div class="d-flex align-center justify-center py-2">
                  <v-pagination
                    v-model="discountPage"
                    :length="Math.ceil(discountStore.pagination.total / discountStore.pagination.size) || 1"
                    total-visible="7"
                    @update:modelValue="onDiscountPageChange"
                  ></v-pagination>
                </div>
              </template>
            </v-data-table>
          </v-window-item>

          <!-- Tab Mã giảm giá -->
          <v-window-item value="coupons">
            <div class="d-flex align-center my-4">
              <v-text-field
                v-model="searchCoupon"
                label="Tìm kiếm mã giảm giá"
                prepend-inner-icon="mdi-magnify"
                density="compact"
                hide-details
                class="mr-4"
                bg-color="background"
                variant="outlined"
                style="max-width: 300px;"
              ></v-text-field>
              <v-spacer></v-spacer>
              <v-btn color="primary" class="text-none" rounded="lg" prepend-icon="mdi-plus">
                THÊM MÃ GIẢM GIÁ
              </v-btn>
            </div>

            <v-data-table
              :headers="couponHeaders"
              :items="discountStore.coupons || []"
              :loading="discountStore.loading"
              hover
              class="mt-2 bg-surface rounded"
            >
              <template v-slot:no-data>
                <div class="text-center py-6">No data available</div>
              </template>
              
              <template v-slot:item.actions="{ item }">
                <div class="d-flex">
                  <v-btn icon variant="text" color="primary" size="small" v-if="item.raw">
                    <v-icon>mdi-eye</v-icon>
                    <v-tooltip activator="parent" location="top">Xem chi tiết</v-tooltip>
                  </v-btn>
                  <v-btn icon variant="text" color="warning" size="small" v-if="item.raw">
                    <v-icon>mdi-pencil</v-icon>
                    <v-tooltip activator="parent" location="top">Chỉnh sửa</v-tooltip>
                  </v-btn>
                  <v-btn icon variant="text" color="error" size="small" v-if="item.raw">
                    <v-icon>mdi-delete</v-icon>
                    <v-tooltip activator="parent" location="top">Xóa</v-tooltip>
                  </v-btn>
                </div>
              </template>

              <template v-slot:bottom>
                <div class="d-flex align-center justify-center py-2">
                  <v-pagination
                    v-model="couponPage"
                    :length="Math.ceil(discountStore.pagination.total / discountStore.pagination.size) || 1"
                    total-visible="7"
                    @update:modelValue="onCouponPageChange"
                  ></v-pagination>
                </div>
              </template>
            </v-data-table>
          </v-window-item>
        </v-window>
      </v-card>
    </div>
  </dashboard-layout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useDiscountStore } from '@/stores/discount'
import DashboardLayout from '@/components/layouts/DashboardLayout.vue'

const discountStore = useDiscountStore()
const activeTab = ref('discounts')
const searchDiscount = ref('')
const searchCoupon = ref('')
const discountPage = ref(1)
const couponPage = ref(1)

const discountHeaders = [
  { title: 'Tên chương trình', key: 'name', align: 'start' },
  { title: 'Mô tả', key: 'description' },
  { title: 'Giá trị giảm', key: 'discountValue' },
  { title: 'Giảm tối đa', key: 'maxDiscountAmount' },
  { title: 'Đơn hàng tối thiểu', key: 'minimumOrderValue' },
  { title: 'Ngày bắt đầu', key: 'validFrom' },
  { title: 'Ngày kết thúc', key: 'validUntil' },
  { title: 'Trạng thái', key: 'isActive' },
  { title: 'Thao tác', key: 'actions', sortable: false, align: 'center' }
]

const couponHeaders = [
  { title: 'Mã giảm giá', key: 'coupon', align: 'start' },
  { title: 'Mô tả', key: 'description' },
  { title: 'Thao tác', key: 'actions', sortable: false, align: 'center' }
]

// Format giá trị giảm giá
const formatDiscountValue = (value, unit) => {
  if (unit === 'PERCENTAGE') {
    return `${value}%`
  }
  return formatCurrency(value)
}

// Format tiền tệ
const formatCurrency = (value) => {
  if (!value) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

// Format ngày tháng
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('vi-VN')
}

// Xử lý khi thay đổi trang
const onDiscountPageChange = (page) => {
  discountStore.fetchDiscounts(page - 1)
}

const onCouponPageChange = (page) => {
  discountStore.fetchCoupons(page - 1)
}

// Theo dõi khi thay đổi tab
watch(activeTab, (newTab) => {
  if (newTab === 'discounts') {
    discountStore.fetchDiscounts(discountPage.value - 1)
  } else {
    discountStore.fetchCoupons(couponPage.value - 1)
  }
})

// Khởi tạo dữ liệu
onMounted(async () => {
  await discountStore.fetchDiscounts()
  await discountStore.fetchCoupons()
})
</script>

<style scoped>
.v-data-table {
  border-radius: 8px;
  overflow: hidden;
}
</style> 