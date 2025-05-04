<template>
  <div>
    <!-- Phần header với tiêu đề -->
    <div class="d-flex justify-space-between align-center mb-4 px-4 pt-4">
      <div>
        <h2 class="text-h5 font-weight-bold">Quản lý khách hàng & Chương trình thành viên</h2>
        <p class="text-subtitle-2 text-medium-emphasis mt-1">
          Quản lý thông tin khách hàng và chương trình thành viên trong hệ thống
        </p>
      </div>
    </div>

    <!-- Alert hiển thị lỗi nếu có -->
    <v-alert
      v-if="customerStore.error"
      type="error"
      variant="tonal"
      closable
      class="mx-4 mb-4"
    >
      {{ customerStore.error }}
    </v-alert>

    <v-alert
      v-if="membershipStore.error"
      type="error"
      variant="tonal"
      closable
      class="mx-4 mb-4"
    >
      {{ membershipStore.error }}
    </v-alert>

    <!-- Tabs -->
    <v-tabs v-model="activeTab" color="primary" align-tabs="start" class="px-4 mb-4">
      <v-tab value="customers">
        <v-icon start>mdi-account-multiple</v-icon>
        Khách hàng
      </v-tab>
      <v-tab value="membership">
        <v-icon start>mdi-star-circle</v-icon>
        Chương trình thành viên
      </v-tab>
    </v-tabs>

    <v-window v-model="activeTab" class="px-4">
      <!-- Tab Khách hàng -->
      <v-window-item value="customers">
        <v-card>
          <v-card-title class="d-flex justify-space-between align-center py-3 px-4">
            <span class="text-h6">Danh sách khách hàng</span>
            <v-btn
              color="primary"
              size="small"
              variant="text"
              prepend-icon="mdi-account-plus"
              @click="openAddCustomerDialog"
            >
              Thêm khách hàng
            </v-btn>
          </v-card-title>

          <v-divider></v-divider>

          <v-card-text class="pa-2">
            <!-- Filter và tìm kiếm -->
            <v-row class="px-2 py-2">
              <v-col cols="12" md="8">
                <v-text-field
                  v-model="customerSearchQuery"
                  label="Tìm kiếm theo tên, email, số điện thoại"
                  density="compact"
                  variant="outlined"
                  hide-details
                  prepend-inner-icon="mdi-magnify"
                  clearable
                  @update:model-value="debounceCustomerSearch"
                ></v-text-field>
              </v-col>

              <v-col cols="12" md="4">
                <v-select
                  v-model="selectedMembership"
                  :items="membershipOptions"
                  label="Loại thành viên"
                  density="compact"
                  variant="outlined"
                  hide-details
                  clearable
                  @update:model-value="loadCustomers"
                ></v-select>
              </v-col>
            </v-row>

            <!-- Bảng khách hàng -->
            <v-data-table
              :headers="customerHeaders"
              :items="filteredCustomers"
              :loading="customerStore.loading"
              loading-text="Đang tải dữ liệu..."
              no-data-text="Không có dữ liệu"
              item-value="id"
              hover
              class="elevation-0"
              :items-per-page="10"
              :page="customerPage"
              @update:page="customerPage = $event"
              :items-per-page-options="[5, 10, 15, 20]"
            >
              <!-- Tên khách hàng -->
              <template v-slot:item.fullName="{ item }">
                <div class="d-flex align-center">
                  <v-avatar size="36" color="grey-lighten-3" class="mr-2">
                    <span class="text-caption">{{ getInitials(item.firstName, item.lastName) }}</span>
                  </v-avatar>
                  <div>
                    {{ item.firstName }} {{ item.lastName }}
                  </div>
                </div>
              </template>

              <!-- Giới tính -->
              <template v-slot:item.gender="{ item }">
                <v-chip
                  v-if="item.gender"
                  size="small"
                  :color="item.gender === 'MALE' ? 'blue' : item.gender === 'FEMALE' ? 'pink' : 'grey'"
                  text-color="white"
                  variant="flat"
                >
                  {{ getGenderText(item.gender) }}
                </v-chip>
                <span v-else class="text-caption">Không xác định</span>
              </template>

              <!-- Điểm thưởng -->
              <template v-slot:item.rewardPoint="{ item }">
                <v-chip
                  size="small"
                  color="primary"
                  variant="outlined"
                >
                  {{ item.rewardPoint }}
                </v-chip>
              </template>

              <!-- Loại thành viên -->
              <template v-slot:item.membershipId="{ item }">
                <v-chip
                  size="small"
                  :color="getMembershipColor(item.membershipId)"
                  variant="flat"
                >
                  {{ getMembershipName(item.membershipId) }}
                </v-chip>
              </template>

              <!-- Hành động -->
              <template v-slot:item.actions="{ item }">
                <div class="d-flex gap-2">
                  <v-btn
                    icon="mdi-pencil"
                    size="small"
                    color="primary"
                    variant="text"
                    @click="openEditCustomerDialog(item)"
                  ></v-btn>
                  <v-btn
                    icon="mdi-delete"
                    size="small"
                    color="error"
                    variant="text"
                    @click="openDeleteCustomerDialog(item)"
                  ></v-btn>
                </div>
              </template>
            </v-data-table>

            <!-- Phân trang cho khách hàng -->
            <div class="d-flex justify-center py-4">
              <v-pagination
                v-if="customerStore.totalCustomers > 0"
                v-model="customerPage"
                :length="totalFilteredCustomerPages"
                :total-visible="7"
                @update:model-value="handleCustomerPageChange"
              ></v-pagination>
            </div>
          </v-card-text>
        </v-card>
      </v-window-item>

      <!-- Tab Chương trình thành viên -->
      <v-window-item value="membership">
        <v-card>
          <v-card-title class="d-flex justify-space-between align-center py-3 px-4">
            <span class="text-h6">Chương trình thành viên</span>
            <v-btn
              color="primary"
              size="small"
              variant="text"
              prepend-icon="mdi-star-plus"
              @click="openAddMembershipDialog"
            >
              Thêm loại thành viên
            </v-btn>
          </v-card-title>

          <v-divider></v-divider>

          <v-card-text>
            <!-- Hiển thị danh sách thẻ thành viên -->
            <div v-if="membershipStore.loading" class="d-flex justify-center py-4">
              <v-progress-circular
                indeterminate
                color="primary"
                size="64"
              ></v-progress-circular>
            </div>

            <div v-else-if="membershipStore.membershipTypes.length === 0" class="text-center py-4">
              <v-icon size="64" color="grey-lighten-2" class="mb-2">mdi-credit-card-off</v-icon>
              <p class="text-body-1 text-medium-emphasis">Không có dữ liệu chương trình thành viên</p>
            </div>

            <v-row v-else>
              <v-col
                v-for="item in membershipStore.membershipTypes"
                :key="item.id"
                cols="12" sm="6" md="4" lg="3"
              >
                <v-card
                  class="membership-card"
                  :class="{'inactive-card': !item.isActive}"
                  elevation="3"
                >
                  <div class="membership-header" :class="getMembershipHeaderClass(item)">
                    <div class="d-flex align-center">
                      <v-avatar class="membership-avatar" :class="getMembershipAvatarClass(item)">
                        <v-icon color="white">mdi-crown</v-icon>
                      </v-avatar>
                      <div>
                        <div class="text-h6 font-weight-bold text-white">{{ item.name }}</div>
                        <div class="text-body-2 text-white">{{ item.requiredPoint }} điểm</div>
                      </div>
                    </div>

                    <div v-if="item.validUntil" class="text-caption text-white mt-1">
                      Hạn: {{ formatDate(item.validUntil) }}
                    </div>
                  </div>

                  <v-card-text>
                    <div v-if="item.description" class="membership-description mb-3">
                      <div class="font-weight-bold mb-1">Mô tả:</div>
                      <div>{{ item.description }}</div>
                    </div>

                    <div class="font-weight-bold mb-2">Quyền lợi:</div>
                    <v-list density="compact" class="membership-benefits pa-0">
                      <v-list-item v-if="item.discountValue > 0">
                        <template v-slot:prepend>
                          <v-icon color="success" size="small">mdi-check-circle</v-icon>
                        </template>
                        <v-list-item-title class="text-body-2">
                          Giảm {{ formatDiscountValue(item) }} cho mỗi đơn hàng
                        </v-list-item-title>
                      </v-list-item>
                    </v-list>

                    <div class="d-flex align-center justify-space-between mt-4">

                      <div class="d-flex gap-2">
                        <v-btn
                          icon="mdi-pencil"
                          size="small"
                          color="primary"
                          variant="text"
                          @click="openEditMembershipDialog(item)"
                        ></v-btn>
                        <v-btn
                          icon="mdi-delete"
                          size="small"
                          color="error"
                          variant="text"
                          @click="openDeleteMembershipDialog(item)"
                        ></v-btn>
                      </div>
                    </div>
                  </v-card-text>

                  <v-chip
                    v-if="!item.isActive"
                    class="status-chip"
                    color="error"
                    size="small"
                  >
                    Không hoạt động
                  </v-chip>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-window-item>
    </v-window>

    <!-- Dialog thêm/sửa khách hàng -->
    <v-dialog v-model="customerDialog" max-width="1000px" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          {{ editMode ? 'Chỉnh sửa thông tin khách hàng' : 'Thêm khách hàng mới' }}
        </v-card-title>

        <v-divider></v-divider>

        <v-card-text class="pa-0">
          <v-tabs v-model="activeCustomerTab" bg-color="primary">
            <v-tab value="info">
              <v-icon start>mdi-account</v-icon>
              Thông tin khách hàng
            </v-tab>
            <v-tab value="account" v-if="editMode && customerDetail && customerDetail.accountId">
              <v-icon start>mdi-shield-account</v-icon>
              Thông tin tài khoản
            </v-tab>
            <v-tab value="password" v-if="editMode && customerDetail && customerDetail.accountId">
              <v-icon start>mdi-lock-reset</v-icon>
              Đổi mật khẩu
            </v-tab>
            <v-tab value="role" v-if="editMode && customerDetail && customerDetail.accountId">
              <v-icon start>mdi-account-cog</v-icon>
              Phân quyền
            </v-tab>
          </v-tabs>

          <v-window v-model="activeCustomerTab">
            <!-- Tab thông tin khách hàng -->
            <v-window-item value="info">
              <v-container class="pa-4">
                <v-form ref="customerForm" @submit.prevent="saveCustomer">
                  <!-- Hiển thị lỗi dialog -->
                  <v-alert
                    v-if="customerDialogError"
                    type="error"
                    variant="tonal"
                    closable
                    class="mb-4"
                    @update:model-value="customerDialogError = null"
                  >
                    {{ customerDialogError }}
                  </v-alert>

                  <v-row>
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedCustomer.firstName"
                        @update:model-value="value => editedCustomer.firstName = toUpperCaseValue(value)"
                        label="Họ"
                        variant="outlined"
                        hint="Có thể để trống"
                        persistent-hint
                        :rules="[
                          v => !v || v.length <= 70 || 'Họ không được vượt quá 70 ký tự',
                          v => !v || /^[a-zA-ZÀ-ỹ\s]+$/.test(v) || 'Họ không được chứa ký tự đặc biệt'
                        ]"
                      ></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedCustomer.lastName"
                        @update:model-value="value => editedCustomer.lastName = toUpperCaseValue(value)"
                        label="Tên"
                        variant="outlined"
                        hint="Có thể để trống"
                        persistent-hint
                        :rules="[
                          v => !v || v.length <= 70 || 'Tên không được vượt quá 70 ký tự',
                          v => !v || /^[a-zA-ZÀ-ỹ\s]+$/.test(v) || 'Tên không được chứa ký tự đặc biệt'
                        ]"
                      ></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedCustomer.email"
                        label="Email"
                        variant="outlined"
                        hint="Có thể để trống"
                        persistent-hint
                        :rules="emailRules"
                      ></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="editedCustomer.phone"
                        label="Số điện thoại"
                        variant="outlined"
                        :rules="[
                          v => !!v || 'Vui lòng nhập số điện thoại',
                          v => /^(0|\+84)[3|5|7|8|9][0-9]{8}$/.test(v) || 'Số điện thoại không hợp lệ'
                        ]"
                      ></v-text-field>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-select
                        v-model="editedCustomer.gender"
                        label="Giới tính"
                        variant="outlined"
                        :items="genderOptions"
                        hint="Có thể để trống"
                        persistent-hint
                      ></v-select>
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-select
                        v-model="editedCustomer.membershipId"
                        :items="membershipOptions"
                        label="Loại thành viên"
                        variant="outlined"
                        disabled
                        hint="Không thể thay đổi hạng thành viên"
                        persistent-hint
                      ></v-select>
                    </v-col>

                    <template v-if="!editMode">
                      <v-col cols="12">
                        <v-divider class="mb-2"></v-divider>
                        <v-subheader>Thông tin tài khoản (nếu muốn tạo)</v-subheader>
                      </v-col>

                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="editedCustomer.username"
                          label="Tên đăng nhập"
                          variant="outlined"
                          hint="Để trống nếu không tạo tài khoản"
                          :rules="[
                            v => /^[a-zA-Z0-9_-]+$/.test(v) || 'Tên đăng nhập chỉ được chứa chữ cái, số và các ký tự . _ -',
                            v => (v && v.length >= 3 && v.length <= 50) || 'Tên đăng nhập phải có ít nhất 3 ký tự và không quá 50 ký tự'
                          ]"
                          persistent-hint
                        ></v-text-field>
                      </v-col>

                      <v-col cols="12" md="6">
                        <v-text-field
                          v-model="editedCustomer.password"
                          label="Mật khẩu"
                          variant="outlined"
                          type="password"
                          hint="Để trống nếu không tạo tài khoản"
                          :rules="[
                            v => (v && v.length >= 6) || 'Mật khẩu phải có ít nhất 6 ký tự'
                          ]"
                          persistent-hint
                        ></v-text-field>
                      </v-col>

                      <v-col cols="12" v-if="editedCustomer.username || editedCustomer.password">
                        <v-select
                          v-model="editedCustomer.roleId"
                          label="Vai trò"
                          variant="outlined"
                          :items="roleOptions"
                          :rules="[(editedCustomer.username || editedCustomer.password) ? v => !!v || 'Vui lòng chọn vai trò' : () => true]"
                        ></v-select>
                      </v-col>
                    </template>
                  </v-row>
                </v-form>

                <v-divider class="my-4"></v-divider>

                <div class="d-flex justify-end">
                  <v-btn variant="text" class="me-2" @click="closeCustomerDialog">Hủy</v-btn>
                  <v-btn
                    color="primary"
                    @click="activeCustomerTab === 'info' ? saveCustomer() : null"
                    :loading="loading"
                    v-if="activeCustomerTab === 'info'"
                  >
                    {{ editMode ? 'Cập nhật' : 'Thêm mới' }}
                  </v-btn>
                </div>
              </v-container>
            </v-window-item>

            <!-- Tab thông tin tài khoản -->
            <v-window-item value="account">
              <v-container class="pa-4" v-if="customerDetail && customerDetail.accountId && accountDetail">
                <v-skeleton-loader v-if="loading" type="article" class="my-3"></v-skeleton-loader>

                <template v-else>
                  <div class="d-flex align-center mb-4">
                    <v-avatar size="80" color="primary" class="mr-4">
                      <span class="text-h4 font-weight-medium white--text">
                        {{ getInitials(customerDetail.firstName, customerDetail.lastName) }}
                      </span>
                    </v-avatar>

                    <div>
                      <h2 class="text-h5 mb-1">{{ customerDetail.firstName }} {{ customerDetail.lastName }}</h2>
                      <div class="d-flex align-center">
                        <v-chip
                          v-if="customerDetail.gender"
                          size="small"
                          :color="customerDetail.gender === 'MALE' ? 'blue' : 'pink'"
                          text-color="white"
                          variant="flat"
                          class="mr-2"
                        >
                          {{ getGenderText(customerDetail.gender) }}
                        </v-chip>
                        <v-chip
                          size="small"
                          :color="getMembershipColor(customerDetail.membershipId)"
                          variant="flat"
                          class="mr-2"
                        >
                          {{ getMembershipName(customerDetail.membershipId) }}
                        </v-chip>
                      </div>
                    </div>
                  </div>

                  <v-divider class="mb-4"></v-divider>

                  <v-row>
                    <v-col cols="12" md="6">
                      <h3 class="text-subtitle-1 font-weight-bold mb-3">Thông tin cá nhân</h3>

                      <v-list density="compact">
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-email</v-icon>
                          </template>
                          <v-list-item-title>Email</v-list-item-title>
                          <v-list-item-subtitle>{{ customerDetail.email || 'Chưa cung cấp' }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-phone</v-icon>
                          </template>
                          <v-list-item-title>Số điện thoại</v-list-item-title>
                          <v-list-item-subtitle>{{ customerDetail.phone }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-star</v-icon>
                          </template>
                          <v-list-item-title>Điểm thưởng</v-list-item-title>
                          <v-list-item-subtitle>
                            <v-chip size="small" color="primary" variant="outlined">
                              {{ customerDetail.rewardPoint }} điểm
                            </v-chip>
                          </v-list-item-subtitle>
                        </v-list-item>
                      </v-list>
                    </v-col>

                    <v-col cols="12" md="6">
                      <h3 class="text-subtitle-1 font-weight-bold mb-3">Thông tin tài khoản</h3>

                      <v-list density="compact">
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-account</v-icon>
                          </template>
                          <v-list-item-title>Tài khoản</v-list-item-title>
                          <v-list-item-subtitle>{{ accountDetail.username }}</v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-shield-account</v-icon>
                          </template>
                          <v-list-item-title>Vai trò</v-list-item-title>
                          <v-list-item-subtitle>
                            <v-chip size="small" color="primary" class="mt-1">
                              {{ accountDetail.role.name }}
                            </v-chip>
                            <div v-if="accountDetail.role.description" class="mt-1 text-caption">
                              {{ accountDetail.role.description }}
                            </div>
                          </v-list-item-subtitle>
                        </v-list-item>

                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-account-check</v-icon>
                          </template>
                          <v-list-item-title>Trạng thái hoạt động</v-list-item-title>
                          <v-list-item-subtitle>
                            <v-chip 
                              size="small" 
                              :color="accountDetail.isActive ? 'success' : 'warning'" 
                              class="mt-1"
                            >
                              {{ accountDetail.isActive ? 'Đang hoạt động' : 'Vô hiệu hóa' }}
                            </v-chip>
                          </v-list-item-subtitle>
                        </v-list-item>
                        
                        <v-list-item>
                          <template v-slot:prepend>
                            <v-icon color="primary" class="mr-2">mdi-lock</v-icon>
                          </template>
                          <v-list-item-title>Trạng thái khóa</v-list-item-title>
                          <v-list-item-subtitle>
                            <v-chip 
                              size="small" 
                              :color="accountDetail.isLocked ? 'error' : 'success'" 
                              class="mt-1"
                            >
                              {{ accountDetail.isLocked ? 'Đã khóa' : 'Không khóa' }}
                            </v-chip>
                          </v-list-item-subtitle>
                        </v-list-item>
                      </v-list>
                    </v-col>
                  </v-row>

                  <div class="d-flex gap-2 mt-4 flex-column">
                    <v-btn
                      color="deep-purple"
                      variant="outlined"
                      @click="activeCustomerTab = 'role'; initRoleChange()"
                      class="mb-3"
                    >
                      <v-icon start>mdi-shield-edit</v-icon>
                      Đổi vai trò
                    </v-btn>

                    <v-btn
                      :color="accountDetail.isLocked ? 'success' : 'error'"
                      variant="outlined"
                      @click="toggleAccountLockStatus"
                    >
                      <v-icon start>{{ accountDetail.isLocked ? 'mdi-lock-open' : 'mdi-lock' }}</v-icon>
                      {{ accountDetail.isLocked ? 'Mở khóa tài khoản' : 'Khóa tài khoản' }}
                    </v-btn>
                  </div>
                </template>

                <v-alert v-if="!accountDetail && customerDetail && customerDetail.accountId" type="info" variant="tonal" class="my-3">
                  Đang tải thông tin tài khoản...
                </v-alert>

                <v-alert v-if="!customerDetail || !customerDetail.accountId" type="info" variant="tonal" class="my-3">
                  Khách hàng chưa có tài khoản trong hệ thống
                </v-alert>

                <v-divider class="my-4"></v-divider>

                <div class="d-flex justify-end">
                  <v-btn variant="text" @click="closeCustomerDialog">Đóng</v-btn>
                </div>
              </v-container>
            </v-window-item>

            <!-- Tab đổi mật khẩu -->
            <v-window-item value="password">
              <v-container class="pa-4">
                <v-form ref="passwordForm" @submit.prevent="savePasswordChange">
                  <v-text-field
                    v-model="passwordChange.oldPassword"
                    label="Mật khẩu cũ"
                    type="password"
                    variant="outlined"
                    :rules="[v => !!v || 'Vui lòng nhập mật khẩu cũ']"
                  ></v-text-field>

                  <v-text-field
                    v-model="passwordChange.newPassword"
                    label="Mật khẩu mới"
                    type="password"
                    variant="outlined"
                    :rules="[
                      v => !!v || 'Vui lòng nhập mật khẩu mới',
                      v => (v && v.length >= 6) || 'Mật khẩu phải có ít nhất 6 ký tự'
                    ]"
                  ></v-text-field>

                  <v-text-field
                    v-model="passwordChange.confirmPassword"
                    label="Xác nhận mật khẩu mới"
                    type="password"
                    variant="outlined"
                    :rules="[
                      v => !!v || 'Vui lòng xác nhận mật khẩu mới',
                      v => v === passwordChange.newPassword || 'Mật khẩu xác nhận không khớp'
                    ]"
                  ></v-text-field>

                  <div class="d-flex justify-end mt-4">
                    <v-btn
                      color="primary"
                      @click="savePasswordChange"
                      :loading="loading"
                    >
                      Cập nhật mật khẩu
                    </v-btn>
                  </div>
                </v-form>
              </v-container>
            </v-window-item>

            <!-- Tab đổi vai trò -->
            <v-window-item value="role">
              <v-container class="pa-4">
                <v-form ref="roleForm" @submit.prevent="saveRoleChange">
                  <v-select
                    v-model="roleChange.roleId"
                    label="Vai trò mới"
                    variant="outlined"
                    :items="roleOptions"
                    :rules="[v => !!v || 'Vui lòng chọn vai trò']"
                  ></v-select>

                  <div class="d-flex justify-end mt-4">
                    <v-btn
                      color="primary"
                      @click="saveRoleChange"
                      :loading="loading"
                    >
                      Cập nhật vai trò
                    </v-btn>
                  </div>
                </v-form>
              </v-container>
            </v-window-item>
          </v-window>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- Dialog thêm/sửa chương trình thành viên -->
    <v-dialog v-model="membershipDialog" max-width="600px" persistent>
      <v-card>
        <v-card-title class="text-h5 font-weight-bold pa-4">
          {{ editMode ? 'Chỉnh sửa chương trình thành viên' : 'Thêm chương trình thành viên mới' }}
        </v-card-title>

        <v-divider></v-divider>

        <v-card-text>
          <v-form ref="membershipForm" @submit.prevent="saveMembership">
            <!-- Hiển thị lỗi dialog -->
            <v-alert
              v-if="membershipDialogError"
              type="error"
              variant="tonal"
              closable
              class="mb-4"
              @update:model-value="membershipDialogError = null"
            >
              {{ membershipDialogError }}
            </v-alert>

            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="editedMembership.name"
                  label="Tên chương trình"
                  variant="outlined"
                  :rules="[v => !!v || 'Vui lòng nhập tên chương trình']"
                ></v-text-field>
              </v-col>

              <v-col cols="12">
                <v-textarea
                  v-model="editedMembership.description"
                  label="Mô tả"
                  variant="outlined"
                  rows="3"
                  hint="Mô tả chi tiết về chương trình thành viên"
                  persistent-hint
                ></v-textarea>
              </v-col>

              <v-col cols="12" md="6">
                <v-select
                  v-model="editedMembership.discountUnit"
                  label="Đơn vị giảm giá"
                  variant="outlined"
                  :items="[
                    { title: 'Phần trăm (%)', value: 'PERCENTAGE' },
                    { title: 'Giá trị cố định (VNĐ)', value: 'FIXED' },
                  ]"
                  :rules="[v => !!v || 'Vui lòng chọn đơn vị giảm giá']"
                ></v-select>
              </v-col>

              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="editedMembership.discountValue"
                  label="Giá trị giảm giá"
                  variant="outlined"
                  type="number"
                  min="0"
                  :rules="[
                    v => !!v || 'Vui lòng nhập giá trị giảm giá',
                    v => v >= 0 || 'Giá trị phải lớn hơn hoặc bằng 0',
                    v => editedMembership.discountUnit !== 'PERCENTAGE' || v <= 100 || 'Phần trăm không được vượt quá 100%',
                    v => editedMembership.discountUnit !== 'FIXED' || v >= 1000 || 'Giá trị phải lớn hơn 1000'
                  ]"
                ></v-text-field>
              </v-col>

              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="editedMembership.requiredPoint"
                  label="Điểm thưởng yêu cầu"
                  variant="outlined"
                  type="number"
                  min="0"
                  :rules="[
                    v => !!v || 'Vui lòng nhập số điểm thưởng yêu cầu',
                    v => v >= 0 || 'Số điểm phải lớn hơn hoặc bằng 0'
                  ]"
                ></v-text-field>
              </v-col>

              <v-col cols="12" md="6">
                <v-menu
                  v-model="dateMenu"
                  :close-on-content-click="false"
                  location="bottom"
                >
                  <template v-slot:activator="{ props }">
                    <v-text-field
                      v-model="formattedValidUntil"
                      label="Ngày hết hạn"
                      variant="outlined"
                      readonly
                      v-bind="props"
                      clearable
                      @click:clear="editedMembership.validUntil = null"
                      hint="Để trống nếu không có hạn sử dụng"
                      persistent-hint
                    ></v-text-field>
                  </template>
                  <v-date-picker
                    v-model="editedMembership.validUntil"
                    @update:model-value="dateMenu = false"
                    type="date"
                  ></v-date-picker>
                </v-menu>
              </v-col>

              <v-col cols="12" v-if="editMode">
                <v-switch
                  v-model="editedMembership.isActive"
                  label="Đang hoạt động"
                  color="success"
                  inset
                ></v-switch>
                <div class="text-caption text-medium-emphasis">
                  Chỉ hiển thị và áp dụng các chương trình đang hoạt động
                </div>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeMembershipDialog">Hủy</v-btn>
          <v-btn
            color="primary"
            @click="saveMembership"
            :loading="customerStore.loading"
          >
            {{ editMode ? 'Cập nhật' : 'Thêm mới' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog xác nhận xóa -->
    <v-dialog v-model="deleteDialog" max-width="520px" persistent>
      <v-card>
        <v-card-title class="text-h6 pa-4 bg-error text-white">
          Xác nhận xóa
        </v-card-title>

        <v-card-text class="py-4 pt-5">
          <template v-if="activeTab === 'customers'">
            Bạn có chắc chắn muốn xóa khách hàng <strong>{{ editedCustomer.firstName }} {{ editedCustomer.lastName }}</strong>?
          </template>
          <template v-else>
            Bạn có chắc chắn muốn xóa loại thành viên <strong>{{ editedMembership.name }}</strong>?
            <p class="text-medium-emphasis mt-2 text-justify">Lưu ý: Tất cả khách hàng thuộc loại thành viên này sẽ bị hạ về <strong>NEWMEM</strong>.</p>
          </template>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn variant="text" @click="closeDeleteDialog">Hủy</v-btn>
          <v-btn
            color="error"
            @click="deleteItem"
            :loading="customerStore.loading"
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
      {{ snackbar.text }}
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
import { useCustomerStore } from '@/stores/customer'
import { useMembershipStore } from '@/stores/membership'
import { useRoleStore } from '@/stores/role'
import { debounce } from 'lodash'
import { accountService } from '@/services/accountService'

const customerStore = useCustomerStore()
const membershipStore = useMembershipStore()
const roleStore = useRoleStore()

// State
const activeTab = ref('customers')
const customerPage = ref(1)
const customerSearchQuery = ref('')
const selectedMembership = ref(null)
const customerDialog = ref(false)
const membershipDialog = ref(false)
const deleteDialog = ref(false)
const editMode = ref(false)
const dateMenu = ref(false)
const activeCustomerTab = ref('info')

// Edited Items
const editedCustomer = ref({
  id: null,
  firstName: null,
  lastName: null,
  email: null,
  phone: null,
  membershipId: null,
  accountId: null,
  gender: null,
  username: null,
  password: null,
  roleId: null
})

const editedMembership = ref({
  id: null,
  name: '',
  description: null,
  validUntil: null,
  isActive: ref(true),
  discountUnit: 'PERCENTAGE',
  requiredPoint: 0,
  discountValue: 0,
})

// Default Values
const defaultCustomer = {
  id: null,
  firstName: null,
  lastName: null,
  email: null,
  phone: null,
  membershipId: null,
  accountId: null,
  gender: null,
  username: null,
  password: null,
  roleId: null
}

const defaultMembership = {
  id: null,
  name: '',
  description: null,
  requiredPoint: 0,
  discountValue: 0,
  discountUnit: 'PERCENTAGE',
  validUntil: null,
  isActive: ref(true)
}

// Forms
const customerForm = ref(null)
const membershipForm = ref(null)
const passwordForm = ref(null)
const roleForm = ref(null)

// Snackbar
const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
})

