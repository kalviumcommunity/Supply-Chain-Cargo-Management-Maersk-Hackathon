<template>
  <div class="route-map-container h-[600px] w-full rounded-xl overflow-hidden border border-gray-200">
    <div ref="mapContainer" class="w-full h-full"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import globalCoordinates from '@/data/globalCoordinates'


// Props
const props = defineProps({
  routes: {
    type: Array,
    required: true,
    default: () => []
  },
  filteredRoutes: {
    type: Array,
    default: () => []
  },
  highlightedRouteId: {
    type: [Number, String],
    default: null
  }
})

// Emits
const emit = defineEmits(['marker-clicked'])

// Reactive data
const mapContainer = ref(null)
let map = null
let markersLayer = null
let routesLayer = null


// Transport mode colors and styling
const transportModeStyles = {
  'AIR': {
    color: '#ef4444', // red-500 - Air routes (fastest)
    weight: 3,
    opacity: 0.8,
    dashArray: '10, 5', // Dashed line for air routes
    icon: '✈️'
  },
  'OCEAN': {
    color: '#0ea5e9', // sky-500 - Ocean routes (bulk cargo)
    weight: 5,
    opacity: 0.9,
    dashArray: null, // Solid line for ocean routes
    icon: '🚢'
  },
  'RAIL': {
    color: '#059669', // emerald-600 - Rail routes (efficient land transport)
    weight: 4,
    opacity: 0.8,
    dashArray: '5, 5, 1, 5', // Dash-dot pattern for rail
    icon: '🚂'
  },
  'ROAD': {
    color: '#000000', // Bold black road routes
    weight: 5,
    opacity: 0.95,
    dashArray: null, // Solid line for road routes
    icon: '🚛'
  },
  // Legacy support for existing route types
  'express': {
    color: '#ef4444',
    weight: 4,
    opacity: 0.8,
    dashArray: '10, 5',
    icon: '⚡'
  },
  'standard': {
    color: '#3b82f6',
    weight: 3,
    opacity: 0.7,
    dashArray: null,
    icon: '📦'
  },
  'priority': {
    color: '#f59e0b',
    weight: 4,
    opacity: 0.8,
    dashArray: '5, 5',
    icon: '⭐'
  }
}

// Status colors
const statusColors = {
  'Active': '#10b981', // emerald-500
  'Delayed': '#f59e0b', // amber-500
  'Closed': '#6b7280'   // gray-500
}

// Utility: Convert degrees to radians
const toRad = (deg) => (deg * Math.PI) / 180
// Utility: Convert radians to degrees
const toDeg = (rad) => (rad * 180) / Math.PI

// Generate great-circle points between two lat/lng pairs
// Returns an array of [lat, lng]
const greatCircleSegment = (start, end, segments = 128) => {
  const [lat1, lon1] = start.map(toRad)
  const [lat2, lon2] = end.map(toRad)

  const d = 2 * Math.asin(
    Math.sqrt(
      Math.sin((lat2 - lat1) / 2) ** 2 +
        Math.cos(lat1) * Math.cos(lat2) * Math.sin((lon2 - lon1) / 2) ** 2
    )
  )
  if (d === 0) return [start, end]

  const points = []
  for (let i = 0; i <= segments; i++) {
    const f = i / segments
    const A = Math.sin((1 - f) * d) / Math.sin(d)
    const B = Math.sin(f * d) / Math.sin(d)

    const x = A * Math.cos(lat1) * Math.cos(lon1) + B * Math.cos(lat2) * Math.cos(lon2)
    const y = A * Math.cos(lat1) * Math.sin(lon1) + B * Math.cos(lat2) * Math.sin(lon2)
    const z = A * Math.sin(lat1) + B * Math.sin(lat2)

    const lat = Math.atan2(z, Math.sqrt(x * x + y * y))
    const lon = Math.atan2(y, x)
    points.push([toDeg(lat), toDeg(lon)])
  }
  return points
}

