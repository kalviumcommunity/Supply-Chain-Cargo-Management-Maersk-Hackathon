<script setup>
import Dashboard from './components/Dashboard.vue'

const handleNewShipment = () => {
  console.log('New shipment clicked')
}

const handleActivityClick = (activity) => {
  console.log('Activity clicked:', activity)
}

const handleActionClick = (actionId) => {
  console.log('Action clicked:', actionId)
}
</script>

<template>
  <Dashboard 
    @new-shipment="handleNewShipment"
    @activity-click="handleActivityClick"
    @action-click="handleActionClick"
  />
</template>
