# restaurant-service — TODO

## Stan aktualny

Serwis ma szkielet: modele, repozytoria, serwis, kontroler, DTOs, migrację Flyway.
**Brak testów.** Kilka bugów i brakujących endpointów.

---

## 1. Błędy (BUG) — do naprawy

### 1.1 `deleteCategory` / `deleteMenuItem` — brak weryfikacji przynależności
**Problem:** `deleteCategory` wywołuje `categoryRepository.deleteById(categoryId)` bez sprawdzenia czy kategoria
należy do danej restauracji. Właściciel restauracji A może usunąć kategorię restauracji B.

**Naprawa:**
```java
// RestaurantService.deleteCategory
findOrThrowOwned(ownerId, restaurantId);
categoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
categoryRepository.deleteById(categoryId);
```
To samo dla `deleteMenuItem` — użyć `menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)`.

### 1.2 `search` — brak obsługi nieprawidłowego `cuisineType`
**Problem:** `CuisineType.valueOf(cuisineType.toUpperCase())` rzuca `IllegalArgumentException` gdy wartość
nie istnieje w enum. `GlobalExceptionHandler` jej nie obsługuje → HTTP 500.

**Naprawa:**
```java
// GlobalExceptionHandler — dodać:
@ExceptionHandler(IllegalArgumentException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public Map<String, String> handleIllegalArg(IllegalArgumentException ex) {
    return Map.of("error", ex.getMessage());
}
```
Opcjonalnie: zastąpić `CuisineType.valueOf` bezpieczną metodą z własnym komunikatem błędu.

### 1.3 `GlobalExceptionHandler` — brakujące handlery
Brak obsługi:
- `MethodArgumentNotValidException` → 400 ze szczegółami walidacji
- `IllegalArgumentException` → 400
- Generic `Exception` → 500 bez stacktrace

**Naprawa** (dodać do `GlobalExceptionHandler`):
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return Map.of("error", message);
}

@ExceptionHandler(Exception.class)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public Map<String, String> handleGeneric(Exception ex) {
    log.error("Unexpected error", ex);
    return Map.of("error", "Internal server error");
}
```

### 1.4 `RestaurantNotFoundException` — używana dla kategorii i pozycji menu
**Problem:** `updateCategory` i `updateMenuItem` rzucają `RestaurantNotFoundException(categoryId)`
gdy nie znaleziono kategorii — mylący komunikat błędu.

**Naprawa:** Dodać osobne wyjątki:
- `CategoryNotFoundException extends RuntimeException`
- `MenuItemNotFoundException extends RuntimeException`
  
Zarejestrować w `GlobalExceptionHandler` → 404.

### 1.5 `@Param` brakuje w JPQL query
**Problem:** `RestaurantRepository.search` używa named parameters `:city`, `:cuisineType`, `:search`
bez `@Param` adnotacji. Działa tylko gdy kompilator zachowuje nazwy parametrów (`-parameters` flag).

**Naprawa:**
```java
Page<Restaurant> search(
    @Param("city") String city,
    @Param("cuisineType") CuisineType cuisineType,
    @Param("search") String search,
    Pageable pageable);
```

---

## 2. Brakujące endpointy

### 2.1 `DELETE /restaurants/{id}` — soft delete
Restauracja powinna być dezaktywowana, nie usuwana (historyczne zamówienia).

```java
// Controller
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deactivate(@RequestHeader("X-User-Id") Long ownerId,
                       @PathVariable Long id) {
    restaurantService.deactivate(ownerId, id);
}

