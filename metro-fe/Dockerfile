# 构建阶段 - 优化为低内存环境 (2GB RAM)
FROM node:18-alpine AS build
WORKDIR /app

# 设置 Node.js 内存限制
ENV NODE_OPTIONS="--max-old-space-size=1536"
ENV LOW_MEMORY=true
ENV VITE_DEPS_NO_DISCOVERY=true

# 分段复制和安装以优化缓存利用
COPY package*.json .npmrc ./
RUN npm ci --no-audit --prefer-offline

# 复制源代码
COPY . .

# 使用优化的生产环境构建
RUN npm run build

# Nginx阶段
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 3333 