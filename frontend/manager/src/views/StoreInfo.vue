<template>
  <v-div>
     <v-card class="mb-4">
        <v-card-title class="text-h4 d-flex align-center">
          <v-icon large class="mr-2">mdi-store</v-icon>
          Thông tin cửa hàng
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text v-if="loading">
          <v-skeleton-loader
            type="article"
            class="my-4"
          ></v-skeleton-loader>
        </v-card-text>
        
        <v-card-text v-else-if="error">
          <v-alert
            type="error"
            border="left"
            prominent
          >
            {{ error }}
          </v-alert>
          <div class="d-flex justify-center mt-4">
            <v-btn 
              color="primary" 
              @click="fetchStoreInfo"
            >
              <v-icon left>mdi-refresh</v-icon>
              Thử lại
            </v-btn>
          </div>
        </v-card-text>
        
        <template v-else>
          <v-card-text>
            <!-- Thông báo thành công -->
            <v-alert
              v-if="showSuccessAlert"
              type="success"
              variant="tonal"
              closable
              class="mb-4"
            >
              {{ successMessage }}
            </v-alert>
            
            <!-- Thông báo lỗi -->
            <v-alert
              v-if="showErrorAlert"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
            >
              {{ errorMessage }}
            </v-alert>
            
            <v-form ref="form" v-model="isFormValid" @submit.prevent="saveStoreInfo">
              <!-- Thông tin cơ bản -->
              <v-card class="mb-4 pa-4 rounded-lg">
                <div class="d-flex align-center justify-space-between mb-4">
                  <div class="d-flex align-center">
                    <v-icon size="24" color="primary" class="mr-2">mdi-information-outline</v-icon>
                    <span class="text-h6">Thông tin cơ bản</span>
                  </div>
                </div>
                
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="editedStore.name"
                      label="Tên cửa hàng *"
                      placeholder="Ví dụ: Milk Tea Shop Nguyễn Văn Cừ"
                      :rules="[v => !!v || 'Vui lòng nhập tên cửa hàng']"
                      prepend-inner-icon="mdi-store"
                      variant="outlined"
                      density="comfortable"
                    ></v-text-field>
                    
                    <v-text-field
                      v-model="editedStore.address"
                      label="Địa chỉ *"
                      placeholder="Ví dụ: 123 Nguyễn Văn Cừ, Quận 5, TP.HCM"
                      :rules="[v => !!v || 'Vui lòng nhập địa chỉ']"
                      prepend-inner-icon="mdi-map-marker"
                      variant="outlined"
                      density="comfortable"
                    ></v-text-field>
                  </v-col>
                  
                  <v-col cols="12" md="6">  
                    <v-text-field
                      v-model="editedStore.openingDate"
                      label="Ngày khai trương"
                      type="date"
                      prepend-inner-icon="mdi-calendar"
                      variant="outlined"
                      density="comfortable"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-card>
              
              <!-- Thông tin liên hệ -->
              <v-card class="mb-4 pa-4 rounded-lg">
                <div class="d-flex align-center mb-4">
                  <v-icon size="24" color="primary" class="mr-2">mdi-contacts</v-icon>
                  <span class="text-h6">Thông tin liên hệ</span>
                </div>
                
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="editedStore.phone"
                      label="Số điện thoại *"
                      placeholder="Ví dụ: 0287654321, +842887654321"
                      :rules="[v => !!v || 'Vui lòng nhập số điện thoại']"
                      prepend-inner-icon="mdi-phone"
                      variant="outlined"
                      density="comfortable"
                    ></v-text-field>
                  </v-col>
                  
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="editedStore.email"
                      label="Email *"
                      placeholder="Ví dụ: info@milkteashop.com"
                      :rules="[
                        v => !!v || 'Vui lòng nhập email',
                        v => /.+@.+\..+/.test(v) || 'Email không hợp lệ'
                      ]"
                      prepend-inner-icon="mdi-email"
                      variant="outlined"
                      density="comfortable"
                    ></v-text-field>
                  </v-col>
                  
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="editedStore.taxCode"
                      label="Mã số thuế"
                      placeholder="Ví dụ: 0123456789"
                      prepend-inner-icon="mdi-file-document"
                      variant="outlined"
                      density="comfortable"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-card>
              
              <!-- Thời gian hoạt động -->
              <v-card class="mb-4 pa-4 rounded-lg">
                <div class="d-flex align-center mb-4">
                  <v-icon size="24" color="primary" class="mr-2">mdi-clock-outline</v-icon>
                  <span class="text-h6">Thời gian hoạt động</span>
                </div>
                
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="editedStore.openTime"
                      label="Giờ mở cửa"
                      type="time"
                      prepend-inner-icon="mdi-clock-outline"
                      variant="outlined"
                      density="comfortable"
                    ></v-text-field>
                  </v-col>
                  
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="editedStore.closeTime"
                      label="Giờ đóng cửa"
                      type="time"
                      prepend-inner-icon="mdi-clock-outline"
                      variant="outlined"
                      density="comfortable"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-card>
              
              <div class="d-flex justify-end">
                <v-btn
                  color="grey-darken-1"
                  variant="outlined"
                  class="mr-2"
                  @click="resetForm"
                >
                  <v-icon left>mdi-close</v-icon>
                  Hủy
                </v-btn>
                
                <v-btn
                  color="primary"
                  type="submit"
                  :loading="loading"
                  :disabled="!isFormValid"
                >
                  <v-icon left>mdi-content-save</v-icon>
                  Cập nhật
                </v-btn>
              </div>
            </v-form>
          </v-card-text>
        </template>
      </v-card>
    
    <!-- Hộp thoại xác nhận -->
    <v-dialog v-model="showConfirmDialog" max-width="500px" persistent>
      <v-card>
        <v-card-title class="text-h5 pa-4 bg-primary text-white">
          <v-icon color="white" class="mr-2">mdi-alert</v-icon>
          Xác nhận thay đổi
        </v-card-title>
        
        <v-card-text class="pa-4 pt-5">
          Bạn có chắc chắn muốn cập nhật thông tin cửa hàng không?
          <p class="text-medium-emphasis mt-2">Thông tin sẽ được hiển thị cho khách hàng.</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="showConfirmDialog = false">
            Hủy
          </v-btn>
          <v-btn color="primary" @click="confirmSave" :loading="loading">
            Xác nhận
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useStoreInfoStore } from '@/stores/store'
import { storeToRefs } from 'pinia'
import DashboardLayout from '@/components/layouts/DashboardLayout.vue'

