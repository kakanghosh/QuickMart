package com.wsd.quickmart.dto;

import java.math.BigDecimal;

public record TopSellingItemDTO(Long productId, String productName, Long totalQuantity, BigDecimal totalSales) {
}

