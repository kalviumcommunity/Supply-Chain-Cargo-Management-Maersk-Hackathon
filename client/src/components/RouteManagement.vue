<template>
  <div class="route-management-page bg-[#F5F5F7] min-h-screen p-10">
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
      <div class="flex items-center justify-between">
        <div class="search-wrapper relative">
          <svg class="search-icon absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <path d="m21 21-4.35-4.35"></path>
          </svg>
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Search routes by ID, name, ports, or status..."
            class="search-input w-[540px] h-12 pl-12 pr-4 bg-[#F9FAFB] border border-[#E5E7EB] rounded-xl text-sm text-[#374151] focus:border-blue-500 focus:ring-3 focus:ring-blue-100 focus:outline-none transition-all"
          />
        </div>
        <div class="toolbar-actions flex items-center gap-3">
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
                    <!-- Updated status filters to match database schema: 'Active', 'Delayed', 'Closed' -->
                    <!-- Note: 'inactive' replaced with 'Closed' to match database CHECK constraint -->
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
              <svg class="w-3 h-3 ml-1" :class="{ 'rotate-180': sortOrder === 'desc' }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
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
                      <svg class="w-3 h-3" :class="{ 'rotate-180': sortOrder === 'desc' }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="6,9 12,15 18,9"></polyline>
                      </svg>
                    </div>
                  </button>
                </div>
              </div>
            </transition>
          </div>

          <button 
            class="btn-map-view flex items-center gap-2 h-12 px-5 border-1.5 border-[#E5E7EB] rounded-xl bg-white font-medium text-sm text-[#374151] hover:border-blue-500 hover:bg-blue-50 hover:text-blue-600 transition-all duration-200"
            @click="toggleMapView"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polygon points="3 6 9 1 15 6 21 1 21 18 15 23 9 18 3 23"></polygon>
              <line x1="9" y1="1" x2="9" y2="18"></line>
              <line x1="15" y1="6" x2="15" y2="23"></line>
            </svg>
            Map View
          </button>
        </div>
      </div>
    </div>

    <!-- Route Network Table -->
    <div class="routes-table-container bg-white rounded-2xl shadow-sm overflow-hidden">
      <div class="table-header flex justify-between items-center p-7 border-b-2 border-gray-100">
        <div class="header-left">
          <h2 class="table-title text-xl font-semibold text-[#1E293B] mb-1">Route Network</h2>
          <p class="table-subtitle text-sm text-[#64748B]">Overview of all shipping corridors</p>
        </div>
        <div class="header-actions flex items-center gap-4">
          <div class="view-toggle flex bg-gray-100 rounded-lg p-1">
            <button 
              :class="['px-4 py-2 text-sm font-medium rounded-md transition-all', view === 'table' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700']"
              @click="view = 'table'"
            >
              Table
            </button>
            <button 
              :class="['px-4 py-2 text-sm font-medium rounded-md transition-all', view === 'map' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700']"
              @click="view = 'map'"
            >
              Map
            </button>
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

      <!-- Table Content -->
      <div v-if="view === 'table'" class="table-wrapper max-h-[calc(100vh-520px)] overflow-auto">
        <table class="routes-table w-full table-fixed">
          <colgroup>
            <col class="w-20"> <!-- Checkbox -->
            <col class="w-28"> <!-- Route ID -->
            <col class="w-56"> <!-- Route Name -->
            <col class="w-72"> <!-- Origin → Destination -->
            <col class="w-32"> <!-- Duration -->
            <col class="w-36"> <!-- Distance -->
            <col class="w-36"> <!-- Status -->
            <col class="w-36"> <!-- Active Shipments -->
            <col class="w-40"> <!-- Actions -->
          </colgroup>
          <thead class="bg-gradient-to-b from-gray-50 to-gray-100 sticky top-0 z-10">
            <tr class="border-b-2 border-gray-200">
              <th class="text-left p-5">
                <input type="checkbox" v-model="selectAll" @change="toggleSelectAll" class="rounded border-gray-300 text-blue-600 focus:ring-blue-500" />
              </th>
              <th class="text-left p-5 text-xs font-bold text-gray-600 uppercase tracking-wider">Route ID</th>
              <th class="text-left p-5 text-xs font-bold text-gray-600 uppercase tracking-wider">Route Name</th>
              <th class="text-left p-5 text-xs font-bold text-gray-600 uppercase tracking-wider">Origin → Destination</th>
              <th class="text-left p-5 text-xs font-bold text-gray-600 uppercase tracking-wider">Duration</th>
              <th class="text-left p-5 text-xs font-bold text-gray-600 uppercase tracking-wider">Distance</th>
              <th class="text-left p-5 text-xs font-bold text-gray-600 uppercase tracking-wider">Status</th>
              <th class="text-center p-5 text-xs font-bold text-gray-600 uppercase tracking-wider">Shipments</th>
              <th class="text-center p-5 text-xs font-bold text-gray-600 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="route in filteredRoutes"
              :key="route.id"
              @click="viewRouteDetails(route)"
              class="route-row group border-b border-gray-100 hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 transition-all duration-200 cursor-pointer"
              :class="{ 'bg-blue-50 border-blue-200': isSelected(route.id) }"
            >
              <td class="p-5">
                <input 
                  type="checkbox" 
                  :value="route.id" 
                  v-model="selectedRoutes" 
                  @click.stop 
                  class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                />
              </td>
              <td class="route-id p-5">
                <div class="flex items-center gap-2 group/id">
                  <span class="text-sm font-bold text-[#1E293B] font-mono tracking-wide truncate">{{ route.id }}</span>
                  <button 
                    class="copy-btn opacity-0 group-hover/id:opacity-100 transition-opacity p-1 hover:bg-blue-100 rounded flex-shrink-0"
                    @click.stop="copyToClipboard(route.id)"
                    title="Copy Route ID"
                  >
                    <svg class="w-3 h-3 text-gray-400 hover:text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                      <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                    </svg>
                  </button>
                </div>
              </td>
              <td class="route-name p-5">
                <div class="space-y-2">
                  <div class="name-primary text-sm font-semibold text-[#1E293B] leading-snug truncate">{{ route.name }}</div>
                  <span 
                    class="route-type-badge inline-flex items-center px-2.5 py-1 text-xs font-bold uppercase tracking-wider rounded-md"
                    :class="getRouteTypeBadgeClass(route.routeType)"
                  >
                    {{ route.routeType }}
                  </span>
                </div>
              </td>
              <td class="route-path p-5">
                <div class="space-y-3">
                  <div class="path-origin flex items-center gap-3">
                    <div class="flex-shrink-0">
                      <svg class="w-4 h-4 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M2 20a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2H2Z"></path>
                        <path d="m6 12 4-4 4 4"></path>
                        <path d="M10 8V4a2 2 0 0 1 2-2h0a2 2 0 0 1 2 2v4"></path>
                      </svg>
                    </div>
                    <div class="location-info flex-1 min-w-0">
                      <div class="location text-sm font-semibold text-[#1E293B] truncate">{{ route.origin.location }}</div>
                      <div class="port text-xs text-[#64748B] truncate">{{ route.origin.port }}</div>
                    </div>
                  </div>
                  <div class="path-arrow flex justify-center">
                    <div class="flex items-center justify-center w-6 h-6 rounded-full bg-gray-100 group-hover:bg-blue-100 transition-colors">
                      <svg class="w-3 h-3 text-gray-500 group-hover:text-blue-600 transition-colors" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="9,18 15,12 9,6"></polyline>
                      </svg>
                    </div>
                  </div>
                  <div class="path-destination flex items-center gap-3">
                    <div class="flex-shrink-0">
                      <svg class="w-4 h-4 text-green-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"></path>
                        <circle cx="12" cy="10" r="3"></circle>
                      </svg>
                    </div>
                    <div class="location-info flex-1 min-w-0">
                      <div class="location text-sm font-semibold text-[#1E293B] truncate">{{ route.destination.location }}</div>
                      <div class="port text-xs text-[#64748B] truncate">{{ route.destination.port }}</div>
                    </div>
                  </div>
                </div>
              </td>
              <td class="duration p-5">
                <div class="space-y-2">
                  <div class="flex items-center gap-2">
                    <svg class="w-4 h-4 text-gray-400 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"></circle>
                      <polyline points="12,6 12,12 16,14"></polyline>
                    </svg>
                    <span class="duration-value text-sm font-semibold text-[#1E293B]">{{ route.duration }}h</span>
                  </div>
                  <div class="duration-badge" v-if="getDurationComparison(route.duration)">
                    <span 
                      class="text-xs px-2 py-1 rounded-md font-medium"
                      :class="getDurationBadgeClass(route.duration)"
                    >
                      {{ getDurationComparison(route.duration) }}
                    </span>
                  </div>
                </div>
              </td>
              <td class="distance p-5">
                <div class="space-y-3">
                  <div class="distance-value text-sm font-semibold text-[#1E293B]">{{ formatNumber(route.distance) }} km</div>
                  <div class="distance-bar-container">
                    <div class="flex items-center justify-between text-xs text-gray-500 mb-1">
                      <span>Distance</span>
                      <span>{{ Math.round(getDistancePercentage(route.distance)) }}%</span>
                    </div>
                    <div class="distance-bar w-full h-2 bg-gray-200 rounded-full overflow-hidden">
                      <div 
                        class="distance-fill h-full rounded-full transition-all duration-700 ease-out"
                        :style="{ 
                          width: getDistancePercentage(route.distance) + '%',
                          background: getDistanceColor(route.distance)
                        }"
                      ></div>
                    </div>
                  </div>
                </div>
              </td>
              <td class="p-5">
                <div class="status-container">
                  <span 
                    class="status-badge inline-flex items-center gap-2 px-3 py-2 text-xs font-bold uppercase tracking-wider rounded-lg transition-all duration-200"
                    :class="getStatusBadgeClass(route.status)"
                  >
                    <div class="status-icon w-3.5 h-3.5 flex-shrink-0">
                      <!-- Active Status Icon -->
                      <svg v-if="route.status === 'Active'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                        <polyline points="22,4 12,14.01 9,11.01"></polyline>
                      </svg>
                      <!-- Delayed Status Icon -->
                      <svg v-else-if="route.status === 'Delayed'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"></path>
                        <line x1="12" y1="9" x2="12" y2="13"></line>
                        <line x1="12" y1="17" x2="12.01" y2="17"></line>
                      </svg>
                      <!-- Closed Status Icon (formerly Inactive) -->
                      <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="10" y1="15" x2="10" y2="9"></line>
                        <line x1="14" y1="15" x2="14" y2="9"></line>
                      </svg>
                    </div>
                    {{ route.status }}
                  </span>
                  <!-- Status timestamp -->
                  <div class="mt-2 text-xs text-gray-400">
                    {{ getStatusTimestamp(route.status) }}
                  </div>
                </div>
              </td>
              <td class="shipments-count p-5">
                <div class="shipments-info text-center">
                  <div class="count-value text-lg font-bold text-[#1E293B] mb-1">{{ route.activeShipments }}</div>
                  <div class="count-label text-xs text-[#64748B] mb-2 truncate">
                    shipment{{ route.activeShipments !== 1 ? 's' : '' }}
                  </div>
                  <!-- Shipment avatars -->
                  <div v-if="route.activeShipments > 0" class="shipment-avatars flex justify-center -space-x-1">
                    <div 
                      v-for="i in Math.min(route.activeShipments, 2)" 
                      :key="i"
                      class="w-5 h-5 rounded-full border-2 border-white flex items-center justify-center text-xs font-bold text-white"
                      :class="getShipmentAvatarColor(i)"
                    >
                      {{ i }}
                    </div>
                    <div 
                      v-if="route.activeShipments > 2"
                      class="w-5 h-5 rounded-full bg-gray-400 border-2 border-white flex items-center justify-center text-xs font-bold text-white"
                    >
                      +{{ route.activeShipments - 2 }}
                    </div>
                  </div>
                </div>
              </td>
              <td class="actions-cell p-5">
                <div class="actions-container flex items-center justify-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                  <button 
                    class="action-btn view w-8 h-8 flex items-center justify-center rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-all duration-150 flex-shrink-0"
                    @click.stop="viewRoute(route)"
                    title="View Route Details"
                  >
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                      <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                  </button>
                  <button 
                    class="action-btn edit w-8 h-8 flex items-center justify-center rounded-lg hover:bg-green-50 hover:text-green-600 transition-all duration-150 flex-shrink-0"
                    @click.stop="editRoute(route)"
                    title="Edit Route"
                  >
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                  </button>
                  <button 
                    class="action-btn delete w-8 h-8 flex items-center justify-center rounded-lg hover:bg-red-50 hover:text-red-600 transition-all duration-150 flex-shrink-0"
                    @click.stop="promptDeleteRoute(route)"
                    title="Delete Route"
                  >
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="3,6 5,6 21,6"></polyline>
                      <path d="m19,6v14a2,2 0 0,1-2,2H7a2,2 0 0,1-2-2V6m3,0V4a2,2 0 0,1,2-2h4a2,2 0 0,1,2,2v2"></path>
                      <line x1="10" y1="11" x2="10" y2="17"></line>
                      <line x1="14" y1="11" x2="14" y2="17"></line>
                    </svg>
                  </button>
                  <button 
                    class="action-btn optimize flex items-center justify-center gap-1.5 px-3 py-2 border border-gray-300 rounded-lg text-xs font-semibold hover:border-green-500 hover:bg-green-50 hover:text-green-700 transition-all duration-150 flex-shrink-0"
                    @click.stop="optimizeRoute(route)"
                    :disabled="route.status === 'Closed'"
                    :class="{ 'opacity-50 cursor-not-allowed': route.status === 'Closed' }"
                  >
                    <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon>
                    </svg>
                    <span>Opt</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

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
      <div v-if="selectedRoutes.length > 0" class="bulk-actions-bar fixed bottom-6 left-1/2 transform -translate-x-1/2 bg-white border border-gray-200 rounded-2xl shadow-lg p-4 flex items-center gap-4">
        <span class="selected-count text-sm font-medium text-gray-700">{{ selectedRoutes.length }} route(s) selected</span>
        <div class="bulk-actions flex items-center gap-2">
          <button 
            class="px-4 py-2 bg-blue-500 text-white text-sm font-medium rounded-lg hover:bg-blue-600 transition-colors"
            @click="bulkOptimize"
          >
            Optimize Selected
          </button>
          <button 
            class="px-4 py-2 border border-gray-300 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-50 transition-colors"
            @click="bulkExport"
          >
            Export
          </button>
          <button 
            class="px-4 py-2 text-gray-500 text-sm font-medium hover:text-gray-700 transition-colors"
            @click="clearSelection"
          >
            Clear
          </button>
        </div>
      </div>
    </transition>
    
    <!-- Create Route Modal -->
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
          :disabled="isLoading"
          class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
        >
          <div v-if="isLoading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
          {{ isLoading ? (isEditMode ? 'Updating...' : 'Creating...') : (isEditMode ? 'Update Route' : 'Create Route') }}
        </button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import BaseModal from './shared/BaseModal.vue'
