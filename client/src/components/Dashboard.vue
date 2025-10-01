<template>
  <main class="min-h-screen bg-[#F5F5F7] p-10">
    <div class="max-w-[1400px] mx-auto">
      <!-- Header Section -->
      <header class="flex justify-between items-start mb-10">
        <div>
          <h1 class="text-[32px] font-bold text-[#1A1A1A] mb-2">Dashboard</h1>
          <p class="text-[15px] text-[#6B7280]">Welcome back! Here's your supply chain overview.</p>
        </div>
        <button 
          @click="$emit('new-shipment')"
          class="bg-[#3B82F6] text-white px-6 py-3 rounded-xl hover:shadow-lg transition-all duration-200 hover:scale-105 flex items-center gap-3"
        >
          <Plus class="w-5 h-5" />
          New Shipment
        </button>
      </header>

      <!-- Metrics Grid -->
      <section class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6 mb-10">
        <div 
          v-for="metric in metrics" 
          :key="metric.id"
          class="bg-white rounded-3xl p-7 shadow-[0_2px_12px_rgba(0,0,0,0.04),_0_1px_4px_rgba(0,0,0,0.02)] hover:shadow-[0_8px_25px_rgba(0,0,0,0.1)] transition-all duration-300 hover:-translate-y-1 hover:scale-[1.02]"
        >
          <div class="flex items-start justify-between mb-4">
            <div :class="['w-10 h-10 rounded-full flex items-center justify-center', metric.iconBgColor]">
              <component :is="metric.icon" :class="['w-6 h-6', metric.iconColor]" />
            </div>
          </div>
          <div class="mb-2">
            <div class="text-[40px] font-bold text-[#1A1A1A] leading-none">{{ metric.value }}</div>
            <div class="text-[12px] font-medium text-[#9CA3AF] mt-1">{{ metric.label }}</div>
          </div>
          <div :class="['text-[12px] flex items-center gap-1', getChangeColor(metric.changeType)]">
            <component :is="getChangeIcon(metric.changeType)" class="w-3 h-3" />
            {{ metric.change }}
          </div>
        </div>
      </section>

      <!-- Main Content -->
      <div class="grid grid-cols-1 lg:grid-cols-5 gap-6 mb-10 items-stretch">
        <!-- Recent Activity -->
        <section class="lg:col-span-3 flex">
          <div class="bg-white rounded-3xl p-7 shadow-[0_2px_12px_rgba(0,0,0,0.04),_0_1px_4px_rgba(0,0,0,0.02)] w-full flex flex-col">
            <div class="flex items-center gap-3 mb-6">
              <Clock class="w-5 h-5 text-[#6B7280]" />
              <h2 class="text-[16px] font-semibold text-[#1A1A1A]">Recent Activity</h2>
            </div>
            <div class="space-y-4 flex-1">
              <div 
                v-for="(activity, index) in activities" 
                :key="activity.id"
                @click="$emit('activity-click', activity)"
                class="relative flex items-start gap-4 p-4 rounded-2xl hover:bg-[#F9FAFB] cursor-pointer transition-colors duration-200"
              >
                <!-- Timeline line -->
                <div v-if="index < activities.length - 1" class="absolute left-5 top-10 w-px h-8 bg-[#E5E7EB]"></div>
                
                <!-- Status dot -->
                <div :class="['w-2 h-2 rounded-full mt-2 flex-shrink-0', getActivityDotColor(activity.type)]"></div>
                
                <div class="flex-1 min-w-0">
                  <div class="flex items-start justify-between">
                    <div>
                      <p class="text-[14px] text-[#4B5563]">
                        <span class="font-semibold text-[#1A1A1A]">{{ activity.shipmentId }}</span>
                        {{ activity.action }}
                      </p>
                      <p class="text-[12px] text-[#9CA3AF] mt-1">{{ activity.timestamp }}</p>
                    </div>
                    <div v-if="activity.status" :class="['px-2 py-1 rounded-full text-[10px] font-medium', getStatusBadgeClass(activity.status)]">
                      {{ activity.status }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Right Column -->
        <section class="lg:col-span-2 flex flex-col gap-6">
          <!-- Quick Actions -->
          <div class="bg-white rounded-3xl p-7 shadow-[0_2px_12px_rgba(0,0,0,0.04),_0_1px_4px_rgba(0,0,0,0.02)] flex-1">
            <h2 class="text-[16px] font-semibold text-[#1A1A1A] mb-6">Quick Actions</h2>
            <div class="space-y-3">
              <button 
                v-for="action in quickActions" 
                :key="action.id"
                @click="$emit('action-click', action.id)"
                class="w-full h-12 border border-[#3B82F6] text-[#3B82F6] rounded-xl hover:bg-[#3B82F6] hover:text-white transition-all duration-200 flex items-center justify-center gap-3"
              >
                <component :is="action.icon" class="w-5 h-5" />
                {{ action.label }}
              </button>
            </div>
          </div>

          <!-- Shipment Status -->
          <div class="bg-white rounded-3xl p-7 shadow-[0_2px_12px_rgba(0,0,0,0.04),_0_1px_4px_rgba(0,0,0,0.02)] flex-1">
            <h2 class="text-[16px] font-semibold text-[#1A1A1A] mb-6">Shipment Status</h2>
            
            <!-- Status Bar -->
            <div class="h-10 bg-[#F3F4F6] rounded-xl overflow-hidden mb-4 flex">
              <div 
                v-for="status in shipmentStatuses" 
                :key="status.status"
                :class="[status.color, 'transition-all duration-500']"
                :style="{ width: status.percentage + '%' }"
              ></div>
            </div>

            <!-- Legend -->
            <div class="space-y-2">
              <div 
                v-for="status in shipmentStatuses" 
                :key="status.status"
                class="flex items-center justify-between text-[12px]"
              >
                <div class="flex items-center gap-2">
                  <div :class="['w-2 h-2 rounded-full', status.color]"></div>
                  <span class="text-[#4B5563]">{{ status.status }}</span>
                </div>
                <span class="text-[#9CA3AF]">({{ status.count }})</span>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { 
  Plus, Clock, Truck, Package, Route, Users, 
  TrendingUp, TrendingDown,
  MapPin, UserPlus, ArrowUp, ArrowDown, Minus
} from 'lucide-vue-next'

// Types
interface Metric {
  id: string
  label: string
  value: number
  change: string
  changeType: 'positive' | 'negative' | 'neutral'
  icon: any
  iconBgColor: string
  iconColor: string
}

interface Activity {
  id: string
  shipmentId: string
  action: string
  timestamp: string
  type: 'status' | 'cargo' | 'route' | 'vendor'
  status?: 'delayed' | 'picked-up' | 'in-transit'
}

interface ShipmentStatus {
  status: string
  count: number
  color: string
  percentage: number
}

interface QuickAction {
  id: string
  label: string
  icon: any
}

// Props
defineProps<{
  userId?: string
  refreshInterval?: number
}>()

// Emits
defineEmits<{
  'new-shipment': []
  'activity-click': [activity: Activity]
  'action-click': [actionId: string]
}>()

// Reactive data
const metrics = reactive<Metric[]>([
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
    iconBgColor: 'bg-[#F0FDFA]',
    iconColor: 'text-[#14B8A6]'
  },
  {
    id: 'routes',
    label: 'Available Routes',
    value: 3,
    change: '1 delayed',
    changeType: 'neutral',
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
    iconBgColor: 'bg-[#F5F3FF]',
    iconColor: 'text-[#8B5CF6]'
  }
])

