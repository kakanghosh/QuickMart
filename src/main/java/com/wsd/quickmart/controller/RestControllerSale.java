package com.wsd.quickmart.controller;

import com.wsd.quickmart.controller.response.SaleResponse;
import com.wsd.quickmart.controller.response.SalesSummaryResponse;
import com.wsd.quickmart.controller.response.TopSellingItemResponse;
import com.wsd.quickmart.service.SaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/sales")
public class RestControllerSale {
    private static final Logger logger = LoggerFactory.getLogger(RestControllerSale.class);
    private final SaleService saleService;

    public RestControllerSale(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("total/today")
    public ResponseEntity<SaleResponse> getTotalSaleAmountOfToday() {
        var optionalSaleOfDay = saleService.getTotalSaleAmountOfToday();
        return optionalSaleOfDay.map(bigDecimal -> ResponseEntity.ok(new SaleResponse(bigDecimal)))
                                .orElseGet(() -> ResponseEntity.ok(new SaleResponse(BigDecimal.ZERO)));
    }

    @GetMapping("max")
    public ResponseEntity<SalesSummaryResponse> getMaxSaleDayOfTimeRange(@RequestParam String startDate, @RequestParam String endDate) {
        logger.info("::MaxSaleDayOfTimeRange:: Start date: {}, End date: {}", startDate, endDate);
        var optionalMaxSale = saleService.getMaxSaleDayOfTimeRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
        return optionalMaxSale.map(summaryDTO -> ResponseEntity.ok(new SalesSummaryResponse(summaryDTO)))
                              .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("top5")
    public ResponseEntity<TopSellingItemResponse> getTopSellingItemsOfAllTime() {
        var top5Sales = saleService.getTop5SellingItemsOfAllTimeBasedOnSaleAmount();
        return ResponseEntity.ok(new TopSellingItemResponse(top5Sales));
    }

    @GetMapping("last-month/top5")
    public ResponseEntity<TopSellingItemResponse> getTopSellingItemsOfLastMonth() {
        var top5Sales = saleService.getTop5SellingItemsOfLastMonthBasedOnNumberOfSales();
        return ResponseEntity.ok(new TopSellingItemResponse(top5Sales));
    }
}
