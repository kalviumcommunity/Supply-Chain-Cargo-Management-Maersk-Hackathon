import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '../components/LandingPage.vue'
import Dashboard from '../components/Dashboard.vue'
import CargoManagement from '../components/CargoManagement.vue'
import ShipmentTracking from '../components/ShipmentTracking.vue'
import RouteManagement from '../components/RouteManagement.vue'
import VendorManagement from '../components/VendorManagement.vue'
import Login from '../components/Login.vue'
import OAuthCallback from '../components/OAuthCallback.vue'
import { useAuth } from '../services/auth'

// Create placeholder components for other routes
const Analytics = { template: '<div class="p-8"><h1 class="text-2xl font-bold mb-4">Analytics</h1><p>View analytics and reports.</p></div>' }
const Settings = { template: '<div class="p-8"><h1 class="text-2xl font-bold mb-4">Settings</h1><p>Configure application settings.</p></div>' }

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: 'Login',
      requiresAuth: false,
      hideForAuth: true // Hide from authenticated users
    }
  },
  {
    path: '/oauth-callback',
    name: 'OAuthCallback',
    component: OAuthCallback,
    meta: {
      title: 'Completing Sign In',
      requiresAuth: false
    }
  },
  {
    path: '/',
    name: 'LandingPage',
    component: LandingPage,
    meta: {
      title: 'CargoFlow - Supply Chain Management',
      hideNavbar: true // Hide main navbar on landing page since it has its own navigation
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: {
      title: 'Dashboard',
      subtitle: 'Welcome back! Here\'s your supply chain overview.',
      requiresAuth: true
    }
  },
  {
    path: '/cargo',
    name: 'CargoManagement',
    component: CargoManagement,
    meta: {
      title: 'Cargo Management',
      subtitle: 'Manage and track your cargo inventory',
      requiresAuth: true
    }
  },
  {
    path: '/shipments',
    name: 'ShipmentTracking',
    component: ShipmentTracking,
    meta: {
      title: 'Shipment Tracking',
      subtitle: 'Monitor shipment status and delivery progress',
      requiresAuth: true
    }
  },
  {
    path: '/routes',
    name: 'RouteManagement',
    component: RouteManagement,
    meta: {
      title: 'Route Management',
      subtitle: 'Plan and optimize shipping routes',
      requiresAuth: true
    }
  },
  {
    path: '/vendors',
    name: 'VendorManagement',
    component: VendorManagement,
    meta: {
      title: 'Vendor Management',
      subtitle: 'Manage vendor relationships and partnerships',
      requiresAuth: true
    }
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: Analytics,
    meta: {
      title: 'Analytics',
      subtitle: 'View performance metrics and insights',
      requiresAuth: true
    }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: {
      title: 'Settings',
      subtitle: 'Configure your application preferences',
      requiresAuth: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // Scroll behavior for smooth navigation
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// Navigation guards for authentication and tracking
router.beforeEach(async (to, from, next) => {
  // Update document title
  if (to.meta.title) {
    document.title = `${to.meta.title} - CargoFlow`
  }
  
  const auth = useAuth()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const hideForAuth = to.matched.some(record => record.meta.hideForAuth)
  
  // If not authenticated and trying to fetch current user hasn't been done yet
  if (!auth.isAuthenticated.value && !auth.user.value) {
    try {
      // Try to get current user from session (for page refreshes)
      await auth.getCurrentUser()
    } catch (err) {
      // User not authenticated
      console.log('No active session')
    }
  }
  
  // Check if route requires authentication
  if (requiresAuth && !auth.isAuthenticated.value) {
    // Redirect to login page
    next('/login')
  } 
  // Check if authenticated user trying to access login page
  else if (hideForAuth && auth.isAuthenticated.value) {
    // Redirect to dashboard
    next('/dashboard')
  } 
  else {
    next()
  }
})

export default router