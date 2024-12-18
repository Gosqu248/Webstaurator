package pl.urban.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name="restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    private String logoUrl;

    private String imageUrl;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonIgnore
    private Delivery delivery;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonIgnore
    private RestaurantAddress restaurantAddress;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "restaurant_menu",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    @JsonIgnore
    private Set<Menu> menu = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<DeliveryHour> deliveryHours;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<RestaurantOpinion> restaurantOpinions;

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<FavouriteRestaurant> favouriteRestaurants;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "restaurant_payment",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_id")
    )
    @JsonIgnore
    private List<Payment> payments;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Order> orders;

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", delivery=" + (delivery != null ? delivery.getId() : "null") +
                ", restaurantAddress=" + (restaurantAddress != null ? restaurantAddress.getId() : "null") +
                ", menu=" + menu +
                ", deliveryHours=" + deliveryHours +
                ", restaurantOpinions=" + restaurantOpinions +
                ", favouriteRestaurants=" + favouriteRestaurants +
                ", payments=" + payments +
                ", orders=" + orders +
                '}';
    }
}