import ConfirmDialog from './shared/ConfirmDialog.vue'

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
  // Updated to match database schema route status values
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

// Route Form State
const showCreateRouteModal = ref(false)
const isLoading = ref(false)
const isEditMode = ref(false)

// Delete Confirmation State
const showDeleteConfirm = ref(false)
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
  routeType: 'standard' as 'express' | 'standard' | 'priority',
  // Updated to match database schema route status values  
  status: 'Active' as 'Active' | 'Delayed' | 'Closed'
})

// Available options
const routeTypes = [
  { value: 'express', label: 'Express' },
  { value: 'standard', label: 'Standard' },
  { value: 'priority', label: 'Priority' }
]

const statusOptions = [
  { value: 'active', label: 'Active' },
  { value: 'delayed', label: 'Delayed' },
  { value: 'inactive', label: 'Inactive' }
]

// Mock Data - Updated status values to match database schema
const routes = ref<Route[]>([
  {
    id: 'RT001',
    name: 'Mumbai-Chennai Corridor',
    origin: { location: 'Mumbai', port: 'Jawaharlal Nehru Port' },
    destination: { location: 'Chennai', port: 'Chennai Port' },
    duration: 48,
    distance: 1350,
    status: 'Active', // Updated from 'active' to match DB schema
    activeShipments: 3,
    efficiencyScore: 94,
    routeType: 'express',
    createdAt: new Date('2024-01-15')
  },
  {
    id: 'RT002',
    name: 'Delhi-Kolkata Express',
    origin: { location: 'Delhi', port: 'Inland Container Depot' },
    destination: { location: 'Kolkata', port: 'Kolkata Port' },
    duration: 36,
    distance: 1480,
    status: 'Active', // Updated from 'active' to match DB schema
    activeShipments: 2,
    efficiencyScore: 87,
    routeType: 'express',
    createdAt: new Date('2024-02-01')
  },
  {
    id: 'RT003',
    name: 'Bangalore-Hyderabad Link',
    origin: { location: 'Bangalore', port: 'Bangalore ICD' },
    destination: { location: 'Hyderabad', port: 'Hyderabad Hub' },
    duration: 24,
    distance: 570,
    status: 'Delayed', // Updated from 'delayed' to match DB schema
    activeShipments: 1,
    efficiencyScore: 73,
    routeType: 'standard',
    createdAt: new Date('2024-03-10')
  },
  {
    id: 'RT004',
    name: 'Pune-Goa Coastal',
    origin: { location: 'Pune', port: 'Pune Dry Port' },
    destination: { location: 'Goa', port: 'Mormugao Port' },
    duration: 18,
    distance: 450,
    status: 'Closed', // Updated from 'inactive' to 'Closed' to match DB schema
    activeShipments: 0,
    efficiencyScore: 0,
    routeType: 'standard',
    createdAt: new Date('2024-04-05')
  }
])

