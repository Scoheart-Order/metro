package com.scoding.metro.exception;

import com.scoding.metro.common.RCode;
import lombok.Getter;

/**
 * 业务逻辑异常
 * 用于表示业务逻辑验证失败的情况
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final int code;
    private final String message;

    public BusinessException(RCode rCode) {
        super(rCode.getMessage());
        this.code = rCode.getCode();
        this.message = rCode.getMessage();
    }

    public BusinessException(RCode rCode, String message) {
        super(message);
        this.code = rCode.getCode();
        this.message = message;
    }
    
    public BusinessException(String message) {
        super(message);
        this.code = RCode.BAD_REQUEST.getCode();
        this.message = message;
    }
} 