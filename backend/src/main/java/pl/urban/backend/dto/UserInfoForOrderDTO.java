package pl.urban.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoForOrderDTO {
    private String name;
    private String email;
    private String phoneNumber;
}