// Service
@Transactional
public void deactivate(Long ownerId, Long restaurantId) {
    Restaurant r = findOrThrowOwned(ownerId, restaurantId);
    r.setIsActive(false);
    restaurantRepository.save(r);
}
```

### 2.2 `GET /restaurants/owner` — lista restauracji właściciela
Repozytorium ma `findAllByOwnerId` ale brak endpointu.

```java
@GetMapping("/owner")
public List<RestaurantResponse> getOwnerRestaurants(
        @RequestHeader("X-User-Id") Long ownerId) {
    return restaurantService.getByOwner(ownerId);
}
```

### 2.3 `GET /restaurants/{id}/hours` — pobieranie godzin otwarcia
Można ustawić godziny (`PUT /{id}/hours`) ale nie pobrać.

Brakuje też `OpeningHoursResponse` DTO:
```java
public record OpeningHoursResponse(Long id, String dayOfWeek, LocalTime openTime, LocalTime closeTime) {
    public static OpeningHoursResponse from(OpeningHours h) { ... }
}
```

### 2.4 `PATCH /restaurants/{id}/menu/items/{itemId}/availability` — toggle dostępności
Właściciel powinien móc włączyć/wyłączyć pozycję bez edycji całości.

```java
@PatchMapping("/{id}/menu/items/{itemId}/availability")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void toggleItemAvailability(@RequestHeader("X-User-Id") Long ownerId,
                                   @PathVariable Long id,
                                   @PathVariable Long itemId,
                                   @RequestParam boolean available) {
    restaurantService.setItemAvailability(ownerId, id, itemId, available);
}
```

### 2.5 `PATCH /restaurants/{id}/categories/{catId}/active` — toggle kategorii
Analogicznie do 2.4 — ukrywanie/pokazywanie całej kategorii.

---

## 3. Testy — PRIORYTET 1

Brak jakichkolwiek testów. Wymagane 80%+ pokrycia (zgodnie ze standardami projektu).

### 3.1 Testy jednostkowe — `RestaurantServiceTest`

Plik: `src/test/java/com/gosqu/restaurant/service/RestaurantServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock RestaurantRepository restaurantRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock MenuItemRepository menuItemRepository;
    @Mock OpeningHoursRepository openingHoursRepository;

    private RestaurantService service;

    @BeforeEach
    void setUp() {
        service = new RestaurantService(restaurantRepository, categoryRepository,
                                        menuItemRepository, openingHoursRepository);
    }
    // ...
}
```

**Przypadki testowe do pokrycia:**

| Metoda | Scenariusz | Oczekiwany rezultat |
|--------|-----------|---------------------|
| `getById` | restauracja istnieje | zwraca `RestaurantResponse` |
| `getById` | restauracja nie istnieje | rzuca `RestaurantNotFoundException` |
| `create` | poprawne dane | zapisuje i zwraca response |
| `update` | właściciel aktualizuje | zapisuje zmiany |
| `update` | inny user próbuje edytować | rzuca `ForbiddenException` |
| `search` | brak filtrów | deleguje do repo z nullami |
| `search` | z cuisineType | konwertuje string → enum |
| `search` | nieprawidłowy cuisineType | rzuca `IllegalArgumentException` |
| `addCategory` | właściciel dodaje | zwraca `CategoryResponse` |
| `deleteCategory` | kategoria innej restauracji | rzuca wyjątek (po bugfixie 1.1) |
| `addMenuItem` | poprawne dane | zwraca `MenuItemResponse` |
| `setOpeningHours` | właściciel ustawia | usuwa stare, zapisuje nowe |
| `updateAvgRating` | restauracja istnieje | ustawia nowy rating |

### 3.2 Testy warstwy web — `RestaurantControllerTest`

Plik: `src/test/java/com/gosqu/restaurant/controller/RestaurantControllerTest.java`

```java
@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean RestaurantService restaurantService;
    @Autowired ObjectMapper objectMapper;
    // ...
}
```

**Przypadki testowe:**

| Endpoint | Scenariusz | HTTP Status |
|----------|-----------|-------------|
| `GET /restaurants` | bez parametrów | 200 |
| `GET /restaurants?cuisineType=INVALID` | błędny enum | 400 |
| `GET /restaurants/{id}` | istnieje | 200 |
| `GET /restaurants/{id}` | nie istnieje | 404 |
| `POST /restaurants` | brak `name` | 400 (walidacja) |
| `POST /restaurants` | poprawne dane | 201 |
| `PUT /restaurants/{id}` | inny właściciel | 403 |
| `POST /restaurants/{id}/categories` | brak nazwy | 400 |
| `PUT /restaurants/{id}/rating` | poprawny internal header | 204 |
| `PUT /restaurants/{id}/rating` | zły `X-Internal-Service` | 403 |
| `PUT /restaurants/{id}/rating` | rating > 5.0 | 400 |

### 3.3 Testy integracyjne — `RestaurantRepositoryIT`

Plik: `src/test/java/com/gosqu/restaurant/repository/RestaurantRepositoryIT.java`

Użyć Testcontainers + PostgreSQL. Serwis ma już `testcontainers-postgresql` w `pom.xml`.

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class RestaurantRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired RestaurantRepository restaurantRepository;
    // ...
}
```

**Przypadki testowe:**

| Metoda | Scenariusz | Oczekiwany rezultat |
|--------|-----------|---------------------|
| `search` | filtr city | zwraca tylko danego miasta |
| `search` | filtr cuisineType | zwraca tylko danego typu |
| `search` | filtr q (name) | zwraca pasujące nazwy |
| `search` | restauracja nieaktywna | nie pojawia się w wynikach |
| `findAllByOwnerId` | owner ma 2 restauracje | zwraca 2 |
| `save` + `findById` | round-trip | pola zachowane |

---

## 4. Ulepszenia opcjonalne (V2)

### 4.1 Caching menu (Redis)
Menu restauracji zmienia się rzadko — warto cachować.

```java
@Cacheable(value = "menu", key = "#restaurantId")
public MenuResponse getMenu(Long restaurantId) { ... }

@CacheEvict(value = "menu", key = "#restaurantId")
public void addMenuItem(...) { ... }
```
Wymaga: dodać `spring-boot-starter-data-redis` do `pom.xml`.

### 4.2 Integracja MinIO — upload zdjęć
Endpointy do przesyłania zdjęć logo/bannera/pozycji menu.
Wymaga: nowy serwis `ImageStorageService`, dependency `io.minio:minio`.

Endpointy:
- `POST /restaurants/{id}/logo`
- `POST /restaurants/{id}/banner`  
- `POST /restaurants/{id}/menu/items/{itemId}/image`

### 4.3 Elasticsearch — wyszukiwanie full-text
Architektura przewiduje Elasticsearch (patrz `ARCHITECTURE.md`).
Aktualna implementacja JPQL `LIKE` nie skaluje się.
Wymaga: `spring-boot-starter-data-elasticsearch`, migracja indeksu.

### 4.4 Silniejsza autoryzacja wewnętrznego endpointu
`PUT /restaurants/{id}/rating` sprawdza tylko nagłówek `X-Internal-Service`.
Zalecane: weryfikacja przez współdzielony sekret (`X-Internal-Token`) lub dedykowany JWT serwisu.

### 4.5 Paginacja `getOwnerRestaurants`
Jeśli właściciel może mieć wiele restauracji, `GET /restaurants/owner` powinno zwracać `Page<>`.

---

## Kolejność implementacji

1. **Bugfixy 1.1–1.5** — naprawić przed testami
2. **Testy 3.1** (`RestaurantServiceTest`) — napisać pierwsze, zgodnie z TDD
3. **Testy 3.2** (`RestaurantControllerTest`) 
4. **Testy 3.3** (integracyjne)
5. **Brakujące endpointy 2.1–2.5**
6. **V2 ulepszenia** — w zależności od priorytetu produktu
