<template>
  <v-card class="customer-search-modal">
    <v-toolbar color="primary" density="compact">
      <v-toolbar-title>Tìm kiếm khách hàng</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon @click="$emit('cancel')">
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </v-toolbar>

    <v-card-text class="py-4">
      <!-- Tìm kiếm khách hàng -->
      <v-text-field
          v-model="phoneNumber"
          label="Số điện thoại khách hàng"
          prepend-inner-icon="mdi-phone"
          variant="outlined"
          clearable
          hide-details
          @keyup.enter="searchCustomer"
      >
        <template v-slot:append>
          <v-btn
              color="primary"
              variant="tonal"
              icon="mdi-magnify"
              @click="searchCustomer"
              :loading="searching"
          ></v-btn>
        </template>
      </v-text-field>

      <!-- Hiển thị trạng thái tìm kiếm -->
      <div v-if="searching" class="d-flex flex-column align-center py-8">
        <v-progress-circular indeterminate color="primary" size="50"></v-progress-circular>
        <span class="text-body-2 mt-4">Đang tìm kiếm khách hàng...</span>
      </div>

      <!-- Không tìm thấy khách hàng -->
      <div v-else-if="searchPerformed && !customer" class="d-flex flex-column align-center py-8">
        <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-account-question</v-icon>
        <span class="text-body-1 text-medium-emphasis mb-2">Không tìm thấy khách hàng</span>
        <span class="text-body-2 text-grey mb-4">Không tìm thấy khách hàng với số điện thoại này</span>
        <v-btn
            color="primary"
            prepend-icon="mdi-account-plus"
            variant="elevated"
            @click="openNewCustomerForm"
        >
          Thêm khách hàng mới
        </v-btn>
      </div>

      <!-- Hiển thị thông tin khách hàng tìm thấy -->
      <v-card
          v-else-if="customer"
          variant="outlined"
          class="mt-4 customer-card"
      >
        <v-card-item>
          <template v-slot:prepend>
            <v-avatar color="primary" variant="tonal" size="56">
              <v-icon size="large" color="primary">mdi-account</v-icon>
            </v-avatar>
          </template>

          <v-card-title>{{ customer.firstName }} {{ customer.lastName }}</v-card-title>
          <v-card-subtitle>
            <v-icon size="small" class="me-1">mdi-phone</v-icon>
            {{ customer.phone }}
          </v-card-subtitle>

          <template v-slot:append>
            <v-chip
                :color="getMembershipColor(customer.membership)"
                :text="customer.membership ? customer.membership.name : 'Chưa là thành viên'"
                size="small"
                class="font-weight-medium"
            ></v-chip>
          </template>
        </v-card-item>

        <v-card-text>
          <v-list density="compact" class="pa-0">
            <v-list-item v-if="customer.email">
              <template v-slot:prepend>
                <v-icon size="small" color="grey">mdi-email</v-icon>
              </template>
              <v-list-item-title class="text-body-2">{{ customer.email }}</v-list-item-title>
            </v-list-item>

            <v-list-item v-if="customer.points">
              <template v-slot:prepend>
                <v-icon size="small" color="amber-darken-2">mdi-star</v-icon>
              </template>
              <v-list-item-title class="text-body-2">
                {{ customer.points }} điểm tích lũy
              </v-list-item-title>
            </v-list-item>

            <v-list-item v-if="customer.membership && customer.membership.discountValue">
              <template v-slot:prepend>
                <v-icon size="small" color="green">mdi-sale</v-icon>
              </template>
              <v-list-item-title class="text-body-2">
                {{ formatMembershipDiscount(customer.membership) }}
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
              color="primary"
              variant="tonal"
              @click="selectCustomer"
          >
            Chọn khách hàng này
          </v-btn>
        </v-card-actions>
      </v-card>

      <!-- Form tạo khách hàng mới -->
      <v-expand-transition>
        <div v-if="showNewCustomerForm">
          <v-divider class="my-4"></v-divider>

          <v-card variant="outlined" class="pa-4 new-customer-form">
            <v-card-title class="px-0 pt-0 text-h6">Thông tin khách hàng mới</v-card-title>

            <v-form ref="form" v-model="validForm" @submit.prevent="createNewCustomer">
              <v-text-field
                  v-model="newCustomer.phone"
                  label="Số điện thoại"
                  readonly
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-phone"
                  class="mb-3"
              ></v-text-field>

              <div class="d-flex gap-3 mb-3">
                <v-text-field
                    v-model="newCustomer.firstName"
                    label="Họ"
                    variant="outlined"
                    density="comfortable"
                    :rules="[v => !!v || 'Vui lòng nhập họ']"
                ></v-text-field>

                <v-text-field
                    v-model="newCustomer.lastName"
                    label="Tên"
                    variant="outlined"
                    density="comfortable"
                    :rules="[v => !!v || 'Vui lòng nhập tên']"
                ></v-text-field>
              </div>

              <v-text-field
                  v-model="newCustomer.email"
                  label="Email (không bắt buộc)"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-email"
                  class="mb-3"
                  :rules="[
                  v => !v || /.+@.+\..+/.test(v) || 'Email không hợp lệ'
                ]"
              ></v-text-field>

              <v-select
                  v-model="newCustomer.gender"
                  label="Giới tính"
                  :items="genderOptions"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-gender-male-female"
                  class="mb-3"
              ></v-select>

              <v-card-actions class="px-0 pt-3 pb-0">
                <v-spacer></v-spacer>
                <v-btn
                    variant="tonal"
                    @click="showNewCustomerForm = false"
                >
                  Hủy
                </v-btn>
                <v-btn
                    color="primary"
                    type="submit"
                    :loading="savingCustomer"
                    :disabled="!validForm"
                >
                  <v-icon left>mdi-account-plus</v-icon>
                  Lưu khách hàng
                </v-btn>
              </v-card-actions>
            </v-form>
          </v-card>
        </div>
      </v-expand-transition>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import CustomerService from '../services/customer.service';