// Biến theo dõi lỗi dialog
const customerDialogError = ref(null)
const membershipDialogError = ref(null)
const passwordDialogError = ref(null)
const roleDialogError = ref(null)

// Validation rules
const emailRules = [
  v => !v || /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(v) || 'Email không hợp lệ'
]

// Customer detail
const customerDetail = ref(null)
const accountDetail = ref(null)
const loading = ref(false)

// State cho đổi mật khẩu
const passwordChange = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// State cho đổi vai trò
const roleChange = ref({
  roleId: null
})

// Headers cho bảng khách hàng
const customerHeaders = [
  { title: 'ID', key: 'id', align: 'start', sortable: true, width: '70px' },
  { title: 'Họ Tên', key: 'fullName', align: 'start', sortable: false},
  { title: 'Điểm thưởng', key: 'rewardPoint', align: 'center', sortable: true, width: '100px' },
  { title: 'Số điện thoại', key: 'phone', align: 'start', sortable: true },
  { title: 'Giới tính', key: 'gender', align: 'center', sortable: true, width: '120px' },
  { title: 'Loại thành viên', key: 'membershipId', align: 'center', sortable: false, width: '150px' },
  { title: 'Hành động', key: 'actions', align: 'center', sortable: false, width: '100px' }
]

// Computed
const membershipOptions = computed(() => {
  return membershipStore.membershipTypes.map(type => ({
    title: type.name,
    value: type.id
  }))
})

