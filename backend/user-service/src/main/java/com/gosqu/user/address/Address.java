package com.gosqu.user.address;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    private String label;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    private String postalCode;

    @Column(nullable = false)
    private String country;

    private Double latitude;
    private Double longitude;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDefault = false;

    @UpdateTimestamp
    private Instant updatedAt;
}
