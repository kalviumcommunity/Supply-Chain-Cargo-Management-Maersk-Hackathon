<template>
  <div class="container mx-auto px-4 py-8 max-w-3xl">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center space-x-4">
        <Button @click="$router.push('/routes')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Routes
        </Button>
        <div>
          <h1 class="text-3xl font-bold text-gray-900 dark:text-sidebar-foreground">
            {{ isEditMode ? 'Edit Route' : 'Create New Route' }}
          </h1>
          <p class="mt-2 text-gray-600 dark:text-sidebar-foreground/70">
            {{ isEditMode ? 'Update route information' : 'Set up a new shipping route' }}
          </p>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center h-32">
        <Loader2 class="h-6 w-6 animate-spin dark:text-sidebar-foreground" />
        <span class="ml-2 dark:text-sidebar-foreground">{{ isEditMode ? 'Loading route...' : 'Saving route...' }}</span>
      </div>

      <!-- Form Card -->
      <Card v-else>
        <CardHeader>
          <CardTitle>Route Information</CardTitle>
          <CardDescription>
            Fill in the details for the shipping route
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <!-- Origin and Destination -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="originPort">
                  Origin Port <span class="text-red-500">*</span>
                </label>
                <Input
                  id="originPort"
                  v-model="form.originPort"
                  placeholder="e.g., Jawaharlal Nehru Port"
                  required
                />
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="destinationPort">
                  Destination Port <span class="text-red-500">*</span>
                </label>
                <Input
                  id="destinationPort"
                  v-model="form.destinationPort"
                  placeholder="e.g., Chennai Port"
                  required
                />
              </div>
            </div>

            <!-- Duration and Distance -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="duration">
                  Duration (days) <span class="text-red-500">*</span>
                </label>
                <Input
                  id="duration"
                  v-model.number="form.duration"
                  type="number"
                  min="1"
                  placeholder="0"
                  required
                />
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="distance">
                  Distance (km)
                </label>
                <Input
                  id="distance"
                  v-model.number="form.distance"
                  type="number"
                  min="0"
                  step="0.1"
                  placeholder="0"
                />
              </div>
            </div>

            <!-- Transportation Mode and Status -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="transportationMode">
                  Transport Mode
                </label>
                <select
                  id="transportationMode"
                  v-model="form.transportationMode"
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                >
                  <option value="OCEAN">🚢 Ocean Transport</option>
                  <option value="AIR">✈️ Air Transport</option>
                  <option value="ROAD">🚛 Road Transport</option>
                  <option value="RAIL">🚂 Rail Transport</option>
                </select>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium dark:text-sidebar-foreground" for="status">
                  Status <span class="text-red-500">*</span>
                </label>
                <select
                  id="status"
                  v-model="form.status"
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                  required
                >
                  <option value="Active">Active</option>
                  <option value="Delayed">Delayed</option>
                  <option value="Closed">Closed</option>
                </select>
              </div>
            </div>

            <!-- Cost -->
            <div class="space-y-2">
              <label class="text-sm font-medium dark:text-sidebar-foreground" for="cost">
                Cost ($)
              </label>
              <Input
                id="cost"
                v-model.number="form.cost"
                type="number"
                min="0"
                step="0.01"
                placeholder="0.00"
              />
            </div>

            <!-- Loading Message -->
            <div v-if="isSubmitting" class="bg-blue-50 border border-blue-200 text-blue-700 px-4 py-3 rounded-md flex items-center">
              <Loader2 class="h-5 w-5 mr-2 animate-spin" />
              <span>Saving route... This may take up to a minute. Please wait.</span>
            </div>

            <!-- Error Message -->
            <div v-if="errorMessage" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md flex items-center">
              <AlertCircle class="h-5 w-5 mr-2" />
              <span>{{ errorMessage }}</span>
            </div>

            <!-- Success Message -->
            <div v-if="successMessage" class="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-md flex items-center">
              <CheckCircle class="h-5 w-5 mr-2" />
              <span>{{ successMessage }}</span>
            </div>

            <!-- Form Actions -->
            <div class="flex items-center justify-end space-x-4 pt-4 border-t">
              <Button type="button" variant="outline" @click="handleCancel" :disabled="isSubmitting">
                Cancel
              </Button>
              <Button type="submit" :disabled="isSubmitting">
                <Loader2 v-if="isSubmitting" class="mr-2 h-4 w-4 animate-spin" />
                {{ isEditMode ? 'Update Route' : 'Create Route' }}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { ArrowLeft, Loader2, AlertCircle, CheckCircle } from 'lucide-vue-next'
