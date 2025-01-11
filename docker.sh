# сборка контейнера
docker build -t my_short_link_app .
# запуск контейнера
docker run -d -p 8088:8088 my_short_link_app