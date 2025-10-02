<template>
  <div class="space-y-8">
    <!-- Header Section -->
    <div class="flex justify-between items-start">
      <div>
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Dashboard</h1>
        <p class="text-gray-600">Welcome back! Here's your supply chain overview.</p>
      </div>
      <button 
        @click="$emit('new-shipment')"
        class="bg-blue-500 text-white px-6 py-3 rounded-xl hover:bg-blue-600 hover:scale-105 hover:shadow-lg transition-all duration-200 flex items-center gap-3"
      >
        <Plus class="w-5 h-5" />
        New Shipment
      </button>
    </div>

    <!-- Metrics Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
      <div 
        v-for="metric in metrics" 
        :key="metric.id"
        class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg hover:-translate-y-1 hover:scale-105 transition-all duration-300 cursor-pointer"
      >
        <div class="flex items-start justify-between mb-4">
          <div :class="['w-10 h-10 rounded-full flex items-center justify-center', metric.iconBgColor]">
            <component :is="metric.icon" :class="['w-6 h-6', metric.iconColor]" />
          </div>
        </div>
        <div class="mb-2">
          <div class="text-3xl font-bold text-gray-900">{{ metric.value }}</div>
          <div class="text-sm text-gray-500 mt-1">{{ metric.label }}</div>
        </div>
        <div :class="['text-sm flex items-center gap-1', getChangeColor(metric.changeType)]">
          <component :is="getChangeIcon(metric.changeType)" class="w-4 h-4" />
          {{ metric.change }}
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Recent Activity -->
      <div class="lg:col-span-2 bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3 mb-6">
          <Clock class="w-5 h-5 text-gray-500" />
          <h2 class="text-lg font-semibold text-gray-900">Recent Activity</h2>
        </div>
        <div class="space-y-4">
          <div 
            v-for="(activity, index) in activities" 
            :key="activity.id"
            @click="$emit('activity-click', activity)"
            class="flex items-start gap-4 p-4 rounded-xl hover:bg-gray-50 hover:translate-x-1 cursor-pointer transition-all duration-200"
          >
            <div :class="['w-2 h-2 rounded-full mt-2', getActivityDotColor(activity.type)]"></div>
            <div class="flex-1">
              <div class="flex items-start justify-between">
                <div>
                  <p class="text-sm text-gray-600">
                    <span class="font-semibold text-gray-900">{{ activity.shipmentId }}</span>
                    {{ activity.action }}
                  </p>
                  <p class="text-xs text-gray-400 mt-1">{{ activity.timestamp }}</p>
                </div>
                <div v-if="activity.status" :class="['px-2 py-1 rounded-full text-xs font-medium', getStatusBadgeClass(activity.status)]">
                  {{ activity.status }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="space-y-6">
        <!-- Quick Actions -->
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
          <div class="space-y-3">
            <button 
              v-for="action in quickActions" 
              :key="action.id"
              @click="$emit('action-click', action.id)"
              class="w-full h-12 border border-blue-500 text-blue-500 rounded-lg hover:bg-blue-500 hover:text-white hover:scale-105 transition-all duration-200 flex items-center justify-center gap-3"
            >
              <component :is="action.icon" class="w-5 h-5" />
              {{ action.label }}
            </button>
          </div>
        </div>

        <!-- Shipment Status -->
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Shipment Status</h2>
          
          <!-- Status Bar -->
          <div class="h-3 bg-gray-100 rounded-full overflow-hidden mb-4 flex">
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
              class="flex items-center justify-between text-sm"
            >
              <div class="flex items-center gap-2">
                <div :class="['w-2 h-2 rounded-full', status.color]"></div>
                <span class="text-gray-600">{{ status.status }}</span>
              </div>
              <span class="text-gray-400">({{ status.count }})</span>
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
  TrendingUp, TrendingDown,
  MapPin, UserPlus, ArrowUp, ArrowDown, Minus
} from 'lucide-vue-next'

// Reactive data
const metrics = reactive([
  {
    id: 'shipments',
    label: 'Total Shipments',
    value: 5,
    change: '+2 from last week',
    changeType: 'positive',
    icon: Truck,
    iconBgColor: 'bg-blue-50',
    iconColor: 'text-blue-500'
  },
  {
    id: 'cargo',
    label: 'Active Cargo',
    value: 8,
    change: '+3 new items',
    changeType: 'positive',
    icon: Package,
    iconBgColor: 'bg-green-50',
    iconColor: 'text-green-500'
  },
  {
    id: 'routes',
    label: 'Available Routes',
    value: 3,
    change: '1 delayed',
    changeType: 'neutral',
    icon: Route,
    iconBgColor: 'bg-yellow-50',
    iconColor: 'text-yellow-500'
  },
  {
    id: 'vendors',
    label: 'Partner Vendors',
    value: 4,
    change: 'All verified',
    changeType: 'positive',
    icon: Users,
    iconBgColor: 'bg-purple-50',
    iconColor: 'text-purple-500'
  }
])

const activities = reactive([
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

const quickActions = reactive([
  { id: 'add-cargo', label: 'Add New Cargo', icon: Plus },
  { id: 'create-route', label: 'Create Route', icon: MapPin },
  { id: 'add-vendor', label: 'Add Vendor', icon: UserPlus }
])

const shipmentStatuses = reactive([
  { status: 'In Transit', count: 1, color: 'bg-green-500', percentage: 20 },
  { status: 'Delivered', count: 1, color: 'bg-blue-500', percentage: 20 },
  { status: 'Picked Up', count: 1, color: 'bg-purple-500', percentage: 20 },
  { status: 'Created', count: 1, color: 'bg-gray-400', percentage: 20 },
  { status: 'Delayed', count: 1, color: 'bg-yellow-500', percentage: 20 }
])

// Helper functions
const getChangeColor = (type) => {
  switch (type) {
    case 'positive': return 'text-green-600'
    case 'negative': return 'text-red-600'
    default: return 'text-yellow-600'
  }
}

const getChangeIcon = (type) => {
  switch (type) {
    case 'positive': return ArrowUp
    case 'negative': return ArrowDown
    default: return Minus
  }
}

const getActivityDotColor = (type) => {
  switch (type) {
    case 'status': return 'bg-blue-500'
    case 'cargo': return 'bg-green-500'
    case 'route': return 'bg-yellow-500'
    case 'vendor': return 'bg-purple-500'
    default: return 'bg-gray-400'
  }
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'delayed': return 'bg-yellow-100 text-yellow-800'
    case 'picked-up': return 'bg-green-100 text-green-800'
    case 'in-transit': return 'bg-blue-100 text-blue-800'
    default: return 'bg-gray-100 text-gray-800'
  }
}
</script>