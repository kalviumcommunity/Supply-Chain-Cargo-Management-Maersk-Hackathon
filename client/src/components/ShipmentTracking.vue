<template>
  <div class="shipment-tracking-page bg-[#F5F5F7] min-h-screen">
    <!-- Main Container with proper spacing -->
    <div class="w-full px-10 py-8 space-y-10">
      <!-- Page Header -->
      <header class="flex justify-between items-start mb-8">
        <div class="flex items-center gap-4">
          <div class="w-7 h-7 text-[#3B82F6]">
            <Truck class="w-full h-full" />
          </div>
          <div>
            <h1 class="text-4xl font-bold text-[#0F172A] tracking-tight leading-none">Shipment Tracking</h1>
            <p class="text-[#64748B] text-base mt-1">Monitor and track all shipments in real-time.</p>
          </div>
        </div>
        <button 
          @click="createNewShipment"
          class="bg-gradient-to-br from-[#3B82F6] to-[#2563EB] text-white px-7 py-3.5 rounded-xl hover:scale-102 active:scale-98 hover:shadow-lg shadow-[0_4px_12px_rgba(59,130,246,0.3)] transition-all duration-200 flex items-center gap-3 font-semibold text-sm"
        >
          <Plus class="w-5 h-5" />
          New Shipment
        </button>
      </header>

      <!-- Error State -->
      <div v-if="error" class="bg-red-50 border-l-4 border-red-500 p-6 rounded-xl mb-8">
        <div class="flex items-center gap-3">
          <svg class="w-5 h-5 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" y1="9" x2="9" y2="15"></line>
            <line x1="9" y1="9" x2="15" y2="15"></line>
          </svg>
          <span class="text-red-700 font-medium">{{ error }}</span>
          <button 
            @click="loadShipments" 
            class="ml-auto px-3 py-1 bg-red-100 text-red-700 rounded-md hover:bg-red-200 transition-colors text-sm font-medium"
          >
            Retry
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center py-24">
        <div class="flex flex-col items-center gap-4">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
          <p class="text-gray-600 font-medium">Loading shipments...</p>
        </div>
      </div>

      <!-- Main Content -->
      <div v-else class="space-y-10">

        <!-- Quick Stats Banner -->
        <div class="bg-gradient-to-r from-[#F0F9FF] to-[#E0F2FE] border-l-4 border-[#3B82F6] p-6 rounded-xl shadow-sm">
          <div class="flex items-center gap-6 text-sm font-medium text-[#1E293B]">
            <span>Total Active: <strong class="text-[#3B82F6]">{{ metrics.totalActive }}</strong> shipments</span>
            <span class="text-[#94A3B8]">•</span>
            <span>On Time: <strong class="text-[#10B981]">{{ onTimeCount }} ({{ metrics.onTimePercentage }}%)</strong></span>
            <span class="text-[#94A3B8]">•</span>
            <span>Delayed: <strong class="text-[#EF4444]">{{ metrics.delayed }} ({{ delayedPercentage }}%)</strong></span>
          </div>
        </div>

        <!-- Status Overview Cards -->
        <div class="grid grid-cols-1 md:grid-cols-5 gap-6">
          <div 
            v-for="status in statusCards" 
            :key="status.id"
            @click="filterByStatus(status.id)"
            :class="[
              'status-card group bg-white rounded-3xl p-8 border border-gray-100 transition-all duration-300 cursor-pointer',
              'hover:shadow-[0_20px_60px_-12px_rgba(0,0,0,0.15)] hover:-translate-y-2',
              activeFilter === status.id ? 'ring-2 ring-blue-500 shadow-lg' : ''
            ]"
          >
        <div class="flex flex-col items-center text-center">
          <!-- Icon Circle -->
          <div :class="[
            'p-4 rounded-full transition-all duration-300 group-hover:scale-110 group-hover:rotate-3 mb-4',
            status.iconBg
          ]">
            <component :is="status.icon" :class="['w-7 h-7', status.iconColor]" />
          </div>
          
          <!-- Number -->
          <p :class="['text-3xl font-bold mb-2 transition-colors duration-300', status.textColor]">
            {{ status.count }}
          </p>
          
          <!-- Label -->
          <p :class="['text-sm font-medium mb-3 transition-colors duration-300', status.labelColor]">
            {{ status.label }}
          </p>
          
          <!-- Status Badge -->
          <div :class="[
            'px-3 py-1.5 rounded-lg text-xs font-bold uppercase tracking-wider flex items-center gap-2',
            status.badgeClass,
            status.id === 'delayed' ? 'animate-pulse' : ''
          ]">
            <component :is="status.icon" class="w-3 h-3" />
            {{ status.label }}
          </div>
          
          <!-- Progress Bar -->
          <div class="w-full mt-4 h-1 bg-gray-100 rounded-full overflow-hidden">
            <div 
              :class="['h-full rounded-full transition-all duration-500', status.progressColor]"
              :style="{ width: status.progress + '%' }"
            ></div>
          </div>
        </div>
      </div>
    </div>

        <!-- Search & Filter Toolbar -->
        <div class="bg-white rounded-2xl p-8 shadow-sm border border-gray-100">
          <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
        <!-- Search -->
        <div class="relative flex-1 max-w-lg">
          <Search class="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
          <input
            v-model="searchQuery"
            @focus="showSearchSuggestions = true"
            @blur="hideSearchSuggestions"
            type="text"
            placeholder="Search shipments by ID, origin, destination, or status..."
            class="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
          />
        </div>

        <!-- Filter Actions -->
        <div class="flex items-center gap-3">
          <!-- Filter Dropdown -->
          <div class="relative">
            <button
              @click="toggleFilterMenu"
              class="flex items-center gap-2 px-4 py-3 border border-gray-200 rounded-xl hover:border-blue-500 hover:bg-blue-50 hover:text-blue-700 transition-all duration-200"
            >
              <Filter class="w-5 h-5" />
              Filter by Status
              <span v-if="activeFilters.length" class="ml-1 px-2 py-0.5 bg-blue-500 text-white text-xs rounded-full">
                {{ activeFilters.length }}
              </span>
            </button>
            
            <!-- Filter Dropdown Menu -->
            <div v-if="showFilterDropdown" class="absolute top-full right-0 mt-2 w-64 bg-white rounded-xl shadow-lg border border-gray-100 z-50">
              <div class="p-4">
                <h3 class="font-semibold text-gray-900 mb-3">Filter by Status</h3>
                <div class="space-y-2">
                  <label v-for="status in statusOptions" :key="status.value" class="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-50 cursor-pointer">
                    <input 
                      type="checkbox" 
                      :value="status.value" 
                      v-model="activeFilters"
                      class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                    />
                    <div class="flex items-center gap-2">
                      <component :is="status.icon" class="w-4 h-4" :class="status.color" />
                      <span class="text-sm text-gray-700">{{ status.label }}</span>
                    </div>
                  </label>
                </div>
                <div class="flex gap-2 mt-4 pt-3 border-t border-gray-100">
                  <button 
                    @click="clearFilters"
                    class="flex-1 px-3 py-2 text-sm text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50"
                  >
                    Clear
                  </button>
                  <button 
                    @click="applyFilters"
                    class="flex-1 px-3 py-2 text-sm text-white bg-blue-600 rounded-lg hover:bg-blue-700"
                  >
                    Apply
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Sort Dropdown -->
          <div class="relative">
            <button
              @click="toggleSortMenu"
              class="flex items-center gap-2 px-4 py-3 border border-gray-200 rounded-xl hover:border-blue-500 hover:bg-blue-50 hover:text-blue-700 transition-all duration-200"
            >
              <ArrowUpDown class="w-5 h-5" />
              Sort by Date
            </button>
            
            <!-- Sort Dropdown Menu -->
            <div v-if="showSortDropdown" class="absolute top-full right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-gray-100 z-50">
              <div class="p-2">
                <button 
                  v-for="option in sortOptions" 
                  :key="option.value"
                  @click="setSortOption(option.value)"
                  :class="[
                    'w-full flex items-center gap-3 px-3 py-2 text-sm rounded-lg hover:bg-gray-50 transition-colors',
                    sortBy === option.value ? 'bg-blue-50 text-blue-700' : 'text-gray-700'
                  ]"
                >
                  <component :is="option.icon" class="w-4 h-4" />
                  {{ option.label }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Search Suggestions -->
      <div v-if="showSearchSuggestions && searchSuggestions.length" class="relative">
        <div class="absolute top-2 left-0 w-full max-w-lg bg-white rounded-xl shadow-lg border border-gray-100 z-50">
          <div class="p-2">
            <button 
              v-for="suggestion in searchSuggestions" 
              :key="suggestion"
              @click="applySuggestion(suggestion)"
              class="w-full text-left px-3 py-2 text-sm text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              {{ suggestion }}
            </button>
          </div>
        </div>
      </div>
    </div>

        <!-- Active Shipments Table -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <!-- Table Header -->
          <div class="flex justify-between items-center p-8 border-b border-gray-100">
            <h2 class="text-xl font-semibold text-gray-900">Active Shipments</h2>
            <button 
              @click="exportShipments"
              class="flex items-center gap-2 px-4 py-2 text-gray-700 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors duration-200"
            >
              <Download class="w-4 h-4" />
              Export
            </button>
          </div>

      <!-- Table -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gradient-to-r from-gray-50 to-gray-100">
            <tr>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Shipment ID</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Route</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Status</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Cargo Value</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Est. Delivery</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Created</th>
              <th class="px-6 py-4 text-left text-xs font-bold text-gray-600 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr 
              v-for="shipment in filteredShipments" 
              :key="shipment.id"
              @click="viewShipmentDetails(shipment)"
              class="shipment-row group hover:bg-gradient-to-r hover:from-blue-50 hover:to-transparent hover:border-l-4 hover:border-l-blue-500 transition-all duration-200 cursor-pointer"
            >
              <!-- Shipment ID -->
              <td class="px-6 py-6">
                <span class="font-mono font-bold text-gray-900 group-hover:text-blue-600 transition-colors">
                  {{ shipment.id }}
                </span>
              </td>

              <!-- Route -->
              <td class="px-6 py-6">
                <div class="flex items-center gap-2">
                  <MapPin class="w-4 h-4 text-gray-400" />
                  <span class="font-medium text-gray-900">{{ shipment.route.origin }}</span>
                  <ArrowRight class="w-4 h-4 text-gray-400" />
                  <span class="font-medium text-gray-900">{{ shipment.route.destination }}</span>
                </div>
              </td>

              <!-- Status -->
              <td class="px-6 py-6">
                <div :class="getStatusBadgeClass(shipment.status)">
                  <component :is="getStatusIcon(shipment.status)" class="w-3 h-3" />
                  {{ getStatusLabel(shipment.status) }}
                </div>
              </td>

              <!-- Cargo Value -->
              <td class="px-6 py-6">
                <div>
                  <div class="font-bold text-gray-900">₹{{ formatCurrency(shipment.cargoValue) }}</div>
                  <div class="text-sm text-gray-500">{{ shipment.cargoItems }} items</div>
                </div>
              </td>

              <!-- Est. Delivery -->
              <td class="px-6 py-6">
                <div class="flex items-center gap-2">
                  <Calendar class="w-4 h-4 text-gray-400" />
                  <span :class="isOverdue(shipment.estimatedDelivery) ? 'text-red-600 font-medium' : 'text-gray-700'">
                    {{ formatDate(shipment.estimatedDelivery) }}
                  </span>
                </div>
              </td>

              <!-- Created -->
              <td class="px-6 py-6 text-gray-600">
                {{ formatDate(shipment.created) }}
              </td>

              <!-- Actions -->
              <td class="px-6 py-6">
                <div class="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                  <button 
                    @click.stop="viewShipment(shipment)"
                    class="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors duration-200"
                    title="View Details"
                  >
                    <Eye class="w-4 h-4" />
                  </button>
                  <button 
                    @click.stop="editShipment(shipment)"
                    class="p-2 text-gray-400 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition-colors duration-200"
                    title="Edit Shipment"
                  >
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                      <path d="m18.5 2.5 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                  </button>
                  <button 
                    @click.stop="promptDeleteShipment(shipment)"
                    class="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors duration-200"
                    title="Delete Shipment"
                  >
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="m19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      <line x1="10" y1="11" x2="10" y2="17"></line>
                      <line x1="14" y1="11" x2="14" y2="17"></line>
                    </svg>
                  </button>
                  <button 
                    @click.stop="trackShipment(shipment)"
                    class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 hover:text-blue-700 transition-all duration-200"
                  >
                    Track
                    <Navigation class="w-3 h-3" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Empty State -->
        <div v-if="filteredShipments.length === 0" class="flex flex-col items-center justify-center py-16">
          <Package class="w-16 h-16 text-gray-300 mb-4" />
          <p class="text-lg font-medium text-gray-500 mb-2">No shipments found</p>
          <p class="text-gray-400 mb-6">Create your first shipment to start tracking</p>
          <button 
            @click="createNewShipment"
            class="flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors duration-200"
          >
            <Plus class="w-5 h-5" />
            New Shipment
          </button>
        </div>
      </div>
    </div>

    <!-- Shipment Detail Modal -->
    <div v-if="showShipmentModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click="closeShipmentModal">
      <div @click.stop class="bg-white rounded-2xl shadow-2xl w-full max-w-4xl mx-4 max-h-[90vh] overflow-hidden">
        <!-- Modal Header -->
        <div class="flex justify-between items-center p-6 border-b border-gray-100">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">Shipment Details</h2>
            <p class="text-gray-600">{{ selectedShipment?.id }}</p>
          </div>
          <button @click="closeShipmentModal" class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- Modal Content -->
        <div class="p-6 overflow-y-auto max-h-[calc(90vh-100px)]">
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <!-- Left Column - Basic Info -->
            <div class="space-y-6">
              <!-- Route Information -->
              <div class="bg-gray-50 rounded-xl p-6">
                <h3 class="text-lg font-semibold text-gray-900 mb-4">Route Information</h3>
                <div class="space-y-3">
                  <div class="flex items-center gap-3">
                    <MapPin class="w-5 h-5 text-blue-600" />
                    <div>
                      <p class="font-medium text-gray-900">Origin</p>
                      <p class="text-gray-600">{{ selectedShipment?.route.origin }}</p>
                    </div>
                  </div>
                  <div class="flex items-center gap-3">
                    <MapPin class="w-5 h-5 text-green-600" />
                    <div>
                      <p class="font-medium text-gray-900">Destination</p>
                      <p class="text-gray-600">{{ selectedShipment?.route.destination }}</p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Cargo Information -->
              <div class="bg-gray-50 rounded-xl p-6">
                <h3 class="text-lg font-semibold text-gray-900 mb-4">Cargo Information</h3>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <p class="text-sm text-gray-600">Total Value</p>
                    <p class="text-xl font-bold text-gray-900">₹{{ formatCurrency(selectedShipment?.cargoValue || 0) }}</p>
                  </div>
                  <div>
                    <p class="text-sm text-gray-600">Items Count</p>
                    <p class="text-xl font-bold text-gray-900">{{ selectedShipment?.cargoItems }} items</p>
                  </div>
                </div>
              </div>

              <!-- Dates -->
              <div class="bg-gray-50 rounded-xl p-6">
                <h3 class="text-lg font-semibold text-gray-900 mb-4">Important Dates</h3>
                <div class="space-y-3">
                  <div class="flex justify-between">
                    <span class="text-gray-600">Created</span>
                    <span class="font-medium">{{ formatDate(selectedShipment?.created) }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600">Estimated Delivery</span>
                    <span class="font-medium" :class="isOverdue(selectedShipment?.estimatedDelivery) ? 'text-red-600' : ''">
                      {{ formatDate(selectedShipment?.estimatedDelivery) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right Column - Timeline -->
            <div class="space-y-6">
              <!-- Current Status -->
              <div class="bg-gray-50 rounded-xl p-6">
                <h3 class="text-lg font-semibold text-gray-900 mb-4">Current Status</h3>
                <div v-if="selectedShipment" :class="getStatusBadgeClass(selectedShipment.status)">
                  <component :is="getStatusIcon(selectedShipment.status)" class="w-4 h-4" />
                  {{ getStatusLabel(selectedShipment.status) }}
                </div>
              </div>

              <!-- Tracking Timeline -->
              <div class="bg-gray-50 rounded-xl p-6">
                <h3 class="text-lg font-semibold text-gray-900 mb-4">Tracking Timeline</h3>
                <div class="space-y-4">
                  <div v-for="event in getTimelineEvents(selectedShipment)" :key="event.id" class="flex gap-4">
                    <div class="flex flex-col items-center">
                      <div :class="['w-3 h-3 rounded-full', event.completed ? 'bg-green-500' : 'bg-gray-300']"></div>
                      <div v-if="!event.isLast" class="w-0.5 h-8 bg-gray-200 mt-2"></div>
                    </div>
                    <div class="flex-1 pb-8">
                      <p class="font-medium text-gray-900">{{ event.title }}</p>
                      <p class="text-sm text-gray-600">{{ event.description }}</p>
                      <p class="text-xs text-gray-500 mt-1">{{ event.time }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex justify-end gap-3 pt-6 border-t border-gray-100 mt-8">
            <button @click="closeShipmentModal" class="px-6 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
              Close
            </button>
            <button @click="trackShipment(selectedShipment)" class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
              Track Live
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- New Shipment Modal -->
    <BaseModal :show="showNewShipmentModal" @close="closeNewShipmentModal" max-width="lg">
      <template #header>
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
            <Plus class="w-5 h-5 text-blue-600" />
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900">{{ isEditMode ? 'Edit Shipment' : 'Create New Shipment' }}</h2>
            <p class="text-sm text-gray-500">{{ isEditMode ? 'Update shipment details' : 'Fill out the shipment details' }}</p>
          </div>
        </div>
      </template>
      
      <template #body>
        <form @submit.prevent="saveShipment" class="space-y-6">
          <!-- Shipment ID -->
          <div>
            <label for="shipmentId" class="block text-sm font-medium text-gray-700 mb-2">
              Shipment ID
            </label>
            <input
              id="shipmentId"
              v-model="shipmentForm.id"
              type="text"
              readonly
              class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 font-mono text-sm"
            />
          </div>

          <!-- Route Selection -->
          <div>
            <label for="route" class="block text-sm font-medium text-gray-700 mb-2">
              Route <span class="text-red-500">*</span>
            </label>
            <select
              id="route"
              v-model="shipmentForm.route"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Select a route</option>
              <option v-for="route in availableRoutes" :key="route" :value="route">{{ route }}</option>
            </select>
          </div>

          <!-- Status -->
          <div>
            <label for="status" class="block text-sm font-medium text-gray-700 mb-2">
              Status <span class="text-red-500">*</span>
            </label>
            <select
              id="status"
              v-model="shipmentForm.status"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Select status</option>
              <option value="created">Created</option>
              <option value="picked-up">Picked Up</option>
              <option value="in-transit">In Transit</option>
              <option value="delivered">Delivered</option>
              <option value="delayed">Delayed</option>
            </select>
          </div>

          <!-- Cargo Value -->
          <div>
            <label for="cargoValue" class="block text-sm font-medium text-gray-700 mb-2">
              Cargo Value (₹) <span class="text-red-500">*</span>
            </label>
            <input
              id="cargoValue"
              v-model.number="shipmentForm.cargoValue"
              type="number"
              min="0"
              step="1000"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Enter cargo value"
            />
          </div>

          <!-- Cargo Weight -->
          <div>
            <label for="cargoWeight" class="block text-sm font-medium text-gray-700 mb-2">
              Cargo Weight (kg) <span class="text-red-500">*</span>
            </label>
            <input
              id="cargoWeight"
              v-model.number="shipmentForm.cargoWeight"
              type="number"
              min="0"
              step="0.1"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Enter cargo weight"
            />
          </div>

          <!-- Estimated Delivery Date -->
          <div>
            <label for="estimatedDelivery" class="block text-sm font-medium text-gray-700 mb-2">
              Estimated Delivery Date <span class="text-red-500">*</span>
            </label>
            <input
              id="estimatedDelivery"
              v-model="shipmentForm.estimatedDelivery"
              type="date"
              required
              :min="new Date().toISOString().split('T')[0]"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <!-- Notes -->
          <div>
            <label for="notes" class="block text-sm font-medium text-gray-700 mb-2">
              Notes
            </label>
            <textarea
              id="notes"
              v-model="shipmentForm.notes"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Add any additional notes or special instructions..."
            ></textarea>
          </div>
        </form>
      </template>
      
      <template #footer>
        <button
          type="button"
          @click="closeNewShipmentModal"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
        >
          Cancel
        </button>
        <button
          type="button"
          @click="saveShipment"
          :disabled="isFormLoading"
          class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
        >
          <div v-if="isFormLoading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
          {{ isFormLoading ? (isEditMode ? 'Updating...' : 'Creating...') : (isEditMode ? 'Update Shipment' : 'Create Shipment') }}
        </button>
      </template>
    </BaseModal>

    <!-- Delete Confirmation Dialog -->
    <ConfirmDialog
      :show="showDeleteConfirm"
      title="Delete Shipment"
      :message="`Are you sure you want to delete shipment ${shipmentToDelete?.id}? This action cannot be undone.`"
      confirm-text="Delete"
      cancel-text="Cancel"
      variant="danger"
      @confirm="handleConfirmDelete"
      @cancel="cancelDelete"
    />
    
      </div> <!-- End of v-else content -->
    </div> <!-- End of main container -->
  </div> <!-- End of shipment-tracking-page -->
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import {
  Truck, Plus, Search, Filter, ArrowUpDown, Download, Eye, Navigation,
  Package, MapPin, ArrowRight, Calendar, CheckCircle, AlertTriangle,
  FileText, Clock
} from 'lucide-vue-next'
import BaseModal from './shared/BaseModal.vue'
import ConfirmDialog from './shared/ConfirmDialog.vue'
import { shipmentApi } from '../services/api.js'

// ============================================================================
// REACTIVE STATE
// ============================================================================

const searchQuery = ref('')
const activeFilter = ref('')
const activeFilters = ref([])
const showFilterDropdown = ref(false)
const showSortDropdown = ref(false)
const showSearchSuggestions = ref(false)
const showShipmentModal = ref(false)
const selectedShipment = ref(null)
const sortBy = ref('created-desc')

// API State
const shipments = ref([])
const isLoading = ref(false)
const error = ref(null)

// New Shipment Form State
const showNewShipmentModal = ref(false)
const isFormLoading = ref(false)
const isEditMode = ref(false)

// Delete Confirmation State
const showDeleteConfirm = ref(false)
const shipmentToDelete = ref(null)

// Form Data
const shipmentForm = ref({
  id: '',
  route: '',
  status: '',
  cargoValue: 0,
  cargoWeight: 0,
  estimatedDelivery: '',
  notes: ''
})

// Available options
const availableRoutes = [
  'Mumbai → Chennai',
  'Delhi → Bangalore',
  'Kolkata → Hyderabad',
  'Pune → Kochi',
  'Ahmedabad → Goa',
  'Jaipur → Lucknow'
]

// Mock Data - Commented out, now using API calls
// const shipments = ref([
//   {
//     id: 'SH001',
//     route: { origin: 'Mumbai', destination: 'Chennai' },
//     status: 'in-transit',
//     cargoValue: 750000,
//     cargoItems: 12,
//     estimatedDelivery: new Date('2025-10-15'),
//     created: new Date('2025-10-05')
//   },
//   {
//     id: 'SH002',
//     route: { origin: 'Delhi', destination: 'Bangalore' },
//     status: 'delivered',
//     cargoValue: 320000,
//     cargoItems: 8,
//     estimatedDelivery: new Date('2025-10-12'),
//     created: new Date('2025-10-02')
//   },
//   {
//     id: 'SH003',
//     route: { origin: 'Kolkata', destination: 'Hyderabad' },
//     status: 'picked-up',
//     cargoValue: 180000,
//     cargoItems: 5,
//     estimatedDelivery: new Date('2025-10-18'),
//     created: new Date('2025-10-08')
//   },
//   {
//     id: 'SH004',
//     route: { origin: 'Pune', destination: 'Kochi' },
//     status: 'created',
//     cargoValue: 95000,
//     cargoItems: 3,
//     estimatedDelivery: new Date('2025-10-20'),
//     created: new Date('2025-10-10')
//   },
//   {
//     id: 'SH005',
//     route: { origin: 'Ahmedabad', destination: 'Jaipur' },
//     status: 'delayed',
//     cargoValue: 420000,
//     cargoItems: 15,
//     estimatedDelivery: new Date('2025-10-08'), // Overdue
//     created: new Date('2025-09-28')
//   }
// ])

// ============================================================================
// COMPUTED PROPERTIES
// ============================================================================

// Status card configurations
// Note: Updated to match database schema - status values use proper case
// 'Picked Up' status commented out as it's not in database CHECK constraint
const statusCards = computed(() => [
  {
    id: 'In Transit',
    icon: Truck,
    count: shipments.value.filter(s => s.status === 'In Transit').length,
    label: 'In Transit',
    iconBg: 'bg-gradient-to-br from-teal-50 to-teal-100',
    iconColor: 'text-teal-600',
    textColor: 'text-gray-900 group-hover:text-teal-700',
    labelColor: 'text-gray-600 group-hover:text-teal-600',
    badgeClass: 'bg-gradient-to-r from-teal-100 to-teal-200 text-teal-800 border-l-2 border-teal-500',
    progressColor: 'bg-teal-500',
    progress: 75
  },
  {
    id: 'Delivered',
    icon: CheckCircle,
    count: shipments.value.filter(s => s.status === 'Delivered').length,
    label: 'Delivered',
    iconBg: 'bg-gradient-to-br from-green-50 to-green-100',
    iconColor: 'text-green-600',
    textColor: 'text-gray-900 group-hover:text-green-700',
    labelColor: 'text-gray-600 group-hover:text-green-600',
    badgeClass: 'bg-gradient-to-r from-green-100 to-green-200 text-green-800 border-l-2 border-green-500',
    progressColor: 'bg-green-500',
    progress: 100
  },
  // Picked Up status commented out - not supported in current database schema
  // Can be restored when schema is updated to support this status after Kafka integration
  /*
  {
    id: 'Picked Up',
    icon: Package,
    count: shipments.value.filter(s => s.status === 'Picked Up').length,
    label: 'Picked Up',
    iconBg: 'bg-gradient-to-br from-blue-50 to-blue-100',
    iconColor: 'text-blue-600',
    textColor: 'text-gray-900 group-hover:text-blue-700',
    labelColor: 'text-gray-600 group-hover:text-blue-600',
    badgeClass: 'bg-gradient-to-r from-blue-100 to-blue-200 text-blue-800 border-l-2 border-blue-500',
    progressColor: 'bg-blue-500',
    progress: 50
  },
  */
  {
    id: 'Created',
    icon: FileText,
    count: shipments.value.filter(s => s.status === 'Created').length,
    label: 'Created',
    iconBg: 'bg-gradient-to-br from-gray-50 to-gray-100',
    iconColor: 'text-gray-600',
    textColor: 'text-gray-900 group-hover:text-gray-700',
    labelColor: 'text-gray-600 group-hover:text-gray-600',
    badgeClass: 'bg-gradient-to-r from-gray-100 to-gray-200 text-gray-800 border-l-2 border-gray-500',
    progressColor: 'bg-gray-500',
    progress: 25
  },
  {
    id: 'Delayed',
    icon: AlertTriangle,
    count: shipments.value.filter(s => s.status === 'Delayed').length,
    label: 'Delayed',
    iconBg: 'bg-gradient-to-br from-red-50 to-red-100',
    iconColor: 'text-red-600',
    textColor: 'text-gray-900 group-hover:text-red-700',
    labelColor: 'text-gray-600 group-hover:text-red-600',
    badgeClass: 'bg-gradient-to-r from-red-100 to-red-200 text-red-800 border-l-2 border-red-500',
    progressColor: 'bg-red-500',
    progress: 60
  }
])

// Status options for filter dropdown
// Note: Aligned with database CHECK constraint: shipment_status IN ('Created', 'In Transit', 'Delivered', 'Delayed')
// Updated 2024: Removed 'picked-up' status as it's not supported in database schema
// 'Picked Up' status commented out below - can be restored when schema supports it after Kafka integration
const statusOptions = computed(() => [
  { value: 'In Transit', label: 'In Transit', icon: Truck, color: 'text-teal-600' },
  { value: 'Delivered', label: 'Delivered', icon: CheckCircle, color: 'text-green-600' },
  // { value: 'Picked Up', label: 'Picked Up', icon: Package, color: 'text-blue-600' }, // Not in DB schema
  { value: 'Created', label: 'Created', icon: FileText, color: 'text-gray-600' },
  { value: 'Delayed', label: 'Delayed', icon: AlertTriangle, color: 'text-red-600' }
])

// Sort options
const sortOptions = computed(() => [
  { value: 'created-desc', label: 'Created Date (Newest)', icon: Calendar },
  { value: 'created-asc', label: 'Created Date (Oldest)', icon: Calendar },
  { value: 'delivery-asc', label: 'Delivery Date (Soonest)', icon: Clock },
  { value: 'delivery-desc', label: 'Delivery Date (Latest)', icon: Clock },
  { value: 'value-desc', label: 'Cargo Value (Highest)', icon: ArrowUpDown },
  { value: 'value-asc', label: 'Cargo Value (Lowest)', icon: ArrowUpDown }
])

// Search suggestions
const searchSuggestions = computed(() => {
  if (!searchQuery.value || searchQuery.value.length < 2) return []
  
  const suggestions = new Set()
  shipments.value.forEach(shipment => {
    if (shipment.id.toLowerCase().includes(searchQuery.value.toLowerCase())) {
      suggestions.add(shipment.id)
    }
    if (shipment.route.origin.toLowerCase().includes(searchQuery.value.toLowerCase())) {
      suggestions.add(shipment.route.origin)
    }
    if (shipment.route.destination.toLowerCase().includes(searchQuery.value.toLowerCase())) {
      suggestions.add(shipment.route.destination)
    }
    if (shipment.status.toLowerCase().includes(searchQuery.value.toLowerCase())) {
      suggestions.add(getStatusLabel(shipment.status))
    }
  })
  return Array.from(suggestions).slice(0, 5)
})

// Metrics calculations
const metrics = computed(() => {
  const total = shipments.value.length
  const delayed = shipments.value.filter(s => s.status === 'delayed').length
  const delivered = shipments.value.filter(s => s.status === 'delivered').length
  const onTime = total - delayed
  
  return {
    totalActive: total,
    delayed,
    onTimePercentage: total > 0 ? Math.round((onTime / total) * 100) : 0
  }
})

const onTimeCount = computed(() => shipments.value.length - metrics.value.delayed)
const delayedPercentage = computed(() => {
  const total = shipments.value.length
  return total > 0 ? Math.round((metrics.value.delayed / total) * 100) : 0
})

// Filtered shipments based on search and filters
const filteredShipments = computed(() => {
  let filtered = shipments.value

  // Apply search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(shipment =>
      shipment.id.toLowerCase().includes(query) ||
      shipment.route.origin.toLowerCase().includes(query) ||
      shipment.route.destination.toLowerCase().includes(query) ||
      shipment.status.toLowerCase().includes(query)
    )
  }

  // Apply status filters (multi-select)
  if (activeFilters.value.length > 0) {
    filtered = filtered.filter(shipment => activeFilters.value.includes(shipment.status))
  }

  // Apply single filter from status card click
  if (activeFilter.value) {
    filtered = filtered.filter(shipment => shipment.status === activeFilter.value)
  }

  // Apply sorting
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'created-desc':
        return new Date(b.created) - new Date(a.created)
      case 'created-asc':
        return new Date(a.created) - new Date(b.created)
      case 'delivery-asc':
        return new Date(a.estimatedDelivery) - new Date(b.estimatedDelivery)
      case 'delivery-desc':
        return new Date(b.estimatedDelivery) - new Date(a.estimatedDelivery)
      case 'value-desc':
        return b.cargoValue - a.cargoValue
      case 'value-asc':
        return a.cargoValue - b.cargoValue
      default:
        return 0
    }
  })

  return filtered
})

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('en-IN').format(amount)
}

