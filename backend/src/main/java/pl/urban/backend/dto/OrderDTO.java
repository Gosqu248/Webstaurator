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
    private Long id;
    private String deliveryOption;
    private String deliveryTime;
    private ZonedDateTime orderDate;
    private List<OrderMenu> orderMenus;
    private String paymentId;
    private String paymentMethod;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantLogo;
    private OrderStatus status;
    private double totalPrice;
    private UserAddress userAddress;
    private boolean hasOpinion;

}