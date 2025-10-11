<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from './components/Sidebar.vue'

const sidebar = ref(null)
const route = useRoute()

// Check if current route should show sidebar
const shouldShowSidebar = computed(() => {
  const noSidebarRoutes = ['/login', '/oauth-callback', '/']
  return !noSidebarRoutes.includes(route.path)
})
</script>

<template>
  <div class="flex h-screen bg-[#F5F5F7]">
    <!-- Sidebar - Only show when authenticated -->
    <Sidebar v-if="shouldShowSidebar" ref="sidebar" />
    
    <!-- Main Content -->
    <main 
      class="flex-1 overflow-y-auto transition-all duration-300"
      :class="shouldShowSidebar ? (sidebar?.isCollapsed ? 'ml-16' : 'ml-60') : 'ml-0'"
    >
      <div :class="shouldShowSidebar ? 'p-10' : ''">
        <router-view />
      </div>
    </main>
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

body {
  font-family: 'Inter', sans-serif;
  margin: 0;
  padding: 0;
}
</style>
