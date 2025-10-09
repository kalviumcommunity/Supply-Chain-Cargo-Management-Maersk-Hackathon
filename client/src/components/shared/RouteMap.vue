<template>
  <div class="route-map-container h-[600px] w-full rounded-xl overflow-hidden border border-gray-200">
    <div ref="mapContainer" class="w-full h-full"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

// Props
const props = defineProps({
  routes: {
    type: Array,
    required: true,
    default: () => []
  },
  filteredRoutes: {
    type: Array,
    required: true,
    default: () => []
  }
})

// Emits
const emit = defineEmits(['route-clicked', 'marker-clicked'])

// Reactive data
const mapContainer = ref(null)
let map = null
let markersLayer = null
let routesLayer = null

// Port coordinates mapping (real coordinates for major Indian ports)
const portCoordinates = {
  // Major Indian Ports
  'Mumbai': [19.0760, 72.8777],
  'Chennai': [13.0827, 80.2707],
  'Delhi': [28.7041, 77.1025],
  'Bangalore': [12.9716, 77.5946],
  'Kolkata': [22.5726, 88.3639],
  'Hyderabad': [17.3850, 78.4867],
  'Pune': [18.5204, 73.8567],
  'Kochi': [9.9312, 76.2673],
  'Ahmedabad': [23.0225, 72.5714],
  'Jaipur': [26.9124, 75.7873],
  'Lucknow': [26.8467, 80.9462],
  'Goa': [15.2993, 74.1240],
  'Surat': [21.1702, 72.8311],
  'Vadodara': [22.3072, 73.1812],
  'Coimbatore': [11.0168, 76.9558],
  'Visakhapatnam': [17.6868, 83.2185],
  'Bhubaneswar': [20.2961, 85.8245],
  'Thiruvananthapuram': [8.5241, 76.9366],
  'Mangalore': [12.9141, 74.8560],
  'Nashik': [19.9975, 73.7898]
}

// Route type colors
const routeTypeColors = {
  'express': '#ef4444', // red-500
  'standard': '#3b82f6', // blue-500
  'priority': '#f59e0b'  // amber-500
}

// Status colors
const statusColors = {
  'Active': '#10b981', // emerald-500
  'Delayed': '#f59e0b', // amber-500
  'Closed': '#6b7280'   // gray-500
}

// Initialize map
const initMap = () => {
  if (!mapContainer.value) {
    console.error('RouteMap: mapContainer ref is not available')
    return
  }

  console.log('RouteMap: Initializing map with routes:', {
    totalRoutes: props.routes?.length || 0,
    filteredRoutes: props.filteredRoutes?.length || 0
  })

  // Create map centered on India
  map = L.map(mapContainer.value, {
    center: [20.5937, 78.9629], // Center of India
    zoom: 5,
    minZoom: 4,
    maxZoom: 12,
    zoomControl: true,
    scrollWheelZoom: true,
    doubleClickZoom: true,
    dragging: true
  })

  // Add OpenStreetMap tiles with custom styling
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors',
    className: 'map-tiles'
  }).addTo(map)

  // Create layers for markers and routes
  markersLayer = L.layerGroup().addTo(map)
  routesLayer = L.layerGroup().addTo(map)

  console.log('RouteMap: Map initialized successfully')

  // Initial render
  renderRoutes()
}

// Get coordinates for a location
const getCoordinates = (location) => {
  return portCoordinates[location] || [20.5937, 78.9629] // Default to center of India
}

// Create custom marker icon
const createMarkerIcon = (type, status) => {
  const color = type === 'origin' ? '#3b82f6' : '#10b981' // blue for origin, green for destination
  const statusColor = statusColors[status] || '#6b7280'
  
  return L.divIcon({
    className: 'custom-marker',
    html: `
      <div class="flex items-center justify-center w-8 h-8 rounded-full border-2 border-white shadow-lg" 
           style="background-color: ${color};">
        <div class="w-3 h-3 rounded-full" style="background-color: ${statusColor};"></div>
      </div>
    `,
    iconSize: [32, 32],
    iconAnchor: [16, 16],
    popupAnchor: [0, -16]
  })
}

// Create route popup content
const createRoutePopup = (route) => {
  const efficiency = route.efficiencyScore || 0
  const efficiencyColor = efficiency >= 90 ? '#10b981' : efficiency >= 75 ? '#f59e0b' : '#ef4444'
  
  return `
    <div class="route-popup p-3 min-w-[250px]">
      <div class="flex items-center justify-between mb-2">
        <h3 class="font-bold text-lg text-gray-900">${route.id}</h3>
        <span class="px-2 py-1 text-xs font-medium rounded-full" 
              style="background-color: ${routeTypeColors[route.routeType]}20; color: ${routeTypeColors[route.routeType]};">
          ${route.routeType.charAt(0).toUpperCase() + route.routeType.slice(1)}
        </span>
      </div>
      
      <div class="space-y-2 text-sm">
        <div>
          <span class="font-medium text-gray-700">Route:</span>
          <span class="text-gray-900">${route.name}</span>
        </div>
        
        <div class="grid grid-cols-2 gap-4">
          <div>
            <span class="font-medium text-gray-700">Distance:</span>
            <div class="text-gray-900 font-mono">${route.distance.toLocaleString()} km</div>
          </div>
          <div>
            <span class="font-medium text-gray-700">Duration:</span>
            <div class="text-gray-900">${route.duration}h</div>
          </div>
        </div>
        
        <div>
          <span class="font-medium text-gray-700">Status:</span>
          <span class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full ml-2" 
                style="background-color: ${statusColors[route.status]}20; color: ${statusColors[route.status]};">
            ${route.status}
          </span>
        </div>
        
        <div>
          <span class="font-medium text-gray-700">Efficiency:</span>
          <div class="flex items-center mt-1">
            <div class="flex-1 bg-gray-200 rounded-full h-2 mr-2">
              <div class="h-2 rounded-full transition-all duration-300" 
                   style="width: ${efficiency}%; background-color: ${efficiencyColor};"></div>
            </div>
            <span class="text-sm font-medium" style="color: ${efficiencyColor};">${efficiency}%</span>
          </div>
        </div>
        
        <div class="flex items-center justify-between pt-2 border-t border-gray-200">
          <span class="font-medium text-gray-700">Active Shipments:</span>
          <span class="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs font-medium">
            ${route.activeShipments || 0}
          </span>
        </div>
      </div>
    </div>
  `
}