const formatDate = (date) => {
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  }).format(new Date(date))
}

const isOverdue = (date) => {
  return new Date() > new Date(date)
}

const getStatusIcon = (status) => {
  const icons = {
    'in-transit': Truck,
    'delivered': CheckCircle,
    'picked-up': Package,
    'created': FileText,
    'delayed': AlertTriangle
  }
  return icons[status] || FileText
}

const getStatusLabel = (status) => {
  const labels = {
    'in-transit': 'In Transit',
    'delivered': 'Delivered',
    'picked-up': 'Picked Up',
    'created': 'Created',
    'delayed': 'Delayed'
  }
  return labels[status] || 'Unknown'
}

const getStatusBadgeClass = (status) => {
  // Updated to match database schema status values (proper case)
  // Added mapping for backward compatibility with any legacy lowercase values
  const classes = {
    'In Transit': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-teal-100 to-teal-200 text-teal-800 border-l-2 border-teal-500',
    'Delivered': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-green-100 to-green-200 text-green-800 border-l-2 border-green-500',
    // 'Picked Up': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-blue-100 to-blue-200 text-blue-800 border-l-2 border-blue-500', // Not in DB schema
    'Created': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-gray-100 to-gray-200 text-gray-800 border-l-2 border-gray-500',
    'Delayed': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-red-100 to-red-200 text-red-800 border-l-2 border-red-500 animate-pulse',
    // Legacy mappings for backward compatibility (remove after full migration)
    'in-transit': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-teal-100 to-teal-200 text-teal-800 border-l-2 border-teal-500',
    'delivered': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-green-100 to-green-200 text-green-800 border-l-2 border-green-500',
    'created': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-gray-100 to-gray-200 text-gray-800 border-l-2 border-gray-500',
    'delayed': 'inline-flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold uppercase bg-gradient-to-r from-red-100 to-red-200 text-red-800 border-l-2 border-red-500 animate-pulse'
  }
  return classes[status] || classes['Created']
}

