package com.example.myorder.repository;

import com.example.myorder.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    Page<Order> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, String status, Pageable pageable);
    
    List<Order> findByStatus(String status);
    
    @Query("SELECT o FROM Order o WHERE DATE(o.createTime) = CURRENT_DATE")
    List<Order> findTodayOrders();
    
    Optional<Order> findByOrderNumber(String orderNumber);
} 