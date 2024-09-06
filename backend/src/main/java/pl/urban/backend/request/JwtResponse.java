package pl.urban.backend.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String jwt;

    public JwtResponse(String token) {
        this.jwt = token;
    }

}
