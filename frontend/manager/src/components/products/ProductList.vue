<template>
  <div class="p-6 ml-56">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold mb-6 flex items-center">
        <CubeIcon class="w-6 h-6 mr-2 text-orange-500" />Products List
      </h1>
      <button
        @click="navigateToProductDetail()"
        class="bg-primary bg-orange-500 text-white px-4 py-2 rounded-md flex items-center gap-2"
      >
        <PlusIcon class="w-5 h-5" />
        Add Product
      </button>
    </div>

    <!-- Filters -->
    <div class="mb-6 grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="relative">
        <SearchIcon
          class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5"
        />
        <input
          v-model="filters.search"
          type="text"
          placeholder="Search products..."
          class="w-full pl-10 pr-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
        />
      </div>
      <select
        v-model="filters.category"
        class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
      >
        <option value="">All Categories</option>
        <option v-for="category in categories" :key="category.id" :value="category.id">
          {{ category.name }}
        </option>
      </select>
      <select
        v-model="filters.status"
        class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
      >
        <option value="">All Status</option>
        <option value="active">Active</option>
        <option value="inactive">Inactive</option>
      </select>
    </div>

    <!-- Products Table -->
    <div class="bg-white rounded-md shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-100">
          <tr>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Image</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Name</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Category</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Base Price</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Status</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="product in filteredProducts" :key="product.id">
            <td class="px-6 py-4 whitespace-nowrap">
              <img :src="product.image" alt="Product" class="h-12 w-12 rounded-md object-cover" />
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              {{ product.name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              {{ getCategoryName(product.categoryId) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              {{ formatPrice(product.basePrice) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                :class="[
                  'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                  product.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                ]"
              >
                {{ product.active ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <div class="flex space-x-2">
                <button
                  @click="navigateToProductDetail(product.id)"
                  class="text-indigo-600 hover:text-indigo-900"
                >
                  <PencilIcon class="w-5 h-5" />
                </button>
                <button @click="confirmDelete(product)" class="text-red-600 hover:text-red-900">
                  <TrashIcon class="w-5 h-5" />
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredProducts.length === 0">
            <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">
              No products found
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-6 w-full max-w-md">
        <h2 class="text-xl font-bold mb-4">Confirm Delete</h2>
        <p class="mb-6">
          Are you sure you want to delete the product "{{ productToDelete?.name }}"? This action
          cannot be undone.
        </p>
        <div class="flex justify-end space-x-3">
          <button
            @click="showDeleteModal = false"
            class="px-4 py-2 border rounded-md text-gray-700 hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            @click="deleteProduct"
            class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, computed } from 'vue'
import { PlusIcon, PencilIcon, TrashIcon, SearchIcon } from 'lucide-vue-next'
import { CubeIcon } from '@heroicons/vue/24/outline'
import router from '@/router'
// const router = useRouter() // Giữ lại
// const route = useRoute()

// Sample data - replace with actual API calls
const categories = ref([
  { id: 1, name: 'Milk Tea' },
  { id: 2, name: 'Fruit Tea' },
  { id: 3, name: 'Coffee' },
  { id: 4, name: 'Smoothies' },
  { id: 5, name: 'Seasonal' },
])

const products = ref([
  {
    id: 1,
    name: 'Classic Milk Tea',
    categoryId: 1,
    basePrice: 3.99,
    image: 'https://placehold.co/100x100/png',
    active: true,
  },
  {
    id: 2,
    name: 'Taro Milk Tea',
    categoryId: 1,
    basePrice: 4.5,
    image: 'https://placehold.co/100x100/png',
    active: true,
  },
  {
    id: 3,
    name: 'Strawberry Fruit Tea',
    categoryId: 2,
    basePrice: 4.25,
    image: 'https://placehold.co/100x100/png',
    active: true,
  },
  {
    id: 4,
    name: 'Mango Smoothie',
    categoryId: 4,
    basePrice: 5.5,
    image: 'https://placehold.co/100x100/png',
    active: true,
  },
  {
    id: 5,
    name: 'Pumpkin Spice Latte',
    categoryId: 5,
    basePrice: 5.99,
    image: 'https://placehold.co/100x100/png',
    active: false,
  },
])

const filters = ref({
  search: '',
  category: '',
  status: '',
})

const showDeleteModal = ref(false)
const productToDelete = ref(null)

const filteredProducts = computed(() => {
  return products.value.filter((product) => {
    // Filter by search query
    if (
      filters.value.search &&
      !product.name.toLowerCase().includes(filters.value.search.toLowerCase())
    ) {
      return false
    }

    // Filter by category
    if (filters.value.category && product.categoryId !== parseInt(filters.value.category)) {
      return false
    }

    // Filter by status
    if (filters.value.status === 'active' && !product.active) {
      return false
    }

    if (filters.value.status === 'inactive' && product.active) {
      return false
    }

    return true
  })
})

function getCategoryName(categoryId) {
  const category = categories.value.find((c) => c.id === categoryId)
  return category ? category.name : 'Unknown'
}

function formatPrice(price) {
  return `$${price.toFixed(2)}`
}

function navigateToProductDetail(productId = null) {
  // // In a real app, you would use router.push
  // console.log(`Navigate to product detail: ${productId || 'new'}`)
  // // router.push({ name: 'ProductDetail', params: { id: productId || 'new' } })
  if (productId) {
    // Navigate to edit existing product
    router.push(`/products/${productId}`)
  } else {
    // Navigate to add new product
    router.push('/products/new')
  }
}

function confirmDelete(product) {
  productToDelete.value = product
  showDeleteModal.value = true
}

function deleteProduct() {
  if (productToDelete.value) {
    products.value = products.value.filter((p) => p.id !== productToDelete.value.id)
    showDeleteModal.value = false
    productToDelete.value = null
  }
}
</script>
