package pl.urban.backend.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderRequest {

    private String customerIp;   // Adres IP klienta
    private String merchantPosId; // Identyfikator punktu płatności
    private String description;    // Opis zamówienia
    private String currencyCode;   // Kod waluty (np. "PLN")
    private Integer totalAmount;    // Całkowita kwota zamówienia w groszach
    private List<Product> products; // Lista produktów w zamówieniu

    // Informacje o kliencie
    private String email;
    private String phone;
    private String name;

    @Setter
    @Getter
    public static class Product {
        private String name;       // Nazwa produktu
        private Integer unitPrice; // Cena jednostkowa w groszach
        private Integer quantity;   // Ilość
    }
}
