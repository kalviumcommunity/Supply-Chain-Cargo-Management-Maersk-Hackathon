<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Vendor Management</h1>
          <p class="mt-2 text-gray-600">Manage your supply chain vendors</p>
        </div>
        <Button @click="$router.push('/vendors/create')">
          <Plus class="mr-2 h-4 w-4" />
          Add Vendor
        </Button>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <!-- Total Vendors Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Total Vendors</span>
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

        <!-- Active Vendors Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Active Vendors</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ stats.active }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 8%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Currently operational</span>
            </div>
          </CardContent>
        </Card>

        <!-- Logistics Vendors Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Logistics</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ stats.logistics }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 5%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Service providers</span>
            </div>
          </CardContent>
        </Card>

        <!-- Transportation Vendors Card -->
        <Card class="rounded-xl border-l border-r border-b border-gray-200/60 shadow-sm hover:shadow-md transition-shadow overflow-hidden !pt-0 border-t-4 !border-t-[#f4f6f8]">
          <div class="px-4 pt-2.5 pb-1.5 bg-white">
            <span class="text-[13px] font-medium text-gray-600">Transportation</span>
          </div>
          <CardContent class="px-4 py-1.5 pb-3">
            <div class="flex items-center gap-2">
              <div class="text-3xl font-semibold tracking-tight text-gray-900">{{ stats.transportation }}</div>
              <span class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[10px] font-medium text-emerald-700 ring-1 ring-inset ring-emerald-100">↗ 3%</span>
            </div>
            <div class="mt-1">
              <span class="text-xs text-gray-500">Service providers</span>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Vendors Table -->
      <Card>
        <CardHeader>
          <CardTitle>All Vendors</CardTitle>
          <CardDescription>
            A comprehensive list of all vendors in your supply chain
          </CardDescription>
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
                placeholder="Search by ID, name, email, phone, or address..."
                class="pl-10"
              />
            </div>

            <!-- Filter by Service Type -->
            <Select v-model="filterServiceType">
              <SelectTrigger class="w-full md:w-[200px]">
                <SelectValue placeholder="Service Type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Types</SelectItem>
                <SelectItem value="Logistics">Logistics</SelectItem>
                <SelectItem value="Transportation">Transportation</SelectItem>
                <SelectItem value="Warehousing">Warehousing</SelectItem>
                <SelectItem value="Freight">Freight</SelectItem>
              </SelectContent>
            </Select>

            <!-- Clear Filters Button -->
            <Button
              v-if="searchQuery || filterServiceType !== 'all'"
              @click="clearFilters"
              variant="outline"
              class="w-full md:w-auto"
            >
              <X class="h-4 w-4 mr-2" />
              Clear
            </Button>
          </div>

          <div v-if="isLoading" class="flex items-center justify-center h-32">
            <Loader2 class="h-6 w-6 animate-spin" />
            <span class="ml-2">Loading vendors...</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadVendors" class="mt-4" variant="outline">
              Try Again
            </Button>
          </div>
          
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Name</TableHead>
                  <TableHead>Service Type</TableHead>
                  <TableHead>Contact Email</TableHead>
                  <TableHead>Contact Phone</TableHead>
                  <TableHead>Address</TableHead>
                  <TableHead class="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="filteredVendors.length === 0">
                  <TableCell :colspan="7" class="h-24 text-center">
                    <div v-if="searchQuery || filterServiceType !== 'all'" class="space-y-2">
                      <p>No vendors match your filters.</p>
                      <Button @click="clearFilters" variant="outline" size="sm">
                        Clear Filters
                      </Button>
                    </div>
                    <p v-else>No vendors found. Create your first vendor to get started.</p>
                  </TableCell>
                </TableRow>
                <TableRow v-for="vendor in filteredVendors" :key="vendor.vendorId">
                  <TableCell class="font-medium">{{ vendor.vendorId }}</TableCell>
                  <TableCell>
                    <div class="font-medium">{{ vendor.name }}</div>
                  </TableCell>
                  <TableCell>
                    <Badge :variant="getServiceTypeBadgeVariant(vendor.serviceType)">
                      {{ vendor.serviceType }}
                    </Badge>
                  </TableCell>
                  <TableCell>{{ vendor.contactEmail || 'N/A' }}</TableCell>
                  <TableCell>{{ vendor.contactPhone || 'N/A' }}</TableCell>
                  <TableCell>
                    <div class="max-w-xs truncate">{{ vendor.address || 'N/A' }}</div>
                  </TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="viewVendor(vendor)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                      <Button @click="editVendor(vendor)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="confirmDelete(vendor)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
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
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { 
  Plus, 
  Loader2, 
  AlertCircle, 
  Eye,
  Edit, 
  Trash2,
  Search,
  X 
} from 'lucide-vue-next'
import { vendorApi } from '@/services/api'

