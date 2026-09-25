# Employee Management

Spring Boot application for managing employees and departments. It provides a server-rendered web interface with Thymeleaf, REST endpoints, JWT-based authentication, employee statistics, and PostgreSQL persistence.

## Prerequisites

- Java 21
- Docker and Docker Compose

## Quick start

1. Create the local environment file from the repository's environment template.
2. Set database credentials, application port, and optional administrator credentials.
3. Start PostgreSQL, the application, and pgAdmin:

   ```bash
   ./gradlew runLocal
   ```

4. Open the application at <http://localhost:8080>.

The development profile uses PostgreSQL from Docker and updates the database schema automatically. Keep real credentials and JWT secrets out of version control.

## Useful commands

```bash
# Run the test suite
./gradlew test

# Build the executable JAR
./gradlew bootJar

# Stop local services
./gradlew devDown
```

## Features

- Employee create, read, update, and delete operations
- Department listing and creation
- Employee count and department statistics
- Login, registration, logout, and JWT authentication
- Health, info, and metrics endpoints through Spring Boot Actuator
- PostgreSQL in development and production; H2 is available for tests

## Architecture and request flow

The application follows a layered Spring Boot architecture:

1. **Controller layer** receives REST or browser requests and maps them to DTOs, views, and HTTP responses.
2. **Service layer** contains authentication, employee, department, reporting, and utility logic.
3. **Repository layer** uses Spring Data JPA to access the app_user, employee, and department tables.
4. **Entity layer** models users, roles, employees, and departments.
5. **Security filter** reads a JWT before the request reaches the controller and populates Spring Security's SecurityContext.

Typical REST flow:

~~~text
Client
  -> SecurityFilterChain
  -> JwtAuthenticationFilter
  -> Controller
  -> Service
  -> Repository
  -> PostgreSQL / H2
~~~

Browser requests use Thymeleaf views and server-side form validation. REST requests return JSON and are handled by GlobalExceptionHandler.

## Domain model and business rules

| Model | Main fields | Rules |
| --- | --- | --- |
| User | username, encoded password, role | Username is unique. New registrations receive the USER role. |
| Employee | name, unique email, optional department | Name and email are required; email must be valid and unique. |
| Department | name, employee collection | A department can have many employees. An employee may be unassigned. |
| Role | USER, ADMIN | Controls access to read and write operations. |

Employee create and update trim name, email, username, and department name before persistence. A missing department keeps the employee unassigned. A missing employee or department returns 404; duplicate username or email returns 409.

## Authentication and authorization

### Authentication flow

1. Register or log in through the REST API or browser form.
2. The password is checked or stored with BCrypt.
3. A signed JWT is issued with username, role, issue time, and expiration.
4. REST clients send Authorization: Bearer <token>.
5. Browser flows store the token in the EMPLOYEE_AUTH_TOKEN HTTP-only cookie.
6. The filter prioritizes the Authorization header, then falls back to the cookie.
7. Invalid tokens are treated as unauthenticated requests and are rejected by protected routes.

JWT secrets must contain at least 32 characters and are required in every profile; there is no hardcoded fallback. The default token lifetime is PT1H. Production enables secure cookies; local development keeps them usable over HTTP.

### Access policy

| Area | Anonymous | USER | ADMIN |
| --- | --- | --- | --- |
| Register, login, logout | Yes | Yes | Yes |
| Hello and actuator health | Yes | Yes | Yes |
| Employee and department read pages | No | Yes | Yes |
| Employee list/report API reads | No | Yes | Yes |
| Create, update, delete employee | No | No | Yes |
| Create department and open add-employee page | No | No | Yes |
| Actuator endpoints other than health | No | No | Yes |

In the dev profile, `APP_SECURITY_ENABLED=false` may be used for local troubleshooting and permits all routes. Production always uses authentication and CSRF protection; its security policy cannot be disabled through environment flags. CSRF is enabled by default for browser forms; versioned API routes are excluded so bearer-token clients can call them. Browser forms include the CSRF token automatically. Actuator health is public but returns status only; other actuator endpoints require ADMIN.

## Endpoints

### Employee API query behavior

