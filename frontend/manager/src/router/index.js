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
      name: 'products',
      component: () => import('@/views/ProductsCategories.vue'),
      meta: {
        title: 'Products & Categories'
      }
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
      //component: () => import('@/views/AreasTables.vue'),
      meta: {
        title: 'Areas & Tables'
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
