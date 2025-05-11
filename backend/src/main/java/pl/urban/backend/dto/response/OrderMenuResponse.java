package pl.urban.backend.dto.response;


import java.util.List;

public record OrderMenuResponse(
        Long id,
        int quantity,
        MenuResponse menu,
        List<AdditivesResponse> chooseAdditives
) {
}
