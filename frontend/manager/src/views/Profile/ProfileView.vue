<template>
  <div>
    <v-container>
      <v-row>
        <v-col cols="12" lg="8" class="mx-auto">
          <v-card class="mt-4">
            <v-card-title class="text-h5 d-flex align-center pa-4">
              <v-icon icon="mdi-account-circle" size="large" class="mr-2"></v-icon>
              Hồ sơ cá nhân
            </v-card-title>

            <v-divider></v-divider>

            <v-card-text class="pa-4">
              <div v-if="managerStore.loading" class="d-flex justify-center py-4">
                <v-progress-circular indeterminate color="primary"></v-progress-circular>
              </div>

              <v-alert
                v-if="managerStore.error"
                type="error"
                variant="tonal"
                closable
                class="mb-4"
              >
                {{ managerStore.error }}
              </v-alert>

              <v-alert
                v-if="successMessage"
                type="success"
                variant="tonal"
                closable
                class="mb-4"
              >
                {{ successMessage }}
              </v-alert>

              <v-form
                ref="form"
                v-model="valid"
                @submit.prevent="updateProfile"
                v-if="!managerStore.loading"
              >
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="profile.firstName"
                      label="Tên"
                      variant="outlined"
                      :rules="nameRules"
                      required
                    ></v-text-field>
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="profile.lastName"
                      label="Họ"
                      variant="outlined"
                      :rules="nameRules"
                      required
                    ></v-text-field>
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="profile.email"
                      label="Email"
                      variant="outlined"
                      :rules="emailRules"
                      required
                      type="email"
                    ></v-text-field>
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="profile.phone"
                      label="Số điện thoại"
                      variant="outlined"
                      :rules="phoneRules"
                      required
                    ></v-text-field>
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-select
                      v-model="profile.gender"
                      :items="genderOptions"
                      label="Giới tính"
                      variant="outlined"
                      required
                    ></v-select>
                  </v-col>

                  <v-col cols="12">
                    <v-divider class="mb-4"></v-divider>
                    <v-btn
                      color="primary"
                      type="submit"
                      :loading="updating"
                      :disabled="!valid"
                      block
                    >
                      Cập nhật thông tin
                    </v-btn>
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
          </v-card>

          <v-card class="mt-4">
            <v-card-title class="text-h5 d-flex align-center pa-4">
              <v-icon icon="mdi-lock" size="large" class="mr-2"></v-icon>
              Thông tin tài khoản
            </v-card-title>

            <v-divider></v-divider>

            <v-card-text class="pa-4">
              <div v-if="accountLoading" class="d-flex justify-center py-4">
                <v-progress-circular indeterminate color="primary"></v-progress-circular>
              </div>

              <v-alert
                v-if="accountError"
                type="error"
                variant="tonal"
                closable
                class="mb-4"
              >
                {{ accountError }}
              </v-alert>

              <v-alert
                v-if="accountSuccessMessage"
                type="success"
                variant="tonal"
                closable
                class="mb-4"
              >
                {{ accountSuccessMessage }}
              </v-alert>

              <v-row v-if="!accountLoading">
                <v-col cols="12">
                  <div class="d-flex align-center justify-space-between py-2">
                    <div class="d-flex align-center">
                      <v-icon icon="mdi-account" class="mr-4" color="primary"></v-icon>
                      <div>
                        <div class="text-subtitle-1 font-weight-medium">Tên đăng nhập</div>
                        <div class="text-body-2 text-medium-emphasis">
                          {{ accountInfo.username }}
                        </div>
                      </div>
                    </div>
                    <v-btn
                      size="small"
                      color="primary"
                      variant="text"
                      icon="mdi-pencil"
                      @click="usernameDialog = true"
                    ></v-btn>
                  </div>
                </v-col>

                <v-col cols="12">
                  <div class="d-flex align-center py-2">
                    <v-icon icon="mdi-account-key" class="mr-4" color="primary"></v-icon>
                    <div>
                      <div class="text-subtitle-1 font-weight-medium">Vai trò</div>
                      <div class="text-body-2 text-medium-emphasis">
                        {{ accountInfo.role ? accountInfo.role.name : 'Đang tải...' }}
                      </div>
                    </div>
                  </div>
                </v-col>

                <v-col cols="12">
                  <div class="d-flex align-center py-2" v-if="accountInfo.role && accountInfo.role.description">
                    <v-icon icon="mdi-information-outline" class="mr-4" color="primary"></v-icon>
                    <div>
                      <div class="text-subtitle-1 font-weight-medium">Mô tả vai trò</div>
                      <div class="text-body-2 text-medium-emphasis">
                        {{ accountInfo.role.description }}
                      </div>
                    </div>
                  </div>
                </v-col>

                <v-col cols="12" class="d-flex align-center justify-center py-3">
                  <v-btn
                    color="primary"
                    variant="outlined"
                    prepend-icon="mdi-lock-reset"
                    @click="passwordDialog = true"
                  >
                    Thay đổi mật khẩu
                  </v-btn>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- Dialog thay đổi tên đăng nhập -->
    <v-dialog v-model="usernameDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5 d-flex align-center pa-4">
          <v-icon icon="mdi-account-edit" class="mr-2"></v-icon>
          Thay đổi tên đăng nhập
        </v-card-title>

        <v-divider></v-divider>

        <v-card-text class="pa-4">
          <v-alert
            v-if="usernameError"
            type="error"
            variant="tonal"
            closable
            class="mb-4"
          >
            {{ usernameError }}
          </v-alert>

          <v-form ref="usernameForm" v-model="usernameValid" @submit.prevent="updateUsername">
            <v-text-field
              v-model="newUsername"
              label="Tên đăng nhập mới"
              variant="outlined"
              :rules="usernameRules"
              required
              class="mb-2"
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4 pt-0">
          <v-spacer></v-spacer>
          <v-btn color="error" variant="text" @click="closeUsernameDialog">Hủy</v-btn>
          <v-btn
            color="primary"
            variant="elevated"
            :loading="updatingUsername"
            :disabled="!usernameValid"
            @click="updateUsername"
          >
            Xác nhận
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog thay đổi mật khẩu -->
    <v-dialog v-model="passwordDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5 d-flex align-center pa-4">
          <v-icon icon="mdi-lock-reset" class="mr-2"></v-icon>
          Thay đổi mật khẩu
        </v-card-title>

        <v-divider></v-divider>

        <v-card-text class="pa-4">
          <v-alert
            v-if="passwordError"
            type="error"
            variant="tonal"
            closable
            class="mb-4"
          >
            {{ passwordError }}
          </v-alert>

          <v-form ref="passwordForm" v-model="passwordValid" @submit.prevent="updatePassword">
            <v-text-field
              v-model="currentPassword"
              label="Mật khẩu hiện tại"
              variant="outlined"
              :rules="[v => !!v || 'Mật khẩu hiện tại là bắt buộc']"
              required
              type="password"
              class="mb-2"
            ></v-text-field>

            <v-text-field
              v-model="newPassword"
              label="Mật khẩu mới"
              variant="outlined"
              :rules="passwordRules"
              required
              type="password"
              class="mb-2"
            ></v-text-field>

            <v-text-field
              v-model="confirmPassword"
              label="Xác nhận mật khẩu mới"
              variant="outlined"
              :rules="[
                v => !!v || 'Xác nhận mật khẩu là bắt buộc',
                v => v === newPassword || 'Mật khẩu không khớp'
              ]"
              required
              type="password"
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4 pt-0">
          <v-spacer></v-spacer>
          <v-btn color="error" variant="text" @click="closePasswordDialog">Hủy</v-btn>
          <v-btn
            color="primary"
            variant="elevated"
            :loading="updatingPassword"
            :disabled="!passwordValid"
            @click="updatePassword"
          >
            Xác nhận
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useManagerStore } from '@/stores/manager'
import { useAccountStore } from '@/stores/account'

