package pl.urban.backend.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoCodingResponse {
    private String lat;
    private String lon;
}