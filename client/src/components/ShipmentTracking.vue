<template>
  <div class="shipment-tracking-page bg-[#F5F5F7] min-h-screen p-10 space-y-7">
    <!-- Page Header -->
    <header class="flex justify-between items-start">
      <div class="flex items-center gap-4">
        <div class="w-7 h-7 text-[#3B82F6]">
          <Truck class="w-full h-full" />
        </div>
        <div>
          <h1 class="text-4xl font-bold text-[#0F172A] tracking-tight leading-none">Shipment Tracking</h1>
          <p class="text-[#64748B] text-base mt-1">Monitor and track all shipments in real-time.</p>
        </div>
      </div>
      <button 
        @click="createNewShipment"
        class="bg-gradient-to-br from-[#3B82F6] to-[#2563EB] text-white px-7 py-3.5 rounded-xl hover:scale-102 active:scale-98 hover:shadow-lg shadow-[0_4px_12px_rgba(59,130,246,0.3)] transition-all duration-200 flex items-center gap-3 font-semibold text-sm"
      >
        <Plus class="w-5 h-5" />
        New Shipment
      </button>
    </header>

    <!-- Quick Stats Banner -->
    <div class="bg-gradient-to-r from-[#F0F9FF] to-[#E0F2FE] border-l-4 border-[#3B82F6] p-4 rounded-xl">
      <div class="flex items-center gap-6 text-sm font-medium text-[#1E293B]">
        <span>Total Active: <strong class="text-[#3B82F6]">{{ metrics.totalActive }}</strong> shipments</span>
        <span class="text-[#94A3B8]">•</span>
        <span>On Time: <strong class="text-[#10B981]">{{ onTimeCount }} ({{ metrics.onTimePercentage }}%)</strong></span>
        <span class="text-[#94A3B8]">•</span>
        <span>Delayed: <strong class="text-[#EF4444]">{{ metrics.delayed }} ({{ delayedPercentage }}%)</strong></span>
      </div>
    </div>

    <!-- Status Overview Cards -->
    <div class="grid grid-cols-1 md:grid-cols-5 gap-5">
      <div 
        v-for="status in statusCards" 
        :key="status.id"
        @click="filterByStatus(status.id)"
        :class="[
          'group bg-white rounded-3xl p-7 border border-gray-100 transition-all duration-300 cursor-pointer',
          'hover:shadow-[0_20px_60px_-12px_rgba(0,0,0,0.15)] hover:-translate-y-2',
          activeFilter === status.id ? 'ring-2 ring-blue-500 shadow-lg' : ''
        ]"
      >
        <div class="flex flex-col items-center text-center">
          <!-- Icon Circle -->
          <div :class="[
            'p-4 rounded-full transition-all duration-300 group-hover:scale-110 group-hover:rotate-3 mb-4',
            status.iconBg
          ]">
            <component :is="status.icon" :class="['w-7 h-7', status.iconColor]" />
          </div>
          
          <!-- Number -->
          <p :class="['text-3xl font-bold mb-2 transition-colors duration-300', status.textColor]">
            {{ status.count }}
          </p>
          
          <!-- Label -->
          <p :class="['text-sm font-medium mb-3 transition-colors duration-300', status.labelColor]">
            {{ status.label }}
          </p>
          
          <!-- Status Badge -->
          <div :class="[
            'px-3 py-1.5 rounded-lg text-xs font-bold uppercase tracking-wider flex items-center gap-2',
            status.badgeClass,
            status.id === 'delayed' ? 'animate-pulse' : ''
          ]">
            <component :is="status.icon" class="w-3 h-3" />
            {{ status.label }}
          </div>
          
          <!-- Progress Bar -->
          <div class="w-full mt-4 h-1 bg-gray-100 rounded-full overflow-hidden">
            <div 
              :class="['h-full rounded-full transition-all duration-500', status.progressColor]"
              :style="{ width: status.progress + '%' }"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Search & Filter Toolbar -->
    <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        <!-- Search -->
        <div class="relative flex-1 max-w-lg">
          <Search class="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search shipments by ID, origin, destination, or status..."
            class="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          />
        </div>

        <!-- Filter Actions -->
        <div class="flex items-center gap-3">
          <button
            @click="toggleFilterMenu"
            class="flex items-center gap-2 px-4 py-3 border border-gray-200 rounded-xl hover:border-blue-500 hover:bg-blue-50 hover:text-blue-700 transition-all duration-200"
          >
            <Filter class="w-5 h-5" />
            Filter by Status
            <span v-if="activeFilters.length" class="ml-1 px-2 py-0.5 bg-blue-500 text-white text-xs rounded-full">
              {{ activeFilters.length }}
            </span>
          </button>
          
          <button
            @click="toggleSortMenu"
            class="flex items-center gap-2 px-4 py-3 border border-gray-200 rounded-xl hover:border-blue-500 hover:bg-blue-50 hover:text-blue-700 transition-all duration-200"
          >
            <ArrowUpDown class="w-5 h-5" />
            Sort by Date
          </button>
        </div>
      </div>
    </div>

    <!-- Active Shipments Table -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
      <!-- Table Header -->
      <div class="flex justify-between items-center p-6 border-b border-gray-100">
        <h2 class="text-xl font-semibold text-gray-900">Active Shipments</h2>
        <button 
          @click="exportShipments"
          class="flex items-center gap-2 px-4 py-2 text-gray-700 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors duration-200"
        >
          <Download class="w-4 h-4" />
          Export
        </button>
      </div>

      <!-- Table -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gradient-to-r from-gray-50 to-gray-100">
            <tr>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Shipment ID</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Route</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Status</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Cargo Value</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Est. Delivery</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Created</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr 
              v-for="shipment in filteredShipments" 
              :key="shipment.id"
              @click="viewShipmentDetails(shipment)"
              class="group hover:bg-gradient-to-r hover:from-blue-50 hover:to-transparent hover:border-l-4 hover:border-l-blue-500 transition-all duration-200 cursor-pointer"
            >
              <!-- Shipment ID -->
              <td class="px-6 py-6">
                <span class="font-mono font-bold text-gray-900 group-hover:text-blue-600 transition-colors">
                  {{ shipment.id }}
                </span>
              </td>

              <!-- Route -->
              <td class="px-6 py-6">
                <div class="flex items-center gap-2">
                  <MapPin class="w-4 h-4 text-gray-400" />
                  <span class="font-medium text-gray-900">{{ shipment.route.origin }}</span>
                  <ArrowRight class="w-4 h-4 text-gray-400" />
                  <span class="font-medium text-gray-900">{{ shipment.route.destination }}</span>
                </div>
              </td>

              <!-- Status -->
              <td class="px-6 py-6">
                <div :class="getStatusBadgeClass(shipment.status)">
                  <component :is="getStatusIcon(shipment.status)" class="w-3 h-3" />
                  {{ getStatusLabel(shipment.status) }}
                </div>
              </td>

              <!-- Cargo Value -->
              <td class="px-6 py-6">
                <div>
                  <div class="font-bold text-gray-900">₹{{ formatCurrency(shipment.cargoValue) }}</div>
                  <div class="text-sm text-gray-500">{{ shipment.cargoItems }} items</div>
                </div>
              </td>

              <!-- Est. Delivery -->
              <td class="px-6 py-6">
                <div class="flex items-center gap-2">
                  <Calendar class="w-4 h-4 text-gray-400" />
                  <span :class="isOverdue(shipment.estimatedDelivery) ? 'text-red-600 font-medium' : 'text-gray-700'">
                    {{ formatDate(shipment.estimatedDelivery) }}
                  </span>
                </div>
              </td>

              <!-- Created -->
              <td class="px-6 py-6 text-gray-600">
                {{ formatDate(shipment.created) }}
              </td>

              <!-- Actions -->
              <td class="px-6 py-6">
                <div class="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                  <button 
                    @click.stop="viewShipment(shipment)"
                    class="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors duration-200"
                    title="View Details"
                  >
                    <Eye class="w-4 h-4" />
                  </button>
                  <button 
                    @click.stop="trackShipment(shipment)"
                    class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 hover:text-blue-700 transition-all duration-200"
                  >
                    Track
                    <Navigation class="w-3 h-3" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Empty State -->
        <div v-if="filteredShipments.length === 0" class="flex flex-col items-center justify-center py-16">
          <Package class="w-16 h-16 text-gray-300 mb-4" />
          <p class="text-lg font-medium text-gray-500 mb-2">No shipments found</p>
          <p class="text-gray-400 mb-6">Create your first shipment to start tracking</p>
          <button 
            @click="createNewShipment"
            class="flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors duration-200"
          >
            <Plus class="w-5 h-5" />
            New Shipment
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  Truck, Plus, Search, Filter, ArrowUpDown, Download, Eye, Navigation,
  Package, MapPin, ArrowRight, Calendar, CheckCircle, AlertTriangle,
  FileText, Clock
} from 'lucide-vue-next'

