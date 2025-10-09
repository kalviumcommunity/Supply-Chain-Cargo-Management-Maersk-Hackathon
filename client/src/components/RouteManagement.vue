<template>
  <div class="route-management-page bg-[#F5F5F7] min-h-screen p-6">
    <!-- Page Header -->
    <header class="page-header flex justify-between items-center mb-6">
      <div class="header-left flex items-center gap-4">
        <div class="page-icon w-7 h-7 text-blue-500">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polygon points="3 11 22 2 13 21 11 13 3 11"></polygon>
          </svg>
        </div>
        <div class="header-text">
          <h1 class="page-title text-4xl font-bold text-[#0F172A] tracking-tight">Route Management</h1>
          <p class="page-subtitle text-base text-[#64748B] mt-1">Manage shipping routes and optimize delivery paths.</p>
        </div>
      </div>
      <button 
        class="btn-create-route flex items-center gap-3 px-7 py-3.5 bg-gradient-to-r from-blue-500 to-blue-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl hover:-translate-y-0.5 transition-all duration-200"
        @click="openCreateRouteModal"
      >
        <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        Create Route
      </button>
    </header>

    <!-- Error State -->
    <div v-if="error" class="bg-red-50 border-l-4 border-red-500 p-4 rounded-xl mb-6">
      <div class="flex items-center gap-3">
        <svg class="w-5 h-5 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="15" y1="9" x2="9" y2="15"></line>
          <line x1="9" y1="9" x2="15" y2="15"></line>
        </svg>
        <span class="text-red-700 font-medium">{{ error }}</span>
        <button 
          @click="loadRoutes" 
          class="ml-auto px-3 py-1 bg-red-100 text-red-700 rounded-md hover:bg-red-200 transition-colors text-sm font-medium"
        >
          Retry
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="flex items-center justify-center py-20">
      <div class="flex flex-col items-center gap-4">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        <p class="text-gray-600 font-medium">Loading routes...</p>
      </div>
    </div>

    <!-- Main Content -->
    <div v-else>

    <!-- Network Health Banner -->
    <div class="network-banner bg-gradient-to-r from-blue-50 to-cyan-50 border-l-4 border-blue-500 p-4 rounded-xl mb-6">
      <div class="flex items-center gap-4 text-sm font-medium text-slate-700">
        <span>Network Coverage: <strong class="text-slate-900">{{ formatNumber(metrics.totalDistance) }} km</strong> across {{ metrics.activeRoutes }} active corridors</span>
        <span class="text-slate-400">•</span>
        <span>Average On-Time Rate: <strong class="text-slate-900">{{ metrics.onTimeRate }}%</strong></span>
        <span class="text-slate-400">•</span>
        <span>Optimization Opportunities: <strong class="text-slate-900">{{ optimizableRoutes }}</strong></span>
      </div>
    </div>

    <!-- Metrics Grid -->
    <div class="metrics-grid grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
      <div
        v-for="(metric, index) in metricsCards"
        :key="metric.id"
        class="metric-card bg-white p-7 rounded-2xl shadow-sm hover:shadow-lg hover:-translate-y-2 hover:scale-[1.02] transition-all duration-300 cursor-pointer group border border-transparent hover:border-opacity-20"
        :class="metric.hoverBorder"
        :style="{ animationDelay: `${index * 80}ms` }"
        @click="handleMetricClick(metric)"
      >
        <div class="flex items-start justify-between relative">
          <div class="metric-content flex-1">
            <!-- Icon with enhanced styling -->
            <div 
              class="metric-icon w-14 h-14 rounded-full flex items-center justify-center mb-6 transition-all duration-300 group-hover:scale-110"
              :class="metric.iconBg"
            >
              <div :class="['w-7 h-7 transition-colors duration-300', metric.iconColor]">
                <!-- Network Icon -->
                <svg v-if="metric.iconName === 'Network'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="16" y="16" width="6" height="6" rx="1"></rect>
                  <rect x="2" y="16" width="6" height="6" rx="1"></rect>
                  <rect x="9" y="2" width="6" height="6" rx="1"></rect>
                  <path d="m5 16 3-8 3 8"></path>
                  <path d="m19 16-3-8-3 8"></path>
                </svg>
                <!-- Activity Icon -->
                <svg v-else-if="metric.iconName === 'Activity'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="m22 12-4-4-6 6-4-4-6 6"></path>
                  <path d="m2 16 4-4 6 6 4-4 6 6"></path>
                </svg>
                <!-- Compass Icon -->
                <svg v-else-if="metric.iconName === 'Compass'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="10"></circle>
                  <polygon points="16.24,7.76 14.12,14.12 7.76,16.24 9.88,9.88"></polygon>
                </svg>
                <!-- Clock Icon -->
                <svg v-else-if="metric.iconName === 'Clock'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12,6 12,12 16,14"></polyline>
                </svg>
              </div>
            </div>
            
            <!-- Animated number with count-up effect -->
            <div class="metric-number text-5xl font-bold text-[#0F172A] tracking-tight mb-2 font-mono">
              <span ref="numberElement" :data-target="metric.rawValue" :data-suffix="metric.suffix || ''">
                {{ metric.displayValue }}
              </span>
            </div>
            
            <div class="metric-label text-sm font-semibold text-[#374151] mb-1">
              {{ metric.label }}
            </div>
            
            <div class="flex items-center gap-2">
              <div class="metric-subtitle text-xs font-medium text-[#94A3B8]">
                {{ metric.subtitle }}
              </div>
              <!-- Live indicator for active routes -->
              <div v-if="metric.id === 'active-routes'" class="flex items-center gap-1">
                <div class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                <span class="text-xs text-green-600 font-medium">Live</span>
              </div>
            </div>
            
            <!-- Progress ring for active routes -->
            <div v-if="metric.id === 'active-routes'" class="mt-3">
              <div class="flex items-center justify-between text-xs text-gray-500 mb-1">
                <span>Active</span>
                <span>{{ Math.round((metrics.activeRoutes / metrics.totalRoutes) * 100) }}%</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-1.5">
                <div 
                  class="bg-green-500 h-1.5 rounded-full transition-all duration-1000 ease-out"
                  :style="{ width: `${(metrics.activeRoutes / metrics.totalRoutes) * 100}%` }"
                ></div>
              </div>
            </div>
            
            <!-- Mini bar chart for distance -->
            <div v-if="metric.id === 'total-distance'" class="mt-3">
              <div class="flex items-end gap-1 h-8">
                <div 
                  v-for="(route, i) in routes.slice(0, 4)" 
                  :key="i"
                  class="bg-yellow-300 rounded-sm flex-1 transition-all duration-500 hover:bg-yellow-400"
                  :style="{ 
                    height: `${(route.distance / Math.max(...routes.map(r => r.distance))) * 100}%`,
                    animationDelay: `${i * 100}ms` 
                  }"
                ></div>
              </div>
            </div>
            
            <!-- Efficiency badge for duration -->
            <div v-if="metric.id === 'avg-duration'" class="mt-3">
              <span 
                class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-md"
                :class="getEfficiencyBadge()"
              >
                {{ getEfficiencyText() }}
              </span>
            </div>
          </div>
          
          <!-- Background pattern -->
          <div class="absolute top-0 right-0 opacity-5 pointer-events-none">
            <div class="w-20 h-20 text-current">
              <!-- Background Icon -->
              <svg v-if="metric.iconName === 'Network'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                <rect x="16" y="16" width="6" height="6" rx="1"></rect>
                <rect x="2" y="16" width="6" height="6" rx="1"></rect>
                <rect x="9" y="2" width="6" height="6" rx="1"></rect>
                <path d="m5 16 3-8 3 8"></path>
                <path d="m19 16-3-8-3 8"></path>
              </svg>
              <svg v-else-if="metric.iconName === 'Activity'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                <path d="m22 12-4-4-6 6-4-4-6 6"></path>
                <path d="m2 16 4-4 6 6 4-4 6 6"></path>
              </svg>
              <svg v-else-if="metric.iconName === 'Compass'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <polygon points="16.24,7.76 14.12,14.12 7.76,16.24 9.88,9.88"></polygon>
              </svg>
              <svg v-else-if="metric.iconName === 'Clock'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12,6 12,12 16,14"></polyline>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Search & Filter Toolbar -->
    <div class="toolbar-card bg-white p-5 rounded-2xl shadow-sm mb-6">
      <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
        <div class="search-wrapper relative flex-1 lg:flex-none">
          <svg class="search-icon absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <path d="m21 21-4.35-4.35"></path>
          </svg>
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Search routes by ID, name, ports, or status..."
            class="search-input w-full lg:w-[540px] h-12 pl-12 pr-4 bg-[#F9FAFB] border border-[#E5E7EB] rounded-xl text-sm text-[#374151] focus:border-blue-500 focus:ring-3 focus:ring-blue-100 focus:outline-none transition-all"
          />
        </div>
        <div class="toolbar-actions flex items-center gap-3 flex-wrap">
          <!-- Filter Dropdown -->
          <div class="relative">
            <button 
              class="btn-filter flex items-center gap-2 h-12 px-5 border-1.5 border-[#E5E7EB] rounded-xl bg-white font-medium text-sm text-[#374151] hover:border-blue-500 hover:bg-blue-50 hover:text-blue-600 transition-all duration-200"
              @click="toggleFilterMenu"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"></polygon>
              </svg>
              Filter by Status
              <span v-if="activeFilters.length" class="filter-badge bg-blue-500 text-white text-xs px-2 py-0.5 rounded-full ml-1">{{ activeFilters.length }}</span>
            </button>
            <!-- Filter Dropdown Menu -->
            <transition name="dropdown">
              <div v-if="showFilterMenu" class="absolute top-full left-0 mt-2 w-56 bg-white border border-gray-200 rounded-xl shadow-lg z-20">
                <div class="p-3">
                  <div class="flex items-center justify-between mb-3">
                    <span class="text-sm font-semibold text-gray-700">Filter by Status</span>
                    <button @click="clearFilters" class="text-xs text-blue-600 hover:text-blue-800">Clear All</button>
                  </div>
                  <div class="space-y-2">
                    <label v-for="status in ['Active', 'Delayed', 'Closed']" :key="status" class="flex items-center gap-2 cursor-pointer">
                      <input 
                        type="checkbox" 
                        :value="status" 
                        @change="toggleFilter(status)"
                        :checked="activeFilters.includes(status)"
                        class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                      />
                      <span class="text-sm text-gray-700 capitalize">{{ status }}</span>
                      <span class="text-xs text-gray-500 ml-auto">
                        {{ routes.filter(r => r.status === status).length }}
                      </span>
                    </label>
                  </div>
                </div>
              </div>
            </transition>
          </div>

          <!-- Sort Dropdown -->
          <div class="relative">
            <button 
              class="btn-sort flex items-center gap-2 h-12 px-5 border-1.5 border-[#E5E7EB] rounded-xl bg-white font-medium text-sm text-[#374151] hover:border-blue-500 hover:bg-blue-50 hover:text-blue-600 transition-all duration-200"
              @click="toggleSortMenu"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="m3 16 4 4 4-4"></path>
                <path d="M7 20V4"></path>
                <path d="m21 8-4-4-4 4"></path>
                <path d="M17 4v16"></path>
              </svg>
              Sort by {{ sortBy.charAt(0).toUpperCase() + sortBy.slice(1) }}
              <svg class="w-3 h-3 ml-1 transition-transform" :class="{ 'rotate-180': sortOrder === 'desc' }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="6,9 12,15 18,9"></polyline>
              </svg>
            </button>
            <!-- Sort Dropdown Menu -->
            <transition name="dropdown">
              <div v-if="showSortMenu" class="absolute top-full left-0 mt-2 w-48 bg-white border border-gray-200 rounded-xl shadow-lg z-20">
                <div class="p-2">
                  <button 
                    v-for="field in [
                      { key: 'name', label: 'Route Name' },
                      { key: 'duration', label: 'Duration' },
                      { key: 'distance', label: 'Distance' }
                    ]" 
                    :key="field.key"
                    @click="setSorting(field.key as any)"
                    class="w-full flex items-center justify-between px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg transition-colors"
                    :class="{ 'bg-blue-50 text-blue-600': sortBy === field.key }"
                  >
                    <span>{{ field.label }}</span>
                    <div v-if="sortBy === field.key" class="flex items-center gap-1">
                      <svg class="w-3 h-3 transition-transform" :class="{ 'rotate-180': sortOrder === 'desc' }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="6,9 12,15 18,9"></polyline>
                      </svg>
                    </div>
                  </button>
                </div>
              </div>
            </transition>
          </div>

          <button 
            class="btn-view-toggle flex items-center gap-2 h-12 px-6 border-2 border-[#E5E7EB] rounded-xl bg-white font-medium text-sm text-[#374151] hover:border-blue-500 hover:bg-blue-50 hover:text-blue-600 transition-all duration-200 shadow-sm hover:shadow-md"
            @click="toggleMapView"
            :title="`Switch to ${view === 'table' ? 'Map' : 'Table'} View`"
          >
            <svg v-if="view === 'table'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polygon points="3 6 9 1 15 6 21 1 21 18 15 23 9 18 3 23"></polygon>
              <line x1="9" y1="1" x2="9" y2="18"></line>
              <line x1="15" y1="6" x2="15" y2="23"></line>
            </svg>
            <svg v-else class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 3h7v9H3z"></path>
              <path d="M14 3h7v5h-7z"></path>
              <path d="M14 12h7v9h-7z"></path>
              <path d="M3 16h7v5H3z"></path>
            </svg>
            <span class="font-semibold">{{ view === 'table' ? 'Map View' : 'Table View' }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Route Network Table / Map Container -->
    <div class="routes-table-container bg-white rounded-2xl shadow-sm overflow-hidden">
      <div class="table-header flex justify-between items-center p-7 border-b-2 border-gray-100">
        <div class="header-left">
          <h2 class="table-title text-xl font-semibold text-[#1E293B] mb-1">Route Network</h2>
          <p class="table-subtitle text-sm text-[#64748B]">Overview of all shipping corridors</p>
        </div>
        <div class="header-actions flex items-center gap-4">
          <!-- Current View Indicator -->
          <div class="view-indicator flex items-center gap-2 px-3 py-2 bg-gray-50 rounded-lg">
            <svg v-if="view === 'table'" class="w-4 h-4 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 3h7v9H3z"></path>
              <path d="M14 3h7v5h-7z"></path>
              <path d="M14 12h7v9h-7z"></path>
              <path d="M3 16h7v5H3z"></path>
            </svg>
            <svg v-else class="w-4 h-4 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polygon points="3 6 9 1 15 6 21 1 21 18 15 23 9 18 3 23"></polygon>
              <line x1="9" y1="1" x2="9" y2="18"></line>
              <line x1="15" y1="6" x2="15" y2="23"></line>
            </svg>
            <span class="text-sm font-medium text-gray-700">{{ view === 'table' ? 'Table View' : 'Map View' }}</span>
          </div>
          
          <button 
            class="btn-export flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-all"
            @click="exportRoutes"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="7,10 12,15 17,10"></polyline>
              <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
            Export
          </button>
        </div>
      </div>

      <!-- Map View -->
      <div v-if="view === 'map'" class="map-view-container p-6" key="map-view">
        <div class="map-header mb-4 flex items-center justify-between">
          <div>
            <h3 class="text-lg font-semibold text-gray-900">Route Network Map</h3>
            <p class="text-sm text-gray-600">{{ filteredRoutes.length }} route{{ filteredRoutes.length !== 1 ? 's' : '' }} displayed</p>
          </div>
          <div class="map-controls flex items-center gap-2">
            <div class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded">
              Click routes for details
            </div>
          </div>
        </div>
        <RouteMap 
          :routes="routes" 
          :filtered-routes="filteredRoutes"
          @route-clicked="viewRouteDetails"
          @marker-clicked="handleMarkerClick"
        />
      </div>

      <!-- Table Content -->
      <div v-if="view === 'table'" class="table-wrapper" key="table-view">
        <!-- Desktop Table View -->
        <div class="hidden lg:block overflow-x-auto">
          <div class="max-h-[calc(100vh-520px)] overflow-y-auto">
            <table class="routes-table w-full min-w-[1200px]">
              <thead class="table-header bg-gray-50 border-b border-gray-200 sticky top-0 z-10">
                <tr>
                  <th class="text-left p-3 w-12">
                    <input 
                      type="checkbox" 
                      v-model="selectAll" 
                      @change="toggleSelectAll"
                      class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                    />
                  </th>
                  <th class="text-left p-3 text-xs font-semibold text-gray-600 uppercase tracking-wider min-w-[100px]">Route ID</th>
                  <th class="text-left p-3 text-xs font-semibold text-gray-600 uppercase tracking-wider min-w-[200px]">Route Details</th>
                  <th class="text-left p-3 text-xs font-semibold text-gray-600 uppercase tracking-wider min-w-[250px]">Path</th>
                  <th class="text-left p-3 text-xs font-semibold text-gray-600 uppercase tracking-wider min-w-[120px]">Duration</th>
                  <th class="text-left p-3 text-xs font-semibold text-gray-600 uppercase tracking-wider min-w-[120px]">Distance</th>
                  <th class="text-left p-3 text-xs font-semibold text-gray-600 uppercase tracking-wider min-w-[120px]">Status</th>
                  <th class="text-left p-3 text-xs font-semibold text-gray-600 uppercase tracking-wider min-w-[140px]">Efficiency</th>
                  <th class="text-left p-3 text-xs font-semibold text-gray-600 uppercase tracking-wider min-w-[100px]">Shipments</th>
                  <th class="text-center p-3 text-xs font-semibold text-gray-600 uppercase tracking-wider min-w-[140px]">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200 bg-white">
                <tr
                  v-for="route in filteredRoutes"
                  :key="route.id"
                  class="route-row hover:bg-gray-50 transition-colors duration-150 group"
                  :class="{ 'bg-blue-50': isSelected(route.id) }"
                >
                  <!-- Checkbox -->
                  <td class="p-3">
                    <input 
                      type="checkbox" 
                      :value="route.id" 
                      v-model="selectedRoutes"
                      class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                    />
                  </td>

                  <!-- Route ID -->
                  <td class="p-3">
                    <div class="flex items-center gap-2">
                      <span class="font-mono text-sm font-medium text-blue-600">{{ route.id }}</span>
                      <button 
                        @click="copyToClipboard(route.id)"
                        class="opacity-0 group-hover:opacity-100 transition-opacity p-1 hover:bg-gray-200 rounded"
                        title="Copy ID"
                      >
                        <svg class="w-3 h-3 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                          <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                        </svg>
                      </button>
                    </div>
                  </td>

                  <!-- Route Details (Name + Type) -->
                  <td class="p-3">
                    <div class="space-y-1">
                      <div class="font-semibold text-gray-900">{{ route.name }}</div>
                      <div 
                        :class="getRouteTypeBadgeClass(route.routeType)"
                        class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                      >
                        {{ route.routeType.charAt(0).toUpperCase() + route.routeType.slice(1) }}
                      </div>
                    </div>
                  </td>

                  <!-- Origin → Destination -->
                  <td class="p-3">
                    <div class="space-y-2">
                      <div class="flex items-center text-sm">
                        <div class="flex items-center gap-2">
                          <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
                          <span class="font-medium text-gray-900">{{ route.origin.location }}</span>
                        </div>
                        <svg class="w-4 h-4 text-gray-400 mx-3 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <path d="m9 18 6-6-6-6"/>
                        </svg>
                        <div class="flex items-center gap-2">
                          <div class="w-2 h-2 bg-green-500 rounded-full"></div>
                          <span class="font-medium text-gray-900">{{ route.destination.location }}</span>
                        </div>
                      </div>
                      <div class="text-xs text-gray-500">
                        {{ route.origin.port }} → {{ route.destination.port }}
                      </div>
                    </div>
                      </td>

                  <!-- Duration -->
                  <td class="p-3">
                    <div class="space-y-1">
                      <div class="flex items-center gap-2">
                        <svg class="w-4 h-4 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <circle cx="12" cy="12" r="10"></circle>
                          <polyline points="12,6 12,12 16,14"></polyline>
                        </svg>
                        <span class="text-sm font-semibold text-gray-900">{{ route.duration }}h</span>
                      </div>
                      <div 
                        v-if="getDurationComparison(route.duration)"
                        :class="getDurationBadgeClass(route.duration)"
                        class="text-xs px-2 py-1 rounded-full font-medium inline-block"
                      >
                        {{ getDurationComparison(route.duration) }}
                      </div>
                    </div>
                  </td>

                  <!-- Distance -->
                  <td class="p-3">
                    <div class="space-y-2">
                      <div class="text-sm font-semibold text-gray-900">{{ formatNumber(route.distance) }} km</div>
                      <div class="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
                        <div 
                          class="h-2 rounded-full distance-fill transition-all duration-700"
                          :style="{ 
                            width: `${getDistancePercentage(route.distance)}%`,
                            background: getDistanceColor(route.distance)
                          }"
                        ></div>
                      </div>
                    </div>
                  </td>

                  <!-- Status -->
                  <td class="p-3">
                    <div class="space-y-1">
                      <div :class="getStatusBadgeClass(route.status)" class="status-badge px-3 py-2 rounded-lg text-xs font-semibold inline-flex items-center gap-2">
                        <div 
                          v-if="route.status === 'Active'" 
                          class="w-2 h-2 bg-green-500 rounded-full animate-pulse"
                        ></div>
                        {{ route.status }}
                      </div>
                      <div class="text-xs text-gray-500">
                        {{ getStatusTimestamp(route.status) }}
                      </div>
                    </div>
                  </td>

                  <!-- Efficiency -->
                  <td class="p-3">
                    <div class="flex items-center gap-3">
                      <div class="relative w-10 h-10 flex-shrink-0">
                        <svg class="w-10 h-10 transform -rotate-90" viewBox="0 0 36 36">
                          <path 
                            d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                            fill="none" 
                            stroke="#E5E7EB" 
                            stroke-width="2"
                          />
                          <path 
                            d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                            fill="none" 
                            :stroke="getEfficiencyColor(route.efficiencyScore)" 
                            stroke-width="2"
                            stroke-linecap="round"
                            :stroke-dasharray="`${route.efficiencyScore} ${100 - route.efficiencyScore}`"
                            class="circular-progress"
                          />
                        </svg>
                        <div class="absolute inset-0 flex items-center justify-center">
                          <span class="text-xs font-bold" :class="getEfficiencyTextColor(route.efficiencyScore)">
                            {{ route.efficiencyScore }}%
                          </span>
                        </div>
                      </div>
                      <div class="min-w-0">
                        <div class="text-sm font-medium text-gray-900">{{ getEfficiencyLabel(route.efficiencyScore) }}</div>
                        <div class="text-xs text-gray-500">Performance</div>
                      </div>
                    </div>
                  </td>

                  <!-- Active Shipments -->
                  <td class="p-3">
                    <div class="flex items-center gap-2">
                      <div class="shipment-avatars flex -space-x-1">
                        <div 
                          v-for="i in Math.min(route.activeShipments, 3)" 
                          :key="i"
                          :class="getShipmentAvatarColor(i)"
                          class="w-6 h-6 rounded-full border-2 border-white flex items-center justify-center"
                        >
                          <span class="text-xs font-medium text-white">{{ i }}</span>
                        </div>
                        <div 
                          v-if="route.activeShipments > 3"
                          class="w-6 h-6 rounded-full border-2 border-white bg-gray-500 flex items-center justify-center"
                        >
                          <span class="text-xs font-medium text-white">+{{ route.activeShipments - 3 }}</span>
                        </div>
                      </div>
                      <span class="text-sm font-medium text-gray-600">{{ route.activeShipments }}</span>
                    </div>
                  </td>

                  <!-- Actions -->
                  <td class="p-3">
                    <div class="actions-container flex items-center justify-center gap-1">
                      <button 
                        @click="viewRoute(route)" 
                        class="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors tooltip-container"
                        title="View Details"
                      >
                        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                          <circle cx="12" cy="12" r="3"></circle>
                        </svg>
                      </button>
                      <button 
                        @click="editRoute(route)" 
                        class="p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors tooltip-container"
                        title="Edit Route"
                      >
                        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                          <path d="M18.5 2.5a2.12 2.12 0 0 1 3 3L12 15l-4 1 1-4z"></path>
                        </svg>
                      </button>
                      <button 
                        @click="optimizeRoute(route)" 
                        class="p-2 text-green-600 hover:bg-green-100 rounded-lg transition-colors tooltip-container"
                        title="Optimize Route"
                      >
                        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <polyline points="23,6 13.5,15.5 8.5,10.5 1,18"></polyline>
                          <polyline points="17,6 23,6 23,12"></polyline>
                        </svg>
                      </button>
                      <button 
                        @click="promptDeleteRoute(route)" 
                        class="p-2 text-red-600 hover:bg-red-100 rounded-lg transition-colors tooltip-container"
                        title="Delete Route"
                      >
                        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <polyline points="3,6 5,6 21,6"></polyline>
                          <path d="M19,6v14a2,2 0 0,1-2,2H7a2,2 0 0,1-2-2V6m3,0V4a2,2 0 0,1,2-2h4a2,2 0 0,1,2,2v2"></path>
                        </svg>
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
            v-for="route in filteredRoutes"
            :key="route.id"
            class="bg-white border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow"
            :class="{ 'ring-2 ring-blue-500 bg-blue-50': isSelected(route.id) }"
          >
            <!-- Header with checkbox and route ID -->
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center gap-3">
                <input 
                  type="checkbox" 
                  :value="route.id" 
                  v-model="selectedRoutes"
                  class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                />
                <div>
                  <div class="font-mono text-sm font-medium text-blue-600">{{ route.id }}</div>
                  <div class="text-lg font-semibold text-gray-900">{{ route.name }}</div>
                </div>
              </div>
              <div 
                :class="getRouteTypeBadgeClass(route.routeType)"
                class="px-2 py-1 rounded-full text-xs font-medium"
              >
                {{ route.routeType.charAt(0).toUpperCase() + route.routeType.slice(1) }}
              </div>
            </div>

            <!-- Route Path -->
            <div class="mb-4">
              <div class="flex items-center justify-between text-sm mb-2">
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
                  <span class="font-medium text-gray-900">{{ route.origin.location }}</span>
                </div>
                <svg class="w-4 h-4 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="m9 18 6-6-6-6"/>
                </svg>
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 bg-green-500 rounded-full"></div>
                  <span class="font-medium text-gray-900">{{ route.destination.location }}</span>
                </div>
              </div>
              <div class="text-xs text-gray-500 text-center">
                {{ route.origin.port }} → {{ route.destination.port }}
              </div>
            </div>

            <!-- Stats Grid -->
            <div class="grid grid-cols-2 gap-4 mb-4">
              <!-- Duration -->
              <div class="text-center p-3 bg-gray-50 rounded-lg">
                <div class="flex items-center justify-center gap-1 mb-1">
                  <svg class="w-4 h-4 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12,6 12,12 16,14"></polyline>
                  </svg>
                  <span class="text-sm font-semibold text-gray-900">{{ route.duration }}h</span>
                </div>
                <div class="text-xs text-gray-500">Duration</div>
                <div 
                  v-if="getDurationComparison(route.duration)"
                  :class="getDurationBadgeClass(route.duration)"
                  class="text-xs px-2 py-1 rounded-full font-medium mt-1 inline-block"
                >
                  {{ getDurationComparison(route.duration) }}
                </div>
              </div>

              <!-- Distance -->
              <div class="text-center p-3 bg-gray-50 rounded-lg">
                <div class="text-sm font-semibold text-gray-900 mb-1">{{ formatNumber(route.distance) }} km</div>
                <div class="text-xs text-gray-500 mb-2">Distance</div>
                <div class="w-full bg-gray-200 rounded-full h-1.5 overflow-hidden">
                  <div 
                    class="h-1.5 rounded-full distance-fill transition-all duration-700"
                    :style="{ 
                      width: `${getDistancePercentage(route.distance)}%`,
                      background: getDistanceColor(route.distance)
                    }"
                  ></div>
                </div>
              </div>
            </div>

            <!-- Status and Efficiency Row -->
            <div class="flex items-center justify-between mb-4">
              <!-- Status -->
              <div>
                <div :class="getStatusBadgeClass(route.status)" class="status-badge px-3 py-2 rounded-lg text-xs font-semibold inline-flex items-center gap-2">
                  <div 
                    v-if="route.status === 'Active'" 
                    class="w-2 h-2 bg-green-500 rounded-full animate-pulse"
                  ></div>
                  {{ route.status }}
                </div>
                <div class="text-xs text-gray-500 mt-1">
                  {{ getStatusTimestamp(route.status) }}
                </div>
              </div>

              <!-- Efficiency -->
              <div class="flex items-center gap-2">
                <div class="relative w-8 h-8">
                  <svg class="w-8 h-8 transform -rotate-90" viewBox="0 0 36 36">
                    <path 
                      d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                      fill="none" 
                      stroke="#E5E7EB" 
                      stroke-width="3"
                    />
                    <path 
                      d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                      fill="none" 
                      :stroke="getEfficiencyColor(route.efficiencyScore)" 
                      stroke-width="3"
                      stroke-linecap="round"
                      :stroke-dasharray="`${route.efficiencyScore} ${100 - route.efficiencyScore}`"
                      class="circular-progress"
                    />
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <span class="text-xs font-bold" :class="getEfficiencyTextColor(route.efficiencyScore)">
                      {{ route.efficiencyScore }}%
                    </span>
                  </div>
                </div>
                <div class="text-xs text-gray-500">Efficiency</div>
              </div>
            </div>

            <!-- Shipments and Actions -->
            <div class="flex items-center justify-between">
              <!-- Active Shipments -->
              <div class="flex items-center gap-2">
                <div class="shipment-avatars flex -space-x-1">
                  <div 
                    v-for="i in Math.min(route.activeShipments, 3)" 
                    :key="i"
                    :class="getShipmentAvatarColor(i)"
                    class="w-5 h-5 rounded-full border-2 border-white flex items-center justify-center"
                  >
                    <span class="text-xs font-medium text-white">{{ i }}</span>
                  </div>
                  <div 
                    v-if="route.activeShipments > 3"
                    class="w-5 h-5 rounded-full border-2 border-white bg-gray-500 flex items-center justify-center"
                  >
                    <span class="text-xs font-medium text-white">+{{ route.activeShipments - 3 }}</span>
                  </div>
                </div>
                <span class="text-sm font-medium text-gray-600">{{ route.activeShipments }} shipments</span>
              </div>

              <!-- Actions -->
              <div class="flex items-center gap-1">
                <button 
                  @click="viewRoute(route)" 
                  class="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors"
                  title="View Details"
                >
                  <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                </button>
                <button 
                  @click="editRoute(route)" 
                  class="p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
                  title="Edit Route"
                >
                  <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                    <path d="M18.5 2.5a2.12 2.12 0 0 1 3 3L12 15l-4 1 1-4z"></path>
                  </svg>
                </button>
                <button 
                  @click="optimizeRoute(route)" 
                  class="p-2 text-green-600 hover:bg-green-100 rounded-lg transition-colors"
                  title="Optimize Route"
                >
                  <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="23,6 13.5,15.5 8.5,10.5 1,18"></polyline>
                    <polyline points="17,6 23,6 23,12"></polyline>
                  </svg>
                </button>
                <button 
                  @click="promptDeleteRoute(route)" 
                  class="p-2 text-red-600 hover:bg-red-100 rounded-lg transition-colors"
                  title="Delete Route"
                >
                  <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="3,6 5,6 21,6"></polyline>
                    <path d="M19,6v14a2,2 0 0,1-2,2H7a2,2 0 0,1-2-2V6m3,0V4a2,2 0 0,1,2-2h4a2,2 0 0,1,2,2v2"></path>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="filteredRoutes.length === 0" class="empty-state text-center py-20">
          <svg class="empty-icon w-16 h-16 text-gray-300 mx-auto mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polygon points="3 6 9 1 15 6 21 1 21 18 15 23 9 18 3 23"></polygon>
            <line x1="9" y1="1" x2="9" y2="18"></line>
            <line x1="15" y1="6" x2="15" y2="23"></line>
          </svg>
          <p class="empty-title text-lg font-semibold text-[#64748B] mb-2">No routes configured</p>
          <p class="empty-description text-sm text-[#94A3B8] mb-6">Create your first shipping route to optimize deliveries</p>
          <button 
            class="btn-create-first flex items-center gap-2 mx-auto px-6 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all"
            @click="openCreateRouteModal"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            Create Route
          </button>
        </div>
      </div>
    </div>

    <!-- Bulk Actions Bar -->
    <transition name="slide-up">
      <div v-if="selectedRoutes.length > 0" class="bulk-actions-bar fixed bottom-6 left-1/2 transform -translate-x-1/2 bg-white border border-gray-200 rounded-2xl shadow-lg p-4 flex items-center gap-4 z-30">
        <span class="selected-count text-sm font-medium text-gray-700">{{ selectedRoutes.length }} route(s) selected</span>
        <div class="bulk-actions flex items-center gap-2">
          <button 
            @click="bulkDeleteRoutes"
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
            </svg>
            Delete Selected
          </button>
          <button 
            @click="clearSelection"
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-600 hover:text-gray-700 hover:bg-gray-50 rounded-lg transition-colors"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
            Clear Selection
          </button>
        </div>
      </div>
    </transition>
    
    <!-- FIX: Removed redundant RouteMap component instance -->
    <!-- <RouteMap v-if="view === 'map'" :routes="routes" /> -->
    
    <!-- Create/Edit Route Modal -->
    <BaseModal :show="showCreateRouteModal" @close="closeCreateRouteModal" max-width="lg">
      <template #header>
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
            <svg class="w-5 h-5 text-blue-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polygon points="3 11 22 2 13 21 11 13 3 11"></polygon>
            </svg>
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900">{{ isEditMode ? 'Edit Route' : 'Create New Route' }}</h2>
            <p class="text-sm text-gray-500">{{ isEditMode ? 'Update route details' : 'Set up a new shipping route' }}</p>
          </div>
        </div>
      </template>
      
      <template #body>
        <form @submit.prevent="saveRoute" class="space-y-6">
          <!-- Route ID -->
          <div>
            <label for="routeId" class="block text-sm font-medium text-gray-700 mb-2">
              Route ID
            </label>
            <input
              id="routeId"
              v-model="routeForm.id"
              type="text"
              readonly
              class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 font-mono text-sm"
            />
          </div>

          <!-- Route Name -->
          <div>
            <label for="routeName" class="block text-sm font-medium text-gray-700 mb-2">
              Route Name <span class="text-red-500">*</span>
            </label>
            <input
              id="routeName"
              v-model="routeForm.name"
              type="text"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="e.g., Mumbai-Chennai Corridor"
            />
          </div>

          <!-- Origin -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label for="originLocation" class="block text-sm font-medium text-gray-700 mb-2">
                Origin City <span class="text-red-500">*</span>
              </label>
              <input
                id="originLocation"
                v-model="routeForm.originLocation"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="e.g., Mumbai"
              />
            </div>
            <div>
              <label for="originPort" class="block text-sm font-medium text-gray-700 mb-2">
                Origin Port
              </label>
              <input
                id="originPort"
                v-model="routeForm.originPort"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="e.g., Jawaharlal Nehru Port"
              />
            </div>
          </div>

          <!-- Destination -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label for="destinationLocation" class="block text-sm font-medium text-gray-700 mb-2">
                Destination City <span class="text-red-500">*</span>
              </label>
              <input
                id="destinationLocation"
                v-model="routeForm.destinationLocation"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="e.g., Chennai"
              />
            </div>
            <div>
              <label for="destinationPort" class="block text-sm font-medium text-gray-700 mb-2">
                Destination Port
              </label>
              <input
                id="destinationPort"
                v-model="routeForm.destinationPort"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="e.g., Chennai Port"
              />
            </div>
          </div>

          <!-- Duration and Distance -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label for="duration" class="block text-sm font-medium text-gray-700 mb-2">
                Duration (hours) <span class="text-red-500">*</span>
              </label>
              <input
                id="duration"
                v-model.number="routeForm.duration"
                type="number"
                min="1"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="e.g., 48"
              />
            </div>
            <div>
              <label for="distance" class="block text-sm font-medium text-gray-700 mb-2">
                Distance (km) <span class="text-red-500">*</span>
              </label>
              <input
                id="distance"
                v-model.number="routeForm.distance"
                type="number"
                min="1"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="e.g., 1350"
              />
            </div>
          </div>

          <!-- Route Type and Status -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label for="routeType" class="block text-sm font-medium text-gray-700 mb-2">
                Route Type <span class="text-red-500">*</span>
              </label>
              <select
                id="routeType"
                v-model="routeForm.routeType"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option v-for="type in routeTypes" :key="type.value" :value="type.value">
                  {{ type.label }}
                </option>
              </select>
            </div>
            <div>
              <label for="status" class="block text-sm font-medium text-gray-700 mb-2">
                Status <span class="text-red-500">*</span>
              </label>
              <select
                id="status"
                v-model="routeForm.status"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option v-for="status in statusOptions" :key="status.value" :value="status.value">
                  {{ status.label }}
                </option>
              </select>
            </div>
          </div>
        </form>
      </template>
      
      <template #footer>
        <button
          type="button"
          @click="closeCreateRouteModal"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
        >
          Cancel
        </button>
        <button
          type="button"
          @click="saveRoute"
          :disabled="isFormLoading"
          class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
        >
          <div v-if="isFormLoading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
          {{ isFormLoading ? (isEditMode ? 'Updating...' : 'Creating...') : (isEditMode ? 'Update Route' : 'Create Route') }}
        </button>
      </template>
    </BaseModal>

    <!-- Route Details Modal -->
    <BaseModal :show="showRouteDetailsModal" @close="closeRouteDetailsModal" max-width="2xl">
      <template #header>
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
            <svg class="w-5 h-5 text-blue-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polygon points="3 11 22 2 13 21 11 13 3 11"></polygon>
            </svg>
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900">Route Details</h2>
            <p class="text-sm text-gray-600">{{ selectedRouteDetails?.id }} - {{ selectedRouteDetails?.name }}</p>
          </div>
        </div>
      </template>
      
      <template #body>
        <div v-if="selectedRouteDetails" class="space-y-6">
          <!-- Route Overview -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-4">
              <div>
                <label class="text-sm font-medium text-gray-700">Origin</label>
                <div class="mt-1 p-3 bg-gray-50 rounded-lg">
                  <div class="font-medium">{{ selectedRouteDetails.origin.location }}</div>
                  <div class="text-sm text-gray-600">Port: {{ selectedRouteDetails.origin.port }}</div>
                </div>
              </div>
              
              <div>
                <label class="text-sm font-medium text-gray-700">Destination</label>
                <div class="mt-1 p-3 bg-gray-50 rounded-lg">
                  <div class="font-medium">{{ selectedRouteDetails.destination.location }}</div>
                  <div class="text-sm text-gray-600">Port: {{ selectedRouteDetails.destination.port }}</div>
                </div>
              </div>
            </div>
            
            <div class="space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="text-sm font-medium text-gray-700">Distance</label>
                  <div class="mt-1 text-2xl font-bold text-gray-900">{{ selectedRouteDetails.distance.toLocaleString() }} km</div>
                </div>
                <div>
                  <label class="text-sm font-medium text-gray-700">Duration</label>
                  <div class="mt-1 text-2xl font-bold text-gray-900">{{ selectedRouteDetails.duration }}h</div>
                </div>
              </div>
              
              <div>
                <label class="text-sm font-medium text-gray-700">Route Type</label>
                <div class="mt-1">
                  <span :class="getRouteTypeBadgeClass(selectedRouteDetails.routeType)" class="px-2 py-1 rounded-full text-xs font-medium">
                    {{ selectedRouteDetails.routeType.charAt(0).toUpperCase() + selectedRouteDetails.routeType.slice(1) }}
                  </span>
                </div>
              </div>
              
              <div>
                <label class="text-sm font-medium text-gray-700">Status</label>
                <div class="mt-1">
                  <span :class="getStatusBadgeClass(selectedRouteDetails.status)" class="px-3 py-2 rounded-lg text-xs font-semibold inline-flex items-center gap-2">
                    {{ selectedRouteDetails.status }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Performance Metrics -->
          <div class="border-t pt-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Performance Metrics</h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div class="bg-blue-50 p-4 rounded-lg">
                <div class="text-2xl font-bold text-blue-600">{{ selectedRouteDetails.efficiencyScore }}%</div>
                <div class="text-sm text-gray-600">Efficiency Score</div>
              </div>
              <div class="bg-green-50 p-4 rounded-lg">
                <div class="text-2xl font-bold text-green-600">{{ selectedRouteDetails.activeShipments }}</div>
                <div class="text-sm text-gray-600">Active Shipments</div>
              </div>
              <div class="bg-amber-50 p-4 rounded-lg">
                <div class="text-2xl font-bold text-amber-600">{{ getDurationComparison(selectedRouteDetails.duration) || 'Average' }}</div>
                <div class="text-sm text-gray-600">Duration vs Average</div>
              </div>
            </div>
          </div>
          
          <!-- Route History -->
          <div class="border-t pt-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Route Information</h3>
            <div class="space-y-3">
              <div class="flex justify-between py-2 border-b border-gray-100">
                <span class="text-gray-600">Created</span>
                <span class="font-medium">{{ selectedRouteDetails.createdAt.toLocaleDateString() }}</span>
              </div>
              <div class="flex justify-between py-2 border-b border-gray-100">
                <span class="text-gray-600">Last Optimized</span>
                <span class="font-medium">{{ selectedRouteDetails.lastOptimized?.toLocaleDateString() || 'Never' }}</span>
              </div>
            </div>
          </div>
        </div>
      </template>
      
      <template #footer>
        <div class="flex justify-end gap-3">
          <button @click="closeRouteDetailsModal" class="px-4 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50">
            Close
          </button>
          <button 
            @click="selectedRouteDetails && editRoute(selectedRouteDetails)" 
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            :disabled="!selectedRouteDetails"
          >
            Edit Route
          </button>
        </div>
      </template>
    </BaseModal>

    <!-- Delete Confirmation Dialog -->
    <ConfirmDialog
      :show="showDeleteConfirm"
      title="Delete Route"
      :message="`Are you sure you want to delete route ${routeToDelete?.id} (${routeToDelete?.name})? This action cannot be undone.`"
      confirm-text="Delete"
      cancel-text="Cancel"
      variant="danger"
      @confirm="handleConfirmDelete"
      @cancel="cancelDelete"
    />
    
    </div> <!-- End of v-else content -->
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import BaseModal from './shared/BaseModal.vue'
import ConfirmDialog from './shared/ConfirmDialog.vue'
import RouteMap from './shared/RouteMap.vue'
import { routeApi } from '../services/api.js'

// TypeScript Interfaces
interface Route {
  id: string
  name: string
  origin: {
    location: string
    port: string
    coordinates?: { lat: number; lng: number }
  }
  destination: {
    location: string
    port: string
    coordinates?: { lat: number; lng: number }
  }
  duration: number // in hours
  distance: number // in km
  status: 'Active' | 'Delayed' | 'Closed'
  activeShipments: number
  efficiencyScore: number // percentage
  routeType: 'express' | 'standard' | 'priority'
  createdAt: Date
  lastOptimized?: Date
  waypoints?: Array<{ lat: number; lng: number; name: string }>
}

interface RouteMetrics {
  totalRoutes: number
  activeRoutes: number
  totalDistance: number
  avgDuration: number
  networkEfficiency: number
  onTimeRate: number
}

interface MetricCard {
  id: string
  label: string
  displayValue: string
  rawValue: number
  suffix?: string
  subtitle: string
  iconName: string
  iconBg: string
  iconColor: string
  hoverBorder: string
}

// Reactive State
const searchQuery = ref('')
const activeFilters = ref<string[]>([])
const selectedRoutes = ref<string[]>([])
const selectAll = ref(false)
const view = ref<'table' | 'map'>('table')
const sortBy = ref<'duration' | 'distance' | 'name'>('name')
const sortOrder = ref<'asc' | 'desc'>('asc')
const showFilterMenu = ref(false)
const showSortMenu = ref(false)

// API State
const routes = ref<Route[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

// Route Form State
const showCreateRouteModal = ref(false)
const isFormLoading = ref(false)
const isEditMode = ref(false)

// Modal States
const showRouteDetailsModal = ref(false)
const showDeleteConfirm = ref(false)

// Data State
const selectedRouteDetails = ref<Route | null>(null)
const routeToDelete = ref<Route | null>(null)

// Form Data
const routeForm = ref({
  id: '',
  name: '',
  originLocation: '',
  originPort: '',
  destinationLocation: '',
  destinationPort: '',
  duration: 0,
  distance: 0,
  cost: 0,
  routeType: 'standard' as 'express' | 'standard' | 'priority',
  status: 'Active' as 'Active' | 'Delayed' | 'Closed'
})

// Available options
const routeTypes = [
  { value: 'express', label: 'Express' },
  { value: 'standard', label: 'Standard' },
  { value: 'priority', label: 'Priority' }
]

// FIX: Corrected statusOptions to match the database schema ('Active', 'Delayed', 'Closed').
// This ensures data consistency when creating or editing routes.
const statusOptions = [
  { value: 'Active', label: 'Active' },
  { value: 'Delayed', label: 'Delayed' },
  { value: 'Closed', label: 'Closed' }
]

// Mock Data - Commented out, now using API calls
// (Original mock data kept for reference but not used)

// FIX: Converted `metrics` to a computed property.
// This ensures that metrics are always in sync with the `routes` data,
// removing the need for manual updates and reducing potential bugs.
const metrics = computed<RouteMetrics>(() => {
  const totalRoutes = routes.value.length;
  const activeRoutes = routes.value.filter(r => r.status === 'Active').length;
  const totalDistance = routes.value.reduce((sum, route) => sum + route.distance, 0);
  const avgDuration = totalRoutes > 0 ? Math.round(routes.value.reduce((sum, route) => sum + route.duration, 0) / totalRoutes) : 0;
  
  // Calculate network efficiency based on route efficiency scores
  const avgEfficiency = totalRoutes > 0 ? routes.value.reduce((sum, route) => sum + (route.efficiencyScore || 85), 0) / totalRoutes : 85;
  const networkEfficiency = Math.round(avgEfficiency);
  const onTimeRate = Math.round(avgEfficiency); // Using efficiency as proxy for on-time rate

  return {
    totalRoutes,
    activeRoutes,
    totalDistance,
    avgDuration,
    networkEfficiency,
    onTimeRate
  };
})


const optimizableRoutes = computed(() => 
  routes.value.filter(route => route.efficiencyScore < 85 && route.status !== 'Closed').length
)

const metricsCards = computed<MetricCard[]>(() => [
  {
    id: 'total-routes',
    label: 'Total Routes',
    displayValue: metrics.value.totalRoutes.toString(),
    rawValue: metrics.value.totalRoutes,
    subtitle: 'Established paths',
    iconName: 'Network',
    iconBg: 'bg-blue-100',
    iconColor: 'text-blue-500',
    hoverBorder: 'hover:border-blue-200'
  },
  {
    id: 'active-routes',
    label: 'Active Routes',
    displayValue: metrics.value.activeRoutes.toString(),
    rawValue: metrics.value.activeRoutes,
    subtitle: 'Currently operational',
    iconName: 'Activity',
    iconBg: 'bg-green-100',
    iconColor: 'text-green-500',
    hoverBorder: 'hover:border-green-200'
  },
  {
    id: 'total-distance',
    label: 'Total Distance',
    displayValue: `${formatNumber(metrics.value.totalDistance)} km`,
    rawValue: metrics.value.totalDistance,
    suffix: ' km',
    subtitle: 'Combined coverage',
    iconName: 'Compass',
    iconBg: 'bg-yellow-100',
    iconColor: 'text-yellow-600',
    hoverBorder: 'hover:border-yellow-200'
  },
  {
    id: 'avg-duration',
    label: 'Avg. Duration',
    displayValue: `${metrics.value.avgDuration}h`,
    rawValue: metrics.value.avgDuration,
    suffix: 'h',
    subtitle: 'Per route',
    iconName: 'Clock',
    iconBg: 'bg-purple-100',
    iconColor: 'text-purple-500',
    hoverBorder: 'hover:border-purple-200'
  }
])

const filteredRoutes = computed(() => {
  let filtered = [...routes.value];

  // Apply search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(route =>
      route.id.toLowerCase().includes(query) ||
      route.name.toLowerCase().includes(query) ||
      route.origin.location.toLowerCase().includes(query) ||
      route.destination.location.toLowerCase().includes(query) ||
      route.origin.port.toLowerCase().includes(query) ||
      route.destination.port.toLowerCase().includes(query) ||
      route.status.toLowerCase().includes(query) ||
      route.routeType.toLowerCase().includes(query)
    )
  }

  // Apply status filters
  if (activeFilters.value.length > 0) {
    filtered = filtered.filter(route => activeFilters.value.includes(route.status))
  }

  // Apply sorting
  return filtered.sort((a, b) => {
    let aValue: string | number;
    let bValue: string | number;

    switch (sortBy.value) {
      case 'duration':
        aValue = a.duration;
        bValue = b.duration;
        break;
      case 'distance':
        aValue = a.distance;
        bValue = b.distance;
        break;
      case 'name':
      default:
        aValue = a.name.toLowerCase();
        bValue = b.name.toLowerCase();
        break;
    }

    if (aValue < bValue) return sortOrder.value === 'asc' ? -1 : 1;
    if (aValue > bValue) return sortOrder.value === 'asc' ? 1 : -1;
    return 0;
  });
})

