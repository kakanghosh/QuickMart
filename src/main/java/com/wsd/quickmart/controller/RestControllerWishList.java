package com.wsd.quickmart.controller;

import com.wsd.quickmart.controller.response.WishListResponse;
import com.wsd.quickmart.service.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RestControllerWishList {

    private final WishListService wishListService;

    public RestControllerWishList(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @GetMapping("wish-lists/{customerId}")
    public ResponseEntity<WishListResponse> getWishListByCustomerId(@PathVariable Long customerId) {
        var wishList = wishListService.getWithListByCustomerId(customerId);
        return ResponseEntity.ok(new WishListResponse(wishList));
    }
}
