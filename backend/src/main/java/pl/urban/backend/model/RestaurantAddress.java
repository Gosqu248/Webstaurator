package pl.urban.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name="restaurant_addresses")
public class RestaurantAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String flatNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;


    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