// Store
const storeInfoStore = useStoreInfoStore()

// Sử dụng storeToRefs để giữ tính reactive của các state từ store
const { 
  storeInfo, 
  loading, 
  error, 
  formattedOpeningDate, 
  formattedOpenTime, 
  formattedCloseTime 
} = storeToRefs(storeInfoStore)

// Lấy các actions từ store
const { fetchStoreInfo, updateStoreInfo } = storeInfoStore

// Form
const form = ref(null)
const isFormValid = ref(true)
const editedStore = ref({})

// Thêm trạng thái thông báo
const showSuccessAlert = ref(false)
const successMessage = ref('')
const showErrorAlert = ref(false)
const errorMessage = ref('')
const showConfirmDialog = ref(false)

// Phương thức
function resetForm() {
  // Reset form về giá trị ban đầu
  editedStore.value = { ...storeInfo.value }
  
  // Ẩn các thông báo
  showSuccessAlert.value = false
  showErrorAlert.value = false
  showConfirmDialog.value = false
}

async function saveStoreInfo() {
  if (!isFormValid.value) return
  
  // Hiển thị hộp thoại xác nhận
  showConfirmDialog.value = true
}

async function confirmSave() {
  try {
    // Tạo một bản sao plain object từ editedStore để tránh tham chiếu vòng tròn
    const storeData = { ...editedStore.value }
    
    // Đảm bảo định dạng thời gian HH:mm:ss cho backend
    if (storeData.openTime) {
      // Đếm số dấu hai chấm để xác định định dạng hiện tại
      const colonCount = (storeData.openTime.match(/:/g) || []).length;
      
      if (colonCount === 1) {
        // Nếu chỉ có 1 dấu hai chấm (HH:mm), thêm phần giây
        storeData.openTime = storeData.openTime + ':00';
      } else if (colonCount > 2) {
        // Nếu có nhiều hơn 2 dấu, cắt lấy chỉ HH:mm:ss
        const timeParts = storeData.openTime.split(':');
        storeData.openTime = `${timeParts[0]}:${timeParts[1]}:${timeParts[2]}`;
      }
    }
    
    if (storeData.closeTime) {
      // Đếm số dấu hai chấm để xác định định dạng hiện tại
      const colonCount = (storeData.closeTime.match(/:/g) || []).length;
      
      if (colonCount === 1) {
        // Nếu chỉ có 1 dấu hai chấm (HH:mm), thêm phần giây
        storeData.closeTime = storeData.closeTime + ':00';
      } else if (colonCount > 2) {
        // Nếu có nhiều hơn 2 dấu, cắt lấy chỉ HH:mm:ss
        const timeParts = storeData.closeTime.split(':');
        storeData.closeTime = `${timeParts[0]}:${timeParts[1]}:${timeParts[2]}`;
      }
    }
    
    console.log('Dữ liệu gửi đi:', storeData);
    
    // Gọi API cập nhật với dữ liệu đã được xử lý
    await updateStoreInfo(storeData)
    
    // Hiển thị thông báo thành công
    successMessage.value = 'Cập nhật thông tin cửa hàng thành công!'
    showSuccessAlert.value = true
    showErrorAlert.value = false
    
    // Đóng hộp thoại xác nhận
    showConfirmDialog.value = false
    
    // Tự động ẩn thông báo sau 2 giây
    setTimeout(() => {
      showSuccessAlert.value = false
    }, 2000)
  } catch (err) {
    // Hiển thị thông báo lỗi
    console.error('Lỗi khi cập nhật thông tin cửa hàng:', err)
    errorMessage.value = err.response?.data?.detail || err.message || 'Đã xảy ra lỗi khi cập nhật thông tin cửa hàng'
    showErrorAlert.value = true
    showSuccessAlert.value = false
    
    // Đóng hộp thoại xác nhận
    showConfirmDialog.value = false
  }
}

// Theo dõi thay đổi của storeInfo để cập nhật form
function updateEditedStore() {
  editedStore.value = { ...storeInfo.value }
}

// Mounted
onMounted(async () => {
  try {
    await fetchStoreInfo()
    editedStore.value = { ...storeInfo.value }
  } catch (error) {
    // Nếu có lỗi khi lấy thông tin, tạo dữ liệu mẫu
    editedStore.value = {
      id: 1,
      name: 'Cửa hàng Trà sữa',
      address: '123 Đường ABC, Quận 1, TP.HCM',
      phone: '0123456789',
      email: 'info@milktea.com',
      taxCode: '0123456789',
      openTime: '08:00',
      closeTime: '22:00',
      openingDate: '2023-01-01'
    }
  }
})

// Kiểm tra quyền chỉnh sửa (đơn giản là ROLE_MANAGER)
const hasEditPermission = computed(() => {
  // Tạm thời trả về true để luôn hiển thị form chỉnh sửa
  return true
  
  // Dòng code gốc - bỏ comment khi triển khai thực tế
  // const userRole = localStorage.getItem('userRole')
  // return userRole === 'MANAGER'
})
</script> 