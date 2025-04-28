<template>
  <div>
    <!-- Phần header với tiêu đề và nút thêm mới -->
    <div class="d-flex justify-space-between align-center mb-4 px-4 pt-4">
      <div>
        <h2 class="text-h5 font-weight-bold">Sản phẩm và danh mục</h2>
        <p class="text-subtitle-2 text-medium-emphasis mt-1">
          Quản lý thông tin sản phẩm và danh mục trong hệ thống
        </p>
      </div>
    </div>
    
    <!-- Alert hiển thị lỗi nếu có -->
    <v-alert
      v-if="productStore.error"
      type="error"
      variant="tonal"
      closable
      class="mx-4 mb-4"
    >
      {{ productStore.error }}
    </v-alert>

    <!-- Tabs cho sản phẩm và danh mục -->
    <v-tabs v-model="activeTab" color="primary" align-tabs="start" class="px-4 mb-4">
      <v-tab value="products">
        <v-icon start>mdi-package-variant-closed</v-icon>
        Sản phẩm
      </v-tab>
      <v-tab value="categories">
        <v-icon start>mdi-shape-outline</v-icon>
        Danh mục
      </v-tab>
    </v-tabs>

    <v-window v-model="activeTab" class="px-4">
      <!-- Tab Sản phẩm -->
      <v-window-item value="products">
        <v-card>
          <v-card-title class="d-flex justify-space-between align-center py-3 px-4">
            <span class="text-h6">Danh sách sản phẩm</span>
            <v-btn 
              color="primary" 
              size="small"
              variant="text"
              prepend-icon="mdi-package-variant-plus" 
              @click="openAddDialog"
            >
              Thêm mới
            </v-btn>
          </v-card-title>
          
          <v-divider></v-divider>
          
          <v-card-text class="pa-2">
            <!-- Filter và Search -->
            <v-row class="px-2 py-2">
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="searchQuery"
                  label="Tìm kiếm"
                  density="compact"
                  variant="outlined"
                  hide-details
                  prepend-inner-icon="mdi-magnify"
                  clearable
                  @update:model-value="debounceSearch"
                ></v-text-field>
              </v-col>
              
              <v-col cols="12" md="3">
                <v-select
                  v-model="selectedCategory"
                  :items="categoryOptions"
                  label="Danh mục"
                  density="compact"
                  variant="outlined"
                  hide-details
                  clearable
                ></v-select>
              </v-col>
              
              <v-col cols="12" md="3">
                <v-select
                  v-model="selectedStatus"
                  :items="statusOptions"
                  label="Trạng thái"
                  density="compact"
                  variant="outlined"
                  hide-details
                  clearable
                ></v-select>
              </v-col>
              
              <v-col cols="12" md="2">
                <v-btn 
                  color="primary" 
                  variant="outlined" 
                  block
                  @click="resetFilters"
                  :disabled="!hasFilters"
                  density="compact"
                >
                  Đặt lại
                </v-btn>
              </v-col>
            </v-row>

            <!-- Grid hiển thị sản phẩm -->
            <v-row class="px-2">
              <v-col 
                v-for="product in paginatedProducts" 
                :key="product.id" 
                cols="12" 
                sm="6" 
                md="6" 
                lg="4"
              >
                <v-card class="h-100">
                  <v-img
                    :src="product.image_url || '/images/placeholder.png'"
                    cover
                    height="180"
                    class="align-end text-white"
                  >
                    <div v-if="product.signature" class="signature-badge">
                      <v-icon small>mdi-star</v-icon>
                      <span>Đặc trưng</span>
                    </div>
                    <div v-if="product.available === false" class="status-badge status-inactive">
                      <v-icon small>mdi-close-circle</v-icon>
                      <span>Ngừng bán</span>
                    </div>
                  </v-img>
                  
                  <v-card-title class="d-flex justify-space-between align-center py-2">
                    <span class="text-truncate">{{ product.name }}</span>
                    <v-chip
                      v-if="product.catId"
                      size="small"
                      label
                      color="primary"
                      variant="outlined"
                      class="ml-2"
                    >
                      {{ getCategoryName(product.catId) }}
                    </v-chip>
                  </v-card-title>
                  
                  <v-card-text class="pt-1 pb-2">
                    <p class="text-subtitle-2 text-medium-emphasis text-truncate mb-2">
                      {{ product.description || 'Không có mô tả' }}
                    </p>
                    
                    <div v-if="product.prices && product.prices.length > 0" class="d-flex flex-wrap gap-1 mt-2">
                      <v-chip
                        v-for="price in product.prices.slice(0, 3)"
                        :key="price.id"
                        size="x-small"
                        color="success"
                        variant="flat"
                        class="font-weight-medium"
                      >
                        {{ price.size }}: {{ formatCurrency(price.price) }}
                      </v-chip>
                      <v-chip
                        v-if="product.prices.length > 3"
                        size="x-small"
                        color="grey"
                        variant="flat"
                        class="font-weight-medium"
                      >
                        +{{ product.prices.length - 3 }} kích cỡ khác
                      </v-chip>
                    </div>
                    <div v-else class="text-caption text-medium-emphasis">
                      Chưa có thông tin giá
                    </div>
                  </v-card-text>
                  
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn 
                      icon="mdi-pencil" 
                      size="small" 
                      color="primary" 
                      variant="text" 
                      @click="openEditDialog(product)"
                    ></v-btn>
                    <v-btn 
                      icon="mdi-delete" 
                      size="small" 
                      color="error" 
                      variant="text" 
                      @click="openDeleteDialog(product)"
                    ></v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>

              <!-- Hiển thị khi không có dữ liệu -->
              <v-col 
                v-if="!productStore.loading && filteredProducts.length === 0" 
                cols="12"
                class="text-center py-6"
              >
                <v-icon icon="mdi-package-variant" size="x-large" class="mb-2 text-medium-emphasis"></v-icon>
                <div class="text-subtitle-1 text-medium-emphasis">Không có sản phẩm nào</div>
                <v-btn color="primary" size="small" class="mt-2" prepend-icon="mdi-package-variant-plus" @click="openAddDialog">
                  Thêm sản phẩm
                </v-btn>
              </v-col>

              <!-- Loading skeleton -->
              <template v-if="productStore.loading">
                <v-col v-for="i in 6" :key="i" cols="12" sm="6" md="6" lg="4">
                  <v-skeleton-loader type="card" height="300"></v-skeleton-loader>
                </v-col>
              </template>
            </v-row>

            <!-- Pagination cho sản phẩm -->
            <v-row v-if="filteredProducts.length > 0" class="py-4">
              <v-col cols="12" class="d-flex justify-center">
                <v-pagination
                  v-model="productPage"
                  :length="totalProductPages"
                  :total-visible="5"
                  @update:model-value="handleProductPageChange"
                  rounded
                ></v-pagination>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-window-item>
      
      <!-- Tab Danh mục -->
      <v-window-item value="categories">
        <v-card class="mb-4">
          <v-card-title class="d-flex justify-space-between align-center py-3 px-4">
            <span class="text-h6">Danh mục sản phẩm</span>
            <v-btn 
              color="primary" 
              size="small"
              variant="text"
              prepend-icon="mdi-shape-plus" 
              @click="openAddCategoryDialog"
            >
              Thêm mới
            </v-btn>
          </v-card-title>
          
          <v-divider></v-divider>
          
          <v-card-text class="pa-2">
            <v-list lines="two" class="py-0">
              <v-list-item
                v-for="category in productStore.categories"
                :key="category.id"
                :title="category.name"
                :subtitle="category.description || 'Không có mô tả'"
                :value="category.id"
                rounded="lg"
                class="mb-1"
                @click="selectCategory(category)"
              >
                <template v-slot:prepend>
                  <v-avatar color="primary" size="36">
                    <span class="text-h7 font-weight-medium">{{ category.name.charAt(0) }}</span>
                  </v-avatar>
                </template>
                
                <template v-slot:append>
                  <div class="d-flex gap-1">
                    <v-btn
                      icon="mdi-pencil"
                      size="x-small"
                      color="primary"
                      variant="text"
                      @click.stop="openEditCategoryDialog(category)"
                    ></v-btn>
                    <v-btn
                      icon="mdi-delete"
                      size="x-small"
                      color="error"
                      variant="text"
                      @click.stop="openDeleteCategoryDialog(category)"
                    ></v-btn>
                  </div>
                </template>
              </v-list-item>
              
              <div v-if="productStore.categories.length === 0 && !productStore.loading" class="text-center py-6">
                <v-icon icon="mdi-shape" size="x-large" class="mb-2 text-medium-emphasis"></v-icon>
                <div class="text-subtitle-1 text-medium-emphasis">Không có danh mục nào</div>
                <v-btn color="primary" size="small" class="mt-2" prepend-icon="mdi-shape-plus" @click="openAddCategoryDialog">
                  Thêm danh mục
                </v-btn>
              </div>
              
              <div v-if="productStore.loading" class="pa-4">
                <v-skeleton-loader type="list-item-avatar-two-line" class="mb-2"></v-skeleton-loader>
                <v-skeleton-loader type="list-item-avatar-two-line" class="mb-2"></v-skeleton-loader>
                <v-skeleton-loader type="list-item-avatar-two-line"></v-skeleton-loader>
              </div>
            </v-list>

            <!-- Pagination cho danh mục -->
            <v-row v-if="productStore.categories.length > 0" class="py-4">
              <v-col cols="12" class="d-flex justify-center">
                <v-pagination
                  v-model="categoryPage"
                  :length="Math.ceil(categoryTotal / categoryPageSize)"
                  :total-visible="5"
                  @update:model-value="handleCategoryPageChange"
                  rounded
                ></v-pagination>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-window-item>
    </v-window>

    <!-- Các dialog components cho thêm/sửa/xóa sản phẩm và danh mục -->
    <!-- Dialog thêm/sửa sản phẩm -->
    <v-dialog v-model="addDialog" width="700" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          Thêm sản phẩm mới
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="addForm" @submit.prevent="saveProduct">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="productDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="productDialogError = null"
            >
              {{ productDialogError }}
            </v-alert>

            <v-text-field
              v-model="editedProduct.name"
              label="Tên sản phẩm"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập tên sản phẩm']"
              class="mb-3"
            ></v-text-field>
            
            <v-textarea
              v-model="editedProduct.description"
              label="Mô tả"
              variant="outlined"
              auto-grow
              rows="3"
              class="mb-3"
            ></v-textarea>
            
            <v-select
              v-model="editedProduct.categoryId"
              :items="categoryOptions"
              label="Danh mục"
              variant="outlined"
              item-title="title"
              item-value="value"
              class="mb-3"
              clearable
            ></v-select>
            
            <div class="d-flex gap-3 mb-3">
              <v-switch
                v-model="editedProduct.signature"
                label="Sản phẩm đặc trưng"
                color="primary"
                hide-details
              ></v-switch>
              
              <v-switch
                v-model="editedProduct.available"
                label="Sản phẩm có sẵn"
                color="success"
                hide-details
              ></v-switch>

              <v-switch
                v-model="editedProduct.isTopping"
                label="Là topping"
                color="info"
                hide-details
                @update:model-value="handleToppingChange"
              ></v-switch>
            </div>
            
            <div class="mb-3">
              <label class="text-subtitle-2 mb-2 d-block">Hình ảnh sản phẩm</label>
              
              <div class="d-flex flex-column flex-md-row align-center gap-4">
                <v-img
                  :src="editedProduct.imageUrl || '/images/placeholder.png'"
                  width="120"
                  height="120"
                  cover
                  class="bg-grey-lighten-2 rounded elevation-1"
                ></v-img>
                
                <div class="flex-grow-1">
                  <v-card variant="outlined" class="pa-3">
                    <v-file-input
                      v-model="productImage"
                      accept="image/*"
                      label="Tải lên hình ảnh"
                      variant="outlined"
                      density="compact"
                      prepend-icon="mdi-camera"
                      :show-size="true"
                      @update:model-value="handleImageChange"
                      hint="Chọn ảnh có kích thước tối ưu 500x500px"
                      persistent-hint
                    ></v-file-input>
                    
                    <div v-if="uploadingImage" class="mt-2">
                      <v-progress-linear
                        indeterminate
                        color="primary"
                      ></v-progress-linear>
                      <p class="text-caption text-center mt-1">Đang tải lên...</p>
                    </div>

                    <div v-if="editedProduct.imageUrl" class="mt-2 d-flex justify-end">
                      <v-btn 
                        size="small" 
                        color="error" 
                        variant="text" 
                        density="compact"
                        prepend-icon="mdi-delete"
                        @click="removeImage"
                      >
                        Xóa ảnh
                      </v-btn>
                    </div>
                  </v-card>
                </div>
              </div>
            </div>
            
            <!-- Phần quản lý giá theo size -->
            <v-card v-if="!editDialog" variant="outlined" class="mt-3 mb-3">
              <v-card-title class="d-flex justify-space-between align-center py-3">
                <span class="text-h6">Giá sản phẩm</span>
                <v-btn 
                  size="small" 
                  color="primary" 
                  variant="text" 
                  prepend-icon="mdi-plus" 
                  @click="addProductPrice"
                >
                  Thêm giá
                </v-btn>
              </v-card-title>
              
              <v-divider></v-divider>
              
              <v-card-text class="pa-0">
                <v-table density="compact">
                  <thead>
                    <tr>
                      <th>Kích cỡ</th>
                      <th>Giá (VNĐ)</th>
                      <th class="text-center" style="width: 100px">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(price, index) in editedProduct.prices" :key="index">
                      <td>
                        <v-select
                          v-model="price.sizeId"
                          :items="productStore.productSizes"
                          item-title="name"
                          item-value="id"
                          variant="plain"
                          density="compact"
                          hide-details
                          class="ma-1"
                        ></v-select>
                      </td>
                      <td>
                        <v-text-field
                          v-model.number="price.price"
                          type="number"
                          variant="plain"
                          density="compact"
                          hide-details
                          class="ma-1"
                        ></v-text-field>
                      </td>
                      <td class="text-center">
                        <v-btn 
                          icon="mdi-delete" 
                          size="x-small" 
                          color="error" 
                          variant="text" 
                          @click="removeProductPrice(index)"
                        ></v-btn>
                      </td>
                    </tr>
                    <tr v-if="editedProduct.prices.length === 0">
                      <td colspan="3" class="text-center py-3 text-medium-emphasis">
                        Không có giá nào được thiết lập
                      </td>
                    </tr>
                  </tbody>
                </v-table>
              </v-card-text>
            </v-card>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeAddDialog">Đóng</v-btn>
          <v-btn 
            color="primary" 
            @click="saveProduct" 
            :loading="productStore.loading"
          >
            Lưu
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog chỉnh sửa sản phẩm -->
    <v-dialog v-model="editDialog" width="700" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          Chỉnh sửa sản phẩm
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="editForm" @submit.prevent="updateProduct">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="productDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="productDialogError = null"
            >
              {{ productDialogError }}
            </v-alert>

            <v-text-field
              v-model="editedProduct.name"
              label="Tên sản phẩm"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập tên sản phẩm']"
              class="mb-3"
            ></v-text-field>
            
            <v-textarea
              v-model="editedProduct.description"
              label="Mô tả"
              variant="outlined"
              auto-grow
              rows="3"
              class="mb-3"
            ></v-textarea>
            
            <v-select
              v-model="editedProduct.categoryId"
              :items="categoryOptions"
              label="Danh mục"
              variant="outlined"
              item-title="title"
              item-value="value"
              class="mb-3"
              clearable
            ></v-select>
            
            <div class="d-flex gap-3 mb-3">
              <v-switch
                v-model="editedProduct.signature"
                label="Sản phẩm đặc trưng"
                color="primary"
                hide-details
              ></v-switch>
              
              <v-switch
                v-model="editedProduct.available"
                label="Sản phẩm có sẵn"
                color="success"
                hide-details
              ></v-switch>

              <v-switch
                v-model="editedProduct.isTopping"
                label="Là topping"
                color="info"
                hide-details
                @update:model-value="handleToppingChange"
              ></v-switch>
            </div>
            
            <div class="mb-3">
              <label class="text-subtitle-2 mb-2 d-block">Hình ảnh sản phẩm</label>
              
              <div class="d-flex flex-column flex-md-row align-center gap-4">
                <v-img
                  :src="editedProduct.imageUrl || '/images/placeholder.png'"
                  width="120"
                  height="120"
                  cover
                  class="bg-grey-lighten-2 rounded elevation-1"
                ></v-img>
                
                <div class="flex-grow-1">
                  <v-card variant="outlined" class="pa-3">
                    <v-file-input
                      v-model="productImage"
                      accept="image/*"
                      label="Tải lên hình ảnh"
                      variant="outlined"
                      density="compact"
                      prepend-icon="mdi-camera"
                      :show-size="true"
                      @update:model-value="handleImageChange"
                      hint="Chọn ảnh có kích thước tối ưu 500x500px"
                      persistent-hint
                    ></v-file-input>
                    
                    <div v-if="uploadingImage" class="mt-2">
                      <v-progress-linear
                        indeterminate
                        color="primary"
                      ></v-progress-linear>
                      <p class="text-caption text-center mt-1">Đang tải lên...</p>
                    </div>

                    <div v-if="editedProduct.imageUrl" class="mt-2 d-flex justify-end">
                      <v-btn 
                        size="small" 
                        color="error" 
                        variant="text" 
                        density="compact"
                        prepend-icon="mdi-delete"
                        @click="removeImage"
                      >
                        Xóa ảnh
                      </v-btn>
                    </div>
                  </v-card>
                </div>
              </div>
            </div>
            
            <!-- Phần quản lý giá theo size -->
            <v-card variant="outlined" class="mt-3 mb-3">
              <v-card-title class="d-flex justify-space-between align-center py-3">
                <span class="text-h6">Giá sản phẩm</span>
                <v-btn 
                  size="small" 
                  color="primary" 
                  variant="text" 
                  prepend-icon="mdi-plus" 
                  @click="addProductPrice"
                >
                  Thêm giá
                </v-btn>
              </v-card-title>
              
              <v-divider></v-divider>
              
              <v-card-text class="pa-0">
                <v-table density="compact">
                  <thead>
                    <tr>
                      <th>Kích cỡ</th>
                      <th>Giá (VNĐ)</th>
                      <th class="text-center" style="width: 100px">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(price, index) in editedProduct.prices" :key="index">
                      <td>
                        <div class="pa-2">{{ getSizeName(price.sizeId) }} ({{ getSizeDetail(price.sizeId) }})</div>
                      </td>
                      <td>
                        <v-text-field
                          v-model.number="price.price"
                          type="number"
                          variant="plain"
                          density="compact"
                          hide-details
                          class="ma-1"
                        ></v-text-field>
                      </td>
                      <td class="text-center">
                        <v-btn 
                          icon="mdi-delete" 
                          size="x-small" 
                          color="error" 
                          variant="text" 
                          @click="confirmDeletePrice(price)"
                        ></v-btn>
                      </td>
                    </tr>
                    <tr v-if="editedProduct.prices.length === 0">
                      <td colspan="3" class="text-center py-3 text-medium-emphasis">
                        Không có giá nào được thiết lập
                      </td>
                    </tr>
                  </tbody>
                </v-table>
              </v-card-text>
            </v-card>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeEditDialog">Đóng</v-btn>
          <v-btn 
            color="primary" 
            @click="updateProduct" 
            :loading="productStore.loading"
          >
            Cập nhật
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog xác nhận xóa sản phẩm -->
    <v-dialog v-model="deleteDialog" width="400" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-medium pa-4 bg-error text-white">
          Xác nhận xóa
        </v-card-title>
        
        <v-card-text class="pa-4 pt-5">
          Bạn có chắc chắn muốn xóa sản phẩm <strong>{{ editedProduct.name }}</strong>?
          <p class="text-medium-emphasis mt-2">Hành động này không thể hoàn tác.</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDeleteDialog">Đóng</v-btn>
          <v-btn 
            color="error" 
            @click="deleteSelectedProduct" 
            :loading="productStore.loading"
          >
            Xóa
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Các dialog về danh mục -->
    <!-- Dialog thêm danh mục mới -->
    <v-dialog v-model="addCategoryDialog" width="500" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          Thêm danh mục mới
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="addCategoryForm" @submit.prevent="saveCategory">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="categoryDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="categoryDialogError = null"
            >
              {{ categoryDialogError }}
            </v-alert>

            <v-text-field
              v-model="editedCategory.name"
              label="Tên danh mục"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập tên danh mục']"
              class="mb-3"
            ></v-text-field>
            
            <v-textarea
              v-model="editedCategory.description"
              label="Mô tả"
              variant="outlined"
              auto-grow
              rows="3"
              class="mb-3"
            ></v-textarea>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeAddCategoryDialog">Đóng</v-btn>
          <v-btn 
            color="primary" 
            @click="saveCategory" 
            :loading="productStore.loading"
          >
            Lưu
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog chỉnh sửa danh mục -->
    <v-dialog v-model="editCategoryDialog" width="500" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          Chỉnh sửa danh mục
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="editCategoryForm" @submit.prevent="updateCategory">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="categoryDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="categoryDialogError = null"
            >
              {{ categoryDialogError }}
            </v-alert>

            <v-text-field
              v-model="editedCategory.name"
              label="Tên danh mục"
              variant="outlined"
              required
              :rules="[v => !!v || 'Vui lòng nhập tên danh mục']"
              class="mb-3"
            ></v-text-field>
            
            <v-textarea
              v-model="editedCategory.description"
              label="Mô tả"
              variant="outlined"
              auto-grow
              rows="3"
              class="mb-3"
            ></v-textarea>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeEditCategoryDialog">Đóng</v-btn>
          <v-btn 
            color="primary" 
            @click="updateCategory" 
            :loading="productStore.loading"
          >
            Cập nhật
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog xác nhận xóa danh mục -->
    <v-dialog v-model="deleteCategoryDialog" width="400" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-medium pa-4 bg-error text-white">
          Xác nhận xóa
        </v-card-title>
        
        <v-card-text class="pa-4 pt-5">
          Bạn có chắc chắn muốn xóa danh mục <strong>{{ editedCategory.name }}</strong>?
          <p class="text-medium-emphasis mt-2">Lưu ý: Sản phẩm thuộc danh mục này sẽ không bị xóa, nhưng sẽ không còn thuộc danh mục nào.</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDeleteCategoryDialog">Đóng</v-btn>
          <v-btn 
            color="error" 
            @click="deleteSelectedCategory" 
            :loading="productStore.loading"
          >
            Xóa
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog thêm mới giá sản phẩm -->
    <v-dialog v-model="addPriceDialog" width="500" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          Thêm giá mới
        </v-card-title>
        
        <v-divider></v-divider>
        
        <v-card-text class="pa-4">
          <v-form ref="addPriceForm" @submit.prevent="saveProductPrice">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="priceDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="priceDialogError = null"
            >
              {{ priceDialogError }}
            </v-alert>

            <v-select
              v-model="newPrice.sizeId"
              :items="availableSizes"
              label="Kích cỡ"
              variant="outlined"
              item-title="name"
              item-value="id"
              :rules="[v => !!v || 'Vui lòng chọn kích cỡ']"
              class="mb-3"
              required
            ></v-select>
            
            <v-text-field
              v-model.number="newPrice.price"
              label="Giá (VNĐ)"
              variant="outlined"
              type="number"
              :rules="[
                v => !!v || 'Vui lòng nhập giá',
                v => v > 0 || 'Giá phải lớn hơn 0'
              ]"
              class="mb-3"
              required
            ></v-text-field>
          </v-form>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeAddPriceDialog">Đóng</v-btn>
          <v-btn 
            color="primary" 
            @click="saveProductPrice" 
            :loading="productStore.loading"
          >
            Thêm
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog xác nhận xóa giá sản phẩm -->
    <v-dialog v-model="deletePriceDialog" width="400" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-medium pa-4 bg-error text-white">
          Xác nhận xóa giá
        </v-card-title>
        
        <v-card-text class="pa-4 pt-5">
          Bạn có chắc chắn muốn xóa giá cho kích cỡ <strong>{{ selectedPrice ? getSizeName(selectedPrice.sizeId) : '' }}</strong>?
          <p class="text-medium-emphasis mt-2">Hành động này không thể hoàn tác.</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closePriceDeleteDialog">Đóng</v-btn>
          <v-btn 
            color="error" 
            @click="deletePrice" 
            :loading="productStore.loading"
          >
            Xóa
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Dialog xác nhận thay đổi topping -->
    <v-dialog v-model="toppingConfirmationDialog" width="450" persistent>
      <v-card>
        <v-card-title class="text-h6 font-weight-medium pa-4 bg-warning text-white">
          Xác nhận thay đổi thành Topping
        </v-card-title>
        
        <v-card-text class="pa-4 pt-5">
          <p>Việc đánh dấu sản phẩm này là "Topping" sẽ thực hiện các thay đổi sau:</p>
          <ul class="my-3">
            <li>Xóa tất cả các giá theo kích thước hiện có.</li>
            <li>Thêm kích thước "NA" với giá mặc định (1.000đ).</li>
            <li>Tự động chuyển sang danh mục "Topping".</li>
          </ul>
          <p>Bạn có chắc chắn muốn tiếp tục không?</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="cancelToppingChange">Hủy</v-btn>
          <v-btn 
            color="warning" 
            @click="confirmToppingChange"
          >
            Xác nhận
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
    <!-- Snackbar thông báo -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
    >
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn
          color="white"
          icon="mdi-close"
          variant="text"
          @click="snackbar.show = false"
        ></v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { useProductStore } from '@/stores/product'
