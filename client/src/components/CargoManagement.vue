<template>
  <div class="cargo-management-page bg-[#F5F5F7] min-h-screen p-6 space-y-6">
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

    <!-- Error State -->
    <div v-if="error" class="bg-red-50 border border-red-200 rounded-2xl p-6 mb-8">
      <div class="flex items-center gap-3">
        <div class="w-6 h-6 text-red-600">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" y1="9" x2="9" y2="15"></line>
            <line x1="9" y1="9" x2="15" y2="15"></line>
          </svg>
        </div>
        <div>
          <h3 class="text-red-800 font-semibold">Error Loading Data</h3>
          <p class="text-red-600 text-sm">{{ error }}</p>
        </div>
        <button 
          @click="loadCargos"
          class="ml-auto px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
        >
          Retry
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoadingData" class="bg-white rounded-2xl p-8 shadow-sm border border-gray-100 mb-8">
      <div class="flex items-center justify-center gap-3">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <p class="text-gray-600">Loading cargo data...</p>
      </div>
    </div>

    <!-- Main Content - Only show when not loading and no error -->
    <div v-if="!isLoadingData && !error">
      <!-- ===== CARGO METRICS DASHBOARD ===== -->
      <!-- Interactive metrics cards showing key cargo statistics -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <!-- Total Cargo Count -->
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

    <!-- ===== SEARCH AND FILTERING CONTROLS ===== -->
    <!-- Comprehensive search, filter, and action bar -->
    <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 mb-8">
      <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        <!-- Search Input with Icon -->
        <div class="relative flex-1 max-w-md">
          <Search class="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search cargo by ID, type, or description..."
            class="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          />
        </div>

        <!-- Filter Controls and Actions -->
        <div class="flex items-center gap-3">
          <!-- Type Filter Dropdown -->
          <select
            v-model="filterType"
            class="px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          >
            <option value="">All Types</option>
            <option v-for="type in cargoTypes" :key="type" :value="type">{{ type }}</option>
          </select>

          <!-- Sort Options Dropdown -->
          <select
            v-model="currentSort"
            class="px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          >
            <option value="value-desc">Value (High to Low)</option>
            <option value="value-asc">Value (Low to High)</option>
            <option value="weight-desc">Weight (Heavy to Light)</option>
            <option value="id">Cargo ID</option>
          </select>

          <!-- Primary Action Button -->
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

      <!-- Desktop Table View -->
      <div class="hidden lg:block overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50 sticky top-0">
              <tr>
                <th class="p-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider min-w-[120px]">Cargo ID</th>
                <th class="p-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider min-w-[180px]">Shipment</th>
                <th class="p-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider min-w-[100px]">Type</th>
                <th class="p-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider min-w-[100px]">Value</th>
                <th class="p-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider min-w-[80px]">Weight</th>
                <th class="p-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider min-w-[200px]">Description</th>
                <th class="p-3 text-center text-xs font-semibold text-gray-700 uppercase tracking-wider min-w-[120px]">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="cargo in filteredCargo" :key="cargo.id" class="hover:bg-gray-50 transition-colors duration-150">
                
                <!-- Cargo ID -->
                <td class="p-3">
                  <span class="font-mono text-sm font-medium text-gray-900">{{ cargo.id }}</span>
                </td>

                <!-- Shipment -->
                <td class="p-3">
                  <div class="text-sm">
                    <div class="font-medium text-gray-900">{{ cargo.shipmentId }}</div>
                    <div class="text-gray-500 text-xs">{{ cargo.shipmentRoute.origin }} → {{ cargo.shipmentRoute.destination }}</div>
                  </div>
                </td>

                <!-- Type -->
                <td class="p-3">
                  <span :class="getTypeBadgeClass(cargo.type)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                    {{ cargo.type }}
                  </span>
                </td>

                <!-- Value -->
                <td class="p-3">
                  <span class="text-sm font-medium text-gray-900">₹{{ formatNumber(cargo.value) }}</span>
                </td>

                <!-- Weight -->
                <td class="p-3">
                  <span class="text-sm text-gray-600">{{ cargo.weight }} kg</span>
                </td>

                <!-- Description -->
                <td class="p-3">
                  <span class="text-sm text-gray-600 max-w-xs truncate block" :title="cargo.description">{{ cargo.description }}</span>
                </td>

                <!-- Actions -->
                <td class="p-3">
                  <div class="flex items-center justify-center gap-1">
                    <button 
                      @click="editCargo(cargo)" 
                      class="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors duration-200"
                      :title="`Edit cargo ${cargo.id}`"
                    >
                      <Pencil class="w-4 h-4" />
                    </button>
                    <button 
                      @click.stop="promptDeleteCargo(cargo)" 
                      class="p-2 text-red-600 hover:bg-red-100 rounded-lg transition-colors duration-200"
                      :title="`Delete cargo ${cargo.id}`"
                    >
                      <Trash2 class="w-4 h-4" />
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Mobile/Tablet Card View -->
      <div class="block lg:hidden space-y-4 p-4">
        <div
          v-for="cargo in filteredCargo"
          :key="cargo.id"
          class="bg-white border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow"
        >
          <!-- Header with ID and Type -->
          <div class="flex items-center justify-between mb-3">
            <div class="font-mono font-bold text-gray-900">{{ cargo.id }}</div>
            <span :class="getTypeBadgeClass(cargo.type)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
              {{ cargo.type }}
            </span>
          </div>

          <!-- Shipment Info -->
          <div class="mb-3 p-3 bg-gray-50 rounded-lg">
            <div class="text-sm font-medium text-gray-900 mb-1">{{ cargo.shipmentId }}</div>
            <div class="text-xs text-gray-500">{{ cargo.shipmentRoute.origin }} → {{ cargo.shipmentRoute.destination }}</div>
          </div>

          <!-- Value and Weight Grid -->
          <div class="grid grid-cols-2 gap-4 mb-3">
            <div class="text-center p-3 bg-gray-50 rounded-lg">
              <div class="text-sm font-semibold text-gray-900">₹{{ formatNumber(cargo.value) }}</div>
              <div class="text-xs text-gray-500">Value</div>
            </div>
            <div class="text-center p-3 bg-gray-50 rounded-lg">
              <div class="text-sm font-semibold text-gray-900">{{ cargo.weight }} kg</div>
              <div class="text-xs text-gray-500">Weight</div>
            </div>
          </div>

          <!-- Description -->
          <div class="mb-4">
            <div class="text-xs text-gray-500 mb-1">Description</div>
            <div class="text-sm text-gray-700">{{ cargo.description }}</div>
          </div>

          <!-- Actions -->
          <div class="flex items-center justify-end gap-2">
            <button 
              @click="editCargo(cargo)"
              class="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors"
              :title="`Edit cargo ${cargo.id}`"
            >
              <Pencil class="w-4 h-4" />
            </button>
            <button 
              @click.stop="promptDeleteCargo(cargo)"
              class="p-2 text-red-600 hover:bg-red-100 rounded-lg transition-colors"
              :title="`Delete cargo ${cargo.id}`"
            >
              <Trash2 class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="filteredCargo.length === 0" class="flex flex-col items-center justify-center py-16">
        <Package class="w-16 h-16 text-gray-300 mb-4" />
        <p class="text-lg font-medium text-gray-500 mb-2">No cargo found</p>
        <p class="text-gray-400 mb-6">{{ searchQuery ? 'Try adjusting your search' : 'Add your first cargo item to get started' }}</p>
        <button 
          @click="addNewCargo"
          class="flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors duration-200"
        >
          <Plus class="w-5 h-5" />
          Add Cargo
        </button>
      </div>
    </div>

    <!-- 
      FIX: Custom reusable BaseModal component for cargo add/edit functionality
      - Replaces traditional modal implementations with consistent design
      - Provides proper accessibility features and keyboard navigation
      - Integrates seamlessly with our design system
    -->
    <BaseModal :show="showCargoModal" @close="closeCargoModal" max-width="md">
      <template #header>
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
            <Package class="w-5 h-5 text-blue-600" />
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900">{{ isEditMode ? 'Edit Cargo' : 'Add New Cargo' }}</h2>
            <p class="text-sm text-gray-500">{{ isEditMode ? 'Update cargo information' : 'Add cargo to shipment' }}</p>
          </div>
        </div>
      </template>
      <template #body>
        <form @submit.prevent="saveCargo" id="cargoForm" class="space-y-4">
          <!-- Cargo ID Field - disabled in edit mode to prevent changes -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Cargo ID</label>
            <input type="text" 
                   v-model="cargoForm.id" 
                   :disabled="isEditMode" 
                   :class="isEditMode ? 'w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 focus:outline-none' : 'w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'" />
          </div>
          
          <!-- Shipment ID Field -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Shipment ID</label>
            <input type="text" 
                   v-model="cargoForm.shipmentId" 
                   class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" 
                   placeholder="Enter shipment ID" />
          </div>
          
          <!-- Cargo Type Dropdown -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Type</label>
            <select v-model="cargoForm.type" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option value="">Select Type</option>
              <option v-for="type in cargoTypes" :key="type" :value="type">{{ type }}</option>
            </select>
          </div>
          
          <!-- Value and Weight Fields - side by side -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Value (₹)</label>
              <input type="number" 
                     v-model="cargoForm.value" 
                     min="0"
                     step="1"
                     class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" 
                     placeholder="0" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Weight (kg)</label>
              <input type="number" 
                     v-model="cargoForm.weight" 
                     min="0"
                     step="0.1"
                     class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" 
                     placeholder="0.0" />
            </div>
          </div>
          
          <!-- Description Field -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea v-model="cargoForm.description" 
                      rows="3" 
                      class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      placeholder="Enter cargo description..."></textarea>
          </div>
        </form>
      </template>
      <template #footer>
        <!-- Cancel Button -->
        <button type="button" 
                @click="closeCargoModal" 
                class="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors duration-200">
          Cancel
        </button>
        <!-- Submit Button - text changes based on mode -->
        <button type="submit" 
                form="cargoForm" 
                class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200">
          {{ isEditMode ? 'Save Changes' : 'Add Cargo' }}
        </button>
      </template>
    </BaseModal>

    <!-- 
      FIX: Custom ConfirmDialog component replacing browser's native confirm()
      - Provides consistent branding and styling across the application
      - Better accessibility with proper ARIA labels and focus management
      - Improved UX with smooth animations and clear action buttons
      - Prevents accidental deletions with prominent warning styling
    -->
    <ConfirmDialog
      :show="showConfirmDialog"
      title="Delete Cargo Item"
      :message="cargoToDelete ? `Are you sure you want to permanently delete cargo item '${cargoToDelete.id}'? This action cannot be undone and will remove all associated data.` : ''"
      confirm-text="Delete"
      cancel-text="Cancel"
      variant="danger"
      @confirm="handleConfirmDelete"
      @cancel="cancelDelete"
    />
    </div> <!-- End of main content conditional -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  Package, Plus, Search, Pencil, Trash2, IndianRupee, Grid3X3, TrendingUp
} from 'lucide-vue-next'
import BaseModal from './shared/BaseModal.vue'
// FIX: Import reusable ConfirmDialog to replace browser's native confirm() dialog
// This provides better UX, consistent styling, and accessibility features
import ConfirmDialog from './shared/ConfirmDialog.vue'
import { cargoApi } from '../services/api.js'

