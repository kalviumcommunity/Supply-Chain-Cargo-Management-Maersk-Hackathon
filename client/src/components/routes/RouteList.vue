<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <PageHeader 
        title="Route Management"
        description="Manage and optimize your shipping routes"
      >
        <template #actions>
          <Button @click="$router.push('/routes/create')">
            <Plus class="mr-2 h-4 w-4" />
            Add Route
          </Button>
        </template>
      </PageHeader>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <!-- Total Routes Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Total Routes</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ formatNumber(stats.total) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Compared to the previous period</span>
            </div>
          </CardContent>
        </Card>

        <!-- Avg Duration Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Avg Duration</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ stats.avgDuration }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Days per route</span>
            </div>
          </CardContent>
        </Card>

        <!-- Avg Cost Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Avg Cost</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">${{ formatNumber(stats.avgCost) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Per route</span>
            </div>
          </CardContent>
        </Card>

        <!-- Total Distance Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Total Distance</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ formatNumber(stats.totalDistance) }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 12%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Kilometers covered</span>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Routes Table/Map Section -->
      <Card>
        <CardHeader>
          <div class="flex items-center justify-between">
            <div>
              <CardTitle>All Routes</CardTitle>
              <CardDescription>
                A comprehensive list of all shipping routes
              </CardDescription>
            </div>
            <div class="flex items-center space-x-2">
              <Button 
                @click="viewMode = 'table'" 
                :variant="viewMode === 'table' ? 'default' : 'outline'"
                size="sm"
              >
                <List class="mr-2 h-4 w-4" />
                Table
              </Button>
              <Button 
                @click="viewMode = 'map'" 
                :variant="viewMode === 'map' ? 'default' : 'outline'"
                size="sm"
              >
                <MapIcon class="mr-2 h-4 w-4" />
                Map
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <!-- Search and Filter Section -->
          <div class="flex flex-col md:flex-row gap-4 mb-6">
            <!-- Search Input -->
            <div class="relative flex-1">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                v-model="searchQuery"
                type="text"
                placeholder="Search by route ID, origin, or destination..."
                class="pl-10"
              />
            </div>

            <!-- Filter by Transportation Mode -->
            <Select v-model="filterMode">
              <SelectTrigger class="w-full md:w-[200px]">
                <SelectValue placeholder="Transport Mode" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Modes</SelectItem>
                <SelectItem value="SEA">Sea</SelectItem>
                <SelectItem value="AIR">Air</SelectItem>
                <SelectItem value="LAND">Land</SelectItem>
                <SelectItem value="RAIL">Rail</SelectItem>
              </SelectContent>
            </Select>

            <!-- Filter by Status -->
            <Select v-model="filterStatus">
              <SelectTrigger class="w-full md:w-[200px]">
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Status</SelectItem>
                <SelectItem value="Active">Active</SelectItem>
                <SelectItem value="Delayed">Delayed</SelectItem>
                <SelectItem value="Closed">Closed</SelectItem>
              </SelectContent>
            </Select>

            <!-- Clear Filters Button -->
            <Button 
              v-if="searchQuery || filterMode !== 'all' || filterStatus !== 'all'"
              @click="clearFilters" 
              variant="outline"
              size="icon"
              class="shrink-0"
            >
              <X class="h-4 w-4" />
            </Button>
          </div>
          <div v-if="isLoading" class="flex items-center justify-center h-32">
            <Loader2 class="h-6 w-6 animate-spin" />
            <span class="ml-2">Loading routes...</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadRoutes" class="mt-4" variant="outline">
              Try Again
            </Button>
          </div>
          
          <!-- Map View -->
          <div v-else-if="viewMode === 'map'" class="h-[600px]">
            <RouteMap :routes="routes" class="h-full rounded-lg" />
          </div>
          
          <!-- Table View -->
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Origin Port</TableHead>
                  <TableHead>Destination Port</TableHead>
                  <TableHead>Distance (km)</TableHead>
                  <TableHead>Duration (days)</TableHead>
                  <TableHead>Mode</TableHead>
                  <TableHead>Cost ($)</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead class="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="filteredRoutes.length === 0">
                  <TableCell :colspan="9" class="h-24 text-center">
                    <div v-if="searchQuery || filterMode !== 'all' || filterStatus !== 'all'">
                      <p class="text-gray-600 mb-2">No routes match your filters</p>
                      <Button @click="clearFilters" variant="outline" size="sm">
                        Clear Filters
                      </Button>
                    </div>
                    <p v-else class="text-gray-600">
                      No routes found. Create your first route to get started.
                    </p>
                  </TableCell>
                </TableRow>
                <TableRow v-for="route in filteredRoutes" :key="route.routeId">
                  <TableCell class="font-medium">{{ route.routeId }}</TableCell>
                  <TableCell>{{ route.originPort }}</TableCell>
                  <TableCell>{{ route.destinationPort }}</TableCell>
                  <TableCell>{{ formatNumber(route.distance) }}</TableCell>
                  <TableCell>{{ route.duration }}</TableCell>
                  <TableCell>
                    <Badge :variant="getModeBadgeVariant(route.transportationMode)">
                      {{ route.transportationMode }}
                    </Badge>
                  </TableCell>
                  <TableCell>${{ formatNumber(route.cost) }}</TableCell>
                  <TableCell>
                    <Badge :variant="getStatusBadgeVariant(route.status)">
                      {{ route.status }}
                    </Badge>
                  </TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="viewRoute(route)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                      <Button @click="editRoute(route)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="confirmDelete(route)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
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
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '@/components/shared/PageHeader.vue'
import RouteMap from '@/components/shared/RouteMap.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { 
  Plus, 
  Route, 
  Clock, 
  DollarSign, 
  MapPin,
  Loader2, 
  AlertCircle, 
  Eye,
  Edit, 
  Trash2,
  List,
  Map as MapIcon,
  Search,
  X
} from 'lucide-vue-next'
import { routeApi } from '@/services/api'

