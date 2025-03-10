package com.example.myorder.service;

import com.example.myorder.model.MenuItem;
import com.example.myorder.repository.MenuItemRepository;
import com.example.myorder.model.Category;
import com.example.myorder.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import com.example.myorder.dto.MenuItemDTO;

@Service
@Slf4j
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private CategoryService categoryService;
    

    // 获取推荐菜品
    public List<MenuItem> getRecommendItems() {
        try {
            return menuItemRepository.findRecommendItems();
        } catch (Exception e) {
            log.error("获取推荐菜品失败", e);
            throw new RuntimeException("获取推荐菜品失败");
        }
    }
    
    // 获取热销菜品
    public Page<MenuItem> getHotItems(int page, int size) {
        return menuItemRepository.findHotItems(PageRequest.of(page, size));
    }
    
    // 更新菜品销量
    public void updateSalesCount(Long menuItemId, int quantity) {
        try {
            MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));
            menuItem.setSalesCount(menuItem.getSalesCount() + quantity);
            menuItemRepository.save(menuItem);
        } catch (Exception e) {
            log.error("更新菜品销量失败", e);
            throw new RuntimeException("更新菜品销量失败");
        }
    }

    // 获取所有菜品
    public List<MenuItem> getAllMenuItems() {
        try {
            return menuItemRepository.findByAvailableTrue();
        } catch (Exception e) {
            log.error("获取菜品列表失败", e);
            throw new RuntimeException("获取菜品列表失败");
        }
    }

    // 获取单个菜品
    public MenuItem getMenuItem(Long id) {
        return menuItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("菜品不存在"));
    }

    // 根据分类获取菜品
    public List<MenuItem> getMenuItemsByCategory(Long categoryId) {
        try {
            // 如果是"全部"分类（id=0）或没有指定分类，则返回所有菜品
            if (categoryId == null || categoryId == 0) {
                return getAllMenuItems();
            }
            return menuItemRepository.findByCategoryIdAndAvailableTrue(categoryId);
        } catch (Exception e) {
            log.error("获取分类{}的菜品失败", categoryId, e);
            throw new RuntimeException("获取分类菜品失败");
        }
    }

    // 搜索菜品
    public List<MenuItem> searchMenuItems(String keyword) {
        return menuItemRepository.findByNameContainingAndAvailableTrue(keyword);
    }

    // 创建菜品时设置分类
    @Transactional
    public MenuItem createMenuItem(MenuItem menuItem, Long categoryId) {
        Category category = categoryService.getCategory(categoryId);
        menuItem.setCategory(category);
        return menuItemRepository.save(menuItem);
    }

    // 更新菜品分类
    @Transactional
    public MenuItem updateMenuItemCategory(Long menuItemId, Long categoryId) {
        MenuItem menuItem = getMenuItem(menuItemId);
        Category category = categoryService.getCategory(categoryId);
        menuItem.setCategory(category);
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItemDTO> getAllMenuItemsDTO() {
        List<MenuItem> items = menuItemRepository.findByAvailableTrue();
        return items.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private MenuItemDTO convertToDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        BeanUtils.copyProperties(menuItem, dto);
        if (menuItem.getCategory() != null) {
            dto.setCategoryId(menuItem.getCategory().getId());
            dto.setCategoryName(menuItem.getCategory().getName());
        }
        return dto;
    }
} 