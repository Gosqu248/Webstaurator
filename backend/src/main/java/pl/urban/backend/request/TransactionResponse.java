package pl.urban.backend.request;

import lombok.Data;

@Data
public class TransactionResponse {
    private String transactionId;
    private String redirectUrl;
    private String status;
}
