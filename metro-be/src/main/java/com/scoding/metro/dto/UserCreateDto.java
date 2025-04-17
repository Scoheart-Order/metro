package com.scoding.metro.dto;

import com.scoding.metro.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户创建DTO
 */
@Data
public class UserCreateDto {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    private String realName;
    
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private String role;
    
    private String status;

    /**
     * 转换为User实体
     */
    public User toEntity() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setRealName(this.realName);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setEnabled("true".equals(this.status));
        return user;
    }
} 