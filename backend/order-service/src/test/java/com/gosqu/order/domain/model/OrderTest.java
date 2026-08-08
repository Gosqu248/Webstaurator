package com.gosqu.order.domain.model;

import com.gosqu.order.domain.exception.ForbiddenOrderAccessException;
import com.gosqu.order.domain.exception.InvalidOrderTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final DeliveryAddress ADDRESS = new DeliveryAddress(
            "Marszałkowska 1", "Warszawa", "00-001", "Poland", 52.23, 21.01);
    private static final Money DELIVERY_FEE = Money.PLN("5.00");

    private static List<OrderItem> items(int quantity) {
        return List.of(new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Burger",
                Money.PLN("20.00"), quantity));
    }

    private Order newOrder() {
        return Order.create(CUSTOMER_ID, RESTAURANT_ID, "Burger House", items(2), ADDRESS, DELIVERY_FEE);
    }

    @Nested
    @DisplayName("Order.create()")
    class CreateTests {

        @Test
        @DisplayName("starts in PENDING status")
        void create_setsStatusPending() {
            Order order = newOrder();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        }

        @Test
        @DisplayName("total = sum of item subtotals + delivery fee")
        void create_computesTotalCorrectly() {
            Order order = newOrder();
            // 2 items × 20 PLN + 5 PLN delivery = 45 PLN
            assertThat(order.getTotalAmount().amount()).isEqualByComparingTo(new BigDecimal("45.00"));
            assertThat(order.getTotalAmount().currency()).isEqualTo("PLN");
        }

        @Test
        @DisplayName("assigns unique id")
        void create_assignsUniqueId() {
            Order o1 = newOrder();
            Order o2 = newOrder();
            assertThat(o1.getId()).isNotEqualTo(o2.getId());
        }
    }

    @Nested
    @DisplayName("State machine — happy path")
    class StateMachineHappyPathTests {

        @Test
        @DisplayName("full lifecycle: PENDING → DELIVERED")
        void fullLifecycle_happyPath() {
            Order order = newOrder();

            order.confirm();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

            order.startPreparing();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PREPARING);

            order.markPrepared();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PREPARED);

            order.markPickedUp();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PICKED_UP);

            order.markDelivered();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("PENDING → PAYMENT_FAILED")
        void failPayment_fromPending() {
            Order order = newOrder();
            order.failPayment();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        }

        @Test
        @DisplayName("cancel sets reason")
        void cancel_fromPending_setsReason() {
            Order order = newOrder();
            order.cancel("customer changed mind");
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            assertThat(order.getCancellationReason()).isEqualTo("customer changed mind");
        }
    }

    @Nested
    @DisplayName("State machine — illegal transitions")
    class IllegalTransitionTests {

        @Test
        @DisplayName("confirm() from PREPARING throws")
        void confirm_fromPreparing_throws() {
            Order order = newOrder();
            order.confirm();
            order.startPreparing();
            assertThatThrownBy(order::confirm)
                    .isInstanceOf(InvalidOrderTransitionException.class);
        }

        @Test
        @DisplayName("startPreparing() from PENDING throws")
        void startPreparing_fromPending_throws() {
            Order order = newOrder();
            assertThatThrownBy(order::startPreparing)
                    .isInstanceOf(InvalidOrderTransitionException.class);
        }

        @Test
        @DisplayName("markPrepared() from CONFIRMED throws")
        void markPrepared_fromConfirmed_throws() {
            Order order = newOrder();
            order.confirm();
            assertThatThrownBy(order::markPrepared)
                    .isInstanceOf(InvalidOrderTransitionException.class);
        }

        @Test
        @DisplayName("markPickedUp() from PREPARING throws")
        void markPickedUp_fromPreparing_throws() {
            Order order = newOrder();
            order.confirm();
            order.startPreparing();
            assertThatThrownBy(order::markPickedUp)
                    .isInstanceOf(InvalidOrderTransitionException.class);
        }

        @Test
        @DisplayName("cancel() from DELIVERED throws")
        void cancel_fromDelivered_throws() {
            Order order = newOrder();
            order.confirm();
            order.startPreparing();
            order.markPrepared();
            order.markPickedUp();
            order.markDelivered();
            assertThatThrownBy(() -> order.cancel("late"))
                    .isInstanceOf(InvalidOrderTransitionException.class);
        }

        @Test
        @DisplayName("cancel() from PICKED_UP throws")
        void cancel_fromPickedUp_throws() {
            Order order = newOrder();
            order.confirm();
            order.startPreparing();
            order.markPrepared();
            order.markPickedUp();
            assertThatThrownBy(() -> order.cancel("late"))
                    .isInstanceOf(InvalidOrderTransitionException.class);
        }

        @Test
        @DisplayName("failPayment() from CONFIRMED throws")
        void failPayment_fromConfirmed_throws() {
            Order order = newOrder();
            order.confirm();
            assertThatThrownBy(order::failPayment)
                    .isInstanceOf(InvalidOrderTransitionException.class);
        }
    }

    @Nested
    @DisplayName("Ownership")
    class OwnershipTests {

        @Test
        @DisplayName("requireOwnership() passes for the customer who placed the order")
        void requireOwnership_sameCustomer_passes() {
            Order order = newOrder();
            assertThatCode(() -> order.requireOwnership(CUSTOMER_ID))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("requireOwnership() throws for different user")
        void requireOwnership_differentUser_throws() {
            Order order = newOrder();
            UUID other = UUID.randomUUID();
            assertThatThrownBy(() -> order.requireOwnership(other))
                    .isInstanceOf(ForbiddenOrderAccessException.class);
        }
    }

    @Nested
    @DisplayName("Money value object")
    class MoneyTests {

        @Test
        @DisplayName("add() sums same-currency amounts")
        void money_add_sameCurrency() {
            Money a = Money.PLN("10.00");
            Money b = Money.PLN("5.50");
            assertThat(a.add(b).amount()).isEqualByComparingTo(new BigDecimal("15.50"));
        }

        @Test
        @DisplayName("add() throws for different currencies")
        void money_add_differentCurrencies_throws() {
            Money pln = Money.PLN("10.00");
            Money eur = Money.of(new BigDecimal("10.00"), "EUR");
            assertThatThrownBy(() -> pln.add(eur))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("multiply() scales amount by quantity")
        void money_multiply() {
            Money price = Money.PLN("7.50");
            assertThat(price.multiply(3).amount()).isEqualByComparingTo(new BigDecimal("22.50"));
        }

        @Test
        @DisplayName("negative amount throws")
        void money_negativeAmount_throws() {
            assertThatThrownBy(() -> Money.PLN("-1.00"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
