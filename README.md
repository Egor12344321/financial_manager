# ФИНАНСОВЫЙ МЕНЕДЖЕР

В данном проекте (консольное приложение) реализована работа с транзакциями пользователя, информация о которых сохраняется в базу данных PostgreSQL.

## Реализованы следующие функции:
- вывод текущего баланса
- добавление и сохранение новой транзакции в БД
- вывод истории всех операций
- редактирование транзакции (пользователь вводит пункт в транзакции, который хочет изменить, он может менять транзакцию до тех пор, пока не нажмет кнопку выхода из редактирования)
- удаление транзакции
- вывод транзакций и их сумму за определенный период
- вывод суммы транзакций по категориям
- выход из программы

## Технологии:
- PostgreSQL + Spring JDBC + Hibernate
- Maven
- Spring Framework 
- Hibernate ORM

## Структура проекта:
```
src/
├── main/
│   ├── java/
│   │   ├── model/
│   │   │   └── Transaction.java
│   │   ├── org.example.jdbc.dao/
│   │   │   ├── Service/
│   │   │   │   └── FinanceService.java
│   │   │   ├── ConfigLoader.java
│   │   │   ├── DatabaseConnection.java
│   │   │   ├── Main.java
│   │   │   ├── TestConnection.java
│   │   │   └── TransactionDao.java
│   │   └── spring/
│   │       ├── AppConfig.java
│   │       ├── FinanceServiceSpring.java
│   │       ├── MainSpring.java
│   │       └── TransactionDaoHibernate.java
│   └── resources/
│       ├── application.properties
│       └── config.properties
├── test/
├── target/
├── .gitignore
└── pom.xml
```

## Поля модели Transaction:
- id
- amount
- type
- category
- description
- date