import { debounce } from 'lodash'
import { productService } from '@/services/productService'

const productStore = useProductStore()

// State cho sản phẩm
const productPage = ref(1)
const productPageSize = ref(12)
const categoryPage = ref(1)
const categoryPageSize = ref(10)
const categoryTotal = ref(0)
const searchQuery = ref('')
const selectedCategory = ref(null)
const selectedStatus = ref(null)
const activeTab = ref('products')
const addDialog = ref(false)
const editDialog = ref(false)
const deleteDialog = ref(false)
const addPriceDialog = ref(false)
const deletePriceDialog = ref(false)
const editedIndex = ref(-1)
const selectedPrice = ref(null)
const editedProduct = ref({
  name: '',
  description: '',
  categoryId: null,
  imageUrl: '',
  signature: false,
  available: true,
  isTopping: false,
  prices: []
})
const defaultProduct = {
  name: '',
  description: '',
  categoryId: null,
  imageUrl: '',
  signature: false,
  available: true,
  isTopping: false,
  prices: []
}

const newPrice = ref({
  sizeId: null,
  price: 0
})

// Lưu trữ toàn bộ sản phẩm để lọc ở frontend
const allProducts = ref([])

// State cho danh mục
const addCategoryDialog = ref(false)
const editCategoryDialog = ref(false)
const deleteCategoryDialog = ref(false)
const editedCategory = ref({
  id: null,
  name: '',
  description: ''
})
const defaultCategory = {
  id: null,
  name: '',
  description: ''
}