const metrics = ref<RouteMetrics>({
  totalRoutes: 4,
  activeRoutes: 3,
  totalDistance: 4590,
  avgDuration: 35,
  networkEfficiency: 92,
  onTimeRate: 92
})

// Computed Properties
const optimizableRoutes = computed(() => 
  routes.value.filter(route => route.efficiencyScore < 85).length
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
  let filtered = routes.value

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
  filtered.sort((a, b) => {
    let aValue: string | number
    let bValue: string | number

    switch (sortBy.value) {
      case 'duration':
        aValue = a.duration
        bValue = b.duration
        break
      case 'distance':
        aValue = a.distance
        bValue = b.distance
        break
      case 'name':
      default:
        aValue = a.name.toLowerCase()
        bValue = b.name.toLowerCase()
        break
    }

    if (typeof aValue === 'string' && typeof bValue === 'string') {
      return sortOrder.value === 'asc' 
        ? aValue.localeCompare(bValue)
        : bValue.localeCompare(aValue)
    } else {
      return sortOrder.value === 'asc' 
        ? (aValue as number) - (bValue as number)
        : (bValue as number) - (aValue as number)
    }
  })

  return filtered
})

// Methods
const formatNumber = (num: number): string => {
  return new Intl.NumberFormat().format(num)
}

