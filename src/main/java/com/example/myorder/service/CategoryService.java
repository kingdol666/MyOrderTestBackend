package com.example.myorder.service;

import com.example.myorder.model.Category;
import com.example.myorder.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 获取所有分类（包含"全部"选项）
    public List<Category> getAllCategoriesWithAll() {
        return categoryRepository.findAll();
    }

    // 获取单个分类
    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("分类不存在"));
    }

    // 创建分类
    @Transactional
    public Category createCategory(Category category) {
        // 检查名称是否重复
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("分类名称已存在");
        }
        
        // 设置默认值
        if (category.getSort() == null) {
            category.setSort(0);
        }
        category.setAvailable(true);
        
        return categoryRepository.save(category);
    }

    // 更新分类
    @Transactional
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = getCategory(id);
        
        // 检查名称是否重复（排除自身）
        if (!existingCategory.getName().equals(category.getName()) 
            && categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("分类名称已存在");
        }
        
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setSort(category.getSort());
        
        return categoryRepository.save(existingCategory);
    }

    // 删除分类
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategory(id);
        
        // 检查是否有关联的菜品
        if (!category.getMenuItems().isEmpty()) {
            throw new RuntimeException("该分类下还有菜品，不能删除");
        }
        
        categoryRepository.delete(category);
    }

    // 禁用分类
    @Transactional
    public Category disableCategory(Long id) {
        Category category = getCategory(id);
        category.setAvailable(false);
        return categoryRepository.save(category);
    }

    // 启用分类
    @Transactional
    public Category enableCategory(Long id) {
        Category category = getCategory(id);
        category.setAvailable(true);
        return categoryRepository.save(category);
    }

    public Page<Category> getAllCategories(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size, Sort.by("sort").ascending()));
    }
} 