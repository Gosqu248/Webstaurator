package com.gosqu.restaurant.menu.dto.response;

import java.util.UUID;

public record CategoryResponse(UUID id, String name, Integer displayOrder) {}
