import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';
import { fileURLToPath, URL } from 'node:url';
// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  base: '/',
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
        configure: (proxy, options) => {
          // 代理请求开始
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('-------------- 请求开始 --------------');
            console.log(`原始请求: ${req.method} ${req.url || ''}`);
            console.log(`代理目标: ${options.target}${proxyReq.path}`);
          });

          // 代理响应开始接收
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('-------------- 响应开始 --------------');
            console.log(
              `响应状态: ${proxyRes.statusCode} ${proxyRes.statusMessage}`
            );
            console.log(`原始请求: ${req.method} ${req.url || ''}`);
            console.log(
              `代理到: ${options.target}${(req.url || '').replace(
                /^\/api/,
                ''
              )}`
            );
          });

          // 代理错误
          proxy.on('error', (err, req, res) => {
            console.log('-------------- 代理错误 --------------');
            console.log(`原始请求: ${req.method} ${req.url || ''}`);
            console.log(
              `代理目标: ${options.target}${(req.url || '').replace(
                /^\/api/,
                ''
              )}`
            );
          });

          // 代理结束
          proxy.on('end', (req, res, proxyRes) => {
            console.log('-------------- 请求结束 --------------\n');
          });
        },
      },
    },
  },
});