GET /api/v1/employees accepts:

- name: case-insensitive partial match on employee name
- department: case-insensitive partial match on department name
- page: zero-based page number
- size: requested page size, capped at 100
- sort: Spring Data sort expression; name and id are used when no sort is supplied

When both filters are present, they are combined with AND. The service always appends id ASC when the requested sort does not already include id, which keeps pagination deterministic.

### Response and error conventions

- Successful create operations return 201 Created and a Location header.
- Successful delete returns 204 No Content.
- Validation failures return 400 with a message and field-level errors.
- Malformed JSON returns 400 with a clear request-body error.
- Business conflicts return 409.
- Missing resources return 404.
- Invalid credentials return 401 with a generic message.
- Unexpected failures return 500 without exposing internal exception details.

Web pages are available under `/login`, `/register`, `/employees/list`, `/employees/add`, `/employees/statistics`, and `/departments`.

REST endpoints:

| Method | Endpoint | Purpose | Access | Success response |
| --- | --- | --- | --- | --- |
| POST | `/api/v1/auth/register` | Register a new user | Public | `201 Created` + JWT payload |
| POST | `/api/v1/auth/login` | Authenticate a user | Public | `200 OK` + JWT payload |
| GET | `/api/v1/employees` | List, filter, and paginate employees | USER, ADMIN | `200 OK` + paged employee data |
| POST | `/api/v1/employees` | Create an employee | ADMIN | `201 Created` + employee data |
| GET | `/api/v1/employees/{id}` | Get one employee | USER, ADMIN | `200 OK` + employee data |
| PUT | `/api/v1/employees/{id}` | Update an employee | ADMIN | `200 OK` + employee data |
| DELETE | `/api/v1/employees/{id}` | Delete an employee | ADMIN | `204 No Content` |
| GET | `/api/v1/departments` | List departments | USER, ADMIN | `200 OK` + department list |
| POST | `/api/v1/departments` | Create a department | ADMIN | `201 Created` + department data |
| GET | `/api/v1/reports/employees/count` | Return total employee count | USER, ADMIN | `200 OK` + count |
| GET | `/api/v1/reports/employees/statistics` | Return totals grouped by department | USER, ADMIN | `200 OK` + statistics |
| GET | `/api/v1/hello` | Return a greeting | Public | `200 OK` + text |
| GET | `/actuator/health` | Check application health | Public | `200 OK` + health status |

Employee list pagination defaults to 20 records per page and is capped at 100 records per request. Results are ordered deterministically by name and then id. Browser forms use CSRF tokens; REST `/api/**` requests remain CSRF-exempt for bearer-token clients.

Before deploying to an existing production database, apply unique constraints for `app_user.username` and `employee.email`; production schema validation expects them.

## Configuration

### Environment variables

| Variable | Purpose | Default / note |
| --- | --- | --- |
| DB_HOST, DB_PORT | Database host and port | Required by the active profile |
| DB_NAME | PostgreSQL database name | Example: employee-management |
| DB_USERNAME, DB_PASSWORD | Database credentials | Keep secrets outside version control |
| APP_PORT | HTTP port | 8080 |
| JWT_SECRET | JWT signing key | At least 32 characters; required in production |
| JWT_EXPIRATION | JWT lifetime | PT1H |
| APP_SECURITY_ENABLED | Enable or disable route security in dev | true; ignored in prod |
| APP_SECURITY_CSRF_ENABLED | Enable browser CSRF protection in dev | true; ignored in prod |
| ADMIN_USERNAME, ADMIN_PASSWORD | Optional first admin account | Created once at startup; password requires 12+ chars with upper/lower/digit/special |
| PGADMIN_PORT, PGADMIN_EMAIL, PGADMIN_PASSWORD | pgAdmin access | Used by Docker Compose |

The application imports an optional environment file based on ENV_FILE_SUFFIX. The local Gradle task uses the local environment file; development, staging, and production tasks use their corresponding suffixes.

Never commit environment files or reuse exposed credentials. Rotate any database, pgAdmin, admin, or JWT credentials that have ever been committed, then purge them from repository history using the repository's approved history-rewrite process.

### Profiles and schema strategy

