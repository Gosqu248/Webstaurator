---
description: Java 21 coding standards for DMS
paths:
  - "**/*.java"
---

# Java Code Instructions

## Java 21 Features

### Records for Immutable Data
Use Records for DTOs, value objects, and return types:

```java
public record UserDto(String id, String name, String email) {}

// With validation
public record EmailAddress(String value) {
    public EmailAddress {
        Objects.requireNonNull(value, "Email cannot be null");
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
```

**When NOT to use Records**: JPA entities, classes needing inheritance, mutable state

### Sealed Classes
For restricted type hierarchies:

```java
public sealed interface Result<T> permits Success, Error {
    record Success<T>(T value) implements Result<T> {}
    record Error<T>(String message, Throwable cause) implements Result<T> {}
}
```

### Pattern Matching with Switch
Prefer over if-else chains:

```java
return switch (obj) {
    case String s when s.isEmpty() -> "Empty string";
    case String s -> "String: " + s.toUpperCase();
    case Integer i when i > 0 -> "Positive: " + i;
    case null -> "null value";
    default -> "Unknown type";
};
```

### Text Blocks
For multi-line strings (SQL, JSON, HTML):

```java
String query = """
    SELECT id, name, email
    FROM users
    WHERE status = 'active'
    """;
```

## Virtual Threads NOT Supported

Spring Boot 2.7.x does not support Virtual Threads. **DO NOT use**:
- `Executors.newVirtualThreadPerTaskExecutor()`
- `Thread.startVirtualThread()`

**Alternatives**: `@Async` with `TaskExecutor`, `CompletableFuture`, Kotlin coroutines

## Code Style

### Dependency Injection
Use constructor injection:

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EventManager eventManager;
}
```

### Imports Organization
- Remove unused imports
- No wildcards (`import java.util.*`)
- Group: `java.*` -> `javax.*` -> third-party -> project

### Null Safety
- Use `@NonNull` and `@Nullable` annotations (JSR-305)
- Use `Objects.requireNonNull()` for preconditions
- Return `Optional` for nullable results

## Logging

Use SLF4J with Logback:

```java
private static final Logger logger = LoggerFactory.getLogger(UserService.class);

logger.info("Created user userId={}", user.getId());
logger.error("Failed to process request", e);
```

### Performance Logging
Use Guava's Stopwatch:

```java
import com.google.common.base.Stopwatch;

Stopwatch stopwatch = Stopwatch.createStarted();
try {
    // I/O operations
    logger.info("Completed in {}", stopwatch);
} catch (Exception e) {
    logger.error("Failed after {}", stopwatch, e);
    throw e;
}
```

## JPA Entity Design

### Entity Rules
- **Must be mutable classes** with getters/setters
- **Do NOT use**: Records, Lombok `@Data`/`@EqualsAndHashCode`/`@ToString`
- **Acceptable**: Lombok `@Getter`/`@Setter`
- Override `equals()` and `hashCode()` manually based on business keys

```java
@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return email != null && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
```

### Native Query SQL Dialect
Native queries must use **MySQL 5.7 syntax**, not SQL-standard or PostgreSQL:
- `TIMESTAMPADD(MONTH, -1, NOW())` for date arithmetic (works in both MySQL and H2 tests; avoid `now() - interval 1 month` and `DATE_SUB()` which fail in H2)
- `IFNULL()` not `COALESCE()` (when only 2 args)
- `DATE_FORMAT()` not `TO_CHAR()`
- `GROUP_CONCAT()` not `STRING_AGG()`

### Avoid PersistentEntityManager.findById()
Create specific repository queries instead:

```java
@Query("SELECT u FROM User u WHERE u.id = :id AND u.serviceProviderId = :spId")
Optional<User> findByIdAndServiceProvider(@Param("id") Long id, @Param("spId") Long serviceProviderId);
```

## JMS Listener Pattern

```java
@Component
@EnableJms
@RequiredArgsConstructor
public class ExampleMDP {
    private static final Logger errorLogger = LoggerFactory.getLogger(ErrorLogger.class);

    private final ExampleService exampleService;
    private final ObjectMapper mapper;

    @JmsListener(destination = "EXAMPLE_QUEUE")
    public void listener(JmsMessage jmsMessage) {
        try {
            // Handle message
        } catch (Exception e) {
            errorLogger.error("listener:EXAMPLE_QUEUE handleException() ", e);
            throw e; // Re-throw for retry/DLQ
        }
    }
}
```

## Error Handling

- Use custom exceptions extending `DmsException`
- Provide multiple constructors: message-only, message + cause, cause-only
- Include meaningful error messages with context

```java
public class UserNotFoundException extends DmsException {
    public UserNotFoundException(Long userId) {
        super(ErrorType.NOT_FOUND, "User not found: " + userId);
    }
}
```

## Performance

- Use `StringBuilder` for string concatenation in loops
- Prefer `EnumSet` and `EnumMap` for enum collections
- Use primitive streams for numeric processing
- Use `CompletableFuture` for async operations

## Kotlin Interop

- Add `@NonNull`/`@Nullable` annotations for Kotlin callers
- Return `Optional` for nullable results
- Use `@JvmOverloads` equivalent patterns when needed