const handleMetricClick = (metric: MetricCard) => {
  console.log('Metric clicked:', metric.id)
}

const toggleFilterMenu = () => {
  showFilterMenu.value = !showFilterMenu.value
  showSortMenu.value = false
}

const toggleSortMenu = () => {
  showSortMenu.value = !showSortMenu.value
  showFilterMenu.value = false
}

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

const toggleMapView = () => {
  view.value = view.value === 'table' ? 'map' : 'table'
}

const exportRoutes = () => {
  console.log('Export routes')
}

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedRoutes.value = filteredRoutes.value.map(route => route.id)
  } else {
    selectedRoutes.value = []
  }
}

const isSelected = (routeId: string): boolean => {
  return selectedRoutes.value.includes(routeId)
}

const copyToClipboard = (text: string) => {
  navigator.clipboard.writeText(text)
}

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
  const maxDistance = Math.max(...routes.value.map(r => r.distance))
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

const getStatusBadgeClass = (status: string): string => {
  // Updated to match database schema status values (proper case)
  // Added mapping for backward compatibility with any legacy lowercase values
  const classes = {
    'Active': 'bg-gradient-to-r from-green-50 to-emerald-50 text-green-800 border-l-4 border-green-500 shadow-sm',
    'Delayed': 'bg-gradient-to-r from-red-50 to-rose-50 text-red-800 border-l-4 border-red-500 shadow-sm animate-pulse',
    'Closed': 'bg-gradient-to-r from-gray-50 to-slate-50 text-gray-600 border-l-4 border-gray-400 shadow-sm',
    // Legacy mappings for backward compatibility (remove after full migration)
    'active': 'bg-gradient-to-r from-green-50 to-emerald-50 text-green-800 border-l-4 border-green-500 shadow-sm',
    'delayed': 'bg-gradient-to-r from-red-50 to-rose-50 text-red-800 border-l-4 border-red-500 shadow-sm animate-pulse',
    'inactive': 'bg-gradient-to-r from-gray-50 to-slate-50 text-gray-600 border-l-4 border-gray-400 shadow-sm'
  }
  return classes[status as keyof typeof classes] || classes['Closed']
}

