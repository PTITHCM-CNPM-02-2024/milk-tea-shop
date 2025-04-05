import { ref, onMounted } from 'vue';
import MembershipService from '../services/membership.service';

export default function useMembership() {
  const memberships = ref([]);
  const loading = ref(false);
  const error = ref(null);

  // Tải danh sách memberships
  async function loadMemberships() {
    if (memberships.value.length > 0) return; // Đã tải rồi thì không tải lại
    
    loading.value = true;
    error.value = null;
    
    try {
      const response = await MembershipService.getMemberships();
      memberships.value = response.data || [];
      console.log('Đã tải danh sách membership:', memberships.value);
    } catch (err) {
      console.error('Lỗi khi tải danh sách membership:', err);
      error.value = 'Không thể tải thông tin thành viên. Vui lòng thử lại sau.';
    } finally {
      loading.value = false;
    }
  }

  // Tìm tên membership dựa vào ID
  function getMembershipName(membershipId) {
    if (!membershipId || !memberships.value.length) return 'Chưa xác định';
    
    const membership = memberships.value.find(m => m.id === membershipId);
    return membership ? membership.name : 'Chưa xác định';
  }

  // Lấy thông tin chi tiết về membership
  function getMembershipDetails(membershipId) {
    if (!membershipId || !memberships.value.length) return null;
    
    return memberships.value.find(m => m.id === membershipId) || null;
  }

  // Xác định màu cho membership
  function getMembershipColor(membershipId) {
    if (!membershipId) return 'grey';
    
    const membership = getMembershipDetails(membershipId);
    if (!membership) return 'grey';
    
    // Dựa vào tên để xác định màu
    const colorMap = {
      'BRONZE': 'brown',
      'SILVER': 'blue-grey',
      'GOLD': 'amber-darken-2',
      'PLATINUM': 'deep-purple',
      'DIAMOND': 'light-blue-darken-1'
    };
    
    return colorMap[membership.name] || 'primary';
  }

  // Định dạng hiển thị giảm giá
  function formatMembershipDiscount(membership) {
    if (!membership || !membership.discountValue) return 'Không có ưu đãi';
    
    if (membership.discountUnit === 'PERCENT') {
      return `Giảm ${membership.discountValue}% cho mỗi đơn hàng`;
    } else {
      return `Giảm ${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(membership.discountValue)} cho mỗi đơn hàng`;
    }
  }

  // Tìm membership tiếp theo
  function getNextMembership(currentMembershipId, rewardPoints) {
    if (!memberships.value.length) return null;
    
    // Sắp xếp theo requiredPoints tăng dần
    const sortedMemberships = [...memberships.value].sort((a, b) => a.requiredPoints - b.requiredPoints);
    
    // Tìm membership hiện tại trong danh sách đã sắp xếp
    const currentIndex = sortedMemberships.findIndex(m => m.id === currentMembershipId);
    
    // Nếu là membership cao nhất rồi thì không có cấp tiếp theo
    if (currentIndex === sortedMemberships.length - 1) return null;
    
    // Trả về membership tiếp theo
    return sortedMemberships[currentIndex + 1];
  }

  // Tính số điểm cần để lên cấp tiếp theo
  function getPointsToNextLevel(currentMembershipId, rewardPoints) {
    const nextMembership = getNextMembership(currentMembershipId, rewardPoints);
    if (!nextMembership) return 0;
    
    return Math.max(0, nextMembership.requiredPoints - rewardPoints);
  }

  // Tự động tải membership khi sử dụng composable
  onMounted(() => {
    loadMemberships();
  });

  return {
    memberships,
    loading,
    error,
    loadMemberships,
    getMembershipName,
    getMembershipDetails,
    getMembershipColor,
    formatMembershipDiscount,
    getNextMembership,
    getPointsToNextLevel
  };
}