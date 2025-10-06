<template>
  <div class="cargo-management-page bg-[#F5F5F7] min-h-screen p-10 space-y-7">
    <!-- Page Header -->
    <header class="flex justify-between items-start">
      <div class="flex items-center gap-4">
        <div class="w-7 h-7 text-[#3B82F6]">
          <Package class="w-full h-full" />
        </div>
        <div>
          <h1 class="text-4xl font-bold text-[#0F172A] tracking-tight leading-none">Cargo Management</h1>
          <p class="text-[#64748B] text-base mt-1">Manage and track all cargo items across shipments.</p>
        </div>
      </div>
    </header>

    <!-- Clean Metrics Grid -->
        <!-- Metrics Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <!-- Total Cargo -->
      <div class="group bg-white rounded-3xl p-8 border border-gray-100 transition-all duration-300 hover:shadow-[0_20px_60px_-12px_rgba(59,130,246,0.15)] hover:border-blue-200 hover:-translate-y-1 cursor-pointer">
        <div class="flex flex-col items-center text-center">
          <div class="p-4 bg-gradient-to-br from-blue-50 to-blue-100 rounded-full group-hover:from-blue-100 group-hover:to-blue-200 transition-all duration-300 group-hover:scale-110 group-hover:rotate-3 mb-4">
            <Package class="w-8 h-8 text-blue-600 group-hover:text-blue-700 transition-colors duration-300" />
          </div>
          <p class="text-2xl font-bold text-gray-900 group-hover:text-blue-700 transition-colors duration-300 mb-1">{{ formatNumber(metrics.totalCargo) }}</p>
          <p class="text-sm font-medium text-gray-600 group-hover:text-blue-600 transition-colors duration-300 mb-1">Total Cargo Items</p>
          <p class="text-xs text-gray-500 group-hover:text-blue-500 transition-colors duration-300">Across all shipments</p>
        </div>
      </div>

      <!-- Total Value -->
      <div class="group bg-white rounded-3xl p-8 border border-gray-100 transition-all duration-300 hover:shadow-[0_20px_60px_-12px_rgba(34,197,94,0.15)] hover:border-green-200 hover:-translate-y-1 cursor-pointer">
        <div class="flex flex-col items-center text-center">
          <div class="p-4 bg-gradient-to-br from-green-50 to-green-100 rounded-full group-hover:from-green-100 group-hover:to-green-200 transition-all duration-300 group-hover:scale-110 group-hover:rotate-3 mb-4">
            <IndianRupee class="w-8 h-8 text-green-600 group-hover:text-green-700 transition-colors duration-300" />
          </div>
          <p class="text-2xl font-bold text-gray-900 group-hover:text-green-700 transition-colors duration-300 mb-1">₹{{ formatNumber(metrics.totalValue) }}</p>
          <p class="text-sm font-medium text-gray-600 group-hover:text-green-600 transition-colors duration-300 mb-1">Total Cargo Value</p>
          <p class="text-xs text-gray-500 group-hover:text-green-500 transition-colors duration-300">Combined inventory worth</p>
        </div>
      </div>

      <!-- Categories -->
      <div class="group bg-white rounded-3xl p-8 border border-gray-100 transition-all duration-300 hover:shadow-[0_20px_60px_-12px_rgba(168,85,247,0.15)] hover:border-purple-200 hover:-translate-y-1 cursor-pointer">
        <div class="flex flex-col items-center text-center">
          <div class="p-4 bg-gradient-to-br from-purple-50 to-purple-100 rounded-full group-hover:from-purple-100 group-hover:to-purple-200 transition-all duration-300 group-hover:scale-110 group-hover:rotate-3 mb-4">
            <Grid3X3 class="w-8 h-8 text-purple-600 group-hover:text-purple-700 transition-colors duration-300" />
          </div>
          <p class="text-2xl font-bold text-gray-900 group-hover:text-purple-700 transition-colors duration-300 mb-1">{{ metrics.categories }}</p>
          <p class="text-sm font-medium text-gray-600 group-hover:text-purple-600 transition-colors duration-300 mb-1">Cargo Categories</p>
          <p class="text-xs text-gray-500 group-hover:text-purple-500 transition-colors duration-300">Different item types</p>
        </div>
      </div>

      <!-- High Value Items -->
      <div class="group bg-white rounded-3xl p-8 border border-gray-100 transition-all duration-300 hover:shadow-[0_20px_60px_-12px_rgba(245,158,11,0.15)] hover:border-amber-200 hover:-translate-y-1 cursor-pointer">
        <div class="flex flex-col items-center text-center">
          <div class="p-4 bg-gradient-to-br from-amber-50 to-amber-100 rounded-full group-hover:from-amber-100 group-hover:to-amber-200 transition-all duration-300 group-hover:scale-110 group-hover:rotate-3 mb-4">
            <TrendingUp class="w-8 h-8 text-amber-600 group-hover:text-amber-700 transition-colors duration-300" />
          </div>
          <p class="text-2xl font-bold text-gray-900 group-hover:text-amber-700 transition-colors duration-300 mb-1">{{ metrics.highValueItems }}</p>
          <p class="text-sm font-medium text-gray-600 group-hover:text-amber-600 transition-colors duration-300 mb-1">High Value Items</p>
          <p class="text-xs text-gray-500 group-hover:text-amber-500 transition-colors duration-300">Worth >₹50k each</p>
        </div>
      </div>
    </div>

    <!-- Search and Actions Bar -->
    <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 mb-8">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        <!-- Search -->
        <div class="relative flex-1 max-w-md">
          <Search class="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search cargo by ID, type, or description..."
            class="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          />
        </div>

        <!-- Filters and Actions -->
        <div class="flex items-center gap-3">
          <!-- Type Filter -->
          <select
            v-model="filterType"
            class="px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          >
            <option value="">All Types</option>
            <option v-for="type in cargoTypes" :key="type" :value="type">{{ type }}</option>
          </select>

          <!-- Sort -->
          <select
            v-model="currentSort"
            class="px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          >
            <option value="value-desc">Value (High to Low)</option>
            <option value="value-asc">Value (Low to High)</option>
            <option value="weight-desc">Weight (Heavy to Light)</option>
            <option value="id">Cargo ID</option>
          </select>

          <!-- Add Cargo Button -->
          <button
            @click="openAddCargoModal"
            class="flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-all duration-200 hover:shadow-lg hover:-translate-y-0.5"
          >
            <Plus class="w-5 h-5" />
            Add Cargo
          </button>
        </div>
      </div>
    </div>

    <!-- Filter Dropdown -->
        <!-- Cargo Table -->

    <!-- Sort Dropdown -->
    <div v-if="showSortDropdown" class="relative z-50">
      <div class="absolute top-2 right-0 w-56 bg-white rounded-2xl shadow-[0_8px_24px_rgba(0,0,0,0.12)] border border-[#E5E7EB] p-2">
        <button 
          v-for="option in sortOptions" 
          :key="option.value"
          @click="setSortOption(option.value)"
          class="w-full text-left px-3 py-2 text-sm text-[#374151] rounded-lg hover:bg-[#F9FAFB] flex items-center justify-between"
        >
          {{ option.label }}
          <Check v-if="currentSort === option.value" class="w-4 h-4 text-[#3B82F6]" />
        </button>
      </div>
    </div>

    <!-- Simple Cargo Table -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
      <!-- Table Header -->
      <div class="px-6 py-4 border-b border-gray-100">
        <h2 class="text-lg font-semibold text-gray-900">Cargo Inventory</h2>
        <p class="text-sm text-gray-600 mt-1">{{ filteredCargo.length }} items</p>
      </div>

      <!-- Table -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Cargo ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Shipment</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Value</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Weight</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="cargo in filteredCargo" :key="cargo.id" class="hover:bg-gray-50 transition-colors duration-150">
              <td class="px-6 py-4 text-sm font-mono font-medium text-gray-900">{{ cargo.id }}</td>
              <td class="px-6 py-4">
                <div class="text-sm font-medium text-gray-900">{{ cargo.shipmentId }}</div>
                <div class="text-xs text-gray-500">{{ cargo.shipmentRoute.origin }} → {{ cargo.shipmentRoute.destination }}</div>
              </td>
              <td class="px-6 py-4">
                <span :class="getTypeBadgeClass(cargo.type)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ cargo.type }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm font-medium text-gray-900">₹{{ formatNumber(cargo.value) }}</td>
              <td class="px-6 py-4 text-sm text-gray-500">{{ cargo.weight }} kg</td>
              <td class="px-6 py-4 text-sm text-gray-500 max-w-xs truncate">{{ cargo.description }}</td>
              <td class="px-6 py-4">
                <div class="flex items-center space-x-2">
                  <button @click="editCargo(cargo)" class="text-blue-600 hover:text-blue-800 transition-colors duration-150">
                    <Pencil class="w-4 h-4" />
                  </button>
                  <!-- MODIFICATION: Changed the @click handler to call a new method that opens our custom dialog -->
                  <button @click.stop="promptDeleteCargo(cargo)" class="text-red-600 hover:text-red-800 transition-colors duration-150">
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Empty State -->
        <div v-if="filteredCargo.length === 0" class="text-center py-12">
          <Package class="w-12 h-12 text-gray-300 mx-auto mb-4" />
          <p class="text-gray-500 font-medium mb-2">No cargo found</p>
          <p class="text-gray-400 text-sm">{{ searchQuery ? 'Try adjusting your search' : 'Add your first cargo item to get started' }}</p>
        </div>
      </div>
    </div>

    <!-- 
      MODIFICATION: Replaced the old modal div with the new reusable BaseModal component.
      - We bind the `show` prop to our local `showCargoModal` ref.
      - We listen for the `@close` event to hide the modal.
      - The form is now placed inside the named slots for header, body, and footer.
    -->
    <BaseModal :show="showCargoModal" @close="closeCargoModal" max-width="md">
      <template #header>
        <h3 class="text-lg font-semibold text-gray-900">{{ isEditMode ? 'Edit Cargo' : 'Add New Cargo' }}</h3>
      </template>
      <template #body>
        <form @submit.prevent="saveCargo" id="cargoForm" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Cargo ID</label>
            <input type="text" v-model="cargoForm.id" :disabled="isEditMode" :class="isEditMode ? 'w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 focus:outline-none' : 'w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'" />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Shipment ID</label>
            <input type="text" v-model="cargoForm.shipmentId" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Type</label>
            <select v-model="cargoForm.type" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option value="">Select Type</option>
              <option v-for="type in cargoTypes" :key="type" :value="type">{{ type }}</option>
            </select>
          </div>
          
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Value (₹)</label>
              <input type="number" v-model="cargoForm.value" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Weight (kg)</label>
              <input type="number" v-model="cargoForm.weight" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea v-model="cargoForm.description" rows="3" class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"></textarea>
          </div>
        </form>
      </template>
      <template #footer>
        <button type="button" @click="closeCargoModal" class="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors duration-200">
          Cancel
        </button>
        <button type="submit" form="cargoForm" class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200">
          {{ isEditMode ? 'Save Changes' : 'Add Cargo' }}
        </button>
      </template>
    </BaseModal>

    <!-- MODIFICATION: Add the ConfirmDialog component -->
    <ConfirmDialog
      :show="showConfirmDialog"
      title="Delete Cargo Item"
      :message="`Are you sure you want to permanently delete cargo item ${cargoToDelete?.id}? This action cannot be undone.`"
      confirm-text="Delete"
      variant="danger"
      @confirm="handleConfirmDelete"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { 
  Package, Plus, Search, Pencil, Trash2, IndianRupee, Grid3X3, TrendingUp
} from 'lucide-vue-next'
import BaseModal from './shared/BaseModal.vue'
// MODIFICATION: Import the new ConfirmDialog component
import ConfirmDialog from './shared/ConfirmDialog.vue'

