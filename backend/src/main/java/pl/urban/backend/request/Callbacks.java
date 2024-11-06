package pl.urban.backend.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Callbacks {
    private String successUrl;
    private String errorUrl;
    private String notificationUrl;
}