import { routeApi } from '@/services/api'

const router = useRouter()
const route = useRoute()

const isEditMode = ref(false)
const isLoading = ref(false)
const isSubmitting = ref(false)
const errorMessage = ref(null)
const successMessage = ref(null)

const form = ref({
  originPort: '',
  destinationPort: '',
  duration: null,
  distance: null,
  transportationMode: 'OCEAN',
  status: 'Active',
  cost: null
})

const loadRoute = async () => {
  const routeId = Number(route.params.id)
  if (!routeId) return

  isLoading.value = true
  errorMessage.value = null

  try {
    const data = await routeApi.getById(routeId)
    form.value = {
      originPort: data.originPort || '',
      destinationPort: data.destinationPort || '',
      duration: data.duration || null,
      distance: data.distance || null,
      transportationMode: data.transportationMode || 'OCEAN',
      status: data.status || 'Active',
      cost: data.cost || null
    }
  } catch (err) {
    errorMessage.value = err.message || 'Failed to load route'
    console.error('Error loading route:', err)
  } finally {
    isLoading.value = false
  }
}

const handleSubmit = async () => {
  errorMessage.value = null
  successMessage.value = null
  isSubmitting.value = true

  try {
    // Prepare route data matching backend model
    const routeData = {
      originPort: form.value.originPort,
      destinationPort: form.value.destinationPort,
      duration: Number(form.value.duration) || 0,
      distance: Number(form.value.distance) || 0,
      status: form.value.status || 'Active',
      transportationMode: form.value.transportationMode || 'OCEAN',
      cost: Number(form.value.cost) || 0
    }

    // Validate required fields
    if (!routeData.originPort || !routeData.destinationPort) {
      errorMessage.value = 'Origin Port and Destination Port are required'
      return
    }

    if (!routeData.duration || routeData.duration < 1) {
      errorMessage.value = 'Duration must be at least 1 day'
      return
    }

    console.log('Sending route data:', routeData)

    let savedRoute
    try {
      if (isEditMode.value) {
        const routeId = Number(route.params.id)
        savedRoute = await routeApi.update(routeId, routeData)
        successMessage.value = 'Route updated successfully!'
      } else {
        savedRoute = await routeApi.create(routeData)
        successMessage.value = 'Route created successfully!'
      }

      console.log('Route saved:', savedRoute)

      // Dispatch event for real-time updates with details
      window.dispatchEvent(new CustomEvent('routes-updated', {
        detail: {
          action: isEditMode.value ? 'updated' : 'created',
          route: {
            routeId: savedRoute?.routeId,
            originPort: routeData.originPort,
            destinationPort: routeData.destinationPort,
            transportationMode: routeData.transportationMode
          }
        }
      }))

      // Redirect after short delay
      setTimeout(() => {
        router.push('/routes')
      }, 1500)
    } catch (apiError) {
      // If we get a timeout but the route might have been created,
      // verify by checking if we can load the routes list
      if (apiError.message && apiError.message.includes('timeout')) {
        console.warn('Timeout occurred, verifying route creation...')
        
        try {
          // Try to load routes to verify if creation was successful
          const allRoutes = await routeApi.getAll()
          
          // Check if a route with our data exists (created in last 10 seconds)
          const justCreated = allRoutes.find(r => 
            r.originPort === routeData.originPort && 
            r.destinationPort === routeData.destinationPort &&
            new Date(r.createdAt) > new Date(Date.now() - 10000)
          )
          
          if (justCreated) {
            console.log('Route was created successfully despite timeout:', justCreated)
            successMessage.value = 'Route created successfully!'
            
            // Dispatch event for real-time updates
            window.dispatchEvent(new CustomEvent('routes-updated'))
            
            // Redirect after short delay
            setTimeout(() => {
              router.push('/routes')
            }, 1500)
            return // Exit early, don't show error
          }
        } catch (verifyError) {
          console.error('Could not verify route creation:', verifyError)
        }
      }
      
      // If we couldn't verify or it wasn't a timeout, throw the error
      throw apiError
    }

  } catch (err) {
    console.error('Error saving route:', err)
    errorMessage.value = err.message || 'Failed to save route. Please try again.'
  } finally {
    isSubmitting.value = false
  }
}

const handleCancel = () => {
  if (confirm('Are you sure you want to cancel? Any unsaved changes will be lost.')) {
    router.push('/routes')
  }
}

onMounted(() => {
  isEditMode.value = route.path.includes('/edit')
  if (isEditMode.value) {
    loadRoute()
  }
})
</script>
