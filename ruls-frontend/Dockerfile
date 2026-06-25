# Use an official Node.js runtime as a parent image
FROM node:18-alpine

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to install dependencies
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY . .

# Build the application
RUN npm run build

# Serve the application using a lightweight web server
RUN npm install -g serve
CMD ["serve", "-s", "build"]

# Expose the application port
EXPOSE 3000
