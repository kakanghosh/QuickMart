package com.wsd.quickmart.service;

import com.wsd.quickmart.dto.ProductDTO;
import com.wsd.quickmart.repository.WishListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
final public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;

    public WishListServiceImpl(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    @Override
    public List<ProductDTO> getWithListByCustomerId(Long customerId) {
        return wishListRepository.findWishListByCustomerId(customerId);
    }
}