// Computed property cho khách hàng đã lọc
const filteredCustomers = computed(() => {
  let result = [...customerStore.customers]

  // Lọc theo từ khóa tìm kiếm
  if (customerSearchQuery.value) {
    const query = customerSearchQuery.value.toLowerCase()
    result = result.filter(customer =>
      (customer.firstName || '').toLowerCase().includes(query) ||
      (customer.lastName || '').toLowerCase().includes(query) ||
      (customer.email || '').toLowerCase().includes(query) ||
      (customer.phone || '').includes(query)
    )
  }

  // Lọc theo loại thành viên
  if (selectedMembership.value) {
    result = result.filter(customer => customer.membershipId === selectedMembership.value)
  }

  return result
})

// Computed property cho tổng số trang dựa trên kết quả đã lọc
const totalFilteredCustomerPages = computed(() => {
  return Math.ceil(filteredCustomers.value.length / 10) // 10 là số mục mỗi trang
})

const genderOptions = computed(() => [
  { title: 'Nam', value: 'MALE' },
  { title: 'Nữ', value: 'FEMALE' },
  { title: 'Khác', value: 'OTHER' }
])

// Danh sách vai trò từ roleStore
const roleOptions = computed(() => {
  return roleStore.roles.map(role => ({
    title: role.name,
    value: role.id
  }))
})

