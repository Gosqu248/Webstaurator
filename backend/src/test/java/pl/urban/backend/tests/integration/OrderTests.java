package pl.urban.backend.tests.integration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.urban.backend.dto.response.AdminOrderResponse;
import pl.urban.backend.dto.response.OrderResponse;
import pl.urban.backend.repository.OrderRepository;
import pl.urban.backend.service.OrderService;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderTests {

    private static final Logger logger = LoggerFactory.getLogger(OrderTests.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testGetOrderById() {
        // Given
        Long existingOrderId = 1L;

        // When
        OrderResponse order = orderService.getOrderById(existingOrderId);

        // Then
        assertNotNull(order);
        assertEquals(existingOrderId, order.id());
        assertNotNull(order.restaurantId());
        assertNotNull(order.orderMenus());
        assertFalse(order.orderMenus().isEmpty());
        logger.info("Test zakończony sukcesem - znaleziono zamówienie o ID: {} , koszt zamówienia: {}zł", order.id(), order.totalPrice());
    }

    @Test
    public void testGetAllRestaurantOrders() {
        // Given
        Long restaurantId = 2L;
        Long orderCount = orderRepository.countByRestaurantId(restaurantId);

        // When
        List<OrderResponse> orders = orderService.getAllRestaurantOrders(restaurantId);

        // Then
        assertEquals(orderCount, orders.size());
        logger.info("Test zakończony sukcesem - znaleziono {} zamówień dla restauracji o ID: {}", orders.size(), restaurantId);
    }

    @Test
    public void testGetAllOrders() {
        // Given
        Long orderCount = orderRepository.count();

        // When
        List<AdminOrderResponse> order = orderService.getAllOrders();

        // Then
        assertEquals(orderCount, order.size());
        logger.info("Test zakończony sukcesem - znaleziono {} zamówień ", order.size());
    }
}