// ============================================================================
// EVENT HANDLERS
// ============================================================================

const filterByStatus = (status) => {
  activeFilter.value = activeFilter.value === status ? '' : status
  // Clear multi-select filters when using card filter
  if (activeFilter.value) {
    activeFilters.value = []
  }
}

const toggleFilterMenu = () => {
  showFilterDropdown.value = !showFilterDropdown.value
  showSortDropdown.value = false
}

const toggleSortMenu = () => {
  showSortDropdown.value = !showSortDropdown.value
  showFilterDropdown.value = false
}

const clearFilters = () => {
  activeFilters.value = []
}

const applyFilters = () => {
  showFilterDropdown.value = false
  // Clear single filter when using multi-select
  if (activeFilters.value.length > 0) {
    activeFilter.value = ''
  }
}

const setSortOption = (option) => {
  sortBy.value = option
  showSortDropdown.value = false
}

const applySuggestion = (suggestion) => {
  searchQuery.value = suggestion
  showSearchSuggestions.value = false
}

const hideSearchSuggestions = () => {
  setTimeout(() => {
    showSearchSuggestions.value = false
  }, 200)
}

const getTimelineEvents = (shipment) => {
  if (!shipment) return []
  
  const events = [
    {
      id: 1,
      title: 'Shipment Created',
      description: 'Shipment has been registered in the system',
      time: formatDate(shipment.created),
      completed: true,
      isLast: false
    },
    {
      id: 2,
      title: 'Picked Up',
      description: 'Cargo has been collected from origin',
      time: 'Processing...',
      completed: ['picked-up', 'in-transit', 'delivered'].includes(shipment.status),
      isLast: false
    },
    {
      id: 3,
      title: 'In Transit',
      description: 'Shipment is on its way to destination',
      time: 'Processing...',
      completed: ['in-transit', 'delivered'].includes(shipment.status),
      isLast: false
    },
    {
      id: 4,
      title: 'Delivered',
      description: 'Shipment has been delivered successfully',
      time: shipment.status === 'delivered' ? formatDate(shipment.estimatedDelivery) : 'Pending...',
      completed: shipment.status === 'delivered',
      isLast: true
    }
  ]
  
  if (shipment.status === 'delayed') {
    events.splice(2, 0, {
      id: 5,
      title: 'Delayed',
      description: 'Shipment is experiencing delays',
      time: 'Current status',
      completed: true,
      isLast: false
    })
    events[events.length - 1].isLast = true
  }
  
  return events
}

