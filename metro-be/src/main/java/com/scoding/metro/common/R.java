package com.scoding.metro.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok() {
        return R.<T>builder()
                .code(RCode.SUCCESS.getCode())
                .message(RCode.SUCCESS.getMessage())
                .build();
    }

    public static <T> R<T> ok(T data) {
        return R.<T>builder()
                .code(RCode.SUCCESS.getCode())
                .message(RCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static <T> R<T> ok(String message, T data) {
        return R.<T>builder()
                .code(RCode.SUCCESS.getCode())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> R<T> error(RCode rCode) {
        return R.<T>builder()
                .code(rCode.getCode())
                .message(rCode.getMessage())
                .build();
    }

    public static <T> R<T> error(RCode rCode, String message) {
        return R.<T>builder()
                .code(rCode.getCode())
                .message(message)
                .build();
    }

    public static <T> R<T> error(RCode rCode, T data) {
        return R.<T>builder()
                .code(rCode.getCode())
                .message(rCode.getMessage())
                .data(data)
                .build();
    }
} 