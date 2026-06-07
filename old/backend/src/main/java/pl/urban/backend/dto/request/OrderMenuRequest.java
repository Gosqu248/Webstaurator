package pl.urban.backend.dto.request;

import java.util.List;

public record OrderMenuRequest(
        int quantity,
        Long menuId,
        List<Long> chooseAdditivesId
) {
}