const closeShipmentModal = () => {
  showShipmentModal.value = false
  selectedShipment.value = null
}

const createNewShipment = () => {
  openNewShipmentModal()
}

const openNewShipmentModal = () => {
  isEditMode.value = false
  shipmentForm.value = {
    id: `SH${String(shipments.value.length + 1).padStart(3, '0')}`,
    route: '',
    status: 'created',
    cargoValue: 0,
    cargoWeight: 0,
    estimatedDelivery: '',
    notes: ''
  }
  showNewShipmentModal.value = true
}

const closeNewShipmentModal = () => {
  showNewShipmentModal.value = false
  isEditMode.value = false
}

const saveShipment = async () => {
  if (!shipmentForm.value.route || !shipmentForm.value.status || 
      !shipmentForm.value.cargoValue || !shipmentForm.value.cargoWeight || 
      !shipmentForm.value.estimatedDelivery) {
    return
  }

  const shipmentData = {
    route: shipmentForm.value.route,
    status: shipmentForm.value.status,
    cargoValue: shipmentForm.value.cargoValue,
    cargoWeight: shipmentForm.value.cargoWeight,
    cargoItems: Math.floor(shipmentForm.value.cargoWeight / 10), // Mock calculation
    estimatedDelivery: new Date(shipmentForm.value.estimatedDelivery),
    notes: shipmentForm.value.notes
  }

  if (isEditMode.value) {
    await updateShipment(shipmentForm.value.id, shipmentData)
  } else {
    await createShipment(shipmentData)
  }
}

