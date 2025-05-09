package pl.urban.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column()
    private int pickupTime;

    @OneToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
