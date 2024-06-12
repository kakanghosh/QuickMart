package com.wsd.quickmart.service;

import com.wsd.quickmart.dto.ProductDTO;

import java.util.List;

public interface WishListService {
    List<ProductDTO> getWithListByCustomerId(Long customerId);
}
