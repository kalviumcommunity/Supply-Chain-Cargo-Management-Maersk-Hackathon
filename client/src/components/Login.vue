<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
    <div class="w-full max-w-md">
      <!-- Logo/Title -->
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-gray-900 mb-2">Supply Chain</h1>
        <p class="text-gray-600">Cargo Management System</p>
      </div>

      <!-- Auth Card -->
      <div class="bg-white rounded-2xl shadow-2xl p-8">
        <!-- Tabs -->
        <div class="flex mb-8 border-b border-gray-200">
          <button
            @click="activeTab = 'login'"
            :class="[
              'flex-1 pb-3 text-center font-semibold transition-colors',
              activeTab === 'login'
                ? 'text-blue-600 border-b-2 border-blue-600'
                : 'text-gray-500 hover:text-gray-700'
            ]"
          >
            Login
          </button>
          <button
            @click="activeTab = 'signup'"
            :class="[
              'flex-1 pb-3 text-center font-semibold transition-colors',
              activeTab === 'signup'
                ? 'text-blue-600 border-b-2 border-blue-600'
                : 'text-gray-500 hover:text-gray-700'
            ]"
          >
            Sign Up
          </button>
        </div>

        <!-- Google OAuth Button -->
        <button
          @click="loginWithGoogle"
          class="w-full flex items-center justify-center gap-3 px-4 py-3 border-2 border-gray-300 rounded-lg hover:bg-gray-50 hover:border-gray-400 transition-all mb-6 font-medium text-gray-700"
        >
          <svg viewBox="-3 0 262 262" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid" class="w-5 h-5">
            <path d="M255.878 133.451c0-10.734-.871-18.567-2.756-26.69H130.55v48.448h71.947c-1.45 12.04-9.283 30.172-26.69 42.356l-.244 1.622 38.755 30.023 2.685.268c24.659-22.774 38.875-56.282 38.875-96.027" fill="#4285F4"></path>
            <path d="M130.55 261.1c35.248 0 64.839-11.605 86.453-31.622l-41.196-31.913c-11.024 7.688-25.82 13.055-45.257 13.055-34.523 0-63.824-22.773-74.269-54.25l-1.531.13-40.298 31.187-.527 1.465C35.393 231.798 79.49 261.1 130.55 261.1" fill="#34A853"></path>
            <path d="M56.281 156.37c-2.756-8.123-4.351-16.827-4.351-25.82 0-8.994 1.595-17.697 4.206-25.82l-.073-1.73L15.26 71.312l-1.335.635C5.077 89.644 0 109.517 0 130.55s5.077 40.905 13.925 58.602l42.356-32.782" fill="#FBBC05"></path>
            <path d="M130.55 50.479c24.514 0 41.05 10.589 50.479 19.438l36.844-35.974C195.245 12.91 165.798 0 130.55 0 79.49 0 35.393 29.301 13.925 71.947l42.211 32.783c10.59-31.477 39.891-54.251 74.414-54.251" fill="#EB4335"></path>
          </svg>
          Continue with Google
        </button>

        <!-- Divider -->
        <div class="flex items-center gap-4 mb-6">
          <div class="flex-1 h-px bg-gray-300"></div>
          <span class="text-gray-500 text-sm">OR</span>
          <div class="flex-1 h-px bg-gray-300"></div>
        </div>

        <!-- Login Form -->
        <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="space-y-4">
          <!-- Email -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Email</label>
            <input
              v-model="loginForm.email"
              type="email"
              required
              placeholder="your@email.com"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
            />
          </div>

          <!-- Password -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Password</label>
            <input
              v-model="loginForm.password"
              type="password"
              required
              placeholder="••••••••"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
            />
          </div>

          <!-- Error Message -->
          <div v-if="error" class="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
            {{ error }}
          </div>

          <!-- Success Message -->
          <div v-if="success" class="p-3 bg-green-50 border border-green-200 rounded-lg text-green-700 text-sm">
            {{ success }}
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors disabled:bg-blue-300 disabled:cursor-not-allowed"
          >
            <span v-if="!loading">Login</span>
            <span v-else class="flex items-center justify-center gap-2">
              <svg class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Logging in...
            </span>
          </button>
        </form>

        <!-- Signup Form -->
        <form v-if="activeTab === 'signup'" @submit.prevent="handleSignup" class="space-y-4">
          <!-- Name -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Full Name</label>
            <input
              v-model="signupForm.name"
              type="text"
              required
              placeholder="John Doe"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
            />
          </div>

          <!-- Email -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Email</label>
            <input
              v-model="signupForm.email"
              type="email"
              required
              placeholder="your@email.com"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
            />
          </div>

          <!-- Password -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Password</label>
            <input
              v-model="signupForm.password"
              type="password"
              required
              minlength="6"
              placeholder="••••••••"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
            />
            <p class="text-xs text-gray-500 mt-1">At least 6 characters</p>
          </div>

          <!-- Error Message -->
          <div v-if="error" class="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
            {{ error }}
          </div>

          <!-- Success Message -->
          <div v-if="success" class="p-3 bg-green-50 border border-green-200 rounded-lg text-green-700 text-sm">
            {{ success }}
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors disabled:bg-blue-300 disabled:cursor-not-allowed"
          >
            <span v-if="!loading">Create Account</span>
            <span v-else class="flex items-center justify-center gap-2">
              <svg class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Creating account...
            </span>
          </button>
        </form>
      </div>

      <!-- Footer -->
      <p class="text-center text-gray-600 text-sm mt-6">
        By continuing, you agree to our Terms of Service and Privacy Policy
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuth } from '../services/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuth()

const activeTab = ref('login')
const loading = ref(false)
const error = ref('')
const success = ref('')

const loginForm = ref({
  email: '',
  password: ''
})

const signupForm = ref({
  name: '',
  email: '',
  password: ''
})

// Check if user is already authenticated
onMounted(() => {
  if (auth.isAuthenticated.value) {
    router.push('/')
  }
  
  // Check for OAuth callback success
  if (route.query.oauth === 'success') {
    // Fetch current user after OAuth
    auth.getCurrentUser().then(user => {
      if (user) {
        router.push('/')
      }
    })
  }
})

const loginWithGoogle = () => {
  auth.loginWithGoogle()
}

const handleLogin = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await auth.login(loginForm.value)
    
    if (response.success) {
      success.value = 'Login successful! Redirecting...'
      
      // Redirect to dashboard after short delay
      setTimeout(() => {
        router.push('/')
      }, 500)
    } else {
      error.value = response.message || 'Login failed'
    }
  } catch (err) {
    error.value = err.message || 'An error occurred during login'
  } finally {
    loading.value = false
  }
}

const handleSignup = async () => {
  loading.value = true
  error.value = ''
  success.value = ''

  try {
    const response = await auth.signup(signupForm.value)
    
    if (response.success) {
      success.value = 'Account created successfully! Please login.'
      
      // Store email for convenience
      const email = signupForm.value.email
      
      // Reset form
      signupForm.value = {
        name: '',
        email: '',
        password: ''
      }
      
      // Switch to login tab after 2 seconds
      setTimeout(() => {
        activeTab.value = 'login'
        loginForm.value.email = email
        success.value = ''
      }, 2000)
    } else {
      error.value = response.message || 'Signup failed'
    }
  } catch (err) {
    error.value = err.message || 'An error occurred during signup'
  } finally {
    loading.value = false
  }
}
</script>