// Given array of points, generate great-circle path across all
const greatCirclePath = (coords, segmentsPerLeg = 64) => {
  if (!coords || coords.length < 2) return []
  const result = []
  for (let i = 0; i < coords.length - 1; i++) {
    const seg = greatCircleSegment(coords[i], coords[i + 1], segmentsPerLeg)
    if (i > 0) seg.shift() // avoid duplicate point at joints
    result.push(...seg)
  }
  return result
}

// Heuristic: infer ocean waypoints (Suez/Panama/Cape) for OCEAN routes
const inferOceanWaypoints = (origin, destination) => {
  const [olat, olon] = origin
  const [dlat, dlon] = destination
  const wps = []
  // East Asia to US East/Europe -> Panama
  if (olon > 100 && dlon < -40) {
    wps.push(globalCoordinates['Panama Canal'])
  }
  // Europe/North Atlantic to Indian Ocean/Asia -> Suez
  if (olon >= -15 && olon <= 30 && dlon >= 40 && dlon <= 120) {
    wps.push(globalCoordinates['Suez Canal'])
  }
  // Southern routing option around Africa (fallback if both are in Atlantic/Indian and far south)
  if ((olat < 0 || dlat < 0) && Math.abs(olon - dlon) > 60) {
    // include Cape of Good Hope as scenic fallback
    if (!wps.includes(globalCoordinates['Cape of Good Hope'])) {
      wps.push(globalCoordinates['Cape of Good Hope'])
    }
  }
  return wps
}

// Fetch turn-by-turn road geometry between ordered coordinates via OSRM
// coordinates: array of [lat, lng]
const fetchRoadGeometry = async (coordinates) => {
  try {
    if (!coordinates || coordinates.length < 2) return null
    const coordParam = coordinates
      .map(([lat, lng]) => `${lng},${lat}`) // OSRM expects lon,lat
      .join(';')

    const url = `https://router.project-osrm.org/route/v1/driving/${coordParam}?overview=full&geometries=geojson&steps=false&alternatives=false`
    const res = await fetch(url)
    if (!res.ok) throw new Error(`OSRM error ${res.status}`)
    const data = await res.json()
    if (!data?.routes?.[0]?.geometry?.coordinates) return null
    const coords = data.routes[0].geometry.coordinates.map(([lon, lat]) => [lat, lon])
    return coords
  } catch (e) {
    console.warn('OSRM routing failed, falling back to great-circle/straight:', e)
    return null
  }
}

// Initialize map
const initMap = () => {
  if (!mapContainer.value) {
    console.error('RouteMap: mapContainer ref is not available')
    return
  }

  console.log('RouteMap: Initializing global map with routes:', {
    totalRoutes: props.routes?.length || 0,
    filteredRoutes: props.filteredRoutes?.length || 0
  })

  // Create map with global view - center on Greenwich (0,0) for global perspective
  map = L.map(mapContainer.value, {
    center: [20, 0], // Slightly north for better global view
    zoom: 2, // Global zoom level
    minZoom: 1,
    maxZoom: 15,
    zoomControl: true,
    scrollWheelZoom: true,
    doubleClickZoom: true,
    dragging: true,
    worldCopyJump: true // Enable seamless world wrapping
  })

  // Add OpenStreetMap tiles with custom styling optimized for global view
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors',
    className: 'map-tiles',
    maxZoom: 19,
    noWrap: false // Allow world wrapping
  }).addTo(map)

  // Create layers for markers and routes
  markersLayer = L.layerGroup().addTo(map)
  routesLayer = L.layerGroup().addTo(map)

  console.log('RouteMap: Global map initialized successfully')

  // Initial render
  renderRoutes()
}

