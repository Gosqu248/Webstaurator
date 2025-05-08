package pl.urban.backend.dto.request;

import pl.urban.backend.enums.OrderStatus;

import java.util.List;

public record OrderRequest(
        Long userId,
        Long userAddressId,
        Long restaurantId,
        List<OrderMenuRequest> orderMenus,
        String deliveryOption,
        String deliveryTime,
        String paymentId,
        String paymentMethod,
        OrderStatus status,
        double totalPrice,
        String comment
) {
}
