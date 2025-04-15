import {createRouter, createWebHistory} from "vue-router";
import AuthService from "../services/auth.service";

// Sử dụng import động để tải lazy các components
const PosView = () => import('../views/PosView.vue')
const ReportsView = () => import('../views/ReportsView.vue')
const OrdersView = () => import('../views/OrdersView.vue')
const UserProfileView = () => import('../views/UserProfileView.vue')
const LoginView = () => import('../views/LoginView.vue')

const routes = [
    {
        path : '/login',
        name : 'login',
        component : LoginView,
        meta: {
            requiresAuth: false,
            hideForAuth: true
        }
    },
    {
        path : '/',
        name : 'pos',
        component : PosView,
        meta: {
            requiresAuth: true
        }
    },
    {
        path : '/orders',
        name : 'Orders',
        component : OrdersView,
        meta: {
            requiresAuth: true
        }
    },
    {
        path : '/reports',
        name : 'reports',
        component : ReportsView,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/profile',
        name: 'profile',
        component: UserProfileView,
        meta: {
            requiresAuth: true
        }
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

// Bảo vệ route yêu cầu xác thực
router.beforeEach((to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const hideForAuth = to.matched.some(record => record.meta.hideForAuth);
    const isAuthenticated = AuthService.isAuthenticated();
    
    if (requiresAuth && !isAuthenticated) {
        // Chuyển hướng đến trang đăng nhập nếu chưa xác thực
        next({ name: 'login' });
    } else if (hideForAuth && isAuthenticated) {
        // Chuyển hướng đến trang chính nếu đã xác thực nhưng cố truy cập trang đăng nhập
        next({ name: 'pos' });
    } else {
        // Cho phép truy cập
        next();
    }
});

export default router