<script setup>
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import Button from '@/components/ui/button/Button.vue'

const { locale, t } = useI18n()

// Settings state
const darkMode = ref(false)
const notifications = ref(true)
const language = ref(localStorage.getItem('language') || 'en')

// Set initial locale
locale.value = language.value

function toggleDarkMode() {
  darkMode.value = !darkMode.value
}

function toggleNotifications() {
  notifications.value = !notifications.value
}

// Watch language changes and update locale
watch(language, (newLang) => {
  locale.value = newLang
  localStorage.setItem('language', newLang)
})
</script>

<template>
  <div class="flex justify-center py-10">
    <Card class="w-full max-w-xl">
      <CardHeader>
        <CardTitle>{{ $t('settings.title') }}</CardTitle>
      </CardHeader>
      <CardContent class="flex flex-col gap-6">
        <div class="flex items-center justify-between">
          <span class="font-medium">{{ $t('settings.darkMode') }}</span>
          <Button @click="toggleDarkMode" :variant="darkMode ? 'default' : 'outline'">
            {{ darkMode ? $t('settings.on') : $t('settings.off') }}
          </Button>
        </div>
        <div class="flex items-center justify-between">
          <span class="font-medium">{{ $t('settings.notifications') }}</span>
          <Button @click="toggleNotifications" :variant="notifications ? 'default' : 'outline'">
            {{ notifications ? $t('settings.enabled') : $t('settings.disabled') }}
          </Button>
        </div>
        <div class="flex items-center justify-between">
          <span class="font-medium">{{ $t('settings.language') }}</span>
          <select v-model="language" class="border rounded px-2 py-1 bg-background text-foreground">
            <option value="en">English</option>
            <option value="es">Español</option>
            <option value="fr">Français</option>
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