// Render routes on map
const renderRoutes = () => {
  if (!map || !markersLayer || !routesLayer) {
    console.warn('RouteMap: Map or layers not ready for rendering')
    return
  }

  // Use filtered routes for rendering
  const routesToRender = props.filteredRoutes && props.filteredRoutes.length > 0 
    ? props.filteredRoutes 
    : props.routes

  console.log('RouteMap: Rendering routes:', {
    filteredRoutesCount: props.filteredRoutes?.length || 0,
    totalRoutesCount: props.routes?.length || 0,
    routesToRenderCount: routesToRender?.length || 0,
    routesToRender: routesToRender?.map(r => ({ id: r.id, name: r.name })) || []
  })

  // Clear existing layers
  markersLayer.clearLayers()
  routesLayer.clearLayers()

  if (!routesToRender || routesToRender.length === 0) {
    console.log('RouteMap: No routes to render')
    return
  }

  const processedLocations = new Set()

  routesToRender.forEach((route, index) => {
    console.log(`RouteMap: Processing route ${index + 1}/${routesToRender.length}:`, route.id, route.name)
    
    const originCoords = getCoordinates(route.origin.location)
    const destCoords = getCoordinates(route.destination.location)

    console.log(`RouteMap: Coordinates for ${route.id}:`, {
      origin: { location: route.origin.location, coords: originCoords },
      destination: { location: route.destination.location, coords: destCoords }
    })

    // Add markers for origin and destination (avoid duplicates)
    const originKey = `${route.origin.location}-origin`
    const destKey = `${route.destination.location}-destination`

    if (!processedLocations.has(originKey)) {
      const originMarker = L.marker(originCoords, {
        icon: createMarkerIcon('origin', route.status)
      })
      .bindPopup(`
        <div class="p-2">
          <h4 class="font-bold text-blue-600">${route.origin.location}</h4>
          <p class="text-sm text-gray-600">Origin Port: ${route.origin.port}</p>
        </div>
      `)
      .on('click', () => emit('marker-clicked', { type: 'origin', location: route.origin.location }))

      markersLayer.addLayer(originMarker)
      processedLocations.add(originKey)
    }

    if (!processedLocations.has(destKey)) {
      const destMarker = L.marker(destCoords, {
        icon: createMarkerIcon('destination', route.status)
      })
      .bindPopup(`
        <div class="p-2">
          <h4 class="font-bold text-green-600">${route.destination.location}</h4>
          <p class="text-sm text-gray-600">Destination Port: ${route.destination.port}</p>
        </div>
      `)
      .on('click', () => emit('marker-clicked', { type: 'destination', location: route.destination.location }))

      markersLayer.addLayer(destMarker)
      processedLocations.add(destKey)
    }

    // Add route polyline
    const routeLine = L.polyline([originCoords, destCoords], {
      color: routeTypeColors[route.routeType] || '#3b82f6',
      weight: route.status === 'Active' ? 4 : 2,
      opacity: route.status === 'Closed' ? 0.5 : 0.8,
      smoothFactor: 1
    })
    .bindPopup(createRoutePopup(route))
    .on('click', () => emit('route-clicked', route))

    routesLayer.addLayer(routeLine)
  })

  // Fit map to show all routes if there are any
  if (routesToRender.length > 0) {
    try {
      const group = new L.featureGroup([markersLayer, routesLayer])
      map.fitBounds(group.getBounds().pad(0.1))
      console.log('RouteMap: Map bounds fitted to show all routes')
    } catch (error) {
      console.error('RouteMap: Error fitting bounds:', error)
    }
  }

  console.log('RouteMap: Route rendering completed')
}

// Watch for route changes
watch(() => props.filteredRoutes, (newRoutes, oldRoutes) => {
  console.log('RouteMap: filteredRoutes changed:', {
    newCount: newRoutes?.length || 0,
    oldCount: oldRoutes?.length || 0,
    newRoutes: newRoutes
  })
  renderRoutes()
}, { deep: true })

watch(() => props.routes, (newRoutes, oldRoutes) => {
  console.log('RouteMap: routes changed:', {
    newCount: newRoutes?.length || 0,
    oldCount: oldRoutes?.length || 0
  })
  renderRoutes()
}, { deep: true })

// Lifecycle hooks
onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
  }
})
</script>

<style scoped>
/* Map container styling */
.route-map-container {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* Global styles for Leaflet elements */
:global(.leaflet-popup-content-wrapper) {
  border-radius: 12px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

:global(.leaflet-popup-tip) {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

:global(.custom-marker) {
  background: transparent !important;
  border: none !important;
}

:global(.leaflet-container) {
  font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif;
}

:global(.map-tiles) {
  filter: brightness(1.1) contrast(1.1);
}

/* Route popup custom styling */
:global(.route-popup) {
  font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif;
}

/* Hover effects for polylines */
:global(.leaflet-interactive:hover) {
  filter: brightness(1.2);
  transition: filter 0.2s ease;
}
</style>