// Snackbar
const snackbar = ref({
  show: false ,
  message: '',
  color: 'success'
})

// Biến theo dõi lỗi dialog
const productDialogError = ref(null)
const categoryDialogError = ref(null)
const priceDialogError = ref(null)

// Dialog xác nhận topping
const toppingConfirmationDialog = ref(false)

// Xử lý hình ảnh
const productImage = ref(null)
const uploadingImage = ref(false)

// Methods cho sản phẩm
const loadProducts = async () => {
  try {
    const response = await productStore.fetchProducts(0, 1000) // Lấy tất cả sản phẩm
    allProducts.value = response.content || []
    // Reset lại trang khi cập nhật bộ lọc
    if (hasFilters.value) {
      productPage.value = 1
    }
  } catch (error) {
    console.error("Lỗi khi tải sản phẩm:", error)
  }
}

const loadCategories = async () => {
  try {
    const response = await productStore.fetchCategories(categoryPage.value - 1, categoryPageSize.value)
    categoryTotal.value = response.totalElements || 0
  } catch (error) {
    console.error("Lỗi khi tải danh mục:", error)
  }
}

const loadProductSizes = async () => {
  try {
    await productStore.fetchProductSizes()
  } catch (error) {
    console.error("Lỗi khi tải kích cỡ sản phẩm:", error)
  }
}

