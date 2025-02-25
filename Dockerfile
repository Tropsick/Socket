# Используем образ JDK 17 (или нужную версию)
FROM openjdk:17

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем все файлы проекта
COPY . .

# Компилируем сервер
RUN javac WebSocketServerApp.java

# Запускаем сервер
CMD ["java", "Socket"]
