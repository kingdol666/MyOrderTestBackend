package com.example.myorder.service;

import com.example.myorder.model.CartItem;
import com.example.myorder.model.MenuItem;
import com.example.myorder.model.User;
import com.example.myorder.repository.CartItemRepository;
import com.example.myorder.repository.MenuItemRepository;
import com.example.myorder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;
import com.example.myorder.dto.CartItemDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    public List<CartItemDTO> getCartItems(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setUserId(cartItem.getUser().getId());
        dto.setMenuItemId(cartItem.getMenuItem().getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setCreateTime(cartItem.getCreateTime());
        dto.setUpdateTime(cartItem.getUpdateTime());

        // 设置 MenuItem 相关信息
        MenuItem menuItem = cartItem.getMenuItem();
        dto.setItemName(menuItem.getName());
        dto.setItemDescription(menuItem.getDescription());
        dto.setItemPrice(menuItem.getPrice());
        dto.setItemImageUrl(menuItem.getImageUrl());
        dto.setItemAvailable(menuItem.getAvailable());

        return dto;
    }

    @Transactional
    public CartItem saveCartItem(Long userId, Long menuItemId, Integer quantity) {
        // 验证数量
//        if (quantity <= 0) {
//            throw new RuntimeException("商品数量必须大于0");
//        }

        // 检查用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查菜品
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));

        // 检查菜品是否可用
        if (!menuItem.getAvailable()) {
            throw new RuntimeException("该菜品已下架");
        }

        try {
            // 查找已存在的购物车项
            CartItem cartItem = cartItemRepository
                    .findByUserIdAndMenuItemId(userId, menuItemId)
                    .orElse(new CartItem());
            Integer PreQuantity = cartItem.getQuantity();
            if (cartItem.getId() == null) {
                PreQuantity = 0;
            }
            // 设置或更新购物车项
            cartItem.setUser(user);
            cartItem.setMenuItem(menuItem);
            cartItem.setQuantity(PreQuantity+quantity);

            // 保存购物车项
            return cartItemRepository.save(cartItem);
        } catch (Exception e) {
            log.error("保存购物车项失败", e);
            throw new RuntimeException("保存购物车项失败");
        }
    }

    @Transactional
    public void removeFromCart(Long userId, Long menuItemId) {
        cartItemRepository.deleteByUserIdAndMenuItemId(userId, menuItemId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}