const managerStore = useManagerStore()
const accountStore = useAccountStore()

// Trạng thái form
const form = ref(null)
const valid = ref(false)
const updating = ref(false)
const successMessage = ref('')

// Trạng thái tài khoản
const accountLoading = ref(false)
const accountError = ref(null)
const accountSuccessMessage = ref('')
const accountInfo = reactive({
  id: null,
  username: '',
  role: {
    id: null,
    name: '',
    description: ''
  }
})

// Profile data
const profile = reactive({
  firstName: '',
  lastName: '',
  username: '',
  email: '',
  phone: '',
  gender: 'MALE',
  role: '',
  accountId: null
})

// Dialog thay đổi username
const usernameDialog = ref(false)
const usernameForm = ref(null)
const usernameValid = ref(false)
const newUsername = ref('')
const passwordConfirm = ref('')
const usernameError = ref('')
const updatingUsername = ref(false)

// Dialog thay đổi password
const passwordDialog = ref(false)
const passwordForm = ref(null)
const passwordValid = ref(false)
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const passwordError = ref('')
const updatingPassword = ref(false)

// Validation rules
const nameRules = [
  v => !!v || 'Trường này là bắt buộc',
  v => v.length <= 50 || 'Tên không được quá 50 ký tự'
]

// Giữ lại usernameRules để kiểm tra username trong dữ liệu
const usernameRules = [
  v => !!v || 'Tên đăng nhập là bắt buộc',
  v => (v && v.length >= 3) || 'Tên đăng nhập phải có ít nhất 3 ký tự',
  v => /^[a-zA-Z0-9._-]+$/.test(v) || 'Tên đăng nhập chỉ được chứa chữ cái, số và các ký tự . _ -'
]

const emailRules = [
  v => !!v || 'Email là bắt buộc',
  v => /^((?!\.)[\w\-_.]*[^.])(@\w+)(\.\w+(\.\w+)?[^.\W])$/.test(v) || 'Email không hợp lệ'
]