// --- Watchers ---

watch(selectedRoutes, (newSelection) => {
  if (newSelection.length === 0) {
    selectAll.value = false;
  } else if (newSelection.length === filteredRoutes.value.length) {
    selectAll.value = true;
  }
});


// --- Methods ---

const formatNumber = (num: number): string => {
  return new Intl.NumberFormat().format(num)
}

const handleMetricClick = (metric: MetricCard) => {
  console.log('Metric clicked:', metric.id)
  // Example: filter by status if an active routes card is clicked
  if (metric.id === 'active-routes') {
    activeFilters.value = ['Active'];
  }
}

// UI Toggles
const toggleFilterMenu = () => {
  showFilterMenu.value = !showFilterMenu.value;
  if (showFilterMenu.value) showSortMenu.value = false;
}

const toggleSortMenu = () => {
  showSortMenu.value = !showSortMenu.value;
  if (showSortMenu.value) showFilterMenu.value = false;
}

const toggleMapView = () => {
  view.value = view.value === 'table' ? 'map' : 'table';
}

// Filtering and Sorting
const toggleFilter = (status: string) => {
  const index = activeFilters.value.indexOf(status)
  if (index > -1) {
    activeFilters.value.splice(index, 1)
  } else {
    activeFilters.value.push(status)
  }
}