const viewRouteDetails = (route: Route) => {
  console.log('View route details:', route.id)
}

const viewRoute = (route: Route) => {
  console.log('View route:', route.id)
}

const optimizeRoute = (route: Route) => {
  console.log('Optimize route:', route.id)
}

const bulkOptimize = () => {
  console.log('Bulk optimize:', selectedRoutes.value)
  // Simulate optimization
  selectedRoutes.value.forEach(routeId => {
    const route = routes.value.find(r => r.id === routeId)
    if (route && route.status !== 'Closed') { // Updated from 'inactive' to 'Closed'
      route.efficiencyScore = Math.min(100, route.efficiencyScore + Math.floor(Math.random() * 10) + 5)
      route.lastOptimized = new Date()
    }
  })
  clearSelection()
}

const bulkExport = () => {
  console.log('Bulk export:', selectedRoutes.value)
  const selectedData = routes.value.filter(route => selectedRoutes.value.includes(route.id))
  const exportData = {
    routes: selectedData,
    exportDate: new Date().toISOString(),
    totalRoutes: selectedData.length
  }
  // Simulate file download
  const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `routes-export-${new Date().toISOString().split('T')[0]}.json`
  a.click()
  URL.revokeObjectURL(url)
}

const clearSelection = () => {
  selectedRoutes.value = []
  selectAll.value = false
}

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
    routeType: 'standard',
    status: 'Active' // Updated from 'active' to match DB schema
  }
  showCreateRouteModal.value = true
}