const formattedValidUntil = computed(() => {
  if (!editedMembership.value.validUntil) return ''
  const date = new Date(editedMembership.value.validUntil)
  return date.toLocaleDateString('vi-VN')
})

// Methods
// Load data
const loadCustomers = async () => {
  try {
    await customerStore.fetchCustomers(customerPage.value - 1, 10)
  } catch (error) {
    const detailMessage = error.response?.data?.detail || 'Không thể tải danh sách khách hàng.'
    showSnackbar(detailMessage, 'error')
  }
}

const loadMemberships = async () => {
  try {
    await membershipStore.fetchMembershipTypes()
  } catch (error) {
    const detailMessage = error.response?.data?.detail || 'Không thể tải danh sách loại thành viên.'
    showSnackbar(detailMessage, 'error')
  }
}

// Lấy danh sách vai trò
const loadRoles = () => {
  roleStore.fetchRoles(0, 100) // Lấy tất cả vai trò với kích thước lớn
}

// Helper functions
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN')
}

const getInitials = (firstName, lastName) => {
  if (!firstName && !lastName) return 'N/A'
  return ((firstName ? firstName.charAt(0) : '') + (lastName ? lastName.charAt(0) : '')).toUpperCase()
}

const getMembershipName = (membershipId) => {
  if (!membershipId) return 'Không có'
  const membership = membershipStore.membershipTypes.find(m => m.id === membershipId)
  return membership ? membership.name : 'Không xác định'
}

