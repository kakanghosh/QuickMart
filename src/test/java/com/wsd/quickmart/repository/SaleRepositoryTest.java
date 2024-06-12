package com.wsd.quickmart.repository;

import com.wsd.quickmart.entity.Sale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
        var totalSaleAmountOfCurrentDay = saleRepository.getTotalSaleAmountByDate(LocalDate.now());
        assertThat(totalSaleAmountOfCurrentDay.amount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldGetTotalSaleAmountByDate() {
        saleRepository.saveAll(List.of(
                new Sale(1L, LocalDateTime.of(2024, 5, 2, 2, 2), new BigDecimal("250.00")),
                new Sale(2L, LocalDateTime.of(2024, 5, 2, 2, 2), new BigDecimal("300.25"))
        ));
        var totalSaleAmountOfCurrentDay = saleRepository.getTotalSaleAmountByDate(LocalDate.of(2024, 5, 2));
        assertThat(totalSaleAmountOfCurrentDay.amount()).isGreaterThan(BigDecimal.ZERO);
        assertThat(totalSaleAmountOfCurrentDay.amount()).isEqualTo(new BigDecimal("550.25"));
    }
}