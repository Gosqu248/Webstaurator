package pl.urban.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Setter
@Getter
@Entity
@Table(name = "restaurant_opinions")
public class RestaurantOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double qualityRating;

    @Column(nullable = false)
    private double deliveryRating;

    private String comment;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
    }

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

}
