<template>
  <div class="space-y-7 animate-fade-in">
    <!-- Header Section -->
    <div class="flex justify-between items-start">
      <div>
        <h1 class="text-4xl font-bold text-[#0F172A] mb-2 tracking-tight">Dashboard</h1>
        <p class="text-[#64748B] text-base leading-relaxed">Welcome back! Here's your supply chain overview.</p>
      </div>
      <button 
        @click="$emit('new-shipment')"
        class="bg-gradient-to-br from-[#3B82F6] to-[#2563EB] text-white px-6 py-3 rounded-xl hover:scale-102 active:scale-98 hover:shadow-lg shadow-[0_4px_12px_rgba(59,130,246,0.3)] transition-all duration-200 flex items-center gap-3 font-semibold"
      >
        <Plus class="w-5 h-5" />
        New Shipment
      </button>
    </div>

    <!-- Metrics Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
      <div 
        v-for="(metric, index) in metrics" 
        :key="metric.id"
        class="bg-white rounded-[20px] p-7 shadow-[0_1px_3px_rgba(0,0,0,0.03),0_8px_16px_rgba(0,0,0,0.04)] hover:shadow-[0_8px_24px_rgba(0,0,0,0.12)] hover:-translate-y-0.5 hover:scale-102 transition-all duration-300 cursor-pointer group"
        :style="{ animationDelay: `${index * 100}ms` }"
      >
        <div class="flex items-start justify-between mb-6">
          <div :class="['w-12 h-12 rounded-full flex items-center justify-center shadow-sm', metric.iconBgColor]">
            <component :is="metric.icon" :class="['w-7 h-7', metric.iconColor]" />
          </div>
        </div>
        <div class="mb-4">
          <div class="text-5xl font-bold text-[#0F172A] leading-none tracking-tight mb-2">{{ metric.value }}</div>
          <div class="text-[13px] font-medium text-[#6B7280]">{{ metric.label }}</div>
        </div>
        <div :class="['inline-flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-[13px] font-semibold', getChangeBadgeClass(metric.changeType)]">
          <component :is="getChangeIcon(metric.changeType)" class="w-3.5 h-3.5" />
          {{ metric.change }}
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-7">
      <!-- Recent Activity -->
      <div class="lg:col-span-2 bg-white rounded-[20px] p-6 shadow-[0_1px_3px_rgba(0,0,0,0.03),0_8px_16px_rgba(0,0,0,0.04)] hover:shadow-[0_8px_24px_rgba(0,0,0,0.08)] transition-all duration-300">
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center gap-3">
            <Clock class="w-5 h-5 text-[#6B7280]" />
            <h2 class="text-lg font-semibold text-[#1E293B]">Recent Activity</h2>
          </div>
          <button class="text-sm text-[#3B82F6] hover:underline font-medium">View all</button>
        </div>
        
        <div class="relative">
          <!-- Timeline line -->
          <div class="absolute left-5 top-0 bottom-0 w-0.5 bg-[#E5E7EB]"></div>
          
          <div class="space-y-3">
            <div 
              v-for="(activity, index) in activities" 
              :key="activity.id"
              @click="$emit('activity-click', activity)"
              class="relative flex items-start gap-4 p-4 pl-12 rounded-xl hover:bg-[#F9FAFB] hover:translate-x-0.5 cursor-pointer transition-all duration-200 group"
            >
              <!-- Timeline dot with icon -->
              <div :class="['absolute left-2 w-2.5 h-2.5 rounded-full border-2 border-white shadow-sm', getActivityDotColor(activity.type)]"></div>
              
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <div class="flex items-center gap-2 mb-1">
                      <component :is="getActivityIcon(activity.type)" :class="['w-4 h-4', getActivityIconColor(activity.type)]" />
                      <p class="text-sm text-[#475569]">
                        <span class="font-semibold text-[#1A1A1A]">{{ activity.shipmentId }}</span>
                        {{ activity.action }}
                      </p>
                    </div>
                    <p class="text-xs text-[#9CA3AF] font-medium">{{ activity.timestamp }}</p>
                  </div>
                  <div v-if="activity.status" :class="['px-2.5 py-1 rounded-md text-[11px] font-bold uppercase tracking-wider', getStatusBadgeClass(activity.status)]">
                    {{ activity.status }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="space-y-6">
        <!-- Quick Actions -->
        <div class="bg-white rounded-[20px] p-6 shadow-[0_1px_3px_rgba(0,0,0,0.03),0_8px_16px_rgba(0,0,0,0.04)] hover:shadow-[0_8px_24px_rgba(0,0,0,0.08)] transition-all duration-300">
          <h2 class="text-lg font-semibold text-[#1E293B] mb-5">Quick Actions</h2>
          <div class="space-y-3.5">
            <button 
              v-for="action in quickActions" 
              :key="action.id"
              @click="$emit('action-click', action.id)"
              class="w-full h-[52px] bg-white border-2 border-[#E5E7EB] text-[#374151] rounded-xl hover:border-[#3B82F6] hover:bg-gradient-to-br hover:from-white hover:to-[#F0F9FF] hover:text-[#2563EB] hover:-translate-y-0.5 hover:shadow-[0_4px_12px_rgba(59,130,246,0.15)] active:scale-98 transition-all duration-200 flex items-center justify-center gap-3 font-semibold text-[15px] group"
            >
              <component :is="action.icon" class="w-5.5 h-5.5 text-[#6B7280] group-hover:text-[#3B82F6] transition-colors duration-200" />
              {{ action.label }}
            </button>
          </div>
        </div>

        <!-- Shipment Status -->
        <div class="bg-white rounded-[20px] p-6 shadow-[0_1px_3px_rgba(0,0,0,0.03),0_8px_16px_rgba(0,0,0,0.04)] hover:shadow-[0_8px_24px_rgba(0,0,0,0.08)] transition-all duration-300">
          <h2 class="text-lg font-semibold text-[#1E293B] mb-5">Shipment Status</h2>
          
          <!-- Status Bar with gaps -->
          <div class="h-12 bg-[#F3F4F6] rounded-xl overflow-hidden mb-5 flex gap-1 p-1">
            <div 
              v-for="(status, index) in shipmentStatuses" 
              :key="status.status"
              :class="[status.gradientClass, 'rounded-lg transition-all duration-500 hover:scale-y-105 cursor-pointer']"
              :style="{ width: `calc(${status.percentage}% - 4px)`, animationDelay: `${index * 150}ms` }"
            ></div>
          </div>

          <!-- Legend -->
          <div class="space-y-3">
            <div 
              v-for="status in shipmentStatuses" 
              :key="status.status"
              class="flex items-center justify-between text-sm group cursor-pointer"
            >
              <div class="flex items-center gap-3">
                <div :class="['w-2.5 h-2.5 rounded-full shadow-sm transition-transform duration-200 group-hover:scale-125', status.color]"></div>
                <span class="text-[#4B5563] font-medium">{{ status.status }}</span>
              </div>
              <span class="text-[#1F2937] font-semibold">({{ status.count }})</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { 
  Plus, Clock, Truck, Package, Route, Users, 
  TrendingUp, TrendingDown, ArrowUp, ArrowDown, Minus,
  CheckCircle, AlertCircle, MapPin, UserPlus
} from 'lucide-vue-next'

// Reactive data with premium styling
const metrics = reactive([
  {
    id: 'shipments',
    label: 'Total Shipments',
    value: 5,
    change: '+2 from last week',
    changeType: 'positive',
    icon: Truck,
    iconBgColor: 'bg-[#EFF6FF]',
    iconColor: 'text-[#3B82F6]'
  },
  {
    id: 'cargo',
    label: 'Active Cargo',
    value: 8,
    change: '+3 new items',
    changeType: 'positive',
    icon: Package,
    iconBgColor: 'bg-[#ECFDF5]',
    iconColor: 'text-[#10B981]'
  },
  {
    id: 'routes',
    label: 'Available Routes',
    value: 3,
    change: '1 delayed',
    changeType: 'warning',
    icon: Route,
    iconBgColor: 'bg-[#FEF3C7]',
    iconColor: 'text-[#F59E0B]'
  },
  {
    id: 'vendors',
    label: 'Partner Vendors',
    value: 4,
    change: 'All verified',
    changeType: 'positive',
    icon: Users,
    iconBgColor: 'bg-[#F3E8FF]',
    iconColor: 'text-[#A855F7]'
  }
])

const activities = reactive([
  {
    id: '1',
    shipmentId: 'SH001',
    action: 'has been picked up from Mumbai warehouse',
    timestamp: 'in 2 days',
    type: 'picked-up',
    status: 'picked-up'
  },
  {
    id: '2',
    shipmentId: 'SH002',
    action: 'new cargo added (Electronics)',
    timestamp: 'in 3 days',
    type: 'cargo'
  },
  {
    id: '3',
    shipmentId: 'SH003',
    action: 'route updated - Delhi to Bangalore',
    timestamp: 'in 5 days',
    type: 'route'
  },
  {
    id: '4',
    shipmentId: 'SH004',
    action: 'delivered successfully',
    timestamp: 'in 1 week',
    type: 'delivered'
  },
  {
    id: '5',
    shipmentId: 'SH005',
    action: 'is experiencing delays',
    timestamp: 'in 1 week',
    type: 'delayed',
    status: 'delayed'
  }
])

const quickActions = reactive([
  { id: 'add-cargo', label: 'Add New Cargo', icon: Plus },
  { id: 'create-route', label: 'Create Route', icon: MapPin },
  { id: 'add-vendor', label: 'Add Vendor', icon: UserPlus }
])

const shipmentStatuses = reactive([
  { 
    status: 'In Transit', 
    count: 1, 
    color: 'bg-[#14B8A6]', 
    percentage: 20,
    gradientClass: 'bg-gradient-to-r from-[#14B8A6] to-[#0D9488]'
  },
  { 
    status: 'Delivered', 
    count: 1, 
    color: 'bg-[#10B981]', 
    percentage: 20,
    gradientClass: 'bg-gradient-to-r from-[#10B981] to-[#059669]'
  },
  { 
    status: 'Picked Up', 
    count: 1, 
    color: 'bg-[#3B82F6]', 
    percentage: 20,
    gradientClass: 'bg-gradient-to-r from-[#3B82F6] to-[#2563EB]'
  },
  { 
    status: 'Created', 
    count: 1, 
    color: 'bg-[#6B7280]', 
    percentage: 20,
    gradientClass: 'bg-gradient-to-r from-[#6B7280] to-[#4B5563]'
  },
  { 
    status: 'Delayed', 
    count: 1, 
    color: 'bg-[#F59E0B]', 
    percentage: 20,
    gradientClass: 'bg-gradient-to-r from-[#F59E0B] to-[#D97706]'
  }
])

// Helper functions for premium styling
const getChangeBadgeClass = (type) => {
  switch (type) {
    case 'positive': return 'bg-[#D1FAE5] text-[#065F46]'
    case 'negative': return 'bg-[#FEE2E2] text-[#991B1B]'
    case 'warning': return 'bg-[#FEF3C7] text-[#92400E]'
    default: return 'bg-[#E0E7FF] text-[#3730A3]'
  }
}

const getChangeIcon = (type) => {
  switch (type) {
    case 'positive': return ArrowUp
    case 'negative': return ArrowDown
    case 'warning': return Minus
    default: return ArrowUp
  }
}

const getActivityDotColor = (type) => {
  switch (type) {
    case 'picked-up': return 'bg-[#3B82F6]'
    case 'cargo': return 'bg-[#10B981]'
    case 'route': return 'bg-[#F59E0B]'
    case 'delivered': return 'bg-[#8B5CF6]'
    case 'delayed': return 'bg-[#EF4444]'
    default: return 'bg-[#6B7280]'
  }
}

const getActivityIcon = (type) => {
  switch (type) {
    case 'picked-up': return CheckCircle
    case 'cargo': return Plus
    case 'route': return Route
    case 'delivered': return Package
    case 'delayed': return AlertCircle
    default: return CheckCircle
  }
}

const getActivityIconColor = (type) => {
  switch (type) {
    case 'picked-up': return 'text-[#3B82F6]'
    case 'cargo': return 'text-[#10B981]'
    case 'route': return 'text-[#F59E0B]'
    case 'delivered': return 'text-[#8B5CF6]'
    case 'delayed': return 'text-[#EF4444]'
    default: return 'text-[#6B7280]'
  }
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'delayed': return 'bg-[#FEE2E2] text-[#991B1B]'
    case 'picked-up': return 'bg-[#DBEAFE] text-[#1E40AF]'
    case 'in-transit': return 'bg-[#E0F2FE] text-[#0C4A6E]'
    default: return 'bg-[#F3F4F6] text-[#374151]'
  }
}
</script>