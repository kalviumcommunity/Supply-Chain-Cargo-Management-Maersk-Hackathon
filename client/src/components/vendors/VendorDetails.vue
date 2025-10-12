<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-6xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <Button @click="goBack" variant="outline" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Vendors
        </Button>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin" />
        <span class="ml-2">Loading vendor details...</span>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <Button @click="loadVendor" variant="outline">
          Try Again
        </Button>
      </div>

      <!-- Vendor Details -->
      <div v-else-if="vendor" class="space-y-6">
        <!-- Header with Actions -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">{{ vendor.name }}</h1>
            <p class="mt-2 text-gray-600">Vendor ID: {{ vendor.vendorId }}</p>
          </div>
          <div class="flex space-x-2">
            <Button @click="editVendor" variant="outline">
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
          <!-- Service Type Card -->
          <Card class="bg-gradient-to-br from-blue-50 to-blue-100 border-blue-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-blue-600">Service Type</p>
                  <p class="text-2xl font-bold text-blue-900 mt-1">{{ vendor.serviceType }}</p>
                </div>
                <Package class="h-8 w-8 text-blue-600" />
              </div>
            </CardContent>
          </Card>

          <!-- Contact Status Card -->
          <Card class="bg-gradient-to-br from-green-50 to-green-100 border-green-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-green-600">Contact Status</p>
                  <p class="text-2xl font-bold text-green-900 mt-1">
                    {{ vendor.contactEmail ? 'Available' : 'No Contact' }}
                  </p>
                </div>
                <Mail class="h-8 w-8 text-green-600" />
              </div>
            </CardContent>
          </Card>

          <!-- Phone Status Card -->
          <Card class="bg-gradient-to-br from-purple-50 to-purple-100 border-purple-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-purple-600">Phone Status</p>
                  <p class="text-2xl font-bold text-purple-900 mt-1">
                    {{ vendor.contactPhone ? 'Available' : 'No Phone' }}
                  </p>
                </div>
                <Phone class="h-8 w-8 text-purple-600" />
              </div>
            </CardContent>
          </Card>

          <!-- Address Status Card -->
          <Card class="bg-gradient-to-br from-orange-50 to-orange-100 border-orange-200">
            <CardContent class="pt-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-medium text-orange-600">Address Status</p>
                  <p class="text-2xl font-bold text-orange-900 mt-1">
                    {{ vendor.address ? 'Available' : 'No Address' }}
                  </p>
                </div>
                <MapPin class="h-8 w-8 text-orange-600" />
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Vendor Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Basic Information -->
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
              <CardDescription>Essential vendor details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500">Vendor ID</label>
                <p class="mt-1 text-base font-medium">{{ vendor.vendorId }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Vendor Name</label>
                <p class="mt-1 text-base font-medium">{{ vendor.name }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Service Type</label>
                <Badge class="mt-1" :variant="getServiceTypeBadgeVariant(vendor.serviceType)">
                  {{ vendor.serviceType }}
                </Badge>
              </div>
            </CardContent>
          </Card>

          <!-- Contact Information -->
          <Card>
            <CardHeader>
              <CardTitle>Contact Information</CardTitle>
              <CardDescription>Communication details</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-500">Email</label>
                <p class="mt-1 text-base">{{ vendor.contactEmail || 'Not provided' }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Phone</label>
                <p class="mt-1 text-base">{{ vendor.contactPhone || 'Not provided' }}</p>
              </div>
              <div>
                <label class="text-sm font-medium text-gray-500">Address</label>
                <p class="mt-1 text-base">{{ vendor.address || 'Not provided' }}</p>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Additional Information (placeholder for future features) -->
        <Card>
          <CardHeader>
            <CardTitle>Associated Shipments</CardTitle>
            <CardDescription>Shipments managed by this vendor</CardDescription>
          </CardHeader>
          <CardContent>
            <div class="text-center py-8 text-gray-500">
              <Package class="h-12 w-12 mx-auto mb-2 opacity-50" />
              <p>No shipment associations configured yet.</p>
              <p class="text-sm mt-2">This feature will show shipments associated with this vendor.</p>
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
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  Edit,
  Trash2,
  Package,
  Mail,
  Phone,
  MapPin
} from 'lucide-vue-next'
import { vendorApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const vendor = ref(null)
const isLoading = ref(false)
const error = ref(null)

const vendorId = route.params.id

const loadVendor = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await vendorApi.getById(vendorId)
    vendor.value = data
  } catch (err) {
    error.value = err.message || 'Failed to load vendor details'
    console.error('❌ Error loading vendor:', err)
  } finally {
    isLoading.value = false
  }
}

const getServiceTypeBadgeVariant = (serviceType) => {
  switch (serviceType) {
    case 'Logistics':
      return 'default'
    case 'Transportation':
      return 'secondary'
    case 'Warehousing':
      return 'outline'
    case 'Freight':
      return 'destructive'
    default:
      return 'outline'
  }
}

const editVendor = () => {
  router.push(`/vendors/${vendorId}/edit`)
}

const confirmDelete = async () => {
  if (!confirm(`Are you sure you want to delete vendor "${vendor.value.name}"?`)) {
    return
  }

  try {
    await vendorApi.delete(vendorId)
    // Emit event for real-time updates
    window.dispatchEvent(new Event('vendors-updated'))
    router.push('/vendors')
  } catch (error) {
    console.error('❌ Error deleting vendor:', error)
    alert('Failed to delete vendor. Please try again.')
  }
}

const goBack = () => {
  router.push('/vendors')
}

onMounted(() => {
  loadVendor()
})
</script>
