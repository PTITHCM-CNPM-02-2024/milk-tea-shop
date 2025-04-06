<template>
  <div class="p-6 ml-56">
    <div class="flex items-center mb-6">
      <button @click="goBack" class="mr-3 text-gray-600 hover:text-gray-900">
        <ArrowLeftIcon class="w-5 h-5" />
      </button>
      <h1 class="text-2xl font-bold">{{ isEditing ? 'Edit Product' : 'Add New Product' }}</h1>
    </div>

    <form @submit.prevent="saveProduct" class="space-y-8">
      <!-- Basic Information -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h2 class="text-lg font-semibold mb-4">Basic Information</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Product Name</label>
            <input
              v-model="productForm.name"
              type="text"
              required
              class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Category</label>
            <select
              v-model="productForm.categoryId"
              required
              class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              v-model="productForm.description"
              rows="4"
              class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
            ></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Base Price</label>
            <div class="relative">
              <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">$</span>
              <input
                v-model="productForm.basePrice"
                type="number"
                step="0.01"
                min="0"
                required
                class="w-full pl-8 pr-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <div class="flex items-center mt-2">
              <label class="inline-flex items-center">
                <input
                  v-model="productForm.active"
                  type="checkbox"
                  class="rounded text-primary focus:ring-primary h-4 w-4"
                />
                <span class="ml-2 text-sm text-gray-700">Active</span>
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- Product Image -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h2 class="text-lg font-semibold mb-4">Product Image</h2>
        <div class="flex flex-col items-center">
          <div class="mb-4 w-40 h-40 relative">
            <img
              :src="productForm.image || 'https://placehold.co/400x400/png'"
              alt="Product"
              class="w-full h-full object-cover rounded-lg border"
            />
            <button
              v-if="productForm.image"
              @click.prevent="productForm.image = ''"
              class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1"
            >
              <XIcon class="w-4 h-4" />
            </button>
          </div>
          <label class="cursor-pointer bg-gray-100 hover:bg-gray-200 px-4 py-2 rounded-md">
            <span>Upload Image</span>
            <input type="file" class="hidden" @change="handleImageUpload" accept="image/*" />
          </label>
        </div>
      </div>

      <!-- Size Options -->
      <div class="bg-white p-6 rounded-lg shadow">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-semibold">Size Options</h2>
          <button
            type="button"
            @click="addSize"
            class="text-primary hover:text-primary-dark flex items-center gap-1"
          >
            <PlusIcon class="w-4 h-4" />
            Add Size
          </button>
        </div>
        <div v-if="productForm.sizes.length === 0" class="text-center py-4 text-gray-500">
          No size options added yet
        </div>
        <div v-else class="space-y-4">
          <div
            v-for="(size, index) in productForm.sizes"
            :key="index"
            class="flex items-center gap-4 p-3 border rounded-md"
          >
            <div class="flex-1">
              <label class="block text-sm font-medium text-gray-700 mb-1">Size Name</label>
              <input
                v-model="size.name"
                type="text"
                required
                class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
            <div class="flex-1">
              <label class="block text-sm font-medium text-gray-700 mb-1">Additional Price</label>
              <div class="relative">
                <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500"
                  >$</span
                >
                <input
                  v-model="size.additionalPrice"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  class="w-full pl-8 pr-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>
            </div>
            <div class="flex items-end">
              <button
                type="button"
                @click="removeSize(index)"
                class="mb-1 text-red-500 hover:text-red-700"
              >
                <TrashIcon class="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Discount Options -->
      <div class="bg-white p-6 rounded-lg shadow">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-semibold">Discount</h2>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Discount Type</label>
            <select
              v-model="productForm.discount.type"
              class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option value="none">No Discount</option>
              <option value="percentage">Percentage</option>
              <option value="fixed">Fixed Amount</option>
            </select>
          </div>
          <div v-if="productForm.discount.type !== 'none'">
            <label class="block text-sm font-medium text-gray-700 mb-1">
              {{
                productForm.discount.type === 'percentage'
                  ? 'Discount Percentage'
                  : 'Discount Amount'
              }}
            </label>
            <div class="relative">
              <span
                v-if="productForm.discount.type === 'fixed'"
                class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500"
              >
                $
              </span>
              <span v-else class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500">
                %
              </span>
              <input
                v-model="productForm.discount.value"
                type="number"
                :min="0"
                :max="productForm.discount.type === 'percentage' ? 100 : null"
                :class="[
                  'w-full py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary',
                  productForm.discount.type === 'fixed' ? 'pl-8 pr-4' : 'pl-4 pr-8',
                ]"
              />
            </div>
          </div>
          <div v-if="productForm.discount.type !== 'none'" class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1">Discount Period</label>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-xs text-gray-500 mb-1">Start Date</label>
                <input
                  v-model="productForm.discount.startDate"
                  type="date"
                  class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">End Date</label>
                <input
                  v-model="productForm.discount.endDate"
                  type="date"
                  class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Form Actions -->
      <div class="flex justify-end space-x-3">
        <button
          type="button"
          @click="goBack"
          class="px-4 py-2 border rounded-md text-gray-700 hover:bg-gray-50"
        >
          Cancel
        </button>
        <button
          type="submit"
          class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark"
        >
          {{ isEditing ? 'Update Product' : 'Create Product' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowLeftIcon, PlusIcon, TrashIcon, XIcon } from 'lucide-vue-next'

// Sample data - replace with actual API calls
const categories = ref([
  { id: 1, name: 'Milk Tea' },
  { id: 2, name: 'Fruit Tea' },
  { id: 3, name: 'Coffee' },
  { id: 4, name: 'Smoothies' },
  { id: 5, name: 'Seasonal' },
])

const productId = ref(null) // In a real app, get this from route params
const isEditing = computed(() => !!productId.value)

const productForm = ref({
  name: '',
  categoryId: 1,
  description: '',
  basePrice: 0,
  image: '',
  active: true,
  sizes: [],
  discount: {
    type: 'none',
    value: 0,
    startDate: '',
    endDate: '',
  },
})

onMounted(() => {
  // Simulate fetching product data if editing
  // In a real app, you would fetch from an API
  if (productId.value) {
    // Mock data for editing
    productForm.value = {
      name: 'Classic Milk Tea',
      categoryId: 1,
      description: 'Our signature milk tea with premium tea leaves and fresh milk.',
      basePrice: 3.99,
      image: 'https://placehold.co/400x400/png',
      active: true,
      sizes: [
        { name: 'Small', additionalPrice: 0 },
        { name: 'Medium', additionalPrice: 0.5 },
        { name: 'Large', additionalPrice: 1.0 },
      ],
      discount: {
        type: 'percentage',
        value: 10,
        startDate: '2023-04-01',
        endDate: '2023-04-30',
      },
    }
  }
})

function handleImageUpload(event) {
  const file = event.target.files[0]
  if (file) {
    // In a real app, you would upload the file to a server
    // For now, we'll just create a local URL
    productForm.value.image = URL.createObjectURL(file)
  }
}

function addSize() {
  productForm.value.sizes.push({
    name: '',
    additionalPrice: 0,
  })
}

function removeSize(index) {
  productForm.value.sizes.splice(index, 1)
}

function saveProduct() {
  // In a real app, you would send this to an API
  console.log('Saving product:', productForm.value)
  // After saving, navigate back to product list
  goBack()
}

function goBack() {
  // In a real app, you would use router.back() or router.push()
  console.log('Navigate back to product list')
}
</script>