const getMembershipColor = (membershipId) => {
  if (!membershipId) return 'grey'

  // Đơn giản hóa, trong thực tế có thể lưu màu trong database
  const colors = ['primary', 'success', 'purple', 'orange', 'pink']
  const index = (membershipId % colors.length)
  return colors[index]
}

const getGenderText = (gender) => {
  if (!gender) return 'Không xác định'
  return gender === 'MALE' ? 'Nam' : gender === 'FEMALE' ? 'Nữ' : 'Khác'
}

// Hàm format giá trị giảm giá
const formatDiscountValue = (item) => {
  if (item.discountUnit === 'PERCENTAGE') {
    return `${item.discountValue}%`
  } else if (item.discountUnit === 'FIXED') {
    return `${item.discountValue.toLocaleString()} đ`
  }
  return `${item.discountValue}`
}

// Lấy class cho phần header của thẻ thành viên dựa vào tên
const getMembershipHeaderClass = (item) => {
  const nameLower = item.name.toLowerCase()
  if (nameLower.includes('platinum')) {
    return 'platinum-header'
  } else if (nameLower.includes('gold')) {
    return 'gold-header'
  } else if (nameLower.includes('silver')) {
    return 'silver-header'
  } else if (nameLower.includes('bronze')) {
    return 'bronze-header'
  } else {
    return 'newmem-header'
  }
}

