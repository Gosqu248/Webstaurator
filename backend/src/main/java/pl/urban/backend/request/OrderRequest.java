package pl.urban.backend.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    @JsonProperty("customerIp")
    private String customerIp;

    @JsonProperty("merchantPosId")
    private String merchantPosId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("currencyCode")
    private String currencyCode;

    @JsonProperty("totalAmount")
    private String totalAmount;

    @JsonProperty("notifyUrl")
    private String notifyUrl;

    @JsonProperty("products")
    private List<Product> products;


    @Getter
    @Setter
    public static class Product {
        @JsonProperty("name")
        private String name;

        @JsonProperty("unitPrice")
        private String unitPrice;

        @JsonProperty("quantity")
        private String quantity;

    }
}
