FROM openjdk:17

WORKDIR /app

# Копируем код Java в контейнер
COPY app/src/main/java /app/src/main/java

# Проверяем структуру файлов
RUN ls -R /app/src/main/java

# Компилируем сервер
RUN javac /app/src/main/java/com/example/goodisnear/WebSocketServerApp.java

# Запускаем сервер
CMD java -cp /app/src/main/java com.example.goodisnear.WebSocketServerApp
