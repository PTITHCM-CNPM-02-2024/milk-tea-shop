<template>
  <dashboard-layout>
    <v-container>
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
          <v-tabs v-model="activeTab" bg-color="primary">
            <v-tab value="info">
              <v-icon start>mdi-information-outline</v-icon>
              Thông tin chung
            </v-tab>
            <v-tab value="edit" v-if="hasEditPermission">
              <v-icon start>mdi-pencil</v-icon>
              Chỉnh sửa
            </v-tab>
          </v-tabs>
          
          <v-window v-model="activeTab">
            <!-- Tab thông tin -->
            <v-window-item value="info">
              <v-card-text>
                <v-row>
                  <v-col cols="12" md="6">
                    <!-- Thông tin cơ bản -->
                    <v-list>
                      <v-list-subheader>Thông tin cơ bản</v-list-subheader>
                      
                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-store</v-icon>
                        </template>
                        <v-list-item-title>Tên cửa hàng</v-list-item-title>
                        <v-list-item-subtitle>{{ storeInfo.name }}</v-list-item-subtitle>
                      </v-list-item>
                      
                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-map-marker</v-icon>
                        </template>
                        <v-list-item-title>Địa chỉ</v-list-item-title>
                        <v-list-item-subtitle>{{ storeInfo.address }}</v-list-item-subtitle>
                      </v-list-item>
                      
                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-calendar-check</v-icon>
                        </template>
                        <v-list-item-title>Ngày khai trương</v-list-item-title>
                        <v-list-item-subtitle>{{ formattedOpeningDate }}</v-list-item-subtitle>
                      </v-list-item>
                    </v-list>
                  </v-col>
                  
                  <v-col cols="12" md="6">
                    <!-- Thông tin liên hệ -->
                    <v-list>
                      <v-list-subheader>Thông tin liên hệ</v-list-subheader>
                      
                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-phone</v-icon>
                        </template>
                        <v-list-item-title>Số điện thoại</v-list-item-title>
                        <v-list-item-subtitle>{{ storeInfo.phone }}</v-list-item-subtitle>
                      </v-list-item>
                      
                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-email</v-icon>
                        </template>
                        <v-list-item-title>Email</v-list-item-title>
                        <v-list-item-subtitle>{{ storeInfo.email }}</v-list-item-subtitle>
                      </v-list-item>
                      
                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-file-document</v-icon>
                        </template>
                        <v-list-item-title>Mã số thuế</v-list-item-title>
                        <v-list-item-subtitle>{{ storeInfo.taxCode }}</v-list-item-subtitle>
                      </v-list-item>
                    </v-list>
                  </v-col>
                  
                  <v-col cols="12">
                    <!-- Thời gian hoạt động -->
                    <v-list>
                      <v-list-subheader>Thời gian hoạt động</v-list-subheader>
                      
                      <v-list-item>
                        <template v-slot:prepend>
                          <v-icon color="primary">mdi-clock-outline</v-icon>
                        </template>
                        <v-list-item-title>Giờ mở cửa - đóng cửa</v-list-item-title>
                        <v-list-item-subtitle>{{ formattedOpenTime }} - {{ formattedCloseTime }}</v-list-item-subtitle>
                      </v-list-item>
                    </v-list>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-window-item>
            
            <!-- Tab chỉnh sửa -->
            <v-window-item value="edit" v-if="hasEditPermission">
              <v-card-text>
                <v-form ref="form" v-model="isFormValid" @submit.prevent="saveStoreInfo">
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedStore.name"
                        label="Tên cửa hàng"
                        :rules="[v => !!v || 'Vui lòng nhập tên cửa hàng']"
                        prepend-inner-icon="mdi-store"
                        variant="outlined"
                      ></v-text-field>
                      
                      <v-text-field
                        v-model="editedStore.address"
                        label="Địa chỉ"
                        :rules="[v => !!v || 'Vui lòng nhập địa chỉ']"
                        prepend-inner-icon="mdi-map-marker"
                        variant="outlined"
                      ></v-text-field>
                      
                      <v-text-field
                        v-model="editedStore.phone"
                        label="Số điện thoại"
                        :rules="[v => !!v || 'Vui lòng nhập số điện thoại']"
                        prepend-inner-icon="mdi-phone"
                        variant="outlined"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedStore.email"
                        label="Email"
                        :rules="[
                          v => !!v || 'Vui lòng nhập email',
                          v => /.+@.+\..+/.test(v) || 'Email không hợp lệ'
                        ]"
                        prepend-inner-icon="mdi-email"
                        variant="outlined"
                      ></v-text-field>
                      
                      <v-text-field
                        v-model="editedStore.taxCode"
                        label="Mã số thuế"
                        prepend-inner-icon="mdi-file-document"
                        variant="outlined"
                      ></v-text-field>
                      
                      <v-text-field
                        v-model="editedStore.openingDate"
                        label="Ngày khai trương"
                        type="date"
                        prepend-inner-icon="mdi-calendar"
                        variant="outlined"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedStore.openTime"
                        label="Giờ mở cửa"
                        type="time"
                        prepend-inner-icon="mdi-clock-outline"
                        variant="outlined"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedStore.closeTime"
                        label="Giờ đóng cửa"
                        type="time"
                        prepend-inner-icon="mdi-clock-outline"
                        variant="outlined"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                  
                  <v-divider class="my-4"></v-divider>
                  
                  <div class="d-flex justify-end">
                    <v-btn
                      color="grey-darken-1"
                      variant="text"
                      class="mr-2"
                      @click="resetForm"
                    >
                      Hủy
                    </v-btn>
                    
                    <v-btn
                      color="primary"
                      type="submit"
                      :loading="loading"
                      :disabled="!isFormValid"
                    >
                      <v-icon left>mdi-content-save</v-icon>
                      Lưu thay đổi
                    </v-btn>
                  </div>
                </v-form>
              </v-card-text>
            </v-window-item>
          </v-window>
        </template>
      </v-card>
    </v-container>
  </dashboard-layout>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useStoreInfoStore } from '@/stores/store'
