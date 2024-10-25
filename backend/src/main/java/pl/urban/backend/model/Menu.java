package pl.urban.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="menu")
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

    @JsonIgnore
    @ManyToMany(mappedBy = "menu")
    private Set<Restaurant> restaurant;



}