const editShipment = (shipment) => {
  isEditMode.value = true
  shipmentForm.value = {
    id: shipment.id,
    route: shipment.route,
    status: shipment.status,
    cargoValue: shipment.cargoValue,
    cargoWeight: shipment.cargoWeight,
    estimatedDelivery: shipment.estimatedDelivery instanceof Date 
      ? shipment.estimatedDelivery.toISOString().split('T')[0]
      : shipment.estimatedDelivery,
    notes: shipment.notes || ''
  }
  showNewShipmentModal.value = true
}

const promptDeleteShipment = (shipment) => {
  shipmentToDelete.value = shipment
  showDeleteConfirm.value = true
}

const handleConfirmDelete = async () => {
  if (shipmentToDelete.value) {
    try {
      await deleteShipment(shipmentToDelete.value.id)
      console.log(`Successfully deleted shipment: ${shipmentToDelete.value.id}`)
    } catch (error) {
      console.error('Error deleting shipment:', error)
    }
  }
  cancelDelete()
}

const cancelDelete = () => {
  showDeleteConfirm.value = false
  shipmentToDelete.value = null
}

const exportShipments = () => {
  console.log('Export shipments')
}

const viewShipmentDetails = (shipment) => {
  selectedShipment.value = shipment
  showShipmentModal.value = true
}

