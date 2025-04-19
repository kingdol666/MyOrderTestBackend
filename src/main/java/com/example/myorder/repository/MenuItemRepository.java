package com.example.myorder.repository;

import com.example.myorder.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByAvailableTrue();
    
    List<MenuItem> findByCategoryIdAndAvailableTrue(Long categoryId);
    
    List<MenuItem> findByNameContainingAndAvailableTrue(String keyword);
    
    // 获取推荐菜品
    @Query("SELECT m FROM MenuItem m WHERE m.isRecommend = true AND m.available = true")
    List<MenuItem> findRecommendItems();
    
    // 获取热销菜品，按销量降序排序，限制前10个
    @Query("SELECT m FROM MenuItem m WHERE m.available = true ORDER BY m.salesCount DESC")
    Page<MenuItem> findHotItems(Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE m.name = :name")
    MenuItem findByName(String name);

    // 获取所有可用菜品并加载关联的 Category
    @Query("SELECT m FROM MenuItem m JOIN FETCH m.category")
    List<MenuItem> findAllWithCategory();
}