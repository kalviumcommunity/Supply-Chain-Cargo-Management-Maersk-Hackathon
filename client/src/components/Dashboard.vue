<template>
  <div class="space-y-5 animate-fade-in">
    <!-- Page Header -->
    <PageHeader
      title="Dashboard"
      description="Welcome back! Here's your supply chain overview."

    >
      <template #actions>
        <Button 
          @click="router.push('/shipments/create')"
          class="shadow-sm hover:shadow-md"
        >
          <Plus class="w-4 h-4 mr-2" />
          New Shipment
        </Button>
      </template>
    </PageHeader>

    <!-- Error State -->
    <div v-if="error" class="bg-red-50 border-l-4 border-red-500 p-6 rounded-xl mb-8">
      <div class="flex items-center gap-3">
        <svg class="w-5 h-5 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="15" y1="9" x2="9" y2="15"></line>
          <line x1="9" y1="9" x2="15" y2="15"></line>
        </svg>
        <span class="text-red-700 font-medium">{{ error }}</span>
        <button 
          @click="loadDashboardData" 
          class="ml-auto px-3 py-1 bg-red-100 text-red-700 rounded-md hover:bg-red-200 transition-colors text-sm font-medium"
        >
          Retry
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="flex items-center justify-center py-24">
      <div class="flex flex-col items-center gap-4">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        <p class="text-gray-600 font-medium">Loading dashboard...</p>
      </div>
    </div>

    <!-- Metrics Grid - Compact & Clean Design -->
    <div v-if="!isLoading" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
      <Card 
        v-for="(metric, index) in metrics" 
        :key="metric.id"
        class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 cursor-pointer group"
        :class="getMetricBorderColor(metric.id)"
        :style="{ animationDelay: `${index * 100}ms` }"
      >
        <CardContent class="px-4 py-3">
          <div class="flex items-center justify-between mb-2">
            <div :class="['w-9 h-9 rounded-lg flex items-center justify-center transition-transform duration-200 group-hover:scale-105', metric.iconBgColor]">
              <component :is="metric.icon" :class="['w-5 h-5', metric.iconColor]" />
            </div>
            <Badge variant="secondary" class="text-[10px] px-1.5 py-0.5">
              {{ metric.change }}
            </Badge>
          </div>
          <div class="space-y-0.5">
            <div class="text-2xl font-bold text-gray-900 leading-none tracking-tight">{{ metric.value }}</div>
            <div class="text-xs font-medium text-gray-500">{{ metric.label }}</div>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Main Content (only show when not loading) -->
    <div v-if="!isLoading" class="grid grid-cols-1 lg:grid-cols-3 gap-5">
      <!-- Recent Activity -->
      <Card class="lg:col-span-2 rounded-xl border border-gray-200/60 shadow-sm hover:shadow-md transition-shadow">
        <CardHeader class="pb-3">
          <div class="flex items-center justify-between">
            <CardTitle class="flex items-center gap-2 text-base">
              <Clock class="w-4 h-4 text-gray-600" />
              Recent Activity
            </CardTitle>
            <Button variant="link" size="sm" class="text-blue-600 text-xs h-auto p-0">
              View all
            </Button>
          </div>
        </CardHeader>
        <CardContent class="pt-0">
          <div class="relative">
            <!-- Timeline line -->
            <div class="absolute left-4 top-0 bottom-0 w-px bg-gray-200"></div>
            
            <div class="space-y-1">
              <div 
                v-for="(activity, index) in activities" 
                :key="activity.id"
                @click="$emit('activity-click', activity)"
                class="relative flex items-start gap-3 p-3 pl-10 rounded-lg hover:bg-gray-50 cursor-pointer transition-all duration-200 group"
              >
                <!-- Timeline dot with icon -->
                <div :class="['absolute left-2 w-2 h-2 rounded-full border-2 border-white shadow-sm', getActivityDotColor(activity.type)]"></div>
                
                <div class="flex-1 min-w-0">
                  <div class="flex items-start justify-between gap-2">
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-1.5 mb-0.5">
                        <component :is="getActivityIcon(activity.type)" :class="['w-3.5 h-3.5', getActivityIconColor(activity.type)]" />
                        <p class="text-xs text-gray-700 truncate">
                          <span class="font-semibold text-gray-900">{{ activity.shipmentId }}</span>
                          {{ activity.action }}
                        </p>
                      </div>
                      <p class="text-[10px] text-gray-500 font-medium">{{ activity.timestamp }}</p>
                    </div>
                    <Badge v-if="activity.status" :variant="getActivityBadgeVariant(activity.status)" class="text-[10px] px-1.5 py-0 shrink-0">
                      {{ activity.status }}
                    </Badge>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Right Column -->
      <div class="space-y-5">
        <!-- Quick Actions -->
        <Card class="rounded-xl border border-gray-200/60 shadow-sm hover:shadow-md transition-shadow">
          <CardHeader class="pb-3">
            <CardTitle class="text-base">Quick Actions</CardTitle>
          </CardHeader>
          <CardContent class="space-y-2 pt-0">
            <Button 
              v-for="action in quickActions" 
              :key="action.id"
              @click="$emit('action-click', action.id)"
              variant="outline"
              size="sm"
              class="w-full justify-start gap-2 h-9 hover:border-blue-500 hover:bg-blue-50 hover:text-blue-700 transition-all duration-200"
            >
              <component :is="action.icon" class="w-4 h-4" />
              <span class="text-sm">{{ action.label }}</span>
            </Button>
          </CardContent>
        </Card>

        <!-- Shipment Status -->
        <Card class="rounded-xl border border-gray-200/60 shadow-sm hover:shadow-md transition-shadow">
          <CardHeader class="pb-3">
            <CardTitle class="text-base">Shipment Status</CardTitle>
          </CardHeader>
          <CardContent class="pt-0">
            <!-- Status Bar with gaps -->
            <div class="h-8 bg-gray-100 rounded-lg overflow-hidden mb-3 flex gap-0.5 p-0.5">
              <div 
                v-for="(status, index) in shipmentStatuses" 
                :key="status.status"
                :class="[status.gradientClass, 'rounded transition-all duration-500 hover:scale-y-105 cursor-pointer']"
                :style="{ width: `calc(${status.percentage}% - 2px)`, animationDelay: `${index * 150}ms` }"
              ></div>
            </div>

            <!-- Legend -->
            <div class="space-y-2">
              <div 
                v-for="status in shipmentStatuses" 
                :key="status.status"
                class="flex items-center justify-between text-xs group cursor-pointer"
              >
                <div class="flex items-center gap-2">
                  <div :class="['w-2 h-2 rounded-full shadow-sm transition-transform duration-200 group-hover:scale-125', status.color]"></div>
                  <span class="text-gray-700 font-medium">{{ status.status }}</span>
                </div>
                <Badge variant="secondary" class="text-[10px] px-1.5 py-0">{{ status.count }}</Badge>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Plus, Clock, Truck, Package, Route, Users, 
  TrendingUp, TrendingDown, ArrowUp, ArrowDown, Minus,
  CheckCircle, AlertCircle, MapPin, UserPlus, Home
} from 'lucide-vue-next'
import { dashboardApi } from '../services/api.js'
import PageHeader from './shared/PageHeader.vue'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'

