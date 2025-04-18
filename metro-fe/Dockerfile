FROM node:22

# Set working directory
WORKDIR /app

# Configure NPM to use Taobao registry
COPY .npmrc ./

# Copy package.json and package-lock.json first for better caching
COPY package*.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Set environment to use Docker configuration
ENV VITE_DOCKER_ENV=true

# Expose development server port
EXPOSE 3333

# Start development server
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "3333"] 