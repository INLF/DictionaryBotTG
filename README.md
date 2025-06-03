
Как запустить локально проект:

1. Нужно иметь на устройстве JVM 17 + gradle + docker/docker compose + Ngork (https://ngrok.com/download)
2. Создаем бота в https://t.me/BotFather и запоминаем botToken
3. В корне проекта пишем: `docker compose -f local.docker-compose.yaml up -d`
4. Нажимаем в idea -> edit configuration -> в active profiles пишем local (для того, чтобы сервер стартанул с
   application-local.yaml конфигурацией)
5. Также в idea -> edit configuration -> modify options -> environment variables ->  TELEGRAM_BOT_TOKEN=botToken (из bot
   father)
6. В idea -> edit configuration -> modify options -> program arguments 
   добавляем --db.init.json-file= `${path_to_json_db_file}`
7. Запускаем ngrok и пишем `ngrok http 8080` и копируем forwarding https url
8. Далее выполняем запрос (get запрос => можно просто в браузере
   открыть) https://api.telegram.org/bot${botToken}/setWebhook?url=${ngrok_url}/telegram, где `${botToken}` меняем на
   токен бота и `${ngrok_url}` меняем на ngrok https url

9. Запускаем Spring Boot
10. Пишем боту созданному в BotFather

[//]: # (    docker compose down -v --remove-orphans)

[//]: # (    docker builder prune -f                      # очистка кеша билда)

[//]: # (    docker rmi dictionarybot-spring-app:latest   # удаление старого образа)

[//]: # (    docker compose build --no-cache)

[//]: # (    docker compose up -d)
