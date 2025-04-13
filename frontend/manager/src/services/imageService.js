import api from './api'

export const imageService = {
  // Upload hình ảnh lên Firebase
  uploadImage(file) {
    // Tạo FormData để gửi file
    const formData = new FormData()
    formData.append('file', file)
    
    return api.post('/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // Xóa hình ảnh từ Firebase
  deleteImage(fileName) {
    return api.post('/images/delete', null, {
      params: {
        fileName
      }
    })
  }
}

export default imageService 