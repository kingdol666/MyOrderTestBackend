package com.example.myorder.repository;

import com.example.myorder.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndMenuItemId(Long userId, Long menuItemId);
    void deleteByUserIdAndMenuItemId(Long userId, Long menuItemId);
    void deleteByUserId(Long userId);
} 