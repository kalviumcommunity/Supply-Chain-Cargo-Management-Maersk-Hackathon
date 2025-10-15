<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-sidebar-foreground">Deliveries</h1>
        <p class="mt-1 text-sm text-gray-600 dark:text-sidebar-foreground/70">Track your delivery status</p>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
        <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
          <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Total Deliveries</span>
        </div>
        <CardContent class="px-4 py-1.5 pb-3">
          <div class="flex items-center gap-2">
            <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.total }}</div>
            <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 12%</span>
          </div>
          <div class="mt-1 flex items-center gap-1.5">
            <Truck class="w-3.5 h-3.5 text-blue-500 dark:text-sidebar-foreground/60" />
            <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">All deliveries tracked</span>
          </div>
        </CardContent>
      </Card>
      
      <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
        <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
          <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Delivered</span>
        </div>
        <CardContent class="px-4 py-1.5 pb-3">
          <div class="flex items-center gap-2">
            <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.delivered }}</div>
            <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 8%</span>
          </div>
          <div class="mt-1 flex items-center gap-1.5">
            <CheckCircle class="w-3.5 h-3.5 text-green-500 dark:text-sidebar-foreground/60" />
            <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Successfully completed</span>
          </div>
        </CardContent>
      </Card>
      
      <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
        <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
          <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">In Transit</span>
        </div>
        <CardContent class="px-4 py-1.5 pb-3">
          <div class="flex items-center gap-2">
            <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.inTransit }}</div>
            <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 5%</span>
          </div>
          <div class="mt-1 flex items-center gap-1.5">
            <Clock class="w-3.5 h-3.5 text-yellow-500 dark:text-sidebar-foreground/60" />
            <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Currently on the way</span>
          </div>
        </CardContent>
      </Card>
      
      <Card class="rounded-xl border shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0">
        <div class="px-4 pt-2.5 pb-1.5 bg-white dark:bg-sidebar">
          <span class="text-[13px] font-medium text-gray-600 dark:text-sidebar-foreground/70">Pending</span>
        </div>
        <CardContent class="px-4 py-1.5 pb-3">
          <div class="flex items-center gap-2">
            <div class="text-3xl font-semibold tracking-tight text-gray-900 dark:text-sidebar-foreground">{{ stats.pending }}</div>
            <span class="inline-flex items-center rounded-md bg-emerald-50 dark:bg-sidebar-accent px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 dark:text-sidebar-accent-foreground ring-1 ring-inset ring-emerald-100 dark:ring-sidebar-border">↗ 15%</span>
          </div>
          <div class="mt-1 flex items-center gap-1.5">
            <Package class="w-3.5 h-3.5 text-purple-500 dark:text-sidebar-foreground/60" />
            <span class="text-xs text-gray-500 dark:text-sidebar-foreground/60">Awaiting pickup</span>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Deliveries Table -->
    <Card class="hover:shadow-md transition-shadow">
      <CardHeader>
        <CardTitle>All Deliveries</CardTitle>
        <CardDescription>
          Track the status of all deliveries from delivered shipments
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div v-if="isLoading" class="flex items-center justify-center h-32">
          <Loader2 class="h-6 w-6 animate-spin" />
          <span class="ml-2">Loading deliveries...</span>
        </div>
        
        <div v-else-if="error" class="text-center py-8">
          <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
          <p class="text-gray-600">{{ error }}</p>
          <Button @click="loadDeliveries" class="mt-4" variant="outline">
            Try Again
          </Button>
        </div>
        
        <div v-else>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Delivery ID</TableHead>
                <TableHead>Shipment ID</TableHead>
                <TableHead>From</TableHead>
                <TableHead>To</TableHead>
                <TableHead>Delivery Date</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Recipient</TableHead>
                <TableHead class="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              <TableRow v-if="deliveries.length === 0">
                <TableCell :colspan="8" class="h-24 text-center">
                  No deliveries found.
                </TableCell>
              </TableRow>
              <TableRow v-for="delivery in deliveries" :key="delivery.deliveryId" class="hover:bg-gray-50">
                <TableCell class="font-medium">{{ delivery.deliveryId }}</TableCell>
                <TableCell>
                  <span class="text-blue-600">#{{ delivery.shipment?.shipmentId || 'N/A' }}</span>
                </TableCell>
                <TableCell>{{ delivery.shipment?.origin || 'Unknown' }}</TableCell>
                <TableCell>{{ delivery.shipment?.destination || 'Unknown' }}</TableCell>
                <TableCell>{{ formatDate(delivery.actualDeliveryDate) }}</TableCell>
                <TableCell>
                  <Badge :variant="getStatusBadgeVariant(delivery.shipment?.status || 'Unknown')">
                    {{ delivery.shipment?.status || 'Unknown' }}
                  </Badge>
                </TableCell>
                <TableCell>{{ delivery.recipient || 'N/A' }}</TableCell>
                <TableCell class="text-right">
                  <div class="flex items-center justify-end space-x-2">
                    <Button @click="viewDelivery(delivery)" size="sm" variant="outline">
                      <Eye class="h-4 w-4" />
                    </Button>
                    <Button @click="updateDelivery(delivery)" size="sm" variant="outline">
                      <Edit class="h-4 w-4" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, onActivated } from 'vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { 
  Truck, 
  CheckCircle, 
  Clock, 
  Package, 
  Loader2, 
  AlertCircle, 
  Eye,
  Edit 
} from 'lucide-vue-next'
import { deliveryApi } from '@/services/api'

