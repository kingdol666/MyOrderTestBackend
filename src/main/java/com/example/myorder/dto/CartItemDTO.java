package com.example.myorder.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItemDTO {
    private Long id;
    private Long userId;
    private Long menuItemId;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // MenuItem 相关信息
    private String itemName;
    private String itemDescription;
    private BigDecimal itemPrice;
    private String itemImageUrl;
    private Boolean itemAvailable;
}