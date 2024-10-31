package pl.urban.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.urban.backend.enums.OrderStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private double totalPrice;

    private String deliveryTime;

    private String deliveryOption;

    private String comment;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_address_id", nullable = true)
    private UserAddress userAddress;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderMenu> orderMenus;


    private ZonedDateTime orderDate;

    @PrePersist
    protected void onCreate() {
        orderDate = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
    }
}