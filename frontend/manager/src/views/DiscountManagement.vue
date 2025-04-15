<template>
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
            <v-btn 
              color="primary" 
              class="text-none" 
              rounded="lg" 
              prepend-icon="mdi-plus"
              @click="openDiscountDialog()"
            >
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
                <v-btn icon variant="text" color="primary" size="small" @click="viewDiscountDetail(item.id)">
                  <v-icon>mdi-eye</v-icon>
                  <v-tooltip activator="parent" location="top">Xem chi tiết</v-tooltip>
                </v-btn>
                <v-btn icon variant="text" color="warning" size="small" @click="openDiscountDialog(item.id)">
                  <v-icon>mdi-pencil</v-icon>
                  <v-tooltip activator="parent" location="top">Chỉnh sửa</v-tooltip>
                </v-btn>
                <v-btn icon variant="text" color="error" size="small" @click="confirmDeleteDiscount(item.id)">
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
            <v-btn 
              color="primary" 
              class="text-none" 
              rounded="lg" 
              prepend-icon="mdi-plus"
              @click="openCouponDialog()"
            >
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
                <v-btn icon variant="text" color="primary" size="small" @click="viewCouponDetail(item.id)">
                  <v-icon>mdi-eye</v-icon>
                  <v-tooltip activator="parent" location="top">Xem chi tiết</v-tooltip>
                </v-btn>
                <v-btn icon variant="text" color="warning" size="small" @click="openCouponDialog(item.id)">
                  <v-icon>mdi-pencil</v-icon>
                  <v-tooltip activator="parent" location="top">Chỉnh sửa</v-tooltip>
                </v-btn>
                <v-btn icon variant="text" color="error" size="small" @click="confirmDeleteCoupon(item.id)">
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

  <!-- Dialog xem chi tiết mã giảm giá -->
  <v-dialog v-model="couponDetailDialog" max-width="500">
    <v-card>
      <v-card-title class="text-h5">
        Chi tiết mã giảm giá
      </v-card-title>
      <v-card-text v-if="selectedCoupon">
        <v-row>
          <v-col cols="12">
            <div class="text-subtitle-1 font-weight-bold">Mã giảm giá:</div>
            <div>{{ selectedCoupon.coupon }}</div>
          </v-col>
          <v-col cols="12">
            <div class="text-subtitle-1 font-weight-bold">Mô tả:</div>
            <div>{{ selectedCoupon.description || 'Không có mô tả' }}</div>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="couponDetailDialog = false">
          Đóng
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- Dialog thêm/sửa mã giảm giá -->
  <v-dialog v-model="couponDialog" max-width="500">
    <v-card>
      <v-card-title class="text-h5">
        {{ editCouponId ? 'Cập nhật mã giảm giá' : 'Thêm mã giảm giá mới' }}
      </v-card-title>
      <v-card-text>
        <v-form ref="couponFormRef" v-model="validCouponForm" @submit.prevent="saveCoupon">
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="couponForm.coupon"
                label="Mã giảm giá"
                :rules="[v => !!v || 'Vui lòng nhập mã giảm giá',
                        v => /^[a-zA-Z0-9]{3,15}$/.test(v) || 'Mã giảm giá phải từ 3 đến 15 ký tự, chỉ chứa chữ và số'
                ]"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="couponForm.description"
                label="Mô tả"
                rows="3"
                auto-grow
              ></v-textarea>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="error" text @click="couponDialog = false">
          Hủy
        </v-btn>
        <v-btn 
          color="primary" 
          text 
          @click="saveCoupon"
          :loading="discountStore.loading"
          :disabled="!validCouponForm"
        >
          {{ editCouponId ? 'Cập nhật' : 'Tạo mới' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- Dialog xác nhận xóa -->
  <v-dialog v-model="deleteDialog" max-width="400">
    <v-card>
      <v-card-title class="text-h5">
        Xác nhận xóa
      </v-card-title>
      <v-card-text>
        Bạn có chắc chắn muốn xóa {{ activeTab === 'discounts' ? 'chương trình khuyến mãi' : 'mã giảm giá' }} này?
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="deleteDialog = false">
          Hủy
        </v-btn>
        <v-btn 
          color="error" 
          text 
          @click="activeTab === 'discounts' ? deleteDiscount() : deleteCoupon()"
          :loading="discountStore.loading"
        >
          Xóa
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- Dialog xem chi tiết chương trình khuyến mãi -->
  <v-dialog v-model="discountDetailDialog" max-width="750">
    <v-card>
      <v-card-title class="text-h5">
        Chi tiết chương trình khuyến mãi
      </v-card-title>
      <v-card-text v-if="selectedDiscount">
        <v-row>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Tên chương trình:</div>
            <div>{{ selectedDiscount.name }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Mã coupon:</div>
            <div>{{ selectedDiscount.couponId }}</div>
          </v-col>
          <v-col cols="12">
            <div class="text-subtitle-1 font-weight-bold">Mô tả:</div>
            <div>{{ selectedDiscount.description || 'Không có mô tả' }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Giá trị giảm:</div>
            <div>{{ formatDiscountValue(selectedDiscount.discountValue, selectedDiscount.discountUnit) }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Giảm tối đa:</div>
            <div>{{ formatCurrency(selectedDiscount.maxDiscountAmount) }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Đơn hàng tối thiểu:</div>
            <div>{{ formatCurrency(selectedDiscount.minimumOrderValue) }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Số sản phẩm tối thiểu:</div>
            <div>{{ selectedDiscount.minimumRequiredProduct || 'Không giới hạn' }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Ngày bắt đầu:</div>
            <div>{{ formatDate(selectedDiscount.validFrom) }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Ngày kết thúc:</div>
            <div>{{ formatDate(selectedDiscount.validUntil) }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Số lần sử dụng tối đa:</div>
            <div>{{ selectedDiscount.maxUsage || 'Không giới hạn' }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Số lần sử dụng hiện tại:</div>
            <div>{{ selectedDiscount.currentUsage || 0 }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Số lần sử dụng tối đa/khách hàng:</div>
            <div>{{ selectedDiscount.maxUsagePerCustomer || 'Không giới hạn' }}</div>
          </v-col>
          <v-col cols="12" sm="6">
            <div class="text-subtitle-1 font-weight-bold">Trạng thái:</div>
            <div>
              <v-chip
                :color="selectedDiscount.isActive ? 'success' : 'error'"
                :text="selectedDiscount.isActive ? 'Hoạt động' : 'Không hoạt động'"
                size="small"
              ></v-chip>
            </div>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="discountDetailDialog = false">
          Đóng
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- Dialog thêm/sửa chương trình khuyến mãi -->
  <v-dialog v-model="discountDialog" max-width="800">
    <v-card>
      <v-card-title class="text-h5">
        {{ editDiscountId ? 'Cập nhật chương trình khuyến mãi' : 'Thêm chương trình khuyến mãi mới' }}
      </v-card-title>
      <v-card-text>
        <v-form ref="discountFormRef" v-model="validDiscountForm" @submit.prevent="saveDiscount">
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="discountForm.name"
                label="Tên chương trình"
                :rules="[v => !!v || 'Vui lòng nhập tên chương trình']"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-select
                v-model="discountForm.couponId"
                label="Mã giảm giá"
                :items="editDiscountId ? [...discountStore.unusedCoupons, selectedCoupon] : discountStore.unusedCoupons"
                item-title="coupon"
                item-value="id"
                :rules="[v => !!v || 'Vui lòng chọn mã giảm giá']"
                required
                :loading="discountStore.loading"
                :hint="'Chọn mã giảm giá cho chương trình'"
                persistent-hint
              ></v-select>
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="discountForm.description"
                label="Mô tả"
                rows="3"
                auto-grow
              ></v-textarea>
            </v-col>
            <v-col cols="12" sm="6">
              <v-select
                v-model="discountForm.discountUnit"
                label="Đơn vị giảm giá"
                :items="[
                  { title: 'Phần trăm (%)', value: 'PERCENTAGE' },
                  { title: 'Số tiền cố định', value: 'FIXED' }
                ]"
                item-title="title"
                item-value="value"
                :rules="[v => !!v || 'Vui lòng chọn đơn vị giảm giá']"
                required
              ></v-select>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.discountValue"
                label="Giá trị giảm"
                type="number"
                :rules="[
                  v => !!v || 'Vui lòng nhập giá trị giảm',
                  v => v > 0 || 'Giá trị phải lớn hơn 0',
                  v => (discountForm.discountUnit !== 'PERCENTAGE' || v <= 100) || 'Giá trị phần trăm không được vượt quá 100%',
                  v => (discountForm.discountUnit !== 'PERCENTAGE' || v > 0) || 'Giá trị phần trăm phải lớn hơn 0',
                  v => (discountForm.discountUnit !== 'FIXED' || v >= 1000) || 'Giá trị giảm cố định phải từ 1000đ trở lên'
                ]"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.maxDiscountAmount"
                label="Giảm tối đa"
                type="number"
                :rules="[
                  v => !!v || 'Vui lòng nhập giá trị giảm tối đa',
                  v => v > 0 || 'Giá trị phải lớn hơn 0'
                ]"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.minimumOrderValue"
                label="Đơn hàng tối thiểu"
                type="number"
                :rules="[
                  v => !!v || 'Vui lòng nhập giá trị đơn hàng tối thiểu',
                  v => v >= 0 || 'Giá trị không được nhỏ hơn 0'
                ]"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.minimumRequiredProduct"
                label="Số sản phẩm tối thiểu"
                type="number"
                hint="Để trống nếu không yêu cầu"
                min="0"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.maxUsage"
                label="Số lần sử dụng tối đa"
                type="number"
                hint="Để trống nếu không giới hạn"
                min="0"
                :rules="[
                  v => !v || v > 0 || 'Số lần sử dụng tối đa phải lớn hơn 0',
                  v => !v || !discountForm.maxUsagePerCustomer || v >= discountForm.maxUsagePerCustomer || 'Số lần sử dụng tối đa phải lớn hơn hoặc bằng số lần sử dụng tối đa/khách hàng'
                ]"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.maxUsagePerCustomer"
                label="Số lần sử dụng tối đa/khách hàng"
                type="number"
                hint="Để trống nếu không giới hạn"
                min="0"
                :rules="[
                  v => !v || v > 0 || 'Số lần sử dụng tối đa/khách hàng phải lớn hơn 0'
                ]"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" v-if="editDiscountId">
              <v-text-field
                v-model.number="discountForm.currentUsage"
                label="Số lần đã sử dụng"
                type="number"
                readonly
                disabled
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="discountForm.validFrom"
                label="Ngày bắt đầu"
                type="datetime-local"
                :rules="[
                  v => !v || !discountForm.validUntil || new Date(v) < new Date(discountForm.validUntil) || 'Ngày bắt đầu phải trước ngày kết thúc'
                ]"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="discountForm.validUntil"
                label="Ngày kết thúc"
                type="datetime-local"
                :rules="[
                  v => !!v || 'Vui lòng nhập ngày kết thúc',
                  v => !discountForm.validFrom || new Date(v) > new Date(discountForm.validFrom) || 'Ngày kết thúc phải sau ngày bắt đầu',
                  v => new Date(v) > new Date() || 'Ngày kết thúc phải sau thời điểm hiện tại'
                ]"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12" v-if="editDiscountId">
              <v-switch
                v-model="discountForm.active"
                color="success"
                label="Kích hoạt chương trình"
                hide-details
              ></v-switch>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="error" text @click="discountDialog = false">
          Hủy
        </v-btn>
        <v-btn 
          color="primary" 
          text 
          @click="saveDiscount"
          :loading="discountStore.loading"
          :disabled="!validDiscountForm"
        >
          {{ editDiscountId ? 'Cập nhật' : 'Tạo mới' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-snackbar
    v-model="snackbar.show"
    :color="snackbar.color"
    :timeout="3000"
  >
    {{ snackbar.text }}
    <template v-slot:actions>
      <v-btn
        color="white"
        icon="mdi-close"
        variant="text"
        @click="snackbar.show = false"
      ></v-btn>
    </template>
  </v-snackbar>
</template>

<script setup>
import { ref, onMounted, watch, reactive } from 'vue'
import { useDiscountStore } from '@/stores/discount'

const discountStore = useDiscountStore()
const activeTab = ref('discounts')
const searchDiscount = ref('')
const searchCoupon = ref('')
const discountPage = ref(1)
const couponPage = ref(1)

const snackbar = ref({
  show: false,
  text: '',
  color: 'error'
})

function showSnackbar(text, color = 'error') {
  snackbar.value = {
    show: true,
    text,
    color
  }
}

// Biến cho quản lý dialog coupon
const couponDialog = ref(false)
const couponDetailDialog = ref(false)
const deleteDialog = ref(false)
const editCouponId = ref(null)
const selectedCoupon = ref(null)
const couponForm = reactive({
  coupon: '',
  description: ''
})
const validCouponForm = ref(false)
const couponFormRef = ref(null)

// Biến cho quản lý dialog discount
const discountDialog = ref(false)
const discountDetailDialog = ref(false)
const editDiscountId = ref(null)
const selectedDiscount = ref(null)
const discountForm = reactive({
  name: '',
  description: '',
  couponId: null,
  discountUnit: 'PERCENTAGE',
  discountValue: 0,
  maxDiscountAmount: 0,
  minimumOrderValue: 0,
  minimumRequiredProduct: null,
  validFrom: null,
  validUntil: null,
  maxUsage: null,
  maxUsagePerCustomer: null,
  currentUsage: 0,
  active: true
})
const validDiscountForm = ref(false)
const discountFormRef = ref(null)

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

// Mở dialog thêm/sửa mã giảm giá
const openCouponDialog = async (id = null) => {
  resetCouponForm()
  
  if (id) {
    editCouponId.value = id
    const couponDetail = await discountStore.getCouponById(id)
    if (couponDetail) {
      couponForm.coupon = couponDetail.coupon
      couponForm.description = couponDetail.description || ''
    }
  } else {
    editCouponId.value = null
  }
  
  couponDialog.value = true
}

// Reset form mã giảm giá
const resetCouponForm = () => {
  couponForm.coupon = ''
  couponForm.description = ''
  if (couponFormRef.value) {
    couponFormRef.value.reset()
  }
}

// Lưu mã giảm giá (thêm mới hoặc cập nhật)
const saveCoupon = async () => {
  if (!validCouponForm.value) return

  const couponData = {
    coupon: couponForm.coupon,
    description: couponForm.description
  }

  let result
  if (editCouponId.value) {
    // Cập nhật
    result = await discountStore.updateCoupon(editCouponId.value, couponData)
  } else {
    // Thêm mới
    result = await discountStore.createCoupon(couponData)
  }

  if (result) {
    couponDialog.value = false
    resetCouponForm()
    showSnackbar('Thành công', 'success')
  } else {
    showSnackbar('Thất bại', 'error')
  }
}

// Xem chi tiết mã giảm giá
const viewCouponDetail = async (id) => {
  const couponDetail = await discountStore.getCouponById(id)
  if (couponDetail) {
    selectedCoupon.value = couponDetail
    couponDetailDialog.value = true
  }
}

// Xác nhận xóa mã giảm giá
const confirmDeleteCoupon = (id) => {
  editCouponId.value = id
  deleteDialog.value = true
}

// Xóa mã giảm giá
const deleteCoupon = async () => {
  if (!editCouponId.value) return
  
  const result = await discountStore.deleteCoupon(editCouponId.value)
  if (result) {
    deleteDialog.value = false
    editCouponId.value = null
  }
}

// Mở dialog thêm/sửa chương trình khuyến mãi
const openDiscountDialog = async (id = null) => {
  resetDiscountForm()
  
  // Tải danh sách coupon
  await discountStore.fetchUnusedCoupons()
  
  // Nếu chưa có dữ liệu tất cả coupons, tải thêm
  if (!discountStore.coupons || discountStore.coupons.length === 0) {
    await discountStore.fetchCoupons()
  }
  
  if (id) {
    editDiscountId.value = id
    const discountDetail = await discountStore.getDiscountById(id)
    if (discountDetail) {
      discountForm.name = discountDetail.name
      discountForm.description = discountDetail.description || ''
      discountForm.couponId = discountDetail.couponId
      discountForm.discountUnit = discountDetail.discountUnit
      discountForm.discountValue = discountDetail.discountValue
      discountForm.maxDiscountAmount = discountDetail.maxDiscountAmount
      discountForm.minimumOrderValue = discountDetail.minimumOrderValue
      discountForm.minimumRequiredProduct = discountDetail.minimumRequiredProduct
      
      // Định dạng ngày tháng cho input datetime-local
      if (discountDetail.validFrom) {
        discountForm.validFrom = formatDateTimeForInput(discountDetail.validFrom)
      }
      discountForm.validUntil = formatDateTimeForInput(discountDetail.validUntil)
      
      discountForm.maxUsage = discountDetail.maxUsage
      discountForm.maxUsagePerCustomer = discountDetail.maxUsagePerCustomer
      discountForm.active = discountDetail.isActive
      discountForm.currentUsage = discountDetail.currentUsage || 0
      
      // Lưu thông tin coupon hiện tại để hiển thị trong danh sách lựa chọn
      let currentCoupon = discountStore.coupons.find(c => c.id === discountDetail.couponId)
      
      // Nếu chưa có thông tin coupon hiện tại, tải thông tin chi tiết
      if (!currentCoupon && discountDetail.couponId) {
        const couponDetail = await discountStore.getCouponById(discountDetail.couponId)
        if (couponDetail) {
          currentCoupon = couponDetail
        }
      }
      
      selectedCoupon.value = {
        id: discountDetail.couponId,
        coupon: currentCoupon?.coupon || 'Mã hiện tại'
      }
    }
  } else {
    editDiscountId.value = null
    selectedCoupon.value = null
  }
  
  discountDialog.value = true
}

// Reset form chương trình khuyến mãi
const resetDiscountForm = () => {
  discountForm.name = ''
  discountForm.description = ''
  discountForm.couponId = null
  discountForm.discountUnit = 'PERCENTAGE'
  discountForm.discountValue = 0
  discountForm.maxDiscountAmount = 0
  discountForm.minimumOrderValue = 0
  discountForm.minimumRequiredProduct = null
  discountForm.validFrom = null
  discountForm.validUntil = null
  discountForm.maxUsage = null
  discountForm.maxUsagePerCustomer = null
  discountForm.currentUsage = 0
  discountForm.active = true
  
  if (discountFormRef.value) {
    discountFormRef.value.reset()
  }
}

// Định dạng datetime cho input
const formatDateTimeForInput = (dateString) => {
  if (!dateString) return null
  const date = new Date(dateString)
  return new Date(date.getTime() - date.getTimezoneOffset() * 60000)
    .toISOString()
    .slice(0, 16)
}

// Lưu chương trình khuyến mãi (thêm mới hoặc cập nhật)
const saveDiscount = async () => {
  if (!validDiscountForm.value) return

  const discountData = {
    name: discountForm.name,
    description: discountForm.description,
    couponId: discountForm.couponId,
    discountUnit: discountForm.discountUnit,
    discountValue: discountForm.discountValue,
    maxDiscountAmount: discountForm.maxDiscountAmount,
    minimumOrderValue: discountForm.minimumOrderValue,
    minimumRequiredProduct: discountForm.minimumRequiredProduct,
    validFrom: discountForm.validFrom,
    validUntil: discountForm.validUntil,
    maxUsage: discountForm.maxUsage,
    maxUsagePerCustomer: discountForm.maxUsagePerCustomer
  }

  // Thêm trường active khi cập nhật
  if (editDiscountId.value) {
    discountData.active = discountForm.active
  }

  let result
  if (editDiscountId.value) {
    // Cập nhật
    result = await discountStore.updateDiscount(editDiscountId.value, discountData)
  } else {
    // Thêm mới
    result = await discountStore.createDiscount(discountData)
  }

  if (result) {
    discountDialog.value = false
    resetDiscountForm()
    showSnackbar('Thành công', 'success')
  } else {
    showSnackbar('Thất bại', 'error')
  }
}

// Xem chi tiết chương trình khuyến mãi
const viewDiscountDetail = async (id) => {
  const discountDetail = await discountStore.getDiscountById(id)
  if (discountDetail) {
    selectedDiscount.value = discountDetail
    discountDetailDialog.value = true
  }
}

// Xác nhận xóa chương trình khuyến mãi
const confirmDeleteDiscount = (id) => {
  editDiscountId.value = id
  deleteDialog.value = true
}

// Xóa chương trình khuyến mãi
const deleteDiscount = async () => {
  if (!editDiscountId.value) return
  
  const result = await discountStore.deleteDiscount(editDiscountId.value)
  if (result) {
    deleteDialog.value = false
    editDiscountId.value = null
    showSnackbar('Đã xóa thành công', 'success')
  } else {
    showSnackbar('Xóa thất bại', 'error')
  }
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