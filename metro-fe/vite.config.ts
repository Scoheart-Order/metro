import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
// @ts-ignore
import VueDevTools from 'vite-plugin-vue-devtools';
import { fileURLToPath, URL } from 'node:url';
import { visualizer } from 'rollup-plugin-visualizer';
import compression from 'vite-plugin-compression';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

// https://vite.dev/config/
export default defineConfig(({ mode, command }) => {
  const isProd = mode === 'production';
  const isAnalyze = process.env.ANALYZE === 'true';
  const isLowMemory = process.env.LOW_MEMORY === 'true';
  
  return {
    plugins: [
      vue(),
      // Add auto import and component resolver for Element Plus
      AutoImport({
        resolvers: [ElementPlusResolver()],
      }),
      Components({
        resolvers: [ElementPlusResolver()],
      }),
      // Only enable devtools in development
      mode === 'development' && VueDevTools(),
      // Generate bundle size analysis report only when explicitly requested
      isProd && isAnalyze && visualizer({
        filename: 'dist/stats.html',
        open: false,
        gzipSize: true,
      }),
      // Enable Gzip compression in production with memory constraints
      isProd && compression({ 
        algorithm: 'gzip',
        threshold: 10240, // Only compress files > 10KB
        verbose: false,
      }),
      // Enable Brotli compression conditionally - not enabled in low memory mode
      isProd && !isLowMemory && compression({
        algorithm: 'brotli' as any,
        threshold: 10240,
        verbose: false,
      }),
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
      // Higher chunk size warning limit for Element Plus
      chunkSizeWarningLimit: 800,
      // Memory optimization for assets
      assetsInlineLimit: 4096,
      // CSS optimization for memory constraints
      cssCodeSplit: false,
      // Use esbuild for CSS minification too (instead of lightningcss that requires additional dependency)
      cssMinify: 'esbuild',
      rollupOptions: {
        output: {
          // Manual chunk splitting strategy
          manualChunks: {
            'vendor-vue': ['vue', 'vue-router', 'pinia'],
            'vendor-element': ['element-plus'],
            'vendor-utils': ['axios', 'date-fns'],
          },
          // Simpler chunk naming for better caching
          chunkFileNames: 'assets/[name]-[hash].js',
          assetFileNames: 'assets/[name]-[hash][extname]',
        },
      },
      // Use esbuild for minification (more memory efficient)
      minify: 'esbuild',
      terserOptions: {
        compress: {
          drop_console: isProd,
          drop_debugger: isProd,
        },
        // Disable parallel processing to reduce memory usage
        parallel: false,
      },
    },
    optimizeDeps: {
      // Pre-bundle dependencies for faster development
      include: ['vue', 'vue-router', 'pinia', 'element-plus', 'axios'],
      // In production builds, we don't need dependency discovery
      ...(command === 'build' ? { noDiscovery: true } : {})
    },
    server: {
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          configure: (proxy, options) => {
            // Simplified proxy logging
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log(`Proxying ${req.method} ${req.url} → ${options.target}${proxyReq.path}`);
            });

            proxy.on('error', (err, req, res) => {
              console.error(`Proxy error: ${err.message}`);
            });
          },
        },
      },
    },
  };
});