const router = useRouter()

const vendors = ref([])
const isLoading = ref(false)
const error = ref(null)

// Search and filter state
const searchQuery = ref('')
const debouncedSearchQuery = ref('')
const filterServiceType = ref('all')
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

// Filtered vendors based on search and filters
const filteredVendors = computed(() => {
  let filtered = vendors.value

  // Apply search filter
  if (debouncedSearchQuery.value) {
    const query = debouncedSearchQuery.value.toLowerCase()
    filtered = filtered.filter(vendor => 
      vendor.vendorId?.toString().toLowerCase().includes(query) ||
      vendor.name?.toLowerCase().includes(query) ||
      vendor.contactEmail?.toLowerCase().includes(query) ||
      vendor.contactPhone?.toLowerCase().includes(query) ||
      vendor.address?.toLowerCase().includes(query)
    )
  }

  // Apply service type filter
  if (filterServiceType.value && filterServiceType.value !== 'all') {
    filtered = filtered.filter(vendor => 
      vendor.serviceType === filterServiceType.value
    )
  }

  return filtered
})

const stats = computed(() => {
  const total = filteredVendors.value.length
  const active = filteredVendors.value.filter(v => v.serviceType !== 'Inactive').length
  const logistics = filteredVendors.value.filter(v => v.serviceType === 'Logistics').length
  const transportation = filteredVendors.value.filter(v => v.serviceType === 'Transportation').length
  
  return { total, active, logistics, transportation }
})

const clearFilters = () => {
  searchQuery.value = ''
  debouncedSearchQuery.value = ''
  filterServiceType.value = 'all'
}

const loadVendors = async () => {
  isLoading.value = true
  error.value = null
  try {
    const data = await vendorApi.getAll()
    vendors.value = data || []
  } catch (err) {
    error.value = err.message || 'Failed to load vendors'
    console.error('Error loading vendors:', err)
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

const formatNumber = (num) => {
  if (!num) return '0'
  return new Intl.NumberFormat().format(num)
}

const viewVendor = (vendor) => {
  router.push(`/vendors/${vendor.vendorId}`)
}

const editVendor = (vendor) => {
  router.push(`/vendors/${vendor.vendorId}/edit`)
}

const confirmDelete = async (vendor) => {
  if (!confirm(`Are you sure you want to delete vendor "${vendor.name}"?`)) {
    return
  }
  
  try {
    await vendorApi.delete(vendor.vendorId)
    await loadVendors() // Reload vendors after deletion
  } catch (error) {
    console.error('Error deleting vendor:', error)
    alert('Failed to delete vendor. Please try again.')
  }
}

onMounted(() => {
  loadVendors()
  
  // Listen for real-time updates
  window.addEventListener('vendors-updated', loadVendors)
})

onUnmounted(() => {
  if (debounceTimeout) {
    clearTimeout(debounceTimeout)
  }
  window.removeEventListener('vendors-updated', loadVendors)
})
</script>