// Simple reactive data
const searchQuery = ref('')
const filterType = ref('')
const currentSort = ref('value-desc')
const showCargoModal = ref(false)
const isLoading = ref(false)
const isEditMode = ref(false)

// MODIFICATION: Add new state for the confirmation dialog
const showConfirmDialog = ref(false)
const cargoToDelete = ref(null)

// Simple form data
const cargoForm = ref({
  id: '',
  shipmentId: '',
  type: '',
  value: 0,
  weight: 0,
  description: ''
})

// Sample data
const cargoData = ref([
  {
    id: 'CG001',
    shipmentId: 'SH001',
    shipmentRoute: { origin: 'Mumbai', destination: 'Chennai' },
    type: 'Electronics',
    value: 75000,
    weight: 45,
    description: 'High-end smartphones and tablets for retail distribution'
  },
  {
    id: 'CG002',
    shipmentId: 'SH001',
    shipmentRoute: { origin: 'Mumbai', destination: 'Chennai' },
    type: 'Textiles',
    value: 32000,
    weight: 120,
    description: 'Cotton fabric rolls and garment materials'
  },
  {
    id: 'CG003',
    shipmentId: 'SH002',
    shipmentRoute: { origin: 'Delhi', destination: 'Bangalore' },
    type: 'Machinery',
    value: 180000,
    weight: 350,
    description: 'Industrial printing equipment and spare parts'
  },
  {
    id: 'CG004',
    shipmentId: 'SH002',
    shipmentRoute: { origin: 'Delhi', destination: 'Bangalore' },
    type: 'Food Items',
    value: 15000,
    weight: 80,
    description: 'Organic spices and packaged food products'
  },
  {
    id: 'CG005',
    shipmentId: 'SH003',
    shipmentRoute: { origin: 'Kolkata', destination: 'Hyderabad' },
    type: 'Books',
    value: 8500,
    weight: 25,
    description: 'Educational textbooks and reference materials'
  },
  {
    id: 'CG006',
    shipmentId: 'SH003',
    shipmentRoute: { origin: 'Kolkata', destination: 'Hyderabad' },
    type: 'Furniture',
    value: 95000,
    weight: 200,
    description: 'Office furniture and ergonomic seating solutions'
  },
  {
    id: 'CG007',
    shipmentId: 'SH004',
    shipmentRoute: { origin: 'Pune', destination: 'Kochi' },
    type: 'Electronics',
    value: 125000,
    weight: 60,
    description: 'Gaming laptops and computer peripherals'
  },
  {
    id: 'CG008',
    shipmentId: 'SH004',
    shipmentRoute: { origin: 'Pune', destination: 'Kochi' },
    type: 'Machinery',
    value: 220000,
    weight: 450,
    description: 'CNC machine components and precision tools'
  }
])