// Lấy class cho avatar của thẻ thành viên
const getMembershipAvatarClass = (item) => {
  const nameLower = item.name.toLowerCase()
  if (nameLower.includes('platinum')) {
    return 'platinum-avatar'
  } else if (nameLower.includes('gold')) {
    return 'gold-avatar'
  } else if (nameLower.includes('silver')) {
    return 'silver-avatar'
  } else if (nameLower.includes('bronze')) {
    return 'bronze-avatar'
  } else {
    return 'newmem-avatar'
  }
}

// Tính số lượng thành viên (mẫu dữ liệu)
const getMemberCount = (membershipId) => {
  // Hardcoded cho mục đích demo, trong thực tế sẽ lấy từ API
  const memberCounts = {
    1: 120, // Hạng đồng
    2: 85,  // Hạng bạc
    3: 42,  // Hạng vàng
    4: 18   // Hạng bạch kim
  }

  return memberCounts[membershipId] || Math.floor(Math.random() * 100)
}

// Dialog controls
const openAddCustomerDialog = () => {
  editMode.value = false
  editedCustomer.value = {...defaultCustomer}
  customerDetail.value = null
  accountDetail.value = null
  activeCustomerTab.value = 'info'
  customerDialog.value = true
}

const openEditCustomerDialog = async (customer) => {
  loading.value = true
  editMode.value = true
  activeCustomerTab.value = 'info'
  customerDetail.value = null
  accountDetail.value = null

  try {
    // Lấy thông tin chi tiết khách hàng
    const response = await customerStore.fetchCustomerById(customer.id)
    customerDetail.value = response

    // Set dữ liệu cho form
    editedCustomer.value = {
      id: customerDetail.value.id,
      firstName: customerDetail.value.firstName,
      lastName: customerDetail.value.lastName,
      email: customerDetail.value.email,
      phone: customerDetail.value.phone,
      gender: customerDetail.value.gender,
      membershipId: customerDetail.value.membershipId,
      accountId: customerDetail.value.accountId
    }

    console.log(editedCustomer.value);

    // Nếu khách hàng có tài khoản liên kết, lấy thông tin tài khoản
    if (customerDetail.value.accountId) {
      try {
        const accountResponse = await accountService.getAccountById(customerDetail.value.accountId)
        accountDetail.value = accountResponse.data
        activeCustomerTab.value = 'account' // Nếu có tài khoản, mở tab thông tin tài khoản
      } catch (error) {
        console.error('Lỗi khi lấy thông tin tài khoản:', error)
        accountDetail.value = null
      }
    } else {
      accountDetail.value = null
    }

    customerDialog.value = true
  } catch (error) {
    console.error('Lỗi khi lấy thông tin khách hàng:', error)
    showSnackbar('Đã xảy ra lỗi khi tải thông tin chi tiết khách hàng', 'error')
  } finally {
    loading.value = false
  }
}

