<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Shipment Tracking</h1>
          <p class="mt-2 text-gray-600">Monitor and manage your shipments</p>
        </div>
        <Button @click="$router.push('/shipments/create')">
          <Plus class="mr-2 h-4 w-4" />
          Create Shipment
        </Button>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <!-- Total Shipments Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Total Shipments</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ formatNumber(stats.total) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 15%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Compared to the previous period</span>
            </div>
          </CardContent>
        </Card>

        <!-- In Transit Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">In Transit</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ stats.inTransit }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 8%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Currently moving</span>
            </div>
          </CardContent>
        </Card>

        <!-- Delivered Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Delivered</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ stats.delivered }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 10%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Successfully completed</span>
            </div>
          </CardContent>
        </Card>

        <!-- Pending Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Pending</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ stats.pending }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 5%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Awaiting processing</span>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Shipments Table -->
      <Card>
        <CardHeader>
          <CardTitle>All Shipments</CardTitle>
          <CardDescription>
            A comprehensive list of all shipments in your supply chain
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div v-if="isLoading" class="flex items-center justify-center h-32">
            <Loader2 class="h-6 w-6 animate-spin" />
            <span class="ml-2">Loading shipments...</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadShipments" class="mt-4" variant="outline">
              Try Again
            </Button>
          </div>
          
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Origin → Destination</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Est. Delivery</TableHead>
                  <TableHead>Route</TableHead>
                  <TableHead>Vendor</TableHead>
                  <TableHead class="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="shipments.length === 0">
                  <TableCell :colspan="7" class="h-24 text-center">
                    <div class="flex flex-col items-center justify-center text-gray-500">
                      <Truck class="h-12 w-12 text-gray-300 mb-2" />
                      <p class="font-medium">No shipments found</p>
                      <p class="text-sm">Create your first shipment to get started.</p>
                    </div>
                  </TableCell>
                </TableRow>
                <TableRow v-for="shipment in shipments" :key="shipment.shipmentId">
                  <TableCell class="font-medium">#{{ shipment.shipmentId }}</TableCell>
                  <TableCell>
                    <div class="flex flex-col">
                      <span class="font-medium">{{ shipment.origin }} → {{ shipment.destination }}</span>
                      <span v-if="shipment.cargoItems && shipment.cargoItems.length > 0" class="text-xs text-gray-500">
                        {{ shipment.cargoItems.length }} cargo item(s)
                      </span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge :variant="getStatusVariant(shipment.status)">
                      {{ shipment.status }}
                    </Badge>
                  </TableCell>
                  <TableCell>{{ formatDate(shipment.estimatedDelivery) }}</TableCell>
                  <TableCell>
                    <span v-if="shipment.assignedRoute" class="text-sm">
                      {{ shipment.assignedRoute.originPort }} → {{ shipment.assignedRoute.destinationPort }}
                    </span>
                    <span v-else class="text-gray-400 text-sm">Not assigned</span>
                  </TableCell>
                  <TableCell>
                    <span v-if="shipment.assignedVendor" class="text-sm">
                      {{ shipment.assignedVendor.name }}
                    </span>
                    <span v-else class="text-gray-400 text-sm">Not assigned</span>
                  </TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="viewShipment(shipment)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                      <Button @click="editShipment(shipment)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="confirmDelete(shipment)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
                        <Trash2 class="h-4 w-4" />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { 
  Plus, 
  Loader2, 
  AlertCircle, 
  Eye,
  Edit, 
  Trash2,
  Truck
} from 'lucide-vue-next'
import { shipmentApi } from '@/services/api'

const router = useRouter()

const shipments = ref([])
const isLoading = ref(false)
const error = ref(null)

const stats = computed(() => {
  const total = shipments.value.length
  const inTransit = shipments.value.filter(s => 
    s.status === 'In Transit' || s.status === 'Shipped' || s.status === 'Picked Up'
  ).length
  const delivered = shipments.value.filter(s => s.status === 'Delivered').length
  const pending = shipments.value.filter(s => 
    s.status === 'Pending' || s.status === 'Created'
  ).length
  
  return { total, inTransit, delivered, pending }
})

const loadShipments = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await shipmentApi.getAll()
    shipments.value = data || []
  } catch (err) {
    error.value = err.message || 'Failed to load shipments'
    console.error('❌ Error loading shipments:', err)
  } finally {
    isLoading.value = false
  }
}

const getStatusVariant = (status) => {
  const variants = {
    'Shipped': 'default',
    'In Transit': 'secondary',
    'Delivered': 'default',
    'Cancelled': 'destructive',
    'Pending': 'outline',
    'Created': 'outline',
    'Picked Up': 'secondary'
  }
  return variants[status] || 'outline'
}

const formatNumber = (num) => {
  if (!num) return '0'
  return new Intl.NumberFormat().format(num)
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
}

const viewShipment = (shipment) => {
  router.push(`/shipments/${shipment.shipmentId}`)
}

const editShipment = (shipment) => {
  router.push(`/shipments/${shipment.shipmentId}/edit`)
}

const confirmDelete = async (shipment) => {
  if (!confirm(`Are you sure you want to delete shipment #${shipment.shipmentId}?`)) {
    return
  }
  
  try {
    await shipmentApi.delete(shipment.shipmentId)
    await loadShipments() // Reload shipments after deletion
  } catch (error) {
    console.error('❌ Error deleting shipment:', error)
    alert('Failed to delete shipment. Please try again.')
  }
}

onMounted(() => {
  loadShipments()
  
  // Listen for real-time updates
  window.addEventListener('shipments-updated', loadShipments)
  window.addEventListener('shipment-updated', loadShipments)
})

onUnmounted(() => {
  window.removeEventListener('shipments-updated', loadShipments)
  window.removeEventListener('shipment-updated', loadShipments)
})
</script>
