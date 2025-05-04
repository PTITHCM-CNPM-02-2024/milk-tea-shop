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
              <div class="text-center py-6">Không có dữ liệu</div>
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
        <!-- Hiển thị lỗi dialog -->
        <v-alert
          v-if="couponDialogError"
          type="error"
          variant="tonal"
          closable
          class="mb-3"
          @update:model-value="couponDialogError = null"
        >
          {{ couponDialogError }}
        </v-alert>

        <v-form ref="couponFormRef" v-model="validCouponForm">
          <v-text-field
            v-model="couponForm.coupon"
            label="Mã giảm giá"
            :rules="[v => !!v || 'Vui lòng nhập mã giảm giá']"
            required
          ></v-text-field>
          <v-text-field
            v-model="couponForm.description"
            label="Mô tả"
          ></v-text-field>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="couponDialog = false">
          Đóng
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
  <v-dialog v-model="deleteCouponDialog" max-width="400">
    <v-card>
      <v-card-title class="text-h5 font-weight-medium pa-4 bg-error text-white">
        Xác nhận xóa
      </v-card-title>
      <v-card-text class="text-justify text-wrap">
        <p>Bạn có chắc chắn muốn xóa mã giảm giá "<strong>{{ couponToDelete.coupon }}</strong>" không?</p>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="deleteCouponDialog = false">
          Đóng
        </v-btn>
        <v-btn 
          color="error" 
          text 
          @click="deleteCoupon()"
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
  <v-dialog v-model="discountDialog" max-width="900">
    <v-card>
      <v-card-title class="text-h5">
        {{ editDiscountId ? 'Cập nhật chương trình khuyến mãi' : 'Thêm chương trình khuyến mãi mới' }}
      </v-card-title>
      <v-card-text>
        <!-- Hiển thị lỗi dialog -->
        <v-alert
          v-if="discountDialogError"
          type="error"
          variant="tonal"
          closable
          class="mb-3"
          @update:model-value="discountDialogError = null"
        >
          {{ discountDialogError }}
        </v-alert>

        <v-form ref="discountFormRef" v-model="validDiscountForm">
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="discountForm.name"
                label="Tên chương trình"
                :rules="[v => !!v || 'Vui lòng nhập tên chương trình']"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="discountForm.description"
                label="Mô tả"
                rows="2"
                auto-grow
              ></v-textarea>
            </v-col>
            
            <v-col cols="12" sm="6">
              <v-select
                v-model="discountForm.discountUnit"
                :items="[
                  { title: 'Phần trăm (%)', value: 'PERCENTAGE' },
                  { title: 'Số tiền cố định', value: 'FIXED' }
                ]"
                label="Loại giảm giá"
                :rules="[v => !!v || 'Vui lòng chọn loại giảm giá']"
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
                  v => v >= 0 || 'Giá trị không được âm',
                  v => discountForm.discountUnit !== 'PERCENTAGE' || v <= 100 || 'Giá trị phần trăm không được vượt quá 100%'
                ]"
                required
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.maxDiscountAmount"
                label="Số tiền giảm tối đa (đồng)"
                type="number"
                :rules="[
                  v => v === 0 || v >= 1000 || 'Số tiền phải bằng 0 hoặc lớn hơn 1000đ'
                ]"
                min="0"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.minimumOrderValue"
                label="Giá trị đơn hàng tối thiểu (đồng)"
                type="number"
                :rules="[
                  v => v === 0 || v >= 1000 || 'Giá trị phải bằng 0 hoặc lớn hơn 1000đ'
                ]"
                min="0"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-select
                v-model="discountForm.couponId"
                :items="discountStore.unusedCoupons"
                item-value="id"
                item-title="coupon"
                label="Mã giảm giá kèm theo"
                clearable
              ></v-select>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.maxUsage"
                label="Số lần sử dụng tối đa (tùy chọn)"
                type="number"
                min="0"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model.number="discountForm.maxUsagePerCustomer"
                label="Số lần sử dụng tối đa/khách hàng (tùy chọn)"
                type="number"
                min="0"
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
        <v-btn text @click="discountDialog = false">
          Đóng
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

  <!-- Dialog xác nhận xóa -->
  <v-dialog v-model="deleteDiscountDialog" max-width="400">
    <v-card>
      <v-card-title class="text-h5 font-weight-medium pa-4 bg-error text-white">
        Xác nhận xóa
      </v-card-title>
      <v-card-text class="text-justify text-wrap">
        <p v-if="discountToDelete">Bạn có chắc chắn muốn xóa chương trình khuyến mãi "<strong>{{ discountToDelete.name }}</strong>" không?</p>
        <p class="text-warning mt-2">
          Lưu ý:
          <br> Có thể gây mất mát dữ liệu với các đơn hàng đã được tạo.
        </p>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="deleteDiscountDialog = false">
          Đóng
        </v-btn>
        <v-btn 
          color="error" 
          text 
          @click="deleteDiscount()"
          :loading="discountStore.loading"
        >
          Xóa
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
const editCouponId = ref(null)
const selectedCoupon = ref(null)
const couponForm = reactive({
  coupon: '',
  description: ''
})
const validCouponForm = ref(false)
const couponFormRef = ref(null)
// Thêm state lỗi cho dialog mã giảm giá
const couponDialogError = ref(null)

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
// Thêm state lỗi cho dialog khuyến mãi
const discountDialogError = ref(null)