const router = useRouter()

const routes = ref([])
const isLoading = ref(false)
const error = ref(null)
const viewMode = ref('table') // 'table' or 'map'

// Search and Filter
const searchQuery = ref('')
const debouncedSearchQuery = ref('')
const filterMode = ref('all')
const filterStatus = ref('all')
let debounceTimeout = null

// Debounced search with 500ms delay
watch(searchQuery, (newValue) => {
  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }
  debounceTimeout = setTimeout(() => {
    debouncedSearchQuery.value = newValue
  }, 500)
})

// Filtered routes based on search and filters
const filteredRoutes = computed(() => {
  let filtered = routes.value

  // Apply search filter
  if (debouncedSearchQuery.value) {
    const query = debouncedSearchQuery.value.toLowerCase()
    filtered = filtered.filter(route => 
      route.routeId?.toString().toLowerCase().includes(query) ||
      route.originPort?.toLowerCase().includes(query) ||
      route.destinationPort?.toLowerCase().includes(query)
    )
  }

  // Apply transportation mode filter
  if (filterMode.value && filterMode.value !== 'all') {
    filtered = filtered.filter(route => 
      route.transportationMode?.toUpperCase() === filterMode.value
    )
  }

  // Apply status filter
  if (filterStatus.value && filterStatus.value !== 'all') {
    filtered = filtered.filter(route => 
      route.status === filterStatus.value
    )
  }

  return filtered
})

const stats = computed(() => {
  const total = filteredRoutes.value.length
  const avgDuration = total > 0 
    ? Math.round(filteredRoutes.value.reduce((sum, route) => sum + (route.duration || 0), 0) / total)
    : 0
  const avgCost = total > 0 
    ? Math.round(filteredRoutes.value.reduce((sum, route) => sum + (route.cost || 0), 0) / total)
    : 0
  const totalDistance = filteredRoutes.value.reduce((sum, route) => sum + (route.distance || 0), 0)
  
  return { total, avgDuration, avgCost, totalDistance }
})

const clearFilters = () => {
  searchQuery.value = ''
  debouncedSearchQuery.value = ''
  filterMode.value = 'all'
  filterStatus.value = 'all'
}

const loadRoutes = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await routeApi.getAll()
    routes.value = data || []
  } catch (err) {
    error.value = err.message || 'Failed to load routes'
    console.error('❌ Error loading routes:', err)
  } finally {
    isLoading.value = false
  }
}

const getModeBadgeVariant = (mode) => {
  const modeUpper = (mode || '').toUpperCase()
  switch (modeUpper) {
    case 'SEA':
    case 'OCEAN':
      return 'default'
    case 'AIR':
      return 'secondary'
    case 'LAND':
    case 'ROAD':
      return 'outline'
    case 'RAIL':
      return 'destructive'
    default:
      return 'outline'
  }
}

const getStatusBadgeVariant = (status) => {
  switch (status) {
    case 'Active':
      return 'default'
    case 'Delayed':
      return 'destructive'
    case 'Closed':
      return 'secondary'
    default:
      return 'outline'
  }
}

const formatNumber = (num) => {
  if (!num) return '0'
  return new Intl.NumberFormat().format(num)
}

const viewRoute = (route) => {
  router.push(`/routes/${route.routeId}`)
}

const editRoute = (route) => {
  router.push(`/routes/${route.routeId}/edit`)
}

const confirmDelete = async (route) => {
  if (!confirm(`Are you sure you want to delete route "${route.originPort} → ${route.destinationPort}"?`)) {
    return
  }
  
  try {
    await routeApi.delete(route.routeId)
    await loadRoutes() // Reload routes after deletion
  } catch (error) {
    console.error('❌ Error deleting route:', error)
    alert('Failed to delete route. Please try again.')
  }
}

onMounted(() => {
  loadRoutes()
  
  // Listen for real-time updates
  window.addEventListener('routes-updated', loadRoutes)
})

onUnmounted(() => {
  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }
  window.removeEventListener('routes-updated', loadRoutes)
})
</script>