const closeCustomerDialog = () => {
  customerDialog.value = false
  // Reset các tab về ban đầu
  setTimeout(() => {
    activeCustomerTab.value = 'info'
  }, 300)
}

const openAddMembershipDialog = () => {
  editMode.value = false
  editedMembership.value = {...defaultMembership}
  membershipDialog.value = true
}

const openEditMembershipDialog = (membership) => {
  editMode.value = true
  editedMembership.value = { ...membership };
  membershipDialog.value = true;
}

const closeMembershipDialog = () => {
  membershipDialog.value = false
}

const openDeleteCustomerDialog = (customer) => {
  activeTab.value = 'customers'
  editedCustomer.value = {...customer}
  deleteDialog.value = true
}

const openDeleteMembershipDialog = (membership) => {
  activeTab.value = 'membership'
  editedMembership.value = {...membership}
  deleteDialog.value = true
}

const closeDeleteDialog = () => {
  deleteDialog.value = false
}

// Save/Delete operations
const saveCustomer = async () => {
  if (!customerForm.value) return

  const { valid } = await customerForm.value.validate()
  if (!valid) return

  try {
    // Kiểm tra tình trạng tạo tài khoản nếu đang thêm mới
    if (!editMode.value) {
      // Nếu có username/password nhưng không có roleId
      if ((editedCustomer.value.username || editedCustomer.value.password) && !editedCustomer.value.roleId) {
        showSnackbar('Vui lòng chọn vai trò cho tài khoản', 'warning')
        return
      }

      // Nếu chỉ có username hoặc chỉ có password
      if ((editedCustomer.value.username && !editedCustomer.value.password) ||
          (!editedCustomer.value.username && editedCustomer.value.password)) {
        showSnackbar('Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu', 'warning')
        return
      }
    }

    const customerData = { ...editedCustomer.value }

    // Khi cập nhật, không gửi các thông tin tài khoản
    if (editMode.value) {
      delete customerData.username
      delete customerData.password
      delete customerData.roleId

      await customerStore.updateCustomer(customerData.id, customerData)
      showSnackbar('Cập nhật thông tin khách hàng thành công', 'success')
    } else {
      await customerStore.createCustomer(customerData)
      showSnackbar('Thêm khách hàng mới thành công', 'success')
    }

    closeCustomerDialog()
    loadCustomers()
  } catch (error) {
    console.error('Lỗi khi lưu thông tin khách hàng:', error)
    const detailMessage = error.response?.data?.detail || (editMode.value ? 'Lỗi cập nhật khách hàng' : 'Lỗi tạo khách hàng mới')
    customerDialogError.value = detailMessage
  }
}

const saveMembership = async () => {
  if (!membershipForm.value) return

  const { valid } = await membershipForm.value.validate()
  if (!valid) return

  try {
    if (editMode.value) {
      await membershipStore.updateMembershipType(editedMembership.value.id, editedMembership.value)
      showSnackbar('Cập nhật loại thành viên thành công', 'success')
    } else {
      await membershipStore.createMembershipType(editedMembership.value)
      showSnackbar('Thêm loại thành viên mới thành công', 'success')
    }
    closeMembershipDialog()
    loadMemberships()
  } catch (error) {
    console.error('Lỗi khi lưu loại thành viên:', error)
    const detailMessage = error.response?.data?.detail || (editMode.value ? 'Lỗi cập nhật loại thành viên.' : 'Lỗi tạo loại thành viên mới.')
    membershipDialogError.value = detailMessage
  }
}