// Get coordinates for a location with global fallback
const getCoordinates = (location) => {
  // First try exact match
  if (globalCoordinates[location]) {
    return globalCoordinates[location]
  }
  
  // Try case-insensitive match
  const lowerLocation = location.toLowerCase()
  for (const [key, coords] of Object.entries(globalCoordinates)) {
    if (key.toLowerCase() === lowerLocation) {
      return coords
    }
  }
  
  // Try partial match (for cases like "Port of Hamburg" matching "Hamburg")
  for (const [key, coords] of Object.entries(globalCoordinates)) {
    if (location.toLowerCase().includes(key.toLowerCase()) || key.toLowerCase().includes(lowerLocation)) {
      return coords
    }
  }
  
  // Default to center of world map if location not found
  console.warn(`RouteMap: Location "${location}" not found in coordinate mapping, using default coordinates`)
  return [20, 0] // Global center
}

// Create custom marker icon with transport mode support
const createMarkerIcon = (type, status, transportMode = null) => {
  const baseColor = type === 'origin' ? '#3b82f6' : '#10b981' // blue for origin, green for destination
  const statusColor = statusColors[status] || '#6b7280'
  
  // Get transport mode icon if available
  const modeStyle = transportMode ? transportModeStyles[transportMode] : null
  const modeIcon = modeStyle?.icon || ''
  
  return L.divIcon({
    className: 'custom-marker',
    html: `
      <div class="flex items-center justify-center w-10 h-10 rounded-full border-2 border-white shadow-lg relative" 
           style="background-color: ${baseColor};">
        <div class="w-4 h-4 rounded-full" style="background-color: ${statusColor};"></div>
        ${modeIcon ? `<span class="absolute -top-1 -right-1 text-xs bg-white rounded-full w-5 h-5 flex items-center justify-center shadow-sm">${modeIcon}</span>` : ''}
      </div>
    `,
    iconSize: [40, 40],
    iconAnchor: [20, 20],
    popupAnchor: [0, -20]
  })
}

// Create route popup content with enhanced transport mode information
const createRoutePopup = (route) => {
  // Get transport mode details
  const transportMode = route.transportationMode || route.routeType || 'standard'
  const modeStyle = transportModeStyles[transportMode] || transportModeStyles['standard']
  const modeIcon = modeStyle.icon
  const modeLabel = transportMode.charAt(0).toUpperCase() + transportMode.slice(1).toLowerCase()
  
  return `
    <div class="route-popup p-4 min-w-[280px] max-w-[350px]">
      <div class="flex items-center justify-between mb-3">
        <h3 class="font-bold text-lg text-gray-900">Route #${route.routeId}</h3>
        <div class="flex items-center gap-2">
          <span class="text-lg">${modeIcon}</span>
          <span class="px-3 py-1 text-xs font-medium rounded-full border" 
                style="background-color: ${modeStyle.color}20; color: ${modeStyle.color}; border-color: ${modeStyle.color}40;">
            ${modeLabel}
          </span>
        </div>
      </div>
      
      <div class="space-y-3 text-sm">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <span class="font-medium text-gray-700">Distance:</span>
            <div class="text-gray-900 font-mono">${(route.distance || 0).toLocaleString()} km</div>
          </div>
          <div>
            <span class="font-medium text-gray-700">Duration:</span>
            <div class="text-gray-900">${route.duration || 0} days</div>
          </div>
        </div>
        
        <div class="grid grid-cols-2 gap-4">
          <div>
            <span class="font-medium text-gray-700">Origin:</span>
            <div class="text-gray-900">${route.originPort}</div>
          </div>
          <div>
            <span class="font-medium text-gray-700">Destination:</span>
            <div class="text-gray-900">${route.destinationPort}</div>
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
            <span class="font-medium text-gray-700">Cost:</span>
            <span class="text-gray-900 ml-1">$${(route.cost || 0).toLocaleString()}</span>
        </div>
      </div>
    </div>
  `
}

let routeLayers = {};

