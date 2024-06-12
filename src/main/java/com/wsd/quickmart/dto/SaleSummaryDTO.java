package com.wsd.quickmart.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SaleSummaryDTO(LocalDate saleDate, BigDecimal totalSales) {

    public SaleSummaryDTO(LocalDateTime saleDateTime, BigDecimal totalSales) {
        this(saleDateTime.toLocalDate(), totalSales);
    }
}
