<template>
  <aside :class="[
    'fixed left-0 top-0 h-screen bg-white border-r border-[#E5E7EB] z-50 transition-all duration-500 ease-[cubic-bezier(0.4,0,0.2,1)]',
    'shadow-[0_10px_40px_-10px_rgba(0,0,0,0.08)]',
    isCollapsed ? 'w-16' : 'w-60'
  ]">
    <!-- Premium Brand Header -->
    <div class="h-20 px-6 flex items-center border-b border-[#F3F4F6] bg-gradient-to-r from-[#FAFAFA] to-white">
      <div class="flex items-center gap-3">
        <!-- Premium Logo Container -->
        <div class="w-9 h-9 bg-gradient-to-br from-[#3B82F6] via-[#2563EB] to-[#1E40AF] rounded-xl shadow-[0_4px_12px_rgba(59,130,246,0.3)] flex items-center justify-center">
          <span class="text-white font-bold text-sm tracking-wide drop-shadow-sm">CF</span>
        </div>
        
        <!-- Brand Text with Sophisticated Typography -->
        <div v-show="!isCollapsed" class="transition-all duration-500 ease-[cubic-bezier(0.4,0,0.2,1)]">
          <h1 class="text-[#0F172A] font-bold text-lg tracking-[-0.02em] leading-none">CargoFlow</h1>
          <p class="text-[#64748B] text-xs font-medium mt-0.5 tracking-[0.01em]">Supply Chain Management</p>
        </div>
      </div>
    </div>

    <!-- Premium Navigation -->
    <nav class="p-4 space-y-1">
      <div v-show="!isCollapsed" class="text-xs font-semibold text-[#94A3B8] uppercase tracking-[0.05em] px-3 py-3 transition-all duration-500">
        Navigation
      </div>
      
      <router-link 
        v-for="link in navigationLinks" 
        :key="link.path"
        :to="link.path"
        :class="[
          'group flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-xl transition-all duration-200 ease-out relative overflow-hidden',
          'active:scale-[0.98]',
          $route.path === link.path 
            ? 'bg-gradient-to-r from-[#3B82F6] to-[#2563EB] text-white shadow-[0_4px_16px_rgba(59,130,246,0.25)]'
            : 'text-[#64748B] hover:text-[#334155] hover:bg-gradient-to-r hover:from-[#EFF6FF] hover:to-[#F0F9FF] hover:shadow-[0_2px_8px_rgba(59,130,246,0.08)]',
          { 'justify-center': isCollapsed }
        ]"
        :title="isCollapsed ? link.label : ''"
      >
        <!-- Premium Active Indicator -->
        <div 
          v-if="$route.path === link.path" 
          class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-white rounded-r-full shadow-sm"
        ></div>
        
        <!-- Enhanced Icon Container -->
        <div :class="[
          'w-5 h-5 flex items-center justify-center transition-all duration-200',
          $route.path === link.path 
            ? 'text-white drop-shadow-sm' 
            : 'text-[#64748B] group-hover:text-[#3B82F6] group-hover:scale-110'
        ]">
          <component :is="link.icon" class="w-5 h-5" />
        </div>
        
        <!-- Sophisticated Menu Text -->
        <span v-show="!isCollapsed" :class="[
          'transition-all duration-200 tracking-[-0.01em]',
          $route.path === link.path 
            ? 'text-white drop-shadow-sm' 
            : 'text-[#334155] group-hover:text-[#1E293B]'
        ]">
          {{ link.label }}
        </span>
        
        <!-- Premium Badge -->
        <span v-if="link.badge && !isCollapsed" :class="[
          'ml-auto px-2 py-0.5 text-xs font-semibold rounded-full transition-all duration-200',
          $route.path === link.path
            ? 'bg-white/20 text-white backdrop-blur-sm'
            : 'bg-[#EF4444] text-white shadow-[0_2px_8px_rgba(239,68,68,0.25)]'
        ]">
          {{ link.badge }}
        </span>
        
        <!-- Premium Hover Glow Effect -->
        <div 
          v-if="$route.path !== link.path"
          class="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-200 bg-gradient-to-r from-[#3B82F6]/5 to-[#2563EB]/5 rounded-xl"
        ></div>
      </router-link>

      <div v-show="!isCollapsed" class="text-xs font-semibold text-[#94A3B8] uppercase tracking-[0.05em] px-3 py-3 mt-6 transition-all duration-500">
        Tools
      </div>
      
      <router-link 
        v-for="link in toolLinks" 
        :key="link.path"
        :to="link.path"
        :class="[
          'group flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-xl transition-all duration-200 ease-out relative overflow-hidden',
          'active:scale-[0.98]',
          $route.path === link.path 
            ? 'bg-gradient-to-r from-[#3B82F6] to-[#2563EB] text-white shadow-[0_4px_16px_rgba(59,130,246,0.25)]'
            : 'text-[#64748B] hover:text-[#334155] hover:bg-gradient-to-r hover:from-[#EFF6FF] hover:to-[#F0F9FF] hover:shadow-[0_2px_8px_rgba(59,130,246,0.08)]',
          { 'justify-center': isCollapsed }
        ]"
        :title="isCollapsed ? link.label : ''"
      >
        <!-- Premium Active Indicator -->
        <div 
          v-if="$route.path === link.path" 
          class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-white rounded-r-full shadow-sm"
        ></div>
        
        <!-- Enhanced Icon Container -->
        <div :class="[
          'w-5 h-5 flex items-center justify-center transition-all duration-200',
          $route.path === link.path 
            ? 'text-white drop-shadow-sm' 
            : 'text-[#64748B] group-hover:text-[#3B82F6] group-hover:scale-110'
        ]">
          <component :is="link.icon" class="w-5 h-5" />
        </div>
        
        <!-- Sophisticated Menu Text -->
        <span v-show="!isCollapsed" :class="[
          'transition-all duration-200 tracking-[-0.01em]',
          $route.path === link.path 
            ? 'text-white drop-shadow-sm' 
            : 'text-[#334155] group-hover:text-[#1E293B]'
        ]">
          {{ link.label }}
        </span>
        
        <!-- Premium Hover Glow Effect -->
        <div 
          v-if="$route.path !== link.path"
          class="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-200 bg-gradient-to-r from-[#3B82F6]/5 to-[#2563EB]/5 rounded-xl"
        ></div>
      </router-link>
    </nav>

    <!-- Sophisticated Toggle Button -->
    <button 
      @click="toggleSidebar"
      class="absolute -right-3 top-24 w-7 h-7 bg-white border border-[#E2E8F0] rounded-full flex items-center justify-center text-[#64748B] hover:text-[#475569] hover:border-[#CBD5E1] hover:shadow-md active:scale-95 transition-all duration-200 shadow-sm group"
    >
      <ChevronLeft v-if="!isCollapsed" class="w-3.5 h-3.5 transition-transform duration-200 group-hover:scale-110" />
      <ChevronRight v-else class="w-3.5 h-3.5 transition-transform duration-200 group-hover:scale-110" />
    </button>

    <!-- Premium User Profile Section -->
    <div class="absolute bottom-0 left-0 right-0 p-4 border-t border-[#E5E7EB]">
      <div class="relative">
        <!-- User Profile Card -->
        <div 
          @click="toggleUserMenu"
          :class="[
            'flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-white to-gray-50',
            'border border-gray-200 hover:border-blue-200 hover:shadow-lg',
            'transition-all duration-300 ease-out cursor-pointer group',
            { 'justify-center px-2': isCollapsed }
          ]"
        >
          <!-- User Information -->
          <div v-show="!isCollapsed" class="flex-1 min-w-0">
            <p class="text-gray-900 font-semibold text-sm truncate leading-tight">{{ displayName }}</p>
            <p class="text-gray-500 text-xs font-medium truncate mt-0.5 leading-tight">{{ displayRole }}</p>
          </div>
          
          <!-- Dropdown Arrow -->
          <button 
            v-show="!isCollapsed" 
            class="flex-shrink-0 text-gray-400 group-hover:text-gray-600 transition-colors"
          >
            <svg 
              class="w-4 h-4 transition-transform duration-200" 
              :class="{ 'rotate-180': showUserMenu }"
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </button>
        </div>

        <!-- User Menu Dropdown -->
        <div 
          v-if="showUserMenu && !isCollapsed"
          class="absolute bottom-full left-0 right-0 mb-2 bg-white rounded-xl shadow-xl border border-gray-200 overflow-hidden animate-in fade-in slide-in-from-bottom-2 duration-200"
        >
          <!-- User Info Header -->
          <div class="px-4 py-3 border-b border-gray-100 bg-gray-50">
            <p class="text-sm font-semibold text-gray-900 truncate">{{ displayName }}</p>
            <p class="text-xs text-gray-500 truncate mt-0.5">{{ auth.userEmail.value }}</p>
          </div>
          
          <!-- Logout Button -->
          <button
            @click="handleLogout"
            class="w-full flex items-center gap-3 px-4 py-3 text-left text-sm font-medium text-red-600 hover:bg-red-50 transition-colors"
          >
            <LogOut class="w-4 h-4" />
            <span>Logout</span>
          </button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
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
  ChevronRight,
  LogOut
} from 'lucide-vue-next'
import { useAuth } from '../services/auth'

const router = useRouter()
const auth = useAuth()
const isCollapsed = ref(false)
const showUserMenu = ref(false)

const navigationLinks = [
  { path: '/', label: 'Dashboard', icon: Home },
  { path: '/cargo', label: 'Cargo Management', icon: Package },
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

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const handleLogout = async () => {
  await auth.logout()
  router.push('/login')
}

// Get user avatar (picture or initials)
const userAvatar = computed(() => {
  return auth.userPicture.value || null
})

const displayName = computed(() => {
  return auth.userName.value || 'User'
})

const displayRole = computed(() => {
  return auth.userRole.value || 'User'
})

const displayInitials = computed(() => {
  return auth.userInitials.value || 'U'
})

defineExpose({ isCollapsed })
</script>