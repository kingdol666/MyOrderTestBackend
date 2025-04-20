package com.example.myorder.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nickname;

    public UserDTO(Long id, String nickName) {
        this.id = id;
        this.nickname = nickName;
    }

    public UserDTO() {

    }
}