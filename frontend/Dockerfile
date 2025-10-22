# ---- Build Stage ----
FROM node:20-alpine AS builder

WORKDIR /app

# Recibe la variable como argumento de build
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

COPY package*.json ./
RUN npm ci
COPY . .

# Debug: Imprime la variable para verificar
RUN echo "Building with VITE_API_BASE_URL: $VITE_API_BASE_URL"

RUN npm run build

# ---- Runtime Stage ----
FROM nginx:stable-alpine

# Remover archivos default de nginx
RUN rm -rf /usr/share/nginx/html/*

# Copiar archivos build y configuración
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
