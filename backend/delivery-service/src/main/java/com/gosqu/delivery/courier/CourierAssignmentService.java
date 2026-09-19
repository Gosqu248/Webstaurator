package com.gosqu.delivery.courier;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CourierAssignmentService {

    // TODO: prawdziwy matching kurierów — na razie brak courier-service, patrz przewodnik sekcja 3
    private static final List<UUID> TEST_COURIER_POOL = List.of(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            UUID.fromString("22222222-2222-2222-2222-222222222222")
    );

    public UUID assignCourier(UUID orderId) {
        // round-robin/losowy wybór z puli testowej — realny matching to osobny temat
        return TEST_COURIER_POOL.get(Math.abs(orderId.hashCode()) % TEST_COURIER_POOL.size());
    }
}
