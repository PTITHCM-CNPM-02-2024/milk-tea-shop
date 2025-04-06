<template>
  <div class="p-6 ml-56">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-800">Quản lý hạng thành viên</h1>
      <button class="bg-primary text-white px-4 py-2 rounded-lg flex items-center gap-2">
        <PlusIcon class="w-5 h-5" />
        Thêm hạng thành viên mới
      </button>
    </div>

    <!-- Membership tiers -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
      <div
        v-for="tier in membershipTiers"
        :key="tier.id"
        class="bg-white rounded-lg shadow overflow-hidden"
      >
        <div class="p-6 border-b border-gray-200">
          <div class="flex items-center mb-4">
            <div
              :class="getMembershipIconClass(tier.level)"
              class="h-12 w-12 rounded-full flex items-center justify-center mr-4"
            >
              <CrownIcon class="h-6 w-6" />
            </div>
            <div>
              <h3 class="text-lg font-bold">Hạng {{ tier.name }}</h3>
              <p class="text-sm text-gray-500">{{ tier.pointsRequired }} điểm</p>
            </div>
          </div>
          <div class="space-y-4">
            <div>
              <p class="text-sm font-medium mb-2">Quyền lợi:</p>
              <ul class="text-sm text-gray-600 space-y-2">
                <li v-for="(benefit, index) in tier.benefits" :key="index" class="flex items-start">
                  <CheckIcon class="h-5 w-5 text-green-500 mr-2 flex-shrink-0" />
                  <span>{{ benefit }}</span>
                </li>
              </ul>
            </div>
            <div>
              <p class="text-sm font-medium mb-2">Thành viên hiện tại:</p>
              <p class="text-2xl font-bold">{{ tier.memberCount }}</p>
            </div>
          </div>
        </div>
        <div class="px-6 py-4 bg-gray-50 flex justify-end">
          <button @click="editTier(tier.id)" class="text-primary hover:text-primary-dark mr-2">
            <EditIcon class="w-5 h-5" />
          </button>
          <button
            v-if="tier.level !== 'bronze'"
            @click="deleteTier(tier.id)"
            class="text-red-600 hover:text-red-900"
          >
            <TrashIcon class="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>

    <!-- Membership settings -->
    <div class="bg-white rounded-lg shadow overflow-hidden mb-6">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-medium text-gray-800">Cài đặt tích điểm</h2>
      </div>
      <div class="p-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Tỷ lệ tích điểm</label>
            <div class="flex items-center">
              <input
                type="number"
                v-model="pointsSettings.pointsPerAmount"
                class="w-20 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary mr-2"
              />
              <span class="text-sm text-gray-600">điểm cho mỗi</span>
              <input
                type="number"
                v-model="pointsSettings.amountPerPoint"
                class="w-24 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary mx-2"
              />
              <span class="text-sm text-gray-600">VNĐ chi tiêu</span>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Giá trị điểm khi đổi</label>
            <div class="flex items-center">
              <input
                type="number"
                v-model="pointsSettings.pointValue"
                class="w-24 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary mr-2"
              />
              <span class="text-sm text-gray-600">VNĐ cho mỗi điểm</span>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Điểm tối thiểu để đổi</label
            >
            <div class="flex items-center">
              <input
                type="number"
                v-model="pointsSettings.minPointsToRedeem"
                class="w-24 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary mr-2"
              />
              <span class="text-sm text-gray-600">điểm</span>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Thời hạn điểm</label>
            <div class="flex items-center">
              <input
                type="number"
                v-model="pointsSettings.pointsExpiry"
                class="w-24 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary mr-2"
              />
              <span class="text-sm text-gray-600">tháng kể từ ngày tích điểm</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end mt-6">
          <button @click="savePointsSettings" class="bg-primary text-white px-4 py-2 rounded-lg">
            Lưu cài đặt
          </button>
        </div>
      </div>
    </div>

    <!-- Special rewards -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
        <h2 class="text-lg font-medium text-gray-800">Phần thưởng đặc biệt</h2>
        <button
          @click="addSpecialReward"
          class="text-primary hover:text-primary-dark text-sm font-medium"
        >
          Thêm phần thưởng
        </button>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Tên phần thưởng
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Mô tả
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Điểm cần thiết
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Hạng thành viên
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Trạng thái
              </th>
              <th
                scope="col"
                class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="reward in specialRewards" :key="reward.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ reward.name }}</div>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm text-gray-900">{{ reward.description }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ reward.pointsRequired }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getMembershipClass(reward.membershipLevel)">
                  {{ getMembershipText(reward.membershipLevel) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(reward.status)">
                  {{ getStatusText(reward.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex justify-end gap-2">
                  <button
                    @click="editReward(reward.id)"
                    class="text-primary hover:text-primary-dark"
                  >
                    <EditIcon class="w-5 h-5" />
                  </button>
                  <button
                    @click="toggleRewardStatus(reward.id)"
                    :class="
                      reward.status === 'active'
                        ? 'text-red-600 hover:text-red-900'
                        : 'text-green-600 hover:text-green-900'
                    "
                  >
                    <component
                      :is="reward.status === 'active' ? XIcon : CheckIcon"
                      class="w-5 h-5"
                    />
                  </button>
                  <button @click="deleteReward(reward.id)" class="text-red-600 hover:text-red-900">
                    <TrashIcon class="w-5 h-5" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Edit Tier Modal -->
    <div
      v-if="showEditTierModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg shadow-lg p-6 w-full max-w-md">
        <h3 class="text-lg font-medium text-gray-900 mb-4">Chỉnh sửa hạng thành viên</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Tên hạng</label>
            <input
              type="text"
              v-model="editingTier.name"
              class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Điểm cần thiết</label>
            <input
              type="number"
              v-model="editingTier.pointsRequired"
              class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Quyền lợi</label>
            <div
              v-for="(benefit, index) in editingTier.benefits"
              :key="index"
              class="flex items-center mb-2"
            >
              <input
                type="text"
                v-model="editingTier.benefits[index]"
                class="flex-1 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary mr-2"
              />
              <button @click="removeBenefit(index)" class="text-red-600 hover:text-red-900">
                <XIcon class="w-5 h-5" />
              </button>
            </div>
            <button
              @click="addBenefit"
              class="text-primary hover:text-primary-dark text-sm font-medium"
            >
              + Thêm quyền lợi
            </button>
          </div>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button
            @click="showEditTierModal = false"
            class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
          >
            Hủy
          </button>
          <button
            @click="saveTier"
            class="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark"
          >
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { PlusIcon, CrownIcon, CheckIcon, EditIcon, TrashIcon, XIcon } from 'lucide-vue-next'

const membershipTiers = ref([])
const specialRewards = ref([])
const showEditTierModal = ref(false)
const editingTier = ref({})

const pointsSettings = ref({
  pointsPerAmount: 1,
  amountPerPoint: 10000,
  pointValue: 1000,
  minPointsToRedeem: 10,
  pointsExpiry: 12,
})

onMounted(() => {
  fetchMembershipTiers()
  fetchSpecialRewards()
})

const fetchMembershipTiers = () => {
  // Mô phỏng dữ liệu hạng thành viên
  membershipTiers.value = [
    {
      id: 1,
      name: 'Đồng',
      level: 'bronze',
      pointsRequired: 0,
      memberCount: 120,
      benefits: [
        'Tích 1 điểm cho mỗi 10,000đ chi tiêu',
        'Ưu đãi sinh nhật',
        'Thông báo khuyến mãi sớm',
      ],
    },
    {
      id: 2,
      name: 'Bạc',
      level: 'silver',
      pointsRequired: 100,
      memberCount: 85,
      benefits: [
        'Tích 1.2 điểm cho mỗi 10,000đ chi tiêu',
        'Ưu đãi sinh nhật',
        'Thông báo khuyến mãi sớm',
        'Giảm 5% cho đơn hàng trên 100,000đ',
      ],
    },
    {
      id: 3,
      name: 'Vàng',
      level: 'gold',
      pointsRequired: 300,
      memberCount: 42,
      benefits: [
        'Tích 1.5 điểm cho mỗi 10,000đ chi tiêu',
        'Ưu đãi sinh nhật đặc biệt',
        'Thông báo khuyến mãi sớm',
        'Giảm 10% cho đơn hàng trên 100,000đ',
        'Miễn phí topping cho 1 đồ uống/tháng',
      ],
    },
    {
      id: 4,
      name: 'Bạch kim',
      level: 'platinum',
      pointsRequired: 500,
      memberCount: 18,
      benefits: [
        'Tích 2 điểm cho mỗi 10,000đ chi tiêu',
        'Ưu đãi sinh nhật đặc biệt',
        'Thông báo khuyến mãi sớm',
        'Giảm 15% cho đơn hàng trên 100,000đ',
        'Miễn phí topping cho 2 đồ uống/tháng',
        'Ưu tiên phục vụ tại cửa hàng',
      ],
    },
  ]
}

const fetchSpecialRewards = () => {
  // Mô phỏng dữ liệu phần thưởng đặc biệt
  specialRewards.value = [
    {
      id: 1,
      name: 'Voucher giảm giá 50,000đ',
      description: 'Áp dụng cho đơn hàng từ 100,000đ',
      pointsRequired: 50,
      membershipLevel: 'bronze',
      status: 'active',
    },
    {
      id: 2,
      name: 'Đồ uống miễn phí size M',
      description: 'Áp dụng cho tất cả đồ uống trừ đồ uống đặc biệt',
      pointsRequired: 100,
      membershipLevel: 'silver',
      status: 'active',
    },
    {
      id: 3,
      name: 'Voucher giảm giá 150,000đ',
      description: 'Áp dụng cho đơn hàng từ 200,000đ',
      pointsRequired: 150,
      membershipLevel: 'gold',
      status: 'active',
    },
    {
      id: 4,
      name: 'Combo 2 đồ uống size L',
      description: 'Áp dụng cho tất cả đồ uống',
      pointsRequired: 200,
      membershipLevel: 'platinum',
      status: 'inactive',
    },
  ]
}

const getMembershipClass = (level) => {
  const classes = {
    bronze:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800',
    silver:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800',
    gold: 'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-300 text-yellow-800',
    platinum:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800',
  }
  return classes[level] || ''
}

const getMembershipIconClass = (level) => {
  const classes = {
    bronze: 'bg-yellow-100 text-yellow-800',
    silver: 'bg-gray-100 text-gray-800',
    gold: 'bg-yellow-300 text-yellow-800',
    platinum: 'bg-purple-100 text-purple-800',
  }
  return classes[level] || ''
}

const getMembershipText = (level) => {
  const levelText = {
    bronze: 'Đồng',
    silver: 'Bạc',
    gold: 'Vàng',
    platinum: 'Bạch kim',
  }
  return levelText[level] || level
}

const getStatusClass = (status) => {
  const classes = {
    active:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800',
    inactive:
      'px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800',
  }
  return classes[status] || ''
}

const getStatusText = (status) => {
  const statusText = {
    active: 'Đang hoạt động',
    inactive: 'Không hoạt động',
  }
  return statusText[status] || status
}

const editTier = (tierId) => {
  const tier = membershipTiers.value.find((t) => t.id === tierId)
  if (tier) {
    editingTier.value = JSON.parse(JSON.stringify(tier)) // Deep copy
    showEditTierModal.value = true
  }
}

const deleteTier = (tierId) => {
  if (confirm('Bạn có chắc chắn muốn xóa hạng thành viên này không?')) {
    membershipTiers.value = membershipTiers.value.filter((t) => t.id !== tierId)
  }
}

const addBenefit = () => {
  editingTier.value.benefits.push('')
}

const removeBenefit = (index) => {
  editingTier.value.benefits.splice(index, 1)
}

const saveTier = () => {
  // Lọc bỏ các quyền lợi trống
  editingTier.value.benefits = editingTier.value.benefits.filter((b) => b.trim() !== '')

  const index = membershipTiers.value.findIndex((t) => t.id === editingTier.value.id)
  if (index !== -1) {
    membershipTiers.value[index] = { ...editingTier.value }
  }

  showEditTierModal.value = false
}

const savePointsSettings = () => {
  // Lưu cài đặt tích điểm
  console.log('Saving points settings:', pointsSettings.value)
  alert('Đã lưu cài đặt tích điểm thành công!')
}

const addSpecialReward = () => {
  // Thêm phần thưởng đặc biệt mới
  console.log('Adding new special reward')
}

const editReward = (rewardId) => {
  // Chỉnh sửa phần thưởng
  console.log('Editing reward:', rewardId)
}

const toggleRewardStatus = (rewardId) => {
  const index = specialRewards.value.findIndex((r) => r.id === rewardId)
  if (index !== -1) {
    specialRewards.value[index].status =
      specialRewards.value[index].status === 'active' ? 'inactive' : 'active'
  }
}

const deleteReward = (rewardId) => {
  if (confirm('Bạn có chắc chắn muốn xóa phần thưởng này không?')) {
    specialRewards.value = specialRewards.value.filter((r) => r.id !== rewardId)
  }
}
</script>

<style scoped>
.bg-primary {
  background-color: #f97316;
}
.text-primary {
  color: #f97316;
}
.hover\:bg-primary-dark:hover {
  background-color: #f97316;
}
.hover\:text-primary-dark:hover {
  color: #f97316;
}
.border-primary {
  border-color: #f97316;
}
.focus\:ring-primary:focus {
  --tw-ring-color: #f97316;
}
</style>
