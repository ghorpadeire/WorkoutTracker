# Workout Tracker API

A RESTful backend API for tracking workouts, managing exercises, and generating progress reports. Built with Spring Boot and secured with JWT authentication.

## Features

- **User Authentication**: Secure signup and login with JWT tokens
- **Exercise Catalog**: Pre-seeded database with 32+ exercises across cardio, strength, and flexibility categories
- **Workout Management**: Create, update, delete, and schedule workouts
- **Progress Tracking**: Generate reports and track workout statistics
- **API Documentation**: Interactive Swagger UI documentation

## Tech Stack

- **Java 21**
- **Spring Boot 3.2**
- **Spring Security** with JWT
- **Spring Data JPA**
- **MySQL**
- **Springdoc OpenAPI** (Swagger)
- **Lombok**
- **JUnit 5** & **Mockito**

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+
- MySQL 8.0+

### Database Setup

```sql
CREATE DATABASE workout_tracker;
```

### Configuration

Update `src/main/resources/application.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/workout_tracker
    username: your_username
    password: your_password
```

Or use environment variables:
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret (min 256 bits)

### Running the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Run with dev profile (verbose logging)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start at `http://localhost:8080`

### Running Tests

```bash
mvn test
```

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/login` | Authenticate and get JWT token |

### Exercises (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/exercises` | Get all exercises |
| GET | `/api/exercises/{id}` | Get exercise by ID |
| GET | `/api/exercises/category/{category}` | Filter by category |
| GET | `/api/exercises/muscle-group/{group}` | Filter by muscle group |

### Workouts (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/workouts` | Create a new workout |
| GET | `/api/workouts` | Get all user workouts |
| GET | `/api/workouts/{id}` | Get workout by ID |
| PUT | `/api/workouts/{id}` | Update a workout |
| DELETE | `/api/workouts/{id}` | Delete a workout |
| GET | `/api/workouts/scheduled` | Get scheduled workouts |

### Reports (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports/summary` | Get workout summary stats |
| GET | `/api/reports/progress` | Get weekly progress |
| GET | `/api/reports/exercises/{id}` | Get exercise statistics |

## Example Usage

### Register a User
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Create a Workout
```bash
curl -X POST http://localhost:8080/api/workouts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Morning Workout",
    "description": "Full body strength training",
    "scheduledAt": "2024-02-15T08:00:00",
    "exercises": [
      {
        "exerciseId": 1,
        "sets": 3,
        "reps": 10,
        "weight": 60.0
      }
    ]
  }'
```

## Project Structure

```
src/
├── main/
│   ├── java/com/workout/tracker/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Exception handlers
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # JWT & security
│   │   ├── seeder/          # Data seeders
│   │   └── service/         # Java Business logic 
│   └── resources/
│       ├── application.yml
│       └── data/exercises.json
└── test/
    └── java/com/workout/tracker/
        ├── controller/      # Controller tests
        └── service/         # Service tests
```

## License

This project is licensed under the MIT License.
