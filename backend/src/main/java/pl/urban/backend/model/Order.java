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

    private ZonedDateTime orderDate;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "order_menu",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    List<Menu> products;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @PrePersist
    protected void onCreate() {
        orderDate = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
    }

}