const closeCreateRouteModal = () => {
  showCreateRouteModal.value = false
  isEditMode.value = false
}

const saveRoute = async () => {
  if (!routeForm.value.name || !routeForm.value.originLocation || 
      !routeForm.value.destinationLocation || !routeForm.value.duration || 
      !routeForm.value.distance) {
    return
  }

  isLoading.value = true

  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))

    const newRoute: Route = {
      id: routeForm.value.id,
      name: routeForm.value.name,
      origin: {
        location: routeForm.value.originLocation,
        port: routeForm.value.originPort || `${routeForm.value.originLocation} Port`
      },
      destination: {
        location: routeForm.value.destinationLocation,
        port: routeForm.value.destinationPort || `${routeForm.value.destinationLocation} Port`
      },
      duration: routeForm.value.duration,
      distance: routeForm.value.distance,
      status: routeForm.value.status,
      activeShipments: 0,
      efficiencyScore: Math.floor(Math.random() * 20) + 80, // Random score between 80-100
      routeType: routeForm.value.routeType,
      createdAt: new Date()
    }

    if (isEditMode.value) {
      const index = routes.value.findIndex(r => r.id === routeForm.value.id)
      if (index !== -1) {
        routes.value[index] = { ...routes.value[index], ...newRoute }
      }
    } else {
      routes.value.unshift(newRoute)
      // Update metrics
      metrics.value.totalRoutes++
      if (newRoute.status === 'Active') { // Updated from 'active' to 'Active'
        metrics.value.activeRoutes++
      }
      metrics.value.totalDistance += newRoute.distance
      metrics.value.avgDuration = Math.round(
        routes.value.reduce((sum, route) => sum + route.duration, 0) / routes.value.length
      )
    }

    closeCreateRouteModal()
  } catch (error) {
    console.error('Error saving route:', error)
  } finally {
    isLoading.value = false
  }
}

