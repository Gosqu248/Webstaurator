package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoForOrderDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
}