// Router
const router = useRouter()

// Loading and error states
const isLoading = ref(true)
const error = ref(null)

// Reactive data with premium styling
const metrics = reactive([
  {
    id: 'shipments',
    label: 'Total Shipments',
    value: 0,
    change: 'Loading...',
    changeType: 'positive',
    icon: Truck,
    iconBgColor: 'bg-[#EFF6FF]',
    iconColor: 'text-[#3B82F6]'
  },
  {
    id: 'cargo',
    label: 'Active Cargo',
    value: 0,
    change: 'Loading...',
    changeType: 'positive',
    icon: Package,
    iconBgColor: 'bg-[#ECFDF5]',
    iconColor: 'text-[#10B981]'
  },
  {
    id: 'routes',
    label: 'Available Routes',
    value: 0,
    change: 'Loading...',
    changeType: 'warning',
    icon: Route,
    iconBgColor: 'bg-[#FEF3C7]',
    iconColor: 'text-[#F59E0B]'
  },
  {
    id: 'vendors',
    label: 'Partner Vendors',
    value: 0,
    change: 'Loading...',
    changeType: 'positive',
    icon: Users,
    iconBgColor: 'bg-[#F3E8FF]',
    iconColor: 'text-[#A855F7]'
  }
])

const activities = reactive([])

const quickActions = reactive([
  { id: 'add-cargo', label: 'Add New Cargo', icon: Plus },
  { id: 'create-route', label: 'Create Route', icon: MapPin },
  { id: 'add-vendor', label: 'Add Vendor', icon: UserPlus }
])