const viewShipment = (shipment) => {
  selectedShipment.value = shipment
  showShipmentModal.value = true
}

// ============================================================================
// API FUNCTIONS
// ============================================================================

const loadShipments = async () => {
  isLoading.value = true
  error.value = null
  
  try {
    console.log('Loading shipments from API...')
    const data = await shipmentApi.getAll()
    console.log('Received shipment data:', data)
    
    // Transform backend data to frontend format
    shipments.value = (data || []).map(shipment => ({
      id: shipment.shipmentCode || `SH${String(shipment.shipmentId).padStart(3, '0')}`,
      route: {
        origin: shipment.origin,
        destination: shipment.destination
      },
      status: shipment.status?.toLowerCase() || 'pending',
      cargoValue: 0, // Will need to calculate from related cargo
      cargoItems: 1, // Default value
      estimatedDelivery: shipment.estimatedDelivery || new Date().toISOString(),
      created: shipment.createdAt || new Date().toISOString(), // Match template expectation
      shipmentDate: shipment.createdAt || new Date().toISOString(),
      vendor: shipment.assignedVendor,
      assignedRoute: shipment.assignedRoute,
      // Keep original backend data for API operations
      _original: shipment
    }))
    console.log('Transformed shipments:', shipments.value)
  } catch (err) {
    error.value = 'Failed to load shipments. Please try again.'
    console.error('Error loading shipments:', err)
    shipments.value = []
  } finally {
    isLoading.value = false
  }
}

