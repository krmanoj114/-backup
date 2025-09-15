FROM node:18-alpine

RUN addgroup -S nonroot \
    && adduser -S nonroot -G nonroot

USER nonroot

WORKDIR /app

COPY package*.json ./

RUN npm cache clean --force

RUN npm install

RUN mkdir node_modules/.cache && chmod -R 777 node_modules/.cache

COPY ./src /app/src

COPY ./public /app/public

COPY ./.env /app/.env

RUN npm run build

ENV TZ=Asia/Calcutta

EXPOSE 3000

CMD ["npm", "run", "dev"]