const clearFilters = () => {
  activeFilters.value = []
}

const setSorting = (field: 'duration' | 'distance' | 'name') => {
  if (sortBy.value === field) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = field
    sortOrder.value = 'asc'
  }
  showSortMenu.value = false
}

// Table Actions
const exportRoutes = () => {
  console.log('Exporting routes:', filteredRoutes.value);
  // Implement actual export logic (e.g., CSV)
}

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedRoutes.value = filteredRoutes.value.map(route => route.id);
  } else {
    selectedRoutes.value = [];
  }
}

const isSelected = (routeId: string): boolean => {
  return selectedRoutes.value.includes(routeId)
}

const copyToClipboard = (text: string) => {
  navigator.clipboard.writeText(text).then(() => {
    // Optional: show a toast notification for feedback
    console.log(`Copied "${text}" to clipboard.`);
  });
}

// Route CRUD
const openCreateRouteModal = () => {
  isEditMode.value = false
  routeForm.value = {
    id: `RT${String(routes.value.length + 1).padStart(3, '0')}`,
    name: '',
    originLocation: '',
    originPort: '',
    destinationLocation: '',
    destinationPort: '',
    duration: 0,
    distance: 0,
    cost: 0,
    routeType: 'standard',
    status: 'Active'
  }
  showCreateRouteModal.value = true
}

