package com.example.myorder.controller;

import com.example.myorder.dto.CartRequest;
import com.example.myorder.dto.CartItemDTO;
import com.example.myorder.model.CartItem;
import com.example.myorder.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping("/save")
    public ResponseEntity<CartItem> saveCartItem(
            @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.saveCartItem(
                request.getUserId(),
                request.getMenuItemId(),
                request.getQuantity()));
    }

    @DeleteMapping("/{userId}/{menuItemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long menuItemId) {
        cartService.removeFromCart(userId, menuItemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}