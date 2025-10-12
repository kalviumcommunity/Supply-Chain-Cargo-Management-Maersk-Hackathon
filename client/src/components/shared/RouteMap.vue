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

// Global coordinates mapping for major cities and ports worldwide
const globalCoordinates = {
  // Asia-Pacific
  'Mumbai': [19.0760, 72.8777],
  'Chennai': [13.0827, 80.2707],
  'Delhi': [28.7041, 77.1025],
  'Bangalore': [12.9716, 77.5946],
  'Kolkata': [22.5726, 88.3639],
  'Ahmedabad': [23.0225, 72.5714],
  'Pune': [18.5204, 73.8567],
  'Hyderabad': [17.3850, 78.4867],
  'Jaipur': [26.9124, 75.7873],
  'Surat': [21.1702, 72.8311],
  'Visakhapatnam': [17.6868, 83.2185],
  'Kochi': [9.9312, 76.2673],
  'Thoothukudi': [8.7642, 78.1348],
  'Goa': [15.3860, 73.8150],
  'Coimbatore': [11.0168, 76.9558],
  'Nagpur': [21.1458, 79.0882],
  'Lucknow': [26.8467, 80.9462],
  'Patna': [25.5941, 85.1376],
  'Bhubaneswar': [20.2961, 85.8245],
  'Indore': [22.7196, 75.8577],
  'Kanpur': [26.4499, 80.3319],
  'Vadodara': [22.3072, 73.1812],
  'Ludhiana': [30.9009, 75.8573],
  'Ranchi': [23.3441, 85.3096],
  'Guwahati': [26.1445, 91.7362],
  // Major Indian ports
  'JNPT (Nhava Sheva)': [18.9460, 72.9460],
  'Mundra Port': [22.7295, 69.7095],
  'Kandla': [23.0333, 70.2167],
  'Haldia': [22.0400, 88.0700],
  'Paradip': [20.3167, 86.6167],
  'Ennore': [13.2167, 80.3167],
  'Krishnapatnam': [14.2522, 80.1203],
  'Vizhinjam': [8.3776, 76.9781],
  'Mangaluru': [12.9141, 74.8560],
  'Singapore': [1.3521, 103.8198],
  'Tokyo': [35.6762, 139.6503],
  'Hong Kong': [22.3193, 114.1694],
  'Shanghai': [31.2304, 121.4737],
  'Seoul': [37.5665, 126.9780],
  'Bangkok': [13.7563, 100.5018],
  'Manila': [14.5995, 120.9842],
  'Jakarta': [6.2088, 106.8456],
  'Kuala Lumpur': [3.1390, 101.6869],
  'Sydney': [-33.8688, 151.2093],
  'Melbourne': [-37.8136, 144.9631],
  'Busan': [35.1796, 129.0756],
  'Yokohama': [35.4437, 139.6380],
  'Nagoya': [35.1815, 136.9066],
  'Shenzhen': [22.5431, 114.0579],
  'Guangzhou': [23.1291, 113.2644],
  'Ningbo': [29.8683, 121.5440],
  'Qingdao': [36.0671, 120.3826],
  'Tianjin': [39.3434, 117.3616],
  'Xiamen': [24.4798, 118.0894],
  'Kaohsiung': [22.6273, 120.3014],
  'Tanjung Pelepas': [1.3566, 103.5392],
  'Port Klang': [2.9947, 101.3929],
  'Colombo': [6.9271, 79.8612],
  
  // Europe
  'London': [51.5074, -0.1278],
  'Hamburg': [53.5511, 9.9937],
  'Rotterdam': [51.9244, 4.4777],
  'Antwerp': [51.2194, 4.4025],
  'Paris': [48.8566, 2.3522],
  'Amsterdam': [52.3702, 4.8951],
  'Frankfurt': [50.1109, 8.6821],
  'Milan': [45.4642, 9.1900],
  'Madrid': [40.4168, -3.7038],
  'Barcelona': [41.3851, 2.1734],
  'Rome': [41.9028, 12.4964],
  'Berlin': [52.5200, 13.4050],
  'Munich': [48.1351, 11.5820],
  'Vienna': [48.2082, 16.3738],
  'Stockholm': [59.3293, 18.0686],
  'Oslo': [59.9139, 10.7522],
  'Copenhagen': [55.6761, 12.5683],
  'Helsinki': [60.1695, 24.9354],
  'Gibraltar': [36.1408, -5.3536],
  'Algeciras': [36.1270, -5.4477],
  'Valencia': [39.4699, -0.3763],
  'Le Havre': [49.4944, 0.1079],
  'Felixstowe': [51.9550, 1.3500],
  'Southampton': [50.9097, -1.4044],
  'Piraeus': [37.9420, 23.6460],
  'Gdansk': [54.3520, 18.6466],
  'Gothenburg': [57.7089, 11.9746],
  
  // North America
  'New York': [40.7128, -74.0060],
  'Los Angeles': [34.0522, -118.2437],
  'Chicago': [41.8781, -87.6298],
  'Miami': [25.7617, -80.1918],
  'Seattle': [47.6062, -122.3321],
  'Vancouver': [49.2827, -123.1207],
  'Toronto': [43.6532, -79.3832],
  'Montreal': [45.5017, -73.5673],
  'San Francisco': [37.7749, -122.4194],
  'Houston': [29.7604, -95.3698],
  'Atlanta': [33.7490, -84.3880],
  'Boston': [42.3601, -71.0589],
  'Washington DC': [38.9072, -77.0369],
  'Mexico City': [19.4326, -99.1332],
  'Long Beach': [33.7701, -118.1937],
  'Oakland': [37.8044, -122.2712],
  'Prince Rupert': [54.3150, -130.3200],
  'Manzanillo MX': [19.1138, -104.3421],
  'Veracruz': [19.1738, -96.1342],
  'Colon': [9.3590, -79.9014],
  'Balboa': [8.9490, -79.5556],
  'New Orleans': [29.9511, -90.0715],
  'Savannah': [32.0809, -81.0912],
  'Charleston': [32.7765, -79.9311],
  'Norfolk': [36.8508, -76.2859],
  'Newark': [40.7357, -74.1724],
  'Halifax': [44.6488, -63.5752],
  
  // South America
  'São Paulo': [-23.5505, -46.6333],
  'Rio de Janeiro': [-22.9068, -43.1729],
  'Buenos Aires': [-34.6118, -58.3960],
  'Lima': [-12.0464, -77.0428],
  'Bogotá': [4.7110, -74.0721],
  'Santiago': [-33.4489, -70.6693],
  'Caracas': [10.4806, -66.9036],
  'Montevideo': [-34.9011, -56.1645],
  
  // Africa
  'Cairo': [30.0444, 31.2357],
  'Cape Town': [-33.9249, 18.4241],
  'Lagos': [6.5244, 3.3792],
  'Nairobi': [-1.2921, 36.8219],
  'Johannesburg': [-26.2041, 28.0473],
  'Casablanca': [33.5731, -7.5898],
  'Alexandria': [31.2001, 29.9187],
  'Durban': [-29.8587, 31.0218],
  'Algiers': [36.7538, 3.0588],
  'Tunis': [36.8065, 10.1815],
  'Port Said': [31.2653, 32.3019],
  'Mombasa': [-4.0435, 39.6682],
  'Dar es Salaam': [-6.7924, 39.2083],
  'Walvis Bay': [-22.9576, 14.5053],
  'Tema': [5.6690, -0.0166],
  'Abidjan': [5.3599, -4.0083],
  
  // Middle East
  'Dubai': [25.2048, 55.2708],
  'Abu Dhabi': [24.2539, 54.3773],
  'Doha': [25.2854, 51.5310],
  'Kuwait City': [29.3759, 47.9774],
  'Riyadh': [24.7136, 46.6753],
  'Jeddah': [21.4858, 39.1925],
  'Tehran': [35.6892, 51.3890],
  'Baghdad': [33.3152, 44.3661],
  'Istanbul': [41.0082, 28.9784],
  'Ankara': [39.9334, 32.8597],
  'Jebel Ali': [25.0025, 55.0633],
  'Salalah': [17.0197, 54.0897],
  'Dammam': [26.4207, 50.0888],
  'Muscat': [23.5880, 58.3829],
  'Sohar': [24.3460, 56.7075],

  // Strategic maritime choke points
  'Suez Canal': [30.5850, 32.2654],
  'Panama Canal': [9.0800, -79.6800],
  'Cape of Good Hope': [-34.3568, 18.4740]
}

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
  const efficiency = route.efficiencyScore || 0
  const efficiencyColor = efficiency >= 90 ? '#10b981' : efficiency >= 75 ? '#f59e0b' : '#ef4444'
  
  // Get transport mode details
  const transportMode = route.transportMode || route.routeType || 'standard'
  const modeStyle = transportModeStyles[transportMode] || transportModeStyles['standard']
  const modeIcon = modeStyle.icon
  const modeLabel = transportMode.charAt(0).toUpperCase() + transportMode.slice(1).toLowerCase()
  
  return `
    <div class="route-popup p-4 min-w-[280px] max-w-[350px]">
      <div class="flex items-center justify-between mb-3">
        <h3 class="font-bold text-lg text-gray-900">${route.id}</h3>
        <div class="flex items-center gap-2">
          <span class="text-lg">${modeIcon}</span>
          <span class="px-3 py-1 text-xs font-medium rounded-full border" 
                style="background-color: ${modeStyle.color}20; color: ${modeStyle.color}; border-color: ${modeStyle.color}40;">
            ${modeLabel}
          </span>
        </div>
      </div>
      
      <div class="space-y-3 text-sm">
        <div>
          <span class="font-medium text-gray-700">Route:</span>
          <span class="text-gray-900 ml-1">${route.name}</span>
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
        
        <div class="grid grid-cols-2 gap-4">
          <div>
            <span class="font-medium text-gray-700">Origin:</span>
            <div class="text-gray-900">${route.origin.location}</div>
            <div class="text-xs text-gray-500">${route.origin.port || 'N/A'}</div>
          </div>
          <div>
            <span class="font-medium text-gray-700">Destination:</span>
            <div class="text-gray-900">${route.destination.location}</div>
            <div class="text-xs text-gray-500">${route.destination.port || 'N/A'}</div>
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
const renderRoutes = async () => {
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

  // Collect layers to later fit bounds
  const polylines = []

  for (let index = 0; index < routesToRender.length; index++) {
    const route = routesToRender[index]
    try {
      // Validate route structure
      if (!route || typeof route !== 'object') {
        console.warn(`RouteMap: Invalid route at index ${index}:`, route)
        continue
      }

      if (!route.origin?.location || !route.destination?.location) {
        console.warn(`RouteMap: Route ${route.id || index} missing origin or destination location`)
        continue
      }

      console.log(`RouteMap: Processing route ${index + 1}/${routesToRender.length}:`, route.id, route.name)
      
      const originCoords = getCoordinates(route.origin.location)
      const destCoords = getCoordinates(route.destination.location)
      
      if (!originCoords || !destCoords) {
        console.warn(`RouteMap: Could not get coordinates for route ${route.id}`)
        continue
      }
      
      const transportMode = route.transportMode || route.routeType || 'standard'

      console.log(`RouteMap: Coordinates for ${route.id}:`, {
        origin: { location: route.origin.location, coords: originCoords },
        destination: { location: route.destination.location, coords: destCoords },
        transportMode: transportMode
      })

    // Add markers for origin and destination (avoid duplicates)
      const originKey = `${route.origin.location}-origin`
      const destKey = `${route.destination.location}-destination`

      if (!processedLocations.has(originKey)) {
        const originMarker = L.marker(originCoords, {
          icon: createMarkerIcon('origin', route.status, transportMode)
        })
          .bindPopup(`
        <div class="p-3">
          <h4 class="font-bold text-blue-600 mb-2">${route.origin.location}</h4>
          <p class="text-sm text-gray-600 mb-1">Port/Terminal: ${route.origin.port || 'N/A'}</p>
          <p class="text-xs text-gray-500">Origin for ${transportMode.toUpperCase()} routes</p>
        </div>
      `)
          .on('click', () => emit('marker-clicked', { type: 'origin', location: route.origin.location, transportMode }))

        markersLayer.addLayer(originMarker)
        processedLocations.add(originKey)
      }

      if (!processedLocations.has(destKey)) {
        const destMarker = L.marker(destCoords, {
          icon: createMarkerIcon('destination', route.status, transportMode)
        })
          .bindPopup(`
        <div class="p-3">
          <h4 class="font-bold text-green-600 mb-2">${route.destination.location}</h4>
          <p class="text-sm text-gray-600 mb-1">Port/Terminal: ${route.destination.port || 'N/A'}</p>
          <p class="text-xs text-gray-500">Destination for ${transportMode.toUpperCase()} routes</p>
        </div>
      `)
          .on('click', () => emit('marker-clicked', { type: 'destination', location: route.destination.location, transportMode }))

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
        // Road: try OSRM with optional waypoints
        const roadCoords = [originCoords]
        if (Array.isArray(route.waypoints) && route.waypoints.length > 0) {
          for (const w of route.waypoints) {
            if (typeof w?.lat === 'number' && typeof w?.lng === 'number') roadCoords.push([w.lat, w.lng])
          }
        }
        roadCoords.push(destCoords)
        geometry = await fetchRoadGeometry(roadCoords)
        if (!geometry) {
          // Fallback to great-circle
          geometry = greatCirclePath([originCoords, destCoords], 128)
        }
      } else if (transportMode === 'OCEAN') {
        // Ocean: attempt to include inferred canal waypoints unless route.waypoints provided
        const oceanCoords = [originCoords]
        if (Array.isArray(route.waypoints) && route.waypoints.length > 0) {
          for (const w of route.waypoints) {
            if (typeof w?.lat === 'number' && typeof w?.lng === 'number') oceanCoords.push([w.lat, w.lng])
          }
        } else {
          const inferred = inferOceanWaypoints(originCoords, destCoords)
          oceanCoords.push(...inferred)
        }
        oceanCoords.push(destCoords)
        geometry = greatCirclePath(oceanCoords, 96)
      } else if (transportMode === 'AIR') {
        // Use exact provided waypoints if present; otherwise draw geodesic
        if (Array.isArray(route.waypoints) && route.waypoints.length > 1) {
          const airCoords = [originCoords]
          for (const w of route.waypoints) {
            if (typeof w?.lat === 'number' && typeof w?.lng === 'number') airCoords.push([w.lat, w.lng])
          }
          airCoords.push(destCoords)
          geometry = greatCirclePath(airCoords, 96)
        } else {
          geometry = greatCirclePath([originCoords, destCoords], 128)
        }
      } else {
        // Legacy or unknown: default to straight or gentle great-circle
        geometry = greatCirclePath([originCoords, destCoords], 32)
      }

      const routeLine = L.polyline(geometry && geometry.length > 1 ? geometry : [originCoords, destCoords], routeLineOptions)
        .bindPopup(createRoutePopup(route))
        .on('click', () => emit('route-clicked', route))

      routesLayer.addLayer(routeLine)
      // Emphasize road visibility when overlapping with others
      if (transportMode === 'ROAD') {
        routeLine.bringToFront()
      }
      polylines.push(routeLine)
    } catch (routeError) {
      console.error(`RouteMap: Error processing route ${route.id || index}:`, routeError)
    }
  }

  // Fit map to show all routes if there are any
  if (routesToRender.length > 0) {
    try {
      // Check if layers have any actual content before creating featureGroup
      const allLayers = []
      
      // Add all markers from markersLayer
      markersLayer.eachLayer((layer) => {
        allLayers.push(layer)
      })
      
      // Add all routes from routesLayer
      routesLayer.eachLayer((layer) => {
        allLayers.push(layer)
      })
      
      if (allLayers.length > 0) {
        const group = new L.featureGroup(allLayers)
        map.fitBounds(group.getBounds().pad(0.1))
        console.log('RouteMap: Map bounds fitted to show all routes')
      } else {
        console.warn('RouteMap: No layers to fit bounds to')
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