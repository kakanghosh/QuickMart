package com.wsd.quickmart.service;

import com.wsd.quickmart.dto.SaleSummaryDTO;
import com.wsd.quickmart.dto.TopSellingItemDTO;
import com.wsd.quickmart.exception.InvalidDateRangeException;
import com.wsd.quickmart.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private TimeHelperService timeHelperService;

    @InjectMocks
    private SaleServiceImpl saleService;

    @Test
    void shouldGetTotalSaleAmountOfToday() {
        var current = LocalDate.of(2024, 6, 12);
        given(timeHelperService.getCurrentDate()).willReturn(current);
        given(saleRepository.getTotalSaleAmountByDate(current)).willReturn(Optional.of(new BigDecimal("200.50")));
        var optionalSale = saleService.getTotalSaleAmountOfToday();
        assertThat(optionalSale.isPresent()).isTrue();
        assertThat(optionalSale.get()).isEqualTo(new BigDecimal("200.50"));
    }

    @Test
    void shouldThrowExceptionForInvalidTimeRange() {
        var startDate = LocalDate.of(2024, 6, 12);
        var endDate = LocalDate.of(2024, 6, 10);
        assertThatThrownBy(() -> saleService.getMaxSaleDayOfTimeRange(startDate, endDate))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void shouldGetMaxSaleDayOfTimeRange() {
        var startDate = LocalDate.of(2024, 6, 8);
        var endDate = LocalDate.of(2024, 6, 10);
        var givenSalesSummary = new SaleSummaryDTO(LocalDate.of(2024, 6, 9), new BigDecimal("100.65"));
        given(saleRepository.findMaxSaleDayOfTimeRange(startDate, endDate)).willReturn(Optional.of(givenSalesSummary));
        var optionalSalesSummary = saleService.getMaxSaleDayOfTimeRange(startDate, endDate);
        assertThat(optionalSalesSummary.isPresent()).isTrue();
        assertThat(optionalSalesSummary.get()).isEqualTo(givenSalesSummary);
    }

    @Test
    void shouldGetTop5SellingItemsOfAllTimeBasedOnSaleAmount() {
        var products = List.of(
                new TopSellingItemDTO(1L, "Product-A", 10L, new BigDecimal("5550.90")),
                new TopSellingItemDTO(3L, "Product-C", 20L, new BigDecimal("15550.90")),
                new TopSellingItemDTO(2L, "Product-B", 15L, new BigDecimal("8650.90"))
        );
        given(saleRepository.findTopSellingItemsOfAllTime(Pageable.ofSize(5))).willReturn(products);
        var top5Products = saleService.getTop5SellingItemsOfAllTimeBasedOnSaleAmount();
        assertThat(top5Products.isEmpty()).isFalse();
        assertThat(top5Products).isEqualTo(products);
    }

    @Test
    void shouldGetTop5SellingItemsOfLastMonthBasedOnNumberOfSales() {
        var products = List.of(
                new TopSellingItemDTO(2L, "Product-B", 25L, new BigDecimal("8650.90")),
                new TopSellingItemDTO(3L, "Product-C", 20L, new BigDecimal("15550.90")),
                new TopSellingItemDTO(1L, "Product-A", 10L, new BigDecimal("5550.90"))
        );
        var previousMonthDate = LocalDate.of(2024, 6, 10);
        given(timeHelperService.getFirstDayOfThePreviousMonth()).willReturn(previousMonthDate);
        given(saleRepository.findTopSellingItemsOfLastMonth(eq(previousMonthDate), any())).willReturn(products);
        var top5Products = saleService.getTop5SellingItemsOfLastMonthBasedOnNumberOfSales();
        assertThat(top5Products.isEmpty()).isFalse();
        assertThat(top5Products).isEqualTo(products);
    }
}