import DashboardLayout from '@/components/layouts/DashboardLayout.vue'

// Store
const storeInfoStore = useStoreInfoStore()
const { 
  storeInfo, 
  loading, 
  error, 
  formattedOpeningDate, 
  formattedOpenTime, 
  formattedCloseTime, 
  fetchStoreInfo, 
  updateStoreInfo 
} = storeInfoStore

// Kiểm tra quyền chỉnh sửa (đơn giản là ROLE_MANAGER)
const hasEditPermission = computed(() => {
  // Đọc từ localStorage hoặc vuex store, tùy vào ứng dụng của bạn
  const userRole = localStorage.getItem('userRole')
  return userRole === 'MANAGER'
})

// Form
const form = ref(null)
const isFormValid = ref(false)
const activeTab = ref('info')
const editedStore = reactive({
  id: null,
  name: '',
  address: '',
  phone: '',
  email: '',
  taxCode: '',
  openTime: '',
  closeTime: '',
  openingDate: ''
})

// Phương thức
function resetForm() {
  // Quay về tab thông tin
  activeTab.value = 'info'
  
  // Reset form về giá trị ban đầu
  Object.assign(editedStore, storeInfo.value)
}

async function saveStoreInfo() {
  if (!isFormValid.value) return
  
  try {
    await updateStoreInfo(editedStore)
    activeTab.value = 'info'
  } catch (err) {
    console.error('Lỗi khi cập nhật thông tin cửa hàng:', err)
  }
}

// Theo dõi thay đổi của storeInfo để cập nhật form
function updateEditedStore() {
  Object.assign(editedStore, storeInfo.value)
}

// Mounted
onMounted(async () => {
  await fetchStoreInfo()
  updateEditedStore()
})
</script> 