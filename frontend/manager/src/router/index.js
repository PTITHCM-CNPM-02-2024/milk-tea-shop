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

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/accounts', component: EmployeeManagementNew },
    { path: '/products', component: CategoryList }, // Hiển thị CategoryList khi vào /products
    { path: '/products/categories', component: CategoryList },
    { path: '/products/product-list', component: ProductList }, // Hiển thị ProductList khi vào /product-list
    { path: '/products/product-detail', component: ProductDetail }, // Hiển thị ProductDetail khi vào /product-detail
    { path: '/orders', component: OrderList },
    { path: '/orders/list', component: OrderList },
    { path: '/orders/payment', component: PaymentManagement },
    { path: '/customers', component: CustomerList },
    { path: '/customers/list', component: CustomerList },
    { path: '/customers/membership', component: Membership },
    { path: '/areas/list', component: AreasList },
    { path: '/areas/tables', component: Tables },
    { path: '/store', component: StoreManagement },
    { path: '/discount/list', component: DiscountList },
    { path: '/discount/promotion', component: Promotion },
  ],
})

export default router
