package com.example.myorder.dto;

import lombok.Data;

@Data
public class CartRequest {
    private Long userId;
    private Long menuItemId;
    private Integer quantity;
} 