# Родительский образ контейнера с java внутри
FROM eclipse-temurin
# Путь к jar-файлу в качестве аргумента
ARG JAR_FILE=target/*-jar-with-dependencies.jar
# Копирование jar-файла в контейнер
COPY ${JAR_FILE} app.jar
# Путь к файлу с данными в качестве аргумента
ARG DATA_FILE=example_database.txt
# Копирование файла с данными в контейнер
COPY ${DATA_FILE} data.txt
# Открытие порта 8088
EXPOSE 8088
# Команда, которая будет запущена при старте контейнера java -jar ./app.jar --host 127.0.0.1 --port 8088 --file /data.txt --readonly
ENTRYPOINT ["java", "-jar", "/app.jar", "--host", "127.0.0.1", "--port", "8088", "--file", "/data.txt", "--readonly"]