- **dev**: PostgreSQL with spring.jpa.hibernate.ddl-auto=update; useful for local development.
- **prod**: PostgreSQL with spring.jpa.hibernate.ddl-auto=validate; the schema must already exist and match the entities.
- **test**: H2 is supplied as a test runtime database.

Do not use automatic schema updates as a production migration strategy. Apply database constraints and migrations before starting a production deployment.

Environment-specific variables support local, development, staging, and production deployments. The application provides `dev` and `prod` Spring profiles. Use the repository's environment template to create the required local configuration before starting Docker Compose.

## Security and reporting behavior

### Reports and caching

- GET /api/v1/reports/employees/count returns the total employee count.
- GET /api/v1/reports/employees/statistics returns the total and employee counts grouped by department.
- Department statistics include departments with zero employees because the query uses a left join.
- Report results are cached with Caffeine for one minute.
- Employee create, update, and delete evict employee count and statistics caches.
- Department creation evicts the statistics cache.
- A scheduler writes a system status log every 30 seconds.

Each request passes through `JwtAuthenticationFilter`. The application checks a Bearer token first, then the `EMPLOYEE_AUTH_TOKEN` cookie. A valid token creates an `Authentication` in `SecurityContextHolder`; a request without a token can only access endpoints configured with `permitAll()`.

## Project layout

```text
employee-management/
├── .env.example                         Environment variable template
├── Dockerfile                            Application container image
├── compose.yaml                          Local PostgreSQL, application, and pgAdmin services
├── Gradle project configuration           Dependencies, plugins, and local service tasks
├── settings.gradle                       Gradle project settings
├── gradlew / gradlew.bat                 Gradle wrapper entry points
├── gradle/wrapper/                       Gradle wrapper distribution metadata
├── src/
│   ├── main/
│   │   ├── java/com/learning/employeemanagement/
│   │   │   ├── EmployeeManagementApplication.java  Spring Boot entry point
│   │   │   ├── config/                  Application, security, and admin initialization
│   │   │   ├── constant/                Shared API, web, actuator, cache, and security paths
│   │   │   ├── controller/              REST and server-rendered MVC controllers
│   │   │   ├── dto/                     Request, response, form, and report data objects
│   │   │   ├── entity/                  JPA entities and the user role model
│   │   │   ├── exception/                API error models and global exception handling
│   │   │   ├── repository/               Spring Data repositories and projections
│   │   │   ├── security/                 JWT authentication and user details services
│   │   │   └── service/                  Business logic, reports, utilities, and scheduler
│   │   └── resources/
│   │       ├── application.yaml          Shared Spring configuration
│   │       ├── application-dev.yaml      Development profile configuration
│   │       ├── application-prod.yaml     Production profile configuration
│   │       └── templates/                Thymeleaf views
│   │           ├── auth/                 Login and registration pages
│   │           ├── departments/          Department pages
│   │           ├── employees/            Employee CRUD and statistics pages
│   │           └── fragments/            Shared layout and navigation fragments
│   └── test/
│       ├── java/com/learning/employeemanagement/  Controller, security, service, and app tests
│       └── resources/application.yaml             Test profile configuration
├── README.md                             Project documentation and setup guide
└── .gitignore                            Version-control exclusions
```

Generated directories are intentionally omitted. They contain Gradle caches, compiled classes, test reports, and packaged output rather than source files.

## Local services

Docker Compose starts three services:

- **postgres**: PostgreSQL 16 with a health check.
- **app**: the Spring Boot application, connected to PostgreSQL through the internal service name postgres.
- **pgadmin**: database administration UI, exposed on the configured pgAdmin port.

The application waits for PostgreSQL to become healthy before starting. The default local URLs are:

- Application: <http://localhost:8080>
- pgAdmin: <http://localhost:5050>
- PostgreSQL: localhost:5432

## Testing

Run the test suite with:

~~~bash
./gradlew test
~~~

The tests cover application startup, REST controllers, Thymeleaf controllers, validation, authentication registration, CSRF behavior, employee pagination and duplicate-email handling, reports, actuator health, and utility services.

## License

No license has been declared for this project yet.
