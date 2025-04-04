<script>
export default {
  data() {
    return {
      searchQuery: '',
      typeFilter: 'all',
      statusFilter: 'all',
      showForm: false,
      editingPromotion: null,
      newPromotion: {
        name: '',
        type: '',
        description: '',
        startDate: '',
        endDate: '',
        status: 'active',
        applicableProducts: '',
        conditions: '',
        discountType: 'product',
        image: '',
      },
      promotions: [
        {
          id: 1,
          name: '50% off on Electronics',
          type: 'discount',
          description: 'Get 50% off on all electronics.',
          startDate: '2025-04-01',
          endDate: '2025-04-30',
          status: 'active',
          applicableProducts: 'Smartphones, Laptops',
          conditions: 'Limited to 5 items per customer.',
          discountType: 'product',
          image: 'https://via.placeholder.com/150',
        },
        {
          id: 2,
          name: 'Buy 1 Get 1 Free',
          type: 'offer',
          description: 'Buy one item and get another free.',
          startDate: '2025-04-10',
          endDate: '2025-05-10',
          status: 'inactive',
          applicableProducts: 'T-Shirts, Shoes',
          conditions: 'Only valid on selected items.',
          discountType: 'category',
          image: 'https://via.placeholder.com/150',
        },
      ],
      promotionTypes: [
        { value: 'discount', label: 'Discount' },
        { value: 'offer', label: 'Offer' },
        { value: 'coupon', label: 'Coupon' },
      ],
    };
  },
  computed: {
    filteredPromotions() {
      return this.promotions.filter(promotion => {
        const matchesQuery = promotion.name.toLowerCase().includes(this.searchQuery.toLowerCase());
        const matchesType = this.typeFilter === 'all' || promotion.type === this.typeFilter;
        const matchesStatus =
          this.statusFilter === 'all' || promotion.status === this.statusFilter;
        return matchesQuery && matchesType && matchesStatus;
      });
    },
  },
  methods: {
    initializeNewPromotion() {
      this.showForm = true;
      this.newPromotion = {
        name: '',
        type: '',
        description: '',
        startDate: '',
        endDate: '',
        status: 'active',
        applicableProducts: '',
        conditions: '',
        discountType: 'product',
        image: '',
      };
      this.editingPromotion = null;
    },
    addPromotion() {
      if (!this.isValidPromotion(this.newPromotion)) {
        alert('Please fill in all required fields!');
        return;
      }
      const newId = this.promotions.length + 1;
      this.promotions.push({
        id: newId,
        ...this.newPromotion,
      });
      this.resetForm();
    },
    updatePromotion() {
      if (!this.isValidPromotion(this.newPromotion)) {
        alert('Please fill in all required fields!');
        return;
      }
      const index = this.promotions.findIndex(promotion => promotion.id === this.editingPromotion.id);
      if (index !== -1) {
        this.promotions[index] = {
          ...this.editingPromotion,
          ...this.newPromotion,
        };
      }
      this.resetForm();
    },
    startEdit(promotion) {
      this.showForm = true;
      this.editingPromotion = promotion;
      this.newPromotion = { ...promotion };
    },
    resetForm() {
      this.showForm = false;
      this.newPromotion = {
        name: '',
        type: '',
        description: '',
        startDate: '',
        endDate: '',
        status: 'active',
        applicableProducts: '',
        conditions: '',
        discountType: 'product',
        image: '',
      };
      this.editingPromotion = null;
    },
    deletePromotion(id) {
      this.promotions = this.promotions.filter(promotion => promotion.id !== id);
    },
    toggleStatus(promotion) {
      promotion.status = promotion.status === 'active' ? 'inactive' : 'active';
    },
    getPromotionTypeLabel(type) {
      const typeObj = this.promotionTypes.find(t => t.value === type);
      return typeObj ? typeObj.label : 'Unknown';
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
    isValidPromotion(promotion) {
      // Check if all required fields are filled out
      return (
        promotion.name &&
        promotion.type &&
        promotion.startDate &&
        promotion.endDate &&
        promotion.status &&
        promotion.applicableProducts
      );
    },
  },
};
</script>

<template>
  <div
    class="flex flex-col h-screen transition-all"
    :class="darkMode ? 'bg-[#dde2fa] text-black' : 'bg-[#f8f8f8] text-[#060c12]'"
  >
    <div class="flex-1 p-6 ml-16 md:ml-56">
      <div class="max-w-7xl mx-auto">
        <!-- Header -->
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-2xl font-bold">Promotion Management</h1>
          <button
            @click="initializeNewPromotion"
            class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
          >
            <PlusIcon class="w-5 h-5" />
            <span>Add New Promotion</span>
          </button>
        </div>

        <!-- Search and Filter Bar -->
        <div class="mb-6 grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search promotions..."
              class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500 bg-white"
            />
            <MagnifyingGlassIcon class="absolute left-3 top-2.5 w-5 h-5 text-gray-400" />
          </div>
          <div>
            <select
              v-model="typeFilter"
              class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500 bg-white"
            >
              <option value="all">All Types</option>
              <option v-for="type in promotionTypes" :key="type.value" :value="type.value">{{ type.label }}</option>
            </select>
          </div>
          <div>
            <select
              v-model="statusFilter"
              class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500 bg-white"
            >
              <option value="all">All Status</option>
              <option value="active">Active</option>
              <option value="inactive">Inactive</option>
            </select>
          </div>
        </div>

        <!-- Form for adding/editing promotion -->
        <div v-if="showForm" class="mb-6 p-4 rounded-lg shadow-md bg-white">
          <h2 class="text-xl font-semibold mb-4">{{ editingPromotion ? 'Edit Promotion' : 'Add New Promotion' }}</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium mb-1">Promotion Name</label>
              <input
                v-model="newPromotion.name"
                type="text"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter promotion name"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Promotion Type</label>
              <select
                v-model="newPromotion.type"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              >
                <option v-for="type in promotionTypes" :key="type.value" :value="type.value">{{ type.label }}</option>
              </select>
            </div>
            <div class="md:col-span-2">
              <label class="block text-sm font-medium mb-1">Description</label>
              <textarea
                v-model="newPromotion.description"
                rows="2"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter promotion description"
              ></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Start Date</label>
              <input
                v-model="newPromotion.startDate"
                type="date"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">End Date</label>
              <input
                v-model="newPromotion.endDate"
                type="date"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Status</label>
              <select
                v-model="newPromotion.status"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              >
                <option value="active">Active</option>
                <option value="inactive">Inactive</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Applicable Products</label>
              <input
                v-model="newPromotion.applicableProducts"
                type="text"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter applicable products"
              />
            </div>
            <div class="md:col-span-2">
              <label class="block text-sm font-medium mb-1">Conditions</label>
              <textarea
                v-model="newPromotion.conditions"
                rows="2"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter promotion conditions"
              ></textarea>
            </div>
            <div class="md:col-span-2">
              <label class="block text-sm font-medium mb-1">Discount Type</label>
              <select
                v-model="newPromotion.discountType"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              >
                <option value="product">By Product</option>
                <option value="category">By Category</option>
                <option value="invoice">By Invoice Amount</option>
              </select>
            </div>
          </div>
          <div class="flex justify-end gap-2 mt-4">
            <button
              @click="resetForm"
              class="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors"
            >
              Cancel
            </button>
            <button
              @click="editingPromotion ? updatePromotion() : addPromotion()"
              class="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
            >
              {{ editingPromotion ? 'Update' : 'Save' }}
            </button>
          </div>
        </div>

        <!-- Promotions List -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div
            v-for="promotion in filteredPromotions"
            :key="promotion.id"
            class="bg-white rounded-lg shadow overflow-hidden"
          >
            <div class="h-32 bg-gray-200 flex items-center justify-center">
              <img :src="promotion.image" alt="Promotion" class="h-full w-full object-cover" />
            </div>
            <div class="p-4">
              <div class="flex justify-between items-start">
                <h3 class="text-lg font-semibold text-gray-900">{{ promotion.name }}</h3>
                <span
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                  :class="promotion.status === 'active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                >
                  {{ promotion.status === 'active' ? 'Active' : 'Inactive' }}
                </span>
              </div>
              <div class="mt-1 flex items-center text-sm text-gray-500">
                <GiftIcon class="w-4 h-4 mr-1" />
                <span>{{ getPromotionTypeLabel(promotion.type) }}</span>
              </div>
              <p class="mt-2 text-sm text-gray-600">{{ promotion.description }}</p>
              <div class="mt-2 flex items-center text-xs text-gray-500">
                <CalendarIcon class="w-3.5 h-3.5 mr-1" />
                <span>{{ formatDate(promotion.startDate) }} - {{ formatDate(promotion.endDate) }}</span>
              </div>
              <div class="mt-1 flex items-center text-xs text-gray-500">
                <ShoppingBagIcon class="w-3.5 h-3.5 mr-1" />
                <span>{{ promotion.applicableProducts }}</span>
              </div>
              <div class="mt-1 flex items-center text-xs text-gray-500">
                <span>Discount Type: {{ promotion.discountType === 'product' ? 'By Product' : promotion.discountType === 'category' ? 'By Category' : 'By Invoice Amount' }}</span>
              </div>
              <div class="mt-3 flex justify-end gap-2">
                <button
                  @click="toggleStatus(promotion)"
                  class="p-1 rounded-full hover:bg-gray-200"
                  :title="promotion.status === 'active' ? 'Deactivate' : 'Activate'"
                >
                  <CheckCircleIcon v-if="promotion.status === 'inactive'" class="w-5 h-5 text-green-500" />
                  <XCircleIcon v-else class="w-5 h-5 text-red-500" />
                </button>
                <button
                  @click="startEdit(promotion)"
                  class="p-1 rounded-full hover:bg-gray-200"
                  title="Edit"
                >
                  <PencilIcon class="w-5 h-5 text-blue-500" />
                </button>
                <button
                  @click="deletePromotion(promotion.id)"
                  class="p-1 rounded-full hover:bg-gray-200"
                  title="Delete"
                >
                  <TrashIcon class="w-5 h-5 text-red-500" />
                </button>
              </div>
            </div>
          </div>

          <!-- Empty state -->
          <div v-if="filteredPromotions.length === 0" class="md:col-span-2 lg:col-span-3 p-8 text-center">
            <div class="mx-auto w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
              <GiftIcon class="w-8 h-8 text-gray-400" />
            </div>
            <h3 class="text-lg font-medium text-gray-900">No promotions found</h3>
            <p class="mt-1 text-sm text-gray-500">
              Please add a new promotion or adjust your search filters.
            </p>
            <button
              @click="initializeNewPromotion"
              class="mt-4 inline-flex items-center px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
            >
              <PlusIcon class="w-5 h-5 mr-2" />
              Add New Promotion
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
