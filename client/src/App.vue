<script setup>
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'

const sidebar = ref(null)
</script>

<template>
  <div class="flex h-screen bg-[#F5F5F7]">
    <!-- Sidebar -->
    <Sidebar ref="sidebar" />
    
    <!-- Main Content -->
    <main 
      class="flex-1 overflow-y-auto transition-all duration-300"
      :class="sidebar?.isCollapsed ? 'ml-16' : 'ml-60'"
    >
      <div class="p-10">
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
