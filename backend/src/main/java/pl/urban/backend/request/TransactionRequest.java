package pl.urban.backend.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionRequest {
    private BigDecimal amount;
    private String description;
    private PayerData payer;
    private Callbacks callbacks;
    private String hiddenDescription;
}




