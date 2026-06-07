package pl.urban.backend.dto.response;

import pl.urban.backend.enums.OrderStatus;

import java.time.ZonedDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String deliveryOption,
        String deliveryTime,
        ZonedDateTime orderDate,
        List<OrderMenuResponse> orderMenus,
        String paymentId,
        String paymentMethod,
        Long restaurantId,
        String restaurantName,
        String restaurantLogo,
        OrderStatus status,
        double totalPrice,
        UserAddressResponse userAddress,
        boolean hasOpinion
) {
}
