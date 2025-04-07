import { createRouter, createWebHistory } from 'vue-router'
import EmployeeManagementNew from '@/components/EmployeeManagementNew.vue'
import CategoryList from '@/components/products/CategoryList.vue'
import ProductList from '@/components/products/ProductList.vue'
import ProductDetail from '@/components/products/ProductDetail.vue'
import OrderList from '@/orders & payment/OrderList.vue'
import PaymentManagement from '@/orders & payment/PaymentManagement.vue'
import CustomerList from '@/customersmembers/CustomerList.vue'
import Membership from '@/customersmembers/Membership.vue'
import AreasList from '@/components/areastables/AreasList.vue'
import Tables from '@/components/areastables/Tables.vue'
import StoreManagement from '@/components/storemanagement/Store.vue'
import DiscountList from '@/components/discountpromotion/DiscountList.vue'
import Promotion from '@/components/discountpromotion/Promotion.vue'

// Tạo đối tượng router với cấu hình đường dẫn và lịch sử web
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [

    {
      path : '/',
      name : 'home',
      redirectTo : '/accounts'
    },
    // Quản lý nhân viên
    { path: '/accounts', component: EmployeeManagementNew },

    // Quản lý sản phẩm và danh mục
    { path: '/products', component: CategoryList },
    { path: '/products/categories', component: CategoryList },
    { path: '/products/product-list', component: ProductList },
    { path: '/products/product-detail', component: ProductDetail },

    // Quản lý đơn hàng và thanh toán
    { path: '/orders', component: OrderList },
    { path: '/orders/list', component: OrderList },
    { path: '/orders/payment', component: PaymentManagement },

    // Quản lý khách hàng và thành viên
    { path: '/customers', component: CustomerList },
    { path: '/customers/list', component: CustomerList },
    { path: '/customers/membership', component: Membership },

    // Quản lý khu vực và bàn
    { path: '/areas/list', component: AreasList },
    { path: '/areas/tables', component: Tables },

    // Quản lý cửa hàng
    { path: '/store', component: StoreManagement },

    // Quản lý khuyến mãi và giảm giá
    { path: '/discount/list', component: DiscountList },
    { path: '/discount/promotion', component: Promotion },
  ],
})

export default router