const getCategoryName = (catId) => {
  const category = productStore.categories.find(cat => cat.id === catId)
  return category ? category.name : 'Không có danh mục'
}

const getSizeName = (sizeId) => {
  const size = productStore.productSizes.find(s => s.id === sizeId)
  return size ? size.name : 'Không xác định'
}

const getSizeDetail = (sizeId) => {
  const size = productStore.productSizes.find(s => s.id === sizeId)
  if (!size) return ''
  return `${size.quantity || ''} ${size.unitSymbol || ''}`
}

const openAddDialog = () => {
  editedProduct.value = {...defaultProduct}
  addDialog.value = true
}

const closeAddDialog = () => {
  addDialog.value = false
}

const openEditDialog = async (product) => {
  try {
    const productDetail = await productStore.fetchProductById(product.id)
    
    editedProduct.value = {
      id: productDetail.id,
      name: productDetail.name,
      description: productDetail.description || '',
      categoryId: productDetail.category ? productDetail.category.id : null,
      imageUrl: productDetail.image_url || '',
      signature: productDetail.signature || false,
      available: productDetail.available !== undefined ? productDetail.available : true,
      isTopping: productDetail.isTopping || false,
      prices: (productDetail.prices || []).map(p => ({
        id: p.id,
        sizeId: p.sizeId,
        price: p.price
      }))
    }
    
    editDialog.value = true
  } catch (error) {
    showSnackbar(`Lỗi: ${error.message}`, 'error')
  }
}

