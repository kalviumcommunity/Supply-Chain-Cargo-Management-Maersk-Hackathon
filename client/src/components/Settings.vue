<script setup>
import { ref } from 'vue'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import Button from '@/components/ui/button/Button.vue'
// import { useDarkMode } from '@/composables/useDarkMode'
import { notificationApi } from '@/services/api'

// const { isDark, toggleDarkMode } = useDarkMode()

// Example settings state (expand as needed)
const notifications = ref(true)
const language = ref('en')
const testEmail = ref('')
const emailStatus = ref(null)
const isSendingEmail = ref(false)

function toggleNotifications() {
  notifications.value = !notifications.value
}

async function sendTestEmail() {
  if (isSendingEmail.value) return

  isSendingEmail.value = true
  emailStatus.value = null

  try {
    const payload = {
      subject: 'Test Notification',
      message: 'This is a test email from the Supply Chain system to verify notification settings.'
    }

    const recipients = testEmail.value
      .split(',')
      .map((email) => email.trim())
      .filter(Boolean)

    if (recipients.length) {
      payload.recipients = recipients
    }

    await notificationApi.sendEmail(payload)
    emailStatus.value = {
      type: 'success',
      message: 'Notification email queued successfully.'
    }
  } catch (error) {
    emailStatus.value = {
      type: 'error',
      message: error.message || 'Failed to send notification email.'
    }
  } finally {
    isSendingEmail.value = false
  }
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
          <Button @click="toggleDarkMode" :variant="isDark ? 'default' : 'outline'">
            {{ isDark ? 'On' : 'Off' }}
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
        <div class="flex flex-col gap-2">
          <div class="flex items-center justify-between">
            <span class="font-medium">Send Test Notification</span>
            <Button
              @click="sendTestEmail"
              :variant="isSendingEmail ? 'outline' : 'default'"
              :disabled="isSendingEmail"
            >
              {{ isSendingEmail ? 'Sending...' : 'Send Email' }}
            </Button>
          </div>
          <p class="text-sm text-muted-foreground">
            Leave the email field empty to use the default recipients configured on the server.
          </p>
          <input
            v-model="testEmail"
            type="text"
            placeholder="ops@example.com, team@example.com"
            class="border rounded px-3 py-2 bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          />
          <p
            v-if="emailStatus"
            :class="['text-sm', emailStatus.type === 'success' ? 'text-green-600' : 'text-red-500']"
          >
            {{ emailStatus.message }}
          </p>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<style scoped>
select:focus,
input:focus {
  outline: 2px solid var(--primary);
}
</style>
