package com.wsd.quickmart.controller;

import com.wsd.quickmart.entity.Sale;
import com.wsd.quickmart.entity.SaleItem;
import com.wsd.quickmart.repository.ProductRepository;
import com.wsd.quickmart.repository.SaleItemRepository;
import com.wsd.quickmart.repository.SaleRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestControllerSaleTest {

    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0-debian");

    @LocalServerPort
    private Integer port;

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

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        saleRepository.deleteAll();
        saleItemRepository.deleteAll();
    }

    @AfterEach
    void afterEach() {
        saleRepository.deleteAll();
    }

    @Test
    void shouldGetTotalSaleAmountOfToday() {
        saleRepository.saveAll(List.of(
                new Sale(1L, LocalDateTime.now(), new BigDecimal("250.00")),
                new Sale(2L, LocalDateTime.now(), new BigDecimal("300.25"))
        ));
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/sales/total/today")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("amount", notNullValue())
                .body("amount", Matchers.equalTo(550.25f));
    }

    @Test
    void shouldGetMaxSaleDayOfTimeRange() {
        saleRepository.saveAll(List.of(
                new Sale(1L, LocalDateTime.now().minusDays(7), new BigDecimal("250.00")),
                new Sale(2L, LocalDateTime.now().minusDays(6), new BigDecimal("300.25")),
                new Sale(2L, LocalDateTime.now(), new BigDecimal("298.25"))
        ));
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("startDate", LocalDate.now().minusDays(7).toString())
                .queryParam("endDate", LocalDate.now().toString())
                .get("/api/v1/sales/max")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("summary", notNullValue())
                .body("summary.saleDate", equalTo(LocalDate.now().minusDays(6).toString()))
                .body("summary.totalSales", equalTo(300.25f));
    }

    @Test
    void shouldGetTop5SellingItemsOfAllTime() {
        saleRepository.saveAll(List.of(
                new Sale(1L, LocalDateTime.of(2024, 6, 8, 2, 2), new BigDecimal("250.00")),
                new Sale(2L, LocalDateTime.of(2024, 6, 8, 2, 2), new BigDecimal("300.25")),
                new Sale(3L, LocalDateTime.of(2024, 6, 10, 2, 2), new BigDecimal("300.25"))
        ));
        var sales = saleRepository.findAll();
        var productA = productRepository.findById(1L);
        var productB = productRepository.findById(2L);
        var productC = productRepository.findById(3L);
        saleItemRepository.saveAll(List.of(
                new SaleItem(sales.get(0).getId(), productA.get().getId(), 2, productA.get().getPrice()),
                new SaleItem(sales.get(0).getId(), productB.get().getId(), 5, productB.get().getPrice()),

                new SaleItem(sales.get(1).getId(), productB.get().getId(), 5, productB.get().getPrice()),
                new SaleItem(sales.get(1).getId(), productA.get().getId(), 6, productA.get().getPrice()),
                new SaleItem(sales.get(1).getId(), productC.get().getId(), 2, productC.get().getPrice()),

                new SaleItem(sales.get(2).getId(), productB.get().getId(), 5, productB.get().getPrice()),
                new SaleItem(sales.get(2).getId(), productC.get().getId(), 4, productC.get().getPrice())
        ));
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/sales/top5")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items", notNullValue())
                .body("items.size()", equalTo(3));
    }

    @Test
    void shouldGetTop5SellingItemsLastMonth() {
        saleRepository.saveAll(List.of(
                new Sale(1L, LocalDateTime.now().minusMonths(1), new BigDecimal("250.00")),
                new Sale(2L, LocalDateTime.now().minusMonths(1), new BigDecimal("300.25")),
                new Sale(3L, LocalDateTime.now().minusMonths(1), new BigDecimal("300.25"))
        ));
        var sales = saleRepository.findAll();
        var productA = productRepository.findById(1L);
        var productB = productRepository.findById(2L);
        var productC = productRepository.findById(3L);
        saleItemRepository.saveAll(List.of(
                new SaleItem(sales.get(0).getId(), productA.get().getId(), 16, productA.get().getPrice()),
                new SaleItem(sales.get(0).getId(), productB.get().getId(), 5, productB.get().getPrice()),

                new SaleItem(sales.get(1).getId(), productB.get().getId(), 5, productB.get().getPrice()),
                new SaleItem(sales.get(1).getId(), productA.get().getId(), 6, productA.get().getPrice()),
                new SaleItem(sales.get(1).getId(), productC.get().getId(), 2, productC.get().getPrice()),

                new SaleItem(sales.get(2).getId(), productB.get().getId(), 5, productB.get().getPrice()),
                new SaleItem(sales.get(2).getId(), productC.get().getId(), 4, productC.get().getPrice())
        ));
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/sales/last-month/top5")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items", notNullValue())
                .body("items.size()", equalTo(3));
    }
}