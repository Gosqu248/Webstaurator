package pl.urban.backend.dto.response;


import java.util.List;

public record MenuResponse(
         Long id,
         String name,
         String category,
         String ingredients,
         double price,
         String image,
         List<AdditivesResponse> additives
) {
}
