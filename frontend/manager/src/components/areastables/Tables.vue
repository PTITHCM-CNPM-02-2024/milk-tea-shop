<script>
export default {
  data() {
    return {
      tables: [
        // Dữ liệu bàn mẫu
        { id: 1, number: 'T1', areaId: 1, capacity: 4, status: 'available', shape: 'round' },
        { id: 2, number: 'T2', areaId: 1, capacity: 2, status: 'occupied', shape: 'square' },
        { id: 3, number: 'T3', areaId: 2, capacity: 6, status: 'reserved', shape: 'rectangle' },
        { id: 4, number: 'T4', areaId: 2, capacity: 4, status: 'maintenance', shape: 'round' },
        // Thêm các bàn khác ở đây...
      ],
      areas: [
        { id: 1, name: 'Area 1' },
        { id: 2, name: 'Area 2' },
        // Thêm các khu vực khác ở đây...
      ],
      selectedArea: null, // Mã khu vực được chọn
      searchQuery: '', // Dữ liệu tìm kiếm
      showForm: false, // Điều khiển hiển thị form
      newTable: {
        number: '',
        areaId: null,
        capacity: 1,
        status: 'available',
        shape: 'square',
      },
      editingTable: null,
    };
  },
  computed: {
    // Bộ lọc bàn theo khu vực và tìm kiếm
    filteredTables() {
      return this.tables.filter(table => {
        const matchesArea = this.selectedArea ? table.areaId === this.selectedArea : true;
        const matchesSearch = table.number.toLowerCase().includes(this.searchQuery.toLowerCase());
        return matchesArea && matchesSearch;
      });
    },
  },
  methods: {
    // Thêm bàn mới
    addTable() {
      const newTable = { ...this.newTable, id: Date.now() }; // Tạo ID duy nhất bằng timestamp
      this.tables.push(newTable);
      this.resetForm();
    },

    // Cập nhật bàn
    updateTable() {
      const index = this.tables.findIndex(table => table.id === this.editingTable.id);
      if (index !== -1) {
        this.tables[index] = { ...this.newTable }; // Cập nhật bàn
      }
      this.resetForm();
    },

    // Xóa bàn
    deleteTable(id) {
      if (confirm('Are you sure you want to delete this table?')) {
        this.tables = this.tables.filter(table => table.id !== id);
      }
    },

    // Thay đổi trạng thái bàn
    toggleStatus(table) {
      if (table.status === 'available') {
        table.status = 'occupied';
      } else if (table.status === 'occupied') {
        table.status = 'available';
      }
    },

    // Ghép bàn
    mergeTables(table1, table2) {
      const newCapacity = table1.capacity + table2.capacity;
      table1.capacity = newCapacity;
      table2.status = 'maintenance'; // Đánh dấu bàn đã được ghép
    },

    // Chỉnh sửa bàn
    startEdit(table) {
      this.editingTable = table;
      this.newTable = { ...table };
      this.showForm = true;
    },

    // Đặt lại form
    resetForm() {
      this.newTable = { number: '', areaId: null, capacity: 1, status: 'available', shape: 'square' };
      this.editingTable = null;
      this.showForm = false;
    },

    // Lấy tên khu vực từ id
    getAreaName(areaId) {
      const area = this.areas.find(area => area.id === areaId);
      return area ? area.name : 'Unknown Area';
    },

    // Lấy class trạng thái bàn
    getStatusClass(status) {
      if (status === 'available') return 'bg-green-100 text-green-800';
      if (status === 'occupied') return 'bg-red-100 text-red-800';
      if (status === 'reserved') return 'bg-yellow-100 text-yellow-800';
      return 'bg-gray-100 text-gray-800';
    },

    // Lấy icon hình dạng bàn
    getTableShape(shape) {
      if (shape === 'round') return 'CircleIcon';
      if (shape === 'square') return 'SquareIcon';
      return 'RectangleIcon';
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
          <h1 class="text-2xl font-bold">Tables Management</h1>
          <button
            @click="showForm = true"
            class="flex items-center gap-2 px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
          >
            <PlusIcon class="w-5 h-5" />
            <span>Add New Table</span>
          </button>
        </div>

        <!-- Search and Filter Bar -->
        <div class="mb-6 grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search tables..."
              class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500 bg-white"
            />
            <MagnifyingGlassIcon class="absolute left-3 top-2.5 w-5 h-5 text-gray-400" />
          </div>
          <div>
            <select
              v-model="selectedArea"
              class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500 bg-white"
            >
              <option :value="null">All Areas</option>
              <option v-for="area in areas" :key="area.id" :value="area.id">{{ area.name }}</option>
            </select>
          </div>
        </div>

        <!-- Form for adding/editing table -->
        <div v-if="showForm" class="mb-6 p-4 rounded-lg shadow-md bg-white">
          <h2 class="text-xl font-semibold mb-4">{{ editingTable ? 'Edit Table' : 'Add New Table' }}</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium mb-1">Table Number</label>
              <input
                v-model="newTable.number"
                type="text"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter table number"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Area</label>
              <select
                v-model="newTable.areaId"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              >
                <option :value="null" disabled>Select an area</option>
                <option v-for="area in areas" :key="area.id" :value="area.id">{{ area.name }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Capacity</label>
              <input
                v-model.number="newTable.capacity"
                type="number"
                min="1"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
                placeholder="Enter capacity"
              />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Status</label>
              <select
                v-model="newTable.status"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              >
                <option value="available">Available</option>
                <option value="occupied">Occupied</option>
                <option value="reserved">Reserved</option>
                <option value="maintenance">Maintenance</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Shape</label>
              <select
                v-model="newTable.shape"
                class="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-orange-500"
              >
                <option value="square">Square</option>
                <option value="round">Round</option>
                <option value="rectangle">Rectangle</option>
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
              @click="editingTable ? updateTable() : addTable()"
              class="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors"
            >
              {{ editingTable ? 'Update' : 'Save' }}
            </button>
          </div>
        </div>

        <!-- Tables List -->
        <div class="overflow-x-auto rounded-lg shadow">
          <table class="min-w-full divide-y bg-white divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Table
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Area
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Capacity
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Shape
                </th>
                <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="table in filteredTables" :key="table.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="font-medium text-gray-900">{{ table.number }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">{{ getAreaName(table.areaId) }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">{{ table.capacity }} persons</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                    :class="getStatusClass(table.status)"
                  >
                    {{ table.status.charAt(0).toUpperCase() + table.status.slice(1) }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <component :is="getTableShape(table.shape)" class="w-5 h-5 mr-1" />
                    <span class="capitalize">{{ table.shape }}</span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div class="flex justify-end gap-2">
                    <button
                      @click="startEdit(table)"
                      class="p-1 rounded-full hover:bg-gray-200"
                      title="Edit"
                    >
                      <PencilIcon class="w-5 h-5 text-blue-500" />
                    </button>
                    <button
                      @click="deleteTable(table.id)"
                      class="p-1 rounded-full hover:bg-gray-200"
                      title="Delete"
                    >
                      <TrashIcon class="w-5 h-5 text-red-500" />
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredTables.length === 0">
                <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">
                  No tables found. Please add a new table or adjust your search.
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Quick Status Change Menu -->
        <div class="mt-6 p-4 rounded-lg shadow-md bg-white">
          <h3 class="text-lg font-medium mb-3">Quick Status Change</h3>
          <p class="text-sm mb-3 text-gray-600">
            Select a table from the list above and change its status:
          </p>
          <div class="flex flex-wrap gap-2">
            <button
              @click="() => {}"
              class="px-3 py-1.5 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors"
              disabled
            >
              Set Available
            </button>
            <button
              @click="() => {}"
              class="px-3 py-1.5 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
              disabled
            >
              Set Occupied
            </button>
            <button
              @click="() => {}"
              class="px-3 py-1.5 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition-colors"
              disabled
            >
              Set Reserved
            </button>
            <button
              @click="() => {}"
              class="px-3 py-1.5 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
              disabled
            >
              Set Maintenance
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
