<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- Phần trái - Ảnh và thông tin -->
      <div class="login-banner">
        <div class="banner-overlay"></div>
        <div class="banner-content">
          <h1 class="text-h3 font-weight-bold text-white mb-4">Milk Tea Shop</h1>
          <p class="text-h6 text-white">Hệ thống quản lý cửa hàng trà sữa</p>
        </div>
      </div>

      <!-- Phần phải - Form đăng nhập -->
      <div class="login-form-container">
        <div class="login-form-content">
          <div class="text-center mb-6">
            <img 
              src="@/assets/logo.jpg" 
              alt="Milk Tea Logo" 
              height="80" 
              class="mb-4" 
            />
            <h2 class="text-h4 font-weight-bold">Đăng nhập</h2>
            <p class="text-subtitle-1 text-medium-emphasis mt-1">
              Đăng nhập vào tài khoản quản lý của bạn
            </p>
          </div>

          <v-form ref="form" v-model="valid" @submit.prevent="login">
            <v-alert
              v-if="error"
              type="error"
              variant="tonal"
              density="compact"
              closable
              class="mb-4 text-justify"
              @click:close="error = null"
            >
              {{ error }}
            </v-alert>

            <v-text-field
              v-model="username"
              label="Tên đăng nhập"
              variant="outlined"
              prepend-inner-icon="mdi-account"
              :rules="[v => !!v || 'Vui lòng nhập tên đăng nhập']"
              required
              autofocus
              class="mb-3"
            ></v-text-field>

            <v-text-field
              v-model="password"
              label="Mật khẩu"
              variant="outlined"
              prepend-inner-icon="mdi-lock"
              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              :type="showPassword ? 'text' : 'password'"
              :rules="[v => !!v || 'Vui lòng nhập mật khẩu']"
              required
              @click:append-inner="showPassword = !showPassword"
              class="mb-6"
            ></v-text-field>

            <v-btn
              block
              color="primary"
              size="large"
              type="submit"
              :loading="loading"
              class="mb-4"
            >
              Đăng nhập
            </v-btn>

            <div class="text-center text-body-2 text-medium-emphasis mt-4">
              © {{ new Date().getFullYear() }} Milk Tea Shop Management System
            </div>
          </v-form>
        </div>
      </div>
    </div>
    
    <!-- Hình ảnh Panda -->
    <div class="panda-image">
      <img 
        src="https://cdn-icons-png.flaticon.com/512/2003/2003736.png" 
        alt="Panda" 
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authService } from '@/services/authService'

const router = useRouter()
const username = ref('')
const password = ref('')
const valid = ref(false)
const loading = ref(false)
const error = ref(null)
const showPassword = ref(false)
const form = ref(null)

// Xử lý đăng nhập
const login = async () => {
  if (!form.value || ! await form.value.validate()) return
  
  loading.value = true
  error.value = null
  
  try {
    // Gọi API đăng nhập
    const response = await authService.login(username.value, password.value)
    
    // Lưu token vào localStorage
    const { accessToken, id } = response.data
    authService.saveToken(accessToken)
    
    // Lấy thông tin quản lý từ id tài khoản
    const managerResponse = await authService.getManagerByAccountId(id)
    
    // Lưu thông tin người dùng
    const userInfo = {
      accountId: id,
      managerId: managerResponse.data.id,
      username: username.value
    }
    authService.saveUserInfo(userInfo)
    
    // Chuyển hướng đến trang dashboard
    router.push('/')
  } catch (err) {
    // Xử lý lỗi đăng nhập
    if (err.response && err.response.data) {
      // Ưu tiên hiển thị lỗi cụ thể từ ProblemDetail nếu có
      const errorData = err.response.data
      console.log(errorData);
      error.value = errorData.detail || 'Tên đăng nhập hoặc mật khẩu không chính xác.'; // Thông báo chung cho lỗi 401
    } else {
      error.value = 'Đã xảy ra lỗi khi đăng nhập. Vui lòng thử lại sau.'
      console.error('Login error:', err)
    }
  } finally {
    loading.value = false
  }
}

// Kiểm tra nếu đã đăng nhập thì chuyển hướng đến dashboard
onMounted(() => {
  if (authService.isAuthenticated()) {
    router.push('/')
  }
})
</script>

<style scoped>
.login-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  position: relative;
  overflow: hidden;
}

.login-wrapper {
  width: 900px;
  min-height: 500px;
  display: flex;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  background-color: white;
  position: relative;
  z-index: 10;
}

.login-banner {
  flex: 1;
  position: relative;
  background-image: url('https://images.unsplash.com/photo-1561296160-7ea9d1b5511d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=800&q=80');
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(115, 103, 240, 0.9) 0%, rgba(40, 48, 129, 0.85) 100%);
  z-index: 1;
}

.banner-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 2rem;
}

.login-form-container {
  flex: 1;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-form-content {
  max-width: 360px;
  margin: 0 auto;
  width: 100%;
}

.panda-image {
  position: absolute;
  bottom: -10px;
  right: 15%;
  z-index: 5;
  width: 250px;
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: float 3s ease-in-out infinite;
}

.panda-image img {
  width: 100%;
  height: auto;
  object-fit: contain;
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
  100% { transform: translateY(0px); }
}

@media (max-width: 960px) {
  .login-wrapper {
    width: 95%;
    flex-direction: column;
  }
  
  .login-banner {
    height: 200px;
  }
  
  .login-form-container {
    padding: 2rem 1.5rem;
  }
  
  .panda-image {
    width: 150px;
    height: 150px;
    right: 10px;
    bottom: 10px;
  }
}

/* Animation */
.login-banner {
  animation: fadeIn 1s ease-in-out;
}

.login-form-container {
  animation: slideIn 0.8s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { 
    transform: translateY(20px);
    opacity: 0;
  }
  to { 
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
