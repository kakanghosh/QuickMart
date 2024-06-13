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
    private static final Logger logger = LoggerFactory.getLogger("sales-report");
    private final SaleService saleService;

    public RestControllerSale(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("total/today")
    public ResponseEntity<SaleResponse> getTotalSaleAmountOfToday() {
        var optionalSaleOfDay = saleService.getTotalSaleAmountOfToday();
        logger.info("::TotalSaleAmountOfToday:: {}", optionalSaleOfDay.orElse(BigDecimal.ZERO));
        return optionalSaleOfDay.map(bigDecimal -> ResponseEntity.ok(new SaleResponse(bigDecimal)))
                                .orElseGet(() -> ResponseEntity.ok(new SaleResponse(BigDecimal.ZERO)));
    }

    @GetMapping("max")
    public ResponseEntity<SalesSummaryResponse> getMaxSaleDayOfTimeRange(@RequestParam String startDate, @RequestParam String endDate) {
        var optionalMaxSale = saleService.getMaxSaleDayOfTimeRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
        logger.info("::MaxSaleDayOfTimeRange:: Start date: {}, End date: {}, Sale: {}", startDate, endDate, optionalMaxSale);
        return optionalMaxSale.map(summaryDTO -> ResponseEntity.ok(new SalesSummaryResponse(summaryDTO)))
                              .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("top5")
    public ResponseEntity<TopSellingItemResponse> getTopSellingItemsOfAllTime() {
        var top5Sales = saleService.getTop5SellingItemsOfAllTimeBasedOnSaleAmount();
        logger.info("::TopSellingItemsOfAllTime:: {}", top5Sales);
        return ResponseEntity.ok(new TopSellingItemResponse(top5Sales));
    }

    @GetMapping("last-month/top5")
    public ResponseEntity<TopSellingItemResponse> getTopSellingItemsOfLastMonth() {
        var top5Sales = saleService.getTop5SellingItemsOfLastMonthBasedOnNumberOfSales();
        logger.info("::TopSellingItemsOfLastMonth:: {}", top5Sales);
        return ResponseEntity.ok(new TopSellingItemResponse(top5Sales));
    }
}
