package com.example.myorder.repository;

import com.example.myorder.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByAvailableTrueOrderBySort();
    boolean existsByName(String name);

    @Query("SELECT c FROM Category c WHERE c.available = true ORDER BY c.sort ASC")
    Page<Category> findAllAvailable(Pageable pageable);
} 