<template>
  <div class="p-6 ml-56 max-w-7xl mx-auto">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold mb-6 flex items-center">
        <CubeIcon class="w-6 h-6 mr-2 text-orange-500" />Product Categories
      </h1>
    </div>

    <!-- Search Bar -->
    <div class="flex flex-col sm:flex-row gap-2 mb-6">
      <div class="relative flex-1">
        <SearchIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search categories..."
          class="border p-2 rounded w-full pl-10 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
        />
      </div>
      <button
        @click="openCategoryModal()"
        class="bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded transition-colors flex items-center justify-center sm:justify-start"
      >
        <PlusIcon class="w-5 h-5 mr-2" />
        Add Category
      </button>
    </div>

    <!-- Categories Table -->
    <div class="overflow-x-auto bg-white rounded-lg shadow">
      <table class="w-full border-collapse">
        <thead class="bg-gray-100">
          <tr>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">ID</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Name</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Description</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Products</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Status</th>
            <th class="px-6 py-3 text-left text-gray-700 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="category in filteredCategories" :key="category.id">
            <td class="px-6 py-4 whitespace-nowrap">{{ category.id }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              {{ category.name }}
            </td>
            <td class="px-6 py-4 text-sm">{{ category.description }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              {{ category.productCount }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                :class="[
                  'px-2 inline-flex  leading-5 font-semibold rounded-full',
                  category.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                ]"
              >
                {{ category.active ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <div class="flex space-x-2">
                <button
                  @click="openCategoryModal(category)"
                  class="text-indigo-600 hover:text-indigo-900"
                >
                  <PencilIcon class="w-5 h-5" />
                </button>
                <button @click="confirmDelete(category)" class="text-red-600 hover:text-red-900">
                  <TrashIcon class="w-5 h-5" />
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredCategories.length === 0">
            <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">
              No categories found
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Category Modal -->
    <div
      v-if="showCategoryModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-6 w-full max-w-md">
        <h2 class="text-xl font-bold mb-4">
          {{ editingCategory ? 'Edit Category' : 'Add New Category' }}
        </h2>
        <form @submit.prevent="saveCategory">
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-1">Name</label>
            <input
              v-model="categoryForm.name"
              type="text"
              required
              class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              v-model="categoryForm.description"
              rows="3"
              class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
            ></textarea>
          </div>
          <div class="mb-4">
            <label class="flex items-center">
              <input
                v-model="categoryForm.active"
                type="checkbox"
                class="rounded text-primary focus:ring-primary h-4 w-4"
              />
              <span class="ml-2 text-sm text-gray-700">Active</span>
            </label>
          </div>
          <div class="flex justify-end space-x-3">
            <button
              type="button"
              @click="showCategoryModal = false"
              class="px-4 py-2 border rounded-md text-gray-700 hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              class="px-4 py-2 text-white bg-orange-600 rounded-md hover:bg-orange-700"
            >
              {{ editingCategory ? 'Update' : 'Save' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-6 w-full max-w-md">
        <h2 class="text-xl font-bold mb-4">Confirm Delete</h2>
        <p class="mb-6">
          Are you sure you want to delete the category "{{ categoryToDelete?.name }}"? This action
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
            @click="deleteCategory"
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
import { ref, computed } from 'vue'
import { PlusIcon, PencilIcon, TrashIcon, SearchIcon } from 'lucide-vue-next'
import { CubeIcon } from '@heroicons/vue/24/outline'

// Sample data - replace with actual API calls
const categories = ref([
  {
    id: 1,
    name: 'Milk Tea',
    description: 'Various milk tea flavors',
    productCount: 12,
    active: true,
  },
  { id: 2, name: 'Fruit Tea', description: 'Refreshing fruit teas', productCount: 8, active: true },
  { id: 3, name: 'Coffee', description: 'Coffee-based drinks', productCount: 6, active: true },
  {
    id: 4,
    name: 'Smoothies',
    description: 'Fruit smoothies and slushes',
    productCount: 10,
    active: true,
  },
  {
    id: 5,
    name: 'Seasonal',
    description: 'Limited time seasonal offerings',
    productCount: 4,
    active: false,
  },
])

const searchQuery = ref('')
const showCategoryModal = ref(false)
const showDeleteModal = ref(false)
const editingCategory = ref(null)
const categoryToDelete = ref(null)
const categoryForm = ref({
  name: '',
  description: '',
  active: true,
})

const filteredCategories = computed(() => {
  if (!searchQuery.value) return categories.value

  const query = searchQuery.value.toLowerCase()
  return categories.value.filter(
    (category) =>
      category.name.toLowerCase().includes(query) ||
      category.description.toLowerCase().includes(query),
  )
})

function openCategoryModal(category = null) {
  if (category) {
    editingCategory.value = category
    categoryForm.value = { ...category }
  } else {
    editingCategory.value = null
    categoryForm.value = {
      name: '',
      description: '',
      active: true,
    }
  }
  showCategoryModal.value = true
}

function saveCategory() {
  if (editingCategory.value) {
    // Update existing category
    const index = categories.value.findIndex((c) => c.id === editingCategory.value.id)
    if (index !== -1) {
      categories.value[index] = { ...categories.value[index], ...categoryForm.value }
    }
  } else {
    // Add new category
    const newId = Math.max(0, ...categories.value.map((c) => c.id)) + 1
    categories.value.push({
      id: newId,
      ...categoryForm.value,
      productCount: 0,
    })
  }

  showCategoryModal.value = false
}

function confirmDelete(category) {
  categoryToDelete.value = category
  showDeleteModal.value = true
}

function deleteCategory() {
  if (categoryToDelete.value) {
    categories.value = categories.value.filter((c) => c.id !== categoryToDelete.value.id)
    showDeleteModal.value = false
    categoryToDelete.value = null
  }
}
</script>
