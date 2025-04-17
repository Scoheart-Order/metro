package com.scoding.metro.dto;

import com.scoding.metro.entity.Role;
import com.scoding.metro.entity.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户响应DTO
 * 用于在API响应中返回用户信息，避免直接返回实体对象
 */
@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String realName;
    private String gender;
    private LocalDate birthDate;
    private String avatar;
    private String address;
    private String bio;
    private Double money;
    private List<Role> roles;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    /**
     * 将User实体转换为UserResponseDto
     *
     * @param user 用户实体
     * @return 用户响应DTO
     */
    public static UserResponseDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRealName(user.getRealName());
        dto.setGender(user.getGender());
        dto.setBirthDate(user.getBirthDate());
        dto.setAvatar(user.getAvatar());
        dto.setAddress(user.getAddress());
        dto.setBio(user.getBio());
        dto.setMoney(user.getMoney());
        dto.setRoles(user.getRoles());
        dto.setEnabled(user.isEnabled());
        dto.setAccountNonExpired(user.isAccountNonExpired());
        dto.setAccountNonLocked(user.isAccountNonLocked());
        dto.setCredentialsNonExpired(user.isCredentialsNonExpired());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        
        return dto;
    }
} 