const emit = defineEmits(['select-customer', 'cancel']);

const phoneNumber = ref('');
const customer = ref(null);
const searching = ref(false);
const searchPerformed = ref(false);
const showNewCustomerForm = ref(false);
const validForm = ref(false);
const savingCustomer = ref(false);

const form = ref(null);

const genderOptions = [
  { title: 'Nam', value: 'MALE' },
  { title: 'Nữ', value: 'FEMALE' },
  { title: 'Khác', value: 'OTHER' }
];

const newCustomer = ref({
  firstName: '',
  lastName: '',
  phone: '',
  email: '',
  gender: 'MALE'
});

async function searchCustomer() {
  if (!phoneNumber.value) {
    // Hiển thị thông báo lỗi với Vuetify
    return;
  }

  searching.value = true;
  searchPerformed.value = true;

  try {
    const response = await CustomerService.getCustomerByPhone(phoneNumber.value);
    customer.value = response.data;
  } catch (error) {
    console.error('Error searching customer:', error);
    customer.value = null;
  } finally {
    searching.value = false;
  }
}

function openNewCustomerForm() {
  newCustomer.value.phone = phoneNumber.value;
  showNewCustomerForm.value = true;
}

async function createNewCustomer() {
  if (!validForm.value) return;

  savingCustomer.value = true;

  try {
    const response = await CustomerService.createCustomer(newCustomer.value);
    customer.value = response.data;
    showNewCustomerForm.value = false;

    // Hiển thị thông báo thành công
  } catch (error) {
    console.error('Error creating customer:', error);
    // Hiển thị thông báo lỗi
  } finally {
    savingCustomer.value = false;
  }
}

function selectCustomer() {
  emit('select-customer', customer.value);
}

function getMembershipColor(membership) {
  if (!membership) return 'grey';

  const colorMap = {
    'Bronze': 'grey',
    'Silver': 'blue-grey',
    'Gold': 'amber-darken-2',
    'Platinum': 'red-darken-1',
    'Diamond': 'indigo'
  };

  return colorMap[membership.name] || 'grey';
}

function formatMembershipDiscount(membership) {
  if (!membership || !membership.discountValue) return 'Không có ưu đãi';

  if (membership.discountUnit === 'PERCENT') {
    return `Giảm ${membership.discountValue}% cho mỗi đơn hàng`;
  } else {
    return `Giảm ${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(membership.discountValue)} cho mỗi đơn hàng`;
  }
}
</script>

<style scoped>
.customer-card {
  transition: all 0.3s ease;
}

.customer-card:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.new-customer-form {
  border-color: var(--v-primary-lighten3);
  background-color: var(--v-surface-variant);
}
</style>