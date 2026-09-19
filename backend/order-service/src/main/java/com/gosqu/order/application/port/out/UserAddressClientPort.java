package com.gosqu.order.application.port.out;

import com.gosqu.order.infrastructure.client.dto.AddressSnapshot;

import java.util.UUID;

public interface UserAddressClientPort {

    AddressSnapshot getDefaultAddress(UUID userId);
}
