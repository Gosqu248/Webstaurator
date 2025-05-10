FROM node:22-alpine as build

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .
RUN npm run build

FROM node:22-alpine

WORKDIR /app

COPY --from=build /app/dist/frontend/browser ./dist

RUN npm install -g serve

CMD ["serve", "-s", "dist", "-l", "3000"]
