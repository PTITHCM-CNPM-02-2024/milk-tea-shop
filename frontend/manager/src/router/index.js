import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/views/Dashboard.vue'
import AccountManagementLayout from '@/views/AccountManagementLayout.vue'
import AccountList from '@/views/AccountList.vue'
import RoleManagement from '@/views/RoleManagement.vue'
import Login from '@/views/Login.vue'
import { authService } from '@/services/authService'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: {
        title: 'Đăng nhập',
        layout: 'blank',
        requiresGuest: true
      }
    },
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard,
      meta: {
        title: 'Dashboard',
        requiresAuth: true
      }
    },
    {
      path: '/account',
      component: AccountManagementLayout,
      meta: {
        title: 'Tài khoản'
      },
      children: [
        {
          path: '',
          redirect: '/account/list'
        },
        {
          path: 'list',
          name: 'account-list',
          component: AccountList,
          meta: {
            title: 'Quản lý tài khoản'
          }
        },
        {
          path: 'roles',
          name: 'roles',
          component: RoleManagement,
          meta: {
            title: 'Quản lý vai trò'
          }
        }
      ]
    },
    {
      path: '/products',
      component: () => import('@/views/ProductManagementLayout.vue'),
      meta: {
        title: 'Sản phẩm'
      },
      children: [
        {
          path: '',
          redirect: '/products/list'
        },
        {
          path: 'list',
          name: 'product-list',
          component: () => import('@/views/ProductList.vue'),
          meta: {
            title: 'Sản phẩm & Danh mục'
          }
        },
        {
          path: 'size-units',
          name: 'size-units',
          component: () => import('@/views/SizeUnitManagement.vue'),
          meta: {
            title: 'Kích thước & Đơn vị tính'
          }
        }
      ]
    },
    // Phần quản lý người dùng
    {
      path: '/users',
      component: () => import('@/views/UserManagementLayout.vue'),
      meta: {
      },
      children: [
        {
          path: '',
          redirect: '/users/employees'
        },
        {
          path: 'employees',
          name: 'employees',
          component: () => import('@/views/EmployeeList.vue'),
          meta: {
            title: 'Quản lý nhân viên'
          }
        },
        {
          path: 'customers',
          name: 'customers-membership',
          component: () => import('@/views/CustomersMembers.vue'),
          meta: {
            title: 'Quản lý khách hàng & CT thành viên'
          }
        }
      ]
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('@/views/OrderList.vue'),
      meta: {
        title: 'Quản lý đơn hàng',
        requiresAuth: true
      }
    },
    {
      path: '/payments',
      name: 'payments',
      component: () => import('@/views/PaymentManagement.vue'),
      meta: {
        title: 'Quản lý thanh toán',
        requiresAuth: true
      }
    },
    {
      path: '/customers',
      name: 'customers',
      //component: () => import('@/views/CustomersMembers.vue'),
      meta: {
        title: 'Customers & Members'
      }
    },
    {
      path: '/areas',
      name: 'areas',
      component: () => import('@/views/AreaTableManagement.vue'),
      meta: {
        title: 'Quản lý khu vực & bàn'
      }
    },
    {
      path: '/store',
      name: 'store',
      component: () => import('@/views/StoreInfo.vue'),
      meta: {
        title: 'Thông tin cửa hàng',
        requiresAuth: true
      }
    },
    {
      path: '/discounts',
      name: 'discounts',
      component: () => import('@/views/DiscountManagement.vue'),
      meta: {
        title: 'Quản lý khuyến mãi',
        requiresAuth: true
      }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/Profile/ProfileView.vue'),
      meta: {
        title: 'Hồ sơ cá nhân',
        requiresAuth: true,
        layout: 'default'
      }
    }
  ]
})

// Navigation Guards
router.beforeEach((to, from, next) => {
  const isAuthenticated = authService.isAuthenticated()
  
  // Cập nhật tiêu đề trang
  document.title = to.meta.title ? `${to.meta.title} - Milk Tea Shop` : 'Milk Tea Shop'
  
  // Kiểm tra route yêu cầu đăng nhập
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
      next({ name: 'login' })
    } else {
      next()
    }
  } 
  // Kiểm tra route dành cho khách (chưa đăng nhập)
  else if (to.matched.some(record => record.meta.requiresGuest)) {
    if (isAuthenticated) {
      // Chuyển hướng đến trang dashboard nếu đã đăng nhập
      next({ name: 'dashboard' })
    } else {
      next()
    }
  } else {
    // Route không có yêu cầu xác thực, cho phép truy cập
    next()
  }
})

export default router
