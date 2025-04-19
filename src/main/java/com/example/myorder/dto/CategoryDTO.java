package com.example.myorder.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.nustaq.serialization.annotations.Serialize;

import java.time.LocalDateTime;


@Data
@Getter
@Setter
@Serialize
public class CategoryDTO {
    private Long id;
    private String name;        // 分类名称
    private String description; // 分类描述
    private Boolean available;  // 是否可用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 无参构造函数
    public CategoryDTO() {
    }

    // 有参构造函数
    public CategoryDTO(Long id, String name, String description, Boolean available, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
