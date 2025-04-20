package com.example.myorder.dto;

import com.example.myorder.model.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    private String userOpenId;
    private String userName;
    private List<OrderItem> orderItems;
}
