package com.wsd.quickmart.service;

import com.wsd.quickmart.dto.ProductDTO;
import com.wsd.quickmart.repository.CustomerRepository;
import com.wsd.quickmart.repository.WishListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
final public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final CustomerRepository customerRepository;

    public WishListServiceImpl(WishListRepository wishListRepository,
                               CustomerRepository customerRepository) {
        this.wishListRepository = wishListRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<ProductDTO> getWithListByCustomerId(Long customerId) {
        if (customerRepository.findById(customerId).isEmpty()) {
            return List.of();
        }
        return wishListRepository.findWishListByCustomerId(customerId);
    }
}
