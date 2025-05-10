FROM node:22-alpine as build

WORKDIR /app

COPY package*.json ./

RUN npm install --legacy-peer-deps

COPY . .

CMD [ "npm", "run", "start", "--", "--host", "0.0.0.0" ]