const closeCreateRouteModal = () => {
  showCreateRouteModal.value = false
}

const saveRoute = async () => {
  if (!routeForm.value.name || !routeForm.value.originLocation || 
      !routeForm.value.destinationLocation || !routeForm.value.duration || 
      !routeForm.value.distance) {
    console.error("Validation failed: All required fields must be filled.");
    return;
  }

  isFormLoading.value = true

  try {
    const routeData = {
      name: routeForm.value.name,
      originPort: routeForm.value.originLocation,
      destinationPort: routeForm.value.destinationLocation,
      duration: routeForm.value.duration,
      distance: routeForm.value.distance,
      status: routeForm.value.status,
      transportationMode: routeForm.value.routeType,
      cost: routeForm.value.cost || 0
    }

    if (isEditMode.value) {
      await updateRoute(routeForm.value.id, routeData)
    } else {
      await createRoute(routeData)
    }
  } catch (error) {
    console.error('Error saving route:', error);
  } finally {
    isFormLoading.value = false
  }
}

const editRoute = (route: Route) => {
  if (showRouteDetailsModal.value) {
      closeRouteDetailsModal();
  }
  isEditMode.value = true
  routeForm.value = {
    id: route.id,
    name: route.name,
    originLocation: route.origin.location,
    originPort: route.origin.port,
    destinationLocation: route.destination.location,
    destinationPort: route.destination.port,
    duration: route.duration,
    distance: route.distance,
    cost: route._original?.cost || 0,
    routeType: route.routeType,
    status: route.status
  }
  showCreateRouteModal.value = true
}