const createShipment = async (shipmentData) => {
  isFormLoading.value = true
  error.value = null

  try {
    const newShipment = await shipmentApi.create(shipmentData)
    shipments.value.unshift(newShipment)
    closeNewShipmentModal()
  } catch (err) {
    error.value = 'Failed to create shipment. Please try again.'
    console.error('Error creating shipment:', err)
  } finally {
    isFormLoading.value = false
  }
}

const updateShipment = async (id, shipmentData) => {
  isFormLoading.value = true
  error.value = null

  try {
    const updatedShipment = await shipmentApi.update(id, shipmentData)
    const index = shipments.value.findIndex(s => s.id === id)
    if (index !== -1) {
      shipments.value[index] = updatedShipment
    }
    closeNewShipmentModal()
  } catch (err) {
    error.value = 'Failed to update shipment. Please try again.'
    console.error('Error updating shipment:', err)
  } finally {
    isFormLoading.value = false
  }
}

const deleteShipment = async (id) => {
  try {
    await shipmentApi.delete(id)
    shipments.value = shipments.value.filter(s => s.id !== id)
  } catch (err) {
    error.value = 'Failed to delete shipment. Please try again.'
    console.error('Error deleting shipment:', err)
  }
}

// ============================================================================
// COMPONENT FUNCTIONS
// ============================================================================

