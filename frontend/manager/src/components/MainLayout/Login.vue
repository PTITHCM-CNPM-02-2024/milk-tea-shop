<template>
  <div class="flex items-center justify-center h-screen bg-[#8CF5D2]">
    <div class="flex bg-white rounded-lg shadow-lg overflow-hidden w-3/4 max-w-4xl">
      <!-- Left Side (Image) -->
      <div
        class="w-1/2 h-[600px] bg-cover bg-center"
        style="background-image: url('/public/images/anh1.jpg')"
      ></div>

      <!-- Right Side (Form) -->
      <div class="w-1/2 p-12 bg-[#60667F] text-white rounded-r-lg">
        <h2 class="text-2xl font-semibold">Welcome Back</h2>
        <p class="text-gray-400 mb-6">Please login to your account</p>

        <form @submit.prevent="validateForm">
          <div class="mb-4">
            <label class="block text-gray-300" for="email">Email address</label>
            <input
              v-model="email"
              id="email"
              type="email"
              placeholder="jubear@gmail.com"
              class="w-full p-2 mt-1 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-600"
            />
            <p v-if="errors.email" class="text-red-500 text-sm mt-1">{{ errors.email }}</p>
          </div>

          <div class="mb-4 relative">
            <label class="block text-gray-300" for="password">Password</label>
            <input
              v-model="password"
              id="password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="Password"
              class="w-full p-2 mt-1 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-600"
            />
            <span
              class="absolute top-10 right-4 text-gray-400 cursor-pointer"
              @click="showPassword = !showPassword"
              >👁</span
            >
            <p class="text-red-500 text-sm mt-1 h-5">{{ errors.password }}</p>
          </div>

          <div class="flex items-center justify-between mb-4">
            <div>
              <input type="checkbox" id="remember" class="mr-1" />
              <label for="remember" class="text-gray-400 text-sm">Remember me</label>
            </div>
            <a href="#" class="text-gray-400 text-sm hover:underline">Forgot password?</a>
          </div>

          <button
            type="submit"
            class="w-full bg-[#E93D2D] text-gray-900 py-2 rounded font-semibold hover:bg-gray-300 transition"
          >
            Sign In
          </button>
        </form>

        <div class="mt-4 text-center text-gray-400">
          <p>Or</p>
          <button
            class="mt-2 w-full py-2 border border-gray-500 rounded hover:bg-gray-800 transition"
          >
            Sign in with Google
          </button>
        </div>

        <p class="mt-6 text-center text-gray-400 text-sm">
          Don't have an account?
          <a href="#" class="text-white font-semibold hover:underline">Sign up</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      email: '',
      password: '',
      showPassword: false,
      errors: {},
    }
  },
  methods: {
    validateForm() {
      this.errors = {} // Reset lỗi

      // Kiểm tra email hợp lệ
      if (!this.email) {
        this.errors.email = '❌ Email không được để trống.'
      } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(this.email)) {
        this.errors.email = '❌ Email không đúng định dạng.'
      }

      // Kiểm tra password hợp lệ
      if (!this.password) {
        this.errors.password = '❌ Password không được để trống.'
      } else if (!/(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=]).{8,}$/.test(this.password)) {
        this.errors.password =
          '❌ Password phải có ít nhất 8 ký tự, chứa chữ hoa, số và ký tự đặc biệt.'
      }

      // Nếu không có lỗi -> Xử lý đăng nhập (ví dụ: gửi request API)
      if (Object.keys(this.errors).length === 0) {
        alert('✅ Đăng nhập thành công!')
      }
    },
  },
}
</script>
