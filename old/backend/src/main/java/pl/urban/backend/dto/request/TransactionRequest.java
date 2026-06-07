package pl.urban.backend.dto.request;

import pl.urban.backend.dto.response.CallbacksResponse;

import java.math.BigDecimal;

public record TransactionRequest(
        BigDecimal amount,
        String description,
        PayerData payer,
        CallbacksResponse callbacksResponse,
        String hiddenDescription
) {
}