// ===== REACTIVE STATE MANAGEMENT =====
// Search and filtering state
const searchQuery = ref('')
const filterType = ref('')
const currentSort = ref('value-desc')

// Modal state management
const showCargoModal = ref(false)
const isLoading = ref(false)
const isEditMode = ref(false)

// API state management
const isLoadingData = ref(true)
const error = ref(null)

// FIX: State for custom confirmation dialog (replaces window.confirm)
// This provides a more user-friendly and branded confirmation experience
const showConfirmDialog = ref(false)
const cargoToDelete = ref(null) // Stores the cargo item pending deletion

// ===== FORM DATA =====
// Form structure for adding/editing cargo items
const cargoForm = ref({
  id: '',
  shipmentId: '',
  type: '',
  value: 0,
  weight: 0,
  description: ''
})

// ===== DATA MANAGEMENT =====
// Cargo data - loaded from backend API
const cargoData = ref([])

// ===== SAMPLE DATA - COMMENTED OUT =====
// Mock cargo data for demonstration - replaced with API calls
// This data is now fetched from the backend database
/*
const cargoDataMock = [
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
]
*/

// Available cargo types for dropdown selection
// DB Schema allows: 'Electronics', 'Perishables', 'Hazardous', 'General'
// CHECK constraint: type IN ('Electronics', 'Perishables', 'Hazardous', 'General')
const cargoTypes = ['Electronics', 'Perishables', 'Hazardous', 'General']

