package com.example.myorder.service;

import com.example.myorder.model.Order;
import com.example.myorder.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderManagementService {
    private final OrderRepository orderRepository;
    
    // 获取待处理的订单
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatus(Order.STATUS_PAID);
    }
    
    // 开始制作订单
    @Transactional
    public Order startProcessingOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
            
        if (!Order.STATUS_PAID.equals(order.getStatus())) {
            throw new RuntimeException("Order cannot be processed");
        }
        
        order.setStatus(Order.STATUS_PREPARING);
        return orderRepository.save(order);
    }
    
    // 完成订单
    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
            
        if (!Order.STATUS_PREPARING.equals(order.getStatus())) {
            throw new RuntimeException("Order is not in preparation");
        }
        
        order.setStatus(Order.STATUS_COMPLETED);
        return orderRepository.save(order);
    }
    
    // 获取今日订单
    public List<Order> getTodayOrders() {
        return orderRepository.findTodayOrders();
    }
} 