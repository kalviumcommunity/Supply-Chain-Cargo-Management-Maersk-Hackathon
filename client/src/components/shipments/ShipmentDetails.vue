<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-6xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <Button @click="goBack" variant="outline" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Shipments
        </Button>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin" />
        <span class="ml-2">Loading shipment details...</span>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <Button @click="loadShipment" variant="outline">
          Try Again
        </Button>
      </div>

      <!-- Shipment Details -->
      <div v-else-if="shipment" class="space-y-6">
        <!-- Header with Actions -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">
              Shipment #{{ shipment.shipmentId }}
            </h1>
            <p class="mt-2 text-gray-600">
              {{ shipment.origin }} → {{ shipment.destination }}
            </p>
          </div>
          <div class="flex space-x-2">
            <Button @click="editShipment" variant="outline">
              <Edit class="mr-2 h-4 w-4" />
              Edit
            </Button>
            <Button @click="confirmDelete" variant="destructive">
              <Trash2 class="mr-2 h-4 w-4" />
              Delete
            </Button>
          </div>
        </div>

        <!-- Stats Cards -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <!-- Status Card -->
          <Card class="bg-gradient-to-br from-blue-50 to-blue-100 border-blue-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-blue-600">Status</p>
                  <p class="text-2xl font-bold text-blue-900 mt-1">{{ shipment.status }}</p>
                </div>
                <Truck class="h-8 w-8 text-blue-600" />
              </div>
            </CardContent>
          </Card>

          <!-- Estimated Delivery Card -->
          <Card class="bg-gradient-to-br from-green-50 to-green-100 border-green-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-green-600">Est. Delivery</p>
                  <p class="text-2xl font-bold text-green-900 mt-1">{{ formatDate(shipment.estimatedDelivery) }}</p>
                </div>
                <Calendar class="h-8 w-8 text-green-600" />
              </div>
            </CardContent>
          </Card>

          <!-- Cargo Items Card -->
          <Card class="bg-gradient-to-br from-purple-50 to-purple-100 border-purple-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-purple-600">Cargo Items</p>
                  <p class="text-2xl font-bold text-purple-900 mt-1">
                    {{ shipment.cargoItems?.length || 0 }}
                  </p>
                </div>
                <Package class="h-8 w-8 text-purple-600" />
              </div>
            </CardContent>
          </Card>

          <!-- Route Status Card -->
          <Card class="bg-gradient-to-br from-orange-50 to-orange-100 border-orange-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-orange-600">Route Status</p>
                  <p class="text-2xl font-bold text-orange-900 mt-1">
                    {{ shipment.assignedRoute ? 'Assigned' : 'Unassigned' }}
                  </p>
                </div>
                <MapPin class="h-8 w-8 text-orange-600" />
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Shipment Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Basic Information -->
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
              <CardDescription>Essential shipment details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500">Shipment ID</label>
                <p class="mt-1 text-base font-medium">#{{ shipment.shipmentId }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Origin</label>
                <p class="mt-1 text-base font-medium">{{ shipment.origin }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Destination</label>
                <p class="mt-1 text-base font-medium">{{ shipment.destination }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Status</label>
                <Badge class="mt-1" :variant="getStatusVariant(shipment.status)">
                  {{ shipment.status }}
                </Badge>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Estimated Delivery</label>
                <p class="mt-1 text-base">{{ formatDate(shipment.estimatedDelivery) }}</p>
              </div>
            </CardContent>
          </Card>

          <!-- Assignment Information -->
          <Card>
            <CardHeader>
              <CardTitle>Assignment Information</CardTitle>
              <CardDescription>Route and vendor details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500">Assigned Route</label>
                <div v-if="shipment.assignedRoute" class="mt-1">
                  <p class="text-base font-medium">
                    {{ shipment.assignedRoute.originPort }} → {{ shipment.assignedRoute.destinationPort }}
                  </p>
                  <p class="text-sm text-gray-600">
                    Duration: {{ shipment.assignedRoute.duration }} days
                  </p>
                </div>
                <p v-else class="mt-1 text-base text-gray-400">Not assigned</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Assigned Vendor</label>
                <div v-if="shipment.assignedVendor" class="mt-1">
                  <p class="text-base font-medium">{{ shipment.assignedVendor.name }}</p>
                  <Badge class="mt-1" variant="outline">
                    {{ shipment.assignedVendor.serviceType }}
                  </Badge>
                </div>
                <p v-else class="mt-1 text-base text-gray-400">Not assigned</p>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Cargo Items -->
        <Card>
          <CardHeader>
            <CardTitle>Cargo Items</CardTitle>
            <CardDescription>Items included in this shipment</CardDescription>
          </CardHeader>
          <CardContent>
            <div v-if="shipment.cargoItems && shipment.cargoItems.length > 0">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Cargo ID</TableHead>
                    <TableHead>Name</TableHead>
                    <TableHead>Description</TableHead>
                    <TableHead>Weight</TableHead>
                    <TableHead>Volume</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  <TableRow v-for="item in shipment.cargoItems" :key="item.cargoId">
                    <TableCell class="font-medium">#{{ item.cargoId }}</TableCell>
                    <TableCell>{{ item.name }}</TableCell>
                    <TableCell>{{ item.description || 'N/A' }}</TableCell>
                    <TableCell>{{ item.weight }} kg</TableCell>
                    <TableCell>{{ item.volume }} m³</TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </div>
            <div v-else class="text-center py-8 text-gray-500">
              <Package class="h-12 w-12 mx-auto mb-2 opacity-50" />
              <p>No cargo items assigned to this shipment.</p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  Edit,
  Trash2,
  Truck,
  Calendar,
  Package,
  MapPin
} from 'lucide-vue-next'
import { shipmentApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const shipment = ref(null)
const isLoading = ref(false)
const error = ref(null)

const shipmentId = route.params.id

const loadShipment = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await shipmentApi.getById(shipmentId)
    shipment.value = data
  } catch (err) {
    error.value = err.message || 'Failed to load shipment details'
    console.error('Error loading shipment:', err)
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

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  })
}

const editShipment = () => {
  router.push(`/shipments/${shipmentId}/edit`)
}

const confirmDelete = async () => {
  if (!confirm(`Are you sure you want to delete shipment #${shipment.value.shipmentId}?`)) {
    return
  }

  try {
    await shipmentApi.delete(shipmentId)
    // Emit event for real-time updates
    window.dispatchEvent(new Event('shipments-updated'))
    router.push('/shipments')
  } catch (error) {
    console.error('Error deleting shipment:', error)
    alert('Failed to delete shipment. Please try again.')
  }
}

const goBack = () => {
  router.push('/shipments')
}

onMounted(() => {
  loadShipment()
})
</script>
