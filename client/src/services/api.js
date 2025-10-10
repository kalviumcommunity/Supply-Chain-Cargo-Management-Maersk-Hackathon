// API Service - Centralized HTTP request handling
// Created for backend-frontend integration

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

/**
 * Generic API request handler with error handling
 * @param {string} endpoint - API endpoint (without base URL)
 * @param {Object} options - Fetch options (method, headers, body, etc.)
 * @returns {Promise} - Response data or throws error
 */
const apiRequest = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`
  
  // Add timeout support
  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), 5000) // 5 second timeout
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    signal: controller.signal,
    ...options
  }

  try {
    const response = await fetch(url, defaultOptions)
    clearTimeout(timeoutId)
    
    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`HTTP ${response.status}: ${errorText || response.statusText}`)
    }
    
    // Handle empty responses (like 204 No Content)
    const contentType = response.headers.get('content-type')
    if (contentType && contentType.includes('application/json')) {
      return await response.json()
    }
    
    return null
  } catch (error) {
    clearTimeout(timeoutId)
    
    // Enhanced error messages for common connection issues
    if (error.name === 'AbortError') {
      console.error(`API Request timeout: ${endpoint}`)
      throw new Error('Request timeout - backend server may be slow or unavailable')
    }
    
    if (error.message.includes('Failed to fetch') || error.code === 'ECONNREFUSED') {
      console.error(`API Connection failed: ${endpoint}`)
      throw new Error('Backend server is not running on port 8080. Please start the server or use demo mode.')
    }
    
    console.error(`API Request failed: ${endpoint}`, error)
    throw error
  }
}

// Cargo API endpoints
export const cargoApi = {
  getAll: () => apiRequest('/cargo'),
  getById: (id) => apiRequest(`/cargo/${id}`),
  create: (data) => apiRequest('/cargo', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/cargo/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  remove: (id) => apiRequest(`/cargo/${id}`, {
    method: 'DELETE'
  })
}

// Vendor API endpoints
export const vendorApi = {
  getAll: () => apiRequest('/vendors'),
  getById: (id) => apiRequest(`/vendors/${id}`),
  create: (data) => apiRequest('/vendors', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/vendors/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (id) => apiRequest(`/vendors/${id}`, {
    method: 'DELETE'
  })
}

// Route API endpoints
export const routeApi = {
  getAll: () => apiRequest('/routes'),
  getById: (id) => apiRequest(`/routes/${id}`),
  create: (data) => apiRequest('/routes', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/routes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (id) => apiRequest(`/routes/${id}`, {
    method: 'DELETE'
  })
}

// Shipment API endpoints
export const shipmentApi = {
  getAll: () => apiRequest('/shipments'),
  getById: (id) => apiRequest(`/shipments/${id}`),
  create: (data) => apiRequest('/shipments', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/shipments/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (id) => apiRequest(`/shipments/${id}`, {
    method: 'DELETE'
  })
}

// Delivery API endpoints
export const deliveryApi = {
  getAll: () => apiRequest('/deliveries'),
  getById: (id) => apiRequest(`/deliveries/${id}`),
  create: (data) => apiRequest('/deliveries', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/deliveries/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (id) => apiRequest(`/deliveries/${id}`, {
    method: 'DELETE'
  })
}

// Health check endpoint
export const dashboardApi = {
  getMetrics: () => apiRequest('/dashboard/metrics'),
  getRecentActivities: () => apiRequest('/dashboard/recent-activities')
}

export const healthApi = {
  check: () => apiRequest('/health').catch(() => false)
}

export default {
  cargoApi,
  vendorApi,
  routeApi,
  shipmentApi,
  deliveryApi,
  dashboardApi,
  healthApi
}