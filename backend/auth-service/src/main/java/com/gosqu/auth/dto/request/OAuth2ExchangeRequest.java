package com.gosqu.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OAuth2ExchangeRequest(@NotBlank String code) {}
