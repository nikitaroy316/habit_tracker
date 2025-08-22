# Habit Tracker Backend API

This is the backend API for the Habit Tracker application, built with Spring Boot.

---

## User Controller

| HTTP Method | Endpoint                 | Description                 |
|-------------|--------------------------|-----------------------------|
| GET         | `/api/users/profile`     | Get current user profile    |
| PUT         | `/api/users/profile`     | Update current user profile |
| POST        | `/api/users/register`    | Register a new user         |
| POST        | `/api/users/login`       | User login                 |
| GET         | `/api/users`             | Get list of all users       |
| GET         | `/api/users/{id}`        | Get user details by ID      |
| DELETE      | `/api/users/{id}`        | Delete user by ID           |

---

## Habit Controller

| HTTP Method | Endpoint                   | Description                     |
|-------------|----------------------------|---------------------------------|
| POST        | `/api/habits`              | Create a new habit              |
| POST        | `/api/habits/users/{userId}` | Create a new habit for a user  |
| GET         | `/api/habits/{id}`          | Get habit details by ID         |
| DELETE      | `/api/habits/{id}`          | Delete a habit by ID            |
| GET         | `/api/habits/users/{id}`    | Get all habits for a user       |

---

## Habit Check-in Controller

| HTTP Method | Endpoint                    | Description                     |
|-------------|-----------------------------|---------------------------------|
| POST        | `/api/checkins`             | Create a new check-in           |
| POST        | `/api/checkins/habits/{habitId}` | Create a check-in for a habit |
| GET         | `/api/checkins/habit/{id}`  | Get check-ins for a habit       |
| DELETE      | `/api/checkins/{id}`        | Delete a check-in by ID         |

---

## Getting Started

### Tools and Technologies

IntelliJ IDEA: IDE for Java
Maven: Build and dependency management
Git: Version control
Java: Programming language
Spring Boot: Java web framework
MVC Architecture: Design pattern
Spring Security: Authentication and authorization
RESTful APIs: API design
JWT (JSON Web Tokens): Secure authentication
Spring Data JPA: Database operations
Postman: API testing

## Postman Collection
You can import the [Habit Tracker Postman Collection](./postman/HabitTracker.postman_collection.json) into Postman to test all API endpoints.

### Running the application

1. Configure your database in `application.properties`.
2. Build and run the app:

```bash
mvn clean install
mvn spring-boot:run
