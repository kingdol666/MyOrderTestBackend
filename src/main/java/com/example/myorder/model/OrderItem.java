package com.example.myorder.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private MenuItem menuItem;

    private Integer quantity;
    private BigDecimal price; // 下单时的价格
    private String itemName; // 下单时的商品名称
}