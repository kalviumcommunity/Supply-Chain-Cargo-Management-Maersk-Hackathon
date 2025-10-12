<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-6xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <Button @click="goBack" variant="outline" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Cargo
        </Button>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin" />
        <span class="ml-2">Loading cargo details...</span>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <Button @click="loadCargo" variant="outline">
          Try Again
        </Button>
      </div>

      <!-- Cargo Details -->
      <div v-else-if="cargo" class="space-y-6">
        <!-- Header with Actions -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">
              Cargo #{{ cargo.cargoId }}
            </h1>
            <p class="mt-2 text-gray-600">{{ cargo.type }}</p>
          </div>
          <div class="flex space-x-2">
            <Button @click="editCargo" variant="outline">
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
          <!-- Type Card -->
          <Card class="bg-gradient-to-br from-blue-50 to-blue-100 border-blue-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-blue-600">Cargo Type</p>
                  <p class="text-2xl font-bold text-blue-900 mt-1">{{ cargo.type }}</p>
                </div>
                <Package class="h-8 w-8 text-blue-600" />
              </div>
            </CardContent>
          </Card>

          <!-- Weight Card -->
          <Card class="bg-gradient-to-br from-green-50 to-green-100 border-green-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-green-600">Weight</p>
                  <p class="text-2xl font-bold text-green-900 mt-1">{{ cargo.weight || 0 }} kg</p>
                </div>
                <Weight class="h-8 w-8 text-green-600" />
              </div>
            </CardContent>
          </Card>

          <!-- Value Card -->
          <Card class="bg-gradient-to-br from-purple-50 to-purple-100 border-purple-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-purple-600">Value</p>
                  <p class="text-2xl font-bold text-purple-900 mt-1">${{ formatNumber(cargo.value || 0) }}</p>
                </div>
                <DollarSign class="h-8 w-8 text-purple-600" />
              </div>
            </CardContent>
          </Card>

          <!-- Volume Card -->
          <Card class="bg-gradient-to-br from-orange-50 to-orange-100 border-orange-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-orange-600">Volume</p>
                  <p class="text-2xl font-bold text-orange-900 mt-1">
                    {{ cargo.volume ? `${cargo.volume} m³` : 'N/A' }}
                  </p>
                </div>
                <Box class="h-8 w-8 text-orange-600" />
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Cargo Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Basic Information -->
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
              <CardDescription>Essential cargo details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500">Cargo ID</label>
                <p class="mt-1 text-base font-medium">#{{ cargo.cargoId }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Type</label>
                <Badge class="mt-1" :variant="getTypeBadgeVariant(cargo.type)">
                  {{ cargo.type }}
                </Badge>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Weight</label>
                <p class="mt-1 text-base">{{ cargo.weight || 0 }} kg</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Value</label>
                <p class="mt-1 text-base">${{ formatNumber(cargo.value || 0) }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Volume</label>
                <p class="mt-1 text-base">{{ cargo.volume ? `${cargo.volume} m³` : 'Not specified' }}</p>
              </div>
            </CardContent>
          </Card>

          <!-- Shipment Information -->
          <Card>
            <CardHeader>
              <CardTitle>Shipment Information</CardTitle>
              <CardDescription>Associated shipment details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500">Shipment ID</label>
                <p class="mt-1 text-base font-medium">#{{ cargo.shipment.shipmentId }}</p>
              </div>
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500">Origin</label>
                <p class="mt-1 text-base">{{ cargo.shipment.origin }}</p>
              </div>
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500">Destination</label>
                <p class="mt-1 text-base">{{ cargo.shipment.destination }}</p>
              </div>
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500">Status</label>
                <Badge class="mt-1" :variant="getStatusVariant(cargo.shipment.status)">
                  {{ cargo.shipment.status }}
                </Badge>
              </div>
              <div v-if="cargo.shipment">
                <label class="text-sm font-medium text-gray-500">Estimated Delivery</label>
                <p class="mt-1 text-base">{{ formatDate(cargo.shipment.estimatedDelivery) }}</p>
              </div>
              <div v-if="!cargo.shipment" class="text-center py-8 text-gray-500">
                <Package class="h-12 w-12 mx-auto mb-2 opacity-50" />
                <p>Not assigned to any shipment</p>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Description -->
        <Card v-if="cargo.description">
          <CardHeader>
            <CardTitle>Description</CardTitle>
            <CardDescription>Additional cargo information</CardDescription>
          </CardHeader>
          <CardContent>
            <p class="text-base text-gray-700">{{ cargo.description }}</p>
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
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  Edit,
  Trash2,
  Package,
  Weight,
  DollarSign,
  Box
} from 'lucide-vue-next'
import { cargoApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const cargo = ref(null)
const isLoading = ref(false)
const error = ref(null)

const cargoId = route.params.id

const loadCargo = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await cargoApi.getById(cargoId)
    cargo.value = data
  } catch (err) {
    error.value = err.message || 'Failed to load cargo details'
    console.error('❌ Error loading cargo:', err)
  } finally {
    isLoading.value = false
  }
}

const getTypeBadgeVariant = (type) => {
  const variants = {
    'Electronics': 'default',
    'Clothing': 'secondary',
    'Food': 'outline',
    'Machinery': 'destructive',
    'Perishables': 'secondary',
    'Hazardous': 'destructive',
    'General': 'outline'
  }
  return variants[type] || 'outline'
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
  if (!num && num !== 0) return '0'
  return new Intl.NumberFormat().format(num)
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  })
}

const editCargo = () => {
  router.push(`/cargo/${cargoId}/edit`)
}

const confirmDelete = async () => {
  if (!confirm(`Are you sure you want to delete cargo #${cargo.value.cargoId}?`)) {
    return
  }

  try {
    await cargoApi.remove(cargoId)
    // Emit event for real-time updates
    window.dispatchEvent(new Event('cargo-updated'))
    router.push('/cargo')
  } catch (error) {
    console.error('❌ Error deleting cargo:', error)
    alert('Failed to delete cargo. Please try again.')
  }
}

const goBack = () => {
  router.push('/cargo')
}

onMounted(() => {
  loadCargo()
})
</script>