const phoneRules = [
  v => !!v || 'Số điện thoại là bắt buộc',
  v => /(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)/.test(v) || 'Số điện thoại không hợp lệ (10-11 số)'
]

const passwordRules = [
  v => !!v || 'Mật khẩu là bắt buộc',
  v => (v && v.length >= 6) || 'Mật khẩu phải có ít nhất 6 ký tự',
]

// Danh sách giới tính
const genderOptions = [
  { title: 'Nam', value: 'MALE' },
  { title: 'Nữ', value: 'FEMALE' },
  { title: 'Khác', value: 'OTHER' }
]

// Lấy thông tin tài khoản
const fetchAccountInfo = async (accountId) => {
  if (!accountId) return

  accountLoading.value = true
  accountError.value = null

  try {
    const data = await accountStore.fetchAccountById(accountId)

    // Cập nhật thông tin tài khoản
    accountInfo.id = data.id
    accountInfo.username = data.username

    if (data.role) {
      accountInfo.role.id = data.role.id
      accountInfo.role.name = data.role.name
      accountInfo.role.description = data.role.description
    }

    // Cập nhật tên đăng nhập trong profile
    profile.username = data.username
  } catch (error) {
    accountError.value = 'Lỗi khi tải thông tin tài khoản: ' + (error.message || 'Đã xảy ra lỗi')
    console.error('Lỗi khi tải thông tin tài khoản:', error)
  } finally {
    accountLoading.value = false
  }
}

// Lấy thông tin profile
const fetchProfile = async () => {
  try {
    const data = await managerStore.fetchManagerProfile()

    // Cập nhật thông tin profile
    profile.firstName = data.firstName || ''
    profile.lastName = data.lastName || ''
    profile.email = data.email || ''
    profile.phone = data.phone || ''
    profile.gender = data.gender || 'MALE'
    profile.accountId = data.accountId

    // Lấy thông tin tài khoản nếu có accountId
    if (data.accountId) {
      await fetchAccountInfo(data.accountId)
    }
  } catch (error) {
    console.error('Lỗi khi tải thông tin hồ sơ:', error)
  }
}

// Cập nhật thông tin profile
const updateProfile = async () => {
  if (!form.value.validate()) return

  updating.value = true
  successMessage.value = ''

  try {
    const updateData = {
      firstName: profile.firstName,
      lastName: profile.lastName,
      email: profile.email,
      phone: profile.phone,
      gender: profile.gender
    }

    await managerStore.updateManagerProfile(updateData)
    successMessage.value = 'Cập nhật thông tin thành công'
  } catch (error) {
    console.error('Lỗi khi cập nhật thông tin hồ sơ:', error)
  } finally {
    updating.value = false
  }
}

// Đóng dialog đổi username
const closeUsernameDialog = () => {
  usernameDialog.value = false
  usernameError.value = ''
  newUsername.value = ''
  passwordConfirm.value = ''
}

// Cập nhật username
const updateUsername = async () => {
  if (!usernameForm.value?.validate()) return

  updatingUsername.value = true
  usernameError.value = ''

  try {
    // Thực hiện cập nhật username
    await accountStore.updateAccount(accountInfo.id, {
      username: newUsername.value,
    })

    // Cập nhật lại thông tin
    accountInfo.username = newUsername.value
    profile.username = newUsername.value

    // Hiển thị thông báo thành công
    accountSuccessMessage.value = 'Thay đổi tên đăng nhập thành công'

    // Đóng dialog
    closeUsernameDialog()
  } catch (error) {
    usernameError.value = error.response?.data || 'Đã xảy ra lỗi khi thay đổi tên đăng nhập'
    console.error('Lỗi khi cập nhật tên đăng nhập:', error)
  } finally {
    updatingUsername.value = false
  }
}

// Đóng dialog đổi mật khẩu
const closePasswordDialog = () => {
  passwordDialog.value = false
  passwordError.value = ''
  currentPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
}

// Cập nhật mật khẩu
const updatePassword = async () => {
  if (!passwordForm.value?.validate()) return

  updatingPassword.value = true
  passwordError.value = ''

  try {
    // Thực hiện cập nhật mật khẩu
    await accountStore.updatePassword(accountInfo.id, {
      oldPassword: currentPassword.value,
      newPassword: newPassword.value,
      confirmPassword: confirmPassword.value,
    })

    // Hiển thị thông báo thành công
    accountSuccessMessage.value = 'Thay đổi mật khẩu thành công'

    // Đóng dialog
    closePasswordDialog()
  } catch (error) {
    passwordError.value = error.response?.data?.message || 'Đã xảy ra lỗi khi thay đổi mật khẩu'
    console.error('Lỗi khi cập nhật mật khẩu:', error)
  } finally {
    updatingPassword.value = false
  }
}

// Khởi tạo
onMounted(() => {
  fetchProfile()
})
</script>
