<template>
  <div class="landing-page">
    <!-- Navigation Bar -->
    <nav ref="navbar" class="fixed top-0 left-0 right-0 z-50 transition-all duration-300 bg-white/80 backdrop-blur-md border-b border-gray-100">
      <div class="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
        <!-- Logo Section -->
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
            </svg>
          </div>
          <div>
            <h1 class="text-xl font-bold text-gray-900">CargoFlow</h1>
            <p class="text-xs text-gray-500 tracking-wide uppercase">Supply Chain Management</p>
          </div>
        </div>

        <!-- Navigation Links - Desktop -->
        <div class="hidden lg:flex items-center space-x-8">
          <a href="#features" class="text-gray-600 hover:text-blue-600 font-medium transition-colors">Features</a>
          <a href="#solutions" class="text-gray-600 hover:text-blue-600 font-medium transition-colors">Solutions</a>
          <a href="#about" class="text-gray-600 hover:text-blue-600 font-medium transition-colors">About</a>
        </div>

        <!-- Action Buttons - Desktop -->
        <div class="hidden lg:flex items-center space-x-4">
          <button 
            @click="showLoginModal = true"
            class="px-4 py-2 text-gray-700 border border-gray-200 rounded-lg hover:border-blue-500 hover:text-blue-600 transition-all duration-200"
          >
            Login
          </button>
          <button 
            @click="navigateToDashboard"
            class="px-6 py-2 text-white bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg shadow-md hover:shadow-lg hover:scale-105 transition-all duration-200"
          >
            Try CargoFlow
          </button>
        </div>

        <!-- Mobile Menu Button -->
        <button class="lg:hidden p-2" @click="showMobileMenu = !showMobileMenu">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
          </svg>
        </button>
      </div>

      <!-- Mobile Menu -->
      <div v-if="showMobileMenu" class="lg:hidden bg-white border-t border-gray-100">
        <div class="px-6 py-4 space-y-3">
          <a href="#features" class="block text-gray-600 hover:text-blue-600 font-medium" @click="showMobileMenu = false">Features</a>
          <a href="#solutions" class="block text-gray-600 hover:text-blue-600 font-medium" @click="showMobileMenu = false">Solutions</a>
          <a href="#about" class="block text-gray-600 hover:text-blue-600 font-medium" @click="showMobileMenu = false">About</a>
          <div class="pt-3 space-y-2">
            <button @click="showLoginModal = true; showMobileMenu = false" class="w-full text-left text-gray-700 py-2">Login</button>
            <button @click="navigateToDashboard" class="w-full px-4 py-2 text-white bg-blue-500 rounded-lg">Try CargoFlow</button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="relative min-h-screen flex items-center overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-100">
      <!-- Background Decorations -->
      <div class="absolute top-20 right-20 w-72 h-72 bg-gradient-to-br from-blue-500 to-teal-500 rounded-full blur-3xl opacity-10"></div>
      <div class="absolute bottom-20 left-20 w-96 h-96 bg-gradient-to-br from-indigo-500 to-purple-500 rounded-full blur-3xl opacity-10"></div>
      
      <!-- Floating Particles Animation -->
      <div class="absolute inset-0 overflow-hidden">
        <div 
          v-for="particle in particles" 
          :key="particle.id"
          class="absolute w-2 h-2 bg-blue-400 rounded-full opacity-20"
          :style="{ 
            left: particle.x + '%', 
            top: particle.y + '%',
            animationDelay: particle.delay + 's',
            animation: 'float 6s ease-in-out infinite'
          }"
        ></div>
      </div>

      <div class="container mx-auto px-6 pt-20 grid lg:grid-cols-2 gap-12 items-center relative z-10">
        <!-- Left Column - Hero Content -->
        <div class="space-y-8">
          <div class="text-sm font-semibold text-blue-600 uppercase tracking-wider">
            Modern Logistics Platform
          </div>
          
          <h1 class="text-5xl lg:text-6xl font-bold text-gray-900 leading-tight">
            Streamline Your 
            <span class="bg-gradient-to-r from-blue-600 to-teal-500 bg-clip-text text-transparent">
              Supply Chain
            </span>
            Operations
          </h1>
          
          <p class="text-xl text-gray-600 leading-relaxed max-w-xl">
            Manage cargo, track shipments, optimize routes, and coordinate with vendors—all in one powerful platform.
          </p>

          <!-- CTA Buttons -->
          <div class="flex flex-col sm:flex-row gap-4">
            <button 
              @click="navigateToDashboard"
              class="group px-8 py-4 text-white bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 flex items-center justify-center gap-2"
            >
              Try CargoFlow
              <svg class="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
              </svg>
            </button>
            <button class="px-8 py-4 text-blue-600 border-2 border-blue-600 rounded-lg hover:bg-blue-600 hover:text-white transition-all duration-200 flex items-center justify-center gap-2">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h1.586a1 1 0 01.707.293l4.414 4.414a1 1 0 01.293.707V17M6 10V9a3 3 0 113-3v1"/>
              </svg>
              View Demo
            </button>
          </div>

          <!-- Trust Indicators -->
          <div class="text-sm text-gray-500">
            Professional supply chain management solution
          </div>

          <!-- Company Trust Bar -->
          <div class="flex items-center gap-6 pt-6">
            <span class="text-sm text-gray-600 font-medium">Trusted by 500+ companies</span>
            <div class="flex items-center gap-6 opacity-60">
              <div class="w-16 h-8 bg-gray-300 rounded"></div>
              <div class="w-16 h-8 bg-gray-300 rounded"></div>
              <div class="w-16 h-8 bg-gray-300 rounded"></div>
              <div class="w-16 h-8 bg-gray-300 rounded"></div>
            </div>
          </div>
        </div>

        <!-- Right Column - Dashboard Preview -->
        <div class="relative">
          <!-- Main Dashboard Preview -->
          <div class="relative transform hover:scale-105 transition-transform duration-500">
            <div class="bg-gray-100 rounded-xl p-4 shadow-2xl">
              <!-- Browser Chrome -->
              <div class="flex gap-2 mb-4">
                <div class="w-3 h-3 rounded-full bg-red-500"></div>
                <div class="w-3 h-3 rounded-full bg-yellow-500"></div>
                <div class="w-3 h-3 rounded-full bg-green-500"></div>
              </div>
              <!-- Dashboard Content Placeholder -->
              <div class="bg-white rounded-lg p-6 space-y-4">
                <div class="flex justify-between items-center">
                  <div class="h-6 bg-gray-300 rounded w-32"></div>
                  <div class="h-6 bg-blue-500 rounded w-24"></div>
                </div>
                <div class="grid grid-cols-3 gap-4">
                  <div class="h-20 bg-gradient-to-br from-blue-100 to-blue-200 rounded-lg"></div>
                  <div class="h-20 bg-gradient-to-br from-green-100 to-green-200 rounded-lg"></div>
                  <div class="h-20 bg-gradient-to-br from-orange-100 to-orange-200 rounded-lg"></div>
                </div>
                <div class="h-32 bg-gray-100 rounded-lg"></div>
              </div>
            </div>
          </div>

          <!-- Floating Status Elements -->
          <div class="absolute -top-4 -left-8 bg-white rounded-lg shadow-lg p-3 border border-gray-100 animate-bounce">
            <div class="flex items-center gap-2">
              <div class="w-3 h-3 bg-green-500 rounded-full"></div>
              <span class="text-sm font-medium">4,590 km</span>
            </div>
          </div>

          <div class="absolute top-8 -right-12 bg-white rounded-lg shadow-lg p-3 border border-gray-100" style="animation: float 3s ease-in-out infinite;">
            <div class="flex items-center gap-2">
              <div class="w-2 h-2 bg-green-500 rounded-full"></div>
              <span class="text-sm font-medium text-green-600">Delivered</span>
            </div>
          </div>

          <div class="absolute bottom-8 -left-16 bg-white rounded-lg shadow-lg p-3 border border-gray-100" style="animation: float 4s ease-in-out infinite;">
            <div class="text-sm">
              <div class="font-medium">New Shipment</div>
              <div class="text-gray-500">SH005</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section id="features" class="py-20 bg-white">
      <div class="container mx-auto px-6">
        <!-- Section Header -->
        <div class="text-center mb-16">
          <div class="text-sm font-semibold text-blue-600 uppercase tracking-wider mb-4">
            Powerful Features
          </div>
          <h2 class="text-4xl font-bold text-gray-900 mb-6">
            Everything You Need to Manage Logistics
          </h2>
          <p class="text-xl text-gray-600 max-w-2xl mx-auto">
            Comprehensive tools designed for modern supply chain management
          </p>
        </div>

        <!-- Features Grid -->
        <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
          <!-- Feature Card Template -->
          <div 
            v-for="feature in features" 
            :key="feature.id"
            class="group p-6 bg-white rounded-xl border border-gray-100 hover:border-blue-200 hover:shadow-lg transition-all duration-300"
          >
            <div :class="feature.iconBg" class="w-12 h-12 rounded-lg flex items-center justify-center mb-4">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="feature.iconPath"/>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ feature.title }}</h3>
            <p class="text-gray-600">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Solutions Section -->
    <section id="solutions" class="py-20 bg-gray-50">
      <div class="container mx-auto px-6">
        <div class="text-center mb-16">
          <h2 class="text-4xl font-bold text-gray-900 mb-6">
            How CargoFlow Works
          </h2>
          <p class="text-xl text-gray-600 max-w-2xl mx-auto">
            Simple steps to transform your supply chain management
          </p>
        </div>

        <!-- Workflow Steps -->
        <div class="grid md:grid-cols-3 gap-8">
          <div 
            v-for="(step, index) in workflowSteps" 
            :key="step.id"
            class="text-center"
          >
            <div class="relative mb-6">
              <div class="w-16 h-16 bg-blue-500 rounded-full flex items-center justify-center mx-auto mb-4">
                <span class="text-2xl font-bold text-white">{{ index + 1 }}</span>
              </div>
              <!-- Connection Line (except for last item) -->
              <div v-if="index < workflowSteps.length - 1" class="hidden md:block absolute top-8 left-1/2 w-full h-0.5 bg-gray-300 transform translate-x-8"></div>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-3">{{ step.title }}</h3>
            <p class="text-gray-600">{{ step.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- About Section -->
    <section id="about" class="py-20 bg-gray-50">
      <div class="container mx-auto px-6">
        <div class="max-w-4xl mx-auto text-center">
          <h2 class="text-4xl font-bold text-gray-900 mb-6">
            About CargoFlow
          </h2>
          <p class="text-xl text-gray-600 mb-8">
            We're revolutionizing supply chain management with intelligent automation, 
            real-time tracking, and comprehensive analytics to help businesses optimize 
            their logistics operations.
          </p>
          
          <!-- Stats -->
          <div class="grid md:grid-cols-4 gap-8 mt-12">
            <div v-for="stat in stats" :key="stat.label" class="text-center">
              <div class="text-3xl font-bold text-blue-600 mb-2">{{ stat.value }}</div>
              <div class="text-gray-600">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-gray-900 text-white py-12">
      <div class="container mx-auto px-6">
        <div class="grid md:grid-cols-4 gap-8">
          <!-- Logo and Description -->
          <div class="md:col-span-2">
            <div class="flex items-center space-x-3 mb-4">
              <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
                </svg>
              </div>
              <div>
                <h3 class="text-xl font-bold">CargoFlow</h3>
                <p class="text-sm text-gray-400 uppercase tracking-wide">Supply Chain Management</p>
              </div>
            </div>
            <p class="text-gray-400 max-w-md">
              Streamlining supply chain operations with intelligent automation and real-time insights.
            </p>
          </div>

          <!-- Quick Links -->
          <div>
            <h4 class="font-semibold mb-4">Quick Links</h4>
            <ul class="space-y-2">
              <li><a href="#features" class="text-gray-400 hover:text-white transition-colors">Features</a></li>
              <li><a href="#solutions" class="text-gray-400 hover:text-white transition-colors">Solutions</a></li>
              <li><a href="#about" class="text-gray-400 hover:text-white transition-colors">About</a></li>
            </ul>
          </div>

          <!-- Contact -->
          <div>
            <h4 class="font-semibold mb-4">Contact</h4>
            <ul class="space-y-2 text-gray-400">
              <li>support@cargoflow.com</li>
              <li>+1 (555) 123-4567</li>
              <li>San Francisco, CA</li>
            </ul>
          </div>
        </div>
        
        <div class="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400">
          <p>&copy; 2024 CargoFlow. All rights reserved.</p>
        </div>
      </div>
    </footer>

    <!-- Login Modal -->
    <div v-if="showLoginModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50" @click="showLoginModal = false">
      <div class="bg-white rounded-lg p-8 max-w-md w-full mx-4" @click.stop>
        <div class="text-center mb-6">
          <h3 class="text-2xl font-bold text-gray-900">Welcome Back</h3>
          <p class="text-gray-600">Sign in to your CargoFlow account</p>
        </div>
        
        <form @submit.prevent="handleLogin" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input 
              v-model="loginForm.email" 
              type="email" 
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Enter your email"
            >
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Password</label>
            <input 
              v-model="loginForm.password" 
              type="password" 
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Enter your password"
            >
          </div>
          
          <button type="submit" class="w-full py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors">
            Sign In
          </button>
        </form>
        
        <div class="text-center mt-4">
          <button @click="showLoginModal = false" class="text-gray-500 hover:text-gray-700">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

// Router setup
const router = useRouter()

// Reactive state
const showMobileMenu = ref(false)
const showLoginModal = ref(false)
const navbar = ref(null)

// Login form data
const loginForm = ref({
  email: '',
  password: ''
})

// Floating particles data
const particles = ref([])

// Features data
const features = ref([
  {
    id: 1,
    title: 'Cargo Management',
    description: 'Track and manage all your cargo with advanced inventory systems',
    iconPath: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
    iconBg: 'bg-gradient-to-br from-blue-500 to-blue-600'
  },
  {
    id: 2,
    title: 'Route Optimization',
    description: 'Optimize delivery routes for efficiency and cost reduction',
    iconPath: 'M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7',
    iconBg: 'bg-gradient-to-br from-green-500 to-green-600'
  },
  {
    id: 3,
    title: 'Real-time Tracking',
    description: 'Monitor shipments in real-time with GPS and IoT integration',
    iconPath: 'M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z M15 11a3 3 0 11-6 0 3 3 0 016 0z',
    iconBg: 'bg-gradient-to-br from-orange-500 to-orange-600'
  },
  {
    id: 4,
    title: 'Vendor Coordination',
    description: 'Seamlessly coordinate with vendors and suppliers',
    iconPath: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z',
    iconBg: 'bg-gradient-to-br from-purple-500 to-purple-600'
  }
])

// Workflow steps data
const workflowSteps = ref([
  {
    id: 1,
    title: 'Import & Setup',
    description: 'Import your existing data and configure your supply chain parameters'
  },
  {
    id: 2,
    title: 'Optimize & Plan',
    description: 'Let our AI optimize routes and create efficient delivery plans'
  },
  {
    id: 3,
    title: 'Track & Manage',
    description: 'Monitor shipments in real-time and manage all operations from one dashboard'
  }
])

// Stats data
const stats = ref([
  { value: '500+', label: 'Companies' },
  { value: '50K+', label: 'Shipments Tracked' },
  { value: '99.9%', label: 'Uptime' },
  { value: '24/7', label: 'Support' }
])

// Methods
const navigateToDashboard = () => {
  router.push('/dashboard')
}

const handleLogin = () => {
  // Handle login logic here
  console.log('Login attempt:', loginForm.value)
  // For demo purposes, just close modal and navigate to dashboard
  showLoginModal.value = false
  navigateToDashboard()
}

const generateParticles = () => {
  particles.value = Array.from({ length: 20 }, (_, i) => ({
    id: i,
    x: Math.random() * 100,
    y: Math.random() * 100,
    delay: Math.random() * 5
  }))
}

const handleScroll = () => {
  if (navbar.value) {
    const scrolled = window.scrollY > 50
    navbar.value.classList.toggle('bg-white/90', scrolled)
    navbar.value.classList.toggle('shadow-sm', scrolled)
  }
}

// Lifecycle hooks
onMounted(() => {
  generateParticles()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* Custom animations */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Animation classes */
.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s ease-out;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}

/* Smooth scrolling for anchor links */
html {
  scroll-behavior: smooth;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #3b82f6;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #2563eb;
}
</style>