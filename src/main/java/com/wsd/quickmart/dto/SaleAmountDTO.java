package com.wsd.quickmart.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record SaleAmountDTO(BigDecimal amount) {
    public SaleAmountDTO(BigDecimal amount) {
        this.amount = Objects.requireNonNullElse(amount, BigDecimal.ZERO);
    }
}
