package com.wsd.quickmart.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class WishListRepositoryTest {

    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0-debian")
            .withDatabaseName("quick_mart_db")
            .withUsername("quick_mart_db_user")
            .withPassword("a1643af38034caaa687ae9e12f68dc6d");

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishListItemRepository wishListItemRepository;

    @Autowired
    private WishListRepository wishListRepository;


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
    void shouldNotReturnAnyWishList() {
        var wishList = wishListRepository.findWishListByCustomerId(1L);
        assertThat(wishList).isEmpty();
    }

    @Test
    void shouldFindWishListByCustomerId() {
    }
}