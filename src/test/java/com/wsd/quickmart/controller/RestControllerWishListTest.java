package com.wsd.quickmart.controller;

import com.wsd.quickmart.entity.WishList;
import com.wsd.quickmart.entity.WishListItem;
import com.wsd.quickmart.repository.WishListItemRepository;
import com.wsd.quickmart.repository.WishListRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestControllerWishListTest {

    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0-debian");

    @LocalServerPort
    private Integer port;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private WishListItemRepository wishListItemRepository;

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
        wishListRepository.deleteAll();
        wishListItemRepository.deleteAll();
    }

    @Test
    void shouldGetWishListByCustomerId() {
        var wishList = wishListRepository.save(new WishList(1L, LocalDateTime.now()));
        wishListItemRepository.saveAll(List.of(
                new WishListItem(wishList.getId(), 1L),
                new WishListItem(wishList.getId(), 2L)
        ));
        given()
                .contentType(ContentType.JSON)
                .when()
                .pathParams("customerId", 1)
                .get("/api/v1/wish-lists/{customerId}")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("products", Matchers.notNullValue())
                .body("products.size()", Matchers.equalTo(2))
                .body("products[0].id", Matchers.equalTo(1))
                .body("products[0].name", Matchers.equalTo("Product A"))
                .body("products[0].description", Matchers.equalTo("Description for Product A"))
                .body("products[0].price", Matchers.equalTo(10.25f))
                .body("products[1].id", Matchers.equalTo(2))
                .body("products[1].name", Matchers.equalTo("Product B"))
                .body("products[1].description", Matchers.equalTo("Description for Product B"))
                .body("products[1].price", Matchers.equalTo(20.50f));
    }
}