const trackShipment = (shipment) => {
  console.log('Track shipment:', shipment.id)
  // Simulate real-time tracking
  setTimeout(() => {
    console.log('Tracking data loaded for:', shipment.id)
  }, 1500)
}

// Click outside handler
const handleClickOutside = (event) => {
  if (!event.target.closest('.relative')) {
    showFilterDropdown.value = false
    showSortDropdown.value = false
    showSearchSuggestions.value = false
  }
}

// Initialize component
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  loadShipments()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* Custom scrollbar for table */
.overflow-x-auto::-webkit-scrollbar {
  height: 8px;
}

.overflow-x-auto::-webkit-scrollbar-track {
  background: #f3f4f6;
  border-radius: 8px;
}

.overflow-x-auto::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 8px;
}

.overflow-x-auto::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Smooth transitions for all interactive elements */
* {
  transition-property: color, background-color, border-color, transform, box-shadow, opacity;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

/* Enhanced hover effects for table rows */
.group:hover {
  transform: translateX(2px);
}

/* Font family consistency */
.font-mono {
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', 'Courier New', monospace;
}

/* Status card animations */
.status-card {
  animation: fadeInUp 0.6s ease-out;
}

.status-card:nth-child(1) { animation-delay: 0.1s; }
.status-card:nth-child(2) { animation-delay: 0.2s; }
.status-card:nth-child(3) { animation-delay: 0.3s; }
.status-card:nth-child(4) { animation-delay: 0.4s; }
.status-card:nth-child(5) { animation-delay: 0.5s; }

/* Table row animations */
.shipment-row {
  animation: slideInLeft 0.4s ease-out;
}

.shipment-row:nth-child(1) { animation-delay: 0.05s; }
.shipment-row:nth-child(2) { animation-delay: 0.1s; }
.shipment-row:nth-child(3) { animation-delay: 0.15s; }
.shipment-row:nth-child(4) { animation-delay: 0.2s; }
.shipment-row:nth-child(5) { animation-delay: 0.25s; }

/* Dropdown animations */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}

/* Modal animations */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

/* Custom animations */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* Delayed status badge pulse */
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* Button hover effects */
button:hover {
  transform: translateY(-1px);
}

button:active {
  transform: translateY(0);
}

/* Card hover effects */
.status-card:hover {
  animation-play-state: paused;
}

/* Progress bar animation */
.progress-bar {
  transition: width 1s ease-out;
}

/* Search suggestions animation */
.suggestions-enter-active,
.suggestions-leave-active {
  transition: all 0.2s ease-out;
}

.suggestions-enter-from,
.suggestions-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
</style>