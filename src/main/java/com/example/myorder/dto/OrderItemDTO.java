package com.example.myorder.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemDTO {
    private Long id;
    private String itemName;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl; // 如果需要显示图片
}