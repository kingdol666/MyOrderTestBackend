package com.example.myorder.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private User user;

    private String orderNumber; // 订单编号
    private BigDecimal totalAmount; // 订单总金额
    private String status; // 订单状态：PENDING, PAID, COMPLETED, CANCELLED
    private String remark; // 订单备注

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    // 订单状态常量
    public static final String STATUS_PENDING = "PENDING"; // 待支付
    public static final String STATUS_PAID = "PAID"; // 已支付
    public static final String STATUS_PREPARING = "PREPARING"; // 制作中
    public static final String STATUS_COMPLETED = "COMPLETED"; // 已完成
    public static final String STATUS_CANCELLED = "CANCELLED"; // 已取消

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    private String generateOrderNumber() {
        // 生成订单号：ORDER + 时间戳 + 4位随机数
        return String.format("ORDER%d%04d",
                System.currentTimeMillis(),
                (int) (Math.random() * 10000));
    }
}