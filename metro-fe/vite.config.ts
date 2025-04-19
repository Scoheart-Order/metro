import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
// @ts-ignore
import VueDevTools from 'vite-plugin-vue-devtools';
import { fileURLToPath, URL } from 'node:url';
import { visualizer } from 'rollup-plugin-visualizer';
import compression from 'vite-plugin-compression';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const isProd = mode === 'production';
  
  return {
    plugins: [
      vue(),
      // Only enable devtools in development
      mode === 'development' && VueDevTools(),
      // Generate bundle size analysis report in production
      isProd && visualizer({
        filename: 'dist/stats.html',
        open: false,
        gzipSize: true,
      }),
      // Enable Gzip/Brotli compression in production
      isProd && compression({ algorithm: 'gzip' }),
      isProd && compression({ algorithm: 'brotli' }),
    ].filter(Boolean),
    base: '/',
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    build: {
      // Only generate sourcemaps in development
      sourcemap: !isProd,
      // Reduce console output in production build
      reportCompressedSize: false,
      // Target newer browsers for better code optimization
      target: 'es2015',
      // Set chunk size warning limit
      chunkSizeWarningLimit: 500,
      rollupOptions: {
        output: {
          // Clean CSS output
          cssCodeSplit: true,
          // Implement manual chunk splitting
          manualChunks: {
            // Split vendor dependencies
            'vendor-vue': ['vue', 'vue-router', 'pinia'],
            'vendor-element': ['element-plus'],
            'vendor-utils': ['axios', 'date-fns'],
            // Add other chunks as needed
          },
          // Improve chunk naming for better caching
          chunkFileNames: isProd ? 'assets/[name]-[hash].js' : 'assets/[name].js',
          // Ensure CSS chunk names match JS chunks for proper caching
          assetFileNames: isProd ? 'assets/[name]-[hash][extname]' : 'assets/[name][extname]',
        },
      },
      // Minify options
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: isProd,
          drop_debugger: isProd,
        },
      },
    },
    optimizeDeps: {
      // Pre-bundle dependencies for faster development
      include: ['vue', 'vue-router', 'pinia', 'element-plus', 'axios'],
    },
    server: {
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          configure: (proxy, options) => {
            // Proxy request start
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('-------------- 请求开始 --------------');
              console.log(`原始请求: ${req.method} ${req.url || ''}`);
              console.log(`代理目标: ${options.target}${proxyReq.path}`);
            });

            // Proxy response start
            proxy.on('proxyRes', (proxyRes, req, res) => {
              console.log('-------------- 响应开始 --------------');
              console.log(
                `响应状态: ${proxyRes.statusCode} ${proxyRes.statusMessage}`
              );
              console.log(`原始请求: ${req.method} ${req.url || ''}`);
              console.log(`代理到: ${options.target}${req.url || ''}`);
            });

            // Proxy error
            proxy.on('error', (err, req, res) => {
              console.log('-------------- 代理错误 --------------');
              console.log(`原始请求: ${req.method} ${req.url || ''}`);
              console.log(`代理目标: ${options.target}${req.url || ''}`);
            });

            // Proxy end
            proxy.on('end', (req, res, proxyRes) => {
              console.log('-------------- 请求结束 --------------\n');
            });
          },
        },
      },
    },
  };
});
