// OrderDTO.java
package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;
import pl.urban.backend.enums.OrderStatus;
import pl.urban.backend.model.OrderMenu;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private String paymentMethod;
    private OrderStatus status;
    private double totalPrice;
    private String deliveryTime;
    private String comment;
    private Long restaurantId;
    private Long userId;
    private List<OrderMenuDTO> orderMenus;
    private ZonedDateTime orderDate;

}