const cargoTypes = ['Electronics', 'Textiles', 'Machinery', 'Food Items', 'Books', 'Furniture']

// Simple computed properties
const metrics = computed(() => {
  const totalCargo = cargoData.value.length
  const totalValue = cargoData.value.reduce((sum, cargo) => sum + cargo.value, 0)
  const categories = new Set(cargoData.value.map(cargo => cargo.type)).size
  const highValueItems = cargoData.value.filter(cargo => cargo.value > 50000).length

  return { totalCargo, totalValue, categories, highValueItems }
})

const filteredCargo = computed(() => {
  let filtered = cargoData.value

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(cargo => 
      cargo.id.toLowerCase().includes(query) ||
      cargo.type.toLowerCase().includes(query) ||
      cargo.description.toLowerCase().includes(query)
    )
  }

  // Type filter
  if (filterType.value) {
    filtered = filtered.filter(cargo => cargo.type === filterType.value)
  }

  // Sort
  filtered.sort((a, b) => {
    switch (currentSort.value) {
      case 'value-desc': return b.value - a.value
      case 'value-asc': return a.value - b.value
      case 'weight-desc': return b.weight - a.weight
      case 'id': return a.id.localeCompare(b.id)
      default: return 0
    }
  })

  return filtered
})

// Simple methods
const formatNumber = (num) => num.toLocaleString('en-IN')

