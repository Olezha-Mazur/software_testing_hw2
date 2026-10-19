# Автоматизация REST и GraphQL API

Maven-проект с API-автотестами для учебного задания. Тесты не используют заранее известные `id` или `access_token`: нужные значения извлекаются из ответов во время каждого запуска.

## Используемые сервисы

- REST: `https://playground.nileslabs.com/api/v1`
- GraphQL: `https://graphql.postman-echo.com/graphql`

## Реализованные сценарии

- `RestScenarioTest` — один связанный сценарий `POST /posts` → извлечение `id` → `PATCH /posts/{id}` → `GET /posts/{id}`. Проверяются коды, тот же `id`, новое значение `title` и сохранность не изменявшегося `body`.
- `AuthenticationTest` — получает свежий `access_token` через `POST /auth/login` с `username=Bret` и текущим паролем стенда `Password@123`, передаёт его как `Authorization: Bearer ...` в `GET /auth/me` и проверяет наличие идентификатора пользователя. Отдельный негативный тест убеждается, что заведомо неверный токен даёт `401` или `403`. В исходном учебном задании указан `password123`, но публичный стенд принимает актуальный пароль `Password@123`.
- `GraphQlTest` — отправляет GraphQL query и `createPerson` mutation; в обоих случаях проверяет HTTP 200, `data`, отсутствие `errors` и значения возвращаемых полей.
- `postman/api_testing_seminar_v2.postman_collection.json` — импортируемая коллекция со всеми сценариями семинара, включая обязательную subscription `Greetings` через SSE. В соответствующем запросе указаны `Accept: text/event-stream` и `subscription Greetings { greetings }`.

## Дополнительный сценарий

В `AuthenticationTest` добавлен параметризованный негативный тест `rejectsInvalidLoginCredentials`. Он выполняется для двух независимых наборов некорректных учётных данных: правильный пользователь с неверным паролем и несуществующий пользователь с корректным форматом пароля. Для каждого набора проверяются HTTP 401 и текст ошибки `Invalid username or password.`. Сценарий дополняет обязательную проверку неверного Bearer-токена, а не заменяет её.

## Запуск

Требуется JDK 17+ и Maven 3.9+.

```bash
mvn test
```

Команда запускается из корня проекта. Для subscription импортируйте файл коллекции в Postman и нажмите **Send** у запроса `Subscription Greetings SSE`; клиент должен начать получать SSE-события.
