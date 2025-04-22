import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router/index'

// Import Element Plus styles
import 'element-plus/dist/index.css'

// Create Vue application instance
const app = createApp(App)

// Setup application
const setupApp = async () => {
  // Setup Pinia for state management
  const pinia = createPinia()
  app.use(pinia)
  
  // Register router
  app.use(router)
  
  // Mount application
  app.mount('#app')
}

// Start the application with error handling
setupApp().catch(error => {
  console.error('Error during application initialization:', error)
})
