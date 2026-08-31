# Task Management REST API

A backend REST API for managing tasks, built with Java and Spring Boot.

This project provides CRUD operations for tasks along with pagination, sorting, filtering, searching, input validation, JWT authentication, and role-based authorization.

The project also includes automated tests for controller behavior, validation, exception handling, and security-related access control.

## Features

- Create, read, update, and delete tasks
- Pagination
- Sorting
- Filter tasks by completion status
- Search tasks by title
- Request validation
- DTO-based request and response handling
- Centralized exception handling
- JWT authentication
- Role-based authorization
- BCrypt password hashing
- MySQL database integration
- Automated testing with JUnit and Mockito

## Tech Stack

- Java
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- MySQL
- JWT
- Maven
- JUnit
- Mockito
- Postman
- Docker

## Project Structure

```test
src/
├── main/
│   ├── java/
│   │   └── com/lyin/taskapi/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   │           └── impl/
│   └── resources/
│
└── test/
    └── java/
        └── com/lyin/taskapi/
```

## API Endpoints

### Authentication

| Method | Endpoint         | Description                       | Authentication |
|--------|------------------|-----------------------------------|----------------|
| POST   | `/auth/register` | Register a new user               | None           |
| POST   | `/auth/login`    | Authenticate user and receive JWT | None           |

### Tasks

| Method | Endpoint       | Description           | Authentication |
|--------|----------------|-----------------------|----------------|
| POST   | `/tasks`       | Create a new task     | JWT            |
| GET    | `/tasks`       | Get all tasks         | JWT            |
| GET    | `/tasks/{id}`  | Get a task by ID      | JWT            |
| GET    | `/tasks/search`| Search tasks by title | JWT            |
| PUT    | `/tasks/{id}`  | Update a task         | JWT            |
| DELETE | `/tasks/{id}`  | Delete a task         | JWT            |

### Administration

| Method | Endpoint      | Description               | Authentication   |
|--------|---------------|---------------------------|------------------|
| GET    | `/admin/test` | Test administrator access | JWT + ADMIN role |        

### Task Query Parameters

The `GET /tasks` endpoint supports pagination, sorting, and filtering.


#### Pagination
GET /tasks?page=0&size=10

#### Sorting

```http
GET /tasks?sort=title,asc
GET /tasks?sort=title,desc
```

#### Filtering

```http
GET /tasks?completed=true
GET /tasks?completed=false
```

#### Combining Parameters

```http
GET /tasks?completed=false&page=0&size=10&sort=title,asc
```

#### Task Search 
Search is case-insensitive

```http
GET /tasks/search?keyword=Java
```

## Authentication
This API uses JWT (JSON Web Token) authentication.

### Register
Send a POST request to: 
```http
POST /auth/register
```

```json
{
    "username": "testuser",
    "password": "password123"
}
```

### Login
Send a POST request to: POST /auth/login

```json
{
    "username": "testuser",
    "password": "password123"
}
```

successful login returns a JWT

```json
{
    "token": "your-jwt-token"
}
```


### Using JWT

> **Security Note:** Never commit your JWT secret, database password, or other
> sensitive credentials to the repository. Use environment variables or local
> configuration for secrets.

Protected endpoints require the JWT in the Authorization header
Authorization: Bearer <your-jwt-token>

## Role-Based Authorization
The API supports role-based access control using Spring Security.

Users are assigned one of the following roles:

- `USER`
- `ADMIN`

Newly registered accounts receive the `USER` role by default.

Administrator-only endpoints require the `ADMIN` role.
For example:
GET /admin/test
requires:
Authorization: Bearer <admin-jwt-token>
An authenticated user without the required role will receive a `403 Forbidden` response.

## Password Security

User passwords are not stored as plain text.

Passwords are hashed using Spring Security's `BCryptPasswordEncoder` before being stored in the database.

During login, the submitted password is compared against the stored BCrypt hash.

## Validation

Request validation is implemented using Jakarta Bean Validation.

For example, task titles:
- Cannot be blank
- Cannot exceed 100 characters

Task descriptions cannot exceed 500 characters.

Invalid requests return: 400 Bad Request

## Exception Handling

The API uses a centralized exception handler with `@RestControllerAdvice`.

The application currently handles errors such as:

- Task not found
- Invalid request parameters
- Validation failures
- Invalid JSON requests

### Example: Task Not Found

Request: 
```http
GET /tasks/9999
```

Response:
```json
{
    "status": 404,
    "message": "Task not found.",
    "timestamp": "2026-08-..."
}
```

### Example: Invalid Parameter

Invalid parameter values return: 400 Bad Request
This prevents raw framework exceptions from being exposed directly to API clients.

## Testing

The project includes automated tests using JUnit and Mockito.

The tests cover controller behavior including:

- Creating tasks
- Request validation
- Retrieving tasks
- Handling missing tasks
- Retrieving paginated tasks
- Filtering tasks
- Searching tasks
- Updating tasks
- Deleting tasks
- Sorting
- Authentication-related access control
- Exception handling

```md
Current test status:
```text
Tests run: 25
Failures: 0
Errors: 0
Skipped: 0
```


Tests can be executed using Maven:
./mvnw test

## Running the Application Locally

### Prerequisites

Make sure the following are installed:

- Java 17+
- Maven
- MySQL

### Clone the repository
git clone <repository-url>

Navigate into the project:
cd demo

### Configure the database

Create a MySQL database for the application.

Example: CREATE DATABASE taskdb;

Configure the database connection in: src/main/resources/application.properties
Example:
spring.datasource.url=jdbc:mysql://localhost:3306/taskdb
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

Configure the JWT secret: jwt.secret=YOUR_SECRET_KEY

### Run the application

Using the Maven wrapper: ./mvnw spring-boot:run

The API will be available at:
http://localhost:8080

## Example Authentication Workflow
A typical workflow for using the API is:

### 1. Register
```http
POST /auth/register
```

```json
{
    "username": "testuser",
    "password": "password123"
}
```

### 2. Login
```http
POST /auth/login
```

```json
{
    "username": "testuser",
    "password": "password123"
}
```

Copy the JWT returned by the login endpoint.

### 3. Access a protected endpoint
Add the token to the request:

Authorization: Bearer <your-jwt-token>

Then make a request such as: GET /tasks

### 4. Create a task
```http
POST /tasks
```

```json
{
    "title": "Learn Spring Boot",
    "description": "Build and test a REST API",
    "completed": false
}
```

## API Testing

Postman was used to manually test the API endpoints and authentication flow.

Testing includes:

- Registration
- Login
- JWT authentication
- User access
- Admin access
- CRUD operations
- Pagination
- Sorting
- Filtering
- Searching
- Validation
- Error responses

## Project Status

The core REST API functionality is complete.

Completed:

- REST API CRUD operations
- Pagination
- Sorting
- Filtering
- Searching
- DTO architecture
- Validation
- Exception handling
- JWT authentication
- Role-based authorization
- Automated testing

## Future Improvements

Potential future improvements include:

- Refresh tokens
- More granular permissions
- User-specific task ownership
- Integration tests with a test database
- API documentation with OpenAPI/Swagger
- Production environment configuration
- CI/CD pipeline