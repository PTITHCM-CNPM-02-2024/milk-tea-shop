<template>
  <v-container class="profile-container py-6">
    <v-row>
      <v-col cols="12">
        <h2 class="text-h4 mb-6">Thông tin tài khoản</h2>
      </v-col>
    </v-row>

    <!-- Loading và thông báo lỗi -->
    <v-row v-if="loading">
      <v-col cols="12" class="text-center py-8">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
        <div class="mt-4">Đang tải thông tin...</div>
      </v-col>
    </v-row>
    
    <v-row v-else-if="error" class="text-center py-8">
      <v-col cols="12">
        <v-alert type="error" class="mx-auto" max-width="500">
          {{ errorMessage }}
        </v-alert>
        <v-btn color="primary" class="mt-4" @click="loadData">
          Thử lại
        </v-btn>
      </v-col>
    </v-row>

    <!-- Main content -->
    <template v-else>
      <!-- Thông báo thành công -->
      <v-snackbar
        v-model="showSuccessMessage"
        :timeout="3000"
        color="success"
      >
        {{ successMessage }}
        <template v-slot:actions>
          <v-btn
            variant="text"
            @click="showSuccessMessage = false"
          >
            Đóng
          </v-btn>
        </template>
      </v-snackbar>

      <v-row>
        <v-col cols="12" md="4">
          <v-card class="mb-4">
            <v-card-item>
              <v-card-title>Thông tin cá nhân</v-card-title>
            </v-card-item>
            
            <v-card-text>
              <div class="text-center mb-6">
                <v-avatar color="primary" size="120">
                  <span class="text-h4 text-white">
                    {{ getInitials(userInfo) }}
                  </span>
                </v-avatar>
                <h3 class="text-h5 mt-2">{{ getFullName(userInfo) }}</h3>
                <div class="text-body-1 text-grey">{{ userInfo.position }}</div>
              </div>
              
              <!-- Chế độ xem thông tin cá nhân -->
              <v-list>
                <v-list-item prepend-icon="mdi-email-outline">
                  <v-list-item-title>Email</v-list-item-title>
                  <v-list-item-subtitle>{{ userInfo.email || 'Chưa cập nhật' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item prepend-icon="mdi-phone-outline">
                  <v-list-item-title>Số điện thoại</v-list-item-title>
                  <v-list-item-subtitle>{{ userInfo.phone || 'Chưa cập nhật' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item prepend-icon="mdi-gender-male-female">
                  <v-list-item-title>Giới tính</v-list-item-title>
                  <v-list-item-subtitle>{{ formatGender(userInfo.gender) }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>
        </v-col>
        
        <v-col cols="12" md="8">
          <v-card class="mb-4">
            <v-card-item>
              <v-card-title class="d-flex justify-space-between align-center">
                <span>Thông tin tài khoản</span>
                <v-btn
                  v-if="!editingUsername"
                  icon
                  variant="text"
                  color="primary"
                  @click="startEditUsername"
                >
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
              </v-card-title>
            </v-card-item>
            
            <v-card-text>
              <v-form v-if="editingUsername" ref="usernameForm" @submit.prevent="saveUsername">
                <v-text-field
                  v-model="editedUsername"
                  label="Tên đăng nhập"
                  required
                  :rules="[v => !!v || 'Tên đăng nhập là bắt buộc']"
                ></v-text-field>
                
                <div class="d-flex justify-end gap-2 mt-4">
                  <v-btn @click="cancelEditUsername" color="grey" variant="text">Hủy</v-btn>
                  <v-btn type="submit" color="primary" :loading="saving" :disabled="!isUsernameFormValid">Lưu</v-btn>
                </div>
              </v-form>
              
              <v-list v-else>
                <v-list-item prepend-icon="mdi-account-outline">
                  <v-list-item-title>Tên đăng nhập</v-list-item-title>
                  <v-list-item-subtitle>{{ accountInfo.username }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item prepend-icon="mdi-shield-account-outline">
                  <v-list-item-title>Vai trò</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip color="primary" size="small" class="mr-2">
                      {{ accountInfo.role?.name }}
                    </v-chip>
                    {{ accountInfo.role?.description }}
                  </v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item prepend-icon="mdi-text-box-outline">
                  <v-list-item-title>Chức vụ</v-list-item-title>
                  <v-list-item-subtitle>{{ userInfo.position || 'Không có mô tả' }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>
          
          <v-card>
            <v-card-item>
              <v-card-title>Bảo mật tài khoản</v-card-title>
            </v-card-item>
            
            <v-card-text>
              <div class="mb-4">
                <h3 class="text-subtitle-1 mb-2">Đổi mật khẩu</h3>
                <p class="text-body-2 text-grey">Đổi mật khẩu định kỳ để bảo vệ tài khoản của bạn.</p>
              </div>
              
              <v-form v-if="changingPassword" ref="passwordForm" @submit.prevent="savePassword">
                <v-text-field
                  v-model="passwordData.oldPassword"
                  label="Mật khẩu hiện tại"
                  type="password"
                  :rules="[v => !!v || 'Mật khẩu hiện tại là bắt buộc']"
                ></v-text-field>
                
                <v-text-field
                  v-model="passwordData.newPassword"
                  label="Mật khẩu mới"
                  type="password"              
                  :rules="[v => !!v || 'Mật khẩu mới là bắt buộc']"
                ></v-text-field>
                
                <v-text-field
                  v-model="passwordData.confirmPassword"
                  label="Xác nhận mật khẩu"
                  type="password"
                  :rules="[
                    v => !!v || 'Xác nhận mật khẩu là bắt buộc',
                    v => v === passwordData.newPassword || 'Mật khẩu không khớp'
                  ]"
                ></v-text-field>
                
                <div class="d-flex justify-end gap-2 mt-4">
                  <v-btn @click="cancelChangePassword" color="grey" variant="text">Hủy</v-btn>
                  <v-btn type="submit" color="primary" :loading="saving" :disabled="!isPasswordFormValid">Lưu</v-btn>
                </div>
              </v-form>
              
              <v-btn 
                v-else
                color="primary" 
                prepend-icon="mdi-lock-reset"
                @click="startChangePassword"
              >
                Đổi mật khẩu
              </v-btn>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </template>
  </v-container>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import AccountService from '../services/account.service';

// Props từ App.vue
const props = defineProps({
  employeeId: {
    type: Number,
    required: true
  },
  accountId: {
    type: Number,
    default: 2
  }
});

// State
const userInfo = ref({});
const accountInfo = ref({});
const loading = ref(true);
const error = ref(false);
const errorMessage = ref('');
const saving = ref(false);

// Thông báo thành công
const showSuccessMessage = ref(false);
const successMessage = ref('');

// Biến chỉnh sửa thông tin
const editingUsername = ref(false);
const changingPassword = ref(false);

// Form refs
const usernameForm = ref(null);
const passwordForm = ref(null);

// Dữ liệu chỉnh sửa
const editedUsername = ref('');

const passwordData = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// Thêm các biến theo dõi trạng thái hợp lệ của form
const isUsernameFormValid = ref(false);
const isPasswordFormValid = ref(false);

// Lấy chữ cái đầu tiên của tên và họ
function getInitials(user) {
  if (!user || (!user.firstName && !user.lastName)) return '?';
  
  let initials = '';
  if (user.firstName) initials += user.firstName.charAt(0);
  if (user.lastName) initials += user.lastName.charAt(0);
  
  return initials.toUpperCase();
}

// Lấy họ tên đầy đủ và viết hoa toàn bộ
function getFullName(user) {
  if (!user) return 'CHƯA CẬP NHẬT';
  
  let fullName = '';
  if (user.lastName) fullName += user.lastName.toUpperCase() + ' ';
  if (user.firstName) fullName += user.firstName.toUpperCase();
  
  return fullName.trim() || 'CHƯA CẬP NHẬT';
}

// Format giới tính
function formatGender(gender) {
  if (!gender) return 'Chưa cập nhật';
  
  switch(gender) {
    case 'MALE': return 'Nam';
    case 'FEMALE': return 'Nữ';
    case 'OTHER': return 'Khác';
    default: return gender;
  }
}

// Tải dữ liệu
async function loadData() {
  loading.value = true;
  error.value = false;
  
  try {
    // Lấy thông tin nhân viên từ employeeId
    const userResponse = await AccountService.getEmployeeInfo(props.employeeId);
    userInfo.value = userResponse.data;
    
    // Lấy thông tin tài khoản
    const accountResponse = await AccountService.getAccountInfo(props.accountId);
    accountInfo.value = accountResponse.data;
    
  } catch (err) {
    console.error('Error loading user data:', err);
    error.value = true;
    errorMessage.value = err.message || 'Không thể tải thông tin người dùng. Vui lòng thử lại sau.';
  } finally {
    loading.value = false;
  }
}

// Bắt đầu chỉnh sửa tên đăng nhập
function startEditUsername() {
  editedUsername.value = accountInfo.value.username || '';
  editingUsername.value = true;
}

// Hủy chỉnh sửa tên đăng nhập
function cancelEditUsername() {
  editingUsername.value = false;
}

// Lưu tên đăng nhập
async function saveUsername() {
  const { valid } = await usernameForm.value.validate();
  if (!valid) return;
  
  saving.value = true;
  try {
    await AccountService.updateAccount(props.accountId, editedUsername.value);
    
    // Cập nhật dữ liệu hiển thị
    accountInfo.value.username = editedUsername.value;
    
    // Hiển thị thông báo
    successMessage.value = 'Cập nhật tên đăng nhập thành công';
    showSuccessMessage.value = true;
    
    // Tắt chế độ chỉnh sửa
    editingUsername.value = false;
  } catch (err) {
    console.error('Error updating username:', err);
    errorMessage.value = err.message || 'Không thể cập nhật tên đăng nhập. Vui lòng thử lại sau.';
    error.value = true;
  } finally {
    saving.value = false;
  }
}

// Bắt đầu đổi mật khẩu
function startChangePassword() {
  passwordData.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  changingPassword.value = true;
}

// Hủy đổi mật khẩu
function cancelChangePassword() {
  changingPassword.value = false;
}

// Lưu mật khẩu mới
async function savePassword() {
  const { valid } = await passwordForm.value.validate();
  if (!valid) return;
  
  saving.value = true;
  try {
    await AccountService.changePassword(
      props.accountId,
      passwordData.value.oldPassword,
      passwordData.value.newPassword,
      passwordData.value.confirmPassword
    );
    
    // Hiển thị thông báo
    successMessage.value = 'Đổi mật khẩu thành công';
    showSuccessMessage.value = true;
    
    // Tắt chế độ đổi mật khẩu
    changingPassword.value = false;
  } catch (err) {
    console.error('Error changing password:', err);
    errorMessage.value = err.message || 'Không thể đổi mật khẩu. Vui lòng thử lại sau.';
    error.value = true;
  } finally {
    saving.value = false;
  }
}

// Hàm kiểm tra form username
async function validateUsernameForm() {
  if (!usernameForm.value) return;
  const { valid } = await usernameForm.value.validate();
  isUsernameFormValid.value = valid;
}

// Hàm kiểm tra form password
async function validatePasswordForm() {
  if (!passwordForm.value) return;
  const { valid } = await passwordForm.value.validate();
  isPasswordFormValid.value = valid;
}

// Theo dõi thay đổi của username
watch(() => editedUsername.value, validateUsernameForm);

// Theo dõi thay đổi của password data
watch([
  () => passwordData.value.oldPassword,
  () => passwordData.value.newPassword,
  () => passwordData.value.confirmPassword
], validatePasswordForm);

// Khi form được mở, kiểm tra ngay
watch(() => editingUsername.value, (val) => {
  if (val) setTimeout(validateUsernameForm, 0);
});

watch(() => changingPassword.value, (val) => {
  if (val) setTimeout(validatePasswordForm, 0);
});

// Tải dữ liệu khi component được tạo
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
}

.gap-2 {
  gap: 8px;
}
</style> 