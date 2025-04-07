<script setup lang="ts">
import { ref } from 'vue';

// Store Information State
const storeInfo = ref({
  name: "Milk Tea",
  description: "Một quán cà phê ấm cúng phục vụ cà phê hảo hạng và bánh ngọt ngon",
  logo: "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=300",
  address: "Đường Man Thiện, quận Thủ Đức, TPHCM",
  phone: "+84 1900 8189",
  email: "contact@thecoffeehouse.com",
  website: "www.thecoffeehouse.com",
  taxId: "0123456789",
  openingSince: "2024-01-01",
  currency: "VND",
  taxRate: 10
});

// Business Hours State
const businessHours = ref([
  { day: "Monday", open: true, openTime: "07:00", closeTime: "22:00" },
  { day: "Tuesday", open: true, openTime: "07:00", closeTime: "22:00" },
  { day: "Wednesday", open: true, openTime: "07:00", closeTime: "22:00" },
  { day: "Thursday", open: true, openTime: "07:00", closeTime: "22:00" },
  { day: "Friday", open: true, openTime: "07:00", closeTime: "23:00" },
  { day: "Saturday", open: true, openTime: "07:00", closeTime: "23:00" },
  { day: "Sunday", open: true, openTime: "08:00", closeTime: "22:00" }
]);

// Payment Methods State
const paymentMethods = ref([
  { id: 1, name: "Cash", enabled: true },
  { id: 2, name: "Credit Card", enabled: true },
  { id: 3, name: "Momo", enabled: true },
  { id: 4, name: "ZaloPay", enabled: true }
]);

// Store Settings State
const storeSettings = ref({
  enableOnlineOrders: true,
  enableReservations: true,
  enableTakeaway: true,
  enableDelivery: true,
  deliveryRadius: 5,
  deliveryFee: 15000,
  minOrderForFreeDelivery: 200000,
  enableLoyaltyProgram: true,
  pointsPerPurchase: 0.001,
  amountPerPoint: 100
});

// UI State
const isEditing = ref(false);
const activeTab = ref('general');
const isSaving = ref(false);
const showSuccessMessage = ref(false);

// Handlers
const toggleEditMode = () => {
  isEditing.value = !isEditing.value;
  if (!isEditing.value) {
    showSuccessMessage.value = false;
  }
};

const saveChanges = async () => {
  isSaving.value = true;
  // Simulate API call
  await new Promise(resolve => setTimeout(resolve, 1000));
  isSaving.value = false;
  isEditing.value = false;
  showSuccessMessage.value = true;
  setTimeout(() => showSuccessMessage.value = false, 3000);
};

const togglePaymentMethod = (id: number) => {
  const methodIndex = paymentMethods.value.findIndex(method => method.id === id);
  if (methodIndex !== -1) {
    paymentMethods.value[methodIndex].enabled = !paymentMethods.value[methodIndex].enabled;
  }
};

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount);
};
</script>

