import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router/index'

// 创建 Vue 应用实例
const app = createApp(App)

// 注册插件
app.use(createPinia())
app.use(ElementPlus)
app.use(router)

// 挂载应用
app.mount('#app')

// For development builds, Vue 3 devtools are enabled by default
if (import.meta.env.DEV) {
  console.log('Vue Devtools enabled in development mode')
}
