import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../components/Dashboard.vue'
import CargoManagement from '../components/CargoManagement.vue'
import ShipmentTracking from '../components/ShipmentTracking.vue'
import RouteManagement from '../components/RouteManagement.vue'
import VendorCoordination from '../components/VendorCoordination.vue'

// Create placeholder components for other routes
const Analytics = { template: '<div class="p-8"><h1 class="text-2xl font-bold mb-4">Analytics</h1><p>View analytics and reports.</p></div>' }
const Settings = { template: '<div class="p-8"><h1 class="text-2xl font-bold mb-4">Settings</h1><p>Configure application settings.</p></div>' }

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: {
      title: 'Dashboard',
      subtitle: 'Welcome back! Here\'s your supply chain overview.'
    }
  },
  {
    path: '/cargo',
    name: 'CargoManagement',
    component: CargoManagement,
    meta: {
      title: 'Cargo Management',
      subtitle: 'Manage and track your cargo inventory'
    }
  },
  {
    path: '/shipments',
    name: 'ShipmentTracking',
    component: ShipmentTracking,
    meta: {
      title: 'Shipment Tracking',
      subtitle: 'Monitor shipment status and delivery progress'
    }
  },
  {
    path: '/routes',
    name: 'RouteManagement',
    component: RouteManagement,
    meta: {
      title: 'Route Management',
      subtitle: 'Plan and optimize shipping routes'
    }
  },
  {
    path: '/vendors',
    name: 'VendorCoordination',
    component: VendorCoordination,
    meta: {
      title: 'Vendor Coordination',
      subtitle: 'Manage vendor relationships and partnerships'
    }
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: Analytics,
    meta: {
      title: 'Analytics',
      subtitle: 'View performance metrics and insights'
    }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: {
      title: 'Settings',
      subtitle: 'Configure your application preferences'
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

// Navigation guards for analytics and user tracking
router.beforeEach((to, from, next) => {
  // Update document title
  if (to.meta.title) {
    document.title = `${to.meta.title} - CargoFlow`
  }
  
  // You can add authentication checks here
  // if (requiresAuth && !isAuthenticated) {
  //   next('/login')
  // } else {
  //   next()
  // }
  
  next()
})

export default router