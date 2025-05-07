package pl.urban.backend.dto.response;


import java.util.List;

public record OrderMenuResponse(
        Long id,
        int quantity,
        long menuId,
        String menuName,
        List<AdditivesResponse> chooseAdditives
) {
}