const closeEditDialog = () => {
  editDialog.value = false
}

const openDeleteDialog = (product) => {
  editedProduct.value = {...product}
  deleteDialog.value = true
}

const closeDeleteDialog = () => {
  deleteDialog.value = false
}

const addProductPrice = () => {
  newPrice.value = {
    sizeId: null,
    price: 0
  }
  addPriceDialog.value = true
}

const closeAddPriceDialog = () => {
  addPriceDialog.value = false
}

const saveProductPrice = () => {
  // Reset lỗi dialog
  priceDialogError.value = null
  
  try {
    if (!newPrice.value.sizeId) {
      priceDialogError.value = 'Vui lòng chọn kích cỡ'
      return
    }
    
    // Kiểm tra xem kích cỡ đã tồn tại trong danh sách giá chưa
    const existingPrice = editedProduct.value.prices.find(p => p.sizeId === newPrice.value.sizeId)
    if (existingPrice) {
      priceDialogError.value = 'Kích cỡ này đã được thiết lập giá'
      return
    }
    
    // Kiểm tra giá tối thiểu nếu sản phẩm là topping
    if (editedProduct.value.isTopping && newPrice.value.price < 1000) {
      priceDialogError.value = 'Giá topping phải từ 1.000đ trở lên'
      return
    }
    
    if (newPrice.value.price <= 0) {
      priceDialogError.value = 'Giá phải lớn hơn 0'
      return
    }
    
    editedProduct.value.prices.push({
      sizeId: newPrice.value.sizeId,
      price: newPrice.value.price
    })
    
    closeAddPriceDialog()
  } catch (error) {
    console.error('Lỗi khi thêm giá sản phẩm:', error)
    priceDialogError.value = error.message || 'Đã xảy ra lỗi khi thêm giá'
  }
}

