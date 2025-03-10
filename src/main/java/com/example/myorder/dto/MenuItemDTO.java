package com.example.myorder.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MenuItemDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean available;
    private boolean isRecommend;
    private int salesCount;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 