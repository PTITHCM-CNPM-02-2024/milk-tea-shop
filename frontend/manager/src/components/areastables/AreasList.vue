<script>
export default {
  data() {
    return {
      areas: [
        // Dữ liệu mẫu cho khu vực
        { id: 1, name: 'Area 1', description: 'Description of Area 1', capacity: 10, status: 'active' },
        { id: 2, name: 'Area 2', description: 'Description of Area 2', capacity: 15, status: 'inactive' },
        // Thêm các khu vực mẫu ở đây...
      ],
      newArea: {
        name: '',
        capacity: 0,
        description: '',
        status: 'active',
      },
      editingArea: null,
      showForm: false,
      searchQuery: '',
    };
  },
  computed: {
    // Bộ lọc khu vực theo tìm kiếm
    filteredAreas() {
      return this.areas.filter(area =>
        area.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    },
  },
  methods: {
    // Hàm thêm khu vực mới
    addArea() {
      const newArea = { ...this.newArea, id: Date.now() }; // Tạo ID duy nhất bằng timestamp
      this.areas.push(newArea); // Thêm khu vực vào danh sách
      this.resetForm(); // Đặt lại form sau khi thêm
    },

    // Hàm sửa khu vực
    updateArea() {
      const index = this.areas.findIndex(area => area.id === this.editingArea.id);
      if (index !== -1) {
        this.areas[index] = { ...this.newArea }; // Cập nhật khu vực với dữ liệu mới
      }
      this.resetForm(); // Đặt lại form sau khi sửa
    },

    // Hàm xóa khu vực
    deleteArea(id) {
      if (confirm('Are you sure you want to delete this area?')) {
        this.areas = this.areas.filter(area => area.id !== id); // Xóa khu vực khỏi danh sách
      }
    },

    // Hàm thay đổi trạng thái khu vực
    toggleStatus(area) {
      area.status = area.status === 'active' ? 'inactive' : 'active';
    },

    // Hàm bắt đầu chỉnh sửa khu vực
    startEdit(area) {
      this.editingArea = area;
      this.newArea = { ...area }; // Lấy dữ liệu khu vực để chỉnh sửa
      this.showForm = true; // Hiển thị form để sửa
    },

    // Hàm làm mới form
    resetForm() {
      this.newArea = { name: '', capacity: 0, description: '', status: 'active' };
      this.editingArea = null;
      this.showForm = false;
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
          <h1 class="text-2xl font-bold">Areas Management</h1>
          <button
            @click="showForm = true"
            class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
          >
            <PlusIcon class="w-5 h-5" />
            <span>Add New Area</span>
          </button>
        </div>

        <!-- Search Bar -->
        <div class="mb-6 relative">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search areas..."
              class="w-full pl-10 pr-4 py-2 rounded-lg border focus:outline-none focus:ring-2 focus:ring-orange-500"
              :class="darkMode ? 'bg-white border-gray-300' : 'bg-white border-gray-300'"
            />
            <MagnifyingGlassIcon class="absolute left-3 top-2.5 w-5 h-5 text-gray-400" />
          </div>
        </div>

        <!-- Form for adding/editing area -->
        <div v-if="showForm" class="mb-6 p-4 rounded-lg shadow-md bg-white">
          <h2 class="text-xl font-semibold mb-4">{{ editingArea ? 'Edit Area' : 'Add New Area' }}</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium mb-1">Area Name</label>
              <input
                v-model="newArea.name"
                type="text"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter area name"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Capacity</label>
              <input
                v-model.number="newArea.capacity"
                type="number"
                min="0"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter capacity"
              />
            </div>
            <div class="md:col-span-2">
              <label class="block text-sm font-medium mb-1">Description</label>
              <textarea
                v-model="newArea.description"
                rows="2"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter description"
              ></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Status</label>
              <select
                v-model="newArea.status"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              >
                <option value="active">Active</option>
                <option value="inactive">Inactive</option>
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
              @click="editingArea ? updateArea() : addArea()"
              class="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
            >
              {{ editingArea ? 'Update' : 'Save' }}
            </button>
          </div>
        </div>

        <!-- Areas List -->
        <div class="overflow-x-auto rounded-lg shadow">
          <table class="min-w-full divide-y bg-white divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Area Name
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Description
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Capacity
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
              <tr v-for="area in filteredAreas" :key="area.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="font-medium text-gray-900">{{ area.name }}</div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-gray-900">{{ area.description }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">{{ area.capacity }} seats</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                    :class="area.status === 'active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                  >
                    {{ area.status === 'active' ? 'Active' : 'Inactive' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div class="flex justify-end gap-2">
                    <button
                      @click="toggleStatus(area)"
                      class="p-1 rounded-full hover:bg-gray-200"
                      :title="area.status === 'active' ? 'Deactivate' : 'Activate'"
                    >
                      <CheckCircleIcon v-if="area.status === 'inactive'" class="w-5 h-5 text-green-500" />
                      <XCircleIcon v-else class="w-5 h-5 text-red-500" />
                    </button>
                    <button
                      @click="startEdit(area)"
                      class="p-1 rounded-full hover:bg-gray-200"
                      title="Edit"
                    >
                      <PencilIcon class="w-5 h-5 text-blue-500" />
                    </button>
                    <button
                      @click="deleteArea(area.id)"
                      class="p-1 rounded-full hover:bg-gray-200"
                      title="Delete"
                    >
                      <TrashIcon class="w-5 h-5 text-red-500" />
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredAreas.length === 0">
                <td colspan="5" class="px-6 py-4 text-center text-sm text-gray-500">
                  No areas found. Please add a new area or adjust your search.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
