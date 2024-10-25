package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;
import pl.urban.backend.enums.OrderStatus;

@Getter
@Setter
public class OrderDTO {

    private Long id;
    private String paymentMethod;
    private OrderStatus status;
    private double totalPrice;
    private String comment;


    private String city;
    private String zipCode;
    private String phone;
    private String email;
    private double total;
    private String createdAt;
    private UserNameDTO user;

}
