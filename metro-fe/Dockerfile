# Build stage
FROM node:16 AS build

# 配置 NPM 缓存目录
ARG NPM_CONFIG_CACHE
ENV NPM_CONFIG_CACHE=$NPM_CONFIG_CACHE

WORKDIR /app

# Configure NPM to use Taobao registry
COPY .npmrc ./

# Copy package.json and package-lock.json first for better caching
COPY package*.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the application
RUN npm run build

# Production stage
FROM nginx:stable-alpine

# Copy the build output to the nginx html directory
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"] 