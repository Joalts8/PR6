# AGENTS.md - SpringUMA Project Context

## Project Overview
**SpringUMA** is a medical records system built with Spring Boot. It provides a REST API to manage doctors (`Medico`), patients (`Paciente`), medical images (`Imagen`), and AI-generated reports (`Informe`).

## Technical Stack
- **Language:** Java 21+
- **Framework:** Spring Boot 3.2+
- **Data Access:** Spring Data JPA with H2 (in-memory)
- **Testing:** JUnit 5, MockMvc, WebTestClient
- **CI/CD:** GitHub Actions (Compiling, Testing, Docker build, K8s deployment)
- **API Documentation:** SpringDoc OpenAPI (Swagger)

## Core Guidelines
AI agents MUST adhere to the following guidelines:
1.  **Java Coding:** Follow `JAVA_CODING_GUIDELINES.md`. Focus on Records for DTOs, Sealed classes, Optional, and clean code practices.
2.  **Git/Commits:** Follow `GIT_GUIDELINES.md`. Use Conventional Commits and ensure atomic changes.
3.  **Documentation:** Maintain Javadoc for all public APIs.
4.  **Testing:** Ensure high test coverage and follow the Given-When-Then pattern.

## Domain Model
- `Medico`: 1-to-N relationship with `Paciente`.
- `Paciente`: 1-to-N relationship with `Imagen`.
- `Imagen`: 1-to-N relationship with `Informe`.

## Key Files
- Controllers: `src/main/java/com/uma/example/springuma/controller/`
- Services/Entities: `src/main/java/com/uma/example/springuma/model/`
- Tests: `src/test/java/com/uma/example/springuma/`

## Strategy for Agents
- **Refactoring:** Prioritize moving towards Java 21+ idioms (Records, Optional).
- **Validation:** Always run `./mvnw verify` to ensure both unit and integration tests pass.
- **Security:** Do not expose sensitive data; use DTOs instead of entities in controllers.
