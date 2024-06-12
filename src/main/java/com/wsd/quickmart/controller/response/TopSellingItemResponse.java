package com.wsd.quickmart.controller.response;

import com.wsd.quickmart.dto.TopSellingItemDTO;

import java.util.List;

public record TopSellingItemResponse(List<TopSellingItemDTO> items) {
}