const deleteItem = async () => {
  try {
    if (activeTab.value === 'customers') {
      await customerStore.deleteCustomer(editedCustomer.value.id)
      showSnackbar('Xóa khách hàng thành công', 'success')
      loadCustomers()
    } else {
      await membershipStore.deleteMembershipType(editedMembership.value.id)
      showSnackbar('Xóa loại thành viên thành công', 'success')
      loadMemberships()
    }
    closeDeleteDialog()
  } catch (error) {
    console.error('Lỗi khi xóa khách hàng:', error)
    const detailMessage = error.response?.data?.detail || 'Đã xảy ra lỗi khi xóa khách hàng'
    showSnackbar(detailMessage, 'error')
  }
}

// Handle pagination
const handleCustomerPageChange = (newPage) => {
  customerPage.value = newPage
}

// Debounce search
const debounceCustomerSearch = debounce(() => {
  customerPage.value = 1 // Reset về trang đầu tiên khi tìm kiếm
}, 300)

// Show snackbar
const showSnackbar = (text, color = 'success') => {
  snackbar.text = text
  snackbar.color = color
  snackbar.show = true
}

// Lưu thay đổi mật khẩu
const savePasswordChange = async () => {
  if (!passwordForm.value) return

  const { valid } = await passwordForm.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await accountService.changePassword(
      customerDetail.value.accountId,
      passwordChange.value.oldPassword,
      passwordChange.value.newPassword,
      passwordChange.value.confirmPassword
    )
    showSnackbar('Đổi mật khẩu thành công', 'success')

    // Reset form và chuyển về tab thông tin tài khoản
    passwordChange.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
    activeCustomerTab.value = 'account'
  } catch (error) {
    console.error('Lỗi khi đổi mật khẩu:', error)
    showSnackbar('Đã xảy ra lỗi: ' + (error.response?.data?.detail || error.message), 'error')
  } finally {
    loading.value = false
  }
}

// Lưu thay đổi vai trò
const saveRoleChange = async () => {
  if (!roleForm.value) return

  const { valid } = await roleForm.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await accountService.changeRole(
      customerDetail.value.accountId,
      roleChange.value.roleId
    )

    // Cập nhật lại thông tin hiển thị
    const accountResponse = await accountService.getAccountById(customerDetail.value.accountId)
    accountDetail.value = accountResponse.data

    showSnackbar('Thay đổi vai trò thành công', 'success')
    activeCustomerTab.value = 'account'
  } catch (error) {
    console.error('Lỗi khi đổi vai trò:', error)
    showSnackbar('Đã xảy ra lỗi: ' + (error.response?.data?.detail || error.message), 'error')
  } finally {
    loading.value = false
  }
}

// Khởi tạo dữ liệu cho thay đổi vai trò
const initRoleChange = () => {
  // Đảm bảo đã tải danh sách vai trò
  if (roleStore.roles.length === 0) {
    loadRoles()
  }

  roleChange.value = {
    roleId: accountDetail.value.role.id
  }
}

// Khóa/Mở khóa tài khoản
const toggleAccountLockStatus = async () => {
  if (!customerDetail.value || !customerDetail.value.account) return
  loading.value = true
  try {
    const newLockStatus = !customerDetail.value.account.isLocked
    await customerStore.toggleAccountLock(customerDetail.value.account.id, newLockStatus)
    customerDetail.value.account.isLocked = newLockStatus
    showSnackbar(`Tài khoản đã được ${newLockStatus ? 'khóa' : 'mở khóa'}.`, 'success')
    await loadCustomers()
  } catch (error) {
    console.error('Lỗi khi thay đổi trạng thái khóa:', error)
    showSnackbar('Đã xảy ra lỗi: ' + (error.response?.data?.detail || error.message), 'error')
  } finally {
    loading.value = false
  }
}

// Helper function to convert string to uppercase
const toUpperCaseValue = (str) => {
  if (!str) return '';
  return str.toUpperCase();
};

// Lifecycle hooks
onMounted(() => {
  // Tải dữ liệu loại thành viên trước để hiển thị trong form khách hàng
  loadMemberships()
  loadCustomers()
  loadRoles()
})

// Theo dõi thay đổi tab
watch(activeTab, (newTab) => {
  if (newTab === 'customers') {
    loadCustomers()
  } else {
    loadMemberships()
  }
})
</script>

<style scoped>
/* Card styles */
.membership-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
}

.membership-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

.inactive-card {
  opacity: 0.7;
}

.membership-header {
  padding: 16px;
  color: white;
}

.newmem-header {
  background: linear-gradient(135deg, #e4a47c, #d88c64);
}

.bronze-header {
  background: linear-gradient(135deg, #cd7f32, #a05a2c);
}

.silver-header {
  background: linear-gradient(135deg, #c0c0c0, #a8a8a8);
}

.gold-header {
  background: linear-gradient(135deg, #ffd700, #ffa500);
}

.platinum-header {
  background: linear-gradient(135deg, #e5e4e2, #8a2be2);
}

.membership-avatar {
  margin-right: 12px;
  width: 40px;
  height: 40px;
}

.newmem-avatar {
  background-color: #d88c64;
}

.bronze-avatar {
  background-color: #cd7f32;
}

.silver-avatar {
  background-color: #a8a8a8;
}

.gold-avatar {
  background-color: #ffa500;
}

.platinum-avatar {
  background-color: #8a2be2;
}

.membership-benefits {
  background-color: transparent !important;
}

.status-chip {
  position: absolute;
  top: 10px;
  right: 10px;
}

.membership-description {
  color: rgba(0, 0, 0, 0.6);
  font-style: italic;
}
</style>
