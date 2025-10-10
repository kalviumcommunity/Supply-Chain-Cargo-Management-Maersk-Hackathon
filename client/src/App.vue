<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from './components/Sidebar.vue'

const sidebar = ref(null)
const route = useRoute()

// Check if current route should hide the sidebar
const shouldHideSidebar = computed(() => {
  return route.meta?.hideNavbar === true
})
</script>

<template>
  <div class="flex h-screen bg-[#F5F5F7]">
    <!-- Sidebar - only show on dashboard pages -->
    <Sidebar v-if="!shouldHideSidebar" ref="sidebar" />
    
    <!-- Main Content -->
    <main 
      class="flex-1 overflow-y-auto transition-all duration-300"
      :class="shouldHideSidebar ? '' : (sidebar?.isCollapsed ? 'ml-16' : 'ml-60')"
    >
      <div :class="shouldHideSidebar ? '' : 'p-10'">
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