const editRoute = (route: Route) => {
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
    routeType: route.routeType,
    status: route.status
  }
  showCreateRouteModal.value = true
}

const promptDeleteRoute = (route: Route) => {
  routeToDelete.value = route
  showDeleteConfirm.value = true
}

const handleConfirmDelete = () => {
  if (routeToDelete.value) {
    routes.value = routes.value.filter(r => r.id !== routeToDelete.value!.id)
    
    // Update metrics
    metrics.value.totalRoutes--
    if (routeToDelete.value.status === 'Active') { // Updated from 'active' to 'Active'
      metrics.value.activeRoutes--
    }
    metrics.value.totalDistance -= routeToDelete.value.distance
    if (routes.value.length > 0) {
      metrics.value.avgDuration = Math.round(
        routes.value.reduce((sum, route) => sum + route.duration, 0) / routes.value.length
      )
    }
    
    console.log(`Successfully deleted route: ${routeToDelete.value.id}`)
  }
  cancelDelete()
}

const cancelDelete = () => {
  showDeleteConfirm.value = false
  routeToDelete.value = null
}

// Efficiency helper functions
const getEfficiencyBadge = (): string => {
  const avgEfficiency = routes.value.reduce((sum, route) => sum + route.efficiencyScore, 0) / routes.value.length
  if (avgEfficiency >= 85) return 'bg-green-100 text-green-700'
  if (avgEfficiency >= 70) return 'bg-yellow-100 text-yellow-700'
  return 'bg-red-100 text-red-700'
}

const getEfficiencyText = (): string => {
  const avgEfficiency = routes.value.reduce((sum, route) => sum + route.efficiencyScore, 0) / routes.value.length
  if (avgEfficiency >= 85) return 'Efficient'
  if (avgEfficiency >= 70) return 'Good'
  return 'Needs Optimization'
}

// Additional helper functions for enhanced table
const getEfficiencyLabel = (score: number): string => {
  if (score >= 90) return 'Excellent'
  if (score >= 80) return 'Good'
  if (score >= 70) return 'Average'
  return 'Poor'
}