<template>
  <div class="flex-1 p-6 ml-16 md:ml-56">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-gray-900">Store Management</h1>
        <div class="flex gap-2">
          <button
            v-if="!isEditing"
            @click="toggleEditMode"
            class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
          >
            <Cog class="w-5 h-5" />
            <span>Edit Store</span>
          </button>
          <template v-else>
            <button
              @click="saveChanges"
              :disabled="isSaving"
              class="flex items-center gap-2 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors disabled:opacity-50"
            >
              <ArrowPathIcon v-if="isSaving" class="w-5 h-5 animate-spin" />
              <Check v-else class="w-5 h-5" />
              <span>{{ isSaving ? 'Saving...' : 'Save Changes' }}</span>
            </button>
            <button
              @click="toggleEditMode"
              class="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors"
            >
              <span>Cancel</span>
            </button>
          </template>
        </div>
      </div>

      <!-- Success Message -->
      <div
        v-if="showSuccessMessage"
        class="mb-6 p-4 bg-green-100 text-green-800 rounded-lg flex items-center"
      >
        <Check class="w-5 h-5 mr-2" />
        Store information has been updated successfully.
      </div>

      <!-- Tabs -->
      <div class="mb-6 border-b border-gray-200">
        <nav class="flex space-x-8">
          <button
            v-for="tab in ['general', 'hours', 'payment', 'settings']"
            :key="tab"
            @click="activeTab = tab"
            class="py-4 px-1 border-b-2 font-medium text-sm"
            :class="activeTab === tab
              ? 'border-orange-500 text-orange-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
          >
            {{ tab.charAt(0).toUpperCase() + tab.slice(1) }}
          </button>
        </nav>
      </div>

      <!-- Content based on active tab -->
      <div class="bg-white shadow rounded-lg p-6">
        <!-- General Tab -->
        <div v-if="activeTab === 'general'" class="space-y-6">
          <!-- Store Logo -->
          <div class="flex items-center space-x-6">
            <div class="w-32 h-32 bg-gray-200 rounded-lg overflow-hidden">
              <img
                :src="storeInfo.logo"
                alt="Store Logo"
                class="w-full h-full object-cover"
              />
            </div>
            <button
              v-if="isEditing"
              class="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-100"
            >
              <PhotoIcon class="w-5 h-5" />
              <span>Change Logo</span>
            </button>
          </div>

          <!-- Store Details -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Store Name
              </label>
              <input
                v-if="isEditing"
                v-model="storeInfo.name"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
              />
              <p v-else class="text-gray-900">{{ storeInfo.name }}</p>
            </div>

            <div class="col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Description
              </label>
              <textarea
                v-if="isEditing"
                v-model="storeInfo.description"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-md"
              />
              <p v-else class="text-gray-600">{{ storeInfo.description }}</p>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Address
              </label>
              <div class="flex items-center">
                <MapPin class="w-5 h-5 text-gray-400 mr-2" />
                <input
                  v-if="isEditing"
                  v-model="storeInfo.address"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md"
                />
                <span v-else class="text-gray-600">{{ storeInfo.address }}</span>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Phone
              </label>
              <div class="flex items-center">
                <Phone class="w-5 h-5 text-gray-400 mr-2" />
                <input
                  v-if="isEditing"
                  v-model="storeInfo.phone"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md"
                />
                <span v-else class="text-gray-600">{{ storeInfo.phone }}</span>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Email
              </label>
              <div class="flex items-center">
                <Mail class="w-5 h-5 text-gray-400 mr-2" />
                <input
                  v-if="isEditing"
                  v-model="storeInfo.email"
                  type="email"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md"
                />
                <span v-else class="text-gray-600">{{ storeInfo.email }}</span>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Website
              </label>
              <div class="flex items-center">
                <Globe class="w-5 h-5 text-gray-400 mr-2" />
                <input
                  v-if="isEditing"
                  v-model="storeInfo.website"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md"
                />
                <span v-else class="text-gray-600">{{ storeInfo.website }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Hours Tab -->
        <div v-if="activeTab === 'hours'" class="space-y-4">
          <div
            v-for="(day, index) in businessHours"
            :key="day.day"
            class="flex items-center justify-between py-2"
          >
            <span class="font-medium w-32">{{ day.day }}</span>
            <div class="flex items-center gap-4">
              <label class="relative inline-flex items-center cursor-pointer">
                <input
                  type="checkbox"
                  v-model="day.open"
                  class="sr-only peer"
                  :disabled="!isEditing"
                />
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-orange-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-orange-500"></div>
              </label>
              <div v-if="day.open" class="flex items-center gap-2">
                <template v-if="isEditing">
                  <input
                    type="time"
                    v-model="day.openTime"
                    class="px-2 py-1 border border-gray-300 rounded"
                  />
                  <span>to</span>
                  <input
                    type="time"
                    v-model="day.closeTime"
                    class="px-2 py-1 border border-gray-300 rounded"
                  />
                </template>
                <span v-else>
                  {{ day.openTime }} to {{ day.closeTime }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