const deliveries = ref([])
const isLoading = ref(false)
const error = ref(null)

const stats = computed(() => {
  const total = deliveries.value.length
  const delivered = deliveries.value.filter(d => d.shipment?.status === 'Delivered').length
  const inTransit = deliveries.value.filter(d => d.shipment?.status === 'In Transit').length
  const pending = deliveries.value.filter(d => d.shipment?.status === 'Pending' || d.shipment?.status === 'Created').length
  
  return { total, delivered, inTransit, pending }
})

const loadDeliveries = async () => {
  isLoading.value = true
  error.value = null
  try {
    const response = await deliveryApi.getAll()
    deliveries.value = response
  } catch (err) {
    error.value = 'Failed to load deliveries'
    console.error('Error loading deliveries:', err)
  } finally {
    isLoading.value = false
  }
}

// Refresh function to be called from anywhere
const refresh = () => {
  loadDeliveries()
}

const getStatusBadgeVariant = (status) => {
  switch (status) {
    case 'Delivered':
      return 'default'
    case 'In Transit':
      return 'secondary'
    case 'Pending':
    case 'Created':
      return 'outline'
    case 'Failed':
      return 'destructive'
    default:
      return 'outline'
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

const viewDelivery = (delivery) => {
  // Navigate to delivery details page (when implemented)
  console.log('View delivery:', delivery)
}

const updateDelivery = (delivery) => {
  // Navigate to update delivery page (when implemented)
  console.log('Update delivery:', delivery)
}

onMounted(() => {
  loadDeliveries()
  
  // Add window focus listener to refresh data when returning to tab
  window.addEventListener('focus', loadDeliveries)
  
  // Listen for shipment updates from other components
  window.addEventListener('shipment-updated', refresh)
  
  // Listen for real-time updates from WebSocket
  window.addEventListener('deliveries-updated', refresh)
  window.addEventListener('shipments-updated', refresh)
})

// Refresh data when component is activated (navigating back to this page)
onActivated(() => {
  loadDeliveries()
})

onUnmounted(() => {
  // Cleanup event listener
  window.removeEventListener('focus', loadDeliveries)
  window.removeEventListener('shipment-updated', refresh)
  window.removeEventListener('deliveries-updated', refresh)
  window.removeEventListener('shipments-updated', refresh)
})
</script>
