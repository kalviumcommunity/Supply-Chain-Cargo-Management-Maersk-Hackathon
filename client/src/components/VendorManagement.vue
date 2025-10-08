<template>
  <div class="space-y-10 animate-fade-in bg-[#F5F5F7] min-h-screen p-10">
    <!-- Page Header -->
    <div class="flex justify-between items-start">
      <div class="flex items-start gap-4">
        <div class="w-14 h-14 rounded-full bg-[#EFF6FF] flex items-center justify-center">
          <Users class="w-7 h-7 text-[#3B82F6]" />
        </div>
        <div>
          <h1 class="text-[36px] font-bold text-[#0F172A] leading-tight tracking-tight">Vendor Management</h1>
          <p class="text-[#64748B] text-base leading-relaxed mt-1">Manage partnerships and coordinate with logistics vendors.</p>
        </div>
      </div>
      <button 
        @click="openCreateVendorModal"
        class="bg-gradient-to-br from-[#3B82F6] to-[#2563EB] text-white px-7 py-[14px] rounded-xl hover:scale-102 active:scale-98 hover:shadow-[0_8px_24px_rgba(59,130,246,0.4)] shadow-[0_4px_12px_rgba(59,130,246,0.3)] transition-all duration-200 flex items-center gap-3 font-semibold text-[15px]"
      >
        <Plus class="w-5 h-5" />
        Add Vendor
      </button>
    </div>

    <!-- Metrics Cards Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
      <div 
        v-for="(metric, index) in vendorMetrics" 
        :key="metric.id"
        class="bg-white rounded-[24px] p-7 shadow-[0_1px_3px_rgba(0,0,0,0.03),0_8px_16px_rgba(0,0,0,0.04)] hover:shadow-[0_8px_24px_rgba(0,0,0,0.12)] hover:-translate-y-1 transition-all duration-300 cursor-pointer group"
        :style="{ animationDelay: `${index * 100}ms` }"
      >
        <!-- Icon Circle -->
        <div class="flex items-start justify-between mb-6">
          <div :class="['w-14 h-14 rounded-full flex items-center justify-center shadow-sm', metric.iconBgColor]">
            <component :is="metric.icon" :class="['w-7 h-7', metric.iconColor]" />
          </div>
        </div>
        
        <!-- Number and Label -->
        <div class="mb-4">
          <div class="text-[48px] font-bold text-[#0F172A] leading-none tracking-tight mb-2">{{ metric.value }}</div>
          <div class="text-[14px] font-medium text-[#6B7280] mb-1">{{ metric.label }}</div>
          <div class="text-[12px] text-[#9CA3AF]">{{ metric.subtitle }}</div>
        </div>
        
        <!-- Badge/Additional Info -->
        <div v-if="metric.badge" :class="['inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold', metric.badgeClass]">
          <component :is="metric.badgeIcon" class="w-3.5 h-3.5" />
          {{ metric.badge }}
        </div>
      </div>
    </div>

    <!-- Search and Filters -->
    <div class="bg-white rounded-[24px] p-8 shadow-[0_1px_3px_rgba(0,0,0,0.03),0_8px_16px_rgba(0,0,0,0.04)]">
      <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
        <!-- Search Input -->
        <div class="relative flex-1 max-w-md">
          <Search class="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-[#9CA3AF]" />
          <input 
            v-model="searchQuery"
            type="text" 
            placeholder="Search vendors by name, ID, service type, or contact..."
            class="w-full pl-12 pr-4 py-4 border border-[#E5E7EB] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#3B82F6] focus:border-transparent transition-all duration-200 text-[15px]"
          />
        </div>
        
        <!-- Filter and Sort Buttons -->
        <div class="flex items-center gap-4">
          <button class="flex items-center gap-2 px-5 py-3 border border-[#E5E7EB] rounded-xl hover:border-[#3B82F6] hover:text-[#3B82F6] transition-colors font-medium text-[#6B7280]">
            <Filter class="w-4 h-4" />
            Filter
          </button>
          <button class="flex items-center gap-2 px-5 py-3 border border-[#E5E7EB] rounded-xl hover:border-[#3B82F6] hover:text-[#3B82F6] transition-colors font-medium text-[#6B7280]">
            <SortDesc class="w-4 h-4" />
            Sort
          </button>
        </div>
      </div>
    </div>

    <!-- Vendors Grid -->
    <div v-if="filteredVendors.length > 0">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-bold text-[#0F172A]">All Vendors</h2>
        <span class="text-[#64748B] text-sm">{{ filteredVendors.length }} vendor{{ filteredVendors.length !== 1 ? 's' : '' }} found</span>
      </div>
      
      <!-- Responsive Grid: 3 columns desktop, 2 tablet, 1 mobile -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="vendor in filteredVendors" 
          :key="vendor.id"
          class="vendor-card bg-white rounded-[20px] p-7 min-h-[320px] shadow-[0_1px_3px_rgba(0,0,0,0.03),0_8px_16px_rgba(0,0,0,0.04)] hover:shadow-[0_12px_32px_rgba(0,0,0,0.12)] hover:-translate-y-1 hover:border-[#E0E7FF] transition-all duration-300 cursor-pointer group relative border border-transparent"
          :class="[
            selectedVendor === vendor.id ? 'border-[#3B82F6] ring-2 ring-[#3B82F6]/20' : '',
            vendor.rating >= 4.8 ? 'border-[#F59E0B] border-2' : ''
          ]"
          @click="handleVendorClick(vendor)"
        >
          <!-- Premium Partner Ribbon -->
          <div 
            v-if="vendor.rating >= 4.8" 
            class="absolute -top-0 -left-0 bg-gradient-to-r from-[#F59E0B] to-[#D97706] text-white text-[10px] font-bold uppercase tracking-wide px-3 py-1 rounded-tl-[20px] rounded-br-xl"
          >
            Premium Partner
          </div>

          <!-- Card Header -->
          <div class="flex items-start justify-between mb-6">
            <div class="flex-1">
              <!-- Vendor Logo -->
              <div class="mb-4">
                <div 
                  v-if="vendor.logo" 
                  class="w-12 h-12 rounded-lg overflow-hidden"
                >
                  <img :src="vendor.logo" :alt="vendor.name" class="w-full h-full object-cover" />
                </div>
                <div 
                  v-else 
                  class="w-12 h-12 rounded-lg bg-gradient-to-br from-[#3B82F6] to-[#2563EB] flex items-center justify-center shadow-sm"
                >
                  <span class="text-white font-bold text-[18px]">{{ getInitials(vendor.name) }}</span>
                </div>
              </div>

              <!-- Vendor Name -->
              <h3 class="text-[18px] font-semibold text-[#1E293B] leading-tight mb-1 group-hover:text-[#3B82F6] transition-colors">
                {{ vendor.name }}
              </h3>

              <!-- Vendor ID -->
              <p class="text-[12px] text-[#94A3B8] font-mono tracking-wide">
                {{ vendor.id }}
              </p>
            </div>

            <!-- Rating Badge -->
            <div class="bg-[rgba(254,243,199,0.3)] px-3 py-1.5 rounded-lg flex items-center gap-2">
              <Star class="w-4 h-4 text-[#F59E0B] fill-current" />
              <span class="text-[16px] font-bold text-[#92400E]">{{ vendor.rating }}</span>
            </div>
          </div>

          <!-- Service Type Badge -->
          <div class="mb-6">
            <div :class="['inline-flex items-center gap-2 px-3 py-2 rounded-lg text-[13px] font-medium', getServiceBadgeClass(vendor.serviceType)]">
              <component :is="getServiceIcon(vendor.serviceType)" class="w-4 h-4" />
              {{ vendor.serviceType }}
            </div>
          </div>

          <!-- Contact Info -->
          <div class="space-y-3 mb-6">
            <div class="flex items-center gap-3">
              <Phone class="w-4 h-4 text-[#6B7280]" />
              <a 
                :href="`tel:${vendor.contact.phone || vendor.contact}`" 
                class="text-[13px] text-[#6B7280] hover:text-[#3B82F6] transition-colors"
              >
                {{ vendor.contact.phone || vendor.contact }}
              </a>
            </div>
            <div class="flex items-center gap-3">
              <Mail class="w-4 h-4 text-[#6B7280]" />
              <a 
                :href="`mailto:${vendor.contact.email}`" 
                class="text-[13px] text-[#6B7280] hover:text-[#3B82F6] transition-colors truncate"
              >
                {{ vendor.contact.email }}
              </a>
            </div>
          </div>

          <!-- Active Shipments -->
          <div class="flex items-center justify-between mb-6">
            <div class="flex items-center gap-2">
              <Package class="w-4 h-4 text-[#6B7280]" />
              <span class="text-[13px] text-[#6B7280]">Active Shipments</span>
            </div>
            <span class="text-[16px] font-bold text-[#1F2937]">{{ vendor.activeShipments }}</span>
          </div>

          <!-- Action Button -->
          <div class="flex items-center justify-between">
            <div class="relative">
              <button 
                @click.stop="toggleQuickActions(vendor.id)"
                class="p-2 rounded-lg hover:bg-[#F3F4F6] transition-colors"
              >
                <MoreHorizontal class="w-4 h-4 text-[#6B7280]" />
              </button>

              <!-- Quick Actions Dropdown -->
              <transition name="dropdown">
                <div 
                  v-if="showQuickActions === vendor.id" 
                  class="absolute bottom-full left-0 mb-2 w-48 bg-white rounded-lg shadow-lg border border-[#E5E7EB] py-2 z-10"
                  @click.stop
                >
                  <button 
                    v-for="action in quickActions" 
                    :key="action.id"
                    @click="handleQuickAction(action, vendor)"
                    :class="[
                      'w-full px-3 py-2 text-left text-sm hover:bg-[#F9FAFB] transition-colors flex items-center gap-2',
                      action.danger ? 'text-[#EF4444] hover:bg-[#FEF2F2]' : 'text-[#374151]'
                    ]"
                  >
                    <component :is="action.icon" class="w-4 h-4" />
                    {{ action.label }}
                  </button>
                </div>
              </transition>
            </div>

            <button 
              @click.stop="openVendorDetails(vendor)"
              class="flex items-center gap-2 px-3 py-1.5 border border-[#E5E7EB] rounded-lg text-[13px] font-medium text-[#374151] hover:border-[#3B82F6] hover:bg-[#3B82F6] hover:text-white transition-all duration-200"
            >
              <Eye class="w-4 h-4" />
              View Details
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-16">
      <div class="flex justify-center mb-6">
        <div class="w-16 h-16 rounded-full bg-[#F1F5F9] flex items-center justify-center">
          <Users class="w-8 h-8 text-[#CBD5E1]" />
        </div>
      </div>
      <h3 class="text-xl font-semibold text-[#1E293B] mb-2">No vendors found</h3>
      <p class="text-[#64748B] mb-6">Add your first logistics partner to get started</p>
      <button 
        @click="showAddVendorModal = true"
        class="bg-gradient-to-br from-[#3B82F6] to-[#2563EB] text-white px-6 py-3 rounded-xl hover:scale-102 active:scale-98 hover:shadow-[0_8px_24px_rgba(59,130,246,0.4)] shadow-[0_4px_12px_rgba(59,130,246,0.3)] transition-all duration-200 flex items-center gap-3 font-semibold text-[15px] mx-auto"
      >
        <Plus class="w-5 h-5" />
        Add Vendor
      </button>
    </div>

    <!-- Vendor Details Slide-over Panel -->
    <transition name="backdrop">
      <div 
        v-if="showVendorDetails" 
        class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50"
        @click="closeVendorDetails"
      ></div>
    </transition>

    <transition name="slide-over">
      <div 
        v-if="showVendorDetails" 
        class="fixed right-0 top-0 h-full w-[400px] bg-white shadow-xl z-50 flex flex-col"
        @click.stop
      >
        <!-- Header -->
        <div class="p-6 border-b border-[#E5E7EB] bg-gradient-to-r from-[#F8FAFC] to-white">
          <div class="flex items-start justify-between mb-4">
            <div class="flex items-start gap-4">
              <!-- Vendor Logo -->
              <div class="w-12 h-12 rounded-lg bg-gradient-to-br from-[#3B82F6] to-[#2563EB] flex items-center justify-center shadow-sm">
                <span class="text-white font-bold text-lg">{{ getInitials(selectedVendorDetails?.name) }}</span>
              </div>
              
              <!-- Vendor Info -->
              <div>
                <h2 class="text-xl font-bold text-[#1E293B] leading-tight">{{ selectedVendorDetails?.name }}</h2>
                <p class="text-[#64748B] text-sm">{{ selectedVendorDetails?.id }}</p>
                
                <!-- Rating Display -->
                <div class="flex items-center gap-2 mt-2">
                  <div class="flex gap-1">
                    <Star v-for="star in 5" :key="star" 
                          :class="star <= Math.floor(selectedVendorDetails?.rating || 0) ? 'text-[#F59E0B] fill-current' : 'text-[#E5E7EB]'" 
                          class="w-4 h-4" />
                  </div>
                  <span class="text-[#1F2937] font-semibold">{{ selectedVendorDetails?.rating }}</span>
                </div>
              </div>
            </div>
            
            <!-- Close Button -->
            <button 
              @click="closeVendorDetails"
              class="w-8 h-8 rounded-lg hover:bg-[#F3F4F6] flex items-center justify-center transition-colors"
            >
              <X class="w-5 h-5 text-[#6B7280]" />
            </button>
          </div>
        </div>

        <!-- Tab Navigation -->
        <div class="border-b border-[#E5E7EB] px-6">
          <nav class="flex space-x-8">
            <button 
              v-for="tab in detailTabs" 
              :key="tab.id"
              @click="activeDetailTab = tab.id"
              :class="[
                'py-3 px-1 text-sm font-medium border-b-2 transition-colors',
                activeDetailTab === tab.id 
                  ? 'border-blue-500 text-blue-600' 
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              ]"
            >
              <component :is="tab.icon" class="w-4 h-4 inline mr-2" />
              {{ tab.label }}
            </button>
          </nav>
        </div>

        <!-- Tab Content -->
        <div class="flex-1 overflow-y-auto custom-scrollbar">
          <!-- Overview Tab -->
          <div v-if="activeDetailTab === 'overview'" class="p-6 space-y-6">
            <!-- Metrics Cards -->
            <div class="grid grid-cols-2 gap-4">
              <div class="metric-card bg-[#F8FAFC] rounded-xl p-4 text-center">
                <div class="text-2xl font-bold text-[#1F2937] mb-1">42</div>
                <div class="text-xs text-[#6B7280] uppercase tracking-wide">Total Shipments</div>
              </div>
              <div class="metric-card bg-[#F8FAFC] rounded-xl p-4 text-center">
                <div class="text-2xl font-bold text-[#1F2937] mb-1">96%</div>
                <div class="text-xs text-[#6B7280] uppercase tracking-wide">Satisfaction</div>
              </div>
            </div>

            <!-- On-time Delivery Circular Progress -->
            <div class="bg-[#F8FAFC] rounded-xl p-6">
              <h3 class="text-sm font-semibold text-[#374151] mb-4">On-time Delivery Rate</h3>
              <div class="flex items-center justify-center">
                <div class="relative w-24 h-24">
                  <svg class="w-24 h-24 transform -rotate-90" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="40" stroke="#E5E7EB" stroke-width="8" fill="none" />
                    <circle 
                      cx="50" cy="50" r="40" 
                      stroke="#10B981" 
                      stroke-width="8" 
                      fill="none"
                      stroke-linecap="round"
                      stroke-dasharray="251.2"
                      stroke-dashoffset="15.07"
                      class="transition-all duration-1000 ease-out"
                    />
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <div class="text-center">
                      <div class="text-lg font-bold text-[#1F2937]">94%</div>
                      <div class="text-xs text-[#6B7280]">On-time</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Recent Shipments -->
            <div>
              <h3 class="text-sm font-semibold text-[#374151] mb-3">Recent Shipments</h3>
              <div class="space-y-3">
                <div 
                  v-for="shipment in recentShipments" 
                  :key="shipment.id"
                  class="bg-white border border-[#E5E7EB] rounded-lg p-3 hover:border-[#3B82F6] transition-colors"
                >
                  <div class="flex items-center justify-between mb-2">
                    <span class="font-mono text-sm text-[#3B82F6]">{{ shipment.id }}</span>
                    <div :class="['px-2 py-1 rounded text-xs font-medium', getShipmentStatusClass(shipment.status)]">
                      {{ shipment.status }}
                    </div>
                  </div>
                  <div class="text-sm text-[#6B7280]">{{ shipment.route }}</div>
                  <div class="text-xs text-[#9CA3AF] mt-1">{{ shipment.date }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Performance Tab -->
          <div v-if="activeDetailTab === 'performance'" class="p-6 space-y-6">
            <!-- Performance Chart Placeholder -->
            <div class="chart-animate bg-[#F8FAFC] rounded-xl p-6">
              <h3 class="text-sm font-semibold text-[#374151] mb-4">Monthly Performance</h3>
              <div class="h-48 flex items-center justify-center border-2 border-dashed border-[#D1D5DB] rounded-lg bg-white">
                <div class="text-center">
                  <TrendingUp class="w-8 h-8 text-[#9CA3AF] mx-auto mb-2" />
                  <p class="text-sm text-[#6B7280] font-medium">Performance Chart</p>
                  <p class="text-xs text-[#9CA3AF]">Chart.js integration will display monthly trends</p>
                </div>
              </div>
            </div>

            <!-- Comparison Bars -->
            <div class="space-y-4">
              <h3 class="text-sm font-semibold text-[#374151]">vs Network Average</h3>
              <div class="space-y-3">
                <div v-for="(metric, index) in performanceMetrics" :key="metric.label" class="performance-metric space-y-2">
                  <div class="flex justify-between text-sm">
                    <span class="text-[#6B7280]">{{ metric.label }}</span>
                    <div class="flex items-center gap-2">
                      <span class="font-semibold text-[#1F2937]">{{ metric.value }}%</span>
                      <component :is="metric.trend === 'up' ? TrendingUp : TrendingDown" 
                                 :class="metric.trend === 'up' ? 'text-[#10B981]' : 'text-[#EF4444]'" 
                                 class="w-4 h-4" />
                    </div>
                  </div>
                  <div class="w-full bg-[#E5E7EB] rounded-full h-2">
                    <div 
                      :class="[
                        'h-2 rounded-full transition-all duration-1000 ease-out progress-bar',
                        metric.value >= 70 ? 'bg-[#10B981]' : metric.value >= 50 ? 'bg-[#F59E0B]' : 'bg-[#EF4444]'
                      ]"
                      :style="{ width: `${metric.value}%`, animationDelay: `${index * 100}ms` }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- History Tab -->
          <div v-if="activeDetailTab === 'history'" class="p-6">
            <h3 class="text-sm font-semibold text-[#374151] mb-4">Shipment History</h3>
            
            <!-- Timeline -->
            <div class="relative">
              <div class="absolute left-5 top-0 bottom-0 w-0.5 bg-[#E5E7EB]"></div>
              
              <div class="space-y-4">
                <div 
                  v-for="(event, index) in shipmentHistory" 
                  :key="event.id"
                  class="timeline-item relative flex items-start gap-4 pl-12"
                  :style="{ animationDelay: `${index * 50}ms` }"
                >
                  <div :class="['absolute left-2 w-2.5 h-2.5 rounded-full border-2 border-white shadow-sm', getEventDotColor(event.type)]"></div>
                  
                  <div class="flex-1 bg-white border border-[#E5E7EB] rounded-lg p-3 hover:border-[#3B82F6] transition-colors">
                    <div class="flex items-center justify-between mb-1">
                      <span class="font-mono text-sm text-[#3B82F6]">{{ event.shipmentId }}</span>
                      <span class="text-xs text-[#9CA3AF]">{{ event.timestamp }}</span>
                    </div>
                    <p class="text-sm text-[#6B7280]">{{ event.description }}</p>
                    <div v-if="event.status" :class="['inline-block px-2 py-1 rounded text-xs font-medium mt-2', getShipmentStatusClass(event.status)]">
                      {{ event.status }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Contact Tab -->
          <div v-if="activeDetailTab === 'contact'" class="p-6 space-y-6">
            <!-- Contact Information Grid -->
            <div class="grid gap-4">
              <!-- Primary Contact -->
              <div class="bg-[#F8FAFC] rounded-xl p-4 border-l-4 border-[#3B82F6]">
                <div class="flex items-center justify-between mb-3">
                  <h3 class="text-sm font-semibold text-[#374151]">Primary Contact</h3>
                  <span class="px-2 py-1 bg-[#3B82F6] text-white text-xs font-medium rounded-full">Primary</span>
                </div>
                <div class="space-y-3">
                  <div>
                    <div class="text-sm font-medium text-[#1F2937] mb-1">John Smith</div>
                    <div class="text-xs text-[#6B7280]">Operations Manager</div>
                  </div>
                  <div class="flex items-center gap-3">
                    <Phone class="w-4 h-4 text-[#3B82F6]" />
                    <span class="text-sm">{{ selectedVendorDetails?.contact?.phone }}</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <Mail class="w-4 h-4 text-[#3B82F6]" />
                    <span class="text-sm">{{ selectedVendorDetails?.contact?.email }}</span>
                  </div>
                </div>
              </div>

              <!-- Secondary Contact -->
              <div class="bg-[#F8FAFC] rounded-xl p-4 border-l-4 border-[#F59E0B]">
                <div class="flex items-center justify-between mb-3">
                  <h3 class="text-sm font-semibold text-[#374151]">Secondary Contact</h3>
                  <span class="px-2 py-1 bg-[#F59E0B] text-white text-xs font-medium rounded-full">Secondary</span>
                </div>
                <div class="space-y-3">
                  <div>
                    <div class="text-sm font-medium text-[#1F2937] mb-1">Sarah Johnson</div>
                    <div class="text-xs text-[#6B7280]">Customer Service Lead</div>
                  </div>
                  <div class="flex items-center gap-3">
                    <Phone class="w-4 h-4 text-[#F59E0B]" />
                    <span class="text-sm">+91 98765 43211</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <Mail class="w-4 h-4 text-[#F59E0B]" />
                    <span class="text-sm">sarah@expresslogistics.com</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Business Hours -->
            <div class="bg-[#F8FAFC] rounded-xl p-4">
              <h3 class="text-sm font-semibold text-[#374151] mb-3">Business Hours</h3>
              <div class="space-y-2 text-sm">
                <div class="flex justify-between items-center">
                  <span class="text-[#6B7280]">Monday - Friday</span>
                  <span class="font-medium text-[#1F2937]">9:00 AM - 6:00 PM</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-[#6B7280]">Saturday</span>
                  <span class="font-medium text-[#1F2937]">9:00 AM - 2:00 PM</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-[#6B7280]">Sunday</span>
                  <span class="font-medium text-[#EF4444]">Closed</span>
                </div>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="space-y-3">
              <button 
                @click="messageVendor(selectedVendorDetails)"
                class="action-button w-full bg-[#3B82F6] text-white py-3 rounded-xl hover:bg-[#2563EB] transition-all flex items-center justify-center gap-2 font-medium"
              >
                <MessageSquare class="w-4 h-4" />
                Send Message
              </button>
              <button 
                :href="`tel:${selectedVendorDetails?.contact?.phone}`"
                class="action-button w-full border border-[#E5E7EB] text-[#374151] py-3 rounded-xl hover:border-[#3B82F6] hover:text-[#3B82F6] transition-all flex items-center justify-center gap-2 font-medium"
              >
                <Phone class="w-4 h-4" />
                Call Now
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Create Vendor Modal -->
    <BaseModal :show="showCreateVendorModal" @close="closeCreateVendorModal" max-width="lg">
      <template #header>
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
            <Plus class="w-5 h-5 text-blue-600" />
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900">{{ isEditMode ? 'Edit Vendor' : 'Add New Vendor' }}</h2>
            <p class="text-sm text-gray-500">{{ isEditMode ? 'Update vendor information' : 'Add a new vendor to your network' }}</p>
          </div>
        </div>
      </template>
      
      <template #body>
        <form @submit.prevent="saveVendor" class="space-y-6">
          <!-- Vendor ID -->
          <div>
            <label for="vendorId" class="block text-sm font-medium text-gray-700 mb-2">
              Vendor ID
            </label>
            <input
              id="vendorId"
              v-model="vendorForm.id"
              type="text"
              readonly
              class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 font-mono text-sm"
            />
          </div>

          <!-- Vendor Name -->
          <div>
            <label for="vendorName" class="block text-sm font-medium text-gray-700 mb-2">
              Vendor Name <span class="text-red-500">*</span>
            </label>
            <input
              id="vendorName"
              v-model="vendorForm.name"
              type="text"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="e.g., Express Logistics Co."
            />
          </div>

          <!-- Service Type -->
          <div>
            <label for="serviceType" class="block text-sm font-medium text-gray-700 mb-2">
              Service Type <span class="text-red-500">*</span>
            </label>
            <select
              id="serviceType"
              v-model="vendorForm.serviceType"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Select service type</option>
              <option v-for="service in serviceTypes" :key="service" :value="service">{{ service }}</option>
            </select>
          </div>

          <!-- Contact Information -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label for="contactPhone" class="block text-sm font-medium text-gray-700 mb-2">
                Phone Number <span class="text-red-500">*</span>
              </label>
              <input
                id="contactPhone"
                v-model="vendorForm.contactPhone"
                type="tel"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="+91 98765 43210"
              />
            </div>
            <div>
              <label for="contactEmail" class="block text-sm font-medium text-gray-700 mb-2">
                Email Address <span class="text-red-500">*</span>
              </label>
              <input
                id="contactEmail"
                v-model="vendorForm.contactEmail"
                type="email"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="info@vendor.com"
              />
            </div>
          </div>

          <!-- Rating and Status -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label for="rating" class="block text-sm font-medium text-gray-700 mb-2">
                Initial Rating
              </label>
              <select
                id="rating"
                v-model.number="vendorForm.rating"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option :value="5.0">5.0 ⭐⭐⭐⭐⭐</option>
                <option :value="4.5">4.5 ⭐⭐⭐⭐☆</option>
                <option :value="4.0">4.0 ⭐⭐⭐⭐</option>
                <option :value="3.5">3.5 ⭐⭐⭐☆</option>
                <option :value="3.0">3.0 ⭐⭐⭐</option>
              </select>
            </div>
            <div>
              <label for="status" class="block text-sm font-medium text-gray-700 mb-2">
                Status
              </label>
              <select
                id="status"
                v-model="vendorForm.status"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="Active">Active</option>
                <option value="Inactive">Inactive</option>
              </select>
            </div>
          </div>
        </form>
      </template>
      
      <template #footer>
        <button
          type="button"
          @click="closeCreateVendorModal"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
        >
          Cancel
        </button>
        <button
          type="button"
          @click="saveVendor"
          :disabled="isLoading"
          class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
        >
          <div v-if="isLoading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
          {{ isLoading ? (isEditMode ? 'Updating...' : 'Adding...') : (isEditMode ? 'Update Vendor' : 'Add Vendor') }}
        </button>
      </template>
    </BaseModal>

    <!-- Delete Confirmation Dialog -->
    <ConfirmDialog
      :show="showDeleteConfirm"
      title="Delete Vendor"
      :message="`Are you sure you want to delete vendor ${vendorToDelete?.name}? This action cannot be undone.`"
      confirm-text="Delete"
      cancel-text="Cancel"
      variant="danger"
      @confirm="handleConfirmDeleteVendor"
      @cancel="cancelDeleteVendor"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { 
  Users, Building, Layers, Star, Award, Plus, Search, Filter, SortDesc,
  TrendingUp, TrendingDown, Truck, Ship, Plane, Train, Phone, Mail, Package, 
  Eye, MoreHorizontal, X, Edit, Send, Download, UserX, Trash2, MessageSquare,
  BarChart3, History, User, Clock
} from 'lucide-vue-next'

import BaseModal from './shared/BaseModal.vue'
import ConfirmDialog from './shared/ConfirmDialog.vue'

// Reactive data
const showAddVendorModal = ref(false)
const showVendorDetails = ref(false)
const selectedVendorDetails = ref(null)
const selectedVendor = ref(null)
const searchQuery = ref('')
const showQuickActions = ref(null)
const activeDetailTab = ref('overview')

// Vendor Form State
const showCreateVendorModal = ref(false)
const isLoading = ref(false)
const isEditMode = ref(false)

// Delete Confirmation State
const showDeleteConfirm = ref(false)
const vendorToDelete = ref(null)

// Form Data
const vendorForm = ref({
  id: '',
  name: '',
  serviceType: '',
  contactPhone: '',
  contactEmail: '',
  rating: 5.0,
  status: 'Active'
})

// Available options
const serviceTypes = [
  'Road Transport',
  'Sea Transport', 
  'Air Transport',
  'Rail Transport',
  'Warehousing',
  'Last Mile Delivery'
]

// Vendor metrics for dashboard cards
const vendorMetrics = reactive([
  {
    id: 'totalVendors',
    icon: Building,
    iconBgColor: 'bg-[#EFF6FF]',
    iconColor: 'text-[#3B82F6]',
    value: '24',
    label: 'Total Vendors',
    subtitle: 'Active partnerships',
    badge: 'Active',
    badgeClass: 'bg-[#D1FAE5] text-[#065F46]',
    badgeIcon: Award
  },
  {
    id: 'serviceTypes',
    icon: Layers,
    iconBgColor: 'bg-[#F0FDF4]',
    iconColor: 'text-[#16A34A]',
    value: '4',
    label: 'Service Types',
    subtitle: 'Transport modes',
    badge: 'Complete',
    badgeClass: 'bg-[#DBEAFE] text-[#1E40AF]',
    badgeIcon: Award
  },
  {
    id: 'avgRating',
    icon: Star,
    iconBgColor: 'bg-[#FFFBEB]',
    iconColor: 'text-[#D97706]',
    value: '4.7',
    label: 'Average Rating',
    subtitle: 'Vendor performance',
    badge: '5★ Vendors',
    badgeClass: 'bg-[#FEF3C7] text-[#92400E]',
    badgeIcon: Star
  },
  {
    id: 'onTimeRate',
    icon: TrendingUp,
    iconBgColor: 'bg-[#ECFDF5]',
    iconColor: 'text-[#059669]',
    value: '94%',
    label: 'On-time Rate',
    subtitle: 'Delivery performance',
    badge: '+2.3%',
    badgeClass: 'bg-[#D1FAE5] text-[#065F46]',
    badgeIcon: TrendingUp
  }
])

// Sample Vendor Data
const vendors = reactive([
  {
    id: 'VEN001',
    name: 'Express Logistics Co.',
    logo: null,
    serviceType: 'Road Transport',
    rating: 4.8,
    contact: {
      phone: '+91 98765 43210',
      email: 'info@expresslogistics.com'
    },
    activeShipments: 12,
    status: 'Active'
  },
  {
    id: 'VEN002',
    name: 'Ocean Freight Solutions',
    logo: null,
    serviceType: 'Sea Transport',
    rating: 4.6,
    contact: {
      phone: '+91 87654 32109',
      email: 'info@oceanfreight.com'
    },
    activeShipments: 8,
    status: 'Active'
  },
  {
    id: 'VEN003',
    name: 'AirCargo Express',
    logo: null,
    serviceType: 'Air Transport',
    rating: 4.9,
    contact: {
      phone: '+91 76543 21098',
      email: 'support@aircargo.com'
    },
    activeShipments: 15,
    status: 'Active'
  },
  {
    id: 'VEN004',
    name: 'Rail Connect India',
    logo: null,
    serviceType: 'Combined',
    rating: 4.2,
    contact: {
      phone: '+91 65432 10987',
      email: 'support@railconnect.in'
    },
    activeShipments: 5,
    status: 'Pending'
  }
])

// Quick Actions configuration
const quickActions = [
  { id: 'edit', label: 'Edit Vendor', icon: Edit, action: 'edit' },
  { id: 'assign', label: 'Assign Shipment', icon: Package, action: 'assign-shipment' },
  { id: 'message', label: 'Send Message', icon: MessageSquare, action: 'message' },
  { id: 'download', label: 'Download Report', icon: Download, action: 'download-report' },
  { id: 'inactive', label: 'Mark Inactive', icon: UserX, action: 'mark-inactive' },
  { id: 'delete', label: 'Delete', icon: Trash2, action: 'delete', danger: true }
]

// Detail tabs configuration
const detailTabs = [
  { id: 'overview', label: 'Overview', icon: Eye },
  { id: 'performance', label: 'Performance', icon: BarChart3 },
  { id: 'history', label: 'History', icon: History },
  { id: 'contact', label: 'Contact', icon: User }
]

// Mock data for details panel
const performanceMetrics = [
  { label: 'On-time Delivery', value: 94, trend: 'up' },
  { label: 'Customer Satisfaction', value: 96, trend: 'up' },
  { label: 'Cost Efficiency', value: 87, trend: 'down' },
  { label: 'Communication', value: 92, trend: 'up' }
]

const recentShipments = [
  { id: 'SH001', route: 'Mumbai → Delhi', status: 'Delivered', date: '2 days ago' },
  { id: 'SH002', route: 'Chennai → Bangalore', status: 'In Transit', date: '1 day ago' },
  { id: 'SH003', route: 'Kolkata → Hyderabad', status: 'Delivered', date: '3 days ago' },
  { id: 'SH004', route: 'Pune → Ahmedabad', status: 'Delayed', date: '4 hours ago' }, // Changed from 'Picked Up' to 'Delayed'
  { id: 'SH005', route: 'Jaipur → Lucknow', status: 'Created', date: '1 hour ago' }
]

const shipmentHistory = [
  { id: 1, shipmentId: 'SH001', description: 'Shipment delivered successfully', status: 'Delivered', timestamp: '2 days ago', type: 'delivered' },
  { id: 2, shipmentId: 'SH002', description: 'Package in transit to destination', status: 'In Transit', timestamp: '1 day ago', type: 'transit' },
  { id: 3, shipmentId: 'SH003', description: 'Shipment delayed due to weather conditions', status: 'Delayed', timestamp: '3 days ago', type: 'delayed' }, // Changed from 'Picked Up' to 'Delayed'
  { id: 4, shipmentId: 'SH004', description: 'Shipment created and scheduled', status: 'Created', timestamp: '4 days ago', type: 'created' }
]

// Computed property for filtered vendors
const filteredVendors = computed(() => {
  if (!searchQuery.value) return vendors
  
  return vendors.filter(vendor => 
    vendor.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    vendor.id.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    vendor.serviceType.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    vendor.contact.phone?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    vendor.contact.email?.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// Helper functions
const getInitials = (name) => {
  if (!name) return 'VN'
  return name
    .split(' ')
    .map(word => word.charAt(0))
    .join('')
    .substring(0, 2)
    .toUpperCase()
}

const getServiceIcon = (serviceType) => {
  switch (serviceType.toLowerCase()) {
    case 'road transport':
      return Truck
    case 'sea transport':
      return Ship
    case 'air transport':
      return Plane
    case 'combined':
      return Layers
    default:
      return Truck
  }
}

const getServiceBadgeClass = (serviceType) => {
  switch (serviceType.toLowerCase()) {
    case 'road transport':
      return 'bg-[#DBEAFE] text-[#1E40AF]'
    case 'sea transport':
      return 'bg-[#CCFBF1] text-[#115E59]'
    case 'air transport':
      return 'bg-[#FED7AA] text-[#9A3412]'
    case 'combined':
      return 'bg-[#D1FAE5] text-[#065F46]'
    default:
      return 'bg-[#DBEAFE] text-[#1E40AF]'
  }
}

const getShipmentStatusClass = (status) => {
  // Updated to match database schema shipment status values
  // Removed 'Picked Up' as it's not supported in current database schema
  switch (status) {
    case 'Delivered': return 'bg-[#D1FAE5] text-[#065F46]'
    case 'In Transit': return 'bg-[#DBEAFE] text-[#1E40AF]'
    // case 'Picked Up': return 'bg-[#E0F2FE] text-[#0C4A6E]' // Not in DB schema
    case 'Created': return 'bg-[#F3F4F6] text-[#374151]'
    case 'Delayed': return 'bg-[#FEF2F2] text-[#991B1B]' // Added Delayed status
    default: return 'bg-[#F3F4F6] text-[#374151]'
  }
}

const getEventDotColor = (type) => {
  switch (type) {
    case 'delivered': return 'bg-[#10B981]'
    case 'transit': return 'bg-[#3B82F6]'
    case 'delayed': return 'bg-[#EF4444]' // Updated from 'pickup' to 'delayed'
    case 'created': return 'bg-[#6B7280]'
    default: return 'bg-[#6B7280]'
  }
}

// Event handlers
const handleVendorClick = (vendor) => {
  selectedVendor.value = selectedVendor.value === vendor.id ? null : vendor.id
}

const toggleQuickActions = (vendorId) => {
  showQuickActions.value = showQuickActions.value === vendorId ? null : vendorId
}

const handleQuickAction = (action, vendor) => {
  showQuickActions.value = null
  
  switch (action.action) {
    case 'edit':
      editVendor(vendor)
      break
    case 'assign-shipment':
      console.log('Assign shipment to vendor:', vendor.id)
      break
    case 'message':
      messageVendor(vendor)
      break
    case 'download-report':
      console.log('Download report for vendor:', vendor.id)
      break
    case 'mark-inactive':
      vendor.status = 'Inactive'
      console.log('Marked vendor as inactive:', vendor.id)
      break
    case 'delete':
      promptDeleteVendor(vendor)
      break
    default:
      console.log('Quick action:', action.action, vendor)
  }
}

const openVendorDetails = (vendor) => {
  selectedVendorDetails.value = vendor
  showVendorDetails.value = true
  activeDetailTab.value = 'overview'
}

const closeVendorDetails = () => {
  showVendorDetails.value = false
  selectedVendorDetails.value = null
}

const messageVendor = (vendor) => {
  console.log('Message vendor:', vendor)
  // Handle message vendor logic
}

// Vendor Form Methods
const openCreateVendorModal = () => {
  isEditMode.value = false
  vendorForm.value = {
    id: `VEN${String(vendors.length + 1).padStart(3, '0')}`,
    name: '',
    serviceType: '',
    contactPhone: '',
    contactEmail: '',
    rating: 5.0,
    status: 'Active'
  }
  showCreateVendorModal.value = true
}

const closeCreateVendorModal = () => {
  showCreateVendorModal.value = false
  isEditMode.value = false
}

const saveVendor = async () => {
  if (!vendorForm.value.name || !vendorForm.value.serviceType || 
      !vendorForm.value.contactPhone || !vendorForm.value.contactEmail) {
    return
  }

  isLoading.value = true

  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))

    const newVendor = {
      id: vendorForm.value.id,
      name: vendorForm.value.name,
      logo: null,
      serviceType: vendorForm.value.serviceType,
      rating: vendorForm.value.rating,
      contact: {
        phone: vendorForm.value.contactPhone,
        email: vendorForm.value.contactEmail
      },
      activeShipments: 0,
      status: vendorForm.value.status
    }

    if (isEditMode.value) {
      const index = vendors.findIndex(v => v.id === vendorForm.value.id)
      if (index !== -1) {
        vendors[index] = { ...vendors[index], ...newVendor }
      }
    } else {
      vendors.unshift(newVendor)
    }

    closeCreateVendorModal()
  } catch (error) {
    console.error('Error saving vendor:', error)
  } finally {
    isLoading.value = false
  }
}

const editVendor = (vendor) => {
  isEditMode.value = true
  vendorForm.value = {
    id: vendor.id,
    name: vendor.name,
    serviceType: vendor.serviceType,
    contactPhone: vendor.contact.phone,
    contactEmail: vendor.contact.email,
    rating: vendor.rating,
    status: vendor.status
  }
  showCreateVendorModal.value = true
}

const promptDeleteVendor = (vendor) => {
  vendorToDelete.value = vendor
  showDeleteConfirm.value = true
}

const handleConfirmDeleteVendor = () => {
  if (vendorToDelete.value) {
    const index = vendors.findIndex(v => v.id === vendorToDelete.value.id)
    if (index !== -1) {
      vendors.splice(index, 1)
    }
    console.log(`Successfully deleted vendor: ${vendorToDelete.value.id}`)
  }
  cancelDeleteVendor()
}

const cancelDeleteVendor = () => {
  showDeleteConfirm.value = false
  vendorToDelete.value = null
}

// Handle escape key and click outside
const handleEscapeKey = (event) => {
  if (event.key === 'Escape') {
    if (showVendorDetails.value) {
      closeVendorDetails()
    } else if (showQuickActions.value) {
      showQuickActions.value = null
    }
  }
}

const handleClickOutside = () => {
  showQuickActions.value = null
}

// Event listeners
onMounted(() => {
  document.addEventListener('keydown', handleEscapeKey)
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscapeKey)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* Vendor card animations */
.vendor-card {
  position: relative;
  overflow: hidden;
}

.vendor-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.02) 0%, rgba(37, 99, 235, 0.02) 100%);
  transition: opacity 0.3s ease;
  border-radius: 20px;
}

.vendor-card:hover::before {
  opacity: 1;
}

/* Backdrop transitions */
.backdrop-enter-active, .backdrop-leave-active {
  transition: opacity 300ms ease;
}
.backdrop-enter-from, .backdrop-leave-to {
  opacity: 0;
}

/* Slide-over transitions */
.slide-over-enter-active, .slide-over-leave-active {
  transition: transform 300ms ease-out;
}
.slide-over-enter-from, .slide-over-leave-to {
  transform: translateX(100%);
}

/* Dropdown transitions */
.dropdown-enter-active, .dropdown-leave-active {
  transition: all 150ms ease;
  transform-origin: bottom left;
}
.dropdown-enter-from, .dropdown-leave-to {
  opacity: 0;
  transform: scale(0.95) translateY(5px);
}

/* Custom scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Animations */
@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fade-in 0.6s ease-out;
}

