***Павлюков Владимир Владимирович, группа*** **QAMID45**

# Домашнее задание к занятию «3.2. SQL»

<details><summary>Вводная часть.</summary>

В качестве результата пришлите ссылки на ваши GitHub-проекты в личном кабинете студента на сайте [netology.ru](https://netology.ru).

Все задачи этого занятия нужно делать **в разных репозиториях**.

**Важно**: если у вас что-то не получилось, то оформляйте issue [по установленным правилам](https://github.com/netology-code/aqa-homeworks/blob/master/report-requirements.md).

**Важно**: не делайте ДЗ всех занятий в одном репозитории. Иначе вам потом придётся достаточно сложно подключать системы Continuous integration.

## Как сдавать задачи

1. Инициализируйте на своём компьютере пустой Git-репозиторий.
2. Добавьте в него готовый файл[.gitignore](https://github.com/netology-code/aqa-homeworks/blob/master/.gitignore).
3. Добавьте в этот же каталог код, требуемый в ДЗ.
4. Сделайте необходимые коммиты.
5. Создайте публичный репозиторий на GitHub и свяжите свой локальный репозиторий с удалённым.
6. Сделайте пуш — удостоверьтесь, что ваш код появился на GitHub.
7. Ссылку на ваш проект отправьте в личном кабинете на сайте [netology.ru](https://netology.ru).
8. Задачи, отмеченные как необязательные, можно не сдавать, это не повлияет на получение зачёта.

**Важно**: задачи этого занятия не предполагают подключения к CI.

## Volumes

Пожалуйста, ознакомьтесь с кратким руководством по работе с [volumes](https://github.com/netology-code/aqa-homeworks/blob/master/sql/volumes.md).

## SQL

Пожалуйста, ознакомьтесь с кратким руководством по работе с клиентами [SQL](https://github.com/netology-code/aqa-homeworks/blob/master/sql/mysql-psql.md).

</details>

## [Задача №1: скоро дедлайн](https://github.com/PavlyukovVladimir/PavlyukovVVQamid45AutotestingSQL1)


## Задача №2: backend vs frontend (необязательная)

<details><summary>Развернуть Задача №2: backend vs frontend (необязательная)</summary>

Бэкенд-разработчики сказали, что они всё уже сделали, это фронтендщики тормозят. Поэтому функцию перевода денег с карты на карту мы не можем протестировать через веб-интерфейс.

Зато они выдали нам описание REST API, которое позволяет это сделать, использовать нужно тот же `app-deadline.jar`.

Вот описание API:

- Логин
```http
POST http://localhost:9999/api/auth
Content-Type: application/json

{
  "login": "vasya",
  "password": "qwerty123"
}
```

- Верификация
```http
POST http://localhost:9999/api/auth/verification
Content-Type: application/json

{
  "login": "vasya",
  "code": "599640"
}
```
В ответе, в поле «token» придёт токен аутентификации, который нужно использовать в последующих запросах.

<details>
<summary>Подсказка по REST-assured</summary>

Если вам приходит в ответ следующий JSON:
```json
{
  "status": "ok"
}
```

Вы можете вытащить значение из ответа с помощью REST-assured следующим образом:

```
      String status = ... // ваш обычный запрос  
      .then()
          .statusCode(200)
      .extract()
          .path("status")
      ;

      // используются matcher'ы Hamcrest
      assertThat(status, equalTo("ok"));
```

Если вам нужно вытащить весь ответ, чтобы потом искать по нему, например, если нужно несколько полей, то:

```
      Response response = ... // ваш обычный запрос  
      .then()
          .statusCode(200)
      .extract()
          .response()
      ;

      String status = response.path("status");
      // используются matcher'ы Hamcrest
      assertThat(status, equalTo("ok"));
```

</details>

- Просмотр карт
```http
GET http://localhost:9999/api/cards
Content-Type: application/json
Authorization: Bearer {{token}}
```

Где {{token}} — это значение «token» с предыдущего шага. Фигурные скобки писать не нужно.

- Перевод с карты на карту (любую)
```
POST http://localhost:9999/api/transfer
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "from": "5559 0000 0000 0002",
  "to": "5559 0000 0000 0008",
  "amount": 5000
}
```

Внимательно изучите запросы и ответы и, используя любой инструмент, который вам нравится, реализуйте тесты API.

В результате выполнения этой задачи вы должны положить в репозиторий следующие файлы:
* docker-compose.yml*,
* app-deadline.jar,
* schema.sql,
* код ваших автотестов.

P.S. Всё не может быть хорошо, наверняка разработчики где-то допустили ошибки. Не забывайте заводить issue о найденных багах 😈


</details>

# Подготовка к запуску тестов

Тесты готовят базу автоматически, но при желании можно отключить автоматическую подготовку к запуску и пост обработку,
для этого измените в [классе c константами](jetbrains://idea/navigate/reference?project=PavlyukovVVQamid45AutotestingSQL1&fqn=ru.netology.AuthTest.AuthTest) значения полей:
"PRE_TEST_PREPARATION", "POST_TEST_PREPARATION"
с true на false.

<details><summary>Подготовка к тестированию вручную.</summary>

## Действия перед проведением тестирования

* Запуск БД в контейнере (лучше перед запуском SUT подождать пол минуты, чтобы база построилась и завелись нужные таблицы)
```sh
docker-compose up --build -d
```
* Запуск SUT:
```sh
source .env
java -jar artifacts/app-deadline.jar & echo $! > ./testserver.pid &
```

## Действия после проведения тестирования

* Остановка SUT:
```sh
* kill -TERM $(cat ./testserver.pid)
```
* Остановка и удаление контейнеров:
```sh
docker-compose down
```
* Удаление файлов БД:
```sh
sudo rm -R ./.data
```

</details>

# Запуск тестов

* Runs all tests: `./gradlew test --info`
* Delete previous data about tests: `./gradlew clean`

* [Просмотр отчета(локальное выполнение тестов)](build/reports/tests/test/index.html)

# Багрепорты

* [Нет ошибки когда отправляем перевод на пустой номер карты](https://github.com/PavlyukovVladimir/PavlyukovVVQamid45AutotestingSQL2/issues/1)
* [Нет ошибки, при попытке сделать отрицательный перевод](https://github.com/PavlyukovVladimir/PavlyukovVVQamid45AutotestingSQL2/issues/2)
* [Если баланс в БД не кратен 100, то дробная часть отбрасывается в запросе GET /api/cards](https://github.com/PavlyukovVladimir/PavlyukovVVQamid45AutotestingSQL2/issues/3)
* [Нет ошибки при попытке отправить нулевой перевод](https://github.com/PavlyukovVladimir/PavlyukovVVQamid45AutotestingSQL2/issues/4)
* [Ошибка на стороне сервера(500), когда отправляешь 0,01 руб](https://github.com/PavlyukovVladimir/PavlyukovVVQamid45AutotestingSQL2/issues/5)
* [Нет ошибки, когда отправляешь больше чем есть на карте](https://github.com/PavlyukovVladimir/PavlyukovVVQamid45AutotestingSQL2/issues/6)

# Памятка

<details><summary>Команды linux</summary>

* Проверка открытия порта:
```sh
nc -z -v -w5 <host> <port>
```
* Посмотреть какие процессы занимают порт 9999:
```sh
lsof -i tcp:9999
```
* завершить процесс по его PID:
```sh
kill -TERM <PID>
```
* Узнать текущего пользователя
```sh
echo $(logname)
```
* Узнать UID текущего пользователя
```sh
echo $(id -u $(logname))
```
* Удалить папку
```sh
sudo rm -R ./.data
```

</details>

<details><summary>Команды докера(все команды можно получить с помощью --help)</summary>

* Посмотреть все образы:
```sh
docker image ls --all
```
* Удалить образ:
```sh
docker rmi <IMAGE ID>
```
* Удалить все образы
```sh
docker rmi -f $(docker images -aq)
```
* Посмотреть все контейнеры:
```sh
docker container ls --all
```
* Приостановить контейнер:
```sh
docker container pause <CONTAINER ID>
```
* Возобновить работу после приостановки:
```sh
docker container pause <CONTAINER ID>
```
* Остановить контейнер:
```sh
docker container stop <CONTAINER ID>
```
* Остановить все контейнеры:
```sh
docker container stop $(docker container ls -aq)
```
* Удалить контейнер:
```sh
docker rm <CONTAINER ID>
```
* Удалить все контейнеры
```sh
docker rm -f $(docker container ls --all -aq)
```
</details>

<details><summary>FOR RUN docker as non-root user</summary>
Create the docker group if it does not exist

```sh
sudo groupadd docker
```
Add your user to the docker group.

```sh
sudo usermod -aG docker $USER
```

Log in to the new docker group (to avoid having to log out / log in again; but if not enough, try to reboot):

```sh
newgrp docker
```

reboot pc

</details>