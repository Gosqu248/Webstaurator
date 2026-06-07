package pl.urban.backend.dto.response;

public record TransactionResponse(
        String transactionId,
        String redirectUrl,
        String status
) {
}