.animate-fade-in > * {
  animation: fade-in 0.6s ease-out;
  animation-fill-mode: both;
}

/* Progress bar animations */
@keyframes progressFill {
  from {
    width: 0%;
  }
}

.progress-bar {
  animation: progressFill 1000ms ease-out;
}

/* Performance metric stagger */
.performance-metric:nth-child(1) { animation-delay: 0ms; }
.performance-metric:nth-child(2) { animation-delay: 100ms; }
.performance-metric:nth-child(3) { animation-delay: 200ms; }
.performance-metric:nth-child(4) { animation-delay: 300ms; }

/* Card hover effects */
.metric-card {
  transition: all 200ms ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Timeline animations */
@keyframes timelineSlide {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.timeline-item {
  animation: timelineSlide 400ms ease-out;
}

.timeline-item:nth-child(1) { animation-delay: 0ms; }
.timeline-item:nth-child(2) { animation-delay: 50ms; }
.timeline-item:nth-child(3) { animation-delay: 100ms; }
.timeline-item:nth-child(4) { animation-delay: 150ms; }
.timeline-item:nth-child(5) { animation-delay: 200ms; }

/* Button animations */
.action-button {
  transition: all 200ms ease;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.3);
}

.action-button:active {
  transform: translateY(0);
}
</style>