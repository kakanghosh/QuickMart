package com.wsd.quickmart.controller.response;

import com.wsd.quickmart.dto.ProductDTO;

import java.util.List;

public record WishListResponse(List<ProductDTO> products) {
}
