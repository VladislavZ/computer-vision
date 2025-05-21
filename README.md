# Проект для изучения Computer Vision

## Описание
Этот проект представляет собой Spring Boot приложение для работы с компьютерным зрением.

## Технологии
- Java 17
- Spring Boot 3.2.3
- Webcam Capture 0.3.12
- OpenCV 4.7.0
- Lombok
- Maven

## Требования
- JDK 17 или выше
- Maven 3.6 или выше
- Веб-камера

## Установка и запуск

1. Клонируйте репозиторий:
```bash
git clone [URL репозитория]
```

2. Перейдите в директорию проекта:
```bash
cd computer-vision
```

3. Соберите проект с помощью Maven:
```bash
mvn clean install
```

4. Запустите приложение:
```bash
mvn spring-boot:run
```

## Структура проекта
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── computervision/
│   │               ├── ComputerVisionApplication.java
│   │               ├── controller/
│   │               ├── service/
│   │               └── model/
│   └── resources/
│       └── application.properties
└── test/
    └── java/
```

## Функциональность
- Захват изображений с веб-камеры
- Распознавание лиц в реальном времени
- Обработка изображений с помощью OpenCV
- REST API для взаимодействия с приложением

## API Endpoints
(Будут добавлены по мере разработки)

## Лицензия
MIT

## Автор
[VZ] 