const removeProductPrice = (index) => {
  editedProduct.value.prices.splice(index, 1)
}

const confirmDeletePrice = (price) => {
  selectedPrice.value = price
  deletePriceDialog.value = true
}

const closePriceDeleteDialog = () => {
  deletePriceDialog.value = false
  selectedPrice.value = null
}

const deletePrice = async () => {
  try {
    // Reset lỗi dialog
    priceDialogError.value = null
    
    if (editDialog.value && editedProduct.value.id && selectedPrice.value.id) {
      await productStore.deleteProductPrice(editedProduct.value.id, selectedPrice.value.sizeId)
      
      // Cập nhật lại danh sách giá trong form
      editedProduct.value.prices = editedProduct.value.prices.filter(
        p => p.sizeId !== selectedPrice.value.sizeId
      )
      
      showSnackbar('Đã xóa giá thành công', 'success')
    } else {
      // Đối với giá mới chưa lưu
      editedProduct.value.prices = editedProduct.value.prices.filter(
        p => p.sizeId !== selectedPrice.value.sizeId
      )
    }
    
    closePriceDeleteDialog()
  } catch (error) {
    console.error('Lỗi khi xóa giá sản phẩm:', error)
    priceDialogError.value = error.message || 'Đã xảy ra lỗi khi xóa giá'
    showSnackbar(`Lỗi: ${error.message}`, 'error')
    // Không đóng dialog khi có lỗi
  }
}

const saveProduct = async () => {
  try {
    // Reset lỗi dialog
    productDialogError.value = null
    
    // Kiểm tra nếu là topping
    if (editedProduct.value.isTopping) {
      // Kiểm tra danh mục
      const isValidCategory = editedProduct.value.categoryId === 1 || editedProduct.value.categoryId === 2
      if (!isValidCategory) {
        showSnackbar('Topping phải thuộc danh mục Topping hoặc Topping bán lẻ', 'error')
        return
      }
      
      // Kiểm tra giá
      const hasInvalidPrice = editedProduct.value.prices.some(price => price.price < 1000)
      if (hasInvalidPrice) {
        showSnackbar('Giá topping phải từ 1.000đ trở lên', 'error')
        return
      }
    }
    
    const productData = {
      name: editedProduct.value.name,
      description: editedProduct.value.description || '',
      categoryId: editedProduct.value.categoryId,
      imagePath: editedProduct.value.imageUrl,
      signature: editedProduct.value.signature,
      available: editedProduct.value.available,
      isTopping: editedProduct.value.isTopping,
      prices: {}
    }
    
    // Chuyển đổi mảng giá thành đối tượng theo yêu cầu của API
    editedProduct.value.prices.forEach(price => {
      productData.prices[price.sizeId] = price.price
    })
    
    await productStore.createProduct(productData)
    showSnackbar('Sản phẩm đã được thêm thành công', 'success')
    closeAddDialog()
    loadProducts()
  } catch (error) {
    console.error('Lỗi khi lưu sản phẩm:', error)
    productDialogError.value = error.message || 'Đã xảy ra lỗi khi lưu sản phẩm'
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
    // Không đóng dialog khi có lỗi
  }
}