const activities = reactive<Activity[]>([
  {
    id: '1',
    shipmentId: 'SH001',
    action: 'has been picked up from Mumbai warehouse',
    timestamp: 'in 2 days',
    type: 'status',
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
    type: 'status'
  },
  {
    id: '5',
    shipmentId: 'SH005',
    action: 'is experiencing delays',
    timestamp: 'in 1 week',
    type: 'status',
    status: 'delayed'
  }
])

const quickActions = reactive<QuickAction[]>([
  { id: 'add-cargo', label: 'Add New Cargo', icon: Plus },
  { id: 'create-route', label: 'Create Route', icon: MapPin },
  { id: 'add-vendor', label: 'Add Vendor', icon: UserPlus }
])

const shipmentStatuses = reactive<ShipmentStatus[]>([
  { status: 'In Transit', count: 1, color: 'bg-[#14B8A6]', percentage: 20 },
  { status: 'Delivered', count: 1, color: 'bg-[#10B981]', percentage: 20 },
  { status: 'Picked Up', count: 1, color: 'bg-[#3B82F6]', percentage: 20 },
  { status: 'Created', count: 1, color: 'bg-[#9CA3AF]', percentage: 20 },
  { status: 'Delayed', count: 1, color: 'bg-[#F59E0B]', percentage: 20 }
])

// Helper functions
const getChangeColor = (type: string) => {
  switch (type) {
    case 'positive': return 'text-[#10B981]'
    case 'negative': return 'text-[#EF4444]'
    default: return 'text-[#F59E0B]'
  }
}

const getChangeIcon = (type: string) => {
  switch (type) {
    case 'positive': return ArrowUp
    case 'negative': return ArrowDown
    default: return Minus
  }
}

const getActivityDotColor = (type: string) => {
  switch (type) {
    case 'status': return 'bg-[#3B82F6]'
    case 'cargo': return 'bg-[#10B981]'
    case 'route': return 'bg-[#F59E0B]'
    case 'vendor': return 'bg-[#8B5CF6]'
    default: return 'bg-[#9CA3AF]'
  }
}

const getStatusBadgeClass = (status: string) => {
  switch (status) {
    case 'delayed': return 'bg-[#FEF3C7] text-[#92400E]'
    case 'picked-up': return 'bg-[#ECFDF5] text-[#065F46]'
    case 'in-transit': return 'bg-[#EFF6FF] text-[#1E40AF]'
    default: return 'bg-[#F3F4F6] text-[#374151]'
  }
}
</script>