const getTypeBadgeClass = (type) => {
  const classes = {
    'Electronics': 'bg-blue-100 text-blue-800',
    'Textiles': 'bg-pink-100 text-pink-800',
    'Machinery': 'bg-purple-100 text-purple-800',
    'Food Items': 'bg-green-100 text-green-800',
    'Books': 'bg-yellow-100 text-yellow-800',
    'Furniture': 'bg-orange-100 text-orange-800'
  }
  return classes[type] || 'bg-gray-100 text-gray-800'
}

const openAddCargoModal = () => {
  isEditMode.value = false
  cargoForm.value = {
    id: `CG${String(cargoData.value.length + 1).padStart(3, '0')}`,
    shipmentId: '',
    type: '',
    value: 0,
    weight: 0,
    description: ''
  }
  showCargoModal.value = true
}

const closeCargoModal = () => {
  showCargoModal.value = false
  isEditMode.value = false
}

const saveCargo = () => {
  if (cargoForm.value.id && cargoForm.value.type) {
    if (isEditMode.value) {
      // Update existing cargo
      const index = cargoData.value.findIndex(c => c.id === cargoForm.value.id)
      if (index !== -1) {
        cargoData.value[index] = {
          ...cargoForm.value,
          shipmentRoute: cargoData.value[index].shipmentRoute || { origin: 'Mumbai', destination: 'Chennai' }
        }
      }
    } else {
      // Add new cargo
      cargoData.value.push({
        ...cargoForm.value,
        shipmentRoute: { origin: 'Mumbai', destination: 'Chennai' }
      })
    }
    closeCargoModal()
  }
}