const updateProduct = async () => {
  try {
    // Reset lỗi dialog
    productDialogError.value = null
    
    // Kiểm tra nếu là topping
    if (editedProduct.value.isTopping) {
      // Kiểm tra danh mục
      const isValidCategory = editedProduct.value.categoryId === 1 || editedProduct.value.categoryId === 2
      if (!isValidCategory) {
        showSnackbar('Topping phải thuộc danh mục Topping hoặc Topping bán lẻ', 'error')
        return
      }
      
      // Kiểm tra giá
      const hasInvalidPrice = editedProduct.value.prices.some(price => price.price < 1000)
      if (hasInvalidPrice) {
        showSnackbar('Giá topping phải từ 1.000đ trở lên', 'error')
        return
      }
    }
    
    // 1. Cập nhật thông tin cơ bản của sản phẩm
    const productData = {
      name: editedProduct.value.name,
      description: editedProduct.value.description || '',
      categoryId: editedProduct.value.categoryId,
      imagePath: editedProduct.value.imageUrl,
      signature: editedProduct.value.signature,
      available: editedProduct.value.available,
      isTopping: editedProduct.value.isTopping
    }
    
    await productStore.updateProduct(editedProduct.value.id, productData)
    
    // 2. Cập nhật giá (nếu có thay đổi)
    const pricesToUpdate = editedProduct.value.prices.filter(p => p.id); // Chỉ những giá đã tồn tại
    
    if (pricesToUpdate.length > 0) {
      await productStore.updateProductPrice(editedProduct.value.id, pricesToUpdate)
    }
    
    // 3. Thêm giá mới (nếu có)
    const newPrices = editedProduct.value.prices.filter(p => !p.id); // Những giá chưa có id
    
    if (newPrices.length > 0) {
      await productStore.addProductPrice(editedProduct.value.id, newPrices)
    }
    
    showSnackbar('Sản phẩm đã được cập nhật thành công', 'success')
    closeEditDialog()
    loadProducts()
  } catch (error) {
    console.error('Lỗi khi cập nhật sản phẩm:', error)
    productDialogError.value = error.message || 'Đã xảy ra lỗi khi cập nhật sản phẩm'
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
    // Không đóng dialog khi có lỗi
  }
}

const deleteSelectedProduct = async () => {
  try {
    await productStore.deleteProduct(editedProduct.value.id)
    showSnackbar('Sản phẩm đã được xóa thành công', 'success')
    closeDeleteDialog()
    loadProducts()
  } catch (error) {
    console.error('Lỗi khi xóa sản phẩm:', error)
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
    // Không đóng dialog khi có lỗi
  }
}

const handleProductPageChange = (newPage) => {
  productPage.value = newPage
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = null
  selectedStatus.value = null
  productPage.value = 1
}

// Methods cho danh mục
const selectCategory = (category) => {
  selectedCategory.value = category.id
}

const openAddCategoryDialog = () => {
  editedCategory.value = {...defaultCategory}
  addCategoryDialog.value = true
}

const closeAddCategoryDialog = () => {
  addCategoryDialog.value = false
}

const openEditCategoryDialog = (category) => {
  editedCategory.value = {...category}
  editCategoryDialog.value = true
}

const closeEditCategoryDialog = () => {
  editCategoryDialog.value = false
}

const openDeleteCategoryDialog = (category) => {
  editedCategory.value = {...category}
  deleteCategoryDialog.value = true
}

const closeDeleteCategoryDialog = () => {
  deleteCategoryDialog.value = false
}

const saveCategory = async () => {
  try {
    // Reset lỗi dialog
    categoryDialogError.value = null
    
    await productStore.createCategory(editedCategory.value)
    showSnackbar('Danh mục đã được thêm thành công', 'success')
    closeAddCategoryDialog()
    loadCategories()
  } catch (error) {
    console.error('Lỗi khi lưu danh mục:', error)
    categoryDialogError.value = error.message || 'Đã xảy ra lỗi khi lưu danh mục'
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
    // Không đóng dialog khi có lỗi
  }
}

const updateCategory = async () => {
  try {
    // Reset lỗi dialog
    categoryDialogError.value = null
    
    await productStore.updateCategory(editedCategory.value.id, editedCategory.value)
    showSnackbar('Danh mục đã được cập nhật thành công', 'success')
    closeEditCategoryDialog()
    loadCategories()
  } catch (error) {
    console.error('Lỗi khi cập nhật danh mục:', error)
    categoryDialogError.value = error.message || 'Đã xảy ra lỗi khi cập nhật danh mục'
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
    // Không đóng dialog khi có lỗi
  }
}

const deleteSelectedCategory = async () => {
  try {
    await productStore.deleteCategory(editedCategory.value.id)
    showSnackbar('Danh mục đã được xóa thành công', 'success')
    closeDeleteCategoryDialog()
    loadCategories()
    // Nếu đang lọc theo danh mục này, reset lọc
    if (selectedCategory.value === editedCategory.value.id) {
      selectedCategory.value = null
    }
  } catch (error) {
    console.error('Lỗi khi xóa danh mục:', error)
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
    // Không đóng dialog khi có lỗi
  }
}

const handleCategoryPageChange = (newPage) => {
  categoryPage.value = newPage
  loadCategories()
}

