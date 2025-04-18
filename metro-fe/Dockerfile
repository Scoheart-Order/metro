FROM node:18-alpine

# Set working directory
WORKDIR /app

# Configure NPM to use Taobao registry
COPY .npmrc ./

# Copy package.json and package-lock.json first for better caching
COPY package*.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Expose port 3333
EXPOSE 3333

# Start the preview server on port 3333
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "3333"] 