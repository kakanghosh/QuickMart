package com.wsd.quickmart.service;

import com.wsd.quickmart.dto.SaleSummaryDTO;
import com.wsd.quickmart.dto.TopSellingItemDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

sealed public interface SaleService permits SaleServiceImpl {
    Optional<BigDecimal> getTotalSaleAmountOfToday();

    Optional<SaleSummaryDTO> getMaxSaleDayOfTimeRange(LocalDate startDate, LocalDate endDate);

    List<TopSellingItemDTO> getTop5SellingItemsOfAllTimeBasedOnSaleAmount();

    List<TopSellingItemDTO> getTop5SellingItemsOfLastMonthBasedOnNumberOfSales();
}
