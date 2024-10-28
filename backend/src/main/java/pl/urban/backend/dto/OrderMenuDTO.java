package pl.urban.backend.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderMenuDTO {
    private Long id;
    private int quantity;
    private long menuId;
    private String menuName;
    private List<AdditivesDTO> chooseAdditives;

}