// ============================================================================
// REACTIVE STATE
// ============================================================================

const searchQuery = ref('')
const activeFilter = ref('')
const activeFilters = ref([])

// Sample shipment data
const shipments = ref([
  {
    id: 'SH001',
    route: { origin: 'Mumbai', destination: 'Chennai' },
    status: 'in-transit',
    cargoValue: 750000,
    cargoItems: 12,
    estimatedDelivery: new Date('2025-10-15'),
    created: new Date('2025-10-05')
  },
  {
    id: 'SH002',
    route: { origin: 'Delhi', destination: 'Bangalore' },
    status: 'delivered',
    cargoValue: 320000,
    cargoItems: 8,
    estimatedDelivery: new Date('2025-10-12'),
    created: new Date('2025-10-02')
  },
  {
    id: 'SH003',
    route: { origin: 'Kolkata', destination: 'Hyderabad' },
    status: 'picked-up',
    cargoValue: 180000,
    cargoItems: 5,
    estimatedDelivery: new Date('2025-10-18'),
    created: new Date('2025-10-08')
  },
  {
    id: 'SH004',
    route: { origin: 'Pune', destination: 'Kochi' },
    status: 'created',
    cargoValue: 95000,
    cargoItems: 3,
    estimatedDelivery: new Date('2025-10-20'),
    created: new Date('2025-10-10')
  },
  {
    id: 'SH005',
    route: { origin: 'Ahmedabad', destination: 'Jaipur' },
    status: 'delayed',
    cargoValue: 420000,
    cargoItems: 15,
    estimatedDelivery: new Date('2025-10-08'), // Overdue
    created: new Date('2025-09-28')
  }
])

