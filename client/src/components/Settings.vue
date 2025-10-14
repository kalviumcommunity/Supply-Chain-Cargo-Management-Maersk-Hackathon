<script setup>
import { ref } from 'vue'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import Button from '@/components/ui/button/Button.vue'

// Example settings state (expand as needed)
const darkMode = ref(false)
const notifications = ref(true)
const language = ref('en')

function toggleDarkMode() {
  darkMode.value = !darkMode.value
}
function toggleNotifications() {
  notifications.value = !notifications.value
}
</script>

<template>
  <div class="flex justify-center py-10">
    <Card class="w-full max-w-xl">
      <CardHeader>
        <CardTitle>Settings</CardTitle>
      </CardHeader>
      <CardContent class="flex flex-col gap-6">
        <div class="flex items-center justify-between">
          <span class="font-medium">Dark Mode</span>
          <Button @click="toggleDarkMode" :variant="darkMode ? 'default' : 'outline'">
            {{ darkMode ? 'On' : 'Off' }}
          </Button>
        </div>
        <div class="flex items-center justify-between">
          <span class="font-medium">Notifications</span>
          <Button @click="toggleNotifications" :variant="notifications ? 'default' : 'outline'">
            {{ notifications ? 'Enabled' : 'Disabled' }}
          </Button>
        </div>
        <div class="flex items-center justify-between">
          <span class="font-medium">Language</span>
          <select v-model="language" class="border rounded px-2 py-1 bg-background text-foreground">
            <option value="en">English</option>
            <option value="es">Spanish</option>
            <option value="fr">French</option>
          </select>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<style scoped>
select:focus {
  outline: 2px solid var(--primary);
}
</style>
