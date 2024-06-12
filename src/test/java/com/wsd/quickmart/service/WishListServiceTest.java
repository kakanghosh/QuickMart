package com.wsd.quickmart.service;

import com.wsd.quickmart.dto.ProductDTO;
import com.wsd.quickmart.entity.Customer;
import com.wsd.quickmart.repository.CustomerRepository;
import com.wsd.quickmart.repository.WishListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WishListServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private WishListRepository wishListRepository;

    @InjectMocks
    private WishListServiceImpl wishListService;

    @Test
    void shouldGetEmptyWishListForInvalidCustomerID() {
        given(customerRepository.findById(1L)).willReturn(Optional.empty());
        var wishList = wishListService.getWithListByCustomerId(1L);
        assertThat(wishList.isEmpty()).isTrue();
    }

    @Test
    void shouldGetWishListForValidCustomerID() {
        var customer = new Customer("John Doe", "john@example.com", LocalDateTime.now());
        var givenWishList = List.of(
                new ProductDTO(1L, "Product A", "Description for Product A", new BigDecimal("10.25"))
        );
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
        given(wishListRepository.findWishListByCustomerId(1L)).willReturn(givenWishList);

        var wishList = wishListService.getWithListByCustomerId(1L);

        assertThat(wishList.isEmpty()).isFalse();
        assertThat(wishList).isEqualTo(givenWishList);
    }
}