const shipmentStatuses = reactive([])

// API Methods
const loadDashboardData = async () => {
  isLoading.value = true
  error.value = null
  
  try {
    // Load metrics and activities in parallel
    const [metricsData, activitiesData] = await Promise.all([
      dashboardApi.getMetrics(),
      dashboardApi.getRecentActivities()
    ])
    
    // Update metrics
    updateMetrics(metricsData)
    
    // Update activities
    activities.splice(0, activities.length, ...activitiesData)
    
    // Update shipment statuses
    updateShipmentStatuses(metricsData.shipmentStatuses || {})
    
  } catch (err) {
    console.error('Failed to load dashboard data:', err)
    error.value = 'Failed to load dashboard data. Please try again.'
  } finally {
    isLoading.value = false
  }
}

const updateMetrics = (data) => {
  // Update metrics values
  const metricsUpdate = {
    shipments: {
      value: data.totalShipments || 0,
      change: `${data.totalShipments || 0} total`
    },
    cargo: {
      value: data.activeCargo || 0,
      change: `${data.activeCargo || 0} items`
    },
    routes: {
      value: data.availableRoutes || 0,
      change: `${data.availableRoutes || 0} available`
    },
    vendors: {
      value: data.partnerVendors || 0,
      change: `${data.partnerVendors || 0} partners`
    }
  }
  
  // Update the reactive metrics array
  metrics.forEach(metric => {
    if (metricsUpdate[metric.id]) {
      metric.value = metricsUpdate[metric.id].value
      metric.change = metricsUpdate[metric.id].change
    }
  })
}

const updateShipmentStatuses = (statusData) => {
  const totalShipments = Object.values(statusData).reduce((sum, count) => sum + count, 0)
  
  // Clear existing statuses
  shipmentStatuses.splice(0, shipmentStatuses.length)
  
  // Map status names to display information
  const statusMapping = {
    'in transit': {
      status: 'In Transit',
      color: 'bg-[#14B8A6]',
      gradientClass: 'bg-gradient-to-r from-[#14B8A6] to-[#0D9488]'
    },
    'delivered': {
      status: 'Delivered',
      color: 'bg-[#10B981]',
      gradientClass: 'bg-gradient-to-r from-[#10B981] to-[#059669]'
    },
    'picked up': {
      status: 'Picked Up',
      color: 'bg-[#3B82F6]',
      gradientClass: 'bg-gradient-to-r from-[#3B82F6] to-[#2563EB]'
    },
    'created': {
      status: 'Created',
      color: 'bg-[#6B7280]',
      gradientClass: 'bg-gradient-to-r from-[#6B7280] to-[#4B5563]'
    },
    'delayed': {
      status: 'Delayed',
      color: 'bg-[#F59E0B]',
      gradientClass: 'bg-gradient-to-r from-[#F59E0B] to-[#D97706]'
    }
  }
  
  // Add statuses from API data
  Object.entries(statusData).forEach(([status, count]) => {
    const mapping = statusMapping[status.toLowerCase()] || {
      status: status,
      color: 'bg-[#6B7280]',
      gradientClass: 'bg-gradient-to-r from-[#6B7280] to-[#4B5563]'
    }
    
    shipmentStatuses.push({
      ...mapping,
      count: count,
      percentage: totalShipments > 0 ? (count / totalShipments) * 100 : 0
    })
  })
  
  // If no data, add default empty statuses
  if (shipmentStatuses.length === 0) {
    Object.entries(statusMapping).forEach(([key, mapping]) => {
      shipmentStatuses.push({
        ...mapping,
        count: 0,
        percentage: 0
      })
    })
  }
}

// Load data on component mount
onMounted(() => {
  loadDashboardData()
})

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

const getBadgeVariant = (type) => {
  switch (type) {
    case 'positive': return 'default'
    case 'negative': return 'destructive'
    case 'warning': return 'secondary'
    default: return 'outline'
  }
}

const getActivityBadgeVariant = (status) => {
  switch (status) {
    case 'delayed': return 'destructive'
    case 'picked-up': return 'default'
    case 'in-transit': return 'secondary'
    default: return 'outline'
  }
}

const getMetricBorderColor = (id) => {
  switch (id) {
    case 'shipments': return '!border-t-blue-400'
    case 'cargo': return '!border-t-emerald-400'
    case 'routes': return '!border-t-amber-400'
    case 'vendors': return '!border-t-purple-400'
    default: return '!border-t-gray-300'
  }
}
</script>