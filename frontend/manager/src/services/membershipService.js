import api from './api'

export const membershipService = {
  // API cho chương trình thành viên
  getMembershipTypes(page = 0, size = 10) {
    return api.get('/memberships', {
      params: { page, size }
    })
  },

  getMembershipTypeById(id) {
    return api.get(`/memberships/${id}`)
  },

  createMembershipType(data) {
    const request = {
      name: data.name,
      description: data.description,
      discountUnit: data.discountUnit,
      discountValue: data.discountValue,
      requiredPoint: data.requiredPoint,
      validUntil: data.validUntil
    }
    return api.post('/memberships', request)
  },

  updateMembershipType(id, data) {
    const request = {
      name: data.name,
      description: data.description,
      discountUnit: data.discountUnit,
      discountValue: data.discountValue,
      requiredPoint: data.requiredPoint,
      validUntil: data.validUntil,
      active: data.isActive
    }
    return api.put(`/memberships/${id}`, request)
  },

  deleteMembershipType(id) {
    return api.delete(`/memberships/${id}`)
  }
} 