package com.wsd.quickmart.repository;

import com.wsd.quickmart.entity.WishList;
import com.wsd.quickmart.entity.WishListItem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class WishListRepositoryTest {

    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0-debian");

    @Value("${spring.application.database.name}")
    String databaseName;

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

    @Test
    void shouldNotReturnAnyWishList() {
        var wishList = wishListRepository.findWishListByCustomerId(1L);
        assertThat(wishList).isEmpty();
    }

    @Test
    void shouldFindWishListByCustomerId() {
        wishListRepository.saveAll(List.of(
                new WishList(1L, LocalDateTime.now().minusDays(1)),
                new WishList(2L, LocalDateTime.now().minusHours(5))
        ));
        wishListItemRepository.saveAll(List.of(
                new WishListItem(1L, 1L),
                new WishListItem(1L, 2L),
                new WishListItem(2L, 2L),
                new WishListItem(2L, 3L)
        ));
        var wishListOfUser1 = wishListRepository.findWishListByCustomerId(1L);
        var wishListOfUser2 = wishListRepository.findWishListByCustomerId(2L);
        assertThat(wishListOfUser1).isNotEmpty();
        assertThat(wishListOfUser2).isNotEmpty();
    }
}