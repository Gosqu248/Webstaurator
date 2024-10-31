// OrderDTO.java
package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;
import pl.urban.backend.enums.OrderStatus;
import pl.urban.backend.model.OrderMenu;
import pl.urban.backend.model.Restaurant;
import pl.urban.backend.model.User;
import pl.urban.backend.model.UserAddress;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private String paymentMethod;
    private OrderStatus status;
    private double totalPrice;
    private String deliveryTime;
    private String deliveryOption;
    private String comment;
    private List<OrderMenu> orderMenus;
    private User user;
    private UserAddress userAddress;
    private Restaurant restaurant;
    private ZonedDateTime orderDate;

}