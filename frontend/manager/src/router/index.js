import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/views/Dashboard.vue'
import AccountManagementLayout from '@/views/AccountManagementLayout.vue'
import AccountList from '@/views/AccountList.vue'
import RoleManagement from '@/views/RoleManagement.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard,
      meta: {
        title: 'Dashboard'
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
    {
      path: '/orders',
      name: 'orders',
      //component: () => import('@/views/OrdersPayments.vue'),
      meta: {
        title: 'Orders & Payments'
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
      //component: () => import('@/views/StoreManagement.vue'),
      meta: {
        title: 'Store Management'
      }
    },
    {
      path: '/discounts',
      name: 'discounts',
      //component: () => import('@/views/DiscountsPromotions.vue'),
      meta: {
        title: 'Discounts & Promotions'
      }
    }
  ]
})

export default router