// Thay thế dialog xóa chung bằng dialog xóa riêng
const deleteCouponDialog = ref(false)
const deleteDiscountDialog = ref(false)
const couponToDelete = ref(null)
const discountToDelete = ref(null)

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
  { title: 'Thao tác', key: 'actions', sortable: false, align: 'center', width: '100px' }
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
  couponDialogError.value = null // Reset lỗi trước khi lưu
  
  try {
    const couponData = {
      coupon: couponForm.coupon,
      description: couponForm.description
    }

    let result
    if (editCouponId.value) {
      result = await discountStore.updateCoupon(editCouponId.value, couponData)
    } else {
      result = await discountStore.createCoupon(couponData)
    }

    if (result) {
      couponDialog.value = false
      resetCouponForm()
      showSnackbar('Thành công', 'success')
    }
  } catch (error) {
    console.error("Lỗi khi lưu mã giảm giá:", error)
    couponDialogError.value = error.message || 'Đã xảy ra lỗi khi lưu mã giảm giá'
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
  couponToDelete.value = discountStore.coupons.find(c => c.id === id)
  deleteCouponDialog.value = true
}

// Đóng dialog xác nhận xóa mã giảm giá
const closeDeleteCouponDialog = () => {
  deleteCouponDialog.value = false
  couponToDelete.value = null
}

// Xóa mã giảm giá
const deleteCoupon = async () => {
  if (!couponToDelete.value) return
  
  try {
    const result = await discountStore.deleteCoupon(couponToDelete.value.id)
    if (result) {
      showSnackbar('Đã xóa mã giảm giá thành công', 'success')
    }
  } catch (error) {
    console.error("Lỗi khi xóa mã giảm giá:", error)
    showSnackbar(error.message || 'Đã xảy ra lỗi khi xóa mã giảm giá', 'error')
  } finally {
    closeDeleteCouponDialog()
  }
}

// Mở dialog thêm/sửa chương trình khuyến mãi
const openDiscountDialog = async (id = null) => {
  resetDiscountForm()
  discountDialogError.value = null // Reset lỗi khi mở dialog
  
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
  discountDialogError.value = null // Reset lỗi khi reset form
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
  discountDialogError.value = null // Reset lỗi trước khi lưu

  try {
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
    }
  } catch (error) {
    console.error("Lỗi khi lưu khuyến mãi:", error)
    discountDialogError.value = error.message || 'Đã xảy ra lỗi khi lưu khuyến mãi'
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
  discountToDelete.value = discountStore.discounts.find(d => d.id === id)
  deleteDiscountDialog.value = true
}

// Đóng dialog xác nhận xóa chương trình khuyến mãi
const closeDeleteDiscountDialog = () => {
  deleteDiscountDialog.value = false
  discountToDelete.value = null
}

// Xóa chương trình khuyến mãi
const deleteDiscount = async () => {
  if (!discountToDelete.value) return
  
  try {
    const result = await discountStore.deleteDiscount(discountToDelete.value.id)
    if (result) {
      showSnackbar('Đã xóa thành công', 'success')
    }
  } catch (error) {
    console.error("Lỗi khi xóa khuyến mãi:", error)
    showSnackbar(error.message || 'Đã xảy ra lỗi khi xóa khuyến mãi', 'error')
  } finally {
    closeDeleteDiscountDialog()
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