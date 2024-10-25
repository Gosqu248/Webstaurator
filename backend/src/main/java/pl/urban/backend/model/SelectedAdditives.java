package pl.urban.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "selected_additives")
public class SelectedAdditives {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String value;

    @ManyToMany(mappedBy = "selectedAdditives")
    private List<OrderMenu> orderMenus;
}