const getStatusTimestamp = (status: string): string => {
  const now = new Date()
  const timeOptions: Intl.DateTimeFormatOptions = { 
    hour: '2-digit', 
    minute: '2-digit' 
  }
  
  switch (status) {
    case 'active':
      return 'Active since ' + now.toLocaleTimeString('en-US', timeOptions)
    case 'delayed':
      return 'Delayed 2h ago'
    case 'inactive':
      return 'Stopped 1d ago'
    default:
      return ''
  }
}

const getShipmentAvatarColor = (index: number): string => {
  const colors = [
    'bg-blue-500',
    'bg-green-500', 
    'bg-purple-500',
    'bg-orange-500'
  ]
  return colors[(index - 1) % colors.length]
}

// Lifecycle
onMounted(() => {
  console.log('Route Management component mounted')
  
  // Close dropdowns when clicking outside
  const handleClickOutside = (event: Event) => {
    const target = event.target as HTMLElement
    if (!target.closest('.relative')) {
      showFilterMenu.value = false
      showSortMenu.value = false
    }
  }
  
  document.addEventListener('click', handleClickOutside)
  
  // Cleanup on unmount
  return () => {
    document.removeEventListener('click', handleClickOutside)
  }
})
</script>

<style scoped>
.route-row:hover .actions-cell .opacity-0 {
  opacity: 1;
}

.route-row:hover {
  box-shadow: inset 4px 0 0 #3B82F6;
}

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
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.metric-card {
  animation: fadeInUp 0.6s ease-out both;
}

/* Pulse animation for live indicator */
@keyframes pulse-green {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}

.animate-pulse {
  animation: pulse-green 2s infinite;
}

/* Mini bar chart animation */
@keyframes slideUp {
  from {
    height: 0;
  }
  to {
    height: var(--target-height);
  }
}

/* Smooth number transitions */
.metric-number {
  transition: all 0.3s ease-out;
}

/* Hover effects for cards */
.metric-card:hover .metric-number {
  transform: scale(1.05);
}

/* Custom scrollbar */
.table-wrapper::-webkit-scrollbar {
  width: 8px;
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

/* Enhanced table row animations */
.route-row {
  position: relative;
  transition: background-color 0.2s ease, border-color 0.2s ease;
}

.route-row:hover {
  box-shadow: inset 4px 0 0 #3B82F6;
}

/* Remove unwanted transforms that cause shaking */
.route-row:hover .actions-container {
  transform: none;
}

/* Ensure table layout is stable */
.routes-table {
  table-layout: fixed;
  border-collapse: collapse;
}

/* Status badge animations */
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
.circular-progress circle {
  transition: stroke-dashoffset 0.7s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Shipment avatars animation */
.shipment-avatars {
  transition: transform 0.2s ease-out;
}

.route-row:hover .shipment-avatars {
  transform: scale(1.1);
}

/* Action buttons animation - simplified */
.actions-container {
  transition: opacity 0.2s ease;
}

/* Remove any transform animations that cause shaking */
.shipment-avatars {
  transition: none;
}

/* Copy button tooltip */
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
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 10;
  margin-bottom: 4px;
}

/* Distance bar fill animation */
.distance-fill {
  background: linear-gradient(90deg, var(--start-color, #10B981), var(--end-color, #34D399));
  transition: width 0.7s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Enhanced status pulse for delayed routes */
@keyframes statusPulse {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.4);
  }
  50% {
    box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.1);
  }
}

.status-badge.animate-pulse {
  animation: statusPulse 2s infinite;
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

/* Search input focus animation */
.search-input:focus {
  transform: scale(1.01);
}

/* Filter chip animations */
.filter-chip {
  transition: all 0.2s ease-out;
}

.filter-chip:hover {
  transform: scale(1.05);
}

/* Rotate animation for sort arrows */
.rotate-180 {
  transform: rotate(180deg);
}
</style>