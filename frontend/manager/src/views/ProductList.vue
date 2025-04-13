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
            
            <v-text-field
              v-model="editedProduct.imageUrl"
              label="Đường dẫn hình ảnh"
              variant="outlined"
              class="mb-3"
              placeholder="https://..."
            ></v-text-field>
            
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
          <v-btn variant="text" @click="closeAddDialog">Hủy</v-btn>
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
            
            <v-text-field
              v-model="editedProduct.imageUrl"
              label="Đường dẫn hình ảnh"
              variant="outlined"
              class="mb-3"
              placeholder="https://..."
            ></v-text-field>
            
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
          <v-btn variant="text" @click="closeEditDialog">Hủy</v-btn>
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
    <v-dialog v-model="deleteDialog" width="400">
      <v-card>
        <v-card-title class="text-h5 font-weight-medium pa-4">
          Xác nhận xóa
        </v-card-title>
        
        <v-card-text class="pa-4">
          Bạn có chắc chắn muốn xóa sản phẩm <strong>{{ editedProduct.name }}</strong>?
          <p class="text-medium-emphasis mt-2">Hành động này không thể hoàn tác.</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDeleteDialog">Hủy</v-btn>
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
          <v-btn variant="text" @click="closeAddCategoryDialog">Hủy</v-btn>
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
          <v-btn variant="text" @click="closeEditCategoryDialog">Hủy</v-btn>
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
    <v-dialog v-model="deleteCategoryDialog" width="400">
      <v-card>
        <v-card-title class="text-h5 font-weight-medium pa-4">
          Xác nhận xóa
        </v-card-title>
        
        <v-card-text class="pa-4">
          Bạn có chắc chắn muốn xóa danh mục <strong>{{ editedCategory.name }}</strong>?
          <p class="text-medium-emphasis mt-2">Lưu ý: Sản phẩm thuộc danh mục này sẽ không bị xóa, nhưng sẽ không còn thuộc danh mục nào.</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDeleteCategoryDialog">Hủy</v-btn>
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
          <v-btn variant="text" @click="closeAddPriceDialog">Hủy</v-btn>
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
    <v-dialog v-model="deletePriceDialog" width="400">
      <v-card>
        <v-card-title class="text-h5 font-weight-medium pa-4">
          Xác nhận xóa giá
        </v-card-title>
        
        <v-card-text class="pa-4">
          Bạn có chắc chắn muốn xóa giá cho kích cỡ <strong>{{ selectedPrice ? getSizeName(selectedPrice.sizeId) : '' }}</strong>?
          <p class="text-medium-emphasis mt-2">Hành động này không thể hoàn tác.</p>
        </v-card-text>
        
        <v-divider></v-divider>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closePriceDeleteDialog">Hủy</v-btn>
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useProductStore } from '@/stores/product'
import { debounce } from 'lodash'

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
  prices: []
})
const defaultProduct = {
  name: '',
  description: '',
  categoryId: null,
  imageUrl: '',
  signature: false,
  available: true,
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
  if (!newPrice.value.sizeId || newPrice.value.price <= 0) {
    showSnackbar('Vui lòng nhập đầy đủ thông tin', 'error')
    return
  }
  
  editedProduct.value.prices.push({
    sizeId: newPrice.value.sizeId,
    price: newPrice.value.price
  })
  
  closeAddPriceDialog()
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
    showSnackbar(`Lỗi: ${error.message}`, 'error')
  }
}

const saveProduct = async () => {
  try {
    const productData = {
      name: editedProduct.value.name,
      description: editedProduct.value.description || '',
      categoryId: editedProduct.value.categoryId,
      imagePath: editedProduct.value.imageUrl,
      signature: editedProduct.value.signature,
      available: editedProduct.value.available,
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
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
  }
}

const updateProduct = async () => {
  try {
    // 1. Cập nhật thông tin cơ bản của sản phẩm
    const productData = {
      name: editedProduct.value.name,
      description: editedProduct.value.description || '',
      categoryId: editedProduct.value.categoryId,
      imagePath: editedProduct.value.imageUrl,
      signature: editedProduct.value.signature,
      available: editedProduct.value.available
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
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
  }
}

const deleteSelectedProduct = async () => {
  try {
    await productStore.deleteProduct(editedProduct.value.id)
    showSnackbar('Sản phẩm đã được xóa thành công', 'success')
    closeDeleteDialog()
    loadProducts()
  } catch (error) {
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
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
    await productStore.createCategory(editedCategory.value)
    showSnackbar('Danh mục đã được thêm thành công', 'success')
    closeAddCategoryDialog()
    loadCategories()
  } catch (error) {
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
  }
}

const updateCategory = async () => {
  try {
    await productStore.updateCategory(editedCategory.value.id, editedCategory.value)
    showSnackbar('Danh mục đã được cập nhật thành công', 'success')
    closeEditCategoryDialog()
    loadCategories()
  } catch (error) {
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
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
    showSnackbar('Đã xảy ra lỗi: ' + error.message, 'error')
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