package com.example.myorder.controller;

import com.example.myorder.model.MenuItem;
import com.example.myorder.service.MenuItemService;
import com.example.myorder.service.OssService;
import com.example.myorder.dto.MenuItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/menu-items")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MenuItemController {
    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private OssService ossService;

    // 获取所有菜品，并更新菜单图片为OSS访问地址
    @GetMapping
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        log.info("获取所有菜品");
        List<MenuItem> items = menuItemService.getAllMenuItems();
        // 转换为 MenuItemDTO 并更新 imageUrl
        List<MenuItemDTO> itemDTOs = items.stream()
            .map(item -> {
                MenuItemDTO dto = menuItemService.convertToDTO(item);
                if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                    dto.setImageUrl(ossService.getFileUrl(item.getName()));
                }
                return dto;
            })
            .collect(Collectors.toList());
        log.info("获取到 {} 个菜品", itemDTOs.size());
        return ResponseEntity.ok(itemDTOs);
    }

    // 获取单个菜品详情，并更新imageUrl为OSS访问地址
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItem(@PathVariable Long id) {
        MenuItem item = menuItemService.getMenuItem(id);
        MenuItemDTO dto = menuItemService.convertToDTO(item);
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            dto.setImageUrl(ossService.getFileUrl(item.getName()));
        }
        return ResponseEntity.ok(dto);
    }

    // 获取推荐菜品，并更新imageUrl为OSS访问地址
    @GetMapping("/recommend")
    public ResponseEntity<List<MenuItemDTO>> getRecommendItems() {
        List<MenuItem> items = menuItemService.getRecommendItems();
        List<MenuItemDTO> itemDTOs = items.stream()
            .map(item -> {
                MenuItemDTO dto = menuItemService.convertToDTO(item);
                if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                    dto.setImageUrl(ossService.getFileUrl(item.getName()));
                }
                return dto;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    // 获取热销菜品，并更新imageUrl为OSS访问地址
    @GetMapping("/hot")
    public ResponseEntity<List<MenuItemDTO>> getHotItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MenuItem> hotItemsPage = menuItemService.getHotItems(page, size);
        List<MenuItem> items = hotItemsPage.getContent();
        List<MenuItemDTO> itemDTOs = items.stream()
            .map(item -> {
                MenuItemDTO dto = menuItemService.convertToDTO(item);
                if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                    dto.setImageUrl(ossService.getFileUrl(item.getName()));
                }
                return dto;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    // 根据分类获取菜品，并更新imageUrl为OSS访问地址
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByCategory(@PathVariable Long categoryId) {
        log.info("获取分类{}的菜品", categoryId);
        List<MenuItem> items = menuItemService.getMenuItemsByCategory(categoryId);
        List<MenuItemDTO> itemDTOs = items.stream()
            .map(item -> {
                MenuItemDTO dto = menuItemService.convertToDTO(item);
                if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                    dto.setImageUrl(ossService.getFileUrl(item.getName()));
                }
                return dto;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    // 搜索菜品，并更新imageUrl为OSS访问地址
    @GetMapping("/search")
    public ResponseEntity<List<MenuItemDTO>> searchMenuItems(
            @RequestParam(required = true) String keyword // 确保参数必填
    ) {
        log.info("搜索菜品，关键词：{}", keyword);
        List<MenuItem> items = menuItemService.searchMenuItems(keyword);
        List<MenuItemDTO> itemDTOs = items.stream()
            .map(item -> {
                MenuItemDTO dto = menuItemService.convertToDTO(item);
                if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                    dto.setImageUrl(ossService.getFileUrl(item.getName()));
                }
                return dto;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @PostMapping("/createMenu")
    public ResponseEntity<String> createMenuItem( @RequestPart("file") MultipartFile file,
                                                  @RequestPart("menuItem") String menuItemJson, // 接收JSON字符串
                                                  @RequestParam("categoryId") Long categoryId) {
        try {
            // 手动将JSON字符串反序列化为MenuItem对象
            ObjectMapper objectMapper = new ObjectMapper();
            MenuItem menuItem = objectMapper.readValue(menuItemJson, MenuItem.class);
            // --- 现在你可以使用 file, menuItem, categoryId ---
            // ... 你的业务逻辑 ...
            if (menuItemService.getMenuItemByName(menuItem.getName()) == null) {
                menuItemService.createMenuItem(menuItem, categoryId);
                // 获取文件名
                String fileName = menuItem.getName();
                // 获取文件输入流
                InputStream inputStream = file.getInputStream();
                // 调用 OssService 上传文件
                String fileUrl = ossService.uploadFile(fileName, inputStream);
                menuItem.setImageUrl(fileUrl);
                menuItemService.updateMenuItemCategory(menuItem.getId(), categoryId);
                return ResponseEntity.ok("File uploaded successfully: ");
            } else {
                return ResponseEntity.badRequest().body("菜品已存在");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        }
    }
    @PostMapping("/updateMenu")
    public ResponseEntity<String> updateMenuItem(@RequestPart(value = "file", required = false) MultipartFile file,
                                                 @RequestPart("menuItem") String menuItemJson, // 接收JSON字符串
                                                 @RequestParam("categoryId") Long categoryId) {
        try {
            // 手动将JSON字符串反序列化为MenuItem对象
            ObjectMapper objectMapper = new ObjectMapper();
            MenuItem menuItem = objectMapper.readValue(menuItemJson, MenuItem.class);
            // --- 现在你可以使用 file, menuItem, categoryId ---
            // ... 你的业务逻辑 ...
            // 检查菜品是否存在
            MenuItem existingMenuItem = menuItemService.getMenuItemByName(menuItem.getName());
            if (existingMenuItem != null) {
                // 更新菜品信息
                menuItem.setId(existingMenuItem.getId());
                menuItemService.updateMenuItem(menuItem, categoryId);

                // 如果有新的文件上传，则更新图片URL
                if (file != null && !file.isEmpty()) {
                    // 获取文件名
                    String fileName = menuItem.getName();
                    // 获取文件输入流
                    InputStream inputStream = file.getInputStream();
                    // 删除原图
                    ossService.deleteFile(fileName);
                    // 调用 OssService 上传文件
                    String fileUrl = ossService.uploadFile(fileName, inputStream);
                    menuItem.setImageUrl(fileUrl);
                    menuItemService.updateMenuItem(menuItem, categoryId);
                }

                return ResponseEntity.ok("菜品更新成功");
            } else {
                return ResponseEntity.badRequest().body("菜品不存在");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件输入流
            InputStream inputStream = file.getInputStream();

            // 调用 OssService 上传文件
            String fileUrl = ossService.uploadFile(fileName, inputStream);
            return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenuItem(@PathVariable Long id) {
        try {
            menuItemService.deleteMenuItem(id);
            return ResponseEntity.ok("菜品删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("删除菜品失败: " + e.getMessage());
        }
    }
}
