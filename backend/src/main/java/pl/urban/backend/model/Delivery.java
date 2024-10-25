package pl.urban.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int deliveryMinTime;

    @Column(nullable = false)
    private int deliveryMaxTime;

    @Column(nullable = false)
    private double deliveryPrice;

    @Column(nullable = false)
    private double minimumPrice;

    @Column(nullable = true)
    private int pickupTime;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;



}
