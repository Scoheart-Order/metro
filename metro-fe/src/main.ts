import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router/index'

// Create Vue application instance
const app = createApp(App)

// Optimize plugin registration
const setupPlugins = async () => {
  // Setup Pinia for state management
  const pinia = createPinia()
  app.use(pinia)
  
  // Register Element Plus with reduced memory options in production
  app.use(ElementPlus, {
    size: 'default',
  })
  
  // Register router
  app.use(router)
  
  // Mount application once everything is ready
  app.mount('#app')
}

// Start the application with error handling
setupPlugins().catch(error => {
  console.error('Error during application initialization:', error)
})
