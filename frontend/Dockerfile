FROM node:lts-alpine
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install

COPY frontend .
EXPOSE 5173
VOLUME [ "app/data" ]
ENTRYPOINT ["npm", "run", "dev"]