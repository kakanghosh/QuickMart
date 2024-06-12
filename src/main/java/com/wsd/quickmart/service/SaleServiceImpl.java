package com.wsd.quickmart.service;

import com.wsd.quickmart.dto.SaleSummaryDTO;
import com.wsd.quickmart.dto.TopSellingItemDTO;
import com.wsd.quickmart.exception.InvalidDateRangeException;
import com.wsd.quickmart.repository.SaleRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
final public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final TimeHelperService timeHelperService;

    public SaleServiceImpl(SaleRepository saleRepository,
                           TimeHelperService timeHelperService) {
        this.saleRepository = saleRepository;
        this.timeHelperService = timeHelperService;
    }

    @Override
    public Optional<BigDecimal> getTotalSaleAmountOfToday() {
        return saleRepository.getTotalSaleAmountByDate(timeHelperService.getCurrentDate());
    }

    @Override
    public Optional<SaleSummaryDTO> getMaxSaleDayOfTimeRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("Start date: " + startDate + ", EndDate: " + endDate);
        }
        return saleRepository.findMaxSaleDayOfTimeRange(startDate, endDate);
    }

    @Override
    public List<TopSellingItemDTO> getTop5SellingItemsOfAllTimeBasedOnSaleAmount() {
        return saleRepository.findTopSellingItemsOfAllTime(Pageable.ofSize(5));
    }

    @Override
    public List<TopSellingItemDTO> getTop5SellingItemsOfLastMonthBasedOnNumberOfSales() {
        return saleRepository.findTopSellingItemsOfLastMonth(
                timeHelperService.getFirstDayOfThePreviousMonth(),
                Pageable.ofSize(5)
        );
    }
}