const promptDeleteRoute = (route: Route) => {
  routeToDelete.value = route
  showDeleteConfirm.value = true
}

const handleConfirmDelete = async () => {
  if (routeToDelete.value) {
    try {
      await deleteRoute(routeToDelete.value.id)
      console.log(`Successfully deleted route: ${routeToDelete.value.id}`)
    } catch (error) {
      console.error('Error deleting route:', error)
    }
  }
  cancelDelete();
}

const cancelDelete = () => {
  showDeleteConfirm.value = false
  routeToDelete.value = null
}

const bulkDeleteRoutes = () => {
  if (selectedRoutes.value.length === 0) return;
  const idsToDelete = new Set(selectedRoutes.value);
  routes.value = routes.value.filter(route => !idsToDelete.has(route.id));
  console.log(`Successfully bulk deleted ${idsToDelete.size} routes`);
  clearSelection();
}

const clearSelection = () => {
  selectedRoutes.value = [];
}

const viewRouteDetails = (route: Route) => {
  selectedRouteDetails.value = route;
  showRouteDetailsModal.value = true;
}

const closeRouteDetailsModal = () => {
  showRouteDetailsModal.value = false;
  // No need to nullify, will be overwritten on next open
}

const viewRoute = (route: Route) => {
  viewRouteDetails(route);
}