// ============================================================================
// COMPUTED PROPERTIES
// ============================================================================

// Status card configurations
const statusCards = computed(() => [
  {
    id: 'in-transit',
    icon: Truck,
    count: shipments.value.filter(s => s.status === 'in-transit').length,
    label: 'In Transit',
    iconBg: 'bg-gradient-to-br from-teal-50 to-teal-100',
    iconColor: 'text-teal-600',
    textColor: 'text-gray-900 group-hover:text-teal-700',
    labelColor: 'text-gray-600 group-hover:text-teal-600',
    badgeClass: 'bg-gradient-to-r from-teal-100 to-teal-200 text-teal-800 border-l-2 border-teal-500',
    progressColor: 'bg-teal-500',
    progress: 75
  },
  {
    id: 'delivered',
    icon: CheckCircle,
    count: shipments.value.filter(s => s.status === 'delivered').length,
    label: 'Delivered',
    iconBg: 'bg-gradient-to-br from-green-50 to-green-100',
    iconColor: 'text-green-600',
    textColor: 'text-gray-900 group-hover:text-green-700',
    labelColor: 'text-gray-600 group-hover:text-green-600',
    badgeClass: 'bg-gradient-to-r from-green-100 to-green-200 text-green-800 border-l-2 border-green-500',
    progressColor: 'bg-green-500',
    progress: 100
  },
  {
    id: 'picked-up',
    icon: Package,
    count: shipments.value.filter(s => s.status === 'picked-up').length,
    label: 'Picked Up',
    iconBg: 'bg-gradient-to-br from-blue-50 to-blue-100',
    iconColor: 'text-blue-600',
    textColor: 'text-gray-900 group-hover:text-blue-700',
    labelColor: 'text-gray-600 group-hover:text-blue-600',
    badgeClass: 'bg-gradient-to-r from-blue-100 to-blue-200 text-blue-800 border-l-2 border-blue-500',
    progressColor: 'bg-blue-500',
    progress: 50
  },
  {
    id: 'created',
    icon: FileText,
    count: shipments.value.filter(s => s.status === 'created').length,
    label: 'Created',
    iconBg: 'bg-gradient-to-br from-gray-50 to-gray-100',
    iconColor: 'text-gray-600',
    textColor: 'text-gray-900 group-hover:text-gray-700',
    labelColor: 'text-gray-600 group-hover:text-gray-600',
    badgeClass: 'bg-gradient-to-r from-gray-100 to-gray-200 text-gray-800 border-l-2 border-gray-500',
    progressColor: 'bg-gray-500',
    progress: 25
  },
  {
    id: 'delayed',
    icon: AlertTriangle,
    count: shipments.value.filter(s => s.status === 'delayed').length,
    label: 'Delayed',
    iconBg: 'bg-gradient-to-br from-red-50 to-red-100',
    iconColor: 'text-red-600',
    textColor: 'text-gray-900 group-hover:text-red-700',
    labelColor: 'text-gray-600 group-hover:text-red-600',
    badgeClass: 'bg-gradient-to-r from-red-100 to-red-200 text-red-800 border-l-2 border-red-500',
    progressColor: 'bg-red-500',
    progress: 60
  }
])

