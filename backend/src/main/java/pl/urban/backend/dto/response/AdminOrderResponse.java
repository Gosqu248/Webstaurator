package pl.urban.backend.dto.response;


import pl.urban.backend.enums.OrderStatus;
import pl.urban.backend.model.OrderMenu;

import java.time.ZonedDateTime;
import java.util.List;

public record AdminOrderResponse(
         Long id,
         String deliveryOption,
         String deliveryTime,
         ZonedDateTime orderDate,
         List<OrderMenu> orderMenus,
         String paymentId,
         String paymentMethod,
         Long restaurantId,
         String restaurantName,
         String restaurantLogo,
         OrderStatus status,
         double totalPrice,
         String userName,
         String userEmail,
         String userPhone,
         String street,
         String houseNumber,
         String city,
         String zipCode
) {

}