const handleMarkerClick = (markerData: any) => {
  console.log('Marker clicked:', markerData);
  if (markerData.location) {
    searchQuery.value = markerData.location;
  }
}

const optimizeRoute = (route: Route) => {
  console.log('Optimize route:', route.id);
  // Simulate optimization
  const index = routes.value.findIndex(r => r.id === route.id);
  if (index !== -1 && routes.value[index].status !== 'Closed') {
    routes.value[index].efficiencyScore = Math.min(100, routes.value[index].efficiencyScore + Math.floor(Math.random() * 10) + 5);
    routes.value[index].lastOptimized = new Date();
  }
}


// UI Helper Functions
const getRouteTypeBadgeClass = (type: string): string => {
  const classes = {
    express: 'bg-blue-100 text-blue-800',
    standard: 'bg-gray-100 text-gray-800',
    priority: 'bg-purple-100 text-purple-800'
  }
  return classes[type as keyof typeof classes] || classes.standard
}

const getDurationComparison = (duration: number): string => {
  const diff = duration - metrics.value.avgDuration
  if (Math.abs(diff) < 2) return ''
  return diff > 0 ? `+${diff}h slower` : `${diff}h faster`
}

const getDurationBadgeClass = (duration: number): string => {
  const diff = duration - metrics.value.avgDuration
  return diff > 0 ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'
}