// Render routes on map
const renderRoutes = async () => {
  if (!map || !markersLayer || !routesLayer) {
    console.warn('RouteMap: Map or layers not ready for rendering')
    return
  }

  // Use filtered routes for rendering
  const routesToRender =
    props.filteredRoutes && props.filteredRoutes.length > 0
      ? props.filteredRoutes
      : props.routes;

  console.log('RouteMap: Rendering routes:', {
    filteredRoutesCount: props.filteredRoutes?.length || 0,
    totalRoutesCount: props.routes?.length || 0,
    routesToRenderCount: routesToRender?.length || 0,
  })

  // Clear existing layers
  markersLayer.clearLayers()
  routesLayer.clearLayers()
  routeLayers = {}

  if (!routesToRender || routesToRender.length === 0) {
    console.log('RouteMap: No routes to render')
    return
  }

  const processedLocations = new Set()

  for (let index = 0; index < routesToRender.length; index++) {
    const route = routesToRender[index]
    try {
      // Validate route structure
      if (!route || typeof route !== 'object') {
        console.warn(`RouteMap: Invalid route at index ${index}:`, route)
        continue
      }

      if (!route.originPort || !route.destinationPort) {
        console.warn(`RouteMap: Route ${route.routeId || index} missing origin or destination port`)
        continue
      }
      
      const originCoords = getCoordinates(route.originPort)
      const destCoords = getCoordinates(route.destinationPort)
      
      if (!originCoords || !destCoords) {
        console.warn(`RouteMap: Could not get coordinates for route ${route.routeId}`)
        continue
      }
      
      const transportMode = route.transportationMode || 'STANDARD'

    // Add markers for origin and destination (avoid duplicates)
      const originKey = `${route.originPort}-origin`
      const destKey = `${route.destinationPort}-destination`

      if (!processedLocations.has(originKey)) {
        const originMarker = L.marker(originCoords, {
          icon: createMarkerIcon('origin', route.status, transportMode)
        })
          .bindPopup(`
        <div class="p-3">
          <h4 class="font-bold text-blue-600 mb-2">${route.originPort}</h4>
          <p class="text-xs text-gray-500">Origin for ${transportMode.toUpperCase()} routes</p>
        </div>
      `)
          .on('click', () => emit('marker-clicked', { type: 'origin', location: route.originPort, transportMode }))

        markersLayer.addLayer(originMarker)
        processedLocations.add(originKey)
      }

      if (!processedLocations.has(destKey)) {
        const destMarker = L.marker(destCoords, {
          icon: createMarkerIcon('destination', route.status, transportMode)
        })
          .bindPopup(`
        <div class="p-3">
          <h4 class="font-bold text-green-600 mb-2">${route.destinationPort}</h4>
          <p class="text-xs text-gray-500">Destination for ${transportMode.toUpperCase()} routes</p>
        </div>
      `)
          .on('click', () => emit('marker-clicked', { type: 'destination', location: route.destinationPort, transportMode }))

        markersLayer.addLayer(destMarker)
        processedLocations.add(destKey)
      }

    // Get transport mode styling
    const modeStyle = transportModeStyles[transportMode] || transportModeStyles['standard']
    
    // Create route polyline with transport mode specific styling
    const routeLineOptions = {
      color: modeStyle.color,
      weight: route.status === 'Active' ? modeStyle.weight : Math.max(2, modeStyle.weight - 1),
      opacity: route.status === 'Closed' ? 0.4 : modeStyle.opacity,
      smoothFactor: 1,
      lineCap: 'round',
      lineJoin: 'round'
    }
    
    // Add dash pattern if specified for the transport mode
    if (modeStyle.dashArray) {
      routeLineOptions.dashArray = modeStyle.dashArray
    }

      // Build geometry based on transport mode
      let geometry = null
      if (transportMode === 'ROAD' || transportMode === 'RAIL') {
        const roadCoords = [originCoords, destCoords]
        geometry = await fetchRoadGeometry(roadCoords)
        if (!geometry) {
          // Fallback to great-circle
          geometry = greatCirclePath([originCoords, destCoords], 128)
        }
      } else if (transportMode === 'OCEAN') {
        const oceanCoords = [originCoords]
        const inferred = inferOceanWaypoints(originCoords, destCoords)
        oceanCoords.push(...inferred)
        oceanCoords.push(destCoords)
        geometry = greatCirclePath(oceanCoords, 96)
      } else { // AIR or other
        geometry = greatCirclePath([originCoords, destCoords], 128)
      }

      const routeLine = L.polyline(geometry && geometry.length > 1 ? geometry : [originCoords, destCoords], routeLineOptions)
        .bindPopup(createRoutePopup(route))

      routesLayer.addLayer(routeLine)
      routeLayers[route.routeId] = { line: routeLine, options: routeLineOptions };

      // Emphasize road visibility when overlapping with others
      if (transportMode === 'ROAD') {
        routeLine.bringToFront()
      }
    } catch (routeError) {
      console.error(`RouteMap: Error processing route ${route.routeId || index}:`, routeError)
    }
  }

  // Fit map to show all routes if there are any
  if (routesToRender.length > 0) {
    try {
      const allLayers = []
      markersLayer.eachLayer((layer) => { allLayers.push(layer) })
      routesLayer.eachLayer((layer) => { allLayers.push(layer) })
      
      if (allLayers.length > 0) {
        const group = new L.featureGroup(allLayers)
        map.fitBounds(group.getBounds().pad(0.1))
      }
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

watch(
    () => props.highlightedRouteId,
    (newId, oldId) => {
      if (oldId && routeLayers[oldId]) {
        routeLayers[oldId].line.setStyle(routeLayers[oldId].options);
      }
      if (newId && routeLayers[newId]) {
        routeLayers[newId].line.setStyle({
          color: '#ffc107',
          weight: routeLayers[newId].options.weight + 3,
          opacity: 1
        });
        routeLayers[newId].line.bringToFront();
      }
    }
  );

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
/* Map container styling with enhanced global support */
.route-map-container {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* Global styles for enhanced Leaflet elements */
:global(.leaflet-popup-content-wrapper) {
  border-radius: 12px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  max-width: 350px;
}

:global(.leaflet-popup-tip) {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

:global(.custom-marker) {
  background: transparent !important;
  border: none !important;
  z-index: 1000;
}

:global(.leaflet-container) {
  font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif;
  background: #f8fafc;
}

:global(.map-tiles) {
  filter: brightness(1.02) contrast(1.05) saturate(0.95);
}

/* Enhanced route popup styling for global routes */
:global(.route-popup) {
  font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif;
}

/* Transport mode specific hover effects */
:global(.leaflet-interactive) {
  transition: all 0.2s ease-in-out;
}

:global(.leaflet-interactive:hover) {
  filter: brightness(1.3) saturate(1.2);
  transform: scale(1.02);
  z-index: 1000;
}

/* Dashed line styles for different transport modes */
:global(.leaflet-interactive[stroke-dasharray*="10"]) {
  /* Air routes - dashed lines */
  animation: dashFlow 3s linear infinite;
}

:global(.leaflet-interactive[stroke-dasharray*="5, 5, 1"]) {
  /* Rail routes - dash-dot pattern */
  animation: railFlow 4s linear infinite;
}

:global(.leaflet-interactive[stroke-dasharray*="2, 8"]) {
  /* Road routes - short dashes */
  animation: roadFlow 2s linear infinite;
}

@keyframes dashFlow {
  to {
    stroke-dashoffset: -20;
  }
}

@keyframes railFlow {
  to {
    stroke-dashoffset: -16;
  }
}

@keyframes roadFlow {
  to {
    stroke-dashoffset: -10;
  }
}

/* Enhanced marker styling for global locations */
:global(.leaflet-marker-icon) {
  transition: transform 0.2s ease-in-out;
}

:global(.leaflet-marker-icon:hover) {
  transform: scale(1.1);
  z-index: 1001;
}
</style>