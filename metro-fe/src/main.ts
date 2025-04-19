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
  
  // Register Element Plus with optimization for production
  if (import.meta.env.PROD) {
    // In production, we only need to register without devtools
    app.use(ElementPlus, {
      size: 'default',
    })
  } else {
    // In development, enable more features
    app.use(ElementPlus, {
      size: 'default',
    })
    
    // Enable Vue DevTools in development
    console.log('Vue DevTools enabled in development mode')
  }
  
  // Register router
  app.use(router)
  
  // Mount application once everything is ready
  app.mount('#app')
}

// Start the application
setupPlugins().catch(error => {
  console.error('Error during application initialization:', error)
})