// Metrics calculations
const metrics = computed(() => {
  const total = shipments.value.length
  const delayed = shipments.value.filter(s => s.status === 'delayed').length
  const delivered = shipments.value.filter(s => s.status === 'delivered').length
  const onTime = total - delayed
  
  return {
    totalActive: total,
    delayed,
    onTimePercentage: total > 0 ? Math.round((onTime / total) * 100) : 0
  }
})

const onTimeCount = computed(() => shipments.value.length - metrics.value.delayed)
const delayedPercentage = computed(() => {
  const total = shipments.value.length
  return total > 0 ? Math.round((metrics.value.delayed / total) * 100) : 0
})

// Filtered shipments based on search and filters
const filteredShipments = computed(() => {
  let filtered = shipments.value

  // Apply search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(shipment =>
      shipment.id.toLowerCase().includes(query) ||
      shipment.route.origin.toLowerCase().includes(query) ||
      shipment.route.destination.toLowerCase().includes(query) ||
      shipment.status.toLowerCase().includes(query)
    )
  }

  // Apply status filter
  if (activeFilter.value) {
    filtered = filtered.filter(shipment => shipment.status === activeFilter.value)
  }

  return filtered
})

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('en-IN').format(amount)
}

const formatDate = (date) => {
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  }).format(date)
}

const isOverdue = (date) => {
  return new Date() > new Date(date)
}

const getStatusIcon = (status) => {
  const icons = {
    'in-transit': Truck,
    'delivered': CheckCircle,
    'picked-up': Package,
    'created': FileText,
    'delayed': AlertTriangle
  }
  return icons[status] || FileText
}

const getStatusLabel = (status) => {
  const labels = {
    'in-transit': 'In Transit',
    'delivered': 'Delivered',
    'picked-up': 'Picked Up',
    'created': 'Created',
    'delayed': 'Delayed'
  }
  return labels[status] || 'Unknown'
}

const getStatusBadgeClass = (status) => {
  const classes = {
    'in-transit': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-teal-100 to-teal-200 text-teal-800 border-l-2 border-teal-500',
    'delivered': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-green-100 to-green-200 text-green-800 border-l-2 border-green-500',
    'picked-up': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-blue-100 to-blue-200 text-blue-800 border-l-2 border-blue-500',
    'created': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-gray-100 to-gray-200 text-gray-800 border-l-2 border-gray-500',
    'delayed': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-red-100 to-red-200 text-red-800 border-l-2 border-red-500 animate-pulse'
  }
  return classes[status] || classes.created
}

// ============================================================================
// EVENT HANDLERS
// ============================================================================

const filterByStatus = (status) => {
  activeFilter.value = activeFilter.value === status ? '' : status
}

const toggleFilterMenu = () => {
  console.log('Toggle filter menu')
}

const toggleSortMenu = () => {
  console.log('Toggle sort menu')
}

const createNewShipment = () => {
  console.log('Create new shipment')
}

const exportShipments = () => {
  console.log('Export shipments')
}

const viewShipmentDetails = (shipment) => {
  console.log('View shipment details:', shipment.id)
}

const viewShipment = (shipment) => {
  console.log('View shipment:', shipment.id)
}

const trackShipment = (shipment) => {
  console.log('Track shipment:', shipment.id)
}
</script>

<style scoped>
/* Custom scrollbar for table */
.overflow-x-auto::-webkit-scrollbar {
  height: 8px;
}

.overflow-x-auto::-webkit-scrollbar-track {
  background: #f3f4f6;
  border-radius: 8px;
}

.overflow-x-auto::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 8px;
}

.overflow-x-auto::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Smooth transitions for all interactive elements */
* {
  transition-property: color, background-color, border-color, transform, box-shadow, opacity;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

/* Enhanced hover effects for table rows */
.group:hover {
  transform: translateX(2px);
}

/* Font family consistency */
.font-mono {
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', 'Courier New', monospace;
}
</style>