package pl.urban.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    private String ingredients;

    @Column(nullable = false)
    private double price;

    private String image;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "menus")
    private List<Additives> additives;

    @ManyToMany(mappedBy = "menu")
    private Set<Restaurant> restaurant;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderMenu> orderMenus;

}
