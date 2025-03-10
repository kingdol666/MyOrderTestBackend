package com.example.myorder.controller;

import com.example.myorder.model.Order;
import com.example.myorder.service.OrderManagementService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/manage/orders")
@RequiredArgsConstructor
public class OrderManagementController {
    private final OrderManagementService orderManagementService;
    
    // 获取待处理订单
    @GetMapping("/pending")
    public List<Order> getPendingOrders() {
        return orderManagementService.getPendingOrders();
    }
    
    // 开始处理订单
    @PostMapping("/{orderId}/process")
    public Order startProcessingOrder(@PathVariable Long orderId) {
        return orderManagementService.startProcessingOrder(orderId);
    }
    
    // 完成订单
    @PostMapping("/{orderId}/complete")
    public Order completeOrder(@PathVariable Long orderId) {
        return orderManagementService.completeOrder(orderId);
    }
    
    // 获取今日订单
    @GetMapping("/today")
    public List<Order> getTodayOrders() {
        return orderManagementService.getTodayOrders();
    }
} 