package pl.urban.backend.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayerData {
    private String email;
    private String name;
    private String phone;
}