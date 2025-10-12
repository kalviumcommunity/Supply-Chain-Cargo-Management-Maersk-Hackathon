<template>
  <div class="app-container">
    <!-- Show Sidebar only for authenticated routes -->
    <Sidebar v-if="shouldShowSidebar" ref="sidebarRef" />
    
    <!-- Main Content Area -->
    <main 
      :class="[
        'main-content',
        shouldShowSidebar ? (isSidebarCollapsed ? 'ml-16' : 'ml-60') : 'ml-0',
        'transition-all duration-300 ease-in-out',
        'min-h-screen bg-gradient-to-br from-[#F8FAFC] via-[#F1F5F9] to-[#E2E8F0]'
      ]"
    >
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition 
            name="page-fade" 
            mode="out-in"
            appear
          >
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from './components/Sidebar.vue'
import { useAuth } from './services/auth'

const route = useRoute()
const auth = useAuth()
const sidebarRef = ref(null)

// Determine if sidebar should be shown
const shouldShowSidebar = computed(() => {
  // Don't show sidebar on landing page, login, or oauth callback
  const excludedRoutes = ['/', '/login', '/oauth-callback']
  return !excludedRoutes.includes(route.path) && auth.isAuthenticated.value
})

// Track sidebar collapsed state
const isSidebarCollapsed = computed(() => {
  return sidebarRef.value?.isCollapsed || false
})

// Watch for route changes and scroll to top
watch(route, () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap');

* {
  box-sizing: border-box;
}

body {
  font-family: 'Inter', sans-serif;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.app-container {
  min-height: 100vh;
  position: relative;
}

.main-content {
  position: relative;
  z-index: 1;
}

.content-wrapper {
  padding: 2rem;
  max-width: 1920px;
  margin: 0 auto;
}

/* Page transitions */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Responsive adjustments */
@media (max-width: 1024px) {
  .content-wrapper {
    padding: 1.5rem;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 1rem;
  }
  
  .main-content {
    margin-left: 0 !important;
  }
}

/* Custom scrollbar styling */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Smooth transitions for interactive elements */
button, a, input, select, textarea {
  transition: all 0.2s ease-in-out;
}

/* Focus visible styles for accessibility */
*:focus-visible {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}
</style>
