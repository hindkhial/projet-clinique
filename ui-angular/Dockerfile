FROM node:22-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY . .

# Expose Angular dev server port
EXPOSE 4200

# Start development server with hot reload
CMD ["npm", "start", "--", "--host", "0.0.0.0", "--poll", "2000"]