const editCargo = (cargo) => {
  isEditMode.value = true
  cargoForm.value = { ...cargo }
  showCargoModal.value = true
}

// MODIFICATION: This function now opens the confirmation dialog instead of using window.confirm
const promptDeleteCargo = (cargo) => {
  cargoToDelete.value = cargo
  showConfirmDialog.value = true
}

// MODIFICATION: This function handles the actual deletion after confirmation
const handleConfirmDelete = () => {
  if (cargoToDelete.value) {
    cargoData.value = cargoData.value.filter(c => c.id !== cargoToDelete.value.id)
  }
  cancelDelete() // Close the dialog and reset state
}

// MODIFICATION: This function closes the dialog and resets the state when canceled
const cancelDelete = () => {
  showConfirmDialog.value = false
  cargoToDelete.value = null
}
</script>

<style scoped>
/* Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #F3F4F6;
}

::-webkit-scrollbar-thumb {
  background: #CBD5E1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94A3B8;
}

/* Animation classes */
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-slide-in {
  animation: slideIn 0.3s ease-out;
}

.animate-slide-down {
  animation: slideDown 0.3s ease-out;
}

.animate-modal-in {
  animation: modalIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes modalIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* Hover scale utility */
.hover\:scale-102:hover {
  transform: scale(1.02);
}

.active\:scale-98:active {
  transform: scale(0.98);
}

/* Pulse animation for status indicators */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* Skeleton loading animation */
@keyframes skeleton {
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: calc(200px + 100%) 0;
  }
}

.skeleton {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: skeleton 1.5s infinite;
}
</style>