package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;
import pl.urban.backend.enums.OrderStatus;
import pl.urban.backend.model.OrderMenu;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class AdminOrderDTO {
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
    private String UserName;
    private String UserEmail;
    private String UserPhone;
    private String street;
    private String houseNumber;
    private String city;
    private String zipCode;
}
