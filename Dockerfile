# Этап сборки (Gradle)
FROM gradle:9.0.0-jdk21 AS build

WORKDIR /app
COPY . .

WORKDIR /app/composeApp

# Очистка старых файлов (опционально)
RUN rm -rf /app/composeApp/build/dist/wasmJs/developmentExecutable/* && \
     rm -rf /usr/share/nginx/html/*

# Сборка в режиме разработки
RUN gradle wasmJsBrowserDevelopmentExecutableDistribution

# Этап запуска (Nginx)
FROM nginx:alpine

# Копируем собранные файлы в папку Nginx
COPY --from=build /app/composeApp/build/dist/wasmJs/developmentExecutable /usr/share/nginx/html

# Порт 80 и запуск Nginx
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]