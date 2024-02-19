FROM node:20.11.1-alpine3.19

WORKDIR /app

COPY public public
COPY src src
COPY .env.production ./
COPY .eslintrc.json ./
COPY entrypoint.sh ./
COPY next.config.mjs ./
COPY package-lock.json* ./
COPY package.json ./
COPY postcss.config.js ./
COPY tailwind.config.ts ./
COPY tsconfig.json ./


RUN chmod +x entrypoint.sh

# We need to build the application after the spring boot backend starts up. 
# Build process of nextjs needs connection to backend and there is no way i know about
# that we can build the frontend after an other service has started in docker compose
# (That's why the entrypoint.sh is here)
RUN npm install

ENTRYPOINT ["/bin/sh", "entrypoint.sh"]
