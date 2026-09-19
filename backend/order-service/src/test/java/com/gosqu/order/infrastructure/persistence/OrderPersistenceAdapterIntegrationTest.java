package com.gosqu.order.infrastructure.persistence;

import com.gosqu.order.application.port.out.OrderEventPublisherPort;
import com.gosqu.order.application.port.out.RestaurantClientPort;
import com.gosqu.order.application.port.out.UserAddressClientPort;
import com.gosqu.order.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
)
class OrderPersistenceAdapterIntegrationTest {

    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @MockitoBean
    private OrderEventPublisherPort eventPublisher;

    @MockitoBean
    private RestaurantClientPort restaurantClientPort;

    @MockitoBean
    private UserAddressClientPort userAddressClientPort;

    @Autowired
    private OrderPersistenceAdapter adapter;

    private static Order buildOrder(UUID customerId, UUID restaurantId) {
        DeliveryAddress address = new DeliveryAddress(
                "Marszałkowska 1", "Warszawa", "00-001", "Poland", 52.23, 21.01);
        List<OrderItem> items = List.of(new OrderItem(
                UUID.randomUUID(), UUID.randomUUID(), "Burger", Money.PLN("25.00"), 1));
        return Order.create(customerId, restaurantId, "Burger House", items, address, Money.PLN("5.00"));
    }

    @Test
    @DisplayName("Flyway migrations run and schema validates (ddl-auto=validate)")
    void context_loads_and_schema_validates() {
        // If this test runs, it means the Spring context loaded,
        // Flyway ran all migrations, and ddl-auto=validate passed.
        assertThat(adapter).isNotNull();
    }

    @Test
    @DisplayName("save and findById round-trip preserves all fields")
    void saveAndFindById_roundTrip() {
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Order order = buildOrder(customerId, restaurantId);

        adapter.save(order);
        Optional<Order> found = adapter.findById(order.getId());

        assertThat(found).isPresent();
        Order loaded = found.get();
        assertThat(loaded.getId()).isEqualTo(order.getId());
        assertThat(loaded.getCustomerId()).isEqualTo(customerId);
        assertThat(loaded.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(loaded.getRestaurantName()).isEqualTo("Burger House");
        assertThat(loaded.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(loaded.getTotalAmount().amount()).isEqualByComparingTo("30.00");
        assertThat(loaded.getTotalAmount().currency()).isEqualTo("PLN");
        assertThat(loaded.getItems()).hasSize(1);
        assertThat(loaded.getItems().get(0).name()).isEqualTo("Burger");
        assertThat(loaded.getDeliveryAddress().city()).isEqualTo("Warszawa");
    }

    @Test
    @DisplayName("findByCustomerId returns only that customer's orders")
    void findByCustomerId_returnsMatchingOrders() {
        UUID target = UUID.randomUUID();
        UUID other = UUID.randomUUID();

        adapter.save(buildOrder(target, UUID.randomUUID()));
        adapter.save(buildOrder(target, UUID.randomUUID()));
        adapter.save(buildOrder(other, UUID.randomUUID()));

        List<Order> result = adapter.findByCustomerId(target);

        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(o -> assertThat(o.getCustomerId()).isEqualTo(target));
    }

    @Test
    @DisplayName("save after status change persists new status")
    void saveAfterStatusChange_persistsNewStatus() {
        Order order = buildOrder(UUID.randomUUID(), UUID.randomUUID());
        adapter.save(order);

        order.confirm();
        adapter.save(order);

        Order reloaded = adapter.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }
}
