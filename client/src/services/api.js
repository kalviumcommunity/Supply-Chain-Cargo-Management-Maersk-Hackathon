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
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  }

  try {
    const response = await fetch(url, defaultOptions)
    
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
    console.error(`API Request failed: ${endpoint}`, error)
    throw error
  }
}

// Cargo API endpoints
export const cargoApi = {
  getAll: () => apiRequest('/cargos'),
  getById: (id) => apiRequest(`/cargos/${id}`),
  create: (data) => apiRequest('/cargos', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  update: (id, data) => apiRequest(`/cargos/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  }),
  delete: (id) => apiRequest(`/cargos/${id}`, {
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