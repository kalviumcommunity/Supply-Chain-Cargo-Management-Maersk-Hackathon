<template>
  <!-- Backdrop -->
  <transition name="backdrop">
    <div 
      v-if="isOpen" 
      class="fixed inset-0 bg-black/50 backdrop-blur-sm z-40"
      @click="$emit('close')"
    ></div>
  </transition>

  <!-- Slide-over Panel -->
  <transition name="slide-over">
    <div 
      v-if="isOpen"
      class="fixed top-0 right-0 h-full w-[400px] bg-white shadow-2xl z-50 flex flex-col"
      @click.stop
    >
    <!-- Header -->
    <div class="p-6 border-b border-[#E5E7EB] bg-gradient-to-r from-[#F8FAFC] to-white">
      <div class="flex items-start justify-between mb-4">
        <div class="flex items-start gap-4">
          <!-- Vendor Logo -->
          <div class="w-12 h-12 rounded-lg bg-gradient-to-br from-[#3B82F6] to-[#2563EB] flex items-center justify-center shadow-sm">
            <span class="text-white font-bold text-lg">{{ getInitials(vendor?.name) }}</span>
          </div>
          
          <!-- Vendor Info -->
          <div>
            <h2 class="text-xl font-bold text-[#1E293B] leading-tight">{{ vendor?.name }}</h2>
            <p class="text-[#64748B] text-sm">{{ vendor?.id }}</p>
            
            <!-- Rating Display -->
            <div class="flex items-center gap-2 mt-2">
              <div class="flex gap-1">
                <Star v-for="star in 5" :key="star" 
                      :class="star <= Math.floor(vendor?.rating || 0) ? 'text-[#F59E0B] fill-current' : 'text-[#E5E7EB]'" 
                      class="w-4 h-4" />
              </div>
              <span class="text-[#1F2937] font-semibold">{{ vendor?.rating }}</span>
            </div>
          </div>
        </div>
        
        <!-- Header Actions -->
        <div class="flex items-center gap-2">
          <!-- Quick Actions Dropdown -->
          <div class="relative">
            <button 
              @click.stop="showQuickActions = !showQuickActions"
              class="w-8 h-8 rounded-lg hover:bg-[#F3F4F6] flex items-center justify-center transition-colors"
            >
              <MoreHorizontal class="w-5 h-5 text-[#6B7280]" />
            </button>

            <!-- Quick Actions Dropdown Menu -->
            <transition name="dropdown">
              <div 
                v-if="showQuickActions" 
                class="absolute top-full right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-[#E5E7EB] py-2 z-10"
                @click.stop
              >
                <button 
                  v-for="action in quickActions" 
                  :key="action.id"
                  @click="handleQuickAction(action)"
                  :class="[
                    'w-full px-4 py-2.5 text-left text-sm hover:bg-[#F9FAFB] transition-colors flex items-center gap-3',
                    action.danger ? 'text-[#EF4444] hover:bg-[#FEF2F2]' : 'text-[#374151]'
                  ]"
                >
                  <component :is="action.icon" class="w-4 h-4" />
                  {{ action.label }}
                </button>
              </div>
            </transition>
          </div>
          
          <!-- Close Button -->
          <button 
            @click="$emit('close')"
            class="w-8 h-8 rounded-lg hover:bg-[#F3F4F6] flex items-center justify-center transition-colors"
          >
            <X class="w-5 h-5 text-[#6B7280]" />
          </button>
        </div>
      </div>
    </div>

    <!-- Tabs -->
    <div class="flex border-b border-[#E5E7EB] bg-[#F9FAFB]">
      <button 
        v-for="tab in tabs" 
        :key="tab.id"
        @click="activeTab = tab.id"
        class="flex-1 px-4 py-3 text-sm font-medium transition-all duration-200 relative"
        :class="activeTab === tab.id ? 'text-[#3B82F6] bg-white border-b-2 border-[#3B82F6]' : 'text-[#6B7280] hover:text-[#374151]'"
      >
        <component :is="tab.icon" class="w-4 h-4 mx-auto mb-1" />
        {{ tab.label }}
      </button>
    </div>

    <!-- Tab Content -->
    <div class="flex-1 overflow-y-auto custom-scrollbar">
      <!-- Overview Tab -->
      <div v-if="activeTab === 'overview'" class="p-6 space-y-6">
        <!-- Metrics Cards -->
        <div class="grid grid-cols-2 gap-4">
          <!-- Total Shipments -->
          <div class="metric-card bg-[#F8FAFC] rounded-xl p-4 text-center">
            <div class="text-2xl font-bold text-[#1F2937] mb-1">{{ vendorMetrics.totalShipments }}</div>
            <div class="text-xs text-[#6B7280] uppercase tracking-wide">Total Shipments</div>
          </div>
          
          <!-- Customer Satisfaction -->
          <div class="metric-card bg-[#F8FAFC] rounded-xl p-4 text-center">
            <div class="text-2xl font-bold text-[#1F2937] mb-1">{{ vendorMetrics.satisfaction }}%</div>
            <div class="text-xs text-[#6B7280] uppercase tracking-wide">Satisfaction</div>
          </div>
        </div>

        <!-- On-time Delivery Circular Progress -->
        <div class="bg-[#F8FAFC] rounded-xl p-6">
          <h3 class="text-sm font-semibold text-[#374151] mb-4">On-time Delivery Rate</h3>
          <div class="flex items-center justify-center">
            <div class="relative w-24 h-24">
              <svg class="w-24 h-24 transform -rotate-90" viewBox="0 0 100 100">
                <!-- Background circle -->
                <circle cx="50" cy="50" r="40" stroke="#E5E7EB" stroke-width="8" fill="none" />
                <!-- Progress circle -->
                <circle 
                  cx="50" cy="50" r="40" 
                  stroke="#10B981" 
                  stroke-width="8" 
                  fill="none"
                  stroke-linecap="round"
                  :stroke-dasharray="progressCircumference"
                  :stroke-dashoffset="progressOffset"
                  class="transition-all duration-1000 ease-out"
                />
              </svg>
              <div class="absolute inset-0 flex items-center justify-center">
                <div class="text-center">
                  <div class="text-lg font-bold text-[#1F2937]">{{ vendorMetrics.onTimeRate }}%</div>
                  <div class="text-xs text-[#6B7280]">On-time</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Average Delivery Time -->
        <div class="bg-[#F8FAFC] rounded-xl p-4">
          <h3 class="text-sm font-semibold text-[#374151] mb-2">Average Delivery Time</h3>
          <div class="flex items-center gap-2">
            <Clock class="w-5 h-5 text-[#3B82F6]" />
            <span class="text-xl font-bold text-[#1F2937]">{{ vendorMetrics.avgDeliveryTime }}</span>
            <span class="text-sm text-[#6B7280]">hours</span>
          </div>
        </div>

        <!-- Recent Shipments -->
        <div>
          <h3 class="text-sm font-semibold text-[#374151] mb-3">Recent Shipments</h3>
          <div class="space-y-3">
            <div 
              v-for="shipment in recentShipments" 
              :key="shipment.id"
              class="bg-white border border-[#E5E7EB] rounded-lg p-3 hover:border-[#3B82F6] transition-colors"
            >
              <div class="flex items-center justify-between mb-2">
                <span class="font-mono text-sm text-[#3B82F6]">{{ shipment.id }}</span>
                <div :class="['px-2 py-1 rounded text-xs font-medium', getShipmentStatusClass(shipment.status)]">
                  {{ shipment.status }}
                </div>
              </div>
              <div class="text-sm text-[#6B7280]">{{ shipment.route }}</div>
              <div class="text-xs text-[#9CA3AF] mt-1">{{ shipment.date }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Performance Tab -->
      <div v-if="activeTab === 'performance'" class="p-6 space-y-6">
        <!-- Performance Chart Placeholder -->
        <div class="chart-animate bg-[#F8FAFC] rounded-xl p-6">
          <h3 class="text-sm font-semibold text-[#374151] mb-4">Monthly Performance</h3>
          <div class="h-48 flex items-center justify-center border-2 border-dashed border-[#D1D5DB] rounded-lg bg-white">
            <div class="text-center">
              <TrendingUp class="w-8 h-8 text-[#9CA3AF] mx-auto mb-2" />
              <p class="text-sm text-[#6B7280] font-medium">Performance Chart</p>
              <p class="text-xs text-[#9CA3AF]">Chart.js integration will display monthly trends</p>
            </div>
          </div>
        </div>

        <!-- Comparison Bars -->
        <div class="space-y-4">
          <h3 class="text-sm font-semibold text-[#374151]">vs Network Average</h3>
          
          <div class="space-y-3">
            <div v-for="(metric, index) in performanceMetrics" :key="metric.label" class="performance-metric space-y-2">
              <div class="flex justify-between text-sm">
                <span class="text-[#6B7280]">{{ metric.label }}</span>
                <div class="flex items-center gap-2">
                  <span class="font-semibold text-[#1F2937]">{{ metric.value }}%</span>
                  <component :is="metric.trend === 'up' ? TrendingUp : TrendingDown" 
                             :class="metric.trend === 'up' ? 'text-[#10B981]' : 'text-[#EF4444]'" 
                             class="w-4 h-4" />
                </div>
              </div>
              <div class="w-full bg-[#E5E7EB] rounded-full h-2">
                <div 
                  :class="[
                    'h-2 rounded-full transition-all duration-1000 ease-out progress-bar',
                    metric.value >= 70 ? 'bg-[#10B981]' : metric.value >= 50 ? 'bg-[#F59E0B]' : 'bg-[#EF4444]'
                  ]"
                  :style="{ width: `${metric.value}%`, animationDelay: `${index * 100}ms` }"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Performance Badges -->
        <div>
          <h3 class="text-sm font-semibold text-[#374151] mb-3">Performance Badges</h3>
          <div class="flex flex-wrap gap-2">
            <div 
              v-for="badge in performanceBadges" 
              :key="badge.name"
              class="flex items-center gap-2 px-3 py-2 bg-gradient-to-r from-[#F59E0B] to-[#D97706] text-white rounded-lg text-sm font-medium"
            >
              <Award class="w-4 h-4" />
              {{ badge.name }}
            </div>
          </div>
        </div>
      </div>

      <!-- History Tab -->
      <div v-if="activeTab === 'history'" class="p-6">
        <h3 class="text-sm font-semibold text-[#374151] mb-4">Shipment History</h3>
        
        <!-- Timeline -->
        <div class="relative">
          <!-- Timeline line -->
          <div class="absolute left-5 top-0 bottom-0 w-0.5 bg-[#E5E7EB]"></div>
          
          <div class="space-y-4">
            <div 
              v-for="(event, index) in shipmentHistory" 
              :key="event.id"
              class="timeline-item relative flex items-start gap-4 pl-12"
              :style="{ animationDelay: `${index * 50}ms` }"
            >
              <!-- Timeline dot -->
              <div :class="['absolute left-2 w-2.5 h-2.5 rounded-full border-2 border-white shadow-sm', getEventDotColor(event.type)]"></div>
              
              <div class="flex-1 bg-white border border-[#E5E7EB] rounded-lg p-3 hover:border-[#3B82F6] transition-colors">
                <div class="flex items-center justify-between mb-1">
                  <span class="font-mono text-sm text-[#3B82F6]">{{ event.shipmentId }}</span>
                  <span class="text-xs text-[#9CA3AF]">{{ event.timestamp }}</span>
                </div>
                <p class="text-sm text-[#6B7280]">{{ event.description }}</p>
                <div v-if="event.status" :class="['inline-block px-2 py-1 rounded text-xs font-medium mt-2', getShipmentStatusClass(event.status)]">
                  {{ event.status }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Contact Tab -->
      <div v-if="activeTab === 'contact'" class="p-6 space-y-6">
        <!-- Contact Information Grid -->
        <div class="grid gap-4">
          <!-- Primary Contact -->
          <div class="bg-[#F8FAFC] rounded-xl p-4 border-l-4 border-[#3B82F6]">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-semibold text-[#374151]">Primary Contact</h3>
              <span class="px-2 py-1 bg-[#3B82F6] text-white text-xs font-medium rounded-full">Primary</span>
            </div>
            <div class="space-y-3">
              <div>
                <div class="text-sm font-medium text-[#1F2937] mb-1">John Smith</div>
                <div class="text-xs text-[#6B7280]">Operations Manager</div>
              </div>
              <div class="flex items-center gap-3">
                <Phone class="w-4 h-4 text-[#3B82F6]" />
                <span class="text-sm">{{ vendor?.contact?.phone }}</span>
              </div>
              <div class="flex items-center gap-3">
                <Mail class="w-4 h-4 text-[#3B82F6]" />
                <span class="text-sm">{{ vendor?.contact?.email }}</span>
              </div>
            </div>
          </div>

          <!-- Secondary Contact -->
          <div class="bg-[#F8FAFC] rounded-xl p-4 border-l-4 border-[#F59E0B]">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-semibold text-[#374151]">Secondary Contact</h3>
              <span class="px-2 py-1 bg-[#F59E0B] text-white text-xs font-medium rounded-full">Secondary</span>
            </div>
            <div class="space-y-3">
              <div>
                <div class="text-sm font-medium text-[#1F2937] mb-1">Sarah Johnson</div>
                <div class="text-xs text-[#6B7280]">Customer Service Lead</div>
              </div>
              <div class="flex items-center gap-3">
                <Phone class="w-4 h-4 text-[#F59E0B]" />
                <span class="text-sm">+91 98765 43211</span>
              </div>
              <div class="flex items-center gap-3">
                <Mail class="w-4 h-4 text-[#F59E0B]" />
                <span class="text-sm">sarah@expresslogistics.com</span>
              </div>
            </div>
          </div>

          <!-- Emergency Contact -->
          <div class="bg-[#F8FAFC] rounded-xl p-4 border-l-4 border-[#EF4444]">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-semibold text-[#374151]">Emergency Contact</h3>
              <span class="px-2 py-1 bg-[#EF4444] text-white text-xs font-medium rounded-full">Emergency</span>
            </div>
            <div class="space-y-3">
              <div>
                <div class="text-sm font-medium text-[#1F2937] mb-1">Emergency Hotline</div>
                <div class="text-xs text-[#6B7280]">24/7 Support</div>
              </div>
              <div class="flex items-center gap-3">
                <Phone class="w-4 h-4 text-[#EF4444]" />
                <span class="text-sm">+91 98765 00000</span>
              </div>
              <div class="flex items-center gap-3">
                <Mail class="w-4 h-4 text-[#EF4444]" />
                <span class="text-sm">emergency@expresslogistics.com</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Business Hours -->
        <div class="bg-[#F8FAFC] rounded-xl p-4">
          <h3 class="text-sm font-semibold text-[#374151] mb-3">Business Hours</h3>
          <div class="space-y-2 text-sm">
            <div class="flex justify-between items-center">
              <span class="text-[#6B7280]">Monday - Friday</span>
              <span class="font-medium text-[#1F2937]">9:00 AM - 6:00 PM</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-[#6B7280]">Saturday</span>
              <span class="font-medium text-[#1F2937]">9:00 AM - 2:00 PM</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-[#6B7280]">Sunday</span>
              <span class="font-medium text-[#EF4444]">Closed</span>
            </div>
            <div class="flex justify-between items-center pt-2 border-t border-[#E5E7EB]">
              <span class="text-[#6B7280]">Time Zone</span>
              <span class="font-medium text-[#1F2937]">IST (GMT+5:30)</span>
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="space-y-3">
          <button 
            @click="$emit('message', vendor)"
            class="action-button w-full bg-[#3B82F6] text-white py-3 rounded-xl hover:bg-[#2563EB] transition-all flex items-center justify-center gap-2 font-medium"
          >
            <MessageSquare class="w-4 h-4" />
            Send Message
          </button>
          <button 
            :href="`tel:${vendor?.contact?.phone}`"
            class="action-button w-full border border-[#E5E7EB] text-[#374151] py-3 rounded-xl hover:border-[#3B82F6] hover:text-[#3B82F6] transition-all flex items-center justify-center gap-2 font-medium"
          >
            <Phone class="w-4 h-4" />
            Call Now
          </button>
        </div>
      </div>
      </div>
    </div>
  </transition>
</template><script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { 
  X, Star, Package, TrendingUp, TrendingDown, Award, Clock, 
  Phone, Mail, MessageSquare, Eye, BarChart3, History, User,
  MoreHorizontal, Edit, Send, Download, UserX, Trash2
} from 'lucide-vue-next'

// Props
const props = defineProps({
  vendor: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value === 'object' && value.id && value.name
    }
  },
  isOpen: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['close', 'edit', 'delete', 'message'])

// State
const activeTab = ref('overview')
const showQuickActions = ref(false)

// Tabs configuration
const tabs = [
  { id: 'overview', label: 'Overview', icon: Eye },
  { id: 'performance', label: 'Performance', icon: BarChart3 },
  { id: 'history', label: 'History', icon: History },
  { id: 'contact', label: 'Contact', icon: User }
]

// Quick Actions configuration
const quickActions = [
  { id: 'edit', label: 'Edit Vendor', icon: Edit, action: 'edit' },
  { id: 'performance', label: 'View Performance', icon: Eye, action: 'view-performance' },
  { id: 'assign', label: 'Assign Shipment', icon: Package, action: 'assign-shipment' },
  { id: 'message', label: 'Send Message', icon: MessageSquare, action: 'message' },
  { id: 'download', label: 'Download Report', icon: Download, action: 'download-report' },
  { id: 'inactive', label: 'Mark Inactive', icon: UserX, action: 'mark-inactive' },
  { id: 'delete', label: 'Delete', icon: Trash2, action: 'delete', danger: true }
]

// Mock data - in real app, this would come from vendor prop or API
const vendorMetrics = computed(() => ({
  totalShipments: props.vendor?.totalShipments || 42,
  onTimeRate: props.vendor?.onTimeRate || 94,
  avgDeliveryTime: props.vendor?.avgDeliveryTime || 18,
  satisfaction: props.vendor?.satisfaction || 96
}))

const performanceMetrics = computed(() => [
  { label: 'On-time Delivery', value: vendorMetrics.value.onTimeRate, trend: 'up' },
  { label: 'Customer Satisfaction', value: vendorMetrics.value.satisfaction, trend: 'up' },
  { label: 'Cost Efficiency', value: props.vendor?.costEfficiency || 87, trend: 'down' },
  { label: 'Communication', value: props.vendor?.communication || 92, trend: 'up' }
])

const performanceBadges = computed(() => {
  const badges = []
  if (vendorMetrics.value.onTimeRate >= 95) badges.push({ name: 'Top Performer' })
  if (vendorMetrics.value.satisfaction >= 95) badges.push({ name: 'Reliable Partner' })
  if (props.vendor?.rating >= 4.8) badges.push({ name: 'Quality Service' })
  return badges
})

const recentShipments = computed(() => 
  props.vendor?.recentShipments || [
    { id: 'SH001', route: 'Mumbai → Delhi', status: 'Delivered', date: '2 days ago' },
    { id: 'SH002', route: 'Chennai → Bangalore', status: 'In Transit', date: '1 day ago' },
    { id: 'SH003', route: 'Kolkata → Hyderabad', status: 'Delivered', date: '3 days ago' },
    { id: 'SH004', route: 'Pune → Ahmedabad', status: 'Picked Up', date: '4 hours ago' },
    { id: 'SH005', route: 'Jaipur → Lucknow', status: 'Created', date: '1 hour ago' }
  ]
)

const shipmentHistory = computed(() => 
  props.vendor?.shipmentHistory || [
    { id: 1, shipmentId: 'SH001', description: 'Shipment delivered successfully', status: 'Delivered', timestamp: '2 days ago', type: 'delivered' },
    { id: 2, shipmentId: 'SH002', description: 'Package in transit to destination', status: 'In Transit', timestamp: '1 day ago', type: 'transit' },
    { id: 3, shipmentId: 'SH003', description: 'Pickup completed from origin', status: 'Picked Up', timestamp: '3 days ago', type: 'pickup' },
    { id: 4, shipmentId: 'SH004', description: 'Shipment created and scheduled', status: 'Created', timestamp: '4 days ago', type: 'created' }
  ]
)

// Computed properties
const progressCircumference = computed(() => 2 * Math.PI * 40)
const progressOffset = computed(() => {
  const percentage = vendorMetrics.value.onTimeRate
  return progressCircumference.value - (percentage / 100) * progressCircumference.value
})

// Helper functions
const getInitials = (name) => {
  if (!name) return 'VN'
  return name
    .split(' ')
    .map(word => word.charAt(0))
    .join('')
    .substring(0, 2)
    .toUpperCase()
}

const getShipmentStatusClass = (status) => {
  switch (status) {
    case 'Delivered': return 'bg-[#D1FAE5] text-[#065F46]'
    case 'In Transit': return 'bg-[#DBEAFE] text-[#1E40AF]'
    case 'Picked Up': return 'bg-[#E0F2FE] text-[#0C4A6E]'
    case 'Created': return 'bg-[#F3F4F6] text-[#374151]'
    default: return 'bg-[#F3F4F6] text-[#374151]'
  }
}

const getEventDotColor = (type) => {
  switch (type) {
    case 'delivered': return 'bg-[#10B981]'
    case 'transit': return 'bg-[#3B82F6]'
    case 'pickup': return 'bg-[#F59E0B]'
    case 'created': return 'bg-[#6B7280]'
    default: return 'bg-[#6B7280]'
  }
}

const handleQuickAction = (action) => {
  showQuickActions.value = false
  
  switch (action.action) {
    case 'edit':
      emit('edit', props.vendor)
      break
    case 'delete':
      emit('delete', props.vendor)
      break
    case 'message':
      emit('message', props.vendor)
      break
    case 'view-performance':
      activeTab.value = 'performance'
      break
    case 'assign-shipment':
      console.log('Assign shipment to vendor:', props.vendor.id)
      break
    case 'download-report':
      console.log('Download report for vendor:', props.vendor.id)
      break
    case 'mark-inactive':
      console.log('Mark vendor inactive:', props.vendor.id)
      break
    default:
      console.log('Quick action:', action.action, props.vendor)
  }
}

const closeQuickActions = () => {
  showQuickActions.value = false
}

// Reset active tab when panel opens
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    activeTab.value = 'overview'
    // Add escape key listener
    document.addEventListener('keydown', handleEscapeKey)
    // Add click outside listener for dropdown
    document.addEventListener('click', handleClickOutside)
  } else {
    // Remove listeners when panel closes
    document.removeEventListener('keydown', handleEscapeKey)
    document.removeEventListener('click', handleClickOutside)
  }
})

// Handle escape key
const handleEscapeKey = (event) => {
  if (event.key === 'Escape') {
    if (showQuickActions.value) {
      showQuickActions.value = false
    } else {
      emit('close')
    }
  }
}

// Handle click outside
const handleClickOutside = (event) => {
  // This will be handled by the quick actions dropdown specifically
  const dropdown = event.target.closest('.absolute')
  if (!dropdown) {
    showQuickActions.value = false
  }
}

// Cleanup on unmount
onUnmounted(() => {
  document.removeEventListener('keydown', handleEscapeKey)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* Custom scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Backdrop transitions */
.backdrop-enter-active, .backdrop-leave-active {
  transition: opacity 300ms ease;
}
.backdrop-enter-from, .backdrop-leave-to {
  opacity: 0;
}

/* Slide-over transitions */
.slide-over-enter-active, .slide-over-leave-active {
  transition: transform 300ms ease-out;
}
.slide-over-enter-from, .slide-over-leave-to {
  transform: translateX(100%);
}

/* Tab content transitions */
.tab-content-enter-active, .tab-content-leave-active {
  transition: opacity 200ms ease;
}
.tab-content-enter-from, .tab-content-leave-to {
  opacity: 0;
}

/* Dropdown transitions */
.dropdown-enter-active, .dropdown-leave-active {
  transition: all 150ms ease;
  transform-origin: top right;
}
.dropdown-enter-from, .dropdown-leave-to {
  opacity: 0;
  transform: scale(0.95) translateY(-5px);
}

/* Chart animations */
@keyframes chartSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.chart-animate {
  animation: chartSlideIn 600ms ease-out;
}

/* Progress bar animations */
@keyframes progressFill {
  from {
    width: 0%;
  }
}

.progress-bar {
  animation: progressFill 1000ms ease-out;
}

/* Stagger animation for performance bars */
.performance-metric:nth-child(1) { animation-delay: 0ms; }
.performance-metric:nth-child(2) { animation-delay: 100ms; }
.performance-metric:nth-child(3) { animation-delay: 200ms; }
.performance-metric:nth-child(4) { animation-delay: 300ms; }

/* Card hover effects */
.metric-card {
  transition: all 200ms ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Timeline animations */
@keyframes timelineSlide {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.timeline-item {
  animation: timelineSlide 400ms ease-out;
}

.timeline-item:nth-child(1) { animation-delay: 0ms; }
.timeline-item:nth-child(2) { animation-delay: 50ms; }
.timeline-item:nth-child(3) { animation-delay: 100ms; }
.timeline-item:nth-child(4) { animation-delay: 150ms; }
.timeline-item:nth-child(5) { animation-delay: 200ms; }

/* Button animations */
.action-button {
  transition: all 200ms ease;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.3);
}

.action-button:active {
  transform: translateY(0);
}
</style>