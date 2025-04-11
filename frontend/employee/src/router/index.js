import {createRouter, createWebHistory} from "vue-router";

// Sử dụng import động để tải lazy các components
const PosView = () => import('../views/PosView.vue')
const ReportsView = () => import('../views/ReportsView.vue')
const OrdersView = () => import('../views/OrdersView.vue')
const UserProfileView = () => import('../views/UserProfileView.vue')
const routes = [
    {
        path : '/',
        name : 'pos',
        component : PosView
    },
    {
        path : '/orders',
        name : 'Orders',
        component : OrdersView
    },
    {
        path : '/reports',
        name : 'reports',
        component : ReportsView
    },
    {
        path: '/profile',
        name: 'profile',
        component: UserProfileView
    },
    // Thêm fallback route
    {
        path: '/:pathMatch(.*)*',
        redirect: '/'
    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL || '/'),
    routes
})

export default router