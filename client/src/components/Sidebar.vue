<template>
  <aside :class="[
    'fixed left-0 top-0 h-screen bg-white border-r border-gray-200 z-50 transition-all duration-300',
    isCollapsed ? 'w-16' : 'w-60'
  ]">
    <!-- Brand Header -->
    <div class="h-20 px-6 flex items-center border-b border-gray-100">
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
          CF
        </div>
        <div v-show="!isCollapsed" class="transition-opacity duration-200">
          <h1 class="text-lg font-semibold text-gray-900">CargoFlow</h1>
          <p class="text-xs text-gray-500">Supply Chain Management</p>
        </div>
      </div>
    </div>

    <!-- Navigation -->
    <nav class="p-4 space-y-1">
      <div v-show="!isCollapsed" class="text-xs font-medium text-gray-400 uppercase tracking-wide px-3 py-2 transition-opacity duration-200">
        Navigation
      </div>
      
      <router-link 
        v-for="link in navigationLinks" 
        :key="link.path"
        :to="link.path"
        :class="[
          'flex items-center gap-3 px-3 py-2 text-sm font-medium text-gray-600 rounded-lg hover:bg-gray-50 hover:text-gray-900 hover:scale-105 transition-all duration-200',
          { 'bg-blue-50 text-blue-700 border-l-4 border-blue-500 ml-0 pl-2': $route.path === link.path },
          { 'justify-center': isCollapsed }
        ]"
        :title="isCollapsed ? link.label : ''"
      >
        <component :is="link.icon" class="w-5 h-5 flex-shrink-0" />
        <span v-show="!isCollapsed" class="transition-opacity duration-200">{{ link.label }}</span>
        <span v-if="link.badge && !isCollapsed" class="ml-auto bg-red-500 text-white text-xs rounded-full px-2 py-0.5 transition-opacity duration-200">
          {{ link.badge }}
        </span>
      </router-link>

      <div v-show="!isCollapsed" class="text-xs font-medium text-gray-400 uppercase tracking-wide px-3 py-2 mt-6 transition-opacity duration-200">
        Tools
      </div>
      
      <router-link 
        v-for="link in toolLinks" 
        :key="link.path"
        :to="link.path"
        :class="[
          'flex items-center gap-3 px-3 py-2 text-sm font-medium text-gray-600 rounded-lg hover:bg-gray-50 hover:text-gray-900 hover:scale-105 transition-all duration-200',
          { 'bg-blue-50 text-blue-700 border-l-4 border-blue-500 ml-0 pl-2': $route.path === link.path },
          { 'justify-center': isCollapsed }
        ]"
        :title="isCollapsed ? link.label : ''"
      >
        <component :is="link.icon" class="w-5 h-5 flex-shrink-0" />
        <span v-show="!isCollapsed" class="transition-opacity duration-200">{{ link.label }}</span>
      </router-link>
    </nav>

    <!-- Collapse Toggle Button -->
    <button 
      @click="toggleSidebar"
      class="absolute -right-3 top-24 w-6 h-6 bg-white border border-gray-200 rounded-full flex items-center justify-center text-gray-500 hover:text-gray-700 hover:scale-110 transition-all duration-200 shadow-md"
    >
      <ChevronLeft v-if="!isCollapsed" class="w-3 h-3" />
      <ChevronRight v-else class="w-3 h-3" />
    </button>

    <!-- User Profile -->
    <div class="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-100 bg-gray-50">
      <div :class="['flex items-center gap-3', { 'justify-center': isCollapsed }]">
        <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center text-white text-sm font-semibold">
          JS
        </div>
        <div v-show="!isCollapsed" class="flex-1 min-w-0 transition-opacity duration-200">
          <p class="text-sm font-medium text-gray-900 truncate">James Sullivan</p>
          <p class="text-xs text-gray-500 truncate">Admin User</p>
        </div>
        <button v-show="!isCollapsed" class="text-gray-400 hover:text-gray-600 hover:scale-110 transition-all duration-200">
          <MoreHorizontal class="w-4 h-4" />
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref } from 'vue'
import { 
  Home, 
  Package, 
  Truck, 
  Map, 
  Users,
  BarChart3,
  Settings,
  MoreHorizontal,
  ChevronLeft,
  ChevronRight
} from 'lucide-vue-next'

const isCollapsed = ref(false)

const navigationLinks = [
  { path: '/', label: 'Dashboard', icon: Home },
  { path: '/cargo', label: 'Cargo Management', icon: Package, badge: 3 },
  { path: '/shipments', label: 'Shipment Tracking', icon: Truck },
  { path: '/routes', label: 'Route Management', icon: Map },
  { path: '/vendors', label: 'Vendor Coordination', icon: Users }
]

const toolLinks = [
  { path: '/analytics', label: 'Analytics', icon: BarChart3 },
  { path: '/settings', label: 'Settings', icon: Settings }
]

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

defineExpose({ isCollapsed })
</script>