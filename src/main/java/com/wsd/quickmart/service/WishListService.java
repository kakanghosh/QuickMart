package com.wsd.quickmart.service;

import com.wsd.quickmart.dto.ProductDTO;

import java.util.List;

sealed public interface WishListService permits WishListServiceImpl {
    List<ProductDTO> getWithListByCustomerId(Long customerId);
}