const getDistancePercentage = (distance: number): number => {
  const maxDistance = Math.max(...routes.value.map(r => r.distance), 1); // Avoid division by zero
  return (distance / maxDistance) * 100
}

const getDistanceColor = (distance: number): string => {
  const percentage = getDistancePercentage(distance)
  if (percentage < 40) return 'linear-gradient(90deg, #10B981, #34D399)'
  if (percentage < 70) return 'linear-gradient(90deg, #F59E0B, #FBBF24)'
  return 'linear-gradient(90deg, #EF4444, #F87171)'
}

const getEfficiencyColor = (score: number): string => {
  if (score >= 90) return '#10B981'
  if (score >= 75) return '#F59E0B'
  return '#EF4444'
}

const getEfficiencyTextColor = (score: number): string => {
  if (score >= 90) return 'text-green-600'
  if (score >= 75) return 'text-yellow-600'
  return 'text-red-600'
}

// FIX: Removed legacy lowercase status checks. Now only uses correct schema values.
const getStatusBadgeClass = (status: 'Active' | 'Delayed' | 'Closed'): string => {
  const classes = {
    'Active': 'bg-gradient-to-r from-green-50 to-emerald-50 text-green-800 border-l-4 border-green-500 shadow-sm',
    'Delayed': 'bg-gradient-to-r from-red-50 to-rose-50 text-red-800 border-l-4 border-red-500 shadow-sm animate-pulse',
    'Closed': 'bg-gradient-to-r from-gray-50 to-slate-50 text-gray-600 border-l-4 border-gray-400 shadow-sm',
  }
  return classes[status] || classes['Closed'];
}

const getEfficiencyBadge = (): string => {
  const avgEfficiency = metrics.value.networkEfficiency; // Using computed metric
  if (avgEfficiency >= 85) return 'bg-green-100 text-green-700'
  if (avgEfficiency >= 70) return 'bg-yellow-100 text-yellow-700'
  return 'bg-red-100 text-red-700'
}

const getEfficiencyText = (): string => {
  const avgEfficiency = metrics.value.networkEfficiency;
  if (avgEfficiency >= 85) return 'Efficient'
  if (avgEfficiency >= 70) return 'Good'
  return 'Needs Optimization'
}

const getEfficiencyLabel = (score: number): string => {
  if (score >= 90) return 'Excellent'
  if (score >= 80) return 'Good'
  if (score >= 70) return 'Average'
  return 'Poor'
}

// FIX: Corrected switch cases to use proper capitalized status values.
const getStatusTimestamp = (status: string): string => {
  switch (status) {
    case 'Active':
      return 'Currently active';
    case 'Delayed':
      return 'Delayed 2h ago';
    case 'Closed':
      return 'Stopped 1d ago';
    default:
      return '';
  }
}

const getShipmentAvatarColor = (index: number): string => {
  const colors = ['bg-blue-500', 'bg-green-500', 'bg-purple-500', 'bg-orange-500'];
  return colors[(index - 1) % colors.length];
}

// Lifecycle
onMounted(() => {
  console.log('Route Management component mounted')
  loadRoutes()
  
  const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as HTMLElement;
    if (showFilterMenu.value && !target.closest('.btn-filter')) {
      showFilterMenu.value = false;
    }
    if (showSortMenu.value && !target.closest('.btn-sort')) {
      showSortMenu.value = false;
    }
  };
  
  document.addEventListener('click', handleClickOutside);
})

// API Functions
const loadRoutes = async () => {
  isLoading.value = true
  error.value = null
  
  try {
    const data = await routeApi.getAll()
    
    // Transform backend data to frontend format
    routes.value = (data || []).map(route => ({
      id: `RT${String(route.routeId).padStart(3, '0')}`,
      name: route.name || `Route ${route.routeId}`,
      routeType: route.transportationMode || 'EXPRESS',
      origin: {
        location: route.originPort || 'Unknown',
        port: route.originPort || 'N/A'
      },
      destination: {
        location: route.destinationPort || 'Unknown',
        port: route.destinationPort || 'N/A'
      },
      distance: route.distance || 0,
      duration: route.duration || 0,
      status: route.status || 'ACTIVE',
      efficiencyScore: Math.floor(Math.random() * 20) + 80, // Generate efficiency score for display
      activeShipments: 0, // Could be calculated from shipments data
      // Keep original backend data for API operations
      _original: route
    }))
    
  } catch (err) {
    error.value = 'Failed to load routes. Please try again.'
    console.error('Error loading routes:', err)
    routes.value = []
  } finally {
    isLoading.value = false
  }
}