// ===== COMMENTED OUT - UI options not supported by current DB schema =====
// To be re-enabled after schema update and Kafka integration
// const cargoTypesExtended = ['Electronics', 'Textiles', 'Machinery', 'Food Items', 'Books', 'Furniture']
// Mapping suggestions for future:
// - 'Food Items' -> 'Perishables'
// - 'Books', 'Furniture' -> 'General' 
// - 'Textiles', 'Machinery' -> New constraint values needed

// ===== COMPUTED PROPERTIES =====
// Dashboard metrics calculated from cargo data
const metrics = computed(() => {
  const totalCargo = cargoData.value.length
  const totalValue = cargoData.value.reduce((sum, cargo) => sum + cargo.value, 0)
  const categories = new Set(cargoData.value.map(cargo => cargo.type)).size
  const highValueItems = cargoData.value.filter(cargo => cargo.value > 50000).length

  return { totalCargo, totalValue, categories, highValueItems }
})

// Filtered and sorted cargo list based on search query, type filter, and sort option
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

// ===== UTILITY METHODS =====
// Format numbers with Indian locale (adds commas for thousands)
const formatNumber = (num) => num.toLocaleString('en-IN')

// Get appropriate CSS classes for cargo type badges
// Updated to match DB schema constraints
const getTypeBadgeClass = (type) => {
  const classes = {
    // Current DB-allowed types
    'Electronics': 'bg-blue-100 text-blue-800',
    'Perishables': 'bg-green-100 text-green-800',
    'Hazardous': 'bg-red-100 text-red-800',
    'General': 'bg-gray-100 text-gray-800'
    
    // ===== COMMENTED OUT - Not supported by current DB schema =====
    // To be re-enabled after schema update
    // 'Textiles': 'bg-pink-100 text-pink-800',
    // 'Machinery': 'bg-purple-100 text-purple-800', 
    // 'Food Items': 'bg-green-100 text-green-800',  // Consider mapping to 'Perishables'
    // 'Books': 'bg-yellow-100 text-yellow-800',     // Consider mapping to 'General'
    // 'Furniture': 'bg-orange-100 text-orange-800'  // Consider mapping to 'General'
  }
  return classes[type] || 'bg-gray-100 text-gray-800'
}

