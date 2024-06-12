package com.wsd.quickmart.repository;

import com.wsd.quickmart.entity.Sale;
import com.wsd.quickmart.entity.SaleItem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class SaleRepositoryTest {

    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0-debian");

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    void shouldGetTotalSaleAmountByDateZero() {
        var optionalSaleAmount = saleRepository.getTotalSaleAmountByDate(LocalDate.now());
        assertThat(optionalSaleAmount.isEmpty()).isTrue();
    }

    @Test
    void shouldGetTotalSaleAmountByDate() {
        saleRepository.saveAll(List.of(
                new Sale(1L, LocalDateTime.of(2024, 5, 2, 2, 2), new BigDecimal("250.00")),
                new Sale(2L, LocalDateTime.of(2024, 5, 2, 2, 2), new BigDecimal("300.25"))
        ));
        var optionalCurrentDaySale = saleRepository.getTotalSaleAmountByDate(LocalDate.of(2024, 5, 2));
        assertThat(optionalCurrentDaySale.isPresent()).isTrue();
        assertThat(optionalCurrentDaySale.get()).isEqualTo(new BigDecimal("550.25"));
    }

    @Test
    void shouldFindMaxSaleDayOfTimeRange() {
        saleRepository.saveAll(List.of(
                new Sale(1L, LocalDateTime.of(2024, 6, 8, 2, 2), new BigDecimal("250.00")),
                new Sale(2L, LocalDateTime.of(2024, 6, 8, 2, 2), new BigDecimal("300.25")),
                new Sale(2L, LocalDateTime.of(2024, 6, 10, 2, 2), new BigDecimal("300.25"))
        ));
        var startDate = LocalDate.of(2024, 6, 8);
        var endDate = LocalDate.of(2024, 6, 12);
        var optionalSalesSummary = saleRepository.findMaxSaleDayOfTimeRange(startDate, endDate);
        assertThat(optionalSalesSummary.isPresent()).isTrue();
        assertThat(optionalSalesSummary.get().saleDate()).isEqualTo(LocalDate.of(2024, 6, 8));
        assertThat(optionalSalesSummary.get().totalSales()).isEqualTo(new BigDecimal("550.25"));
    }

    @Test
    void shouldFindTopSellingItemsOfAllTime() {
        saleRepository.saveAll(List.of(
                new Sale(1L, LocalDateTime.of(2024, 6, 8, 2, 2), new BigDecimal("250.00")),
                new Sale(2L, LocalDateTime.of(2024, 6, 8, 2, 2), new BigDecimal("300.25")),
                new Sale(3L, LocalDateTime.of(2024, 6, 10, 2, 2), new BigDecimal("300.25"))
        ));
        var productA = productRepository.findById(1L);
        var productB = productRepository.findById(2L);
        var productC = productRepository.findById(3L);
        saleItemRepository.saveAll(List.of(
                new SaleItem(1L, productA.get().getId(), 2, productA.get().getPrice()),
                new SaleItem(1L, productB.get().getId(), 5, productB.get().getPrice()),

                new SaleItem(2L, productB.get().getId(), 5, productB.get().getPrice()),
                new SaleItem(2L, productA.get().getId(), 6, productA.get().getPrice()),
                new SaleItem(2L, productC.get().getId(), 2, productC.get().getPrice()),

                new SaleItem(3L, productB.get().getId(), 5, productB.get().getPrice()),
                new SaleItem(3L, productC.get().getId(), 4, productC.get().getPrice())
        ));
        var top5Selling = saleRepository.findTopSellingItemsOfAllTime(Pageable.ofSize(5));
        assertThat(top5Selling).isNotEmpty();
        assertThat(top5Selling.get(0).productId()).isEqualTo(productB.get().getId());
        assertThat(top5Selling.get(1).productId()).isEqualTo(productC.get().getId());
        assertThat(top5Selling.get(2).productId()).isEqualTo(productA.get().getId());
    }
}