const createRoute = async (routeData: any) => {
  isFormLoading.value = true
  error.value = null

  try {
    const newRoute = await routeApi.create(routeData)
    // Add the new route to the local array with proper formatting
    const formattedRoute = {
      id: `RT${String(newRoute.routeId).padStart(3, '0')}`,
      name: newRoute.name || `Route ${newRoute.routeId}`,
      routeType: newRoute.transportationMode || 'EXPRESS',
      origin: {
        location: newRoute.originPort || 'Unknown',
        port: newRoute.originPort || 'N/A'
      },
      destination: {
        location: newRoute.destinationPort || 'Unknown',
        port: newRoute.destinationPort || 'N/A'
      },
      distance: newRoute.distance || 0,
      duration: newRoute.duration || 0,
      status: newRoute.status || 'ACTIVE',
      efficiencyScore: Math.floor(Math.random() * 20) + 80,
      activeShipments: 0,
      _original: newRoute
    }
    routes.value.unshift(formattedRoute)
    closeCreateRouteModal()
  } catch (err) {
    error.value = 'Failed to create route. Please try again.'
    console.error('Error creating route:', err)
  } finally {
    isFormLoading.value = false
  }
}

const updateRoute = async (id: string, routeData: any) => {
  isFormLoading.value = true
  error.value = null

  try {
    // Extract the numeric ID from the formatted ID (e.g., "RT001" -> 1)
    const numericId = parseInt(id.replace('RT', ''))
    const updatedRoute = await routeApi.update(numericId, routeData)
    
    const index = routes.value.findIndex(r => r.id === id)
    if (index !== -1) {
      // Update the route with the response data
      routes.value[index] = {
        ...routes.value[index],
        name: updatedRoute.name,
        routeType: updatedRoute.transportationMode || routes.value[index].routeType,
        origin: {
          location: updatedRoute.originPort || routes.value[index].origin.location,
          port: updatedRoute.originPort || routes.value[index].origin.port
        },
        destination: {
          location: updatedRoute.destinationPort || routes.value[index].destination.location,
          port: updatedRoute.destinationPort || routes.value[index].destination.port
        },
        distance: updatedRoute.distance || routes.value[index].distance,
        duration: updatedRoute.duration || routes.value[index].duration,
        status: updatedRoute.status || routes.value[index].status,
        _original: updatedRoute
      }
    }
    closeCreateRouteModal()
  } catch (err) {
    error.value = 'Failed to update route. Please try again.'
    console.error('Error updating route:', err)
  } finally {
    isFormLoading.value = false
  }
}

const deleteRoute = async (id: string) => {
  try {
    // Extract the numeric ID from the formatted ID (e.g., "RT001" -> 1)
    const numericId = parseInt(id.replace('RT', ''))
    await routeApi.delete(numericId)
    routes.value = routes.value.filter(r => r.id !== id)
  } catch (err) {
    error.value = 'Failed to delete route. Please try again.'
    console.error('Error deleting route:', err)
  }
}

// Cleanup function for click outside handling
onUnmounted(() => {
  const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as HTMLElement;
    if (showFilterMenu.value && !target.closest('.btn-filter')) {
      showFilterMenu.value = false;
    }
    if (showSortMenu.value && !target.closest('.btn-sort')) {
      showSortMenu.value = false;
    }
  };
  document.removeEventListener('click', handleClickOutside);
})
</script>

<style scoped>
/* Responsive table layout */
.routes-table {
  border-collapse: collapse;
  table-layout: auto;
}

.routes-table th,
.routes-table td {
  vertical-align: top;
}

/* Improved row hover effect */
.route-row {
  transition: all 0.2s ease-in-out;
}

.route-row:hover {
  background-color: #f8fafc;
  box-shadow: inset 4px 0 0 #3B82F6;
  transform: translateY(-1px);
}

.route-row:hover .actions-container .opacity-0 {
  opacity: 1;
}

/* Mobile card hover effects */
@media (max-width: 1023px) {
  .route-row:hover {
    transform: none;
    box-shadow: 0 4px 12px -2px rgba(0, 0, 0, 0.1);
  }
}

/* Bulk actions bar animation */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease-out;
}
.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(100%) translateX(-50%);
  opacity: 0;
}

/* Metric card animations */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.metric-card {
  animation: fadeInUp 0.6s ease-out both;
}
.metric-card:hover .metric-number {
  transform: scale(1.05);
}
.metric-number {
  transition: all 0.3s ease-out;
}

/* Live indicator pulse */
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* Custom scrollbar for desktop table */
.table-wrapper::-webkit-scrollbar { 
  width: 8px; 
  height: 8px;
}
.table-wrapper::-webkit-scrollbar-track { 
  background: #F3F4F6; 
  border-radius: 8px; 
}
.table-wrapper::-webkit-scrollbar-thumb { 
  background: #CBD5E1; 
  border-radius: 8px; 
}
.table-wrapper::-webkit-scrollbar-thumb:hover { 
  background: #94A3B8; 
}

/* Status badge shine effect on row hover */
.status-badge {
  position: relative;
  overflow: hidden;
}
.status-badge::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}
.route-row:hover .status-badge::before {
  left: 100%;
}

/* Efficiency circle animation */
.circular-progress {
  transition: stroke-dasharray 0.7s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Tooltip for action buttons - only on desktop */
@media (min-width: 1024px) {
  .tooltip-container {
    position: relative;
  }
  .tooltip-container:hover::after {
    content: attr(title);
    position: absolute;
    bottom: 100%;
    left: 50%;
    transform: translateX(-50%);
    background: #1F2937;
    color: white;
    padding: 6px 12px;
    border-radius: 6px;
    font-size: 12px;
    white-space: nowrap;
    z-index: 10;
    margin-bottom: 8px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  }
  .tooltip-container:hover::before {
    content: '';
    position: absolute;
    bottom: 100%;
    left: 50%;
    transform: translateX(-50%);
    border: 4px solid transparent;
    border-top-color: #1F2937;
    margin-bottom: 2px;
    z-index: 10;
  }
}

/* Distance bar fill animation */
.distance-fill {
  transition: width 0.7s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Enhanced status pulse for delayed routes */
.status-badge.animate-pulse {
  animation: statusPulse 2s infinite;
}
@keyframes statusPulse {
  50% { box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.1); }
}

/* Dropdown animations */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease-out;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}

/* General transition for sort arrow */
.transition-transform {
  transition: transform 0.2s ease-in-out;
}

/* Mobile optimizations */
@media (max-width: 1023px) {
  .route-management-page {
    padding: 1rem !important;
  }
  
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr) !important;
    gap: 1rem !important;
  }
  
  .toolbar-card {
    padding: 1rem !important;
  }
  
  .search-wrapper input {
    width: 100% !important;
    max-width: none !important;
  }
  
  .toolbar-actions {
    flex-wrap: wrap;
    gap: 0.5rem !important;
  }
}

@media (max-width: 640px) {
  .metrics-grid {
    grid-template-columns: 1fr !important;
  }
  
  .toolbar-card .flex {
    flex-direction: column;
    gap: 1rem;
  }
  
  .toolbar-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .btn-view-toggle span {
    display: none;
  }
  
  .bulk-actions-bar {
    flex-direction: column !important;
    gap: 0.75rem !important;
    padding: 1rem !important;
  }
  
  .bulk-actions {
    width: 100%;
    justify-content: center;
  }
}

/* Card animations for mobile */
@media (max-width: 1023px) {
  .space-y-4 > * {
    animation: slideInUp 0.4s ease-out both;
  }
  
  .space-y-4 > *:nth-child(1) { animation-delay: 0ms; }
  .space-y-4 > *:nth-child(2) { animation-delay: 100ms; }
  .space-y-4 > *:nth-child(3) { animation-delay: 200ms; }
  .space-y-4 > *:nth-child(4) { animation-delay: 300ms; }
  .space-y-4 > *:nth-child(n+5) { animation-delay: 400ms; }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Ensure proper table responsiveness */
@media (min-width: 1024px) {
  .table-wrapper {
    border-radius: 0 0 1rem 1rem;
    overflow: hidden;
  }
  
  .routes-table th:first-child,
  .routes-table td:first-child {
    padding-left: 1.5rem;
  }
  
  .routes-table th:last-child,
  .routes-table td:last-child {
    padding-right: 1.5rem;
  }
}
</style>