// ===== MODAL MANAGEMENT =====
// Open modal for adding a new cargo item
const openAddCargoModal = () => {
  isEditMode.value = false
  // Generate next cargo ID automatically
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

// Close cargo modal and reset edit mode
const closeCargoModal = () => {
  showCargoModal.value = false
  isEditMode.value = false
}

// Save cargo (add new or update existing)
const saveCargo = async () => {
  // Basic validation - ensure required fields are filled
  if (cargoForm.value.id && cargoForm.value.type) {
    try {
      if (isEditMode.value) {
        // Update existing cargo item via API
        await updateCargo(cargoForm.value.id, cargoForm.value)
      } else {
        // Create new cargo item via API
        await createCargo(cargoForm.value)
      }
      closeCargoModal()
    } catch (err) {
      // Error is already handled in the API functions
      // Modal remains open so user can retry
    }
  }
}

// Open modal for editing existing cargo
const editCargo = (cargo) => {
  isEditMode.value = true
  // Deep copy to avoid direct mutation
  cargoForm.value = { ...cargo }
  showCargoModal.value = true
}

// ===== DELETION WORKFLOW (FIX FOR BROKEN CONFIRM DIALOG) =====
// FIX: Replaced window.confirm() with custom ConfirmDialog component
// This provides better UX, consistent branding, and proper accessibility
const promptDeleteCargo = (cargo) => {
  cargoToDelete.value = cargo
  showConfirmDialog.value = true
}

// Execute deletion after user confirms in the custom dialog
const handleConfirmDelete = async () => {
  if (cargoToDelete.value) {
    try {
      // Delete cargo via API
      await deleteCargo(cargoToDelete.value.id)
      
      // FIX: Add success feedback (could be extended with toast notifications)
      console.log(`Successfully deleted cargo item: ${cargoToDelete.value.id}`)
    } catch (err) {
      // Error is already handled in the API function
    }
  }
  // Always clean up state after deletion attempt
  cancelDelete()
}

// Cancel deletion and reset confirmation dialog state
const cancelDelete = () => {
  showConfirmDialog.value = false
  cargoToDelete.value = null
}

// ===== API FUNCTIONS =====
// Load cargo data from backend
const loadCargos = async () => {
  try {
    isLoadingData.value = true
    error.value = null
    const response = await cargoApi.getAll()
    
    // Transform backend data to frontend format
    cargoData.value = (response || []).map(cargo => ({
      id: `CG${String(cargo.cargoId).padStart(3, '0')}`,
      shipmentId: cargo.shipment ? `SH${String(cargo.shipment.shipmentId).padStart(3, '0')}` : 'N/A',
      shipmentRoute: {
        origin: cargo.shipment?.origin || 'Unknown',
        destination: cargo.shipment?.destination || 'Unknown'
      },
      type: cargo.type,
      value: cargo.value,
      weight: cargo.weight,
      description: cargo.description
    }))
  } catch (err) {
    error.value = 'Failed to load cargo data. Please try again.'
    console.error('Error loading cargos:', err)
    cargoData.value = []
  } finally {
    isLoadingData.value = false
  }
}

// Create new cargo item
const createCargo = async (cargoItem) => {
  try {
    isLoading.value = true
    const newCargo = await cargoApi.create(cargoItem)
    cargoData.value.push(newCargo)
    return newCargo
  } catch (err) {
    error.value = 'Failed to create cargo. Please try again.'
    console.error('Error creating cargo:', err)
    throw err
  } finally {
    isLoading.value = false
  }
}

// Update existing cargo item
const updateCargo = async (id, cargoItem) => {
  try {
    isLoading.value = true
    const updatedCargo = await cargoApi.update(id, cargoItem)
    const index = cargoData.value.findIndex(c => c.id === id)
    if (index !== -1) {
      cargoData.value[index] = updatedCargo
    }
    return updatedCargo
  } catch (err) {
    error.value = 'Failed to update cargo. Please try again.'
    console.error('Error updating cargo:', err)
    throw err
  } finally {
    isLoading.value = false
  }
}

// Delete cargo item
const deleteCargo = async (id) => {
  try {
    await cargoApi.delete(id)
    cargoData.value = cargoData.value.filter(c => c.id !== id)
  } catch (err) {
    error.value = 'Failed to delete cargo. Please try again.'
    console.error('Error deleting cargo:', err)
    throw err
  }
}

// ===== LIFECYCLE HOOKS =====
// Load data when component mounts
onMounted(() => {
  loadCargos()
})
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