// Snackbar
function showSnackbar(message, color = 'success') {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

// Debounce search
const debounceSearch = debounce(() => {
  productPage.value = 1 // Reset về trang đầu tiên khi tìm kiếm
}, 300)

// Lifecycle
onMounted(() => {
  loadProducts()
  loadCategories()
  loadProductSizes()
})

// Thêm hàm theo dõi tab
watch(activeTab, (newTab) => {
  if (newTab === 'categories') {
    loadCategories()
  }
})

// Theo dõi các thay đổi bộ lọc
watch([searchQuery, selectedCategory, selectedStatus], () => {
  productPage.value = 1 // Reset lại trang 1 khi thay đổi bộ lọc
})

const formatCurrency = (value) => {
  if (!value) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

// Hàm xử lý khi chọn hình ảnh
const handleImageChange = async (file) => {
  if (!file) return
  
  try {
    uploadingImage.value = true
    const imageData = await productService.uploadProductImage(file)
    console.log(imageData)
    // Cập nhật đường dẫn hình ảnh
    editedProduct.value.imageUrl = imageData.fileUrl
    showSnackbar('Tải hình ảnh lên thành công', 'success')
  } catch (error) {
    showSnackbar('Lỗi khi tải hình ảnh: ' + error.message, 'error')
  } finally {
    uploadingImage.value = false
    productImage.value = null
  }
}

// Thêm hàm xử lý khi thay đổi trạng thái topping
const handleToppingChange = (value) => {
  if (value && editDialog.value && editedProduct.value.prices.length > 1) {
    // Nếu bật topping khi đang chỉnh sửa và có nhiều hơn 1 giá
    toppingConfirmationDialog.value = true
    // Tạm thời revert lại giá trị để chờ xác nhận
    // Dùng nextTick để đảm bảo model được cập nhật trước khi revert
    nextTick(() => {
      editedProduct.value.isTopping = false
    })
  } else {
    // Áp dụng thay đổi ngay nếu không cần xác nhận
    applyToppingChange(value)
  }
}

// Hàm thực hiện logic thay đổi topping sau khi xác nhận hoặc không cần xác nhận
const applyToppingChange = (value) => {
  // Cập nhật lại trạng thái isTopping thực sự
  editedProduct.value.isTopping = value

  if (value) {
    // Nếu là topping, tự động thêm kích thước NA nếu chưa có
    const naSize = productStore.productSizes.find(s => s.name === 'NA')
    if (naSize) {
      const hasNASize = editedProduct.value.prices.some(price => price.sizeId === naSize.id)
      if (!hasNASize) {
        // Xóa tất cả kích thước hiện có và thêm NA
        editedProduct.value.prices = [{
          sizeId: naSize.id,
          price: 1000 // Giá mặc định tối thiểu cho topping
        }]
      }
    }
    
    // Tự động chọn danh mục Topping hoặc Topping bán lẻ
    const toppingCategories = productStore.categories.filter(cat => 
      cat.id === 1 || cat.id === 2
    )
    
    if (toppingCategories.length > 0) {
      // Mặc định chọn danh mục Topping (ID: 1)
      const defaultToppingCategory = toppingCategories.find(cat => cat.id === 1) || toppingCategories[0]
      editedProduct.value.categoryId = defaultToppingCategory.id
    }
  } else {
    // Nếu không còn là topping, có thể cần xử lý thêm nếu muốn khôi phục giá cũ hoặc yêu cầu người dùng nhập lại
    // Hiện tại chỉ bỏ đánh dấu
  }
}

// Hàm xác nhận thay đổi topping
const confirmToppingChange = () => {
  applyToppingChange(true)
  toppingConfirmationDialog.value = false
}

// Hàm hủy thay đổi topping
const cancelToppingChange = () => {
  // Giá trị isTopping đã được revert trong handleToppingChange
  toppingConfirmationDialog.value = false
}

// Thêm hàm xóa ảnh
const removeImage = () => {
  editedProduct.value.imageUrl = ''
  productImage.value = null
  showSnackbar('Đã xóa ảnh sản phẩm', 'info')
}

// Computed properties cho lọc và phân trang
const hasFilters = computed(() => {
  return searchQuery.value || selectedCategory.value || selectedStatus.value
})

// Lọc sản phẩm ở frontend
const filteredProducts = computed(() => {
  let result = [...allProducts.value]
  
  // Lọc theo tên sản phẩm
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(product => 
      product.name.toLowerCase().includes(query) || 
      (product.description && product.description.toLowerCase().includes(query))
    )
  }
  
  // Lọc theo danh mục
  if (selectedCategory.value) {
    result = result.filter(product => product.catId === selectedCategory.value)
  }
  
  // Lọc theo trạng thái
  if (selectedStatus.value) {
    result = result.filter(product => product.status === selectedStatus.value)
  }
  
  return result
})

// Sản phẩm đã được phân trang
const paginatedProducts = computed(() => {
  const startIndex = (productPage.value - 1) * productPageSize.value
  const endIndex = startIndex + productPageSize.value
  return filteredProducts.value.slice(startIndex, endIndex)
})

// Tổng số trang sau khi lọc
const totalProductPages = computed(() => {
  return Math.ceil(filteredProducts.value.length / productPageSize.value)
})

const categoryOptions = computed(() => {
  if (!productStore.categories) return []
  return productStore.categories.map(cat => ({
    title: cat.name,
    value: cat.id
  }))
})

const statusOptions = ref([
  { title: 'Đang bán', value: 'active' },
  { title: 'Ngừng bán', value: 'inactive' }
])

// Danh sách kích cỡ đã được chọn
const selectedSizes = computed(() => {
  return editedProduct.value.prices.map(p => p.sizeId)
})

// Danh sách kích cỡ còn lại có thể chọn
const availableSizes = computed(() => {
  return productStore.productSizes.filter(size => !selectedSizes.value.includes(size.id))
})
</script>

<style scoped>
.signature-badge {
  background-color: rgba(var(--v-theme-primary), 0.9);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  display: inline-flex;
  align-items: center;
  margin: 8px;
}

.signature-badge .v-icon {
  margin-right: 4px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  display: inline-flex;
  align-items: center;
  margin: 8px;
}

.status-badge .v-icon {
  margin-right: 4px;
}

.status-inactive {
  background-color: rgba(var(--v-theme-error), 0.9);
  color: white;
}

.max-width-200 {
  max-width: 200px;
}
</style> 