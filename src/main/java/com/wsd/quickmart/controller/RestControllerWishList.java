package com.wsd.quickmart.controller;

import com.wsd.quickmart.controller.response.WishListResponse;
import com.wsd.quickmart.service.WishListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wish-lists")
public class RestControllerWishList {
    private static final Logger logger = LoggerFactory.getLogger("wish-list");
    private final WishListService wishListService;

    public RestControllerWishList(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @GetMapping("{customerId}")
    public ResponseEntity<WishListResponse> getWishListByCustomerId(@PathVariable Long customerId) {
        var wishList = wishListService.getWithListByCustomerId(customerId);
        logger.info("::WishListByCustomerId:: Customer ID: {}, wishList: {}", customerId, wishList);
        return ResponseEntity.ok(new WishListResponse(wishList));
    }
}
