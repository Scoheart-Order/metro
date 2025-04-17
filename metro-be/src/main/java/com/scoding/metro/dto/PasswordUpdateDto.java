package com.scoding.metro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码更新DTO
 */
@Data
public class PasswordUpdateDto {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}