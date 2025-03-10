package com.example.myorder.controller;

import com.example.myorder.model.MenuItem;
import com.example.myorder.service.MenuItemService;
import com.example.myorder.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/menu-items")
@Slf4j
public class MenuItemController {
    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private OssService ossService;

    // 获取所有菜品，并更新菜单图片为OSS访问地址
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        log.info("获取所有菜品");
        List<MenuItem> items = menuItemService.getAllMenuItems();
        // 更新每个菜品的imageUrl
        items.forEach(item -> {
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                item.setImageUrl(ossService.getFileUrl(item.getName()));
            }
        });
        log.info("获取到 {} 个菜品", items.size());
        return ResponseEntity.ok(items);
    }

    // 获取单个菜品详情，并更新imageUrl为OSS访问地址
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable Long id) {
        MenuItem item = menuItemService.getMenuItem(id);
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            item.setImageUrl(ossService.getFileUrl(item.getName()));
        }
        return ResponseEntity.ok(item);
    }

    // 获取推荐菜品，并更新imageUrl为OSS访问地址
    @GetMapping("/recommend")
    public ResponseEntity<List<MenuItem>> getRecommendItems() {
        List<MenuItem> items = menuItemService.getRecommendItems();
        items.forEach(item -> {
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                item.setImageUrl(ossService.getFileUrl(item.getName()));
            }
        });
        return ResponseEntity.ok(items);
    }

    // 获取热销菜品，并更新imageUrl为OSS访问地址
    @GetMapping("/hot")
    public ResponseEntity<List<MenuItem>> getHotItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MenuItem> hotItemsPage = menuItemService.getHotItems(page, size);
        List<MenuItem> items = hotItemsPage.getContent();
        items.forEach(item -> {
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                item.setImageUrl(ossService.getFileUrl(item.getName()));
            }
        });
        return ResponseEntity.ok(items);
    }

    // 根据分类获取菜品，并更新imageUrl为OSS访问地址
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(@PathVariable Long categoryId) {
        log.info("获取分类{}的菜品", categoryId);
        List<MenuItem> items = menuItemService.getMenuItemsByCategory(categoryId);
        items.forEach(item -> {
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                item.setImageUrl(ossService.getFileUrl(item.getName()));
            }
        });
        return ResponseEntity.ok(items);
    }

    // 搜索菜品，并更新imageUrl为OSS访问地址
    @GetMapping("/search")
    public ResponseEntity<List<MenuItem>> searchMenuItems(
            @RequestParam(required = true) String keyword // 确保参数必填
    ) {
        log.info("搜索菜品，关键词：{}", keyword);
        List<MenuItem> items = menuItemService.searchMenuItems(keyword);
        items.forEach(item -> {
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                item.setImageUrl(ossService.getFileUrl(item.getName()));
            }
        });
        return ResponseEntity.ok(items);
    }
}