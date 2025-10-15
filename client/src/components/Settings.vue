<script setup>
import { ref, computed } from 'vue'
import { useColorMode } from '@vueuse/core'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import Button from '@/components/ui/button/Button.vue'
import ModeToggle from '@/components/shared/ModeToggle.vue'

// Get color mode
const mode = useColorMode()

// Example settings state (expand as needed)
const notifications = ref(true)
const language = ref('en')

// Computed property to show current theme
const currentTheme = computed(() => {
  return mode.value.charAt(0).toUpperCase() + mode.value.slice(1)
})

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
          <div>
            <span class="font-medium">Theme</span>
            <p class="text-sm text-muted-foreground mt-1">
              Switch between light, dark, or system theme
            </p>
          </div>
          <div class="flex items-center gap-3">
            <span class="text-sm text-muted-foreground">{{ currentTheme }}</span>
            <ModeToggle />
          </div>
        </div>
        <div class="flex items-center justify-between">
          <div>
            <span class="font-medium">Notifications</span>
            <p class="text-sm text-muted-foreground mt-1">
              Receive updates and alerts
            </p>
          </div>
          <Button @click="toggleNotifications" :variant="notifications ? 'default' : 'outline'">
            {{ notifications ? 'Enabled' : 'Disabled' }}
          </Button>
        </div>
        <div class="flex items-center justify-between">
          <div>
            <span class="font-medium">Language</span>
            <p class="text-sm text-muted-foreground mt-1">
              Select your preferred language
            </p>
          </div>
          <select v-model="language" class="border rounded px-3 py-2 bg-background text-foreground border-input focus:ring-2 focus:ring-ring">
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
