package pl.urban.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_menu")
public class OrderMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "order_menu_additives",
            joinColumns = @JoinColumn(name = "order_menu_id"),
            inverseJoinColumns = @JoinColumn(name = "additive_id")
    )
    private List<Additives> chooseAdditives;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
