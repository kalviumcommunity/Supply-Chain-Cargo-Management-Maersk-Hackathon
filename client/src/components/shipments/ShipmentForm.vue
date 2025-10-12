<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-3xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <Button @click="goBack" variant="outline" class="mb-4">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Shipments
        </Button>
        <h1 class="text-3xl font-bold text-gray-900">
          {{ isEditMode ? 'Edit Shipment' : 'Create Shipment' }}
        </h1>
        <p class="mt-2 text-gray-600">
          {{ isEditMode ? 'Update shipment information' : 'Add a new shipment to your supply chain' }}
        </p>
      </div>

      <!-- Form Card -->
      <Card>
        <CardHeader>
          <CardTitle>Shipment Information</CardTitle>
          <CardDescription>
            Fill in the details for the shipment
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <!-- Origin and Destination -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label for="origin" class="text-sm font-medium">
                  Origin <span class="text-red-500">*</span>
                </label>
                <Input
                  id="origin"
                  v-model="formData.origin"
                  placeholder="Enter origin location"
                  required
                  :disabled="isSubmitting"
                />
              </div>
              
              <div class="space-y-2">
                <label for="destination" class="text-sm font-medium">
                  Destination <span class="text-red-500">*</span>
                </label>
                <Input
                  id="destination"
                  v-model="formData.destination"
                  placeholder="Enter destination location"
                  required
                  :disabled="isSubmitting"
                />
              </div>
            </div>

            <!-- Status and Estimated Delivery -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label for="status" class="text-sm font-medium">
                  Status <span class="text-red-500">*</span>
                </label>
                <select 
                  id="status"
                  v-model="formData.status" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  required
                  :disabled="isSubmitting"
                >
                  <option value="">Select status</option>
                  <option value="Created">Created</option>
                  <option value="Pending">Pending</option>
                  <option value="Picked Up">Picked Up</option>
                  <option value="In Transit">In Transit</option>
                  <option value="Shipped">Shipped</option>
                  <option value="Delivered">Delivered</option>
                  <option value="Cancelled">Cancelled</option>
                </select>
              </div>
              
              <div class="space-y-2">
                <label for="estimatedDelivery" class="text-sm font-medium">
                  Estimated Delivery <span class="text-red-500">*</span>
                </label>
                <Input
                  id="estimatedDelivery"
                  v-model="formData.estimatedDelivery"
                  type="date"
                  required
                  :disabled="isSubmitting"
                />
              </div>
            </div>

            <!-- Route and Vendor -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label for="assignedRoute" class="text-sm font-medium">
                  Assigned Route (Optional)
                </label>
                <div class="relative">
                  <select 
                    id="assignedRoute"
                    v-model="formData.assignedRouteId" 
                    class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                    :disabled="isSubmitting || isLoadingData"
                  >
                    <option value="">
                      {{ isLoadingData ? 'Loading routes...' : 'Select route (optional)' }}
                    </option>
                    <option 
                      v-for="route in routes" 
                      :key="route.routeId" 
                      :value="route.routeId.toString()"
                    >
                      {{ route.originPort }} → {{ route.destinationPort }} ({{ route.duration }} days)
                    </option>
                  </select>
                  <Loader2 v-if="isLoadingData" class="absolute right-3 top-3 h-4 w-4 animate-spin text-gray-400" />
                </div>
                <p v-if="routes.length === 0 && !isLoadingData" class="text-xs text-gray-500">
                  No routes available. Create a route first.
                </p>
              </div>
              
              <div class="space-y-2">
                <label for="assignedVendor" class="text-sm font-medium">
                  Assigned Vendor (Optional)
                </label>
                <div class="relative">
                  <select 
                    id="assignedVendor"
                    v-model="formData.assignedVendorId" 
                    class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                    :disabled="isSubmitting || isLoadingData"
                  >
                    <option value="">
                      {{ isLoadingData ? 'Loading vendors...' : 'Select vendor (optional)' }}
                    </option>
                    <option 
                      v-for="vendor in vendors" 
                      :key="vendor.vendorId" 
                      :value="vendor.vendorId.toString()"
                    >
                      {{ vendor.name }} ({{ vendor.serviceType }})
                    </option>
                  </select>
                  <Loader2 v-if="isLoadingData" class="absolute right-3 top-3 h-4 w-4 animate-spin text-gray-400" />
                </div>
                <p v-if="vendors.length === 0 && !isLoadingData" class="text-xs text-gray-500">
                  No vendors available. Create a vendor first.
                </p>
              </div>
            </div>

            <!-- Error Message -->
            <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-md">
              <div class="flex">
                <AlertCircle class="h-5 w-5 text-red-600 mr-2" />
                <p class="text-sm text-red-600">{{ errorMessage }}</p>
              </div>
            </div>

            <!-- Success Message -->
            <div v-if="successMessage" class="p-4 bg-green-50 border border-green-200 rounded-md">
              <div class="flex">
                <CheckCircle class="h-5 w-5 text-green-600 mr-2" />
                <p class="text-sm text-green-600">{{ successMessage }}</p>
              </div>
            </div>

            <!-- Form Actions -->
            <div class="flex items-center justify-end space-x-4 pt-4 border-t">
              <Button
                type="button"
                @click="goBack"
                variant="outline"
                :disabled="isSubmitting"
              >
                Cancel
              </Button>
              <Button
                type="submit"
                :disabled="isSubmitting || !isFormValid"
              >
                <Loader2 v-if="isSubmitting" class="mr-2 h-4 w-4 animate-spin" />
                {{ isSubmitting ? 'Saving...' : (isEditMode ? 'Update Shipment' : 'Create Shipment') }}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { ArrowLeft, Loader2, AlertCircle, CheckCircle } from 'lucide-vue-next'
