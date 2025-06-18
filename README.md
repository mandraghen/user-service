# User-service

This project provides a resource server for managing users, built with Spring Boot and Gradle. 
It follows best practices for microservices architecture and is designed to be easily deployable using Docker.

## Prerequisites

- **Java 21**: Ensure the JDK is installed and configured.
- **Gradle**: Install Gradle for building the project.
- **Docker**: Required for containerized deployment.
- **PostgreSQL**: Docker Compose will manage the database instance automatically.

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/mandraghen/user-service.git
cd user-service
```

### Build the Project

```bash
./gradlew build
```

### Run the Application Locally

```bash
./gradlew bootRun
```

### Run with Docker Compose

1. Ensure Docker is running.
2. Start the services:

```bash
docker compose up
```

This will start the application and manage all external dependencies, including a PostgreSQL database instance.

### Test the Application

Run the tests using:

```bash
./gradlew test
```

## Configuration

### Application Properties

The application is configured using YAML files located in `src/main/resources`:

- `application.yaml`: Default configuration.
- `application-local.yaml`: Configuration for the `local` profile. Active by default.
- `application-cds.yaml`: Configuration for the `cds` profile. It's used for the dockerization.

### Docker Compose

The `compose.yaml` file defines the services required for the application, including a PostgreSQL database.

### Dockerfile

The `Dockerfile` is optimized for layered builds and CDS. It includes a builder stage for extracting dependencies and a runtime stage for efficient startup.

## Profiles

- **local**: Default profile for local development.
- **cds**: Profile optimized for Class Data Sharing (CDS).

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Submit a pull request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
