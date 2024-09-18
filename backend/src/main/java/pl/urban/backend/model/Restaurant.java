package pl.urban.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private Delivery delivery;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private RestaurantAddress restaurantAddress;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "restaurant_menu",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    private Set<Menu> menu = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DeliveryHour> deliveryHours;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RestaurantOpinion> restaurantOpinions;


}
