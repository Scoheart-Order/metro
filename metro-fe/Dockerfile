# Build stage
FROM node:18-alpine AS build

# Set working directory
WORKDIR /app

# Copy package.json and npmrc
COPY package*.json .npmrc ./

# Install dependencies
RUN npm install

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Nginx stage
FROM nginx:alpine

# Copy built files from build stage
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/templates/default.conf.template

# Create startup script for environment variable substitution
RUN echo '#!/bin/sh \n\
envsubst "`env | awk -F = \'{printf \"$%s \", $1}\'`" < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf \n\
nginx -g "daemon off;"' > /docker-entrypoint.sh && \
chmod +x /docker-entrypoint.sh

# Expose port 3333
EXPOSE 3333

# Start nginx with our custom entrypoint
ENTRYPOINT ["/docker-entrypoint.sh"] 