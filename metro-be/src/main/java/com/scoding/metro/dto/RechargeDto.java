package com.scoding.metro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RechargeDto {
    @NotNull(message = "充值金额不能为空")
    @Min(value = 1, message = "充值金额必须大于等于1元")
    private Double amount;
} 