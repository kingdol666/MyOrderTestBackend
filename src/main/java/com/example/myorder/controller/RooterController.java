package com.example.myorder.controller;

import com.example.myorder.dto.CategoryDTO;
import com.example.myorder.dto.MenuItemDTO;
import com.example.myorder.model.Category;
import com.example.myorder.model.MenuItem;
import com.example.myorder.service.CategoryService;
import com.example.myorder.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooter")
@Slf4j
public class RooterController {
    @Autowired
    private MenuService menuService;

    private CategoryService categoryService;
    @RequestMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/getMenuItems")
    public ResponseEntity<List<MenuItemDTO>> getMenuItems() {
        List<MenuItem> allMenuItems = menuService.getAllMenuItems();
        List<MenuItemDTO> menuItemDTOS = allMenuItems.stream().map(menuItem -> {
            MenuItemDTO menuItemDTO = new MenuItemDTO();
            menuItemDTO.setId(menuItem.getId());
            menuItemDTO.setName(menuItem.getName());
            menuItemDTO.setDescription(menuItem.getDescription());
            menuItemDTO.setPrice(menuItem.getPrice());
            menuItemDTO.setImageUrl(menuItem.getImageUrl());
            menuItemDTO.setAvailable(menuItem.getAvailable());
            menuItemDTO.setSalesCount(menuItem.getSalesCount());
            menuItemDTO.setUpdateTime(menuItem.getUpdateTime());
            menuItemDTO.setCreateTime(menuItem.getCreateTime());
            menuItemDTO.setCategoryId(menuItem.getCategory().getId());
            menuItemDTO.setCategoryName(menuItem.getCategory().getName());
            menuItemDTO.setIsRecommend(menuItem.getIsRecommend());
            return menuItemDTO;
        }).toList();
        return ResponseEntity.ok(menuItemDTOS);
    }

    @GetMapping("/getCategories")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<Category> allCategoriesWithAll = categoryService.getAllCategoriesWithAll();
        List<CategoryDTO> categoryDTOS = allCategoriesWithAll.stream().map(category -> {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            categoryDTO.setAvailable(category.getAvailable());
            categoryDTO.setCreateTime(category.getCreateTime());
            categoryDTO.setUpdateTime(category.getUpdateTime());
            return categoryDTO;
        }).toList();

        return ResponseEntity.ok(categoryDTOS);
    }

}

