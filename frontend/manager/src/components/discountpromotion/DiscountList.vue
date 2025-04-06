<script>
export default {
  data() {
    return {
      searchQuery: "",
      statusFilter: "all",
      showForm: false,
      editingDiscount: null,
      newDiscount: {
        name: "",
        code: "",
        type: "percentage",
        value: 0,
        minPurchase: 0,
        maxDiscount: 0,
        startDate: "",
        endDate: "",
        status: "active",
        applicableProducts: ""
      },
      discounts: [] // Your discounts data will go here
    };
  },
  computed: {
    filteredDiscounts() {
      return this.discounts.filter(discount => {
        const matchesQuery = discount.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                              discount.code.toLowerCase().includes(this.searchQuery.toLowerCase());
        const matchesStatus = this.statusFilter === "all" || discount.status === this.statusFilter;
        return matchesQuery && matchesStatus;
      });
    }
  },
  methods: {
    initializeNewDiscount() {
      this.showForm = true;
      this.editingDiscount = null;
      this.newDiscount = {
        name: "",
        code: "",
        type: "percentage",
        value: 0,
        minPurchase: 0,
        maxDiscount: 0,
        startDate: "",
        endDate: "",
        status: "active",
        applicableProducts: ""
      };
    },
    startEdit(discount) {
      this.showForm = true;
      this.editingDiscount = discount;
      this.newDiscount = { ...discount };
    },
    resetForm() {
      this.showForm = false;
    },
    addDiscount() {
      this.discounts.push({ ...this.newDiscount, id: Date.now() });
      this.resetForm();
    },
    updateDiscount() {
      const index = this.discounts.findIndex(d => d.id === this.editingDiscount.id);
      if (index !== -1) {
        this.discounts[index] = { ...this.newDiscount };
      }
      this.resetForm();
    },
    deleteDiscount(id) {
      this.discounts = this.discounts.filter(discount => discount.id !== id);
    },
    toggleStatus(discount) {
      discount.status = discount.status === 'active' ? 'inactive' : 'active';
    },
    formatCurrency(value) {
      return new Intl.NumberFormat().format(value);
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    }
  }
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
          <h1 class="text-2xl font-bold">Discount Management</h1>
          <button
            @click="initializeNewDiscount"
            class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
          >
            <PlusIcon class="w-5 h-5" />
            <span>Add New Discount</span>
          </button>
        </div>

        <!-- Search and Filter Bar -->
        <div class="mb-6 grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search discounts..."
              class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500 bg-white"
            />
            <MagnifyingGlassIcon class="absolute left-3 top-2.5 w-5 h-5 text-gray-400" />
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

        <!-- Form for adding/editing discount -->
        <div v-if="showForm" class="mb-6 p-4 rounded-lg shadow-md bg-white">
          <h2 class="text-xl font-semibold mb-4">{{ editingDiscount ? 'Edit Discount' : 'Add New Discount' }}</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium mb-1">Discount Name</label>
              <input
                v-model="newDiscount.name"
                type="text"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter discount name"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Discount Code</label>
              <input
                v-model="newDiscount.code"
                type="text"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter discount code"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Discount Type</label>
              <select
                v-model="newDiscount.type"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              >
                <option value="percentage">Percentage (%)</option>
                <option value="fixed">Fixed Amount (VND)</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">
                {{ newDiscount.type === 'percentage' ? 'Discount Percentage (%)' : 'Discount Amount (VND)' }}
              </label>
              <input
                v-model.number="newDiscount.value"
                type="number"
                min="0"
                :max="newDiscount.type === 'percentage' ? 100 : null"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                :placeholder="newDiscount.type === 'percentage' ? 'Enter percentage' : 'Enter amount'"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Minimum Purchase (VND)</label>
              <input
                v-model.number="newDiscount.minPurchase"
                type="number"
                min="0"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter minimum purchase amount"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Maximum Discount (VND, 0 for unlimited)</label>
              <input
                v-model.number="newDiscount.maxDiscount"
                type="number"
                min="0"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter maximum discount amount"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Start Date</label>
              <input
                v-model="newDiscount.startDate"
                type="date"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">End Date</label>
              <input
                v-model="newDiscount.endDate"
                type="date"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Status</label>
              <select
                v-model="newDiscount.status"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              >
                <option value="active">Active</option>
                <option value="inactive">Inactive</option>
              </select>
            </div>
            <div class="md:col-span-2">
              <label class="block text-sm font-medium mb-1">Applicable Products</label>
              <input
                v-model="newDiscount.applicableProducts"
                type="text"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter applicable products (e.g., 'All products', 'Premium drinks only')"
              />
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
              @click="editingDiscount ? updateDiscount() : addDiscount()"
              class="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
            >
              {{ editingDiscount ? 'Update' : 'Save' }}
            </button>
          </div>
        </div>

        <!-- Discounts List -->
        <div class="overflow-x-auto rounded-lg shadow">
          <table class="min-w-full divide-y bg-white divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Discount
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Code
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Value
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Validity
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="discount in filteredDiscounts" :key="discount.id" class="hover:bg-gray-50">
                <td class="px-6 py-4">
                  <div class="font-medium text-gray-900">{{ discount.name }}</div>
                  <div class="text-sm text-gray-500">
                    Min: {{ formatCurrency(discount.minPurchase) }}
                    <span v-if="discount.maxDiscount > 0">
                      | Max: {{ formatCurrency(discount.maxDiscount) }}
                    </span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                    <TagIcon class="w-3.5 h-3.5 mr-1" />
                    {{ discount.code }}
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <PercentIcon v-if="discount.type === 'percentage'" class="w-4 h-4 mr-1 text-orange-500" />
                    <span>
                      {{ discount.type === 'percentage' ? `${discount.value}%` : formatCurrency(discount.value) }}
                    </span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <CalendarIcon class="w-4 h-4 mr-1 text-gray-500" />
                    <span class="text-sm">
                      {{ formatDate(discount.startDate) }} - {{ formatDate(discount.endDate) }}
                    </span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                    :class="discount.status === 'active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                  >
                    {{ discount.status === 'active' ? 'Active' : 'Inactive' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div class="flex justify-end gap-2">
                    <button
                      @click="toggleStatus(discount)"
                      class="p-1 rounded-full hover:bg-gray-200"
                      :title="discount.status === 'active' ? 'Deactivate' : 'Activate'"
                    >
                      <CheckCircleIcon v-if="discount.status === 'inactive'" class="w-5 h-5 text-green-500" />
                      <XCircleIcon v-else class="w-5 h-5 text-red-500" />
                    </button>
                    <button
                      @click="startEdit(discount)"
                      class="p-1 rounded-full hover:bg-gray-200"
                      title="Edit"
                    >
                      <PencilIcon class="w-5 h-5 text-blue-500" />
                    </button>
                    <button
                      @click="deleteDiscount(discount.id)"
                      class="p-1 rounded-full hover:bg-gray-200"
                      title="Delete"
                    >
                      <TrashIcon class="w-5 h-5 text-red-500" />
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredDiscounts.length === 0">
                <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">
                  No discounts found. Please add a new discount or adjust your search.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