import { shipmentApi, routeApi, vendorApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const isEditMode = computed(() => !!route.params.id)
const shipmentId = computed(() => route.params.id)

const formData = ref({
  origin: '',
  destination: '',
  status: '',
  estimatedDelivery: '',
  assignedRouteId: '',
  assignedVendorId: ''
})

const routes = ref([])
const vendors = ref([])
const isSubmitting = ref(false)
const isLoadingData = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const isFormValid = computed(() => {
  return formData.value.origin.trim() !== '' && 
         formData.value.destination.trim() !== '' &&
         formData.value.status !== '' &&
         formData.value.estimatedDelivery !== ''
})

const loadData = async () => {
  isLoadingData.value = true
  try {
    console.log('🔄 Loading routes and vendors...')
    const [routesData, vendorsData] = await Promise.all([
      routeApi.getAll(),
      vendorApi.getAll()
    ])
    routes.value = routesData || []
    vendors.value = vendorsData || []
    console.log('✅ Routes loaded:', routes.value.length)
    console.log('✅ Vendors loaded:', vendors.value.length)
  } catch (error) {
    console.error('❌ Error loading data:', error)
    errorMessage.value = 'Failed to load routes and vendors. Please refresh the page.'
  } finally {
    isLoadingData.value = false
  }
}

const loadShipment = async () => {
  if (!isEditMode.value) return
  
  try {
    const shipment = await shipmentApi.getById(shipmentId.value)
    formData.value = {
      origin: shipment.origin || '',
      destination: shipment.destination || '',
      status: shipment.status || '',
      estimatedDelivery: shipment.estimatedDelivery?.split('T')[0] || '',
      assignedRouteId: shipment.assignedRoute?.routeId?.toString() || '',
      assignedVendorId: shipment.assignedVendor?.vendorId?.toString() || ''
    }
  } catch (error) {
    console.error('Error loading shipment:', error)
    errorMessage.value = 'Failed to load shipment details. Please try again.'
  }
}

const handleSubmit = async () => {
  console.log('🚀 Form submitted')
  console.log('📋 Form data:', formData.value)
  
  if (!isFormValid.value) {
    errorMessage.value = 'Please fill in all required fields'
    console.log('❌ Form validation failed')
    return
  }

  isSubmitting.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    // Prepare payload
    const payload = {
      origin: formData.value.origin.trim(),
      destination: formData.value.destination.trim(),
      status: formData.value.status,
      estimatedDelivery: formData.value.estimatedDelivery
    }

    // Add route if selected and valid
    if (formData.value.assignedRouteId && formData.value.assignedRouteId !== '') {
      const routeId = Number(formData.value.assignedRouteId)
      console.log('🛣️ Assigning route ID:', routeId)
      payload.assignedRoute = {
        routeId: routeId
      }
    } else {
      console.log('⚠️ No route selected')
    }

    // Add vendor if selected and valid
    if (formData.value.assignedVendorId && formData.value.assignedVendorId !== '') {
      const vendorId = Number(formData.value.assignedVendorId)
      console.log('🏢 Assigning vendor ID:', vendorId)
      payload.assignedVendor = {
        vendorId: vendorId
      }
    } else {
      console.log('⚠️ No vendor selected')
    }

    console.log('📦 Final payload to send:', JSON.stringify(payload, null, 2))

    if (isEditMode.value) {
      // Update existing shipment
      console.log('🔄 Updating shipment...')
      const result = await shipmentApi.update(shipmentId.value, payload)
      console.log('✅ Shipment updated:', result)
      successMessage.value = 'Shipment updated successfully!'
    } else {
      // Create new shipment
      console.log('➕ Creating new shipment...')
      const result = await shipmentApi.create(payload)
      console.log('✅ Shipment created:', result)
      successMessage.value = 'Shipment created successfully!'
    }

    // Emit event for real-time updates
    window.dispatchEvent(new Event('shipments-updated'))

    // Redirect after short delay
    setTimeout(() => {
      router.push('/shipments')
    }, 1500)

  } catch (error) {
    console.error('Error saving shipment:', error)
    errorMessage.value = error.message || 'Failed to save shipment. Please try again.'
    
    // If timeout error, verify if shipment was actually created
    if (error.message?.toLowerCase().includes('timeout') && !isEditMode.value) {
      try {
        setTimeout(async () => {
          const shipments = await shipmentApi.getAll()
          const created = shipments.find(s => 
            s.origin === payload.origin && 
            s.destination === payload.destination &&
            s.status === payload.status
          )
          
          if (created) {
            successMessage.value = 'Shipment created successfully (verified)!'
            errorMessage.value = ''
            setTimeout(() => router.push('/shipments'), 1500)
          }
        }, 3000)
      } catch (verifyError) {
        console.error('Verification error:', verifyError)
      }
    }
  } finally {
    isSubmitting.value = false
  }
}

const goBack = () => {
  router.push('/shipments')
}

onMounted(async () => {
  await loadData()
  await loadShipment()
})
</script>
